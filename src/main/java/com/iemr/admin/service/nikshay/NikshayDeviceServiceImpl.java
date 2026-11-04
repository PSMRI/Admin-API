package com.iemr.admin.service.nikshay;

import static com.iemr.admin.service.nikshay.NikshayRowUtils.fromJson;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.require;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.str;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.toBool;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.toInt;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.toJson;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.trimToNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.nikshay.NikshayCreatedDeviceDTO;
import com.iemr.admin.data.nikshay.NikshayDeviceConfiguration;
import com.iemr.admin.data.nikshay.NikshayDeviceFieldErrorDTO;
import com.iemr.admin.data.nikshay.NikshayDeviceName;
import com.iemr.admin.data.nikshay.NikshayDeviceVillageMapping;
import com.iemr.admin.data.nikshay.NikshayFailedDeviceDTO;
import com.iemr.admin.data.nikshay.NikshayFacility;
import com.iemr.admin.data.nikshay.NikshayPendingDeviceDTO;
import com.iemr.admin.data.nikshay.NikshayVillage;
import com.iemr.admin.model.nikshay.NikshayActivateRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceBulkCreateRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceBulkCreateResponse;
import com.iemr.admin.model.nikshay.NikshayDeviceRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceResponse;
import com.iemr.admin.model.nikshay.NikshayVillageEnrollRequest;
import com.iemr.admin.model.nikshay.NikshayVillageRemoveRequest;
import com.iemr.admin.repo.nikshay.NikshayDeviceConfigurationRepo;
import com.iemr.admin.repo.nikshay.NikshayDeviceVillageMappingRepo;
import com.iemr.admin.repo.nikshay.NikshayFacilityRepo;
import com.iemr.admin.repo.nikshay.NikshayVillageRepo;
import com.iemr.admin.utils.crypto.NikshayPasswordCipher;

/**
 * Camp devices (NAAT / XRAY) with their Ni-kshay PHI login, and the villages
 * each device serves. A device serves many villages; a village has at most one
 * active device of each type (one NAAT, one XRAY), so the Ni-kshay job always
 * finds exactly one login of the type it needs for a beneficiary's village. Passwords are encrypted before saving and never
 * returned or logged.
 */
@Service
public class NikshayDeviceServiceImpl implements NikshayDeviceService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private NikshayDeviceConfigurationRepo deviceRepo;

	@Autowired
	private NikshayDeviceVillageMappingRepo mappingRepo;

	@Autowired
	private NikshayVillageRepo nikshayVillageRepo;

	@Autowired
	private NikshayFacilityRepo nikshayFacilityRepo;

	@Autowired
	private NikshayPasswordCipher passwordCipher;

	private static final int MAX_DEVICES_PER_REQUEST = 1000;
	private static final int INSERT_CHUNK_SIZE = 200;

	/**
	 * /create: creates one or more devices with partial success. Every row is
	 * validated in memory, existing serial numbers are found with one IN query,
	 * and the rest are inserted with deviceRepo.batchInsert in chunks of
	 * INSERT_CHUNK_SIZE, each chunk in its own transaction. If a chunk fails (e.g.
	 * a concurrent insert of the same serial number), it is rolled back and
	 * retried row by row so only the bad rows fail. Deliberately not
	 * @Transactional: a failed chunk must not roll back the chunks already saved.
	 */
	@Override
	public NikshayDeviceBulkCreateResponse createDevices(NikshayDeviceBulkCreateRequest request) throws Exception {
		require(request, "Request body is required");
		String createdBy = require(request.getCreatedBy(), "Created by is required");
		List<NikshayDeviceRequest> devices = request.getDevices();
		if (devices == null || devices.isEmpty()) {
			throw new IllegalArgumentException("At least one device is required");
		}
		if (devices.size() > MAX_DEVICES_PER_REQUEST) {
			throw new IllegalArgumentException(
					"A maximum of " + MAX_DEVICES_PER_REQUEST + " devices can be uploaded at a time");
		}

		NikshayDeviceBulkCreateResponse result = new NikshayDeviceBulkCreateResponse();
		result.setTotal(devices.size());

		// 1. Field validation and duplicates inside the request (no database calls)
		Map<String, Integer> firstRowBySerial = new HashMap<>();
		List<NikshayPendingDeviceDTO> pending = new ArrayList<>();
		for (int row = 0; row < devices.size(); row++) {
			NikshayDeviceRequest d = devices.get(row);
			if (d == null) {
				fail(result, row, null, NikshayFailedDeviceDTO.VALIDATION_ERROR,
						List.of(new NikshayDeviceFieldErrorDTO(null, "Device entry is empty")));
				continue;
			}
			List<NikshayDeviceFieldErrorDTO> errors = new ArrayList<>();
			NikshayDeviceName deviceName = validateDevice(d, errors);
			if (!errors.isEmpty()) {
				fail(result, row, d, NikshayFailedDeviceDTO.VALIDATION_ERROR, errors);
				continue;
			}
			String serialNumber = d.getDeviceSerialNumber().trim();
			Integer firstRow = firstRowBySerial.putIfAbsent(serialKey(serialNumber), row);
			if (firstRow != null) {
				fail(result, row, d, NikshayFailedDeviceDTO.DUPLICATE_IN_REQUEST,
						List.of(new NikshayDeviceFieldErrorDTO("deviceSerialNumber", "Serial number " + serialNumber
								+ " is repeated in this upload (first at row " + firstRow + ")")));
				continue;
			}
			pending.add(new NikshayPendingDeviceDTO(row, d, serialNumber, deviceName));
		}

		// 2. Serial numbers already in the database (one query)
		if (!pending.isEmpty()) {
			Set<String> existing = deviceRepo
					.findByDeviceSerialNumberIn(
							pending.stream().map(NikshayPendingDeviceDTO::getSerialNumber).collect(Collectors.toList()))
					.stream().map(e -> serialKey(e.getDeviceSerialNumber())).collect(Collectors.toSet());
			List<NikshayPendingDeviceDTO> fresh = new ArrayList<>();
			for (NikshayPendingDeviceDTO p : pending) {
				if (existing.contains(serialKey(p.getSerialNumber()))) {
					fail(result, p.getRowIndex(), p.getSource(), NikshayFailedDeviceDTO.ALREADY_EXISTS,
							List.of(new NikshayDeviceFieldErrorDTO("deviceSerialNumber",
									"A device with serial number " + p.getSerialNumber() + " already exists")));
				} else {
					p.setEntity(newDevice(p, createdBy));
					fresh.add(p);
				}
			}
			pending = fresh;
		}

		// 3. Batch insert in chunks, each chunk in its own transaction (repository)
		List<NikshayPendingDeviceDTO> inserted = new ArrayList<>();
		for (int from = 0; from < pending.size(); from += INSERT_CHUNK_SIZE) {
			List<NikshayPendingDeviceDTO> chunk = pending.subList(from,
					Math.min(from + INSERT_CHUNK_SIZE, pending.size()));
			inserted.addAll(insertChunk(chunk, result));
		}

		// 4. Created devices, with the ids returned by the insert itself (no read-back query)
		for (NikshayPendingDeviceDTO p : inserted) {
			NikshayDeviceConfiguration e = p.getEntity();
			NikshayCreatedDeviceDTO c = new NikshayCreatedDeviceDTO();
			c.setRowIndex(p.getRowIndex());
			c.setId(e.getId());
			c.setDeviceSerialNumber(e.getDeviceSerialNumber());
			c.setDeviceName(e.getDeviceName());
			c.setPhiUserId(e.getPhiUserId());
			c.setIsActive(e.getIsActive());
			result.getCreated().add(c);
		}

		result.getCreated().sort(Comparator.comparingInt(NikshayCreatedDeviceDTO::getRowIndex));
		result.getFailed().sort(Comparator.comparingInt(NikshayFailedDeviceDTO::getRowIndex));
		result.setSuccessCount(result.getCreated().size());
		result.setFailedCount(result.getFailed().size());
		logger.info("Nikshay bulk device create by {}: total {}, created {}, failed {}", createdBy, result.getTotal(),
				result.getSuccessCount(), result.getFailedCount());
		return result;
	}

	// Same values the single-device create saves; the password is encrypted here
	private NikshayDeviceConfiguration newDevice(NikshayPendingDeviceDTO p, String createdBy) {
		NikshayDeviceRequest source = p.getSource();
		NikshayDeviceConfiguration device = new NikshayDeviceConfiguration();
		device.setDeviceSerialNumber(p.getSerialNumber());
		device.setDeviceName(p.getDeviceName());
		device.setPhiUserId(source.getPhiUserId().trim());
		device.setPassword(passwordCipher.encrypt(source.getPassword().trim()));
		device.setIsActive(true);
		device.setMoreData(toJson(source.getMoreData()));
		device.setCreatedBy(createdBy);
		return device;
	}

	// Collects every field error of one device; returns the parsed device name when valid
	private NikshayDeviceName validateDevice(NikshayDeviceRequest d, List<NikshayDeviceFieldErrorDTO> errors) {
		if (trimToNull(d.getDeviceSerialNumber()) == null) {
			errors.add(new NikshayDeviceFieldErrorDTO("deviceSerialNumber", "Device serial number is required"));
		}
		NikshayDeviceName deviceName = null;
		try {
			deviceName = NikshayDeviceName.from(d.getDeviceName());
		} catch (IllegalArgumentException e) {
			errors.add(new NikshayDeviceFieldErrorDTO("deviceName", e.getMessage()));
		}
		if (trimToNull(d.getPhiUserId()) == null) {
			errors.add(new NikshayDeviceFieldErrorDTO("phiUserId", "PHI user ID is required"));
		}
		if (trimToNull(d.getPassword()) == null) {
			errors.add(new NikshayDeviceFieldErrorDTO("password", "Password is required"));
		}
		return deviceName;
	}

	// Inserts one chunk; returns the rows that were saved (their entities carry the
	// generated ids) and adds failed rows to result
	private List<NikshayPendingDeviceDTO> insertChunk(List<NikshayPendingDeviceDTO> chunk,
			NikshayDeviceBulkCreateResponse result) {
		try {
			deviceRepo.batchInsert(chunk.stream().map(NikshayPendingDeviceDTO::getEntity).collect(Collectors.toList()));
			return chunk;
		} catch (DataAccessException e) {
			// The whole chunk was rolled back; retry row by row so only the bad rows fail
			logger.warn("Batch insert of {} Nikshay devices failed, retrying one by one: {}", chunk.size(),
					e.getMostSpecificCause().getMessage());
		}
		List<NikshayPendingDeviceDTO> saved = new ArrayList<>();
		for (NikshayPendingDeviceDTO p : chunk) {
			try {
				p.getEntity().setId(null);
				deviceRepo.insert(p.getEntity());
				saved.add(p);
			} catch (DataAccessException e) {
				String message = e instanceof DuplicateKeyException
						? "A device with serial number " + p.getSerialNumber() + " already exists"
						: e.getMostSpecificCause().getMessage();
				logger.warn("Nikshay device {} could not be created: {}", p.getSerialNumber(), message);
				fail(result, p.getRowIndex(), p.getSource(), NikshayFailedDeviceDTO.DB_ERROR,
						List.of(new NikshayDeviceFieldErrorDTO(null, "Failed to create device entry: " + message)));
			}
		}
		return saved;
	}

	// Echoes the submitted values (never the password) so the row can be corrected
	private void fail(NikshayDeviceBulkCreateResponse result, int rowIndex, NikshayDeviceRequest d, String reason,
			List<NikshayDeviceFieldErrorDTO> errors) {
		NikshayFailedDeviceDTO f = new NikshayFailedDeviceDTO();
		f.setRowIndex(rowIndex);
		if (d != null) {
			f.setDeviceSerialNumber(d.getDeviceSerialNumber());
			f.setDeviceName(d.getDeviceName());
			f.setPhiUserId(d.getPhiUserId());
		}
		f.setReason(reason);
		f.setErrors(new ArrayList<>(errors));
		result.getFailed().add(f);
	}

	// device_serial_number is unique under MySQL's case-insensitive collation, so compare the same way
	private String serialKey(String serialNumber) {
		return serialNumber.trim().toLowerCase(Locale.ROOT);
	}

	/**
	 * Single-device create used by the Excel upload (/upload); runs inside
	 * uploadDevices' transaction. /create uses createDevices instead.
	 */
	private NikshayDeviceResponse createDevice(NikshayDeviceRequest request) throws Exception {
		require(request, "Request body is required");
		String deviceSerialNumber = require(request.getDeviceSerialNumber(), "Device serial number is required");
		String password = require(request.getPassword(), "Password is required");
		String createdBy = require(request.getCreatedBy(), "Created by is required");

		if (deviceRepo.findByDeviceSerialNumber(deviceSerialNumber) != null) {
			throw new IllegalArgumentException("A device with serial number " + deviceSerialNumber + " already exists");
		}

		NikshayDeviceConfiguration device = new NikshayDeviceConfiguration();
		device.setDeviceSerialNumber(deviceSerialNumber);
		device.setDeviceName(NikshayDeviceName.from(request.getDeviceName()));
		device.setPhiUserId(require(request.getPhiUserId(), "PHI user ID is required"));
		device.setPassword(passwordCipher.encrypt(password));
		device.setIsActive(true);
		device.setMoreData(toJson(request.getMoreData()));
		device.setCreatedBy(createdBy);
		device = deviceRepo.save(device);
		logger.info("Nikshay device {} created by {}", deviceSerialNumber, createdBy);
		return toResponse(device);
	}

	@Override
	public byte[] getUploadTemplate() throws Exception {
		return NikshayDeviceExcel.template();
	}

	/**
	 * Creates the devices of an uploaded template. All rows are checked first and
	 * nothing is saved if any row is invalid; the error lists every bad row.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> uploadDevices(InputStream excel, String createdBy) throws Exception {
		String uploadedBy = require(createdBy, "Created by is required");
		List<NikshayDeviceExcel.DeviceRow> rows;
		try {
			rows = NikshayDeviceExcel.read(excel);
		} catch (IOException | RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				throw e;
			}
			throw new IllegalArgumentException("Could not read the Excel file; upload the downloaded template (.xlsx)");
		}
		if (rows.isEmpty()) {
			throw new IllegalArgumentException("The Excel file has no device rows");
		}

		List<String> errors = new ArrayList<>();
		Set<String> seen = new HashSet<>();
		for (NikshayDeviceExcel.DeviceRow row : rows) {
			NikshayDeviceRequest r = row.request;
			List<String> rowErrors = new ArrayList<>();
			if (r.getDeviceSerialNumber() == null) {
				rowErrors.add("device serial number is required");
			} else if (!seen.add(r.getDeviceSerialNumber().toLowerCase())) {
				rowErrors.add("serial number " + r.getDeviceSerialNumber() + " is repeated in the file");
			} else if (deviceRepo.findByDeviceSerialNumber(r.getDeviceSerialNumber()) != null) {
				rowErrors.add("serial number " + r.getDeviceSerialNumber() + " already exists");
			}
			try {
				NikshayDeviceName.from(r.getDeviceName());
			} catch (IllegalArgumentException e) {
				rowErrors.add(e.getMessage().toLowerCase());
			}
			if (r.getPhiUserId() == null) {
				rowErrors.add("PHI user ID is required");
			}
			if (r.getPassword() == null) {
				rowErrors.add("password is required");
			}
			if (!rowErrors.isEmpty()) {
				errors.add("Row " + row.rowNumber + ": " + String.join(", ", rowErrors));
			}
		}
		if (!errors.isEmpty()) {
			throw new IllegalArgumentException("No devices created. " + String.join("; ", errors));
		}

		List<NikshayDeviceResponse> created = new ArrayList<>();
		for (NikshayDeviceExcel.DeviceRow row : rows) {
			row.request.setCreatedBy(uploadedBy);
			created.add(createDevice(row.request));
		}
		logger.info("{} Nikshay device(s) uploaded by {}", created.size(), uploadedBy);

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("createdCount", created.size());
		result.put("devices", created);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public NikshayDeviceResponse updateDevice(NikshayDeviceRequest request) throws Exception {
		require(request, "Request body is required");
		NikshayDeviceConfiguration device = findDevice(require(request.getId(), "Device id is required"));
		String updatedBy = require(request.getUpdatedBy(), "Updated by is required");

		String deviceSerialNumber = trimToNull(request.getDeviceSerialNumber());
		if (deviceSerialNumber != null && !deviceSerialNumber.equals(device.getDeviceSerialNumber())) {
			NikshayDeviceConfiguration other = deviceRepo.findByDeviceSerialNumber(deviceSerialNumber);
			if (other != null && !other.getId().equals(device.getId())) {
				throw new IllegalArgumentException("A device with serial number " + deviceSerialNumber + " already exists");
			}
			device.setDeviceSerialNumber(deviceSerialNumber);
		}
		if (trimToNull(request.getDeviceName()) != null) {
			device.setDeviceName(NikshayDeviceName.from(request.getDeviceName()));
		}
		if (trimToNull(request.getPhiUserId()) != null) {
			device.setPhiUserId(request.getPhiUserId().trim());
		}

		if (trimToNull(request.getPassword()) != null) {
			device.setPassword(passwordCipher.encrypt(request.getPassword().trim()));
		}
		if (request.getMoreData() != null) {
			device.setMoreData(toJson(request.getMoreData()));
		}
		device.setUpdatedBy(updatedBy);
		device = deviceRepo.save(device);
		logger.info("Nikshay device {} updated by {}", device.getDeviceSerialNumber(), updatedBy);
		return toResponse(device);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public NikshayDeviceResponse setDeviceActive(NikshayActivateRequest request) throws Exception {
		require(request, "Request body is required");
		NikshayDeviceConfiguration device = findDevice(require(request.getId(), "Device id is required"));
		Boolean active = require(request.getIsActive(), "isActive is required");
		String updatedBy = require(request.getUpdatedBy(), "Updated by is required");

		device.setIsActive(active);
		device.setUpdatedBy(updatedBy);
		device = deviceRepo.save(device);
		// An inactive device must not keep its villages, otherwise those villages
		// would look covered while the job ignores the device. On re-activation
		// the villages have to be enrolled again.
		if (!active) {
			int released = mappingRepo.deactivateAllForDevice(device.getId(), updatedBy);
			logger.info("Nikshay device {} deactivated by {}, {} village mapping(s) released", device.getDeviceSerialNumber(),
					updatedBy, released);
		}
		return toResponse(device);
	}

	@Override
	public List<NikshayDeviceResponse> listDevices(String deviceName, Boolean isActive) throws Exception {
		NikshayDeviceName name = trimToNull(deviceName) == null ? null : NikshayDeviceName.from(deviceName);
		List<NikshayDeviceResponse> result = new ArrayList<>();
		for (NikshayDeviceConfiguration device : deviceRepo.search(name, isActive)) {
			NikshayDeviceResponse response = toResponse(device);
			response.setVillages(getVillagesOfDevice(device.getId(), true));
			result.add(response);
		}
		return result;
	}

	@Override
	public List<NikshayDeviceResponse> searchByDeviceSerialNumber(String deviceSerialNumber) throws Exception {
		String term = require(deviceSerialNumber, "Device serial number is required");
		List<NikshayDeviceResponse> result = new ArrayList<>();
		for (NikshayDeviceConfiguration device : deviceRepo.searchByDeviceSerialNumber(term)) {
			NikshayDeviceResponse response = toResponse(device);
			response.setVillages(getVillagesOfDevice(device.getId(), true));
			result.add(response);
		}
		return result;
	}

	@Override
	public NikshayDeviceResponse getDevice(Integer id) throws Exception {
		NikshayDeviceResponse response = toResponse(findDevice(require(id, "Device id is required")));
		response.setVillages(getVillagesOfDevice(id, true));
		return response;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> enrollVillages(NikshayVillageEnrollRequest request) throws Exception {
		require(request, "Request body is required");
		NikshayDeviceConfiguration device = findDevice(require(request.getDeviceDetailId(), "Device id is required"));
		String createdBy = require(request.getCreatedBy(), "Created by is required");
		return enroll(device, request, createdBy);
	}

	/**
	 * Maps an active device to the villages picked from one or more facilities
	 * of a TBU. Every facility must belong to the TBU and every village to one
	 * of the facilities; each mapping row keeps the village's own facility.
	 * Villages already mapped to this device are skipped; the whole call is
	 * rejected if any village is actively mapped to another device of the same
	 * type (a village has one NAAT and one XRAY).
	 */
	private Map<String, Object> enroll(NikshayDeviceConfiguration device, NikshayVillageEnrollRequest request,
			String createdBy) {
		if (!Boolean.TRUE.equals(device.getIsActive())) {
			throw new IllegalArgumentException("Device " + device.getDeviceSerialNumber() + " is inactive");
		}
		Integer stateId = require(request.getStateId(), "State is required");
		Integer districtId = require(request.getDistrictId(), "District is required");
		Integer tbuId = require(request.getTbuId(), "TBU is required");
		List<Integer> facilityIds = distinctIds(request.getFacilityIds(), "At least one facility is required");
		List<Integer> villageIds = distinctIds(request.getVillageIds(), "At least one village is required");

		Set<Integer> knownFacilities = new HashSet<>();
		for (NikshayFacility f : nikshayFacilityRepo.findByIds(facilityIds)) {
			if (!tbuId.equals(f.getNikshayTUID())) {
				throw new IllegalArgumentException("Facility " + f.getNikshayFacilityID() + " does not belong to TBU " + tbuId);
			}
			knownFacilities.add(f.getNikshayFacilityID());
		}
		List<Integer> unknownFacilities = facilityIds.stream().filter(id -> !knownFacilities.contains(id))
				.collect(Collectors.toList());
		if (!unknownFacilities.isEmpty()) {
			throw new IllegalArgumentException("Ni-kshay facility(s) not found: " + unknownFacilities);
		}

		// village id -> its facility; the village must be under one of the selected facilities
		Map<Integer, Integer> facilityOfVillage = new HashMap<>();
		List<Integer> outsideFacilities = new ArrayList<>();
		for (NikshayVillage v : nikshayVillageRepo.findAllById(villageIds)) {
			if (Boolean.TRUE.equals(v.getDeleted())) {
				continue;
			}
			if (!knownFacilities.contains(v.getNikshayFacilityID())) {
				outsideFacilities.add(v.getNikshayVillageID());
			}
			facilityOfVillage.put(v.getNikshayVillageID(), v.getNikshayFacilityID());
		}
		List<Integer> unknown = villageIds.stream().filter(id -> !facilityOfVillage.containsKey(id))
				.collect(Collectors.toList());
		if (!unknown.isEmpty()) {
			throw new IllegalArgumentException("Ni-kshay village(s) not found: " + unknown);
		}
		if (!outsideFacilities.isEmpty()) {
			throw new IllegalArgumentException(
					"Village(s) " + outsideFacilities + " do not belong to the selected facilities " + facilityIds);
		}

		Set<Integer> alreadyMapped = new HashSet<>();
		List<Integer> mappedToOther = new ArrayList<>();
		for (NikshayDeviceVillageMapping m : mappingRepo.findActiveByVillageIdsAndDeviceName(villageIds,
				device.getDeviceName())) {
			if (m.getDeviceDetailId().equals(device.getId())) {
				alreadyMapped.add(m.getVillageId());
			} else {
				mappedToOther.add(m.getVillageId());
			}
		}
		if (!mappedToOther.isEmpty()) {
			throw new IllegalArgumentException("Village(s) " + mappedToOther + " already have an active "
					+ device.getDeviceName() + " device. Remove them from that device first.");
		}

		List<NikshayDeviceVillageMapping> toSave = new ArrayList<>();
		for (Integer villageId : villageIds) {
			if (alreadyMapped.contains(villageId)) {
				continue;
			}
			NikshayDeviceVillageMapping m = new NikshayDeviceVillageMapping();
			m.setDeviceDetailId(device.getId());
			m.setStateId(stateId);
			m.setDistrictId(districtId);
			m.setTbuId(tbuId);
			m.setFacilityId(facilityOfVillage.get(villageId));
			m.setVillageId(villageId);
			m.setIsActive(true);
			m.setMoreData(toJson(request.getMoreData()));
			m.setCreatedBy(createdBy);
			toSave.add(m);
		}
		mappingRepo.saveAll(toSave);
		logger.info("Nikshay device {} enrolled for {} village(s) by {}", device.getDeviceSerialNumber(), toSave.size(), createdBy);

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("deviceDetailId", device.getId());
		result.put("enrolledCount", toSave.size());
		result.put("alreadyMappedVillageIds", new ArrayList<>(alreadyMapped));
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> removeVillages(NikshayVillageRemoveRequest request) throws Exception {
		require(request, "Request body is required");
		String updatedBy = require(request.getUpdatedBy(), "Updated by is required");
		if (request.getMappingIds() == null || request.getMappingIds().isEmpty()) {
			throw new IllegalArgumentException("At least one mapping id is required");
		}
		List<Integer> ids = request.getMappingIds().stream().distinct().collect(Collectors.toList());
		List<NikshayDeviceVillageMapping> rows = new ArrayList<>();
		mappingRepo.findAllById(ids).forEach(rows::add);
		if (rows.size() != ids.size()) {
			Set<Integer> found = rows.stream().map(NikshayDeviceVillageMapping::getId).collect(Collectors.toSet());
			throw new IllegalArgumentException("Mapping(s) not found: "
					+ ids.stream().filter(id -> !found.contains(id)).collect(Collectors.toList()));
		}
		int removed = 0;
		for (NikshayDeviceVillageMapping m : rows) {
			if (Boolean.TRUE.equals(m.getIsActive())) {
				m.setIsActive(false);
				m.setUpdatedBy(updatedBy);
				removed++;
			}
		}
		mappingRepo.saveAll(rows);

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("removedCount", removed);
		return result;
	}

	@Override
	public List<Map<String, Object>> getVillagesOfDevice(Integer deviceDetailId, Boolean activeOnly) throws Exception {
		require(deviceDetailId, "Device id is required");
		List<Map<String, Object>> result = new ArrayList<>();
		// row layout: see NikshayDeviceVillageMappingRepo.findVillagesOfDevice
		for (Object[] row : mappingRepo.findVillagesOfDevice(deviceDetailId, !Boolean.FALSE.equals(activeOnly))) {
			Map<String, Object> m = new LinkedHashMap<>();
			m.put("mappingId", toInt(row[0]));
			m.put("villageId", toInt(row[1]));
			m.put("villageName", row[2]);
			m.put("facilityId", toInt(row[3]));
			m.put("facilityName", row[4]);
			m.put("tbuId", toInt(row[5]));
			m.put("tbuName", row[6]);
			m.put("districtId", toInt(row[7]));
			m.put("stateId", toInt(row[8]));
			m.put("isActive", toBool(row[9]));
			m.put("createdBy", row[10]);
			m.put("createdAt", str(row[11]));
			result.add(m);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getPhiByLocation(Integer stateId, Integer districtId, Integer tbuId,
			Integer facilityId, Integer villageId, String deviceName) throws Exception {
		if (stateId == null && districtId == null && tbuId == null && facilityId == null && villageId == null) {
			throw new IllegalArgumentException("At least one of state, district, TBU, facility or village is required");
		}
		String name = trimToNull(deviceName) == null ? null : NikshayDeviceName.from(deviceName).name();
		List<Map<String, Object>> result = new ArrayList<>();
		// row layout: see NikshayDeviceVillageMappingRepo.findPhiByLocation
		for (Object[] row : mappingRepo.findPhiByLocation(stateId, districtId, tbuId, facilityId, villageId, name)) {
			Map<String, Object> m = new LinkedHashMap<>();
			m.put("mappingId", toInt(row[0]));
			m.put("villageId", toInt(row[1]));
			m.put("villageName", row[2]);
			m.put("facilityId", toInt(row[3]));
			m.put("tbuId", toInt(row[4]));
			m.put("tbuName", row[11]);
			m.put("districtId", toInt(row[5]));
			m.put("stateId", toInt(row[6]));
			m.put("deviceDetailId", toInt(row[7]));
			m.put("deviceSerialNumber", row[8]);
			m.put("deviceName", row[9]);
			m.put("phiUserId", row[10]);
			result.add(m);
		}
		return result;
	}

	// Non-null ids in request order, without duplicates
	private List<Integer> distinctIds(List<Integer> ids, String message) {
		List<Integer> result = ids == null ? new ArrayList<>()
				: ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
		if (result.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
		return result;
	}

	private NikshayDeviceConfiguration findDevice(Integer id) {
		return deviceRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No device found with id " + id));
	}

	private NikshayDeviceResponse toResponse(NikshayDeviceConfiguration d) {
		NikshayDeviceResponse r = new NikshayDeviceResponse();
		r.setId(d.getId());
		r.setDeviceSerialNumber(d.getDeviceSerialNumber());
		r.setDeviceName(d.getDeviceName());
		r.setPhiUserId(d.getPhiUserId());
		r.setIsActive(d.getIsActive());
		r.setMoreData(fromJson(d.getMoreData()));
		r.setActiveVillageCount(d.getId() == null ? 0L : mappingRepo.countActiveByDevice(d.getId()));
		r.setCreatedBy(d.getCreatedBy());
		r.setCreatedAt(str(d.getCreatedAt()));
		r.setUpdatedBy(d.getUpdatedBy());
		r.setUpdatedAt(str(d.getUpdatedAt()));
		return r;
	}
}
