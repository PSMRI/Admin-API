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
package com.iemr.admin.repo.VanSpokeMappingRepo;


import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.VanSpokeMapping.m_VanSpokeMapping;

@Repository
@RestResource(exported = false)
public interface VanSpokeMappingRepo extends CrudRepository<m_VanSpokeMapping, Integer>{

//	@Query("SELECT vsm.VanspokeID,vsm.tm_StateID,vsm.tm_StateName,vsm.tm_ZoneID,vsm.tm_ZoneName,vsm.tm_HubID, "
//			+ " vsm.tm_HubName,vsm.tm_servicePointID,vsm.tm_servicepointname,vsm.tm_SpokeID,vsm.tm_spokeName,vsm.mmu_StateID, "
//			+ " vsm.mmu_StateName,vsm.tm_vanTypeID,vsm.tm_ProviderServiceMapID,vsm.mmu_ZoneID,vsm.mmu_ZoneName,vsm.mmu_parkingPlaceID, "
//			+ " vsm.mmu_parkingPlaceName,vsm.mmu_servicePointID,vsm.mmu_servicePointName,vsm.mmu_VanID,vsm.mmu_vantypeID,vsm.mmu_vanName, "
//			+ " vsm.mmu_ProviderServiceMapID,vsm.deleted from m_VanSpokeMapping vsm inner join m_parkingplace pp on   where vsm.mmu_parkingPlaceID=:parkingPlaceID AND vsm.tm_servicePointID=:servicePointID AND vsm.deleted is false")
//	public ArrayList<m_VanSpokeMapping> getVanSpokeMappingDetails(@Param("parkingPlaceID")Integer parkingPlaceID, @Param("servicePointID")Integer servicePointID);
	
	@Query("SELECT vsm from m_VanSpokeMapping vsm where vsm.mmu_parkingPlaceID=:parkingPlaceID AND vsm.mmu_servicePointID=:servicePointID AND vsm.mmu_vantypeID=:vanTypeID ")
	public ArrayList<m_VanSpokeMapping> getVanSpokeMappingDetails(@Param("parkingPlaceID")Integer parkingPlaceID, @Param("servicePointID")Integer servicePointID,@Param("vanTypeID")Integer vanTypeID);

}
