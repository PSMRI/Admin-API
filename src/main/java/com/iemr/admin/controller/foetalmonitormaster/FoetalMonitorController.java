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
package com.iemr.admin.controller.foetalmonitormaster;

import java.util.ArrayList;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.foetalmonitormaster.FoetalMonitorDeviceID;
import com.iemr.admin.service.foetalmonitormaster.FoetalMonitorService;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@CrossOrigin
@RequestMapping(value = "fetosense", headers = "Authorization")
public class FoetalMonitorController {
	@Autowired
	private FoetalMonitorService foetalMonitorService;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@CrossOrigin
	@ApiOperation(value = "Create foetal monitor test master by provider admin", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/createFetosenseTestMaster" }, method = { RequestMethod.POST })
	public String createFoetalMonitorTestMaster(@RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			String s = foetalMonitorService.createFoetalMonitorTestMaster(requestOBJ);
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
	@ApiOperation(value = "Fetch foetal monitor tests master for provider-service-map-id", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/fetchFetosenseTestMaster/{psmID}" }, method = { RequestMethod.GET })
	public String fetchFoetalMonitorTestMaster(@PathVariable("psmID") Integer psmID) {
		OutputResponse response = new OutputResponse();
		try {
			if (psmID != null & psmID > 0) {
				String s = foetalMonitorService.getFoetalMonitorTestMaster(psmID);
				response.setResponse(s);
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "Update foetal monitor tests master for a particular procedure", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/updateFetosenseTestMaster" }, method = { RequestMethod.POST })
	public String updateProcedureMaster(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			String s = foetalMonitorService.updateFoetalMonitorTestMaster(requestOBJ);
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
	@ApiOperation(value = "Update procedure status for enable or disable", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/updateFetosenseTestMasterStatus" }, method = { RequestMethod.POST })
	public String updateProcedureStatus(@ApiParam(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject jsnOBJ = new JSONObject(requestOBJ);
			if (jsnOBJ != null && jsnOBJ.has("foetalMonitorTestID") && jsnOBJ.getInt("foetalMonitorTestID") > 0
					&& jsnOBJ.has("deleted")) {
				String s = foetalMonitorService.updateFoetalMonitorTestMasterStatus(jsnOBJ.getInt("foetalMonitorTestID"),
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
	@ApiOperation(value = "To save foetal monitor device id")
	@RequestMapping(value = "/createFetosenseDeviceID", method = RequestMethod.POST, headers = "Authorization")
	public String saveFoetalMonitorDeviceID(
			@ApiParam("[\r\n" + "  {\r\n" + "    \"deviceID\": \"String\",\r\n" + "    \"deviceName\": \"String\",\r\n"
					+ "    \"providerServiceMapID\": Integer,\r\n" + "    \"createdBy\": \"String\"\r\n"
					+ "  }]") @RequestBody ArrayList<FoetalMonitorDeviceID> requestObj,
			@RequestHeader(value = "Authorization") String authorization) {

		logger.info("Request Object for saving foetal monitor device ID - " + requestObj);
		OutputResponse output = new OutputResponse();

		try {

			int deviceIDSaved = foetalMonitorService.saveFoetalMonitorDeviceID(requestObj);
			if (deviceIDSaved == 1)
				output.setResponse("Device ID saved successfully");
		} catch (IEMRException e) {
			logger.error("saveFoetalMonitorDeviceID Error - " + e.getMessage());
			output.setError(5000, e.getMessage());
		}

		return output.toString();
	}

	/***
	 * @param requestObj
	 * @param authorization
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "To save mapping of van id and foetal monitor device id")
	@RequestMapping(value = "/mapping/vanIDAndDeviceID", method = RequestMethod.POST, headers = "Authorization")
	public String saveVanIDandDeviceIDMapping(
			@ApiParam("\r\n" + "{\r\n" + "      \"spokeID\":this.spokeID,\r\n"
					+ "      \"deviceID\": this.deviceID,\r\n"
					+ "      \"providerServiceMapID\": this.providerServiceMapID,\r\n"
					+ "      \"createdBy\": this.commonDataService.uname\r\n" + " \r\n" + "  }\r\n"
					+ "") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String authorization) {

		logger.info("Request Object for mapping vanID and foetal monitor device ID - " + requestObj);
		OutputResponse output = new OutputResponse();

		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			int mappingDone = foetalMonitorService.vanIDAndDeviceIDMapping(foetalMonitorRequest);
			if (mappingDone == 1)
				output.setResponse("Mapping Done successfully");
		} catch (IEMRException e) {
			logger.error("saveVanIDandDeviceIDMapping Error - " + e.getMessage());
			output.setError(5000, e.getMessage());
		}
		return output.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "Provides the foetal monitor device id")
	@RequestMapping(value = "/fetch/fetosenseDeviceID", method = RequestMethod.POST, headers = "Authorization")
	public String getFoetalMonitorDeviceID(
			@ApiParam("{ 'providerServiceMapID': Integer }") @RequestBody String requestObj) {

		logger.info("Request Object for getting foetal monitor DeviceID - " + requestObj);
		OutputResponse output = new OutputResponse();
		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			String response = foetalMonitorService.getFoetalMonitorDeviceID(foetalMonitorRequest);
			if (response != null)
				output.setResponse(response);
		} catch (IEMRException e) {
			logger.error("getFoetalMonitorDeviceID failed with error " + e.getMessage());
			output.setError(5000, e.getMessage());
		}
		return output.toString();
	}

	/***
	 * @param requestObj
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "Provides the van id and foetal monitor device id")
	@RequestMapping(value = "/fetch/vanIDAndFetosenseDeviceID", method = RequestMethod.POST, headers = "Authorization")
	public String getVanIDAndDeviceID(
			@ApiParam("\r\n" + "{\"parkingPlaceID\" : Integer,\r\n" + "                    \"vanTypeID\" : Integer,\r\n"
					+ "                    \"providerServiceMapID\" : Integer\r\n" + "                  };\r\n"
					+ "") @RequestBody String requestObj) {

		logger.info("Request Object for getting van ID and foetal monitor DeviceID - " + requestObj);
		OutputResponse output = new OutputResponse();

		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			String response = foetalMonitorService.getvanIDAndFoetalMonitorDeviceID(foetalMonitorRequest);
			if (response != null)
				output.setResponse(response);
		} catch (IEMRException e) {
			logger.error("getVanIDAndDeviceID failed with error " + e.getMessage());
			output.setError(5000, e.getMessage());
		}

		return output.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "Provides the worklist of van id and foetal monitor device id")
	@RequestMapping(value = "/fetch/mappingWorklist", method = RequestMethod.POST, headers = "Authorization")
	public String getMappedWorklist(
			@ApiParam("\r\n" + "{\"parkingPlaceID\" : Integer,\r\n" + "                    \"vanTypeID\" : Integer,\r\n"
					+ "                    \"providerServiceMapID\" : Integer\r\n" + "                  };\r\n"
					+ "") @RequestBody String requestObj) {
		logger.info("Request Object for getting van ID and foetal monitor DeviceID - " + requestObj);
		OutputResponse output = new OutputResponse();
		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			String mappedWorklist = foetalMonitorService.getVanIDMappingWorklist(foetalMonitorRequest);
			if (mappedWorklist != null)
				output.setResponse(mappedWorklist);
		} catch (IEMRException e) {
			logger.error("getMappedWorklist failed with error " + e.getMessage());
			output.setError(5000, e.getMessage());
		}
		return output.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "Update device id", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/update/fetosenseDeviceID", method = { RequestMethod.POST })
	public String updateFoetalMonitorDeviceID(
			@ApiParam("" + "{\r\n" + "        \"VfdID\": \"Integer\",\r\n" + "        \"deviceName\": \"String\",\r\n"
					+ "        \"deviceID\": \"String\",\r\n" + "        \"vanID\": \"Integer\",\r\n"
					+ "        \"parkingPlaceID\": \"Integer\",\r\n" + "        \"vanTypeID\": \"Integer\",\r\n"
					+ "        \"vanName\": \"String\",\r\n" + "        \"providerServiceMapID\": \"Integer\",\r\n"
					+ "        \"deactivated\": \"Boolean\",\r\n" + "        \"deleted\": \"Boolean\",\r\n"
					+ "        \"processed\": \"String\",\r\n" + "        \"createdBy\": \"String\"\",\r\n"
					+ "        \"modifiedBy\": \"String\"\r\n" + "      }") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for foetal monitor data updating  :" + requestObj);

		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			int deviceIDUpdated = foetalMonitorService.updateFoetalMonitorDeviceID(foetalMonitorRequest);
			if (deviceIDUpdated == 1) {
				response.setResponse("DeviceID updated successfully");
			}
		} catch (IEMRException e) {
			response.setError(5000, "Unable to update deviceID - " + e.getMessage());
			logger.error("updateFoetalMonitorDeviceID :" + e.getMessage());
		}
		return response.toString();
	}

	/***
	 * @param requestObj
	 * @param Authorization
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "Delete foetal monitor device id", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/delete/fetosenseDeviceID", method = { RequestMethod.POST })
	public String deleteFoetalMonitorDeviceID(
			@ApiParam("{\r\n" + "        \"VfdID\": \"Integer\",\r\n" + "        \"deviceName\": \"String\",\r\n"
					+ "        \"deviceID\": \"String\",\r\n" + "        \"vanID\": \"Integer\",\r\n"
					+ "        \"parkingPlaceID\": \"Integer\",\r\n" + "        \"vanTypeID\": \"Integer\",\r\n"
					+ "        \"vanName\": \"String\",\r\n" + "        \"providerServiceMapID\": \"Integer\",\r\n"
					+ "        \"deactivated\": \"Boolean\",\r\n" + "        \"deleted\": \"Boolean\",\r\n"
					+ "        \"processed\": \"String\",\r\n" + "        \"createdBy\": \"String\"\",\r\n"
					+ "        \"modifiedBy\": \"String\"\r\n" + "      }") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		logger.info("Request object for deleting foetal monitor deviceID :" + requestObj);

		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			int deleteResponse = foetalMonitorService.deleteFoetalMonitorDeviceID(foetalMonitorRequest);
			if (deleteResponse == 1)
				response.setResponse("Device ID de-activated successfully");
		} catch (IEMRException e) {
			response.setError(5000, e.getMessage());
			logger.error("deleteFoetalMonitorDeviceID - " + e.getMessage());
		}
		return response.toString();
	}

	@CrossOrigin
	@ApiOperation(value = "Update van id and foetal monitor device id mapping", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/update/vanIDAndFetosenseDeviceIDMapping", method = { RequestMethod.POST })
	public String updateMapping(
			@ApiParam("{\r\n" + "        \"VfdID\": \"Integer\",\r\n" + "        \"deviceName\": \"String\",\r\n"
					+ "        \"deviceID\": \"String\",\r\n" + "        \"vanID\": \"Integer\",\r\n"
					+ "        \"parkingPlaceID\": \"Integer\",\r\n" + "        \"vanTypeID\": \"Integer\",\r\n"
					+ "        \"vanName\": \"String\",\r\n" + "        \"providerServiceMapID\": \"Integer\",\r\n"
					+ "        \"deactivated\": \"Boolean\",\r\n" + "        \"deleted\": \"Boolean\",\r\n"
					+ "        \"processed\": \"String\",\r\n" + "        \"createdBy\": \"String\"\",\r\n"
					+ "        \"modifiedBy\": \"String\"\r\n" + "      }") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		logger.info("Request object for updating vanID and foetal monitor deviceID mapping :" + requestObj);

		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			int updateResponse = foetalMonitorService.updatingvanIDAndDeviceIDMapping(foetalMonitorRequest);
			if (updateResponse == 1)
				response.setResponse("Mapping updated successfully");
		} catch (IEMRException e) {
			response.setError(5000, e.getMessage());
			logger.error("updateMapping error - " + e.getMessage());
		}
		return response.toString();
	}

	/***
	 * @param requestObj
	 * @param Authorization
	 * @return
	 */
	@CrossOrigin
	@ApiOperation(value = "Deactivate van id and foetal monitor device id mapping ", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/delete/vanIDAndFetosenseDeviceIDMapping", method = { RequestMethod.POST })
	public String deleteVanIDAndFoetalMonitorDeviceID(
			@ApiParam("{\r\n" + "        \"VfdID\": \"Integer\",\r\n" + "  \"vanID\": \"Integer\",\r\n"
					+ "  \"deactivated\": \"Boolean\", \r\n" + "}") @RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		logger.info("Request object for deleting vanID and foetal monitor deviceID mapping :" + requestObj);

		try {
			FoetalMonitorDeviceID foetalMonitorRequest = InputMapper.gson().fromJson(requestObj, FoetalMonitorDeviceID.class);
			int deleteStaus = foetalMonitorService.deleteVanIDAndDeviceIDMapping(foetalMonitorRequest);
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
