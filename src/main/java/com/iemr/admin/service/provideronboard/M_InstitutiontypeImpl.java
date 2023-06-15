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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.provideronboard.M_Institutiontype;
import com.iemr.admin.repository.provideronboard.M_InstitutiontypeRepo;

@Service
public class M_InstitutiontypeImpl implements M_InstitutiontypeInter{
	
	
	
 @Autowired	
 private M_InstitutiontypeRepo m_InstitutiontypeRepo;

@Override
public ArrayList<M_Institutiontype> createInstuteType(List<M_Institutiontype> instuteType) {
	ArrayList<M_Institutiontype> data=(ArrayList<M_Institutiontype>) m_InstitutiontypeRepo.save(instuteType);
	return data;
}

@Override
public ArrayList<M_Institutiontype> getInstuteType(Integer providerServiceMapID) {
	ArrayList<M_Institutiontype> data=m_InstitutiontypeRepo.getInstuteType(providerServiceMapID);
	return data;
}

@Override
public M_Institutiontype editInstuteType(Integer institutionTypeID) {
	 M_Institutiontype data=m_InstitutiontypeRepo.editdata(institutionTypeID);
	return data;
}

@Override
public M_Institutiontype saveEditdata(M_Institutiontype editInstuteType1) {
	M_Institutiontype data=m_InstitutiontypeRepo.save(editInstuteType1);
	return data;
}	
/*
 * Created BY Du20091017
 */
@Override
public ArrayList<M_Institutiontype> createInstuteTypeByDist(List<M_Institutiontype> instuteType) {
	ArrayList<M_Institutiontype> data=(ArrayList<M_Institutiontype>) m_InstitutiontypeRepo.save(instuteType);
	return data;
}	

/*
 * Created BY Du20091017
 */
@Override
public ArrayList<M_Institutiontype> getInstuteTypeByDist(Integer providerServiceMapID, Integer districtId,
		Integer subDistrictId,Integer villageId) {
	
	ArrayList<M_Institutiontype> data = new ArrayList<>();
	if(subDistrictId==null && villageId == null ){
		  
		data=m_InstitutiontypeRepo.getInstuteTypeByDist(providerServiceMapID,districtId);
	   }else if (subDistrictId!=null && villageId == null){
		   data=m_InstitutiontypeRepo.getInstutionTypeByBlock(providerServiceMapID,districtId,subDistrictId);
	   }else if(subDistrictId==null && villageId != null) {
		   data=m_InstitutiontypeRepo.getInstutionTypeByVillage(providerServiceMapID,districtId,villageId);
	   }else {
		   data=m_InstitutiontypeRepo.getInstutionByBlockAndVillage(providerServiceMapID,districtId,subDistrictId,villageId);
	   }
	
	return data;
}	

}
