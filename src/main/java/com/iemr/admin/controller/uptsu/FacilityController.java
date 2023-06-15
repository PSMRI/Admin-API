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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.admin.data.uptsu.M_FacilityMapping;
import com.iemr.admin.data.uptsu.UploadRequest;
import com.iemr.admin.service.uptsu.FacilityService;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.annotations.ApiParam;

@RestController
@CrossOrigin
@RequestMapping({ "/uptsu" })
public class FacilityController {
	@Autowired
	private FacilityService uptsuService;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@PostMapping(value = "/saveFacility")
	@CrossOrigin
	public String saveFacilityData(
			@ApiParam("[\r\n" + "  {\r\n" + "    \"createdBy\": \"String\",\r\n" + "    \"fileName\": \"String\",\r\n"
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
			if(null != data && !ObjectUtils.isEmpty(data)) {
				output.setResponse("saveFacilityData saved successfully");
			}
		} catch (Exception e) {
			logger.error("error while saving saveFacilityData {} - "+ e.getMessage());
			output.setError(5000, "Invalid Request","Failed");
			
		}

		return output.toString();
	}

}
