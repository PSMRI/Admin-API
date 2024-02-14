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
package com.iemr.admin.service.userParkingPlaceMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.userParkingPlaceMap.M_UserParkingPlaceMap;
import com.iemr.admin.data.userParkingPlaceMap.M_UserVanMapping;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repository.userParkingPlaceMap.UserParkingPlaceMapRepository;
import com.iemr.admin.repository.userParkingPlaceMap.UserVanMappingRepository;

@Service
public class UserParkingPlaceMapServiceImpl implements UserParkingPlaceMapService {

	@Autowired
	private UserParkingPlaceMapRepository userParkingPlaceMapRepository;

	@Autowired
	private UserVanMappingRepository userVanMappingRepository;

	@Autowired
	private EmployeeMasterRepo employeeMasterRepo;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ArrayList<M_UserParkingPlaceMap> saveUserParkingPlaceDetails(
			List<M_UserParkingPlaceMap> userParkingPlaceMaster) {
		ArrayList<M_UserParkingPlaceMap> allData = (ArrayList<M_UserParkingPlaceMap>) userParkingPlaceMapRepository
				.saveAll(userParkingPlaceMaster);

		ArrayList<M_UserVanMapping> usrvanmaplist = new ArrayList();
		for (M_UserParkingPlaceMap usrppmap : allData) {
			for (M_UserVanMapping usrvanmap : usrppmap.getUservanmapping()) {
				usrvanmap.setUserParkingPlaceMapID(usrppmap.getUserParkingPlaceMapID());
				usrvanmap.setCreatedBy(usrppmap.getCreatedBy());
				usrvanmap.setProviderServiceMapID(usrppmap.getProviderServiceMapID());
				usrvanmaplist.add(usrvanmap);
			}
		}
		userVanMappingRepository.saveAll(usrvanmaplist);
		return allData;
	}

	@Override
	public ArrayList<M_UserParkingPlaceMap> getUserParkingPlaceMappings(Integer serviceProviderID, Integer stateID,
			Integer districtID, Integer parkingPlaceID, Integer designationID) {

		String stateId = "%%";
		if (null != stateID) {
			stateId = stateID + "";
		}

		String districtId = "%%";
		if (null != districtID) {
			districtId = districtID + "";
		}

		String parkingPlaceId = "%%";
		if (null != parkingPlaceID) {
			parkingPlaceId = parkingPlaceID + "";
		}

		String designationId = "%%";
		if (null != designationID) {
			designationId = designationID + "";
		}

		ArrayList<M_UserParkingPlaceMap> userParkingPlaces = new ArrayList<M_UserParkingPlaceMap>();

		List<Objects[]> allData = userParkingPlaceMapRepository.getUserParkingPlaceMappings(serviceProviderID, stateId,
				districtId, parkingPlaceId, designationId);

		for (Object[] objects : allData) {

			userParkingPlaces
					.add(new M_UserParkingPlaceMap((Integer) objects[0], (Integer) objects[1], (String) objects[2],
							(String) objects[3], (String) objects[4], (Integer) objects[5], (String) objects[6],
							(String) objects[7], (Short) objects[8], (String) objects[9], (Integer) objects[10],
							(String) objects[11], (Integer) objects[12], (String) objects[13], (Integer) objects[14],
							(String) objects[15], (String) objects[16], (Integer) objects[17], (Boolean) objects[18]));

		}
		return userParkingPlaces;

	}

	@Override
	public int updateUserParkingPlaceMapStatus(M_UserParkingPlaceMap userParkingPlaceMaster) {
		int response = userParkingPlaceMapRepository.updateUserParkingPlaceMapStatus(
				userParkingPlaceMaster.getUserParkingPlaceMapID(), userParkingPlaceMaster.getDeleted(),
				userParkingPlaceMaster.getModifiedBy());
		return response;
	}

	@Override
	public M_UserParkingPlaceMap getUserParkingPlaceMapByID(Integer userParkingPlaceMapID) {
		M_UserParkingPlaceMap allData = userParkingPlaceMapRepository.getUserParkingPlaceMapByID(userParkingPlaceMapID);

		return allData;
	}

	public ArrayList<M_UserParkingPlaceMap> getUserParkingPlaceMappings1(Integer providerServiceMapID,
			Integer districtID, Integer parkingPlaceID, Integer designationID) {

		ArrayList<M_UserParkingPlaceMap> userParkingPlaces = new ArrayList<M_UserParkingPlaceMap>();

		List<Objects[]> allData = userParkingPlaceMapRepository.getUserParkingPlaceMappings1(providerServiceMapID,
				parkingPlaceID, designationID);

		for (Object[] objects : allData) {

			userParkingPlaces.add(new M_UserParkingPlaceMap((Integer) objects[0], (Integer) objects[1],
					(String) objects[2], (String) objects[3], (String) objects[4], (Integer) objects[5],
					(Integer) objects[6], (Integer) objects[7], (String) objects[8], (Integer) objects[9],
					(Boolean) objects[10], (Boolean) objects[11], (String) objects[12]));

		}
		return userParkingPlaces;
	}

	public ArrayList<M_UserParkingPlaceMap> saveUserParkingPlaceDetails1(
			List<M_UserParkingPlaceMap> parkingPlaceMaster) {
		ArrayList<M_UserParkingPlaceMap> data = (ArrayList<M_UserParkingPlaceMap>) userParkingPlaceMapRepository
				.saveAll(parkingPlaceMaster);
		return data;
	}

	public M_UserParkingPlaceMap getUserParkingPlaceDetails(Integer userParkingPlaceMapID) {
		M_UserParkingPlaceMap data = userParkingPlaceMapRepository.findByUserParkingPlaceMapID(userParkingPlaceMapID);
		return data;
	}

	public M_UserParkingPlaceMap saveediteddata(M_UserParkingPlaceMap userMapping) {

		M_UserParkingPlaceMap data = userParkingPlaceMapRepository.save(userMapping);
		return data;
	}

	public M_UserParkingPlaceMap saveediteddata(M_UserParkingPlaceMap userMapping, List<M_UserVanMapping> list) {
		M_UserParkingPlaceMap data = userParkingPlaceMapRepository.save(userMapping);

		userVanMappingRepository.deactivatebyuserparkingplaceid(data.getUserParkingPlaceMapID(), data.getModifiedBy());

		ArrayList<M_UserVanMapping> usrvanmaplist = new ArrayList();

		for (M_UserVanMapping usrvanmap : list) {
			usrvanmap.setUserParkingPlaceMapID(data.getUserParkingPlaceMapID());
			usrvanmap.setCreatedBy(data.getModifiedBy());
			usrvanmap.setProviderServiceMapID(data.getProviderServiceMapID());
			usrvanmap.setUserParkingPlaceMap(null);
			usrvanmaplist.add(usrvanmap);
		}

		data.setUservanmapping((List<M_UserVanMapping>) userVanMappingRepository.saveAll(usrvanmaplist));

		return data;
	}

	public List<M_User1> getunmappedUser(Integer providerServiceMapID, Integer designationID) {
		// TODO Auto-generated method stub
		List<Integer> allids = userParkingPlaceMapRepository.getmappedids(providerServiceMapID, designationID);
		List<Object[]> user = new ArrayList<>();
		if (allids.size() > 0) {
			user = employeeMasterRepo.getAllEmpByProviderServiceMapIDAndDesignationNotInUserID(providerServiceMapID,
					designationID, allids);
		} else {
			user = employeeMasterRepo.getAllEmpByProviderServiceMapIDAndDesignation(providerServiceMapID,
					designationID);
		}
		List<M_User1> userpp = new ArrayList<>();
		for (Object[] s : user) {
			userpp.add(new M_User1((Integer) s[0], (String) s[1]));
		}
		return userpp;
	}

	public Boolean getuserexist(Integer providerServiceMapID, Integer userID) {
		// TODO Auto-generated method stub
		return (userParkingPlaceMapRepository.findByProviderServiceMapIDAndUserIDAndDeleted(providerServiceMapID,
				userID, false)).size() > 0;
	}

	public List<M_UserVanMapping> getuservanmapping(Integer userParkingPlacesID) {
		// TODO Auto-generated method stub
		return userVanMappingRepository.findByUserParkingPlaceMapIDAndDeleted(userParkingPlacesID);
	}

	public void deleteuservanmapping(M_UserVanMapping userpvanmapID) {
		// TODO Auto-generated method stub
		userVanMappingRepository.deleteUservanMap(userpvanmapID.getUserVanMapID(), userpvanmapID.getModifiedBy());
	}

}
