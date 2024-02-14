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
package com.iemr.admin.repo.calibration;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.calibration.CalibrationStrip;

import jakarta.transaction.Transactional;

@Repository
public interface CalibrationRepo extends CrudRepository<CalibrationStrip, Integer>{

	
	@Query("SELECT iotProcedure FROM CalibrationStrip iotProcedure where iotProcedure.deleted=false and providerServiceMapID = :providerServiceMapID order by 1 desc ")
	public Page<CalibrationStrip> getCalibrationStripsWithPagination(@Param("providerServiceMapID") Integer providerServiceMapID, Pageable pageable);
	
	@Query("SELECT iotProcedure FROM CalibrationStrip iotProcedure where providerServiceMapID = :providerServiceMapID order by 1 desc ")
	public ArrayList<CalibrationStrip>  getCalibrationStripsWithoutPagination(@Param("providerServiceMapID") Integer providerServiceMapID);
	
	@Transactional
	@Modifying
	@Query("update CalibrationStrip set deleted = :deleted where calibrationStripID = :id")
	public int deleteCalibrationStrip(@Param("id") Long id, @Param("deleted") Boolean deleted);

	@Query("SELECT iotProcedure FROM CalibrationStrip iotProcedure where iotProcedure.stripCode =:stripCode and providerServiceMapID = :providerServiceMapID ")
	public ArrayList<CalibrationStrip> checkIfAlreadyStripPresent(@Param("providerServiceMapID") Integer providerServiceMapID, @Param("stripCode") String stripCode);
}
