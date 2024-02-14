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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.provideronboard.M_Subservice;


@Repository
public interface SubserviceMasterRepo extends CrudRepository<M_Subservice, Integer> {
	@Query("SELECT u FROM M_Subservice u where u.providerServiceMapID = :providerServiceMapID AND deleted=0")
	ArrayList<M_Subservice> getServiceNameByProviderMapId(@Param ("providerServiceMapID") Integer providerServiceMapID);
    
	
	@Query("SELECT u FROM M_Subservice u where  u.providerServiceMapID = :providerServiceMapID")
	ArrayList<M_Subservice> getsubServiceName(@Param ("providerServiceMapID") Integer providerServiceMapID);
    
	
	
	@Query("SELECT u FROM M_Subservice u where u.subServiceID =:subServiceID")
	M_Subservice getsubServiceNameById(@Param("subServiceID")Integer subServiceID);

}
