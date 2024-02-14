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
package com.iemr.admin.service.vanSpokeMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.admin.data.VanSpokeMapping.m_VanSpokeMapping;
import com.iemr.admin.repo.VanSpokeMappingRepo.VanSpokeMappingRepo;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;

@Service
public class VanSpokeMappingServiceImpl implements VanSpokeMappingService {

	@Autowired
	private VanSpokeMappingRepo vanSpokeMappingRepo;
	
	@Autowired
	private  VanMasterRepository vanMasterRepository;

	@Override
	public String saveVanSpokeMapping(String requestObj) throws IEMRException {

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		List<m_VanSpokeMapping> vanSpokeRepo = null;
		int updatedVanSpokeMapping = 0;
		if (jsnOBJ != null && jsnOBJ.has("vanSpokeMapping") && !jsnOBJ.get("vanSpokeMapping").isJsonNull()) {
			String request = jsnOBJ.get("vanSpokeMapping").toString();
			m_VanSpokeMapping[] vanSpokeMapping = InputMapper.gson().fromJson(request, m_VanSpokeMapping[].class);

			List<m_VanSpokeMapping> saveVanSpokeReq = new ArrayList<m_VanSpokeMapping>();
			for (m_VanSpokeMapping obj : vanSpokeMapping) {
				saveVanSpokeReq.add(obj);
			}

			vanSpokeRepo = (List<m_VanSpokeMapping>) vanSpokeMappingRepo.saveAll(saveVanSpokeReq);
			
			for(m_VanSpokeMapping vanSpokeMapped : vanSpokeRepo) {
				int vanID = vanSpokeMapped.getMmu_VanID();
				String modifiedBy = vanSpokeMapped.getCreatedBy();
				updatedVanSpokeMapping = vanMasterRepository.updateVanSpokeMapping(vanID, true, modifiedBy);
			}
		}

		if (vanSpokeRepo != null && updatedVanSpokeMapping >= 1)
			return "success";
		else
			return "failure";

	};

	@Override
	public String getVanSpokeMappingDetails(String requestobj) throws IEMRException {

		Map<String, Object> resMap = new HashMap<String, Object>();
		JSONObject obj = new JSONObject(requestobj);
		if (obj.length() > 0) {
			int parkingPlaceId = obj.getInt("mmu_parkingplaceID");
			int servicePointId = obj.getInt("mmu_servicePointId");
			int vanTypeID = obj.getInt("mmu_vanTypeID");
			System.out.println(parkingPlaceId + " " + servicePointId);
			ArrayList<m_VanSpokeMapping> vanSpokeDetails = vanSpokeMappingRepo.getVanSpokeMappingDetails(parkingPlaceId,
					servicePointId,vanTypeID);
			
			
			resMap.put("vanSpokeMappedDetails", vanSpokeDetails);
		}
		System.out.println("test" + resMap);
		return new GsonBuilder()
	               .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
	               .create()
	               .toJson(resMap);
//		return new Gson().toJson(resMap);
	}

	@Override
	public String deleteVanSpokeMapping(String requestObj) throws IEMRException {

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		m_VanSpokeMapping deleteResponse = null;
		int updatedVanSpokeMapping = 0;
		if (jsnOBJ != null && jsnOBJ.has("vanSpokeDelete") && !jsnOBJ.get("vanSpokeDelete").isJsonNull()) {
			String request = jsnOBJ.get("vanSpokeDelete").toString();
			m_VanSpokeMapping deleteReq = InputMapper.gson().fromJson(request, m_VanSpokeMapping.class);
//			deleteReq.setDeleted(true);
			deleteResponse = vanSpokeMappingRepo.save(deleteReq);
			
				int vanID = deleteResponse.getMmu_VanID();
				String modifiedBy = deleteResponse.getCreatedBy();
				updatedVanSpokeMapping = vanMasterRepository.updateVanSpokeMapping(vanID, deleteResponse.getDeleted() == true ? false : true, modifiedBy);

		}

		if (deleteResponse != null && updatedVanSpokeMapping >=1 )
			return "success";
		else
			return "failure";

	}

	@Override
	public String updateVanSpokeMapping(String requestObj) throws IEMRException {

		m_VanSpokeMapping[] updateRequest = InputMapper.gson().fromJson(requestObj, m_VanSpokeMapping[].class);

		List<m_VanSpokeMapping> updateMapping = new ArrayList<m_VanSpokeMapping>();

		for (m_VanSpokeMapping update : updateRequest) {
			updateMapping.add(update);
		}

		ArrayList<m_VanSpokeMapping> deleteResponse = (ArrayList<m_VanSpokeMapping>) vanSpokeMappingRepo
				.saveAll(updateMapping);

		if (deleteResponse != null)
			return "success";
		else
			return "failure";

	}

}
