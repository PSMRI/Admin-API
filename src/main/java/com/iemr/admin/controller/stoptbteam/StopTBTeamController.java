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
package com.iemr.admin.controller.stoptbteam;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.model.stoptbteam.TeamAssignRequest;
import com.iemr.admin.service.stoptbteam.StopTBTeamService;
import com.iemr.admin.utils.mapper.OutputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

/** Stop TB teams (MMU vans) for Work Location Mapping (AMM-2496). */
@RestController
@RequestMapping(value = "/stoptb/team")
public class StopTBTeamController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private StopTBTeamService teamService;

	@Operation(summary = "Stop TB teams (MMU vans) of a state")
	@GetMapping(value = "/vans", produces = MediaType.APPLICATION_JSON)
	public String getStateTeams(@RequestParam("stateID") Integer stateID,
			@RequestParam("serviceProviderID") Integer serviceProviderID) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(OutputMapper.gsonWithoutExpose().toJson(teamService.stateTeams(stateID, serviceProviderID)));
		} catch (Exception e) {
			logger.error("Stop TB teams for state {} failed", stateID, e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "A Stop TB user's current team")
	@GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON)
	public String getUserTeam(@RequestParam("userID") Integer userID,
			@RequestParam("providerServiceMapID") Integer providerServiceMapID) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(OutputMapper.gsonWithoutExpose().toJson(teamService.userTeam(userID, providerServiceMapID)));
		} catch (Exception e) {
			logger.error("Stop TB team of user {} failed", userID, e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Members of a Stop TB team")
	@GetMapping(value = "/members", produces = MediaType.APPLICATION_JSON)
	public String getTeamMembers(@RequestParam("vanID") Integer vanID,
			@RequestParam("providerServiceMapID") Integer providerServiceMapID) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(OutputMapper.gsonWithoutExpose().toJson(teamService.teamMembers(vanID, providerServiceMapID)));
		} catch (Exception e) {
			logger.error("Stop TB team {} members failed", vanID, e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Move a Stop TB user into a team, or out of their team when vanID is null")
	@PostMapping(value = "/assign", produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String assignTeam(@RequestBody TeamAssignRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(OutputMapper.gsonWithoutExpose().toJson(teamService.assignTeam(request)));
		} catch (Exception e) {
			logger.error("Stop TB team assignment failed", e);
			response.setError(e);
		}
		return response.toString();
	}
}
