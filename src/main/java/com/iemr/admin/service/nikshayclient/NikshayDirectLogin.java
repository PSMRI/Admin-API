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

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/**
 * Logs in by posting the Ni-kshay SSO form with ordinary HTTP. "production"
 * uses https://www.nikshay.in. Blank or "uat" uses https://beta.nikshay.in.
 */
public class NikshayDirectLogin {
	private static final Logger logger = LoggerFactory.getLogger(NikshayDirectLogin.class);

	private static final String USER_AGENT = NikshayAuthClient.USER_AGENT;
	private static final Target UAT = new Target(
			"https://sso.beta.nikshay.in/v1/sso/login?returnUrl=https://beta.nikshay.in/Home&clientId=29",
			"https://sso.beta.nikshay.in", "https://beta.nikshay.in");
	private static final Target PRODUCTION = new Target(
			"https://sso.nikshay.in/v1/sso/login?returnUrl=https://www.nikshay.in/Home&clientId=29",
			"https://sso.nikshay.in", "https://www.nikshay.in");

	private static final Pattern CSRF = Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"");
	private static final Pattern CLIENT_ID = Pattern.compile("name=\"clientId\"\\s+value=([^\\s>]+)");
	private static final Pattern RETURN_URL = Pattern.compile("name=\"returnUrl\"\\s+value=([^\\s>]+)");
	private static final Pattern SET_ERROR = Pattern.compile("id=\"setError\"[^>]*>([^<]*)");
	private static final Pattern HREF = Pattern.compile("(?is)<a[^>]+href=\"([^\"]+)\"");

	private final Target target;
	private final CookieManager cookies;
	private final HttpClient http;

	public NikshayDirectLogin(String portalEnv) throws NikshayLoginException {
		if ("production".equalsIgnoreCase(portalEnv)) {
			target = PRODUCTION;
		} else if (portalEnv == null || portalEnv.isBlank() || "uat".equalsIgnoreCase(portalEnv)) {
			target = UAT;
		} else {
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Unknown Ni-kshay portal env '" + portalEnv + "'");
		}
		cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).cookieHandler(cookies)
				.followRedirects(HttpClient.Redirect.NORMAL).build();
	}

	/**
	 * Logs in and returns the portal origin plus the Cookie header for that host.
	 * The password, the hash and the cookie values are not logged.
	 */
	public LoggedIn login(String username, String password) throws NikshayLoginException {
		try {
			return submit(username, password);
		} catch (IOException e) {
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE,
					"Could not reach Ni-kshay: " + e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, "Ni-kshay login was interrupted",
					e);
		}
	}

	private LoggedIn submit(String username, String password)
			throws IOException, InterruptedException, NikshayLoginException {
		HttpResponse<String> page = send(request(target.loginUrl).GET());
		requireOk(page, "login page");
		String html = page.body() == null ? "" : page.body();
		String clientId = headerOrForm(page, "x-sso-client-id", CLIENT_ID, html, "29");
		String csrf = group(CSRF, html);
		String returnUrl = stripQuotes(group(RETURN_URL, html));
		if (csrf == null || returnUrl == null) {
			throw unavailable("Ni-kshay login page did not contain the form fields");
		}

		String captchaText = null;
		if (html.contains("name=\"captcha\"")) {
			HttpResponse<String> captcha = send(request(target.ssoOrigin + "/v1/sso/captcha?" + System.currentTimeMillis())
					.header("Referer", target.loginUrl).GET());
			requireOk(captcha, "captcha");
			captchaText = captcha.body() == null ? ""
					: captcha.body().replaceAll("<[^>]*>", "").replaceAll("[^A-Za-z0-9]", "");
			if (captchaText.length() < 4 || captchaText.length() > 12) {
				throw unavailable("Ni-kshay captcha was not a short code");
			}
		}

		String authToken = authToken(clientId);
		String salt = ssoData(target.ssoOrigin + "/v1/user/saltKey?username=" + url(username), clientId, authToken,
				"salt");
		String sessionKey = ssoData(target.ssoOrigin + "/v1/ssoserver/session-key", clientId, authToken(clientId),
				"session key");
		String hashed = sha256Hex(sessionKey + sha256Hex(salt + password));

		Map<String, String> form = new LinkedHashMap<>();
		form.put("userName", username);
		form.put("password", hashed);
		if (captchaText != null) {
			form.put("captcha", captchaText);
		}
		form.put("returnUrl", returnUrl);
		form.put("refererUrl", "");
		form.put("isEncryptedData", "true");
		form.put("clientId", clientId);
		form.put("isExternalLogin", "false");
		form.put("_csrf", csrf);

		HttpResponse<String> submitted = send(request(target.ssoOrigin + "/v1/sso/loginSubmit")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Origin", target.ssoOrigin)
				.header("Referer", target.loginUrl)
				.POST(HttpRequest.BodyPublishers.ofString(formBody(form))));
		String body = submitted.body() == null ? "" : submitted.body();
		refuseIfRejected(submitted.uri(), body);
		if (body.toLowerCase().contains("already logged in")) {
			String href = group(HREF, body);
			if (href != null) {
				HttpResponse<String> followed = send(request(absolute(target.ssoOrigin, href)).GET());
				refuseIfRejected(followed.uri(), followed.body() == null ? "" : followed.body());
			}
		}

		HttpResponse<String> home = send(request(target.portalOrigin + "/Home").GET());
		String homeBody = home.body() == null ? "" : home.body();
		refuseIfRejected(home.uri(), homeBody);
		if (home.statusCode() >= 400 || home.uri().getHost() == null || home.uri().getHost().startsWith("sso.")
				|| homeBody.contains("name=\"password\"")) {
			throw rejected("Login failed: did not reach the Ni-kshay home page");
		}
		String header = cookieHeader(home.uri().getHost());
		if (header.isEmpty()) {
			throw unavailable("Ni-kshay login returned no session");
		}
		logger.info("Ni-kshay direct login opened {} for {}", target.portalOrigin, username);
		return new LoggedIn(target.portalOrigin, header);
	}

	private void refuseIfRejected(URI uri, String body) throws NikshayLoginException {
		String path = uri.getPath() == null ? "" : uri.getPath();
		if (path.endsWith("/resetPassword")) {
			throw rejected("Login failed: Ni-kshay requires a password change for this login. Log in to the portal "
					+ "by hand, set a new password, then update it in the device configuration.");
		}
		String error = text(SET_ERROR, body);
		if (error != null && !error.isBlank()) {
			throw rejected("Login failed: " + error.trim());
		}
	}

	private String authToken(String clientId) throws IOException, InterruptedException, NikshayLoginException {
		HttpResponse<String> response = send(request(target.ssoOrigin + "/v1/ssoserver/client")
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("X-Requested-With", "XMLHttpRequest")
				.header("X-SSO-Client-Id", clientId)
				.header("Referer", target.loginUrl)
				.GET());
		requireOk(response, "auth token");
		String token = jsonString(response.body(), "authToken");
		if (token == null || token.isBlank()) {
			throw unavailable("Ni-kshay did not issue an auth token");
		}
		return token;
	}

	private String ssoData(String url, String clientId, String authToken, String what)
			throws IOException, InterruptedException, NikshayLoginException {
		HttpResponse<String> response = send(request(url)
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("X-Requested-With", "XMLHttpRequest")
				.header("X-SSO-Client-Id", clientId)
				.header("X-SSO-Auth-Token", authToken)
				.header("Referer", target.loginUrl)
				.GET());
		requireOk(response, what);
		String body = response.body() == null ? "" : response.body();
		if (!body.contains("\"success\":true") && !body.contains("\"success\": true")) {
			throw unavailable("Ni-kshay did not return a " + what);
		}
		String data = jsonString(body, "data");
		if (data == null || data.isBlank()) {
			throw unavailable("Ni-kshay did not return a " + what);
		}
		return data;
	}

	private String cookieHeader(String host) {
		StringBuilder header = new StringBuilder();
		for (HttpCookie cookie : cookies.getCookieStore().getCookies()) {
			if (cookie.hasExpired() || cookie.getName() == null || cookie.getName().isBlank()
					|| !forHost(cookie, host)) {
				continue;
			}
			if (header.length() > 0) {
				header.append("; ");
			}
			header.append(cookie.getName()).append('=').append(cookie.getValue() == null ? "" : cookie.getValue());
		}
		return header.toString();
	}

	private static boolean forHost(HttpCookie cookie, String host) {
		String domain = cookie.getDomain();
		if (domain == null || domain.isBlank() || host == null) {
			return false;
		}
		if (domain.startsWith(".")) {
			domain = domain.substring(1);
		}
		String d = domain.toLowerCase();
		String h = host.toLowerCase();
		return h.equals(d) || h.endsWith("." + d);
	}

	private HttpResponse<String> send(HttpRequest.Builder builder) throws IOException, InterruptedException {
		return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
	}

	private static HttpRequest.Builder request(String url) {
		return HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30))
				.header("User-Agent", USER_AGENT)
				.header("sec-ch-ua", NikshayAuthClient.CHROME_UA)
				.header("sec-ch-ua-mobile", "?0")
				.header("sec-ch-ua-platform", NikshayAuthClient.CHROME_UA_PLATFORM)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.header("Accept-Language", "en-US,en;q=0.9");
	}

	private static void requireOk(HttpResponse<String> response, String step) throws NikshayLoginException {
		if (response.statusCode() >= 400) {
			throw unavailable(step + " answered HTTP " + response.statusCode());
		}
	}

	private static String headerOrForm(HttpResponse<String> response, String header, Pattern form, String html,
			String fallback) {
		String value = response.headers().firstValue(header).orElse(null);
		if (value == null || value.isBlank()) {
			value = stripQuotes(group(form, html));
		}
		return value == null || value.isBlank() ? fallback : value;
	}

	private static String group(Pattern pattern, String text) {
		if (text == null) {
			return null;
		}
		Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group(1).trim() : null;
	}

	private static String text(Pattern pattern, String html) {
		String value = group(pattern, html);
		return value == null ? null : value.replaceAll("\\s+", " ").trim();
	}

	private static String stripQuotes(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length() - 1);
		}
		return value;
	}

	private static String jsonString(String json, String field) {
		if (json == null) {
			return null;
		}
		Matcher matcher = Pattern.compile("\"" + field + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(json);
		return matcher.find() ? matcher.group(1) : null;
	}

	private static String sha256Hex(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder(digest.length * 2);
			for (byte b : digest) {
				hex.append(String.format("%02x", b));
			}
			return hex.toString();
		} catch (Exception e) {
			throw new IllegalStateException("Could not hash the Ni-kshay password");
		}
	}

	private static String formBody(Map<String, String> fields) {
		StringBuilder body = new StringBuilder();
		for (Map.Entry<String, String> field : fields.entrySet()) {
			if (body.length() > 0) {
				body.append('&');
			}
			body.append(url(field.getKey())).append('=').append(url(field.getValue()));
		}
		return body.toString();
	}

	private static String url(String value) {
		return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
	}

	private static String absolute(String origin, String href) {
		if (href.startsWith("http://") || href.startsWith("https://")) {
			return href;
		}
		if (href.startsWith("/")) {
			return origin + href;
		}
		return origin + "/" + href;
	}

	private static NikshayLoginException rejected(String message) {
		return new NikshayLoginException(NikshayFailureReason.LOGIN_REJECTED, message);
	}

	private static NikshayLoginException unavailable(String message) {
		return new NikshayLoginException(NikshayFailureReason.NIKSHAY_UNAVAILABLE, message);
	}

	/** Portal origin and the Cookie header for that host. Cookie values stay inside the session. */
	public static final class LoggedIn {
		private final String origin;
		private final String cookieHeader;

		LoggedIn(String origin, String cookieHeader) {
			this.origin = origin;
			this.cookieHeader = cookieHeader;
		}

		public String getOrigin() {
			return origin;
		}

		public String getCookieHeader() {
			return cookieHeader;
		}
	}

	private static final class Target {
		private final String loginUrl;
		private final String ssoOrigin;
		private final String portalOrigin;

		private Target(String loginUrl, String ssoOrigin, String portalOrigin) {
			this.loginUrl = loginUrl;
			this.ssoOrigin = ssoOrigin;
			this.portalOrigin = portalOrigin;
		}
	}
}
