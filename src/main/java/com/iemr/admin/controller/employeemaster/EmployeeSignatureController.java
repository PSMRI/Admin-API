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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.employeemaster.EmployeeSignature;
import com.iemr.admin.service.employeemaster.EmployeeSignatureServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;


@PropertySource("classpath:application.properties")

@RestController
@RequestMapping(value = "/signature1")
public class EmployeeSignatureController {

	@Autowired
	EmployeeSignatureServiceImpl employeeSignatureServiceImpl;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Operation(summary = "Upload")
	@PostMapping(value = "/upload", headers = "Authorization", produces = {
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
	@GetMapping(value = "/{userID}", headers = "Authorization")
	public ResponseEntity<byte[]> fetchFile(@PathVariable("userID") Long userID) throws Exception {
		logger.debug("File download for userID" + userID);

		try {

			EmployeeSignature userSignID = employeeSignatureServiceImpl.fetchSignature(userID);
			HttpHeaders responseHeaders = new HttpHeaders();
			ContentDisposition cd = ContentDisposition.attachment()
					.filename(userSignID.getFileName(), StandardCharsets.UTF_8).build();
			responseHeaders.setContentDisposition(cd);

			MediaType mediaType;
			try {
				mediaType = MediaType.parseMediaType(userSignID.getFileType());
			} catch (InvalidMediaTypeException | NullPointerException e) {
				mediaType = MediaType.APPLICATION_OCTET_STREAM;
			}

			byte[] fileBytes = userSignID.getSignature(); // MUST be byte[]

			return ResponseEntity.ok().headers(responseHeaders).contentType(mediaType).contentLength(fileBytes.length)
					.body(fileBytes);

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
			response.setResponse(userSignID.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("File download for userID failed with exception " + e.getMessage(), e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Active or DeActive user Signature")
	@PostMapping(value = "/activateOrdeActivateSignature", headers = "Authorization", produces = { "application/json" })
	public String ActivateUser(@RequestBody String activateUser, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			EmployeeSignature empSignature = employeeSignatureServiceImpl.updateUserSignatureStatus(activateUser);
			boolean active = empSignature.getDeleted() == null ? false : !empSignature.getDeleted();
			response.setResponse("{\"userID\":" + empSignature.getUserID() + ",\"active\":" + active + "}");
		} catch (Exception e) {
			logger.error("Active or Deactivate User Signature failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}
}
