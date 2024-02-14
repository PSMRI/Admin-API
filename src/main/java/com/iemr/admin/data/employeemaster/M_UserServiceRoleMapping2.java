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
import java.util.List;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
@Data
@Entity
@Table(name = "m_UserServiceRoleMapping")
public class M_UserServiceRoleMapping2 {

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
	private String agentP;

	@Expose
	@Transient
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
	@Column(name = "Blockid")
	private Integer blockID;
	@Expose
	@Column(name = "BlockName")
	private String blockName;
	@Expose
	@Column(name = "Villageid")
	private String villageidDb;
	@Expose
	@Column(name = "VillageName")
	private String villageNameDb;
	@Transient
	private String[] villageID;
	@Transient
	private String[] villageName;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted=false;
	
	@Expose
	@Column(name = "isSanjeevani")
	private Boolean isSanjeevani;
	
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

	@Expose
	@Column(name = "ServiceProviderID")
	private Integer serviceProviderID;

	@OneToOne(/* fetch = FetchType.EAGER */)
	@JoinColumn(name = "userID", insertable = false, updatable = false)
	@Expose
	private M_User1 employeeMaster;

	/*
	 * @OneToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "StateID", insertable = false, updatable = false)
	 * 
	 * @Expose private State stateMaster;
	 */

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RoleID", insertable = false, updatable = false)
	@Expose
	private M_Role mRole;

	/*
	 * @OneToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "ServiceID", insertable = false, updatable = false)
	 * 
	 * @Expose private ServiceMaster serviceMaster;
	 */

	/*
	 * @OneToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "ServiceProviderID", insertable = false, updatable =
	 * false)
	 * 
	 * @Expose private M_ServiceProvider m_ServiceProvider;
	 */

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WorkingLocationID", insertable = false, updatable = false)
	@Expose
	private M_ProviderServiceAddMapping1 m_ProviderServiceAddMapping;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID", insertable = false, updatable = false)
	@Expose
	private M_UserDemographics m_UserDemographics;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID", insertable = false, updatable = false)
	@Expose
	private M_UserLangMapping m_UserLangMapping;

	@OneToMany(mappedBy = "usrMapping")
	@Transient
	@Expose
	private List<USRAgentMapping> usrAgentMappings;

	@Transient
	@Expose
	private String EmployeeName;

	@Transient
	@Expose
	private String WorkinglocationstateName;

	@Transient
	@Expose
	private String roleName;

	@Transient
	@Expose
	private String serviceName;

	@Transient
	@Expose
	private String serviceProviderName;

	@Transient
	@Expose
	private String locationName;

	@Transient
	@Expose
	private String address;

	@Transient
	@Expose
	private String fathersName;

	@Transient
	@Expose
	private String mothersName;

	@Transient
	@Expose
	private String adderssline1;

	@Transient
	@Expose
	private String adderssline2;

	@Transient
	@Expose
	private String adderssline3;

	@Transient
	@Expose
	private String adderssline4;

	@Transient
	@Expose
	private String adderssline5;

	@Transient
	@Expose
	private Integer demographicID;

	@Transient
	@Expose
	private String cityName;

	@Transient
	@Expose
	private Integer cityID;

	@Transient
	@Expose
	private Integer UstateID;

	@Transient
	@Expose
	private Integer countryID;

	@Transient
	@Expose
	private Integer religionID;

	@Transient
	@Expose
	private Integer communityID;

	@Transient
	@Expose
	private String stateName;

	@Transient
	@Expose
	private String countryName;

	@Transient
	@Expose
	private String religionType;

	@Transient
	@Expose
	private String communityType;

	@Transient
	@Expose
	private Integer userLangID;

	@Transient
	@Expose
	private Integer weightage;

	@Transient
	@Expose
	private Integer languageID;

	@Transient
	@Expose
	private String languageName;

	@Transient
	@Expose
	private Integer titleID;

	@Transient
	@Expose
	private Integer genderID;

	@Transient
	@Expose
	private Integer maritalStausID;

	@Transient
	@Expose
	private String aadharNo;

	@Transient
	@Expose
	private String pAN;

	@Transient
	@Expose
	private java.util.Date dOB;

	@Transient
	@Expose
	private java.util.Date dOJ;

	@Transient
	@Expose
	private Integer qualificationID;

	@Transient
	@Expose
	private String userName;

	@Transient
	@Expose
	private String emailID;

	@Transient
	@Expose
	private Integer statusID;

	@Transient
	@Expose
	private String emergencyContactPerson;

	@Transient
	@Expose
	private String emergencyContactNo;

	@Transient
	@Expose
	private Character titleName;

	@Transient
	@Expose
	private String titleDesc;

	@Transient
	@Expose
	private String status;

	@Transient
	@Expose
	private String statusDesc;

	@Transient
	@Expose
	private String statusFor;

	@Transient
	@Expose
	private String qualificationName;

	@Transient
	@Expose
	private String userQualificationDesc;

	@Transient
	@Expose
	private Character genderName;

	@Transient
	@Expose
	private String maritalStatus;

	@Transient
	@Expose
	private String maritalstatusDesc;

	@Transient
	@Expose
	private String oldAgentID;

	@Transient
	@Expose
	private Boolean isAvailable;

	@Transient
	@Expose
	private Integer usrAgentMappingID;

	@Transient
	@Expose
	private Integer usrMappingID;

	@Expose
	@Column(name = "isInbound")
	private Boolean inbound;

	@Expose
	@Column(name = "isOutbound")
	private Boolean outbound;

	public M_UserServiceRoleMapping2() {
	}

	public M_UserServiceRoleMapping2(Integer userID, Integer roleID, Integer serviceID, Integer worklocationStateID,
			String EmployeeName, String WorkinglocationstateName, String roleName, String serviceName,
			Integer serviceProviderID, String serviceProviderName, Integer workingLocationID, String locationName,
			String address
	/* String fathersName,String cityName */) {
		this.userID = userID;
		this.roleID = roleID;
		// this.serviceID=serviceID;
		// this.stateID=worklocationStateID;
		this.EmployeeName = EmployeeName;
		this.roleName = roleName;
		this.WorkinglocationstateName = WorkinglocationstateName;
		this.serviceName = serviceName;
		this.serviceProviderName = serviceProviderName;
		this.locationName = locationName;
		this.address = address;
		this.workingLocationID = workingLocationID;
		this.fathersName = fathersName;
		this.cityName = cityName;
	}

	public M_UserServiceRoleMapping2(Integer userID, Integer roleID, Integer serviceID, Integer worklocationStateID,
			String EmployeeName, String WorkinglocationstateName, String roleName, String serviceName,
			Integer serviceProviderID, String serviceProviderName, Integer workingLocationID, String locationName,
			String address, String fathersName, String mothersName, String adderssline1, String adderssline2,
			String adderssline3, String adderssline4, String adderssline5, Integer demographicID, Integer cityID,
			Integer UstateID, Integer countryID, Integer religionID, Integer communityID, String cityName,
			String stateName, String countryName, String religionType, String communityType, Integer userLangID,
			Integer weightage, Integer languageID, String languageName, Integer titleID, Integer genderID,
			Integer maritalStatusID, String aadharNo, String pAN, java.util.Date dOB, java.util.Date dOJ,
			Integer qualificationID, String userName, String agentID, String emailID, Integer statusID,
			String emergencyContactPerson, String emergencyContactNo, Character titleName, String titleDesc,
			String status, String statusDesc, String statusFor, String qualificationName, String userQualificationDesc,
			Character genderName, String maritalStatus, String maritalstatusDesc) {
		this.userID = userID;
		this.roleID = roleID;
		// this.serviceID=serviceID;
		// this.stateID=worklocationStateID;
		this.EmployeeName = EmployeeName;
		this.WorkinglocationstateName = WorkinglocationstateName;
		this.roleName = roleName;
		this.serviceName = serviceName;
		// this.serviceProviderID=serviceProviderID;
		this.serviceProviderName = serviceProviderName;
		this.workingLocationID = workingLocationID;
		this.locationName = locationName;
		this.address = address;
		this.fathersName = fathersName;
		this.mothersName = mothersName;
		this.adderssline1 = adderssline1;
		this.adderssline2 = adderssline2;
		this.adderssline3 = adderssline3;
		this.adderssline4 = adderssline4;
		this.adderssline5 = adderssline5;
		this.demographicID = demographicID;
		this.cityID = cityID;
		this.UstateID = UstateID;
		this.countryID = countryID;
		this.religionID = religionID;
		this.communityID = communityID;
		this.cityName = cityName;
		this.stateName = stateName;
		this.countryName = countryName;
		this.religionType = religionType;
		this.communityType = communityType;
		this.userLangID = userLangID;
		this.weightage = weightage;
		this.languageID = languageID;
		this.languageName = languageName;
		this.titleID = titleID;
		this.genderID = genderID;
		this.maritalStausID = maritalStatusID;
		this.aadharNo = aadharNo;
		this.pAN = pAN;
		this.dOB = dOB;
		this.dOJ = dOJ;
		this.qualificationID = qualificationID;
		this.userName = userName;
		this.agentID = agentID;
		this.emailID = emailID;
		this.statusID = statusID;
		this.emergencyContactPerson = emergencyContactPerson;
		this.emergencyContactNo = emergencyContactNo;
		this.titleName = titleName;
		this.titleDesc = titleDesc;
		this.status = status;
		this.statusDesc = statusDesc;
		this.statusFor = statusFor;
		this.qualificationName = qualificationName;
		this.userQualificationDesc = userQualificationDesc;
		this.genderName = genderName;
		this.maritalStatus = maritalStatus;
		this.maritalstatusDesc = maritalstatusDesc;

	}

	public Integer getWorkingLocationID() {
		return workingLocationID;
	}

	public void setWorkingLocationID(Integer workingLocationID) {
		this.workingLocationID = workingLocationID;
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

	public Integer getUsrMappingID() {
		return usrMappingID;
	}

	public void setUsrMappingID(Integer uSRMappingID) {
		this.usrMappingID = uSRMappingID;
	}
	// public Integer getServiceID() {
	// return serviceID;
	// }
	//
	//
	// public void setServiceID(Integer serviceID) {
	// this.serviceID = serviceID;
	// }
	//
	//
	// public Integer getStateID() {
	// return stateID;
	// }
	//
	//
	// public void setStateID(Integer stateID) {
	// this.stateID = stateID;
	// }
	//
	//
	// public Integer getServiceProviderID() {
	// return serviceProviderID;
	// }
	//
	//
	// public void setServiceProviderID(Integer serviceProviderID) {
	// this.serviceProviderID = serviceProviderID;
	// }

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

	public M_User1 getEmployeeMaster() {
		return employeeMaster;
	}

	public void setEmployeeMaster(M_User1 employeeMaster) {
		this.employeeMaster = employeeMaster;
	}

	/*
	 * public State getStateMaster() { return stateMaster; }
	 * 
	 * 
	 * public void setStateMaster(State stateMaster) { this.stateMaster =
	 * stateMaster; }
	 */

	public M_Role getmRole() {
		return mRole;
	}

	public void setmRole(M_Role mRole) {
		this.mRole = mRole;
	}

	/*
	 * public ServiceMaster getServiceMaster() { return serviceMaster; }
	 * 
	 * 
	 * public void setServiceMaster(ServiceMaster serviceMaster) {
	 * this.serviceMaster = serviceMaster; }
	 */

	/*
	 * public M_ServiceProvider getM_ServiceProvider() { return m_ServiceProvider; }
	 * 
	 * public void setM_ServiceProvider(M_ServiceProvider m_ServiceProvider) {
	 * this.m_ServiceProvider = m_ServiceProvider; }
	 */

	public M_ProviderServiceAddMapping1 getM_ProviderServiceAddMapping() {
		return m_ProviderServiceAddMapping;
	}

	public void setM_ProviderServiceAddMapping(M_ProviderServiceAddMapping1 m_ProviderServiceAddMapping) {
		this.m_ProviderServiceAddMapping = m_ProviderServiceAddMapping;
	}

	public String getEmployeeName() {
		return EmployeeName;
	}

	public void setEmployeeName(String employeeName) {
		EmployeeName = employeeName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public M_UserDemographics getM_UserDemographics() {
		return m_UserDemographics;
	}

	public void setM_UserDemographics(M_UserDemographics m_UserDemographics) {
		this.m_UserDemographics = m_UserDemographics;
	}

	public M_UserLangMapping getM_UserLangMapping() {
		return m_UserLangMapping;
	}

	public void setM_UserLangMapping(M_UserLangMapping m_UserLangMapping) {
		this.m_UserLangMapping = m_UserLangMapping;
	}

	/**
	 * @return the agentID
	 */
	public String getAgentID() {
		return agentID;
	}

	/**
	 * @param agentID the agentID to set
	 */
	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

//	/**
//	 * @return the agentPassword
//	 */
//	public String getAgentPassword() {
//		return agentP;
//	}
//
//	/**
//	 * @param agentPassword the agentPassword to set
//	 */
//	public void setAgentPassword(String agentPassword) {
//		this.agentP = agentPassword;
//	}

	/**
	 * @return the cZRole
	 */
	public String getcZRole() {
		return cZRole;
	}

	public String getAgentP() {
		return agentP;
	}

	public void setAgentP(String agentP) {
		this.agentP = agentP;
	}

	public String getAgentPassword() {
		return agentPassword;
	}

	public void setAgentPassword(String agentPassword) {
		this.agentPassword = agentPassword;
	}

	/**
	 * @param cZRole the cZRole to set
	 */
	public void setcZRole(String cZRole) {
		this.cZRole = cZRole;
	}

	public Integer getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
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

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
