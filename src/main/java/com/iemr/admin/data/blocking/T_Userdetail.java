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
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="t_userdetail")
public class T_Userdetail {
	
	
	@Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="UserDetailID")
	private Integer userDetailID;
	@Expose
	@Column(name="UserID")
	private Integer userID;
	@Expose
	@Column(name="UserName")
	private String userName; 
	@Expose
	@Column(name="PreviousPassword")
	private String previousPassword;
	@Expose
	@Column(name="UpdatedPassword")
	private String updatedPassword; 
	@Expose
	@Column(name="CZUserID")
	private String cZUserID; 
	@Expose
	@Column(name="PreviousCZPassword")
	private String previousCZPassword;
	@Expose
	@Column(name="UpdatedCZPassword")
	private String updatedCZPassword;
	@Expose
	@Column(name="AgentID")
	private String agentID;
	@Expose
	@Column(name="PreviousAgentPassword")
	private String previousAgentPassword;
	@Expose
	@Column(name="UpdatedAgentPassword")
	private String updatedAgentPassword; 
	@Expose
	@Column(name="CZRole")
	private String cZRole;
	@Expose
	@Column(name="PreviousStatusID")
	private Integer previousStatusID;
	@Expose
	@Column(name="UpdatedStatusID")
	private Integer updatedStatusID;
	@Expose
	@Column(name="Deleted",insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name="Processed",insertable = false, updatable = true)
	private String processed;
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;
	@Expose
	@Column(name="CreatedDate",insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy; 
	@Expose
	@Column(name="LastModDate",insertable = false, updatable = false)
	private Timestamp lastModDate;
	
	
	public T_Userdetail() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	public Integer getUserDetailID() {
		return userDetailID;
	}


	public void setUserDetailID(Integer userDetailID) {
		this.userDetailID = userDetailID;
	}


	public Integer getUserID() {
		return userID;
	}


	public void setUserID(Integer userID) {
		this.userID = userID;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPreviousPassword() {
		return previousPassword;
	}


	public void setPreviousPassword(String previousPassword) {
		this.previousPassword = previousPassword;
	}


	public String getUpdatedPassword() {
		return updatedPassword;
	}


	public void setUpdatedPassword(String updatedPassword) {
		this.updatedPassword = updatedPassword;
	}


	public String getcZUserID() {
		return cZUserID;
	}


	public void setcZUserID(String cZUserID) {
		this.cZUserID = cZUserID;
	}


	public String getPreviousCZPassword() {
		return previousCZPassword;
	}


	public void setPreviousCZPassword(String previousCZPassword) {
		this.previousCZPassword = previousCZPassword;
	}


	public String getUpdatedCZPassword() {
		return updatedCZPassword;
	}


	public void setUpdatedCZPassword(String updatedCZPassword) {
		this.updatedCZPassword = updatedCZPassword;
	}


	public String getAgentID() {
		return agentID;
	}


	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}


	public String getPreviousAgentPassword() {
		return previousAgentPassword;
	}


	public void setPreviousAgentPassword(String previousAgentPassword) {
		this.previousAgentPassword = previousAgentPassword;
	}


	public String getUpdatedAgentPassword() {
		return updatedAgentPassword;
	}


	public void setUpdatedAgentPassword(String updatedAgentPassword) {
		this.updatedAgentPassword = updatedAgentPassword;
	}


	public String getcZRole() {
		return cZRole;
	}


	public void setcZRole(String cZRole) {
		this.cZRole = cZRole;
	}


	public Integer getPreviousStatusID() {
		return previousStatusID;
	}


	public void setPreviousStatusID(Integer previousStatusID) {
		this.previousStatusID = previousStatusID;
	}


	public Integer getUpdatedStatusID() {
		return updatedStatusID;
	}


	public void setUpdatedStatusID(Integer updatedStatusID) {
		this.updatedStatusID = updatedStatusID;
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













	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	
	
	

}


