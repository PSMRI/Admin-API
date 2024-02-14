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
package com.iemr.admin.data.labmodule;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "m_iotprocedure")
public class IOTProcedure {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "IOTProcedureID")
	private Integer iotProcedureID;
	
	@Expose
	@Column(name = "ProcedureName")
	private String procedureName;
	
	@Expose
	@Column(name = "ProcedureCode")
	private String procedureCode;
	
	@Expose
	@Column(name = "ProcedureStartAPI")
	private String procedureStartAPI;
	
	@Expose
	@Column(name = "ProcedureEndAPI")
	private String procedureEndAPI;
	
	@Expose
	@Column(name = "ProcedureStatusAPI")
	private String procedureStatusAPI;
	@Expose
	@Column(name = "CalibrationStartAPI")
	private String calibrationStartAPI;
	@Expose
	@Column(name = "CalibrationStatusAPI")
	private String calibrationStatusAPI;
	@Expose
	@Column(name = "CalibrationEndAPI")
	private String calibrationEndAPI;
	@Expose
	@Column(name = "CalibrationCode")
	private String calibrationCode;
	@Expose
	@Column(name = "isLabProcedure")
	private Boolean isLabProcedure;
	
	@Expose
	@Column(name = "DiscoveryCode")
	private String discoveryCode;
	
	@Expose
	@Column(name = "Deleted")
	private Boolean deleted;
	
	@Expose
	@Column(name = "Processed")
	private String processed;
	
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	
	@Expose
	@Column(name = "CreatedDate")
	private Timestamp createdDate;
	
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	
	@Expose
	@Column(name = "LastModDate")
	private Timestamp lastModDate;
	
	private static OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	
	
}
