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
package com.iemr.admin.service.blocking;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
import com.iemr.admin.data.blocking.M_Serviceprovider_Blocking;
import com.iemr.admin.data.blocking.M_Status1;
import com.iemr.admin.data.blocking.T_Providerservicemappingdetail;
import com.iemr.admin.data.blocking.T_Serviceproviderdetail;
import com.iemr.admin.data.blocking.T_Userdetail;
import com.iemr.admin.data.blocking.UserForBlocking;
import com.iemr.admin.data.blocking.V_Showproviderservicemapping;

public interface BlockingInter {

	M_Serviceprovider_Blocking getProviderDetailsById(Integer serviceProviderID);

	M_Serviceprovider_Blocking blockServiceProvider(M_Serviceprovider_Blocking getProviderDetails);

	T_Serviceproviderdetail saveData(T_Serviceproviderdetail saveDetails);


	void blockProviderByService(Integer serviceProviderID, Integer stateID,
			Integer serviceID, Integer statusID);

	M_Providerservicemapping_Blocking getProviderServiceMappingDetails(Integer serviceProviderID, Integer stateID,
			Integer serviceID);

	T_Providerservicemappingdetail savetpsdData(T_Providerservicemappingdetail tpsd);

	List<M_Providerservicemapping_Blocking> getProviderStateMappingDetails(Integer serviceProviderID, Integer stateID);

	void blockProviderByState(Integer serviceProviderID, Integer stateID, Integer statusID);

	ArrayList<T_Providerservicemappingdetail> savetpsmd(List<T_Providerservicemappingdetail> resList);

	UserForBlocking getUserDetailByUserId(Integer userID);

	ArrayList<M_Status1> getStatusData();

	void blockUser(Integer userID, Integer statusID);

	T_Userdetail saveUserDetails(T_Userdetail tuserDetails);

	ArrayList<M_Providerservicemapping_Blocking> getProviderStatus(Integer serviceProviderID);

	ArrayList<M_Providerservicemapping_Blocking> getProviderStatusByProviderAndServiceId(Integer serviceProviderID,
			Integer serviceID);


	void blockProvider(Integer serviceProviderID, Integer statusID);

	void blockProviderByProviderIdAndServiceId(Integer serviceProviderID, Integer serviceID, Integer statusID);

	ArrayList<M_Providerservicemapping_Blocking> getServiceLiensUsingProvider(Integer serviceProviderID);

	

	ArrayList<V_Showproviderservicemapping> getProviderStatus1(Integer serviceProviderID);
	ArrayList<V_Showproviderservicemapping> getProviderStatus2(Integer serviceProviderID);

	ArrayList<V_Showproviderservicemapping> getProviderServiceMappingDetails2(Integer serviceProviderID,
			Integer stateID, Integer serviceID);

	ArrayList<V_Showproviderservicemapping> getProviderStateMappingDetails1(Integer serviceProviderID, Integer stateID);

	ArrayList<V_Showproviderservicemapping> getProviderStatusByProviderAndServiceId2(Integer serviceProviderID,
			Integer serviceID);

	ArrayList<M_Providerservicemapping_Blocking> AddServiceProvider(List<M_Providerservicemapping_Blocking> resList);

	public String mapctidata(List<M_Providerservicemapping_Blocking> ctidata);

	ArrayList<M_Providerservicemapping_Blocking> getServiceLiensUsingProvider1(M_Providerservicemapping_Blocking providerDetails);

	M_Providerservicemapping_Blocking getDataByProviderServiceMapId(Integer providerServiceMapID);

	M_Providerservicemapping_Blocking updateProviderData(M_Providerservicemapping_Blocking getProviderData);

	
	

	

	

}
