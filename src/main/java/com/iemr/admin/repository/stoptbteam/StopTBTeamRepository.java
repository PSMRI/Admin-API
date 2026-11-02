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
package com.iemr.admin.repository.stoptbteam;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Stop TB teams (AMM-2496). A team is a van; the user's team is the VanID
 * on their m_userservicerolemapping rows. Team vans come from one service
 * line's vans (MMU today, see stoptb.team.van-service).
 */
@Repository
public class StopTBTeamRepository {
	private static String notDeleted(String column) {
		return "(" + column + " IS NULL OR " + column + " = b'0')";
	}

	private static final String STOPTB_SERVICE_LINES =
			"SELECT p.ProviderServiceMapID FROM m_providerservicemapping p"
					+ " JOIN m_servicemaster s ON s.ServiceID = p.ServiceID WHERE s.ServiceName = 'Stop TB'";

	private static final String SELECT_STATE_VANS =
			"SELECT v.VanID, v.VanName, v.VehicalNo, v.ParkingPlaceID, pp.ParkingPlaceName"
					+ " FROM m_van v"
					+ " JOIN m_providerservicemapping p ON p.ProviderServiceMapID = v.ProviderServiceMapID"
					+ " JOIN m_servicemaster s ON s.ServiceID = p.ServiceID"
					+ " LEFT JOIN m_parkingplace pp ON pp.ParkingPlaceID = v.ParkingPlaceID"
					+ " WHERE s.ServiceName = :vanService AND p.StateID = :stateID"
					+ " AND p.ServiceProviderID = :serviceProviderID"
					+ " AND " + notDeleted("p.Deleted") + " AND " + notDeleted("v.Deleted")
					+ " ORDER BY v.VanName, v.VanID";

	private static final String SELECT_VAN =
			"SELECT v.VanID, p.StateID, p.ServiceProviderID FROM m_van v"
					+ " JOIN m_providerservicemapping p ON p.ProviderServiceMapID = v.ProviderServiceMapID"
					+ " JOIN m_servicemaster s ON s.ServiceID = p.ServiceID"
					+ " WHERE v.VanID = :vanID AND s.ServiceName = :vanService"
					+ " AND " + notDeleted("p.Deleted") + " AND " + notDeleted("v.Deleted");

	private static final String SELECT_STOPTB_SERVICE =
			"SELECT p.StateID, p.ServiceProviderID FROM m_providerservicemapping p"
					+ " WHERE p.ProviderServiceMapID = :providerServiceMapID"
					+ " AND p.ProviderServiceMapID IN (" + STOPTB_SERVICE_LINES + ")";

	private static final String SELECT_USER_TEAM =
			"SELECT m.VanID, v.VanName FROM m_userservicerolemapping m"
					+ " JOIN m_van v ON v.VanID = m.VanID"
					+ " WHERE m.UserID = :userID AND m.ProviderServiceMapID = :providerServiceMapID"
					+ " AND " + notDeleted("m.Deleted")
					+ " ORDER BY m.LastModDate DESC, m.USRMappingID DESC";

	private static final String SELECT_TEAM_MEMBERS =
			"SELECT DISTINCT u.UserID, u.UserName, u.FirstName, u.LastName FROM m_userservicerolemapping m"
					+ " JOIN m_user u ON u.UserID = m.UserID"
					+ " WHERE m.VanID = :vanID AND m.ProviderServiceMapID = :providerServiceMapID"
					+ " AND " + notDeleted("m.Deleted") + " AND " + notDeleted("u.Deleted")
					+ " ORDER BY u.UserName";

	// One team per user: every active Stop TB row of the user gets the same VanID.
	private static final String SET_USER_TEAM =
			"UPDATE m_userservicerolemapping SET VanID = :vanID"
					+ " WHERE UserID = :userID AND ProviderServiceMapID = :providerServiceMapID"
					+ " AND " + notDeleted("Deleted")
					+ " AND ProviderServiceMapID IN (" + STOPTB_SERVICE_LINES + ")";

	@PersistenceContext
	private EntityManager entityManager;

	/** {VanID, VanName, VehicalNo, ParkingPlaceID, ParkingPlaceName} of the state's team vans. */
	@SuppressWarnings("unchecked")
	public List<Object[]> stateVans(String vanService, Integer stateID, Integer serviceProviderID) {
		return entityManager.createNativeQuery(SELECT_STATE_VANS).setParameter("vanService", vanService)
				.setParameter("stateID", stateID).setParameter("serviceProviderID", serviceProviderID)
				.getResultList();
	}

	/** {VanID, StateID, ServiceProviderID} of an active team van, or null. */
	public Object[] van(String vanService, Integer vanID) {
		return first(entityManager.createNativeQuery(SELECT_VAN).setParameter("vanService", vanService)
				.setParameter("vanID", vanID));
	}

	/** {StateID, ServiceProviderID} when the ID is a Stop TB service line, else null. */
	public Object[] stopTBService(Integer providerServiceMapID) {
		return first(entityManager.createNativeQuery(SELECT_STOPTB_SERVICE)
				.setParameter("providerServiceMapID", providerServiceMapID));
	}

	/** {VanID, VanName} of the user's team, or null. */
	public Object[] userTeam(Integer userID, Integer providerServiceMapID) {
		return first(entityManager.createNativeQuery(SELECT_USER_TEAM).setParameter("userID", userID)
				.setParameter("providerServiceMapID", providerServiceMapID));
	}

	/** {UserID, UserName, FirstName, LastName} of the team's members. */
	@SuppressWarnings("unchecked")
	public List<Object[]> teamMembers(Integer vanID, Integer providerServiceMapID) {
		return entityManager.createNativeQuery(SELECT_TEAM_MEMBERS).setParameter("vanID", vanID)
				.setParameter("providerServiceMapID", providerServiceMapID).getResultList();
	}

	/** Puts the user in the team (or none) on all their active Stop TB rows. */
	public int setUserTeam(Integer userID, Integer providerServiceMapID, Integer vanID) {
		return entityManager.createNativeQuery(SET_USER_TEAM).setParameter("vanID", vanID)
				.setParameter("userID", userID).setParameter("providerServiceMapID", providerServiceMapID)
				.executeUpdate();
	}

	private Object[] first(Query query) {
		List<?> rows = query.setMaxResults(1).getResultList();
		return rows.isEmpty() ? null : (Object[]) rows.get(0);
	}
}
