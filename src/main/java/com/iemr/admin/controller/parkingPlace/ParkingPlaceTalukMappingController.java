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
package com.iemr.admin.controller.parkingPlace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMappingTO;
import com.iemr.admin.service.parkingPlace.ParkingPlaceTalukMappingServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping(value = "/parkingPlaceTalukMapping")
public class ParkingPlaceTalukMappingController {
	@Autowired
	private ParkingPlaceTalukMappingServiceImpl parkingPlaceTalukMappingServiceImpl;

	@Operation(summary = "Stores parking place and taluk mapping details")
	@RequestMapping(value = "/create/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String parkingPlacesTalukMapping(
			@Param(value = "{\"parkingPlaceName\":\"string\", \"parkingPlaceDesc\":\"string\", \"providerServiceMapID\":\"integer\", \"areaHQAddress\":\"string\", "
					+ "\"countryID\":\"integer\", \"stateID\":\"integer\", \"districtID\":\"integer\", \"districtBlockID\":\"integer\", \"districtBranchID\":\"integer\", "
					+ " \"createdBy\":\"string\", \"deleted\":\"boolean\"}") @RequestBody List<ParkingplaceTalukMapping> parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {

			parkingPlace = parkingPlaceTalukMappingServiceImpl.saveParkingPlaceTalukMapping(parkingPlace);
			output.setResponse(parkingPlace.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Update parking place and taluk mapping details")
	@RequestMapping(value = "/update/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String updateparkingPlacesTalukMapping(
			@Param(value = "{\"ppSubDistrictMapID\": integer,\"parkingPlaceID\":integer,\"districtBlockID\":integer,\"districtID\":integer,\"providerServiceMapID\":integer,\"createdBy\":string}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
			if (parkingPlace.getPpSubDistrictMapID() != null) {
				ParkingplaceTalukMapping parkingPlacetalukmap = parkingPlaceTalukMappingServiceImpl
						.findbyID(parkingPlace.getPpSubDistrictMapID());
				parkingPlacetalukmap.setParkingPlaceID(parkingPlace.getParkingPlaceID());
				parkingPlacetalukmap.setDistrictBlockID(parkingPlace.getDistrictBlockID());
				parkingPlacetalukmap.setDistrictID(parkingPlace.getDistrictID());
				parkingPlacetalukmap.setProviderServiceMapID(parkingPlace.getProviderServiceMapID());
				parkingPlacetalukmap.setModifiedBy(parkingPlace.getCreatedBy());
				parkingPlace = parkingPlaceTalukMappingServiceImpl.updateParkingPlaceTalukMapping(parkingPlacetalukmap);
			}

			output.setResponse(parkingPlace.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get parking place and taluk mapping by map id")
	@RequestMapping(value = "/getbyid/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getparkingPlacesTalukMapping(
			@Param(value = "{\"ppSubDistrictMapID\":\"integer\"}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
			if (parkingPlace.getPpSubDistrictMapID() != null) {
				parkingPlace = parkingPlaceTalukMappingServiceImpl.findbyID(parkingPlace.getPpSubDistrictMapID());

			}

			output.setResponse(parkingPlace.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get all parking place and taluk mapping based on parking place id")
	@RequestMapping(value = "/getall/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getallparkingPlacesTalukMapping(
			@Param(value = "{\"parkingPlaceID\":\"string\"}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
			List<ParkingplaceTalukMappingTO> parkingPlacelist = parkingPlaceTalukMappingServiceImpl
					.findbyProviderservicemapid(parkingPlace);

			output.setResponse(parkingPlacelist.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get all parking place and taluk mapping based on parking place id")
	@RequestMapping(value = "/getbyppidanddid/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getafilterparkingPlacesTalukMapping(
			@Param(value = "{\"parkingPlaceID\":\"integer\",\"districtID\":\"integer\"}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
			List<ParkingplaceTalukMappingTO> parkingPlacelist = parkingPlaceTalukMappingServiceImpl
					.findbyParkingplaceAndDistrictID(parkingPlace);

			output.setResponse(parkingPlacelist.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Activate/deactivate parking place and taluk mapping ")
	@RequestMapping(value = "/activate/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String activateparkingPlacesTalukMapping(
			@Param(value = "{\"ppSubDistrictMapID\":integer,\"deleted\":boolean}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
			if (parkingPlace.getPpSubDistrictMapID() != null) {
				ParkingplaceTalukMapping parkingPlacetalukmap = parkingPlaceTalukMappingServiceImpl
						.findbyID(parkingPlace.getPpSubDistrictMapID());
				parkingPlacetalukmap.setDeleted(parkingPlace.getDeleted());
				parkingPlace = parkingPlaceTalukMappingServiceImpl.updateParkingPlaceTalukMapping(parkingPlacetalukmap);

			}

			output.setResponse(parkingPlace.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get unmapped taluk by district id")
	@RequestMapping(value = "/get/unmappedtaluk", headers = "Authorization", method = { RequestMethod.POST })
	public String getunmappedtaluk(
			@Param(value = "{\"districtID\":integer,\"providerServiceMapID\":integer}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
			List<DistrictBlock> parkingPlacetalukmap = parkingPlaceTalukMappingServiceImpl
					.getunmappedtaluk(parkingPlace.getDistrictID(), parkingPlace.getProviderServiceMapID());

			output.setResponse(parkingPlacetalukmap.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

}
