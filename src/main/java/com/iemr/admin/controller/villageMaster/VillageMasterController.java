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
package com.iemr.admin.controller.villageMaster;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.service.villageMaster.VillageMasterServiceImpl;
import com.iemr.admin.to.villageMaster.VillageMasterTO;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping(value = "/villageMaster")
public class VillageMasterController {

	@Autowired
	private VillageMasterServiceImpl villageMasterServiceImpl;

	@Operation(summary = "Store village details")
	@RequestMapping(value = "/save/VillageDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String saveVillageDetails(
			@Param(value = "{\"blockID\":\"Integer\", \"panchayatName\":\"String\", \"villageName\":\"String\", \"habitat\":\"String\", \"pinCode\":\"String\","
					+ "\"govVillageID\":\"Integer\", \"govSubDistrictID\":\"Integer\", \"deleted\":\"Boolean\", \"blockName\":\"String\", \"createdBy\":\"String\"}") @RequestBody String villageMaster) {

		OutputResponse output = new OutputResponse();

		try {
			VillageMasterTO village = InputMapper.gson().fromJson(villageMaster, VillageMasterTO.class);

			ArrayList<DistrictBranchMapping> villages = villageMasterServiceImpl
					.storeVillageDetails(village.getDistrictBranchMapping());
			output.setResponse(villages.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get village details")
	@RequestMapping(value = "/get/Villages", headers = "Authorization", method = { RequestMethod.POST })
	public String getVillages(@Param(value = "{\"blockID\":\"Integer\"}") @RequestBody String villageMaster) {

		OutputResponse output = new OutputResponse();

		try {
			DistrictBranchMapping village = InputMapper.gson().fromJson(villageMaster, DistrictBranchMapping.class);

			ArrayList<DistrictBranchMapping> villages = villageMasterServiceImpl
					.getAvailableVillages(village.getBlockID());
			output.setResponse(villages.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Remove village")
	@RequestMapping(value = "/remove/village", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteVillage(
			@Param(value = "{\"districtBranchID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String villageData) {

		OutputResponse output = new OutputResponse();

		try {
			DistrictBranchMapping districtBranch = InputMapper.gson().fromJson(villageData,
					DistrictBranchMapping.class);
			String response;
			int update = villageMasterServiceImpl.updateVillageStatus(districtBranch);
			if (update == 1) {
				response = "status updated successfully";
			} else {
				response = "Failed to update the status";
			}
			output.setResponse(response.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Update village details")
	@RequestMapping(value = "/update/villageData", headers = "Authorization", method = { RequestMethod.POST })
	public String updateVillageData(
			@Param(value = "{\"districtBranchID\":\"Integer\",\"blockID\":\"Integer\", \"panchayatName\":\"String\", \"villageName\":\"String\", \"habitat\":\"String\", "
					+ "\"pinCode\":\"String\", \"govVillageID\":\"Integer\", \"govSubDistrictID\":\"Integer\", \"modifiedBy\":\"String\"}") @RequestBody String villageData) {

		OutputResponse output = new OutputResponse();

		try {

			DistrictBranchMapping village = InputMapper.gson().fromJson(villageData, DistrictBranchMapping.class);
			String response = "";

			int update = villageMasterServiceImpl.updateVillageData(village);
			if (update == 1) {
				response = "status updated successfully";
			} else {
				response = "Failed to update the status";
			}
			output.setResponse(response.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

}
