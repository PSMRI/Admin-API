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
import java.util.ArrayList;
import java.util.Set;

import com.google.gson.annotations.Expose;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

/***
 * 
 * @author Rajeev Tripathi
 * @date 15-02-2018
 *
 */
@Entity
@Data
@Table(name = "m_procedure")
public class ProcedureMaster {
	@Id
	@GeneratedValue()
	@Expose
	@Column(name = "ProcedureID")
	private Integer procedureID;
	@Expose
	@Size(max = 100)
	@Column(name = "ProcedureName")
	private String procedureName;
	@Expose
	@Size(max = 100)
	@Column(name = "ProcedureDesc")
	private String procedureDesc;
	@Expose
	@Size(max = 30)
	@Column(name = "ProcedureType")
	private String procedureType;
	@Expose
	@Size(max = 10)
	@Column(name = "Gender")
	private String gender;
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name = "Deleted", insertable = false)
	private Boolean deleted;
	@Expose
	@Column(name = "Processed", insertable = false)
	private String processed;
	@Expose
	@Size(max = 50)
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "isMandatory")
	private Boolean isMandatory;
	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Size(max = 50)
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name = "LastModDate", insertable = false)
	private Timestamp lastModDate;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "procMaster")
	private Set<ProcedureComponentMapping> pcmSet;
	
	@Expose
	@Column(name = "IOTProcedureID")
	private Integer iotProcedureID;
	
	@Expose
	@Column(name = "IsCalibration")
	Boolean isCalibration;
	
	public ProcedureMaster() {
	}

	public Set<ProcedureComponentMapping> getPcmSet() {
		return pcmSet;
	}

	public void setPcmSet(Set<ProcedureComponentMapping> pcmSet) {
		this.pcmSet = pcmSet;
	}

	public ProcedureMaster(Integer procedureID, String procedureName, String procedureDesc, String procedureType,
			String gender, Integer providerServiceMapID, Boolean deleted, String processed, String createdBy,
			Timestamp createdDate, String modifiedBy, Timestamp lastModDate, Set<ProcedureComponentMapping> pcmSet) {
		super();
		this.procedureID = procedureID;
		this.procedureName = procedureName;
		this.procedureDesc = procedureDesc;
		this.procedureType = procedureType;
		this.gender = gender;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.processed = processed;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.lastModDate = lastModDate;
		this.pcmSet = pcmSet;
		//this.isMandatory=isMandatory;
	}

	public static ProcedureMaster getProcedureMaster(ArrayList<Object[]> obj) {
		if (obj != null && obj.size() > 0) {
			Object[] obj1 = obj.get(0);
			ProcedureMaster tmpOBJ = new ProcedureMaster((Integer) obj1[0], (String) obj1[1], (String) obj1[2],
					(String) obj1[3], (String) obj1[4], (Integer) obj1[5], (Boolean) obj1[6], (String) obj1[7],
					(String) obj1[8], (Timestamp) obj1[9], (String) obj1[10], (Boolean) obj1[11], (Timestamp) obj1[12]);
			return tmpOBJ;
		} else {
			return null;
		}
	}

	public static ArrayList<ProcedureMaster> getProcedureMasterDelFalse(ArrayList<Object[]> obj) {
		ArrayList<ProcedureMaster> pmList = new ArrayList<>();
		ProcedureMaster procedureMaster;
		for (Object[] obj1 : obj) {
			procedureMaster = new ProcedureMaster((Integer) obj1[0], (String) obj1[1], (String) obj1[2], (String) obj1[3]);
			pmList.add(procedureMaster);

		}
		return pmList;
	}

	public ProcedureMaster(Integer procedureID, String procedureName, String procedureDesc, String procedureType) {
		super();
		this.procedureID = procedureID;
		this.procedureName = procedureName;
		this.procedureDesc = procedureDesc;
		this.procedureType = procedureType;
	}

	public ProcedureMaster(Integer procedureID, String procedureName, String procedureDesc, String procedureType,
			String gender, Integer providerServiceMapID, Boolean deleted, String processed, String createdBy,
			Timestamp createdDate, String modifiedBy,Boolean isCalibration, Timestamp lastModDate) {
		super();
		this.procedureID = procedureID;
		this.procedureName = procedureName;
		this.procedureDesc = procedureDesc;
		this.procedureType = procedureType;
		this.gender = gender;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.processed = processed;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.isCalibration = isCalibration;
		this.lastModDate = lastModDate;
	}

	public ProcedureMaster(Integer procedureID, String procedureName, String procedureDesc, String procedureType,
			String gender, Boolean deleted, String createdBy) {
		super();
		this.procedureID = procedureID;
		this.procedureName = procedureName;
		this.procedureDesc = procedureDesc;
		this.procedureType = procedureType;
		this.gender = gender;
		this.deleted = deleted;
		this.createdBy = createdBy;
	}

	public ProcedureMaster(Integer procedureID, String procedureName, String procedureDesc, String procedureType,
			String gender, Boolean deleted, String createdBy,Boolean isMandatory,Boolean isCalibration,Integer iotProcedureID) {
		super();
		this.procedureID = procedureID;
		this.procedureName = procedureName;
		this.procedureDesc = procedureDesc;
		this.procedureType = procedureType;
		this.gender = gender;
		this.deleted = deleted;
		this.createdBy = createdBy;
		this.isMandatory=isMandatory;
		this.iotProcedureID=iotProcedureID;
		this.isCalibration= isCalibration;
	}

	public static ArrayList<ProcedureMaster> getProcedureMasterList(ArrayList<Object[]> obj) {
		ArrayList<ProcedureMaster> pmList = new ArrayList<>();
		ProcedureMaster procedureMaster;
		for (Object[] obj1 : obj) {
			procedureMaster = new ProcedureMaster((Integer) obj1[0], (String) obj1[1], (String) obj1[2],
					(String) obj1[3], (String) obj1[4], (Boolean) obj1[5], (String) obj1[6]);
			pmList.add(procedureMaster);

		}
		return pmList;
	}

	public Integer getProcedureID() {
		return procedureID;
	}

	public void setProcedureID(Integer procedureID) {
		this.procedureID = procedureID;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getProcedureDesc() {
		return procedureDesc;
	}

	public void setProcedureDesc(String procedureDesc) {
		this.procedureDesc = procedureDesc;
	}

	public String getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(String procedureType) {
		this.procedureType = procedureType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
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

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getLastModDate() {
		return lastModDate;
	}

	public void setLastModDate(Timestamp lastModDate) {
		this.lastModDate = lastModDate;
	}
	
	

}
