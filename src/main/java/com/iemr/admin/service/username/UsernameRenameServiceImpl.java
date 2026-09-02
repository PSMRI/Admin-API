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

	private static final int MAX_USERNAME_LENGTH = 20;
	private static final int MAX_EMPLOYEE_ID_LENGTH = 20;

	private static final int MAX_CONTACT_LENGTH = 12;

	@Autowired
	private UsernameRenameRepository usernameRenameRepository;

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
		response.setOldEmployeeId(usernameRenameRepository.currentEmployeeId(request.getUserID()));

		long rowsUpdated = usernameRenameRepository.renameUserRow(request.getUserID(), newUserName, newEmployeeId,
				request.isUpdateContactFields());

		if (newUserName != null) {
			for (AuditTable table : UsernameAuditTables.TABLES) {
				rowsUpdated += usernameRenameRepository.renameInTable(table, oldUserName, newUserName);
			}
		}

		logger.info("Username rename complete: {} rows updated", rowsUpdated);
		return response;
	}

	private UsernameRenameResponse newResponse(UsernameRenameRequest request) {
		UsernameRenameResponse response = new UsernameRenameResponse();
		response.setOldUserName(request.getOldUserName());
		response.setNewUserName(request.getNewUserName());
		response.setNewEmployeeId(request.getNewEmployeeId());
		response.setUserNameUpdated(request.getNewUserName() != null);
		response.setEmployeeIdUpdated(request.getNewEmployeeId() != null);
		return response;
	}

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
