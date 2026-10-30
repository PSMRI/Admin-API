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

import com.iemr.admin.service.nikshayjoblog.NikshayPhase;

/**
 * Which test's results are uploaded to Ni-kshay: the phase its history log entries are
 * recorded under, the device whose login uploads it and the tb_diagnostic_order.order_type
 * of its results. NAAT and RIF are both uploaded by the NAAT_UPLOAD phase.
 */
public enum NikshayTestType {
	NAAT(NikshayPhase.NAAT, "NAAT", "MTB"),
	XRAY(NikshayPhase.XRAY, "XRAY", "XRAY_CHEST"),
	// TrueNat Rif resistance, run on the NAAT device
	RIF(NikshayPhase.NAAT_RIF, "NAAT", "MDR_RIF");

	private final NikshayPhase phase;
	private final String deviceName;
	private final String orderType;

	NikshayTestType(NikshayPhase phase, String deviceName, String orderType) {
		this.phase = phase;
		this.deviceName = deviceName;
		this.orderType = orderType;
	}

	public NikshayPhase getPhase() {
		return phase;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getOrderType() {
		return orderType;
	}
}
