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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.locationmaster.M_ProviderServiceAddMapping;

@Repository
public interface M_ProviderServiceAddMappingRepo extends CrudRepository<M_ProviderServiceAddMapping, Integer>{

	
	@Query("SELECT u FROM M_ProviderServiceAddMapping u WHERE u.pSAddMapID=:pSAddMapID")
	M_ProviderServiceAddMapping editData(@Param ("pSAddMapID")Integer pSAddMapID);
	
	
	@Query("SELECT u FROM M_ProviderServiceAddMapping u where deleted=0")
	ArrayList<M_ProviderServiceAddMapping> getAlldata();

	@Query("SELECT u FROM M_ProviderServiceAddMapping u where u.providerServiceMapID = :pssmID")
	ArrayList<M_ProviderServiceAddMapping> getlocationByMapid(@Param("pssmID") int pssmID);

	@Query("SELECT u FROM M_ProviderServiceAddMapping u where u.providerServiceMapID = :data AND deleted=0")
	ArrayList<M_ProviderServiceAddMapping> getlocationByMapid1(@Param("data")Integer data);


	

}
