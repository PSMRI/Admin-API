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
package com.iemr.admin.service.uom;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.uom.M_Uom;
import com.iemr.admin.repo.uom.UomRepo;

@Service
public class UomServiceImpl implements UomInter {
	
	@Autowired
	private UomRepo uomRepo;

	@Override
	public ArrayList<M_Uom> createDrugtypeData(List<M_Uom> saveUomData) {
		
		ArrayList<M_Uom> saveData=(ArrayList<M_Uom>) uomRepo.save(saveUomData);
		if(saveData.size()>0)
			return saveData;
		return null;
	}

	@Override
	public ArrayList<M_Uom> createDrugtypeData(Integer providerServiceMapID) {
		ArrayList<M_Uom> getUomData=uomRepo.getUom(providerServiceMapID);
//		if(getUomData.size()>0)
		return getUomData;
//		else
//			return null;
	}

	@Override
	public M_Uom editDrugtypeData(Integer uOMID) {
		M_Uom geteditedData=uomRepo.geteditedData(uOMID);
		return geteditedData;
	}

	@Override
	public M_Uom saveeditedData(M_Uom geteditedData) {
		M_Uom saveeditedData=uomRepo.save(geteditedData);
		return saveeditedData;
	}

	@Override
	public Boolean checkUomCode(M_Uom uomData) {
		// TODO Auto-generated method stub
		List<M_Uom> uomDatalist=uomRepo.findByUOMCodeAndProviderServiceMapID(uomData.getuOMCode(),uomData.getProviderServiceMapID());
		if(uomDatalist.size()>0)
			return true;
		return false;
	}
	

}
