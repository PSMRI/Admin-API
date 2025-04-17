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
package com.iemr.admin.controller.drugtype;

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

import com.iemr.admin.data.drugtype.M_Drugtype;
import com.iemr.admin.service.drugtype.DrugtypeInter;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class DrugtypeController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	@Autowired
	private DrugtypeInter drugtypeInter;

	
	@Operation(summary = "Create manufacturer")
	@RequestMapping(value = "/createDrugtype", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createManufacturer(@RequestBody String createDrugtype) {

		OutputResponse response = new OutputResponse();
		try {

			M_Drugtype[] Drugtype = InputMapper.gson().fromJson(createDrugtype, M_Drugtype[].class);
			List<M_Drugtype> DrugtypeData = Arrays.asList(Drugtype);

			ArrayList<M_Drugtype> saveData = drugtypeInter.createDrugtypeData(DrugtypeData);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get manufacturer")
	@RequestMapping(value = "/getDrugtype", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getManufacturer(@RequestBody String getDrugtype) {

		OutputResponse response = new OutputResponse();

		try {

			M_Drugtype Drugtype = InputMapper.gson().fromJson(getDrugtype, M_Drugtype.class);

			ArrayList<M_Drugtype> getedData = drugtypeInter.getDrugtypeData(Drugtype.getProviderServiceMapID());

			response.setResponse(getedData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Edit manufacturer")
	@RequestMapping(value = "/editDrugtype", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editManufacturer(@RequestBody String editDrugtype) {

		OutputResponse response = new OutputResponse();

		try {

			M_Drugtype Drugtype = InputMapper.gson().fromJson(editDrugtype, M_Drugtype.class);

			M_Drugtype geteditedData = drugtypeInter.editDrugtypeData(Drugtype.getDrugTypeID());

			geteditedData.setDrugTypeName(Drugtype.getDrugTypeName());
			geteditedData.setDrugTypeDesc(Drugtype.getDrugTypeDesc());
			geteditedData.setDrugTypeCode(Drugtype.getDrugTypeCode());
			geteditedData.setStatus(Drugtype.getStatus());
			geteditedData.setModifiedBy(Drugtype.getModifiedBy());

			M_Drugtype saveeditedData = drugtypeInter.saveeditDrugtype(geteditedData);

			response.setResponse(saveeditedData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Delete manufacturer")
	@RequestMapping(value = "/deleteDrugtype", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String deleteManufacturer(@RequestBody String deleteDrugtype) {

		OutputResponse response = new OutputResponse();

		try {

			M_Drugtype Drugtype = InputMapper.gson().fromJson(deleteDrugtype, M_Drugtype.class);

			M_Drugtype geteditedData = drugtypeInter.editDrugtypeData(Drugtype.getDrugTypeID());

			geteditedData.setDeleted(Drugtype.getDeleted());

			M_Drugtype deletedData = drugtypeInter.saveeditDrugtype(geteditedData);

			response.setResponse(deletedData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

}
