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

import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

import lombok.Data;

/** A beneficiary that cannot be enrolled in Ni-kshay yet, and why. */
@Data
public class NikshayErroredBeneficiaryDTO {

	private Long beneficiaryId;
	// The Ni-kshay phase that failed (NikshayPhase), e.g. ENROLMENT or CONTACTS
	private String phase;
	private NikshayFailureReason failureReason;
	// What is wrong, by BRD section 13 rule, e.g. "V8: Phone must be 10 digits (found 98765)"
	private String reason;

	public NikshayErroredBeneficiaryDTO(Long beneficiaryId, NikshayFailureReason failureReason, String reason) {
		this.beneficiaryId = beneficiaryId;
		this.failureReason = failureReason;
		this.reason = reason;
	}
}
