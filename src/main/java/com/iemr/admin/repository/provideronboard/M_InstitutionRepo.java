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
package com.iemr.admin.repository.provideronboard;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.provideronboard.M_Institution;

@Repository
public interface M_InstitutionRepo extends CrudRepository<M_Institution, Integer> {

	@Query("SELECT u FROM M_Institution u where u.providerServiceMapID=:providerServiceMapID AND u.stateID=:stateID AND u.districtID=:districtID AND u.blockID=:blockID")
	ArrayList<M_Institution> getInstution(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("stateID") Integer stateID, @Param("districtID") Integer districtID,
			@Param("blockID") Integer blockID);

	@Query("SELECT u FROM M_Institution u where u.providerServiceMapID=:providerServiceMapID AND u.stateID=:stateID AND u.districtID=:districtID")
	ArrayList<M_Institution> getInstution(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("stateID") Integer stateID, @Param("districtID") Integer districtID);

	@Query("SELECT u FROM M_Institution u where u.institutionID=:institutionID")
	M_Institution geteditedData(@Param("institutionID") Integer institutionID);

	@Query("SELECT u FROM M_Institution u where u.providerServiceMapID=:providerServiceMapID AND u.stateID=:stateID AND u.districtID=:districtID AND u.blockID=:blockID")
	ArrayList<M_Institution> getInstutionByBlock(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("stateID") Integer stateID, @Param("districtID") Integer districtID,
			@Param("blockID") Integer blockID);

	@Query("SELECT u FROM M_Institution u where u.providerServiceMapID=:providerServiceMapID AND u.stateID=:stateID AND u.districtID=:districtID AND u.villageID=:villageID")
	ArrayList<M_Institution> getInstutionByVillage(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("stateID") Integer stateID, @Param("districtID") Integer districtID,
			@Param("villageID") Integer villageID);

	@Query("SELECT u FROM M_Institution u where u.providerServiceMapID=:providerServiceMapID AND u.stateID=:stateID "
			+ "AND u.districtID=:districtID AND u.blockID=:blockID AND u.villageID=:villageID")
	ArrayList<M_Institution> getInstutionByBlockAndVillage(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("stateID") Integer stateID, @Param("districtID") Integer districtID,
			@Param("blockID") Integer blockID, @Param("villageID") Integer villageID);
	/*
	 * Created by DU20091017
	 */
	@Query(value =" call db_iemr.PR_InstituteBulkUpload(:institutionDetails, :createdby, :userID, :providerID)", nativeQuery = true)
	ArrayList<Object[]> institutionByFile(@Param("institutionDetails") String institutionDetails,@Param("createdby") String createdby,
			@Param("userID") Integer userID,@Param("providerID") Integer providerID);
}
