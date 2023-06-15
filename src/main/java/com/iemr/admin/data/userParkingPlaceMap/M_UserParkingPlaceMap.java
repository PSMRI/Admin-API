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
package com.iemr.admin.data.userParkingPlaceMap;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.State;
import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;

@Data
@Entity
@Table(name = "m_userparkingplacemap")
public class M_UserParkingPlaceMap {
	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer userParkingPlaceMapID;

	@Expose
	@Column(name = "UserID")
	private Integer userID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "userID")
	private M_User1 m_user;

	@Expose
	@Column(name = "StateID")
	private Integer stateID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "stateID")
	private State state;
	@Expose
	@Transient
	String stateName;

	@Expose
	@Column(name = "DistrictID")
	private Integer districtID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "DistrictID")
	private M_District m_district;
	@Expose
	@Transient
	String districtName;

	@Expose
	@Column(name = "ParkingPlaceID")
	private Integer parkingPlaceID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "parkingPlaceID")
	private M_Parkingplace m_parkingplace;

		
	@OneToMany(mappedBy = "userParkingPlaceMap", fetch = FetchType.LAZY)
	@Expose
	private List<M_UserVanMapping> uservanmapping;
	
	
	@Expose
	@Transient
	private String parkingPlaceName;

	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, insertable = false, name = "ProviderServiceMapID")
	private M_ProviderServiceMapping m_providerServiceMapping;

	@Expose
	@Transient
	private Integer serviceProviderID;

	@Expose
	@Transient
	private String firstName;

	@Expose
	@Transient
	private String lastName;

	@Expose
	@Transient
	private String userName;

	@Expose
	@Transient
	private Boolean userDeleted;

	@Expose
	@Transient
	private Integer designationID;
	
	@Expose
	@Transient
	private String designationName;

	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name = "Processed", insertable = false, updatable = true)
	private String processed;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Date createdDate;
	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Date lastModDate;

	public M_UserParkingPlaceMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public M_UserParkingPlaceMap(Integer userParkingPlaceMapID, Integer userID, String firstName, String lastName,
			String userName, Integer designationID, String roleIDs, String roleName, Short genderID, String genderName,
			Integer stateID, String stateName, Integer districtID, String districtName, Integer parkingPlaceID,
			String parkingPlaceName, String emergencyContactNo, Integer providerServiceMapID, Boolean deleted) {
		super();
		this.userParkingPlaceMapID = userParkingPlaceMapID;
		this.userID = userID;
		this.stateID = stateID;
		this.stateName = stateName;
		this.districtID = districtID;
		this.districtName = districtName;
		this.parkingPlaceID = parkingPlaceID;
		this.parkingPlaceName = parkingPlaceName;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.m_user = new M_User1();
		this.m_user.setUserName(userName);
		this.m_user.setFirstName(firstName);
		this.m_user.setLastName(lastName);
		this.m_user.setGenderID(genderID);
		this.m_user.setGenderName(genderName);
		this.m_user.setDesignationID(designationID);
		this.m_user.setEmergencyContactNo(emergencyContactNo);
		this.m_user.setM_userServiceRoleMapping(new M_UserServiceRoleMapping2());
		// this.m_user.getM_userServiceRoleMapping().setRoleID(roleID);
		this.m_user.getM_userServiceRoleMapping().setRoleName(roleName);
		this.setDeleted(deleted);
	}

	public M_UserParkingPlaceMap(Integer userParkingPlaceMapID, Integer userID, String firstName, String lastName,
			String userName, Integer designationID, Integer districtID, Integer parkingPlaceID, String parkingPlaceName,
			Integer providerServiceMapID, Boolean deleted, Boolean userDeleted) {
		this.userParkingPlaceMapID = userParkingPlaceMapID;
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.designationID = designationID;
		this.districtID = districtID;
		this.parkingPlaceID = parkingPlaceID;
		this.parkingPlaceName = parkingPlaceName;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.userDeleted = userDeleted;

	}

	public M_UserParkingPlaceMap(Integer userParkingPlaceMapID, Integer userID, String firstName, String lastName,
			String userName, Integer designationID, Integer districtID, Integer parkingPlaceID, String parkingPlaceName,
			Integer providerServiceMapID, Boolean deleted, Boolean userDeleted,String designationName) {
		this.userParkingPlaceMapID = userParkingPlaceMapID;
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.designationID = designationID;
		this.designationName=designationName;
		this.districtID = districtID;
		this.parkingPlaceID = parkingPlaceID;
		this.parkingPlaceName = parkingPlaceName;
		this.providerServiceMapID = providerServiceMapID;
		this.deleted = deleted;
		this.userDeleted = userDeleted;

	}
	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public M_User1 getM_user() {
		return m_user;
	}

	public void setM_user(M_User1 m_user) {
		this.m_user = m_user;
	}

	public Integer getStateID() {
		return stateID;
	}

	public void setStateID(Integer stateID) {
		this.stateID = stateID;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Integer getDistrictID() {
		return districtID;
	}

	public void setDistrictID(Integer districtID) {
		this.districtID = districtID;
	}

	public M_District getM_district() {
		return m_district;
	}

	public void setM_district(M_District m_district) {
		this.m_district = m_district;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Integer getParkingPlaceID() {
		return parkingPlaceID;
	}

	public void setParkingPlaceID(Integer parkingPlaceID) {
		this.parkingPlaceID = parkingPlaceID;
	}

	public M_Parkingplace getM_parkingplace() {
		return m_parkingplace;
	}

	public void setM_parkingplace(M_Parkingplace m_parkingplace) {
		this.m_parkingplace = m_parkingplace;
	}

	public String getParkingPlaceName() {
		return parkingPlaceName;
	}

	public void setParkingPlaceName(String parkingPlaceName) {
		this.parkingPlaceName = parkingPlaceName;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}

	public M_ProviderServiceMapping getM_providerServiceMapping() {
		return m_providerServiceMapping;
	}

	public void setM_providerServiceMapping(M_ProviderServiceMapping m_providerServiceMapping) {
		this.m_providerServiceMapping = m_providerServiceMapping;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getProcessed() {
		return processed;
	}

	public void setProcessed(String processed) {
		this.processed = processed;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getLastModDate() {
		return lastModDate;
	}

	public void setLastModDate(Date lastModDate) {
		this.lastModDate = lastModDate;
	}

	public Integer getUserParkingPlaceMapID() {
		return userParkingPlaceMapID;
	}

	public Integer getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Integer serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

}
