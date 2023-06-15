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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

@Entity
@Table(name="v_showsubcategory")
public class V_Showsubcategory {
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "SubCategoryID")
	private Integer subCategoryID;
	@Expose
	@Column(name = "CategoryID")
	private Integer categoryID;
	@Expose
	@Column(name = "CategoryName")
	private String categoryName; 
	@Expose
	@Column(name = "CategoryDesc")
	private String categoryDesc;
	
	@Expose
	@Column(name = "SubCategoryName")
	private String subCategoryName;
	@Expose
	@Column(name = "SubCategoryDesc")
	private String subCategoryDesc;
	@Expose
	@Column(name = "SubCatFilePath")
	private String subCatFilePath; 
	@Expose
	@Column(name = "SubCategoryDeleted")
	private Boolean subCategoryDeleted; 
	@Expose
	@Column(name = "SubServiceID")
	private Integer subServiceID;
	@Expose
	@Column(name = "SubServiceName")
	private String subServiceName;
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID; 
	@Expose
	@Column(name = "CategoryDeleted")
	private Boolean categoryDeleted;
	
	
	
	
	public V_Showsubcategory() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	public Integer getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(Integer categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public Integer getSubCategoryID() {
		return subCategoryID;
	}

	public void setSubCategoryID(Integer subCategoryID) {
		this.subCategoryID = subCategoryID;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public String getSubCategoryDesc() {
		return subCategoryDesc;
	}

	public void setSubCategoryDesc(String subCategoryDesc) {
		this.subCategoryDesc = subCategoryDesc;
	}

	public String getSubCatFilePath() {
		return subCatFilePath;
	}

	public void setSubCatFilePath(String subCatFilePath) {
		this.subCatFilePath = subCatFilePath;
	}

	public Boolean getSubCategoryDeleted() {
		return subCategoryDeleted;
	}

	public void setSubCategoryDeleted(Boolean subCategoryDeleted) {
		this.subCategoryDeleted = subCategoryDeleted;
	}

	public Integer getSubServiceID() {
		return subServiceID;
	}

	public void setSubServiceID(Integer subServiceID) {
		this.subServiceID = subServiceID;
	}

	public String getSubServiceName() {
		return subServiceName;
	}

	public void setSubServiceName(String subServiceName) {
		this.subServiceName = subServiceName;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	public Boolean getCategoryDeleted() {
		return categoryDeleted;
	}

	public void setCategoryDeleted(Boolean categoryDeleted) {
		this.categoryDeleted = categoryDeleted;
	}








	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
