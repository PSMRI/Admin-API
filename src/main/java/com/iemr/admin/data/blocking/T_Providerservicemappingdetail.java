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
@Table(name="t_providerservicemappingdetail")
public class T_Providerservicemappingdetail {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name="ServiceProviderID")
	private Integer serviceProviderID;
	@Expose
	@Column(name="ServiceID")
	private Integer serviceID;
	@Expose
	@Column(name="CountryID")
	private Integer countryID;
	@Expose
	@Column(name="StateID")
	private Integer stateID;
	@Expose
	@Column(name="DistrictID")
	private Integer districtID;
	@Expose
	@Column(name="CityID")
	private Integer cityID;
	@Expose
	@Column(name="DistrictBlockID")
	private Integer districtBlockID;
	@Expose
	@Column(name="Address")
	private String address;
	
	/*ProviderServiceMapID int(11) AI PK 
	ServiceProviderID smallint(6) 
	ServiceID smallint(6) 
	CountryID int(11) 
	StateID int(11) 
	DistrictID int(11) 
	CityID int(11) 
	DistrictBlockID int(11) 
	Address varchar(100) */
	@Expose
	@Column(name="Reason")
	private String reason;
	@Expose
	@Column(name="PreviousStatusID")
	private Integer previousStatusID; 
	@Expose
	@Column(name="UpdatedStatusID")
	private Integer updatedStatusID; 
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
	
	
	
	public T_Providerservicemappingdetail() {
		// TODO Auto-generated constructor stub
	}

	
	
	
	
	


	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}



	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}



	public Integer getServiceProviderID() {
		return serviceProviderID;
	}



	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}



	public Integer getServiceID() {
		return serviceID;
	}



	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}



	public Integer getCountryID() {
		return countryID;
	}



	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}



	public Integer getStateID() {
		return stateID;
	}



	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}



	public Integer getDistrictID() {
		return districtID;
	}



	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}



	public Integer getCityID() {
		return cityID;
	}



	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}



	public Integer getDistrictBlockID() {
		return districtBlockID;
	}



	public void setDistrictBlockID(Integer districtBlockID) {
		this.districtBlockID = districtBlockID;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public Integer getPreviousStatusID() {
		return previousStatusID;
	}



	public void setPreviousStatusID(Integer previousStatusID) {
		this.previousStatusID = previousStatusID;
	}



	public Integer getUpdatedStatusID() {
		return updatedStatusID;
	}



	public void setUpdatedStatusID(Integer updatedStatusID) {
		this.updatedStatusID = updatedStatusID;
	}



	


	public Boolean getDeleted() {
		return deleted;
	}



	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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
	
	
	public String getReason() {
		return reason;
	}








	public void setReason(String reason) {
		this.reason = reason;
	}



	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	

}
