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
package com.iemr.admin.repository.username;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.iemr.admin.repository.username.UsernameAuditTables.AuditTable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class UsernameRenameRepository {
	/**
	 * Schema, table and column names cannot be bound as parameters, so they are
	 * interpolated. Every identifier is checked against this before it reaches a
	 * statement, so a value that is not a plain SQL identifier can never be
	 * concatenated in. All caller-supplied data is bound, never interpolated.
	 */
	private static final String COUNT_BY_USER_NAME =
			"SELECT COUNT(*) FROM db_iemr.m_user WHERE UserName = :value AND UserID <> :excludeUserID";

	private static final String COUNT_BY_EMPLOYEE_ID =
			"SELECT COUNT(*) FROM db_iemr.m_user WHERE EmployeeID = :value AND UserID <> :excludeUserID";

	private static final String SET_USER_NAME =
			"UPDATE db_iemr.m_user SET UserName = :newUserName WHERE UserID = :userID";

	private static final String SET_USER_NAME_AND_CONTACTS =
			"UPDATE db_iemr.m_user SET UserName = :newUserName, EmergencyContactNo = :newUserName,"
					+ " ContactNo = :newUserName WHERE UserID = :userID";

	private static final String SET_EMPLOYEE_ID =
			"UPDATE db_iemr.m_user SET EmployeeID = :newEmployeeId WHERE UserID = :userID";

	private static final String SET_USER_NAME_AND_EMPLOYEE_ID =
			"UPDATE db_iemr.m_user SET UserName = :newUserName, EmployeeID = :newEmployeeId WHERE UserID = :userID";

	private static final String SET_USER_NAME_CONTACTS_AND_EMPLOYEE_ID =
			"UPDATE db_iemr.m_user SET UserName = :newUserName, EmergencyContactNo = :newUserName,"
					+ " ContactNo = :newUserName, EmployeeID = :newEmployeeId WHERE UserID = :userID";

	@PersistenceContext
	private EntityManager entityManager;

	public long renameInTable(AuditTable table, String oldUserName, String newUserName) {
		Query query = entityManager.createNativeQuery(table.getRenameSql());
		query.setParameter("newUserName", newUserName);
		query.setParameter("oldUserName", oldUserName);
		return query.executeUpdate();
	}

	public String currentUserName(Integer userID) {
		return single("SELECT UserName FROM db_iemr.m_user WHERE UserID = :userID", userID);
	}

	public String currentEmployeeId(Integer userID) {
		return single("SELECT EmployeeID FROM db_iemr.m_user WHERE UserID = :userID", userID);
	}

	private String single(String sql, Integer userID) {
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("userID", userID);
		List<?> rows = query.getResultList();
		return rows.isEmpty() ? null : (String) rows.get(0);
	}

	public long renameUserRow(Integer userID, String newUserName, String newEmployeeId,
			boolean updateContactFields) {
		String sql = selectUserUpdate(newUserName, newEmployeeId, updateContactFields);
		if (sql == null) {
			return 0;
		}

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("userID", userID);
		if (newUserName != null) {
			query.setParameter("newUserName", newUserName);
		}
		if (newEmployeeId != null) {
			query.setParameter("newEmployeeId", newEmployeeId);
		}
		return query.executeUpdate();
	}

	private static String selectUserUpdate(String newUserName, String newEmployeeId, boolean updateContactFields) {
		if (newUserName == null) {
			return newEmployeeId == null ? null : SET_EMPLOYEE_ID;
		}
		if (newEmployeeId == null) {
			return updateContactFields ? SET_USER_NAME_AND_CONTACTS : SET_USER_NAME;
		}
		return updateContactFields ? SET_USER_NAME_CONTACTS_AND_EMPLOYEE_ID : SET_USER_NAME_AND_EMPLOYEE_ID;
	}

	public boolean userNameTaken(String userName, Integer excludeUserID) {
		return countMatching(COUNT_BY_USER_NAME, userName, excludeUserID) > 0;
	}

	public boolean employeeIdTaken(String employeeId, Integer excludeUserID) {
		return countMatching(COUNT_BY_EMPLOYEE_ID, employeeId, excludeUserID) > 0;
	}

	private long countMatching(String sql, String value, Integer excludeUserID) {
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("value", value);
		query.setParameter("excludeUserID", excludeUserID);
		return toLong(query.getSingleResult());
	}

	private long toLong(Object result) {
		if (result == null) {
			return 0L;
		}
		if (result instanceof BigInteger bigInteger) {
			return bigInteger.longValue();
		}
		return ((Number) result).longValue();
	}
}
