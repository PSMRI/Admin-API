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
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.rolemaster.StateServiceMapping;

@Repository
public interface RoleMasterRepo extends CrudRepository<StateServiceMapping, Integer> {
		@Query("SELECT distinct rp.stateID,"
			  + " sm.stateName, "
			  //+ " sm.stateName as createdBy "
			  //+ " sm.stateName as createdBy "
			  + "rp.statusID"
			  + " FROM StateServiceMapping rp "
			  + " JOIN rp.stateMaster sm "
			  + " WHERE rp.serviceProviderID =:serviceProviderID AND rp.deleted=0 ORDER BY sm.stateName ")
	ArrayList<Object[]> getStateByServiceProviderId(@Param ("serviceProviderID") Integer serviceProviderID);
		
	
	@Query("SELECT distinct rp.serviceID, rp.providerServiceMapID,"
		+ " sm.serviceName as serviceName,"
		 + "rp.statusID"
		+ " FROM StateServiceMapping rp "
		+ " JOIN rp.serviceMaster sm "
		+ " WHERE rp.serviceProviderID =:serviceProviderID AND rp.stateID =:stateID  AND rp.deleted=0")
		ArrayList<Object[]> getServiceByServiceProviderIdAndStateId(@Param ("serviceProviderID") Integer serviceProviderID, @Param ("stateID") Integer stateID);

		@Query("SELECT u FROM StateServiceMapping u WHERE u.serviceProviderID=:serviceProviderID AND u.stateID=:stateID AND u.serviceID=:serviceID AND u.deleted=0")
	   List getAllByMapId(@Param("serviceProviderID") Integer serviceProviderID, @Param("stateID") Integer stateID, @Param("serviceID") Integer serviceID);
      
		
		
		@Query("SELECT u FROM StateServiceMapping u WHERE u.serviceProviderID=:serviceProviderID AND u.stateID=null AND u.serviceID=:serviceID AND u.deleted=0")
		   List getAlByMapId(@Param("serviceProviderID") Integer serviceProviderID, @Param("serviceID") Integer serviceID);
}
