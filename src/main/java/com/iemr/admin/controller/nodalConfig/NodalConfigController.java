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
package com.iemr.admin.controller.nodalConfig;

import java.util.List;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.model.emailconfig.CreateNodalEmailRequestModel;
import com.iemr.admin.model.emailconfig.NodalEmailRequest;
import com.iemr.admin.model.emailconfig.UpdateNodalEmailRequest;
import com.iemr.admin.service.nodalemailconfig.NodalConfigService;
import com.iemr.admin.utils.mapper.OutputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@ApplicationPath("/nodal")
public class NodalConfigController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	@Autowired
	NodalConfigService nodalConfigService;

	@Operation(summary = "Save config")
	@RequestMapping(value = "/saveConf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String saveConfig(@RequestBody List<CreateNodalEmailRequestModel> createEmailRequests,
			HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("saveConfig received request");
			logger.debug("saveConfig received request");
			response.setResponse(OutputMapper.gsonWithoutExpose()
					.toJson(nodalConfigService.saveNodalEmailConfigs(createEmailRequests)));
		} catch (Exception e) {
			response.setError(e);
		}

		logger.debug("saveConfig sending response " + response.toString());
		logger.info("saveConfig sending response");
		return response.toString();

	}

	@Operation(summary = "Get nodal email configs")
	@RequestMapping(value = "/getNodalEmailConfigs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getNodalEmailConfigs(@RequestBody NodalEmailRequest authEmailRequest, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("getEmailConfigs received request");
			logger.debug("getEmailConfigs received request");
			response.setResponse(OutputMapper.gsonWithoutExpose()
					.toJson(nodalConfigService.getAllNodalEmailConfigs(authEmailRequest)));
		} catch (Exception e) {
			response.setError(e);
		}

		logger.debug("getEmailConfigs sending response " + response.toString());
		logger.info("getEmailConfigs sending response");
		return response.toString();

	}

	@Operation(summary = "Update nodal email config")
	@RequestMapping(value = "/updateNodalEmailConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String updateNodalEmailConfig(@RequestBody UpdateNodalEmailRequest updateEmailRequest,
			HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("updateNodalEmailConfig received request");
			logger.debug("updateNodalEmailConfig received request");
			response.setResponse(OutputMapper.gsonWithoutExpose()
					.toJson(nodalConfigService.updateNodalEmailConfigs(updateEmailRequest)));
		} catch (Exception e) {
			response.setError(e);
		}

		logger.debug("updateNodalEmailConfig sending response " + response.toString());
		logger.info("updateNodalEmailConfig sending response");
		return response.toString();

	}

}
