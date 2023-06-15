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
package com.iemr.admin.data.zonemaster;

import java.sql.Date;

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
import com.iemr.admin.data.employeemaster.M_Country;
import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.State;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.provideronboard.M_ServiceMaster;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="m_zone")
public class M_Zone {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ZoneID")
	private Integer zoneID;
	
	@Expose
	@Column(name="ZoneName")
	private String zoneName;
	
	@Expose
	@Column(name="ZoneDesc")
	private String zoneDesc;
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ProviderServiceMapID")
	private M_ProviderServiceMapping m_providerServiceMapping;
	
	@Expose
	@Column(name="CountryID")
	private Integer countryID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "countryID")
	private M_Country m_country;
	@Expose
	@Transient
	String countryName;
	
	@Expose
	@Column(name="StateID")
	private Integer stateID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "stateID")
	private State state;
	@Expose
	@Transient
	String stateName;
	
	@Expose
	@Column(name="DistrictID")
	private Integer districtID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "districtID")
	private M_District m_district;
	@Expose
	@Transient
	String districtName;
	
	@Expose
	@Column(name="DistrictBlockID")
	private Integer districtBlockID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "districtBlockID")
	private DistrictBlock districtBlock;
	@Expose
	@Transient
	String blockName;
	
	@Expose
	@Column(name="DistrictBranchID")
	private Integer districtBranchID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "districtBranchID")
	private DistrictBranchMapping districtBranchMapping;
	@Expose
	@Transient
	String villageName;
	
	@Expose
	@Column(name="ZoneHQAddress")
	private String zoneHQAddress;
	
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
	private Date createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy; 
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Date lastModDate;
	
	public M_Zone() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public M_Zone(Integer zoneID, String zoneName, String zoneDesc, String zoneHQAddress, Integer providerServiceMapID, 
			Boolean deleted,Integer countryID, String countryName, Integer stateID, String stateName,Integer districtID, String districtName,
			Integer districtBlockID,String blockName,Integer districtBranchID, String villageName, M_ProviderServiceMapping m_providerServiceMapping,
			Integer serviceID,String serviceName){
		super();
		this.zoneID = zoneID;
		this.zoneName = zoneName;
		this.zoneDesc = zoneDesc;
		this.zoneHQAddress = zoneHQAddress;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.countryID = countryID;
		this.countryName = countryName;
		this.stateID = stateID;
		this.stateName = stateName;
		this.districtID = districtID;
		this.districtName = districtName;
		this.districtBlockID = districtBlockID;
		this.blockName = blockName;
		this.districtBranchID = districtBranchID;
		this.villageName = villageName;
		this.m_providerServiceMapping = new M_ProviderServiceMapping();
		this.m_providerServiceMapping.setM_serviceMaster(new M_ServiceMaster());
		this.m_providerServiceMapping.getM_serviceMaster().setServiceID(serviceID);
		this.m_providerServiceMapping.getM_serviceMaster().setServiceName(serviceName);
	}

	public Integer getZoneID() {
		return zoneID;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getZoneDesc() {
		return zoneDesc;
	}

	public void setZoneDesc(String zoneDesc) {
		this.zoneDesc = zoneDesc;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
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

	public Integer getDistrictBranchID() {
		return districtBranchID;
	}

	public void setDistrictBranchID(Integer districtBranchID) {
		this.districtBranchID = districtBranchID;
	}

	public Integer getDistrictBlockID() {
		return districtBlockID;
	}

	public void setDistrictBlockID(Integer districtBlockID) {
		this.districtBlockID = districtBlockID;
	}

	public String getZoneHQAddress() {
		return zoneHQAddress;
	}

	public void setZoneHQAddress(String zoneHQAddress) {
		this.zoneHQAddress = zoneHQAddress;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getLastModDate() {
		return lastModDate;
	}

	public void setLastModDate(Date lastModDate) {
		this.lastModDate = lastModDate;
	}
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
}
