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
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

/***
 * 
 * @author Rajeev Tripathi
 *
 */

@Entity
@Table(name = "m_componentresultmap")
public class ComponentResultMap {
	@Id
	@GeneratedValue
	@Expose
	@Column(name = "ComponentResultMapID")
	private Integer componentResultMapID;
	@Expose
	@Column(name = "TestComponentID")
	private Integer testComponentID;
	@Expose
	@Column(name = "ResultValue")
	private String resultValue;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TestComponentID", insertable = false, updatable = false)
	private ComponentMaster componentMaster;

	public ComponentResultMap() {
	}

	public ComponentResultMap(Integer componentResultMapID, Integer testComponentID, String resultValue,
			Integer providerServiceMapID, Boolean deleted, String processed, String createdBy, Timestamp createdDate,
			String modifiedBy, Timestamp lastModDate) {
		super();
		this.componentResultMapID = componentResultMapID;
		this.testComponentID = testComponentID;
		this.resultValue = resultValue;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.processed = processed;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.lastModDate = lastModDate;
	}

	public static ArrayList<ComponentResultMap> getComponentResultMapList(ArrayList<Map<String, Object>> compOptList,
			Integer psmID, String createdBy, Integer compID) {
		ArrayList<ComponentResultMap> responseList = new ArrayList<>();
		ComponentResultMap obj = null;
		for (Map<String, Object> s : compOptList) {

			obj = new ComponentResultMap();
			obj.setResultValue(s.get("name").toString());
			obj.setProviderServiceMapID(psmID);
			obj.setCreatedBy(createdBy);
			obj.setTestComponentID(compID);

			responseList.add(obj);

		}
		return responseList;
	}

	public static ArrayList<Map<String, Object>> getComplist(ArrayList<ComponentResultMap> compResultMapList) {
		ArrayList<Map<String, Object>> returnOBJ = new ArrayList<>();
		Map<String, Object> compMap = null;
		if (compResultMapList != null && compResultMapList.size() > 0) {
			for (ComponentResultMap crm : compResultMapList) {
				compMap = new HashMap<String, Object>();
				compMap.put("name", crm.getResultValue());
				returnOBJ.add(compMap);
			}
		}
		return returnOBJ;
	}

	public Integer getComponentResultMapID() {
		return componentResultMapID;
	}

	public void setComponentResultMapID(Integer componentResultMapID) {
		this.componentResultMapID = componentResultMapID;
	}

	public Integer getTestComponentID() {
		return testComponentID;
	}

	public void setTestComponentID(Integer testComponentID) {
		this.testComponentID = testComponentID;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
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
