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



import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name="v_userservicerolemapping")
public class V_Userservicerolemapping {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="uSRMappingID")
	private Integer uSRMappingID;
	
	@Expose
	@Column(name="UserID")
	private Integer userID;
	@Expose
    @Column(name="Name")
	private String name;
	@Expose
	@Column(name="UserName")
	private String userName; 
	@Expose
	@Column(name="ServiceID")
	private Integer serviceID;
	@Expose
	@Column(name="ServiceName")
	private String serviceName;
	@Expose
	@Column(name="StateID")
	private Integer stateID;
	@Expose
	@Column(name="StateName")
	private String  stateName;
	@Expose
	@Column(name="WorkingDistrictID")
	private String workingDistrictID;
	@Expose
	@Column(name="WorkingDistrictName")
	private String workingDistrictName;
	@Expose
	@Column(name="WorkingLocationID")
	private String workingLocationID;
	@Expose
	@Column(name="LocationName")
	private String locationName;
	@Expose
	@Column(name="WorkingLocationAddress")
	private String workingLocationAddress;
	@Expose
	@Column(name="RoleID")
	private Integer roleID;
	@Expose
	@Column(name="RoleName")
	private String roleName;
	@Expose
	@Column(name="AgentID")
	private String agentID;
	@Expose
	@Column(name="PSMStatusID")
	private Integer pSMStatusID;
	@Expose
	@Column(name="PSMStatus")
	private String pSMStatus;
	@Expose
	@Column(name="UserServciceRoleDeleted")
	private Boolean userServciceRoleDeleted;
	@Expose
	@Column(name="UserDeleted")
	private Boolean userDeleted;
	@Expose
	@Column(name="ServiceProviderDeleted")
	private Boolean serviceProviderDeleted;
	@Expose
	@Column(name="RoleDeleted")
	private Boolean roleDeleted;
	@Expose
	@Column(name="ProviderServiceMappingDeleted")
	private Boolean providerServiceMappingDeleted;
	

	
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	
    @Expose
	@Column(name="ServiceProviderID")
	private Integer serviceProviderID;
    
    @Expose
	@Column(name="isInbound")
	private Boolean inbound;
    
    @Expose
	@Column(name="isOutbound")
	private Boolean outbound;
	   
    @Expose
	@Column(name = "blockid")
	private Integer blockID;
	@Expose
	@Column(name = "blockname")
	private String blockName;
	@Expose
	@Column(name = "villageid")
	private String villageidDb;
	@Expose
	@Column(name = "villagename")
	private String villageNameDb;
	@Transient
	private String[] villageID;
	@Transient
	private String[] villageName;   
	
    @Expose
	@Column(name="isSanjeevani")
	private Boolean isSanjeevani;
	   
	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	public V_Userservicerolemapping() {
		// TODO Auto-generated constructor stub
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getWorkingDistrictID() {
		return workingDistrictID;
	}

	public void setWorkingDistrictID(String workingDistrictID) {
		this.workingDistrictID = workingDistrictID;
	}

	public String getWorkingDistrictName() {
		return workingDistrictName;
	}

	public void setWorkingDistrictName(String workingDistrictName) {
		this.workingDistrictName = workingDistrictName;
	}

	public String getWorkingLocationID() {
		return workingLocationID;
	}

	public void setWorkingLocationID(String workingLocationID) {
		this.workingLocationID = workingLocationID;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getWorkingLocationAddress() {
		return workingLocationAddress;
	}

	public void setWorkingLocationAddress(String workingLocationAddress) {
		this.workingLocationAddress = workingLocationAddress;
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

	public String getAgentID() {
		return agentID;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public Integer getpSMStatusID() {
		return pSMStatusID;
	}

	public void setpSMStatusID(Integer pSMStatusID) {
		this.pSMStatusID = pSMStatusID;
	}

	public String getpSMStatus() {
		return pSMStatus;
	}

	public void setpSMStatus(String pSMStatus) {
		this.pSMStatus = pSMStatus;
	}

	public Boolean getUserServciceRoleDeleted() {
		return userServciceRoleDeleted;
	}

	public void setUserServciceRoleDeleted(Boolean userServciceRoleDeleted) {
		this.userServciceRoleDeleted = userServciceRoleDeleted;
	}

	public Boolean getUserDeleted() {
		return userDeleted;
	}

	public void setUserDeleted(Boolean userDeleted) {
		this.userDeleted = userDeleted;
	}

	public Boolean getServiceProviderDeleted() {
		return serviceProviderDeleted;
	}

	public void setServiceProviderDeleted(Boolean serviceProviderDeleted) {
		this.serviceProviderDeleted = serviceProviderDeleted;
	}

	public Boolean getRoleDeleted() {
		return roleDeleted;
	}

	public void setRoleDeleted(Boolean roleDeleted) {
		this.roleDeleted = roleDeleted;
	}

	public Boolean getProviderServiceMappingDeleted() {
		return providerServiceMappingDeleted;
	}

	public void setProviderServiceMappingDeleted(Boolean providerServiceMappingDeleted) {
		this.providerServiceMappingDeleted = providerServiceMappingDeleted;
	}
	
	
	
	
	
	

	public Integer getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
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
