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
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.provideronboard.M_104drugmaster;


@Repository
@RestResource(exported = false)
public interface DrugMasterRepo extends CrudRepository<M_104drugmaster, Integer> {
    
	
	
/*	@Query("SELECT u FROM M_104drugmaster u")
	ArrayList<M_104drugmaster> getAllDrugData();
	*/


	
	@Query("SELECT m.drugID, m.drugName, m.drugDesc, m.remarks, m.deleted, m.serviceProviderID "
			+ " FROM M_104drugmaster m "
			+ "where  (m.drugID IS NULL or cast(m.drugID as string) like %:drugID%)"
			+ " AND (m.serviceProviderID IS NULL or cast(m.serviceProviderID as string) like %:serviceProviderID%)")
	List<Objects[]> getAllDrugData(@Param("drugID")String drugID, @Param("serviceProviderID")String serviceProviderID);
    
	
	@Query("SELECT m.drugID, m.drugName, m.drugDesc, m.remarks, m.deleted, m.serviceProviderID "
			+ " FROM M_104drugmaster m "
			+ "where  m.deleted = false"
			+ " AND (m.serviceProviderID IS NULL or cast(m.serviceProviderID as string) like %:serviceProviderID%)")
	List<Objects[]> getValidDrugData(@Param("serviceProviderID")String serviceProviderID);
	
	
	@Query("SELECT u FROM M_104drugmaster u where u.drugID =:drugID")
	M_104drugmaster getDrugDataById(@Param("drugID")Integer drugID);

	@Transactional
	@Modifying
	@Query("update M_104drugmaster u set u.deleted=:deleted, u.modifiedBy=:modifiedBy where u.drugID =:drugID")
	int updateStatus(@Param("drugID")Integer drugID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
}
