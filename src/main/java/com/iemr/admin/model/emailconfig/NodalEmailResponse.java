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
package com.iemr.admin.model.emailconfig;

import java.sql.Timestamp;

import com.iemr.admin.model.location.DistrictBlockModelResponse;
import com.iemr.admin.model.location.DistrictBranchMappingModelResponse;
import com.iemr.admin.model.location.DistrictModelResponse;
import com.iemr.admin.model.location.StateModelResponse;
import com.iemr.admin.model.provider.ProviderServiceMapModelResponse;
import com.iemr.admin.model.user.DesignationModelResponse;

import lombok.Data;

@Data
public class NodalEmailResponse {
	
	private Integer authorityEmailID;
	private Integer stateID;
	private StateModelResponse state;
	private Integer districtID;
	private DistrictModelResponse district;
	private Integer blockID;
	private DistrictBlockModelResponse districtBlock;
	private Integer districtBranchMappingID;
	private DistrictBranchMappingModelResponse districtBranch;
	private Integer designationID;
	private DesignationModelResponse designation;
	private String authorityName;
	private String emailID;
	private String contactNo;
	private Integer providerServiceMapID;
	private ProviderServiceMapModelResponse providerService;
	private String mobileNo;
	private String createdBy;
	private Timestamp createdDate;
	private String modifiedBy;
	private Timestamp lastModDate;
	private Boolean deleted;
	

}
