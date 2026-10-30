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
package com.iemr.admin.controller.nikshayupload;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.service.nikshayclient.NikshayAuthClient;
import com.iemr.admin.service.nikshayclient.NikshayLoginException;
import com.iemr.admin.service.nikshayclient.NikshaySession;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Test endpoint for the Ni-kshay login, which runs the nikshay-enroll Node worker
 * (login-json.js) through NikshayAuthClient.
 */
@RestController
@RequestMapping(value = "/stopTb/nikshay", headers = "Authorization")
public class NikshayEnrollWorkerController {
	private static final Logger logger = LoggerFactory.getLogger(NikshayEnrollWorkerController.class);

	@Autowired
	private NikshayAuthClient nikshayAuthClient;

	@Value("${nikshay.portal.env:uat}")
	private String nikshayPortalEnv;

	/** Test only: checks a Ni-kshay login. Returns no cookies or tokens, only the outcome. */
	@Operation(summary = "Test a Ni-kshay portal login (username + password); returns success or the failure reason")
	@PostMapping(value = "/testLogin", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> testLogin(@RequestBody Map<String, String> body) {
		String username = body != null ? body.get("username") : null;
		String password = body != null ? body.get("password") : null;
		if (username == null || username.isBlank() || password == null || password.isEmpty()) {
			return ResponseEntity.badRequest().body("username and password are required");
		}
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("username", username);
		result.put("portal", nikshayPortalEnv);
		try {
			NikshaySession session = nikshayAuthClient.login(username, password);
			result.put("success", true);
			result.put("portalUrl", session.getPortalUrl());
			result.put("loggedInAt", session.getLoggedInAt().toString());
			return ResponseEntity.ok(result);
		} catch (NikshayLoginException e) {
			result.put("success", false);
			result.put("reason", e.getReason());
			result.put("message", e.getMessage());
			// Wrong credentials: 401; portal down or unexpected answer: 503
			return ResponseEntity
					.status(e.getReason() == NikshayFailureReason.LOGIN_REJECTED ? 401 : 503).body(result);
		} catch (Exception e) {
			logger.error("Error testing Ni-kshay login for {}", username, e);
			return ResponseEntity.status(500).body("Could not test the Ni-kshay login");
		}
	}
}
