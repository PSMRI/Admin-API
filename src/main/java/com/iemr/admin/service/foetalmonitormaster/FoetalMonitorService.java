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
package com.iemr.admin.service.foetalmonitormaster;

import java.util.ArrayList;

import com.iemr.admin.data.foetalmonitormaster.FoetalMonitorDeviceID;
import com.iemr.admin.utils.exception.IEMRException;


public interface FoetalMonitorService {

	String createFoetalMonitorTestMaster(String requestOBJ) throws Exception;

	String getFoetalMonitorTestMaster(Integer psmID);

	String updateFoetalMonitorTestMaster(String requestOBJ);

	String updateFoetalMonitorTestMasterStatus(Integer foetalMonitorTestID, Boolean deleted) throws Exception;
	
	int saveFoetalMonitorDeviceID(ArrayList<FoetalMonitorDeviceID> foetalMonitorRequest) throws IEMRException;

	int updateFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

	String getFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

	int deleteFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

	String getvanIDAndFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

	int vanIDAndDeviceIDMapping(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

	String getVanIDMappingWorklist(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

	int updatingvanIDAndDeviceIDMapping(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

	int deleteVanIDAndDeviceIDMapping(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException;

}
