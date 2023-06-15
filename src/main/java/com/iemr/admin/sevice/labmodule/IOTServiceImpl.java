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
package com.iemr.admin.sevice.labmodule;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.labmodule.IOTComponent;
import com.iemr.admin.data.labmodule.IOTProcedure;
import com.iemr.admin.repo.labmodule.IOTRepo;

@Service
public class IOTServiceImpl implements IOTService{

	@Autowired
	IOTRepo iotRepo;
	
	@Override
	public String getIOTProcedure()
	{
		ArrayList<IOTProcedure> list = iotRepo.getIOTProcedure();
		return list.toString();
	}
	
	@Override
	public String getIOTComponent()
	{
		ArrayList<IOTComponent> list = iotRepo.getIOTComponent();
		return list.toString();
	}
}
