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
@Table(name="m_UserDemographics")
public class M_UserDemographics {
	@Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   @Expose
	   @Column(name="DemographicID")
	   private Integer demographicID;
	   @Expose
	   @Column(name="UserID")
	   private  Integer userID; 
	   @Expose
	   @Column(name="FathersName")
	   private String fathersName;
	   @Expose
	   @Column(name="MothersName")
	   private String  mothersName;
	   @Expose
	   @Column(name="CommunityID")
	   private Integer communityID; 
	   @Expose
	   @Column(name="ReligionID")
	   private Integer religionID;
	   @Expose
	   @Column(name="AddressLine1")
	   private String addressLine1;
	   @Expose
	   @Column(name="AddressLine2")
	   private String addressLine2;
	   @Expose
	   @Column(name="PermAddressLine1")
	   private String permAddressLine1;
	   @Expose
	   @Column(name="PermAddressLine2")
	   private String permAddressLine2;
	   @Expose
	   @Column(name="PermanentAddress")
	   private String permanentAddress;
	   
	   
	   @Expose
	   @Column(name="PermStateID")
	   private Integer permStateID;
	   
	   @Expose
	   @Column(name="PermDistrictID")
	   private Integer permDistrictID;
	   @Expose
	   @Column(name="PermPinCode")
	   private Integer permPinCode;
	   @Expose
	   @Column(name="CityID")
	   private Integer cityID;
	   @Expose
	   @Column(name="StateID")
	   private Integer stateID;
	   @Expose
	   @Column(name="CountryID")
	   private Integer countryID;
	   @Expose
	   @Column(name="PinCode")
	   private String pinCode;
	   @Expose
	   @Column(name="IsPresent")
	   private Boolean isPresent;
	   @Expose
	   @Column(name="IsPermanent")
	   private Boolean isPermanent;
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
	   
	   @Expose
	   @Column(name="DistrictID")
	   private Integer districtID;
	   
	   
//	    @OneToOne(mappedBy="m_UserDemographics")
//		private M_UserServiceRoleMapping2 m_UserServiceRoleMapping;
	    
	    
	       @OneToOne(fetch = FetchType.LAZY)
		   @JoinColumn(name = "cityID", insertable = false, updatable = false)
		   @Expose
			private M_City m_City;
	   
	       
	       @OneToOne(fetch = FetchType.LAZY)
		   @JoinColumn(name = "stateID", insertable = false, updatable = false)
		   @Expose
			private State mstate;
	       
	       
	       @OneToOne(fetch = FetchType.LAZY)
		   @JoinColumn(name = "countryID", insertable = false, updatable = false)
		   @Expose
			private M_Country mcountry;
	       
	       @OneToOne(fetch = FetchType.LAZY)
		   @JoinColumn(name = "religionID", insertable = false, updatable = false)
		   @Expose
			private M_Religion m_Religion;
	       
	       @OneToOne(fetch = FetchType.LAZY)
		   @JoinColumn(name = "communityID", insertable = false, updatable = false)
		   @Expose
			private M_Community m_Community;
	       
	   public M_UserDemographics() {
		// TODO Auto-generated constructor stub
	}

	public Integer getDemographicID() {
		return demographicID;
	}

	public void setDemographicID(Integer demographicID) {
		this.demographicID = demographicID;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public String getMothersName() {
		return mothersName;
	}

	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}

	public Integer getCommunityID() {
		return communityID;
	}

	public void setCommunityID(Integer communityID) {
		this.communityID = communityID;
	}

	public Integer getReligionID() {
		return religionID;
	}

	public void setReligionID(Integer religionID) {
		this.religionID = religionID;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	

	public String getPermAddressLine1() {
		return permAddressLine1;
	}

	public void setPermAddressLine1(String permAddressLine1) {
		this.permAddressLine1 = permAddressLine1;
	}

	public String getPermAddressLine2() {
		return permAddressLine2;
	}

	public void setPermAddressLine2(String permAddressLine2) {
		this.permAddressLine2 = permAddressLine2;
	}

	public Integer getPermStateID() {
		return permStateID;
	}

	public void setPermStateID(Integer permStateID) {
		this.permStateID = permStateID;
	}

	public Integer getPermDistrictID() {
		return permDistrictID;
	}

	public void setPermDistrictID(Integer permDistrictID) {
		this.permDistrictID = permDistrictID;
	}

	public Integer getPermPinCode() {
		return permPinCode;
	}

	public void setPermPinCode(Integer permPinCode) {
		this.permPinCode = permPinCode;
	}

	public Integer getCityID() {
		return cityID;
	}

	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}

	public Integer getStateID() {
		return stateID;
	}

	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}

	public Integer getCountryID() {
		return countryID;
	}

	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public Boolean getIsPresent() {
		return isPresent;
	}

	public void setIsPresent(Boolean isPresent) {
		this.isPresent = isPresent;
	}

	public Boolean getIsPermanent() {
		return isPermanent;
	}

	public void setIsPermanent(Boolean isPermanent) {
		this.isPermanent = isPermanent;
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

//	public M_UserServiceRoleMapping2 getM_UserServiceRoleMapping() {
//		return m_UserServiceRoleMapping;
//	}
//
//	public void setM_UserServiceRoleMapping(M_UserServiceRoleMapping2 m_UserServiceRoleMapping) {
//		this.m_UserServiceRoleMapping = m_UserServiceRoleMapping;
//	}

	public M_City getM_City() {
		return m_City;
	}

	public void setM_City(M_City m_City) {
		this.m_City = m_City;
	}
	
	
	
	
	
	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}
  
	
	




	public Integer getDistrictID() {
		return districtID;
	}

	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}







	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

	
}
