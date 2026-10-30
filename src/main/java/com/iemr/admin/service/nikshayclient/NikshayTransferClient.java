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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * Ni-kshay transfer out: POST /v1/transfer on the registry service (registryservice.beta.nikshay.in
 * on UAT, registryservice.nikshay.in on production), with the portal login's cookies. Moves
 * an episode from one PHI to another.
 */
@Service
public class NikshayTransferClient {
	private static final Logger logger = LoggerFactory.getLogger(NikshayTransferClient.class);

	private static final String TRANSFER_PATH = "/v1/transfer";

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Sends the transfer and returns Ni-kshay's answer ("HTTP status: body"), which is
	 * logged since its format is not documented. Throws NikshayApiException with the
	 * request and answer: TRANSFER_REJECTED when Ni-kshay refuses it, NIKSHAY_UNAVAILABLE
	 * when it can't be reached or no longer accepts the login session.
	 */
	public String transfer(NikshaySession session, ObjectNode payload) throws NikshayApiException {
		String request = payload.toString();
		String url = registryUrl(session.getPortalUrl()) + TRANSFER_PATH;
		HttpResponse<String> response;
		try {
			response = session.postJsonToUrl(url, request);
		} catch (IOException e) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not reach Ni-kshay for the transfer: " + e.getMessage()).withExchange(request, null);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "The transfer was interrupted")
					.withExchange(request, null);
		}
		String answer = NikshayEnrollClient.exchange(response);
		logger.info("Transfer of episode {} answered {}", payload.path("episodeId").asText(), answer);
		try {
			checkTransfer(response);
		} catch (NikshayApiException e) {
			throw e.withExchange(request, answer);
		}
		return answer;
	}

	// Throws when the transfer answer is a failure: login session rejected or expired, a
	// server error, an HTTP error, or a JSON body reporting an error
	private void checkTransfer(HttpResponse<String> response) throws NikshayApiException {
		int status = response.statusCode();
		if (status == 401 || status == 403) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay rejected the login session for the transfer (HTTP " + status + ")");
		}
		if (status >= 500) {
			throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay transfer returned HTTP " + status);
		}
		String raw = response.body() == null ? "" : response.body().trim();
		JsonNode body = null;
		if (!raw.isEmpty()) {
			try {
				body = objectMapper.readTree(raw);
			} catch (IOException e) {
				// An expired session is redirected to the HTML login page instead of answering JSON
				throw new NikshayApiException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
						"Ni-kshay transfer answered without JSON; the login session has probably expired");
			}
		}
		boolean errorReported = body != null && (isFalse(body, "Success") || isFalse(body, "success")
				|| isError(body.path("error")) || isError(body.path("Error")));
		if (status >= 400 || errorReported) {
			throw new NikshayApiException(NikshayFailureReason.TRANSFER_REJECTED,
					"Ni-kshay rejected the transfer (HTTP " + status + "): " + message(body, raw));
		}
	}

	// UAT https://beta.nikshay.in -> https://registryservice.beta.nikshay.in;
	// production https://www.nikshay.in -> https://registryservice.nikshay.in
	static String registryUrl(String portalUrl) {
		String host = portalUrl.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
		return "https://registryservice." + host;
	}

	private static boolean isFalse(JsonNode body, String field) {
		return body.has(field) && body.get(field).isBoolean() && !body.get(field).asBoolean();
	}

	// An "error" field other than false / null / empty
	private static boolean isError(JsonNode error) {
		if (error.isMissingNode() || error.isNull()) {
			return false;
		}
		if (error.isBoolean()) {
			return error.asBoolean();
		}
		return !error.asText().isBlank() || error.isObject();
	}

	private static String message(JsonNode body, String raw) {
		if (body != null) {
			for (String field : new String[] { "ErrorMessage", "Message", "message", "error", "Error" }) {
				JsonNode node = body.path(field);
				if (node.isTextual() && !node.asText().isBlank()) {
					return node.asText();
				}
				if (node.isObject() && node.path("Message").isTextual()) {
					return node.path("Message").asText();
				}
			}
		}
		return raw.length() > 300 ? raw.substring(0, 300) + "…" : raw;
	}
}
