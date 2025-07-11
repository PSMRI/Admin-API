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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.telemedicine.M_UserTemp;
import com.iemr.admin.data.telemedicine.VideoConsultationDomain;
import com.iemr.admin.data.telemedicine.UserVideoConsultation;
import com.iemr.admin.service.telemedicine.VideoConsultationInter;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping(value = "/videoConsultation")
public class VideoConsultationController {

	@Autowired
	private VideoConsultationInter videoConsultationInter;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Operation(summary = "Get unmapped user")
	@RequestMapping(value = "/getunmappedUser/{serviceproviderID}/{designationID}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String getUserTM(@PathVariable("serviceproviderID") Integer serviceproviderID,
			@PathVariable("designationID") Integer designationID) {

		OutputResponse response = new OutputResponse();

		try {

			List<M_UserTemp> createdData = videoConsultationInter.getunmappedUser(serviceproviderID, designationID);

			response.setResponse(createdData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Create user")
	@RequestMapping(value = "/createUser", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createUserTM(@RequestBody UserVideoConsultation userVideoConsultation) {

		OutputResponse response = new OutputResponse();

		try {

			UserVideoConsultation createdData = videoConsultationInter.createUser(userVideoConsultation);

			response.setResponse(createdData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);
			logger.error(e.toString());

		}

		return response.toString();

	}

	@Operation(summary = "Edit user")
	@RequestMapping(value = "/editUser", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editUser(@RequestBody UserVideoConsultation userVideoConsultation) {

		OutputResponse response = new OutputResponse();

		try {

			UserVideoConsultation createdData = videoConsultationInter.editUser(userVideoConsultation);

			response.setResponse(createdData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Delete user")
	@RequestMapping(value = "/deleteUser/{userVideoConsultationMapID}/{deletedflag}/{ModifiedBy}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String createUserTM(@PathVariable("ModifiedBy") String modifiedBy,
			@PathVariable("userVideoConsultationMapID") Long userVideoConsultationMapID, @PathVariable("deletedflag") Boolean deletedflag) {

		OutputResponse response = new OutputResponse();

		try {

			UserVideoConsultation createdData = videoConsultationInter.deleteUser(userVideoConsultationMapID, deletedflag, modifiedBy);

			response.setResponse(createdData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Get mapped user")
	@RequestMapping(value = "/getmappedUsers/{serviceproviderID}", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getmappedUsers(@PathVariable("serviceproviderID") Integer serviceproviderID) {

		OutputResponse response = new OutputResponse();

		try {

			List<UserVideoConsultation> createdData = videoConsultationInter.fetchmappedUser(serviceproviderID);

			response.setResponse(createdData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Get domain")
	@RequestMapping(value = "/getdomain/{serviceproviderID}", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getdomain(@PathVariable("serviceproviderID") Integer serviceproviderID) {

		OutputResponse response = new OutputResponse();

		try {

			List<VideoConsultationDomain> createdData = videoConsultationInter.getdomain(serviceproviderID);

			response.setResponse(createdData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

}
