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
package com.iemr.admin.service.fetosensemaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.iemr.admin.data.fetosensemaster.FetosenseDeviceID;
import com.iemr.admin.data.fetosensemaster.M_Fetosense;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.repo.fetosensemaster.FetosenseDeviceIDRepo;
import com.iemr.admin.repo.fetosensemaster.FetosenseRepository;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;


@Service
public class FetosenseServiceImpl implements FetosenseService{
	
	@Autowired
	public FetosenseRepository fetosenseRepository;

	@Autowired
	private VanMasterRepository masterVanRepo;

	@Autowired
	private FetosenseDeviceIDRepo fetosenseDeviceIDRepo;

	@Override
	public String createFetosenseTestMaster(String requestOBJ) throws Exception {
		M_Fetosense[] testMasterOBJ = InputMapper.gson().fromJson(requestOBJ, M_Fetosense[].class);
		List<M_Fetosense> testMasterRS = null;
		List<M_Fetosense> alist = Arrays.asList(testMasterOBJ);
		testMasterRS = (List<M_Fetosense>) fetosenseRepository.save(alist);
		if (testMasterRS != null && testMasterOBJ.length == testMasterRS.size())
			return new Gson().toJson(testMasterRS);
		else
			return null;
	}
	
	@Override
	public String getFetosenseTestMaster(Integer psmID) {
		ArrayList<M_Fetosense> procMasterListRS = fetosenseRepository.getByProviderServiceMapID(psmID);
		return new Gson().toJson(procMasterListRS);
	}

	@Override
	public String updateFetosenseTestMaster(String requestOBJ) {
		String returnOBJ = null;
		M_Fetosense fmOBJ = InputMapper.gson().fromJson(requestOBJ, M_Fetosense.class);
		if (fmOBJ != null && fmOBJ.getFetosenseTestID() != null && fmOBJ.getFetosenseTestID() > 0) {
			Integer x = fetosenseRepository.updateFetosenseDetails(fmOBJ.getFetosenseTestID(), fmOBJ.getTestName(),
					fmOBJ.getTestDesc(), fmOBJ.getModifiedBy());
			if (x != null && x == 1) {
				M_Fetosense procedureDetailsRS = fetosenseRepository.getByFetosenseTestID(fmOBJ.getFetosenseTestID());
				returnOBJ = new Gson().toJson(procedureDetailsRS);
			}
		}
		return returnOBJ;
	}
	
	@Override
	public String updateFetosenseTestMasterStatus(Integer fetosenseTestID, Boolean deleted) throws Exception {
		int status = fetosenseRepository.updateFetosenseStatus(fetosenseTestID, deleted);
		if (status > 0) {
			M_Fetosense fetosenseDetailsRS = fetosenseRepository.getByFetosenseTestID(fetosenseTestID);
			return new Gson().toJson(fetosenseDetailsRS);
		} else {
			return null;
		}

	}
	
	/***
	 * @author DU20091017 saves the Feto-sense device ID.
	 */
	@Override
	public int saveFetosenseDeviceID(ArrayList<FetosenseDeviceID> fetosenseData) throws IEMRException {
		// TODO Auto-generated method stub

		try {
			ArrayList<FetosenseDeviceID> deviceIdsaved = (ArrayList<FetosenseDeviceID>) fetosenseDeviceIDRepo
					.save(fetosenseData);
			if (deviceIdsaved.size() > 0 && deviceIdsaved.get(0).getVfdID() > 0)
				return 1;
			else
				throw new IEMRException("Error in saving fetosense device ID");
		} catch (Exception e) {
			throw new IEMRException("Error in saving fetosene deviceID " + e.getMessage());
		}
		
	}

	@Override
	public String getFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException {

		try {
			Map<String, Object> resMap = new HashMap<>();
			ArrayList<FetosenseDeviceID> deviceIDs = fetosenseDeviceIDRepo
					.getFetoseneDeviceID(fetosenseData.getProviderServiceMapID());
			resMap.put("fetosenseDeviceIDs", deviceIDs);
			return new Gson().toJson(resMap);
		} catch (Exception e) {
			throw new IEMRException("Error in getting fetosense DeviceID " + e.getMessage());
		}
		
	}

	@Override
	public int deleteFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException {
		try {
			FetosenseDeviceID deleteResponse = fetosenseDeviceIDRepo.save(fetosenseData);
			if (deleteResponse != null && deleteResponse.getVfdID() > 0) {
				if (deleteResponse.getVanID() != null && deleteResponse.getVanID() > 0) {
					Boolean vanStatus = false;
					if (deleteResponse.getDeleted())
						vanStatus = false;
					else
						vanStatus = true;
					int vanUpdate = masterVanRepo.updateVanFetosenesmapping(vanStatus, deleteResponse.getVanID());
					if (vanUpdate == 0)
						throw new IEMRException("Error in updating the van mapping details");
				}
				return 1;
			} else
				throw new IEMRException("Error in de-activating the device ID");
		} catch (Exception e) {
			throw new IEMRException("Error in deactivating device ID - " + e.getMessage());
		}
		

	}

	/***
	 * @author DU20091017
	 */
	@Override
	public String getvanIDAndFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException {
		try {
			Map<String, Object> resMap = new HashMap<>();
			ArrayList<M_Van> vanDetail = new ArrayList<M_Van>();
			ArrayList<Object[]> vanDetails = masterVanRepo.getVanIDNotMappedWithDevice(fetosenseData.getVanTypeID(),
					fetosenseData.getParkingPlaceID(), fetosenseData.getProviderServiceMapID());
			for (Object[] van : vanDetails) {
				M_Van vanIDAndValue = new M_Van((Integer) van[0], (String) van[1],(String) van[2]);
				vanDetail.add(vanIDAndValue);
			}
			resMap.put("VanIDs", vanDetail);
			ArrayList<FetosenseDeviceID> deviceIDDetails = fetosenseDeviceIDRepo
					.getFetoseneDeviceIDNotMapped(fetosenseData.getProviderServiceMapID());
			resMap.put("deviceIDs", deviceIDDetails);
			return new Gson().toJson(resMap);
		} catch (Exception e) {
			throw new IEMRException("Error in getting vanID and fetosenseID" + e.getMessage());
		}
		
	}

	/***
	 * @author DU20091017
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int vanIDAndDeviceIDMapping(FetosenseDeviceID fetosenseData) throws IEMRException {
		try {
			fetosenseData.setModifiedBy(fetosenseData.getCreatedBy());
			int vanIDUpdated = fetosenseDeviceIDRepo.createMappingOfVanIDAndDeviceID(fetosenseData.getVanID(),
					fetosenseData.getVanTypeID(), fetosenseData.getParkingPlaceID(), fetosenseData.getVanName(),
					fetosenseData.getModifiedBy(), fetosenseData.getDeviceID());
			if (vanIDUpdated > 0) {
				int mappingUpdated = masterVanRepo.updateVanFetosenesmapping(true, fetosenseData.getVanID());
				if (mappingUpdated > 0) {
					return 1;
				} else
					throw new IEMRException("Error in updating mapping for vanID");
			} else
				throw new IEMRException("Error in updating the VanID");
		} catch (Exception e) {
			throw new IEMRException("Error - " + e.getMessage());
		}
		

	}

	/***
	 * @author DU20091017
	 */
	@Override
	public int updateFetosenseDeviceID(FetosenseDeviceID fetosenseData) throws IEMRException {
		try {
			FetosenseDeviceID updateDeviceID = fetosenseDeviceIDRepo.save(fetosenseData);
			if (updateDeviceID != null && updateDeviceID.getVfdID() > 0) {
				return 1;
			} else
				throw new IEMRException("Error in updating the Device ID");
		} catch (Exception e) {
			throw new IEMRException("Error " + e.getMessage());
		}
		
	}

	/***
	 * @author DU20091017
	 */
	@Override
	public String getVanIDMappingWorklist(FetosenseDeviceID fetosenseData) throws IEMRException {
		try {
			ArrayList<FetosenseDeviceID> mappedWorklist = fetosenseDeviceIDRepo.getMappedWorklist(
					fetosenseData.getVanTypeID(), fetosenseData.getParkingPlaceID(),
					fetosenseData.getProviderServiceMapID());
			return new Gson().toJson(mappedWorklist);
		} catch (Exception e) {
			throw new IEMRException("Error in getting vanID mapping worklist" + e.getMessage()); 
		}
		
	}

	/***
	 * @author DU20091017
	 */
	@Override
	public int updatingvanIDAndDeviceIDMapping(FetosenseDeviceID fetosenseData) throws IEMRException {

		int vanDetailsNull = fetosenseDeviceIDRepo.updateVanDetailsToNull(fetosenseData.getVfdID());
		if (vanDetailsNull > 0) {
			try {
				int vanIDUpdated = fetosenseDeviceIDRepo.createMappingOfVanIDAndDeviceID(fetosenseData.getVanID(),
						fetosenseData.getVanTypeID(), fetosenseData.getParkingPlaceID(), fetosenseData.getVanName(),
						fetosenseData.getModifiedBy(), fetosenseData.getDeviceID());
				if (vanIDUpdated > 0) {
					return 1;
				} else
					throw new IEMRException("Error in updating van details");
			} catch (Exception e) {
				throw new IEMRException("Invalid parameters" + e.getMessage());
			}

		} else
			throw new IEMRException("Error in updating the van details to null");
//		return vanDetailsNull;

	}

	/***
	 * @author DU20091017
	 */
	@Override
	public int deleteVanIDAndDeviceIDMapping(FetosenseDeviceID fetosenseData) throws IEMRException {

		try {
			if (!fetosenseData.getDeactivated()) {
				int countOfVan = fetosenseDeviceIDRepo.getMappedVanDetails(fetosenseData.getVanID());
				if (countOfVan > 0) {
					throw new IEMRException("The Van is already mapped with a device");
				}
			}
			int mappingDeleted = fetosenseDeviceIDRepo.deleteMapping(fetosenseData.getDeactivated(),
					fetosenseData.getVfdID());
			if (mappingDeleted > 0) {
				Boolean vanStatus = false;
				if (fetosenseData.getDeactivated())
					vanStatus = false;
				else
					vanStatus = true;
				int vanMappingStatusUpdated = masterVanRepo.updateVanFetosenesmapping(vanStatus,
						fetosenseData.getVanID());
				if (vanMappingStatusUpdated > 0) {
					return 1;
				} else
					throw new IEMRException("Error in updating the van status");
			} else
				throw new IEMRException("Error in updating the mapping");
		} catch (Exception e) {
			throw new IEMRException("Error - " + e.getMessage());
		}

	}
}
