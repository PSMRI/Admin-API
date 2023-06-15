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
@Table(name="m_feedbacknature")
public class M_Feedbacknature {
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "FeedbackNatureID")
	private Integer feedbackNatureID;
	@Expose
	@Column(name = "FeedbackNature")
	private String feedbackNature;
	@Expose
	@Column(name = "FeedbackNatureDesc")
	private String feedbackNatureDesc;
	@Expose
	@Column(name = "FeedbackTypeID")
	private Integer feedbackTypeID;
	@Expose
	@Column(name = "Deleted",insertable=false,updatable=true)
	private Boolean deleted ; 
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate",insertable=false,updatable=false)
	private Timestamp createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy; 
	@Expose
	@Column(name = "LastModDate",insertable=false,updatable=false)
	private Timestamp lastModDate; 
	@Expose
	@Column(name = "FeedbackTypeName")
	private String feedbackTypeName;
	
	
	
	public M_Feedbacknature() {
		// TODO Auto-generated constructor stub
	}



	/**
	 * @return the feedbackNatureID
	 */
	public Integer getFeedbackNatureID() {
		return feedbackNatureID;
	}



	/**
	 * @param feedbackNatureID the feedbackNatureID to set
	 */
	public void setFeedbackNatureID(Integer feedbackNatureID) {
		this.feedbackNatureID = feedbackNatureID;
	}



	/**
	 * @return the feedbackNature
	 */
	public String getFeedbackNature() {
		return feedbackNature;
	}



	/**
	 * @param feedbackNature the feedbackNature to set
	 */
	public void setFeedbackNature(String feedbackNature) {
		this.feedbackNature = feedbackNature;
	}



	/**
	 * @return the feedbackNatureDesc
	 */
	public String getFeedbackNatureDesc() {
		return feedbackNatureDesc;
	}



	/**
	 * @param feedbackNatureDesc the feedbackNatureDesc to set
	 */
	public void setFeedbackNatureDesc(String feedbackNatureDesc) {
		this.feedbackNatureDesc = feedbackNatureDesc;
	}



	/**
	 * @return the feedbackTypeID
	 */
	public Integer getFeedbackTypeID() {
		return feedbackTypeID;
	}



	/**
	 * @param feedbackTypeID the feedbackTypeID to set
	 */
	public void setFeedbackTypeID(Integer feedbackTypeID) {
		this.feedbackTypeID = feedbackTypeID;
	}



	/**
	 * @return the deleted
	 */
	public Boolean getDeleted() {
		return deleted;
	}



	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}



	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}



	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}



	/**
	 * @return the createdDate
	 */
	


	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}



	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}



	/**
	 * @return the lastModDate
	 */
	


	/**
	 * @return the feedbackTypeName
	 */
	public String getFeedbackTypeName() {
		return feedbackTypeName;
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
	 * @param feedbackTypeName the feedbackTypeName to set
	 */
	public void setFeedbackTypeName(String feedbackTypeName) {
		this.feedbackTypeName = feedbackTypeName;
	}
	
	
	
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
