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

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="m_Language")
public class M_Language {
	
	   @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="LanguageID")
	   private Integer languageID; 
	   @Expose
	   @Column(name="LanguageName")
	   private String languageName;
	   @Expose
	   @Column(name="LanguageDesc")
	   private String languageDesc;
	   @Expose
	   @Column(name="PropertyFilePath")
	   private String propertyFilePath;
	   @Expose
	   @Column(name="IVRFilePath")
	   private String iVRFilePath;
	   @Expose
	   @Column(name="Deleted",insertable = false, updatable = true)
	   private Boolean deleted; 
	   @Expose
	   @Column(name="CreatedBy")
	   private String createdBy;
	   @Expose
	   @Column(name="CreatedDate",insertable = false, updatable = false)
	   private Timestamp createdDate;
	   @Expose
	   @Column(name="ModifiedBy")
	   private String modifiedBy;
	   @Expose
	   @Column(name="LastModDate",insertable = false, updatable = false)
	   private Timestamp lastModDate;
	   
	   @OneToOne(mappedBy="m_Language")
		private M_UserLangMapping m_UserLangMapping;
	   
	   @OneToOne(mappedBy="m_Language1")
		private M_UserLangMapping m_UserLangMapping1;
	   
	   
	   
	   public M_Language() {
		// TODO Auto-generated constructor stub
	}



	public Integer getLanguageID() {
		return languageID;
	}



	public void setLanguageID(Integer languageID) {
		this.languageID = languageID;
	}



	public String getLanguageName() {
		return languageName;
	}



	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}



	public String getLanguageDesc() {
		return languageDesc;
	}



	public void setLanguageDesc(String languageDesc) {
		this.languageDesc = languageDesc;
	}



	public String getPropertyFilePath() {
		return propertyFilePath;
	}



	public void setPropertyFilePath(String propertyFilePath) {
		this.propertyFilePath = propertyFilePath;
	}



	public String getiVRFilePath() {
		return iVRFilePath;
	}



	public void setiVRFilePath(String iVRFilePath) {
		this.iVRFilePath = iVRFilePath;
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



	public M_UserLangMapping getM_UserLangMapping() {
		return m_UserLangMapping;
	}



	public void setM_UserLangMapping(M_UserLangMapping m_UserLangMapping) {
		this.m_UserLangMapping = m_UserLangMapping;
	}
	
	
	
	

	
	public M_UserLangMapping getM_UserLangMapping1() {
		return m_UserLangMapping1;
	}



	public void setM_UserLangMapping1(M_UserLangMapping m_UserLangMapping1) {
		this.m_UserLangMapping1 = m_UserLangMapping1;
	}






	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	   
	   
}
