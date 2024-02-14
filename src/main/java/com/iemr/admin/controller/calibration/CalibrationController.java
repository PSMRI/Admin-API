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
package com.iemr.admin.controller.calibration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.calibration.CalibrationStrip;
import com.iemr.admin.service.calibration.CalibrationService;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
public class CalibrationController {
	@Autowired
	private CalibrationService calibrationService;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	/**
	 * @param request
	 * @return CalibrationStripMasterData
	 */
	@CrossOrigin()
	@Operation(summary = "Create calibration strip")
	@RequestMapping(value = "/createCalibrationStrip", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createCalibrationStrip(
			@Param(value = "{\"stripCode\":\"String\",\"expiryDate\":\"Timestamp\",\"providerServiceMapID\":\"Integer\",\"createdBy\":\"String\"}") @RequestBody String request) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for Calibration strip master creation :" + request);

		try {
			CalibrationStrip calibration = InputMapper.gson().fromJson(request, CalibrationStrip.class);

			Integer saveData = calibrationService.saveData(calibration);
			if (saveData > 0)
				response.setResponse("Data saved successfully");
			else
				response.setError(5000, "Error while saving Calibration master data");
		} catch (IEMRException e) {
			e.printStackTrace();
			logger.error("Error while saving data:" + e);
			response.setError(5000, e.getMessage());

		}

		return response.toString();

	}

	/**
	 * @param request
	 * @return CalibrationStripList
	 */
	@CrossOrigin()
	@Operation(summary = "Fetch calibration strip")
	@RequestMapping(value = "/fetchCalibrationStrips", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String fetchCalibrationStrips(
			@Param(value = "{\"providerServiceMapID\":\"Integer\"}") @RequestBody String request) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for fetching Calibration strip :" + request);
		try {
			CalibrationStrip calibration = InputMapper.gson().fromJson(request, CalibrationStrip.class);

			String saveData = calibrationService.fetchData(calibration);
			response.setResponse(saveData);
		} catch (IEMRException e) {
			e.printStackTrace();
			logger.error("Error while fetching data:" + e);
			response.setError(e);
		}

		return response.toString();

	}

	/**
	 * @param request
	 * @return Response
	 */
	@CrossOrigin()
	@Operation(summary = "Delete calibration strip")
	@RequestMapping(value = "/deleteCalibrationStrip", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteCalibrationStrip(
			@Param(value = "{\"deleted\":\"Boolean\",\"CalibrationStripID\":\"Integer\"}") @RequestBody String request) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object to delete Calibration data :" + request);
		try {
			CalibrationStrip calibration = InputMapper.gson().fromJson(request, CalibrationStrip.class);

			Integer updateData = calibrationService.deleteData(calibration);
			if (updateData > 0)
				response.setResponse("Data deleted successfully");
			else
				response.setError(5000, "Error while updating Calibration master data");
		} catch (IEMRException e) {
			e.printStackTrace();
			logger.error("Error while updating data:" + e);
			response.setError(e);

		}

		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Update calibration strip")
	@RequestMapping(value = "/updateCalibrationStrip", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateCalibrationStrip(
			@Param(value = "{\"stripCode\":\"String\",\"expiryDate\":\"Timestamp\",\"providerServiceMapID\":\"Integer\",\"createdBy\":\"String\"}") @RequestBody String request) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for updating Calibration strip data :" + request);
		try {
			CalibrationStrip calibration = InputMapper.gson().fromJson(request, CalibrationStrip.class);

			Integer saveData = calibrationService.updateData(calibration);
			if (saveData > 0)
				response.setResponse("Data updated successfully");
			else
				response.setError(5000, "Error while updating Calibration master data");
		} catch (IEMRException e) {
			e.printStackTrace();
			logger.error("Error while updating data:" + e);
			response.setError(5000, e.getMessage());

		}

		return response.toString();

	}

}
