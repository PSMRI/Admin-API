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
package com.iemr.admin.service.nikshaytransferout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.data.nikshayidgenerate.NikshayEnrollResult;
import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayIdGenerateBeneficiaryDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayLinkStatus;
import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryType;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseCandidate;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseStatus;
import com.iemr.admin.data.nikshaytransferout.NikshayContactTestsDTO;
import com.iemr.admin.data.nikshaytransferout.NikshayDesignatedPhiDTO;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.repo.nikshayidgenerate.NikshayIdGenerateRepository;
import com.iemr.admin.repo.nikshaytransferout.NikshayTransferOutRepository;
import com.iemr.admin.service.nikshayclient.NikshayApiException;
import com.iemr.admin.service.nikshayclient.NikshayAuthClient;
import com.iemr.admin.service.nikshayclient.NikshayDeviceCredentialService;
import com.iemr.admin.service.nikshayclient.NikshayLoginException;
import com.iemr.admin.service.nikshayclient.NikshaySession;
import com.iemr.admin.service.nikshayclient.NikshayTransferClient;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseRun;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseStatusService;
import com.iemr.admin.service.nikshayupload.NikshayTestType;

/**
 * TRANSFER_OUT phase (BRD FR-09): transfers the episode of each beneficiary whose tests are
 * uploaded to the designated PHI of their village, with the village's NAAT device login.
 * Ni-kshay starts the transfer ("Transfer Initiated successfully", IsTransferInProcess) and
 * the episode moves once the designated PHI accepts it.
 * Who is transferred (BRD 10.1, T5, T7, V13):
 * - index patients, for treatment;
 * - contacts: with a TB Positive NAAT for treatment, with an X-ray suggestive of TB for
 *   evaluation, symptomatic ones not at all (not TPT-eligible), the others for TPT;
 * - members of all-negative households are not transferred.
 * A contact is transferred with the index patient's details (address, phone, village and
 * facility), as they were enrolled.
 */
@Service
public class NikshayTransferOutService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayTransferOutService.class);

	@Autowired
	private NikshayPhaseStatusService phaseStatusService;

	@Autowired
	private NikshayTransferOutRepository transferOutRepository;

	@Autowired
	private NikshayIdGenerateRepository idGenerateRepository;

	@Autowired
	private NikshayDeviceCredentialService credentialService;

	@Autowired
	private NikshayAuthClient authClient;

	@Autowired
	private NikshayTransferClient transferClient;

	@Autowired
	private NikshayTransferPayloadBuilder payloadBuilder;

	@Autowired
	private NikshayJobHistoryLogService logService;

	/** One beneficiary to transfer, with everything the call needs. */
	private static class Transfer {
		NikshayPhaseCandidate candidate;
		NikshayEnrollResult episode;
		NikshayDesignatedPhiDTO designatedPhi;
		long fromPhi;
		long toPhi;
		String address;
		String phone;
		Integer villageId;
		// Why they are transferred, e.g. "treatment" or "TPT"
		String purpose;
	}

	/**
	 * Transfers up to limit beneficiaries waiting in TRANSFER_OUT, or only the given ones
	 * (whose X-ray and NAAT uploads must be finished). Failures are added to the run.
	 */
	public void runTransferOut(List<Long> benRegIds, int limit, NikshayPhaseRun run) {
		List<NikshayPhaseCandidate> candidates = findCandidates(benRegIds, limit, run);
		logger.info("TRANSFER_OUT: {} beneficiaries to check", candidates.size());
		if (candidates.isEmpty()) {
			return;
		}
		List<Long> ids = new ArrayList<>();
		for (NikshayPhaseCandidate candidate : candidates) {
			ids.add(candidate.getBenRegId());
		}
		Map<Long, NikshayEnrollResult> episodes = idGenerateRepository.findLatestEpisodes(ids);
		Set<Long> transferred = transferOutRepository.findTransferred(ids);
		Map<Long, NikshayContactTestsDTO> contactTests = transferOutRepository.findContactTests(ids);

		// Whose details go in the call: the beneficiary's own, or a contact's index patient's
		List<Long> detailIds = new ArrayList<>();
		for (NikshayPhaseCandidate candidate : candidates) {
			Long detailId = detailId(candidate);
			if (detailId != null && !detailIds.contains(detailId)) {
				detailIds.add(detailId);
			}
		}
		Map<Long, NikshayIdGenerateBeneficiaryDTO> details = new HashMap<>();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : idGenerateRepository.findBeneficiariesByBenRegIds(detailIds)) {
			details.put(beneficiary.getBeneficiaryId(), beneficiary);
		}

		List<Transfer> transfers = new ArrayList<>();
		for (NikshayPhaseCandidate candidate : candidates) {
			Long benRegId = candidate.getBenRegId();
			NikshayEnrollResult episode = episodes.get(benRegId);
			if (transferred.contains(benRegId)) {
				markDone(candidate, episode, "Already transferred out (job history): not sent again");
				continue;
			}
			if (episode == null) {
				fail(run, candidate, NikshayFailureReason.MANDATORY_FIELD_MISSING,
						"The beneficiary has no Ni-kshay episode saved yet", null);
				continue;
			}
			if (episode.getLinkStatus() == NikshayLinkStatus.LINKED_NO_ACTION) {
				markNotApplicable(candidate, "Already '" + episode.getStageFound() + "' in Ni-kshay: no action");
				continue;
			}
			String purpose = purpose(candidate, contactTests.get(benRegId));
			if (purpose == null) {
				continue;
			}
			NikshayIdGenerateBeneficiaryDTO detail = details.get(detailId(candidate));
			String missing = missingDetail(detail);
			if (missing != null) {
				fail(run, candidate, NikshayFailureReason.MANDATORY_FIELD_MISSING, missing, episode.getEpisodeId());
				continue;
			}
			Transfer transfer = new Transfer();
			transfer.candidate = candidate;
			transfer.episode = episode;
			transfer.fromPhi = detail.getSelectedHierarchyId();
			transfer.address = detail.getAddress().trim();
			transfer.phone = detail.getPrimaryPhone();
			transfer.villageId = detail.getNikshayVillageId();
			transfer.purpose = purpose;
			transfers.add(transfer);
		}
		if (!transfers.isEmpty()) {
			sendTransfers(transfers, run);
		}
	}

	// Without benRegIds: those waiting in the phase. With benRegIds: their TRANSFER_OUT rows,
	// when their tests are uploaded and they are not transferred already
	private List<NikshayPhaseCandidate> findCandidates(List<Long> benRegIds, int limit, NikshayPhaseRun run) {
		if (benRegIds == null) {
			return phaseStatusService.findWaiting(NikshayPhase.TRANSFER_OUT, limit);
		}
		List<NikshayPhaseCandidate> candidates = new ArrayList<>();
		Set<Long> found = new HashSet<>();
		for (NikshayPhaseCandidate row : phaseStatusService.findRows(NikshayPhase.TRANSFER_OUT, benRegIds)) {
			found.add(row.getBenRegId());
			if (NikshayPhaseStatus.DONE.name().equals(row.getStatus())) {
				addErrored(run, row.getBenRegId(), null, "Already transferred out");
			} else if (NikshayPhaseStatus.NOT_APPLICABLE.name().equals(row.getStatus())) {
				addErrored(run, row.getBenRegId(), null, "Transfer out does not apply to this beneficiary");
			} else {
				candidates.add(row);
			}
		}
		for (Long benRegId : benRegIds) {
			if (!found.contains(benRegId)) {
				addErrored(run, benRegId, null, "X-ray and NAAT uploads are not finished yet");
			}
		}
		return candidates;
	}

	// Why the beneficiary is transferred, or null (and NOT_APPLICABLE) when they are not
	private String purpose(NikshayPhaseCandidate candidate, NikshayContactTestsDTO tests) {
		NikshayBeneficiaryType type = candidate.getBeneficiaryType();
		if (type == NikshayBeneficiaryType.INDEX) {
			return "treatment";
		}
		if (type == NikshayBeneficiaryType.ALL_NEGATIVE) {
			markNotApplicable(candidate, "All-negative household: no transfer");
			return null;
		}
		if (tests != null && tests.isNaatPositive()) {
			return "treatment (M.Tb. detected)";
		}
		if (tests != null && isSuggestiveXray(tests.getLatestXray())) {
			return "evaluation (X-ray suggestive of TB)";
		}
		if (tests != null && tests.isSymptomatic()) {
			markNotApplicable(candidate, "V13: Symptomatic contact, not eligible for TPT: no transfer");
			return null;
		}
		return "TPT";
	}

	// X-ray "TB Presumptive" (also misspelt "TB Presumtive"): Abnormal - Suggestive of TB
	private static boolean isSuggestiveXray(String result) {
		String normalized = result == null ? "" : result.trim().replaceAll("\\s+", " ").toLowerCase();
		return normalized.equals("tb presumptive") || normalized.equals("tb presumtive");
	}

	// A contact is transferred with their index patient's details, as they were enrolled
	private static Long detailId(NikshayPhaseCandidate candidate) {
		return candidate.getBeneficiaryType() == NikshayBeneficiaryType.CONTACT ? candidate.getIndexBenRegId()
				: candidate.getBenRegId();
	}

	private static String missingDetail(NikshayIdGenerateBeneficiaryDTO detail) {
		if (detail == null) {
			return "The beneficiary's (or index patient's) registration details are not found";
		}
		if (detail.getSelectedHierarchyId() == null) {
			return "No Ni-kshay facility code for the village (hierarchyMappingFrom)";
		}
		if (detail.getNikshayVillageId() == null) {
			return "V10: The village is not a Ni-kshay village";
		}
		if (detail.getAddress() == null || detail.getAddress().isBlank()) {
			return "No address (newAddress)";
		}
		if (detail.getPrimaryPhone() == null || detail.getPrimaryPhone().isBlank()) {
			return "No phone number (mobileNo)";
		}
		return null;
	}

	// Finds each village's designated PHI and NAAT device login, then sends the transfers
	private void sendTransfers(List<Transfer> transfers, NikshayPhaseRun run) {
		List<Integer> villageIds = new ArrayList<>();
		for (Transfer transfer : transfers) {
			if (!villageIds.contains(transfer.villageId)) {
				villageIds.add(transfer.villageId);
			}
		}
		Map<Integer, NikshayDesignatedPhiDTO> designatedPhis = transferOutRepository.findDesignatedPhis(villageIds);
		Set<Integer> undecryptableVillages = new HashSet<>();
		Map<Integer, NikshayDeviceCredentialDTO> credentials = credentialService.findCredentials(villageIds,
				NikshayTestType.NAAT.name(), undecryptableVillages);

		// The transfers to send, by NAAT device (one login per device)
		Map<Integer, List<Transfer>> byDevice = new LinkedHashMap<>();
		Map<Integer, NikshayDeviceCredentialDTO> deviceCredentials = new HashMap<>();
		for (Transfer transfer : transfers) {
			NikshayDesignatedPhiDTO phi = designatedPhis.get(transfer.villageId);
			if (phi == null) {
				fail(run, transfer.candidate, NikshayFailureReason.DESIGNATED_PHI_NOT_MAPPED, "S7: No active designated "
						+ "PHI for Ni-kshay village " + transfer.villageId + ": transfer held until one is mapped",
						transfer.episode.getEpisodeId());
				continue;
			}
			try {
				transfer.toPhi = Long.parseLong(phi.getDesignatedPhiId().trim());
			} catch (NumberFormatException e) {
				fail(run, transfer.candidate, NikshayFailureReason.DESIGNATED_PHI_NOT_MAPPED, "The designated PHI ID '"
						+ phi.getDesignatedPhiId() + "' of Ni-kshay village " + transfer.villageId
						+ " is not a Ni-kshay ID", transfer.episode.getEpisodeId());
				continue;
			}
			transfer.designatedPhi = phi;
			if (transfer.toPhi == transfer.fromPhi) {
				markDone(transfer.candidate, transfer.episode,
						"Already at the designated PHI " + phiLabel(transfer) + ": nothing sent");
				continue;
			}
			NikshayDeviceCredentialDTO credential = credentials.get(transfer.villageId);
			if (credential == null) {
				fail(run, transfer.candidate, undecryptableVillages.contains(transfer.villageId)
						? NikshayFailureReason.DEVICE_CONFIGURATION_MISSING : NikshayFailureReason.VILLAGE_MISSING,
						undecryptableVillages.contains(transfer.villageId)
								? "The NAAT device password for Ni-kshay village " + transfer.villageId
										+ " could not be decrypted"
								: "No active NAAT device is mapped to Ni-kshay village " + transfer.villageId,
						transfer.episode.getEpisodeId());
				continue;
			}
			List<Transfer> deviceTransfers = byDevice.get(credential.getDeviceDetailId());
			if (deviceTransfers == null) {
				deviceTransfers = new ArrayList<>();
				byDevice.put(credential.getDeviceDetailId(), deviceTransfers);
				deviceCredentials.put(credential.getDeviceDetailId(), credential);
			}
			deviceTransfers.add(transfer);
		}

		for (Map.Entry<Integer, List<Transfer>> entry : byDevice.entrySet()) {
			NikshayDeviceCredentialDTO credential = deviceCredentials.get(entry.getKey());
			NikshaySession session;
			try {
				session = authClient.login(credential.getPhiUserId(), credential.getPassword());
			} catch (NikshayLoginException e) {
				for (Transfer transfer : entry.getValue()) {
					fail(run, transfer.candidate, e.getReason(), "NAAT device " + credential.getDeviceSerialNumber()
							+ " login failed: " + e.getMessage(), transfer.episode.getEpisodeId());
				}
				continue;
			}
			for (Transfer transfer : entry.getValue()) {
				send(session, transfer, run);
			}
		}
	}

	private void send(NikshaySession session, Transfer transfer, NikshayPhaseRun run) {
		NikshayPhaseCandidate candidate = transfer.candidate;
		String episodeId = transfer.episode.getEpisodeId();
		phaseStatusService.markInProgress(candidate.getBenRegId(), candidate.getBeneficiaryType(),
				candidate.getIndexBenRegId(), NikshayPhase.TRANSFER_OUT);
		ObjectNode payload = payloadBuilder.build(episodeId, transfer.fromPhi, transfer.toPhi, transfer.address,
				transfer.phone);
		String answer;
		try {
			answer = transferClient.transfer(session, payload);
		} catch (NikshayApiException e) {
			addErrored(run, candidate.getBenRegId(), e.getReason(), e.getMessage());
			run.getLogEntries().add(logService.failure(run.getContext(), NikshayPhase.TRANSFER_OUT.getLogPhase(),
					candidate.getBenRegId(), episodeId, e));
			phaseStatusService.markFailed(candidate.getBenRegId(), candidate.getBeneficiaryType(),
					candidate.getIndexBenRegId(), NikshayPhase.TRANSFER_OUT, e.getReason(), e.getMessage());
			return;
		}
		// Ni-kshay answers "Transfer Initiated successfully": the episode is marked as transfer in
		// process and moves once the designated PHI accepts it
		String note = "Transfer initiated from " + transfer.fromPhi + " to " + phiLabel(transfer) + " for "
				+ transfer.purpose + "; moves once the PHI accepts it";
		logger.info("Beneficiary {}: episode {} {}", candidate.getBenRegId(), episodeId, note);
		phaseStatusService.markDone(candidate.getBenRegId(), candidate.getBeneficiaryType(),
				candidate.getIndexBenRegId(), NikshayPhase.TRANSFER_OUT, transfer.episode.getNikshayId(), episodeId, note);
		// Ni-kshay's answer is kept while its format is not documented
		String message = note + "; Ni-kshay answered " + answer;
		run.getLogEntries().add(logService.success(run.getContext(), NikshayPhase.TRANSFER_OUT.getLogPhase(),
				candidate.getBenRegId(), episodeId, episodeId, message.length() > 900 ? message.substring(0, 900) : message));
	}

	private static String phiLabel(Transfer transfer) {
		String name = transfer.designatedPhi == null ? null : transfer.designatedPhi.getPhiName();
		return name == null ? String.valueOf(transfer.toPhi) : name + " (" + transfer.toPhi + ")";
	}

	private void markDone(NikshayPhaseCandidate candidate, NikshayEnrollResult episode, String note) {
		phaseStatusService.markDone(candidate.getBenRegId(), candidate.getBeneficiaryType(),
				candidate.getIndexBenRegId(), NikshayPhase.TRANSFER_OUT, episode == null ? null : episode.getNikshayId(),
				episode == null ? null : episode.getEpisodeId(), note);
	}

	private void markNotApplicable(NikshayPhaseCandidate candidate, String note) {
		phaseStatusService.markNotApplicable(candidate.getBenRegId(), candidate.getBeneficiaryType(),
				candidate.getIndexBenRegId(), NikshayPhase.TRANSFER_OUT, note);
	}

	// Adds the failure to the run (response and job history log) and records the phase as FAILED
	private void fail(NikshayPhaseRun run, NikshayPhaseCandidate candidate, NikshayFailureReason reason, String detail,
			String episodeId) {
		addErrored(run, candidate.getBenRegId(), reason, detail);
		run.getLogEntries().add(logService.failure(run.getContext(), NikshayPhase.TRANSFER_OUT.getLogPhase(),
				candidate.getBenRegId(), episodeId, reason, detail));
		phaseStatusService.markFailed(candidate.getBenRegId(), candidate.getBeneficiaryType(),
				candidate.getIndexBenRegId(), NikshayPhase.TRANSFER_OUT, reason, detail);
	}

	// Adds the beneficiary to the run's response only
	private static void addErrored(NikshayPhaseRun run, Long benRegId, NikshayFailureReason reason, String detail) {
		NikshayErroredBeneficiaryDTO errored = new NikshayErroredBeneficiaryDTO(benRegId, reason, detail);
		errored.setPhase(NikshayPhase.TRANSFER_OUT.name());
		run.getErroredBeneficiaries().add(errored);
	}
}
