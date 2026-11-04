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
 * AMRIT occupation text to a value Ni-kshay's enrolment form accepts. A blank value,
 * or any text that is not one of those choices and has no mapping, is sent as Unknown
 * so enrolment is not rejected for occupation.
 */
public final class NikshayOccupationMapper {

	static final String UNKNOWN = "Unknown";

	// Exact strings from beta.nikshay.in enrolment (indiaAddCase.js), including Ni-kshay's spellings.
	private static final String[] NIKSHAY = {
			"Legislators and Senior officials",
			"Corporate Manager",
			"General Manager",
			"Physical, mathematical and engineering science professional",
			"Life sciences and health professional",
			"Teaching professional",
			"Other professional",
			"Office Clerk",
			"Customer Services Clerks",
			"Personal Protective Service Providers",
			"Models, Sales Persons and Demonstrators",
			"Market oriented skilled agriculutre and fishery workers",
			"Subsitence agriculture and fishery workers",
			"Extraction and building trade workers",
			"Metal, Machinery and related trades workers",
			"Precision, handicraft, printing and related trade workers",
			"Other Craft and related traders and workers",
			"Stationary Plant and related Operators",
			"Machine Operators and Assembler",
			"Drivers and Mobile Plant Operators",
			"Sales and Services elementry occupations",
			"Agriculture, fishery and related labour",
			"Laborers in mining, construction, manufecturing and transport",
			"New Workers seeking employment",
			"Workers reporting occupation unidentifiable or inadequately",
			"Workers no reporting any occupation",
			"House Wife",
			UNKNOWN };

	private static final Map<String, String> NIKSHAY_BY_KEY = new LinkedHashMap<>();
	private static final Map<String, String> AMRIT_TO_NIKSHAY = new LinkedHashMap<>();

	static {
		for (String value : NIKSHAY) {
			NIKSHAY_BY_KEY.put(key(value), value);
		}
		// m_beneficiaryoccupation, plus the free-text values stored on the beneficiary.
		put("cultivation agriculture", "Market oriented skilled agriculutre and fishery workers");
		put("cultivation", "Market oriented skilled agriculutre and fishery workers");
		put("cultivator", "Market oriented skilled agriculutre and fishery workers");
		put("farmer", "Market oriented skilled agriculutre and fishery workers");
		put("farming", "Market oriented skilled agriculutre and fishery workers");
		put("agriculture", "Market oriented skilled agriculutre and fishery workers");
		put("agricultural labour", "Agriculture, fishery and related labour");
		put("agricultural labor", "Agriculture, fishery and related labour");
		put("farm labour", "Agriculture, fishery and related labour");
		put("farm labor", "Agriculture, fishery and related labour");
		put("business", "Models, Sales Persons and Demonstrators");
		put("shopkeeper", "Models, Sales Persons and Demonstrators");
		put("trader", "Models, Sales Persons and Demonstrators");
		put("govt employee", "Other professional");
		put("government employee", "Other professional");
		put("government servant", "Other professional");
		put("private employee", "Other professional");
		put("teacher", "Teaching professional");
		put("teaching", "Teaching professional");
		put("doctor", "Life sciences and health professional");
		put("nurse", "Life sciences and health professional");
		put("health worker", "Life sciences and health professional");
		put("engineer", "Physical, mathematical and engineering science professional");
		put("clerk", "Office Clerk");
		put("driver", "Drivers and Mobile Plant Operators");
		put("driver and owner", "Drivers and Mobile Plant Operators");
		put("owner driver", "Drivers and Mobile Plant Operators");
		put("truck driver", "Drivers and Mobile Plant Operators");
		put("cleaner and helper", "Sales and Services elementry occupations");
		put("cleaner", "Sales and Services elementry occupations");
		put("helper", "Sales and Services elementry occupations");
		put("sweeper", "Sales and Services elementry occupations");
		put("labour", "Laborers in mining, construction, manufecturing and transport");
		put("labor", "Laborers in mining, construction, manufecturing and transport");
		put("daily wage", "Laborers in mining, construction, manufecturing and transport");
		put("mason", "Laborers in mining, construction, manufecturing and transport");
		put("construction", "Laborers in mining, construction, manufecturing and transport");
		put("housewife or homemaker", "House Wife");
		put("housewife", "House Wife");
		put("homemaker", "House Wife");
		put("house wife", "House Wife");
		put("unemployed", "New Workers seeking employment");
		put("job seeker", "New Workers seeking employment");
		// No Ni-kshay choice: student, other, not applicable, trucker-family label.
		put("student", UNKNOWN);
		put("other", UNKNOWN);
		put("not applicable", UNKNOWN);
		put("family member of trucker community", UNKNOWN);
		put("na", UNKNOWN);
		put("none", UNKNOWN);
		put("nil", UNKNOWN);
	}

	private NikshayOccupationMapper() {
	}

	/** A Ni-kshay occupation. Never null, and never a value outside Ni-kshay's list. */
	public static String toNikshay(String amritOccupation) {
		String normalized = key(amritOccupation);
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
