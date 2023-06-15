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
package com.iemr.admin.to.employeemaster;

public class UserTO1 {
	
	private Integer newRoleId;
	private Integer oldRoleId;
	
	private Integer oldLanguageID;
	
	private Integer newLanguageID;

	public Integer getNewRoleId() {
		return newRoleId;
	}

	public void setNewRoleId(Integer newRoleId) {
		this.newRoleId = newRoleId;
	}

	public Integer getOldRoleId() {
		return oldRoleId;
	}

	public void setOldRoleId(Integer oldRoleId) {
		this.oldRoleId = oldRoleId;
	}

	public Integer getOldLanguageID() {
		return oldLanguageID;
	}

	public void setOldLanguageID(Integer oldLanguageID) {
		this.oldLanguageID = oldLanguageID;
	}

	public Integer getNewLanguageID() {
		return newLanguageID;
	}

	public void setNewLanguageID(Integer newLanguageID) {
		this.newLanguageID = newLanguageID;
	}
	
	
	
	

}
