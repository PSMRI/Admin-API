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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.provideronboard.M_UserservicerolemappingForRole;
import com.iemr.admin.data.provideronboard.ServiceProvider_Model;
import com.iemr.admin.data.provideronboard.V_Showprovideradmin;
import com.iemr.admin.exceptionhandler.DataNotFound;
import com.iemr.admin.repository.provideronboard.IemrServiceRepository1;
import com.iemr.admin.repository.provideronboard.M_ProviderServiceMappingRepo;
import com.iemr.admin.repository.provideronboard.M_UserservicerolemappingForRoleRepo;
import com.iemr.admin.repository.provideronboard.V_ShowprovideradminRepo;

@Service
public class ServiceProvider_ServiceImpl implements ServiceProvider_Service {
	
	private Logger logger = LoggerFactory.getLogger(ServiceProvider_ServiceImpl.class);

	/**
	 * Author: Neeraj (298657); Date: 09-07-2017; Purpose: create Provider
	 */
	
	
	@Autowired
	private V_ShowprovideradminRepo v_ShowprovideradminRepo;
	
	@Autowired
	private M_UserservicerolemappingForRoleRepo m_UserservicerolemappingForRoleRepo;

	@Autowired
	private IemrServiceRepository1 iemrServiceRepository1;

	public void setIemrServiceRepository1(IemrServiceRepository1 iemrServiceRepository1) {
		this.iemrServiceRepository1 = iemrServiceRepository1;
	}

	@Override
	public Integer createProvider(Set<ServiceProvider_Model> serviceProvider_Model) {
		ArrayList<ServiceProvider_Model> objList = null;
		logger.info("Inside creating provider.....");
		objList = (ArrayList<ServiceProvider_Model>) this.iemrServiceRepository1.saveAll(serviceProvider_Model);
		Integer serviceProviderId = objList.get(0).getServiceProviderId();
		logger.info("provider created");
		
		
		return serviceProviderId;
	}

	private M_ProviderServiceMappingRepo m_ProviderServiceMappingRepo;

	@Autowired
	public void setM_ProviderServiceMappingRepo(M_ProviderServiceMappingRepo m_ProviderServiceMappingRepo) {
		this.m_ProviderServiceMappingRepo = m_ProviderServiceMappingRepo;
	}

	@Override
	public List<M_ProviderServiceMapping> mapProviderStateService(
			Set<M_ProviderServiceMapping> m_ProviderServiceMappingSet) {
		List<M_ProviderServiceMapping> sevProMapList = new ArrayList<M_ProviderServiceMapping>();
		sevProMapList = (List<M_ProviderServiceMapping>) this.m_ProviderServiceMappingRepo
				.saveAll(m_ProviderServiceMappingSet);
		
		return sevProMapList;
	}

	public String getProviderName(String serviceProviderName) {
		return iemrServiceRepository1.getProviderName(serviceProviderName);
	}

	public M_ProviderServiceMapping getProviderserviceMapId(Integer providerServiceMapID) {
		M_ProviderServiceMapping data=m_ProviderServiceMappingRepo.getPSMID(providerServiceMapID);
		return data;
	}

	public ArrayList<ServiceProvider_Model> getAllProviderName() {
		
		return iemrServiceRepository1.getAllProviderName();
	}

	public ServiceProvider_Model getProviderData(Integer serviceProviderId) {
		// TODO Auto-generated method stub
		return iemrServiceRepository1.getProviderData(serviceProviderId);
	}

	public ServiceProvider_Model upDateProviderDetails(ServiceProvider_Model getProviderDetails) {
		ServiceProvider_Model data=	iemrServiceRepository1.save(getProviderDetails);
		return data;
	}

	public ArrayList<ServiceProvider_Model> createProvider(List<ServiceProvider_Model> createServiceProvider) {
		
		return (ArrayList<ServiceProvider_Model>) iemrServiceRepository1.saveAll(createServiceProvider);
	}

	public ArrayList<ServiceProvider_Model> createProvider1(List<ServiceProvider_Model> createServiceProvider) {
		if(createServiceProvider.isEmpty())
		throw new DataNotFound("Please Enter valid data"); 
		else{
		ArrayList<ServiceProvider_Model> data=(ArrayList<ServiceProvider_Model>)iemrServiceRepository1.saveAll(createServiceProvider);
		if(data.isEmpty()){
		 throw new DataNotFound("Please Enter valid data");
		}else 
			return data;
	}

	}

	public ArrayList<M_UserservicerolemappingForRole> AddUserRole(List<M_UserservicerolemappingForRole> resList) {
		
		
		ArrayList<M_UserservicerolemappingForRole> data= (ArrayList<M_UserservicerolemappingForRole>) m_UserservicerolemappingForRoleRepo.saveAll(resList);
		return data;
	}

	public M_UserservicerolemappingForRole getPADataForEdit(Integer uSRMappingID) {
		M_UserservicerolemappingForRole getData=m_UserservicerolemappingForRoleRepo.findByUSRMappingID(uSRMappingID);
		return getData;
	}

	public M_UserservicerolemappingForRole insertEditedData(M_UserservicerolemappingForRole getdataforedit) {
	
		
		M_UserservicerolemappingForRole data=m_UserservicerolemappingForRoleRepo.save(getdataforedit);
		return data;
	}

	public ArrayList<V_Showprovideradmin> getProviderAdmins() {
		ArrayList<V_Showprovideradmin> data=v_ShowprovideradminRepo.getAllProviderAdmin();
		return data;
	}
}
