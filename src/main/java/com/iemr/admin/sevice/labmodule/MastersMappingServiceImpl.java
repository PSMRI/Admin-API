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
import com.iemr.admin.data.labmodule.ProcedureComponentMapping;
import com.iemr.admin.repo.labmodule.ProcedureComponentMappingRepo;
import com.iemr.admin.utils.mapper.InputMapper;

/***
 * 
 * @author Rajeev Tripathi
 *
 */
@Service
public class MastersMappingServiceImpl implements MastersMappingService {
	private ProcedureComponentMappingRepo procedureComponentMappingRepo;

	@Autowired
	public void setProcedureComponentMappingRepo(ProcedureComponentMappingRepo procedureComponentMappingRepo) {
		this.procedureComponentMappingRepo = procedureComponentMappingRepo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createProcedureComponentMapping(String requestOBJ) throws Exception {
		ProcedureComponentMapping pcmOBJ = InputMapper.gson().fromJson(requestOBJ, ProcedureComponentMapping.class);
		ArrayList<Map<String, Object>> compList = pcmOBJ.getCompList();
		if (compList != null && compList.size() > 0 && pcmOBJ != null) {
			ArrayList<ProcedureComponentMapping> pcmList = ProcedureComponentMapping
					.getProcedureComponentMappingObjList(compList, pcmOBJ.getProcedureID(),
							pcmOBJ.getProviderServiceMapID(), pcmOBJ.getCreatedBy());
			Integer x = procedureComponentMappingRepo.softDeleteProcCompMapping(pcmOBJ.getProcedureID(),
					pcmOBJ.getCreatedBy());
			ArrayList<ProcedureComponentMapping> pcmListRS = (ArrayList<ProcedureComponentMapping>) procedureComponentMappingRepo
					.saveAll(pcmList);
			if (pcmList.size() == pcmListRS.size()) {
				ArrayList<Object[]> tempListRS = procedureComponentMappingRepo
						.getProcedureComponentMappingListForProcedureID(pcmOBJ.getProcedureID());
				ArrayList<Map<String, Object>> responseList = ProcedureComponentMapping
						.getProcedureComponentMappingObjListDetails(tempListRS);
				return new Gson().toJson(responseList);
			} else {
				return null;
			}
		} else {
			return "1";
		}
	}

}
