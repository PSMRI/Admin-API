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

import org.springframework.stereotype.Repository;

import com.iemr.admin.repository.username.UsernameAuditTables.AuditTable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Native-SQL sweeps behind the username rename.
 *
 * <p>Table and column names are interpolated because SQL will not bind them as
 * parameters; they come only from the {@link UsernameAuditTables} whitelist and
 * never from request input. Every username value IS bound, so no caller-supplied
 * string ever reaches the statement text.
 *
 * <p>Schemas are fully qualified (db_iemr / db_identity) following the existing
 * cross-schema precedent in Common-API. Both live on the same MySQL instance,
 * which is what lets a single transaction span them.
 */
@Repository
public class UsernameRenameRepository {

	@PersistenceContext
	private EntityManager entityManager;

	/** Rows in {@code table} attributed to {@code userName} in either audit column. */
	public long countAffected(AuditTable table, String userName) {
		String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = :userName OR %s = :userName",
				table.getQualifiedName(), table.getCreatedByColumn(), table.getModifiedByColumn());
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("userName", userName);
		return toLong(query.getSingleResult());
	}

	/**
	 * Repoints both audit columns in one table.
	 *
	 * <p>Deliberately two statements rather than one combined UPDATE: a single
	 * {@code SET CreatedBy=:new, ModifiedBy=:new WHERE CreatedBy=:old} would
	 * overwrite ModifiedBy on rows a DIFFERENT user last modified, destroying
	 * their attribution, and would miss rows this user only modified.
	 *
	 * @return rows touched across both statements (a row updated in each counts twice)
	 */
	public long renameInTable(AuditTable table, String oldUserName, String newUserName) {
		long updated = executeUpdate(table.getQualifiedName(), table.getCreatedByColumn(), oldUserName, newUserName);
		updated += executeUpdate(table.getQualifiedName(), table.getModifiedByColumn(), oldUserName, newUserName);
		return updated;
	}

	private long executeUpdate(String qualifiedName, String column, String oldUserName, String newUserName) {
		String sql = String.format("UPDATE %s SET %s = :newUserName WHERE %s = :oldUserName", qualifiedName, column,
				column);
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("newUserName", newUserName);
		query.setParameter("oldUserName", oldUserName);
		return query.executeUpdate();
	}

	/**
	 * Updates the identity row itself. EmployeeID tracks the username per the
	 * existing bulk-registration convention; the contact columns are only
	 * rewritten where the username is known to be the user's mobile number.
	 */
	public long renameUserRow(String oldUserName, String newUserName, boolean updateContactFields) {
		String sql = updateContactFields
				? "UPDATE db_iemr.m_user SET UserName = :newUserName, EmployeeID = :newUserName, "
						+ "EmergencyContactNo = :newUserName, ContactNo = :newUserName WHERE UserName = :oldUserName"
				: "UPDATE db_iemr.m_user SET UserName = :newUserName, EmployeeID = :newUserName "
						+ "WHERE UserName = :oldUserName";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("newUserName", newUserName);
		query.setParameter("oldUserName", oldUserName);
		return query.executeUpdate();
	}

	public boolean userExists(String userName) {
		Query query = entityManager
				.createNativeQuery("SELECT COUNT(*) FROM db_iemr.m_user WHERE UserName = :userName");
		query.setParameter("userName", userName);
		return toLong(query.getSingleResult()) > 0;
	}

	/**
	 * Both UserName and EmployeeID carry UNIQUE keys on m_user and the rename
	 * writes the new value into both, so either being taken blocks the rename.
	 */
	public boolean userNameOrEmployeeIdTaken(String userName) {
		Query query = entityManager.createNativeQuery(
				"SELECT COUNT(*) FROM db_iemr.m_user WHERE UserName = :userName OR EmployeeID = :userName");
		query.setParameter("userName", userName);
		return toLong(query.getSingleResult()) > 0;
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
