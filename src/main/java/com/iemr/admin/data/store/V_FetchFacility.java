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
package com.iemr.admin.data.store;

import java.util.Date;

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

@Data
@Entity
@Table(name="v_fetchfacility")
public class V_FetchFacility {

	@Id
	@Expose
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "FacilityID")
	private Integer facilityID;

	@Expose
	@Column(name = "FacilityName")
	private String facilityName;

	@Expose
	@Column(name = "FacilityDesc")
	private String facilityDesc;

	@Expose
	@Column(name = "FacilityCode")
	private String facilityCode;

	@Expose
	@Column(name = "FacilityTypeID")
	private Integer facilityTypeID;

	@Expose
	@Column(name = "Location")
	private String location;

	@Expose
	@Column(name = "PhysicalLocation")
	private String physicalLocation;

	@Expose
	@Column(name = "StoreType")
	private String storeType;

	@Expose
	@Column(name = "Status")
	private String status;

	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;

	@Expose
	@Column(name = "IsMainFacility")
	private Boolean isMainFacility;

	@Expose
	@Column(name = "MainFacilityID")
	private Integer mainFacilityID;

	@Expose
	@Column(name = "FacilityDeleted")
	private Boolean facilityDeleted;

	@Expose
	@Column(name = "FacilityCreatedBy")
	private String facilityCreatedBy;

	@Expose
	@Column(name = "FacilityCreatedDate")
	private Date facilityCreatedDate;

	@Expose
	@Column(name = "FacilityModifiedBy")
	private String facilityModifiedBy;

	@Expose
	@Column(name = "FacilityLastModDate")
	private Date facilityLastModDate;

	@Expose
	@Column(name = "ParkingPlaceID")
	private Integer parkingPlaceID;

	@Expose
	@Column(name = "ParkingPlaceName")
	private String parkingPlaceName;

	@Expose
	@Column(name = "ParkingPlaceDesc")
	private String parkingPlaceDesc;

	@Expose
	@Column(name = "AreaHQAddress")
	private String areaHQAddress;

	@Expose
	@Column(name = "PPDeleted")
	private Boolean pPDeleted;

	@Expose
	@Column(name = "VanID")
	private Integer vanID;

	@Expose
	@Column(name = "VanName")
	private String vanName;

	@Expose
	@Column(name = "VehicalNo")
	private String vehicalNo;

	@Expose
	@Column(name = "VanTypeID")
	private Integer vanTypeID;

	@Expose
	@Column(name = "VanParkingPlaceID")
	private Integer vanParkingPlaceID;

	@Expose
	@Column(name = "VanDeleted")
	private Boolean vanDeleted;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
