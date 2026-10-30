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
package com.iemr.admin.repo.nikshayupload;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshay.NikshayDeviceName;
import com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO;
import com.iemr.admin.data.nikshay.NikshayDeviceVillageMapping;

@Repository
public interface NikshayDeviceCredentialRepo extends CrudRepository<NikshayDeviceVillageMapping, Integer> {

	// Active device logins of the given type (NAAT / XRAY) for the given villages — only
	// active mappings of active devices, the same rule findPhiByLocation uses.
	// The mapping's facility is the device's PHI, sent to Ni-kshay as the testing facility, with
	// its district and state (through its TU) as the testing facility's district and state.
	@Query(" SELECT new com.iemr.admin.data.nikshayupload.NikshayDeviceCredentialDTO(m.villageId, d.id, d.deviceSerialNumber, "
			+ " d.deviceName, d.phiUserId, d.password, f.nikshayCode, f.facilityName, nd.nikshayCode, nd.districtName, "
			+ " ns.nikshayCode, ns.stateName) "
			+ " FROM NikshayDeviceVillageMapping m "
			+ " JOIN NikshayDeviceConfiguration d ON d.id = m.deviceDetailId "
			+ " LEFT JOIN NikshayFacility f ON f.nikshayFacilityID = m.facilityId AND f.deleted = false "
			+ " LEFT JOIN NikshayTU nt ON nt.nikshayTUID = f.nikshayTUID AND nt.deleted = false "
			+ " LEFT JOIN NikshayDistrict nd ON nd.nikshayDistrictID = nt.nikshayDistrictID AND nd.deleted = false "
			+ " LEFT JOIN NikshayState ns ON ns.nikshayStateID = nd.nikshayStateID AND ns.deleted = false "
			+ " WHERE m.isActive = true AND d.isActive = true "
			+ " AND d.deviceName = :deviceName AND m.villageId IN :villageIds ")
	List<NikshayDeviceCredentialDTO> findActiveCredentials(@Param("villageIds") List<Integer> villageIds,
			@Param("deviceName") NikshayDeviceName deviceName);
}
