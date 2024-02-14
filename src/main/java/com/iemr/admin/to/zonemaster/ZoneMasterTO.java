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
package com.iemr.admin.to.zonemaster;

import java.util.List;

import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.data.zonemaster.M_ZoneDistrictMap;

import jakarta.persistence.Transient;

public class ZoneMasterTO {

	@Transient
	private List<M_Zone> zones;
	
	@Transient
	private List<M_ZoneDistrictMap> zoneDistrictMappings;

	public List<M_Zone> getZones() {
		return zones;
	}

	public void setZones(List<M_Zone> zones) {
		this.zones = zones;
	}

	public List<M_ZoneDistrictMap> getZoneDistrictMappings() {
		return zoneDistrictMappings;
	}

	public void setZoneDistrictMappings(List<M_ZoneDistrictMap> zoneDistrictMappings) {
		this.zoneDistrictMappings = zoneDistrictMappings;
	}
	
	
	
}
