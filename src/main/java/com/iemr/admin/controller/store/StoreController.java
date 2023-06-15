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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.data.store.M_facilityMap;
import com.iemr.admin.data.store.V_FetchFacility;
import com.iemr.admin.service.store.StoreService;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@RestController
public class StoreController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private StoreService storeService;

	@CrossOrigin()
	@RequestMapping(value = "/createStore", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createStore(@RequestBody String store) {

		OutputResponse response = new OutputResponse();
		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,
			// ItemMaster.class);
			String saveData = "Invalid Store Type";

			M_Facility[] mainStore = InputMapper.gson().fromJson(store, M_Facility[].class);
			List<M_Facility> addMainStore = Arrays.asList(mainStore);

			saveData = storeService.addAllMainStore(addMainStore).toString();

			response.setResponse(saveData);

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/editStore", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editStore(@RequestBody String store) {

		OutputResponse response = new OutputResponse();
		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,
			// ItemMaster.class);
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
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/getAllStore/{providerServiceMapID}", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllStore(@PathVariable("providerServiceMapID") Integer providerServiceMapID) {

		OutputResponse response = new OutputResponse();
		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,
			// ItemMaster.class);
			// String saveData = "Invalid Store Type";

			List<M_Facility> saveData = storeService.getAllMainStore(providerServiceMapID);
			response.setResponse(saveData.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();
	}

	@CrossOrigin()
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
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/getsubFacility", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getsubFacility(@RequestBody String getMainFacility) {

		OutputResponse response = new OutputResponse();
		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,
			// ItemMaster.class);

			M_Facility mainStore = InputMapper.gson().fromJson(getMainFacility, M_Facility.class);
			ArrayList<M_Facility> mainStoreUpdate = storeService.getMainFacility(mainStore.getProviderServiceMapID(),
					mainStore.getIsMainFacility(), mainStore.getMainFacilityID());

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();
	}

	@CrossOrigin()

	@RequestMapping(value = "/deleteStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String deleteStore(@RequestBody M_Facility facility) {

		OutputResponse response = new OutputResponse();

		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,

			// ItemMaster.class);

			M_Facility mainStoreUpdate = storeService.deleteStore(facility);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			
			logger.error("Unexpected error:" , e);
//			response.setResponse(e.toString());
			 response.setError(e);

		}

		/**
		 * 
		 * sending the response...
		 * 
		 */

		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/mapStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String mapStore(@RequestBody List<M_facilityMap> facilitymap) {

		OutputResponse response = new OutputResponse();

		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,

			// ItemMaster.class);

			Integer mainStoreUpdate = storeService.mapStore(facilitymap);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			
			logger.error("Unexpected error:" , e);
//			response.setResponse(e.toString());
			 response.setError(e);

		}

		/**
		 * 
		 * sending the response...
		 * 
		 */

		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/deleteMapStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String deleteMapStore(@RequestBody M_facilityMap facilitymap) {

		OutputResponse response = new OutputResponse();

		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,

			// ItemMaster.class);

			Integer mainStoreUpdate = storeService.deleteMapStore(facilitymap);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			
			logger.error("Unexpected error:" , e);
//			response.setResponse(e.toString());
			 response.setError(e);

		}

		/**
		 * 
		 * sending the response...
		 * 
		 */

		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/getMapStore", headers = "Authorization", method = {

			RequestMethod.POST }, produces = { "application/json" })

	public String getMapStore(@RequestBody V_FetchFacility facilitymap) {

		OutputResponse response = new OutputResponse();

		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,

			// ItemMaster.class);

			List<V_FetchFacility> mainStoreUpdate = storeService.getMapStore(facilitymap);

			response.setResponse(mainStoreUpdate.toString());

		} catch (Exception e) {

			
			logger.error("Unexpected error:" , e);
//			response.setResponse(e.toString());
			 response.setError(e);

		}

		/**
		 * 
		 * sending the response...
		 * 
		 */

		return response.toString();

	}
	
	@CrossOrigin()
	@RequestMapping(value =  "/checkStoreCode" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String checkStoreCode(@RequestBody String deleteManufacturer) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_Facility Manufacturer = InputMapper.gson().fromJson(deleteManufacturer,
					M_Facility.class);
		     // List<M_Manufacturer> ManufacturerData = Arrays.asList(Manufacturer);
			
			
			Boolean saveData=storeService.checkStoreCode(Manufacturer);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(saveData.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}
}
