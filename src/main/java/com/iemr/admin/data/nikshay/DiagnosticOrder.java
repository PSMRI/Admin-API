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

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * Diagnostic device order (X-ray / TrueNat), written by FLW-API on the van and
 * synced here. Mapped read-only, with only the columns the Ni-kshay upload needs.
 */
@Data
@Entity
@Table(name = "tb_diagnostic_order")
public class DiagnosticOrder {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "beneficiary_id")
	private Long beneficiaryId;

	@Column(name = "visitCode")
	private Long visitCode;

	@Column(name = "vanID")
	private Integer vanID;

	@Column(name = "order_type")
	private String orderType;

	@Column(name = "external_order_id")
	private String externalOrderId;

	@Column(name = "status")
	private String status;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "deleted")
	private Boolean deleted;
}
