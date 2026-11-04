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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseCandidate;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseStatus;
import com.iemr.admin.service.nikshayidgenerate.NikshayIdGenerateService;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;
import com.iemr.admin.service.nikshaytransferout.NikshayTransferOutService;
import com.iemr.admin.service.nikshayupload.NikshayUploadService;

/**
 * Runs one Ni-kshay phase: ENROLMENT, XRAY_UPLOAD, NAAT_UPLOAD (MTB and Rif), CONTACTS or
 * TRANSFER_OUT. Only one run of a phase at a time; each run takes up to
 * nikshay.phase.batch-size beneficiaries and writes its job history log entries when it ends.
 * A manual run of one phase also writes a JOB summary row with its counts (the whole job
 * writes one row for all its phases, see NikshayPhaseJob).
 */
@Service
public class NikshayPhaseRunService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseRunService.class);

	@Value("${nikshay.phase.batch-size:200}")
	private int batchSize;

	@Autowired
	private NikshayIdGenerateService idGenerateService;

	@Autowired
	private NikshayUploadService uploadService;

	@Autowired
	private NikshayTransferOutService transferOutService;

	@Autowired
	private NikshayPhaseStatusService phaseStatusService;

	@Autowired
	private NikshayJobHistoryLogService logService;

	// Phases running now (one Admin-API server)
	private final Set<NikshayPhase> running = new HashSet<>();

	/**
	 * Runs the phase for the beneficiaries waiting in it, or for the given BenRegIds only
	 * (null = waiting ones). Returns the beneficiaries that failed. Throws
	 * IllegalStateException when the phase is already running.
	 */
	public List<NikshayErroredBeneficiaryDTO> run(NikshayPhase phase, List<Long> benRegIds,
			NikshayJobRunContext context) {
		return run(phase, benRegIds, batchSize, null, context);
	}

	/**
	 * Same as run(phase, benRegIds, context). limit replaces the batch size. beneficiaryType
	 * (INDEX, CONTACT, ALL_NEGATIVE), when set, limits a waiting upload query to that type.
	 */
	public List<NikshayErroredBeneficiaryDTO> run(NikshayPhase phase, List<Long> benRegIds, int limit,
			String beneficiaryType, NikshayJobRunContext context) {
		long startedAt = System.currentTimeMillis();
		NikshayPhaseRun run;
		try {
			run = execute(phase, benRegIds, limit, beneficiaryType, context);
		} catch (IllegalStateException e) {
			// Already running: no summary, as before
			throw e;
		} catch (RuntimeException e) {
			saveSummary(context, summaryOf(startedAt, NikshayPhaseMetrics.couldNotRun(phase.name(), e)));
			throw e;
		}
		saveSummary(context, summaryOf(startedAt, count(phase, run)));
		return run.getErroredBeneficiaries();
	}

	/**
	 * One pass of the whole job: runs the phase like run(...) and returns its counts. No
	 * summary row is written here; the job writes one for all its passes.
	 */
	public NikshayPhaseMetrics runInJob(NikshayPhase phase, NikshayJobRunContext context) {
		return runInJob(phase, batchSize, null, context);
	}

	/** Same as runInJob(phase, context), with limit and beneficiaryType as in run(...). */
	public NikshayPhaseMetrics runInJob(NikshayPhase phase, int limit, String beneficiaryType,
			NikshayJobRunContext context) {
		return count(phase, execute(phase, null, limit, beneficiaryType, context));
	}

	/**
	 * Saves the run's JOB row in nikshay_worker_job_history_log, its counts as JSON in the
	 * message (FAILED with reason PHASE_COULD_NOT_RUN when a pass could not run). Returns the
	 * row, or null when it could not be saved; the run itself is not affected.
	 */
	public NikshayJobHistoryLog saveSummary(NikshayJobRunContext context, NikshayJobRunSummary summary) {
		try {
			String message = summary.toMessage();
			NikshayJobHistoryLog entry = summary.isAnyCouldNotRun()
					? logService.failure(context, com.iemr.admin.service.nikshayjoblog.NikshayPhase.JOB, null, null,
							NikshayFailureReason.PHASE_COULD_NOT_RUN, message)
					: logService.success(context, com.iemr.admin.service.nikshayjoblog.NikshayPhase.JOB, null, null,
							null, message);
			List<NikshayJobHistoryLog> entries = new ArrayList<>();
			entries.add(entry);
			logService.saveAll(entries);
			return entry;
		} catch (Exception e) {
			logger.error("Run {}: could not write the Ni-kshay run summary", context.getJobRunId(), e);
			return null;
		}
	}

	private static NikshayJobRunSummary summaryOf(long startedAt, NikshayPhaseMetrics metrics) {
		NikshayJobRunSummary summary = new NikshayJobRunSummary(startedAt);
		summary.add(metrics);
		summary.finish();
		return summary;
	}

	/**
	 * Counts the picked beneficiaries by their status in the phase now. A failure here is
	 * only logged: the counts stay 0 apart from picked, and the run goes on.
	 */
	private NikshayPhaseMetrics count(NikshayPhase phase, NikshayPhaseRun run) {
		NikshayPhaseMetrics metrics = new NikshayPhaseMetrics(phase.name());
		List<Long> picked = new ArrayList<>(run.getPickedBenRegIds());
		metrics.setPicked(picked.size());
		if (picked.isEmpty()) {
			return metrics;
		}
		try {
			int succeeded = 0;
			int failed = 0;
			int notApplicable = 0;
			for (NikshayPhaseCandidate row : phaseStatusService.findRows(phase, picked)) {
				if (NikshayPhaseStatus.DONE.name().equals(row.getStatus())) {
					succeeded++;
				} else if (NikshayPhaseStatus.FAILED.name().equals(row.getStatus())) {
					failed++;
				} else if (NikshayPhaseStatus.NOT_APPLICABLE.name().equals(row.getStatus())) {
					notApplicable++;
				}
			}
			metrics.setSucceeded(succeeded);
			metrics.setFailed(failed);
			metrics.setNotApplicable(notApplicable);
			// PENDING, IN_PROGRESS or no row yet
			metrics.setWaiting(picked.size() - succeeded - failed - notApplicable);
		} catch (Exception e) {
			logger.warn("{} run {}: could not count the picked beneficiaries", phase,
					run.getContext().getJobRunId(), e);
		}
		return metrics;
	}

	// The phase run itself (unchanged): lock, run, write the history log entries
	private NikshayPhaseRun execute(NikshayPhase phase, List<Long> benRegIds, int limit, String beneficiaryType,
			NikshayJobRunContext context) {
		synchronized (running) {
			if (!running.add(phase)) {
				throw new IllegalStateException(phase + " is already running");
			}
		}
		NikshayPhaseRun run = new NikshayPhaseRun(context);
		logger.info("{} run {} started by {} ({})", phase, context.getJobRunId(), context.getTriggeredBy(),
				benRegIds == null ? "waiting beneficiaries" : benRegIds.size() + " given beneficiaries");
		try {
			switch (phase) {
				case ENROLMENT:
					idGenerateService.runEnrolment(benRegIds, limit, run);
					break;
				case NAAT_UPLOAD:
				case XRAY_UPLOAD:
					uploadService.runPhase(phase, benRegIds, limit, beneficiaryType, run);
					break;
				case CONTACTS:
					idGenerateService.runContacts(benRegIds, limit, run);
					break;
				case TRANSFER_OUT:
					transferOutService.runTransferOut(benRegIds, limit, run);
					break;
				default:
					break;
			}
		} finally {
			synchronized (running) {
				running.remove(phase);
			}
			try {
				logService.saveAll(run.getLogEntries());
			} catch (Exception e) {
				logger.error("Could not write {} Ni-kshay history log entries", run.getLogEntries().size(), e);
			}
		}
		logger.info("{} run {} finished: {} beneficiaries failed", phase, context.getJobRunId(),
				run.getErroredBeneficiaries().size());
		return run;
	}

	/**
	 * Sets the given beneficiaries back to PENDING in the phase (reprocess); with runNow,
	 * runs the phase for them straight away and returns the ones that failed. Returns null
	 * when not run now.
	 */
	public List<NikshayErroredBeneficiaryDTO> reprocess(NikshayPhase phase, List<Long> benRegIds, boolean runNow,
			NikshayJobRunContext context) {
		int count = phaseStatusService.reprocess(phase, benRegIds);
		logger.info("{}: {} of {} beneficiaries set for reprocess", phase, count, benRegIds.size());
		return runNow ? run(phase, benRegIds, context) : null;
	}
}
