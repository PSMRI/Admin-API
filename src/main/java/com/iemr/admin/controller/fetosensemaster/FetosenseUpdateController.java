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
public class FetosenseUpdateController {

	@Autowired
	private FetosenseService fetosenseService;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@CrossOrigin
	@ApiOperation(value = "update deviceID", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/update/fetosenseDeviceID", method = { RequestMethod.POST })
	public String updateFetosenseDeviceID(@ApiParam(""
			+ "{\r\n" + 
			"        \"VfdID\": \"Integer\",\r\n" + 
			"        \"deviceName\": \"String\",\r\n" + 
			"        \"deviceID\": \"String\",\r\n" + 
			"        \"vanID\": \"Integer\",\r\n" + 
			"        \"parkingPlaceID\": \"Integer\",\r\n" + 
			"        \"vanTypeID\": \"Integer\",\r\n" + 
			"        \"vanName\": \"String\",\r\n" + 
			"        \"providerServiceMapID\": \"Integer\",\r\n" + 
			"        \"deactivated\": \"Boolean\",\r\n" + 
			"        \"deleted\": \"Boolean\",\r\n" + 
			"        \"processed\": \"String\",\r\n" + 
			"        \"createdBy\": \"String\"\",\r\n" + 
			"        \"modifiedBy\": \"String\"\r\n" + 
			"      }") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for fetosense data updating  :" + requestObj);

		try {
			FetosenseDeviceID fetosenseRequest = InputMapper.gson().fromJson(requestObj, FetosenseDeviceID.class);
			int deviceIDUpdated = fetosenseService.updateFetosenseDeviceID(fetosenseRequest);
			if (deviceIDUpdated == 1) {
				response.setResponse("DeviceID updated successfully");
			}
		} catch (IEMRException e) {
			response.setError(5000, "Unable to update deviceID - " + e.getMessage());
			logger.error("updateFetosenseDeviceID :" + e.getMessage());
		}
		return response.toString();
	}

	/***
	 * @author DU20091017
	 * @param requestObj
	 * @param Authorization
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "delete fetosense deviceID", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/delete/fetosenseDeviceID", method = { RequestMethod.POST })
	public String deleteFetosenseDeviceID(@ApiParam("{\r\n" + 
			"        \"VfdID\": \"Integer\",\r\n" + 
			"        \"deviceName\": \"String\",\r\n" + 
			"        \"deviceID\": \"String\",\r\n" + 
			"        \"vanID\": \"Integer\",\r\n" + 
			"        \"parkingPlaceID\": \"Integer\",\r\n" + 
			"        \"vanTypeID\": \"Integer\",\r\n" + 
			"        \"vanName\": \"String\",\r\n" + 
			"        \"providerServiceMapID\": \"Integer\",\r\n" + 
			"        \"deactivated\": \"Boolean\",\r\n" + 
			"        \"deleted\": \"Boolean\",\r\n" + 
			"        \"processed\": \"String\",\r\n" + 
			"        \"createdBy\": \"String\"\",\r\n" + 
			"        \"modifiedBy\": \"String\"\r\n" + 
			"      }") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		logger.info("Request object for deleting fetosense deviceID :" + requestObj);

		try {
			FetosenseDeviceID fetosenseRequest = InputMapper.gson().fromJson(requestObj, FetosenseDeviceID.class);
			int deleteResponse = fetosenseService.deleteFetosenseDeviceID(fetosenseRequest);
			if (deleteResponse == 1)
				response.setResponse("Device ID de-activated successfully");
		} catch (IEMRException e) {
			response.setError(5000, e.getMessage());
			logger.error("deleteFetosenseDeviceID - " + e.getMessage());
		}
		return response.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "update vanID and fetosense deviceID mapping", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/update/vanIDAndFetosenseDeviceIDMapping", method = { RequestMethod.POST })
	public String updateMapping(@ApiParam("{\r\n" + 
			"        \"VfdID\": \"Integer\",\r\n" + 
			"        \"deviceName\": \"String\",\r\n" + 
			"        \"deviceID\": \"String\",\r\n" + 
			"        \"vanID\": \"Integer\",\r\n" + 
			"        \"parkingPlaceID\": \"Integer\",\r\n" + 
			"        \"vanTypeID\": \"Integer\",\r\n" + 
			"        \"vanName\": \"String\",\r\n" + 
			"        \"providerServiceMapID\": \"Integer\",\r\n" + 
			"        \"deactivated\": \"Boolean\",\r\n" + 
			"        \"deleted\": \"Boolean\",\r\n" + 
			"        \"processed\": \"String\",\r\n" + 
			"        \"createdBy\": \"String\"\",\r\n" + 
			"        \"modifiedBy\": \"String\"\r\n" + 
			"      }") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		logger.info("Request object for updating vanID and fetosense deviceID mapping :" + requestObj);

		try {
			FetosenseDeviceID fetosenseRequest = InputMapper.gson().fromJson(requestObj, FetosenseDeviceID.class);
			int updateResponse = fetosenseService.updatingvanIDAndDeviceIDMapping(fetosenseRequest);
			if (updateResponse == 1)
				response.setResponse("Mapping updated successfully");
		} catch (IEMRException e) {
			response.setError(5000, e.getMessage());
			logger.error("updateMapping error - " + e.getMessage());
		}
		return response.toString();
	}

	/***
	 * @author DU20091017
	 * @param requestObj
	 * @param Authorization
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "deactivate vanID and fetosense deviceID mapping ", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/delete/vanIDAndFetosenseDeviceIDMapping", method = { RequestMethod.POST })
	public String deleteVanIDAndFetosenseDeviceID(@ApiParam("{\r\n" + 
			"        \"VfdID\": \"Integer\",\r\n" + 
			"  \"vanID\": \"Integer\",\r\n" + 
			"  \"deactivated\": \"Boolean\", \r\n" + 
			"}") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		logger.info("Request object for deleting vanID and fetosense deviceID mapping :" + requestObj);

		try {
			FetosenseDeviceID fetosenseRequest = InputMapper.gson().fromJson(requestObj, FetosenseDeviceID.class);
			int deleteStaus = fetosenseService.deleteVanIDAndDeviceIDMapping(fetosenseRequest);
			if (deleteStaus > 0) {
				response.setResponse("Mapped deactivated successfully");
			}
		} catch (IEMRException e) {
			response.setError(5000, e.getMessage());
			logger.error("updateMapping error - " + e.getMessage());
		}
		return response.toString();
	}
}
