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
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.provideronboard.M_104drugmaster;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.data.zonemaster.M_ZoneDistrictMap;
import com.iemr.admin.repository.zonemaster.ZoneDistrictMappingRepo;
import com.iemr.admin.repository.zonemaster.ZoneMasterRepository;

@Service
public class ZoneMasterServiceImpl implements ZoneMasterService{

	@Autowired
	private ZoneMasterRepository zoneMasterRepo;
	
	@Autowired
	private ZoneDistrictMappingRepo zoneDistrictMappingRepo;
	
	@Override
	public ArrayList<M_Zone> createZone(List<M_Zone> zoneList) {
		ArrayList<M_Zone> zones=(ArrayList<M_Zone>) zoneMasterRepo.saveAll(zoneList);
		return zones;
	}

	@Override
	public ArrayList<M_Zone> getAvailableZones(Integer providerServiceMapID) {
		

		ArrayList<M_Zone> zoneList = new ArrayList<M_Zone>();		
		
		List<Objects[]>  allData=zoneMasterRepo.getAvailableZones(providerServiceMapID);
		
		for (Object[] objects : allData) {

			zoneList.add(new M_Zone((Integer)objects[0], (String)objects[1], (String)objects[2], (String)objects[3], (Integer)objects[4], (Boolean)objects[5], 
					(Integer)objects[6], (String)objects[7],(Integer)objects[8], (String)objects[9],(Integer)objects[10], (String)objects[11],(Integer)objects[12],
						(String)objects[13],(Integer)objects[14], (String)objects[15],(M_ProviderServiceMapping)objects[16],(Integer)objects[17], (String)objects[18]));
			
		}
		return zoneList;
	}
	
	@Override
	public ArrayList<M_ZoneDistrictMap> createZoneDistrictMapping(List<M_ZoneDistrictMap> zoneList) {
		ArrayList<M_ZoneDistrictMap> zones=(ArrayList<M_ZoneDistrictMap>) zoneDistrictMappingRepo.saveAll(zoneList);
		return zones;
	}

	@Override
	public ArrayList<M_ZoneDistrictMap> getAvailableZoneDistrictMappings(Integer providerServiceMapID) {
		
		ArrayList<M_ZoneDistrictMap> zoneList = new ArrayList<M_ZoneDistrictMap>();		
		
		List<Objects[]>  allData=zoneDistrictMappingRepo.getAvailableZoneDistrictMappings(providerServiceMapID);
		
		for (Object[] objects : allData) {
			
			zoneList.add(new M_ZoneDistrictMap((Integer)objects[0], (Integer)objects[1], (String)objects[2], (Integer)objects[3], (Integer)objects[4], (Boolean)objects[5], (Integer)objects[6], 
					(String)objects[7], (String)objects[8],  (Integer)objects[9], (String)objects[10],(Boolean) objects[11]));
		}
		return zoneList;
	}

	@Override
	public int updateZoneStatus(M_Zone m_zone) {
		int response = zoneMasterRepo.updateZoneStatus(m_zone.getZoneID(), m_zone.getDeleted(), m_zone.getModifiedBy());
		List<Objects[]>  allData=zoneDistrictMappingRepo.getAvailableZoneDistrictMappingss(m_zone.getZoneID().toString());
		for (Object[] objects : allData) {
			
			M_ZoneDistrictMap m_zoneDistrictMap = new M_ZoneDistrictMap((Integer)objects[0], (Integer)objects[1], (String)objects[2], (Integer)objects[3], (Integer)objects[4], (Boolean)objects[5], (Integer)objects[6], 
					(String)objects[7], (String)objects[8], (Integer)objects[9], (String)objects[10],(Boolean) objects[11]);
			m_zoneDistrictMap.setDeleted(m_zone.getDeleted());
			m_zoneDistrictMap.setModifiedBy(m_zone.getModifiedBy());
		updateZoneDistrictMappingStatus(m_zoneDistrictMap);
		
			
		}
		return response;
	}

	@Override
	public int updateZoneDistrictMappingStatus(M_ZoneDistrictMap m_zoneDistrictMap) {
		int response = zoneDistrictMappingRepo.updateZoneDistrictMappingStatus(m_zoneDistrictMap.getZoneDistrictMapID(), m_zoneDistrictMap.getDeleted(), m_zoneDistrictMap.getModifiedBy());
		return response;
	}

	@Override
	public M_Zone updateZoneData(M_Zone m_zone) {
		M_Zone zone = zoneMasterRepo.save(m_zone);
		return zone;
	}

	@Override
	public M_Zone getzoneByID(Integer zoneID) {
		M_Zone zone = zoneMasterRepo.getZoneById(zoneID);
		return zone;
	}

	public M_ZoneDistrictMap editZoneDistrictMapping(Integer zoneDistrictMapID) {
		M_ZoneDistrictMap data=zoneDistrictMappingRepo.findByZoneDistrictMapID(zoneDistrictMapID);
		return data;
	}

	public M_ZoneDistrictMap saveeditedData(M_ZoneDistrictMap zones) {
		M_ZoneDistrictMap data=zoneDistrictMappingRepo.save(zones);
		return data;
	}

	public ArrayList<M_ZoneDistrictMap> editZoneDistrictMapping1(Integer zoneID) {
		//ArrayList<M_ZoneDistrictMap> data=zoneDistrictMappingRepo.editZoneDistrictMapping1(zoneID);
		
		
        ArrayList<M_ZoneDistrictMap> zoneList = new ArrayList<M_ZoneDistrictMap>();		
		
		List<Objects[]>  allData=zoneDistrictMappingRepo.editZoneDistrictMapping1(zoneID);
		
		for (Object[] objects : allData) {
			
			zoneList.add(new M_ZoneDistrictMap((String)objects[0], (Integer)objects[1]));
		}
		return zoneList;
	}

	public int getAllMappedRecord(Integer zoneID) {
		int count=200;
		List count1=zoneDistrictMappingRepo.getRecord(zoneID);
		if(count1.size()>1)
		return 100;
		else
		return count;
	}

	
	

}
