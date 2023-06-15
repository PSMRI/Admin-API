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
package com.iemr.admin.service.vanMaster;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.data.vanType.M_VanType;

public interface VanMasterService {
	
	ArrayList<M_Van> saveVanDetails(List<M_Van> vanMaster);

	//ArrayList<M_Van> getAvailableVans(Integer stateID, Integer districtID, Integer parkingPlaceID,Integer vanTypeID,Integer serviceProviderID);
	
	int updateVanStatus(M_Van vanMaster);
	
	ArrayList<M_VanType> saveVanTypeDetails(List<M_VanType> vanTypeMaster);
	
	int updateVanTypeStatus(M_VanType vanType);
	
	ArrayList<M_VanType> getVanTypes();
	
	M_Van getVanByID(Integer vanID);
	
	M_Van updateVanData(M_Van van);

//	ArrayList<M_Van> getAvailableVans(Integer districtID, Integer parkingPlaceID, Integer vanTypeID,
//			Integer providerServiceMapID);

	ArrayList<M_Van> getAvailableVans( Integer parkingPlaceID,Integer vanTypeID, Integer providerServiceMapID);

//	ArrayList<M_Van> getNonMappedAvailablevans(Integer parkingPlaceID, Integer vanTypeID, Integer providerServiceMapID);
	
}
