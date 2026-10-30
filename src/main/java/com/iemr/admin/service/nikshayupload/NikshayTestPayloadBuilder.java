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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.data.nikshayupload.NikshayTestResult;
import com.iemr.admin.service.nikshayclient.NikshayApiException;

/**
 * Builds the Ni-kshay "add diagnostics test" payload for one test type. One
 * implementation per test type (X-ray, NAAT); NikshayTestResultPusher uses it
 * without knowing the type.
 */
public interface NikshayTestPayloadBuilder {

	/** Test type, as the history log records it. */
	NikshayTestType getTestType();

	/**
	 * The payload for one result, tested on the given device's facility. Throws
	 * NikshayApiException when a value Ni-kshay needs is missing or can't be mapped.
	 */
	ObjectNode build(NikshayTestResult result, NikshayDeviceCredentialDTO device) throws NikshayApiException;
}
