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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="m_screen")
public class M_Screen {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ScreenID")
	private Integer screenID;
	@Expose
	@Column(name="ScreenName")
	private String screenName;
	@Expose
	@Column(name="ApiUsed")
	private String apiUsed;
	@Expose
	@Column(name="WorkflowName")
	private String workflowName; 
	@Expose
	@Column(name="ScreenDesc")
	private String screenDesc; 
	@Expose
	@Column(name="Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name="Processed", insertable = false, updatable = false)
	private String processed;
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
	
	
//	@OneToOne(mappedBy="m_Screen")
//	private RoleScreenMapping roleScreenMapping;
	
	@Expose
	@Column(name="ProviderServiceMapId")
	private Integer providerServiceMapId;
	
	/**
	 * @return the roleScreenMapping
	 */
	/*public RoleScreenMapping getRoleScreenMapping() {
		return roleScreenMapping;
	}


	*//**
	 * @param roleScreenMapping the roleScreenMapping to set
	 *//*
	public void setRoleScreenMapping(RoleScreenMapping roleScreenMapping) {
		this.roleScreenMapping = roleScreenMapping;
	}*/









	@Expose
	@Column(name="ServiceID")
	private Integer serviceID;
	
	
	
	public M_Screen() {
		// TODO Auto-generated constructor stub
	}


	public Integer getScreenID() {
		return screenID;
	}


	public void setScreenID(Integer screenID) {
		this.screenID = screenID;
	}


	public String getScreenName() {
		return screenName;
	}


	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}


	public String getApiUsed() {
		return apiUsed;
	}


	public void setApiUsed(String apiUsed) {
		this.apiUsed = apiUsed;
	}


	public String getWorkflowName() {
		return workflowName;
	}


	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}


	public String getScreenDesc() {
		return screenDesc;
	}


	public void setScreenDesc(String screenDesc) {
		this.screenDesc = screenDesc;
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


	public Integer getProviderServiceMapId() {
		return providerServiceMapId;
	}


	public void setProviderServiceMapId(Integer providerServiceMapId) {
		this.providerServiceMapId = providerServiceMapId;
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





	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

}
