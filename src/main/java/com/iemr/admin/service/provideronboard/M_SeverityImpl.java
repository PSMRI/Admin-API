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

import com.iemr.admin.data.provideronboard.M_Severity;
import com.iemr.admin.repository.provideronboard.M_SeverityRepo;



@Service
public class M_SeverityImpl implements M_SeverityInter{
	
   @Autowired
   private 	M_SeverityRepo M_ServerityRepo;

@Override
public ArrayList<M_Severity> getServerity(Integer providerServiceMapID) {
	ArrayList<M_Severity>	data=M_ServerityRepo.getAllServerity(providerServiceMapID);
	return data;
}

@Override
public ArrayList<M_Severity> saveServerity(List<M_Severity> serveritydata) {
	ArrayList<M_Severity> saveSerdata=(ArrayList<M_Severity>) M_ServerityRepo.save(serveritydata);
	return saveSerdata;
}

@Override
public M_Severity getDataByServId(Integer severityID) {
	M_Severity editdata=M_ServerityRepo.editServerity(severityID);
	return editdata;
}

@Override
public M_Severity deletedataser(M_Severity getDAta) {
	M_Severity deleted=M_ServerityRepo.save(getDAta);
	return deleted;
}


}

