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
package com.iemr.admin.data.employeemaster;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


@Entity
@Table(name="m_designation")
public class M_Designation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="DesignationID")
	private Integer designationID;
	@Expose
	@Column(name="DesignationName")
	private String designationName;
	@Expose
	@Column(name="DesignationDesc")
	private String designationDesc;
	@Expose
	@Column(name="Deleted")
	private Boolean deleted;
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;
	@Expose
	@Column(name="CreatedDate")
	private Timestamp createdDate;
	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name="LastModDate")
	private Timestamp lastModDate;
	  
	
	public M_Designation() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	
	public Integer getDesignationID() {
		return designationID;
	}



	public void setDesignationID(Integer designationID) {
		this.designationID = designationID;
	}



	public String getDesignationName() {
		return designationName;
	}



	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}



	public String getDesignationDesc() {
		return designationDesc;
	}



	public void setDesignationDesc(String designationDesc) {
		this.designationDesc = designationDesc;
	}



	public Boolean getDeleted() {
		return deleted;
	}



	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}



	public String getCreatedBy() {
		return createdBy;
	}



	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}



	



	public String getModifiedBy() {
		return modifiedBy;
	}



	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}



	






	public Timestamp getCreatedDate() {
		return createdDate;
	}






	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}






	public Timestamp getLastModDate() {
		return lastModDate;
	}






	public void setLastModDate(Timestamp lastModDate) {
		this.lastModDate = lastModDate;
	}










	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
