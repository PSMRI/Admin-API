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
package com.iemr.admin.data.apiman;

import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;

@Data
public class ApimanContract {

	@Expose
	private String planId;
	@Expose
	private String apiOrgId;
	@Expose
	private String apiId;
	@Expose
	private String apiVersion;
	
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

	public ApimanContract(){
		
	}
	
	public ApimanContract(String planId, String apiOrgId, String apiId, String apiVersion) {
		super();
		this.planId = planId;
		this.apiOrgId = apiOrgId;
		this.apiId = apiId;
		this.apiVersion = apiVersion;
	}
}
