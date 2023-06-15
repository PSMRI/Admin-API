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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="showuserdetailsfromuserservicerolemapping")
//@NamedStoredProcedureQuery(name = "PR_FetchUserDetails", procedureName = "PR_FetchUserDetails",
//	                              parameters = {
//	                                 @StoredProcedureParameter(mode = ParameterMode.IN, name = "v_ServiceProviderID", type = Integer.class)
//	                              })
public class Showuserdetailsfromuserservicerolemapping {

	
	 @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="USRMappingID")
	   private Integer uSRMappingID; 
	   @Expose
	   @Column(name="ProviderServiceMapID")
	   private Integer providerServiceMapID;
	   @Expose
	   @Column(name="UserID")
	   private Integer userID ;
	   @Expose
	   @Column(name="RoleID")
	   private Integer roleID;
	   @Expose
	   @Column(name="RoleName")
	   private String roleName;
	   @Expose
	   @Column(name="TitleID")
	   private Integer titleID;
	   @Expose
	   @Column(name="TitleName")
	   private String titleName;
	   @Expose
	   @Column(name="FirstName")
	   private String firstName; 
	   @Expose
	   @Column(name="MiddleName")
	   private String middleName;
	   @Expose
	   @Column(name="LastName")
	   private String  lastName;
	   @Expose
	   @Column(name="GenderID")
	   private Integer genderID;
	   @Expose
	   @Column(name="GenderName")
	   private String genderName;
	   @Expose
	   @Column(name="MaritalStatusID")
	   private Integer maritalStatusID;
	   @Expose
	   @Column(name="MaritalStatus")
	   private String  maritalStatus;
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
	   @Column(name="Qualification")
	   private String qualification;
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
	   @Column(name="UserStatus")
	   private String userStatus;
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
	   @Column(name="UserDeleted")
	   private Boolean userDeleted; 
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
	   @Column(name="UserAddressLine1")
	   private String userAddressLine1;
	   @Expose
	   @Column(name="UserAddressLine2")
	   private String userAddressLine2;
	   @Column(name="PermAddressLine1")
	   private String permAddressLine1;
	   @Expose
	   @Column(name="PermAddressLine2")
	   private String permAddressLine2;
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
	   @Column(name="UserPremanentAddress")
	   private String userPremanentAddress; 
	   @Expose
	   @Column(name="UserCityID")
	   private Integer userCityID;
	   @Expose
	   @Column(name="UserCity")
	   private String userCity;
	   @Expose
	   @Column(name="UserStateID")
	   private Integer userStateID; 
	   @Expose
	   @Column(name="UserState")
	   private String userState;
	   @Expose
	   @Column(name="UserCountryID")
	   private Integer userCountryID;
	   @Expose
	   @Column(name="UserCountry")
	   private String userCountry; 
	   @Expose
	   @Column(name="PinCode")
	   private String pinCode;
	   @Expose
	   @Column(name="IsPresent")
	   private Boolean isPresent ;
	   @Expose
	   @Column(name="IsPermanent")
	   private Boolean isPermanent;
	   @Expose
	   @Column(name="ServiceProviderID")
	   private Integer serviceProviderID;
	   @Expose
	   @Column(name="ServiceProviderName")
	   private String serviceProviderName;
	   @Expose
	   @Column(name="ServiceID")
	   private Integer serviceID;
	   @Expose
	   @Column(name="ServiceName")
	   private String serviceName;
	   @Expose
	   @Column(name="PSMStateID")
	   private Integer pSMStateID;
	   @Expose
	   @Column(name="PSMStateName")
	   private String pSMStateName;
	   @Expose
	   @Column(name="WorkingLocationID")
	   private Integer workingLocationID;
	   @Expose
	   @Column(name="WorkingLocationName")
	   private String workingLocationName; 
	   @Expose
	   @Column(name="WorkingDistrictID")
	   private Integer workingDistrictID;
	   @Expose
	   @Column(name="WorkingDistrictName")
	   private String workingDistrictName;
	   @Expose
	   @Column(name="WorkingAddress")
	   private String workingAddress;
	   @Expose
	   @Column(name="USRMDeleted")
	   private Boolean uSRMDeleted;
	   
	   @Expose
	   @Column(name="Languages")
	   private String languages;
	   
	   
	   public Showuserdetailsfromuserservicerolemapping() {
		// TODO Auto-generated constructor stub
	}


	public Integer getuSRMappingID() {
		return uSRMappingID;
	}


	public void setuSRMappingID(Integer uSRMappingID) {
		this.uSRMappingID = uSRMappingID;
	}


	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}


	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
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


	public String getRoleName() {
		return roleName;
	}


	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	public Integer getTitleID() {
		return titleID;
	}


	public void setTitleID(Integer titleID) {
		this.titleID = titleID;
	}


	public String getTitleName() {
		return titleName;
	}


	public void setTitleName(String titleName) {
		this.titleName = titleName;
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


	public String getGenderName() {
		return genderName;
	}


	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}


	public Integer getMaritalStatusID() {
		return maritalStatusID;
	}


	public void setMaritalStatusID(Integer maritalStatusID) {
		this.maritalStatusID = maritalStatusID;
	}


	public String getMaritalStatus() {
		return maritalStatus;
	}


	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
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


	public String getQualification() {
		return qualification;
	}


	public void setQualification(String qualification) {
		this.qualification = qualification;
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


	public String getUserStatus() {
		return userStatus;
	}


	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
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


	public Boolean getUserDeleted() {
		return userDeleted;
	}


	public void setUserDeleted(Boolean userDeleted) {
		this.userDeleted = userDeleted;
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


	public String getUserAddressLine1() {
		return userAddressLine1;
	}


	public void setUserAddressLine1(String userAddressLine1) {
		this.userAddressLine1 = userAddressLine1;
	}


	public String getUserAddressLine2() {
		return userAddressLine2;
	}


	public void setUserAddressLine2(String userAddressLine2) {
		this.userAddressLine2 = userAddressLine2;
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


	public String getUserPremanentAddress() {
		return userPremanentAddress;
	}


	public void setUserPremanentAddress(String userPremanentAddress) {
		this.userPremanentAddress = userPremanentAddress;
	}


	public Integer getUserCityID() {
		return userCityID;
	}


	public void setUserCityID(Integer userCityID) {
		this.userCityID = userCityID;
	}


	public String getUserCity() {
		return userCity;
	}


	public void setUserCity(String userCity) {
		this.userCity = userCity;
	}


	public Integer getUserStateID() {
		return userStateID;
	}


	public void setUserStateID(Integer userStateID) {
		this.userStateID = userStateID;
	}


	public String getUserState() {
		return userState;
	}


	public void setUserState(String userState) {
		this.userState = userState;
	}


	public Integer getUserCountryID() {
		return userCountryID;
	}


	public void setUserCountryID(Integer userCountryID) {
		this.userCountryID = userCountryID;
	}


	public String getUserCountry() {
		return userCountry;
	}


	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}


	public String getPinCode() {
		return pinCode;
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


	public Integer getServiceProviderID() {
		return serviceProviderID;
	}


	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

     
	
	

	public String getServiceProviderName() {
		return serviceProviderName;
	}


	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}


	public Integer getServiceID() {
		return serviceID;
	}


	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}


	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public Integer getpSMStateID() {
		return pSMStateID;
	}


	public void setpSMStateID(Integer pSMStateID) {
		this.pSMStateID = pSMStateID;
	}


	public String getpSMStateName() {
		return pSMStateName;
	}


	public void setpSMStateName(String pSMStateName) {
		this.pSMStateName = pSMStateName;
	}


	public Integer getWorkingLocationID() {
		return workingLocationID;
	}


	public void setWorkingLocationID(Integer workingLocationID) {
		this.workingLocationID = workingLocationID;
	}


	public String getWorkingLocationName() {
		return workingLocationName;
	}


	public void setWorkingLocationName(String workingLocationName) {
		this.workingLocationName = workingLocationName;
	}


	public Integer getWorkingDistrictID() {
		return workingDistrictID;
	}


	public void setWorkingDistrictID(Integer workingDistrictID) {
		this.workingDistrictID = workingDistrictID;
	}


	public String getWorkingDistrictName() {
		return workingDistrictName;
	}


	public void setWorkingDistrictName(String workingDistrictName) {
		this.workingDistrictName = workingDistrictName;
	}


	public String getWorkingAddress() {
		return workingAddress;
	}


	public void setWorkingAddress(String workingAddress) {
		this.workingAddress = workingAddress;
	}


	public Boolean getuSRMDeleted() {
		return uSRMDeleted;
	}


	public void setuSRMDeleted(Boolean uSRMDeleted) {
		this.uSRMDeleted = uSRMDeleted;
	}
	
	
	
	
	
	public String getLanguages() {
		return languages;
	}


	public void setLanguages(String languages) {
		this.languages = languages;
	}



	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	   
	   

}
