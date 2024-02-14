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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="m_UserLangMapping")
public class M_UserLangMapping {
	 
	
	   @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="UserLangID")
	   private Integer userLangID;
	   @Expose
	   @Column(name="UserID")
	   private Integer userID;
	   @Expose
	   @Column(name="LanguageID")
	   private Integer languageID;
	   @Expose
	   @Column(name="Weightage")
	   private Integer weightage;
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
	   @Column(name="LastModDate",insertable = false, updatable = false)
	   private Timestamp lastModDate;
	   
	  @Expose
	  @Column(name="CanRead") 
	  private Boolean canRead;
	  @Expose
	  @Column(name="CanWrite")
	  private Boolean canWrite;
	  @Expose
	  @Column(name="CanSpeak")
	  private Boolean canSpeak;
	  
	  
	  @Expose
	   @Column(name="Weightage_Read")
	   private Integer weightage_Read;
	   @Expose
	   @Column(name="Weightage_Write")
	   private Integer weightage_Write;
	   @Expose
	   @Column(name="Weightage_Speak")
	   private Integer weightage_Speak;
	   
	   
	   @Expose
	   @Column(name="ServiceProviderID")
	   private Integer serviceProviderID;
	   
	   
	   
	   
	   @OneToOne(mappedBy="m_UserLangMapping")
		private M_UserServiceRoleMapping2 m_UserServiceRoleMapping;
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "userID", insertable = false, updatable = false)
	   @Expose
		private M_Language m_Language;
	   
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "languageID", insertable = false, updatable = false)
	   @Expose
		private M_Language m_Language1;
	   
	   
	   @OneToOne(fetch = FetchType.LAZY)
	   @JoinColumn(name = "userID", insertable = false, updatable = false)
	   @Expose
		private M_User1 m_user;
	  
	   
	   public M_UserLangMapping() {
		// TODO Auto-generated constructor stub
	}
	   
	   @Expose
	   @Transient
	   private String languageName;
	   @Expose
	   @Transient
	   private String userName;
	   
	   @Expose
	   @Transient
	   private Boolean userDeleted;
	   //this.userDeleted=userDeleted;
	   
	   //userDeleted
	   
	   public M_UserLangMapping(Integer userlangID,Integer userID,Integer languageID,Integer weightage,String languageName,String userName,Boolean canRead,Boolean canWrite,Boolean canSpeak,String createdBy,Boolean deleted,
			   Integer weightage_Read,Integer weightage_Write,Integer weightage_Speak,Boolean userDeleted){
		   this.userLangID=userlangID;
		   this.userID=userID;
		   this.languageID=languageID;
		   this.weightage=weightage;
		   this.languageName=languageName;
		   this.userName=userName;
		   this.canRead=canRead;
		   this.canWrite=canWrite;
		   this.canSpeak=canSpeak;
		   this.createdBy=createdBy;
		   this.deleted=deleted;
		   this.weightage_Read=weightage_Read;
		   this.weightage_Write=weightage_Write;
		   this.weightage_Speak=weightage_Speak;
		   this.userDeleted=userDeleted;
		   
	   }


	public Integer getUserLangID() {
		return userLangID;
	}


	public void setUserLangID(Integer userLangID) {
		this.userLangID = userLangID;
	}


	public Integer getUserID() {
		return userID;
	}


	public void setUserID(Integer userID) {
		this.userID = userID;
	}


	

	

	public Integer getLanguageID() {
		return languageID;
	}


	public void setLanguageID(Integer languageID) {
		this.languageID = languageID;
	}


	public Integer getWeightage() {
		return weightage;
	}


	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
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


	
	


	public M_User1 getM_user() {
		return m_user;
	}


	public void setM_user(M_User1 m_user) {
		this.m_user = m_user;
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


	public M_UserServiceRoleMapping2 getM_UserServiceRoleMapping() {
		return m_UserServiceRoleMapping;
	}


	public void setM_UserServiceRoleMapping(M_UserServiceRoleMapping2 m_UserServiceRoleMapping) {
		this.m_UserServiceRoleMapping = m_UserServiceRoleMapping;
	}


	public M_Language getM_Language() {
		return m_Language;
	}


	public void setM_Language(M_Language m_Language) {
		this.m_Language = m_Language;
	}
	
	
	
	
	
	
	/**
	 * @return the canRead
	 */
	public Boolean getCanRead() {
		return canRead;
	}


	/**
	 * @param canRead the canRead to set
	 */
	public void setCanRead(Boolean canRead) {
		this.canRead = canRead;
	}


	/**
	 * @return the canWrite
	 */
	public Boolean getCanWrite() {
		return canWrite;
	}


	/**
	 * @param canWrite the canWrite to set
	 */
	public void setCanWrite(Boolean canWrite) {
		this.canWrite = canWrite;
	}


	/**
	 * @return the canSpeak
	 */
	public Boolean getCanSpeak() {
		return canSpeak;
	}


	/**
	 * @param canSpeak the canSpeak to set
	 */
	public void setCanSpeak(Boolean canSpeak) {
		this.canSpeak = canSpeak;
	}
	
	
	






	


	public Integer getWeightage_Read() {
		return weightage_Read;
	}


	public void setWeightage_Read(Integer weightage_Read) {
		this.weightage_Read = weightage_Read;
	}


	public Integer getWeightage_Write() {
		return weightage_Write;
	}


	public void setWeightage_Write(Integer weightage_Write) {
		this.weightage_Write = weightage_Write;
	}


	public Integer getWeightage_Speak() {
		return weightage_Speak;
	}


	public void setWeightage_Speak(Integer weightage_Speak) {
		this.weightage_Speak = weightage_Speak;
	}
	
	
	

	public Integer getServiceProviderID() {
		return serviceProviderID;
	}


	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}



	   
	   

}
