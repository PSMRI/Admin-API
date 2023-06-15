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
package com.iemr.admin.data.rolemaster;

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
@Table(name="m_userservicerolemapping")
public class M_UserservicerolemappingForRoleProviderAdmin {
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "USRMappingID")
	private Integer uSRMappingID;
	@Expose
	@Column(name = "UserID")
	private Integer userID;
	@Expose
	@Column(name = "RoleID")
	private Integer roleID;
	// @Expose
	// @Column(name="ServiceID")
	// private Integer serviceID;
	// @Expose
	// @Column(name="StateID")
	// private Integer stateID;
	// @Expose
	// @Column(name="ServiceProviderID")
	// private Integer serviceProviderID;
	@Expose
	@Column(name = "AgentID")
	private String agentID;
	@Expose
	@Column(name = "AgentPassword")
	private String agentPassword;
	@Expose
	@Column(name = "CZRole")
	private String cZRole;
	
	
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name = "WorkingLocationID")
	private Integer workingLocationID;
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
	
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProviderServiceMapID", insertable = false, updatable = false)
	@Expose
	private StateServiceMapping stateServiceMapping;
	

	@Transient
	@Expose
	private String stateName;
	
	@Transient
	@Expose
	private String serviceName;
	
	@Transient
	@Expose
	private Boolean isNational;
	
	@Transient
	@Expose
	private Integer serviceID;
	
	@Transient
	@Expose
	private Integer stateID;
	
	
	@Transient
	@Expose
	private Integer statusID;
	
	
	
	public M_UserservicerolemappingForRoleProviderAdmin() {
		// TODO Auto-generated constructor stub
	}
	
	
	public M_UserservicerolemappingForRoleProviderAdmin(Integer stateID, String stateName,Integer providerServiceMapID,Integer statusID) {
		this.stateID = stateID;
		this.stateName = stateName;
		this.providerServiceMapID=providerServiceMapID;
		this.statusID=statusID;
	}
	
	
	
	public M_UserservicerolemappingForRoleProviderAdmin(int providerServiceMapID,Integer stateID, String stateName) {
		this.stateID = stateID;
		this.stateName = stateName;
		this.providerServiceMapID=providerServiceMapID;
		//this.statusID=statusID;
	}
	
	
	
	public M_UserservicerolemappingForRoleProviderAdmin(String serviceName,Integer serviceID,Boolean isNational,Integer statusID) {
		this.serviceID = serviceID;
		//this.providerServiceMapID=providerServiceMapID;
		this.serviceName = serviceName;
		this.statusID=statusID;
		this.isNational=isNational;
	}
	
	
	
	
	
	public Integer getuSRMappingID() {
		return uSRMappingID;
	}
	public void setuSRMappingID(Integer uSRMappingID) {
		this.uSRMappingID = uSRMappingID;
	}
	public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	public Integer getRoleID() {
		return roleID;
	}
	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}
	public String getAgentID() {
		return agentID;
	}
	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}
	public String getAgentPassword() {
		return agentPassword;
	}
	public void setAgentPassword(String agentPassword) {
		this.agentPassword = agentPassword;
	}
	public String getcZRole() {
		return cZRole;
	}
	public void setcZRole(String cZRole) {
		this.cZRole = cZRole;
	}
	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}
	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}
	public Integer getWorkingLocationID() {
		return workingLocationID;
	}
	public void setWorkingLocationID(Integer workingLocationID) {
		this.workingLocationID = workingLocationID;
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


	public StateServiceMapping getStateServiceMapping() {
		return stateServiceMapping;
	}


	public void setStateServiceMapping(StateServiceMapping stateServiceMapping) {
		this.stateServiceMapping = stateServiceMapping;
	}






	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

	
	
	
	
	
	
}
