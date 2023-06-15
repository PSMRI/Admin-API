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

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.sevice.labmodule.MastersStatusUpdateImpl;
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
public class LabModuleUpdateController {
	private MastersStatusUpdateImpl mastersStatusUpdateImpl;

	@Autowired
	public void setMastersStatusUpdateImpl(MastersStatusUpdateImpl mastersStatusUpdateImpl) {
		this.mastersStatusUpdateImpl = mastersStatusUpdateImpl;
	}

	@CrossOrigin
	@ApiOperation(value = "update procedure status for enable or disable", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/updateProcedureStatus" }, method = { RequestMethod.POST })
	public String updateProcedureStatus(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject jsnOBJ = new JSONObject(requestOBJ);
			if (jsnOBJ != null && jsnOBJ.has("procedureID") && jsnOBJ.getInt("procedureID") > 0
					&& jsnOBJ.has("deleted")) {
				String s = mastersStatusUpdateImpl.updateProcedureStatus(jsnOBJ.getInt("procedureID"),
						jsnOBJ.getBoolean("deleted"));
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5002, "Failed to update the status");
			} else {
				response.setError(5002, "invalid request");
			}

		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "update Component status for enable or disable", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/updateComponentStatus" }, method = { RequestMethod.POST })
	public String updateComponentStatus(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject jsnOBJ = new JSONObject(requestOBJ);
			if (jsnOBJ != null && jsnOBJ.has("componentID") && jsnOBJ.getInt("componentID") > 0
					&& jsnOBJ.has("deleted")) {
				String s = mastersStatusUpdateImpl.updateComponentStatus(jsnOBJ.getInt("componentID"),
						jsnOBJ.getBoolean("deleted"));
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5002, "Failed to update the status");
			} else {
				response.setError(5002, "invalid request");
			}

		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "update procedure master for a particular procedure", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/updateProcedureMaster" }, method = { RequestMethod.POST })
	public String updateProcedureMaster(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {

			String s = mastersStatusUpdateImpl.updateProcedureMaster(requestOBJ);
			if (s != null) {
				response.setResponse(s);
			} else {
				response.setError(5002, "Failed to update procedure details");
			}

		} catch (Exception e) {
			response.setError(e);
		}

		return response.toString();

	}

	@CrossOrigin
	@ApiOperation(value = "update Component master for a particular Component", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/updateComponentMaster" }, method = { RequestMethod.POST })
	public String updateComponentMaster(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {

			String s = mastersStatusUpdateImpl.updateComponentMaster(requestOBJ);
			if (s != null) {
				response.setResponse(s);
			} else {
				response.setError(5002, "Failed to update component details");
			}

		} catch (Exception e) {
			response.setError(e);
		}

		return response.toString();

	}
}
