/*
* AMRIT – Accessible Medical Records via Integrated Technologies
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

import com.fasterxml.jackson.databind.JsonNode;
import com.iemr.admin.service.nikshayjoblog.NikshayLogStatus;
import com.iemr.admin.service.nikshayjoblog.NikshayTriggerType;

import lombok.Getter;
import lombok.Setter;

/**
 * One Ni-kshay run (the whole job, or one manual phase run), from its JOB row in
 * nikshay_worker_job_history_log. summary is the counts saved as JSON in the row's message;
 * rows written before the counts were added have plain text, given in message instead.
 */
@Getter
@Setter
public class NikshayJobRunDTO {
	private String jobRunId;
	private NikshayTriggerType triggerType;
	private String triggeredBy;
	private NikshayLogStatus status;
	// From the summary. For rows written before the times were added, startedAt is null and
	// finishedAt is the row's created_at.
	private Timestamp startedAt;
	private Timestamp finishedAt;
	private JsonNode summary;
	private String message;
}
