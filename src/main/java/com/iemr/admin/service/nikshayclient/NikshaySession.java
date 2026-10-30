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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

/**
 * A logged-in Ni-kshay portal session for one PHI login. It holds the login cookies
 * (from the browser login in NikshayAuthClient) and sends them on every call made
 * through get()/postJson()/putJson(). One session is meant for one batch (e.g. one village's
 * results) and then dropped. The cookies are a live login: never log or return them.
 */
public class NikshaySession {

	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);

	private final HttpClient httpClient;
	private final String portalUrl;
	private final String phiUserId;
	// "name=value; name2=value2", as a browser sends it
	private final String cookieHeader;
	private final Instant loggedInAt = Instant.now();

	NikshaySession(HttpClient httpClient, String portalUrl, String phiUserId, String cookieHeader) {
		this.httpClient = httpClient;
		this.portalUrl = portalUrl;
		this.phiUserId = phiUserId;
		this.cookieHeader = cookieHeader;
	}

	/** GET a portal path such as "/Api/Patients/GetPossibleDuplicates?…". */
	public HttpResponse<String> get(String path) throws IOException, InterruptedException {
		return httpClient.send(request(path).GET().build(), HttpResponse.BodyHandlers.ofString());
	}

	/** POST a JSON body to a portal path such as "/api/patients/AddIndiaTB". */
	public HttpResponse<String> postJson(String path, String json) throws IOException, InterruptedException {
		return httpClient.send(request(path).header("Content-Type", "application/json; charset=UTF-8")
				.POST(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
	}

	/** PUT a JSON body to a portal path such as "/Api/Patients/ContactTracing". */
	public HttpResponse<String> putJson(String path, String json) throws IOException, InterruptedException {
		return httpClient.send(request(path).header("Content-Type", "application/json; charset=UTF-8")
				.PUT(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
	}

	/**
	 * POST a JSON body to a full URL on another Ni-kshay host (the registry service for
	 * transfers), with the portal's cookies, Origin and Referer, as the portal page does.
	 */
	public HttpResponse<String> postJsonToUrl(String url, String json) throws IOException, InterruptedException {
		return httpClient.send(requestTo(url).header("Content-Type", "application/json; charset=UTF-8")
				.POST(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
	}

	public String getPortalUrl() {
		return portalUrl;
	}

	public String getPhiUserId() {
		return phiUserId;
	}

	public Instant getLoggedInAt() {
		return loggedInAt;
	}

	// The portal's own pages call these APIs as XHRs from the portal origin.
	private HttpRequest.Builder request(String path) {
		return requestTo(portalUrl + path);
	}

	private HttpRequest.Builder requestTo(String url) {
		return HttpRequest.newBuilder(URI.create(url)).timeout(REQUEST_TIMEOUT)
				.header("Accept", "*/*")
				.header("Origin", portalUrl)
				.header("Referer", portalUrl + "/")
				.header("X-Requested-With", "XMLHttpRequest")
				.header("User-Agent", NikshayAuthClient.USER_AGENT)
				.header("Cookie", cookieHeader);
	}
}
