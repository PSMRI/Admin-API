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
	 * village ID, password decrypted. Villages with no active device of that type, or
	 * whose password can't be decrypted, are absent from the map; the latter are also
	 * added to undecryptableVillages.
	 */
	public Map<Integer, NikshayDeviceCredentialDTO> findCredentials(Collection<Integer> villageIds, String deviceType,
			Set<Integer> undecryptableVillages) {
		List<Integer> distinctVillageIds = villageIds.stream().filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());
		Map<Integer, NikshayDeviceCredentialDTO> credentialsByVillage = new HashMap<>();
		if (distinctVillageIds.isEmpty()) {
			return credentialsByVillage;
		}
		for (NikshayDeviceCredentialDTO credential : deviceCredentialRepo.findActiveCredentials(distinctVillageIds,
				NikshayDeviceName.from(deviceType))) {
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
				.filter(id -> !credentialsByVillage.containsKey(id) && !undecryptableVillages.contains(id))
				.collect(Collectors.toList());
		if (!missing.isEmpty()) {
			logger.warn("No active {} device mapped for village(s) {}", deviceType, missing);
		}
		return credentialsByVillage;
	}
}
