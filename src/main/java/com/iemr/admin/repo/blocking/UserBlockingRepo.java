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


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.blocking.UserForBlocking;

import jakarta.transaction.Transactional;

@Repository
public interface UserBlockingRepo extends CrudRepository<UserForBlocking, Integer>{
	@Query("SELECT u FROM UserForBlocking u where u.userID = :userID AND deleted=0")
	UserForBlocking getUserDetailByUserId(@Param("userID")Integer userID);

	
	
	
	
	@Transactional
	@Modifying
	@Query(" UPDATE  UserForBlocking u SET u.statusID=:statusID where u.userID = :userID")
	void blockUser(@Param("userID")  Integer userID,@Param("statusID") Integer statusID);

}
