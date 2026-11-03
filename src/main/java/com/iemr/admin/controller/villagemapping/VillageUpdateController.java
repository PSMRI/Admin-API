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
package com.iemr.admin.controller.villagemapping;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.model.villagemapping.VillageUpdateRequest;
import com.iemr.admin.service.villagemapping.VillageUpdateService;
import com.iemr.admin.utils.mapper.OutputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/villageMapping")
public class VillageUpdateController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private VillageUpdateService villageUpdateService;

	@Operation(summary = "Replace a village on a user's work location mapping and move their beneficiary addresses")
	@RequestMapping(value = "/updateVillage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String updateVillage(@RequestBody VillageUpdateRequest updateRequest) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("updateVillage received request");
			response.setResponse(OutputMapper.gsonWithoutExpose().toJson(villageUpdateService.updateVillage(updateRequest)));
		} catch (Exception e) {
			logger.error("updateVillage failed", e);
			response.setError(e);
		}
		logger.info("updateVillage sending response");
		return response.toString();
	}
}
