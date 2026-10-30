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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayjoblog.NikshayLogStatus;
import com.iemr.admin.service.nikshayjoblog.NikshayPhase;
import com.iemr.admin.service.nikshayjoblog.NikshayTriggerType;

import lombok.Data;

/**
 * One outcome of one Ni-kshay phase for one beneficiary (nikshay_worker_job_history_log).
 * Append-only: a retry adds a new row with the next attempt number. Never holds
 * passwords, request bodies, names or phone numbers.
 */
@Data
@Entity
@Table(name = "nikshay_worker_job_history_log")
public class NikshayJobHistoryLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	// BenRegId
	@Column(name = "ben_id")
	private Long benId;

	@Column(name = "episode_id")
	private String episodeId;

	@Enumerated(EnumType.STRING)
	@Column(name = "phase")
	private NikshayPhase phase;

	// What the row is about, e.g. the diagnostic order's external_order_id for NAAT / X-ray
	@Column(name = "reference_id")
	private String referenceId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private NikshayLogStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "reason")
	private NikshayFailureReason reason;

	@Column(name = "message")
	private String message;

	// JSON sent to Ni-kshay and its full answer, on a failed call
	@Column(name = "request_payload")
	private String requestPayload;

	@Column(name = "response_body")
	private String responseBody;

	@Column(name = "job_run_id")
	private String jobRunId;

	@Enumerated(EnumType.STRING)
	@Column(name = "trigger_type")
	private NikshayTriggerType triggerType;

	@Column(name = "triggered_by")
	private String triggeredBy;

	@Column(name = "attempt")
	private Integer attempt;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
}
