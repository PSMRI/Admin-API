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

import java.sql.Date;
import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="m_ProviderServiceMapping")
public class StateServiceMapping1 {

	
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
	@Expose
	@Column(name="StatusID")
	private Integer statusID; 
	@Expose
	@Column(name="ValidFrom")
	private Date validFrom; 
	@Expose
	@Column(name="ValidTill")
	private Date validTill;
	@Expose
	@Column(name="Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;
	@Expose
	@Column(name="CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name="LastModDate", insertable = false, updatable = false)
	private Timestamp  lastModDate;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ServiceID", insertable = false, updatable = false)
	@Expose
	private ServiceMasterLocation serviceMaster;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "StateID", insertable = false, updatable = false)
	@Expose
	private StateMaster stateMaster;
	
	
	
	
	
	@Transient
	@Expose
	private String stateName;
	
	@Transient
	@Expose
	private String serviceName;
	
	public StateServiceMapping1() {
	
	}


	public StateServiceMapping1(Integer stateID, String stateName,Integer statusID,Integer providerServiceMapID) {
		this.stateID = stateID;
		this.stateName = stateName;
		this.statusID=statusID;
		this.providerServiceMapID=providerServiceMapID;
	}
	
	public StateServiceMapping1(Integer stateID, String stateName, Integer providerServiceMapID) {
		this.stateID = stateID;
		this.providerServiceMapID = providerServiceMapID;
		this.stateName = stateName;
	}
	
	public StateServiceMapping1(Integer serviceID, Integer providerServiceMapID,String serviceName) {
		this.serviceID = serviceID;
		this.providerServiceMapID=providerServiceMapID;
		this.serviceName = serviceName;
	}
	
	public StateServiceMapping1(Integer providerServiceMapID){
		this.providerServiceMapID=providerServiceMapID;
	}



	public Integer getProviderServiceMapID() {
		return this.providerServiceMapID;
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



	public Integer getStatusID() {
		return statusID;
	}



	public void setStatusID(Integer statusID) {
		this.statusID = statusID;
	}



	public Date getValidFrom() {
		return validFrom;
	}



	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}



	public Date getValidTill() {
		return validTill;
	}



	public void setValidTill(Date validTill) {
		this.validTill = validTill;
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


	public ServiceMasterLocation getServiceMaster() {
		return serviceMaster;
	}


	public void setServiceMaster(ServiceMasterLocation serviceMaster) {
		this.serviceMaster = serviceMaster;
	}


	public StateMaster getStateMaster() {
		return stateMaster;
	}


	public void setStateMaster(StateMaster stateMaster) {
		this.stateMaster = stateMaster;
	}







	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	
	
	
}
