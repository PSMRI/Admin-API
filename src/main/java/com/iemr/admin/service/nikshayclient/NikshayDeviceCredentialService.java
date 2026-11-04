/*
* AMRIT – Accessible Medical Records via Integrated Technologies
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
package com.iemr.admin.service.nikshayclient;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.nikshay.NikshayDeviceName;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.repo.nikshayupload.NikshayDeviceCredentialRepo;
import com.iemr.admin.utils.crypto.NikshayPasswordCipher;

/**
 * Finds the device PHI login mapped to each village (the device
 * configuration), with the password decrypted. Used by every Ni-kshay phase.
 */
@Service
public class NikshayDeviceCredentialService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayDeviceCredentialService.class);

	@Autowired
	private NikshayDeviceCredentialRepo deviceCredentialRepo;

	@Autowired
	private NikshayPasswordCipher passwordCipher;

	/**
	 * Logins of the given device type (NAAT / XRAY) for the given villages, keyed by
	 * village ID, password decrypted. One active device of this type per village. The same
	 * device may be mapped to other villages. A village with two different active devices
	 * of this type is added to duplicateVillages and left out. Villages with no active
	 * device, or whose password can't be decrypted, are absent from the map; the latter
	 * are also added to undecryptableVillages.
	 */
	public Map<Integer, NikshayDeviceCredentialDTO> findCredentials(Collection<Integer> villageIds, String deviceType,
			Set<Integer> undecryptableVillages, Set<Integer> duplicateVillages) {
		List<Integer> distinctVillageIds = villageIds.stream().filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());
		Map<Integer, NikshayDeviceCredentialDTO> credentialsByVillage = new HashMap<>();
		if (distinctVillageIds.isEmpty()) {
			return credentialsByVillage;
		}
		Map<Integer, NikshayDeviceCredentialDTO> chosen = new HashMap<>();
		for (NikshayDeviceCredentialDTO credential : deviceCredentialRepo.findActiveCredentials(distinctVillageIds,
				NikshayDeviceName.from(deviceType))) {
			NikshayDeviceCredentialDTO existing = chosen.get(credential.getVillageId());
			if (existing == null) {
				chosen.put(credential.getVillageId(), credential);
			} else if (!existing.getDeviceDetailId().equals(credential.getDeviceDetailId())) {
				duplicateVillages.add(credential.getVillageId());
			}
		}
		for (Integer villageId : duplicateVillages) {
			chosen.remove(villageId);
			logger.error("Village {} has more than one active {} device; one per village is required", villageId,
					deviceType);
		}
		for (NikshayDeviceCredentialDTO credential : chosen.values()) {
			try {
				credential.setPassword(passwordCipher.decrypt(credential.getPassword()));
			} catch (IllegalStateException e) {
				logger.error("Skipping village {}: {} for device {}", credential.getVillageId(), e.getMessage(),
						credential.getDeviceSerialNumber());
				undecryptableVillages.add(credential.getVillageId());
				continue;
			}
			credentialsByVillage.put(credential.getVillageId(), credential);
		}
		List<Integer> missing = distinctVillageIds.stream()
				.filter(id -> !credentialsByVillage.containsKey(id) && !undecryptableVillages.contains(id)
						&& !duplicateVillages.contains(id))
				.collect(Collectors.toList());
		if (!missing.isEmpty()) {
			logger.warn("No active {} device mapped for village(s) {}", deviceType, missing);
		}
		return credentialsByVillage;
	}
}
