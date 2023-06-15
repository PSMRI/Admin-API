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
package com.iemr.admin.service.userParkingPlaceMap;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.userParkingPlaceMap.M_UserParkingPlaceMap;

public interface UserParkingPlaceMapService {
	
	ArrayList<M_UserParkingPlaceMap> saveUserParkingPlaceDetails(List<M_UserParkingPlaceMap> userParkingPlaceMaster) ;

	ArrayList<M_UserParkingPlaceMap> getUserParkingPlaceMappings( Integer providerServiceMapID, Integer stateID, Integer districtID, 
			Integer parkingPlaceID, Integer designationID);
	
	int updateUserParkingPlaceMapStatus(M_UserParkingPlaceMap userParkingPlaceMaster);
	
	M_UserParkingPlaceMap getUserParkingPlaceMapByID(Integer userParkingPlaceMapID);
	
}
