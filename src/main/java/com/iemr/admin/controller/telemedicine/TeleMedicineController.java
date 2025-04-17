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
package com.iemr.admin.controller.telemedicine;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.telemedicine.M_UserTemp;
import com.iemr.admin.data.telemedicine.Specialization;
import com.iemr.admin.data.telemedicine.TMinput;
import com.iemr.admin.data.telemedicine.UserSpecializationMapping;
import com.iemr.admin.service.telemedicine.TMInter;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping(value = "/TM")
public class TeleMedicineController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private TMInter tmInter;

	
	@Operation(summary = "Get user")
	@RequestMapping(value = "/getUser", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getUserTM(@RequestBody TMinput tminput) {

		OutputResponse response = new OutputResponse();

		try {

			ArrayList<M_UserTemp> createdData = tmInter.getUser(tminput);

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get specialization")
	@RequestMapping(value = "/getSpecialization", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getSpecialization() {

		OutputResponse response = new OutputResponse();

		try {

			ArrayList<Specialization> createdData = tmInter.getSpecialization();

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get user specialization")
	@RequestMapping(value = "/getUserSpecialization", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getUserSpecialization(@RequestBody TMinput tminput) {

		OutputResponse response = new OutputResponse();

		try {

			ArrayList<UserSpecializationMapping> createdData = tmInter
					.getUserSpecialization(tminput.getServiceproviderID());

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Save user specialization")
	@RequestMapping(value = "/saveUserSpecialization", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveUserSpecialization(@RequestBody ArrayList<UserSpecializationMapping> userSpecializationMapping) {

		OutputResponse response = new OutputResponse();

		try {

			ArrayList<UserSpecializationMapping> createdData = tmInter
					.saveUserSpecialization(userSpecializationMapping);

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Activate user specialization")
	@RequestMapping(value = "/activateUserSpecialization", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveUserSpecialization(@RequestBody UserSpecializationMapping userSpecializationMapping) {

		OutputResponse response = new OutputResponse();

		try {

			UserSpecializationMapping createdData = tmInter.findUserSpecialization(userSpecializationMapping);
			createdData.setDeleted(userSpecializationMapping.getDeleted());
			createdData.setModifiedBy(userSpecializationMapping.getModifiedBy());

			createdData = tmInter.saveoneUserSpecialization(createdData);

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

}
