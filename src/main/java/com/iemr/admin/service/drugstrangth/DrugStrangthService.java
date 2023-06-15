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
package com.iemr.admin.service.drugstrangth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.drugstrangth.M_104DrugStrength;
import com.iemr.admin.repo.blocking.DrugStrangthRepo;

@Service
public class DrugStrangthService implements DrugStrangthInter{

	@Autowired
	private DrugStrangthRepo drugStrangthRepo;

	@Override
	public ArrayList<M_104DrugStrength> createDrugStrangth(List<M_104DrugStrength> saveDrugStrangth) {
		ArrayList<M_104DrugStrength> data=(ArrayList<M_104DrugStrength>) drugStrangthRepo.save(saveDrugStrangth);
		return data;
	}

	@Override
	public ArrayList<M_104DrugStrength> getDrugStrangth() {
		ArrayList getdata=(ArrayList) drugStrangthRepo.findAll();
		return getdata;
	}

	@Override
	public M_104DrugStrength updateDrugStrangth(Integer drugStrengthID) {
		M_104DrugStrength getdataforupdate=drugStrangthRepo.findOne(drugStrengthID);
		return getdataforupdate;
	}

	@Override
	public M_104DrugStrength saveupdatedData(M_104DrugStrength getdata) {
		M_104DrugStrength savedataforupdate=drugStrangthRepo.save(getdata);
		return savedataforupdate;
	}
	
	
}
