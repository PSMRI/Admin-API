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
package com.iemr.admin.data.VanSpokeMapping;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.data.parkingPlace.M_parkingPlaceMapped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="m_vanSpokeMapping")
public class m_VanSpokeMapping {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="VanspokeID")
	private Integer VanspokeID;
	
	@Expose
	@Column(name="tm_StateID ")
	private Integer tm_StateID;
	
	@Expose
	@Column(name="tm_StateName")
	private String tm_StateName;
	
	@Expose
	@Column(name="tm_ZoneID")
	private Integer tm_ZoneID;
	
	@Expose
	@Column(name="tm_ZoneName")
	private String tm_ZoneName;
	
	@Expose
	@Column(name="tm_HubID")
	private Integer tm_HubID;
	
	@Expose
	@Column(name="tm_HubName")
	private String tm_HubName;
	
	@Expose
	@Column(name="tm_servicePointID")
	private Integer tm_servicePointID;	
	
	@Expose
	@Column(name="tm_servicepointname")
	private String tm_servicepointname;
	
	@Expose
	@Column(name="tm_SpokeID")
	private Integer tm_SpokeID;
	
	@Expose
	@Column(name="tm_spokeName")
	private String tm_spokeName;
	
	@Expose
	@Column(name="mmu_StateID")
	private Integer mmu_StateID;
	
	@Expose
	@Column(name="mmu_StateName")
	private String mmu_StateName;
	
	@Expose
	@Column(name="tm_vanTypeID")
	private Integer tm_vanTypeID;
	
	@Expose
	@Column(name="tm_ProviderServiceMapID")
	private Integer tm_ProviderServiceMapID;
	
	@Expose
	@Column(name="mmu_ZoneID")
	private Integer mmu_ZoneID;
	
	@Expose
	@Column(name="mmu_ZoneName")
	private String mmu_ZoneName;
	
	@Expose
	@Column(name="mmu_parkingPlaceID")
	private Integer mmu_parkingPlaceID ;	
	
	@Expose
	@Column(name="mmu_parkingPlaceName")
	private String mmu_parkingPlaceName;
	
	@Expose
	@Column(name="mmu_servicePointID")
	private Integer mmu_servicePointID ;
	
	@Expose
	@Column(name="mmu_servicePointName")
	private String mmu_servicePointName;
	
	@Expose
	@Column(name="mmu_VanID")
	private Integer mmu_VanID ;
	
	@Expose
	@Column(name="mmu_vantypeID")
	private Integer mmu_vantypeID ;
	
	@Expose
	@Column(name="mmu_vanName")
	private String mmu_vanName;
	
	@Expose
	@Column(name="mmu_ProviderServiceMapID")
	private Integer mmu_ProviderServiceMapID;
	
	@Expose
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(updatable = false, insertable = false, name = "mmu_parkingPlaceID",referencedColumnName = "parkingPlaceID")
	private M_parkingPlaceMapped m_parkingPlaceMapped;
	
	@Expose
	@Column(name = "Deleted",insertable = false, updatable = true)
	private Boolean deleted; 
	@Expose
	@Column(name = "Processed",insertable = false, updatable = true)
	private String processed;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate" ,insertable = false, updatable = false)
	private Timestamp createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy; 
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;
//	
//	@Transient
//	private OutputMapper outputMapper = new OutputMapper();
//
//	@Override
//	public String toString() {
//		return outputMapper.gson().toJson(this);
//	}
}
