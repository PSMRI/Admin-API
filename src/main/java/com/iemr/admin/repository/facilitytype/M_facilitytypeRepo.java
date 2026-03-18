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
package com.iemr.admin.repository.facilitytype;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.facilitytype.M_facilitytype;

@Repository
public interface M_facilitytypeRepo extends CrudRepository<M_facilitytype, Integer> {

	@Query("SELECT u FROM M_facilitytype u WHERE u.providerServiceMapID=:providerServiceMapID order by u.facilityTypeName")
	ArrayList<M_facilitytype> getAllFicilityData(@Param("providerServiceMapID") Integer providerServiceMapID);

	List<M_facilitytype> findByFacilityTypeCodeAndProviderServiceMapID(String facilityTypeCode,
			Integer providerServiceMapID);

	M_facilitytype findByFacilityTypeID(Integer facilityTypeID);

	@Query("SELECT f FROM M_facilitytype f WHERE f.providerServiceMapID=:psm AND f.ruralUrban=:ruralUrban AND f.deleted=false ORDER BY f.facilityTypeName")
	List<M_facilitytype> findByProviderServiceMapIDAndRuralUrban(@Param("psm") Integer psm,
			@Param("ruralUrban") String ruralUrban);

	@Query("SELECT DISTINCT ft FROM M_facilitytype ft WHERE ft.facilityTypeID IN " +
			"(SELECT DISTINCT f.facilityTypeID FROM com.iemr.admin.data.store.M_Facility f " +
			"WHERE f.blockID = :blockID AND f.deleted = false) " +
			"AND ft.deleted = false ORDER BY ft.facilityTypeName")
	List<M_facilitytype> findFacilityTypesByBlock(@Param("blockID") Integer blockID);

	@Query("SELECT f FROM M_facilitytype f WHERE f.stateID = :stateID AND f.deleted = false ORDER BY f.facilityTypeName")
	List<M_facilitytype> findByStateID(@Param("stateID") Integer stateID);

	boolean existsByFacilityTypeNameAndStateIDAndDeletedFalse(String facilityTypeName, Integer stateID);

	// Fix 17: get the highest levelValue (= SC level) for a given service line
	@Query("SELECT MAX(f.levelValue) FROM M_facilitytype f WHERE f.providerServiceMapID = :psm AND f.deleted = false")
	Integer findMaxLevelValueByProviderServiceMapID(@Param("psm") Integer psm);

}
