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
package com.iemr.admin.service.nikshayphasestatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.config.NikshayJobSchedulingConfig;
import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayJobScheduleDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;
import com.iemr.admin.service.nikshayjoblog.NikshayTriggerType;

/**
 * The one automatic Ni-kshay job (BRD FR-19, section 11.2). NikshayJobSchedulingConfig
 * fires it from nikshay_job_schedule (one on/off and one cron for every phase). Each run
 * goes through the phases in order. A phase that is already running (a manual push) is
 * skipped until the next run; a phase that fails does not stop the others. One summary
 * row is written to nikshay_worker_job_history_log.
 */
@Component
public class NikshayPhaseJob {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseJob.class);

	// Shown as triggered_by in the job history log
	static final String TRIGGERED_BY = "nikshay-job";

	// Order of a beneficiary's flow: Ni-kshay ID, the index patient's X-ray and NAAT, contacts,
	// then the contacts' own X-ray and NAAT, then transfer out. The upload phases run again
	// after contacts so a contact enrolled in this run has their results pushed before transfer.
	static final NikshayPhase[] PHASE_ORDER = { NikshayPhase.ENROLMENT, NikshayPhase.XRAY_UPLOAD,
			NikshayPhase.NAAT_UPLOAD, NikshayPhase.CONTACTS, NikshayPhase.XRAY_UPLOAD, NikshayPhase.NAAT_UPLOAD,
			NikshayPhase.TRANSFER_OUT };

	// The batch size caps how many index patients start. The contact X-ray and sputum pass,
	// and transfer, take every one of those households whose results are already done, so
	// contacts do not stay queued behind the batch size. Far above one hour of families.
	private static final int CONTACT_FOLLOW_UP_LIMIT = 10000;

	@Autowired
	private NikshayPhaseRunService phaseRunService;

	@Autowired
	private NikshayPhaseRunSettingService settingService;

	@Autowired
	private NikshayJobHistoryLogService logService;

	public void runAutomaticPhases() {
		NikshayJobScheduleDTO schedule = settingService.findSchedule();
		if (!schedule.isEnabled()) {
			logger.debug("Ni-kshay job is off in nikshay_job_schedule");
			return;
		}
		if (!NikshayJobSchedulingConfig.isDue(schedule.getCronExpression(), ZonedDateTime.now())) {
			logger.debug("Ni-kshay job is on, cron '{}' is not due this minute", schedule.getCronExpression());
			return;
		}
		NikshayJobRunContext context = new NikshayJobRunContext(NikshayTriggerType.JOB, TRIGGERED_BY);
		logger.info("Ni-kshay job {} started", context.getJobRunId());
		List<String> ran = new ArrayList<>();
		List<String> skipped = new ArrayList<>();
		int failedBeneficiaries = 0;
		boolean phaseCrashed = false;
		int uploadPass = 0;
		for (NikshayPhase phase : PHASE_ORDER) {
			try {
				// The first X-ray and TrueNat passes use the batch size. The passes after
				// contacts upload every waiting contact, and transfer sends everyone ready.
				boolean contactFollowUp;
				if (phase == NikshayPhase.XRAY_UPLOAD || phase == NikshayPhase.NAAT_UPLOAD) {
					contactFollowUp = ++uploadPass > 2;
				} else {
					contactFollowUp = phase == NikshayPhase.TRANSFER_OUT;
				}
				List<NikshayErroredBeneficiaryDTO> errored = contactFollowUp
						? phaseRunService.run(phase, null, CONTACT_FOLLOW_UP_LIMIT,
								phase == NikshayPhase.TRANSFER_OUT ? null : "CONTACT", context)
						: phaseRunService.run(phase, null, context);
				failedBeneficiaries += errored.size();
				ran.add(phase.name() + " (" + errored.size() + " failed)");
				logger.info("Ni-kshay job: {} done, {} beneficiaries failed", phase, errored.size());
			} catch (IllegalStateException e) {
				// Already running (a manual push): the next run picks it up
				logger.info("Ni-kshay job: {} skipped: {}", phase, e.getMessage());
				skipped.add(phase.name());
			} catch (Exception e) {
				logger.error("Ni-kshay job: {} could not run", phase, e);
				ran.add(phase.name() + " (could not run)");
				phaseCrashed = true;
			}
		}
		String message = "Cron run. Ran: " + (ran.isEmpty() ? "none" : String.join(", ", ran))
				+ ". Skipped (already running): " + (skipped.isEmpty() ? "none" : String.join(", ", skipped))
				+ ". Beneficiaries failed: " + failedBeneficiaries;
		logger.info("Ni-kshay job {} finished. {}", context.getJobRunId(), message);
		try {
			NikshayJobHistoryLog entry = phaseCrashed
					? logService.failure(context, com.iemr.admin.service.nikshayjoblog.NikshayPhase.JOB, null, null,
							null, message)
					: logService.success(context, com.iemr.admin.service.nikshayjoblog.NikshayPhase.JOB, null, null,
							null, message);
			List<NikshayJobHistoryLog> entries = new ArrayList<>();
			entries.add(entry);
			logService.saveAll(entries);
		} catch (Exception e) {
			logger.error("Ni-kshay job {}: could not write the run summary", context.getJobRunId(), e);
		}
	}
}
