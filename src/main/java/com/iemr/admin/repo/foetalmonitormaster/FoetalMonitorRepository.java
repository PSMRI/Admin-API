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
package com.iemr.admin.repo.foetalmonitormaster;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.foetalmonitormaster.M_FoetalMonitor;

@Repository
public interface FoetalMonitorRepository extends CrudRepository<M_FoetalMonitor, Integer> {
	
	@Query("SELECT F FROM M_FoetalMonitor F WHERE foetalMonitorTestID = :foetalMonitorTestID")
	public M_FoetalMonitor getByFoetalMonitorTestID(@Param("foetalMonitorTestID") Integer foetalMonitorTestID);

	@Query("SELECT F FROM M_FoetalMonitor F WHERE F.providerServiceMapID = :psmID")
	public ArrayList<M_FoetalMonitor> getByProviderServiceMapID(@Param("psmID") Integer psmID);
	
	@Modifying
	@Transactional
	@Query(" UPDATE M_FoetalMonitor SET testName = :testName, testDesc = :testDesc, "
			+ "modifiedBy = :modifiedBy WHERE foetalMonitorTestID = :foetalMonitorTestID ")
	public Integer updateFoetalMonitorDetails(@Param("foetalMonitorTestID") Integer foetalMonitorTestID,
			@Param("testName") String testName, @Param("testDesc") String testDesc,
			@Param("modifiedBy") String modifiedBy);

	@Modifying
	@Transactional
	@Query("UPDATE M_FoetalMonitor SET deleted = :status WHERE foetalMonitorTestID = :foetalMonitorTestID")
	public int updateFoetalMonitorStatus(@Param("foetalMonitorTestID") Integer foetalMonitorTestID, @Param("status") Boolean status);
}
	
