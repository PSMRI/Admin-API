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
@Table(name="m_institution")
public class M_Institution {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "InstitutionID")
	private Integer institutionID;
	@Expose
	@Column(name = "GovtInstituteID")
	private String govtInstituteID;
	@Expose
	@Column(name = "InstitutionName")
	private String institutionName;
	@Expose
	@Column(name = "InstituteTypeId")
	private Integer instituteTypeId;
	@Expose
	@Column(name = "StateID")
	private Integer stateID;
	@Expose
	@Column(name = "DistrictID")
	private Integer districtID;
	@Expose
	@Column(name = "BlockID")
	private Integer blockID;
	@Expose
	@Column(name = "villageid")
	private Integer villageID;
	@Expose
	@Column(name = "DistrictBranchMappingID")
	private Integer districtBranchMappingID;
	@Expose
	@Column(name = "Address")
	private String address; 
	@Expose
	@Column(name = "ContactNo1")
	private String contactNo1;
	@Expose
	@Column(name = "ContactNo2")
	private String contactNo2;
	@Expose
	@Column(name = "ContactNo3")
	private String contactNo3;
	@Expose
	@Column(name = "Deleted",insertable=false,updatable=true)
	private Boolean deleted;
	@Expose
	@Column(name = "Processed",insertable=false,updatable=false)
	private String processed;
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
	@Column(name ="LastModDate",insertable=false,updatable=false)
	private Timestamp lastModDate;
	
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	
	
	@Expose
	@Column(name="ContactPerson1")
	private String contactPerson1;
	@Expose
	@Column(name="ContactPerson1_Email")
	private String contactPerson1_Email;
	@Expose
	@Column(name="ContactPerson2")
	private String contactPerson2;
	@Expose
	@Column(name="ContactPerson2_Email")
	private String contactPerson2_Email;
	@Expose
	@Column(name="ContactPerson3")
	private String contactPerson3;
	@Expose
	@Column(name="ContactPerson3_Email")
	private String contactPerson3_Email;
	@Expose
	@Column(name="Website")
	private String  website;
	
	
	
	/*@OneToOne(mappedBy="m_Institution")
	private M_Institutedirectorymapping m_Institutedirectorymapping;*/
	
	
	public M_Institution() {
		// TODO Auto-generated constructor stub
	}

	public Integer getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(Integer institutionID) {
		this.institutionID = institutionID;
	}

	public String getGovtInstituteID() {
		return govtInstituteID;
	}

	public void setGovtInstituteID(String govtInstituteID) {
		this.govtInstituteID = govtInstituteID;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public Integer getInstituteTypeId() {
		return instituteTypeId;
	}

	public void setInstituteTypeId(Integer instituteTypeId) {
		this.instituteTypeId = instituteTypeId;
	}

	public Integer getStateID() {
		return stateID;
	}

	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}

	public Integer getDistrictID() {
		return districtID;
	}

	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}

	public Integer getBlockID() {
		return blockID;
	}

	public void setBlockID(Integer blockID) {
		this.blockID = blockID;
	}

	public Integer getVillageID() {
		return villageID;
	}

	public void setVillageID(Integer villageID) {
		this.villageID = villageID;
	}

	public Integer getDistrictBranchMappingID() {
		return districtBranchMappingID;
	}

	public void setDistrictBranchMappingID(Integer districtBranchMappingID) {
		this.districtBranchMappingID = districtBranchMappingID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNo1() {
		return contactNo1;
	}

	public void setContactNo1(String contactNo1) {
		this.contactNo1 = contactNo1;
	}

	public String getContactNo2() {
		return contactNo2;
	}

	public void setContactNo2(String contactNo2) {
		this.contactNo2 = contactNo2;
	}

	public String getContactNo3() {
		return contactNo3;
	}

	public void setContactNo3(String contactNo3) {
		this.contactNo3 = contactNo3;
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

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	/**
	 * @return the contactPerson1
	 */
	public String getContactPerson1() {
		return contactPerson1;
	}

	/**
	 * @param contactPerson1 the contactPerson1 to set
	 */
	public void setContactPerson1(String contactPerson1) {
		this.contactPerson1 = contactPerson1;
	}

	/**
	 * @return the contactPerson1_Email
	 */
	public String getContactPerson1_Email() {
		return contactPerson1_Email;
	}

	/**
	 * @param contactPerson1_Email the contactPerson1_Email to set
	 */
	public void setContactPerson1_Email(String contactPerson1_Email) {
		this.contactPerson1_Email = contactPerson1_Email;
	}

	/**
	 * @return the contactPerson2
	 */
	public String getContactPerson2() {
		return contactPerson2;
	}

	/**
	 * @param contactPerson2 the contactPerson2 to set
	 */
	public void setContactPerson2(String contactPerson2) {
		this.contactPerson2 = contactPerson2;
	}

	/**
	 * @return the contactPerson2_Email
	 */
	public String getContactPerson2_Email() {
		return contactPerson2_Email;
	}

	/**
	 * @param contactPerson2_Email the contactPerson2_Email to set
	 */
	public void setContactPerson2_Email(String contactPerson2_Email) {
		this.contactPerson2_Email = contactPerson2_Email;
	}

	/**
	 * @return the contactPerson3
	 */
	public String getContactPerson3() {
		return contactPerson3;
	}

	/**
	 * @param contactPerson3 the contactPerson3 to set
	 */
	public void setContactPerson3(String contactPerson3) {
		this.contactPerson3 = contactPerson3;
	}

	/**
	 * @return the contactPerson3_Email
	 */
	public String getContactPerson3_Email() {
		return contactPerson3_Email;
	}

	/**
	 * @param contactPerson3_Email the contactPerson3_Email to set
	 */
	public void setContactPerson3_Email(String contactPerson3_Email) {
		this.contactPerson3_Email = contactPerson3_Email;
	}

	/**
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * @param website the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}
	
	
	
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

}
