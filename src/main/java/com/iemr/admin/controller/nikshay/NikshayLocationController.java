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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.nikshay.NikshayFacility;
import com.iemr.admin.data.nikshay.NikshayTU;
import com.iemr.admin.data.nikshay.NikshayVillageFacilityMapping;
import com.iemr.admin.data.rolemaster.StateMasterForRole;
import com.iemr.admin.repo.locationmaster.DistrictBranchMappingRepo;
import com.iemr.admin.repo.locationmaster.MdistrictRepo;
import com.iemr.admin.repo.nikshay.NikshayFacilityRepo;
import com.iemr.admin.repo.nikshay.NikshayTURepo;
import com.iemr.admin.repo.nikshay.NikshayVillageFacilityMappingRepo;
import com.iemr.admin.repository.rolemaster.StateMasterRepo;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Read-only cascading lookups for Stop TB's Nikshay location hierarchy:
 * State (reused, existing) -> District (reused, existing) ->
 * TU -> Facility -> Village (all new, or reused via the village mapping).
 *
 * Every endpoint here only reads existing/new tables — nothing is inserted
 * or altered from these calls.
 */
@RestController
public class NikshayLocationController {

	private final Logger logger = LoggerFactory.getLogger(NikshayLocationController.class);

	@Autowired
	private StateMasterRepo stateMasterRepo;

	@Autowired
	private MdistrictRepo mdistrictRepo;

	@Autowired
	private NikshayTURepo nikshayTURepo;

	@Autowired
	private NikshayFacilityRepo nikshayFacilityRepo;

	@Autowired
	private NikshayVillageFacilityMappingRepo nikshayVillageFacilityMappingRepo;

	@Autowired
	private DistrictBranchMappingRepo districtBranchMappingRepo;

	@Operation(summary = "Get all states (reused from AMRIT's existing state master)")
	@GetMapping(value = "/nikshay/location/states", produces = "application/json")
	public String getStates() {
		OutputResponse response = new OutputResponse();
		try {
			ArrayList<StateMasterForRole> states = stateMasterRepo.getAllState();
			response.setResponse(states.toString());
		} catch (Exception e) {
			logger.error("Error fetching Nikshay states: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get districts for a state (reused from AMRIT's existing district master)")
	@GetMapping(value = "/nikshay/location/districts", produces = "application/json")
	public String getDistricts(@RequestParam("stateID") Integer stateID) {
		OutputResponse response = new OutputResponse();
		try {
			ArrayList<M_District> districts = mdistrictRepo.getAllDistrictByStateId(stateID);
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

	@Operation(summary = "Get villages for one or more Nikshay facilities (comma-separated facilityIDs), "
			+ "resolved to AMRIT's existing village master")
	@GetMapping(value = "/nikshay/location/villages", produces = "application/json")
	public String getVillages(@RequestParam("facilityIDs") String facilityIDs) {
		OutputResponse response = new OutputResponse();
		try {
			List<Integer> ids = parseIntCsv(facilityIDs);
			List<NikshayVillageFacilityMapping> mappings = nikshayVillageFacilityMappingRepo.findByFacilityIDs(ids);

			List<Integer> amritVillageIDs = mappings.stream()
					.map(NikshayVillageFacilityMapping::getAmritVillageID)
					.distinct()
					.collect(Collectors.toList());

			List<DistrictBranchMapping> villages = new ArrayList<>();
			districtBranchMappingRepo.findAllById(amritVillageIDs).forEach(villages::add);

			response.setResponse(villages.toString());
		} catch (Exception e) {
			logger.error("Error fetching villages for facilityIDs " + facilityIDs + ": " + e.getMessage(), e);
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
