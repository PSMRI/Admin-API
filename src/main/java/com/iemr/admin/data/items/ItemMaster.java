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
package com.iemr.admin.data.items;

import java.sql.Date;

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
import com.iemr.admin.data.manufacturer.M_Manufacturer;
import com.iemr.admin.data.pharmacologicalcategory.M_Pharmacologicalcategory;
import com.iemr.admin.to.provider.ProviderServiceMappingTO;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;


@Entity
@Table(name="m_item")
@Data
public class ItemMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="ItemID")
	private Integer itemID;
	
	@Expose
	@Column(name="ItemName")
	private String itemName;
	
	@Expose
	@Column(name="isEDL")
	private Boolean isEDL;
	
	@Expose
	@Column(name="ItemDesc")
	private String itemDesc; 
	
	@Expose
	@Column(name="ItemCode")
	private String itemCode; 
	
	@Expose
	@Column(name="ItemCategoryID")
	private Integer itemCategoryID; 
	
	@Expose
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(updatable = false, insertable = false, name = "itemCategoryID")
	private M_ItemCategory itemCategory;
	

	@Expose
	@Column(name="IsMedical")
	private Boolean isMedical;
		
	@Expose
	@Column(name="ItemFormID")
	private Integer itemFormID; 
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ItemFormID")
	private M_ItemForm itemForm;
	
	@Expose
	@Column(name="PharmacologyCategoryID")
	private Integer pharmacologyCategoryID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "PharmacologyCategoryID")
	private M_Pharmacologicalcategory pharmacologyCategory;
	
	@Expose
	@Column(name="ManufacturerID")
	private Integer manufacturerID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ManufacturerID")
	private M_Manufacturer manufacturer;
	
	@Expose
	@Column(name="Strength")
	private String strength;
	
	@Expose
	@Column(name="UOMID")
	private Integer uomID;
	
	@Expose
	@Column(name="IsScheduledDrug")
	private Boolean isScheduledDrug;
	
	@Expose
	@Column(name="Composition")
	private String composition;

	@Expose
	@Column(name="RouteID")
	private Integer routeID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "RouteID")
	private M_Route route;
	
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ProviderServiceMapID")
	private ProviderServiceMappingTO providerServiceMap;
	
	@Expose
	@Column(name="Status")
	private String status;
	
	@Expose
	@Column(name="Discontinued",insertable = false, updatable = true)
	private Boolean discontinued;
	
	@Expose
	@Column(name="Deleted",insertable = false, updatable = true)
	private Boolean deleted; 
	
	@Expose
	@Column(name="Processed",insertable = false, updatable = true)
	private Character processed; 
	
	@Expose
	@Column(name="CreatedBy")
	private String createdBy; 
	
	@Expose
	@Column(name="CreatedDate",insertable = false, updatable = false)
	private Date createdDate;
	
	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy;
	
	@Expose
	@Column(name="LastModDate",insertable = false, updatable = false)
	private Date lastModDate;
	
	@Expose
	@Column(name="Sctcode")
	private String sctCode;
	
	@Expose
	@Column(name="SctTerm")
	private String sctTerm;
	
	@Expose
	@Transient
	private Integer quantity;
	
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	public ItemMaster() {
	}

	public ItemMaster(Integer itemID, String ItemName) {
		// TODO Auto-generated constructor stub
		this.itemID=itemID;
		this.itemName=ItemName;
	}
}
