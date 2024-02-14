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
package com.iemr.admin.data.calibration;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="m_calibration")
public class Calibration {
	
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Expose
		@Column(name="calibrationID")
		private Long calibrationID;
		
		
		@Expose
		@Column(name="ProviderServiceMapID")
		private Integer providerServiceMapID;
		@Expose
		@Column(name = "CalibrationStartAPI")
		private String calibrationStartAPI;
		@Expose
		@Column(name = "CalibrationStatusAPI")
		private String calibrationStatusAPI;
		@Expose
		@Column(name = "CalibrationEndAPI")
		private String calibrationEndAPI;
		@Expose
		@Column(name="Deleted",insertable = false, updatable = true)
		private Boolean deleted;
		
		@Expose
		@Column(name="CreatedBy")
		private String createdBy;
		
		@Expose
		@Column(name="CreatedDate",insertable = false, updatable = false)
		private Timestamp createdDate;
		
		@Expose
		@Column(name="ModifiedBy")
		private String modifiedBy;
		
		@Expose
		@Column(name="LastModDate",insertable = false, updatable = false)
		private Timestamp lastModDate;

		public Long getCalibrationID() {
			return calibrationID;
		}

		public void setCalibrationID(Long calibrationID) {
			this.calibrationID = calibrationID;
		}

		public Integer getProviderServiceMapID() {
			return providerServiceMapID;
		}

		public void setProviderServiceMapID(Integer providerServiceMapID) {
			this.providerServiceMapID = providerServiceMapID;
		}

		public String getCalibrationStartAPI() {
			return calibrationStartAPI;
		}

		public void setCalibrationStartAPI(String calibrationStartAPI) {
			this.calibrationStartAPI = calibrationStartAPI;
		}

		public String getCalibrationStatusAPI() {
			return calibrationStatusAPI;
		}

		public void setCalibrationStatusAPI(String calibrationStatusAPI) {
			this.calibrationStatusAPI = calibrationStatusAPI;
		}

		public String getCalibrationEndAPI() {
			return calibrationEndAPI;
		}

		public void setCalibrationEndAPI(String calibrationEndAPI) {
			this.calibrationEndAPI = calibrationEndAPI;
		}

		public Boolean getDeleted() {
			return deleted;
		}

		public void setDeleted(Boolean deleted) {
			this.deleted = deleted;
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
