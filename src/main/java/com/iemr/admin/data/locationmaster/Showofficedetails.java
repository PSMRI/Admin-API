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
package com.iemr.admin.data.locationmaster;

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
@Table(name="showofficedetails")
public class Showofficedetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="PSAddMapID")
	private Integer pSAddMapID;
	@Expose
	@Column(name="ServiceProviderID")
	private Integer serviceProviderID; 
	@Expose
	@Column(name="ServiceProviderName")
	private String serviceProviderName; 
	@Expose
	@Column(name="StateID")
	private Integer stateID; 
	@Expose
	@Column(name="StateName")
	private String stateName; 
	@Expose
	@Column(name="ServiceID")
	private Integer serviceID; 
	@Expose
	@Column(name="ServiceName")
	private String serviceName; 
	@Expose
	@Column(name="DistrictID")
	private Integer districtID; 
	@Expose
	@Column(name="DistrictName")
	private String districtName;
	@Expose
	@Column(name="LocationName")
	private String locationName; 
	@Expose
	@Column(name="Address")
	private String address;
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
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
	private String modifiedBy ;
	
	@Expose
	@Column(name="LastModDate")
	private Timestamp lastModDate;
	
	public Showofficedetails() {
		// TODO Auto-generated constructor stub
	}

	






	public Integer getpSAddMapID() {
		return pSAddMapID;
	}


	public void setpSAddMapID(Integer pSAddMapID) {
		this.pSAddMapID = pSAddMapID;
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


	public Integer getStateID() {
		return stateID;
	}


	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}


	public String getStateName() {
		return stateName;
	}


	public void setStateName(String stateName) {
		this.stateName = stateName;
	}


	public Integer getServiceID() {
		return serviceID;
	}


	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}


	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public Integer getDistrictID() {
		return districtID;
	}


	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}


	public String getDistrictName() {
		return districtName;
	}


	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}


	public String getLocationName() {
		return locationName;
	}


	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}


	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
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
