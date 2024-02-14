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
package com.iemr.admin.data.telemedicine;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.data.employeemaster.M_Designation;
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
@Table(name = "M_User")
public class M_UserTemp {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "UserID")
	private Long userID;
	private Integer TitleID;
	@Expose
	private String FirstName;
	@Expose
	private String MiddleName;
	@Expose
	private String LastName;
	private Integer GenderID;
	private Integer MaritalStatusID;
	private String AadhaarNo;
	private String PAN;
	private Timestamp DOB;
	private Timestamp DOJ;
	private Integer QualificationID;
	@Expose
	@Column(name = "UserName")
	private String UserName;
	@Expose
	private String EmailID;
	private Integer StatusID;
	private String EmergencyContactPerson;
	private String EmergencyContactNo;
	private Boolean IsSupervisor;
	@Expose
	@Column(name = "DesignationID")
	private Integer designationID;
	@Expose
	@Column(name = "ServiceProviderID")
	private Integer ServiceProviderID;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = false)
	private Boolean Deleted;
	private String CreatedBy;
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp CreatedDate;
	private String ModifiedBy;
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp LastModDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID", insertable = false, updatable = false)
	@Expose
	private UserVideoConsultation userVideoConsultation;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DesignationID", insertable = false, updatable = false)
	@Expose
	private M_Designation designation;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Transient
	@Expose
	private Long userVideoConsultationMapID;

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

	public M_UserTemp() {

	}

	public M_UserTemp(M_UserTemp user, Long userVideoConsultationMapID) {
		this.userID = user.userID;
		this.FirstName = user.FirstName;
		this.LastName = user.LastName;
		this.UserName = user.UserName;
		this.EmailID = user.EmailID;
		this.designationID = user.designationID;
		this.ServiceProviderID = user.ServiceProviderID;
		this.Deleted = user.Deleted;
		this.userVideoConsultationMapID = userVideoConsultationMapID;
	}
}
