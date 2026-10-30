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

import com.iemr.admin.data.nikshay.NikshayDeviceName;

import lombok.Data;
import lombok.ToString;

/** The device PHI login to use for a village. password is still encrypted. */
@Data
@ToString(exclude = "password")
public class NikshayDeviceCredentialDTO {

	private Integer villageId;
	private Integer deviceDetailId;
	private String deviceSerialNumber;
	private String deviceName;
	private String phiUserId;
	private String password;
	// Testing facility for the device's tests: its Ni-kshay code and name (m_nikshay_facility)
	private String facilityNikshayCode;
	private String facilityName;
	// The testing facility's district and state: Ni-kshay codes and names (m_nikshay_district / m_nikshay_state)
	private String districtNikshayCode;
	private String districtName;
	private String stateNikshayCode;
	private String stateName;

	// Used by the JPQL constructor expression in NikshayDeviceCredentialRepo — keep the argument order in sync.
	public NikshayDeviceCredentialDTO(Integer villageId, Integer deviceDetailId, String deviceSerialNumber, NikshayDeviceName deviceName,
			String phiUserId, String password, String facilityNikshayCode, String facilityName) {
		this.villageId = villageId;
		this.deviceDetailId = deviceDetailId;
		this.deviceSerialNumber = deviceSerialNumber;
		this.deviceName = deviceName == null ? null : deviceName.name();
		this.phiUserId = phiUserId;
		this.password = password;
		this.facilityNikshayCode = facilityNikshayCode;
		this.facilityName = facilityName;
	}

	// Used by the JPQL constructor expression in NikshayDeviceCredentialRepo — keep the argument order in sync.
	public NikshayDeviceCredentialDTO(Integer villageId, Integer deviceDetailId, String deviceSerialNumber, NikshayDeviceName deviceName,
			String phiUserId, String password, String facilityNikshayCode, String facilityName, String districtNikshayCode,
			String districtName, String stateNikshayCode, String stateName) {
		this(villageId, deviceDetailId, deviceSerialNumber, deviceName, phiUserId, password, facilityNikshayCode,
				facilityName);
		this.districtNikshayCode = districtNikshayCode;
		this.districtName = districtName;
		this.stateNikshayCode = stateNikshayCode;
		this.stateName = stateName;
	}
}
