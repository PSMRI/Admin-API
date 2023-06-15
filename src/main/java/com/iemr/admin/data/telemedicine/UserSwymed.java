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

@Data
@Entity
@Table(name = "m_userswymedmapping")
public class UserSwymed {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "UserSwymedMapID")
	private Long userSwymedMapID;

	@Expose
	@Column(name = "userID")
	private Long userID;
	@Expose
	@Column(name = "SwymedID")
	private Long swymedID;
	@Expose
	@Column(name = "SwymedPassword")
	private String swymedPassword;
	
	@Expose
	@Column(name = "SwymedEmailID")
	private String swymedEmailID;
	
	@Expose
	@Column(name = "SwymedDomain")
	private String swymedDomain;

	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean Deleted;
	@Expose
	private String CreatedBy;
	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp CreatedDate;
	@Expose
	private String ModifiedBy;
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp LastModDate;

	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID", insertable = false, updatable = false)
	@Expose
	private M_UserTemp user;
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	
	@Expose
	@Transient
	private String  userName;
	
	@Expose
	@Transient
	private String designationName;
	
	@Expose
	@Transient
	private Integer designationID;
	
	@Expose
	@Transient
	private Boolean userDeleted;
	
	
	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	public UserSwymed(){
		
	}
	
	
	public UserSwymed(UserSwymed us,String userName,String designationName,Boolean userDeleted,Integer designationID){
		this.userSwymedMapID=us.userSwymedMapID;
		 this.userID=us.userID;
		 this.swymedID=us.swymedID;
		 this.swymedPassword=us.swymedPassword;
		 this.swymedEmailID=us.swymedEmailID;
		 this.swymedDomain=us.swymedDomain;
		 this.Deleted=us.Deleted;
		 this.CreatedBy=us.CreatedBy;
		 this.CreatedDate=us.CreatedDate;
		 this.ModifiedBy=us.ModifiedBy;
		 this.LastModDate=us.LastModDate;
		 this.userName=userName;
		 this.designationName=designationName;
		 this.userDeleted=userDeleted;
		 this.designationID=designationID;
	}

}
