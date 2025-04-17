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
package com.iemr.admin.controller.manufacturer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.manufacturer.M_Manufacturer;
import com.iemr.admin.service.manufacturer.ManufacturerInter;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class ManufacturerController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	@Autowired
	private ManufacturerInter manufacturerInter;

	
	@Operation(summary = "Create manufacturer")
	@RequestMapping(value = "/createManufacturer", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createManufacturer(@RequestBody String createManufacturer) {

		OutputResponse response = new OutputResponse();

		try {

			M_Manufacturer[] Manufacturer = InputMapper.gson().fromJson(createManufacturer, M_Manufacturer[].class);
			List<M_Manufacturer> ManufacturerData = Arrays.asList(Manufacturer);

			ArrayList<M_Manufacturer> saveData = manufacturerInter.createManufacturer(ManufacturerData);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get manufacturer")
	@RequestMapping(value = "/getManufacturer", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getManufacturer(@RequestBody String getManufacturer) {

		OutputResponse response = new OutputResponse();

		try {

			M_Manufacturer Manufacturer = InputMapper.gson().fromJson(getManufacturer, M_Manufacturer.class);

			ArrayList<M_Manufacturer> getData = manufacturerInter
					.createManufacturer(Manufacturer.getProviderServiceMapID());

			response.setResponse(getData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Edit manufacturer")
	@RequestMapping(value = "/editManufacturer", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editManufacturer(@RequestBody String editManufacturer) {

		OutputResponse response = new OutputResponse();

		try {

			M_Manufacturer Manufacturer = InputMapper.gson().fromJson(editManufacturer, M_Manufacturer.class);

			M_Manufacturer editData = manufacturerInter.editManufacturer(Manufacturer.getManufacturerID());

			editData.setManufacturerDesc(Manufacturer.getManufacturerDesc());
			editData.setContactPerson(Manufacturer.getContactPerson());
			editData.setcST_GST_No(Manufacturer.getcST_GST_No());
			editData.setAddressLine1(Manufacturer.getAddressLine1());
			editData.setAddressLine2(Manufacturer.getAddressLine2());
			editData.setCountryID(Manufacturer.getCountryID());
			editData.setStateID(Manufacturer.getStateID());
			editData.setDistrictID(Manufacturer.getDistrictID());
			editData.setPinCode(Manufacturer.getPinCode());
			editData.setModifiedBy(Manufacturer.getModifiedBy());

			M_Manufacturer saveData = manufacturerInter.saveEditedData(editData);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Delete manufacturer")
	@RequestMapping(value = "/deleteManufacturer", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteManufacturer(@RequestBody String deleteManufacturer) {

		OutputResponse response = new OutputResponse();

		try {

			M_Manufacturer Manufacturer = InputMapper.gson().fromJson(deleteManufacturer, M_Manufacturer.class);

			M_Manufacturer editData = manufacturerInter.editManufacturer(Manufacturer.getManufacturerID());

			editData.setDeleted(Manufacturer.getDeleted());

			M_Manufacturer saveData = manufacturerInter.saveEditedData(editData);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Check manufacturer code")
	@RequestMapping(value = "/checkManufacturerCode", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String checkManufacturerCode(@RequestBody String deleteManufacturer) {

		OutputResponse response = new OutputResponse();

		try {

			M_Manufacturer Manufacturer = InputMapper.gson().fromJson(deleteManufacturer, M_Manufacturer.class);

			Boolean saveData = manufacturerInter.checkManufacturerCode(Manufacturer);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

}
