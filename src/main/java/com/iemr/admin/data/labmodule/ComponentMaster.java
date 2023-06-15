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
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.iemr.admin.data.loinc.Loinc;


import lombok.Data;

/***
 * 
 * @author Rajeev Tripathi
 *
 */
@Data
@Entity
@Table(name = "m_testcomponent")
@JsonIgnoreProperties("compOpt")
public class ComponentMaster {
	@Id
	@GeneratedValue
	@Expose
	@Column(name = "TestComponentID", insertable = false)
	private Integer testComponentID;
	@Expose
	@Column(name = "TestComponentName")
	private String testComponentName;
	@Expose
	@Column(name = "TestComponentDesc")
	private String testComponentDesc;
	@Expose
	@Column(name = "InputType")
	private String inputType;
	@Expose
	@Column(name = "MeasurementUnit")
	private String measurementUnit;
	@Expose
	@Column(name = "isDecimal")
	private Boolean isDecimal;
	@Expose
	@Column(name = "Range_min")
	private Double range_min;
	@Expose
	@Column(name = "Range_normal_min")
	private Double range_normal_min;
	@Expose
	@Column(name = "Range_normal_max")
	private Double range_normal_max;
	@Expose
	@Column(name = "Range_max")
	private Double range_max;
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
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name = "LastModDate", insertable = false)
	private Timestamp lastModDate;
	
	@Expose
	@Column(name = "IOTComponentID")
	private Integer iotComponentID;
	@Expose
	@Column(name = "Loinc_Num")
	private String lionicNum;
	@Expose
	@Column(name = "loinc_Component")
	private String lionicTerm;
	@Transient
	@JsonIgnore
	private ArrayList<Map<String, Object>> compOpt;
	@Expose
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "componentMaster")
	private Set<ComponentResultMap> componentResultMap;
	@Expose
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "compMaster")
	private Set<ProcedureComponentMapping> pcmSet;
//	@Expose
//	@ManyToOne
//	@JoinColumn(name = "lionicNum", insertable = false)
//	private Loinc loinc;
	@Transient
	String component;
//	@Expose
//	@Column(name = "isMandatory")
//	private Boolean isMandatory;
	public ComponentMaster() {
	}

	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc,String lionicNum,
			String inputType, String measurementUnit, Double range_min, Double range_normal_min,
			Double range_normal_max, Double range_max, Integer providerServiceMapID, Boolean deleted,
			String processed, String createdBy, Timestamp createdDate, String modifiedBy, Timestamp lastModDate,
			ArrayList<Map<String, Object>> compOpt, Set<ComponentResultMap> componentResultMap,
			Set<ProcedureComponentMapping> pcmSet) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.lionicNum=lionicNum;
		this.inputType = inputType;
		this.measurementUnit = measurementUnit;
		this.range_min = range_min;
		this.range_normal_min = range_normal_min;
		this.range_normal_max = range_normal_max;
		this.range_max = range_max;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.processed = processed;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.lastModDate = lastModDate;
		this.compOpt = compOpt;
		this.componentResultMap = componentResultMap;
		this.pcmSet = pcmSet;
	}

	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc,String lionicNum,
			String inputType, String measurementUnit, Double range_min, Double range_normal_min,
			Double range_normal_max, Double range_max, Integer providerServiceMapID, Boolean deleted,
			String processed, String createdBy, Timestamp createdDate, String modifiedBy, Timestamp lastModDate,
			ArrayList<Map<String, Object>> compOpt) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.lionicNum=lionicNum;
		this.inputType = inputType;
		this.measurementUnit = measurementUnit;
		this.range_min = range_min;
		this.range_normal_min = range_normal_min;
		this.range_normal_max = range_normal_max;
		this.range_max = range_max;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.processed = processed;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.lastModDate = lastModDate;
		this.compOpt = compOpt;
	}

	public ArrayList<Map<String, Object>> getCompOpt() {
		return compOpt;
	}

	public void setCompOpt(ArrayList<Map<String, Object>> compOpt) {
		this.compOpt = compOpt;
	}

	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc,
			String inputType, String createdBy, Boolean deleted) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.inputType = inputType;
		this.createdBy = createdBy;
		this.deleted = deleted;

	}
	
	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc,
			String inputType, String createdBy, Boolean deleted, Integer iotComponentID) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.inputType = inputType;
		this.createdBy = createdBy;
		this.deleted = deleted;
		this.iotComponentID=iotComponentID;

	}

	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc, String inputType, String lionicNum, String component) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.inputType = inputType;
		this.lionicNum = lionicNum;
		this.component = component;
	}

	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc,
			String inputType, String measurementUnit, Double range_min, Double range_normal_min,
			Double range_normal_max, Double range_max, ArrayList<Map<String, Object>> compOpt) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.inputType = inputType;
		this.measurementUnit = measurementUnit;
		this.range_min = range_min;
		this.range_normal_min = range_normal_min;
		this.range_normal_max = range_normal_max;
		this.range_max = range_max;
		this.compOpt = compOpt;
	}
	
	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc,String lionicNum,
			String inputType, String measurementUnit, Double range_min, Double range_normal_min,
			Double range_normal_max, Double range_max, ArrayList<Map<String, Object>> compOpt, Integer iotComponentID,String component) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.lionicNum=lionicNum;
		this.inputType = inputType;
		this.measurementUnit = measurementUnit;
		this.range_min = range_min;
		this.range_normal_min = range_normal_min;
		this.range_normal_max = range_normal_max;
		this.range_max = range_max;
		this.compOpt = compOpt;
		this.iotComponentID=iotComponentID;
		this.component=component;
		//this.isMandatory=isMandatory;
	}

/*	public static ComponentMaster getCompMasterForEdit(ComponentMaster cm) {
		ComponentMaster returnCM = null;
		if (cm != null) {
			returnCM = new ComponentMaster(cm.getTestComponentID(), cm.getTestComponentName(),
					cm.getTestComponentDesc(), cm.getInputType(), cm.getMeasurementUnit(), cm.getRange_min(),
					cm.getRange_normal_min(), cm.getRange_normal_max(), cm.getRange_max(), cm.getCompOpt());
			returnCM.setIsDecimal(cm.getIsDecimal());
		}
		return returnCM;
	}*/
	
	public static ComponentMaster getCompMasterForEdit(ComponentMaster cm) {
		ComponentMaster returnCM = null;
		if (cm != null) {
			returnCM = new ComponentMaster(cm.getTestComponentID(), cm.getTestComponentName(),
					cm.getTestComponentDesc(),cm.getLionicNum(), cm.getInputType(), cm.getMeasurementUnit(), cm.getRange_min(),
					cm.getRange_normal_min(), cm.getRange_normal_max(), cm.getRange_max(), cm.getCompOpt(), cm.getIotComponentID(),cm.getLionicTerm());
			returnCM.setIsDecimal(cm.getIsDecimal());
		}
		return returnCM;
	}

	public static ArrayList<ComponentMaster> getComponentList(ArrayList<Object[]> comListData) {
		ComponentMaster componentMaster;
		ArrayList<ComponentMaster> compList = new ArrayList<>();
		for (Object[] arrOBJ : comListData) {
			componentMaster = new ComponentMaster((Integer) arrOBJ[0], (String) arrOBJ[1], (String) arrOBJ[2],
					(String) arrOBJ[3], (String) arrOBJ[4], (Boolean) arrOBJ[5]);
			compList.add(componentMaster);
		}
		return compList;
	}

	public static ArrayList<ComponentMaster> getComponentListDelFalse(ArrayList<Object[]> comListData) {
		ComponentMaster componentMaster;
		ArrayList<ComponentMaster> compList = new ArrayList<>();
		for (Object[] arrOBJ : comListData) {
			componentMaster = new ComponentMaster((Integer) arrOBJ[0], (String) arrOBJ[1], (String) arrOBJ[2], (String) arrOBJ[3], (String) arrOBJ[4],
					(String) arrOBJ[5]);
			compList.add(componentMaster);
		}
		return compList;
	}

	public static ComponentMaster getComponent(ArrayList<Object[]> comListData) {
		ComponentMaster componentMaster = null;
		for (Object[] arrOBJ : comListData) {
			componentMaster = new ComponentMaster((Integer) arrOBJ[0], (String) arrOBJ[1], (String) arrOBJ[2],
					(String) arrOBJ[3], (String) arrOBJ[4], (Boolean) arrOBJ[5]);
		}
		return componentMaster;
	}

	public ComponentMaster(Integer testComponentID, String testComponentName, String testComponentDesc,
			String inputType, String measurementUnit, Double range_min, Double range_normal_min,
			Double range_normal_max, Double range_max, Integer providerServiceMapID, Boolean deleted,
			String processed, String createdBy, Timestamp createdDate, String modifiedBy, Timestamp lastModDate) {
		super();
		this.testComponentID = testComponentID;
		this.testComponentName = testComponentName;
		this.testComponentDesc = testComponentDesc;
		this.inputType = inputType;
		this.measurementUnit = measurementUnit;
		this.range_min = range_min;
		this.range_normal_min = range_normal_min;
		this.range_normal_max = range_normal_max;
		this.range_max = range_max;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.processed = processed;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.lastModDate = lastModDate;
	}

	public Integer getTestComponentID() {
		return testComponentID;
	}

	public void setTestComponentID(Integer testComponentID) {
		this.testComponentID = testComponentID;
	}

	public String getTestComponentName() {
		return testComponentName;
	}

	public void setTestComponentName(String testComponentName) {
		this.testComponentName = testComponentName;
	}

	public String getTestComponentDesc() {
		return testComponentDesc;
	}

	public void setTestComponentDesc(String testComponentDesc) {
		this.testComponentDesc = testComponentDesc;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getMeasurementUnit() {
		return measurementUnit;
	}

	public void setMeasurementUnit(String measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	public Double getRange_min() {
		return range_min;
	}

	public void setRange_min(Double range_min) {
		this.range_min = range_min;
	}

	public Double getRange_normal_min() {
		return range_normal_min;
	}

	public void setRange_normal_min(Double range_normal_min) {
		this.range_normal_min = range_normal_min;
	}

	public Double getRange_normal_max() {
		return range_normal_max;
	}

	public void setRange_normal_max(Double range_normal_max) {
		this.range_normal_max = range_normal_max;
	}

	public Double getRange_max() {
		return range_max;
	}

	public void setRange_max(Double range_max) {
		this.range_max = range_max;
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
