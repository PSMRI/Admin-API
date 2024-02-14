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
package com.iemr.admin.to;

import java.util.List;

import com.iemr.admin.data.provideronboard.M_104druggroup;
import com.iemr.admin.data.provideronboard.M_104drugmapping;
import com.iemr.admin.data.provideronboard.M_104drugmaster;

import jakarta.persistence.Transient;

public class DrugMasterTO {
	


	@Transient
	private List<M_104druggroup> drugGroups;
	
	@Transient
	private List<M_104drugmaster> drugMasters;
	
	@Transient
	private List<M_104drugmapping> drugMappings;
	
	
	public DrugMasterTO() {
		// TODO Auto-generated constructor stub
	}

	public List<M_104druggroup> getDrugGroups() {
		return drugGroups;
	}

	public void setDrugGroups(List<M_104druggroup> drugGroups) {
		this.drugGroups = drugGroups;
	}

	public List<M_104drugmaster> getDrugMasters() {
		return drugMasters;
	}

	public void setDrugMasters(List<M_104drugmaster> drugMasters) {
		this.drugMasters = drugMasters;
	}

	public List<M_104drugmapping> getDrugMappings() {
		return drugMappings;
	}

	public void setDrugMappings(List<M_104drugmapping> drugMappings) {
		this.drugMappings = drugMappings;
	}
	
	

}
