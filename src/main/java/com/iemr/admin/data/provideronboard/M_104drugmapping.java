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
import com.iemr.admin.data.locationmaster.State;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name = "m_104drugmapping")
public class M_104drugmapping {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "DrugMapID")
	private Integer drugMapID;
	
	@Expose
	@Column(name = "DrugId")
	private Integer drugId;
	
	@Expose
	@Column(name = "DrugName")
	private String drugName;
	
	@Expose
	@Column(name = "DrugGroupID")
	private Integer drugGroupID;
	
	@Expose
	@Column(name = "DrugGroupName")
	private String drugGroupName;
	
	@Expose
	@Column(name = "Remarks")
	private String remarks;
	
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ProviderServiceMapID")
	private M_ProviderServiceMapping m_providerServiceMapping;
	
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "drugGroupID")
	private M_104druggroup m_104druggroup;
	
	
	
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
	private Timestamp createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy; 
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;
	
	
	
	@Transient
	@Expose
	private Boolean drugGroupDeleted;
	
	public M_104drugmapping() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public M_104drugmapping(Integer drugMapID, Integer drugId, String drugName, Integer drugGroupID,
			String drugGroupName, String remarks,Boolean deleted, Integer providerServiceMapID, Integer stateID,String stateName,Boolean drugGroupDeleted) {
		super();
		this.drugMapID = drugMapID;
		this.drugId = drugId;
		this.drugName = drugName;
		this.drugGroupID = drugGroupID;
		this.drugGroupName = drugGroupName;
		this.remarks = remarks;
		this.deleted = deleted;
		this.providerServiceMapID = providerServiceMapID;
		this.m_providerServiceMapping = new M_ProviderServiceMapping();
		this.m_providerServiceMapping.setState(new State());
		this.m_providerServiceMapping.getState().setStateID(stateID);
		this.m_providerServiceMapping.getState().setStateName(stateName);
		this.drugGroupDeleted=drugGroupDeleted;
	}
	
	public Integer getDrugMapID() {
		return drugMapID;
	}

	public void setDrugMapID(Integer drugMapID) {
		this.drugMapID = drugMapID;
	}

	public Integer getDrugId() {
		return drugId;
	}

	public void setDrugId(Integer drugId) {
		this.drugId = drugId;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public Integer getDrugGroupID() {
		return drugGroupID;
	}

	public void setDrugGroupID(Integer drugGroupID) {
		this.drugGroupID = drugGroupID;
	}

	public String getDrugGroupName() {
		return drugGroupName;
	}

	public void setDrugGroupName(String drugGroupName) {
		this.drugGroupName = drugGroupName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public M_ProviderServiceMapping getM_providerServiceMapping() {
		return m_providerServiceMapping;
	}

	public void setM_providerServiceMapping(M_ProviderServiceMapping m_providerServiceMapping) {
		this.m_providerServiceMapping = m_providerServiceMapping;
	}
	
	
	
	public M_104druggroup getM_104druggroup() {
		return m_104druggroup;
	}

	public void setM_104druggroup(M_104druggroup m_104druggroup) {
		this.m_104druggroup = m_104druggroup;
	}





	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	

}
