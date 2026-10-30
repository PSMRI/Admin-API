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
package com.iemr.admin.service.nikshayupload;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.data.nikshayupload.NikshayTestResult;
import com.iemr.admin.service.nikshayclient.NikshayApiException;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * The AddDiagnosticsTest payload every test type shares (spec section 6): reason for testing
 * ("TB Screening", TrueNat "Diagnosis of TB"), new case, the device's PHI with its district
 * and state as testing facility, a result-only post (sample absent, result present, one stub sample), the lab serial
 * number from the device's serial number, the result date as test and report date,
 * "reported by", the episode as PatientId, plus the result mapped to Ni-kshay's final
 * interpretation. Subclasses give the test type, the lab serial prefix and the mapping.
 */
public abstract class DiagnosticsTestPayloadBuilder implements NikshayTestPayloadBuilder {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${nikshay.test.reported-by:By TBMJA application}")
	private String reportedBy;

	/** Ni-kshay's TestType, e.g. "Chest X Ray". */
	protected abstract String nikshayTestType();

	/** Start of the lab serial number, followed by the device serial number, e.g. "SAMPLE-XRAY-". */
	protected abstract String labSerialPrefix();

	/** EMR Lite result summary (lower case, single spaces) -> Ni-kshay final interpretation. */
	protected abstract Map<String, String> interpretations();

	/** Ni-kshay's ReasonForTesting: "TB Screening" unless the test type says otherwise. */
	protected String reasonForTesting() {
		return "TB Screening";
	}

	/** Adds the fields only this test type sends (none by default). */
	protected void addTestFields(ObjectNode payload) {
	}

	@Override
	public final ObjectNode build(NikshayTestResult result, NikshayDeviceCredentialDTO device) throws NikshayApiException {
		if (result.getNikshayId() == null || result.getNikshayId().isBlank()) {
			throw new NikshayApiException(NikshayFailureReason.MANDATORY_FIELD_MISSING,
					"The beneficiary has no Ni-kshay ID yet");
		}
		String interpretation = interpretations().get(normalize(result.getResult()));
		if (interpretation == null) {
			throw new NikshayApiException(NikshayFailureReason.MANDATORY_FIELD_MISSING, getTestType() + " result '"
					+ result.getResult() + "' can't be mapped to a Ni-kshay interpretation");
		}
		long facilityKey = nikshayCode(device.getFacilityNikshayCode(), device.getFacilityName(), "testing facility",
				device);
		long districtKey = nikshayCode(device.getDistrictNikshayCode(), device.getDistrictName(), "district", device);
		long stateKey = nikshayCode(device.getStateNikshayCode(), device.getStateName(), "state", device);
		if (device.getDeviceSerialNumber() == null || device.getDeviceSerialNumber().isBlank()) {
			throw new NikshayApiException(NikshayFailureReason.DEVICE_CONFIGURATION_MISSING,
					"Device " + device.getDeviceDetailId() + " has no serial number for the lab serial number");
		}
		// Date the result was saved, as the date tested and reported
		String testDate = new SimpleDateFormat("dd-MM-yyyy")
				.format(result.getResultDate() != null ? result.getResultDate() : new Date());

		ObjectNode payload = objectMapper.createObjectNode();
		payload.put("ReasonForTesting", reasonForTesting());
		payload.putNull("PredominantSymptom");
		payload.put("TypeOfCase", "New");
		payload.putArray("DiagnosisOfTB");
		payload.putArray("TypeOfPresumptiveDRTB");
		payload.put("TestType", nikshayTestType());
		payload.set("TestingFacilityState", keyValue(stateKey, device.getStateName()));
		payload.set("TestingFacilityDistrict", keyValue(districtKey, device.getDistrictName()));
		ObjectNode facility = payload.putObject("TestingFacility");
		facility.put("Key", facilityKey);
		facility.put("Value", device.getFacilityName());
		payload.put("SampleAvailability", "Absent");
		payload.put("ResultAvailability", "Present");
		ObjectNode sample = payload.putArray("TestSampleDetails").addObject();
		sample.put("SampleMappingOption", "Add New Sample");
		sample.put("SampleSpecimenType", "Sputum");
		sample.set("SampleCollectionSiteState", keyValue(stateKey, device.getStateName()));
		sample.set("SampleCollectionSiteDistrict", keyValue(districtKey, device.getDistrictName()));
		payload.put("LabSerialNumber", labSerialPrefix() + device.getDeviceSerialNumber().trim());
		payload.put("ResultDateTested", testDate);
		payload.put("ResultDateReported", testDate);
		payload.put("ResultReportedBy", reportedBy);
		payload.put("FLLPA_AddMutationDetails", "Skip");
		payload.put("SLLPA_AddMutationDetails", "Skip");
		payload.put("FinalInterpretation", interpretation);
		payload.putNull("ChestXRayResult");
		addTestFields(payload);
		payload.put("PatientId", result.getNikshayId().trim());
		return payload;
	}

	// The spec's geo KeyValue: {"Value": name, "Key": Ni-kshay ID, "Level": 4}
	private ObjectNode keyValue(long key, String value) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("Value", value);
		node.put("Key", key);
		node.put("Level", 4);
		return node;
	}

	// A Ni-kshay ID of the device PHI's hierarchy, from the master's NikshayCode
	private static long nikshayCode(String code, String name, String what, NikshayDeviceCredentialDTO device)
			throws NikshayApiException {
		try {
			if (code != null && name != null) {
				return Long.parseLong(code.trim());
			}
		} catch (NumberFormatException e) {
			// fall through
		}
		throw new NikshayApiException(NikshayFailureReason.DEVICE_CONFIGURATION_MISSING,
				"Device " + device.getDeviceSerialNumber() + " has no valid " + what + " (Ni-kshay code '" + code + "')");
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase();
	}
}
