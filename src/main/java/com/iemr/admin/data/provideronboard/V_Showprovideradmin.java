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
package com.iemr.admin.data.provideronboard;



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
@Table(name="v_showprovideradmin")
public class V_Showprovideradmin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "USRMappingID")
	private Integer uSRMappingID;
	@Expose
	@Column(name = "UserID")
	private Integer userID;
	@Expose
	@Column(name = "TitleID")
	private Integer titleID;
	@Expose
	@Column(name = "TitleName")
	private String titleName;
	@Expose
	@Column(name = "FirstName")
	private String firstName; 
	@Expose
	@Column(name = "MiddleName")
	private String middleName;
	@Expose
	@Column(name = "LastName")
	private String lastName; 
	@Expose
	@Column(name = "IsSupervisor")
	private Boolean isSupervisor;
	@Expose
	@Column(name = "UserDeleted")
	private Boolean userDeleted; 
	@Expose
	@Column(name = "RoleID")
	private Integer roleID; 
	@Expose
	@Column(name = "RoleName")
	private String roleName;
	@Expose
	@Column(name = "RoleDeleted")
	private Boolean roleDeleted; 
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
	@Column(name = "ProviderServiceMapDeleted")
	private Boolean providerServiceMapDeleted;
	@Expose
	@Column(name = "WorkingLocationID")
	private Integer workingLocationID;
	@Expose
	@Column(name = "LocationName")
	private String locationName; 
	@Expose
	@Column(name = "WorkingLocationDeleted")
	private Boolean workingLocationDeleted;
	@Expose
	@Column(name = "USRMDeleted")
	private Boolean uSRMDeleted; 
	@Expose
	@Column(name = "ServiceProviderID")
	private Integer serviceProviderID;
	@Expose
	@Column(name = "ServiceProviderName")
	private String serviceProviderName;
	@Expose
	@Column(name = "ServiceProviderDeleted")
	private Boolean serviceProviderDeleted;
	@Expose
	@Column(name = "ServiceID")
	private Integer serviceID; 
	@Expose
	@Column(name = "ServiceName")
	private String serviceName; 
	@Expose
	@Column(name = "ServiceDeleted")
	private Boolean serviceDeleted;
	@Expose
	@Column(name = "StateID")
	private Integer stateID ; 
	@Expose
	@Column(name = "StateName")
	private String stateName;
	
	
	

	
	    public V_Showprovideradmin() {
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





		public Boolean getRoleDeleted() {
			return roleDeleted;
		}





		public void setRoleDeleted(Boolean roleDeleted) {
			this.roleDeleted = roleDeleted;
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





		public Boolean getProviderServiceMapDeleted() {
			return providerServiceMapDeleted;
		}





		public void setProviderServiceMapDeleted(Boolean providerServiceMapDeleted) {
			this.providerServiceMapDeleted = providerServiceMapDeleted;
		}





		public Integer getWorkingLocationID() {
			return workingLocationID;
		}





		public void setWorkingLocationID(Integer workingLocationID) {
			this.workingLocationID = workingLocationID;
		}





		public String getLocationName() {
			return locationName;
		}





		public void setLocationName(String locationName) {
			this.locationName = locationName;
		}





		public Boolean getWorkingLocationDeleted() {
			return workingLocationDeleted;
		}





		public void setWorkingLocationDeleted(Boolean workingLocationDeleted) {
			this.workingLocationDeleted = workingLocationDeleted;
		}





		public Boolean getuSRMDeleted() {
			return uSRMDeleted;
		}





		public void setuSRMDeleted(Boolean uSRMDeleted) {
			this.uSRMDeleted = uSRMDeleted;
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





		public Boolean getServiceProviderDeleted() {
			return serviceProviderDeleted;
		}





		public void setServiceProviderDeleted(Boolean serviceProviderDeleted) {
			this.serviceProviderDeleted = serviceProviderDeleted;
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





		public Boolean getServiceDeleted() {
			return serviceDeleted;
		}





		public void setServiceDeleted(Boolean serviceDeleted) {
			this.serviceDeleted = serviceDeleted;
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
	    
	    
	    
	    
		@Transient
		private OutputMapper outputMapper = new OutputMapper();

		@Override
		public String toString() {
			return outputMapper.gson().toJson(this);
		}




}
