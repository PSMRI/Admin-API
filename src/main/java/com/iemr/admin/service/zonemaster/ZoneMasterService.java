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
package com.iemr.admin.service.zonemaster;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.provideronboard.M_104drugmaster;
import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.data.zonemaster.M_ZoneDistrictMap;

public interface ZoneMasterService {

	
	ArrayList<M_Zone> createZone(List<M_Zone> zoneList);

	ArrayList<M_Zone> getAvailableZones(Integer ServiceProviderID);
	
	ArrayList<M_ZoneDistrictMap> createZoneDistrictMapping(List<M_ZoneDistrictMap> zoneList);
	
	ArrayList<M_ZoneDistrictMap> getAvailableZoneDistrictMappings(Integer serviceProviderID);
	
	int updateZoneStatus(M_Zone m_zone);
	
	int updateZoneDistrictMappingStatus(M_ZoneDistrictMap m_zoneDistrictMap);
	
	M_Zone updateZoneData(M_Zone m_zone);
	
	M_Zone getzoneByID(Integer zoneID);
}
