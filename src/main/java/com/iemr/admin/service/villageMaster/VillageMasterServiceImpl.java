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
package com.iemr.admin.service.villageMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.repository.villageMaster.VillageMasterRepository;

@Service
public class VillageMasterServiceImpl implements VillageMasterService{

	@Autowired
	private VillageMasterRepository villageMasterRepository;
	
	@Override
	public ArrayList<DistrictBranchMapping> storeVillageDetails(List<DistrictBranchMapping> villageList) {
		ArrayList<DistrictBranchMapping> villagesList = (ArrayList<DistrictBranchMapping>) villageMasterRepository.saveAll(villageList);
		return villagesList;
	}

	@Override
	public ArrayList<DistrictBranchMapping> getAvailableVillages(Integer blockID) {
		
		ArrayList<DistrictBranchMapping> villageList = new ArrayList<DistrictBranchMapping>();		
		
		List<Objects[]>  allData=villageMasterRepository.getAvailableVillages(blockID);
		
		for (Object[] objects : allData) {
		
			villageList.add(new DistrictBranchMapping((Integer)objects[0], (Integer)objects[1], (String)objects[2], (String)objects[3], (String)objects[4],
					(String)objects[5], (String)objects[6], (Integer)objects[7], (Integer)objects[8], (Boolean)objects[9],
					(Boolean)objects[10] ));
			
		}
		return villageList;
	}

	@Override
	public int updateVillageStatus(DistrictBranchMapping districtBranchMapping) {
		int response = villageMasterRepository.updateVillageStatus(districtBranchMapping.getDistrictBranchID(), districtBranchMapping.getDeleted(), districtBranchMapping.getModifiedBy());
		return response;
	}

	@Override
	public int updateVillageData(DistrictBranchMapping districtBranchMapping) {
		int response = villageMasterRepository.updateVillageData(districtBranchMapping.getPanchayatName(), districtBranchMapping.getVillageName(), 
						districtBranchMapping.getHabitat(), districtBranchMapping.getPinCode(), districtBranchMapping.getGovVillageID(), 
						districtBranchMapping.getGovSubDistrictID(), districtBranchMapping.getDistrictBranchID(), districtBranchMapping.getModifiedBy(),
						districtBranchMapping.getIsRural());
		return response;
	}

}
