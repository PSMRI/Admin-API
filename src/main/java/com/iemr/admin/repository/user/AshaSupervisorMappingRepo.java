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
package com.iemr.admin.repository.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.AshaSupervisorMapping;

@Repository
public interface AshaSupervisorMappingRepo extends CrudRepository<AshaSupervisorMapping, Long> {

	ArrayList<AshaSupervisorMapping> findBySupervisorUserIDAndDeletedFalse(Integer supervisorUserID);

	ArrayList<AshaSupervisorMapping> findByFacilityIDAndDeletedFalse(Integer facilityID);

	ArrayList<AshaSupervisorMapping> findBySupervisorUserIDAndFacilityIDAndDeletedFalse(Integer supervisorUserID, Integer facilityID);

	ArrayList<AshaSupervisorMapping> findBySupervisorUserIDAndFacilityIDInAndDeletedFalse(Integer supervisorUserID, List<Integer> facilityIDs);

	AshaSupervisorMapping findBySupervisorUserIDAndAshaUserIDAndFacilityIDAndDeletedTrue(Integer supervisorUserID, Integer ashaUserID, Integer facilityID);

	ArrayList<AshaSupervisorMapping> findByAshaUserIDAndDeletedFalse(Integer ashaUserID);

	ArrayList<AshaSupervisorMapping> findByAshaUserIDAndFacilityIDAndDeletedFalse(Integer ashaUserID, Integer facilityID);

	// Fix 5: find active row for this ASHA at this facility under a DIFFERENT supervisor
	ArrayList<AshaSupervisorMapping> findByAshaUserIDAndFacilityIDAndDeletedFalseAndSupervisorUserIDNot(
			Integer ashaUserID, Integer facilityID, Integer supervisorUserID);

	// Fix 6: find existing active row for exact (supervisor, ASHA, facility) — for idempotent save
	AshaSupervisorMapping findBySupervisorUserIDAndAshaUserIDAndFacilityIDAndDeletedFalse(
			Integer supervisorUserID, Integer ashaUserID, Integer facilityID);

	/**
	 * Get active supervisor mappings at a facility, excluding mappings where
	 * the supervisor user has been soft-deleted in m_User.
	 * Prevents deleted supervisors from blocking ASHA reassignment.
	 */
	@Query(value = "SELECT asm.*, "
			+ "(SELECT u.FirstName FROM m_User u WHERE u.UserID = asm.ashaUserID) AS ashaFirstName, "
			+ "(SELECT u.LastName FROM m_User u WHERE u.UserID = asm.ashaUserID) AS ashaLastName "
			+ "FROM asha_supervisor_mapping asm "
			+ "JOIN m_User su ON su.UserID = asm.supervisorUserID "
			+ "WHERE asm.facilityID = :facilityID "
			+ "AND asm.deleted = false "
			+ "AND su.Deleted = false", nativeQuery = true)
	ArrayList<AshaSupervisorMapping> findActiveMappingsByFacilityID(@Param("facilityID") Integer facilityID);
}
