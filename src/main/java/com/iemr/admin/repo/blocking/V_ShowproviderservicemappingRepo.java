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
package com.iemr.admin.repo.blocking;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.blocking.V_Showproviderservicemapping;

@Repository
public interface V_ShowproviderservicemappingRepo extends CrudRepository<V_Showproviderservicemapping, Integer>{

	
	
	@Query(value = "SELECT u FROM V_Showproviderservicemapping u where u.serviceProviderID = :serviceProviderID AND u.stateID=:stateID AND u.serviceID=:serviceID AND deleted=0", nativeQuery = true)
	ArrayList<V_Showproviderservicemapping> getProviderServiceMappingDetails1(@Param("serviceProviderID")Integer serviceProviderID,@Param("stateID") Integer stateID,
			@Param("serviceID")Integer serviceID);
    
	
	@Query(value = "SELECT u FROM V_Showproviderservicemapping u where u.serviceProviderID = :serviceProviderID AND u.stateID=:stateID AND deleted=0", nativeQuery = true)
	ArrayList<V_Showproviderservicemapping> getProviderStateMappingDetails(@Param("serviceProviderID")Integer serviceProviderID,@Param("stateID") Integer stateID);
	
    
	
	@Query(value = "SELECT u FROM V_Showproviderservicemapping u where u.serviceProviderID = :serviceProviderID AND deleted=0 "
			+ " GROUP BY u.stateID "
			+ " ORDER BY u.serviceName", nativeQuery = true)
	ArrayList<V_Showproviderservicemapping> getProviderStatus(@Param("serviceProviderID")Integer serviceProviderID);
	
	
	@Query(value = "SELECT u FROM V_Showproviderservicemapping u where u.serviceProviderID = :serviceProviderID AND deleted=0 "
			+ " ORDER BY u.serviceName", nativeQuery = true)
	ArrayList<V_Showproviderservicemapping> getProviderStatus1(@Param("serviceProviderID")Integer serviceProviderID);
   
	
	@Query(value = "SELECT u FROM V_Showproviderservicemapping u where u.serviceProviderID = :serviceProviderID AND u.serviceID=:serviceID AND deleted=0", nativeQuery = true)
	ArrayList<V_Showproviderservicemapping> getProviderStatusByProviderAndServiceId(@Param("serviceProviderID")Integer serviceProviderID,
			@Param("serviceID")Integer serviceID);


	List<V_Showproviderservicemapping> findByProviderServiceMapIDIn(ArrayList<Integer> ids);
	
}
