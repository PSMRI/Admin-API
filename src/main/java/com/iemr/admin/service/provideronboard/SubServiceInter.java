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

import com.iemr.admin.data.provideronboard.M_Subservice;
import com.iemr.admin.data.provideronboard.M_SubservicemasterPA;



public interface SubServiceInter {

	ArrayList<M_Subservice> saveSubList(List<M_Subservice> resList);

	//ArrayList<M_Subservice> getServiceNameByProviderMapId(Integer providerServiceMapID);

	ArrayList<M_Subservice> getsubServiceName(Integer providerServiceMapID);

	M_Subservice getsubServiceNameById(Integer subServiceID);

	M_Subservice saveupdatedData(M_Subservice getSubService);

	ArrayList<M_SubservicemasterPA> getServiceNameByServiceID(Integer serviceID);

	//ArrayList<M_Subservice> getsubServiceName(Integer providerServiceMapID);

}
