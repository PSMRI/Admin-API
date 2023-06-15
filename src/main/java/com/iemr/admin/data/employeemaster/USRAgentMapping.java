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
package com.iemr.admin.data.employeemaster;

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

@Entity
@Table(name = "m_usragentmapping")
public class USRAgentMapping
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "USRAgentMappingID")
	private Integer usrAgentMappingID;
	@Expose
	@Column(name = "USRMappingID")
	private Integer usrMappingID;
	@JoinColumn(name = "USRMappingID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.LAZY)
	private M_UserServiceRoleMapping2 usrMapping;
	@Expose
	@Column(name = "AgentID")
	private String agentID;
	@Expose
	@Column(name = "AgentPassword")
	private String agentPassword;
	@Expose
	@Column(name = "CTI_CampaignName")
	private String cti_CampaignName;
	public String getAgentPassword() {
		return agentPassword;
	}

	public void setAgentPassword(String agentPassword) {
		this.agentPassword = agentPassword;
	}

	@Expose
	@Column(name = "IsAvailable", insertable = false, updatable = true)
	private Boolean isAvailable;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
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
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;

	// M_ProviderServiceMap1
	@JoinColumn(name = "ProviderServiceMapID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.LAZY)
	private M_ProviderServiceMap1 providerServiceMap;

	@Transient
	private String oldAgentID;

	@Transient
	@Expose
	private String assignedUser;

	@Transient
	@Expose
	private String assignedRole;

	public USRAgentMapping()
	{
	}

	public String getCti_CampaignName()
	{
		return cti_CampaignName;
	}

	public Integer getProviderServiceMapID()
	{
		return providerServiceMapID;
	}

	public Boolean getIsAvailable()
	{
		return isAvailable;
	}

	public Integer getUsrAgentMappingID()
	{
		return usrAgentMappingID;
	}

	public Integer getUsrMappingID()
	{
		return usrMappingID;
	}

	public String getAgentID()
	{
		return agentID;
	}

	public String getOldAgentID()
	{
		return oldAgentID;
	}

	public static USRAgentMapping initializeAllUSRAgentMapping(Integer usrAgentMappingID, Integer usrMappingID,
			M_UserServiceRoleMapping2 usrMapping, Integer providerServiceMapID,
			M_ProviderServiceMap1 providerServiceMap, String agentID, String agentPassword, String cti_CampaignName,
			Boolean isAvailable)
	{
		USRAgentMapping usrAgentMapping = new USRAgentMapping();
		usrAgentMapping.usrAgentMappingID = usrAgentMappingID;
		usrAgentMapping.usrMappingID = usrMappingID;
		usrAgentMapping.usrMapping = usrMapping;
		usrAgentMapping.agentID = agentID;
		usrAgentMapping.agentPassword = agentPassword;
		usrAgentMapping.cti_CampaignName = cti_CampaignName;
		usrAgentMapping.isAvailable = isAvailable;
		usrAgentMapping.providerServiceMapID = providerServiceMapID;
		usrAgentMapping.providerServiceMap = providerServiceMap;
		if (usrMapping != null)
		{
			usrAgentMapping.assignedUser = usrMapping.getEmployeeMaster().getUserName();
			usrAgentMapping.assignedRole = usrMapping.getRoleName();
		}
		return usrAgentMapping;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString()
	{
		return outputMapper.gson().toJson(this);
	}

}
