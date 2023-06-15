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
package com.iemr.admin.data.provideronboard;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;



@Entity
@Table(name="m_104druggroup")
public class M_104druggroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "DrugGroupID")
	private Integer drugGroupID;
	@Expose
	@Column(name = "DrugGroup")
	private String drugGroup;
	@Expose
	@Column(name = "DrugGroupDesc")
	private String drugGroupDesc;
	@Expose
	@Column(name = "ServiceProviderID")
	private Short serviceProviderID;
	
	@Expose
	@Column(name = "Deleted",insertable = false, updatable = true)
	private Boolean deleted; 
	@Expose
	@Column(name = "Processed",insertable = false, updatable = true)
	private String processed;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate" ,insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy; 
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;
	
	public M_104druggroup() {
		// TODO Auto-generated constructor stub
	}


	
	public M_104druggroup(Integer drugGroupID, String drugGroup, String drugGroupDesc, Boolean deleted, Short serviceProviderID) {
		super();
		this.drugGroupID = drugGroupID;
		this.drugGroup = drugGroup;
		this.drugGroupDesc = drugGroupDesc;
		this.serviceProviderID = serviceProviderID;
		this.deleted = deleted;
	}

	public Integer getDrugGroupID() {
		return drugGroupID;
	}

	public void setDrugGroupID(Integer drugGroupID) {
		this.drugGroupID = drugGroupID;
	}

	public String getDrugGroup() {
		return drugGroup;
	}

	public void setDrugGroup(String drugGroup) {
		this.drugGroup = drugGroup;
	}

	public String getDrugGroupDesc() {
		return drugGroupDesc;
	}

	public void setDrugGroupDesc(String drugGroupDesc) {
		this.drugGroupDesc = drugGroupDesc;
	}

	public Short getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Short serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getProcessed() {
		return processed;
	}

	public void setProcessed(String processed) {
		this.processed = processed;
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
