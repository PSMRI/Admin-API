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
package com.iemr.admin.data.loinc;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "m_loinc")
public class Loinc {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	@Expose
	@Column(name = "loinc_num")
	private String lionicNum;
	
	@Expose
	@Column(name = "component")
	private String component;
	
	@Expose
	@Column(name = "system")
	private String system;
	
	@Expose
	@Column(name = "class")
	private String class1;
	
	@Expose
	@Column(name = "long_common_name")
	private String long_common_name;
	
	
	public String getLionicNum() {
		return lionicNum;
	}

	public void setLionicNum(String lionicNum) {
		this.lionicNum = lionicNum;
	}

	@Column(name = "status")
	private String status;

	@Transient
	private Integer pageNo;


	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getClass1() {
		return class1;
	}

	public void setClass1(String class1) {
		this.class1 = class1;
	}

	public String getLong_common_name() {
		return long_common_name;
	}

	public void setLong_common_name(String long_common_name) {
		this.long_common_name = long_common_name;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

