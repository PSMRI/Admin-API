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
package com.iemr.admin.controller.labmodule;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.sevice.labmodule.IOTService;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/diagnostics")
public class SmartDiagnosticsController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	IOTService iotService;

	@CrossOrigin()
	@ApiOperation(value = "Get diagnostic test procedure details", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/getDiagnosticProcedure", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public String getIOTProcedure() {
		OutputResponse response = new OutputResponse();
		logger.info("getIOTProcedure request : {}");

		try {
			String data = iotService.getIOTProcedure();
			response.setResponse(data);
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		logger.info("getIOTProcedure response : {}", response);

		return response.toString();

	}

	@CrossOrigin()
	@ApiOperation(value = "Get diagnostic procedure component details", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/getDiagnosticProcedureComponent", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public String getIOTComponent() {
		OutputResponse response = new OutputResponse();
		logger.info("getIOTComponent request : {}");

		try {
			String data = iotService.getIOTComponent();
			response.setResponse(data);
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		logger.info("getIOTComponent response : {}", response);

		return response.toString();

	}
	
	@Value("${biological-screening-device-url}")
	private String biologicalScreeningDeviceUrl ;

	@ApiOperation(value = "Get the physiological and biological rapid screening device URL")
	@RequestMapping(value = "/biologicalScreeningDevice", method = RequestMethod.GET, headers = "Authorization")
	public String getBiologicalScreeningDeviceUrl() {
		return biologicalScreeningDeviceUrl;
	}
}
