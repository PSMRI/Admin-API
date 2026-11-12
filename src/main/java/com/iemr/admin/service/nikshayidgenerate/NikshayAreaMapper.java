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
package com.iemr.admin.service.nikshayidgenerate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AMRIT household area (i_householddetails.residentialArea) to a value Ni-kshay's enrolment
 * form accepts. A blank value, the text "null" (how the app saves a missing area), or any
 * text that is not one of those choices and has no mapping, is sent as Unknown so
 * enrolment is not rejected with "Invalid Area".
 */
public final class NikshayAreaMapper {

	static final String UNKNOWN = "Unknown";

	// Exact strings from beta.nikshay.in enrolment (the area options of the add case form).
	private static final String[] NIKSHAY = {
			"Tribal-Non Particularly Vulnerable Tribal Groups",
			"Tribal-Particularly Vulnerable Tribal Groups",
			"Rural",
			"Urban",
			"Urban Slum",
			UNKNOWN };

	private static final Map<String, String> NIKSHAY_BY_KEY = new LinkedHashMap<>();
	private static final Map<String, String> AMRIT_TO_NIKSHAY = new LinkedHashMap<>();

	static {
		for (String value : NIKSHAY) {
			NIKSHAY_BY_KEY.put(key(value), value);
		}
		// AMRIT's area choices; Rural, Urban, Urban Slum and Unknown are the same as Ni-kshay's.
		put("Tribal NPVTG", "Tribal-Non Particularly Vulnerable Tribal Groups");
		put("Tribal-PVTG", "Tribal-Particularly Vulnerable Tribal Groups");
		put("null", UNKNOWN);
	}

	private NikshayAreaMapper() {
	}

	/** A Ni-kshay area. Never null, and never a value outside Ni-kshay's list. */
	public static String toNikshay(String amritArea) {
		String normalized = key(amritArea);
		if (normalized.isEmpty()) {
			return UNKNOWN;
		}
		String alreadyNikshay = NIKSHAY_BY_KEY.get(normalized);
		if (alreadyNikshay != null) {
			return alreadyNikshay;
		}
		String mapped = AMRIT_TO_NIKSHAY.get(normalized);
		return mapped == null ? UNKNOWN : mapped;
	}

	private static void put(String amrit, String nikshay) {
		AMRIT_TO_NIKSHAY.put(key(amrit), nikshay);
	}

	private static String key(String value) {
		if (value == null) {
			return "";
		}
		return value.trim().toLowerCase().replaceAll("[^a-z0-9]+", " ").replaceAll("\\s+", " ").trim();
	}
}
