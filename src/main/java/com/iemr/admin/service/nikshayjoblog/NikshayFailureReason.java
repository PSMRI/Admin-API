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
package com.iemr.admin.service.nikshayjoblog;

/** Failure reasons shown on the Failed Beneficiaries view (BRD section 10.2). */
public enum NikshayFailureReason {
	// Ni-kshay down, timeout or no internet
	NIKSHAY_UNAVAILABLE,
	// Beneficiary's village is not a Ni-kshay village, or not in any device's village mapping
	VILLAGE_MISSING,
	// A mandatory Ni-kshay field is blank or fails a rule (V1–V16)
	MANDATORY_FIELD_MISSING,
	// NAAT or X-ray service not enabled in the device PHI profile
	SERVICE_NOT_ENABLED,
	// Wrong or expired password for the wrapper PHI login
	LOGIN_REJECTED,
	// No usable device row or login for the device or activity
	DEVICE_CONFIGURATION_MISSING,
	// Ni-kshay rejected a value and returned a message
	NIKSHAY_VALIDATION_ERROR,
	// No active designated PHI for the village (held)
	DESIGNATED_PHI_NOT_MAPPED,
	// Ni-kshay rejected the transfer
	TRANSFER_REJECTED,
	// More than one Ni-kshay record matches
	MULTIPLE_DUPLICATE_MATCHES,
	// The test result is an error / invalid run (e.g. NAAT "Error-2"): not uploaded, waits for a retest
	TEST_RESULT_INVALID
}
