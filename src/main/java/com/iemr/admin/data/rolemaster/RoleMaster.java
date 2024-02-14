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
package com.iemr.admin.data.rolemaster;

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
@Table(name = "m_role")
public class RoleMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "RoleID")
	private Integer roleID;
	@Expose
	@Column(name = "RoleName")
	private String roleName;
	@Expose
	@Column(name = "RoleDesc")
	private String roleDesc;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
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
	private Timestamp LastModDate;

	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "RoleID", insertable = false, updatable = false)
	// @Expose
	// private RoleScreenMapping roleScreenMapping;

	/*@Expose
	@OneToOne(mappedBy = "m_role")
	private RoleScreenMapping roleScreenMapping;
	*/
	@Transient
	@Expose
	private String screenName;
	
	@Transient
	@Expose
	private Integer sRSMappingID;

	@Expose
	@Column(name = "RoleFor")
	private Integer roleFor;
	
	@Expose
	@Column(name = "isWrapUpTime")
	private Boolean isWrapUpTime;
	
	@Expose
	@Column(name = "WrapUpTime")
	private Integer WrapUpTime;

	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;

	public RoleMaster() {
		// TODO Auto-generated constructor stub
	}

	public RoleMaster(Integer roleID, String roleName, String roleDesc, Boolean deleted, String screenName,Integer sRSMappingID ) {
		this.sRSMappingID=sRSMappingID;
		this.roleID = roleID;
		this.roleName = roleName;
		this.roleDesc = roleDesc;
		this.screenName = screenName;
		this.deleted = deleted;
	}

	/**
	 * @return the roleScreenMapping
	 */
	/*public RoleScreenMapping getRoleScreenMapping() {
		return roleScreenMapping;
	}

	*//**
	 * @param roleScreenMapping
	 *            the roleScreenMapping to set
	 *//*
	public void setRoleScreenMapping(RoleScreenMapping roleScreenMapping) {
		this.roleScreenMapping = roleScreenMapping;
	}
*/
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

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
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

	

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getLastModDate() {
		return LastModDate;
	}

	public void setLastModDate(Timestamp lastModDate) {
		LastModDate = lastModDate;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public Integer getRoleFor() {
		return roleFor;
	}

	public void setRoleFor(Integer roleFor) {
		this.roleFor = roleFor;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	
	
	
	public Boolean getIsWrapUpTime() {
		return isWrapUpTime;
	}

	public void setIsWrapUpTime(Boolean isWrapUpTime) {
		this.isWrapUpTime = isWrapUpTime;
	}

	public Integer getWrapUpTime() {
		return WrapUpTime;
	}

	public void setWrapUpTime(Integer wrapUpTime) {
		WrapUpTime = wrapUpTime;
	}




	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
