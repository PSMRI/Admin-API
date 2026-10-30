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
 * BeneficiaryID -> BenRegId mapping (db_identity.m_beneficiaryregidmapping).
 * Mapped read-only, with only the columns the Ni-kshay upload needs. db_identity
 * is a separate database on the same MySQL server, so it's given as the catalog.
 */
@Data
@Entity
@Table(name = "m_beneficiaryregidmapping", catalog = "db_identity")
public class NikshayBenRegIdMapping {

	@Id
	@Column(name = "BenRegId")
	private Long benRegId;

	@Column(name = "BeneficiaryID")
	private Long beneficiaryID;

	@Column(name = "Deleted")
	private Boolean deleted;
}
