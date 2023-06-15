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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;
@Entity
@Data
@Table(name = "m_iotcomponent")
public class IOTComponent {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "IOTComponentId")
	private Integer iotComponentID;
	
	@Expose
	@Column(name = "ComponentName")
	private String componentName;
	
	@Expose
	@Column(name = "ComponentCode")
	private String componentCode;
	
	@Expose
	@Column(name = "IOTProcedureID")
	private Integer iotProcedureID;
	
	@Expose
	@Column(name = "ComponentUnit")
	private String componentUnit;
	
	@Expose
	@Column(name = "InputType")
	private String inputType;
	
	@Expose
	@Column(name = "IsDecimal")
	private Boolean isDecimal;
	
	@Expose
	@Column(name = "Options")
	@Convert(converter = StringListConverter.class)
	private List<String> options;
	
	
	
	
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
