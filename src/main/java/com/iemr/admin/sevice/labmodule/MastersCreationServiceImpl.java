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
import com.iemr.admin.repo.labmodule.IOTRepo;
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
import com.iemr.admin.repo.labmodule.ComponentMasterRepo;
import com.iemr.admin.repo.labmodule.ComponentResultMapRepo;
import com.iemr.admin.repo.labmodule.ProcedureMasterRepo;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.repo.calibration.CalibrationAPIRepo;
import com.iemr.admin.repo.calibration.CalibrationRepo;;
/***
 * 
 * @author Rajeev Tripathi
 * @date 15-02-2018
 *
 */
@Service
public class MastersCreationServiceImpl implements MastersCreationService {
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
	public String createProcedureMaster(String requestOBJ) throws IEMRException {
		Integer iotUpdate=0;
		ProcedureMaster procedureMasterOBJ = InputMapper.gson().fromJson(requestOBJ, ProcedureMaster.class);
		if(procedureMasterOBJ.getIsCalibration() !=null && procedureMasterOBJ.getIsCalibration()==true && procedureMasterOBJ.getIotProcedureID()!=null)
		{
			 iotUpdate=updateIOTProcedure(procedureMasterOBJ);
		}
		if(procedureMasterOBJ.getIsMandatory()==null)
			procedureMasterOBJ.setIsMandatory(false);
		ProcedureMaster procedureMasterRS = procedureMasterRepo.save(procedureMasterOBJ);
		ArrayList<Object[]> procedureDetails = procedureMasterRepo
				.getProcedureDetails(procedureMasterRS.getProcedureID());

		ProcedureMaster tmpOBJ = ProcedureMaster.getProcedureMaster(procedureDetails);
		System.out.println(tmpOBJ);
		if (tmpOBJ != null)
			return new Gson().toJson(tmpOBJ);
		else
			return null;
	}
   public Integer updateIOTProcedure(ProcedureMaster obj) throws IEMRException
   {
	   int updateIOT=0;
	   try
	   {
	   IOTProcedure iotProc=iotRepo.getIOTProcedureByID(obj.getIotProcedureID());
	   String testName=iotProc.getCalibrationCode();
	   Calibration calib=calibrationAPIRepo.getCalibration();
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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createComponentMaster(String requestOBJ) throws Exception {
		ComponentMaster componentMasterOBJ = InputMapper.gson().fromJson(requestOBJ, ComponentMaster.class);
		ComponentMaster componentMasterRS = componentMasterRepo.save(componentMasterOBJ);
		//System.out.println(componentMasterRS);
		if (componentMasterRS != null) {
			ArrayList<Map<String, Object>> compOptList = componentMasterOBJ.getCompOpt();
			if (compOptList != null && compOptList.size() > 0) {
				ArrayList<ComponentResultMap> comResultMapObjList = ComponentResultMap.getComponentResultMapList(
						compOptList, componentMasterOBJ.getProviderServiceMapID(), componentMasterOBJ.getCreatedBy(),
						componentMasterOBJ.getTestComponentID());
				ArrayList<ComponentResultMap> comResultMapObjListRS = (ArrayList<ComponentResultMap>) componentResultMapRepo
						.save(comResultMapObjList);
				if (comResultMapObjList.size() == comResultMapObjListRS.size()) {
					return new Gson().toJson(componentMasterRS);
				} else {
					throw new Exception();
				}
			} else {
				return new Gson().toJson(componentMasterRS);
			}

		} else {
			return null;
		}
	}

	

}
