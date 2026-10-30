package com.iemr.admin.service.nikshayupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.data.nikshayidgenerate.NikshayEnrollResult;
import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayLinkStatus;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseCandidate;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseStatus;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.data.nikshayupload.NikshayTestResult;
import com.iemr.admin.data.nikshayupload.NikshayVillageBatch;
import com.iemr.admin.repo.nikshayidgenerate.NikshayIdGenerateRepository;
import com.iemr.admin.repo.nikshayupload.DiagnosticOrderRepo;
import com.iemr.admin.service.nikshayclient.NikshayDeviceCredentialService;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;
import com.iemr.admin.service.nikshayjoblog.NikshayLogStatus;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseRun;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseStatusService;

/**
 * Uploads Stop TB test results to Ni-kshay: the NAAT_UPLOAD phase (TrueNat MTB and Rif
 * resistance) and the XRAY_UPLOAD phase (chest X-ray).
 */
@Service
public class NikshayUploadService {
	private static final Logger logger = LoggerFactory.getLogger(NikshayUploadService.class);

	@Autowired
	private DiagnosticOrderRepo diagnosticOrderRepo;

	@Autowired
	private NikshayDeviceCredentialService credentialService;

	@Autowired
	private NikshayJobHistoryLogService logService;

	@Autowired
	private NikshayTestResultPusher testResultPusher;

	@Autowired
	private XrayTestPayloadBuilder xrayPayloadBuilder;

	@Autowired
	private NaatTestPayloadBuilder naatPayloadBuilder;

	@Autowired
	private RifTestPayloadBuilder rifPayloadBuilder;

	@Autowired
	private NikshayPhaseStatusService phaseStatusService;

	@Autowired
	private NikshayIdGenerateRepository idGenerateRepository;

	/**
	 * NAAT_UPLOAD phase (TrueNat MTB and Rif resistance results) or XRAY_UPLOAD phase (chest
	 * X-ray results): uploads the completed results of up to limit enrolled beneficiaries to
	 * their latest Ni-kshay episode, each with its own Ni-kshay test type. Without benRegIds,
	 * the beneficiaries waiting in the phase whose enrolment is done and who have a result,
	 * and those DONE who have a new result (e.g. a Rif result after the MTB one); with
	 * benRegIds, those beneficiaries whatever their status. A beneficiary linked to a record
	 * that is diagnosed or on treatment gets no upload (BRD section 7). Error / invalid runs
	 * are not uploaded. Each beneficiary's phase is DONE when all their results are in
	 * Ni-kshay (results uploaded before are not sent again), otherwise FAILED with the first
	 * error. Failures are added to the run; reason for testing is always "TB Screening".
	 */
	public void runPhase(NikshayPhase phase, List<Long> benRegIds, int limit, NikshayPhaseRun run) {
		List<NikshayTestType> testTypes = new ArrayList<>();
		if (phase == NikshayPhase.XRAY_UPLOAD) {
			testTypes.add(NikshayTestType.XRAY);
		} else {
			testTypes.add(NikshayTestType.NAAT);
			testTypes.add(NikshayTestType.RIF);
		}
		String testName = phase == NikshayPhase.XRAY_UPLOAD ? "chest X-ray" : "NAAT";

		List<NikshayPhaseCandidate> candidates = new ArrayList<>();
		if (benRegIds == null) {
			candidates.addAll(phaseStatusService.findWaiting(phase, limit));
		} else {
			List<Long> enrolledIds = new ArrayList<>();
			for (NikshayPhaseCandidate row : phaseStatusService.findRows(NikshayPhase.ENROLMENT, benRegIds)) {
				if (NikshayPhaseStatus.DONE.name().equals(row.getStatus())) {
					candidates.add(row);
					enrolledIds.add(row.getBenRegId());
				}
			}
			for (Long benRegId : benRegIds) {
				if (!enrolledIds.contains(benRegId)) {
					addErrored(run, benRegId, phase, null, "Not enrolled in Ni-kshay yet");
				}
			}
			// Once transferred out, the episode belongs to the designated PHI: no more uploads
			for (NikshayPhaseCandidate row : phaseStatusService.findRows(NikshayPhase.TRANSFER_OUT, enrolledIds)) {
				if (NikshayPhaseStatus.DONE.name().equals(row.getStatus())) {
					candidates.removeIf(candidate -> candidate.getBenRegId().equals(row.getBenRegId()));
					addErrored(run, row.getBenRegId(), phase, null,
							"Already transferred out: test results are no longer uploaded");
				}
			}
			if (phase == NikshayPhase.NAAT_UPLOAD) {
				// The NAAT goes after the chest X-ray; the row stays PENDING until then
				Set<Long> waitingForXray = phaseStatusService.findWaitingForXray(enrolledIds);
				candidates.removeIf(candidate -> waitingForXray.contains(candidate.getBenRegId()));
				for (Long benRegId : waitingForXray) {
					addErrored(run, benRegId, phase, null,
							"Chest X-ray not uploaded yet: the NAAT is uploaded after it");
				}
			}
		}
		logger.info("{}: {} beneficiaries to upload", phase, candidates.size());
		if (candidates.isEmpty()) {
			return;
		}

		List<Long> ids = new ArrayList<>();
		for (NikshayPhaseCandidate candidate : candidates) {
			ids.add(candidate.getBenRegId());
		}
		Map<Long, NikshayEnrollResult> episodes = idGenerateRepository.findLatestEpisodes(ids);
		// The beneficiaries to upload, by BeneficiaryID (the test orders' beneficiary_id)
		Map<Long, NikshayPhaseCandidate> toUpload = new LinkedHashMap<>();
		for (NikshayPhaseCandidate candidate : candidates) {
			NikshayEnrollResult episode = episodes.get(candidate.getBenRegId());
			if (episode == null) {
				fail(run, candidate, phase, NikshayFailureReason.MANDATORY_FIELD_MISSING,
						"The beneficiary has no Ni-kshay episode saved yet");
			} else if (episode.getLinkStatus() == NikshayLinkStatus.LINKED_NO_ACTION) {
				phaseStatusService.markNotApplicable(candidate.getBenRegId(), candidate.getBeneficiaryType(),
						candidate.getIndexBenRegId(), phase,
						"Already '" + episode.getStageFound() + "' in Ni-kshay: no action");
			} else if (candidate.getBeneficiaryId() == null) {
				fail(run, candidate, phase, NikshayFailureReason.MANDATORY_FIELD_MISSING,
						"The beneficiary's BeneficiaryID is not found");
			} else {
				toUpload.put(candidate.getBeneficiaryId(), candidate);
			}
		}
		if (toUpload.isEmpty()) {
			return;
		}

		// The results to send, by test type; error / invalid runs are not sent (no Ni-kshay
		// option, the test is redone), nor results not entered yet
		Map<NikshayTestType, List<NikshayTestResult>> toSendByType = new LinkedHashMap<>();
		Map<Long, Integer> resultCounts = new HashMap<>();
		Map<Long, String> invalidResults = new HashMap<>();
		for (NikshayTestType testType : testTypes) {
			List<NikshayTestResult> toSend = new ArrayList<>();
			for (NikshayTestResult result : findResults(testType, new ArrayList<>(toUpload.keySet()))) {
				NikshayPhaseCandidate candidate = toUpload.get(result.getBeneficiaryId());
				String summary = result.getResult() == null ? "" : result.getResult().trim();
				if (summary.isEmpty()) {
					continue;
				}
				if (isInvalidResult(summary)) {
					invalidResults.put(candidate.getBenRegId(), testType + " result '" + summary
							+ "' is an error / invalid run: not uploaded, waits for a retest");
					continue;
				}
				// The test goes to the latest episode, not the Ni-kshay ID
				result.setNikshayId(episodes.get(candidate.getBenRegId()).getEpisodeId());
				toSend.add(result);
				resultCounts.merge(candidate.getBenRegId(), 1, Integer::sum);
			}
			toSendByType.put(testType, toSend);
		}
		for (NikshayPhaseCandidate candidate : toUpload.values()) {
			if (resultCounts.containsKey(candidate.getBenRegId())) {
				phaseStatusService.markInProgress(candidate.getBenRegId(), candidate.getBeneficiaryType(),
						candidate.getIndexBenRegId(), phase);
			} else if (invalidResults.containsKey(candidate.getBenRegId())) {
				fail(run, candidate, phase, NikshayFailureReason.TEST_RESULT_INVALID,
						invalidResults.get(candidate.getBenRegId()));
			} else {
				phaseStatusService.markPending(candidate.getBenRegId(), candidate.getBeneficiaryType(),
						candidate.getIndexBenRegId(), phase, "Waits for a completed " + testName + " result");
			}
		}

		List<NikshayJobHistoryLog> logEntries = new ArrayList<>();
		try {
			for (NikshayTestType testType : testTypes) {
				Map<Integer, NikshayVillageBatch> batchesByVillage = prepareBatches(toSendByType.get(testType), testType,
						run.getContext(), logEntries);
				testResultPusher.push(batchesByVillage, payloadBuilder(testType), run.getContext(), logEntries);
			}
		} finally {
			run.getLogEntries().addAll(logEntries);
		}

		// Each beneficiary's first failure; one with none has all their results in Ni-kshay
		Map<Long, NikshayJobHistoryLog> firstFailures = new HashMap<>();
		for (NikshayJobHistoryLog entry : logEntries) {
			if (entry.getStatus() == NikshayLogStatus.FAILED && !firstFailures.containsKey(entry.getBenId())) {
				firstFailures.put(entry.getBenId(), entry);
			}
		}
		for (NikshayPhaseCandidate candidate : toUpload.values()) {
			Integer count = resultCounts.get(candidate.getBenRegId());
			if (count == null) {
				continue;
			}
			NikshayJobHistoryLog failure = firstFailures.get(candidate.getBenRegId());
			if (failure != null) {
				addErrored(run, candidate.getBenRegId(), phase, failure.getReason(), failure.getMessage());
				phaseStatusService.markFailed(candidate.getBenRegId(), candidate.getBeneficiaryType(),
						candidate.getIndexBenRegId(), phase, failure.getReason(), failure.getMessage());
				continue;
			}
			NikshayEnrollResult episode = episodes.get(candidate.getBenRegId());
			String note = count + " " + testName + " result(s) in Ni-kshay";
			if (invalidResults.containsKey(candidate.getBenRegId())) {
				note += "; " + invalidResults.get(candidate.getBenRegId());
			}
			phaseStatusService.markDone(candidate.getBenRegId(), candidate.getBeneficiaryType(),
					candidate.getIndexBenRegId(), phase, episode.getNikshayId(), episode.getEpisodeId(), note);
			phaseStatusService.markOtherTestNotDone(candidate.getBenRegId(), candidate.getBeneficiaryId(),
					candidate.getBeneficiaryType(), candidate.getIndexBenRegId(), phase);
			phaseStatusService.markTransferOutIfTestsDone(candidate.getBenRegId(), candidate.getBeneficiaryType(),
					candidate.getIndexBenRegId());
		}
	}

	// The completed results of the test type for the given BeneficiaryIDs
	private List<NikshayTestResult> findResults(NikshayTestType testType, List<Long> beneficiaryIds) {
		List<NikshayTestResult> results = new ArrayList<>();
		switch (testType) {
			case NAAT:
				results.addAll(diagnosticOrderRepo.findMtbResults(beneficiaryIds));
				break;
			case RIF:
				results.addAll(diagnosticOrderRepo.findRifResults(beneficiaryIds));
				break;
			case XRAY:
				results.addAll(diagnosticOrderRepo.findXrayResults(beneficiaryIds));
				break;
		}
		return results;
	}

	private NikshayTestPayloadBuilder payloadBuilder(NikshayTestType testType) {
		switch (testType) {
			case NAAT:
				return naatPayloadBuilder;
			case RIF:
				return rifPayloadBuilder;
			default:
				return xrayPayloadBuilder;
		}
	}

	// An error / invalid run, e.g. NAAT "Error-2" or X-ray "AI Invalid Result" (BRD mapping table)
	private static boolean isInvalidResult(String summary) {
		String normalized = normalizeName(summary);
		return normalized.startsWith("error") || normalized.contains("invalid");
	}

	// Adds the failure to the run (response and job history log) and records the phase as FAILED
	private void fail(NikshayPhaseRun run, NikshayPhaseCandidate candidate, NikshayPhase phase,
			NikshayFailureReason reason, String detail) {
		addErrored(run, candidate.getBenRegId(), phase, reason, detail);
		run.getLogEntries().add(logService.failure(run.getContext(), phase.getLogPhase(), candidate.getBenRegId(), null,
				reason, detail));
		phaseStatusService.markFailed(candidate.getBenRegId(), candidate.getBeneficiaryType(),
				candidate.getIndexBenRegId(), phase, reason, detail);
	}

	// Adds the beneficiary to the run's response only
	private static void addErrored(NikshayPhaseRun run, Long benRegId, NikshayPhase phase, NikshayFailureReason reason,
			String detail) {
		NikshayErroredBeneficiaryDTO errored = new NikshayErroredBeneficiaryDTO(benRegId, reason, detail);
		errored.setPhase(phase.name());
		run.getErroredBeneficiaries().add(errored);
	}

	/**
	 * Keeps the results whose village is a Ni-kshay village, finds each village's device
	 * login and groups the results by village. Every result left out gets a FAILED entry
	 * in logEntries with its reason, so none is dropped silently.
	 */
	private Map<Integer, NikshayVillageBatch> prepareBatches(List<NikshayTestResult> results,
			NikshayTestType testType, NikshayJobRunContext ctx, List<NikshayJobHistoryLog> logEntries) {
		List<NikshayTestResult> inNikshayVillage = new ArrayList<>();
		for (NikshayTestResult result : results) {
			String problem = villageProblem(result);
			if (problem == null) {
				inNikshayVillage.add(result);
			} else {
				logEntries.add(logService.failure(ctx, testType.getPhase(), result.getBenRegId(),
						result.getExternalOrderId(), NikshayFailureReason.VILLAGE_MISSING, problem));
			}
		}
		Set<Integer> undecryptableVillages = new HashSet<>();
		Map<Integer, NikshayDeviceCredentialDTO> credentialsByVillage = credentialService.findCredentials(
				inNikshayVillage.stream().map(NikshayTestResult::getVillageId).collect(Collectors.toList()),
				testType.getDeviceName(), undecryptableVillages);
		return groupByVillage(inNikshayVillage, testType, credentialsByVillage, undecryptableVillages, ctx,
				logEntries);
	}

	/**
	 * Groups results by village, pairing each village with its device login. A result
	 * from a village with no usable login is logged as failed: DEVICE_CONFIGURATION_MISSING
	 * when the device's password couldn't be decrypted, VILLAGE_MISSING when no active
	 * device of this type is mapped to the village.
	 */
	private Map<Integer, NikshayVillageBatch> groupByVillage(List<NikshayTestResult> results,
			NikshayTestType testType, Map<Integer, NikshayDeviceCredentialDTO> credentialsByVillage,
			Set<Integer> undecryptableVillages, NikshayJobRunContext ctx, List<NikshayJobHistoryLog> logEntries) {
		Map<Integer, NikshayVillageBatch> batchesByVillage = new LinkedHashMap<>();
		for (NikshayTestResult result : results) {
			Integer villageId = result.getVillageId();
			NikshayDeviceCredentialDTO credential = credentialsByVillage.get(villageId);
			if (credential == null) {
				boolean undecryptable = undecryptableVillages.contains(villageId);
				logEntries.add(logService.failure(ctx, testType.getPhase(), result.getBenRegId(),
						result.getExternalOrderId(),
						undecryptable ? NikshayFailureReason.DEVICE_CONFIGURATION_MISSING
								: NikshayFailureReason.VILLAGE_MISSING,
						undecryptable
								? "The " + testType + " device password for village " + villageId
										+ " could not be decrypted"
								: "No active " + testType + " device is mapped to village '" + result.getVillage()
										+ "' (" + villageId + ")"));
				continue;
			}
			batchesByVillage.computeIfAbsent(villageId,
					id -> new NikshayVillageBatch(id, result.getVillage(), credential)).getResults().add(result);
		}
		logger.info("Grouped {} result(s) into {} village batch(es)",
				batchesByVillage.values().stream().mapToInt(b -> b.getResults().size()).sum(),
				batchesByVillage.size());
		return batchesByVillage;
	}

	/**
	 * Null when the result's village ID is a Ni-kshay village ID, i.e. the village name
	 * saved at registration matches m_nikshay_village's name for that ID; otherwise the
	 * reason it isn't. Stop TB registrations before the 2026-07-28 fix saved AMRIT village
	 * IDs, whose numbers can belong to a different Ni-kshay village; using them would pick
	 * the wrong device and send the result under the wrong village.
	 */
	private String villageProblem(NikshayTestResult result) {
		if (result.getVillageId() == null) {
			return "The beneficiary has no current village";
		}
		if (result.getNikshayVillageName() == null) {
			return "Village '" + result.getVillage() + "' (" + result.getVillageId()
					+ ") is not a Ni-kshay village ID";
		}
		if (!normalizeName(result.getVillage()).equals(normalizeName(result.getNikshayVillageName()))) {
			return "Village '" + result.getVillage() + "' (" + result.getVillageId()
					+ ") is not a Ni-kshay village; Ni-kshay has '" + result.getNikshayVillageName()
					+ "' for this ID";
		}
		return null;
	}

	// Case- and whitespace-insensitive form of a village name for comparison.
	private static String normalizeName(String name) {
		return name == null ? "" : name.trim().replaceAll("\\s+", " ").toLowerCase();
	}
}
