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
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.State;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.provideronboard.M_ServiceMaster;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="m_zonedistrictmap")
public class M_ZoneDistrictMap {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ZoneDistrictMapID")
	private Integer zoneDistrictMapID;
	
	@Expose
	@Column(name="ZoneID")
	private Integer zoneID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "zoneID")
	private M_Zone m_zone;

	@Expose
	@Transient
	private String zoneName;
	
	@Expose
	@Transient
	private Boolean zoneDeleted;
	
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
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ProviderServiceMapID")
	private M_ProviderServiceMapping m_providerServiceMapping;

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
	


	
	public M_ZoneDistrictMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public M_ZoneDistrictMap(String districtName, Integer districtID) {
	
		this.districtID = districtID;
		this.districtName = districtName;
	
	}
	
	
	
	public M_ZoneDistrictMap(Integer zoneDistrictMapID, Integer zoneID,String zoneName, Integer districtID,
			Integer providerServiceMapID, Boolean deleted, Integer stateID, String stateName, String districtName, Integer serviceID,String serviceName,Boolean zoneDeleted) {
		super();
		this.zoneDistrictMapID = zoneDistrictMapID;
		this.zoneID = zoneID;
		this.zoneName = zoneName;
		this.districtID = districtID;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.m_providerServiceMapping = new M_ProviderServiceMapping();
		this.m_providerServiceMapping.setState(new State());
		this.m_providerServiceMapping.getState().setStateID(stateID);
		this.m_providerServiceMapping.getState().setStateName(stateName);
		this.m_providerServiceMapping.setM_serviceMaster(new M_ServiceMaster());
		this.m_providerServiceMapping.getM_serviceMaster().setServiceID(serviceID);
		this.m_providerServiceMapping.getM_serviceMaster().setServiceName(serviceName);
		this.districtName = districtName;
		this.zoneDeleted=zoneDeleted;
	}
	
	public Integer getZoneID() {
		return zoneID;
	}

	public void setZoneID(Integer zoneID) {
		this.zoneID = zoneID;
	}

	public Integer getDistrictID() {
		return districtID;
	}

	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
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

	public Integer getZoneDistrictMapID() {
		return zoneDistrictMapID;
	}
	
	public M_ProviderServiceMapping getM_providerServiceMapping() {
		return m_providerServiceMapping;
	}

	public void setM_providerServiceMapping(M_ProviderServiceMapping m_providerServiceMapping) {
		this.m_providerServiceMapping = m_providerServiceMapping;
	}
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
