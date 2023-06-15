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
package com.iemr.admin.service.item;

import java.util.List;

import com.iemr.admin.data.items.CodeChecker;
import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.items.M_ItemCategory;
import com.iemr.admin.data.items.M_ItemForm;
import com.iemr.admin.data.items.M_Route;

public interface ItemService {

	ItemMaster createItemMaster(ItemMaster itemMaster);

	ItemMaster getItemMasterCatByID(Integer providerServiceMapID);

	List<M_ItemCategory> getItemCategory(Boolean all, Integer providerServiceMapID);

	M_ItemCategory getItemCategory(Integer catID);

//	List<M_Route> getItemRoute(Boolean all);
//
//	List<M_ItemForm> getItemForm(Boolean all);

	List<M_Route> getItemRouteProviderServiceMapID(Integer providerServiceMapID);

	List<M_ItemForm> getItemFormProviderServiceMapID(Integer providerServiceMapID);

	List<ItemMaster> getItemMaster(Integer providerServiceMapID);

	ItemMaster getItemMasterByID(Integer itemMaster);

	Integer blockItemMaster(Integer itemmasterID, Boolean delete);

	Integer discontinueItemMaster(Integer itemmasterID, Boolean discontinue);

	List<ItemMaster> addAllItemMaster(List<ItemMaster> itemMaster);

	// List<M_ItemIssueConfig> addAllItemIssueConfig(List<M_ItemIssueConfig>
	// itemMaster);
	//
	// M_ItemIssueConfig updateItemIssueConfig(M_ItemIssueConfig itemMaster);
	//
	// M_ItemIssueConfig findItemIssueConfig(Integer itemIssueConfigID);
	//
	// List<M_ItemIssueConfig> findItemIssueConfigProviderServiceMapID(Integer
	// providerServiceMapID);
	//
	Integer updateItemIssueConfig(List<M_ItemCategory> itemCategory);

	List<ItemMaster> getItemMasters(Integer providerServiceMapID, Integer itemCategoryID);
	
	Integer createItemCategories(List<M_ItemCategory> itemCategories);
	
	Integer editItemCategory(M_ItemCategory itemCat);
	
	Integer blockItemCategory(M_ItemCategory itemCat);
	
	
	Integer createItemForms(List<M_ItemForm> itemForms);
	
	Integer editItemForm(M_ItemForm itemForm);
	
	Integer blockItemForm(M_ItemForm itemForm);
	
	
	Integer createRoutes(List<M_Route> routes);
	
	Integer editRoute(M_Route route);
	
	Integer blockRoute(M_Route route);

	Boolean checkCodeCategory(CodeChecker value);

	Boolean checkCodeForm(CodeChecker item1);

	Boolean checkCodeItem(CodeChecker item2);

	Boolean checkCodeRoute(CodeChecker item3);

	Integer updateExpiryAlert(List<M_ItemCategory> itemList);

}
