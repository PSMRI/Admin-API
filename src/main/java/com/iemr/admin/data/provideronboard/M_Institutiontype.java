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
package com.iemr.admin.data.provideronboard;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name = "m_institutiontype")
public class M_Institutiontype
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "InstitutionTypeID")
	private Integer institutionTypeID;
	@Expose
	@Column(name = "InstitutionType")
	private String institutionType;
	@Expose
	@Column(name = "InstitutionTypeDesc")
	private String institutionTypeDesc;
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
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
	@Column(name ="Districtid")
	private Integer districtId;
	@Expose
	@Column(name ="SubDistrictid")
	private Integer subDistrictId;
	@Expose
	@Column(name ="Villageid")
	private Integer villageId;

	public M_Institutiontype()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the institutionTypeID
	 */
	public Integer getInstitutionTypeID()
	{
		return institutionTypeID;
	}

	/**
	 * @param institutionTypeID
	 *            the institutionTypeID to set
	 */
	public void setInstitutionTypeID(Integer institutionTypeID)
	{
		this.institutionTypeID = institutionTypeID;
	}

	/**
	 * @return the institutionType
	 */
	public String getInstitutionType()
	{
		return institutionType;
	}

	/**
	 * @param institutionType
	 *            the institutionType to set
	 */
	public void setInstitutionType(String institutionType)
	{
		this.institutionType = institutionType;
	}

	/**
	 * @return the institutionTypeDesc
	 */
	public String getInstitutionTypeDesc()
	{
		return institutionTypeDesc;
	}

	/**
	 * @param institutionTypeDesc
	 *            the institutionTypeDesc to set
	 */
	public void setInstitutionTypeDesc(String institutionTypeDesc)
	{
		this.institutionTypeDesc = institutionTypeDesc;
	}

	/**
	 * @return the providerServiceMapID
	 */
	public Integer getProviderServiceMapID()
	{
		return providerServiceMapID;
	}

	/**
	 * @param providerServiceMapID
	 *            the providerServiceMapID to set
	 */
	public void setProviderServiceMapID(Integer providerServiceMapID)
	{
		this.providerServiceMapID = providerServiceMapID;
	}

	/**
	 * @return the deleted
	 */
	public Boolean getDeleted()
	{
		return deleted;
	}

	/**
	 * @param deleted
	 *            the deleted to set
	 */
	public void setDeleted(Boolean deleted)
	{
		this.deleted = deleted;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate)
	{
		this.createdDate = createdDate;
	}

	public Timestamp getLastModDate()
	{
		return lastModDate;
	}

	public void setLastModDate(Timestamp lastModDate)
	{
		this.lastModDate = lastModDate;
	}

	public Integer getSubDistrictId() {
		return subDistrictId;
	}

	public void setSubDistrictId(Integer subDistrictId) {
		this.subDistrictId = subDistrictId;
	}
	
	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}	

	public Integer getVillageId() {
		return villageId;
	}

	public void setVillageId(Integer villageId) {
		this.villageId = villageId;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString()
	{
		return outputMapper.gson().toJson(this);
	}

}
