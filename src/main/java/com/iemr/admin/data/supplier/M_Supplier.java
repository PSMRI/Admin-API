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
package com.iemr.admin.data.supplier;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;

@Entity
@Table(name="m_supplier")
@Data
public class M_Supplier {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="SupplierID")
	private Integer supplierID;
	@Expose
	@Column(name="SupplierName")
	private String supplierName;
	@Expose
	@Column(name="SupplierDesc")
	private String supplierDesc;
	@Expose
	@Column(name="SupplierCode")
	private String supplierCode;
	@Expose
	@Column(name="Status")
	private String status; 
	@Expose
	@Column(name="ContactPerson")
	private String contactPerson;
	@Expose
	@Column(name="DrugLicenseNo")
	private String drugLicenseNo;
	@Expose
	@Column(name="CST_GST_No")
	private String cST_GST_No ;
	@Expose
	@Column(name="TIN_No")
	private String tIN_No;
	@Expose
	@Column(name="Email")
	private String email;
	@Expose
	@Column(name="PhoneNo1")
	private String phoneNo1; 
	@Expose
	@Column(name="PhoneNo2")
	private String phoneNo2; 
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name="AddressLine1")
	private String addressLine1;
	@Expose
	@Column(name="AddressLine2")
	private String addressLine2;
	@Expose
	@Column(name="DistrictID")
	private Integer districtID;
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
	@Column(name="Deleted", insertable = false, updatable = true)
	private Boolean deleted; 
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;
	@Expose
	@Column(name="CreatedDate", insertable = false, updatable = false)
	private Date createdDate;
	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name="LastModDate", insertable = false, updatable = false)
	private Date lastModDate;
	
	
	public M_Supplier() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the supplierID
	 */
	public Integer getSupplierID() {
		return supplierID;
	}


	/**
	 * @param supplierID the supplierID to set
	 */
	public void setSupplierID(Integer supplierID) {
		this.supplierID = supplierID;
	}


	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}


	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}


	/**
	 * @return the supplierDesc
	 */
	public String getSupplierDesc() {
		return supplierDesc;
	}


	/**
	 * @param supplierDesc the supplierDesc to set
	 */
	public void setSupplierDesc(String supplierDesc) {
		this.supplierDesc = supplierDesc;
	}


	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}


	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}


	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}


	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}


	/**
	 * @return the drugLicenseNo
	 */
	public String getDrugLicenseNo() {
		return drugLicenseNo;
	}


	/**
	 * @param drugLicenseNo the drugLicenseNo to set
	 */
	public void setDrugLicenseNo(String drugLicenseNo) {
		this.drugLicenseNo = drugLicenseNo;
	}


	/**
	 * @return the cST_GST_No
	 */
	public String getcST_GST_No() {
		return cST_GST_No;
	}


	/**
	 * @param cST_GST_No the cST_GST_No to set
	 */
	public void setcST_GST_No(String cST_GST_No) {
		this.cST_GST_No = cST_GST_No;
	}


	/**
	 * @return the tIN_No
	 */
	public String gettIN_No() {
		return tIN_No;
	}


	/**
	 * @param tIN_No the tIN_No to set
	 */
	public void settIN_No(String tIN_No) {
		this.tIN_No = tIN_No;
	}


	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * @return the phoneNo1
	 */
	public String getPhoneNo1() {
		return phoneNo1;
	}


	/**
	 * @param phoneNo1 the phoneNo1 to set
	 */
	public void setPhoneNo1(String phoneNo1) {
		this.phoneNo1 = phoneNo1;
	}


	/**
	 * @return the phoneNo2
	 */
	public String getPhoneNo2() {
		return phoneNo2;
	}


	/**
	 * @param phoneNo2 the phoneNo2 to set
	 */
	public void setPhoneNo2(String phoneNo2) {
		this.phoneNo2 = phoneNo2;
	}


	/**
	 * @return the providerServiceMapID
	 */
	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}


	/**
	 * @param providerServiceMapID the providerServiceMapID to set
	 */
	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
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
	public Date getCreatedDate() {
		return createdDate;
	}


	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


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
	public Date getLastModDate() {
		return lastModDate;
	}


	/**
	 * @param lastModDate the lastModDate to set
	 */
	public void setLastModDate(Date lastModDate) {
		this.lastModDate = lastModDate;
	}
	
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	

}
