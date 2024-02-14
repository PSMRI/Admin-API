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
package com.iemr.admin.repo.locationmaster;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.locationmaster.Showofficedetails;

@Repository
public interface ShowofficedetailsRepo extends CrudRepository<Showofficedetails, Integer>{
	
	@Query(value = " SELECT * FROM Showofficedetails u where deleted=0",nativeQuery = true)
	ArrayList<Showofficedetails> getAlldata();
	
	
	@Query("SELECT u FROM Showofficedetails u where u.providerServiceMapID = :pssmID")
	ArrayList<Showofficedetails> getlocationByMapid(@Param("pssmID") int pssmID);


	@Query("SELECT u FROM Showofficedetails u where u.providerServiceMapID = :data ")
	ArrayList<Showofficedetails> getlocationByMapid1(@Param("data")Integer data);

	@Query("SELECT u FROM Showofficedetails u where u.providerServiceMapID = :i AND deleted=0 ")
	ArrayList<Showofficedetails> getOfficeName(@Param("i") Integer i);

	@Query("SELECT u FROM Showofficedetails u where u.providerServiceMapID = :pssmID AND u.districtID=:districtID")
	ArrayList<Showofficedetails> getlocationByMapid3(@Param("pssmID")Integer pssmID, @Param("districtID")Integer districtID);

	//ArrayList<Showofficedetails1> getlocationByMapid(int tempProSerStatMapID);
 
}
