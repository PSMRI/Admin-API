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
package com.iemr.admin.data.blocking;

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
@Table(name="m_serviceprovider")
public class M_Serviceprovider_Blocking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ServiceProviderID")
	private Integer serviceProviderID;
	@Expose
	@Column(name="ServiceProviderName")
	private String serviceProviderName;
	@Expose
	@Column(name="JoiningDate")
	private Date joiningDate;
	@Expose
	@Column(name="StateID")
	private Integer stateID;
	@Expose
	@Column(name="LogoFileName")
	private String logoFileName;
	@Expose
	@Column(name="LogoFilePath")
	private String logoFilePath;
	@Expose
	@Column(name="PrimaryContactName")
	private String primaryContactName;
	@Expose
	@Column(name="PrimaryContactNo")
	private String primaryContactNo;
	@Expose
	@Column(name="PrimaryContactEmailID")
	private String primaryContactEmailID; 
	@Expose
	@Column(name="PrimaryContactAddress")
	private String primaryContactAddress; 
	@Expose
	@Column(name="PrimaryContactValidityTillDate")
	private Timestamp primaryContactValidityTillDate;
	@Expose
	@Column(name="SecondaryContactName")
	private String secondaryContactName;
	@Expose
	@Column(name="SecondaryContactNo")
	private String secondaryContactNo;
	@Expose
	@Column(name="SecondaryContactEmailID")
	private String secondaryContactEmailID;
	@Expose
	@Column(name="SecondaryContactAddress")
	private String secondaryContactAddress;
	@Expose
	@Column(name="SecondaryContactValidityTillDate")
	private Timestamp secondaryContactValidityTillDate;
	@Expose
	@Column(name="StatusID")
	private Integer statusID;
	@Expose
	@Column(name="ValidFrom")
	private Timestamp validFrom; 
	@Expose
	@Column(name="ValidTill")
	private Timestamp validTill; 
	@Expose
	@Column(name="Deleted",insertable=false,updatable=true)
	private Boolean deleted;
	@Expose
	@Column(name="Processed",insertable=false,updatable=true)
	private String processed;
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;
	@Expose
	@Column(name="CreatedDate",insertable=false,updatable=false)
	private Timestamp createdDate;
	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name="LastModDate",insertable=false,updatable=false)
	private Timestamp lastModDate;
	
	@Expose
	@Column(name="IsNational")
	private Boolean isNational;
	
	
	
	  public M_Serviceprovider_Blocking() {
		// TODO Auto-generated constructor stub
	}

	  
	  
	  
	  


	public Integer getServiceProviderID() {
		return serviceProviderID;
	}



	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}



	public String getServiceProviderName() {
		return serviceProviderName;
	}



	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}



	public Date getJoiningDate() {
		return joiningDate;
	}



	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}



	public Integer getStateID() {
		return stateID;
	}



	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}



	public String getLogoFileName() {
		return logoFileName;
	}



	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}



	public String getLogoFilePath() {
		return logoFilePath;
	}



	public void setLogoFilePath(String logoFilePath) {
		this.logoFilePath = logoFilePath;
	}



	public String getPrimaryContactName() {
		return primaryContactName;
	}



	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}



	public String getPrimaryContactNo() {
		return primaryContactNo;
	}



	public void setPrimaryContactNo(String primaryContactNo) {
		this.primaryContactNo = primaryContactNo;
	}



	public String getPrimaryContactEmailID() {
		return primaryContactEmailID;
	}



	public void setPrimaryContactEmailID(String primaryContactEmailID) {
		this.primaryContactEmailID = primaryContactEmailID;
	}



	public String getPrimaryContactAddress() {
		return primaryContactAddress;
	}



	public void setPrimaryContactAddress(String primaryContactAddress) {
		this.primaryContactAddress = primaryContactAddress;
	}



	


	public String getSecondaryContactName() {
		return secondaryContactName;
	}



	public void setSecondaryContactName(String secondaryContactName) {
		this.secondaryContactName = secondaryContactName;
	}



	public String getSecondaryContactNo() {
		return secondaryContactNo;
	}



	public void setSecondaryContactNo(String secondaryContactNo) {
		this.secondaryContactNo = secondaryContactNo;
	}



	public String getSecondaryContactEmailID() {
		return secondaryContactEmailID;
	}



	public void setSecondaryContactEmailID(String secondaryContactEmailID) {
		this.secondaryContactEmailID = secondaryContactEmailID;
	}
	
	
	



	public Boolean getIsNational() {
		return isNational;
	}







	public void setIsNational(Boolean isNational) {
		this.isNational = isNational;
	}







	public String getSecondaryContactAddress() {
		return secondaryContactAddress;
	}



	public void setSecondaryContactAddress(String secondaryContactAddress) {
		this.secondaryContactAddress = secondaryContactAddress;
	}



	



	public Integer getStatusID() {
		return statusID;
	}



	public void setStatusID(Integer statusID) {
		this.statusID = statusID;
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



	
	  
	
	public Timestamp getPrimaryContactValidityTillDate() {
		return primaryContactValidityTillDate;
	}







	public void setPrimaryContactValidityTillDate(Timestamp primaryContactValidityTillDate) {
		this.primaryContactValidityTillDate = primaryContactValidityTillDate;
	}







	public Timestamp getSecondaryContactValidityTillDate() {
		return secondaryContactValidityTillDate;
	}







	public void setSecondaryContactValidityTillDate(Timestamp secondaryContactValidityTillDate) {
		this.secondaryContactValidityTillDate = secondaryContactValidityTillDate;
	}







	public Timestamp getValidFrom() {
		return validFrom;
	}







	public void setValidFrom(Timestamp validFrom) {
		this.validFrom = validFrom;
	}







	public Timestamp getValidTill() {
		return validTill;
	}







	public void setValidTill(Timestamp validTill) {
		this.validTill = validTill;
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
