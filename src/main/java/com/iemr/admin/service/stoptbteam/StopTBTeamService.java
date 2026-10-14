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
package com.iemr.admin.service.stoptbteam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.model.stoptbteam.TeamAssignRequest;
import com.iemr.admin.repository.stoptbteam.StopTBTeamRepository;

/**
 * Stop TB team (MMU van) membership for Work Location Mapping. A team has many
 * users; a user is in at most one team per Stop TB service line, so assigning a
 * new team moves the user.
 */
@Service
public class StopTBTeamService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private StopTBTeamRepository teamRepository;

	/** The MMU vans of the state, for the Team dropdown. */
	public List<Map<String, Object>> stateTeams(Integer stateID, Integer serviceProviderID) {
		if (stateID == null || serviceProviderID == null) {
			throw new IllegalArgumentException("State and service provider are required");
		}
		List<Map<String, Object>> teams = new ArrayList<>();
		for (Object[] row : teamRepository.stateVans(stateID, serviceProviderID)) {
			Map<String, Object> team = new LinkedHashMap<>();
			team.put("vanID", toInt(row[0]));
			team.put("vanName", row[1]);
			team.put("vehicalNo", row[2]);
			team.put("parkingPlaceID", toInt(row[3]));
			team.put("parkingPlaceName", row[4]);
			teams.add(team);
		}
		return teams;
	}

	/** The user's current team ({vanID, vanName}), or an empty map. */
	public Map<String, Object> userTeam(Integer userID, Integer providerServiceMapID) {
		if (userID == null || providerServiceMapID == null) {
			throw new IllegalArgumentException("User and service line are required");
		}
		Map<String, Object> team = new LinkedHashMap<>();
		Object[] row = teamRepository.userTeam(userID, providerServiceMapID);
		if (row != null) {
			team.put("vanID", toInt(row[0]));
			team.put("vanName", row[1]);
		}
		return team;
	}

	/** The members of a team under this Stop TB service line. */
	public List<Map<String, Object>> teamMembers(Integer vanID, Integer providerServiceMapID) {
		if (vanID == null || providerServiceMapID == null) {
			throw new IllegalArgumentException("Team and service line are required");
		}
		List<Map<String, Object>> members = new ArrayList<>();
		for (Object[] row : teamRepository.teamMembers(vanID, providerServiceMapID)) {
			Map<String, Object> member = new LinkedHashMap<>();
			member.put("userID", toInt(row[0]));
			member.put("userName", row[1]);
			member.put("firstName", row[2]);
			member.put("lastName", row[3]);
			members.add(member);
		}
		return members;
	}

	/**
	 * Moves the user into the requested team, or out of any team when vanID is
	 * null. Old and new links change in one transaction.
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> assignTeam(TeamAssignRequest request) {
		if (request == null || request.getUserID() == null || request.getProviderServiceMapID() == null) {
			throw new IllegalArgumentException("User and service line are required");
		}
		String modifiedBy = request.getModifiedBy() == null ? "" : request.getModifiedBy().trim();
		if (modifiedBy.isEmpty()) {
			throw new IllegalArgumentException("Modified by is required");
		}
		Object[] stopTB = teamRepository.stopTBService(request.getProviderServiceMapID());
		if (stopTB == null) {
			throw new IllegalArgumentException("Teams can only be assigned on a Stop TB service line");
		}
		Integer stateID = toInt(stopTB[0]);
		Integer serviceProviderID = toInt(stopTB[1]);

		Object[] current = teamRepository.userTeam(request.getUserID(), request.getProviderServiceMapID());
		Integer currentVanID = current == null ? null : toInt(current[0]);
		if (java.util.Objects.equals(currentVanID, request.getVanID())) {
			return userTeam(request.getUserID(), request.getProviderServiceMapID());
		}

		Object[] van = null;
		if (request.getVanID() != null) {
			van = teamRepository.van(request.getVanID());
			if (van == null) {
				throw new IllegalArgumentException("Team " + request.getVanID() + " is not an active MMU van");
			}
			if (!java.util.Objects.equals(toInt(van[2]), stateID)
					|| !java.util.Objects.equals(toInt(van[3]), serviceProviderID)) {
				throw new IllegalArgumentException("Team " + request.getVanID() + " is not in this user's state");
			}
			if (van[1] == null) {
				throw new IllegalArgumentException("Team " + request.getVanID() + " has no parking place");
			}
		}

		teamRepository.removeUserFromTeams(request.getUserID(), request.getProviderServiceMapID(), modifiedBy);
		if (van != null) {
			teamRepository.addUserToTeam(request.getUserID(), request.getVanID(), toInt(van[1]), stateID,
					request.getProviderServiceMapID(), modifiedBy);
		}
		logger.info("Stop TB team for user {} on service line {}: {} -> {}", request.getUserID(),
				request.getProviderServiceMapID(), currentVanID, request.getVanID());
		return userTeam(request.getUserID(), request.getProviderServiceMapID());
	}

	private static Integer toInt(Object value) {
		return value == null ? null : ((Number) value).intValue();
	}
}
