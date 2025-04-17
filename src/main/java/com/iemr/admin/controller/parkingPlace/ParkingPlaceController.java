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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.service.parkingPlace.ParkingPlaceServiceImpl;
import com.iemr.admin.to.parkingPlace.ParkingPlaceTO;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping(value = "/parkingPlaceMaster")
public class ParkingPlaceController {

	@Autowired
	private ParkingPlaceServiceImpl parkingPlaceServiceImpl;

	
	@Operation(summary = "Stores parking place details")
	@RequestMapping(value = "/create/parkingPlaces", headers = "Authorization", method = { RequestMethod.POST })
	public String saveParkingPlace(
			@Param(value = "{\"parkingPlaceName\":\"string\", \"parkingPlaceDesc\":\"string\", \"providerServiceMapID\":\"integer\", \"areaHQAddress\":\"string\", "
					+ "\"countryID\":\"integer\", \"stateID\":\"integer\", \"districtID\":\"integer\", \"districtBlockID\":\"integer\", \"districtBranchID\":\"integer\", "
					+ " \"createdBy\":\"string\", \"deleted\":\"boolean\"}") @RequestBody String parkingPlaceMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		ParkingPlaceTO parkingPlace = InputMapper.gson().fromJson(parkingPlaceMaster, ParkingPlaceTO.class);

		try {

			ArrayList<M_Parkingplace> parkingPlaces = parkingPlaceServiceImpl
					.saveParkingPlace(parkingPlace.getParkingPlaces());
			output.setResponse(parkingPlaces.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Get parking place details")
	@RequestMapping(value = "/get/parkingPlaces", headers = "Authorization", method = { RequestMethod.POST })
	public String getParkingPlaces(
			@Param(value = "{\"stateID\":\"integer\", \"districtID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String parkingPlaceMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Parkingplace parkingPlace = InputMapper.gson().fromJson(parkingPlaceMaster, M_Parkingplace.class);

		try {
			ArrayList<M_Parkingplace> parkingPlaces = parkingPlaceServiceImpl.getAvailableParkingPlaces(
					parkingPlace.getStateID(), parkingPlace.getDistrictID(), parkingPlace.getServiceProviderID());
			output.setResponse(parkingPlaces.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Removes parking place")
	@RequestMapping(value = "/remove/parkingPlace", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteParkingPlace(
			@Param(value = "{\"parkingPlaceID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String parkingPlaceMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Parkingplace parkingPlace = InputMapper.gson().fromJson(parkingPlaceMaster, M_Parkingplace.class);

		try {
			String response;
			int update = parkingPlaceServiceImpl.updateParkingPlaceStatus(parkingPlace);
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

	
	@Operation(summary = "Update parking place details")
	@RequestMapping(value = "/update/parkingPlaceDetails", headers = "Authorization", method = { RequestMethod.POST })
	public String updateParkingPlaceDetails(
			@Param(value = "{\"parkingPlaceName\":\"string\", \"parkingPlaceDesc\":\"string\", \"providerServiceMapID\":\"integer\", \"areaHQAddress\":\"string\", "
					+ "\"countryID\":\"integer\", \"stateID\":\"integer\", \"districtID\":\"integer\", \"districtBlockID\":\"integer\", \"districtBranchID\":\"integer\","
					+ " \"modifiedBy\":\"string\"}") @RequestBody String parkingPlaceData)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Parkingplace m_parkingplace = InputMapper.gson().fromJson(parkingPlaceData, M_Parkingplace.class);

		try {
			String response;
			M_Parkingplace parkingPlaceToUpdate = parkingPlaceServiceImpl
					.getParkingPlaceByID(m_parkingplace.getParkingPlaceID());

			parkingPlaceToUpdate.setParkingPlaceName(m_parkingplace.getParkingPlaceName());
			parkingPlaceToUpdate.setParkingPlaceDesc(m_parkingplace.getParkingPlaceDesc());
			parkingPlaceToUpdate.setAreaHQAddress(m_parkingplace.getAreaHQAddress());
			parkingPlaceToUpdate.setStateID(m_parkingplace.getStateID());
			parkingPlaceToUpdate.setDistrictID(m_parkingplace.getDistrictID());
			parkingPlaceToUpdate.setZoneID(m_parkingplace.getZoneID());
			parkingPlaceToUpdate.setDistrictBlockID(m_parkingplace.getDistrictBlockID());
			parkingPlaceToUpdate.setDistrictBranchID(m_parkingplace.getDistrictBranchID());
			parkingPlaceToUpdate.setModifiedBy(m_parkingplace.getModifiedBy());

			M_Parkingplace m_parkingplaceData = parkingPlaceServiceImpl.updateParkingPlaceData(parkingPlaceToUpdate);

			output.setResponse(m_parkingplaceData.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Get parking place details")
	@RequestMapping(value = "/getParkingPlaces", headers = "Authorization", method = { RequestMethod.POST })
	public String getParkingPlacesProviderserviceMap(
			@Param(value = "{ \"providerServiceMapID\":\"integer\"}") @RequestBody String parkingPlaceMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Parkingplace parkingPlace = InputMapper.gson().fromJson(parkingPlaceMaster, M_Parkingplace.class);

		try {
			List<M_Parkingplace> parkingPlaces = parkingPlaceServiceImpl
					.getParkingPlaces(parkingPlace.getProviderServiceMapID());
			output.setResponse(parkingPlaces.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Get sub district details")
	@RequestMapping(value = "/getSubDistrictByParkingPlaceID", headers = "Authorization", method = {
			RequestMethod.POST })
	public String getSubDistrictDetailsByParkingPlaceID(
			@Param(value = "{ \"ParkingPlaceId\":\"integer\"}") @RequestBody String SubDistrictByParkingPlaceID)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Parkingplace parkingPlace = InputMapper.gson().fromJson(SubDistrictByParkingPlaceID, M_Parkingplace.class);

		try {
			List<M_Parkingplace> parkingPlaces = parkingPlaceServiceImpl
					.getSubDistrict(parkingPlace.getParkingPlaceID());
			output.setResponse(parkingPlaces.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Get parking place details by zone id")
	@RequestMapping(value = "/get/parkingPlacesbyzoneid", headers = "Authorization", method = { RequestMethod.POST })
	public String getparkingPlacesbyzoneid(
			@Param(value = "{\"zoneID\":\"integer\", \"providerServiceMapID\":\"integer\"}") @RequestBody String parkingPlaceMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Parkingplace parkingPlace = InputMapper.gson().fromJson(parkingPlaceMaster, M_Parkingplace.class);

		try {
			ArrayList<M_Parkingplace> parkingPlaces = parkingPlaceServiceImpl.getAvailableParkingPlacesbyZoneID(
					parkingPlace.getZoneID(), parkingPlace.getProviderServiceMapID());
			output.setResponse(parkingPlaces.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}
}
