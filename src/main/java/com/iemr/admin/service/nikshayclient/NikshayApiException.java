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

import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;

/** A Ni-kshay API call that failed (test upload, village lookup, duplicate check), with the history log reason. */
public class NikshayApiException extends Exception {

	private static final long serialVersionUID = 1L;

	private final NikshayFailureReason reason;

	// What was sent to Ni-kshay and its full answer, when the call got that far (saved in the
	// job history log to debug rejections); null otherwise
	private String requestPayload;
	private String responseBody;

	public NikshayApiException(NikshayFailureReason reason, String message) {
		super(message);
		this.reason = reason;
	}

	public NikshayFailureReason getReason() {
		return reason;
	}

	/** Attaches the request sent and Ni-kshay's answer (either may be null); returns this exception. */
	public NikshayApiException withExchange(String requestPayload, String responseBody) {
		this.requestPayload = requestPayload;
		this.responseBody = responseBody;
		return this;
	}

	public String getRequestPayload() {
		return requestPayload;
	}

	public String getResponseBody() {
		return responseBody;
	}
}
