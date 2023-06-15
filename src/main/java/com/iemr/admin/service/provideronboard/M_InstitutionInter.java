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

import com.google.gson.JsonObject;
import com.iemr.admin.data.provideronboard.M_Institution;

public interface M_InstitutionInter {

	ArrayList<M_Institution> getInstution(Integer providerServiceMapID, Integer stateID, Integer districtID,
			Integer blockID);

	ArrayList<M_Institution> createInstution(List<M_Institution> createinstute);

	M_Institution editInstution(Integer institutionID);

	M_Institution saveEditData(M_Institution createInstutionData);

	ArrayList<M_Institution> getInstutionByVillage(Integer providerServiceMapID, Integer stateID, Integer districtID,
			Integer blockID, Integer villageID);

	ArrayList<M_Institution> createInstutionByVillage(List<M_Institution> createinstute);	

	String createInstitutionByFile(JsonObject getInstitutionDetails);
	

}
