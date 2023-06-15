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
package com.iemr.admin.controller.fetosensemaster;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.fetosensemaster.FetosenseDeviceID;
import com.iemr.admin.service.fetosensemaster.FetosenseService;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @author DE40034072
 *
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/fetosense", headers = "Authorization")
public class FetosenseCreateController {

	@Autowired
	private FetosenseService fetosenseService;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	/***
	 * @author DU20091017
	 * @param requestObj
	 * @param authorization
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "To save fetosense device ID")
	@RequestMapping(value = "/createFetosenseDeviceID", method = RequestMethod.POST, headers = "Authorization")
	public String saveFetosenseDeviceID(
			@ApiParam("[\r\n" + "  {\r\n" + "    \"deviceID\": \"String\",\r\n" + "    \"deviceName\": \"String\",\r\n"
					+ "    \"providerServiceMapID\": Integer,\r\n" + "    \"createdBy\": \"String\"\r\n"
					+ "  }]") @RequestBody ArrayList<FetosenseDeviceID> requestObj,
			@RequestHeader(value = "Authorization") String authorization) {

		logger.info("Request Object for saving fetosense device ID - " + requestObj);
		OutputResponse output = new OutputResponse();

		try {
//			ArrayList<FetosenseDeviceID> fetosenseRequest = (ArrayList<FetosenseDeviceID>) InputMapper.gson().fromJson(requestObj, FetosenseDeviceID.class);

			int deviceIDSaved = fetosenseService.saveFetosenseDeviceID(requestObj);
			if (deviceIDSaved == 1)
				output.setResponse("Device ID saved successfully");
		} catch (IEMRException e) {
			logger.error("saveFetosenseDeviceID Error - " + e.getMessage());
			output.setError(5000, e.getMessage());
			// TODO: handle exception
		}

		return output.toString();
	}

	/***
	 * @author DU20091017
	 * @param requestObj
	 * @param authorization
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "To save mapping of vanID and fetosense device ID")
	@RequestMapping(value = "/mapping/vanIDAndDeviceID", method = RequestMethod.POST, headers = "Authorization")
	public String saveVanIDandDeviceIDMapping(
			@ApiParam("\r\n" + "{\r\n" + "      \"spokeID\":this.spokeID,\r\n"
					+ "      \"deviceID\": this.deviceID,\r\n"
					+ "      \"providerServiceMapID\": this.providerServiceMapID,\r\n"
					+ "      \"createdBy\": this.commonDataService.uname\r\n" + " \r\n" + "  }\r\n"
					+ "") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String authorization) {

		logger.info("Request Object for mapping vanID and fetosense device ID - " + requestObj);
		OutputResponse output = new OutputResponse();
		
		try {
			FetosenseDeviceID fetosenseRequest = InputMapper.gson().fromJson(requestObj, FetosenseDeviceID.class);
			int mappingDone = fetosenseService.vanIDAndDeviceIDMapping(fetosenseRequest);
			if(mappingDone == 1)
				output.setResponse("Mapping Done successfully");
		}catch (IEMRException e) {
			logger.error("saveVanIDandDeviceIDMapping Error - " + e.getMessage());
			output.setError(5000, e.getMessage());
		}
		return output.toString();
	}
}
