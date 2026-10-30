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
package com.iemr.admin.service.nikshayphasestatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.service.nikshayidgenerate.NikshayIdGenerateService;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;
import com.iemr.admin.service.nikshaytransferout.NikshayTransferOutService;
import com.iemr.admin.service.nikshayupload.NikshayUploadService;

/**
 * Runs one Ni-kshay phase: ENROLMENT, XRAY_UPLOAD, NAAT_UPLOAD (MTB and Rif), CONTACTS or
 * TRANSFER_OUT. Only one run of a phase at a time; each run takes up to
 * nikshay.phase.batch-size beneficiaries and writes its job history log entries when it ends.
 */
@Service
public class NikshayPhaseRunService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseRunService.class);

	@Value("${nikshay.phase.batch-size:200}")
	private int batchSize;

	@Autowired
	private NikshayIdGenerateService idGenerateService;

	@Autowired
	private NikshayUploadService uploadService;

	@Autowired
	private NikshayTransferOutService transferOutService;

	@Autowired
	private NikshayPhaseStatusService phaseStatusService;

	@Autowired
	private NikshayJobHistoryLogService logService;

	// Phases running now (one Admin-API server)
	private final Set<NikshayPhase> running = new HashSet<>();

	/**
	 * Runs the phase for the beneficiaries waiting in it, or for the given BenRegIds only
	 * (null = waiting ones). Returns the beneficiaries that failed. Throws
	 * IllegalStateException when the phase is already running.
	 */
	public List<NikshayErroredBeneficiaryDTO> run(NikshayPhase phase, List<Long> benRegIds,
			NikshayJobRunContext context) {
		synchronized (running) {
			if (!running.add(phase)) {
				throw new IllegalStateException(phase + " is already running");
			}
		}
		NikshayPhaseRun run = new NikshayPhaseRun(context);
		logger.info("{} run {} started by {} ({})", phase, context.getJobRunId(), context.getTriggeredBy(),
				benRegIds == null ? "waiting beneficiaries" : benRegIds.size() + " given beneficiaries");
		try {
			switch (phase) {
				case ENROLMENT:
					idGenerateService.runEnrolment(benRegIds, batchSize, run);
					break;
				case NAAT_UPLOAD:
				case XRAY_UPLOAD:
					uploadService.runPhase(phase, benRegIds, batchSize, run);
					break;
				case CONTACTS:
					idGenerateService.runContacts(benRegIds, batchSize, run);
					break;
				case TRANSFER_OUT:
					transferOutService.runTransferOut(benRegIds, batchSize, run);
					break;
				default:
					break;
			}
		} finally {
			synchronized (running) {
				running.remove(phase);
			}
			try {
				logService.saveAll(run.getLogEntries());
			} catch (Exception e) {
				logger.error("Could not write {} Ni-kshay history log entries", run.getLogEntries().size(), e);
			}
		}
		logger.info("{} run {} finished: {} beneficiaries failed", phase, context.getJobRunId(),
				run.getErroredBeneficiaries().size());
		return run.getErroredBeneficiaries();
	}

	/**
	 * Sets the given beneficiaries back to PENDING in the phase (reprocess); with runNow,
	 * runs the phase for them straight away and returns the ones that failed. Returns null
	 * when not run now.
	 */
	public List<NikshayErroredBeneficiaryDTO> reprocess(NikshayPhase phase, List<Long> benRegIds, boolean runNow,
			NikshayJobRunContext context) {
		int count = phaseStatusService.reprocess(phase, benRegIds);
		logger.info("{}: {} of {} beneficiaries set for reprocess", phase, count, benRegIds.size());
		return runNow ? run(phase, benRegIds, context) : null;
	}
}
