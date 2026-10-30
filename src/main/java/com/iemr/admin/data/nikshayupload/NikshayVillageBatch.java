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
package com.iemr.admin.data.nikshayupload;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * One village's results of a single test type (NaatResultDTO or XrayResultDTO),
 * together with the device PHI login used to push them to Ni-kshay.
 */
@Data
public class NikshayVillageBatch {

	private Integer villageId;
	private String villageName;
	private NikshayDeviceCredentialDTO credential;
	private List<NikshayTestResult> results = new ArrayList<>();

	public NikshayVillageBatch(Integer villageId, String villageName, NikshayDeviceCredentialDTO credential) {
		this.villageId = villageId;
		this.villageName = villageName;
		this.credential = credential;
	}
}
