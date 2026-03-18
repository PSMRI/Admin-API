/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.admin.service.employeemaster;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.employeemaster.AshaSupervisorMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.repository.user.AshaSupervisorMappingRepo;

@Service
public class AshaSupervisorMappingServiceImpl implements AshaSupervisorMappingService {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private AshaSupervisorMappingRepo ashaSupervisorMappingRepo;

	@Autowired
	private EmployeeMasterRepo employeeMasterRepo;

	@Autowired
	private MainStoreRepo mainStoreRepo;

	@Transactional
	@Override
	public ArrayList<AshaSupervisorMapping> saveAshaSupervisorMappings(List<AshaSupervisorMapping> mappings) {
		ArrayList<AshaSupervisorMapping> savedMappings = new ArrayList<>();
		for (AshaSupervisorMapping mapping : mappings) {
			// Fix 12: reject if facilityID points to a soft-deleted or missing facility
			M_Facility facility = mainStoreRepo.findByFacilityIDAndDeleted(mapping.getFacilityID(), false);
			if (facility == null) {
				throw new RuntimeException("Facility ID " + mapping.getFacilityID()
						+ " is no longer active. Cannot save ASHA supervisor mapping.");
			}
			// Fix 6: skip if identical active row already exists (network retry / duplicate save)
			AshaSupervisorMapping existingActive = ashaSupervisorMappingRepo
					.findBySupervisorUserIDAndAshaUserIDAndFacilityIDAndDeletedFalse(
							mapping.getSupervisorUserID(), mapping.getAshaUserID(), mapping.getFacilityID());
			if (existingActive != null) {
				savedMappings.add(existingActive);
				continue;
			}
			// Fix 5: if ASHA already has an active row under a DIFFERENT supervisor → soft-delete it
			ArrayList<AshaSupervisorMapping> otherSupervisorMappings = ashaSupervisorMappingRepo
					.findByAshaUserIDAndFacilityIDAndDeletedFalseAndSupervisorUserIDNot(
							mapping.getAshaUserID(), mapping.getFacilityID(), mapping.getSupervisorUserID());
			for (AshaSupervisorMapping old : otherSupervisorMappings) {
				old.setDeleted(true);
				old.setModifiedBy(mapping.getCreatedBy());
				ashaSupervisorMappingRepo.save(old);
			}
			// Always create new row for clean audit trail (old soft-deleted rows stay as history)
			savedMappings.add(ashaSupervisorMappingRepo.save(mapping));
		}
		return savedMappings;
	}

	@Override
	public ArrayList<AshaSupervisorMapping> getSupervisorMappingByFacility(Integer facilityID) {
		// Use native query that also filters out mappings where supervisor user is deleted
		return ashaSupervisorMappingRepo.findActiveMappingsByFacilityID(facilityID);
	}

	@Override
	public ArrayList<M_UserServiceRoleMapping2> getAshasByFacility(List<Integer> facilityIDs) {
		return employeeMasterRepo.findAshaUsersByFacilityIDs(facilityIDs);
	}

	@Transactional
	@Override
	public void deleteMappings(List<Long> ids, String modifiedBy) {
		for (Long id : ids) {
			AshaSupervisorMapping mapping = ashaSupervisorMappingRepo.findById(id).orElse(null);
			if (mapping != null) {
				mapping.setDeleted(true);
				mapping.setModifiedBy(modifiedBy);
				ashaSupervisorMappingRepo.save(mapping);
			}
		}
	}

	@Transactional
	@Override
	public void deleteBySupervisorAndFacilities(Integer supervisorUserID, List<Integer> facilityIDs, String modifiedBy) {
		ArrayList<AshaSupervisorMapping> mappings = ashaSupervisorMappingRepo
				.findBySupervisorUserIDAndFacilityIDInAndDeletedFalse(supervisorUserID, facilityIDs);
		for (AshaSupervisorMapping mapping : mappings) {
			mapping.setDeleted(true);
			mapping.setModifiedBy(modifiedBy);
			ashaSupervisorMappingRepo.save(mapping);
		}
	}

	@Transactional
	@Override
	public void restoreMappings(List<Long> ids, String modifiedBy) {
		for (Long id : ids) {
			AshaSupervisorMapping mapping = ashaSupervisorMappingRepo.findById(id).orElse(null);
			if (mapping != null) {
				mapping.setDeleted(false);
				mapping.setModifiedBy(modifiedBy);
				ashaSupervisorMappingRepo.save(mapping);
			}
		}
	}

	@Transactional
	@Override
	public void cascadeDeleteByUserID(Integer userID, String modifiedBy) {
		// Soft-delete all rows where this user is supervisor
		ArrayList<AshaSupervisorMapping> asSupervisor = ashaSupervisorMappingRepo
				.findBySupervisorUserIDAndDeletedFalse(userID);
		logger.info("cascadeDeleteByUserID: userID={}, found {} supervisor rows to soft-delete", userID, asSupervisor.size());
		for (AshaSupervisorMapping m : asSupervisor) {
			m.setDeleted(true);
			m.setModifiedBy(modifiedBy);
			ashaSupervisorMappingRepo.save(m);
		}
		// Soft-delete all rows where this user is ASHA
		ArrayList<AshaSupervisorMapping> asAsha = ashaSupervisorMappingRepo
				.findByAshaUserIDAndDeletedFalse(userID);
		logger.info("cascadeDeleteByUserID: userID={}, found {} ASHA rows to soft-delete", userID, asAsha.size());
		for (AshaSupervisorMapping m : asAsha) {
			m.setDeleted(true);
			m.setModifiedBy(modifiedBy);
			ashaSupervisorMappingRepo.save(m);
		}
	}

	@Transactional
	@Override
	public void cascadeDeleteByFacilityID(Integer facilityID, String modifiedBy) {
		// Fix 8: soft-delete all asha_supervisor_mapping rows for a deleted facility
		ArrayList<AshaSupervisorMapping> mappings = ashaSupervisorMappingRepo.findByFacilityIDAndDeletedFalse(facilityID);
		for (AshaSupervisorMapping m : mappings) {
			m.setDeleted(true);
			m.setModifiedBy(modifiedBy);
			ashaSupervisorMappingRepo.save(m);
		}
	}

	@Transactional
	@Override
	public void cascadeDeleteByUserIDAndFacilityID(Integer userID, Integer facilityID, String modifiedBy) {
		// Soft-delete rows where this user is supervisor at this facility
		ArrayList<AshaSupervisorMapping> asSupervisor = ashaSupervisorMappingRepo
				.findBySupervisorUserIDAndFacilityIDAndDeletedFalse(userID, facilityID);
		for (AshaSupervisorMapping m : asSupervisor) {
			m.setDeleted(true);
			m.setModifiedBy(modifiedBy);
			ashaSupervisorMappingRepo.save(m);
		}
		// Soft-delete rows where this user is ASHA at this facility
		ArrayList<AshaSupervisorMapping> asAsha = ashaSupervisorMappingRepo
				.findByAshaUserIDAndFacilityIDAndDeletedFalse(userID, facilityID);
		for (AshaSupervisorMapping m : asAsha) {
			m.setDeleted(true);
			m.setModifiedBy(modifiedBy);
			ashaSupervisorMappingRepo.save(m);
		}
	}

	@Transactional
	@Override
	public ArrayList<AshaSupervisorMapping> updateAshaMappingsAtomically(
			Integer supervisorUserID, List<Integer> facilityIDs,
			List<AshaSupervisorMapping> newMappings, String modifiedBy) {
		// Fix 7: delete old + save new in a SINGLE transaction
		// If anything fails, the entire operation rolls back — no partial wipe
		deleteBySupervisorAndFacilities(supervisorUserID, facilityIDs, modifiedBy);
		if (newMappings != null && !newMappings.isEmpty()) {
			return saveAshaSupervisorMappings(newMappings);
		}
		return new ArrayList<>();
	}
}
