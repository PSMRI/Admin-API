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
package com.iemr.admin.to.vanMaster;

import java.util.List;

import javax.persistence.Transient;

import com.iemr.admin.data.servicePoint.M_Servicepoint;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.data.vanServicePointMapping.M_VanServicePointMap;
import com.iemr.admin.data.vanType.M_VanType;

public class VanMasterTO {

	@Transient
	private List<M_Van> vanMaster;
	
	@Transient
	private List<M_VanType> vanTypeMaster;
	
	@Transient
	private  List<M_VanServicePointMap> vanServicePointMappings;

	public List<M_VanServicePointMap> getVanServicePointMappings() {
		return vanServicePointMappings;
	}

	public void setVanServicePointMappings(List<M_VanServicePointMap> vanServicePointMappings) {
		this.vanServicePointMappings = vanServicePointMappings;
	}

	public List<M_VanType> getVanTypeMaster() {
		return vanTypeMaster;
	}

	public void setVanTypeMaster(List<M_VanType> vanTypeMaster) {
		this.vanTypeMaster = vanTypeMaster;
	}

	public List<M_Van> getVanMaster() {
		return vanMaster;
	}

	public void setVanMaster(List<M_Van> vanMaster) {
		this.vanMaster = vanMaster;
	}
}
