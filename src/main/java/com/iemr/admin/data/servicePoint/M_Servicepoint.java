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
import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="m_servicepoint")
public class M_Servicepoint {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ServicePointID")
	private Integer servicePointID;
	
	@Expose
	@Column(name="ServicePointName")
	private String servicePointName;
	@Expose
	@Column(name="ServicePointDesc")
	private String servicePointDesc;
	
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ProviderServiceMapID")
	private M_ProviderServiceMapping m_providerServiceMapping;
	
	@Expose
	@Transient
	private Integer serviceProviderID;
	
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
	@Column(name="ServicePointHQAddress")
	private String servicePointHQAddress;
	
	@Expose
	@Column(name="ParkingPlaceID")
	private Integer parkingPlaceID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "parkingPlaceID")
	private M_Parkingplace m_parkingplace;

	@Expose
	@Transient
	private String parkingPlaceName;
	
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
	
	public M_Servicepoint() {
		super();
		// TODO Auto-generated constructor stub
	}

	public M_Servicepoint(Integer servicePointID, String servicePointName, String servicePointDesc,String servicePointHQAddress,
			Integer providerServiceMapID, Boolean deleted, Integer countryID, String countryName, Integer stateID, String stateName, Integer districtID,
			String districtName, Integer districtBlockID, String blockName, Integer districtBranchID, String villageName,
			M_ProviderServiceMapping m_providerServiceMapping, Integer serviceID,String serviceName, Integer parkingPlaceID, String parkingPlaceName) {
		super();
		this.servicePointID = servicePointID;
		this.servicePointName = servicePointName;
		this.servicePointDesc = servicePointDesc;
		this.providerServiceMapID = providerServiceMapID;
		this.providerServiceMapID = providerServiceMapID;
		this.m_providerServiceMapping = m_providerServiceMapping;
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
		this.servicePointHQAddress = servicePointHQAddress;
		this.deleted = deleted;
		this.parkingPlaceID = parkingPlaceID;
		this.parkingPlaceName = parkingPlaceName;
	}

	public String getServicePointName() {
		return servicePointName;
	}

	public void setServicePointName(String servicePointName) {
		this.servicePointName = servicePointName;
	}

	public String getServicePointDesc() {
		return servicePointDesc;
	}

	public void setServicePointDesc(String servicePointDesc) {
		this.servicePointDesc = servicePointDesc;
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

	public Integer getCountryID() {
		return countryID;
	}

	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}

	public M_Country getM_country() {
		return m_country;
	}

	public void setM_country(M_Country m_country) {
		this.m_country = m_country;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getStateID() {
		return stateID;
	}

	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Integer getDistrictID() {
		return districtID;
	}

	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}

	public M_District getM_district() {
		return m_district;
	}

	public void setM_district(M_District m_district) {
		this.m_district = m_district;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Integer getDistrictBlockID() {
		return districtBlockID;
	}

	public void setDistrictBlockID(Integer districtBlockID) {
		this.districtBlockID = districtBlockID;
	}

	public DistrictBlock getDistrictBlock() {
		return districtBlock;
	}

	public void setDistrictBlock(DistrictBlock districtBlock) {
		this.districtBlock = districtBlock;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
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

	public String getServicePointHQAddress() {
		return servicePointHQAddress;
	}

	public void setServicePointHQAddress(String servicePointHQAddress) {
		this.servicePointHQAddress = servicePointHQAddress;
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

	public Integer getServicePointID() {
		return servicePointID;
	}
	
	public Integer getParkingPlaceID() {
		return parkingPlaceID;
	}

	public void setParkingPlaceID(Integer parkingPlaceID) {
		this.parkingPlaceID = parkingPlaceID;
	}

	public M_Parkingplace getM_parkingplace() {
		return m_parkingplace;
	}

	public void setM_parkingplace(M_Parkingplace m_parkingplace) {
		this.m_parkingplace = m_parkingplace;
	}
	
	public String getParkingPlaceName() {
		return parkingPlaceName;
	}

	public void setParkingPlaceName(String parkingPlaceName) {
		this.parkingPlaceName = parkingPlaceName;
	}

	public Integer getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	
}
