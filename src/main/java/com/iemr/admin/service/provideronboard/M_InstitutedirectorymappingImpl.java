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

import com.iemr.admin.data.provideronboard.M_Institutedirectorymapping;
import com.iemr.admin.data.rolemaster.StateServiceMapping;
import com.iemr.admin.repository.provideronboard.M_InstitutedirectorymappingRepo;

@Service
public class M_InstitutedirectorymappingImpl implements M_InstitutedirectorymappingInter{
 @Autowired
 private M_InstitutedirectorymappingRepo m_InstitutedirectorymappingRepo;

@Override
public ArrayList<M_Institutedirectorymapping> createInstituteDirectoryData(
		List<M_Institutedirectorymapping> instuteDiractoty) {
	ArrayList<M_Institutedirectorymapping> data=(ArrayList<M_Institutedirectorymapping>) m_InstitutedirectorymappingRepo.saveAll(instuteDiractoty);
	return data;
}

@Override
public M_Institutedirectorymapping deleteInstituteDirectoryData(Integer instituteDirMapID) {
	M_Institutedirectorymapping data=m_InstitutedirectorymappingRepo.getdata(instituteDirMapID);
	return data;
}

@Override
public M_Institutedirectorymapping setdeletedData(M_Institutedirectorymapping deleteinstutesubDirectorydata) {
	M_Institutedirectorymapping	data=m_InstitutedirectorymappingRepo.save(deleteinstutesubDirectorydata);
	return data;
}

@Override
public ArrayList<M_Institutedirectorymapping> getInstituteDirectoryData(Integer instituteSubDirectoryID) {
	
	
	//ArrayList<M_Institutedirectorymapping> getedData=m_InstitutedirectorymappingRepo.getMappingData(instituteSubDirectoryID);
	
		ArrayList<M_Institutedirectorymapping> stateServiceMappings = new ArrayList<M_Institutedirectorymapping>();
		ArrayList<Object[]> resultSet = m_InstitutedirectorymappingRepo.getMappingData(instituteSubDirectoryID);
		for (Object[] objects : resultSet) {
			if (objects != null && objects.length >= 2) {
				stateServiceMappings
						.add(new M_Institutedirectorymapping((Integer) objects[0], (Integer) objects[1], (Integer) objects[2],(Integer)objects[3],(Integer)objects[4],(Boolean)objects[5],(String)objects[6],(String)objects[7],(String)objects[8],(String)objects[9]));
			}

			//logger.debug("for getting service and providerServiceMapid " + resultSet);
		}

		//logger.debug("getting response with serviceid and Spm mapId " + stateServiceMappings);
		return stateServiceMappings;

}
}
