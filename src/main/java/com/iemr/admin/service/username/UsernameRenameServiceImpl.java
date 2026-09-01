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

import com.iemr.admin.model.username.UsernameAvailabilityResponse;
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
	 * Uses the same repository checks as {@link #rename}, so a name the screen
	 * reports as free cannot then be rejected on submit. Blank input is treated
	 * as available — there is nothing to clash with yet.
	 */
	@Override
	@Transactional(readOnly = true)
	public UsernameAvailabilityResponse checkAvailability(UsernameRenameRequest request) throws Exception {
		UsernameAvailabilityResponse response = new UsernameAvailabilityResponse();
		if (request == null) {
			return response;
		}

		String oldUserName = trimToNull(request.getOldUserName());
		if (oldUserName == null) {
			throw new IllegalArgumentException("Current username is required");
		}

		String newUserName = trimToNull(request.getNewUserName());
		if (newUserName != null && !newUserName.equals(oldUserName)) {
			response.setUserNameAvailable(!usernameRenameRepository.userNameTaken(newUserName, oldUserName));
		}

		String newEmployeeId = trimToNull(request.getNewEmployeeId());
		if (newEmployeeId != null) {
			response.setEmployeeIdAvailable(!usernameRenameRepository.employeeIdTaken(newEmployeeId, oldUserName));
		}

		return response;
	}

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
		logger.info("Username rename starting: {} -> {}", oldUserName, newUserName);

		UsernameRenameResponse response = newResponse(request);

		// The identity row goes first: if the unique key on UserName or
		// EmployeeID rejects the new value, nothing else has been touched yet.
		long userRows = usernameRenameRepository.renameUserRow(oldUserName, newUserName,
				request.isUpdateEmployeeId() ? request.getNewEmployeeId() : null,
				request.isUpdateContactFields());
		response.addTable("db_iemr.m_user", userRows);

		for (AuditTable table : UsernameAuditTables.TABLES) {
			long rows = usernameRenameRepository.renameInTable(table, oldUserName, newUserName);
			response.addTable(table.getQualifiedName(), rows);
		}

		logger.info("Username rename complete: {} -> {}, {} rows across {} tables", oldUserName, newUserName,
				response.getTotalRowsAffected(), response.getTablesAffected());
		return response;
	}

	private UsernameRenameResponse newResponse(UsernameRenameRequest request) {
		UsernameRenameResponse response = new UsernameRenameResponse();
		response.setOldUserName(request.getOldUserName());
		response.setNewUserName(request.getNewUserName());
		return response;
	}

	private void validate(UsernameRenameRequest request) throws Exception {
		if (request == null) {
			throw new IllegalArgumentException("Request body is required");
		}

		String oldUserName = trimToNull(request.getOldUserName());
		String newUserName = trimToNull(request.getNewUserName());

		if (oldUserName == null) {
			throw new IllegalArgumentException("Current username is required");
		}
		if (newUserName == null) {
			throw new IllegalArgumentException("New username is required");
		}
		if (oldUserName.equals(newUserName)) {
			throw new IllegalArgumentException("New username is the same as the current username");
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
		if (!usernameRenameRepository.userExists(oldUserName)) {
			throw new IllegalArgumentException("No user found with username " + oldUserName);
		}
		if (usernameRenameRepository.userNameTaken(newUserName, oldUserName)) {
			throw new IllegalArgumentException("Username " + newUserName + " is already in use");
		}

		request.setOldUserName(oldUserName);
		request.setNewUserName(newUserName);
		validateEmployeeId(request);
	}

	/**
	 * Employee ID is optional: left alone entirely unless the caller asks for it.
	 * It is checked against its own column only, since UserName and EmployeeID
	 * are independent UNIQUE keys.
	 */
	private void validateEmployeeId(UsernameRenameRequest request) throws Exception {
		if (!request.isUpdateEmployeeId()) {
			request.setNewEmployeeId(null);
			return;
		}

		String newEmployeeId = trimToNull(request.getNewEmployeeId());
		if (newEmployeeId == null) {
			throw new IllegalArgumentException("New employee ID is required when updating employee ID");
		}
		if (newEmployeeId.length() > MAX_EMPLOYEE_ID_LENGTH) {
			throw new IllegalArgumentException(
					"New employee ID exceeds " + MAX_EMPLOYEE_ID_LENGTH + " characters (m_user.EmployeeID limit)");
		}
		if (usernameRenameRepository.employeeIdTaken(newEmployeeId, request.getOldUserName())) {
			throw new IllegalArgumentException("Employee ID " + newEmployeeId + " is already in use");
		}

		request.setNewEmployeeId(newEmployeeId);
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
