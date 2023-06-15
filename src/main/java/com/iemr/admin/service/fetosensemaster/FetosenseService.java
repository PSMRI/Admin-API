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
package com.iemr.admin.service.fetosensemaster;

import java.util.ArrayList;

import com.iemr.admin.data.fetosensemaster.FetosenseDeviceID;
import com.iemr.admin.utils.exception.IEMRException;


public interface FetosenseService {

	String createFetosenseTestMaster(String requestOBJ) throws Exception;

	String getFetosenseTestMaster(Integer psmID);

	String updateFetosenseTestMaster(String requestOBJ);

	String updateFetosenseTestMasterStatus(Integer fetosenseTestID, Boolean deleted) throws Exception;
	
	int saveFetosenseDeviceID(ArrayList<FetosenseDeviceID> fetosenseRequest) throws IEMRException;

	int updateFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException;

	String getFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException;

	int deleteFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException;

	String getvanIDAndFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException;

	int vanIDAndDeviceIDMapping(FetosenseDeviceID fetosenseData) throws IEMRException;

	String getVanIDMappingWorklist(FetosenseDeviceID fetosenseData) throws IEMRException;

	int updatingvanIDAndDeviceIDMapping(FetosenseDeviceID fetosenseData) throws IEMRException;

	int deleteVanIDAndDeviceIDMapping(FetosenseDeviceID fetosenseData) throws IEMRException;

}
