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
package com.iemr.admin.data.nikshaytransferout;

import lombok.AllArgsConstructor;
import lombok.Data;

/** What decides whether a contact is transferred out: their test results and symptoms. */
@Data
@AllArgsConstructor
public class NikshayContactTestsDTO {
	private Long benRegId;
	// A completed TrueNat (MTB) result "TB Positive"
	private boolean naatPositive;
	// Their latest completed chest X-ray result, e.g. "Normal" or "TB Presumptive"; null if none
	private String latestXray;
	// Any symptom (cough, fever, weight loss, night sweats) in their latest TB screening
	private boolean symptomatic;
}
