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
package com.iemr.admin.repository.rolemaster;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.rolemaster.RoleScreenMapping;

import jakarta.transaction.Transactional;

@Repository
public interface RoleScreenMappingRepo extends CrudRepository<RoleScreenMapping, Integer>{
	
	@Query("SELECT distinct u.roleID,rm.roleName,rm.roleDesc,rm.deleted,"
			+ " sm.screenName,"
			+ " u.sRSMappingID"
			+ " FROM RoleScreenMapping u "
			+ "INNER JOIN u.m_role rm "
			+ "INNER JOIN u.m_Screen sm "
			+ " where u.providerServiceMapID = :pssmID")
	ArrayList<Object[]> getAllRoleByMapId(@Param("pssmID") int pssmID);
	
	@Query("SELECT distinct u.roleID as roleID,rm.roleName as roleName,rm.roleDesc,rm.deleted,"
			+ " sm.screenName,"
			+ " u.sRSMappingID"
			+ " FROM RoleScreenMapping u "
			+ "INNER JOIN u.m_role rm "
			+ "INNER JOIN u.m_Screen sm "
			+ " where u.providerServiceMapID = :pssmID and sm.deleted=false")
	ArrayList<Object[]> getAllRoleBypsMapId(@Param("pssmID") int pssmID);
	
	
	
	@Transactional
	@Modifying
	@Query(" UPDATE  RoleScreenMapping u SET u.screenID=:screenID where u.sRSMappingID =:sRSMappingID")
	int updatescreenId(@Param("sRSMappingID")Integer sRSMappingID,@Param("screenID")Integer screenID);

}
