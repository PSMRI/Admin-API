package com.iemr.admin.service.nikshay;

import static com.iemr.admin.service.nikshay.NikshayRowUtils.fromJson;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.require;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.str;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.toBool;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.toInt;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.toJson;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.trimToNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.nikshay.NikshayDeviceConfiguration;
import com.iemr.admin.data.nikshay.NikshayDeviceName;
import com.iemr.admin.data.nikshay.NikshayDeviceVillageMapping;
import com.iemr.admin.data.nikshay.NikshayVillage;
import com.iemr.admin.model.nikshay.NikshayActivateRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceResponse;
import com.iemr.admin.model.nikshay.NikshayVillageEnrollRequest;
import com.iemr.admin.model.nikshay.NikshayVillageRemoveRequest;
import com.iemr.admin.repo.nikshay.NikshayDeviceConfigurationRepo;
import com.iemr.admin.repo.nikshay.NikshayDeviceVillageMappingRepo;
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
	private NikshayPasswordCipher passwordCipher;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public NikshayDeviceResponse createDevice(NikshayDeviceRequest request) throws Exception {
		require(request, "Request body is required");
		String deviceId = require(request.getDeviceId(), "Device ID is required");
		String password = require(request.getPassword(), "Password is required");
		String createdBy = require(request.getCreatedBy(), "Created by is required");

		if (deviceRepo.findByDeviceId(deviceId) != null) {
			throw new IllegalArgumentException("A device with Device ID " + deviceId + " already exists");
		}

		NikshayDeviceConfiguration device = new NikshayDeviceConfiguration();
		device.setDeviceId(deviceId);
		device.setDeviceName(NikshayDeviceName.from(request.getDeviceName()));
		device.setPhiUserId(require(request.getPhiUserId(), "PHI user ID is required"));
		device.setPassword(passwordCipher.encrypt(password));
		device.setIsActive(true);
		device.setMoreData(toJson(request.getMoreData()));
		device.setCreatedBy(createdBy);
		device = deviceRepo.save(device);
		logger.info("Nikshay device {} created by {}", deviceId, createdBy);
		return toResponse(device);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public NikshayDeviceResponse updateDevice(NikshayDeviceRequest request) throws Exception {
		require(request, "Request body is required");
		NikshayDeviceConfiguration device = findDevice(require(request.getId(), "Device id is required"));
		String updatedBy = require(request.getUpdatedBy(), "Updated by is required");

		String deviceId = trimToNull(request.getDeviceId());
		if (deviceId != null && !deviceId.equals(device.getDeviceId())) {
			NikshayDeviceConfiguration other = deviceRepo.findByDeviceId(deviceId);
			if (other != null && !other.getId().equals(device.getId())) {
				throw new IllegalArgumentException("A device with Device ID " + deviceId + " already exists");
			}
			device.setDeviceId(deviceId);
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
		logger.info("Nikshay device {} updated by {}", device.getDeviceId(), updatedBy);
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
			logger.info("Nikshay device {} deactivated by {}, {} village mapping(s) released", device.getDeviceId(),
					updatedBy, released);
		}
		return toResponse(device);
	}

	@Override
	public List<NikshayDeviceResponse> listDevices(String deviceName, Boolean isActive) throws Exception {
		NikshayDeviceName name = trimToNull(deviceName) == null ? null : NikshayDeviceName.from(deviceName);
		List<NikshayDeviceResponse> result = new ArrayList<>();
		for (NikshayDeviceConfiguration device : deviceRepo.search(name, isActive)) {
			result.add(toResponse(device));
		}
		return result;
	}

	@Override
	public List<NikshayDeviceResponse> searchByDeviceId(String deviceId) throws Exception {
		String term = require(deviceId, "Device ID is required");
		List<NikshayDeviceResponse> result = new ArrayList<>();
		for (NikshayDeviceConfiguration device : deviceRepo.searchByDeviceId(term)) {
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
		return enroll(device, request.getVillages(), createdBy);
	}

	/**
	 * Maps an active device to villages. Villages already mapped to this device
	 * are skipped; the whole call is rejected if any village is actively mapped
	 * to another device of the same type (a village has one NAAT and one XRAY).
	 */
	private Map<String, Object> enroll(NikshayDeviceConfiguration device, List<NikshayVillageEnrollRequest.Village> villages,
			String createdBy) {
		if (!Boolean.TRUE.equals(device.getIsActive())) {
			throw new IllegalArgumentException("Device " + device.getDeviceId() + " is inactive");
		}
		if (villages == null || villages.isEmpty()) {
			throw new IllegalArgumentException("At least one village is required");
		}

		// One entry per village; the first occurrence wins
		Map<Integer, NikshayVillageEnrollRequest.Village> byVillage = new LinkedHashMap<>();
		for (NikshayVillageEnrollRequest.Village v : villages) {
			require(v, "Village entry is required");
			require(v.getStateId(), "State is required");
			require(v.getDistrictId(), "District is required");
			require(v.getTbuId(), "TBU is required");
			require(v.getFacilityId(), "Facility is required");
			byVillage.putIfAbsent(require(v.getVillageId(), "Village is required"), v);
		}
		List<Integer> villageIds = new ArrayList<>(byVillage.keySet());

		Set<Integer> known = new HashSet<>();
		for (NikshayVillage v : nikshayVillageRepo.findAllById(villageIds)) {
			if (!Boolean.TRUE.equals(v.getDeleted())) {
				known.add(v.getNikshayVillageID());
			}
		}
		List<Integer> unknown = villageIds.stream().filter(id -> !known.contains(id)).collect(Collectors.toList());
		if (!unknown.isEmpty()) {
			throw new IllegalArgumentException("Ni-kshay village(s) not found: " + unknown);
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
		for (Map.Entry<Integer, NikshayVillageEnrollRequest.Village> e : byVillage.entrySet()) {
			if (alreadyMapped.contains(e.getKey())) {
				continue;
			}
			NikshayVillageEnrollRequest.Village v = e.getValue();
			NikshayDeviceVillageMapping m = new NikshayDeviceVillageMapping();
			m.setDeviceDetailId(device.getId());
			m.setStateId(v.getStateId());
			m.setDistrictId(v.getDistrictId());
			m.setTbuId(v.getTbuId());
			m.setFacilityId(v.getFacilityId());
			m.setVillageId(e.getKey());
			m.setIsActive(true);
			m.setMoreData(toJson(v.getMoreData()));
			m.setCreatedBy(createdBy);
			toSave.add(m);
		}
		mappingRepo.saveAll(toSave);
		logger.info("Nikshay device {} enrolled for {} village(s) by {}", device.getDeviceId(), toSave.size(), createdBy);

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
			m.put("districtId", toInt(row[5]));
			m.put("stateId", toInt(row[6]));
			m.put("deviceDetailId", toInt(row[7]));
			m.put("deviceId", row[8]);
			m.put("deviceName", row[9]);
			m.put("phiUserId", row[10]);
			result.add(m);
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
		r.setDeviceId(d.getDeviceId());
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
