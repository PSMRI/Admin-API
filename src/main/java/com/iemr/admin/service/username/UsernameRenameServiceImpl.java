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
package com.iemr.admin.service.username;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.model.username.UsernameRenameRequest;
import com.iemr.admin.model.username.UsernameRenameResponse;
import com.iemr.admin.repository.username.UsernameAuditTables;
import com.iemr.admin.repository.username.UsernameAuditTables.AuditTable;
import com.iemr.admin.repository.username.UsernameRenameRepository;

@Service
public class UsernameRenameServiceImpl implements UsernameRenameService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/** m_user.UserName and m_user.EmployeeID are both varchar(20). */
	private static final int MAX_USERNAME_LENGTH = 20;
	private static final int MAX_EMPLOYEE_ID_LENGTH = 20;

	/**
	 * m_user.ContactNo is varchar(12) — the tightest column the rename writes
	 * into. Anything longer would be truncated silently, or rejected outright
	 * under strict mode, so the whole rename is refused up front instead.
	 */
	private static final int MAX_CONTACT_LENGTH = 12;

	@Autowired
	private UsernameRenameRepository usernameRenameRepository;

	/**
	 * Runs in one transaction spanning db_iemr and db_identity, so a failure
	 * part-way through rolls the whole rename back rather than stranding the
	 * user half-renamed.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public UsernameRenameResponse rename(UsernameRenameRequest request) throws Exception {
		validate(request);

		String oldUserName = request.getOldUserName();
		String newUserName = request.getNewUserName();
		String newEmployeeId = request.getNewEmployeeId();
		logger.info("Username rename starting: user {} -> {}, employeeId -> {}", oldUserName,
				newUserName == null ? "(unchanged)" : newUserName,
				newEmployeeId == null ? "(unchanged)" : newEmployeeId);

		UsernameRenameResponse response = newResponse(request);

		// The identity row goes first: if a unique key rejects either new value,
		// nothing else has been touched yet.
		long userRows = usernameRenameRepository.renameUserRow(request.getUserID(), newUserName, newEmployeeId,
				request.isUpdateContactFields());
		response.addTable("db_iemr.m_user", userRows);

		// CreatedBy/ModifiedBy record the username, so the sweep is only needed
		// when the username itself changed. An employee-ID-only change leaves
		// every audit row already correct.
		if (newUserName != null) {
			for (AuditTable table : UsernameAuditTables.TABLES) {
				response.addTable(table.getQualifiedName(),
						usernameRenameRepository.renameInTable(table, oldUserName, newUserName));
			}
		}

		logger.info("Username rename complete: {} rows across {} tables", response.getTotalRowsAffected(),
				response.getTablesAffected());
		return response;
	}

	private UsernameRenameResponse newResponse(UsernameRenameRequest request) {
		UsernameRenameResponse response = new UsernameRenameResponse();
		response.setOldUserName(request.getOldUserName());
		response.setNewUserName(request.getNewUserName());
		return response;
	}

	/**
	 * Both new values are optional. Each is normalised to null when it is blank
	 * or already equal to what the row holds, which is what the repository
	 * reads as "leave this column alone". At least one must actually change.
	 */
	private void validate(UsernameRenameRequest request) throws Exception {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}

		if (request.getUserID() == null) {
			throw new IllegalArgumentException("User ID is required");
		}

		String storedUserName = usernameRenameRepository.currentUserName(request.getUserID());
		if (storedUserName == null) {
			throw new IllegalArgumentException("No user found with ID " + request.getUserID());
		}

		// Guard against a stale screen: if the row has been renamed since the
		// list was loaded, the audit sweep would match the wrong username.
		String oldUserName = trimToNull(request.getOldUserName());
		if (oldUserName != null && !oldUserName.equals(storedUserName)) {
			throw new IllegalArgumentException("User " + request.getUserID() + " is now named " + storedUserName
					+ ", not " + oldUserName + ". Reload the user list and try again.");
		}
		request.setOldUserName(storedUserName);
		oldUserName = storedUserName;

		request.setNewUserName(resolveNewUserName(request, oldUserName));
		request.setNewEmployeeId(resolveNewEmployeeId(request));

		if (request.getNewUserName() == null && request.getNewEmployeeId() == null) {
			throw new IllegalArgumentException("Nothing to update — enter a new username or a new employee ID");
		}
	}

	private String resolveNewUserName(UsernameRenameRequest request, String oldUserName) throws Exception {
		String newUserName = trimToNull(request.getNewUserName());
		if (newUserName == null || newUserName.equals(oldUserName)) {
			return null;
		}
		if (newUserName.length() > MAX_USERNAME_LENGTH) {
			throw new IllegalArgumentException(
					"New username exceeds " + MAX_USERNAME_LENGTH + " characters (m_user.UserName limit)");
		}
		if (request.isUpdateContactFields() && newUserName.length() > MAX_CONTACT_LENGTH) {
			throw new IllegalArgumentException("New username exceeds " + MAX_CONTACT_LENGTH
					+ " characters and cannot be written to ContactNo. Either shorten it or "
					+ "turn off updating contact numbers.");
		}
		if (usernameRenameRepository.userNameTaken(newUserName, request.getUserID())) {
			throw new IllegalArgumentException("Username " + newUserName + " is already in use");
		}
		return newUserName;
	}

	private String resolveNewEmployeeId(UsernameRenameRequest request) throws Exception {
		String newEmployeeId = trimToNull(request.getNewEmployeeId());
		if (newEmployeeId == null
				|| newEmployeeId.equals(usernameRenameRepository.currentEmployeeId(request.getUserID()))) {
			return null;
		}
		if (newEmployeeId.length() > MAX_EMPLOYEE_ID_LENGTH) {
			throw new IllegalArgumentException(
					"New employee ID exceeds " + MAX_EMPLOYEE_ID_LENGTH + " characters (m_user.EmployeeID limit)");
		}
		if (usernameRenameRepository.employeeIdTaken(newEmployeeId, request.getUserID())) {
			throw new IllegalArgumentException("Employee ID " + newEmployeeId + " is already in use");
		}
		return newEmployeeId;
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
