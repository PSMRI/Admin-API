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
package com.iemr.admin.service.itemfacilitymapping;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.itemfacilitymapping.M_itemfacilitymapping;
import com.iemr.admin.data.itemfacilitymapping.V_fetchItemFacilityMap;
import com.iemr.admin.data.items.ItemInStore;

public interface M_itemfacilitymappingInter{

	ArrayList<M_itemfacilitymapping> mapItemtoStore(List<M_itemfacilitymapping> resList);

	M_itemfacilitymapping editdata(Integer itemStoreMapID);

	M_itemfacilitymapping saveEditedItem(M_itemfacilitymapping getdataforedit);

	ArrayList<M_itemfacilitymapping> getsubitemforsubStote(Integer providerServiceMapID, Integer facilityID);

	ArrayList<V_fetchItemFacilityMap> getAllFacilityMappedData(Integer providerServiceMapID);
	
	List<ItemInStore> getItemMastersFromStoreID(Integer storeID);

	Integer deleteItemStoreMapping(M_itemfacilitymapping storeID);

}
