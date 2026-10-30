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
package com.iemr.admin.data.nikshayupload;

import java.sql.Timestamp;

import lombok.Data;

/** One completed chest X-ray result for a beneficiary visit. */
@Data
public class XrayResultDTO implements NikshayTestResult {

	private Long beneficiaryId;
	private Long benRegId;
	private String nikshayId;
	// Beneficiary's current village (i_beneficiaryaddress.CurrVillageId / CurrVillage)
	private Integer villageId;
	private String village;
	// Name of the same ID in m_nikshay_village. Differs from village when the ID was saved
	// as an AMRIT village ID (registrations before the Stop TB fix of 2026-07-28).
	private String nikshayVillageName;
	// Beneficiary's current state as saved in AMRIT (i_beneficiaryaddress.CurrStateId)
	private Integer stateId;
	// Ni-kshay state code of the village, via m_nikshay_village -> facility -> TU -> district -> state
	private String nikshayStateCode;
	// Ni-kshay health facility (PHI) the village belongs to (m_nikshay_facility)
	private String facilityName;
	private String nikshayFacilityCode;
	private Long visitCode;
	private Integer vanID;
	private String externalOrderId;
	// EMR Lite summary: "Normal" / "TB Presumptive" / "Abnormal but not TB Presumptive"
	private String result;
	private Boolean tbPresence;
	private Timestamp orderedOn;
	private Timestamp resultDate;

	// Used by the JPQL constructor expression in DiagnosticOrderRepo — keep the argument order in sync.
	public XrayResultDTO(Long beneficiaryId, Long benRegId, String nikshayId, Integer villageId, String village,
			String nikshayVillageName, Integer stateId, String nikshayStateCode,
			String facilityName, String nikshayFacilityCode, Long visitCode,
			Integer vanID, String externalOrderId, String result, Boolean tbPresence, Timestamp orderedOn,
			Timestamp resultDate) {
		this.beneficiaryId = beneficiaryId;
		this.benRegId = benRegId;
		this.nikshayId = nikshayId;
		this.villageId = villageId;
		this.village = village;
		this.nikshayVillageName = nikshayVillageName;
		this.stateId = stateId;
		this.nikshayStateCode = nikshayStateCode;
		this.facilityName = facilityName;
		this.nikshayFacilityCode = nikshayFacilityCode;
		this.visitCode = visitCode;
		this.vanID = vanID;
		this.externalOrderId = externalOrderId;
		this.result = result;
		this.tbPresence = tbPresence;
		this.orderedOn = orderedOn;
		this.resultDate = resultDate;
	}
}
