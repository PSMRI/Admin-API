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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;
import com.iemr.admin.service.nikshayjoblog.NikshayTriggerType;

/**
 * The automatic Ni-kshay job (BRD FR-19, section 11.2), scheduled by
 * NikshayJobSchedulingConfig from nikshay.job.cron. Each run goes through the phases in
 * order and runs those set to AUTOMATIC, one batch each, as a manual run would. A phase
 * that is already running (a manual push) is skipped until the next run; a
 * phase that fails does not stop the others.
 */
@Component
public class NikshayPhaseJob {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseJob.class);

	// Shown as triggered_by in the job history log
	static final String TRIGGERED_BY = "nikshay-job";

	// Order of a beneficiary's flow: Ni-kshay ID, X-ray, NAAT, contacts, transfer out
	static final NikshayPhase[] PHASE_ORDER = { NikshayPhase.ENROLMENT, NikshayPhase.XRAY_UPLOAD,
			NikshayPhase.NAAT_UPLOAD, NikshayPhase.CONTACTS, NikshayPhase.TRANSFER_OUT };

	@Autowired
	private NikshayPhaseRunService phaseRunService;

	@Autowired
	private NikshayPhaseRunSettingService settingService;

	public void runAutomaticPhases() {
		NikshayJobRunContext context = new NikshayJobRunContext(NikshayTriggerType.JOB, TRIGGERED_BY);
		logger.info("Ni-kshay job {} started", context.getJobRunId());
		for (NikshayPhase phase : PHASE_ORDER) {
			try {
				if (!settingService.isAutomatic(phase)) {
					logger.info("Ni-kshay job: {} is not automatic, left out", phase);
					continue;
				}
				List<NikshayErroredBeneficiaryDTO> errored = phaseRunService.run(phase, null, context);
				logger.info("Ni-kshay job: {} done, {} beneficiaries failed", phase, errored.size());
			} catch (IllegalStateException e) {
				// Already running (a manual push): the next run picks it up
				logger.info("Ni-kshay job: {} skipped: {}", phase, e.getMessage());
			} catch (Exception e) {
				logger.error("Ni-kshay job: {} could not run", phase, e);
			}
		}
		logger.info("Ni-kshay job {} finished", context.getJobRunId());
	}
}
