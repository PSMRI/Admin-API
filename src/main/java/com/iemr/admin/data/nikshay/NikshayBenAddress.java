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
 * Beneficiary address, holding the current village and state (db_identity.i_beneficiaryaddress).
 * Mapped read-only, with only the columns the Ni-kshay upload needs. db_identity
 * is a separate database on the same MySQL server, so it's given as the catalog.
 */
@Data
@Entity
@Table(name = "i_beneficiaryaddress", catalog = "db_identity")
public class NikshayBenAddress {

	@Id
	@Column(name = "BenAddressID")
	private Long benAddressID;

	@Column(name = "CurrVillageId")
	private Integer currVillageId;

	@Column(name = "CurrVillage")
	private String currVillage;

	// AMRIT state ID (m_state), not a Ni-kshay one
	@Column(name = "CurrStateId")
	private Integer currStateId;
}
