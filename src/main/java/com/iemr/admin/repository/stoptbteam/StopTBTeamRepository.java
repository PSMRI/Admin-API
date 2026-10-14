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
 * Stop TB teams are MMU vans. A user's team is stored the way AMRIT already
 * links users to vans (m_userparkingplacemap -> m_uservanmapping), under the
 * user's Stop TB ProviderServiceMapID, so van-based logins see the same team.
 */
@Repository
public class StopTBTeamRepository {
	private static final String NOT_DELETED = "(%s IS NULL OR %s = b'0')";

	private static String notDeleted(String column) {
		return String.format(NOT_DELETED, column, column);
	}

	private static final String SELECT_STATE_VANS =
			"SELECT v.VanID, v.VanName, v.VehicalNo, v.ParkingPlaceID, pp.ParkingPlaceName"
					+ " FROM m_van v"
					+ " JOIN m_providerservicemapping p ON p.ProviderServiceMapID = v.ProviderServiceMapID"
					+ " JOIN m_servicemaster s ON s.ServiceID = p.ServiceID"
					+ " LEFT JOIN m_parkingplace pp ON pp.ParkingPlaceID = v.ParkingPlaceID"
					+ " WHERE s.ServiceName = 'MMU' AND p.StateID = :stateID"
					+ " AND p.ServiceProviderID = :serviceProviderID"
					+ " AND " + notDeleted("p.Deleted") + " AND " + notDeleted("v.Deleted")
					+ " ORDER BY v.VanName, v.VanID";

	private static final String SELECT_VAN =
			"SELECT v.VanID, v.ParkingPlaceID, p.StateID, p.ServiceProviderID"
					+ " FROM m_van v"
					+ " JOIN m_providerservicemapping p ON p.ProviderServiceMapID = v.ProviderServiceMapID"
					+ " JOIN m_servicemaster s ON s.ServiceID = p.ServiceID"
					+ " WHERE v.VanID = :vanID AND s.ServiceName = 'MMU'"
					+ " AND " + notDeleted("p.Deleted") + " AND " + notDeleted("v.Deleted");

	private static final String SELECT_STOPTB_SERVICE =
			"SELECT p.StateID, p.ServiceProviderID FROM m_providerservicemapping p"
					+ " JOIN m_servicemaster s ON s.ServiceID = p.ServiceID"
					+ " WHERE p.ProviderServiceMapID = :providerServiceMapID AND s.ServiceName = 'Stop TB'";

	private static final String SELECT_USER_TEAM =
			"SELECT uv.VanID, v.VanName FROM m_userparkingplacemap up"
					+ " JOIN m_uservanmapping uv ON uv.UserParkingPlaceMapID = up.UserParkingPlaceMapID"
					+ " JOIN m_van v ON v.VanID = uv.VanID"
					+ " WHERE up.UserID = :userID AND up.ProviderServiceMapID = :providerServiceMapID"
					+ " AND " + notDeleted("up.Deleted") + " AND " + notDeleted("uv.Deleted")
					+ " ORDER BY uv.UserVanMapID DESC";

	private static final String SELECT_TEAM_MEMBERS =
			"SELECT DISTINCT u.UserID, u.UserName, u.FirstName, u.LastName FROM m_userparkingplacemap up"
					+ " JOIN m_uservanmapping uv ON uv.UserParkingPlaceMapID = up.UserParkingPlaceMapID"
					+ " JOIN m_user u ON u.UserID = up.UserID"
					+ " WHERE uv.VanID = :vanID AND up.ProviderServiceMapID = :providerServiceMapID"
					+ " AND " + notDeleted("up.Deleted") + " AND " + notDeleted("uv.Deleted")
					+ " AND " + notDeleted("u.Deleted")
					+ " ORDER BY u.UserName";

	private static final String REMOVE_USER_VANS =
			"UPDATE m_uservanmapping uv"
					+ " JOIN m_userparkingplacemap up ON up.UserParkingPlaceMapID = uv.UserParkingPlaceMapID"
					+ " SET uv.Deleted = b'1', uv.ModifiedBy = :modifiedBy"
					+ " WHERE up.UserID = :userID AND up.ProviderServiceMapID = :providerServiceMapID"
					+ " AND " + notDeleted("uv.Deleted");

	private static final String REMOVE_USER_PARKING_PLACES =
			"UPDATE m_userparkingplacemap SET Deleted = b'1', ModifiedBy = :modifiedBy"
					+ " WHERE UserID = :userID AND ProviderServiceMapID = :providerServiceMapID"
					+ " AND " + notDeleted("Deleted");

	private static final String INSERT_USER_PARKING_PLACE =
			"INSERT INTO m_userparkingplacemap (UserID, ParkingPlaceID, StateID, ProviderServiceMapID, CreatedBy)"
					+ " VALUES (:userID, :parkingPlaceID, :stateID, :providerServiceMapID, :createdBy)";

	private static final String INSERT_USER_VAN =
			"INSERT INTO m_uservanmapping (UserParkingPlaceMapID, VanID, ProviderServiceMapID, CreatedBy)"
					+ " VALUES (:userParkingPlaceMapID, :vanID, :providerServiceMapID, :createdBy)";

	@PersistenceContext
	private EntityManager entityManager;

	/** {VanID, VanName, VehicalNo, ParkingPlaceID, ParkingPlaceName} of the state's MMU vans. */
	@SuppressWarnings("unchecked")
	public List<Object[]> stateVans(Integer stateID, Integer serviceProviderID) {
		return entityManager.createNativeQuery(SELECT_STATE_VANS).setParameter("stateID", stateID)
				.setParameter("serviceProviderID", serviceProviderID).getResultList();
	}

	/** {VanID, ParkingPlaceID, StateID, ServiceProviderID} of an active MMU van, or null. */
	public Object[] van(Integer vanID) {
		return first(entityManager.createNativeQuery(SELECT_VAN).setParameter("vanID", vanID));
	}

	/** {StateID, ServiceProviderID} when the ID is a Stop TB service line mapping, else null. */
	public Object[] stopTBService(Integer providerServiceMapID) {
		return first(entityManager.createNativeQuery(SELECT_STOPTB_SERVICE)
				.setParameter("providerServiceMapID", providerServiceMapID));
	}

	/** {VanID, VanName} of the user's current team, or null. */
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

	/** Soft-deletes every team link the user has under this Stop TB service line. */
	public void removeUserFromTeams(Integer userID, Integer providerServiceMapID, String modifiedBy) {
		entityManager.createNativeQuery(REMOVE_USER_VANS).setParameter("userID", userID)
				.setParameter("providerServiceMapID", providerServiceMapID).setParameter("modifiedBy", modifiedBy)
				.executeUpdate();
		entityManager.createNativeQuery(REMOVE_USER_PARKING_PLACES).setParameter("userID", userID)
				.setParameter("providerServiceMapID", providerServiceMapID).setParameter("modifiedBy", modifiedBy)
				.executeUpdate();
	}

	/** Links the user to the van through the van's parking place. */
	public void addUserToTeam(Integer userID, Integer vanID, Integer parkingPlaceID, Integer stateID,
			Integer providerServiceMapID, String createdBy) {
		entityManager.createNativeQuery(INSERT_USER_PARKING_PLACE).setParameter("userID", userID)
				.setParameter("parkingPlaceID", parkingPlaceID).setParameter("stateID", stateID)
				.setParameter("providerServiceMapID", providerServiceMapID).setParameter("createdBy", createdBy)
				.executeUpdate();
		Number userParkingPlaceMapID = (Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()")
				.getSingleResult();
		entityManager.createNativeQuery(INSERT_USER_VAN)
				.setParameter("userParkingPlaceMapID", userParkingPlaceMapID.intValue()).setParameter("vanID", vanID)
				.setParameter("providerServiceMapID", providerServiceMapID).setParameter("createdBy", createdBy)
				.executeUpdate();
	}

	private Object[] first(Query query) {
		List<?> rows = query.setMaxResults(1).getResultList();
		return rows.isEmpty() ? null : (Object[]) rows.get(0);
	}
}
