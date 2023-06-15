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
package com.iemr.admin.service.snomedMapping;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.snomedMapping.ChildVaccinations;
import com.iemr.admin.data.snomedMapping.DiseaseType;
import com.iemr.admin.data.snomedMapping.OptionalVaccinations;
import com.iemr.admin.repository.snomedRepo.SnomedImmunizationRepo;
import com.iemr.admin.repository.snomedRepo.SnomedMappingRepo;
import com.iemr.admin.repository.snomedRepo.SnomedVaccinationRepo;

import com.iemr.admin.utils.mapper.InputMapper;

@Service
public class SnomedServiceImpl implements SnomedService{
	
	@Autowired
	private SnomedMappingRepo snomedFamilyHistoryRepo;
	
	@Autowired
	private SnomedVaccinationRepo snomedVaccinationRepo;
	
	@Autowired
	private SnomedImmunizationRepo snomedImmunizationRepo;
	
	@Override
	public String editSnomedMappingData(JsonObject requestOBJ,String OBJ) {
		
		Integer res=null;
		String snomedData=null;
		if (null != requestOBJ) {
			switch (requestOBJ.get("masterType").getAsString()) {
			case "Family History": 
				DiseaseType familyReq =  InputMapper.gson().fromJson(OBJ,DiseaseType .class);
				
//				res = snomedFamilyHistoryRepo.updateFamilyHistoryDetails(Short.valueOf(requestOBJ.get("masterID").toString()),requestOBJ.get("sctCode").toString(), requestOBJ.get("sctTerm").toString());
				res = snomedFamilyHistoryRepo.updateFamilyHistoryDetails(familyReq.getMasterID(),familyReq.getSctCode(),familyReq.getSctTerm(),familyReq.getModifiedBy());
				

			
			
			break;
          case "Optional Vaccination": 
        	  OptionalVaccinations vaccinationReq =  InputMapper.gson().fromJson(OBJ,OptionalVaccinations .class);
        	  res = snomedVaccinationRepo.updateVaccinationDetails(vaccinationReq.getMasterID(),vaccinationReq.getSctCode(),vaccinationReq.getSctTerm(),vaccinationReq.getModifiedBy());
				
				
			
			break;
          case "Immunization": 
        	  ChildVaccinations immunizationReq =  InputMapper.gson().fromJson(OBJ,ChildVaccinations .class);
        	  res = snomedImmunizationRepo.updateImmunizationDetails(immunizationReq.getMasterID(),immunizationReq.getSctCode(),immunizationReq.getSctTerm(),immunizationReq.getModifiedBy());
				
			
			break;
			
			
			
			default: 
				snomedData = "Invalid Master Type";
				
			
			}
			
			if(res > 0)
				{
					snomedData="Data Updated";
				}
		} 
	    else {
			snomedData = "Invalid Master Type";
		}
		return snomedData;
	}
	
	
	@Override
	public String saveSnomedMappingData(JsonObject requestOBJ,String reObj) {
		
		Integer res=null;
		String snomedData=null;
		if (null != requestOBJ) {
			switch (requestOBJ.get("masterType").getAsString()) {
			case "Family History": 
				

				DiseaseType familyReq =  InputMapper.gson().fromJson(reObj,DiseaseType.class);
				
				List<DiseaseType> familyResObj=(List<DiseaseType>) snomedFamilyHistoryRepo.save(familyReq.getMappingDetails());

				
				
				
				
				if (null != familyResObj && familyResObj.size() > 0) {
					snomedData="Data Saved";
				}
				
			
			break;
          case "Optional Vaccination": 
        	  OptionalVaccinations vaccinationReq =  InputMapper.gson().fromJson(requestOBJ.toString(),OptionalVaccinations .class);
        	  List<OptionalVaccinations> vaccinationResObj=(List<OptionalVaccinations>) snomedVaccinationRepo.save(vaccinationReq.getMappingDetails());
				
        	  if (null != vaccinationResObj && vaccinationResObj.size() > 0) {
					snomedData="Data Saved";
				}
			
			break;
          case "Immunization": 
        	  ChildVaccinations immunizationReq =  InputMapper.gson().fromJson(requestOBJ.toString(),ChildVaccinations .class);
        	  List<ChildVaccinations> immunizationResObj=(List<ChildVaccinations>) snomedImmunizationRepo.save(immunizationReq.getMappingDetails());
				
        	  if (null != immunizationResObj && immunizationResObj.size() > 0) {
					snomedData="Data Saved";
				}
			
			break;
			
			
			
			default: 
				snomedData = "Invalid Master Type";
				
			
			}
			
		
		} 
	    else {
			snomedData = "Invalid Master Type";
		}
		return snomedData;
	}
	
	@Override
	public String fetchSnomedMaster(JsonObject requestOBJ) {
		
		String res=null;
		if ( requestOBJ!=null) {
			switch (requestOBJ.get("masterType").getAsString()) {
			case "Family History": 
				res = new Gson().toJson(snomedFamilyHistoryRepo.fetchDiseaseType());
			break;
          case "Optional Vaccination": 
        	  res = new Gson().toJson(snomedVaccinationRepo.fetchOptionalVaccinations());
			break;
          case "Immunization": 
        	  res = new Gson().toJson(snomedImmunizationRepo.fetchChildVaccinations());
			break;
			default: 
				res = "Invalid Master Type";
			}
			
		} 
		else 
			res ="Invalid request";
		return res;
	}
	@Override
	public String updateStatus(String requestOBJ) {
		DiseaseType obj = InputMapper.gson().fromJson(requestOBJ, DiseaseType.class);
		Integer res=0;String result=null;
		if ( obj!=null) {
			switch (obj.getMasterType()) {
			case "Family History": 
				res = snomedFamilyHistoryRepo.updateStatus(obj.getMasterID(), obj.getDeleted(), obj.getModifiedBy());
			break;
          case "Optional Vaccination": 
        	  res = snomedVaccinationRepo.updateStatus(obj.getMasterID(), obj.getDeleted(), obj.getModifiedBy());
			break;
          case "Immunization": 
        	  res =snomedImmunizationRepo.updateStatus(obj.getMasterID(), obj.getDeleted(), obj.getModifiedBy());
			break;
			default: 
				result = "Invalid Master Type";
			}
			
		} 
		else 
			result ="Invalid request";
		if(res>0)
			result="Data updated successfully";
		else
			result="Data not updated";
		return result;
	}
}
