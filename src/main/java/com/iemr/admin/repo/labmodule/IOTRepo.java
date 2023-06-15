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
package com.iemr.admin.repo.labmodule;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.labmodule.IOTComponent;
import com.iemr.admin.data.labmodule.IOTProcedure;

@Repository
@RestResource(exported = false)
public interface IOTRepo extends CrudRepository<IOTProcedure, Integer> {

	@Query("SELECT iotProcedure from IOTProcedure iotProcedure where iotProcedure.deleted=false")
	ArrayList<IOTProcedure> getIOTProcedure();
	
	@Query("SELECT iotComponent from IOTComponent iotComponent where iotComponent.deleted=false")
	ArrayList<IOTComponent> getIOTComponent();
	
	@Query("SELECT iotProcedure from IOTProcedure iotProcedure where iotProcedure.iotProcedureID =:id ")
	IOTProcedure getIOTProcedureByID(@Param("id") Integer id);
	
	@Transactional
	@Modifying
	@Query("update IOTProcedure set calibrationStartAPI = :calibrationStartAPI,calibrationStatusAPI = :calibrationStatusAPI,calibrationEndAPI = :calibrationEndAPI "
			+ " where iotProcedureID = :id")
	public int updateIOTWithCalibration(@Param("calibrationStartAPI") String calibrationStartAPI,
			@Param("calibrationStatusAPI") String calibrationStatusAPI,@Param("calibrationEndAPI") String calibrationEndAPI
			,@Param("id") Integer id);
}
