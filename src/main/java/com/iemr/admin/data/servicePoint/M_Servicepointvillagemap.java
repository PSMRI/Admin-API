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
package com.iemr.admin.data.servicePoint;

import java.sql.Date;

import com.google.gson.annotations.Expose;
import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.StateMaster;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
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
@Table(name="m_servicepointvillagemap")
public class M_Servicepointvillagemap {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ServicePointVillageMapID")
	private Integer servicePointVillageMapID;
	
	@Expose
	@Column(name="ServicePointID")
	private Integer servicePointID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "servicePointID")
	private M_Servicepoint m_servicepoint;

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
	@Transient
	String blockName;
	
	@Expose
	@Transient
	Integer blockID;
	
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
	
	public M_Servicepointvillagemap() {
		super();
		// TODO Auto-generated constructor stub
	}

	
		public M_Servicepointvillagemap(Integer servicePointVillageMapID, Integer stateID, String stateName,Integer districtID, String districtName,
			Integer parkingPlaceID, String parkingPlaceName, Integer servicePointID, String servicePointName, Integer districtBranchID, String villageName,
			Integer providerServiceMapID, Boolean deleted) {
		super();
		this.servicePointVillageMapID = servicePointVillageMapID;
		this.providerServiceMapID = providerServiceMapID;
		this.m_providerServiceMapping = new M_ProviderServiceMapping();
		
		this.m_providerServiceMapping.setStateID(stateID);
		this.m_providerServiceMapping.setState(new StateMaster());
		this.m_providerServiceMapping.getState().setStateName(stateName);
		this.m_providerServiceMapping.setDistrictID(districtID);
		this.m_providerServiceMapping.setM_district(new M_District());
		this.m_providerServiceMapping.getM_district().setDistrictName(districtName);
		
		this.servicePointID = servicePointID;
		this.m_servicepoint = new M_Servicepoint();
		this.m_servicepoint.setParkingPlaceID(parkingPlaceID);
		this.m_servicepoint.setParkingPlaceName(parkingPlaceName);
		this.m_servicepoint.setServicePointName(servicePointName);
		
		this.districtBranchID = districtBranchID;
		this.villageName = villageName;
		
		
		this.deleted = deleted;
	}

		public M_Servicepointvillagemap(Integer servicePointVillageMapID, Integer stateID, String stateName,Integer districtID, String districtName,
				Integer parkingPlaceID, String parkingPlaceName, Integer servicePointID, String servicePointName, Integer districtBranchID, String villageName,
				Integer providerServiceMapID, Boolean deleted,Integer blockID,String blockName) {
			super();
			this.servicePointVillageMapID = servicePointVillageMapID;
			this.providerServiceMapID = providerServiceMapID;
			this.m_providerServiceMapping = new M_ProviderServiceMapping();
			
			this.m_providerServiceMapping.setStateID(stateID);
			this.m_providerServiceMapping.setState(new StateMaster());
			this.m_providerServiceMapping.getState().setStateName(stateName);
			this.m_providerServiceMapping.setDistrictID(districtID);
			this.m_providerServiceMapping.setM_district(new M_District());
			this.m_providerServiceMapping.getM_district().setDistrictName(districtName);
			
			this.servicePointID = servicePointID;
			this.m_servicepoint = new M_Servicepoint();
			this.m_servicepoint.setParkingPlaceID(parkingPlaceID);
			this.m_servicepoint.setParkingPlaceName(parkingPlaceName);
			this.m_servicepoint.setServicePointName(servicePointName);
			
			this.districtBranchID = districtBranchID;
			this.villageName = villageName;
			this.blockName=blockName;
			this.blockID=blockID;
			
			this.deleted = deleted;
		}
	public Integer getServicePointID() {
		return servicePointID;
	}

	public void setServicePointID(Integer servicePointID) {
		this.servicePointID = servicePointID;
	}

	public Integer getDistrictBranchID() {
		return districtBranchID;
	}

	public void setDistrictBranchID(Integer districtBranchID) {
		this.districtBranchID = districtBranchID;
	}

	public DistrictBranchMapping getDistrictBranchMapping() {
		return districtBranchMapping;
	}

	public void setDistrictBranchMapping(DistrictBranchMapping districtBranchMapping) {
		this.districtBranchMapping = districtBranchMapping;
	}

	public String getVillageName() {
		return villageName;
	}

	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	public M_ProviderServiceMapping getM_providerServiceMapping() {
		return m_providerServiceMapping;
	}

	public void setM_providerServiceMapping(M_ProviderServiceMapping m_providerServiceMapping) {
		this.m_providerServiceMapping = m_providerServiceMapping;
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

	public Integer getServicePointVillageMapID() {
		return servicePointVillageMapID;
	}
	
	public M_Servicepoint getM_servicepoint() {
		return m_servicepoint;
	}

	public void setM_servicepoint(M_Servicepoint m_servicepoint) {
		this.m_servicepoint = m_servicepoint;
	}
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
}
