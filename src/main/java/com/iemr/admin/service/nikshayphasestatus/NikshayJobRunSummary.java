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
package com.iemr.admin.service.nikshayphasestatus;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

/**
 * The counts of one run (the whole job, or one manual phase run), saved as JSON in the
 * message of its JOB row in nikshay_worker_job_history_log: the totals of every pass, then
 * each pass.
 */
@Getter
@JsonPropertyOrder({ "startedAt", "finishedAt", "picked", "succeeded", "failed", "notApplicable", "waiting",
		"phases" })
public class NikshayJobRunSummary {
	private static final Logger logger = LoggerFactory.getLogger(NikshayJobRunSummary.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	// Size of nikshay_worker_job_history_log.message
	private static final int MESSAGE_MAX_LENGTH = 1000;

	// When the run started and ended, in epoch milliseconds (Admin-API's clock)
	private final long startedAt;
	private long finishedAt;
	private int picked;
	private int succeeded;
	private int failed;
	private int notApplicable;
	private int waiting;
	private List<NikshayPhaseMetrics> phases = new ArrayList<>();

	/** A summary for a run starting now. */
	public NikshayJobRunSummary() {
		this(System.currentTimeMillis());
	}

	public NikshayJobRunSummary(long startedAt) {
		this.startedAt = startedAt;
	}

	/** Sets finishedAt to now; called when the run's last pass is done. */
	public void finish() {
		finishedAt = System.currentTimeMillis();
	}

	public void add(NikshayPhaseMetrics metrics) {
		phases.add(metrics);
		picked += valueOf(metrics.getPicked());
		succeeded += valueOf(metrics.getSucceeded());
		failed += valueOf(metrics.getFailed());
		notApplicable += valueOf(metrics.getNotApplicable());
		waiting += valueOf(metrics.getWaiting());
	}

	// A pass that did not run has no counts
	private static int valueOf(Integer count) {
		return count == null ? 0 : count;
	}

	/** True when a pass threw an error; the JOB row is then saved as FAILED. */
	@JsonIgnore
	public boolean isAnyCouldNotRun() {
		for (NikshayPhaseMetrics metrics : phases) {
			if (NikshayPhaseMetrics.COULD_NOT_RUN.equals(metrics.getResult())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The JSON for the message column. When it would not fit (the column would cut the JSON and
	 * make it unreadable), it is made shorter step by step: errors cut to 80 characters, then
	 * only the passes that did not run, then their errors cut to 40, then only the totals.
	 */
	public String toMessage() {
		List<NikshayPhaseMetrics> allPhases = phases;
		try {
			String json = MAPPER.writeValueAsString(this);
			if (json.length() <= MESSAGE_MAX_LENGTH) {
				return json;
			}
			logger.warn("Ni-kshay run summary is {} characters, over {}: saving a shorter one", json.length(),
					MESSAGE_MAX_LENGTH);
			phases = cutErrors(allPhases, 80, false);
			json = MAPPER.writeValueAsString(this);
			if (json.length() <= MESSAGE_MAX_LENGTH) {
				return json;
			}
			phases = cutErrors(allPhases, 80, true);
			json = MAPPER.writeValueAsString(this);
			if (json.length() <= MESSAGE_MAX_LENGTH) {
				return json;
			}
			phases = cutErrors(allPhases, 40, true);
			json = MAPPER.writeValueAsString(this);
			if (json.length() <= MESSAGE_MAX_LENGTH) {
				return json;
			}
			phases = new ArrayList<>();
			return MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.warn("Could not write the Ni-kshay run summary as JSON", e);
			return "{\"startedAt\":" + startedAt + ",\"finishedAt\":" + finishedAt + ",\"picked\":" + picked
					+ ",\"succeeded\":" + succeeded + ",\"failed\":" + failed + ",\"notApplicable\":" + notApplicable
					+ ",\"waiting\":" + waiting + "}";
		} finally {
			phases = allPhases;
		}
	}

	// Copies of the passes with errors cut to maxLength; notRunOnly keeps only the passes that did not run
	private static List<NikshayPhaseMetrics> cutErrors(List<NikshayPhaseMetrics> passes, int maxLength,
			boolean notRunOnly) {
		List<NikshayPhaseMetrics> copies = new ArrayList<>();
		for (NikshayPhaseMetrics metrics : passes) {
			if (!notRunOnly || !NikshayPhaseMetrics.RAN.equals(metrics.getResult())) {
				copies.add(metrics.withErrorCut(maxLength));
			}
		}
		return copies;
	}
}
