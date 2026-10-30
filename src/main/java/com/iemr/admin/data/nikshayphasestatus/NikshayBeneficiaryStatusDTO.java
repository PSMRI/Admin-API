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

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/** One beneficiary in the status list, with every phase. */
@Data
public class NikshayBeneficiaryStatusDTO {

	private Long benRegId;
	private Long beneficiaryId;
	private String name;
	// INDEX, CONTACT or ALL_NEGATIVE
	private String beneficiaryType;
	// For a CONTACT: their index patient
	private Long indexBenRegId;
	private String indexName;
	// The beneficiary's Ni-kshay ID, from the ENROLMENT phase
	private String nikshayId;
	// Every phase, keyed by NikshayPhase name, in phase order
	private Map<String, NikshayPhaseDetailDTO> phases = new LinkedHashMap<>();
}
