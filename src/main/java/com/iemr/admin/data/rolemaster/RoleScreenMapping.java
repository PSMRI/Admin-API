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

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name = "m_servicerolescreenmapping")
public class RoleScreenMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "SRSMappingID")
	private Integer sRSMappingID;
	@Expose
	@Column(name = "ScreenID")
	private Integer screenID;
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name = "RoleID")
	private Integer roleID;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	/*
	 * @Expose
	 * 
	 * @Column(name = "Processed") private String processed;
	 */
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

	/**
	 * @return the m_role
	 */
	public M_Role getM_role() {
		return m_role;
	}

	/**
	 * @param m_role
	 *            the m_role to set
	 */
	public void setM_role(M_Role m_role) {
		this.m_role = m_role;
	}

	/**
	 * @return the m_Screen
	 */
	public M_Screen getM_Screen() {
		return m_Screen;
	}

	/**
	 * @param m_Screen
	 *            the m_Screen to set
	 */
	public void setM_Screen(M_Screen m_Screen) {
		this.m_Screen = m_Screen;
	}
	
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RoleID", insertable = false, updatable = false)
	@Expose
	private M_Role m_role;

	

	//@OneToOne(mappedBy = "roleScreenMapping")
	//private M_Role m_role;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ScreenID", insertable = false, updatable = false)
	@Expose
	private M_Screen m_Screen;

	public RoleScreenMapping() {
		// TODO Auto-generated constructor stub
	}

	public Integer getsRSMappingID() {
		return sRSMappingID;
	}

	public void setsRSMappingID(Integer sRSMappingID) {
		this.sRSMappingID = sRSMappingID;
	}

	public Integer getScreenID() {
		return screenID;
	}

	public void setScreenID(Integer screenID) {
		this.screenID = screenID;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	public Integer getRoleID() {
		return roleID;
	}

	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	/*
	 * public String getProcessed() { return ; }
	 * 
	 * public void setProcessed(String processed) { this.processed = processed;
	 * }
	 */

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
