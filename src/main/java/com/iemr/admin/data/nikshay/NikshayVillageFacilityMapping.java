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
package com.iemr.admin.data.nikshay;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * Links an existing AMRIT village (m_DistrictBranchMapping.DistrictBranchID)
 * to the Nikshay Facility/TU it falls under. Built once from the location
 * import's name-matching results — used to trace a beneficiary's existing
 * village back to their Nikshay Facility/TU without asking the field worker
 * to re-select it per case.
 */
@Data
@Entity
@Table(name = "m_nikshay_village_facility_mapping")
public class NikshayVillageFacilityMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "MappingID")
	private Integer mappingID;

	@Expose
	@Column(name = "AmritVillageID")
	private Integer amritVillageID;

	@Expose
	@Column(name = "NikshayFacilityID")
	private Integer nikshayFacilityID;

	@Expose
	@Column(name = "NikshayTUID")
	private Integer nikshayTUID;

	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;

	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
