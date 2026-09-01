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
 * Whether a proposed username / employee ID can be taken.
 *
 * <p>Answered with the same repository checks the rename itself runs, so the
 * screen and the server cannot disagree. In particular both count soft-deleted
 * rows: m_user's UNIQUE keys do not exclude them, so a name held by a deleted
 * user is genuinely unavailable even though it looks free.
 */
public class UsernameAvailabilityResponse {

	private boolean userNameAvailable = true;
	private boolean employeeIdAvailable = true;

	public boolean isUserNameAvailable() {
		return userNameAvailable;
	}

	public void setUserNameAvailable(boolean userNameAvailable) {
		this.userNameAvailable = userNameAvailable;
	}

	public boolean isEmployeeIdAvailable() {
		return employeeIdAvailable;
	}

	public void setEmployeeIdAvailable(boolean employeeIdAvailable) {
		this.employeeIdAvailable = employeeIdAvailable;
	}
}
