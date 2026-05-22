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
package com.iemr.admin.service.employeemaster;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.employeemaster.AshaSupervisorMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;

public interface AshaSupervisorMappingService {

	ArrayList<AshaSupervisorMapping> saveAshaSupervisorMappings(List<AshaSupervisorMapping> mappings);

	ArrayList<AshaSupervisorMapping> getSupervisorMappingByFacility(Integer facilityID);

	ArrayList<M_UserServiceRoleMapping2> getAshasByFacility(List<Integer> facilityIDs);

	void deleteMappings(List<Long> ids, String modifiedBy);

	void deleteBySupervisorAndFacilities(Integer supervisorUserID, List<Integer> facilityIDs, String modifiedBy);

	void restoreMappings(List<Long> ids, String modifiedBy);

	// Cascade: soft-delete all asha_supervisor_mapping rows for a user (as supervisor or ASHA)
	void cascadeDeleteByUserID(Integer userID, String modifiedBy);

	// Cascade: soft-delete rows for a user at a specific old facility (role or facility change)
	void cascadeDeleteByUserIDAndFacilityID(Integer userID, Integer facilityID, String modifiedBy);

	// Cascade: soft-delete all asha_supervisor_mapping rows for a facility (facility soft-deleted)
	void cascadeDeleteByFacilityID(Integer facilityID, String modifiedBy);

	// Fix 7: atomic delete-old + save-new in one transaction
	ArrayList<AshaSupervisorMapping> updateAshaMappingsAtomically(
			Integer supervisorUserID, List<Integer> facilityIDs,
			List<AshaSupervisorMapping> newMappings, String modifiedBy);
}
