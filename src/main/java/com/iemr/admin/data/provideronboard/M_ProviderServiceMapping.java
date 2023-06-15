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

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.State;
import com.iemr.admin.utils.mapper.OutputMapper;

/**
 * 
 * @author NE298657
 * @date 09-07-2017
 * @purpose provider state service mapping
 *
 */

@Entity
@Table(name = "m_providerservicemapping")
public class M_ProviderServiceMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name = "ServiceProviderID")
	private Integer serviceProviderID;
	@Expose
	@Column(name = "ServiceID")
	private Integer serviceID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "serviceID")
	private M_ServiceMaster m_serviceMaster;
	
	@Expose
	@Column(name = "CountryID")
	private Integer countryID;
	@Expose
	@Column(name = "StateID")
	private Integer stateID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "StateID")
	private State state;

	@Expose
	@Column(name = "DistrictID")
	private Integer districtID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "StateID")
	private M_District m_district;
	
	@Expose
	@Column(name = "CityID")
	private Integer cityID;
	@Expose
	@Column(name = "DistrictBlockID")
	private Integer districtBlockID;
	@Expose
	@Column(name = "Address")
	private String address;
	@Expose
	@Column(name = "StatusID")
	private Integer statusID;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;

	public M_ProviderServiceMapping() {
	}
	
	
	public M_ProviderServiceMapping(Integer serviceProviderID){
		this.serviceProviderID=serviceProviderID;
		
	}

	public M_ProviderServiceMapping(Integer providerServiceMapID, Integer serviceProviderID, Integer serviceID,
			Integer countryID, Integer stateID, Integer districtID, Integer cityID, Integer districtBlockID,

			String address, Short statusID, Boolean deleted, String createdBy, Timestamp createdDate, String modifiedBy,
			Timestamp lastModDate,State state) {
		super();
		this.providerServiceMapID = providerServiceMapID;
		this.serviceProviderID = serviceProviderID;
		this.serviceID = serviceID;
		this.countryID = countryID;
		this.stateID = stateID;
		this.state = state;
		this.districtID = districtID;
		this.cityID = cityID;
		this.districtBlockID = districtBlockID;
		this.address = address;
		this.statusID = new Integer(statusID);
		this.deleted = deleted;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.lastModDate = lastModDate;
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

	

	/**
	 * @return the serviceID
	 */
	public Integer getServiceID() {
		return serviceID;
	}


	/**
	 * @param serviceID the serviceID to set
	 */
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

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getLastModDate() {
		return lastModDate;
	}

	public void setLastModDate(Timestamp lastModDate) {
		this.lastModDate = lastModDate;
	}
	
	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}
	
	public Integer getStatusID() {
		return statusID;
	}


	public void setStatusID(Integer statusID) {
		this.statusID = statusID;
	}

	public M_District getM_district() {
		return m_district;
	}


	public void setM_district(M_District m_district) {
		this.m_district = m_district;
	}

	public M_ServiceMaster getM_serviceMaster() {
		return m_serviceMaster;
	}

	public void setM_serviceMaster(M_ServiceMaster m_serviceMaster) {
		this.m_serviceMaster = m_serviceMaster;
	}
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

}
