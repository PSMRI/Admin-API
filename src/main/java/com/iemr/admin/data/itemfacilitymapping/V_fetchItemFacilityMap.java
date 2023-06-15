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
package com.iemr.admin.data.itemfacilitymapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;
import lombok.Data;

@Entity
@Table(name="v_fetchItemFacilityMap")
@Data
public class V_fetchItemFacilityMap {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "ItemFacilityMapID")
	private Integer itemFacilityMapID; 
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@Column(name = "Status")
	private String status; 
	@Expose
	@Column(name = "ItemFacilityMappingDeleted")
	private Boolean itemFacilityMappingDeleted; 
	@Expose
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
	@Column(name = "FacilityTypeName")
	private String facilityTypeName;
	@Expose
	@Column(name = "FacilityTypeDesc")
	private String facilityTypeDesc;
	@Expose
	@Column(name = "FacilityTypeCode")
	private String facilityTypeCode; 
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
	@Column(name = "IsMainFacility")
	private Boolean isMainFacility;
	@Expose
	@Column(name = "FacilityDeleted")
	private Boolean facilityDeleted;
	@Expose
	@Column(name = "ItemDeleted")
	private Boolean itemDeleted;
	
	@Expose
	@Column(name = "ItemID")
	private String itemID;
	
	@Expose
	@Column(name = "ItemName")
	private String itemName;
	@Expose
	@Column(name = "ItemDesc")
	private String itemDesc;
	@Expose
	@Column(name = "ItemCode")
	private String itemCode;
	@Expose
	@Column(name = "ItemCategoryID")
	private Integer itemCategoryID;
	@Expose
	@Column(name = "ItemCategoryName")
	private String itemCategoryName;
	@Expose
	@Column(name = "ItemCategoryDesc")
	private String itemCategoryDesc;
	@Expose
	@Column(name = "ItemCategoryCode")
	private String itemCategoryCode;
	@Expose
	@Column(name = "IsMedical")
	private Boolean isMedical;
	@Expose
	@Column(name = "ItemFormID")
	private Integer itemFormID;
	@Expose
	@Column(name = "ItemFormName")
	private String itemFormName;
	@Expose
	@Column(name = "ItemFormDesc")
	private String itemFormDesc;
	@Expose
	@Column(name = "ItemFormCode")
	private String itemFormCode;
	@Expose
	@Column(name = "PharmacologyCategoryID")
	private Integer pharmacologyCategoryID;
	@Expose
	@Column(name = "PharmCategoryName")
	private String pharmCategoryName;
	@Expose
	@Column(name = "PharmCategoryDesc")
	private String pharmCategoryDesc;
	@Expose
	@Column(name = "PharmCategoryCode")
	private String pharmCategoryCode;
	@Expose
	@Column(name = "ManufacturerID")
	private Integer manufacturerID;
	@Expose
	@Column(name = "Strength")
	private String strength;
	@Expose
	@Column(name = "IsScheduledDrug")
	private Boolean isScheduledDrug;
	@Expose
	@Column(name = "RouteID")
	private Integer routeID;
	
	public V_fetchItemFacilityMap() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	
	
	
	
	public Integer getItemFacilityMapID() {
		return itemFacilityMapID;
	}

	public void setItemFacilityMapID(Integer itemFacilityMapID) {
		this.itemFacilityMapID = itemFacilityMapID;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getItemFacilityMappingDeleted() {
		return itemFacilityMappingDeleted;
	}

	public void setItemFacilityMappingDeleted(Boolean itemFacilityMappingDeleted) {
		this.itemFacilityMappingDeleted = itemFacilityMappingDeleted;
	}

	public Integer getFacilityID() {
		return facilityID;
	}

	public void setFacilityID(Integer facilityID) {
		this.facilityID = facilityID;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getFacilityDesc() {
		return facilityDesc;
	}

	public void setFacilityDesc(String facilityDesc) {
		this.facilityDesc = facilityDesc;
	}

	public String getFacilityCode() {
		return facilityCode;
	}

	public void setFacilityCode(String facilityCode) {
		this.facilityCode = facilityCode;
	}

	public Integer getFacilityTypeID() {
		return facilityTypeID;
	}

	public void setFacilityTypeID(Integer facilityTypeID) {
		this.facilityTypeID = facilityTypeID;
	}

	public String getFacilityTypeName() {
		return facilityTypeName;
	}

	public void setFacilityTypeName(String facilityTypeName) {
		this.facilityTypeName = facilityTypeName;
	}

	public String getFacilityTypeDesc() {
		return facilityTypeDesc;
	}

	public void setFacilityTypeDesc(String facilityTypeDesc) {
		this.facilityTypeDesc = facilityTypeDesc;
	}

	public String getFacilityTypeCode() {
		return facilityTypeCode;
	}

	public void setFacilityTypeCode(String facilityTypeCode) {
		this.facilityTypeCode = facilityTypeCode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPhysicalLocation() {
		return physicalLocation;
	}

	public void setPhysicalLocation(String physicalLocation) {
		this.physicalLocation = physicalLocation;
	}

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public Boolean getIsMainFacility() {
		return isMainFacility;
	}

	public void setIsMainFacility(Boolean isMainFacility) {
		this.isMainFacility = isMainFacility;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Integer getItemCategoryID() {
		return itemCategoryID;
	}

	public void setItemCategoryID(Integer itemCategoryID) {
		this.itemCategoryID = itemCategoryID;
	}

	public String getItemCategoryName() {
		return itemCategoryName;
	}

	public void setItemCategoryName(String itemCategoryName) {
		this.itemCategoryName = itemCategoryName;
	}

	public String getItemCategoryDesc() {
		return itemCategoryDesc;
	}

	public void setItemCategoryDesc(String itemCategoryDesc) {
		this.itemCategoryDesc = itemCategoryDesc;
	}

	public String getItemCategoryCode() {
		return itemCategoryCode;
	}

	public void setItemCategoryCode(String itemCategoryCode) {
		this.itemCategoryCode = itemCategoryCode;
	}

	public Boolean getIsMedical() {
		return isMedical;
	}

	public void setIsMedical(Boolean isMedical) {
		this.isMedical = isMedical;
	}

	public Integer getItemFormID() {
		return itemFormID;
	}

	public void setItemFormID(Integer itemFormID) {
		this.itemFormID = itemFormID;
	}

	public String getItemFormName() {
		return itemFormName;
	}

	public void setItemFormName(String itemFormName) {
		this.itemFormName = itemFormName;
	}

	public String getItemFormDesc() {
		return itemFormDesc;
	}

	public void setItemFormDesc(String itemFormDesc) {
		this.itemFormDesc = itemFormDesc;
	}

	public String getItemFormCode() {
		return itemFormCode;
	}

	public void setItemFormCode(String itemFormCode) {
		this.itemFormCode = itemFormCode;
	}

	public Integer getPharmacologyCategoryID() {
		return pharmacologyCategoryID;
	}

	public void setPharmacologyCategoryID(Integer pharmacologyCategoryID) {
		this.pharmacologyCategoryID = pharmacologyCategoryID;
	}

	public String getPharmCategoryName() {
		return pharmCategoryName;
	}

	public void setPharmCategoryName(String pharmCategoryName) {
		this.pharmCategoryName = pharmCategoryName;
	}

	public String getPharmCategoryDesc() {
		return pharmCategoryDesc;
	}

	public void setPharmCategoryDesc(String pharmCategoryDesc) {
		this.pharmCategoryDesc = pharmCategoryDesc;
	}

	public String getPharmCategoryCode() {
		return pharmCategoryCode;
	}

	public void setPharmCategoryCode(String pharmCategoryCode) {
		this.pharmCategoryCode = pharmCategoryCode;
	}

	public Integer getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(Integer manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	public Boolean getIsScheduledDrug() {
		return isScheduledDrug;
	}

	public void setIsScheduledDrug(Boolean isScheduledDrug) {
		this.isScheduledDrug = isScheduledDrug;
	}

	public Integer getRouteID() {
		return routeID;
	}

	public void setRouteID(Integer routeID) {
		this.routeID = routeID;
	}









	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}


}
