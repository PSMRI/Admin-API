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
package com.iemr.admin.data.emailconfig;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.data.employeemaster.M_Designation;
import com.iemr.admin.data.employeemaster.M_ProviderServiceMap1;
import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.StateMaster;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity(name = "m_AuthorityEmail")
public class AuthorityEmail
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "AuthorityEmailID")
	private Integer authorityEmailID;
	// @Column(name = "InstitutionTypeID", insertable = true, updatable = false)
	// private Integer institutionTypeID;
	// @JoinColumn(name = "institutionTypeID", insertable=false, updatable=false)
	// @OneToOne(fetch = FetchType.EAGER)
	// private M_Institutiontype instituteType;
	@Column(name = "StateID", insertable = true, updatable = false)
	private Integer stateID;
	@JoinColumn(name = "stateID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.EAGER)
	private StateMaster state;
	@Column(name = "DistrictID", insertable = true, updatable = false)
	private Integer districtID;
	@JoinColumn(name = "districtID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.EAGER)
	private M_District district;
	@Column(name = "BlockID", insertable = true, updatable = false)
	private Integer blockID;
	@JoinColumn(name = "blockID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.EAGER)
	private DistrictBlock districtBlock;
	@Column(name = "DistrictBranchMappingID", insertable = true, updatable = false)
	private Integer districtBranchMappingID;
	@JoinColumn(name = "districtBranchMappingID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.EAGER)
	private DistrictBranchMapping districtBranch;

	@Column(name = "DesignationID", insertable = true, updatable = true)
	private Integer designationID;
	@JoinColumn(name = "designationID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.EAGER)
	private M_Designation designation;
	@Column(name = "AuthorityName", insertable = true, updatable = true)
	private String authorityName;
	@Column(name = "EmailID", insertable = true, updatable = true)
	private String emailID;
	@Column(name = "ContactNo", insertable = true, updatable = true)
	private String contactNo;
	@Column(name = "ProviderServiceMapID", insertable = true, updatable = false)
	private Integer providerServiceMapID;
	@JoinColumn(name = "providerServiceMapID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.EAGER)
	private M_ProviderServiceMap1 providerService;
	@Column(name = "mobileno", insertable = true, updatable = true)
	private String mobileNo;
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Column(name = "Processed", insertable = false, updatable = false)
	private String processed;
	@Column(name = "CreatedBy", insertable = true, updatable = false)
	private String createdBy;
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;
	@Column(name = "ModifiedBy", insertable = false, updatable = true)
	private String modifiedBy;
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;
	

	@Override
	public String toString()
	{
		String output = OutputMapper.gson().toJson(this);
		return output;
	}
}
