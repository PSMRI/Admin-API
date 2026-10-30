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
package com.iemr.admin.service.nikshayclient;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * Ni-kshay calls used to enrol a beneficiary: the village lookup, the duplicate check
 * (GetPossibleDuplicates), AddIndiaTB and the household counts (ContactTracing). Only the HTTP calls and reading the
 * answers; the decisions are made by NikshayIdGenerateService.
 */
@Service
public class NikshayEnrollClient {
	private static final Logger logger = LoggerFactory.getLogger(NikshayEnrollClient.class);

	private static final String POSSIBLE_DUPLICATES_PATH = "/Api/Patients/GetPossibleDuplicates";
	private static final String ADD_INDIA_TB_PATH = "/api/patients/AddIndiaTB";
	private static final String CONTACT_TRACING_PATH = "/Api/Patients/ContactTracing";

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * The Ni-kshay ID of the village with this name under the facility (all codes are
	 * Ni-kshay's own IDs), or null when no village has exactly this name. Looks, in order,
	 * at the facility's associated villages, all its child villages, then all villages of
	 * its TU and district. Names are compared exactly (ignoring case and extra spaces); no
	 * partial matches, since those can pick a different village. (From the earlier
	 * MMU-API ID generation, nikshay-id-generation-stash.)
	 */
	public Long findVillageId(NikshaySession session, Long facilityCode, Long tuCode, Long districtCode,
			String villageName) throws NikshayApiException {
		String[] paths = {
				"/API/Hierarchy/GetHierarchyChildrenKeyValue?id=" + facilityCode
						+ "&type=VILLAGE&fromAssociation=true&onlyActiveHierarchies=true",
				"/API/Hierarchy/GetHierarchyChildrenKeyValue?id=" + facilityCode
						+ "&type=VILLAGE&onlyActiveHierarchies=true",
				"/API/Hierarchy/GetHierarchyDescendentsAtSpecificLevel?parentId=" + tuCode + "&level=6&types=VILLAGE",
				"/API/Hierarchy/GetHierarchyDescendentsAtSpecificLevel?parentId=" + districtCode
						+ "&level=6&types=VILLAGE" };
		String wanted = normalize(villageName);
		for (String path : paths) {
			HttpResponse<String> response;
			try {
				response = session.get(path);
			} catch (IOException e) {
				throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
						"Could not reach Ni-kshay for the village list: " + e.getMessage());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
						"The village list was interrupted");
			}
			JsonNode body = readBody(response, "village list");
			JsonNode rows = body.isArray() ? body : body.path("Data");
			for (JsonNode row : rows) {
				String name = firstText(row, "Name", "Value", "name", "value");
				String id = firstText(row, "Id", "Key", "id", "key");
				// The list always ends with "Others" (ID 0), which is not a village
				if (normalize(name).equals(wanted) && id != null && id.matches("\\d+") && !id.equals("0")) {
					return Long.valueOf(id);
				}
			}
		}
		return null;
	}

	/**
	 * Records Ni-kshay already has that may be this person (the portal matches mainly on
	 * phone and gender). Date of birth is dd-MM-yyyy. Empty when there are none.
	 */
	public List<JsonNode> findPossibleDuplicates(NikshaySession session, String firstName, String lastName,
			String gender, String phone, Integer age, String dateOfBirth) throws NikshayApiException {
		String path = POSSIBLE_DUPLICATES_PATH + "?PrimaryPhone=" + encode(phone) + "&Gender=" + encode(gender)
				+ "&FirstName=" + encode(firstName) + "&LastName=" + encode(lastName) + "&Age="
				+ (age == null ? "" : age) + "&DateOfBirth=" + encode(dateOfBirth);
		HttpResponse<String> response;
		try {
			response = session.get(path);
		} catch (IOException e) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not reach Ni-kshay for the duplicate check: " + e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "The duplicate check was interrupted");
		}
		JsonNode body = readBody(response, "duplicate check");
		// No match is answered as HTTP 200 with Success false and Data null
		List<JsonNode> records = new ArrayList<>();
		for (JsonNode record : body.path("Data")) {
			records.add(record);
		}
		return records;
	}

	/**
	 * Creates the Ni-kshay record (a new person, or a new episode when the payload has a
	 * parentEpisodeId) and returns its PatientId. Throws NIKSHAY_VALIDATION_ERROR when
	 * Ni-kshay rejects it, NIKSHAY_UNAVAILABLE when it can't be reached.
	 */
	public String addIndiaTb(NikshaySession session, ObjectNode payload) throws NikshayApiException {
		String request = payload.toString();
		HttpResponse<String> response;
		try {
			response = session.postJson(ADD_INDIA_TB_PATH, request);
		} catch (IOException e) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not reach Ni-kshay for AddIndiaTB: " + e.getMessage()).withExchange(request, null);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "AddIndiaTB was interrupted")
					.withExchange(request, null);
		}
		try {
			return readPatientId(response);
		} catch (NikshayApiException e) {
			throw e.withExchange(request, exchange(response));
		}
	}

	// AddIndiaTB's PatientId, or why Ni-kshay did not give one
	private String readPatientId(HttpResponse<String> response) throws NikshayApiException {
		JsonNode body = readBody(response, "AddIndiaTB");
		if (response.statusCode() >= 400 || !body.path("Success").asBoolean(false)) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_VALIDATION_ERROR,
					"Ni-kshay rejected the enrolment (HTTP " + response.statusCode() + "): " + errorMessage(body));
		}
		if (!body.hasNonNull("PatientId")) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay reported success but returned no PatientId");
		}
		return body.get("PatientId").asText();
	}

	/**
	 * Writes the household contact counts on the index patient's episode (PUT
	 * ContactTracing). Creates no person. The response is logged. Beta Ni-kshay answers
	 * HTTP 200 with {"error": false} on success and {"error": "<message>"} on failure, so
	 * an HTTP error, an "error" other than false, or "Success": false is a failure.
	 */
	public void updateContactTracing(NikshaySession session, ObjectNode payload) throws NikshayApiException {
		String request = payload.toString();
		HttpResponse<String> response;
		try {
			response = session.putJson(CONTACT_TRACING_PATH, request);
		} catch (IOException e) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not reach Ni-kshay for ContactTracing: " + e.getMessage()).withExchange(request, null);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "ContactTracing was interrupted")
					.withExchange(request, null);
		}
		logger.info("ContactTracing for index {} answered HTTP {}: {}", payload.path("Id").asText(),
				response.statusCode(), response.body());
		try {
			checkContactTracing(response);
		} catch (NikshayApiException e) {
			throw e.withExchange(request, exchange(response));
		}
	}

	// Throws when the ContactTracing answer is a failure (see updateContactTracing)
	private void checkContactTracing(HttpResponse<String> response) throws NikshayApiException {
		String raw = response.body() == null ? "" : response.body().trim();
		if (raw.isEmpty() && response.statusCode() < 300) {
			return;
		}
		JsonNode body = readBody(response, "ContactTracing");
		JsonNode error = body.path("error");
		boolean errorReported = !error.isMissingNode() && !error.isNull()
				&& !(error.isBoolean() && !error.asBoolean());
		if (response.statusCode() >= 400 || errorReported
				|| (body.has("Success") && !body.path("Success").asBoolean(false))) {
			String message = error.isTextual() ? error.asText() : errorMessage(body);
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_VALIDATION_ERROR,
					"Ni-kshay rejected the household counts (HTTP " + response.statusCode() + "): " + message);
		}
	}

	// Ni-kshay's full answer, as saved in the job history log
	static String exchange(HttpResponse<String> response) {
		return "HTTP " + response.statusCode() + ": " + (response.body() == null ? "" : response.body());
	}

	private JsonNode readBody(HttpResponse<String> response, String what) throws NikshayApiException {
		int status = response.statusCode();
		if (status == 401 || status == 403) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay rejected the login session during the " + what + " (HTTP " + status + ")");
		}
		if (status >= 500) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay " + what + " returned HTTP " + status);
		}
		try {
			return objectMapper.readTree(response.body());
		} catch (IOException e) {
			// An expired session is redirected to the HTML login page instead of answering JSON
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay " + what + " answered without JSON; the login session has probably expired");
		}
	}

	private static String errorMessage(JsonNode body) {
		JsonNode error = body.path("Error");
		if (error.hasNonNull("Message")) {
			return error.get("Message").asText();
		}
		for (String field : new String[] { "ErrorMessage", "Message", "Error" }) {
			if (body.hasNonNull(field) && body.get(field).isTextual() && !body.get(field).asText().isBlank()) {
				return body.get(field).asText();
			}
		}
		String raw = body.toString();
		return raw.length() > 300 ? raw.substring(0, 300) + "…" : raw;
	}

	private static String firstText(JsonNode row, String... fields) {
		for (String field : fields) {
			if (row.hasNonNull(field)) {
				return row.get(field).asText().trim();
			}
		}
		return null;
	}

	// Case- and whitespace-insensitive form for comparing names
	private static String normalize(String value) {
		return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase();
	}

	private static String encode(String value) {
		return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
	}
}
