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
 * Result of a diagnostic device order, written by FLW-API on the van and
 * synced here. Linked to its order by external_order_id, not
 * diagnostic_order_id — the latter is a per-van AUTO_INCREMENT that collides
 * once orders from different vans meet centrally (see AMRIT-DB V103).
 * Mapped read-only, with only the columns the Ni-kshay upload needs.
 */
@Data
@Entity
@Table(name = "tb_diagnostic_result")
public class DiagnosticResult {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "external_order_id")
	private String externalOrderId;

	@Column(name = "result_summary")
	private String resultSummary;

	@Column(name = "tb_presence")
	private Boolean tbPresence;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "deleted")
	private Boolean deleted;
}
