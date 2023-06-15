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
package com.iemr.admin.controller.itemfacilitymapping;

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

import com.iemr.admin.data.itemfacilitymapping.M_itemfacilitymapping;
import com.iemr.admin.data.itemfacilitymapping.V_fetchItemFacilityMap;
import com.iemr.admin.data.items.ItemInStore;
import com.iemr.admin.service.itemfacilitymapping.M_itemfacilitymappingInter;
import com.iemr.admin.to.itemfacilitymapping.ItemfacilityMappingTO;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@RestController
public class M_itemfacilitymappingController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private M_itemfacilitymappingInter M_itemfacilitymappingInter;

	@CrossOrigin()
	@RequestMapping(value = "/mapItemtoStrore", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String mapItemtoStrore(@RequestBody String mapItemtoStrore) {
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try {

			M_itemfacilitymapping[] itemDetails1 = InputMapper.gson().fromJson(mapItemtoStrore,
					M_itemfacilitymapping[].class);

			List<M_itemfacilitymapping> itemDetails = Arrays.asList(itemDetails1);

			ItemfacilityMappingTO[] itemDetailsTo = InputMapper.gson().fromJson(mapItemtoStrore,
					ItemfacilityMappingTO[].class);

			List<ItemfacilityMappingTO> itemdeatails2 = Arrays.asList(itemDetailsTo);

			M_itemfacilitymapping resDataMap = null;
			List<M_itemfacilitymapping> resList = new ArrayList<M_itemfacilitymapping>();

			int itemDetailsIndex = 0;
			Integer[] itemID = null;
			for (ItemfacilityMappingTO blockingto : itemdeatails2) {
				itemID = blockingto.getItemID1();
				M_itemfacilitymapping mappingDetails = itemDetails.get(itemDetailsIndex);

				for (int i = 0; i < itemID.length; i++) {
					resDataMap = new M_itemfacilitymapping();

					resDataMap.setFacilityID(mappingDetails.getFacilityID());
					resDataMap.setItemID(itemID[i]);
					resDataMap.setMappingType(mappingDetails.getMappingType());
					resDataMap.setProviderServiceMapID(mappingDetails.getProviderServiceMapID());
					resDataMap.setStatus(mappingDetails.getStatus());
					resDataMap.setCreatedBy(mappingDetails.getCreatedBy());

					resList.add(resDataMap);
				}
				// itemDetailsIndex++;

			}

			ArrayList<M_itemfacilitymapping> data = M_itemfacilitymappingInter.mapItemtoStore(resList);

			response.setResponse(data.toString());

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
	@RequestMapping(value = "/editItemtoStrore", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editItemtoStrore(@RequestBody String editItemtoStrore) {
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try {

			M_itemfacilitymapping itemDetails = InputMapper.gson().fromJson(editItemtoStrore,
					M_itemfacilitymapping.class);

			M_itemfacilitymapping getdataforedit = M_itemfacilitymappingInter.editdata(itemDetails.getItemStoreMapID());

			getdataforedit.setFacilityID(itemDetails.getFacilityID());
			getdataforedit.setItemID(itemDetails.getItemID());
			getdataforedit.setMappingType(itemDetails.getMappingType());
			getdataforedit.setProviderServiceMapID(itemDetails.getProviderServiceMapID());
			getdataforedit.setStatus(itemDetails.getStatus());

			M_itemfacilitymapping data = M_itemfacilitymappingInter.saveEditedItem(getdataforedit);

			response.setResponse(data.toString());

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
	@RequestMapping(value = "/deleteItemtoStrore", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteItemtoStrore(@RequestBody String deleteItemtoStrore) {
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try {

			M_itemfacilitymapping itemDetails = InputMapper.gson().fromJson(deleteItemtoStrore,
					M_itemfacilitymapping.class);

			M_itemfacilitymapping getdataforedit = M_itemfacilitymappingInter.editdata(itemDetails.getItemStoreMapID());

			getdataforedit.setDeleted(itemDetails.getDeleted());

			M_itemfacilitymapping data = M_itemfacilitymappingInter.saveEditedItem(getdataforedit);

			response.setResponse(data.toString());

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
	@RequestMapping(value = "/getSubStoreitem", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getSubStroreitem(@RequestBody String deleteItemtoStrore) {
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try {

			M_itemfacilitymapping itemDetails = InputMapper.gson().fromJson(deleteItemtoStrore,
					M_itemfacilitymapping.class);

			ArrayList<M_itemfacilitymapping> getsubstoreData = M_itemfacilitymappingInter
					.getsubitemforsubStote(itemDetails.getProviderServiceMapID(), itemDetails.getFacilityID());

			// getdataforedit.setDeleted(itemDetails.getDeleted());

			/// M_itemfacilitymapping data =
			/// M_itemfacilitymappingInter.saveEditedItem(getdataforedit);

			response.setResponse(getsubstoreData.toString());

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
	@RequestMapping(value = "/getAllFacilityMappedData", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllFacilityMappedData(@RequestBody String getAllFacilityMappedData) {
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try {

			V_fetchItemFacilityMap itemDetails = InputMapper.gson().fromJson(getAllFacilityMappedData,
					V_fetchItemFacilityMap.class);

			ArrayList<V_fetchItemFacilityMap> getAllMappedData = M_itemfacilitymappingInter
					.getAllFacilityMappedData(itemDetails.getProviderServiceMapID());

			// getdataforedit.setDeleted(itemDetails.getDeleted());

			/// M_itemfacilitymapping data =
			/// M_itemfacilitymappingInter.saveEditedItem(getdataforedit);

			response.setResponse(getAllMappedData.toString());

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
	@RequestMapping(value = "/getItemFromStoreID/{storeID}", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getItemFromStoreID(@PathVariable("storeID") Integer storeID) {

		OutputResponse response = new OutputResponse();

		try {

			List<ItemInStore> getData = M_itemfacilitymappingInter.getItemMastersFromStoreID(storeID);

			response.setResponse(getData.toString());

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
	@RequestMapping(value = "/deleteItemStoreMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteItemStoreMapping(@RequestBody M_itemfacilitymapping storeID) {

		OutputResponse response = new OutputResponse();

		try {

			Integer getData = M_itemfacilitymappingInter.deleteItemStoreMapping(storeID);

			response.setResponse(getData.toString());

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
