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
package com.iemr.admin.service.nikshayidgenerate;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.data.nikshayidgenerate.NikshayEnrollResult;
import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayFlowScenario;
import com.iemr.admin.data.nikshayidgenerate.NikshayHouseholdMemberDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayIdGenerateBeneficiaryDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayIdGenerateVillageBatch;
import com.iemr.admin.data.nikshayidgenerate.NikshayLinkStatus;
import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryType;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseCandidate;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.repo.nikshayidgenerate.NikshayIdGenerateRepository;
import com.iemr.admin.service.nikshayclient.NikshayApiException;
import com.iemr.admin.service.nikshayclient.NikshayAuthClient;
import com.iemr.admin.service.nikshayclient.NikshayDeviceCredentialService;
import com.iemr.admin.service.nikshayclient.NikshayEnrollClient;
import com.iemr.admin.service.nikshayclient.NikshayLoginException;
import com.iemr.admin.service.nikshayclient.NikshaySession;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseRun;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseStatusService;
import com.iemr.admin.service.nikshayupload.NikshayTestType;

/**
 * Runs the ENROLMENT and CONTACTS phases of the Ni-kshay flow for Stop TB beneficiaries.
 * ENROLMENT gives each eligible beneficiary a Ni-kshay ID and marks their next phases;
 * CONTACTS adds an enrolled index patient's household members as their contacts.
 */
@Service
public class NikshayIdGenerateService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayIdGenerateService.class);

	// ContactTracing tries before the index's contacts are given up for this run
	private static final int CONTACT_COUNTS_ATTEMPTS = 3;
	private static final long CONTACT_COUNTS_RETRY_DELAY_MS = 5000;

	@Autowired
	private NikshayIdGenerateRepository nikshayIdGenerateRepository;

	@Autowired
	private NikshayDeviceCredentialService credentialService;

	@Autowired
	private NikshayBeneficiaryValidator beneficiaryValidator;

	@Autowired
	private NikshayAuthClient authClient;

	@Autowired
	private NikshayEnrollClient enrollClient;

	@Autowired
	private NikshayEnrollPayloadBuilder payloadBuilder;

	@Autowired
	private NikshayPhaseStatusService phaseStatusService;

	@Autowired
	private NikshayJobHistoryLogService logService;

	/**
	 * ENROLMENT phase: enrols up to limit beneficiaries eligible for a Ni-kshay ID, first the
	 * TB-positive index patients, then the members of all-negative households. Contacts are
	 * not added here (CONTACTS phase). With benRegIds, only those beneficiaries are tried,
	 * whatever their status; without, beneficiaries whose last try failed with an error that
	 * is not retried automatically are left out until reprocessed. Failures are added to the
	 * run.
	 */
	public void runEnrolment(List<Long> benRegIds, int limit, NikshayPhaseRun run) {
		List<NikshayIdGenerateBeneficiaryDTO> eligible = nikshayIdGenerateRepository.findEligibleBeneficiaries();
		logger.info("Found {} beneficiaries eligible for Ni-kshay ID generation", eligible.size());
		List<NikshayIdGenerateBeneficiaryDTO> positive = toRun(filterHouseholdScreened(keepRequested(eligible, benRegIds)),
				benRegIds, limit);
		processBeneficiaries(positive, NikshayFlowScenario.POSITIVE, run);

		// The query already requires the whole household to be screened
		List<NikshayIdGenerateBeneficiaryDTO> allNegative = nikshayIdGenerateRepository.findAllNegativeBeneficiaries();
		logger.info("Found {} members of all-negative households for Ni-kshay ID generation", allNegative.size());
		processBeneficiaries(toRun(keepRequested(allNegative, benRegIds), benRegIds, limit - positive.size()),
				NikshayFlowScenario.ALL_NEGATIVE, run);
	}

	// The beneficiaries asked for (all of them when benRegIds is null)
	private static List<NikshayIdGenerateBeneficiaryDTO> keepRequested(List<NikshayIdGenerateBeneficiaryDTO> beneficiaries,
			List<Long> benRegIds) {
		if (benRegIds == null) {
			return beneficiaries;
		}
		List<NikshayIdGenerateBeneficiaryDTO> requested = new ArrayList<>();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			if (benRegIds.contains(beneficiary.getBeneficiaryId())) {
				requested.add(beneficiary);
			}
		}
		return requested;
	}

	/**
	 * The first limit beneficiaries to enrol in this run. When the run was not asked for
	 * given beneficiaries, those waiting for a reprocess are left out first.
	 */
	private List<NikshayIdGenerateBeneficiaryDTO> toRun(List<NikshayIdGenerateBeneficiaryDTO> beneficiaries,
			List<Long> benRegIds, int limit) {
		Set<Long> waitingForReprocess = new HashSet<>();
		if (benRegIds == null) {
			List<Long> ids = new ArrayList<>();
			for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
				ids.add(beneficiary.getBeneficiaryId());
			}
			waitingForReprocess = phaseStatusService.findWaitingForReprocess(NikshayPhase.ENROLMENT, ids);
			if (!waitingForReprocess.isEmpty()) {
				logger.info("Leaving out {} beneficiaries waiting for a reprocess", waitingForReprocess.size());
			}
		}
		List<NikshayIdGenerateBeneficiaryDTO> toRun = new ArrayList<>();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			if (toRun.size() >= limit) {
				break;
			}
			if (!waitingForReprocess.contains(beneficiary.getBeneficiaryId())) {
				toRun.add(beneficiary);
			}
		}
		return toRun;
	}

	/**
	 * Keeps only the beneficiaries whose household members have all been TB screened.
	 * A beneficiary with no household members is kept.
	 */
	private List<NikshayIdGenerateBeneficiaryDTO> filterHouseholdScreened(
			List<NikshayIdGenerateBeneficiaryDTO> beneficiaries) {
		List<NikshayIdGenerateBeneficiaryDTO> householdScreened = new ArrayList<>();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			int notScreened = 0;
			for (NikshayHouseholdMemberDTO member : beneficiary.getHouseholdMembers()) {
				if (!Boolean.TRUE.equals(member.getTbScreened())) {
					notScreened++;
				}
			}
			if (notScreened == 0) {
				householdScreened.add(beneficiary);
			} else {
				logger.info("Skipping beneficiary {}: {} of {} household members not TB screened yet",
						beneficiary.getBeneficiaryId(), notScreened, beneficiary.getHouseholdMembers().size());
				phaseStatusService.markPending(beneficiary.getBeneficiaryId(), NikshayBeneficiaryType.INDEX, null,
						NikshayPhase.ENROLMENT, notScreened + " of " + beneficiary.getHouseholdMembers().size()
								+ " household members not TB screened yet");
			}
		}
		logger.info("{} of {} beneficiaries have their whole household TB screened", householdScreened.size(),
				beneficiaries.size());
		return householdScreened;
	}

	/**
	 * Enrols the given beneficiaries: groups them by village, applies the BRD section 13
	 * rules and enrols the ones that passed. Failures are added to the run.
	 */
	public void processBeneficiaries(List<NikshayIdGenerateBeneficiaryDTO> beneficiaries,
			NikshayFlowScenario flowScenario, NikshayPhaseRun run) {
		logger.info("Processing {} beneficiaries for Ni-kshay ID generation ({})", beneficiaries.size(), flowScenario);
		if (beneficiaries.isEmpty()) {
			return;
		}
		int failedBefore = run.getErroredBeneficiaries().size();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			beneficiary.setBeneficiaryType(flowScenario == NikshayFlowScenario.POSITIVE ? NikshayBeneficiaryType.INDEX
					: NikshayBeneficiaryType.ALL_NEGATIVE);
		}
		Map<Integer, NikshayIdGenerateVillageBatch> batchesByVillage = groupByVillage(beneficiaries, NikshayPhase.ENROLMENT,
				run);
		logger.info("Grouped beneficiaries into {} village batch(es)", batchesByVillage.size());

		for (NikshayIdGenerateVillageBatch batch : batchesByVillage.values()) {
			List<NikshayIdGenerateBeneficiaryDTO> valid = new ArrayList<>();
			for (NikshayIdGenerateBeneficiaryDTO beneficiary : batch.getBeneficiaries()) {
				List<String> problems = beneficiaryValidator.validate(beneficiary);
				if (problems.isEmpty()) {
					valid.add(beneficiary);
				} else {
					fail(run, beneficiary, NikshayPhase.ENROLMENT, NikshayFailureReason.MANDATORY_FIELD_MISSING,
							String.join("; ", problems));
				}
			}
			batch.setBeneficiaries(valid);
		}

		List<NikshayEnrollResult> enrolled = enrollBeneficiaries(batchesByVillage, run, flowScenario);
		logger.info("{} beneficiaries enrolled or linked in Ni-kshay", enrolled.size());
		if (flowScenario == NikshayFlowScenario.POSITIVE) {
			markContactsOfIndexesNotEnrolled(beneficiaries, enrolled);
		}
		logger.info("{} beneficiaries cannot be enrolled in Ni-kshay ({})",
				run.getErroredBeneficiaries().size() - failedBefore, flowScenario);
	}

	/**
	 * CONTACTS phase: adds the household members of up to limit enrolled index patients to
	 * Ni-kshay as their contacts. Without benRegIds, the index patients waiting in the phase
	 * whose enrolment is done; with benRegIds, those index patients whatever their status.
	 * The index patient's details and household come from the DB, and the contacts go to
	 * their latest episode. Failures are added to the run.
	 */
	public void runContacts(List<Long> benRegIds, int limit, NikshayPhaseRun run) {
		List<Long> ids = new ArrayList<>();
		if (benRegIds == null) {
			for (NikshayPhaseCandidate candidate : phaseStatusService.findWaiting(NikshayPhase.CONTACTS, limit)) {
				ids.add(candidate.getBenRegId());
			}
		} else {
			// Only index patients have contacts
			for (NikshayPhaseCandidate candidate : phaseStatusService.findRows(NikshayPhase.ENROLMENT, benRegIds)) {
				if (candidate.getBeneficiaryType() == NikshayBeneficiaryType.INDEX) {
					ids.add(candidate.getBenRegId());
				}
			}
			for (Long benRegId : benRegIds) {
				if (!ids.contains(benRegId)) {
					addErrored(run, benRegId, NikshayPhase.CONTACTS, null,
							"Not an enrolled index patient, so there are no contacts to add");
				}
			}
		}
		logger.info("Adding the contacts of {} index patient(s)", ids.size());
		if (ids.isEmpty()) {
			return;
		}

		Map<Long, NikshayEnrollResult> episodes = nikshayIdGenerateRepository.findLatestEpisodes(ids);
		List<NikshayIdGenerateBeneficiaryDTO> indexes = new ArrayList<>();
		for (NikshayIdGenerateBeneficiaryDTO index : nikshayIdGenerateRepository.findBeneficiariesByBenRegIds(ids)) {
			index.setBeneficiaryType(NikshayBeneficiaryType.INDEX);
			if (episodes.get(index.getBeneficiaryId()) == null) {
				fail(run, index, NikshayPhase.CONTACTS, NikshayFailureReason.MANDATORY_FIELD_MISSING,
						"The index patient has no Ni-kshay episode saved yet");
			} else if (index.getHouseholdMembers().isEmpty()) {
				phaseStatusService.markNotApplicable(index.getBeneficiaryId(), index.getBeneficiaryType(), null,
						NikshayPhase.CONTACTS, "No other household members");
			} else {
				indexes.add(index);
			}
		}

		Map<Integer, NikshayIdGenerateVillageBatch> batchesByVillage = groupByVillage(indexes, NikshayPhase.CONTACTS, run);
		Map<Integer, NikshaySession> sessionsByDevice = new HashMap<>();
		Map<Integer, NikshayLoginException> loginFailuresByDevice = new HashMap<>();
		for (NikshayIdGenerateVillageBatch batch : batchesByVillage.values()) {
			NikshaySession session = login(batch, sessionsByDevice, loginFailuresByDevice, NikshayPhase.CONTACTS, run);
			if (session == null || !setPortalVillageId(session, batch, NikshayPhase.CONTACTS, run)) {
				continue;
			}
			for (NikshayIdGenerateBeneficiaryDTO index : batch.getBeneficiaries()) {
				addContacts(session, index, episodes.get(index.getBeneficiaryId()), run);
			}
		}
	}

	/**
	 * Groups the beneficiaries by Ni-kshay village ID, pairing each village with its NAAT
	 * device login. Beneficiaries with no Ni-kshay village (V10), or whose village has no
	 * usable NAAT device login, fail the phase instead.
	 */
	private Map<Integer, NikshayIdGenerateVillageBatch> groupByVillage(List<NikshayIdGenerateBeneficiaryDTO> beneficiaries,
			NikshayPhase phase, NikshayPhaseRun run) {
		List<Integer> villageIds = new ArrayList<>();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			if (beneficiary.getNikshayVillageId() != null) {
				villageIds.add(beneficiary.getNikshayVillageId());
			}
		}
		Set<Integer> undecryptableVillages = new HashSet<>();
		Map<Integer, NikshayDeviceCredentialDTO> credentialsByVillage = credentialService.findCredentials(villageIds,
				NikshayTestType.NAAT.name(), undecryptableVillages);

		Map<Integer, NikshayIdGenerateVillageBatch> batchesByVillage = new LinkedHashMap<>();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			Integer villageId = beneficiary.getNikshayVillageId();
			if (villageId == null) {
				fail(run, beneficiary, phase, NikshayFailureReason.VILLAGE_MISSING,
						"V10: Village " + beneficiary.getResidenceHierarchyId() + " is not a Ni-kshay village");
				continue;
			}
			NikshayDeviceCredentialDTO credential = credentialsByVillage.get(villageId);
			if (credential == null) {
				if (undecryptableVillages.contains(villageId)) {
					fail(run, beneficiary, phase, NikshayFailureReason.DEVICE_CONFIGURATION_MISSING,
							"The NAAT device password for Ni-kshay village " + villageId + " could not be decrypted");
				} else {
					fail(run, beneficiary, phase, NikshayFailureReason.VILLAGE_MISSING,
							"V10: No active NAAT device is mapped to Ni-kshay village " + villageId);
				}
				continue;
			}
			NikshayIdGenerateVillageBatch batch = batchesByVillage.get(villageId);
			if (batch == null) {
				batch = new NikshayIdGenerateVillageBatch(villageId, credential);
				batchesByVillage.put(villageId, batch);
			}
			batch.getBeneficiaries().add(beneficiary);
		}
		return batchesByVillage;
	}

	/**
	 * The batch's NAAT device login: one login per device, reused for every village the
	 * device serves. When the login fails, every beneficiary of the batch fails the phase
	 * and null is returned.
	 */
	private NikshaySession login(NikshayIdGenerateVillageBatch batch, Map<Integer, NikshaySession> sessionsByDevice,
			Map<Integer, NikshayLoginException> loginFailuresByDevice, NikshayPhase phase, NikshayPhaseRun run) {
		NikshayDeviceCredentialDTO credential = batch.getCredential();
		Integer deviceId = credential.getDeviceDetailId();
		if (!sessionsByDevice.containsKey(deviceId) && !loginFailuresByDevice.containsKey(deviceId)) {
			try {
				sessionsByDevice.put(deviceId, authClient.login(credential.getPhiUserId(), credential.getPassword()));
			} catch (NikshayLoginException e) {
				loginFailuresByDevice.put(deviceId, e);
			}
		}
		NikshaySession session = sessionsByDevice.get(deviceId);
		if (session == null) {
			NikshayLoginException e = loginFailuresByDevice.get(deviceId);
			for (NikshayIdGenerateBeneficiaryDTO beneficiary : batch.getBeneficiaries()) {
				fail(run, beneficiary, phase, e.getReason(),
						"NAAT device " + credential.getDeviceSerialNumber() + " login failed: " + e.getMessage());
			}
		}
		return session;
	}

	/**
	 * Enrols the valid beneficiaries of each village in Ni-kshay with the village's NAAT
	 * device login, following BRD section 7: duplicate check, then a fresh Ni-kshay ID, the
	 * existing record or a new episode depending on the duplicate's status. The Ni-kshay ID
	 * and episode are saved and the beneficiary's next phases marked. Failures are added to
	 * the run.
	 */
	private List<NikshayEnrollResult> enrollBeneficiaries(Map<Integer, NikshayIdGenerateVillageBatch> batchesByVillage,
			NikshayPhaseRun run, NikshayFlowScenario flowScenario) {
		List<NikshayEnrollResult> enrolled = new ArrayList<>();
		Map<Integer, NikshaySession> sessionsByDevice = new HashMap<>();
		Map<Integer, NikshayLoginException> loginFailuresByDevice = new HashMap<>();

		for (NikshayIdGenerateVillageBatch batch : batchesByVillage.values()) {
			if (batch.getBeneficiaries().isEmpty()) {
				continue;
			}
			NikshaySession session = login(batch, sessionsByDevice, loginFailuresByDevice, NikshayPhase.ENROLMENT, run);
			if (session == null || !setPortalVillageId(session, batch, NikshayPhase.ENROLMENT, run)) {
				continue;
			}
			Integer deviceId = batch.getCredential().getDeviceDetailId();
			for (NikshayIdGenerateBeneficiaryDTO beneficiary : batch.getBeneficiaries()) {
				phaseStatusService.markInProgress(beneficiary.getBeneficiaryId(), beneficiary.getBeneficiaryType(),
						null, NikshayPhase.ENROLMENT);
				NikshayEnrollResult result;
				try {
					result = enrollBeneficiary(session, beneficiary, deviceId, null, false);
				} catch (NikshayApiException e) {
					fail(run, beneficiary, NikshayPhase.ENROLMENT, e);
					continue;
				}
				result.setFlowScenario(flowScenario);
				saveEnrollment(result);
				markEnrolled(beneficiary, result, run);
				enrolled.add(result);
			}
		}
		return enrolled;
	}

	/**
	 * Looks up the batch's village on the Ni-kshay portal by name, under the village's
	 * facility (the master has no Ni-kshay village ID), and sets it on every beneficiary of
	 * the batch. When the village is not found, or the lookup fails, every beneficiary of
	 * the batch fails the phase and false is returned.
	 */
	private boolean setPortalVillageId(NikshaySession session, NikshayIdGenerateVillageBatch batch, NikshayPhase phase,
			NikshayPhaseRun run) {
		// All beneficiaries of a batch have the same village, so the first one's details serve
		NikshayIdGenerateBeneficiaryDTO first = batch.getBeneficiaries().get(0);
		NikshayFailureReason failureReason = NikshayFailureReason.VILLAGE_MISSING;
		String reason = null;
		Long portalVillageId = null;
		try {
			portalVillageId = enrollClient.findVillageId(session, first.getSelectedHierarchyId(), first.getNikshayTuId(),
					first.getNikshayDistrictId(), first.getNikshayVillageName());
			if (portalVillageId == null) {
				reason = "V10: Village '" + first.getNikshayVillageName() + "' is not found in Ni-kshay under facility "
						+ first.getSelectedHierarchyId() + " (TU " + first.getNikshayTuId() + ")";
			}
		} catch (NikshayApiException e) {
			failureReason = e.getReason();
			reason = "Could not look up village '" + first.getNikshayVillageName() + "' in Ni-kshay: " + e.getMessage();
		}
		if (portalVillageId == null) {
			for (NikshayIdGenerateBeneficiaryDTO beneficiary : batch.getBeneficiaries()) {
				fail(run, beneficiary, phase, failureReason, reason);
			}
			return false;
		}
		logger.info("Village {} '{}' is Ni-kshay village {}", batch.getNikshayVillageId(),
				first.getNikshayVillageName(), portalVillageId);
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : batch.getBeneficiaries()) {
			beneficiary.setNikshayPortalVillageId(portalVillageId);
		}
		return true;
	}

	/**
	 * Adds the index patient's household members to Ni-kshay as their contacts (BRD section
	 * 8.1, spec Flow 2-A), with the same NAAT device login. First the household counts are
	 * written on the index patient (ContactTracing), then each member not yet in Ni-kshay
	 * is validated and enrolled with Source = contactTracing. The counts must go first:
	 * beta Ni-kshay links a contact to the index only when the index already has them. When
	 * the counts are not accepted, no contact is added. The contacts are added against the
	 * index patient's episode; when the index patient is on treatment, against their parent
	 * ID, and a contact who is already in Ni-kshay is left as is. Failures are added to the
	 * run under the contact's (or, for the counts, the index patient's) beneficiary ID. The
	 * index patient's CONTACTS phase and each contact's ENROLMENT phase are recorded.
	 */
	private void addContacts(NikshaySession session, NikshayIdGenerateBeneficiaryDTO index, NikshayEnrollResult indexResult,
			NikshayPhaseRun run) {
		phaseStatusService.markInProgress(index.getBeneficiaryId(), index.getBeneficiaryType(), null,
				NikshayPhase.CONTACTS);
		boolean indexOnTreatment = isOnTreatment(indexResult.getStageFound());
		String sourcePatientId = indexOnTreatment ? indexResult.getNikshayId() : indexResult.getEpisodeId();

		NikshayApiException countsFailure = updateContactCounts(session, sourcePatientId, index.getHouseholdMembers());
		if (countsFailure != null) {
			fail(run, index, NikshayPhase.CONTACTS, countsFailure.getReason(),
					"Household contact counts not accepted on index " + sourcePatientId + ": "
							+ countsFailure.getMessage(), countsFailure);
			// Contacts added now would not be linked to the index, so none are added
			for (NikshayHouseholdMemberDTO member : index.getHouseholdMembers()) {
				if (member.getNikshayId() == null) {
					phaseStatusService.markPending(member.getBeneficiaryId(), NikshayBeneficiaryType.CONTACT,
							index.getBeneficiaryId(), NikshayPhase.ENROLMENT, "Not added as a contact of index "
									+ sourcePatientId + " yet: household contact counts not accepted");
				}
			}
			return;
		}

		int added = 0;
		int toAdd = 0;
		NikshayFailureReason firstFailure = null;
		for (NikshayHouseholdMemberDTO member : index.getHouseholdMembers()) {
			if (member.getNikshayId() != null) {
				continue;
			}
			toAdd++;
			NikshayIdGenerateBeneficiaryDTO contact = toContact(index, member);
			List<String> problems = beneficiaryValidator.validate(contact);
			if (!problems.isEmpty()) {
				fail(run, contact, NikshayPhase.ENROLMENT, NikshayFailureReason.MANDATORY_FIELD_MISSING,
						String.join("; ", problems));
				firstFailure = firstFailure == null ? NikshayFailureReason.MANDATORY_FIELD_MISSING : firstFailure;
				continue;
			}
			phaseStatusService.markInProgress(contact.getBeneficiaryId(), contact.getBeneficiaryType(),
					contact.getIndexBeneficiaryId(), NikshayPhase.ENROLMENT);
			try {
				NikshayEnrollResult result = enrollBeneficiary(session, contact, indexResult.getDeviceId(),
						sourcePatientId, indexOnTreatment);
				if (result == null) {
					logger.info("Contact {} of index {} is already in Ni-kshay; index is on treatment, no action",
							contact.getBeneficiaryId(), sourcePatientId);
					phaseStatusService.markNotApplicable(contact.getBeneficiaryId(), contact.getBeneficiaryType(),
							contact.getIndexBeneficiaryId(), NikshayPhase.ENROLMENT,
							"Already in Ni-kshay; the index patient is on treatment, so no action");
					continue;
				}
				result.setFlowScenario(NikshayFlowScenario.POSITIVE);
				saveEnrollment(result);
				markEnrolled(contact, result, run);
				added++;
			} catch (NikshayApiException e) {
				fail(run, contact, NikshayPhase.ENROLMENT, e);
				firstFailure = firstFailure == null ? e.getReason() : firstFailure;
			}
		}
		logger.info("Index {}: {} household contact(s) added or linked in Ni-kshay", sourcePatientId, added);
		if (firstFailure == null) {
			String note = "Household counts sent; " + added + " contact(s) added or linked";
			phaseStatusService.markDone(index.getBeneficiaryId(), index.getBeneficiaryType(), null,
					NikshayPhase.CONTACTS, indexResult.getNikshayId(), sourcePatientId, note);
			run.getLogEntries().add(logService.success(run.getContext(), NikshayPhase.CONTACTS.getLogPhase(),
					index.getBeneficiaryId(), null, sourcePatientId, note));
		} else {
			// The index's own record of it; the contacts' rows have the errors
			String detail = (toAdd - added) + " of " + toAdd + " contact(s) not added; see each contact's enrolment";
			phaseStatusService.markFailed(index.getBeneficiaryId(), index.getBeneficiaryType(), null,
					NikshayPhase.CONTACTS, firstFailure, detail);
			run.getLogEntries().add(logService.failure(run.getContext(), NikshayPhase.CONTACTS.getLogPhase(),
					index.getBeneficiaryId(), null, firstFailure, detail));
		}
	}

	/**
	 * Records an enrolled beneficiary's ENROLMENT phase as DONE and marks their next phases.
	 * Linked to a record that is diagnosed or on treatment (no action): no test upload and
	 * no transfer out. Otherwise the X-ray upload, and the NAAT upload (MTB and Rif) except
	 * for an all-negative household (no NAAT), wait for their results. An index patient's contacts
	 * wait for the CONTACTS phase; a contact or all-negative member has none.
	 */
	private void markEnrolled(NikshayIdGenerateBeneficiaryDTO beneficiary, NikshayEnrollResult result,
			NikshayPhaseRun run) {
		Long benRegId = beneficiary.getBeneficiaryId();
		NikshayBeneficiaryType type = beneficiary.getBeneficiaryType();
		Long indexId = beneficiary.getIndexBeneficiaryId();
		String note = result.getLinkStatus() == NikshayLinkStatus.NEW ? null
				: result.getLinkStatus() + " (stage '" + result.getStageFound() + "')";
		phaseStatusService.markDone(benRegId, type, indexId, NikshayPhase.ENROLMENT, result.getNikshayId(),
				result.getEpisodeId(), note);
		run.getLogEntries().add(logService.success(run.getContext(), NikshayPhase.ENROLMENT.getLogPhase(), benRegId,
				result.getNikshayId(), result.getEpisodeId(), note == null ? "Enrolled" : note));

		if (result.getLinkStatus() == NikshayLinkStatus.LINKED_NO_ACTION) {
			String noAction = "Already '" + result.getStageFound() + "' in Ni-kshay: no action";
			phaseStatusService.markNotApplicable(benRegId, type, indexId, NikshayPhase.NAAT_UPLOAD, noAction);
			phaseStatusService.markNotApplicable(benRegId, type, indexId, NikshayPhase.XRAY_UPLOAD, noAction);
			phaseStatusService.markNotApplicable(benRegId, type, indexId, NikshayPhase.TRANSFER_OUT, noAction);
		} else {
			phaseStatusService.markPending(benRegId, type, indexId, NikshayPhase.XRAY_UPLOAD,
					"Waits for a completed chest X-ray result");
			if (type == NikshayBeneficiaryType.ALL_NEGATIVE) {
				phaseStatusService.markNotApplicable(benRegId, type, indexId, NikshayPhase.NAAT_UPLOAD,
						"All-negative household: no NAAT");
			} else {
				phaseStatusService.markPending(benRegId, type, indexId, NikshayPhase.NAAT_UPLOAD,
						"Waits for a completed NAAT result, uploaded after the chest X-ray");
			}
		}

		if (type == NikshayBeneficiaryType.INDEX) {
			phaseStatusService.markPending(benRegId, type, indexId, NikshayPhase.CONTACTS,
					"Waits for the CONTACTS phase run");
		} else if (type == NikshayBeneficiaryType.CONTACT) {
			phaseStatusService.markNotApplicable(benRegId, type, indexId, NikshayPhase.CONTACTS,
					"A contact has no contacts of their own");
		} else {
			phaseStatusService.markNotApplicable(benRegId, type, indexId, NikshayPhase.CONTACTS,
					"All-negative household: no index patient, so no contacts");
		}
	}

	/**
	 * For each index patient not enrolled in this run, records their household members as
	 * PENDING contacts ("waits for index patient ... to be enrolled"), so they show in the
	 * status list instead of being skipped silently. They are not failures of their own, so
	 * they are not added to the errored list. Left out: members who are index patients
	 * themselves (they have their own enrolment row), members already in Ni-kshay or
	 * enrolled in this run, and members already recorded for another index patient of the
	 * same household.
	 */
	private void markContactsOfIndexesNotEnrolled(List<NikshayIdGenerateBeneficiaryDTO> indexes,
			List<NikshayEnrollResult> enrolled) {
		Set<Long> enrolledIds = new HashSet<>();
		for (NikshayEnrollResult result : enrolled) {
			enrolledIds.add(result.getBeneficiaryId());
		}
		Set<Long> indexIds = new HashSet<>();
		for (NikshayIdGenerateBeneficiaryDTO index : indexes) {
			indexIds.add(index.getBeneficiaryId());
		}
		Set<Long> recorded = new HashSet<>();
		for (NikshayIdGenerateBeneficiaryDTO index : indexes) {
			if (enrolledIds.contains(index.getBeneficiaryId()) || index.getHouseholdMembers().isEmpty()) {
				continue;
			}
			phaseStatusService.markPending(index.getBeneficiaryId(), index.getBeneficiaryType(), null,
					NikshayPhase.CONTACTS, "Waits for the index patient's enrolment");
			for (NikshayHouseholdMemberDTO member : index.getHouseholdMembers()) {
				Long memberId = member.getBeneficiaryId();
				if (member.getNikshayId() != null || indexIds.contains(memberId) || enrolledIds.contains(memberId)
						|| !recorded.add(memberId)) {
					continue;
				}
				phaseStatusService.markPending(memberId, NikshayBeneficiaryType.CONTACT, index.getBeneficiaryId(),
						NikshayPhase.ENROLMENT, "Waits for index patient " + index.getBeneficiaryId() + " to be enrolled");
			}
		}
	}

	// Adds the failure to the run (response and job history log) and records the phase as FAILED
	private void fail(NikshayPhaseRun run, NikshayIdGenerateBeneficiaryDTO beneficiary, NikshayPhase phase,
			NikshayFailureReason reason, String detail) {
		fail(run, beneficiary, phase, reason, detail, null);
	}

	// A failed Ni-kshay call: also keeps what was sent and Ni-kshay's answer in the job history log
	private void fail(NikshayPhaseRun run, NikshayIdGenerateBeneficiaryDTO beneficiary, NikshayPhase phase,
			NikshayApiException e) {
		fail(run, beneficiary, phase, e.getReason(), e.getMessage(), e);
	}

	private void fail(NikshayPhaseRun run, NikshayIdGenerateBeneficiaryDTO beneficiary, NikshayPhase phase,
			NikshayFailureReason reason, String detail, NikshayApiException e) {
		addErrored(run, beneficiary.getBeneficiaryId(), phase, reason, detail);
		NikshayJobHistoryLog entry = logService.failure(run.getContext(), phase.getLogPhase(),
				beneficiary.getBeneficiaryId(), null, reason, detail);
		if (e != null) {
			entry.setRequestPayload(e.getRequestPayload());
			entry.setResponseBody(e.getResponseBody());
		}
		run.getLogEntries().add(entry);
		phaseStatusService.markFailed(beneficiary.getBeneficiaryId(), beneficiary.getBeneficiaryType(),
				beneficiary.getIndexBeneficiaryId(), phase, reason, detail);
	}

	// Adds the beneficiary to the run's response only
	private static void addErrored(NikshayPhaseRun run, Long benRegId, NikshayPhase phase, NikshayFailureReason reason,
			String detail) {
		NikshayErroredBeneficiaryDTO errored = new NikshayErroredBeneficiaryDTO(benRegId, reason, detail);
		errored.setPhase(phase.name());
		run.getErroredBeneficiaries().add(errored);
	}

	/**
	 * Writes the household counts on the index patient (ContactTracing), trying up to
	 * CONTACT_COUNTS_ATTEMPTS times a few seconds apart: beta Ni-kshay has once answered
	 * "Failed to update contact tracing details" and accepted the same counts later.
	 * Returns null when accepted, otherwise the last failure.
	 */
	private NikshayApiException updateContactCounts(NikshaySession session, String indexId,
			List<NikshayHouseholdMemberDTO> members) {
		NikshayApiException lastFailure = null;
		for (int attempt = 1; attempt <= CONTACT_COUNTS_ATTEMPTS; attempt++) {
			try {
				enrollClient.updateContactTracing(session, payloadBuilder.buildContactTracing(indexId, members));
				return null;
			} catch (NikshayApiException e) {
				lastFailure = e;
				logger.warn("Index {}: household contact counts not accepted (attempt {} of {}): {}", indexId,
						attempt, CONTACT_COUNTS_ATTEMPTS, e.getMessage());
			}
			if (attempt < CONTACT_COUNTS_ATTEMPTS) {
				try {
					Thread.sleep(CONTACT_COUNTS_RETRY_DELAY_MS);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return lastFailure;
				}
			}
		}
		return lastFailure;
	}

	/**
	 * A household member as a beneficiary to enrol as a contact. Their own name, age,
	 * gender, phone, caste, symptoms and HIV status; the household's address, area,
	 * socioeconomic status, pincode and Ni-kshay hierarchy from the index patient (beta
	 * Ni-kshay rejects a contact without a pincode, although the spec capture had null).
	 */
	private static NikshayIdGenerateBeneficiaryDTO toContact(NikshayIdGenerateBeneficiaryDTO index,
			NikshayHouseholdMemberDTO member) {
		NikshayIdGenerateBeneficiaryDTO contact = new NikshayIdGenerateBeneficiaryDTO();
		contact.setBeneficiaryId(member.getBeneficiaryId());
		contact.setBeneficiaryType(NikshayBeneficiaryType.CONTACT);
		contact.setIndexBeneficiaryId(index.getBeneficiaryId());
		contact.setHouseoldId(member.getHouseoldId());
		contact.setFirstName(member.getFirstName());
		contact.setLastName(member.getLastName());
		contact.setAge(member.getAge());
		contact.setDateOfBirth(member.getDateOfBirth());
		contact.setGender(member.getGender());
		contact.setPrimaryPhone(member.getPrimaryPhone());
		contact.setCaste(member.getCaste());
		// BRD section 12: asymptomatic for TPT, the symptom list (presumptive, V13) otherwise
		contact.setSymptom(member.getSymptom());
		contact.setHivStatus(member.getHivStatus());
		contact.setKeyPopulation("Contact of Known TB Patients");
		contact.setMaritalStatus("Unknown");
		contact.setOccupation("Unknown");
		contact.setAddress(index.getAddress());
		contact.setPincode(index.getPincode());
		contact.setArea(index.getArea());
		contact.setSocioeconomicStatus(index.getSocioeconomicStatus());
		contact.setResidenceHierarchyId(index.getResidenceHierarchyId());
		contact.setNikshayVillageId(index.getNikshayVillageId());
		contact.setNikshayVillageName(index.getNikshayVillageName());
		contact.setNikshayPortalVillageId(index.getNikshayPortalVillageId());
		contact.setNikshayStateId(index.getNikshayStateId());
		contact.setNikshayDistrictId(index.getNikshayDistrictId());
		contact.setNikshayTuId(index.getNikshayTuId());
		contact.setSelectedHierarchyId(index.getSelectedHierarchyId());
		return contact;
	}

	// 2.5 On treatment; "Diagnosed - not on treatment" also contains "on treatment"
	private static boolean isOnTreatment(String stage) {
		String normalized = stage == null ? "" : stage.toLowerCase().replaceAll("[^a-z]+", " ").trim();
		return normalized.contains("on treatment") && !normalized.contains("diagnosed")
				&& !normalized.contains("not on treatment");
	}

	/**
	 * Enrols one beneficiary (BRD section 7). Runs the duplicate check; with no firm match a
	 * fresh Ni-kshay ID is created, otherwise the match's status decides what happens.
	 * With a sourcePatientId the beneficiary is added as that index patient's contact.
	 * Returns null, with no change in Ni-kshay, when skipIfInNikshay is set and the
	 * beneficiary is already there.
	 */
	private NikshayEnrollResult enrollBeneficiary(NikshaySession session, NikshayIdGenerateBeneficiaryDTO beneficiary,
			Integer deviceId, String sourcePatientId, boolean skipIfInNikshay) throws NikshayApiException {
		List<JsonNode> possibleDuplicates = enrollClient.findPossibleDuplicates(session, beneficiary.getFirstName().trim(),
				beneficiary.getLastName().trim(), beneficiary.getGender(), beneficiary.getPrimaryPhone(),
				beneficiary.getAge(), beneficiary.getDateOfBirth());
		List<JsonNode> samePerson = new ArrayList<>();
		for (JsonNode record : possibleDuplicates) {
			if (isSamePerson(record, beneficiary)) {
				samePerson.add(record);
			}
		}

		if (skipIfInNikshay && !samePerson.isEmpty()) {
			return null;
		}

		// Case 1: not in Ni-kshay. Other hits (e.g. same phone only) are sent as a possible duplicate;
		// a contact always is, as they usually share the index patient's phone
		if (samePerson.isEmpty()) {
			ObjectNode payload = payloadBuilder.build(beneficiary, null,
					sourcePatientId != null || !possibleDuplicates.isEmpty(), sourcePatientId);
			String patientId = enrollClient.addIndiaTb(session, payload);
			logger.info("Beneficiary {}: created Ni-kshay ID {}", beneficiary.getBeneficiaryId(), patientId);
			return new NikshayEnrollResult(beneficiary.getBeneficiaryId(), patientId, patientId, NikshayLinkStatus.NEW,
					null, deviceId);
		}
		if (samePerson.size() > 1) {
			List<String> ids = new ArrayList<>();
			for (JsonNode record : samePerson) {
				ids.add(record.path("PatientId").asText());
			}
			throw new NikshayApiException(NikshayFailureReason.MULTIPLE_DUPLICATE_MATCHES,
					"V14: More than one Ni-kshay record matches this person: " + String.join(", ", ids));
		}

		JsonNode existing = samePerson.get(0);
		String existingId = existing.path("PatientId").asText();
		String stage = existing.path("Stage").asText(null);
		boolean outcomeAssigned = existing.path("IsPatientOutcomeAssigned").asBoolean(false);
		NikshayLinkStatus linkStatus = linkStatusFor(stage, outcomeAssigned);
		if (linkStatus == null) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_VALIDATION_ERROR,
					"V14: Ni-kshay record " + existingId + " has an unknown stage '" + stage + "'; review manually");
		}

		if (linkStatus == NikshayLinkStatus.NEW_EPISODE) {
			// Cases 2.2, 2.4, 2.6: new episode under the existing record (parent ID)
			ObjectNode payload = payloadBuilder.build(beneficiary, existingId, true, sourcePatientId);
			String episodeId = enrollClient.addIndiaTb(session, payload);
			logger.info("Beneficiary {}: created episode {} under Ni-kshay ID {} (stage '{}')",
					beneficiary.getBeneficiaryId(), episodeId, existingId, stage);
			return new NikshayEnrollResult(beneficiary.getBeneficiaryId(), existingId, episodeId, linkStatus, stage,
					deviceId);
		}
		// Case 2.1 (use the existing record), 2.3 and 2.5 (no action, ID only)
		logger.info("Beneficiary {}: linked to existing Ni-kshay ID {} as {} (stage '{}')",
				beneficiary.getBeneficiaryId(), existingId, linkStatus, stage);
		return new NikshayEnrollResult(beneficiary.getBeneficiaryId(), existingId, existingId, linkStatus, stage,
				deviceId);
	}

	/**
	 * BRD section 7: what to do with an existing Ni-kshay record, from its Stage and
	 * IsPatientOutcomeAssigned. Stages seen on beta (2026-10-29): "Presumptive (Open)",
	 * "Presumptive (Closed)" (outcome assigned), "Diagnosed But Pending Treatment
	 * (Notified)", "On Treatment (Notified)" and "Outcome Assigned (Notified)" (outcome
	 * assigned); the spec also shows enum-like "PRESUMPTIVE_OPEN". Null when the stage is
	 * not known.
	 */
	private static NikshayLinkStatus linkStatusFor(String stage, boolean outcomeAssigned) {
		// 2.6 Outcome assigned
		if (outcomeAssigned) {
			return NikshayLinkStatus.NEW_EPISODE;
		}
		String normalized = stage == null ? "" : stage.toLowerCase().replaceAll("[^a-z]+", " ").trim();
		if (normalized.contains("outcome")) {
			return NikshayLinkStatus.NEW_EPISODE;
		}
		if (normalized.contains("presumptive")) {
			if (normalized.contains("closed")) {
				// 2.2 Presumptive - Closed
				return NikshayLinkStatus.NEW_EPISODE;
			}
			if (normalized.contains("open")) {
				// 2.1 Presumptive - Open
				return NikshayLinkStatus.LINKED;
			}
			return null;
		}
		if (normalized.contains("diagnosed")) {
			// 2.4 Diagnosed - not on treatment, closed; 2.3 Diagnosed - not on treatment
			return normalized.contains("closed") ? NikshayLinkStatus.NEW_EPISODE : NikshayLinkStatus.LINKED_NO_ACTION;
		}
		if (normalized.contains("on treatment")) {
			// 2.5 On treatment
			return NikshayLinkStatus.LINKED_NO_ACTION;
		}
		return null;
	}

	/**
	 * Same person = same full name, gender and phone, and the same date of birth (or age,
	 * when either side has no date of birth). The portal's own search is mostly by phone
	 * and gender, so its hits are narrowed down here (as in the nikshay-enroll worker).
	 */
	private static boolean isSamePerson(JsonNode record, NikshayIdGenerateBeneficiaryDTO beneficiary) {
		String recordName = normalize(record.path("FirstName").asText("") + " " + record.path("LastName").asText(""));
		String name = normalize(beneficiary.getFirstName() + " " + beneficiary.getLastName());
		if (!recordName.equals(name)) {
			return false;
		}
		if (!normalize(record.path("Gender").asText("")).equals(normalize(beneficiary.getGender()))) {
			return false;
		}
		String recordPhone = record.hasNonNull("PrimaryPhoneNumber") ? record.get("PrimaryPhoneNumber").asText()
				: record.path("PrimaryPhone").asText("");
		if (!recordPhone.replaceAll("\\D", "").equals(beneficiary.getPrimaryPhone())) {
			return false;
		}
		String recordDob = record.path("DateOfBirth").asText("").trim();
		if (!recordDob.isEmpty() && beneficiary.getDateOfBirth() != null) {
			return recordDob.equals(beneficiary.getDateOfBirth());
		}
		return record.hasNonNull("Age") && beneficiary.getAge() != null
				&& record.get("Age").asInt() == beneficiary.getAge();
	}

	/**
	 * Saves the person's Ni-kshay ID on the beneficiary and adds the episode row
	 * (nikshay_beneficiary_episode). A failure here is only logged: the record is already in
	 * Ni-kshay, so the next run's duplicate check finds it and links it again.
	 */
	private void saveEnrollment(NikshayEnrollResult result) {
		try {
			int updated = nikshayIdGenerateRepository.saveEnrollment(result);
			if (updated == 0) {
				logger.warn("Beneficiary {}: Ni-kshay ID {} not saved on the beneficiary, identity record not found",
						result.getBeneficiaryId(), result.getNikshayId());
			}
		} catch (Exception e) {
			logger.error("Beneficiary {}: could not save Ni-kshay ID {} / episode {}", result.getBeneficiaryId(),
					result.getNikshayId(), result.getEpisodeId(), e);
		}
	}

	// Case- and whitespace-insensitive form for comparison
	private static String normalize(String value) {
		return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase();
	}
}
