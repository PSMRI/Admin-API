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
package com.iemr.admin.data.nikshay;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * A device the bulk create could not save, with the values as submitted (never
 * the password) so the row can be corrected. rowIndex is its position in the
 * request's devices list.
 */
@Data
public class NikshayFailedDeviceDTO {

	public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
	public static final String DUPLICATE_IN_REQUEST = "DUPLICATE_IN_REQUEST";
	public static final String ALREADY_EXISTS = "ALREADY_EXISTS";
	public static final String DB_ERROR = "DB_ERROR";

	private int rowIndex;
	private String deviceSerialNumber;
	private String deviceName;
	private String phiUserId;
	private String reason;
	private List<NikshayDeviceFieldErrorDTO> errors = new ArrayList<>();
}
