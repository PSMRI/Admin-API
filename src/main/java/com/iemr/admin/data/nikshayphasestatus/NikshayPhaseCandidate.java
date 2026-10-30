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

import lombok.AllArgsConstructor;
import lombok.Data;

/** A beneficiary picked for a phase run, from their nikshay_beneficiary_phase_status row. */
@Data
@AllArgsConstructor
public class NikshayPhaseCandidate {
	private Long benRegId;
	// BeneficiaryID (db_identity.m_beneficiaryregidmapping); null if not found
	private Long beneficiaryId;
	private NikshayBeneficiaryType beneficiaryType;
	// For a CONTACT: BenRegId of their index patient
	private Long indexBenRegId;
	// Status of the row the beneficiary was picked by (null when the row does not exist)
	private String status;
}
