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
package com.iemr.admin.controller.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.data.store.M_facilityMap;
import com.iemr.admin.data.store.V_FetchFacility;
import com.iemr.admin.service.store.StoreService;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class StoreController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private StoreService storeService;

	@Operation(summary = "Create store")
	@RequestMapping(value = "/createStore", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createStore(@RequestBody String store) {

		OutputResponse response = new OutputResponse();
		try {

			String saveData = "Invalid Store Type";

			M_Facility[] mainStore = InputMapper.gson().fromJson(store, M_Facility[].class);
			List<M_Facility> addMainStore = Arrays.asList(mainStore);

			saveData = storeService.addAllMainStore(addMainStore).toString();

			response.setResponse(saveData);

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Edit store")
	@RequestMapping(value = "/editStore", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editStore(@RequestBody String store) {

		OutputResponse response = new OutputResponse();
		try {

			String saveData = "Invalid Store Type";

			M_Facility mainStore = InputMapper.gson().fromJson(store, M_Facility.class);
			M_Facility mainStoreUpdate = storeService.getMainStore(mainStore.getFacilityID());

			mainStoreUpdate.setFacilityDesc(mainStore.getFacilityDesc());

			mainStoreUpdate.setLocation(mainStore.getLocation());
			mainStoreUpdate.setPhysicalLocation(mainStore.getPhysicalLocation());

			mainStoreUpdate.setModifiedBy(mainStore.getModifiedBy());

			saveData = storeService.createMainStore(mainStoreUpdate).toString();

			response.setResponse(saveData);

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Get all store")
	@RequestMapping(value = "/getAllStore/{providerServiceMapID}", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllStore(@PathVariable("providerServiceMapID") Integer providerServiceMapID) {

		OutputResponse response = new OutputResponse();
		try {

			List<M_Facility> saveData = storeService.getAllMainStore(providerServiceMapID);
			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Get main facility")
	@RequestMapping(value = "/getMainFacility", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getMainFacility(@RequestBody String getMainFacility) {

		OutputResponse response = new OutputResponse();
		try {

			M_Facility mainStore = InputMapper.gson().fromJson(getMainFacility, M_Facility.class);
			ArrayList<M_Facility> mainStoreUpdate = storeService.getMainFacility(mainStore.getProviderServiceMapID(),
					mainStore.getIsMainFacility());

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Get sub facility")
	@RequestMapping(value = "/getsubFacility", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getsubFacility(@RequestBody String getMainFacility) {

		OutputResponse response = new OutputResponse();
		try {

			M_Facility mainStore = InputMapper.gson().fromJson(getMainFacility, M_Facility.class);
			ArrayList<M_Facility> mainStoreUpdate = storeService.getMainFacility(mainStore.getProviderServiceMapID(),
					mainStore.getIsMainFacility(), mainStore.getMainFacilityID());

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Delete store")
	@RequestMapping(value = "/deleteStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String deleteStore(@RequestBody M_Facility facility) {

		OutputResponse response = new OutputResponse();

		try {

			M_Facility mainStoreUpdate = storeService.deleteStore(facility);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Map store")
	@RequestMapping(value = "/mapStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String mapStore(@RequestBody List<M_facilityMap> facilitymap) {

		OutputResponse response = new OutputResponse();

		try {

			Integer mainStoreUpdate = storeService.mapStore(facilitymap);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Delete map store")
	@RequestMapping(value = "/deleteMapStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String deleteMapStore(@RequestBody M_facilityMap facilitymap) {

		OutputResponse response = new OutputResponse();

		try {

			Integer mainStoreUpdate = storeService.deleteMapStore(facilitymap);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Get map store")
	@RequestMapping(value = "/getMapStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String getMapStore(@RequestBody V_FetchFacility facilitymap) {

		OutputResponse response = new OutputResponse();

		try {

			List<V_FetchFacility> mainStoreUpdate = storeService.getMapStore(facilitymap);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Check store code")
	@RequestMapping(value = "/checkStoreCode", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String checkStoreCode(@RequestBody String deleteManufacturer) {

		OutputResponse response = new OutputResponse();

		try {

			M_Facility Manufacturer = InputMapper.gson().fromJson(deleteManufacturer, M_Facility.class);

			Boolean saveData = storeService.checkStoreCode(Manufacturer);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}
}
