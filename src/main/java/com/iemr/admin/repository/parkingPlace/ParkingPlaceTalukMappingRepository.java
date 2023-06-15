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
package com.iemr.admin.repository.parkingPlace;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;

@Repository
@RestResource(exported = false)
public interface ParkingPlaceTalukMappingRepository extends CrudRepository<ParkingplaceTalukMapping, Integer> {

	@Query("SELECT u FROM ParkingplaceTalukMapping u left join u.districtBlock left join u.parkingplace where u.providerServiceMapID=:pid ")
	List<ParkingplaceTalukMapping> findByProviderServiceMapID(@Param("pid")Integer id);

	@Query("SELECT u FROM ParkingplaceTalukMapping u left join u.districtBlock left join u.parkingplace where u.parkingPlaceID=:pid ")
	List<ParkingplaceTalukMapping> findByParkingPlaceID(@Param("pid")Integer parkingPlaceID);

	@Query("SELECT u FROM ParkingplaceTalukMapping u left join u.districtBlock left join u.parkingplace where u.parkingPlaceID=:pid and u.districtID=:did order by u.districtBlock.blockName")
	List<ParkingplaceTalukMapping> findByParkingPlaceIDAndDistrictIDOrderByM_DistrictDistrictNameAsc(
			@Param("pid")Integer parkingPlaceID, @Param("did")Integer districtID);

	@Query("SELECT u.districtBlockID FROM ParkingplaceTalukMapping u where  u.districtID=:did and u.providerServiceMapID=:sid and u.deleted=false")
	List<Integer> finbyDistrictID(@Param("did") Integer districtID,@Param("sid") Integer pID);

	
}
