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
package com.iemr.admin.data.user;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name = "m_UserServiceRoleMapping")
public class M_UserServiceRoleMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer USRMappingID;
	private Integer UserID;
	private Integer RoleID;
	//private Integer ServiceID;
	//private Integer StateID;
	//private Integer ServiceProviderID;
	private Integer ProviderServiceMapID;
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean Deleted;
	private String CreatedBy;
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp CreatedDate;
	private String ModifiedBy;
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp LastModDate;

	public M_UserServiceRoleMapping() {
	}

	public M_UserServiceRoleMapping(Integer uSRMappingID, Integer userID, Integer roleID,
			Integer stateID, /*Integer serviceProviderID*/ Integer providerServiceMapID, Boolean deleted, String createdBy,
			Timestamp createdDate, String modifiedBy, Timestamp lastModDate) {
		super();
		USRMappingID = uSRMappingID;
		UserID = userID;
		RoleID = roleID;
		//ServiceID = serviceID;
		//StateID = stateID;
		//ServiceProviderID = serviceProviderID;
		ProviderServiceMapID = providerServiceMapID;
		Deleted = deleted;
		CreatedBy = createdBy;
		CreatedDate = createdDate;
		ModifiedBy = modifiedBy;
		LastModDate = lastModDate;
	}

	public Integer getUSRMappingID() {
		return USRMappingID;
	}

	public void setUSRMappingID(Integer uSRMappingID) {
		USRMappingID = uSRMappingID;
	}

	public Integer getUserID() {
		return UserID;
	}

	public void setUserID(Integer userID) {
		UserID = userID;
	}

	public Integer getRoleID() {
		return RoleID;
	}

	public void setRoleID(Integer roleID) {
		RoleID = roleID;
	}

	/*public Integer getServiceID() {
		return ServiceID;
	}

	public void setServiceID(Integer serviceID) {
		ServiceID = serviceID;
	}
*/
	/*public Integer getStateID() {
		return StateID;
	}

	public void setStateID(Integer stateID) {
		StateID = stateID;
	}
*/
	/*public Integer getServiceProviderID() {
		return ServiceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID) {
		ServiceProviderID = serviceProviderID;
	}
*/
	public Integer getProviderServiceMapID() {
		return ProviderServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		ProviderServiceMapID = providerServiceMapID;
	}

	public Boolean getDeleted() {
		return Deleted;
	}

	public void setDeleted(Boolean deleted) {
		Deleted = deleted;
	}

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}

	public Timestamp getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		CreatedDate = createdDate;
	}

	public String getModifiedBy() {
		return ModifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		ModifiedBy = modifiedBy;
	}

	public Timestamp getLastModDate() {
		return LastModDate;
	}

	public void setLastModDate(Timestamp lastModDate) {
		LastModDate = lastModDate;
	}
	
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	

}
