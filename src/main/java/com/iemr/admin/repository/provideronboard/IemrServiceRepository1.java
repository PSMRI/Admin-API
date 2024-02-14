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

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.provideronboard.ServiceProvider_Model;

import jakarta.transaction.Transactional;

@Transactional
//@Commit

@Repository
public interface IemrServiceRepository1 extends CrudRepository<ServiceProvider_Model, Integer> {
	@Query("Select b from ServiceProvider_Model b where deleted=0")
	List<ServiceProvider_Model> getAlldata(Pageable pageable);

	@Query("SELECT u FROM ServiceProvider_Model u WHERE LOWER(u.serviceProviderId) = LOWER(:res) AND deleted=0")
	ServiceProvider_Model getDataById(@Param("res") int res);
	// M_User1 findByUserID(@Param("UserID") Long UserID);

	@Query("Select b from ServiceProvider_Model b")
	List<ServiceProvider_Model> getAlldata1();
	@Query("SELECT u.serviceProviderName FROM ServiceProvider_Model u WHERE LOWER(u.serviceProviderName) = LOWER(:serviceProviderName)")
	String getProviderName(@Param("serviceProviderName")String serviceProviderName);
    
	
	@Query(" Select b from ServiceProvider_Model b ORDER By b.serviceProviderName ")
	ArrayList<ServiceProvider_Model> getAllProviderName();
    
	
	@Query("Select b from ServiceProvider_Model b where serviceProviderId=:serviceProviderId")
	ServiceProvider_Model getProviderData(@Param("serviceProviderId")Integer serviceProviderId);

}
