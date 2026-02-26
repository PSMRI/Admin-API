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
package com.iemr.admin.controller.employeemaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.employeemaster.AshaSupervisorMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.service.employeemaster.AshaSupervisorMappingService;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AshaSupervisorMappingController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private AshaSupervisorMappingService ashaSupervisorMappingService;

	@Autowired
	private EmployeeMasterRepo employeeMasterRepo;

	@Autowired
	private MainStoreRepo mainStoreRepo;

	@Operation(summary = "Get ASHA users by facility IDs")
	@RequestMapping(value = "/userFacilityMapping/getAshasByFacility", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAshasByFacility(@RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			AshaSupervisorMapping reqObj = InputMapper.gson().fromJson(request, AshaSupervisorMapping.class);
			List<Integer> facilityIDs = reqObj.getFacilityIDs();
			if (facilityIDs == null || facilityIDs.isEmpty()) {
				if (reqObj.getFacilityID() != null) {
					facilityIDs = Arrays.asList(reqObj.getFacilityID());
				}
			}
			ArrayList<M_UserServiceRoleMapping2> ashaUsers = ashaSupervisorMappingService
					.getAshasByFacility(facilityIDs);
			response.setResponse(ashaUsers.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Save ASHA supervisor mappings")
	@RequestMapping(value = "/userFacilityMapping/ashaSupervisorMapping/save", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveAshaSupervisorMapping(@RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			AshaSupervisorMapping[] reqArray = InputMapper.gson().fromJson(request, AshaSupervisorMapping[].class);
			List<AshaSupervisorMapping> mappings = Arrays.asList(reqArray);
			ArrayList<AshaSupervisorMapping> savedMappings = ashaSupervisorMappingService
					.saveAshaSupervisorMappings(mappings);
			response.setResponse(savedMappings.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get supervisor mappings by facility ID")
	@RequestMapping(value = "/userFacilityMapping/ashaSupervisorMapping/getByFacility", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getSupervisorMappingByFacility(@RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			AshaSupervisorMapping reqObj = InputMapper.gson().fromJson(request, AshaSupervisorMapping.class);
			ArrayList<AshaSupervisorMapping> mappings = ashaSupervisorMappingService
					.getSupervisorMappingByFacility(reqObj.getFacilityID());
			response.setResponse(mappings.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get facility details by USR mapping ID")
	@RequestMapping(value = "/userFacilityMapping/getFacilityByMappingID", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getFacilityByMappingID(@RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			M_UserServiceRoleMapping2 reqObj = InputMapper.gson().fromJson(request, M_UserServiceRoleMapping2.class);
			Integer mappingID = reqObj.getuSRMappingID();
			if (mappingID != null) {
				M_UserServiceRoleMapping2 mapping = employeeMasterRepo.findById(mappingID).orElse(null);
				if (mapping != null && mapping.getFacilityID() != null) {
					M_Facility facility = mainStoreRepo.findById(mapping.getFacilityID()).orElse(null);
					StringBuilder json = new StringBuilder("{");
					json.append("\"facilityID\": ").append(mapping.getFacilityID());
					if (facility != null) {
						json.append(", \"facilityName\": \"").append(facility.getFacilityName() != null ? facility.getFacilityName() : "").append("\"");
						json.append(", \"facilityTypeID\": ").append(facility.getFacilityTypeID());
						json.append(", \"ruralUrban\": \"").append(facility.getRuralUrban() != null ? facility.getRuralUrban() : "").append("\"");
					}
					json.append("}");
					response.setResponse(json.toString());
				} else {
					response.setResponse("{\"facilityID\": null}");
				}
			} else {
				response.setResponse("{\"facilityID\": null}");
			}
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}
}
