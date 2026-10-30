package com.iemr.admin.service.nikshay;

import static com.iemr.admin.service.nikshay.NikshayRowUtils.require;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.str;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.toInt;
import static com.iemr.admin.service.nikshay.NikshayRowUtils.trimToNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.nikshay.NikshayTU;
import com.iemr.admin.data.nikshay.NikshayTransferOutPhiVillageMapping;
import com.iemr.admin.model.nikshay.NikshayActivateRequest;
import com.iemr.admin.model.nikshay.NikshayDesignatedPhiRequest;
import com.iemr.admin.repo.nikshay.NikshayTURepo;
import com.iemr.admin.repo.nikshay.NikshayTransferOutPhiRepo;
import com.iemr.admin.repo.nikshay.NikshayVillageRepo;

/**
 * Designated PHI of each Ni-kshay village, read by the transfer out phase.
 * A village can have several active rows; village + TBU + PHI is unique among
 * active rows. Saving a village + TBU + PHI that is already active updates that row.
 */
@Service
public class NikshayDesignatedPhiServiceImpl implements NikshayDesignatedPhiService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private NikshayTransferOutPhiRepo transferOutPhiRepo;

	@Autowired
	private NikshayVillageRepo nikshayVillageRepo;

	@Autowired
	private NikshayTURepo nikshayTURepo;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> saveDesignatedPhi(NikshayDesignatedPhiRequest request) throws Exception {
		require(request, "Request body is required");
		Integer villageId = require(request.getVillageId(), "Village is required");
		Integer tbuId = require(request.getTbuId(), "TBU is required");
		String phiId = require(request.getDesignatedPhiId(), "Designated PHI is required");
		String hierarchyId = trimToNull(request.getDesignationHierarchyId());
		String createdBy = require(request.getCreatedBy(), "Created by is required");
		requireVillage(villageId);

		// Same village + TBU + PHI already active: update it instead of adding a duplicate
		List<NikshayTransferOutPhiVillageMapping> existing = transferOutPhiRepo.findActiveByVillageAndPhi(villageId,
				tbuId, phiId);
		NikshayTransferOutPhiVillageMapping mapping;
		if (existing.isEmpty()) {
			mapping = new NikshayTransferOutPhiVillageMapping();
			mapping.setVillageId(villageId);
			mapping.setTbuId(tbuId);
			mapping.setDesignatedPhiId(phiId);
			mapping.setStatus(NikshayTransferOutPhiVillageMapping.STATUS_ACTIVE);
			mapping.setCreatedBy(createdBy);
		} else {
			mapping = existing.get(0);
			mapping.setUpdatedBy(createdBy);
		}
		mapping.setDesignationHierarchyId(hierarchyId);
		mapping = transferOutPhiRepo.save(mapping);
		logger.info("Designated PHI {} {} for village {} by {}", phiId, existing.isEmpty() ? "added" : "updated",
				villageId, createdBy);
		return toMap(mapping);
	}

	/**
	 * Edits a mapping by id. Only id and updatedBy are required; every other
	 * field is changed only when it is sent (not null / not blank). Rejected
	 * when it would make a second active row for the same village + TBU + PHI.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> updateDesignatedPhi(NikshayDesignatedPhiRequest request) throws Exception {
		require(request, "Request body is required");
		Integer id = require(request.getId(), "Mapping id is required");
		String updatedBy = require(request.getUpdatedBy(), "Updated by is required");
		NikshayTransferOutPhiVillageMapping mapping = transferOutPhiRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No designated PHI mapping found with id " + id));

		// Only the fields that are sent (not null / not blank) are changed
		if (request.getVillageId() != null) {
			requireVillage(request.getVillageId());
			mapping.setVillageId(request.getVillageId());
		}
		if (request.getTbuId() != null) {
			mapping.setTbuId(request.getTbuId());
		}
		if (trimToNull(request.getDesignatedPhiId()) != null) {
			mapping.setDesignatedPhiId(request.getDesignatedPhiId().trim());
		}
		if (trimToNull(request.getDesignationHierarchyId()) != null) {
			mapping.setDesignationHierarchyId(request.getDesignationHierarchyId().trim());
		}
		if (NikshayTransferOutPhiVillageMapping.STATUS_ACTIVE.equals(mapping.getStatus())) {
			rejectDuplicateActive(id, mapping.getVillageId(), mapping.getTbuId(), mapping.getDesignatedPhiId());
		}
		mapping.setUpdatedBy(updatedBy);
		mapping = transferOutPhiRepo.save(mapping);
		logger.info("Designated PHI mapping {} updated by {}", id, updatedBy);
		return toMap(mapping);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> setActive(NikshayActivateRequest request) throws Exception {
		require(request, "Request body is required");
		Integer id = require(request.getId(), "Mapping id is required");
		Boolean active = require(request.getIsActive(), "isActive is required");
		String updatedBy = require(request.getUpdatedBy(), "Updated by is required");
		NikshayTransferOutPhiVillageMapping mapping = transferOutPhiRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No designated PHI mapping found with id " + id));

		if (active) {
			rejectDuplicateActive(mapping.getId(), mapping.getVillageId(), mapping.getTbuId(),
					mapping.getDesignatedPhiId());
		}
		mapping.setStatus(active ? NikshayTransferOutPhiVillageMapping.STATUS_ACTIVE
				: NikshayTransferOutPhiVillageMapping.STATUS_INACTIVE);
		mapping.setUpdatedBy(updatedBy);
		return toMap(transferOutPhiRepo.save(mapping));
	}

	// Only id and updatedBy are used; an already inactive mapping is returned unchanged
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> markInactive(NikshayActivateRequest request) throws Exception {
		require(request, "Request body is required");
		Integer id = require(request.getId(), "Mapping id is required");
		String updatedBy = require(request.getUpdatedBy(), "Updated by is required");
		NikshayTransferOutPhiVillageMapping mapping = transferOutPhiRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No designated PHI mapping found with id " + id));

		if (NikshayTransferOutPhiVillageMapping.STATUS_INACTIVE.equals(mapping.getStatus())) {
			return toMap(mapping);
		}
		mapping.setStatus(NikshayTransferOutPhiVillageMapping.STATUS_INACTIVE);
		mapping.setUpdatedBy(updatedBy);
		mapping = transferOutPhiRepo.save(mapping);
		logger.info("Designated PHI mapping {} marked inactive by {}", id, updatedBy);
		return toMap(mapping);
	}

	@Override
	public List<Map<String, Object>> getDesignatedPhi(Integer villageId, Integer tbuId, Integer districtId,
			Boolean activeOnly, Boolean unmappedOnly) throws Exception {
		if (villageId == null && tbuId == null && districtId == null) {
			throw new IllegalArgumentException("Village, TBU or district is required");
		}
		if (Boolean.TRUE.equals(unmappedOnly)) {
			List<Map<String, Object>> result = new ArrayList<>();
			// row layout: see NikshayTransferOutPhiRepo.findVillagesWithoutDesignatedPhi
			for (Object[] row : transferOutPhiRepo.findVillagesWithoutDesignatedPhi(villageId, tbuId, districtId)) {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("villageId", toInt(row[0]));
				m.put("villageName", row[1]);
				m.put("facilityId", toInt(row[2]));
				m.put("facilityName", row[3]);
				m.put("tbuId", toInt(row[4]));
				m.put("tbuName", row[5]);
				result.add(m);
			}
			return result;
		}
		return toSearchRows(transferOutPhiRepo.search(villageId, tbuId, districtId, !Boolean.FALSE.equals(activeOnly)));
	}

	// Every designated PHI mapping, with no village / TBU / district filter
	@Override
	public List<Map<String, Object>> getAllDesignatedPhi(Boolean activeOnly) throws Exception {
		return toSearchRows(transferOutPhiRepo.search(null, null, null, Boolean.TRUE.equals(activeOnly)));
	}

	// row layout: see NikshayTransferOutPhiRepo.search
	private List<Map<String, Object>> toSearchRows(List<Object[]> rows) {
		List<Map<String, Object>> result = new ArrayList<>();
		for (Object[] row : rows) {
			Map<String, Object> m = new LinkedHashMap<>();
			m.put("id", toInt(row[0]));
			m.put("villageId", toInt(row[1]));
			m.put("villageName", row[2]);
			m.put("tbuId", toInt(row[3]));
			m.put("tbuName", row[4]);
			m.put("designatedPhiId", str(row[5]));
			m.put("designationHierarchyId", str(row[6]));
			m.put("status", row[7]);
			m.put("createdBy", row[8]);
			m.put("createdAt", str(row[9]));
			m.put("updatedBy", row[10]);
			m.put("updatedAt", str(row[11]));
			result.add(m);
		}
		return result;
	}

	private void requireVillage(Integer villageId) {
		nikshayVillageRepo.findById(villageId)
				.filter(v -> !Boolean.TRUE.equals(v.getDeleted()))
				.orElseThrow(() -> new IllegalArgumentException("No Ni-kshay village found with id " + villageId));
	}

	// A village may have several PHIs, but each village + TBU + PHI only once as active
	private void rejectDuplicateActive(Integer mappingId, Integer villageId, Integer tbuId, String phiId) {
		for (NikshayTransferOutPhiVillageMapping other : transferOutPhiRepo.findActiveByVillageAndPhi(villageId, tbuId,
				phiId)) {
			if (!other.getId().equals(mappingId)) {
				throw new IllegalArgumentException("Village " + villageId + " already has designated PHI " + phiId
						+ " active under TBU " + tbuId + " (mapping " + other.getId() + ")");
			}
		}
	}

	private Map<String, Object> toMap(NikshayTransferOutPhiVillageMapping p) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("id", p.getId());
		m.put("villageId", p.getVillageId());
		m.put("tbuId", p.getTbuId());
		m.put("tbuName", p.getTbuId() == null ? null
				: nikshayTURepo.findById(p.getTbuId()).map(NikshayTU::getTUName).orElse(null));
		m.put("designatedPhiId", p.getDesignatedPhiId());
		m.put("designationHierarchyId", p.getDesignationHierarchyId());
		m.put("status", p.getStatus());
		return m;
	}
}
