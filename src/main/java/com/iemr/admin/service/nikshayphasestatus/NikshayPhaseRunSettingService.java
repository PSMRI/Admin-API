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
import org.springframework.stereotype.Service;

import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseRunSettingDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayRunMode;
import com.iemr.admin.repo.nikshayphasestatus.NikshayPhaseRunSettingRepository;

/** The run mode of each Ni-kshay phase (BRD FR-19), set by Admin. */
@Service
public class NikshayPhaseRunSettingService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseRunSettingService.class);

	@Autowired
	private NikshayPhaseRunSettingRepository settingRepository;

	public List<NikshayPhaseRunSettingDTO> findAll() {
		return settingRepository.findAll();
	}

	/** Sets the phase's run mode and returns the saved setting. */
	public NikshayPhaseRunSettingDTO setRunMode(NikshayPhase phase, NikshayRunMode runMode, String updatedBy) {
		settingRepository.save(phase, runMode, updatedBy);
		logger.info("Ni-kshay phase {} set to {} by {}", phase, runMode, updatedBy);
		return settingRepository.find(phase);
	}

	/**
	 * Whether the scheduled job runs the phase. A phase with no setting row (V135 not run)
	 * is not run by the job.
	 */
	public boolean isAutomatic(NikshayPhase phase) {
		NikshayPhaseRunSettingDTO setting = settingRepository.find(phase);
		if (setting == null) {
			logger.warn("Ni-kshay phase {} has no run mode in nikshay_phase_run_setting; the job leaves it out", phase);
			return false;
		}
		return setting.getRunMode() == NikshayRunMode.AUTOMATIC;
	}
}
