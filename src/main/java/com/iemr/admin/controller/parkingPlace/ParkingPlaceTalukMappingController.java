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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMappingTO;
import com.iemr.admin.service.parkingPlace.ParkingPlaceTalukMappingServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/parkingPlaceTalukMapping")
public class ParkingPlaceTalukMappingController {
	@Autowired
	private ParkingPlaceTalukMappingServiceImpl parkingPlaceTalukMappingServiceImpl;

	@CrossOrigin()
	@ApiOperation(value = "Stores parking place and taluk mapping Details", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/create/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String parkingPlacesTalukMapping(
			@ApiParam(value = "{\"parkingPlaceName\":\"string\", \"parkingPlaceDesc\":\"string\", \"providerServiceMapID\":\"integer\", \"areaHQAddress\":\"string\", "
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

	@CrossOrigin()
	@ApiOperation(value = "update parking place and taluk mapping Details", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/update/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String updateparkingPlacesTalukMapping(
			@ApiParam(value = "{\"ppSubDistrictMapID\": integer,\"parkingPlaceID\":integer,\"districtBlockID\":integer,\"districtID\":integer,\"providerServiceMapID\":integer,\"createdBy\":string}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

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

	@CrossOrigin()
	@ApiOperation(value = "Get parking place and taluk mapping by map id", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/getbyid/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getparkingPlacesTalukMapping(
			@ApiParam(value = "{\"ppSubDistrictMapID\":\"integer\"}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

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

	@CrossOrigin()
	@ApiOperation(value = "Get All parking place and taluk mapping based on parking place ID", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/getall/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getallparkingPlacesTalukMapping(
			@ApiParam(value = "{\"parkingPlaceID\":\"string\"}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

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

	
	
	@CrossOrigin()
	@ApiOperation(value = "Get All parking place and taluk mapping based on parking place ID", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/getbyppidanddid/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getafilterparkingPlacesTalukMapping(
			@ApiParam(value = "{\"parkingPlaceID\":\"integer\",\"districtID\":\"integer\"}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
			List<ParkingplaceTalukMappingTO> parkingPlacelist = parkingPlaceTalukMappingServiceImpl.findbyParkingplaceAndDistrictID(parkingPlace);

			output.setResponse(parkingPlacelist.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}
	@CrossOrigin()
	@ApiOperation(value = "Activate/deactivate parking place and taluk mapping ", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/activate/parkingPlacesTalukMapping", headers = "Authorization", method = {
			RequestMethod.POST })
	public String activateparkingPlacesTalukMapping(
			@ApiParam(value = "{\"ppSubDistrictMapID\":integer,\"deleted\":boolean}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

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
	
	@CrossOrigin()
	@ApiOperation(value = "get unmapped taluk by district ID ", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = "/get/unmappedtaluk", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getunmappedtaluk(
			@ApiParam(value = "{\"districtID\":integer,\"providerServiceMapID\":integer}") @RequestBody ParkingplaceTalukMapping parkingPlace) {

		OutputResponse output = new OutputResponse();

		try {
				List<DistrictBlock> parkingPlacetalukmap = parkingPlaceTalukMappingServiceImpl
						.getunmappedtaluk(parkingPlace.getDistrictID(),parkingPlace.getProviderServiceMapID());
				


			output.setResponse(parkingPlacetalukmap.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

}
