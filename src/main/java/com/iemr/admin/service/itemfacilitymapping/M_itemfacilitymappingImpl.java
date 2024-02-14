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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.itemfacilitymapping.M_itemfacilitymapping;
import com.iemr.admin.data.itemfacilitymapping.V_fetchItemFacilityMap;
import com.iemr.admin.data.items.ItemInStore;
import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.rolemaster.StateServiceMapping;
import com.iemr.admin.repo.stockEntry.ItemStockEntryRepo;
import com.iemr.admin.repository.itemfacilitymapping.M_itemfacilitymappingRepo;
import com.iemr.admin.repository.itemfacilitymapping.V_fetchItemFacilityMapRepo;

@Service
public class M_itemfacilitymappingImpl implements M_itemfacilitymappingInter {

	@Autowired
	private V_fetchItemFacilityMapRepo v_fetchItemFacilityMapRepo;

	@Autowired
	private M_itemfacilitymappingRepo m_itemfacilitymappingRepo;

	@Autowired
	ItemStockEntryRepo itemStockEntryRepo;

	@Override
	public ArrayList<M_itemfacilitymapping> mapItemtoStore(List<M_itemfacilitymapping> resList) {
		ArrayList<M_itemfacilitymapping> data = (ArrayList<M_itemfacilitymapping>) m_itemfacilitymappingRepo
				.saveAll(resList);
		return data;
	}

	@Override
	public M_itemfacilitymapping editdata(Integer itemStoreMapID) {
		M_itemfacilitymapping data = m_itemfacilitymappingRepo.findByItemFacilityMapID(itemStoreMapID);
		return data;
	}

	@Override
	public M_itemfacilitymapping saveEditedItem(M_itemfacilitymapping getdataforedit) {
		M_itemfacilitymapping data = m_itemfacilitymappingRepo.save(getdataforedit);
		return data;
	}

	@Override
	public ArrayList<M_itemfacilitymapping> getsubitemforsubStote(Integer providerServiceMapID, Integer facilityID) {

		ArrayList<M_itemfacilitymapping> itemForsubStore = new ArrayList<M_itemfacilitymapping>();
		ArrayList<Object[]> resultSet = m_itemfacilitymappingRepo.getItemforSubstore(providerServiceMapID, facilityID);
		 for (Object[] objects : resultSet) {
             if (objects != null && objects.length >= 3) {
                   itemForsubStore.add(new M_itemfacilitymapping((Integer) objects[0], (String) objects[1],(Boolean) objects[2],(Integer) objects[3]));
             }

			// logger.debug("for getting state " + resultSet);
		}
		// logger.debug("getting response with stateid " +
		// stateServiceMappings);
		return itemForsubStore;
	}

	@Override
	public ArrayList<V_fetchItemFacilityMap> getAllFacilityMappedData(Integer providerServiceMapID) {
		ArrayList<V_fetchItemFacilityMap> data = v_fetchItemFacilityMapRepo
				.getAllFacilityMappedData(providerServiceMapID);
		return data;
	}

	@Override
	public List<ItemInStore> getItemMastersFromStoreID(Integer storeID) {
		// TODO Auto-generated method stub
		ArrayList<ItemInStore> itemForsubStore = new ArrayList<ItemInStore>();
		ArrayList<Object[]> resultSet = m_itemfacilitymappingRepo.getItemforStore(storeID);
		Integer[] itemID = new Integer[resultSet.size()];
		Object[] objects;
		// for (Object[] objects : resultSet)
		for (int i = 0; i < resultSet.size(); i++) {
			objects = resultSet.get(i);
			if (objects != null && objects.length >= 2) {

				itemID[i]=(Integer) objects[0];
			}

			// logger.debug("for getting state " + resultSet);
		}

		ArrayList<Object[]> quant = itemStockEntryRepo.getQuantity(itemID,storeID);

		for (Object[] objects1 : quant) {
			if (objects1 != null && objects1.length >= 2) {
				itemForsubStore.add(new ItemInStore((Integer) objects1[0], (Integer) objects1[1],(String) objects1[2],(Long) objects1[3]));
			}

			// logger.debug("for getting state " + resultSet);
		}
		return itemForsubStore;
	}

	@Override
	public Integer deleteItemStoreMapping(M_itemfacilitymapping storeID) {
		// TODO Auto-generated method stub M_itemfacilitymapping
		return m_itemfacilitymappingRepo.updateDeleteMap(storeID.getItemStoreMapID(),storeID.getDeleted());
	};
}
