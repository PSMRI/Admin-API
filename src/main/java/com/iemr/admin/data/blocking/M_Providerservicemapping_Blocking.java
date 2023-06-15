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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name = "m_providerservicemapping")
public class M_Providerservicemapping_Blocking
{
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
	@Column(name = "CountryID")
	private Integer countryID;
	@Expose
	@Column(name = "StateID")
	private Integer stateID;
	@Expose
	@Column(name = "DistrictID")
	private Integer districtID;
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
	@Column(name = "CTI_CampaignName")
	private String cTI_CampaignName;
	@Expose
	@Column(name = "APIMANClientID")
	private String apimanClientID;
	@Expose
	@Column(name = "APIMANClientKey")
	private String apimanClientKey;
	@Expose
	@Column(name = "ValidTill")
	private Timestamp validTill;

	@Expose
	@Column(name = "ValidFrom")
	private Timestamp validFrom;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name = "Processed", insertable = false, updatable = false)
	private String processed;
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
	@Column(name = "LastModDate")
	private Timestamp lastModDate;

	@Transient
	@Expose
	private String serviceProviderName;

	@Transient
	@Expose
	private String stateName;

	@Transient
	@Expose
	private String serviceName;

	@Transient
	@Expose
	private String status;

	@Transient
	@Expose
	private Boolean isNational;
	
	
	@Transient
	@Expose
	private Boolean serviceProviderDeleted;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ServiceID", insertable = false, updatable = false)
	@Expose
	private M_ServicemasterForBlocking m_ServicemasterForBlocking;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "StateID", insertable = false, updatable = false)
	@Expose
	private M_StateForBlocking m_StateForBlocking;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ServiceProviderId", insertable = false, updatable = false)
	@Expose
	private ServiceProvider_ModelForBlocking serviceProvider_ModelForBlocking;

	public M_Providerservicemapping_Blocking()
	{
		// TODO Auto-generated constructor stub
	}

	public M_Providerservicemapping_Blocking(String address)
	{
		this.address = address;
	}

	/*
	 * public M_Providerservicemapping_Blocking(Integer serviceProviderID,Integer serviceID,String serviceName) {
	 * this.serviceName=serviceName; this.serviceProviderID=serviceProviderID; this.serviceID=serviceID;
	 * //this.providerServiceMapID=providerServiceMapID; }
	 */

	public M_Providerservicemapping_Blocking(Integer providerServiceMapID, Integer serviceProviderID, Integer serviceID,
			String serviceName, Boolean isNational)
	{
		this.providerServiceMapID = providerServiceMapID;
		this.serviceName = serviceName;
		this.serviceProviderID = serviceProviderID;
		this.serviceID = serviceID;
		this.isNational = isNational;

	}

	public M_Providerservicemapping_Blocking(Integer providerServiceMapID, Integer serviceProviderID, Integer serviceID,
			String serviceName, Integer stateID, String stateName, String serviceProviderName, Boolean deleted,
			String cTI_CampaignName,Boolean serviceProviderDeleted)
	{
		this.providerServiceMapID = providerServiceMapID;
		this.serviceName = serviceName;
		this.serviceProviderID = serviceProviderID;
		this.serviceID = serviceID;
		this.stateID = stateID;
		this.stateName = stateName;
		this.serviceProviderName = serviceProviderName;
		this.deleted = deleted;
		// this.providerServiceMapID=providerServiceMapID;
		this.cTI_CampaignName = cTI_CampaignName;
		this.serviceProviderDeleted=serviceProviderDeleted;
	}

	/*
	 * public M_Providerservicemapping_Blocking(Integer integer, String string) { // TODO Auto-generated constructor
	 * stub }
	 */

	public Integer getProviderServiceMapID()
	{
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID)
	{
		this.providerServiceMapID = providerServiceMapID;
	}

	public Integer getServiceProviderID()
	{
		return serviceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID)
	{
		this.serviceProviderID = serviceProviderID;
	}

	public Integer getServiceID()
	{
		return serviceID;
	}

	public void setServiceID(Integer serviceID)
	{
		this.serviceID = serviceID;
	}

	public Integer getCountryID()
	{
		return countryID;
	}

	public void setCountryID(Integer countryID)
	{
		this.countryID = countryID;
	}

	public Integer getStateID()
	{
		return stateID;
	}

	public void setStateID(Integer stateID)
	{
		this.stateID = stateID;
	}

	public Integer getDistrictID()
	{
		return districtID;
	}

	public void setDistrictID(Integer districtID)
	{
		this.districtID = districtID;
	}

	public Integer getCityID()
	{
		return cityID;
	}

	public void setCityID(Integer cityID)
	{
		this.cityID = cityID;
	}

	public Integer getDistrictBlockID()
	{
		return districtBlockID;
	}

	public void setDistrictBlockID(Integer districtBlockID)
	{
		this.districtBlockID = districtBlockID;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public Integer getStatusID()
	{
		return statusID;
	}

	public void setStatusID(Integer statusID)
	{
		this.statusID = statusID;
	}

	public String getcTI_CampaignName()
	{
		return cTI_CampaignName;
	}

	public void setcTI_CampaignName(String cTI_CampaignName)
	{
		this.cTI_CampaignName = cTI_CampaignName;
	}

	public Boolean getDeleted()
	{
		return deleted;
	}

	public void setDeleted(Boolean deleted)
	{
		this.deleted = deleted;
	}

	public String getProcessed()
	{
		return processed;
	}

	public void setProcessed(String processed)
	{
		this.processed = processed;
	}

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	

	public String getModifiedBy()
	{
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	


	public Timestamp getValidTill() {
		return validTill;
	}

	public void setValidTill(Timestamp validTill) {
		this.validTill = validTill;
	}

	public Timestamp getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Timestamp validFrom) {
		this.validFrom = validFrom;
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

	public M_ServicemasterForBlocking getM_ServicemasterForBlocking()
	{
		return m_ServicemasterForBlocking;
	}

	public void setM_ServicemasterForBlocking(M_ServicemasterForBlocking m_ServicemasterForBlocking)
	{
		this.m_ServicemasterForBlocking = m_ServicemasterForBlocking;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public M_StateForBlocking getM_StateForBlocking()
	{
		return m_StateForBlocking;
	}

	public void setM_StateForBlocking(M_StateForBlocking m_StateForBlocking)
	{
		this.m_StateForBlocking = m_StateForBlocking;
	}

	public ServiceProvider_ModelForBlocking getServiceProvider_ModelForBlocking()
	{
		return serviceProvider_ModelForBlocking;
	}

	public void setServiceProvider_ModelForBlocking(ServiceProvider_ModelForBlocking serviceProvider_ModelForBlocking)
	{
		this.serviceProvider_ModelForBlocking = serviceProvider_ModelForBlocking;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString()
	{
		return outputMapper.gson().toJson(this);
	}

}
