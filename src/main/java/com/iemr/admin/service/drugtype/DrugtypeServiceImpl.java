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
package com.iemr.admin.service.drugtype;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.drugtype.M_Drugtype;
import com.iemr.admin.repo.drugtype.DrugtypeRepo;

@Service
public class DrugtypeServiceImpl implements DrugtypeInter{
	
	@Autowired
	private DrugtypeRepo drugtypeRepo;

	@Override
	public ArrayList<M_Drugtype> createDrugtypeData(List<M_Drugtype> drugtypeData) {
		
		ArrayList<M_Drugtype> savedData=(ArrayList<M_Drugtype>) drugtypeRepo.save(drugtypeData);
		
		if(savedData.size()>0)
			return savedData;
		else
		return null;
	}

	@Override
	public ArrayList<M_Drugtype> getDrugtypeData(Integer providerServiceMapID) {
		ArrayList<M_Drugtype> getDrugtypeData=drugtypeRepo.getDrugtypeData(providerServiceMapID);
//		if(getDrugtypeData.size()>0)
			return getDrugtypeData;
//		return null;
	}

	@Override
	public M_Drugtype editDrugtypeData(Integer drugTypeID) {
		
		M_Drugtype sendeditedData=drugtypeRepo.geteditedData(drugTypeID);
		return sendeditedData;
	}

	@Override
	public M_Drugtype saveeditDrugtype(M_Drugtype geteditedData) {
		M_Drugtype updateeditedData=drugtypeRepo.save(geteditedData);
		return updateeditedData;
	}

}
