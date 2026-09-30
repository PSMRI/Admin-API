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
package com.iemr.admin.repository.villagemapping;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class VillageUpdateRepository {
	private static final String SELECT_MAPPING_FOR_UPDATE =
			"SELECT UserID, Villageid, VillageName, Deleted FROM db_iemr.m_userservicerolemapping"
					+ " WHERE USRMappingID = :uSRMappingID FOR UPDATE";

	private static final String SELECT_USER_NAME =
			"SELECT UserName FROM db_iemr.m_user WHERE UserID = :userID";

	private static final String SELECT_VILLAGE_NAME =
			"SELECT VillageName FROM db_iemr.m_districtbranchmapping"
					+ " WHERE DistrictBranchID = :villageID AND (Deleted IS NULL OR Deleted = b'0')";

	private static final String SET_MAPPING_VILLAGES =
			"UPDATE db_iemr.m_userservicerolemapping SET Villageid = :villageIDs, VillageName = :villageNames,"
					+ " ModifiedBy = :modifiedBy WHERE USRMappingID = :uSRMappingID";

	private static final String SET_PERMANENT_VILLAGE =
			"UPDATE db_identity.i_beneficiaryaddress SET PermVillageId = :newVillageID, PermVillage = :newVillageName,"
					+ " ModifiedBy = :modifiedBy WHERE CreatedBy = :userName AND PermVillageId = :oldVillageID";

	private static final String SET_CURRENT_VILLAGE =
			"UPDATE db_identity.i_beneficiaryaddress SET CurrVillageId = :newVillageID, CurrVillage = :newVillageName,"
					+ " ModifiedBy = :modifiedBy WHERE CreatedBy = :userName AND CurrVillageId = :oldVillageID";

	private static final String SET_ALL_ADDRESS_VILLAGES =
			"UPDATE db_identity.i_beneficiaryaddress SET PermVillageId = :newVillageID, PermVillage = :newVillageName,"
					+ " CurrVillageId = :newVillageID, CurrVillage = :newVillageName, ModifiedBy = :modifiedBy"
					+ " WHERE CreatedBy = :userName";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Locks the mapping row for the rest of the transaction and returns
	 * {UserID, Villageid, VillageName, Deleted}, or null when it does not exist.
	 */
	public Object[] lockMapping(Integer uSRMappingID) {
		Query query = entityManager.createNativeQuery(SELECT_MAPPING_FOR_UPDATE);
		query.setParameter("uSRMappingID", uSRMappingID);
		List<?> rows = query.getResultList();
		return rows.isEmpty() ? null : (Object[]) rows.get(0);
	}

	public String userName(Integer userID) {
		return single(SELECT_USER_NAME, "userID", userID);
	}

	public String villageName(Integer villageID) {
		return single(SELECT_VILLAGE_NAME, "villageID", villageID);
	}

	private String single(String sql, String parameter, Integer value) {
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(parameter, value);
		List<?> rows = query.getResultList();
		return rows.isEmpty() || rows.get(0) == null ? null : rows.get(0).toString();
	}

	public long updateMappingVillages(Integer uSRMappingID, String villageIDs, String villageNames,
			String modifiedBy) {
		Query query = entityManager.createNativeQuery(SET_MAPPING_VILLAGES);
		query.setParameter("villageIDs", villageIDs);
		query.setParameter("villageNames", villageNames);
		query.setParameter("modifiedBy", modifiedBy);
		query.setParameter("uSRMappingID", uSRMappingID);
		return query.executeUpdate();
	}

	public long updatePermanentVillage(String userName, Integer oldVillageID, Integer newVillageID,
			String newVillageName, String modifiedBy) {
		return updateAddressVillage(SET_PERMANENT_VILLAGE, userName, oldVillageID, newVillageID, newVillageName,
				modifiedBy);
	}

	public long updateCurrentVillage(String userName, Integer oldVillageID, Integer newVillageID,
			String newVillageName, String modifiedBy) {
		return updateAddressVillage(SET_CURRENT_VILLAGE, userName, oldVillageID, newVillageID, newVillageName,
				modifiedBy);
	}

	public long updateAllAddressVillages(String userName, Integer newVillageID, String newVillageName,
			String modifiedBy) {
		Query query = entityManager.createNativeQuery(SET_ALL_ADDRESS_VILLAGES);
		query.setParameter("newVillageID", newVillageID);
		query.setParameter("newVillageName", newVillageName);
		query.setParameter("modifiedBy", modifiedBy);
		query.setParameter("userName", userName);
		return query.executeUpdate();
	}

	private long updateAddressVillage(String sql, String userName, Integer oldVillageID, Integer newVillageID,
			String newVillageName, String modifiedBy) {
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("newVillageID", newVillageID);
		query.setParameter("newVillageName", newVillageName);
		query.setParameter("modifiedBy", modifiedBy);
		query.setParameter("userName", userName);
		query.setParameter("oldVillageID", oldVillageID);
		return query.executeUpdate();
	}
}
