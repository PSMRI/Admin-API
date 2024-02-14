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
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.M_ProviderServiceAddMapping;
import com.iemr.admin.data.locationmaster.Showofficedetails;
import com.iemr.admin.data.locationmaster.StateServiceMapping1;
import com.iemr.admin.data.provideronboard.M_Feedbacktype;
import com.iemr.admin.repo.locationmaster.LocationMasterRepo;
import com.iemr.admin.repo.locationmaster.M_ProviderServiceAddMappingRepo;
import com.iemr.admin.repo.locationmaster.MdistrictRepo;
import com.iemr.admin.repo.locationmaster.ShowofficedetailsRepo;
import com.iemr.admin.to.locationmaster.LocationTO;

@Service
public class LocationMasterServiceImpl implements LocationMasterServiceInter{
	
	private Logger logger = LoggerFactory.getLogger(LocationMasterServiceImpl.class);
	
	
	@Autowired
	private ShowofficedetailsRepo showofficedetailsRepo;
	
	@Autowired
	private MdistrictRepo mdistricRepo;
	
	
	@Autowired
	private M_ProviderServiceAddMappingRepo m_ProviderServiceAddMappingRepo;
	
	@Autowired
	private LocationMasterRepo locationMasterRepo;

	@Override
	public ArrayList<StateServiceMapping1> getStateByServiceProviderId(Integer serviceProviderID) {
		ArrayList<StateServiceMapping1 > stateServiceMappings = new ArrayList<StateServiceMapping1>();
		ArrayList<Object[]>resultSet =  locationMasterRepo.getStateByServiceProviderId(serviceProviderID);
		for (Object[] objects : resultSet) {
			if (objects!=null && objects.length>=2) {
				stateServiceMappings.add(new StateServiceMapping1((Integer)objects[0], (String)objects[1],(Integer)objects[2],(Integer)objects[3]));
			}
		}
		return stateServiceMappings;
	}

	@Override
	public ArrayList<StateServiceMapping1> getServiceByServiceProviderIdAndStateId(Integer serviceProviderID,
			Integer stateID) {
		ArrayList<StateServiceMapping1 > stateServiceMappings = new ArrayList<StateServiceMapping1>();
		ArrayList<Object[]>resultSet =  locationMasterRepo.getServiceByServiceProviderIdAndStateId(serviceProviderID, stateID);
		for (Object[] objects : resultSet) {
			if (objects!=null && objects.length>=2) {
				stateServiceMappings.add(new StateServiceMapping1((Integer)objects[0], (Integer)objects[1],(String)objects[2]));
			}
		}
		return stateServiceMappings; 
	}

	@Override
	public StateServiceMapping1 getAllByMapId(Integer serviceProviderID, Integer stateID, Integer serviceID) {
		ArrayList<Integer > stateServiceMappings = new ArrayList<Integer>();
		StateServiceMapping1 data=locationMasterRepo.getProviderServiceMapID(serviceProviderID,stateID,serviceID);
		return data ;
	}

	@Override
	public M_ProviderServiceAddMapping addlocation(M_ProviderServiceAddMapping m_ProviderServiceAddMapping) {
		M_ProviderServiceAddMapping res=m_ProviderServiceAddMappingRepo.save(m_ProviderServiceAddMapping);
		return res;
	}

	@Override
	public ArrayList<M_District> getAllDistrictByStateId(Integer stateID) {
		
		
		
		ArrayList<M_District> mdistrict= new ArrayList<M_District>();
		ArrayList<M_District> data=mdistricRepo.getAllDistrictByStateId(stateID);
		for(M_District objects:data){
			mdistrict.add(new M_District(objects.getDistrictID(), objects.getDistrictName()));
		}
		
		return mdistrict;
	}

	@Override
	public M_ProviderServiceAddMapping editData(Integer pSAddMapID) {
		return m_ProviderServiceAddMappingRepo.editData(pSAddMapID);
	}

	@Override
	public M_ProviderServiceAddMapping saveEditData(M_ProviderServiceAddMapping editdata) {
		M_ProviderServiceAddMapping data=m_ProviderServiceAddMappingRepo.save(editdata);
		return data;
	}

	@Override
	public ArrayList<Showofficedetails> getAlldata() {
		return showofficedetailsRepo.getAlldata();
	}

	@Override
	public ArrayList<M_ProviderServiceAddMapping> addlocation(List<M_ProviderServiceAddMapping> resList) {
		// TODO Auto-generated method stub
		return (ArrayList<M_ProviderServiceAddMapping>) m_ProviderServiceAddMappingRepo.saveAll(resList);
	}

	@Override
	public ArrayList<StateServiceMapping1> getAllByMapId2(Integer serviceProviderID, Integer stateID, Integer serviceID) {
		ArrayList<StateServiceMapping1> resSet = new ArrayList<StateServiceMapping1>();
		resSet = (ArrayList<StateServiceMapping1>) locationMasterRepo.getAllByMapId2(serviceProviderID, stateID, serviceID);
		logger.debug("sending result"+resSet);
		return resSet;
	}

	@Override
	public ArrayList<M_ProviderServiceAddMapping> getlocationByMapid(int tempProSerStatMapID) {
		ArrayList<M_ProviderServiceAddMapping> reslist=m_ProviderServiceAddMappingRepo.getlocationByMapid(tempProSerStatMapID);
		return reslist;
	}

	@Override
	public ArrayList<Showofficedetails> getlocationByMapid2(int tempProSerStatMapID) {
		ArrayList<Showofficedetails> reslist=showofficedetailsRepo.getlocationByMapid(tempProSerStatMapID);
		return reslist;
	}

	@Override
	public ArrayList<StateServiceMapping1> getLocationByServiceId(Integer serviceProviderID, Integer serviceID) {
		
		
		ArrayList<StateServiceMapping1> getmappingData=locationMasterRepo.getLocationByServiceID(serviceProviderID,serviceID);
		
		
		return getmappingData;
	}

	@Override
	public ArrayList<Showofficedetails> getlocationByMapid1(ArrayList<Integer> data) {
		ArrayList<Showofficedetails> reslist = new ArrayList<Showofficedetails>();
		for(Integer i:data){
			reslist.addAll(showofficedetailsRepo.getlocationByMapid1(i));
		}
		//ArrayList<M_ProviderServiceAddMapping> reslist=m_ProviderServiceAddMappingRepo.getlocationByMapid1(data);
		//ArrayList<M_ProviderServiceAddMapping> reslist=(ArrayList<M_ProviderServiceAddMapping>) m_ProviderServiceAddMappingRepo.findAll(data);
		return reslist;
	}

	@Override
	public ArrayList<StateServiceMapping1> getLocationBySateID(Integer serviceProviderID, Integer stateID) {
		ArrayList<StateServiceMapping1> getmappingData=locationMasterRepo.getLocationByStateID(serviceProviderID,stateID);
		return getmappingData;
	}

	/*@Override
	public ArrayList<Showofficedetails> getOfficeName(LocationTO feedbackTypedata) {
		ArrayList<Showofficedetails> reslist=new ArrayList<Showofficedetails>();
		for(Integer i:feedbackTypedata){
			//Integer data=i.getProviderServiceMapID1();s
			reslist.addAll(showofficedetailsRepo.getOfficeName(i));
		}
		return reslist;
	}*/

	@Override
	public ArrayList<Showofficedetails> getOfficeName(ArrayList<Showofficedetails> data1) {
		ArrayList<Showofficedetails> reslist = new ArrayList<Showofficedetails>();
		for(Showofficedetails i:data1){
			reslist.addAll(showofficedetailsRepo.getOfficeName(i.getProviderServiceMapID()));
		}
		//ArrayList<M_ProviderServiceAddMapping> reslist=m_ProviderServiceAddMappingRepo.getlocationByMapid1(data);
		//ArrayList<M_ProviderServiceAddMapping> reslist=(ArrayList<M_ProviderServiceAddMapping>) m_ProviderServiceAddMappingRepo.findAll(data);
		return reslist;
	}
	
	
	@Override
	public ArrayList<StateServiceMapping1> getStatesByServiceId(Integer serviceID,Integer serviceProviderID) {
		ArrayList<StateServiceMapping1 > stateServiceMappings = new ArrayList<StateServiceMapping1>();
		ArrayList<Object[]>resultSet =  locationMasterRepo.getStatesByServiceId(serviceID,serviceProviderID);
		for (Object[] objects : resultSet) {
			if (objects!=null && objects.length>=2) {
				stateServiceMappings.add(new StateServiceMapping1((Integer)objects[0], (String)objects[1], (Integer)objects[2]));
			}
		}
		return stateServiceMappings;
	}
	
	
	
	@Override
	public ArrayList<StateServiceMapping1> getAllByMapId3(Integer serviceProviderID, Integer serviceID) {
		ArrayList<StateServiceMapping1> resSet = new ArrayList<StateServiceMapping1>();
		resSet = (ArrayList<StateServiceMapping1>) locationMasterRepo.getAllByMapId3(serviceProviderID,serviceID);
		logger.debug("sending result"+resSet);
		return resSet;
	}

	@Override
	public ArrayList<Showofficedetails> getlocationByMapid4(Integer tempProSerStatMapID, Integer districtID) {
		ArrayList<Showofficedetails> reslist=showofficedetailsRepo.getlocationByMapid3(tempProSerStatMapID,districtID);
		return reslist;
	}
	
	
	}

	
	


