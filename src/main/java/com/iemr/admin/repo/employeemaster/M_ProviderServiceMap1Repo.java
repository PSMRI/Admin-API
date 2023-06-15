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
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.M_ProviderServiceMap1;


@Repository
@RestResource(exported = false)
public interface M_ProviderServiceMap1Repo  extends CrudRepository<M_ProviderServiceMap1, Integer>{

	
	
	@Query("SELECT u FROM M_ProviderServiceMap1 u WHERE u.serviceProviderID=:serviceProviderID AND u.stateID=:stateID AND u.serviceID=:serviceID AND deleted=0")
	   List getAllByMapId2(@Param("serviceProviderID") Integer serviceProviderID, @Param("stateID") Integer stateID, @Param("serviceID") Integer serviceID);
	//ArrayList<M_ProviderServiceMap1> getAllByMapId2(Integer serviceProviderID, Integer stateID, Integer serviceID);

	
	
}
