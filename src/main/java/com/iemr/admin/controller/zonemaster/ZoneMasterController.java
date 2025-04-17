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
package com.iemr.admin.controller.zonemaster;

import java.util.ArrayList;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.data.zonemaster.M_ZoneDistrictMap;
import com.iemr.admin.service.zonemaster.ZoneMasterServiceImpl;
import com.iemr.admin.to.zonemaster.ZoneMasterTO;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping(value = "/zonemaster")
public class ZoneMasterController {

	@Autowired
	private ZoneMasterServiceImpl zoneMasterServiceImpl;

	
	@Operation(summary = "Store zone details")
	@RequestMapping(value = "/save/zone", headers = "Authorization", method = { RequestMethod.POST })
	public String saveZone(
			@Param(value = "{\"zoneName\":\"string\", \"zoneDesc\":\"string\", \"providerServiceMapID\":\"integer\", \"zoneHQAddress\":\"string\", \"stateID\":\"string\","
					+ " \"createdBy\":\"string\"}") @RequestBody String zonemaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		ZoneMasterTO zone = InputMapper.gson().fromJson(zonemaster, ZoneMasterTO.class);

		try {

			ArrayList<M_Zone> zones = zoneMasterServiceImpl.createZone(zone.getZones());
			output.setResponse(zones.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Get zone details")
	@RequestMapping(value = "/get/zones", headers = "Authorization", method = { RequestMethod.POST })
	public String getZones(@Param(value = "{\"providerServiceMapID\":\"integer\"}") @RequestBody String requestObj) {

		OutputResponse output = new OutputResponse();
		try {
			JSONObject obj = new JSONObject(requestObj);
			if (obj.has("providerServiceMapID")) {
				if (obj.getInt("providerServiceMapID") > 0) {
					ArrayList<M_Zone> zones = zoneMasterServiceImpl
							.getAvailableZones(obj.getInt("providerServiceMapID"));
					output.setResponse(zones.toString());
				}
			} else {
				output.setResponse("Provide providerServiceMapID.");
			}

		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Map zone with district")
	@RequestMapping(value = "/save/zoneDistrictMapping", headers = "Authorization", method = { RequestMethod.POST })
	public String mapZoneWithDistrict(
			@Param(value = "{\"ZoneID\":\"integer\", \"DistrictID\":\"integer\", \"providerServiceMapID\":\"integer\",  \"createdBy\":\"string\"}") @RequestBody String zonemaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		ZoneMasterTO zone = InputMapper.gson().fromJson(zonemaster, ZoneMasterTO.class);

		try {

			ArrayList<M_ZoneDistrictMap> zones = zoneMasterServiceImpl
					.createZoneDistrictMapping(zone.getZoneDistrictMappings());
			output.setResponse(zones.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Edit zone district")
	@RequestMapping(value = "/edit/zoneDistrictMapping", headers = "Authorization", method = { RequestMethod.POST })
	public String editZoneDistrict(
			@Param(value = "{\"ZoneID\":\"integer\", \"DistrictID\":\"integer\", \"providerServiceMapID\":\"integer\",  \"createdBy\":\"string\"}") @RequestBody String zonemaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_ZoneDistrictMap zone = InputMapper.gson().fromJson(zonemaster, M_ZoneDistrictMap.class);

		try {

			M_ZoneDistrictMap zones = zoneMasterServiceImpl.editZoneDistrictMapping(zone.getZoneDistrictMapID());

			zones.setZoneID(zone.getZoneID());
			zones.setDistrictID(zone.getDistrictID());
			zones.setProviderServiceMapID(zone.getProviderServiceMapID());
			zones.setModifiedBy(zone.getModifiedBy());

			M_ZoneDistrictMap saveData = zoneMasterServiceImpl.saveeditedData(zones);

			output.setResponse(saveData.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Get zone details")
	@RequestMapping(value = "/get/zoneDistrictMappings", headers = "Authorization", method = { RequestMethod.POST })
	public String getZoneDistrictMappings(
			@Param(value = "{\"serviceProviderID\":\"integer\"}") @RequestBody String requestObj) {

		OutputResponse output = new OutputResponse();

		JSONObject obj = new JSONObject(requestObj);
		try {

			if (obj.has("providerServiceMapID")) {
				if (obj.getInt("providerServiceMapID") > 0) {
					ArrayList<M_ZoneDistrictMap> zones = zoneMasterServiceImpl
							.getAvailableZoneDistrictMappings(obj.getInt("providerServiceMapID"));
					output.setResponse(zones.toString());
				}
			} else {
				output.setResponse("Provide serviceProviderID.");
			}

		} catch (Exception e) {
			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Remove zone")
	@RequestMapping(value = "/remove/zone", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteZone(
			@Param(value = "{\"ZoneID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String zoneData)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Zone m_zone = InputMapper.gson().fromJson(zoneData, M_Zone.class);

		try {

			String response;
			int update = zoneMasterServiceImpl.updateZoneStatus(m_zone);
			if (update > 0) {
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

	
	@Operation(summary = "Update zone details")
	@RequestMapping(value = "/update/zoneData", headers = "Authorization", method = { RequestMethod.POST })
	public String updateZoneData(
			@Param(value = "{\"zoneID\":\"integer\", \"zoneName\":\"string\", \"zoneDesc\":\"string\", \"stateID\":\"string\", \"zoneHQAddress\":\"string\","
					+ " \"modifiedBy\":\"string\"}") @RequestBody String zoneData)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Zone m_zone = InputMapper.gson().fromJson(zoneData, M_Zone.class);

		try {
			String response;
			M_Zone zoneToUpdate = zoneMasterServiceImpl.getzoneByID(m_zone.getZoneID());

			zoneToUpdate.setZoneName(m_zone.getZoneName());
			zoneToUpdate.setZoneDesc(m_zone.getZoneDesc());
			zoneToUpdate.setZoneHQAddress(m_zone.getZoneHQAddress());
			zoneToUpdate.setStateID(m_zone.getStateID());
			zoneToUpdate.setDistrictID(m_zone.getDistrictID());
			zoneToUpdate.setDistrictBlockID(m_zone.getDistrictBlockID());
			zoneToUpdate.setDistrictBranchID(m_zone.getDistrictBranchID());
			zoneToUpdate.setModifiedBy(m_zone.getModifiedBy());
			M_Zone m_zoneData = zoneMasterServiceImpl.updateZoneData(zoneToUpdate);

			output.setResponse(m_zoneData.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Zone district mapping")

	@RequestMapping(value = "/getdistrictMappedtoZone", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })

	public String getMappedDistrictByZoneID(@RequestBody String zoneMappedDistrict) {

		OutputResponse output = new OutputResponse();

		M_ZoneDistrictMap zone = InputMapper.gson().fromJson(zoneMappedDistrict, M_ZoneDistrictMap.class);

		try {

			ArrayList<M_ZoneDistrictMap> zone1 = zoneMasterServiceImpl.editZoneDistrictMapping1(zone.getZoneID());

			output.setResponse(zone1.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();

	}

	
	@Operation(summary = "Remove zone district mapping")
	@RequestMapping(value = "/remove/zoneDistrictMapping", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteZoneDistrictMapping(
			@Param(value = "{\"zoneDistrictMapID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String zoneData)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_ZoneDistrictMap m_zoneDistrictMap = InputMapper.gson().fromJson(zoneData, M_ZoneDistrictMap.class);

		try {
			String response;
			int update = zoneMasterServiceImpl.updateZoneDistrictMappingStatus(m_zoneDistrictMap);
			if (update == 0) {
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
