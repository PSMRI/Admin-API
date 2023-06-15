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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.sevice.labmodule.MastersCreationServiceImpl;
import com.iemr.admin.sevice.labmodule.MastersMappingServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/***
 * 
 * @author Rajeev Tripathi
 * @category master creation for lab module
 * @date 15-02-2018
 *
 */

@RestController
@CrossOrigin
@RequestMapping(value = "labModule", headers = "Authorization")
public class LabModuleCreateController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	private MastersCreationServiceImpl mastersCreationServiceImpl;
	private MastersMappingServiceImpl mastersMappingServiceImpl;

	@Autowired
	public void setMastersMappingServiceImpl(MastersMappingServiceImpl mastersMappingServiceImpl) {
		this.mastersMappingServiceImpl = mastersMappingServiceImpl;
	}

	@Autowired
	public void setMastersCreationServiceImpl(MastersCreationServiceImpl mastersCreationServiceImpl) {
		this.mastersCreationServiceImpl = mastersCreationServiceImpl;
	}

	@CrossOrigin
	@ApiOperation(value = "Create procedure master by Provider admin", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/createProcedureMaster" }, method = { RequestMethod.POST })
	public String createProcedureMaster(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			String s = mastersCreationServiceImpl.createProcedureMaster(requestOBJ);
			logger.info(requestOBJ);
			System.out.println(requestOBJ);
			if (s != null) {
				response.setResponse(s);

			} else {
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "Create component master by Provider admin", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/createComponentMaster" }, method = { RequestMethod.POST })
	public String createComponentMaster(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			String s = mastersCreationServiceImpl.createComponentMaster(requestOBJ);
			if (s != null) {
				response.setResponse(s);

			} else {
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "Create procedure - component mapping by Provider admin", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/createProcedureComponentMapping" }, method = { RequestMethod.POST })
	public String createProcedureComponentMapping(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info(requestOBJ);
			String s = mastersMappingServiceImpl.createProcedureComponentMapping(requestOBJ);
			if (s != null) {
				if (s.equalsIgnoreCase("1"))
					response.setError(5002, "Invalid request.");
				else
					response.setResponse(s);

			} else {
				response.setError(5002, "Error while saving the data");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

}
