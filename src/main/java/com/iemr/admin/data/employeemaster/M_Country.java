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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="m_Country")
public class M_Country {
	
	   @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="CountryID")
	   private Integer countryID;
	   @Expose
	   @Column(name="CountryName")
	   private String  countryName;
	   @Expose
	   @Column(name="CountryCode")
	   private Character countryCode;
	   @Expose
	   @Column(name="Continent")
	   private String continent;
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
	   
	   @OneToOne(mappedBy="mcountry")
		private M_UserDemographics m_UserDemographics;
	   
	   
	   public M_Country() {
		// TODO Auto-generated constructor stub
	}


	public Integer getCountryID() {
		return countryID;
	}


	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}


	public String getCountryName() {
		return countryName;
	}


	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}


	public Character getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(Character countryCode) {
		this.countryCode = countryCode;
	}


	public String getContinent() {
		return continent;
	}


	public void setContinent(String continent) {
		this.continent = continent;
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


	public M_UserDemographics getM_UserDemographics() {
		return m_UserDemographics;
	}


	public void setM_UserDemographics(M_UserDemographics m_UserDemographics) {
		this.m_UserDemographics = m_UserDemographics;
	}
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	
	   
	   

}
