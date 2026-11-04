/*
* AMRIT – Accessible Medical Records via Integrated Technologies
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

import java.net.http.HttpClient;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * Logs in to the Ni-kshay portal with a PHI login and returns a NikshaySession
 * for the portal API calls. The login is plain Java HTTP. nikshay.portal.env
 * selects the host: uat is https://beta.nikshay.in, production is
 * https://www.nikshay.in. Any other value is refused.
 * The password and the cookies are never logged.
 */
@Service
public class NikshayAuthClient {
	private static final Logger logger = LoggerFactory.getLogger(NikshayAuthClient.class);

	// Chrome on Linux, so Ni-kshay sees a browser request rather than a Java client.
	static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
			+ "(KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";
	static final String CHROME_UA = "\"Chromium\";v=\"128\", \"Not;A=Brand\";v=\"24\", \"Google Chrome\";v=\"128\"";
	static final String CHROME_UA_PLATFORM = "\"Linux\"";

	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(30);

	// uat is beta.nikshay.in. production is www.nikshay.in. Anything else is refused.
	@Value("${nikshay.portal.env:uat}")
	private String portalEnv;

	/**
	 * Logs in on the portal selected by nikshay.portal.env and returns the session.
	 * LOGIN_REJECTED when the portal refuses the login or the session origin does
	 * not match that env. NIKSHAY_UNAVAILABLE when the portal cannot be reached or
	 * the env is not uat or production.
	 */
	public NikshaySession login(String username, String password) throws NikshayLoginException {
		if (username == null || username.isBlank() || password == null || password.isEmpty()) {
			throw new NikshayLoginException(NikshayFailureReason.LOGIN_REJECTED, "Username and password are required");
		}
		String env = portalEnv == null || portalEnv.isBlank() ? "uat" : portalEnv.trim();
		String expectedOrigin = "production".equalsIgnoreCase(env) ? "https://www.nikshay.in" : "https://beta.nikshay.in";
		if (!"uat".equalsIgnoreCase(env) && !"production".equalsIgnoreCase(env)) {
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Ni-kshay login refused: portal env is '" + portalEnv
							+ "'. Only uat (https://beta.nikshay.in) and production (https://www.nikshay.in) are allowed");
		}
		logger.info("Ni-kshay login for {} via Java HTTP ({}, {})", username, env, expectedOrigin);
		NikshayDirectLogin.LoggedIn loggedIn = new NikshayDirectLogin(env).login(username, password);
		if (loggedIn.getOrigin() == null || !loggedIn.getOrigin().startsWith(expectedOrigin)) {
			throw new NikshayLoginException(NikshayFailureReason.LOGIN_REJECTED,
					"Ni-kshay login refused: session origin was not " + expectedOrigin);
		}
		HttpClient client = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT)
				.followRedirects(HttpClient.Redirect.NORMAL).build();
		logger.info("Ni-kshay login succeeded for {} at {}", username, loggedIn.getOrigin());
		return new NikshaySession(client, loggedIn.getOrigin(), username, loggedIn.getCookieHeader());
	}
}
