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
@Table(name="m_title")
public class M_Title {
	   @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="TitleID")
	   private Integer titleID;
	   @Expose
	   @Column(name="TitleName")
	   private String titleName;
	   @Expose
	   @Column(name="TitleDesc")
	   private String titleDesc;
	   @Expose
	   @Column(name="Deleted",insertable = false, updatable = true)
	   private Boolean deleted;
	   @Expose
	   @Column(name="CreatedBy")
	   private String  createdBy;
	   @Expose
	   @Column(name="CreatedDate",insertable = false, updatable = false)
	   private Timestamp createdDate;
	   @Expose
	   @Column(name="ModifiedBy")
	   private String modifiedBy;
	   @Expose
	   @Column(name="LastModDate",insertable = false, updatable =false)
	   private Timestamp lastModDate;
	  /* 
	   @OneToOne(mappedBy="m_Title")
		private M_User1 m_User;*/
	   
	   
	   public M_Title() {
		// TODO Auto-generated constructor stub
	}
	   
	   
	   public M_Title(Integer titleID,String titleName){
		   this.titleID=titleID;
		   this.titleName=titleName;
		   
	   }


	public Integer getTitleID() {
		return titleID;
	}


	public void setTitleID(Integer titleID) {
		this.titleID = titleID;
	}


	/*public Character getTitleName() {
		return titleName;
	}


	public void setTitleName(Character titleName) {
		this.titleName = titleName;
	}*/
	
	


	public String getTitleDesc() {
		return titleDesc;
	}


	public String getTitleName() {
		return titleName;
	}


	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}


	public void setTitleDesc(String titleDesc) {
		this.titleDesc = titleDesc;
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


	

	/*public M_User1 getM_User() {
		return m_User;
	}


	public void setM_User(M_User1 m_User) {
		this.m_User = m_User;
	}*/
	
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
