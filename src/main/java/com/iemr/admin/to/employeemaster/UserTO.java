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
package com.iemr.admin.to.employeemaster;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.gson.annotations.Expose;

public class UserTO {
	
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
	   @Column(name="DesignationID")
	   private Integer designationID;
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
		private Date dOB;
	   @Expose
	   @Column(name="DOJ")
		private Date dOJ;
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
	   @Column(name="IsExternal")
		private Boolean isExternal;
	   @Expose
	   @Column(name="Deleted")
		private Boolean deleted;
	   @Expose
	   @Column(name="CreatedBy")
		private String createdBy; 
	   @Expose
	   @Column(name="CreatedDate")
		private Date createdDate;
	   @Expose
	   @Column(name="ModifiedBy")
		private String modifiedBy;
	   @Expose
	   @Column(name="LastModDate")
		private Date lastModDate;
	   
	   
	   
	   @Expose
	   @Column(name="DemographicID")
	   private Integer demographicID;
	   @Expose
	   @Column(name="FathersName")
	   private String fathersName;
	   @Expose
	   @Column(name="MothersName")
	   private String  mothersName;
	   @Expose
	   @Column(name="CommunityID")
	   private Integer communityID; 
	   @Expose
	   @Column(name="ReligionID")
	   private Integer religionID;
	   @Expose
	   @Column(name="AddressLine1")
	   private String addressLine1;
	   @Expose
	   @Column(name="AddressLine2")
	   private String addressLine2;
	   @Expose
	   @Column(name="AddressLine3")
	   private String addressLine3;
	   @Expose
	   @Column(name="AddressLine4")
	   private String addressLine4;
	   @Expose
	   @Column(name="AddressLine5")
	   private String addressLine5;
	   @Expose
	   @Column(name="CityID")
	   private Integer cityID;
	   @Expose
	   @Column(name="StateID")
	   private Integer stateID;
	   @Expose
	   @Column(name="CountryID")
	   private Integer countryID;
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
	   @Column(name="PSAddMapID")
	   private Integer pSAddMapID;
	   @Expose
	   @Column(name="LocationName")
	   private String  locationName;
	   //@Expose
	   //@Column(name="ProviderServiceMapID")
	   //private Integer  providerServiceMapID;
	   @Expose
	   @Column(name="DistrictID")
	   private Integer  districtID;
	   @Expose
	   @Column(name="Address")
	   private String  address;
	   
	   
	   private Integer oldRoleId;
	   private Integer nRoleId;
	   
	   
	   @Expose
	   @Column(name="UserLangID")
	   private Integer userLangID;
	   @Expose
	   @Column(name="LanguageID")
	   private Integer languageID[];
	   @Expose
	   @Column(name="Weightage")
	   private Integer weightage[];
	   
	   @Expose
	   @Column(name="Weightage_Read")
	   private Integer weightage_Read[];
	   @Expose
	   @Column(name="Weightage_Write")
	   private Integer weightage_Write[];
	   @Expose
	   @Column(name="Weightage_Speak")
	   private Integer weightage_Speak[];
	   
	   
	   @Expose
	   @Column(name="USRMappingID")
	   private Integer uSRMappingID; 
	   @Expose
	  @Column(name="RoleID1")
	   private Integer roleID1[];
	   
	   
	   @Expose
		  @Column(name="CanRead") 
		  private Boolean canRead[];
		  @Expose
		  @Column(name="CanWrite")
		  private Boolean canWrite[];
		  @Expose
		  @Column(name="CanSpeak")
		  private Boolean canSpeak[];

	   
	   
	   @Expose
	   @Column(name="ServiceID")
	   private Integer serviceID; 
	   
	   @Expose
	   @Column(name="ServiceProviderID")
	 private Integer serviceProviderID;
	   
	   @Expose
	   @Column(name="ProviderServiceMapID")
	   private Integer providerServiceMapID;
	   
	   @Expose
	   @Column(name="WorkingLocationID")
	   private Integer workingLocationID;
	   
	   @Expose
		@Column(name = "isInbound")
		private Boolean inbound;
		
		@Expose
		@Column(name = "isOutbound")
		private Boolean outbound;
	   
	   
	   
	   //@Expose
	   //@Column(name="ServiceProviderID")
	   //private Integer serviceProviderID;
	  // @Expose
	   //@Column(name="WorkingLocationID")
	   //private Integer workingLocationID;
	   
	   public UserTO() {
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

	public Integer getReligionID() {
		return religionID;
	}

	public void setReligionID(Integer religionID) {
		this.religionID = religionID;
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

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getAddressLine5() {
		return addressLine5;
	}

	public void setAddressLine5(String addressLine5) {
		this.addressLine5 = addressLine5;
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

	public Integer getCountryID() {
		return countryID;
	}

	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
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

	public Integer getpSAddMapID() {
		return pSAddMapID;
	}

	public void setpSAddMapID(Integer pSAddMapID) {
		this.pSAddMapID = pSAddMapID;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/*public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}*/

	public Integer getDistrictID() {
		return districtID;
	}

	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getUserLangID() {
		return userLangID;
	}

	public void setUserLangID(Integer userLangID) {
		this.userLangID = userLangID;
	}

	
	

	public Integer[] getLanguageID() {
		return languageID;
	}

	public void setLanguageID(Integer[] languageID) {
		this.languageID = languageID;
	}

	
      
	

	/**
	 * @return the weightage
	 */
	public Integer[] getWeightage() {
		return weightage;
	}

	/**
	 * @param weightage the weightage to set
	 */
	public void setWeightage(Integer[] weightage) {
		this.weightage = weightage;
	}
	
	

	public Integer getuSRMappingID() {
		return uSRMappingID;
	}

	public void setuSRMappingID(Integer uSRMappingID) {
		this.uSRMappingID = uSRMappingID;
	}

	
/*
	public Integer[] getRoleID() {
		return roleID;
	}

	public void setRoleID(Integer[] roleID) {
		this.roleID = roleID;
	}*/

	public Integer getServiceID() {
		return serviceID;
	}

	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}

	/*public Integer getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	public Integer getWorkingLocationID() {
		return workingLocationID;
	}

	public void setWorkingLocationID(Integer workingLocationID) {
		this.workingLocationID = workingLocationID;
	}*/

	public Integer getOldRoleId() {
		return oldRoleId;
	}

	public void setOldRoleId(Integer oldRoleId) {
		this.oldRoleId = oldRoleId;
	}

	public Integer getnRoleId() {
		return nRoleId;
	}

	public void setnRoleId(Integer nRoleId) {
		this.nRoleId = nRoleId;
	}

	public Integer[] getRoleID1() {
		return roleID1;
	}

	public void setRoleID1(Integer[] roleID1) {
		this.roleID1 = roleID1;
	}
	
	

	/**
	 * @return the canRead
	 */
	public Boolean[] getCanRead() {
		return canRead;
	}

	/**
	 * @param canRead the canRead to set
	 */
	public void setCanRead(Boolean[] canRead) {
		this.canRead = canRead;
	}

	/**
	 * @return the canWrite
	 */
	public Boolean[] getCanWrite() {
		return canWrite;
	}

	/**
	 * @param canWrite the canWrite to set
	 */
	public void setCanWrite(Boolean[] canWrite) {
		this.canWrite = canWrite;
	}

	/**
	 * @return the canSpeak
	 */
	public Boolean[] getCanSpeak() {
		return canSpeak;
	}

	/**
	 * @param canSpeak the canSpeak to set
	 */
	public void setCanSpeak(Boolean[] canSpeak) {
		this.canSpeak = canSpeak;
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

	

	public Integer[] getWeightage_Read() {
		return weightage_Read;
	}

	public void setWeightage_Read(Integer[] weightage_Read) {
		this.weightage_Read = weightage_Read;
	}

	public Integer[] getWeightage_Write() {
		return weightage_Write;
	}

	public void setWeightage_Write(Integer[] weightage_Write) {
		this.weightage_Write = weightage_Write;
	}

	public Integer[] getWeightage_Speak() {
		return weightage_Speak;
	}

	public void setWeightage_Speak(Integer[] weightage_Speak) {
		this.weightage_Speak = weightage_Speak;
	}

	public Integer getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
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

	public Boolean getInbound() {
		return inbound;
	}

	public void setInbound(Boolean inbound) {
		this.inbound = inbound;
	}

	public Boolean getOutbound() {
		return outbound;
	}

	public void setOutbound(Boolean outbound) {
		this.outbound = outbound;
	}
	
	
	
	

	
	
	
	}
