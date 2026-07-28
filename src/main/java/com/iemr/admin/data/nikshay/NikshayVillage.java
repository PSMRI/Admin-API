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
 * Nikshay's own Village master, linked directly to its Nikshay Facility.
 * Imported straight from Nikshay's location data with no matching against
 * AMRIT's village master (m_DistrictBranchMapping) — a prior approach that
 * matched by name against AMRIT villages left ~44% of villages unmatched,
 * partly from incomplete migration runs and partly from real staleness in
 * AMRIT's own district/block data. This table guarantees full coverage of
 * every village Nikshay has, independent of AMRIT's location hierarchy.
 */
@Data
@Entity
@Table(name = "m_nikshay_village")
public class NikshayVillage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "NikshayVillageID")
	private Integer nikshayVillageID;

	@Expose
	@Column(name = "VillageName")
	private String villageName;

	@Expose
	@Column(name = "NikshayFacilityID")
	private Integer nikshayFacilityID;

	@Expose
	@Column(name = "Deleted")
	private Boolean deleted = false;

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

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
