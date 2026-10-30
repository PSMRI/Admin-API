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
package com.iemr.admin.data.nikshayphasestatus;

import java.sql.Timestamp;

import lombok.Data;

/** One phase of a beneficiary, as shown in the status list. */
@Data
public class NikshayPhaseDetailDTO {

	// NikshayPhaseStatus, or NOT_STARTED when the phase has no row
	private String status;
	private String failureReason;
	private String errorDetail;
	private Integer attempts;
	private String nikshayId;
	private String episodeId;
	private Timestamp lastAttemptAt;
	private Timestamp completedAt;
}
