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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

/**
 * Counts of one phase pass, by the status each picked beneficiary has in the phase once the
 * pass ends: DONE = succeeded, FAILED = failed, NOT_APPLICABLE = notApplicable, anything
 * else (PENDING, or no row) = waiting. The four always add up to picked. A pass that did
 * not run has no counts (they are left out of the JSON) and, when it threw, its error.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "phase", "pass", "result", "picked", "succeeded", "failed", "notApplicable", "waiting",
		"error" })
public class NikshayPhaseMetrics {
	// The pass ran and was counted
	public static final String RAN = "RAN";
	// The pass threw an error (see the Admin-API log)
	public static final String COULD_NOT_RUN = "COULD_NOT_RUN";
	// The phase was already running (a manual push), so this pass was skipped
	public static final String SKIPPED_ALREADY_RUNNING = "SKIPPED_ALREADY_RUNNING";

	private String phase;
	// 1, or 2 for the job's second X-ray and NAAT pass (the contacts)
	private int pass = 1;
	private String result = RAN;
	private Integer picked = 0;
	private Integer succeeded = 0;
	private Integer failed = 0;
	private Integer notApplicable = 0;
	private Integer waiting = 0;
	// COULD_NOT_RUN only: the error's type and message (the full stack trace is in the log)
	private String error;

	// Keeps the summary within the message column
	private static final int ERROR_MAX_LENGTH = 200;

	public NikshayPhaseMetrics(String phase) {
		this.phase = phase;
	}

	/** A pass skipped because the phase was already running. */
	public static NikshayPhaseMetrics skipped(String phase) {
		return notRun(phase, SKIPPED_ALREADY_RUNNING);
	}

	/** A pass that threw: COULD_NOT_RUN, with the error of its deepest cause. */
	public static NikshayPhaseMetrics couldNotRun(String phase, Throwable e) {
		NikshayPhaseMetrics metrics = notRun(phase, COULD_NOT_RUN);
		Throwable cause = e;
		while (cause.getCause() != null && cause.getCause() != cause) {
			cause = cause.getCause();
		}
		String error = cause.getClass().getSimpleName()
				+ (cause.getMessage() == null ? "" : ": " + cause.getMessage().replaceAll("\\s+", " ").trim());
		metrics.setError(cut(error, ERROR_MAX_LENGTH));
		return metrics;
	}

	/** A copy whose error is cut to maxLength (used to fit the summary in the message column). */
	public NikshayPhaseMetrics withErrorCut(int maxLength) {
		NikshayPhaseMetrics copy = new NikshayPhaseMetrics(phase);
		copy.setPass(pass);
		copy.setResult(result);
		copy.setPicked(picked);
		copy.setSucceeded(succeeded);
		copy.setFailed(failed);
		copy.setNotApplicable(notApplicable);
		copy.setWaiting(waiting);
		copy.setError(cut(error, maxLength));
		return copy;
	}

	private static NikshayPhaseMetrics notRun(String phase, String result) {
		NikshayPhaseMetrics metrics = new NikshayPhaseMetrics(phase);
		metrics.setResult(result);
		metrics.setPicked(null);
		metrics.setSucceeded(null);
		metrics.setFailed(null);
		metrics.setNotApplicable(null);
		metrics.setWaiting(null);
		return metrics;
	}

	private static String cut(String text, int maxLength) {
		return text != null && text.length() > maxLength ? text.substring(0, maxLength) : text;
	}
}
