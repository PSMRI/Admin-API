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
package com.iemr.admin.service.villagemapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.model.villagemapping.VillageUpdateRequest;
import com.iemr.admin.model.villagemapping.VillageUpdateResponse;
import com.iemr.admin.repository.villagemapping.VillageUpdateRepository;

@Service
public class VillageUpdateServiceImpl implements VillageUpdateService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final int MAX_VILLAGE_IDS_LENGTH = 100;
	private static final int MAX_VILLAGE_NAMES_LENGTH = 500;

	@Autowired
	private VillageUpdateRepository villageUpdateRepository;

	/**
	 * Replaces one village on a work location mapping and moves the beneficiary
	 * addresses that user registered (i_beneficiaryaddress.CreatedBy) to the new
	 * village, in a single transaction.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public VillageUpdateResponse updateVillage(VillageUpdateRequest request) throws Exception {
		validate(request);

		Object[] mapping = villageUpdateRepository.lockMapping(request.getuSRMappingID());
		if (mapping == null) {
			throw new IllegalArgumentException("No work location mapping found with ID " + request.getuSRMappingID());
		}
		Integer mappedUserID = ((Number) mapping[0]).intValue();
		if (!mappedUserID.equals(request.getUserID())) {
			throw new IllegalArgumentException("Mapping " + request.getuSRMappingID() + " does not belong to user "
					+ request.getUserID() + ". Reload the user and try again.");
		}
		if (isTrue(mapping[3])) {
			throw new IllegalArgumentException("Mapping " + request.getuSRMappingID() + " is deactivated");
		}

		String userName = villageUpdateRepository.userName(request.getUserID());
		if (userName == null) {
			throw new IllegalArgumentException("No user found with ID " + request.getUserID());
		}

		String newVillageName = trimToNull(villageUpdateRepository.villageName(request.getNewVillageID()));
		if (newVillageName == null) {
			throw new IllegalArgumentException("No active village found with ID " + request.getNewVillageID());
		}

		List<String> villageIDs = split((String) mapping[1]);
		List<String> villageNames = split((String) mapping[2]);
		String oldVillageID = String.valueOf(request.getOldVillageID());
		String newVillageID = String.valueOf(request.getNewVillageID());

		int index = villageIDs.indexOf(oldVillageID);
		if (index < 0) {
			throw new IllegalArgumentException("Village " + oldVillageID + " is no longer mapped to mapping "
					+ request.getuSRMappingID() + ". Reload the user and try again.");
		}
		if (villageIDs.contains(newVillageID)) {
			throw new IllegalArgumentException("Village " + newVillageName + " is already mapped to this user");
		}

		String oldVillageName = index < villageNames.size() ? villageNames.get(index) : request.getOldVillageName();
		villageIDs.set(index, newVillageID);
		while (villageNames.size() < villageIDs.size()) {
			villageNames.add("");
		}
		villageNames.set(index, newVillageName);

		String joinedIDs = String.join(",", villageIDs);
		String joinedNames = String.join(",", villageNames);
		if (joinedIDs.length() > MAX_VILLAGE_IDS_LENGTH || joinedNames.length() > MAX_VILLAGE_NAMES_LENGTH) {
			throw new IllegalArgumentException(
					"Updated village list is too long to store on the mapping (Villageid/VillageName limit)");
		}

		String modifiedBy = trimToNull(request.getModifiedBy());
		villageUpdateRepository.updateMappingVillages(request.getuSRMappingID(), joinedIDs, joinedNames, modifiedBy);

		// A single-village user registered every beneficiary under that village,
		// so all of their addresses move. With several villages, only addresses on
		// the replaced village move, so the other villages are left as they are.
		long permanent;
		long current;
		boolean allAddresses = villageIDs.size() == 1;
		if (allAddresses) {
			permanent = villageUpdateRepository.updateAllAddressVillages(userName, request.getNewVillageID(),
					newVillageName, modifiedBy);
			current = permanent;
		} else {
			permanent = villageUpdateRepository.updatePermanentVillage(userName, request.getOldVillageID(),
					request.getNewVillageID(), newVillageName, modifiedBy);
			current = villageUpdateRepository.updateCurrentVillage(userName, request.getOldVillageID(),
					request.getNewVillageID(), newVillageName, modifiedBy);
		}

		logger.info("Village update complete: uSRMappingID {}, village {} -> {}, allAddresses {}, perm {} curr {}",
				request.getuSRMappingID(), oldVillageID, newVillageID, allAddresses, permanent, current);

		VillageUpdateResponse response = new VillageUpdateResponse();
		response.setuSRMappingID(request.getuSRMappingID());
		response.setUserName(userName);
		response.setOldVillageID(request.getOldVillageID());
		response.setOldVillageName(oldVillageName);
		response.setNewVillageID(request.getNewVillageID());
		response.setNewVillageName(newVillageName);
		response.setPermanentAddressesUpdated(permanent);
		response.setCurrentAddressesUpdated(current);
		response.setAllAddressesUpdated(allAddresses);
		return response;
	}

	private void validate(VillageUpdateRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		if (request.getUserID() == null || request.getuSRMappingID() == null) {
			throw new IllegalArgumentException("User ID and mapping ID are required");
		}
		if (request.getOldVillageID() == null || request.getNewVillageID() == null) {
			throw new IllegalArgumentException("Old and new village are required");
		}
		if (request.getOldVillageID().equals(request.getNewVillageID())) {
			throw new IllegalArgumentException("The new village is the same as the old one");
		}
		if (trimToNull(request.getModifiedBy()) == null) {
			throw new IllegalArgumentException("Modified by is required");
		}
	}

	private List<String> split(String csv) {
		List<String> values = new ArrayList<>();
		if (csv == null || csv.trim().isEmpty()) {
			return values;
		}
		for (String value : Arrays.asList(csv.split(","))) {
			values.add(value.trim());
		}
		return values;
	}

	private boolean isTrue(Object value) {
		if (value instanceof Boolean bool) {
			return bool;
		}
		if (value instanceof Number number) {
			return number.intValue() != 0;
		}
		if (value instanceof byte[] bytes) {
			return bytes.length > 0 && bytes[0] != 0;
		}
		return false;
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
