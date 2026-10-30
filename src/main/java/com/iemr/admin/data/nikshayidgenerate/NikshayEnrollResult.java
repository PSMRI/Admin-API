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
package com.iemr.admin.data.nikshayidgenerate;

import lombok.Data;

/** The outcome of enrolling one beneficiary in Ni-kshay. */
@Data
public class NikshayEnrollResult {

	private Long beneficiaryId;
	// The person's own Ni-kshay ID: the fresh or existing record, also when a new episode
	// was created under it (the parent ID). Saved on i_beneficiarydetails.nikshayId
	private String nikshayId;
	// The episode to use for tests, contacts and transfer: the new episode's ID when one was
	// created, otherwise the same as nikshayId
	private String episodeId;
	private NikshayLinkStatus linkStatus;
	// Stage of the duplicate found in Ni-kshay at the time of the check; null when none
	private String stageFound;
	// NAAT device (m_nikshay_device_configuration.id) whose login enrolled the beneficiary
	private Integer deviceId;
	// Set by the flow before the enrolment is saved
	private NikshayFlowScenario flowScenario;

	public NikshayEnrollResult(Long beneficiaryId, String nikshayId, String episodeId, NikshayLinkStatus linkStatus,
			String stageFound, Integer deviceId) {
		this.beneficiaryId = beneficiaryId;
		this.nikshayId = nikshayId;
		this.episodeId = episodeId;
		this.linkStatus = linkStatus;
		this.stageFound = stageFound;
		this.deviceId = deviceId;
	}
}
