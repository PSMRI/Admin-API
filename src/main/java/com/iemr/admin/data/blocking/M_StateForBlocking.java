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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="m_state")
public class M_StateForBlocking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="StateID")
	private Integer stateID; 
	@Expose
	@Column(name="StateName")
	private String stateName;

	@Expose
	@Column(name="StateCode")
	private char stateCode;
	@Expose
	@Column(name="CountryID")
	private Integer countryID;
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
	private Timestamp lastModDate;
	
	
	@OneToOne(mappedBy="m_StateForBlocking")
	private M_Providerservicemapping_Blocking m_Providerservicemapping_Blocking;
	
	/*@OneToOne(mappedBy="stateMaster")
	private StateServiceMapping roleMapping;*/
	
	public M_StateForBlocking() {
		// TODO Auto-generated constructor stub
	}

	public Integer getStateID() {
		return stateID;
	}

	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}


	public char getStateCode() {
		return stateCode;
	}

	public void setStateCode(char stateCode) {
		this.stateCode = stateCode;
	}

	public Integer getCountryID() {
		return countryID;
	}

	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
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

	
	/*public StateServiceMapping getRoleMapping() {
		return roleMapping;
	}

	public void setRoleMapping(StateServiceMapping roleMapping) {
		this.roleMapping = roleMapping;
	}*/
	
	
	

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

	public String getStateName() {
		return stateName;
	}

	public M_Providerservicemapping_Blocking getM_Providerservicemapping_Blocking() {
		return m_Providerservicemapping_Blocking;
	}

	public void setM_Providerservicemapping_Blocking(M_Providerservicemapping_Blocking m_Providerservicemapping_Blocking) {
		this.m_Providerservicemapping_Blocking = m_Providerservicemapping_Blocking;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

	
	


}
