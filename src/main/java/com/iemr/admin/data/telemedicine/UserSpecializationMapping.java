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

import java.sql.Date;

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

import lombok.Data;

@Entity
@Table(name = "m_userspecializationmapping")
@Data
public class UserSpecializationMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "UserSpecializationMapID")
	private Integer userSpecializationMapID;
	@Expose
	@Column(name = "UserID")
	private Integer userID;

	@Expose
	@Transient
	private String userName;
	
	@Expose
	@Transient
	private Boolean userDeleted;

	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID", insertable = false, updatable = false)
	private M_UserTemp user;

	@Expose
	@Column(name = "SpecializationID")
	private Integer specializationID;
	
	@Expose
	@Transient
	private Boolean specializationDeleted;

	@Expose
	@Transient
	private String specializationName;

	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SpecializationID", insertable = false, updatable = false)
	private Specialization specialization;

	@Expose
	@Column(name = "SlotTime_Minute")
	private Integer slotTime_Minute;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Date createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Date lastModDate;

	public UserSpecializationMapping() {

	}

	public UserSpecializationMapping(Integer userSpecializationMapID, Integer userID, String userName,
			Integer specializationID, String specializationName,Boolean deleted,Boolean userDeleted,Boolean specializationDeleted) {
		this.userSpecializationMapID=userSpecializationMapID;
		this.userID=userID;
		this.userName=userName;
		this.specializationID=specializationID;
		this.specializationName=specializationName;
		this.deleted=deleted;
		this.userDeleted=userDeleted;
		this.specializationDeleted=specializationDeleted;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
