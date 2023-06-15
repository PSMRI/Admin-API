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
package com.iemr.admin.service.provideronboard;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.provideronboard.M_104druggroup;
import com.iemr.admin.data.provideronboard.M_104drugmapping;
import com.iemr.admin.data.provideronboard.M_104drugmaster;



public interface DrugMasterInter {

	Integer getDrugGrupId(M_104druggroup druggroup);
	
	ArrayList<M_104druggroup> saveDrugGroup(List<M_104druggroup> resList);
	
	ArrayList<M_104drugmaster> saveDrugData(List<M_104drugmaster> resList);

	ArrayList<M_104drugmaster> getAllDrugData(Integer drugID, Short serviceProviderID, Boolean deleted);

	ArrayList<M_104druggroup> getAllDrugGroups(Integer drugGroupID, Short serviceProviderID, Boolean deleted);
	
	ArrayList<M_104drugmapping> getAllDrugGroupMappings(Integer drugMapID, Integer serviceProviderID, Integer serviceID);

	M_104drugmaster getDrugDataById(Integer drugID);

	M_104drugmaster saveUpdatedData(M_104drugmaster drugMasterdata);
	
	ArrayList<M_104drugmapping> mapDrugWithGroup(List<M_104drugmapping> resList);
	
	int updateDrugGroupStatus(M_104druggroup drugGroupdata);
	
	int updateDrugStatus(M_104drugmaster drugMasterdata);
	
	int updateDrugMappingStatus(M_104drugmapping drugMappingdata);
	
	M_104druggroup saveUpdatedDrugGroup(M_104druggroup drugGroupdata);
	
	M_104drugmapping saveUpdatedDrugMapping(M_104drugmapping drugGroupdata);
	
	M_104druggroup getDrugGroupById(Integer drugGroupID);
	
	M_104drugmapping getDrugMappingsById(Integer drugMapID);
	

}
