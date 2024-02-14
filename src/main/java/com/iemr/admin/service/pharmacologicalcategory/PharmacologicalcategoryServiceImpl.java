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
package com.iemr.admin.service.pharmacologicalcategory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.manufacturer.M_Manufacturer;
import com.iemr.admin.data.pharmacologicalcategory.M_Pharmacologicalcategory;
import com.iemr.admin.repo.pharmacologicalcategory.PharmacologicalcategoryRepo;

@Service
public class PharmacologicalcategoryServiceImpl implements PharmacologicalcategoryInter {
	
	@Autowired
	private PharmacologicalcategoryRepo pharmacologicalcategoryRepo;

	@Override
	public ArrayList<M_Pharmacologicalcategory> createPharmacologicalcategory(
			List<M_Pharmacologicalcategory> createPharmacologicaldata) {
		ArrayList<M_Pharmacologicalcategory> data=(ArrayList<M_Pharmacologicalcategory>) pharmacologicalcategoryRepo.saveAll(createPharmacologicaldata);
		if(data.size()>0)
			return data;
		else	
		return null;
	}

	@Override
	public ArrayList<M_Pharmacologicalcategory> getPharmacologicalcategory(Integer providerServiceMapID) {
		ArrayList<M_Pharmacologicalcategory> getData=pharmacologicalcategoryRepo.getPhormacologicalData(providerServiceMapID);
		return getData;
	}

	@Override
	public M_Pharmacologicalcategory editPharmacologicalcategory(Integer pharmCategoryID) {
		M_Pharmacologicalcategory editData=pharmacologicalcategoryRepo.editPhamacologicalData(pharmCategoryID);
		
		return editData;
	}

	@Override
	public M_Pharmacologicalcategory saveEditedPharData(M_Pharmacologicalcategory saveData) {
		M_Pharmacologicalcategory saveDataphar=pharmacologicalcategoryRepo.save(saveData);
		return saveDataphar;
	}

	@Override
	public Boolean checkPharmacologicalcategoryCode(M_Pharmacologicalcategory pharmacologicalcategory) {
		// TODO Auto-generated method stub
		List<M_Pharmacologicalcategory> manuList=pharmacologicalcategoryRepo.findByPharmCategoryCodeAndProviderServiceMapID(pharmacologicalcategory.getPharmCategoryCode() ,pharmacologicalcategory.getProviderServiceMapID());
		if(manuList.size()>0)
			return true;
		return false;
	}

}
