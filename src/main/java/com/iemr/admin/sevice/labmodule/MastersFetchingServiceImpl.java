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
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iemr.admin.data.labmodule.ComponentMaster;
import com.iemr.admin.data.labmodule.ComponentResultMap;
import com.iemr.admin.data.labmodule.ProcedureComponentMapping;
import com.iemr.admin.data.labmodule.ProcedureMaster;
import com.iemr.admin.repo.labmodule.ComponentMasterRepo;
import com.iemr.admin.repo.labmodule.ComponentResultMapRepo;
import com.iemr.admin.repo.labmodule.ProcedureComponentMappingRepo;
import com.iemr.admin.repo.labmodule.ProcedureMasterRepo;

@Service
public class MastersFetchingServiceImpl implements MastersFetchingService {
	private ProcedureMasterRepo procedureMasterRepo;
	private ComponentMasterRepo componentMasterRepo;
	private ProcedureComponentMappingRepo procedureComponentMappingRepo;
	private ComponentResultMapRepo componentResultMapRepo;

	@Autowired
	public void setComponentResultMapRepo(ComponentResultMapRepo componentResultMapRepo) {
		this.componentResultMapRepo = componentResultMapRepo;
	}

	@Autowired
	public void setProcedureComponentMappingRepo(ProcedureComponentMappingRepo procedureComponentMappingRepo) {
		this.procedureComponentMappingRepo = procedureComponentMappingRepo;
	}

	@Autowired
	public void setComponentMasterRepo(ComponentMasterRepo componentMasterRepo) {
		this.componentMasterRepo = componentMasterRepo;
	}

	@Autowired
	public void setProcedureMasterRepo(ProcedureMasterRepo procedureMasterRepo) {
		this.procedureMasterRepo = procedureMasterRepo;
	}

	@Override
	public String getProcedureMaster(Integer psmID) throws Exception {
//		ArrayList<Object[]> procMasterListRS = procedureMasterRepo.findProcByPSMID(psmID);
		ArrayList<ProcedureMaster> procMasterListRS = procedureMasterRepo.findProcByPSMIDc(psmID);
//		ArrayList<ProcedureMaster> psList = ProcedureMaster.getProcedureMasterList(procMasterListRS);
//		return new Gson().toJson(psList);
		return new Gson().toJson(procMasterListRS);
	}

	@Override
	public String getProcedureMasterDelFalse(Integer psmID) throws Exception {
		ArrayList<Object[]> procMasterListRS = procedureMasterRepo.getProcedureDetailsDelFalse(psmID);
		ArrayList<ProcedureMaster> psList = ProcedureMaster.getProcedureMasterDelFalse(procMasterListRS);
		return new Gson().toJson(psList);
	}

	@Override
	public String getComponentMaster(Integer psmID) throws Exception {
		/*ArrayList<Object[]> procMasterListRS = componentMasterRepo.getComponentDetails(psmID);
		if (procMasterListRS != null && procMasterListRS.size() > 0) {
			ArrayList<ComponentMaster> compList = ComponentMaster.getComponentList(procMasterListRS);

			return new Gson().toJson(compList);
		} else {
			return new Gson().toJson(new ArrayList<>());
		}*/
		ArrayList<ComponentMaster> procMasterListRS = componentMasterRepo.getComponentDetailsBypsmID(psmID);
			return new Gson().toJson(procMasterListRS);
	}

	@Override
	public String getComponentMasterDelFalse(Integer psmID) throws Exception {
		String component = null;
		ArrayList<Object[]> procMasterListRS = componentMasterRepo.getComponentDetailsDelFalse(psmID);
		if (procMasterListRS != null && procMasterListRS.size() > 0) {
			ArrayList<ComponentMaster> compList = ComponentMaster.getComponentListDelFalse(procMasterListRS);
	//		component = componentMasterRepo.getComponentWithLoincCode(String.valueOf(compList.get(4)));
			
			return new Gson().toJson(compList);
		} else {
			return new Gson().toJson(new ArrayList<>());
		}
	}

	public String getProcCompMappingDelFalse(Integer psmID) throws Exception {
		ArrayList<Object[]> procCompMapListRS = procedureComponentMappingRepo.getProcedureComponentMappingList(psmID);
		//System.out.println("hi");
		if (procCompMapListRS != null && procCompMapListRS.size() > 0) {
			ArrayList<Map<String, Object>> responseList = ProcedureComponentMapping
					.getProcedureComponentMappingObjListDetails(procCompMapListRS);
			return new Gson().toJson(responseList);
		} else {
			return new Gson().toJson(new ArrayList<>());
		}
	}

	public String getProcCompMappingForProcedureID(Integer pID) throws Exception {
		ArrayList<Object[]> procCompMapListRS = procedureComponentMappingRepo
				.getProcedureComponentMappingListForProcedureID(pID);
		//System.out.println("hi");
		if (procCompMapListRS != null && procCompMapListRS.size() > 0) {
			ArrayList<Map<String, Object>> responseList = ProcedureComponentMapping.getProcedureComponentMappingObjListDetails(procCompMapListRS);
			return new Gson().toJson(responseList);
		} else {
			return new Gson().toJson(new ArrayList<>());
		}
	}
	
//	public  ArrayList<Map<String, Object>> getProcedureComponentMappingObjListDetails(ArrayList<Object[]> obj) {
//		ArrayList<ProcedureComponentMapping> pcmList = new ArrayList<>();
//		ProcedureComponentMapping pcmOBJ;
//		Map<String, Object> procCompListMap = null;
//		ArrayList<Map<String, Object>> tmpList = new ArrayList<>();
//		Map<String, Object> compMap = null;
//		ArrayList<Map<String, Object>> compList = null;
//		StringBuilder s = null;
//		String component = null;
//		Integer tmpProcID = 0;
//		int i = 0;
//		for (Object[] obj1 : obj) {
//			Integer procID = (Integer) obj1[0];
//
//			if (true) {
//				i = 1;
//				procCompListMap = new HashMap<String, Object>();
//
//				compList = new ArrayList<>();
//				compMap = new HashMap<>();
//				compMap.put("testComponentID", obj1[1]);
//				compMap.put("testComponentName", obj1[4]);
//				compMap.put("testComponentDesc", obj1[6]);
//				compMap.put("testComponentLoincCode", obj1[9]);
//				
//				if(obj1[9]!=null)
//				{
//					System.out.println("code is"+String.valueOf(obj1[9]));
//					component = componentMasterRepo.getComponentWithLoincCode(String.valueOf(obj1[9]));
//				}
//					
//				compMap.put("testComponentLoincComponent",component);
//				compList.add(compMap);
//
//				s = new StringBuilder("(" + i + ")" + (String) obj1[4]);
//				procCompListMap.put("procedureID", procID);
//				procCompListMap.put("procedureName", (String) obj1[2]);
//				procCompListMap.put("procedureDesc", (String) obj1[3]);
//				procCompListMap.put("compList", s);
//
//				procCompListMap.put("compListDetails", compList);
//
//				tmpList.add(procCompListMap);
//				i++;
//			}
////			else {
////				compMap = new HashMap<>();
////				compMap.put("testComponentID", obj1[1]);
////				compMap.put("testComponentName", obj1[4]);
////				compMap.put("testComponentDesc", obj1[6]);
////				compList.add(compMap);
////
////				s.append(", (" + i + ")" + (String) obj1[4]);
////				procCompListMap.put("compList", s);
////
////				procCompListMap.put("compListDetails", compList);
////
////				i++;
////			}
//
//			tmpProcID = procID;
//		}
//		return tmpList;
//	}

	public String getComponentDetailsForComponentID(Integer cID) throws Exception {
		String returnOBJ = null;
		ComponentMaster componentMaster = componentMasterRepo.findByTestComponentID(cID);
		//String component=null;
//		if(componentMaster.getLionicNum()!=null)
//		{
//			 component=componentMasterRepo.getComponentWithLoincCode(componentMaster.getLionicNum());
//		}
		ComponentMaster componentMasterOBJ = ComponentMaster.getCompMasterForEdit(componentMaster);
		//System.out.println("hii");
		//componentMasterOBJ.setComponent(component);
		if (componentMasterOBJ != null) {
			
			if (componentMasterOBJ.getInputType() != null
					&& componentMasterOBJ.getInputType().equalsIgnoreCase("TextBox")) {
				returnOBJ = new Gson().toJson(componentMasterOBJ);
			} else {
				ArrayList<ComponentResultMap> crmRS = componentResultMapRepo.findByTestComponentIDAndDeleted(cID,
						false);
				ArrayList<Map<String, Object>> compList = ComponentResultMap.getComplist(crmRS);
				componentMasterOBJ.setCompOpt(compList);
				returnOBJ = new Gson().toJson(componentMasterOBJ);
				//System.out.println("hii");
			}
		}
		return returnOBJ;
	}
}
