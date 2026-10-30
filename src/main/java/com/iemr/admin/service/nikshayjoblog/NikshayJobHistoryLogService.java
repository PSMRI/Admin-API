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
package com.iemr.admin.service.nikshayjoblog;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.repo.nikshayjoblog.NikshayJobHistoryLogRepo;
import com.iemr.admin.service.nikshayclient.NikshayApiException;

/**
 * Writes and reads the Ni-kshay phase history (nikshay_worker_job_history_log).
 * Phases build entries with failure()/success() while they run and hand them to
 * saveAll() once, so nothing is dropped silently.
 */
@Service
public class NikshayJobHistoryLogService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayJobHistoryLogService.class);

	private static final int MESSAGE_MAX_LENGTH = 1000;

	@Autowired
	private NikshayJobHistoryLogRepo logRepo;

	/** A failed entry; also written to the application log as a warning. */
	public NikshayJobHistoryLog failure(NikshayJobRunContext ctx, NikshayPhase phase, Long benId,
			String referenceId, NikshayFailureReason reason, String message) {
		logger.warn("{} failed for ben {} ({}): {} - {}", phase, benId, referenceId, reason, message);
		NikshayJobHistoryLog entry = entry(ctx, phase, benId, referenceId, NikshayLogStatus.FAILED, message);
		entry.setReason(reason);
		return entry;
	}

	/**
	 * A failed entry for a failed Ni-kshay call, with what was sent and Ni-kshay's full
	 * answer when the call got that far.
	 */
	public NikshayJobHistoryLog failure(NikshayJobRunContext ctx, NikshayPhase phase, Long benId,
			String referenceId, NikshayApiException e) {
		NikshayJobHistoryLog entry = failure(ctx, phase, benId, referenceId, e.getReason(), e.getMessage());
		entry.setRequestPayload(e.getRequestPayload());
		entry.setResponseBody(e.getResponseBody());
		return entry;
	}

	/** A success entry, with the Ni-kshay episode ID when the phase has one. */
	public NikshayJobHistoryLog success(NikshayJobRunContext ctx, NikshayPhase phase, Long benId,
			String referenceId, String episodeId, String message) {
		NikshayJobHistoryLog entry = entry(ctx, phase, benId, referenceId, NikshayLogStatus.SUCCESS, message);
		entry.setEpisodeId(episodeId);
		return entry;
	}

	/**
	 * Saves the entries in their own transaction, so a rollback of the phase's own
	 * work doesn't take its history with it.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveAll(List<NikshayJobHistoryLog> entries) {
		if (!entries.isEmpty()) {
			logRepo.saveAll(entries);
		}
	}

	/** Items whose latest attempt failed, newest first. Null filters are ignored. */
	public Page<NikshayJobHistoryLog> getFailed(NikshayPhase phase, NikshayFailureReason reason, Long benId,
			Timestamp fromDate, Timestamp toDate, int page, int size) {
		return logRepo.findLatestFailed(NikshayLogStatus.FAILED, phase, reason, benId, fromDate, toDate,
				PageRequest.of(page, size));
	}

	/** The given references that already have a SUCCESS entry in this phase (e.g. tests already added). */
	public Set<String> uploadedReferenceIds(NikshayPhase phase, List<String> referenceIds) {
		if (referenceIds.isEmpty()) {
			return new HashSet<>();
		}
		return new HashSet<>(logRepo.findReferenceIdsWithStatus(phase, NikshayLogStatus.SUCCESS, referenceIds));
	}

	/** Every entry of one beneficiary, newest first. */
	public List<NikshayJobHistoryLog> getHistory(Long benId) {
		return logRepo.findByBenIdOrderByIdDesc(benId);
	}

	private NikshayJobHistoryLog entry(NikshayJobRunContext ctx, NikshayPhase phase, Long benId,
			String referenceId, NikshayLogStatus status, String message) {
		NikshayJobHistoryLog entry = new NikshayJobHistoryLog();
		entry.setBenId(benId);
		entry.setPhase(phase);
		entry.setReferenceId(referenceId);
		entry.setStatus(status);
		entry.setMessage(message != null && message.length() > MESSAGE_MAX_LENGTH
				? message.substring(0, MESSAGE_MAX_LENGTH)
				: message);
		entry.setJobRunId(ctx.getJobRunId());
		entry.setTriggerType(ctx.getTriggerType());
		entry.setTriggeredBy(ctx.getTriggeredBy());
		entry.setAttempt((int) logRepo.countAttempts(phase, benId, referenceId) + 1);
		return entry;
	}
}
