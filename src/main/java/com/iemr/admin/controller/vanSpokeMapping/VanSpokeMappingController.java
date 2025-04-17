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
package com.iemr.admin.controller.vanSpokeMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.service.vanSpokeMapping.VanSpokeMappingService;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping(value = "/mapping")
public class VanSpokeMappingController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private VanSpokeMappingService vanSpokeMappingService;

	/***
	 * @param requestObj
	 * @return
	 * @throws IEMRException
	 */
	
	@Operation(summary = "Save van and spoke mapping")
	@RequestMapping(value = { "/save/vanSpokeMapping" }, method = { RequestMethod.POST })
	public String saveBenNCDCareNurseData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) throws IEMRException {

		OutputResponse output = new OutputResponse();

		try {
			String response = vanSpokeMappingService.saveVanSpokeMapping(requestObj);

			if (response.equalsIgnoreCase("success"))
				output.setResponse("Mapping done successfully");
			else
				output.setError(5000, "error in mapping van and spoke");
		} catch (Exception e) {
			output.setError(e);
		}

		return output.toString();
	}

	
	@Operation(summary = "Get van and spoke mapping")
	@RequestMapping(value = { "/get/vanSpokeMapping" }, method = { RequestMethod.POST })
	public String getVanSpokeMapping(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) throws IEMRException {
		OutputResponse output = new OutputResponse();

		try {
			String response = vanSpokeMappingService.getVanSpokeMappingDetails(requestObj);
			if (response != null)
				output.setResponse(response);
			else
				output.setError(5000, "error in fetching the van and spoke data");
		} catch (Exception e) {
			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Delete van and spoke mapping")
	@RequestMapping(value = { "/delete/vanSpokeMapping" }, method = { RequestMethod.POST })
	public String deleteVanSpokeMapping(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) throws IEMRException {
		OutputResponse output = new OutputResponse();

		try {
			String response = vanSpokeMappingService.deleteVanSpokeMapping(requestObj);
			if (response.equalsIgnoreCase("success"))
				output.setResponse("Mapping status got updated");
			else
				output.setError(5000, "Error in deleting mapping");
		} catch (Exception e) {
			output.setError(e);
		}

		return output.toString();
	}

}
