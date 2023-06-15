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
package com.iemr.admin.service.locationmaster;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.M_ProviderServiceAddMapping;
import com.iemr.admin.data.locationmaster.Showofficedetails;
import com.iemr.admin.data.locationmaster.StateServiceMapping1;
import com.iemr.admin.to.locationmaster.LocationTO;

public interface LocationMasterServiceInter {

	ArrayList<StateServiceMapping1> getStateByServiceProviderId(Integer serviceProviderID);

	ArrayList<StateServiceMapping1> getServiceByServiceProviderIdAndStateId(Integer serviceProviderID, Integer stateID);

	public StateServiceMapping1 getAllByMapId(Integer serviceProviderID, Integer stateID, Integer serviceID);

	M_ProviderServiceAddMapping addlocation(M_ProviderServiceAddMapping m_ProviderServiceAddMapping);

	public ArrayList<M_District> getAllDistrictByStateId(Integer stateID);

	public M_ProviderServiceAddMapping editData(Integer pSAddMapID);

	M_ProviderServiceAddMapping saveEditData(M_ProviderServiceAddMapping editdata);

	ArrayList<Showofficedetails> getAlldata();

	ArrayList<M_ProviderServiceAddMapping> addlocation(List<M_ProviderServiceAddMapping> resList);

	ArrayList<StateServiceMapping1> getAllByMapId2(Integer serviceProviderID, Integer stateID, Integer serviceID);

	ArrayList<M_ProviderServiceAddMapping> getlocationByMapid(int tempProSerStatMapID);

	ArrayList<Showofficedetails> getlocationByMapid2(int tempProSerStatMapID);

	ArrayList<StateServiceMapping1> getLocationByServiceId(Integer serviceProviderID, Integer serviceID);

	ArrayList<Showofficedetails> getlocationByMapid1(ArrayList<Integer> data);

	ArrayList<StateServiceMapping1> getLocationBySateID(Integer serviceProviderID, Integer stateID);


	//ArrayList<Showofficedetails> getOfficeName(LocationTO feedbackTypedata);

	ArrayList<Showofficedetails> getOfficeName(ArrayList<Showofficedetails> data1);

	ArrayList<StateServiceMapping1> getStatesByServiceId(Integer serviceID,Integer serviceProviderID);
	
	
	ArrayList<StateServiceMapping1> getAllByMapId3(Integer serviceProviderID, Integer serviceID);

	ArrayList<Showofficedetails> getlocationByMapid4(Integer tempProSerStatMapID, Integer districtID);


}
