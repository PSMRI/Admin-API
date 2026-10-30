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
package com.iemr.admin.service.nikshayphasestatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryStatusDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryStatusPage;
import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryType;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseCandidate;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseDetailDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseStatus;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseStatusCount;
import com.iemr.admin.repo.nikshayphasestatus.NikshayPhaseStatusRepository;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * Records where each beneficiary is in each Ni-kshay phase (latest status, latest error,
 * attempts) and reads it back for the status list (BRD FR-18 Failed Beneficiaries tab).
 * A failure to record a status is only logged: it must never stop the Ni-kshay job.
 */
@Service
public class NikshayPhaseStatusService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseStatusService.class);

	private static final int MAX_ERROR_DETAIL_LENGTH = 2000;

	// Tries of a phase after which a temporary error (Ni-kshay down, login rejected) is no
	// longer retried automatically
	private static final int MAX_AUTO_ATTEMPTS = 3;

	@Autowired
	private NikshayPhaseStatusRepository phaseStatusRepository;

	/** The phase is being tried now; counts one attempt. */
	public void markInProgress(Long benRegId, NikshayBeneficiaryType type, Long indexBenRegId, NikshayPhase phase) {
		save(benRegId, type, indexBenRegId, phase, NikshayPhaseStatus.IN_PROGRESS, null, null, true, null, null);
	}

	/** The phase finished; note is optional (e.g. "Linked - no action"). */
	public void markDone(Long benRegId, NikshayBeneficiaryType type, Long indexBenRegId, NikshayPhase phase,
			String nikshayId, String episodeId, String note) {
		save(benRegId, type, indexBenRegId, phase, NikshayPhaseStatus.DONE, null, note, true, nikshayId, episodeId);
	}

	/** The phase was tried and failed, with the reason and the error message. */
	public void markFailed(Long benRegId, NikshayBeneficiaryType type, Long indexBenRegId, NikshayPhase phase,
			NikshayFailureReason reason, String errorDetail) {
		save(benRegId, type, indexBenRegId, phase, NikshayPhaseStatus.FAILED, reason, errorDetail, true, null, null);
	}

	/** The phase is due but was not tried; note says why (e.g. household not screened yet). */
	public void markPending(Long benRegId, NikshayBeneficiaryType type, Long indexBenRegId, NikshayPhase phase,
			String note) {
		save(benRegId, type, indexBenRegId, phase, NikshayPhaseStatus.PENDING, null, note, false, null, null);
	}

	/** The phase does not apply to the beneficiary; note says why. */
	public void markNotApplicable(Long benRegId, NikshayBeneficiaryType type, Long indexBenRegId, NikshayPhase phase,
			String note) {
		save(benRegId, type, indexBenRegId, phase, NikshayPhaseStatus.NOT_APPLICABLE, null, note, false, null, null);
	}

	/**
	 * BRD T4 ("only one of X-ray or NAAT done: push the test available; mark the other Not
	 * done"): called when one test upload is DONE. When the other test is still PENDING and
	 * the beneficiary has no order at all for it, it is marked NOT_APPLICABLE "Not done", so
	 * the beneficiary can go on to transfer out. A test ordered later is not uploaded.
	 */
	public void markOtherTestNotDone(Long benRegId, Long beneficiaryId, NikshayBeneficiaryType type,
			Long indexBenRegId, NikshayPhase donePhase) {
		NikshayPhase otherPhase = donePhase == NikshayPhase.XRAY_UPLOAD ? NikshayPhase.NAAT_UPLOAD
				: NikshayPhase.XRAY_UPLOAD;
		try {
			List<Long> ids = new ArrayList<>();
			ids.add(benRegId);
			List<NikshayPhaseCandidate> rows = phaseStatusRepository.findRows(otherPhase.name(), ids);
			if (beneficiaryId == null || rows.isEmpty()
					|| !NikshayPhaseStatus.PENDING.name().equals(rows.get(0).getStatus())) {
				return;
			}
			boolean ordered = otherPhase == NikshayPhase.XRAY_UPLOAD ? phaseStatusRepository.hasXrayOrder(beneficiaryId)
					: phaseStatusRepository.hasNaatOrder(beneficiaryId);
			if (ordered) {
				return;
			}
		} catch (Exception e) {
			logger.error("Beneficiary {}: could not check the {} order", benRegId, otherPhase, e);
			return;
		}
		markNotApplicable(benRegId, type, indexBenRegId, otherPhase, "Not done: no "
				+ (otherPhase == NikshayPhase.XRAY_UPLOAD ? "chest X-ray" : "NAAT") + " order (BRD T4)");
	}

	/**
	 * Records TRANSFER_OUT as PENDING once both test uploads are DONE or NOT_APPLICABLE and
	 * the beneficiary has no TRANSFER_OUT row yet (a later test upload, e.g. a Rif result,
	 * leaves it as it is). The transfer out run decides whether the beneficiary is actually
	 * transferred.
	 */
	public void markTransferOutIfTestsDone(Long benRegId, NikshayBeneficiaryType type, Long indexBenRegId) {
		try {
			List<Long> ids = new ArrayList<>();
			ids.add(benRegId);
			if (!phaseStatusRepository.findRows(NikshayPhase.TRANSFER_OUT.name(), ids).isEmpty()) {
				return;
			}
			for (NikshayPhase testPhase : new NikshayPhase[] { NikshayPhase.NAAT_UPLOAD, NikshayPhase.XRAY_UPLOAD }) {
				List<NikshayPhaseCandidate> rows = phaseStatusRepository.findRows(testPhase.name(), ids);
				if (rows.isEmpty() || !(NikshayPhaseStatus.DONE.name().equals(rows.get(0).getStatus())
						|| NikshayPhaseStatus.NOT_APPLICABLE.name().equals(rows.get(0).getStatus()))) {
					return;
				}
			}
		} catch (Exception e) {
			logger.error("Beneficiary {}: could not read the test upload statuses", benRegId, e);
			return;
		}
		markPending(benRegId, type, indexBenRegId, NikshayPhase.TRANSFER_OUT,
				"Tests uploaded; waits for the transfer out run");
	}

	/**
	 * Up to limit beneficiaries waiting in the phase, oldest first: PENDING, or FAILED with
	 * an error that is retried automatically (a missing field or an invalid test result every
	 * run, Ni-kshay down or the login rejected up to MAX_AUTO_ATTEMPTS attempts), and enrolled.
	 * XRAY_UPLOAD and NAAT_UPLOAD only pick those with a completed result, plus those DONE who
	 * have a new result not uploaded yet; NAAT_UPLOAD also waits for the chest X-ray upload,
	 * unless the beneficiary has no X-ray order; neither picks a beneficiary transferred out.
	 * TRANSFER_OUT picks those whose X-ray and NAAT uploads are both DONE or NOT_APPLICABLE.
	 * Each phase's query is in NikshayPhaseStatusRepository. ENROLMENT is not picked here.
	 */
	public List<NikshayPhaseCandidate> findWaiting(NikshayPhase phase, int limit) {
		switch (phase) {
			case CONTACTS:
				return phaseStatusRepository.findWaitingForContacts(MAX_AUTO_ATTEMPTS, limit);
			case XRAY_UPLOAD:
				return phaseStatusRepository.findWaitingForXrayUpload(MAX_AUTO_ATTEMPTS, limit);
			case NAAT_UPLOAD:
				return phaseStatusRepository.findWaitingForNaatUpload(MAX_AUTO_ATTEMPTS, limit);
			case TRANSFER_OUT:
				return phaseStatusRepository.findWaitingForTransferOut(MAX_AUTO_ATTEMPTS, limit);
			default:
				throw new IllegalArgumentException("No waiting query for phase " + phase);
		}
	}

	/** The given beneficiaries whose chest X-ray is still to come or failed, so their NAAT waits. */
	public Set<Long> findWaitingForXray(List<Long> benRegIds) {
		if (benRegIds.isEmpty()) {
			return new HashSet<>();
		}
		return new HashSet<>(phaseStatusRepository.findWaitingForXray(benRegIds));
	}

	/** The given beneficiaries' rows for the phase; a beneficiary with no row is left out. */
	public List<NikshayPhaseCandidate> findRows(NikshayPhase phase, List<Long> benRegIds) {
		if (benRegIds.isEmpty()) {
			return new ArrayList<>();
		}
		return phaseStatusRepository.findRows(phase.name(), benRegIds);
	}

	/**
	 * The given beneficiaries whose last try of the phase failed with an error that is not
	 * retried automatically (Ni-kshay rejected it, village or device not set up, or too many
	 * attempts): they are left out until reprocessed.
	 */
	public Set<Long> findWaitingForReprocess(NikshayPhase phase, List<Long> benRegIds) {
		if (benRegIds.isEmpty()) {
			return new HashSet<>();
		}
		return new HashSet<>(phaseStatusRepository.findWaitingForReprocess(phase.name(), benRegIds, MAX_AUTO_ATTEMPTS));
	}

	/**
	 * Reprocess (BRD FR-11 / FR-18): sets the given beneficiaries back to PENDING in the
	 * phase, with no attempts, so the next run picks them. Returns how many were set.
	 */
	public int reprocess(NikshayPhase phase, List<Long> benRegIds) {
		if (benRegIds.isEmpty()) {
			return 0;
		}
		return phaseStatusRepository.resetToPending(phase.name(), benRegIds, "Set for reprocess");
	}

	private void save(Long benRegId, NikshayBeneficiaryType type, Long indexBenRegId, NikshayPhase phase,
			NikshayPhaseStatus status, NikshayFailureReason reason, String detail, boolean countAttempt,
			String nikshayId, String episodeId) {
		if (benRegId == null) {
			return;
		}
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (detail != null && detail.length() > MAX_ERROR_DETAIL_LENGTH) {
			detail = detail.substring(0, MAX_ERROR_DETAIL_LENGTH);
		}
		try {
			phaseStatusRepository.save(benRegId, type.name(), indexBenRegId, phase.name(), status.name(),
					reason == null ? null : reason.name(), detail, countAttempt, nikshayId, episodeId,
					countAttempt ? now : null, status == NikshayPhaseStatus.DONE ? now : null);
		} catch (Exception e) {
			logger.error("Beneficiary {}: could not save Ni-kshay phase {} as {}", benRegId, phase, status, e);
		}
	}

	/**
	 * One page of beneficiaries with every phase. All filters are optional; see
	 * NikshayPhaseStatusRepository.findBeneficiaries. Page numbers start at 0.
	 */
	public NikshayBeneficiaryStatusPage getStatusList(NikshayPhase phase, String status, boolean failedOnly,
			NikshayFailureReason failureReason, NikshayBeneficiaryType beneficiaryType, String search, int page,
			int size) {
		String phaseName = phase == null ? null : phase.name();
		String reasonName = failureReason == null ? null : failureReason.name();
		String typeName = beneficiaryType == null ? null : beneficiaryType.name();
		String searchText = search == null || search.isBlank() ? null : search.trim();

		NikshayBeneficiaryStatusPage result = new NikshayBeneficiaryStatusPage();
		result.setPage(page);
		result.setSize(size);
		result.setTotal(phaseStatusRepository.countBeneficiaries(phaseName, status, failedOnly, reasonName, typeName,
				searchText));
		List<Map<String, Object>> rows = phaseStatusRepository.findBeneficiaries(phaseName, status, failedOnly,
				reasonName, typeName, searchText, page, size);
		if (rows.isEmpty()) {
			return result;
		}

		Map<Long, NikshayBeneficiaryStatusDTO> byBenRegId = new LinkedHashMap<>();
		for (Map<String, Object> row : rows) {
			NikshayBeneficiaryStatusDTO dto = new NikshayBeneficiaryStatusDTO();
			dto.setBenRegId(toLong(row.get("ben_reg_id")));
			dto.setBeneficiaryId(toLong(row.get("beneficiary_id")));
			dto.setBeneficiaryType((String) row.get("beneficiary_type"));
			dto.setIndexBenRegId(toLong(row.get("index_ben_reg_id")));
			dto.setName((String) row.get("name"));
			dto.setIndexName((String) row.get("index_name"));
			// Every phase is listed; one without a row has not started
			for (NikshayPhase p : NikshayPhase.values()) {
				NikshayPhaseDetailDTO detail = new NikshayPhaseDetailDTO();
				detail.setStatus("NOT_STARTED");
				detail.setAttempts(0);
				dto.getPhases().put(p.name(), detail);
			}
			byBenRegId.put(dto.getBenRegId(), dto);
		}

		for (Map<String, Object> row : phaseStatusRepository.findPhaseRows(new ArrayList<>(byBenRegId.keySet()))) {
			NikshayBeneficiaryStatusDTO dto = byBenRegId.get(toLong(row.get("ben_reg_id")));
			NikshayPhaseDetailDTO detail = dto.getPhases().get((String) row.get("phase"));
			if (detail == null) {
				continue;
			}
			detail.setStatus((String) row.get("status"));
			detail.setFailureReason((String) row.get("failure_reason"));
			detail.setErrorDetail((String) row.get("error_detail"));
			detail.setAttempts(((Number) row.get("attempts")).intValue());
			detail.setNikshayId((String) row.get("nikshay_id"));
			detail.setEpisodeId((String) row.get("episode_id"));
			detail.setLastAttemptAt(toTimestamp(row.get("last_attempt_at")));
			detail.setCompletedAt(toTimestamp(row.get("completed_at")));
			if (NikshayPhase.ENROLMENT.name().equals(row.get("phase"))) {
				dto.setNikshayId(detail.getNikshayId());
			}
		}
		result.getBeneficiaries().addAll(byBenRegId.values());
		return result;
	}

	/** Number of beneficiaries per phase and status. */
	public List<NikshayPhaseStatusCount> getSummary() {
		return phaseStatusRepository.countByPhaseAndStatus();
	}

	private static Long toLong(Object value) {
		return value == null ? null : ((Number) value).longValue();
	}

	// MySQL TIMESTAMP columns come back as LocalDateTime or Timestamp, depending on the driver
	private static Timestamp toTimestamp(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Timestamp) {
			return (Timestamp) value;
		}
		if (value instanceof java.time.LocalDateTime) {
			return Timestamp.valueOf((java.time.LocalDateTime) value);
		}
		return null;
	}
}
