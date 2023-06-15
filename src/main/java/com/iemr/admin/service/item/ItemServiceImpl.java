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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.items.CodeChecker;
import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.items.M_ItemCategory;
import com.iemr.admin.data.items.M_ItemForm;
import com.iemr.admin.data.items.M_Route;
import com.iemr.admin.repository.item.ItemCategoryRepo;
import com.iemr.admin.repository.item.ItemFormRepo;
import com.iemr.admin.repository.item.ItemRepo;
import com.iemr.admin.repository.item.RouteRepo;
import com.iemr.admin.repository.itemfacilitymapping.M_itemfacilitymappingRepo;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private ItemCategoryRepo itemCategoryRepo;

	@Autowired
	private RouteRepo routeRepo;

	@Autowired
	private ItemFormRepo itemFormRepo;

	@Autowired
	M_itemfacilitymappingRepo itemfacilitymappingRepo;

	@Override
	public ItemMaster createItemMaster(ItemMaster itemMaster) {
		// TODO Auto-generated method stub
		return itemRepo.save(itemMaster);
	}

	public List<M_ItemCategory> getItemCategory(Boolean all, Integer providerServiceMapID) {
		// TODO Auto-generated method stub
		List<M_ItemCategory> itemCategory = new ArrayList<M_ItemCategory>();
		if (providerServiceMapID != null) {
			if (all) {
				itemCategory = itemCategoryRepo.findByProviderServiceMapIDOrderByItemCategoryName(providerServiceMapID);
			} else {
				itemCategory = itemCategoryRepo.findByDeletedAndProviderServiceMapIDOrderByItemCategoryName(false, providerServiceMapID);
			}
		}
		return itemCategory;
	}

//	public List<M_Route> getItemRoute(Boolean all) {
//		// TODO Auto-generated method stub
//
//		List<M_Route> route = new ArrayList<M_Route>();
//
//		if (all) {
//			route = routeRepo.getAll();
//		} else {
//			route = routeRepo.findByDeleted(false);
//		}
//
//		return route;
//	}

//	public List<M_ItemForm> getItemForm(Boolean all) {
//		// TODO Auto-generated method stub
//
//		List<M_ItemForm> route = new ArrayList<M_ItemForm>();
//
//		if (all) {
//			route = itemFormRepo.getAll();
//		} else {
//			route = itemFormRepo.findByDeleted(false);
//		}
//
//		return route;
//	}

	public List<ItemMaster> getItemMaster(Integer providerServiceMapID) {

		return itemRepo.findByProviderServiceMapIDOrderByItemName(providerServiceMapID);

	}

	public Integer blockItemMaster(Integer itemmasterID, Boolean deleteflag) {
		return itemRepo.deleteItemMaster(itemmasterID, deleteflag);

	};

	public Integer discontinueItemMaster(Integer itemmasterID, Boolean continueflag) {
		return itemRepo.discontinueItemMaster(itemmasterID, continueflag);

	}

	@Override
	public List<ItemMaster> addAllItemMaster(List<ItemMaster> itemMaster) {
		// TODO Auto-generated method stub
		return (List<ItemMaster>) itemRepo.save(itemMaster);
	}

	// @Override
	// public List<M_ItemIssueConfig>
	// addAllItemIssueConfig(List<M_ItemIssueConfig> itemMaster) {
	// // TODO Auto-generated method stub
	// return (List<M_ItemIssueConfig>) itemIssueRepo.save(itemMaster);
	// };
	//
	// public M_ItemIssueConfig updateItemIssueConfig(M_ItemIssueConfig
	// itemMaster) {
	// // TODO Auto-generated method stub
	// return (M_ItemIssueConfig) itemIssueRepo.save(itemMaster);
	// }
	//
	// @Override
	// public M_ItemIssueConfig findItemIssueConfig(Integer itemIssueConfigID) {
	// // TODO Auto-generated method stub
	// return itemIssueRepo.findOne(itemIssueConfigID);
	// }
	//
	// @Override
	// public List<M_ItemIssueConfig>
	// findItemIssueConfigProviderServiceMapID(Integer providerServiceMapID) {
	// // TODO Auto-generated method stub
	// return itemIssueRepo.findByProviderServiceMapID(providerServiceMapID);
	// }

	@Override
	public ItemMaster getItemMasterByID(Integer itemMasterID) {
		// TODO Auto-generated method stub

		return itemRepo.findOne(itemMasterID);
	}

	public ItemMaster getItemMasterCatByID(Integer itemMasterID) {
		// TODO Auto-generated method stub

		return itemRepo.findDetailOne(itemMasterID);
	}

	@Override
	public Integer updateItemIssueConfig(List<M_ItemCategory> itemCategory) {
		// TODO Auto-generated method stub
		int cnt = 0;
		for (M_ItemCategory m_ItemCategory : itemCategory) {
			if (m_ItemCategory.getItemCategoryID() != null && m_ItemCategory.getIssueType() != null) {
				cnt = cnt + itemCategoryRepo.updateIssueConfig(m_ItemCategory.getItemCategoryID(),
						m_ItemCategory.getIssueType());
			}
		}
		return cnt;
	}

	@Override
	public List<M_Route> getItemRouteProviderServiceMapID(Integer providerServiceMapID) {
		// TODO Auto-generated method stub
//		return routeRepo.findByProviderServiceMapIDOrderByRouteName(providerServiceMapID);
		return routeRepo.getAll();
		
	}

	@Override
	public List<M_ItemForm> getItemFormProviderServiceMapID(Integer providerServiceMapID) {
		// TODO Auto-generated method stub

//		return itemFormRepo.findByProviderServiceMapIDOrderByItemForm(providerServiceMapID);
		return  itemFormRepo.getAll();

	}

	@Override
	public List<ItemMaster> getItemMasters(Integer providerServiceMapID, Integer itemCategoryID) {
		List<ItemMaster> data = itemRepo.getItemMasters(providerServiceMapID, itemCategoryID);
		return data;

	}

	@Override
	public M_ItemCategory getItemCategory(Integer catID) {
		// TODO Auto-generated method stub
		return itemCategoryRepo.findOne(catID);
	}

	@Override
	public Integer createItemCategories(List<M_ItemCategory> itemCategories) {
		Integer categoryId = 0;
		List<M_ItemCategory> categories = (List<M_ItemCategory>) itemCategoryRepo.save(itemCategories);
		if (null != categories && categories.size() > 0) {
			categoryId = categories.get(0).getItemCategoryID();
		}
		return categoryId;
	}

	@Override
	public Integer editItemCategory(M_ItemCategory itemCat) {

		Integer res = itemCategoryRepo.updateItemCategoryDetails(itemCat.getItemCategoryID(),
				itemCat.getItemCategoryDesc(), itemCat.getModifiedBy());

		return res;
	}

	@Override
	public Integer blockItemCategory(M_ItemCategory itemCat) {
		Integer res = itemCategoryRepo.blockItemCategory(itemCat.getItemCategoryID(), itemCat.getDeleted(),
				itemCat.getModifiedBy());

		return res;
	}

	@Override
	public Integer createItemForms(List<M_ItemForm> itemForms) {
		Integer itemFormID = 0;
		List<M_ItemForm> forms = (List<M_ItemForm>) itemFormRepo.save(itemForms);
		if (null != forms && forms.size() > 0) {
			itemFormID = forms.get(0).getItemFormID();
		}
		return itemFormID;
	}

	@Override
	public Integer editItemForm(M_ItemForm itemForm) {
		Integer res = itemFormRepo.updateItemFormDetails(itemForm.getItemFormID(), itemForm.getItemFormDesc(),
				itemForm.getModifiedBy());

		return res;
	}

	@Override
	public Integer blockItemForm(M_ItemForm itemForm) {
		Integer res = itemFormRepo.blockItemForm(itemForm.getItemFormID(), itemForm.getDeleted(),
				itemForm.getModifiedBy());
		return res;
	}

	@Override
	public Integer createRoutes(List<M_Route> routes) {
		Integer routeID = 0;
		List<M_Route> routeList = (List<M_Route>) routeRepo.save(routes);
		if (null != routeList && routeList.size() > 0) {
			routeID = routeList.get(0).getRouteID();
		}
		return routeID;
	}

	@Override
	public Integer editRoute(M_Route route) {
		Integer res = routeRepo.updateRouteDetails(route.getRouteID(), route.getRouteDesc(), route.getModifiedBy());
		return res;
	}

	@Override
	public Integer blockRoute(M_Route route) {
		Integer res = routeRepo.blockRoute(route.getRouteID(), route.getDeleted(), route.getModifiedBy());
		return res;
	}

	@Override
	public Boolean checkCodeCategory(CodeChecker item) {
		// TODO Auto-generated method stub
		List<M_ItemCategory> manuList=itemCategoryRepo.findByItemCategoryCodeAndProviderServiceMapID(item.getCode() ,item.getProviderServiceMapID());
		if(manuList.size()>0)
			return true;
		return false;
	}

	@Override
	public Boolean checkCodeForm(CodeChecker item1) {
		// TODO Auto-generated method stub
		List<M_ItemForm> manuList=itemFormRepo.findByItemFormCodeAndProviderServiceMapID(item1.getCode() ,item1.getProviderServiceMapID());
		if(manuList.size()>0)
			return true;
		return false;
	}

	@Override
	public Boolean checkCodeItem(CodeChecker item2) {
		// TODO Auto-generated method stub
		List<ItemMaster> manuList=itemRepo.findByItemCodeAndProviderServiceMapID(item2.getCode() ,item2.getProviderServiceMapID());
		if(manuList.size()>0)
			return true;
		return false;
	}

	@Override
	public Boolean checkCodeRoute(CodeChecker item3) {
		// TODO Auto-generated method stub
		List<M_Route> manuList=routeRepo.findByRouteCodeAndProviderServiceMapID(item3.getCode() ,item3.getProviderServiceMapID());
		if(manuList.size()>0)
			return true;
		return false;
	}

	@Override
	public Integer updateExpiryAlert(List<M_ItemCategory> itemList) {
		// TODO Auto-generated method stub
		int cnt = 0;
		for (M_ItemCategory m_ItemCategory : itemList) {
			if (m_ItemCategory.getItemCategoryID() != null && m_ItemCategory.getAlertBeforeDays() != null) {
				cnt = cnt + itemCategoryRepo.updateExpiryAlert(m_ItemCategory.getItemCategoryID(),
						m_ItemCategory.getAlertBeforeDays());
			}
		}
		return cnt;
	}

}
