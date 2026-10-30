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

/**
 * A Ni-kshay login that didn't succeed. The reason is LOGIN_REJECTED when the
 * portal refused the credentials (fix the device login, then reprocess) or
 * NIKSHAY_UNAVAILABLE when the portal couldn't be reached or answered wrongly
 * (retry later), matching the history log's reasons.
 */
public class NikshayLoginException extends Exception {

	private static final long serialVersionUID = 1L;

	private final NikshayFailureReason reason;

	public NikshayLoginException(NikshayFailureReason reason, String message) {
		super(message);
		this.reason = reason;
	}

	public NikshayLoginException(NikshayFailureReason reason, String message, Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	public NikshayFailureReason getReason() {
		return reason;
	}
}
