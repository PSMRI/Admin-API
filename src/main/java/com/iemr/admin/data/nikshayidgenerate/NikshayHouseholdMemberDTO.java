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

import java.sql.Timestamp;

import lombok.Data;

/** A household member of an index patient (same houseoldId), with their TB screening status. */
@Data
public class NikshayHouseholdMemberDTO {

	private Long beneficiaryId;
	private Long houseoldId;

	private String firstName;
	private String lastName;
	private Integer age;
	// dd-MM-yyyy
	private String dateOfBirth;
	// Male / Female / Transgender
	private String gender;
	private String primaryPhone;
	// Already in Ni-kshay when not null
	private String nikshayId;
	// SC / ST / Others
	private String caste;
	// From the latest screening, e.g. "Fever," or "Asymptomatic,"
	private String symptom;
	// Reactive / Non-Reactive / Unknown
	private String hivStatus;

	// True when the member has a tb_screening row (latest, not deleted)
	private Boolean tbScreened;
	// Visit date of that latest screening; null when not screened
	private Timestamp screeningDate;
}
