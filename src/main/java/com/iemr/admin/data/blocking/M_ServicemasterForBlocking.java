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
package com.iemr.admin.data.blocking;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;



@Entity
@Table(name="m_servicemaster")
public class M_ServicemasterForBlocking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ServiceID")
	private Integer serviceID; 
	@Expose
	@Column(name="ServiceName")
	private String serviceName;
	@Expose
	@Column(name="ServiceDesc")
	private String serviceDesc;
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer croviderServiceMapID;
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;
	@Expose
	@Column(name="Deleted",insertable = false, updatable = true)
	private Boolean deleted; 
	@Expose
	@Column(name="Processed",insertable = false, updatable = true)
	private String processed; 
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
	@Column(name="IsNational")
	private Boolean isNational;
	
	@OneToOne(mappedBy="m_ServicemasterForBlocking")
	private M_Providerservicemapping_Blocking m_Providerservicemapping_Blocking;
   
	
	
	
	
	public M_ServicemasterForBlocking() {
		// TODO Auto-generated constructor stub
	}



	public Integer getServiceID() {
		return serviceID;
	}



	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}



	public String getServiceName() {
		return serviceName;
	}



	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}



	public String getServiceDesc() {
		return serviceDesc;
	}



	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}



	public Integer getCroviderServiceMapID() {
		return croviderServiceMapID;
	}



	public void setCroviderServiceMapID(Integer croviderServiceMapID) {
		this.croviderServiceMapID = croviderServiceMapID;
	}



	public String getCreatedBy() {
		return createdBy;
	}



	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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



	public M_Providerservicemapping_Blocking getM_Providerservicemapping_Blocking() {
		return m_Providerservicemapping_Blocking;
	}



	public void setM_Providerservicemapping_Blocking(M_Providerservicemapping_Blocking m_Providerservicemapping_Blocking) {
		this.m_Providerservicemapping_Blocking = m_Providerservicemapping_Blocking;
	}
	
	
	
	
	
	public Boolean getIsNational() {
		return isNational;
	}



	public void setIsNational(Boolean isNational) {
		this.isNational = isNational;
	}





	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

}
