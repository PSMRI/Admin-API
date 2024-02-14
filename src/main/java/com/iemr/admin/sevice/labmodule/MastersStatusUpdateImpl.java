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
package com.iemr.admin.sevice.labmodule;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.iemr.admin.data.calibration.Calibration;
import com.iemr.admin.data.labmodule.ComponentMaster;
import com.iemr.admin.data.labmodule.ComponentResultMap;
import com.iemr.admin.data.labmodule.IOTProcedure;
import com.iemr.admin.data.labmodule.ProcedureMaster;
import com.iemr.admin.repo.calibration.CalibrationAPIRepo;
import com.iemr.admin.repo.labmodule.ComponentMasterRepo;
import com.iemr.admin.repo.labmodule.ComponentResultMapRepo;
import com.iemr.admin.repo.labmodule.IOTRepo;
import com.iemr.admin.repo.labmodule.ProcedureMasterRepo;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;

@Service
public class MastersStatusUpdateImpl implements MasterStatusUpdate {
	private ProcedureMasterRepo procedureMasterRepo;
	private ComponentMasterRepo componentMasterRepo;
	private ComponentResultMapRepo componentResultMapRepo;

	@Autowired
	public void setComponentResultMapRepo(ComponentResultMapRepo componentResultMapRepo) {
		this.componentResultMapRepo = componentResultMapRepo;
	}

	@Autowired
	public void setComponentMasterRepo(ComponentMasterRepo componentMasterRepo) {
		this.componentMasterRepo = componentMasterRepo;
	}

	@Autowired
	public void setProcedureMasterRepo(ProcedureMasterRepo procedureMasterRepo) {
		this.procedureMasterRepo = procedureMasterRepo;
	}
	@Autowired
	public CalibrationAPIRepo calibrationAPIRepo;
	@Autowired
	public IOTRepo iotRepo;
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String updateProcedureStatus(Integer procedureID, Boolean deleted) throws Exception {
		int status = procedureMasterRepo.updateProcedureStatus(procedureID, deleted);
		if (status > 0) {
			ArrayList<Object[]> procedureDetailsRS = procedureMasterRepo.getProcedureDetails(procedureID);
			ProcedureMaster obj = ProcedureMaster.getProcedureMaster(procedureDetailsRS);
			return new Gson().toJson(obj);
		} else {
			return null;
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String updateComponentStatus(Integer componentID, Boolean deleted) throws Exception {
		int status = componentMasterRepo.updateComponentStatus(componentID, deleted);
		if (status > 0) {
			ArrayList<Object[]> componentDetailsRS = componentMasterRepo.getComponentDetailsByCompID(componentID);
			ComponentMaster obj = ComponentMaster.getComponent(componentDetailsRS);
			return new Gson().toJson(obj);
		} else {
			return null;
		}

	}
	 public Integer updateIOTProcedure(ProcedureMaster obj) throws IEMRException
	   {
		   int updateIOT=0;
		   try
		   {
		   IOTProcedure iotProc=iotRepo.getIOTProcedureByID(obj.getIotProcedureID());
		   String testName=iotProc.getCalibrationCode();
		   Calibration calib=calibrationAPIRepo.getCalibration();
		   if(obj.getIsCalibration()==false)
		   {
			   updateIOT=iotRepo.updateIOTWithCalibration(null, null, null, obj.getIotProcedureID());
		   }
		   if(iotProc.getCalibrationStartAPI()==null)
		   {
		   String calibStartAPI=calib.getCalibrationStartAPI().replace("{test_name}",testName);
		   String calibStatusAPI=calib.getCalibrationStatusAPI().replace("{test_name}",testName);
		   String calibEndAPI=calib.getCalibrationEndAPI().replace("{test_name}",testName);
		   updateIOT=iotRepo.updateIOTWithCalibration(calibStartAPI, calibStatusAPI, calibEndAPI, obj.getIotProcedureID());
		   }
		   }
		   catch(Exception e)
		   {
			   throw new IEMRException(e.getMessage());
		   }
		   return updateIOT;
	   }
	@Transactional(rollbackFor = Exception.class)
	public String updateProcedureMaster(String requestOBJ) throws Exception {
		String returnOBJ = null;
		Integer iotUpdate=0;
		ProcedureMaster pmOBJ = InputMapper.gson().fromJson(requestOBJ, ProcedureMaster.class);
		if(pmOBJ.getIsCalibration()!=null && pmOBJ.getIotProcedureID()!=null)
		{
			 iotUpdate=updateIOTProcedure(pmOBJ);
		}
		if (pmOBJ != null && pmOBJ.getProcedureID() != null && pmOBJ.getProcedureID() > 0
				&& pmOBJ.getProcedureName() != null && pmOBJ.getProcedureName().length() > 0
				&& pmOBJ.getProcedureType() != null && pmOBJ.getProcedureType().length() > 0
				&& pmOBJ.getGender() != null && pmOBJ.getGender().length() > 0 && pmOBJ.getModifiedBy() != null) {
			if(pmOBJ.getIsMandatory()==null)
				pmOBJ.setIsMandatory(false);
			if(pmOBJ.getIsCalibration()==null)
				pmOBJ.setIsCalibration(false);
			Integer x = procedureMasterRepo.updateProcedureDetails(pmOBJ.getProcedureID(), pmOBJ.getProcedureName(),
					pmOBJ.getProcedureDesc(), pmOBJ.getProcedureType(), pmOBJ.getGender(), pmOBJ.getModifiedBy(),pmOBJ.getIotProcedureID(),pmOBJ.getIsMandatory(),pmOBJ.getIsCalibration());
			if (x != null && x > 0) {
				ArrayList<Object[]> procedureDetailsRS = procedureMasterRepo
						.getProcedureDetails(pmOBJ.getProcedureID());
				ProcedureMaster obj = ProcedureMaster.getProcedureMaster(procedureDetailsRS);
				returnOBJ = new Gson().toJson(obj);
			}
		}
		return returnOBJ;
	}

	@Transactional(rollbackFor = Exception.class)
	public String updateComponentMaster(String requestOBJ) throws Exception {
		String returnOBJ = null;
		ComponentMaster cmOBJ = InputMapper.gson().fromJson(requestOBJ, ComponentMaster.class);
		if (cmOBJ.getInputType() != null) {
//			if(cmOBJ.getIsMandatory()==null)
//				cmOBJ.setIsMandatory(false);
			if (cmOBJ.getInputType().equalsIgnoreCase("TextBox")) {
				Integer x = componentMasterRepo.updateComponentDetailsTextBox(cmOBJ.getTestComponentID(),
						cmOBJ.getTestComponentName(), cmOBJ.getTestComponentDesc(), cmOBJ.getMeasurementUnit(),
						cmOBJ.getRange_min(), cmOBJ.getRange_normal_min(), cmOBJ.getRange_normal_max(),
						cmOBJ.getRange_max(), cmOBJ.getModifiedBy(), cmOBJ.getIotComponentID(),cmOBJ.getLionicNum(),cmOBJ.getLionicTerm());
				if (x != null && x > 0) {
					ArrayList<Object[]> componentDetailsRS = componentMasterRepo
							.getComponentDetailsByCompID(cmOBJ.getTestComponentID());
					ComponentMaster obj = ComponentMaster.getComponent(componentDetailsRS);
					returnOBJ = new Gson().toJson(obj);
				}
			} else {
				Integer x = componentMasterRepo.updateComponentDetailsOtherThenTextBox(cmOBJ.getTestComponentID(),
						cmOBJ.getTestComponentName(), cmOBJ.getTestComponentDesc(), cmOBJ.getModifiedBy(), cmOBJ.getIotComponentID(),cmOBJ.getLionicNum(),cmOBJ.getLionicTerm());
				if (x != null && x > 0) {
					Integer i = componentResultMapRepo.deletePreviousCompResultMappingSoft(cmOBJ.getTestComponentID(),
							cmOBJ.getModifiedBy());

					ArrayList<Map<String, Object>> compOptList = cmOBJ.getCompOpt();

					if (compOptList != null && compOptList.size() > 0) {
						ArrayList<ComponentResultMap> comResultMapObjList = ComponentResultMap
								.getComponentResultMapList(compOptList, cmOBJ.getProviderServiceMapID(),
										cmOBJ.getModifiedBy(), cmOBJ.getTestComponentID());
						ArrayList<ComponentResultMap> comResultMapObjListRS = (ArrayList<ComponentResultMap>) componentResultMapRepo
								.saveAll(comResultMapObjList);
						if (comResultMapObjList.size() == comResultMapObjListRS.size()) {
							ArrayList<Object[]> componentDetailsRS = componentMasterRepo
									.getComponentDetailsByCompID(cmOBJ.getTestComponentID());
							ComponentMaster obj = ComponentMaster.getComponent(componentDetailsRS);
							returnOBJ = new Gson().toJson(obj);
						} else {
							throw new Exception();
						}
					}
				}
			}
		}
		return returnOBJ;
	}
}
