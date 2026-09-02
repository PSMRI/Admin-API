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
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Repoints both audit columns on the rows this user created.
	 *
	 * <p>Driven by primary key through a derived table rather than filtering the
	 * UPDATE on the audit column directly. The extra {@code SELECT ... AS temp}
	 * wrapper is required by MySQL, which will not read from the same table an
	 * UPDATE targets unless the subquery is materialised (error 1093).
	 *
	 * @return rows updated
	 */
	public long renameInTable(AuditTable table, String oldUserName, String newUserName) {
		String sql = String.format(
				"UPDATE %1$s SET %2$s = :newUserName, %3$s = :newUserName "
						+ "WHERE %4$s IN (SELECT %4$s FROM (SELECT %4$s FROM %1$s WHERE %2$s = :oldUserName) AS temp)",
				table.getQualifiedName(), table.getCreatedByColumn(), table.getModifiedByColumn(),
				table.getPrimaryKeyColumn());
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("newUserName", newUserName);
		query.setParameter("oldUserName", oldUserName);
		return query.executeUpdate();
	}

	/** Current Employee ID for a user, or null where none is set. */
	public String currentEmployeeId(String userName) {
		Query query = entityManager
				.createNativeQuery("SELECT EmployeeID FROM db_iemr.m_user WHERE UserName = :userName");
		query.setParameter("userName", userName);
		List<?> rows = query.getResultList();
		return rows.isEmpty() ? null : (String) rows.get(0);
	}

	/**
	 * Updates the identity row, touching only the columns that actually change.
	 *
	 * <p>Either new value may be null, meaning "leave alone". The contact
	 * columns follow the username, so they are only rewritten alongside it.
	 *
	 * @return rows updated, or 0 when there was nothing to change
	 */
	public long renameUserRow(String oldUserName, String newUserName, String newEmployeeId,
			boolean updateContactFields) {
		List<String> assignments = new ArrayList<>();
		if (newUserName != null) {
			assignments.add("UserName = :newUserName");
			if (updateContactFields) {
				assignments.add("EmergencyContactNo = :newUserName");
				assignments.add("ContactNo = :newUserName");
			}
		}
		if (newEmployeeId != null) {
			assignments.add("EmployeeID = :newEmployeeId");
		}
		if (assignments.isEmpty()) {
			return 0;
		}

		Query query = entityManager.createNativeQuery("UPDATE db_iemr.m_user SET " + String.join(", ", assignments)
				+ " WHERE UserName = :oldUserName");
		query.setParameter("oldUserName", oldUserName);
		if (newUserName != null) {
			query.setParameter("newUserName", newUserName);
		}
		if (newEmployeeId != null) {
			query.setParameter("newEmployeeId", newEmployeeId);
		}
		return query.executeUpdate();
	}

	public boolean userExists(String userName) {
		Query query = entityManager
				.createNativeQuery("SELECT COUNT(*) FROM db_iemr.m_user WHERE UserName = :userName");
		query.setParameter("userName", userName);
		return toLong(query.getSingleResult()) > 0;
	}

	/**
	 * UserName and EmployeeID carry separate UNIQUE keys on m_user, so each is
	 * only in conflict with its own column. Checked independently now that a
	 * rename no longer forces EmployeeID to equal the username.
	 */
	public boolean userNameTaken(String userName, String excludeUserName) {
		return countMatching("UserName", userName, excludeUserName) > 0;
	}

	public boolean employeeIdTaken(String employeeId, String excludeUserName) {
		return countMatching("EmployeeID", employeeId, excludeUserName) > 0;
	}

	/**
	 * {@code excludeUserName} skips the row being renamed, so re-entering the
	 * value that row already holds is not reported as a conflict with itself.
	 */
	private long countMatching(String column, String value, String excludeUserName) {
		Query query = entityManager.createNativeQuery(String.format(
				"SELECT COUNT(*) FROM db_iemr.m_user WHERE %s = :value AND UserName <> :excludeUserName", column));
		query.setParameter("value", value);
		query.setParameter("excludeUserName", excludeUserName);
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
