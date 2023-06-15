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
@Table(name = "m_calltype")
public class M_Calltype {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "CallTypeID")
	private Integer callTypeID;
	@Expose
	@Column(name = "CallGroupType")
	private String callGroupType;
	@Expose
	@Column(name = "CallType")
	private String callType;
	@Expose
	@Column(name = "CallTypeDesc")
	private String callTypeDesc;
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name = "FitToBlock")
	private Boolean fitToBlock;
	@Expose
	@Column(name = "FitForFollowup")
	private Boolean fitForFollowup;
	@Expose
	@Column(name = "IsInbound")
	private Boolean isInbound;
	@Expose
	@Column(name = "IsOutbound")
	private Boolean isOutbound;
	@Expose
	@Column(name = "MaxRedial")
	private Integer maxRedial;
	@Expose
	@Column(name = "Deleted" ,insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name = "Processed", insertable = false, updatable = false)
	private String processed;
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

	public M_Calltype() {
		// TODO Auto-generated constructor stub
	}

	public Integer getCallTypeID() {
		return callTypeID;
	}

	public void setCallTypeID(Integer callTypeID) {
		this.callTypeID = callTypeID;
	}

	public String getCallGroupType() {
		return callGroupType;
	}

	public void setCallGroupType(String callGroupType) {
		this.callGroupType = callGroupType;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getCallTypeDesc() {
		return callTypeDesc;
	}

	public void setCallTypeDesc(String callTypeDesc) {
		this.callTypeDesc = callTypeDesc;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	public Boolean getFitToBlock() {
		return fitToBlock;
	}

	public void setFitToBlock(Boolean fitToBlock) {
		this.fitToBlock = fitToBlock;
	}

	public Boolean getFitForFollowup() {
		return fitForFollowup;
	}

	public void setFitForFollowup(Boolean fitForFollowup) {
		this.fitForFollowup = fitForFollowup;
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

	public Integer getMaxRedial() {
		return maxRedial;
	}

	public void setMaxRedial(Integer maxRedial) {
		this.maxRedial = maxRedial;
	}

	public Boolean getIsInbound() {
		return isInbound;
	}

	public void setIsInbound(Boolean isInbound) {
		this.isInbound = isInbound;
	}

	public Boolean getIsOutbound() {
		return isOutbound;
	}

	public void setIsOutbound(Boolean isOutbound) {
		this.isOutbound = isOutbound;
	}

	public OutputMapper getOutputMapper() {
		return outputMapper;
	}

	public void setOutputMapper(OutputMapper outputMapper) {
		this.outputMapper = outputMapper;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
