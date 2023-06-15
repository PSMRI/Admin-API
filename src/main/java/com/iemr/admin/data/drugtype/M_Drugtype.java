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
package com.iemr.admin.data.drugtype;

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
@Table(name="m_drugtype")
public class M_Drugtype {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="DrugTypeID")
	private Integer drugTypeID;
	@Expose
	@Column(name="DrugTypeName")
	private String drugTypeName;
	@Expose
	@Column(name="DrugTypeDesc")
	private String drugTypeDesc;
	@Expose
	@Column(name="DrugTypeCode")
	private String drugTypeCode;
	@Expose
	@Column(name="Status")
	private String status;
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name="Deleted",insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;
	@Expose
	@Column(name="CreatedDate",insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name="LastModDate" ,insertable = false, updatable = false)
	private Timestamp lastModDate;
	
	
	public M_Drugtype() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the drugTypeID
	 */
	public Integer getDrugTypeID() {
		return drugTypeID;
	}


	/**
	 * @param drugTypeID the drugTypeID to set
	 */
	public void setDrugTypeID(Integer drugTypeID) {
		this.drugTypeID = drugTypeID;
	}


	/**
	 * @return the drugTypeName
	 */
	public String getDrugTypeName() {
		return drugTypeName;
	}


	/**
	 * @param drugTypeName the drugTypeName to set
	 */
	public void setDrugTypeName(String drugTypeName) {
		this.drugTypeName = drugTypeName;
	}


	/**
	 * @return the drugTypeDesc
	 */
	public String getDrugTypeDesc() {
		return drugTypeDesc;
	}


	/**
	 * @param drugTypeDesc the drugTypeDesc to set
	 */
	public void setDrugTypeDesc(String drugTypeDesc) {
		this.drugTypeDesc = drugTypeDesc;
	}


	/**
	 * @return the drugTypeCode
	 */
	public String getDrugTypeCode() {
		return drugTypeCode;
	}


	/**
	 * @param drugTypeCode the drugTypeCode to set
	 */
	public void setDrugTypeCode(String drugTypeCode) {
		this.drugTypeCode = drugTypeCode;
	}


	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 * @return the providerServiceMapID
	 */
	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}


	/**
	 * @param providerServiceMapID the providerServiceMapID to set
	 */
	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}


	/**
	 * @return the deleted
	 */
	public Boolean getDeleted() {
		return deleted;
	}


	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}


	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}


	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	/**
	 * @return the createdDate
	 */
	


	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}


	/**
	 * @param modifiedBy the modifiedBy to set
	 */
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
