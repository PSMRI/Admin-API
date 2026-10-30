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

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;

import lombok.Getter;

/**
 * One run of a Ni-kshay phase: who triggered it, the beneficiaries that failed (the run's
 * response) and the job history log entries, written once when the run ends.
 */
@Getter
public class NikshayPhaseRun {
	private final NikshayJobRunContext context;
	private final List<NikshayErroredBeneficiaryDTO> erroredBeneficiaries = new ArrayList<>();
	private final List<NikshayJobHistoryLog> logEntries = new ArrayList<>();

	public NikshayPhaseRun(NikshayJobRunContext context) {
		this.context = context;
	}
}
