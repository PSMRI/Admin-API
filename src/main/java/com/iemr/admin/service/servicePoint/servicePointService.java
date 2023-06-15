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
package com.iemr.admin.service.servicePoint;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.servicePoint.M_Servicepoint;
import com.iemr.admin.data.servicePoint.M_Servicepointvillagemap;

public interface servicePointService {
	ArrayList<M_Servicepoint> saveServicePoint(List<M_Servicepoint> ServicePoints);

	ArrayList<M_Servicepoint> getAvailableServicePoints(Integer stateID, Integer districtID, Integer parkingPlaceID, Integer serviceProviderID);
	
	int updateServicePointStatus(M_Servicepoint m_ServicePoint);
	
	
	ArrayList<M_Servicepointvillagemap> saveServicePointVillageMap(List<M_Servicepointvillagemap> Servicepointvillagemaps);

	ArrayList<M_Servicepointvillagemap> getAvailableServicePointVillageMaps(Integer stateID, Integer districtID, 
			Integer parkingPlaceID, Integer servicePointID,  Integer serviceProviderID);
	
	int updateServicePointVillageMapStatus(M_Servicepointvillagemap m_ServicePoint);
}
