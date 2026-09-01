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
package com.iemr.admin.model.username;

/**
 * Request payload for the username rename operation.
 *
 * <p>{@code updateContactFields} controls whether the user's ContactNo and
 * EmergencyContactNo in m_user are also set to the new username. That is only
 * meaningful in deployments where the username is the user's mobile number.
 */
public class UsernameRenameRequest {

	private String oldUserName;
	private String newUserName;

	/**
	 * Employee ID is left untouched unless this is set. It carries its own
	 * UNIQUE key on m_user and is not required to track the username, so a
	 * rename does not imply a new Employee ID.
	 */
	private boolean updateEmployeeId = false;
	private String newEmployeeId;

	private boolean updateContactFields = true;

	public String getOldUserName() {
		return oldUserName;
	}

	public void setOldUserName(String oldUserName) {
		this.oldUserName = oldUserName;
	}

	public String getNewUserName() {
		return newUserName;
	}

	public void setNewUserName(String newUserName) {
		this.newUserName = newUserName;
	}

	public boolean isUpdateEmployeeId() {
		return updateEmployeeId;
	}

	public void setUpdateEmployeeId(boolean updateEmployeeId) {
		this.updateEmployeeId = updateEmployeeId;
	}

	public String getNewEmployeeId() {
		return newEmployeeId;
	}

	public void setNewEmployeeId(String newEmployeeId) {
		this.newEmployeeId = newEmployeeId;
	}

	public boolean isUpdateContactFields() {
		return updateContactFields;
	}

	public void setUpdateContactFields(boolean updateContactFields) {
		this.updateContactFields = updateContactFields;
	}
}
