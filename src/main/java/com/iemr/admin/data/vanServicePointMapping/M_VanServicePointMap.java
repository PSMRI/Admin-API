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
package com.iemr.admin.data.vanServicePointMapping;

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
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.servicePoint.M_Servicepoint;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;

@Data
@Entity
@Table(name="m_vanservicepointmap")
public class M_VanServicePointMap {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="VanServicePointMapID")
	private Integer vanServicePointMapID;
	
	@Expose
	@Column(name="VanID")
	private Integer vanID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "VanID")
	private M_Van m_van;
	
	@Expose
	@Transient
	private Integer districtID;
	
	@Expose
	@Transient
	private String districtName;
	
	@Expose
	@Transient
	private Integer blockID;
	
	@Expose
	@Transient
	private String blockName;
	
	@Expose
	@Column(name="ServicePointID")
	private Integer servicePointID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ServicePointID")
	private M_Servicepoint m_servicepoint;
	
	@Expose
	@Transient
	private String servicePointName;

	@Expose
	@Column(name="VanSession")
	private Short vanSession;
	
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
	
	public M_VanServicePointMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public M_VanServicePointMap(Integer vanServicePointMapID, Integer vanID, Short vanSession, Integer servicePointID, String servicePointName, 
			Integer providerServiceMapID, Boolean deleted) {
		super();
		this.vanServicePointMapID = vanServicePointMapID;
		this.vanID = vanID;
		this.servicePointID = servicePointID;
		this.servicePointName = servicePointName;
		this.vanSession = vanSession;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
	}
	
	public M_VanServicePointMap(Integer vanServicePointMapID, Integer vanID, Short vanSession, Integer servicePointID, String servicePointName, 
			Integer providerServiceMapID, Boolean deleted,Integer districtID,String districtName,Integer blockID,String blockName) {
		super();
		this.vanServicePointMapID = vanServicePointMapID;
		this.vanID = vanID;
		this.servicePointID = servicePointID;
		this.servicePointName = servicePointName;
		this.vanSession = vanSession;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.districtID=districtID;
		this.districtName=districtName;
		this.blockID=blockID;
		this.blockName=blockName;
	}

	public Integer getVanID() {
		return vanID;
	}

	public void setVanID(Integer vanID) {
		this.vanID = vanID;
	}

	public Integer getServicePointID() {
		return servicePointID;
	}

	public void setServicePointID(Integer servicePointID) {
		this.servicePointID = servicePointID;
	}

	public Short getVanSession() {
		return vanSession;
	}

	public void setVanSession(Short vanSession) {
		this.vanSession = vanSession;
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

	public Integer getVanServicePointMapID() {
		return vanServicePointMapID;
	}
	
	public String getServicePointName() {
		return servicePointName;
	}

	public void setServicePointName(String servicePointName) {
		this.servicePointName = servicePointName;
	}
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
}
