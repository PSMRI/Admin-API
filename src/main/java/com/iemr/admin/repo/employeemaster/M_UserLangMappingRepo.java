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
package com.iemr.admin.repo.employeemaster;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.M_UserLangMapping;


@Repository
public interface M_UserLangMappingRepo extends CrudRepository<M_UserLangMapping, Integer>{
	@Query("SELECT u FROM M_UserLangMapping u WHERE u.userID=:userID AND u.languageID=:languageID AND deleted=0")
	M_UserLangMapping ulangmapedit(@Param ("userID") Integer userID,@Param ("languageID") Integer languageID);

	
	
	
	
	@Query("SELECT distinct ulm.userLangID,ulm.userID,ulm.languageID,ulm.weightage,"
			+ " ml.languageName as languageName,"
			+ " concat(COALESCE(us.firstName, ''), ' ', COALESCE(us.middleName, ''), ' ', COALESCE(us.lastName, '')) as userName,"
			+ " ulm.canRead,"
			+ " ulm.canWrite,"
			+ " ulm.canSpeak,"
			+ " ulm.createdBy,"
			+ " ulm.deleted,"
			+ " ulm.weightage_Read,"
			+ " ulm.weightage_Write,"
			+ " ulm.weightage_Speak,"
			+ " us.deleted as userDeleted "
			+ " FROM M_UserLangMapping ulm "
			+ " JOIN ulm.m_user us"
     		+ " JOIN ulm.m_Language1 ml"
			+ " WHERE ulm.languageID=ml.languageID And ulm.userID=us.userID And ulm.serviceProviderID=:serviceProviderID"
			)
	ArrayList<Object[]> getMappedLanguge(@Param("serviceProviderID") Integer serviceProviderID);




	@Query("SELECT u FROM M_UserLangMapping u WHERE u.userID=:userID")
	ArrayList<M_UserLangMapping> getmappedlanguageData(@Param("userID") Integer userID);
	
	M_UserLangMapping findByUserLangID(Integer userLangID);

}
