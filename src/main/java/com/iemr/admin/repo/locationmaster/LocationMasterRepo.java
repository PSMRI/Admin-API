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
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.locationmaster.StateServiceMapping1;

@Repository
@RestResource(exported = false)
public interface LocationMasterRepo extends CrudRepository<StateServiceMapping1, Integer>{
	
	@Query("SELECT distinct rp.stateID,"
			  + " sm.stateName as stateName, "
			  + " rp.statusID,"
			  + " rp.providerServiceMapID"
			  + " FROM StateServiceMapping1 rp "
			  + " JOIN rp.stateMaster sm "
			  + " WHERE rp.serviceProviderID =:serviceProviderID AND rp.deleted=0 ORDER BY sm.stateName")
	ArrayList<Object[]> getStateByServiceProviderId(@Param("serviceProviderID") Integer serviceProviderID);
	
	
	@Query("SELECT distinct rp.serviceID, rp.providerServiceMapID,"
			+ " sm.serviceName as serviceName"
			+ " FROM StateServiceMapping1 rp "
			+ " JOIN rp.serviceMaster sm "
			+ " WHERE rp.serviceProviderID =:serviceProviderID AND rp.stateID =:stateID AND rp.deleted=0")

	ArrayList<Object[]> getServiceByServiceProviderIdAndStateId(@Param ("serviceProviderID") Integer serviceProviderID, @Param ("stateID") Integer stateID);

	@Query("SELECT u.providerServiceMapID FROM StateServiceMapping1 u WHERE u.serviceProviderID=:serviceProviderID AND u.stateID=:stateID AND u.serviceID=:serviceID AND deleted=0 ")
	StateServiceMapping1 getProviderServiceMapID(@Param("serviceProviderID") Integer serviceProviderID, @Param("stateID") Integer stateID, @Param("serviceID") Integer serviceID);


	
	@Query("SELECT u FROM StateServiceMapping1 u WHERE u.serviceProviderID=:serviceProviderID AND u.stateID=:stateID AND u.serviceID=:serviceID AND deleted=0 ")
	   List getAllByMapId2(@Param("serviceProviderID") Integer serviceProviderID, @Param("stateID") Integer stateID, @Param("serviceID") Integer serviceID);

    
	
	@Query("SELECT u FROM StateServiceMapping1 u WHERE u.serviceProviderID=:serviceProviderID AND u.serviceID=:serviceID AND deleted=0 ")
	ArrayList<StateServiceMapping1> getLocationByServiceID(@Param("serviceProviderID")Integer serviceProviderID,@Param("serviceID") Integer serviceID);

     
	@Query("SELECT u FROM StateServiceMapping1 u WHERE u.serviceProviderID=:serviceProviderID AND u.stateID=:stateID AND deleted=0")
	ArrayList<StateServiceMapping1> getLocationByStateID(@Param("serviceProviderID")Integer serviceProviderID,@Param("stateID")Integer stateID);

	@Query("SELECT distinct u.stateID, sm.stateName as stateName, u.providerServiceMapID  "
			+ "FROM StateServiceMapping1 u "
			+ " JOIN u.stateMaster sm "
			+ "WHERE u.serviceID=:serviceID and u.serviceProviderID =:serviceProviderID  ")
	ArrayList<Object[]> getStatesByServiceId(@Param("serviceID") Integer serviceID,@Param ("serviceProviderID") Integer serviceProviderID);
	
	
	@Query("SELECT u FROM StateServiceMapping1 u WHERE u.serviceProviderID=:serviceProviderID AND u.stateID=null AND u.serviceID=:serviceID AND deleted=0 ")
	   List getAllByMapId3(@Param("serviceProviderID") Integer serviceProviderID, @Param("serviceID") Integer serviceID);

	
}
