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
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.rolemaster.M_Role;
import com.iemr.admin.data.rolemaster.M_Role104;

@Repository
@RestResource(exported = false)
public interface M_RoleRepo extends CrudRepository<M_Role, Integer> {
	

	/*@Query("SELECT distinct u.roleID,u.roleName,u.roleDesc,u.deleted,"
			+ "ms.screenName,"
			+ "rsm.sRSMappingID"
			+ " FROM M_Role u "
			+ "INNER JOIN u.roleScreenMapping rsm "
			+ "INNER JOIN rsm.m_Screen ms "
			+ " where u.providerServiceMapID = :pssmID")
	ArrayList<Object[]> getAllRoleByMapId(@Param("pssmID") int pssmID);
	*/
	
	@Query("SELECT u FROM M_Role u where u.roleID = :roleID")
	M_Role getRoleByRoleId(@Param("roleID") Integer roleID);
	
	
	@Query("SELECT u FROM M_Role u where u.providerServiceMapID = :pssmID AND deleted=0 order by u.roleName")
	ArrayList<M_Role> getAllRoleByMapId1(@Param("pssmID") int pssmID);


	ArrayList<M_Role> findByDeletedAndProviderServiceMapID(boolean b, Integer providerServiceMapID);

}
