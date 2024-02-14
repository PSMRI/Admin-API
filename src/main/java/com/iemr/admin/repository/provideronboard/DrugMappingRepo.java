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

import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.provideronboard.M_104drugmapping;

import jakarta.transaction.Transactional;

@Repository
public interface DrugMappingRepo extends CrudRepository<M_104drugmapping, Integer>{

	
	
	@Query("SELECT g.drugMapID,g.drugId,g.drugName,g.drugGroupID,g.drugGroupName,g.remarks,g.deleted,g.providerServiceMapID, p.stateID,s.stateName,"
			+ " mdg.deleted as drugGroupDeleted "
			+ " FROM M_104drugmapping g "
			+ " LEFT JOIN g.m_providerServiceMapping p "
			+ " LEFT JOIN p.state s "
			+ " LEFT JOIN g.m_104druggroup mdg "
			+ " where (g.drugMapID IS NULL or cast(g.drugMapID as string) like %:drugMapId%)"
			+ " AND p.serviceProviderID=:serviceProviderID AND p.serviceID=:serviceID")
	List<Objects[]> getAllDrugGroupMappings(@Param("drugMapId")String drugMapId, @Param("serviceProviderID")Integer serviceProviderID,
			@Param("serviceID")Integer serviceID );
	
	

	
	
	@Transactional
	@Modifying
	@Query("update M_104drugmapping u set u.deleted=:deleted, u.modifiedBy=:modifiedBy where u.drugMapID =:drugMapID")
	int updateStatus(@Param("drugMapID")Integer drugMapID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
	
	
	@Query("SELECT u FROM M_104drugmapping u where u.drugMapID =:drugMapID")
	M_104drugmapping getDrugMappingById(@Param("drugMapID")Integer drugMapID);
}
