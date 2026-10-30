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

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryType;

import lombok.Data;

/** One row of the Ni-kshay ID generation eligibility query: a beneficiary to enrol in Ni-kshay. */
@Data
public class NikshayIdGenerateBeneficiaryDTO {

	private Long beneficiaryId;
	private Long houseoldId;

	private String firstName;
	private String lastName;
	private String fathersName;
	private Integer age;
	// dd-MM-yyyy
	private String dateOfBirth;
	// Male / Female / Transgender
	private String gender;
	private String primaryPhone;
	private String address;
	private String pincode;
	private String area;
	private String maritalStatus;
	private String occupation;
	private String socioeconomicStatus;
	// SC / ST / Others
	private String caste;
	private String keyPopulation;
	// Comma separated, ends with a comma, e.g. "Fever,Night sweats," or "Asymptomatic,"
	private String symptom;
	// Reactive / Non-Reactive / Unknown
	private String hivStatus;

	// Ni-kshay hierarchy: village (residence) and its facility, TU, district and state
	private Long residenceHierarchyId;
	// m_nikshay_village.NikshayVillageID (AMRIT's own number, used for the device mapping);
	// null when the household village ID is not found in m_nikshay_village
	private Integer nikshayVillageId;
	private String nikshayVillageName;
	// The village's ID in Ni-kshay, looked up on the portal by name before enrolment
	private Long nikshayPortalVillageId;
	// Ni-kshay's own IDs (NikshayCode of m_nikshay_state / district / tu / facility)
	private Long nikshayStateId;
	private Long nikshayDistrictId;
	private Long nikshayTuId;
	private Long selectedHierarchyId;

	// INDEX, CONTACT or ALL_NEGATIVE, for the phase status (nikshay_beneficiary_phase_status)
	private NikshayBeneficiaryType beneficiaryType;
	// For a CONTACT: the BenRegId of their index patient
	private Long indexBeneficiaryId;

	// The other members of this beneficiary's household, with their TB screening status
	private List<NikshayHouseholdMemberDTO> householdMembers = new ArrayList<>();
}
