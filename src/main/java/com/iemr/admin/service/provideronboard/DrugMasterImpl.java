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
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.provideronboard.M_104druggroup;
import com.iemr.admin.data.provideronboard.M_104drugmapping;
import com.iemr.admin.data.provideronboard.M_104drugmaster;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.repository.provideronboard.DrugGroupRepo;
import com.iemr.admin.repository.provideronboard.DrugMappingRepo;
import com.iemr.admin.repository.provideronboard.DrugMasterRepo;



@Service
public class DrugMasterImpl implements DrugMasterInter{
	@Autowired
	private DrugGroupRepo drugGroupRepo;
	@Autowired
	private DrugMasterRepo drugMasterRepo;
	@Autowired
	private DrugMappingRepo drugMappingRepo;
	
	
	
	@Override
	public Integer getDrugGrupId(M_104druggroup druggroup) {
		M_104druggroup  data=drugGroupRepo.save(druggroup);
		return data.getDrugGroupID();
	}

	
	@Override
	public ArrayList<M_104drugmaster> saveDrugData(List<M_104drugmaster> resList) {
		ArrayList<M_104drugmaster> data1=(ArrayList<M_104drugmaster>) drugMasterRepo.save(resList);
		return data1;
	}

	@Override
	public ArrayList<M_104drugmaster> getAllDrugData(Integer drugID, Short serviceProviderID, Boolean deleted) {
		
		String DrugId="";
		if(null!=drugID){
			DrugId = drugID+"";
		}
		
		String serviceProviderId="";
		if(null!=serviceProviderID){
			serviceProviderId = serviceProviderID+"";
		}

		ArrayList<M_104drugmaster> drugsList = new ArrayList<M_104drugmaster>();		
		
		//List<Objects[]>  allData=drugMasterRepo.getAllDrugData(DrugId,serviceProviderId);
		
		List<Objects[]>  allData;
		
		if(deleted!=null && !deleted){
			allData=drugMasterRepo.getValidDrugData(serviceProviderId);
		}else{
			 allData=drugMasterRepo.getAllDrugData(DrugId,serviceProviderId);
		}
		
		
		for (Object[] objects : allData) {
	
			
			drugsList.add(new M_104drugmaster( (Integer)objects[0], (String)objects[1], (String)objects[2],
					(String)objects[3], (Boolean)objects[4],(Short)objects[5]));
			
		}
		return drugsList;
	}

	@Override
	public M_104drugmaster getDrugDataById(Integer drugID) {
		M_104drugmaster drugdataByid=drugMasterRepo.getDrugDataById(drugID);
		return drugdataByid;
	}
	
	@Override
	public M_104druggroup getDrugGroupById(Integer drugGroupID) {
		M_104druggroup drugdataByid=drugGroupRepo.getDrugGroupById(drugGroupID);
		return drugdataByid;
	}

	@Override
	public M_104drugmaster saveUpdatedData(M_104drugmaster drugMasterdata) {
		
		M_104drugmaster saveData=drugMasterRepo.save(drugMasterdata);
		return saveData;
	}
	
	@Override
	public ArrayList<M_104druggroup> saveDrugGroup(List<M_104druggroup> resList) {
		
		ArrayList<M_104druggroup> data1=(ArrayList<M_104druggroup>) drugGroupRepo.save(resList);
		return data1;
	}

	@Override
	public ArrayList<M_104drugmapping> mapDrugWithGroup(List<M_104drugmapping> resList) {
		ArrayList<M_104drugmapping> data1=(ArrayList<M_104drugmapping>) drugMappingRepo.save(resList);
		return data1;
	}


	@Override
	public ArrayList<M_104druggroup> getAllDrugGroups(Integer drugGroupID, Short serviceProviderID, Boolean deleted) {
		
		String DrugGroupId="";
		if(null!=drugGroupID){
			DrugGroupId = drugGroupID+"";
		}
		
		String serviceProviderId="";
		if(null!=serviceProviderID){
			serviceProviderId = serviceProviderID+"";
		}
		
		ArrayList<M_104druggroup> drugGroupsList = new ArrayList<M_104druggroup>();		
		
		//List<Objects[]> allData=drugGroupRepo.getAllDrugGroups(DrugGroupId,serviceProviderId);
		
		List<Objects[]>  allData;
		
		if(deleted!=null && !deleted){
			allData=drugGroupRepo.getValidDrugGroups(serviceProviderId);
		}else{
			 allData=drugGroupRepo.getAllDrugGroups(DrugGroupId,serviceProviderId);
		}
		
		
		for (Object[] objects : allData) {
	
			
			drugGroupsList.add(new M_104druggroup( (Integer)objects[0], (String)objects[1], (String)objects[2],
					(Boolean)objects[3],(Short)objects[4]));
			
		}
		return drugGroupsList;
	}


	@Override
	public int updateDrugGroupStatus(M_104druggroup drugGroupdata) {
		int response=drugGroupRepo.updateStatus(drugGroupdata.getDrugGroupID(), drugGroupdata.getDeleted(),drugGroupdata.getModifiedBy());
		return response;
	}


	@Override
	public int updateDrugStatus(M_104drugmaster drugMasterdata) {
		int response=drugMasterRepo.updateStatus(drugMasterdata.getDrugID(), drugMasterdata.getDeleted(), drugMasterdata.getModifiedBy());
		return response;
	}


	@Override
	public ArrayList<M_104drugmapping> getAllDrugGroupMappings(Integer drugMapID, Integer serviceProviderID, Integer serviceID) {
		
		String drugMapId="";
		if(null!=drugMapID){
			drugMapId = drugMapID+"";
		}
		
		
		
		ArrayList<M_104drugmapping> drugMappingsList = new ArrayList<M_104drugmapping>();		
		
		List<Objects[]> allData=drugMappingRepo.getAllDrugGroupMappings(drugMapId,serviceProviderID,serviceID);
		
		for (Object[] objects : allData) {
	
			
			drugMappingsList.add(new M_104drugmapping( (Integer)objects[0], (Integer)objects[1], (String)objects[2], (Integer)objects[3],  (String)objects[4],
					(String)objects[5],(Boolean)objects[6],(Integer)objects[7], (Integer)objects[8], (String)objects[9],(Boolean) objects[10]));
			
		}
		
		return drugMappingsList;
	}


	@Override
	public M_104druggroup saveUpdatedDrugGroup(M_104druggroup drugGroupdata) {
		M_104druggroup saveData=drugGroupRepo.save(drugGroupdata);
		return saveData;
	}


	@Override
	public M_104drugmapping saveUpdatedDrugMapping(M_104drugmapping drugMappingdata) {
		M_104drugmapping saveData=drugMappingRepo.save(drugMappingdata);
		return saveData;
	}


	@Override
	public int updateDrugMappingStatus(M_104drugmapping drugMappingdata) {
		int response=drugMappingRepo.updateStatus(drugMappingdata.getDrugMapID(), drugMappingdata.getDeleted(), drugMappingdata.getModifiedBy());
		return response;
	}


	@Override
	public M_104drugmapping getDrugMappingsById(Integer drugMapID) {
		M_104drugmapping drugdataByid=drugMappingRepo.getDrugMappingById(drugMapID);
		return drugdataByid;
	}

}
