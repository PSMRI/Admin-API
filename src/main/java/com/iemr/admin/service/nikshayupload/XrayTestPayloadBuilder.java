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
 * Chest X-ray payload for Ni-kshay's AddDiagnosticsTest: type "Chest X Ray", sample
 * absent / result present. "AI Invalid Result" has no Ni-kshay option; FLW-API closes
 * those orders, so they never reach the upload.
 */
@Component
public class XrayTestPayloadBuilder extends DiagnosticsTestPayloadBuilder {

	// Ni-kshay final interpretation values (programme's mapping table)
	static final String NORMAL = "Normal";
	static final String SUGGESTIVE = "Abnormal - Suggestive of TB";
	static final String NOT_SUGGESTIVE = "Abnormal - Not Suggestive of TB";

	// Includes the misspellings seen in tb_diagnostic_result
	private static final Map<String, String> INTERPRETATIONS = Map.of(
			"normal", NORMAL,
			"tb presumptive", SUGGESTIVE,
			"tb presumtive", SUGGESTIVE,
			"abnormal but not tb presumptive", NOT_SUGGESTIVE,
			"abnormal but tb not presumptive", NOT_SUGGESTIVE);

	@Override
	public NikshayTestType getTestType() {
		return NikshayTestType.XRAY;
	}

	@Override
	protected String nikshayTestType() {
		return "Chest X Ray";
	}

	@Override
	protected Map<String, String> interpretations() {
		return INTERPRETATIONS;
	}

	@Override
	protected String labSerialPrefix() {
		return "SAMPLE-XRAY-";
	}
}
