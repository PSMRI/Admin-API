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
@Table(name="v_showuser")
public class V_Showuser {
	

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
		private Short genderID;
	   @Expose
	   @Column(name="IsExternal")
	   private Boolean isExternal;
	   @Expose
	   @Column(name="MaritalStatusID")
		private Integer maritalStatusID;
	   @Expose
	   @Column(name="DesignationID")
	   private Integer designationID;
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
	   @Column(name="HealthProfessionalID")
		private String healthProfessionalID;
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
	   @Column(name="TitleName")
	private String titleName;
	   @Expose
	   @Column(name="GenderName")
	private String genderName; 
	   @Expose
	   @Column(name="UserQualification")
	private String userQualification;
	   @Expose
	   @Column(name="DesignationName")
	private String designationName; 
	   @Expose
	   @Column(name="MaritalStatus")
	private String maritalStatus; 
	@Expose
	@Column(name="Status")
	private String status;
	
	@Expose
	@Column(name="Remarks")
	private String remarks;
	
	
	@Expose
	   @Column(name="IsProviderAdmin")
	   private Boolean isProviderAdmin;
	
	 @Expose
	   @Column(name="ContactNo")
	   private String contactNo;
	 
	 @Expose
	   @Column(name="DemographicID")
	 private Integer demographicID; 
	 @Expose
	   @Column(name="FathersName")
	 private String fathersName;
	 @Expose
	   @Column(name="MothersName")
	 private String mothersName;
	 @Expose
	   @Column(name="CommunityID")
	 private Integer communityID;
	 @Expose
	   @Column(name="CommunityType")
	 private String communityType; 
	 @Expose
	   @Column(name="ReligionID")
	 private Integer religionID;
	 @Expose
	   @Column(name="ReligionType")
	 private String religionType;
	 @Expose
	   @Column(name="AddressLine1")
	 private String addressLine1;
	 @Expose
	   @Column(name="AddressLine2")
	 private String addressLine2;
	 @Expose
	   @Column(name="PermAddressLine1")
	   private String permAddressLine1;
	   @Expose
	   @Column(name="PermAddressLine2")
	   private String permAddressLine2;
	   @Expose
	   @Column(name="PermanentAddress")
	   private String permanentAddress;
	   
	   
	   @Expose
	   @Column(name="PermStateID")
	   private Integer permStateID;
	   
	   @Expose
	   @Column(name="PermDistrictID")
	   private Integer permDistrictID;
	   @Expose
	   @Column(name="PermPinCode")
	   private Integer permPinCode;
	 @Expose
	   @Column(name="CityID")
	 private Integer cityID;
	 @Expose
	   @Column(name="StateID")
	 private Integer stateID;
	 @Expose
	   @Column(name="StateName")
	 private String stateName;
	 @Expose
	   @Column(name="CountryID")
	 private Integer countryID;
	 @Expose
	   @Column(name="CountryName")
	 private String countryName;
	 @Expose
	   @Column(name="PinCode")
	 private String pinCode;
	 @Expose
	   @Column(name="IsPresent")
	 private Boolean isPresent;
	 @Expose
	   @Column(name="IsPermanent")
	 private Boolean isPermanent;
	 @Expose
	   @Column(name="DemographicDeleted")
	 private Boolean demographicDeleted;
	 
	   @Expose
	   @Column(name="ServiceProviderID")
	 private Integer serviceProviderID;
	   
	   @Expose
	   @Column(name="DistrictID")
	   private Integer districtID;

	 
	 
	 
	 
	 
	 
	
	
	public Integer getDemographicID() {
		return demographicID;
	}


	public void setDemographicID(Integer demographicID) {
		this.demographicID = demographicID;
	}


	public String getFathersName() {
		return fathersName;
	}


	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}


	public String getMothersName() {
		return mothersName;
	}


	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}


	public Integer getCommunityID() {
		return communityID;
	}


	public void setCommunityID(Integer communityID) {
		this.communityID = communityID;
	}


	public String getCommunityType() {
		return communityType;
	}


	public void setCommunityType(String communityType) {
		this.communityType = communityType;
	}


	public Integer getReligionID() {
		return religionID;
	}


	public void setReligionID(Integer religionID) {
		this.religionID = religionID;
	}


	public String getReligionType() {
		return religionType;
	}


	public void setReligionType(String religionType) {
		this.religionType = religionType;
	}


	public String getAddressLine1() {
		return addressLine1;
	}


	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}


	public String getAddressLine2() {
		return addressLine2;
	}


	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}


	


	public String getPermAddressLine1() {
		return permAddressLine1;
	}


	public void setPermAddressLine1(String permAddressLine1) {
		this.permAddressLine1 = permAddressLine1;
	}


	public String getPermAddressLine2() {
		return permAddressLine2;
	}


	public void setPermAddressLine2(String permAddressLine2) {
		this.permAddressLine2 = permAddressLine2;
	}


	public Integer getPermStateID() {
		return permStateID;
	}


	public void setPermStateID(Integer permStateID) {
		this.permStateID = permStateID;
	}


	public Integer getPermDistrictID() {
		return permDistrictID;
	}


	public void setPermDistrictID(Integer permDistrictID) {
		this.permDistrictID = permDistrictID;
	}


	public Integer getPermPinCode() {
		return permPinCode;
	}


	public void setPermPinCode(Integer permPinCode) {
		this.permPinCode = permPinCode;
	}


	public String getPermanentAddress() {
		return permanentAddress;
	}


	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}


	public Integer getCityID() {
		return cityID;
	}


	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}


	public Integer getStateID() {
		return stateID;
	}


	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}


	public String getStateName() {
		return stateName;
	}


	public void setStateName(String stateName) {
		this.stateName = stateName;
	}


	public Integer getCountryID() {
		return countryID;
	}


	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}


	public String getCountryName() {
		return countryName;
	}


	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}


	public String getPinCode() {
		return pinCode;
	}


	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}


	public Boolean getIsPresent() {
		return isPresent;
	}


	public void setIsPresent(Boolean isPresent) {
		this.isPresent = isPresent;
	}


	public Boolean getIsPermanent() {
		return isPermanent;
	}


	public void setIsPermanent(Boolean isPermanent) {
		this.isPermanent = isPermanent;
	}


	public Boolean getDemographicDeleted() {
		return demographicDeleted;
	}


	public void setDemographicDeleted(Boolean demographicDeleted) {
		this.demographicDeleted = demographicDeleted;
	}


	public V_Showuser() {
		// TODO Auto-generated constructor stub
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


	public Integer getDesignationID() {
		return designationID;
	}


	public void setDesignationID(Integer designationID) {
		this.designationID = designationID;
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


	public String getTitleName() {
		return titleName;
	}


	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}


	public String getGenderName() {
		return genderName;
	}


	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}


	public String getUserQualification() {
		return userQualification;
	}


	public void setUserQualification(String userQualification) {
		this.userQualification = userQualification;
	}


	public String getDesignationName() {
		return designationName;
	}


	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}


	public String getMaritalStatus() {
		return maritalStatus;
	}


	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}




	public Boolean getIsProviderAdmin() {
		return isProviderAdmin;
	}


	public void setIsProviderAdmin(Boolean isProviderAdmin) {
		this.isProviderAdmin = isProviderAdmin;
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
	
	
	








	public Integer getDistrictID() {
		return districtID;
	}


	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}











	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

}
