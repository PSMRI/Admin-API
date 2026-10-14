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
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.repository.stoptbteam.StopTBTeamRepository;

/**
 * Stop TB teams for Work Location Mapping (AMM-2496). A team is a van (the
 * camp laptop's STOPTB_VAN_ID). A team has many users; a user is in one team,
 * stored as VanID on all of their active Stop TB m_userservicerolemapping rows.
 */
@Service
public class StopTBTeamService {
	@Autowired
	private StopTBTeamRepository teamRepository;

	// Service line whose vans are the teams: MMU today, the Stop TB service
	// once it has its own vans.
	@Value("${stoptb.team.van-service:MMU}")
	private String vanService;

	/** The state's team vans, for the Team dropdown. */
	public List<Map<String, Object>> stateTeams(Integer stateID, Integer serviceProviderID) {
		if (stateID == null || serviceProviderID == null) {
			throw new IllegalArgumentException("State and service provider are required");
		}
		List<Map<String, Object>> teams = new ArrayList<>();
		for (Object[] row : teamRepository.stateVans(vanService, stateID, serviceProviderID)) {
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

	/** The user's team ({vanID, vanName}), or an empty map. */
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
	 * Checks a mapping's team before it is saved: a team is only allowed on a
	 * Stop TB service line, and must be a van of that service line's state.
	 */
	public void validateTeam(Integer providerServiceMapID, Integer vanID) {
		if (vanID == null) {
			return;
		}
		Object[] stopTB = providerServiceMapID == null ? null : teamRepository.stopTBService(providerServiceMapID);
		if (stopTB == null) {
			throw new IllegalArgumentException("A team can only be set on a Stop TB mapping");
		}
		Object[] van = teamRepository.van(vanService, vanID);
		if (van == null) {
			throw new IllegalArgumentException("Team " + vanID + " is not an active " + vanService + " van");
		}
		if (!Objects.equals(toInt(van[1]), toInt(stopTB[0])) || !Objects.equals(toInt(van[2]), toInt(stopTB[1]))) {
			throw new IllegalArgumentException("Team " + vanID + " is not in this user's state");
		}
	}

	/**
	 * After a Stop TB mapping is saved, gives all of the user's active Stop TB
	 * rows the same team, so one user is never in two teams. No-op for other
	 * service lines.
	 */
	@Transactional(rollbackFor = Exception.class)
	public void applyTeam(Integer userID, Integer providerServiceMapID, Integer vanID) {
		if (userID == null || providerServiceMapID == null) {
			return;
		}
		teamRepository.setUserTeam(userID, providerServiceMapID, vanID);
	}

	private static Integer toInt(Object value) {
		return value == null ? null : ((Number) value).intValue();
	}
}
