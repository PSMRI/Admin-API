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
 * Result of a rename: what changed, and whether each column was written.
 */
public class UsernameRenameResponse {

	private String oldUserName;
	private String newUserName;

	/**
	 * The Employee ID before and after. Both mirror the username pair: the "new"
	 * value is null when the column was left alone, and the old value is read
	 * before the update so the caller can see what it replaced.
	 */
	private String oldEmployeeId;
	private String newEmployeeId;

	/**
	 * Whether each column was actually written. Booleans rather than inference
	 * from the strings above: OutputResponse re-serialises without
	 * serializeNulls, so a null "new" value drops out of the JSON entirely and
	 * the caller cannot tell "unchanged" from "missing". A primitive always
	 * survives that pass.
	 */
	private boolean userNameUpdated;
	private boolean employeeIdUpdated;

	public boolean isUserNameUpdated() {
		return userNameUpdated;
	}

	public void setUserNameUpdated(boolean userNameUpdated) {
		this.userNameUpdated = userNameUpdated;
	}

	public boolean isEmployeeIdUpdated() {
		return employeeIdUpdated;
	}

	public void setEmployeeIdUpdated(boolean employeeIdUpdated) {
		this.employeeIdUpdated = employeeIdUpdated;
	}

	public String getOldEmployeeId() {
		return oldEmployeeId;
	}

	public void setOldEmployeeId(String oldEmployeeId) {
		this.oldEmployeeId = oldEmployeeId;
	}

	public String getNewEmployeeId() {
		return newEmployeeId;
	}

	public void setNewEmployeeId(String newEmployeeId) {
		this.newEmployeeId = newEmployeeId;
	}

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
}
