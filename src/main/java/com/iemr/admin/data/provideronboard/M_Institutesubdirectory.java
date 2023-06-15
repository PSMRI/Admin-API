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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;


@Entity
@Table(name="m_institutesubdirectory")
public class M_Institutesubdirectory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "InstituteSubDirectoryID")
	private Integer instituteSubDirectoryID;
	@Expose
	@Column(name = "InstituteDirectoryID")
	private Integer instituteDirectoryID;
	@Expose
	@Column(name = "InstituteSubDirectoryName")
	private String instituteSubDirectoryName;
	@Expose
	@Column(name = "InstituteSubDirectoryDesc")
	private String instituteSubDirectoryDesc; 
	@Expose
	@Column(name = "Deleted",insertable=false,updatable=true)
	private Boolean deleted;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate",insertable=false,updatable=false)
	private Timestamp createdDate;
	@Expose
	@Column(name ="ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name = "LastModDate",insertable=false,updatable=false)
	private Timestamp lastModDate;
	
	
	@Expose
	@Column(name="ProviderServiceMapId")
	private Integer providerServiceMapId ;
	
	
	
	
	/*@OneToOne(mappedBy="m_Institutesubdirectory")
	private M_Institutedirectorymapping m_Institutedirectorymapping;*/
	
	
	public M_Institutesubdirectory() {
		// TODO Auto-generated constructor stub
	}


	public Integer getInstituteSubDirectoryID() {
		return instituteSubDirectoryID;
	}


	public void setInstituteSubDirectoryID(Integer instituteSubDirectoryID) {
		this.instituteSubDirectoryID = instituteSubDirectoryID;
	}


	public Integer getInstituteDirectoryID() {
		return instituteDirectoryID;
	}


	public void setInstituteDirectoryID(Integer instituteDirectoryID) {
		this.instituteDirectoryID = instituteDirectoryID;
	}


	public String getInstituteSubDirectoryName() {
		return instituteSubDirectoryName;
	}


	public void setInstituteSubDirectoryName(String instituteSubDirectoryName) {
		this.instituteSubDirectoryName = instituteSubDirectoryName;
	}


	public String getInstituteSubDirectoryDesc() {
		return instituteSubDirectoryDesc;
	}


	public void setInstituteSubDirectoryDesc(String instituteSubDirectoryDesc) {
		this.instituteSubDirectoryDesc = instituteSubDirectoryDesc;
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


	/**
	 * @return the providerServiceMapId
	 */
	public Integer getProviderServiceMapId() {
		return providerServiceMapId;
	}
	
	
	
	


	/**
	 * @return the m_Institutedirectorymapping
	 *//*
	public M_Institutedirectorymapping getM_Institutedirectorymapping() {
		return m_Institutedirectorymapping;
	}


	*//**
	 * @param m_Institutedirectorymapping the m_Institutedirectorymapping to set
	 *//*
	public void setM_Institutedirectorymapping(M_Institutedirectorymapping m_Institutedirectorymapping) {
		this.m_Institutedirectorymapping = m_Institutedirectorymapping;
	}
*/

	/**
	 * @param providerServiceMapId the providerServiceMapId to set
	 */
	public void setProviderServiceMapId(Integer providerServiceMapId) {
		this.providerServiceMapId = providerServiceMapId;
	}
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	
	
	      
	

}
