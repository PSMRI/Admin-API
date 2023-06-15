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
package com.iemr.admin.data.rolemaster;

import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;

@Data
public class UserRole {
	@Expose
	private Integer UserID;
	@Expose
	private Integer roleID;
	@Expose
	private String rolename;
	@Expose
	private Boolean roleDeleted;
	@Expose
	private Integer screenID;
	@Expose
	private String screenName;
	@Expose
	private Boolean screenDeleted;
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

	public UserRole(){
		
	}
	
	public UserRole(Integer userID, Integer roleID, String rolename, Boolean roleDeleted, Integer screenID, String screenName,
			Boolean screenDeleted) {
		super();
		UserID = userID;
		this.roleID = roleID;
		this.rolename = rolename;
		this.roleDeleted = roleDeleted;
		this.screenID = screenID;
		this.screenName = screenName;
		this.screenDeleted = screenDeleted;
	}
	
	
}
