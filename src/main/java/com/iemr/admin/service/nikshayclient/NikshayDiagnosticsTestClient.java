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
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * Calls Ni-kshay's "add diagnostics test" API (used for both Chest X-ray and
 * TrueNat results) with a logged-in session and reads its answer. Only the HTTP
 * call and the response: payloads are built by the test type's payload builder.
 */
@Service
public class NikshayDiagnosticsTestClient {

	private static final String ADD_DIAGNOSTICS_TEST_PATH = "/API/TestResults/AddDiagnosticsTest";

	private final ObjectMapper objectMapper = new ObjectMapper();

	/** IDs Ni-kshay gives the added test. */
	public static class AddedTest {
		private final long requestId;
		private final long resultId;

		AddedTest(long requestId, long resultId) {
			this.requestId = requestId;
			this.resultId = resultId;
		}

		public long getRequestId() {
			return requestId;
		}

		public long getResultId() {
			return resultId;
		}
	}

	/**
	 * Adds the test. Throws NikshayApiException: NIKSHAY_VALIDATION_ERROR when
	 * Ni-kshay rejects it (Success false, with its message), NIKSHAY_UNAVAILABLE when it
	 * can't be reached or no longer accepts the login session.
	 */
	public AddedTest addDiagnosticsTest(NikshaySession session, ObjectNode payload) throws NikshayApiException {
		String request = payload.toString();
		HttpResponse<String> response;
		try {
			response = session.postJson(ADD_DIAGNOSTICS_TEST_PATH, request);
		} catch (IOException e) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not reach Ni-kshay: " + e.getMessage()).withExchange(request, null);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "Upload was interrupted")
					.withExchange(request, null);
		}
		try {
			return readAddedTest(response);
		} catch (NikshayApiException e) {
			throw e.withExchange(request, NikshayEnrollClient.exchange(response));
		}
	}

	// The added test's IDs, or why Ni-kshay did not give them
	private AddedTest readAddedTest(HttpResponse<String> response) throws NikshayApiException {
		int status = response.statusCode();
		if (status == 401 || status == 403) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay rejected the login session (HTTP " + status + ")");
		}
		if (status >= 500) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay returned HTTP " + status);
		}
		JsonNode body;
		try {
			body = objectMapper.readTree(response.body());
		} catch (IOException e) {
			// An expired session is redirected to the HTML login page instead of answering JSON
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay answered HTTP " + status + " without JSON; the login session has probably expired");
		}
		if (status >= 400 || !body.path("Success").asBoolean(false)) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_VALIDATION_ERROR,
					"Ni-kshay rejected the test (HTTP " + status + "): " + errorMessage(body));
		}
		JsonNode data = body.path("Data");
		if (!data.hasNonNull("ResultId")) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay reported success but returned no ResultId");
		}
		return new AddedTest(data.path("RequestId").asLong(), data.path("ResultId").asLong());
	}

	private static String errorMessage(JsonNode body) {
		for (String field : new String[] { "ErrorMessage", "Message", "Error" }) {
			if (body.hasNonNull(field) && !body.get(field).asText().isBlank()) {
				return body.get(field).asText();
			}
		}
		String raw = body.toString();
		return raw.length() > 300 ? raw.substring(0, 300) + "…" : raw;
	}
}
