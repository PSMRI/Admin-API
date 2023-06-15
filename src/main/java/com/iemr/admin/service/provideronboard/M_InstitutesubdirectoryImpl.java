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

import com.iemr.admin.data.provideronboard.M_Institutedirectory;
import com.iemr.admin.data.provideronboard.M_Institutesubdirectory;
import com.iemr.admin.repository.provideronboard.M_InstitutesubdirectoryRepo;

@Service
public class M_InstitutesubdirectoryImpl implements M_InstitutesubdirectoryInter  {
	
	@Autowired
	private M_InstitutesubdirectoryRepo m_InstitutesubdirectoryRepo;

	
	
	@Override
	public ArrayList<M_Institutesubdirectory> getInstutesubDirectory(Integer instituteDirectoryID,
			Integer providerServiceMapId) {
		ArrayList<M_Institutesubdirectory> data=m_InstitutesubdirectoryRepo.getInstutesubDirectory(instituteDirectoryID,providerServiceMapId);
		return data;
	}



	@Override
	public ArrayList<M_Institutesubdirectory> CreateInstutesubDirectory(
			List<M_Institutesubdirectory> instuteSubDiractoty) {
		ArrayList<M_Institutesubdirectory> data=(ArrayList<M_Institutesubdirectory>) m_InstitutesubdirectoryRepo.save(instuteSubDiractoty);
		return data;
	}



	@Override
	public M_Institutesubdirectory editInstutesubDirectory(Integer instituteSubDirectoryID) {
		M_Institutesubdirectory data=m_InstitutesubdirectoryRepo.editInstutesubDirectory(instituteSubDirectoryID);
		return data;
	}



	@Override
	public M_Institutesubdirectory saveEditedData(M_Institutesubdirectory getinstutesubDirectoryediteddata) {
		M_Institutesubdirectory data=m_InstitutesubdirectoryRepo.save(getinstutesubDirectoryediteddata);
		return data;
	}

	

}
