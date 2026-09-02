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
package com.iemr.admin.controller.username;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.model.username.UsernameRenameRequest;
import com.iemr.admin.service.username.UsernameRenameService;
import com.iemr.admin.utils.mapper.OutputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Username rename, kept off the employee edit screen on purpose: changing a
 * username has to be propagated to every CreatedBy/ModifiedBy column that
 * records it, which is a different operation from editing a user's details.
 */
@RestController
@RequestMapping(value = "/username")
public class UsernameRenameController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private UsernameRenameService usernameRenameService;

	@Operation(summary = "Rename a username and repoint its audit records")
	@RequestMapping(value = "/renameUsername", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String renameUsername(@RequestBody UsernameRenameRequest renameRequest, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("renameUsername received request");
			response.setResponse(OutputMapper.gsonWithoutExpose().toJson(usernameRenameService.rename(renameRequest)));
		} catch (Exception e) {
			logger.error("renameUsername failed", e);
			response.setError(e);
		}
		logger.info("renameUsername sending response");
		return response.toString();
	}
}
