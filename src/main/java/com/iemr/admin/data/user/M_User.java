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
package com.iemr.admin.data.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "M_User")
@JsonIgnoreProperties(ignoreUnknown = true)
public class M_User implements Serializable {
	
	@Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	   @Expose
	   @Column(name="UserID")
	private int userID;
	private Integer TitleID;
	private String FirstName;
	private String MiddleName;
	private String LastName;
	private Integer GenderID;
	private Integer MaritalStatusID;
	private String AadhaarNo;
	private String PAN;
	private Timestamp DOB;
	private Timestamp DOJ;
	private Integer QualificationID;
	private String UserName;
	private String Password;
	private String EmailID;
	private Integer StatusID;
	private String EmergencyContactPerson;
	private String EmergencyContactNo;
	private Boolean IsSupervisor;
	@Expose
	@Column(name="Deleted",insertable = false, updatable = false)
	private Boolean Deleted;
	private String CreatedBy;
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp CreatedDate;
	private String ModifiedBy;
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp LastModDate;

	@Expose
	@Transient
	private ArrayList<Map<String, Object>> providerAdminDetails;

	public M_User() {
	}

	public M_User(int userID, Integer titleID, String firstName, String middleName, String lastName, Integer genderID,
			Integer maritalStatusID, String aadhaarNo, String pAN, Timestamp dOB, Timestamp dOJ,
			Integer qualificationID, String userName, String password, String emailID, Integer statusID,
			String emergencyContactPerson, String emergencyContactNo, boolean isSupervisor, boolean deleted,
			String createdBy, Timestamp createdDate, String modifiedBy, Timestamp lastModDate,
			ArrayList<Map<String, Object>> providerAdminDetails) {
		super();
		userID = userID;
		TitleID = titleID;
		FirstName = firstName;
		MiddleName = middleName;
		LastName = lastName;
		GenderID = genderID;
		MaritalStatusID = maritalStatusID;
		AadhaarNo = aadhaarNo;
		PAN = pAN;
		DOB = dOB;
		DOJ = dOJ;
		QualificationID = qualificationID;
		UserName = userName;
		Password = password;
		EmailID = emailID;
		StatusID = statusID;
		EmergencyContactPerson = emergencyContactPerson;
		EmergencyContactNo = emergencyContactNo;
		IsSupervisor = isSupervisor;
		Deleted = deleted;
		CreatedBy = createdBy;
		CreatedDate = createdDate;
		ModifiedBy = modifiedBy;
		LastModDate = lastModDate;
		this.providerAdminDetails = providerAdminDetails;
	}



	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public Integer getTitleID() {
		return TitleID;
	}

	public void setTitleID(Integer titleID) {
		TitleID = titleID;
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public String getMiddleName() {
		return MiddleName;
	}

	public void setMiddleName(String middleName) {
		MiddleName = middleName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public Integer getGenderID() {
		return GenderID;
	}

	public void setGenderID(Integer genderID) {
		GenderID = genderID;
	}

	public Integer getMaritalStatusID() {
		return MaritalStatusID;
	}

	public void setMaritalStatusID(Integer maritalStatusID) {
		MaritalStatusID = maritalStatusID;
	}

	public String getAadhaarNo() {
		return AadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		AadhaarNo = aadhaarNo;
	}

	public String getPAN() {
		return PAN;
	}

	public void setPAN(String pAN) {
		PAN = pAN;
	}

	public Timestamp getDOB() {
		return DOB;
	}

	public void setDOB(Timestamp dOB) {
		DOB = dOB;
	}

	public Timestamp getDOJ() {
		return DOJ;
	}

	public void setDOJ(Timestamp dOJ) {
		DOJ = dOJ;
	}

	public Integer getQualificationID() {
		return QualificationID;
	}

	public void setQualificationID(Integer qualificationID) {
		QualificationID = qualificationID;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getEmailID() {
		return EmailID;
	}

	public void setEmailID(String emailID) {
		EmailID = emailID;
	}

	public Integer getStatusID() {
		return StatusID;
	}

	public void setStatusID(Integer statusID) {
		StatusID = statusID;
	}

	public String getEmergencyContactPerson() {
		return EmergencyContactPerson;
	}

	public void setEmergencyContactPerson(String emergencyContactPerson) {
		EmergencyContactPerson = emergencyContactPerson;
	}

	public String getEmergencyContactNo() {
		return EmergencyContactNo;
	}

	public void setEmergencyContactNo(String emergencyContactNo) {
		EmergencyContactNo = emergencyContactNo;
	}

	public Boolean isIsSupervisor() {
		return IsSupervisor;
	}

	public void setIsSupervisor(boolean isSupervisor) {
		IsSupervisor = isSupervisor;
	}

	public boolean isDeleted() {
		return Boolean.TRUE.equals(Deleted);
	}

	public void setDeleted(boolean deleted) {
		Deleted = deleted;
	}

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}

	public Timestamp getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		CreatedDate = createdDate;
	}

	public String getModifiedBy() {
		return ModifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		ModifiedBy = modifiedBy;
	}

	public Timestamp getLastModDate() {
		return LastModDate;
	}

	public void setLastModDate(Timestamp lastModDate) {
		LastModDate = lastModDate;
	}

	public ArrayList<Map<String, Object>> getProviderAdminDetails() {
		return providerAdminDetails;
	}

	public void setProviderAdminDetails(ArrayList<Map<String, Object>> providerAdminDetails) {
		this.providerAdminDetails = providerAdminDetails;
	}
	
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
