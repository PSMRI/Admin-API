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

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * TrueNat Rif resistance payload for Ni-kshay's AddDiagnosticsTest: type "Truenat
 * (MTB-RIF)". Invalid / error runs are not uploaded (NikshayUploadService).
 */
@Component
public class RifTestPayloadBuilder extends DiagnosticsTestPayloadBuilder {

	// Ni-kshay final interpretation values (programme's mapping table)
	static final String RIF_NOT_DETECTED = "M.Tb. detected, Rif resistance not detected";
	static final String RIF_DETECTED = "M.Tb. detected, Rif resistance detected";
	static final String RIF_INDETERMINATE = "M.Tb. detected, Rif resistance indeterminate";

	// Stop TB app results, short and long forms
	private static final Map<String, String> INTERPRETATIONS = new HashMap<>();
	static {
		INTERPRETATIONS.put("non-dr tb", RIF_NOT_DETECTED);
		INTERPRETATIONS.put("non-dr tb (rif resistance not detected)", RIF_NOT_DETECTED);
		INTERPRETATIONS.put("rif resistance not detected", RIF_NOT_DETECTED);
		INTERPRETATIONS.put("dr tb", RIF_DETECTED);
		INTERPRETATIONS.put("dr tb (rif resistance detected)", RIF_DETECTED);
		INTERPRETATIONS.put("rif resistance detected", RIF_DETECTED);
		INTERPRETATIONS.put("indeterminate", RIF_INDETERMINATE);
	}

	@Override
	public NikshayTestType getTestType() {
		return NikshayTestType.RIF;
	}

	@Override
	protected String nikshayTestType() {
		return "Truenat (MTB-RIF)";
	}

	@Override
	protected String labSerialPrefix() {
		return "SAMPLE-TRUENAT(MTB-RIF)-";
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
