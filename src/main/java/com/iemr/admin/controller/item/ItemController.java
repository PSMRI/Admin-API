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
package com.iemr.admin.controller.item;

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

import com.iemr.admin.data.items.CodeChecker;
import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.items.M_ItemCategory;
import com.iemr.admin.data.items.M_ItemForm;
import com.iemr.admin.data.items.M_Route;
import com.iemr.admin.service.item.ItemService;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@RestController
public class ItemController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private ItemService itemService;

	@CrossOrigin()
	@RequestMapping(value = "/getItemForm/{providerservicemapID}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String getItemForm(@PathVariable("providerservicemapID") Integer providerservicemapID) {

		OutputResponse response = new OutputResponse();
		try {
			

			// M_ItemCategory item = InputMapper.gson().fromJson(itemCategory,
			// M_ItemCategory.class);

			List<M_ItemForm> saveData = itemService.getItemFormProviderServiceMapID(providerservicemapID);

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
	@RequestMapping(value = "/getItemRoute/{providerservicemapID}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String getItemRoute(@PathVariable("providerservicemapID") Integer providerservicemapID) {

		OutputResponse response = new OutputResponse();
		try {

			// M_ItemCategory item = InputMapper.gson().fromJson(itemCategory,
			// M_ItemCategory.class);

			List<M_Route> saveData = itemService.getItemRouteProviderServiceMapID(providerservicemapID);

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
	@RequestMapping(value = "/getItemCategory/{providerservicemapID}/{bool}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String getItemCategory(@PathVariable("providerservicemapID") Integer providerServicemapID,
			@PathVariable("bool") Integer bool) {

		OutputResponse response = new OutputResponse();
		try {

			// M_ItemCategory item = InputMapper.gson().fromJson(itemCategory,
			// M_ItemCategory.class);

			List<M_ItemCategory> saveData;
			if (bool == 0) {
				saveData = itemService.getItemCategory(true, providerServicemapID);
			} else {
				saveData = itemService.getItemCategory(false, providerServicemapID);
			}

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
	@RequestMapping(value = "/createItemMaster", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createItemMaster(@RequestBody ItemMaster[] item) {

		OutputResponse response = new OutputResponse();
		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,
			// ItemMaster.class);

			List<ItemMaster> itemList = Arrays.asList(item);
			List<ItemMaster> saveData = itemService.addAllItemMaster(itemList);

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
	@RequestMapping(value = "/getItemMaster/{providerservicemapID}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String getItemMaster(@PathVariable("providerservicemapID") Integer providerServicemapID) {

		OutputResponse response = new OutputResponse();
		try {

			// TODO: bool case(0): Show All case(1): Show Non Blocked item
			// case(2): Show Non Blocked and Non Discontinued item

			// M_ItemCategory item = InputMapper.gson().fromJson(itemCategory,
			// M_ItemCategory.class);

			List<ItemMaster> saveData = itemService.getItemMaster(providerServicemapID);

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
	@RequestMapping(value = "/blockItemMaster/{itemmasterid}/{deleteflag}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String blockItemMaster(@PathVariable("itemmasterid") Integer itemmasterID,
			@PathVariable("deleteflag") Boolean delete) {

		OutputResponse response = new OutputResponse();
		try {

			Integer update = itemService.blockItemMaster(itemmasterID, delete);

			response.setResponse(update.toString());

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
	@RequestMapping(value = "/discontinueItemMaster/{itemmasterid}/{deleteflag}", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String discontinueItemMaster(@PathVariable("itemmasterid") Integer itemmasterID,
			@PathVariable("deleteflag") Boolean delete) {

		OutputResponse response = new OutputResponse();
		try {

			Integer update = itemService.discontinueItemMaster(itemmasterID, delete);

			response.setResponse(update.toString());

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
	@RequestMapping(value = "/editItemMaster", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editItemMaster(@RequestBody ItemMaster item) {

		OutputResponse response = new OutputResponse();
		try {

			ItemMaster saveData = itemService.getItemMasterByID(item.getItemID());
			saveData.setIsMedical(item.getIsMedical());
			saveData.setItemCategoryID(item.getItemCategoryID());
			saveData.setPharmacologyCategoryID(item.getPharmacologyCategoryID());
			saveData.setManufacturerID(item.getManufacturerID());
			saveData.setIsScheduledDrug(item.getIsScheduledDrug());
			
			
			saveData.setItemDesc(item.getItemDesc());
			saveData.setSctCode(item.getSctCode());
			saveData.setSctTerm(item.getSctTerm());
			saveData.setModifiedBy(item.getModifiedBy());
			saveData = itemService.createItemMaster(saveData);
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
	@RequestMapping(value = "/configItemIssue", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String configItemIssue(@RequestBody M_ItemCategory[] itemIssue) {

		OutputResponse response = new OutputResponse();
		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,
			// ItemMaster.class);

			List<M_ItemCategory> itemList = Arrays.asList(itemIssue);
			Integer saveData = itemService.updateItemIssueConfig(itemList);

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
	@RequestMapping(value = "/configexpiryalert", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String configexpiryalert(@RequestBody M_ItemCategory[] itemIssue) {

		OutputResponse response = new OutputResponse();
		try {

			// ItemMaster item = InputMapper.gson().fromJson(itemMaster,
			// ItemMaster.class);

			List<M_ItemCategory> itemList = Arrays.asList(itemIssue);
			Integer saveData = itemService.updateExpiryAlert(itemList);

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
	@RequestMapping(value = "/getItem", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getItem(@RequestBody String getItem) {

		OutputResponse response = new OutputResponse();

		try {

			ItemMaster item = InputMapper.gson().fromJson(getItem, ItemMaster.class);

			List<ItemMaster> getData = itemService.getItemMasters(item.getProviderServiceMapID(),
					item.getItemCategoryID());

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
	@RequestMapping(value = "/createItemCategories", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createItemCategories(@RequestBody M_ItemCategory[] item) {

		OutputResponse response = new OutputResponse();
		try {
			List<M_ItemCategory> itemCategories = Arrays.asList(item);
			Integer categoryID = itemService.createItemCategories(itemCategories);
			if (null != categoryID && categoryID > 0)
				response.setResponse("Item Categories saved successfully");
			else
				response.setResponse("Failed to store Item Categories");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/editItemCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editItemCategory(@RequestBody M_ItemCategory item) {

		OutputResponse response = new OutputResponse();
		try {
			Integer res = itemService.editItemCategory(item);
			if (null != res && res > 0)
				response.setResponse("Item Category updated successfully");
			else
				response.setResponse("Failed to update Item Category");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/blockItemCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String blockItemCategory(@RequestBody M_ItemCategory itemCat) {

		OutputResponse response = new OutputResponse();
		try {
			Integer res = itemService.blockItemCategory(itemCat);
			if (null != res && res > 0)
				response.setResponse("Item Category blocked successfully");
			else
				response.setResponse("Failed to block Item Category");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/createItemForms", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createItemForms(@RequestBody M_ItemForm[] item) {

		OutputResponse response = new OutputResponse();
		try {
			List<M_ItemForm> itemForms = Arrays.asList(item);
			Integer formID = itemService.createItemForms(itemForms);
			if (null != formID && formID > 0)
				response.setResponse("Item Forms saved successfully");
			else
				response.setResponse("Failed to store Item Forms");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/editItemForm", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editItemForm(@RequestBody M_ItemForm item) {

		OutputResponse response = new OutputResponse();
		try {
			Integer res = itemService.editItemForm(item);
			if (null != res && res > 0)
				response.setResponse("Item Form updated successfully");
			else
				response.setResponse("Failed to update Item Form");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/blockItemForm", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String blockItemForm(@RequestBody M_ItemForm itemForm) {

		OutputResponse response = new OutputResponse();
		try {
			Integer res = itemService.blockItemForm(itemForm);
			if (null != res && res > 0)
				response.setResponse("Item Form blocked successfully");
			else
				response.setResponse("Failed to block Item Form");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/createRoutes", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createRoutes(@RequestBody M_Route[] routes) {

		OutputResponse response = new OutputResponse();
		try {
			List<M_Route> routesList = Arrays.asList(routes);
			Integer routeID = itemService.createRoutes(routesList);
			if (null != routeID && routeID > 0)
				response.setResponse("Routes saved successfully");
			else
				response.setResponse("Failed to store Routes");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/editRoute", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editRoute(@RequestBody M_Route route) {

		OutputResponse response = new OutputResponse();
		try {
			Integer res = itemService.editRoute(route);
			if (null != res && res > 0)
				response.setResponse("Route data updated successfully");
			else
				response.setResponse("Failed to update Route data ");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/blockRoute", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String blockRoute(@RequestBody M_Route route) {

		OutputResponse response = new OutputResponse();
		try {
			Integer res = itemService.blockRoute(route);
			if (null != res && res > 0)
				response.setResponse("Route blocked successfully");
			else
				response.setResponse("Failed to block Route");
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/checkCode", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String blockRoute(@RequestBody CodeChecker value) {

		OutputResponse response = new OutputResponse();
		try {
			Boolean data = null;
			if (value.getName() != null && value.getCode() != null && value.getProviderServiceMapID() != null
					&& value.getProviderServiceMapID() > 0) {
				switch (value.getName().toLowerCase()) {
				case "itemcategory":
					data = itemService.checkCodeCategory(value);
					break;
				case "itemform":
					data = itemService.checkCodeForm(value);
					break;
				case "itemmaster":
					data = itemService.checkCodeItem(value);
					break;
				case "route":
					data = itemService.checkCodeRoute(value);
					break;

				default:
					break;
				}
				if (data == null) {
//					response.setResponse();
					throw new Exception("Failed to check code for " + value.getName());
				} else {
					response.setResponse(data.toString());
				}
			}else{
				throw new Exception("Name, Code and ProviderServiceMapID is mandatory");
			}

			//
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		return response.toString();
	}

}
