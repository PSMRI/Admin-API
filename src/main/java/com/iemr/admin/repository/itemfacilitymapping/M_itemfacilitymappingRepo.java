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
package com.iemr.admin.repository.itemfacilitymapping;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.itemfacilitymapping.M_itemfacilitymapping;

import jakarta.transaction.Transactional;

@Repository
public interface M_itemfacilitymappingRepo extends CrudRepository<M_itemfacilitymapping, Integer>{
	
	 @Query("SELECT distinct mi.itemID,"
             + " imfm.itemName as itemName, "
             + " imfm.discontinued as discontinued,"
             + " imfm.itemCategoryID as itemCategoryID "
             + " FROM M_itemfacilitymapping mi "
             + " JOIN mi.itemMasterforFacilityMapping imfm  "
             + " WHERE mi.providerServiceMapID =:providerServiceMapID AND mi.facilityID=:facilityID AND mi.deleted=0 AND imfm.deleted=0 ORDER BY imfm.itemName ")
	ArrayList<Object[]> getItemforSubstore(@Param("providerServiceMapID")Integer providerServiceMapID,@Param("facilityID") Integer facilityID);
	
	@Query("SELECT distinct mi.itemID,"
			  + " imfm.itemName as itemName "
			  + " FROM M_itemfacilitymapping mi "
			  + " JOIN mi.itemMasterforFacilityMapping imfm  "
			  + " WHERE mi.facilityID=:facilityID AND mi.deleted=0 AND imfm.deleted=0 ORDER BY imfm.itemName ")
	ArrayList<Object[]> getItemforStore(@Param("facilityID") Integer facilityID);
	
	@Transactional
	@Modifying
	@Query("UPDATE M_itemfacilitymapping c SET c.deleted = :deleted WHERE c.itemFacilityMapID = :mapID")
	Integer updateDeleteMap(@Param("mapID") Integer mapID,@Param("deleted") Boolean deleted);
	
	M_itemfacilitymapping findByItemFacilityMapID(Integer itemFacilityMapID);
	
	
}
