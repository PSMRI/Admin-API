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
import lombok.Data;

@Data
@Entity
@Table(name="m_category")
public class M_Category {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "CategoryID")
	private Integer categoryID;
	
	@Expose
	@Transient
	private Integer oldCategoryID ;
	
	@Expose
	@Column(name = "CategoryName")
	private String categoryName; 
	@Expose
	@Column(name = "CategoryDesc")
	private String categoryDesc;
	@Expose
	@Column(name = "SubServiceID")
	private Integer subServiceID;
	
	
	
	
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name = "Deleted",insertable = false, updatable = true)
	private Boolean deleted;
	
	@Expose
	@Column(name = "104_CS_Type")
	private Boolean s104_CS_Type;
	
	
	@Expose
	@Column(name="FeedbackNatureID")
	private Integer feedbackNatureID;
	
	
	@Expose
	@Transient
	private String feedbackNature;
	
//	private String Processed; 
//	private String CreatedBy; 
//	private Date CreatedDate; 
//	private String ModifiedBy; 
//	private Date LastModDate;
//
	@Expose
	@Column(name = "Processed",insertable = false, updatable = false)
	private String processed;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate" ,insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy; 
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;
	
	
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "feedbackNatureID")
	private M_Feedbacknature m_Feedbacknature;
	
	
	public M_Category() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public M_Category(Integer categoryID,String categoryName,Integer feedbackNatureID,String feedbackNature) {
		
		this.categoryID=categoryID;
		this.categoryName=categoryName;
		this.feedbackNatureID=feedbackNatureID;
		this.feedbackNature=feedbackNature;
	}
	
	public M_Category(Integer categoryID,String categoryName,Integer feedbackNatureID,String feedbackNature,Integer providerServiceMapID) {
		
		this.categoryID=categoryID;
		this.categoryName=categoryName;
		this.feedbackNatureID=feedbackNatureID;
		this.feedbackNature=feedbackNature;
		this.providerServiceMapID=providerServiceMapID;;
	}
	

	public Integer getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(Integer categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public Integer getSubServiceID() {
		return subServiceID;
	}

	public void setSubServiceID(Integer subServiceID) {
		this.subServiceID = subServiceID;
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

	public String getProcessed() {
		return processed;
	}

	public void setProcessed(String processed) {
		this.processed = processed;
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





	public OutputMapper getOutputMapper() {
		return outputMapper;
	}

	public void setOutputMapper(OutputMapper outputMapper) {
		this.outputMapper = outputMapper;
	}
	
	





	/**
	 * @return the s104_CS_Type
	 */
	public Boolean getS104_CS_Type() {
		return s104_CS_Type;
	}





	/**
	 * @param s104_CS_Type the s104_CS_Type to set
	 */
	public void setS104_CS_Type(Boolean s104_CS_Type) {
		this.s104_CS_Type = s104_CS_Type;
	}

	
	






	public Integer getFeedbackNatureID() {
		return feedbackNatureID;
	}





	public void setFeedbackNatureID(Integer feedbackNatureID) {
		this.feedbackNatureID = feedbackNatureID;
	}









	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}


}
