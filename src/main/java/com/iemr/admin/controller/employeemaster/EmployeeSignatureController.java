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
package com.iemr.admin.controller.employeemaster;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.iemr.admin.data.employeemaster.EmployeeSignature;
import com.iemr.admin.service.employeemaster.EmployeeSignatureServiceImpl;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@PropertySource("classpath:application.properties")

@RestController
@RequestMapping(value = "/signature1")
public class EmployeeSignatureController {

	@Autowired
	EmployeeSignatureServiceImpl employeeSignatureServiceImpl;

	private InputMapper inputMapper = new InputMapper();

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Operation(summary = "Upload")
	@RequestMapping(value = "/upload", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String uploadFile(@RequestBody EmployeeSignature emp) {
		OutputResponse response = new OutputResponse();
		logger.debug("upload signature for userID started" + emp);

		try {

			emp.setSignature(Base64.getDecoder().decode(emp.getFileContent()));
			Long userSignID = employeeSignatureServiceImpl.uploadSignature(emp);
			response.setResponse(userSignID.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Signature Upload Failed" + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "User id")
	@RequestMapping(value = "/{userID}", headers = "Authorization", method = { RequestMethod.GET })
	public ResponseEntity<byte[]> fetchFile(@PathVariable("userID") Long userID) throws Exception {
		OutputResponse response = new OutputResponse();
		logger.debug("File download for userID" + userID);

		try {

			EmployeeSignature userSignID = employeeSignatureServiceImpl.fetchSignature(userID);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION,
					"inline; filename=\"" + userSignID.getFileName() + "\"");
			responseHeaders.set("filename", userSignID.getFileName());

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(userSignID.getFileType()))
					.headers(responseHeaders).body(userSignID.getSignature());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("File download for userID failed with exception " + e.getMessage(), e);
			throw new Exception("Error while downloading file. Please contact administrator..");

		}

	}

	@Operation(summary = "Sign exist file")
	@RequestMapping(value = "/signexist/{userID}", headers = "Authorization", method = { RequestMethod.GET })
	public String existFile(@PathVariable("userID") Long userID) throws Exception {
		OutputResponse response = new OutputResponse();
		logger.debug("File download for userID" + userID);

		try {

			Boolean userSignID = employeeSignatureServiceImpl.existSignature(userID);

			Boolean signatureActive = employeeSignatureServiceImpl.isSignatureActive(userID);

			// Create JSON response with both fields
			JsonObject responseData = new JsonObject();
			responseData.addProperty("response", userSignID.toString());
			responseData.addProperty("isSignatureActive", signatureActive.toString());

			// Set the response (existing setResponse method will handle it)
			response.setResponse(responseData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("File download for userID failed with exception " + e.getMessage(), e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();
	}
}
