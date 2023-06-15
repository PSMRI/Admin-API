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
package com.iemr.admin.service.servicePoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.servicePoint.M_Servicepoint;
import com.iemr.admin.data.servicePoint.M_Servicepointvillagemap;
import com.iemr.admin.repo.locationmaster.DistrictBranchMappingRepo;
import com.iemr.admin.repository.servicePoint.ServicePointRepository;
import com.iemr.admin.repository.servicePoint.ServicePointVillageMapRepository;

@Service
public class ServicePointServiceImpl implements servicePointService {

	@Autowired
	private ServicePointRepository servicePointRepository;
	
	@Autowired
	private DistrictBranchMappingRepo districtBranchMappingRepo;
	
	@Autowired
	private ServicePointVillageMapRepository servicePointVillageMapRepository;
	
	@Override
	public ArrayList<M_Servicepoint> saveServicePoint(List<M_Servicepoint> ServicePoints) {
		ArrayList<M_Servicepoint>  allData=(ArrayList<M_Servicepoint>) servicePointRepository.save(ServicePoints);
		return allData;
	}

	@Override
	public ArrayList<M_Servicepoint> getAvailableServicePoints(Integer stateID, Integer districtID, Integer parkingPlaceID, Integer serviceProviderID) {
		String stateId= "%%";
		if(null!=stateID){
			stateId = stateID+"";
		}
		
		String districtId= "%%";
		if(null!=districtID){
			districtId = districtID+"";
		}
		
		String parkingPlaceId= "%%";
		if(null!=parkingPlaceID){
			parkingPlaceId = parkingPlaceID+"";
		}
		
		ArrayList<M_Servicepoint> servicePointList = new ArrayList<M_Servicepoint>();	
		
		
		List<Objects[]>  allData=servicePointRepository.getAvailableServicePoints(stateId, districtId, parkingPlaceId, serviceProviderID);
		
		for (Object[] objects : allData) {

			
			
			servicePointList.add(new M_Servicepoint((Integer)objects[0], (String)objects[1], (String)objects[2], (String)objects[3], (Integer)objects[4], (Boolean)objects[5], 
					(Integer)objects[6], (String)objects[7],(Integer)objects[8], (String)objects[9],(Integer)objects[10], (String)objects[11],(Integer)objects[12],
						(String)objects[13],(Integer)objects[14], (String)objects[15],(M_ProviderServiceMapping)objects[16],(Integer)objects[17], (String)objects[18], (Integer)objects[19], (String)objects[20]));
			
		}
		return servicePointList;
	}

	@Override
	public int updateServicePointStatus(M_Servicepoint m_ServicePoint) {
		int response = servicePointRepository.updateServicePointStatus(m_ServicePoint.getServicePointID(), m_ServicePoint.getDeleted(), m_ServicePoint.getModifiedBy());
		return response;
	}

	@Override
	public ArrayList<M_Servicepointvillagemap> saveServicePointVillageMap(
			List<M_Servicepointvillagemap> Servicepointvillagemaps) {
		ArrayList<M_Servicepointvillagemap>  allData=(ArrayList<M_Servicepointvillagemap>) servicePointVillageMapRepository.save(Servicepointvillagemaps);
		return allData;
	}

	@Override
	public ArrayList<M_Servicepointvillagemap> getAvailableServicePointVillageMaps(Integer stateID, Integer districtID,
			Integer parkingPlaceID, Integer servicePointID, Integer serviceProviderID) {
		String stateId= "%%";
		if(null!=stateID){
			stateId = stateID+"";
		}
		
		String districtId= "%%";
		if(null!=districtID){
			districtId = districtID+"";
		}
		
		String parkingPlaceId= "%%";
		if(null!=parkingPlaceID){
			parkingPlaceId = parkingPlaceID+"";
		}
		
		String servicePointId= "%%";
		if(null!=servicePointID){
			servicePointId = servicePointID+"";
		}
		
		ArrayList<M_Servicepointvillagemap> servicepointvillagemapList = new ArrayList<M_Servicepointvillagemap>();		
		
		List<Objects[]>  allData=servicePointVillageMapRepository.getAvailableServicePointVillageMaps(stateId, districtId, parkingPlaceId, servicePointId, serviceProviderID);
		
		for (Object[] objects : allData) {
			
			servicepointvillagemapList.add(new M_Servicepointvillagemap((Integer)objects[0], (Integer)objects[1], (String)objects[2], (Integer)objects[3],
					(String)objects[4],(Integer)objects[5], (String)objects[6], (Integer)objects[7], (String)objects[8],(Integer)objects[9], 
					(String)objects[10],(Integer)objects[11], (Boolean)objects[12],(Integer)objects[15],(String)objects[16]));
			
		}
		return servicepointvillagemapList;
	}

	@Override
	public int updateServicePointVillageMapStatus(M_Servicepointvillagemap m_servicepointvillagemap) {
		int response = servicePointVillageMapRepository.updateServicePointStatus(m_servicepointvillagemap.getServicePointVillageMapID(), m_servicepointvillagemap.getDeleted(), m_servicepointvillagemap.getModifiedBy());
		return response;
	}

	public M_Servicepoint getdataForEditServicePointStatus(Integer servicePointID) {
		M_Servicepoint data=servicePointRepository.findOne(servicePointID);
		return data;
	}

	public M_Servicepoint saveeditedData(M_Servicepoint data) {
		M_Servicepoint data1=servicePointRepository.save(data);
		return data1;
	}

	public M_Servicepointvillagemap updateServicePointVillageMapStatus(Integer servicePointVillageMapID) {
		M_Servicepointvillagemap data=servicePointVillageMapRepository.findOne(servicePointVillageMapID);
		return data;
	}

	public M_Servicepointvillagemap saveEditedData(M_Servicepointvillagemap getEditedData) {
		M_Servicepointvillagemap data=servicePointVillageMapRepository.save(getEditedData);
		return data;
	}

	public List<DistrictBranchMapping> getunmappedvillages(Integer providerServiceMapID, Integer districtBlockID) {
		// TODO Auto-generated method stub
		List<Integer> intids=servicePointVillageMapRepository.finbyTalukID( providerServiceMapID);
		 List<DistrictBranchMapping> output=new ArrayList<>();
		 if(intids.size()>0){
			 output=districtBranchMappingRepo.getunmappedvillage(intids,districtBlockID);
		 }else{
			 output=districtBranchMappingRepo.getallvillage(districtBlockID);
		 }
		return output;
	}
	

}
