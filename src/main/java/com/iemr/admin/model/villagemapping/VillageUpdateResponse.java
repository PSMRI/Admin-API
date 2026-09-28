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
package com.iemr.admin.model.villagemapping;

public class VillageUpdateResponse {
	private Integer uSRMappingID;
	private String userName;

	private Integer oldVillageID;
	private String oldVillageName;
	private Integer newVillageID;
	private String newVillageName;

	private long permanentAddressesUpdated;
	private long currentAddressesUpdated;

	public Integer getuSRMappingID() {
		return uSRMappingID;
	}

	public void setuSRMappingID(Integer uSRMappingID) {
		this.uSRMappingID = uSRMappingID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getOldVillageID() {
		return oldVillageID;
	}

	public void setOldVillageID(Integer oldVillageID) {
		this.oldVillageID = oldVillageID;
	}

	public String getOldVillageName() {
		return oldVillageName;
	}

	public void setOldVillageName(String oldVillageName) {
		this.oldVillageName = oldVillageName;
	}

	public Integer getNewVillageID() {
		return newVillageID;
	}

	public void setNewVillageID(Integer newVillageID) {
		this.newVillageID = newVillageID;
	}

	public String getNewVillageName() {
		return newVillageName;
	}

	public void setNewVillageName(String newVillageName) {
		this.newVillageName = newVillageName;
	}

	public long getPermanentAddressesUpdated() {
		return permanentAddressesUpdated;
	}

	public void setPermanentAddressesUpdated(long permanentAddressesUpdated) {
		this.permanentAddressesUpdated = permanentAddressesUpdated;
	}

	public long getCurrentAddressesUpdated() {
		return currentAddressesUpdated;
	}

	public void setCurrentAddressesUpdated(long currentAddressesUpdated) {
		this.currentAddressesUpdated = currentAddressesUpdated;
	}
}
