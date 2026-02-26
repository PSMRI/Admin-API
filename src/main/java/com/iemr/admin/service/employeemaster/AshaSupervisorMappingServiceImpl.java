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

import com.iemr.admin.data.employeemaster.AshaSupervisorMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repository.user.AshaSupervisorMappingRepo;

@Service
public class AshaSupervisorMappingServiceImpl implements AshaSupervisorMappingService {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private AshaSupervisorMappingRepo ashaSupervisorMappingRepo;

	@Autowired
	private EmployeeMasterRepo employeeMasterRepo;

	@Override
	public ArrayList<AshaSupervisorMapping> saveAshaSupervisorMappings(List<AshaSupervisorMapping> mappings) {
		ArrayList<AshaSupervisorMapping> savedMappings = new ArrayList<>();
		for (AshaSupervisorMapping mapping : mappings) {
			savedMappings.add(ashaSupervisorMappingRepo.save(mapping));
		}
		return savedMappings;
	}

	@Override
	public ArrayList<AshaSupervisorMapping> getSupervisorMappingByFacility(Integer facilityID) {
		return ashaSupervisorMappingRepo.findByFacilityIDAndDeletedFalse(facilityID);
	}

	@Override
	public ArrayList<M_UserServiceRoleMapping2> getAshasByFacility(List<Integer> facilityIDs) {
		return employeeMasterRepo.findAshaUsersByFacilityIDs(facilityIDs);
	}

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
}
