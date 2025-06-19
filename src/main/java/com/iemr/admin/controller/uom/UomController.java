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
package com.iemr.admin.controller.uom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.uom.M_Uom;
import com.iemr.admin.service.uom.UomInter;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class UomController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	@Autowired
	private UomInter uomInter;

	@Operation(summary = "Create Uom")
	@RequestMapping(value = "/createUom", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createUom(@RequestBody String createUom) {

		OutputResponse response = new OutputResponse();

		try {

			M_Uom[] UomData = InputMapper.gson().fromJson(createUom, M_Uom[].class);
			List<M_Uom> saveUomData = Arrays.asList(UomData);

			ArrayList<M_Uom> saveData = uomInter.createDrugtypeData(saveUomData);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Get Uom")
	@RequestMapping(value = "/getUom", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getUom(@RequestBody String getUom) {

		OutputResponse response = new OutputResponse();

		try {

			M_Uom UomData = InputMapper.gson().fromJson(getUom, M_Uom.class);

			ArrayList<M_Uom> getData = uomInter.createDrugtypeData(UomData.getProviderServiceMapID());

			response.setResponse(getData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@Operation(summary = "Edit Uom")
	@RequestMapping(value = "/editUom", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editUom(@RequestBody String editUom) {

		OutputResponse response = new OutputResponse();

		try {

			M_Uom UomData = InputMapper.gson().fromJson(editUom, M_Uom.class);

			M_Uom geteditedData = uomInter.editDrugtypeData(UomData.getuOMID());
			geteditedData.setuOMName(UomData.getuOMName());
			geteditedData.setuOMDesc(UomData.getuOMDesc());
			geteditedData.setuOMCode(UomData.getuOMCode());
			geteditedData.setStatus(UomData.getStatus());
			geteditedData.setModifiedBy(UomData.getModifiedBy());

			M_Uom editedData = uomInter.saveeditedData(geteditedData);

			response.setResponse(editedData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@Operation(summary = "Delete Uom")
	@RequestMapping(value = "/deleteUom", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String deleteUom(@RequestBody String editUom) {

		OutputResponse response = new OutputResponse();

		try {

			M_Uom UomData = InputMapper.gson().fromJson(editUom, M_Uom.class);

			M_Uom geteditedData = uomInter.editDrugtypeData(UomData.getuOMID());
			geteditedData.setDeleted(UomData.getDeleted());

			M_Uom deletedData = uomInter.saveeditedData(geteditedData);

			response.setResponse(deletedData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@Operation(summary = "Check Uom code")
	@RequestMapping(value = "/checkUomCode", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String checkUomCode(@RequestBody String editUom) {

		OutputResponse response = new OutputResponse();

		try {

			M_Uom UomData = InputMapper.gson().fromJson(editUom, M_Uom.class);

			Boolean geteditedData = uomInter.checkUomCode(UomData);

			response.setResponse(geteditedData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

}
