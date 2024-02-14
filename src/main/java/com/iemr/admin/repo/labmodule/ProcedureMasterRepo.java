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

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.labmodule.ProcedureMaster;

/***
 * 
 * @author Rajeev Tripathi
 *
 */
@Repository
public interface ProcedureMasterRepo extends CrudRepository<ProcedureMaster, Integer> {
	@Query("SELECT procedureID, procedureName, procedureDesc, procedureType, gender, deleted, "
			+ "createdBy, iotProcedureID FROM ProcedureMaster WHERE providerServiceMapID = :psmID")
	ArrayList<Object[]> findProcByPSMID(@Param("psmID") Integer psmID);
	
	@Query("SELECT new ProcedureMaster( procedureID, procedureName, procedureDesc, procedureType, gender, deleted, "
			+ "createdBy, isMandatory, isCalibration, iotProcedureID )FROM ProcedureMaster WHERE providerServiceMapID = :psmID")
	ArrayList<ProcedureMaster> findProcByPSMIDc(@Param("psmID") Integer psmID);

	@Query("SELECT procedureID, procedureName,procedureDesc,procedureType,gender,providerServiceMapID,deleted, "
			+ "processed,createdBy,createdDate,modifiedBy,isCalibration,lastModDate FROM ProcedureMaster WHERE procedureID = :pID")
	public ArrayList<Object[]> getProcedureDetails(@Param("pID") Integer pID);

	@Query("SELECT procedureID, procedureName,procedureDesc,procedureType FROM ProcedureMaster "
			+ " WHERE providerServiceMapID = :psmID AND deleted = false ")
	public ArrayList<Object[]> getProcedureDetailsDelFalse(@Param("psmID") Integer psmID);

	@Modifying
	@Transactional
	@Query("UPDATE ProcedureMaster set deleted = :status WHERE procedureID = :procedureID")
	public int updateProcedureStatus(@Param("procedureID") Integer procedureID, @Param("status") Boolean status);

	@Modifying
	@Transactional
	@Query(" UPDATE ProcedureMaster set procedureName = :procedureName, procedureDesc = :procedureDesc, "
			+ " procedureType = :procedureType, gender = :gender, modifiedBy = :modifiedBy,iotProcedureID =:iotprocedureID,isMandatory =:isMandatory "
			+ " ,isCalibration =:isCalibration Where procedureID = :procedureID ")
	public Integer updateProcedureDetails(@Param("procedureID") Integer procedureID,
			@Param("procedureName") String procedureName, @Param("procedureDesc") String procedureDesc,
			@Param("procedureType") String procedureType, @Param("gender") String gender,
			@Param("modifiedBy") String modifiedBy,@Param("iotprocedureID") Integer iotprocedureID,@Param("isMandatory") Boolean isMandatory,@Param("isCalibration") Boolean isCalibration);
}
