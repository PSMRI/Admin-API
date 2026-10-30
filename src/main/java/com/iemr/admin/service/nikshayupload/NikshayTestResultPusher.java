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
package com.iemr.admin.service.nikshayupload;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.data.nikshayupload.NikshayTestResult;
import com.iemr.admin.data.nikshayupload.NikshayVillageBatch;
import com.iemr.admin.service.nikshayclient.NikshayAuthClient;
import com.iemr.admin.service.nikshayclient.NikshayDiagnosticsTestClient;
import com.iemr.admin.service.nikshayclient.NikshayDiagnosticsTestClient.AddedTest;
import com.iemr.admin.service.nikshayclient.NikshayLoginException;
import com.iemr.admin.service.nikshayclient.NikshaySession;
import com.iemr.admin.service.nikshayclient.NikshayApiException;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;
import com.iemr.admin.service.nikshayjoblog.NikshayPhase;

/**
 * Pushes X-ray or NAAT results to Ni-kshay; the builder decides the payload.
 *
 * 1. Group the results by device (villages sharing a device share one login).
 * 2. For each device: log in with its PHI login, then add each result's test.
 * Every result gets a SUCCESS or FAILED history entry. Results already uploaded
 * (a SUCCESS entry for the same order) are skipped, so a re-run never adds a test twice.
 */
@Service
public class NikshayTestResultPusher {
	private static final Logger logger = LoggerFactory.getLogger(NikshayTestResultPusher.class);

	@Autowired
	private NikshayAuthClient authClient;

	@Autowired
	private NikshayDiagnosticsTestClient testClient;

	@Autowired
	private NikshayJobHistoryLogService logService;

	public void push(Map<Integer, NikshayVillageBatch> batchesByVillage,
			NikshayTestPayloadBuilder builder, NikshayJobRunContext ctx, List<NikshayJobHistoryLog> logEntries) {
		NikshayPhase phase = builder.getTestType().getPhase();
		Map<NikshayDeviceCredentialDTO, List<NikshayTestResult>> resultsByDevice = resultsByDevice(batchesByVillage, phase);

		for (Map.Entry<NikshayDeviceCredentialDTO, List<NikshayTestResult>> entry : resultsByDevice.entrySet()) {
			NikshayDeviceCredentialDTO device = entry.getKey();
			List<NikshayTestResult> results = entry.getValue();

			NikshaySession session;
			try {
				session = authClient.login(device.getPhiUserId(), device.getPassword());
			} catch (NikshayLoginException e) {
				// Without a login none of this device's results can be sent
				for (NikshayTestResult result : results) {
					logEntries.add(logService.failure(ctx, phase, result.getBenRegId(), result.getExternalOrderId(),
							e.getReason(), "Device " + device.getDeviceSerialNumber() + ": " + e.getMessage()));
				}
				continue;
			}

			logger.info("Pushing {} {} result(s) with device {}", results.size(), phase, device.getDeviceSerialNumber());
			for (NikshayTestResult result : results) {
				try {
					ObjectNode payload = builder.build(result, device);
					AddedTest added = testClient.addDiagnosticsTest(session, payload);
					logEntries.add(logService.success(ctx, phase, result.getBenRegId(), result.getExternalOrderId(),
							null, "Added by device " + device.getDeviceSerialNumber() + ": RequestId=" + added.getRequestId()
									+ ", ResultId=" + added.getResultId()));
				} catch (NikshayApiException e) {
					logEntries.add(logService.failure(ctx, phase, result.getBenRegId(), result.getExternalOrderId(), e));
				}
			}
		}
	}

	/** The results still to upload, grouped by the device whose login sends them. */
	private Map<NikshayDeviceCredentialDTO, List<NikshayTestResult>> resultsByDevice(
			Map<Integer, NikshayVillageBatch> batchesByVillage, NikshayPhase phase) {
		List<String> orderIds = batchesByVillage.values().stream().flatMap(b -> b.getResults().stream())
				.map(NikshayTestResult::getExternalOrderId).collect(Collectors.toList());
		Set<String> alreadyUploaded = logService.uploadedReferenceIds(phase, orderIds);

		Map<Integer, NikshayDeviceCredentialDTO> deviceById = new LinkedHashMap<>();
		Map<NikshayDeviceCredentialDTO, List<NikshayTestResult>> resultsByDevice = new LinkedHashMap<>();
		for (NikshayVillageBatch batch : batchesByVillage.values()) {
			// A device serving several villages has one credential per village (they differ only
			// in villageId), so the first one seen is used as that device's key
			NikshayDeviceCredentialDTO device = deviceById.computeIfAbsent(batch.getCredential().getDeviceDetailId(),
					id -> batch.getCredential());
			for (NikshayTestResult result : batch.getResults()) {
				if (alreadyUploaded.contains(result.getExternalOrderId())) {
					logger.info("Skipping {} order {}: already uploaded", phase, result.getExternalOrderId());
				} else {
					resultsByDevice.computeIfAbsent(device, d -> new ArrayList<>()).add(result);
				}
			}
		}
		return resultsByDevice;
	}
}
