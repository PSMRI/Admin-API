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

import com.iemr.admin.model.nikshay.NikshayDeviceRequest;

import lombok.Data;
import lombok.ToString;

/**
 * Bulk create working row: a request row that passed validation and the entity
 * that will be inserted for it (the entity carries the encrypted password and,
 * after the insert, the generated id).
 */
@Data
@ToString(exclude = { "source", "entity" })
public class NikshayPendingDeviceDTO {

	private final int rowIndex;
	private final NikshayDeviceRequest source;
	private final String serialNumber;
	private final NikshayDeviceName deviceName;
	private NikshayDeviceConfiguration entity;
}
