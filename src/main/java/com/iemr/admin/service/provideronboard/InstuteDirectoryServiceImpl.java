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
import com.iemr.admin.repository.provideronboard.InstuteDirectoryRepo;


@Service
public class InstuteDirectoryServiceImpl implements InstuteDirectoryInter{
	@Autowired
	private InstuteDirectoryRepo instuteDirectoryRepo;

	@Override
	public ArrayList<M_Institutedirectory> createInstuteDirectory(List<M_Institutedirectory> instuteDiractoty) {
		ArrayList<M_Institutedirectory> data=(ArrayList<M_Institutedirectory>) instuteDirectoryRepo.saveAll(instuteDiractoty);
		return data;
	}

	@Override
	public ArrayList<M_Institutedirectory> getInstuteDirectory(Integer providerServiceMapId) {
		ArrayList<M_Institutedirectory> data=instuteDirectoryRepo.getInstuteDirectory(providerServiceMapId);
		return data;
	}

	@Override
	public M_Institutedirectory editInstuteDirectory(Integer instituteDirectoryID) {
		M_Institutedirectory data=instuteDirectoryRepo.editInstuteDirectory(instituteDirectoryID);
		return data;
	}

	@Override
	public M_Institutedirectory editdata(M_Institutedirectory getinstuteDirectorydata) {
		M_Institutedirectory data=instuteDirectoryRepo.save(getinstuteDirectorydata);
		return data;
	}


 
}
