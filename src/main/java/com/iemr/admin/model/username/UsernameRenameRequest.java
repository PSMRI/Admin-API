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
	 * Both new values are optional and independent. A blank value, or one equal
	 * to what the row already holds, means "leave this column alone" — so the
	 * caller can change the username, the employee ID, or both.
	 */
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
