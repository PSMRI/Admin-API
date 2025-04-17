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
package com.iemr.admin.controller.uptsu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iemr.admin.data.uptsu.CDSSMapping;
import com.iemr.admin.data.uptsu.M_FacilityMapping;
import com.iemr.admin.data.uptsu.UploadRequest;
import com.iemr.admin.service.uptsu.FacilityService;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController

@RequestMapping({ "/uptsu" })
public class FacilityController {
	@Autowired
	private FacilityService uptsuService;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@PostMapping(value = "/saveFacility")
	@Operation(summary = "Save facility")
	
	public String saveFacilityData(
			@Param("[\r\n" + "  {\r\n" + "    \"createdBy\": \"String\",\r\n" + "    \"fileName\": \"String\",\r\n"
					+ "    \"providerServiceMapID\": Integer,\r\n" + "    \"fileExtension\": \"String\"\r\n"
					+ "    \"fileContent\": \"String\"\r\n" + "  }]") @RequestBody String uploadReq,
			@RequestHeader(value = "Authorization") String authorization) {
		logger.info("Request Object for saving  saveFacilityData ");
		OutputResponse output = new OutputResponse();
		ObjectMapper mapper = new ObjectMapper();
		UploadRequest request;
		try {
			request = mapper.readValue(uploadReq, UploadRequest.class);
			Iterable<M_FacilityMapping> data = uptsuService.saveFacility(request);
			if (null != data && !ObjectUtils.isEmpty(data)) {
				output.setResponse("saveFacilityData saved successfully");
			}
		} catch (Exception e) {
			logger.error("error while saving saveFacilityData {} - " + e.getMessage());
			output.setError(5000, "Invalid Request", "Failed");

		}

		return output.toString();
	}
	
	
	@Operation(summary = "Save cdss details")
	@RequestMapping(value= "/submit/cdss", headers = "Authorization", method = { RequestMethod.POST })
	public String saveCdssDetails(@RequestBody String request) {
		ObjectMapper mapper = new ObjectMapper();
		OutputResponse response = new OutputResponse();
		CDSSMapping requestObj = null;
		try {
			requestObj = mapper.readValue(request, CDSSMapping.class);
			CDSSMapping data = uptsuService.saveCdssDetails(requestObj);
			if (null != data && !ObjectUtils.isEmpty(data)) {
				response.setResponse("Data saved successfully");
			}
		} catch (Exception e) {
			logger.error("error while saving cdss details {} - " + e.getMessage());
			response.setError(5000, e.getMessage());
		}
		return response.toString();
	}
	
	
	@Operation(summary = "Fetch Cdss data")
	@RequestMapping(value="/getCdssData/{psmId}", method = RequestMethod.GET)
	public String getCdssData(@PathVariable Integer psmId, @RequestHeader(value = "Authorization") String authorization) throws Exception {
		OutputResponse response = new OutputResponse();
		try {
			String resp=uptsuService.getCdssData(psmId);
			response.setResponse(resp);
		} catch (Exception e) {
			logger.error("error while getting ,cdss data {} - " + e.getMessage());
			response.setError(e);
		}
		return response.toString();
	}

}
