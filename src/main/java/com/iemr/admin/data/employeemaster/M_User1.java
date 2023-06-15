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
package com.iemr.admin.data.employeemaster;

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
@Table(name="m_User")
public class M_User1{
	
	   @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="UserID")
		private Integer userID;
	   @Expose
		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(updatable = false, insertable = false, name = "userID")
		private M_UserServiceRoleMapping2 m_userServiceRoleMapping;

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
		private Short genderID;
	   @Expose
	   @Transient
	   String genderName;
	   
	   @Expose
	   @Column(name="MaritalStatusID")
		private Integer maritalStatusID;
	   @Expose
	   @Column(name="DesignationID")
	   private Integer designationID;
	   
	   @Expose
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(updatable = false, insertable = false, name = "DesignationID")
	   private M_Designation m_designation;
	   @Expose
	   @Transient
	   String designationName;
	   
	   @Expose
	   @Column(name="AadhaarNo")
		private String aadhaarNo;
	   @Expose
	   @Column(name="PAN")
		private String pAN;
	   @Expose
	   @Column(name="DOB")
		private Date dOB;
	   @Expose
	   @Column(name="DOJ")
		private Date dOJ;
	   @Expose
	   @Column(name="QualificationID")
		private Integer qualificationID;
	   @Expose
	   @Column(name="HealthProfessionalID")
		private String healthProfessionalID; 
	   @Expose
	   @Column(name="UserName")
		private String userName;
	   @Expose
	   @Column(name="Password")
		private String password;
	   @Expose
	   @Column(name="IsExternal")
		private Boolean isExternal;
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
	   @Column(name="EmployeeID")
	   private String employeeID;
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
	   @Column(name="Remarks")
	   private String remarks;
	   
	   @Expose
	   @Column(name="ContactNo")
	   private String contactNo;
	   
	   
	   @Expose
	   @Column(name="IsProviderAdmin")
	   private Boolean isProviderAdmin;
	   
	   @Expose
	   @Column(name="ServiceProviderID")
	 private Integer serviceProviderID;
	   
	   
	   
	   /*@OneToOne(mappedBy="employeeMaster")
		private M_UserServiceRoleMapping2 m_UserServiceRoleMapping;
	   */
	   
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "TitleID", insertable = false, updatable = false)
	   @Expose
	    private M_Title m_Title;
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "StatusID", insertable = false, updatable = false)
	   @Expose
	    private M_Status m_Status;
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "qualificationID", insertable = false, updatable = false)
	   @Expose
	    private M_Userqualification m_Userqualification;
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "genderID", insertable = false, updatable = false)
	   @Expose
	    private M_Gender m_Gender;
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "maritalstatusID", insertable = false, updatable = false)
	   @Expose
	    private M_Maritalstatus m_Maritalstatus;
	   
	   /*@OneToOne(mappedBy="m_user")
		private M_UserLangMapping m_UserLangMapping1;*/
	
	// new field for rate-limit, failed authentication
			@Expose
			@Column(name = "failed_attempt", insertable = false)
			private Integer failedAttempt;
	   public M_User1() {
		// TODO Auto-generated constructor stub
	}

	   public M_User1(Integer userID,String userName) {
			// TODO Auto-generated constructor stub
		   this.userID=userID;
		   this.userName=userName;
		}
	   
	   
	   
	public Integer getFailedAttempt() {
		return failedAttempt;
	}

	public void setFailedAttempt(Integer failedAttempt) {
		this.failedAttempt = failedAttempt;
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

	public Short getGenderID() {
		return genderID;
	}

	public void setGenderID(Short genderID) {
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
	
	public String getHealthProfessionalID() {
		return healthProfessionalID;
	}

	public void setHealthProfessionalID(String healthProfessionalID) {
		this.healthProfessionalID = healthProfessionalID;
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




	public M_Title getM_Title() {
		return m_Title;
	}




	public void setM_Title(M_Title m_Title) {
		this.m_Title = m_Title;
	}




	public M_Status getM_Status() {
		return m_Status;
	}




	public void setM_Status(M_Status m_Status) {
		this.m_Status = m_Status;
	}




	public M_Userqualification getM_Userqualification() {
		return m_Userqualification;
	}




	public void setM_Userqualification(M_Userqualification m_Userqualification) {
		this.m_Userqualification = m_Userqualification;
	}




	public M_Gender getM_Gender() {
		return m_Gender;
	}




	public void setM_Gender(M_Gender m_Gender) {
		this.m_Gender = m_Gender;
	}




	public M_Maritalstatus getM_Maritalstatus() {
		return m_Maritalstatus;
	}




	public void setM_Maritalstatus(M_Maritalstatus m_Maritalstatus) {
		this.m_Maritalstatus = m_Maritalstatus;
	}
	
	public M_UserServiceRoleMapping2 getM_userServiceRoleMapping() {
		return m_userServiceRoleMapping;
	}




	public void setM_userServiceRoleMapping(M_UserServiceRoleMapping2 m_userServiceRoleMapping) {
		this.m_userServiceRoleMapping = m_userServiceRoleMapping;
	}
	



	/**
	 * @return the designationID
	 */
	public Integer getDesignationID() {
		return designationID;
	}




	/**
	 * @param designationID the designationID to set
	 */
	public void setDesignationID(Integer designationID) {
		this.designationID = designationID;
	}

	public String getGenderName() {
		return genderName;
	}




	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}




	public String getDesignationName() {
		return designationName;
	}
	
	
	




	public M_Designation getM_designation() {
		return m_designation;
	}




	public void setM_designation(M_Designation m_designation) {
		this.m_designation = m_designation;
	}




	public String getRemarks() {
		return remarks;
	}




	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}




	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}
	
	
	


	public Boolean getIsProviderAdmin() {
		return isProviderAdmin;
	}
	
	




	/*public M_UserLangMapping getM_UserLangMapping1() {
		return m_UserLangMapping1;
	}




	public void setM_UserLangMapping1(M_UserLangMapping m_UserLangMapping1) {
		this.m_UserLangMapping1 = m_UserLangMapping1;
	}
*/



	public void setIsProviderAdmin(Boolean isProviderAdmin) {
		this.isProviderAdmin = isProviderAdmin;
	}


	
	



	public Date getdOB() {
		return dOB;
	}




	public void setdOB(Date dOB) {
		this.dOB = dOB;
	}




	public Date getdOJ() {
		return dOJ;
	}




	public void setdOJ(Date dOJ) {
		this.dOJ = dOJ;
	}




	public String getContactNo() {
		return contactNo;
	}




	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	
	





	public Integer getServiceProviderID() {
		return serviceProviderID;
	}




	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	public String getEmployeeID() {
		return employeeID;
	}




	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}











	public Boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(Boolean isExternal) {
		this.isExternal = isExternal;
	}











	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	
	
	   
	   
	   

}
