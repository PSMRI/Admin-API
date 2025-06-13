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
package com.iemr.admin.controller.vanMaster;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.data.vanType.M_VanType;
import com.iemr.admin.service.vanMaster.VanMasterServiceImpl;
import com.iemr.admin.to.vanMaster.VanMasterTO;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping(value = "/vanMaster")
public class VanMasterController {

	@Autowired
	private VanMasterServiceImpl vanMasterServiceImpl;

	@Operation(summary = "Store van details")
	@RequestMapping(value = "/save/vanDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String saveVanDetails(
			@Param(value = "{\"vanName\":\"string\", \"vehicalNo\":\"string\", \"providerServiceMapID\":\"integer\", "
					+ "\"countryID\":\"integer\", \"stateID\":\"integer\", \"districtID\":\"integer\", "
					+ " \"createdBy\":\"string\", \"deleted\":\"boolean\" ,"
					+ " \"swymedDomain\":\"string\", \"swymedID\":\"string\", \"swymedEmail\":\"string\"}") @RequestBody String vanDetails)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		VanMasterTO vanMaster = InputMapper.gson().fromJson(vanDetails, VanMasterTO.class);

		try {

			ArrayList<M_Van> van = vanMasterServiceImpl.saveVanDetails(vanMaster.getVanMaster());
			output.setResponse(van.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get van details")
	@RequestMapping(value = "/get/vanDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String getServicePoints(
			@Param(value = "{\"stateID\":\"integer\", \"districtID\":\"integer\", \"parkingPlaceID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String vanMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Van vanDetails = InputMapper.gson().fromJson(vanMaster, M_Van.class);

		try {
			ArrayList<M_Van> vans = vanMasterServiceImpl.getAvailableVans(vanDetails.getParkingPlaceID(),
					vanDetails.getVanTypeID(), vanDetails.getProviderServiceMapID());
			output.setResponse(vans.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Remove van")
	@RequestMapping(value = "/remove/vanDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteVanDetails(
			@Param(value = "{\"vanID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String vanMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Van van = InputMapper.gson().fromJson(vanMaster, M_Van.class);

		try {
			String response;
			int update = vanMasterServiceImpl.updateVanStatus(van);
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

	@Operation(summary = "Update van details")
	@RequestMapping(value = "/update/vanDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String updateZoneData(
			@Param(value = "{\"vanID\":\"integer\", \"vanName\":\"string\", \"vehicalNo\":\"string\", \"providerServiceMapID\":\"integer\", "
					+ "\"countryID\":\"integer\", \"stateID\":\"integer\", \"districtID\":\"integer\", "
					+ " \"createdBy\":\"string\", \"deleted\":\"boolean\", "
					+ " \"swymedDomain\":\"string\", \"swymedID\":\"string\", \"swymedEmail\":\"string\"}") @RequestBody String vanDetails)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Van vanMaster = InputMapper.gson().fromJson(vanDetails, M_Van.class);

		try {
			M_Van vanData = vanMasterServiceImpl.getVanByID(vanMaster.getVanID());
			vanData.setStateID(vanMaster.getStateID());
			vanData.setParkingPlaceID(vanMaster.getParkingPlaceID());
			vanData.setVanName(vanMaster.getVanName());
			vanData.setVehicalNo(vanMaster.getVehicalNo());
			vanData.setVanTypeID(vanMaster.getVanTypeID());
			vanData.setModifiedBy(vanMaster.getModifiedBy());
			vanData.setVideoConsultationDomain(vanMaster.getVideoConsultationDomain());
			vanData.setVideoConsultationEmail(vanMaster.getVideoConsultationEmail());
			vanData.setVideoConsultationID(vanMaster.getVideoConsultationID());

			M_Van updatedData = vanMasterServiceImpl.updateVanData(vanData);

			output.setResponse(updatedData.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Store van type details")
	@RequestMapping(value = "/save/vanTypeDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String saveVanTypeDetails(
			@Param(value = "{\"vanType\":\"string\", \"vanTypeDesc\":\"string\", \"providerServiceMapID\":\"integer\"}") @RequestBody String vanDetails)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		VanMasterTO vanMaster = InputMapper.gson().fromJson(vanDetails, VanMasterTO.class);

		try {

			ArrayList<M_VanType> vanType = vanMasterServiceImpl.saveVanTypeDetails(vanMaster.getVanTypeMaster());
			output.setResponse(vanType.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get van types")
	@RequestMapping(value = "/get/vanTypes", headers = "Authorization", method = { RequestMethod.POST })
	public String getVanTypes() {

		OutputResponse output = new OutputResponse();
		try {
			ArrayList<M_VanType> vans = vanMasterServiceImpl.getVanTypes();
			output.setResponse(vans.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Remove van type")
	@RequestMapping(value = "/remove/vanTypeDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteVanType(
			@Param(value = "{\"vanTypeID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String vanMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_VanType vanType = InputMapper.gson().fromJson(vanMaster, M_VanType.class);

		try {
			String response;
			int update = vanMasterServiceImpl.updateVanTypeStatus(vanType);
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

	@Operation(summary = "Get van details")
	@RequestMapping(value = "/getVanMaster", headers = "Authorization", method = { RequestMethod.POST })
	public String getVanMaster(
			@Param(value = "{\"providerServiceMapID\":\"integer\"}") @RequestBody String vanMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Van vanDetails = InputMapper.gson().fromJson(vanMaster, M_Van.class);

		try {
			List<M_Van> vans = vanMasterServiceImpl.getVanMaster(vanDetails.getProviderServiceMapID(),
					vanDetails.getParkingPlaceID());
			output.setResponse(vans.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get van details")
	@RequestMapping(value = "/getVanFromFacilityID", headers = "Authorization", method = { RequestMethod.POST })
	public String getVanFromFacilityID(
			@Param(value = "{\"providerServiceMapID\":\"integer\"}") @RequestBody M_Van vanMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		try {
			List<M_Van> vans = vanMasterServiceImpl.getVanFromFacilityID(vanMaster.getFacilityID());
			output.setResponse(vans.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}
}
