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
package com.iemr.admin.service.employeemaster;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.employeemaster.M_Designation;
import com.iemr.admin.repo.employeemaster.M_DesignationRepo;

@Service
public class M_DesignationImpl  implements M_DesignationInter{
	@Autowired
	private M_DesignationRepo  m_DesignationRepo;

	@Override
	public ArrayList<M_Designation> getDesinationlist() {
		ArrayList<M_Designation> getlist=m_DesignationRepo.getDesinationlist();
		return getlist ;
	}

}
