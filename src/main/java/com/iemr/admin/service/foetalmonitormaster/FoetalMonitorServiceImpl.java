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
package com.iemr.admin.service.foetalmonitormaster;

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
import com.iemr.admin.data.foetalmonitormaster.FoetalMonitorDeviceID;
import com.iemr.admin.data.foetalmonitormaster.M_FoetalMonitor;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.repo.foetalmonitormaster.FoetalMonitorDeviceIDRepo;
import com.iemr.admin.repo.foetalmonitormaster.FoetalMonitorRepository;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;


@Service
public class FoetalMonitorServiceImpl implements FoetalMonitorService{
	
	@Autowired
	public FoetalMonitorRepository foetalMonitorRepository;

	@Autowired
	private VanMasterRepository masterVanRepo;

	@Autowired
	private FoetalMonitorDeviceIDRepo foetalMonitorDeviceIDRepo;

	@Override
	public String createFoetalMonitorTestMaster(String requestOBJ) throws Exception {
		M_FoetalMonitor[] testMasterOBJ = InputMapper.gson().fromJson(requestOBJ, M_FoetalMonitor[].class);
		List<M_FoetalMonitor> testMasterRS = null;
		List<M_FoetalMonitor> alist = Arrays.asList(testMasterOBJ);
		testMasterRS = (List<M_FoetalMonitor>) foetalMonitorRepository.save(alist);
		if (testMasterRS != null && testMasterOBJ.length == testMasterRS.size())
			return new Gson().toJson(testMasterRS);
		else
			return null;
	}
	
	@Override
	public String getFoetalMonitorTestMaster(Integer psmID) {
		ArrayList<M_FoetalMonitor> procMasterListRS = foetalMonitorRepository.getByProviderServiceMapID(psmID);
		return new Gson().toJson(procMasterListRS);
	}

	@Override
	public String updateFoetalMonitorTestMaster(String requestOBJ) {
		String returnOBJ = null;
		M_FoetalMonitor fmOBJ = InputMapper.gson().fromJson(requestOBJ, M_FoetalMonitor.class);
		if (fmOBJ != null && fmOBJ.getFoetalMonitorTestID() != null && fmOBJ.getFoetalMonitorTestID() > 0) {
			Integer x = foetalMonitorRepository.updateFoetalMonitorDetails(fmOBJ.getFoetalMonitorTestID(), fmOBJ.getTestName(),
					fmOBJ.getTestDesc(), fmOBJ.getModifiedBy());
			if (x != null && x == 1) {
				M_FoetalMonitor procedureDetailsRS = foetalMonitorRepository.getByFoetalMonitorTestID(fmOBJ.getFoetalMonitorTestID());
				returnOBJ = new Gson().toJson(procedureDetailsRS);
			}
		}
		return returnOBJ;
	}
	
	@Override
	public String updateFoetalMonitorTestMasterStatus(Integer foetalMonitorTestID, Boolean deleted) throws Exception {
		int status = foetalMonitorRepository.updateFoetalMonitorStatus(foetalMonitorTestID, deleted);
		if (status > 0) {
			M_FoetalMonitor foetalMonitorDetailsRS = foetalMonitorRepository.getByFoetalMonitorTestID(foetalMonitorTestID);
			return new Gson().toJson(foetalMonitorDetailsRS);
		} else {
			return null;
		}

	}
	
	/***
	 * @author DU20091017 saves the FoetalMonitor device ID.
	 */
	@Override
	public int saveFoetalMonitorDeviceID(ArrayList<FoetalMonitorDeviceID> foetalMonitorData) throws IEMRException {

		try {
			ArrayList<FoetalMonitorDeviceID> deviceIdsaved = (ArrayList<FoetalMonitorDeviceID>) foetalMonitorDeviceIDRepo
					.save(foetalMonitorData);
			if (deviceIdsaved.size() > 0 && deviceIdsaved.get(0).getVfdID() > 0)
				return 1;
			else
				throw new IEMRException("Error in saving foetal monitor device ID");
		} catch (Exception e) {
			throw new IEMRException("Error in saving foetal monitor deviceID " + e.getMessage());
		}
		
	}

	@Override
	public String getFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {

		try {
			Map<String, Object> resMap = new HashMap<>();
			ArrayList<FoetalMonitorDeviceID> deviceIDs = foetalMonitorDeviceIDRepo
					.getFoetalMonitorDeviceID(foetalMonitorData.getProviderServiceMapID());
			resMap.put("fetosenseDeviceIDs", deviceIDs);
			return new Gson().toJson(resMap);
		} catch (Exception e) {
			throw new IEMRException("Error in getting foetal monitor DeviceID " + e.getMessage());
		}
		
	}

	@Override
	public int deleteFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {
		try {
			FoetalMonitorDeviceID deleteResponse = foetalMonitorDeviceIDRepo.save(foetalMonitorData);
			if (deleteResponse != null && deleteResponse.getVfdID() > 0) {
				if (deleteResponse.getVanID() != null && deleteResponse.getVanID() > 0) {
					Boolean vanStatus = false;
					if (deleteResponse.getDeleted())
						vanStatus = false;
					else
						vanStatus = true;
					int vanUpdate = masterVanRepo.updateVanFoetalMonitorsmapping(vanStatus, deleteResponse.getVanID());
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
	public String getvanIDAndFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {
		try {
			Map<String, Object> resMap = new HashMap<>();
			ArrayList<M_Van> vanDetail = new ArrayList<M_Van>();
			ArrayList<Object[]> vanDetails = masterVanRepo.getVanIDNotMappedWithDevice(foetalMonitorData.getVanTypeID(),
					foetalMonitorData.getParkingPlaceID(), foetalMonitorData.getProviderServiceMapID());
			for (Object[] van : vanDetails) {
				M_Van vanIDAndValue = new M_Van((Integer) van[0], (String) van[1],(String) van[2]);
				vanDetail.add(vanIDAndValue);
			}
			resMap.put("VanIDs", vanDetail);
			ArrayList<FoetalMonitorDeviceID> deviceIDDetails = foetalMonitorDeviceIDRepo
					.getFoetalMonitorDeviceIDNotMapped(foetalMonitorData.getProviderServiceMapID());
			resMap.put("deviceIDs", deviceIDDetails);
			return new Gson().toJson(resMap);
		} catch (Exception e) {
			throw new IEMRException("Error in getting vanID and foetalMonitorID" + e.getMessage());
		}
		
	}

	/***
	 * @author DU20091017
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int vanIDAndDeviceIDMapping(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {
		try {
			foetalMonitorData.setModifiedBy(foetalMonitorData.getCreatedBy());
			int vanIDUpdated = foetalMonitorDeviceIDRepo.createMappingOfVanIDAndDeviceID(foetalMonitorData.getVanID(),
					foetalMonitorData.getVanTypeID(), foetalMonitorData.getParkingPlaceID(), foetalMonitorData.getVanName(),
					foetalMonitorData.getModifiedBy(), foetalMonitorData.getDeviceID());
			if (vanIDUpdated > 0) {
				int mappingUpdated = masterVanRepo.updateVanFoetalMonitorsmapping(true, foetalMonitorData.getVanID());
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
	public int updateFoetalMonitorDeviceID(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {
		try {
			FoetalMonitorDeviceID updateDeviceID = foetalMonitorDeviceIDRepo.save(foetalMonitorData);
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
	public String getVanIDMappingWorklist(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {
		try {
			ArrayList<FoetalMonitorDeviceID> mappedWorklist = foetalMonitorDeviceIDRepo.getMappedWorklist(
					foetalMonitorData.getVanTypeID(), foetalMonitorData.getParkingPlaceID(),
					foetalMonitorData.getProviderServiceMapID());
			return new Gson().toJson(mappedWorklist);
		} catch (Exception e) {
			throw new IEMRException("Error in getting vanID mapping worklist" + e.getMessage()); 
		}
		
	}

	/***
	 * @author DU20091017
	 */
	@Override
	public int updatingvanIDAndDeviceIDMapping(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {

		int vanDetailsNull = foetalMonitorDeviceIDRepo.updateVanDetailsToNull(foetalMonitorData.getVfdID());
		if (vanDetailsNull > 0) {
			try {
				int vanIDUpdated = foetalMonitorDeviceIDRepo.createMappingOfVanIDAndDeviceID(foetalMonitorData.getVanID(),
						foetalMonitorData.getVanTypeID(), foetalMonitorData.getParkingPlaceID(), foetalMonitorData.getVanName(),
						foetalMonitorData.getModifiedBy(), foetalMonitorData.getDeviceID());
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
	public int deleteVanIDAndDeviceIDMapping(FoetalMonitorDeviceID foetalMonitorData) throws IEMRException {

		try {
			if (!foetalMonitorData.getDeactivated()) {
				int countOfVan = foetalMonitorDeviceIDRepo.getMappedVanDetails(foetalMonitorData.getVanID());
				if (countOfVan > 0) {
					throw new IEMRException("The Van is already mapped with a device");
				}
			}
			int mappingDeleted = foetalMonitorDeviceIDRepo.deleteMapping(foetalMonitorData.getDeactivated(),
					foetalMonitorData.getVfdID());
			if (mappingDeleted > 0) {
				Boolean vanStatus = false;
				if (foetalMonitorData.getDeactivated())
					vanStatus = false;
				else
					vanStatus = true;
				int vanMappingStatusUpdated = masterVanRepo.updateVanFoetalMonitorsmapping(vanStatus,
						foetalMonitorData.getVanID());
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
