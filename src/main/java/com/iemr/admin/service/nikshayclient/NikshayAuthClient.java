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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * Logs in to the Ni-kshay portal with a PHI login and returns a NikshaySession
 * that Java uses for the portal API calls.
 *
 * The portal refuses its login form from a plain HTTP client (HTTP 403), so the
 * login itself runs in a real browser: the nikshay-enroll Node/Playwright worker
 * (login-json.js) logs in and prints the portal's session cookies, and everything
 * after that is plain Java HTTP. Needs Node, `npm install` in nikshay-enroll and
 * Chrome (or Playwright's Chromium) on the Admin-API host.
 *
 * The password reaches the worker through its environment, never the command
 * line; the password and the cookies are never logged.
 */
@Service
public class NikshayAuthClient {
	private static final Logger logger = LoggerFactory.getLogger(NikshayAuthClient.class);

	static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
			+ "(KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";

	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(30);

	// Folder of the nikshay-enroll login worker and the Node binary that runs it
	@Value("${nikshay.enroll.dir:nikshay-enroll}")
	private String enrollDir;

	@Value("${nikshay.enroll.node:node}")
	private String nodeBinary;

	// Portal the worker logs in to: uat (beta.nikshay.in) or production (www.nikshay.in)
	@Value("${nikshay.portal.env:uat}")
	private String portalEnv;

	@Value("${nikshay.login.timeout-ms:180000}")
	private long timeoutMs;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Logs in and returns the session. Throws NikshayLoginException with
	 * LOGIN_REJECTED when the portal refuses the login, NIKSHAY_UNAVAILABLE when it
	 * can't be reached, the worker can't run or it answers unexpectedly.
	 */
	public NikshaySession login(String username, String password) throws NikshayLoginException {
		if (username == null || username.isBlank() || password == null || password.isEmpty()) {
			throw new NikshayLoginException(NikshayFailureReason.LOGIN_REJECTED, "Username and password are required");
		}
		JsonNode result = runLoginWorker(username, password);
		if (result.hasNonNull("error")) {
			boolean rejected = result.path("rejected").asBoolean(false);
			throw new NikshayLoginException(
					rejected ? NikshayFailureReason.LOGIN_REJECTED : NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay login failed for " + username + ": " + result.get("error").asText());
		}
		String origin = result.path("origin").asText("");
		String cookieHeader = cookieHeader(result.path("cookies"));
		if (origin.isEmpty() || cookieHeader.isEmpty()) {
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay login for " + username + " returned no session");
		}
		HttpClient client = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT)
				.followRedirects(HttpClient.Redirect.NORMAL).build();
		logger.info("Ni-kshay login succeeded for {} at {}", username, origin);
		NikshaySession session = new NikshaySession(client, origin, username, cookieHeader);
		return session;
	}

	/** Runs login-json.js and returns its stdout JSON. */
	private JsonNode runLoginWorker(String username, String password) throws NikshayLoginException {
		Path workDir = Path.of(enrollDir).toAbsolutePath().normalize();
		Path script = workDir.resolve("login-json.js");
		if (!Files.isRegularFile(script)) {
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "Ni-kshay login worker not found at "
					+ script + "; check nikshay.enroll.dir and run npm install there");
		}
		ProcessBuilder pb = new ProcessBuilder(List.of(nodeBinary, script.toString(), "--env", portalEnv));
		pb.directory(workDir.toFile());
		pb.environment().put("NIKSHAY_USER", username);
		pb.environment().put("NIKSHAY_PASS", password);

		logger.info("Ni-kshay login for {} ({}) via {}", username, portalEnv, script);
		Process process;
		try {
			process = pb.start();
		} catch (IOException e) {
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not start the Ni-kshay login worker (" + nodeBinary + "): " + e.getMessage(), e);
		}
		// Read both streams while the worker runs, so the timeout below really applies.
		CompletableFuture<String> stdout = CompletableFuture.supplyAsync(() -> readAll(process.getInputStream()));
		CompletableFuture.runAsync(() -> logLines(process.getErrorStream()));
		try {
			if (!process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
				process.destroyForcibly();
				throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
						"Ni-kshay login timed out after " + timeoutMs + " ms");
			}
			String output = stdout.get(5, TimeUnit.SECONDS);
			if (output.isBlank()) {
				throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
						"Ni-kshay login worker produced no output (exit " + process.exitValue() + ")");
			}
			return objectMapper.readTree(output);
		} catch (InterruptedException e) {
			process.destroyForcibly();
			Thread.currentThread().interrupt();
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "Ni-kshay login was interrupted",
					e);
		} catch (IOException | ExecutionException | TimeoutException e) {
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not read the Ni-kshay login worker's answer", e);
		}
	}

	// "name=value; name2=value2" from the worker's cookie list, as a browser sends it
	private static String cookieHeader(JsonNode cookies) {
		StringBuilder header = new StringBuilder();
		for (JsonNode cookie : cookies) {
			String name = cookie.path("name").asText("");
			if (name.isEmpty()) {
				continue;
			}
			if (header.length() > 0) {
				header.append("; ");
			}
			header.append(name).append('=').append(cookie.path("value").asText(""));
		}
		return header.toString();
	}

	private static String readAll(InputStream in) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			return "";
		}
	}

	// The worker's progress lines (stderr); they never contain the password or cookies.
	private static void logLines(InputStream in) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				logger.info("[nikshay-login] {}", line);
			}
		} catch (IOException e) {
			logger.debug("Error reading the Ni-kshay login worker's log", e);
		}
	}
}
