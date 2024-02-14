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

import com.iemr.admin.data.provideronboard.M_104druggroup;

import jakarta.transaction.Transactional;

@Repository
public interface DrugGroupRepo extends CrudRepository<M_104druggroup, Integer>{


	@Query("SELECT g.drugGroupID,g.drugGroup,g.drugGroupDesc,g.deleted,g.serviceProviderID "
			+ " FROM M_104druggroup g "
			+ "where  (g.drugGroupID IS NULL or cast(g.drugGroupID as string) like %:drugGroupID%)"
			+ " AND (g.serviceProviderID IS NULL or cast(g.serviceProviderID as string) like %:serviceProviderID%)")
	List<Objects[]> getAllDrugGroups(@Param("drugGroupID")String drugGroupID, @Param("serviceProviderID") String serviceProviderID);
    
	
	@Query("SELECT g.drugGroupID,g.drugGroup,g.drugGroupDesc,g.deleted,g.serviceProviderID "
			+ " FROM M_104druggroup g "
			+ " where  g.deleted = false"
			+ " AND (g.serviceProviderID IS NULL or cast(g.serviceProviderID as string) like %:serviceProviderID%)")
	List<Objects[]> getValidDrugGroups(@Param("serviceProviderID") String serviceProviderID);
	
	@Transactional
	@Modifying
	@Query("update M_104druggroup u set u.deleted=:deleted, u.modifiedBy=:modifiedBy where u.drugGroupID =:drugGroupID")
	int updateStatus(@Param("drugGroupID")Integer drugGroupID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
	
	
	@Query("SELECT u FROM M_104druggroup u where u.drugGroupID =:drugGroupID")
	M_104druggroup getDrugGroupById(@Param("drugGroupID")Integer drugGroupID);
}
