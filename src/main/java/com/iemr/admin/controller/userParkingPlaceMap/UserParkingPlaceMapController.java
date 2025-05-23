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
package com.iemr.admin.controller.userParkingPlaceMap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.userParkingPlaceMap.M_UserParkingPlaceMap;
import com.iemr.admin.data.userParkingPlaceMap.M_UserVanMapping;
import com.iemr.admin.service.userParkingPlaceMap.UserParkingPlaceMapServiceImpl;
import com.iemr.admin.to.parkingPlace.ParkingPlaceTO;
import com.iemr.admin.to.userParkingPlace.UserParkingPlaceTO;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/parkingPlaceMaster")
public class UserParkingPlaceMapController {
	@Autowired
	private UserParkingPlaceMapServiceImpl userParkingPlaceMapServiceImpl;

	@Operation(summary = "Save user parking places")
	@RequestMapping(value = "/save/userParkingPlaces", headers = "Authorization", method = { RequestMethod.POST })
	public String saveuserParkingPlaces(
			@Param(value = "{\"userID\":\"integer\", \"parkingPlaceID\":\"integer\", \"providerServiceMapID\":\"integer\", "
					+ "\"createdBy\":\"string\"}") @RequestBody String userParkingPlaces)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		ParkingPlaceTO parkingPlaceMaster = InputMapper.gson().fromJson(userParkingPlaces, ParkingPlaceTO.class);

		try {
			ArrayList<M_UserParkingPlaceMap> mapsList = new ArrayList<M_UserParkingPlaceMap>();
			ArrayList<M_UserParkingPlaceMap> userMapping = userParkingPlaceMapServiceImpl
					.saveUserParkingPlaceDetails(parkingPlaceMaster.getUserParkingPlaceMaps());

			output.setResponse(userMapping.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get user parking places")
	@RequestMapping(value = "/get/userParkingPlaces", headers = "Authorization", method = { RequestMethod.POST })
	public String getuserParkingPlaces(
			@Param(value = "{\"stateID\":\"integer\", \"districtID\":\"integer\", \"parkingPlaceID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String userParkingPlaces)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_UserParkingPlaceMap parkingPlaceMaster = InputMapper.gson().fromJson(userParkingPlaces,
				M_UserParkingPlaceMap.class);

		try {
			ArrayList<M_UserParkingPlaceMap> vans = userParkingPlaceMapServiceImpl.getUserParkingPlaceMappings(
					parkingPlaceMaster.getServiceProviderID(), parkingPlaceMaster.getStateID(),
					parkingPlaceMaster.getDistrictID(), parkingPlaceMaster.getParkingPlaceID(),
					parkingPlaceMaster.getM_user().getDesignationID());
			output.setResponse(vans.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Get user parking places designation")
	@RequestMapping(value = "/get/userParkingPlaces1", headers = "Authorization", method = { RequestMethod.POST })
	public String getuserParkingPlacesDesiganation(
			@Param(value = "{\"stateID\":\"integer\", \"districtID\":\"integer\", \"parkingPlaceID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String userParkingPlaces)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_UserParkingPlaceMap parkingPlaceMaster = InputMapper.gson().fromJson(userParkingPlaces,
				M_UserParkingPlaceMap.class);

		UserParkingPlaceTO parkingPlaceMaster1 = InputMapper.gson().fromJson(userParkingPlaces,
				UserParkingPlaceTO.class);
		try {
			ArrayList<M_UserParkingPlaceMap> vans = userParkingPlaceMapServiceImpl.getUserParkingPlaceMappings1(
					parkingPlaceMaster.getProviderServiceMapID(), parkingPlaceMaster.getDistrictID(),
					parkingPlaceMaster.getParkingPlaceID(), parkingPlaceMaster1.getDesignationID());
			output.setResponse(vans.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Remove user parking place")
	@RequestMapping(value = "/remove/userParkingPlace", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteuserParkingPlaceDetails(
			@Param(value = "{\"vanServicePointMapID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String userParkingPlaces)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_UserParkingPlaceMap parkingPlaceMaster = InputMapper.gson().fromJson(userParkingPlaces,
				M_UserParkingPlaceMap.class);

		try {
			String response;
			int update = userParkingPlaceMapServiceImpl.updateUserParkingPlaceMapStatus(parkingPlaceMaster);
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

	@Operation(summary = "Edit user parking places")
	@RequestMapping(value = "/edit/userParkingPlaces1", headers = "Authorization", method = { RequestMethod.POST })
	public String edituserParkingPlaces(
			@Param(value = "{\"userID\":\"integer\", \"parkingPlaceID\":\"integer\", \"providerServiceMapID\":\"integer\", "
					+ "\"createdBy\":\"string\"}") @RequestBody String userParkingPlaces)
			throws IEMRException {

		OutputResponse output = new OutputResponse();
		try {
			M_UserParkingPlaceMap parkingPlaceMaster1 = InputMapper.gson().fromJson(userParkingPlaces,
					M_UserParkingPlaceMap.class);
			List<M_UserVanMapping> usrvanmaplist = parkingPlaceMaster1.getUservanmapping();

			M_UserParkingPlaceMap userMapping = userParkingPlaceMapServiceImpl
					.getUserParkingPlaceDetails(parkingPlaceMaster1.getUserParkingPlaceMapID());
			userMapping.setParkingPlaceID(parkingPlaceMaster1.getParkingPlaceID());
			userMapping.setModifiedBy(parkingPlaceMaster1.getModifiedBy());
			userMapping.setProviderServiceMapID(parkingPlaceMaster1.getProviderServiceMapID());
			userMapping.setDistrictID(parkingPlaceMaster1.getDistrictID());
			M_UserParkingPlaceMap data = userParkingPlaceMapServiceImpl.saveediteddata(userMapping, usrvanmaplist);
			output.setResponse(userMapping.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Delete user parking places")
	@RequestMapping(value = "/delete/userParkingPlaces1", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteuserParkingPlaces(
			@Param(value = "{\"userID\":\"integer\", \"parkingPlaceID\":\"integer\", \"providerServiceMapID\":\"integer\", "
					+ "\"createdBy\":\"string\"}") @RequestBody String userParkingPlaces)
			throws IEMRException {

		OutputResponse output = new OutputResponse();
		try {
			M_UserParkingPlaceMap parkingPlaceMaster1 = InputMapper.gson().fromJson(userParkingPlaces,
					M_UserParkingPlaceMap.class);

			M_UserParkingPlaceMap userMapping = userParkingPlaceMapServiceImpl
					.getUserParkingPlaceDetails(parkingPlaceMaster1.getUserParkingPlaceMapID());
			if (!parkingPlaceMaster1.getDeleted()) {
				Boolean value = userParkingPlaceMapServiceImpl.getuserexist(userMapping.getProviderServiceMapID(),
						userMapping.getUserID());
				if (value) {
					throw new Exception("User already mapped. Cannot activate");
				}
			}
			userMapping.setDeleted(parkingPlaceMaster1.getDeleted());
			M_UserParkingPlaceMap data = userParkingPlaceMapServiceImpl.saveediteddata(userMapping);
			data.setUservanmapping(null);
			output.setResponse(userMapping.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Fetch un-mapped users")
	@RequestMapping(value = "/get/unmappeduser", headers = "Authorization", method = { RequestMethod.POST })
	public String unmappeduser(@Param(value = "{ \"providerServiceMapID\":\"integer\","
			+ "\"designationID\":\"integer\"}") @RequestBody String userParkingPlaces) throws IEMRException {

		OutputResponse output = new OutputResponse();
		try {
			M_UserParkingPlaceMap parkingPlaceMaster1 = InputMapper.gson().fromJson(userParkingPlaces,
					M_UserParkingPlaceMap.class);

			List<M_User1> userMapping = userParkingPlaceMapServiceImpl.getunmappedUser(
					parkingPlaceMaster1.getProviderServiceMapID(), parkingPlaceMaster1.getDesignationID());

			output.setResponse(userMapping.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Fetch mapped vans for user PP id")
	@RequestMapping(value = "/get/mappedvan/{userppID}", headers = "Authorization", method = { RequestMethod.POST })
	public String mappedvan(@PathVariable("userppID") Integer userParkingPlacesID) throws IEMRException {

		OutputResponse output = new OutputResponse();
		try {

			List<M_UserVanMapping> userMapping = userParkingPlaceMapServiceImpl.getuservanmapping(userParkingPlacesID);

			output.setResponse(userMapping.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@Operation(summary = "Delete mapped vans for user")
	@RequestMapping(value = "/delete/mappedvan", headers = "Authorization", method = { RequestMethod.POST })
	public String deletemappedvan(@RequestBody String userpvanmapID) throws IEMRException {

		OutputResponse output = new OutputResponse();
		try {
			M_UserVanMapping vanmap = InputMapper.gson().fromJson(userpvanmapID, M_UserVanMapping.class);

			userParkingPlaceMapServiceImpl.deleteuservanmapping(vanmap);

			output.setResponse("Success");
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}
}
