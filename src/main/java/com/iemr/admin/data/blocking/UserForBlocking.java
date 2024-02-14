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

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="m_user")
public class UserForBlocking {
	
	@Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="UserID")
		private Integer userID;
	   @Expose
	   @Column(name="TitleID")
		private Integer titleID;
	   @Expose
	   @Column(name="FirstName")
		private String firstName;
	   @Expose
	   @Column(name="MiddleName")
		private String middleName; 
	   @Expose
	   @Column(name="LastName")
		private String lastName; 
	   @Expose
	   @Column(name="GenderID")
		private Integer genderID;
	   @Expose
	   @Column(name="MaritalStatusID")
		private Integer maritalStatusID;
	   @Expose
	   @Column(name="AadhaarNo")
		private String aadhaarNo;
	   @Expose
	   @Column(name="PAN")
		private String pAN;
	   @Expose
	   @Column(name="DOB")
		private Timestamp dOB;
	   @Expose
	   @Column(name="DOJ")
		private Timestamp dOJ;
	   @Expose
	   @Column(name="QualificationID")
		private Integer qualificationID; 
	   @Expose
	   @Column(name="UserName")
		private String userName;
	   @Expose
	   @Column(name="Password")
		private String password;
	   @Expose
	   @Column(name="AgentID")
		private String agentID;
	   @Expose
	   @Column(name="AgentPassword")
		private String agentPassword;
	   @Expose
	   @Column(name="EmailID")
		private String emailID;
	   @Expose
	   @Column(name="StatusID")
		private Integer statusID;
	   @Expose
	   @Column(name="EmergencyContactPerson")
		private String emergencyContactPerson; 
	   @Expose
	   @Column(name="EmergencyContactNo")
		private String emergencyContactNo;
	   @Expose
	   @Column(name="IsSupervisor")
		private Boolean isSupervisor;
	   @Expose
	   @Column(name="Deleted",insertable = false, updatable = true)
		private Boolean deleted;
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
	   
	   
	   @Expose
	   @Column(name="CZUserID")
	  private String  cZUserID; 
	   @Expose
	   @Column(name="CZPassword")
	  private String  cZPassword;
	   @Expose
	   @Column(name="CZRole")
	  private String cZRole;
	   
	   
	   
	 public UserForBlocking() {
		// TODO Auto-generated constructor stub
	}

	   
	   
	 
	 
	 
	 
	 
	 
	 
	   
	public String getcZUserID() {
		return cZUserID;
	}











	public void setcZUserID(String cZUserID) {
		this.cZUserID = cZUserID;
	}











	public String getcZPassword() {
		return cZPassword;
	}











	public void setcZPassword(String cZPassword) {
		this.cZPassword = cZPassword;
	}











	public String getcZRole() {
		return cZRole;
	}











	public void setcZRole(String cZRole) {
		this.cZRole = cZRole;
	}











	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Integer getTitleID() {
		return titleID;
	}

	public void setTitleID(Integer titleID) {
		this.titleID = titleID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getGenderID() {
		return genderID;
	}

	public void setGenderID(Integer genderID) {
		this.genderID = genderID;
	}

	public Integer getMaritalStatusID() {
		return maritalStatusID;
	}

	public void setMaritalStatusID(Integer maritalStatusID) {
		this.maritalStatusID = maritalStatusID;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public String getpAN() {
		return pAN;
	}

	public void setpAN(String pAN) {
		this.pAN = pAN;
	}

	

	public Integer getQualificationID() {
		return qualificationID;
	}

	public void setQualificationID(Integer qualificationID) {
		this.qualificationID = qualificationID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public Integer getStatusID() {
		return statusID;
	}

	public void setStatusID(Integer statusID) {
		this.statusID = statusID;
	}

	public String getEmergencyContactPerson() {
		return emergencyContactPerson;
	}

	public void setEmergencyContactPerson(String emergencyContactPerson) {
		this.emergencyContactPerson = emergencyContactPerson;
	}

	public String getEmergencyContactNo() {
		return emergencyContactNo;
	}

	public void setEmergencyContactNo(String emergencyContactNo) {
		this.emergencyContactNo = emergencyContactNo;
	}

	public Boolean getIsSupervisor() {
		return isSupervisor;
	}

	public void setIsSupervisor(Boolean isSupervisor) {
		this.isSupervisor = isSupervisor;
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

	
	
	

	/*public M_UserServiceRoleMapping2 getM_UserServiceRoleMapping() {
		return m_UserServiceRoleMapping;
	}

	public void setM_UserServiceRoleMapping(M_UserServiceRoleMapping2 m_UserServiceRoleMapping) {
		this.m_UserServiceRoleMapping = m_UserServiceRoleMapping;
	}
	*/


	public Timestamp getdOB() {
		return dOB;
	}











	public void setdOB(Timestamp dOB) {
		this.dOB = dOB;
	}











	public Timestamp getdOJ() {
		return dOJ;
	}











	public void setdOJ(Timestamp dOJ) {
		this.dOJ = dOJ;
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
