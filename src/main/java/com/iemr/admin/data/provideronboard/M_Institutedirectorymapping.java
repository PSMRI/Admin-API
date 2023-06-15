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
@Table(name="m_institutedirectorymapping")
public class M_Institutedirectorymapping {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "InstituteDirMapID")
	private Integer instituteDirMapID; 
	@Expose
	@Column(name = "InstitutionID")
	private Integer institutionID;
	@Expose
	@Column(name = "InstituteDirectoryID")
	private Integer instituteDirectoryID;
	@Expose
	@Column(name = "InstituteSubDirectoryID")
	private Integer instituteSubDirectoryID;
	@Expose
	@Column(name = "InstituteRouteDirectoryID")
	private Integer instituteRouteDirectoryID;
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
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
	private Timestamp lastModDate;
	
	
	@Transient
	@Expose
	private String institutionName;
	
	@Transient
	@Expose
	private String instituteDirectoryName;
	
	
	@Transient
	@Expose
	private String instituteSubDirectoryName;
	
	
	
	
	public M_Institutedirectorymapping() {
		// TODO Auto-generated constructor stub
	}
	
	public M_Institutedirectorymapping(Integer instituteDirMapID,Integer institutionID,Integer instituteDirectoryID,Integer instituteSubDirectoryID
			,Integer providerServiceMapID,Boolean deleted,String createdBy,
			String institutionName,String instituteDirectoryName,String instituteSubDirectoryName ){
		
		this.instituteDirMapID=instituteDirMapID;
		this.institutionID=institutionID;
		this.instituteDirectoryID=instituteDirectoryID;
		this.instituteSubDirectoryID=instituteSubDirectoryID;
		this.providerServiceMapID=providerServiceMapID;
		this.deleted=deleted;
		this.createdBy=createdBy;
		this.institutionName=institutionName;
		this.instituteDirectoryName=instituteDirectoryName;
		this.instituteSubDirectoryName=instituteSubDirectoryName;
		
	}
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "InstitutionID", insertable = false, updatable = false)
	@Expose
	private M_Institution m_Institution;
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "InstituteDirectoryID", insertable = false, updatable = false)
	@Expose
	private M_Institutedirectory m_Institutedirectory;
	
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "InstituteSubDirectoryID", insertable = false, updatable = false)
	@Expose
	private M_Institutesubdirectory m_Institutesubdirectory;

	
	

	public Integer getInstituteDirMapID() {
		return instituteDirMapID;
	}

	public void setInstituteDirMapID(Integer instituteDirMapID) {
		this.instituteDirMapID = instituteDirMapID;
	}

	public Integer getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(Integer institutionID) {
		this.institutionID = institutionID;
	}

	public Integer getInstituteDirectoryID() {
		return instituteDirectoryID;
	}

	public void setInstituteDirectoryID(Integer instituteDirectoryID) {
		this.instituteDirectoryID = instituteDirectoryID;
	}

	public Integer getInstituteSubDirectoryID() {
		return instituteSubDirectoryID;
	}

	public void setInstituteSubDirectoryID(Integer instituteSubDirectoryID) {
		this.instituteSubDirectoryID = instituteSubDirectoryID;
	}

	public Integer getInstituteRouteDirectoryID() {
		return instituteRouteDirectoryID;
	}

	public void setInstituteRouteDirectoryID(Integer instituteRouteDirectoryID) {
		this.instituteRouteDirectoryID = instituteRouteDirectoryID;
	}

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
