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
package com.iemr.admin.data.nikshay;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * Beneficiary details, holding the Ni-kshay ID (db_identity.i_beneficiarydetails).
 * Mapped read-only, with only the columns the Ni-kshay upload needs. db_identity
 * is a separate database on the same MySQL server, so it's given as the catalog.
 */
@Data
@Entity
@Table(name = "i_beneficiarydetails", catalog = "db_identity")
public class NikshayBenDetails {

	@Id
	@Column(name = "BeneficiaryDetailsId")
	private Long beneficiaryDetailsId;

	@Column(name = "nikshayId")
	private String nikshayId;

	@Column(name = "Deleted")
	private Boolean deleted;
}
