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
package com.iemr.admin.controller.nikshay;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.nikshay.NikshayDistrict;
import com.iemr.admin.data.nikshay.NikshayFacility;
import com.iemr.admin.data.nikshay.NikshayState;
import com.iemr.admin.data.nikshay.NikshayTU;
import com.iemr.admin.data.nikshay.NikshayVillage;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repo.nikshay.NikshayDistrictRepo;
import com.iemr.admin.repo.nikshay.NikshayFacilityRepo;
import com.iemr.admin.repo.nikshay.NikshayStateRepo;
import com.iemr.admin.repo.nikshay.NikshayTURepo;
import com.iemr.admin.repo.nikshay.NikshayVillageRepo;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Read-only cascading lookups for Stop TB's Nikshay location hierarchy:
 * State -> District -> TU -> Facility -> Village — all sourced from
 * Nikshay's own imported data (m_nikshay_*), independent of AMRIT's own
 * state/district/village masters. Earlier versions matched against AMRIT's
 * existing location tables by name, but AMRIT's own district/block data has
 * real staleness (e.g. post-2022 Andhra Pradesh district reorganization
 * never propagated), which left a large fraction of villages unmatched.
 * This hierarchy avoids that entirely.
 *
 * Every endpoint here only reads existing tables — nothing is inserted or
 * altered from these calls.
 */
@RestController
public class NikshayLocationController {

	private final Logger logger = LoggerFactory.getLogger(NikshayLocationController.class);

	@Autowired
	private NikshayStateRepo nikshayStateRepo;

	@Autowired
	private EmployeeMasterRepo employeeMasterRepo;

	@Autowired
	private NikshayDistrictRepo nikshayDistrictRepo;

	@Autowired
	private NikshayTURepo nikshayTURepo;

	@Autowired
	private NikshayFacilityRepo nikshayFacilityRepo;

	@Autowired
	private NikshayVillageRepo nikshayVillageRepo;

	@Operation(summary = "Get all Nikshay states")
	@GetMapping(value = "/nikshay/location/states", produces = "application/json")
	public String getStates() {
		OutputResponse response = new OutputResponse();
		try {
			List<NikshayState> states = nikshayStateRepo.findAllActive();
			response.setResponse(states.toString());
		} catch (Exception e) {
			logger.error("Error fetching Nikshay states: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get Nikshay districts for a Nikshay state")
	@GetMapping(value = "/nikshay/location/districts", produces = "application/json")
	public String getDistricts(@RequestParam("stateID") Integer stateID) {
		OutputResponse response = new OutputResponse();
		try {
			List<NikshayDistrict> districts = nikshayDistrictRepo.findByStateID(stateID);
			response.setResponse(districts.toString());
		} catch (Exception e) {
			logger.error("Error fetching districts for stateID " + stateID + ": " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get Nikshay TUs for a district")
	@GetMapping(value = "/nikshay/location/tus", produces = "application/json")
	public String getTUs(@RequestParam("districtID") Integer districtID) {
		OutputResponse response = new OutputResponse();
		try {
			List<NikshayTU> tus = nikshayTURepo.findByDistrictID(districtID);
			response.setResponse(tus.toString());
		} catch (Exception e) {
			logger.error("Error fetching Nikshay TUs for districtID " + districtID + ": " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get Nikshay facilities for one or more TUs (comma-separated tuIDs)")
	@GetMapping(value = "/nikshay/location/facilities", produces = "application/json")
	public String getFacilities(@RequestParam("tuIDs") String tuIDs) {
		OutputResponse response = new OutputResponse();
		try {
			List<Integer> ids = parseIntCsv(tuIDs);
			List<NikshayFacility> facilities = nikshayFacilityRepo.findByTUIDs(ids);
			response.setResponse(facilities.toString());
		} catch (Exception e) {
			logger.error("Error fetching Nikshay facilities for tuIDs " + tuIDs + ": " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get Nikshay villages for one or more Nikshay facilities (comma-separated facilityIDs)")
	@GetMapping(value = "/nikshay/location/villages", produces = "application/json")
	public String getVillages(@RequestParam("facilityIDs") String facilityIDs) {
		OutputResponse response = new OutputResponse();
		try {
			List<Integer> ids = parseIntCsv(facilityIDs);
			List<NikshayVillage> villages = nikshayVillageRepo.findByFacilityIDs(ids);
			response.setResponse(villages.toString());
		} catch (Exception e) {
			logger.error("Error fetching villages for facilityIDs " + facilityIDs + ": " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get the Nikshay DistrictID/TUID/FacilityID saved on a Stop TB "
			+ "user-role mapping row, by USRMappingID. Reads m_userservicerolemapping "
			+ "directly (NOT the shared v_userservicerolemapping view, which does not "
			+ "expose these Stop TB-only columns), so the view and every other service "
			+ "line reading it are untouched.")
	@GetMapping(value = "/nikshay/location/userMapping", produces = "application/json")
	public String getUserMappingNikshayData(@RequestParam("usrMappingID") Integer usrMappingID) {
		OutputResponse response = new OutputResponse();
		try {
			M_UserServiceRoleMapping2 row = employeeMasterRepo.findByUSRMappingID(usrMappingID);
			Map<String, Object> result = new HashMap<>();
			if (row != null) {
				result.put("districtID", row.getDistrictID());
				result.put("nikshayTUID", row.getNikshayTUID());
				result.put("nikshayFacilityID", row.getNikshayFacilityID());
			}
			response.setResponse(new Gson().toJson(result));
		} catch (Exception e) {
			logger.error("Error fetching Nikshay data for usrMappingID " + usrMappingID + ": " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	private List<Integer> parseIntCsv(String csv) {
		return Arrays.stream(csv.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}
}
