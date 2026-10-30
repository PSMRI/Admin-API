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
package com.iemr.admin.service.nikshayupload;

import java.util.Map;

import org.springframework.stereotype.Component;


/**
 * TrueNat (MTB) payload for Ni-kshay's AddDiagnosticsTest: type "Truenat (MTB)".
 * Invalid / error runs are closed and retested by FLW-API, so only detected / not
 * detected results reach the upload.
 */
@Component
public class NaatTestPayloadBuilder extends DiagnosticsTestPayloadBuilder {

	// Ni-kshay final interpretation values (programme's mapping table)
	static final String DETECTED = "M.Tb. detected";
	static final String NOT_DETECTED = "M.Tb. not detected";

	private static final Map<String, String> INTERPRETATIONS = Map.of(
			"tb positive", DETECTED,
			"tb negative", NOT_DETECTED);

	@Override
	public NikshayTestType getTestType() {
		return NikshayTestType.NAAT;
	}

	@Override
	protected String nikshayTestType() {
		return "Truenat (MTB)";
	}

	@Override
	protected String labSerialPrefix() {
		return "SAMPLE-TRUENAT(MTB)-";
	}

	// TrueNat results go to Ni-kshay with reason "Diagnosis of TB" (tested on beta 2026-10-30)
	@Override
	protected String reasonForTesting() {
		return "Diagnosis of TB";
	}

	@Override
	protected Map<String, String> interpretations() {
		return INTERPRETATIONS;
	}
}
