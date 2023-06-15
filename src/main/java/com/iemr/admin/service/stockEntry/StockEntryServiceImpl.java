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
package com.iemr.admin.service.stockEntry;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.stockExit.ItemStockExit;
import com.iemr.admin.data.stockentry.ItemStockEntry;
import com.iemr.admin.data.stockentry.PhysicalStockEntry;
import com.iemr.admin.repo.stockEntry.ItemStockEntryRepo;
import com.iemr.admin.repo.stockEntry.PhysicalStockEntryRepo;
import com.iemr.admin.service.item.ItemService;


@Service
public class StockEntryServiceImpl implements StockEntryService {

	@Autowired
	PhysicalStockEntryRepo physicalStockEntryRepo;

	@Autowired
	ItemStockEntryRepo itemStockEntryRepo;
	
	@Autowired
	ItemService itemService;

	private static final Logger logger = LoggerFactory.getLogger(StockEntryServiceImpl.class);

	@Override
	@Transactional
	public PhysicalStockEntry savePhysicalStockEntry(PhysicalStockEntry physicalStockEntry) {
		// TODO Auto-generated method stub
		PhysicalStockEntry physicalStockEntryinput = physicalStockEntryRepo.save(physicalStockEntry);
		physicalStockEntry.getItemStockEntry().forEach(stock -> {
			stock.setEntryTypeID(physicalStockEntryinput.getPhyEntryID());
			stock.setEntryType("physicalStockEntry");
			stock.setQuantityInHand(stock.getQuantity());
		});
		try {
			physicalStockEntryinput.setItemStockEntry(
					(List<ItemStockEntry>) itemStockEntryRepo.save(physicalStockEntry.getItemStockEntry()));
		} catch (DataIntegrityViolationException sqe) {
			physicalStockEntryinput.setDeleted(true);
			physicalStockEntryRepo.updateDelete(physicalStockEntryinput.getPhyEntryID(),true);
			throw sqe;
		} catch (Exception e) {
			logger.error("Error coccured");
		}

		return physicalStockEntryinput;
	}

	@Override
	public List<ItemStockEntry> getItemBatchForStoreID(ItemStockEntry itemStockEntry) {
		// TODO Auto-generated method stub
		//Integer facilityID,Integer itemID,Integer quantityInHand,Boolean deleted);
		return itemStockEntryRepo.findByFacilityIDAndItemIDAndQuantityInHandGreaterThanAndDeleted(itemStockEntry.getFacilityID(),itemStockEntry.getItemID(),0,false);
	}

	public List<Object[]> getAllItemBatchForStoreID(Integer storeID,Integer[] itemStockID) {
		// TODO Auto-generated method stub
		//Integer facilityID,Integer itemID,Integer quantityInHand,Boolean deleted);
		return itemStockEntryRepo.getQuantityOfStock(itemStockID,storeID);
	}

	public Integer updateStocks(List<ItemStockExit> itemissueListUpdated){
		Integer cnt=0;
		for (ItemStockExit itemStockExit : itemissueListUpdated) {
			cnt=cnt+itemStockEntryRepo.updateStock(itemStockExit.getItemStockEntryID(), itemStockExit.getQuantityInHand()- itemStockExit.getQuantity());
		}
		
		
		return cnt;
		
	}
	@Override
	public List<ItemStockEntry> getItemStockForStoreIDOrderByEntryDateAsc(Integer facilityID, Integer itemID) {
		// TODO Auto-generated method stub
		return itemStockEntryRepo.findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByAsc(facilityID,itemID,false,0);
	}

	@Override
	public List<ItemStockEntry> getItemStockForStoreIDOrderByEntryDateDesc(Integer facilityID, Integer itemID) {
		// TODO Auto-generated method stub
		return itemStockEntryRepo.findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByDesc(facilityID,itemID,false,0);
	}

	@Override
	public List<ItemStockEntry> getItemStockForStoreIDOrderByExpiryDate(Integer facilityID, Integer itemID) {
		// TODO Auto-generated method stub
		return itemStockEntryRepo.findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByExpiryDateDesc(facilityID,itemID,false,0);
	};
	
public List<ItemStockEntry> getItemStockFromItemID(Integer facilityID,List<ItemStockExit> itemStockExitList){
		
		List<ItemStockEntry> itemStockList=new ArrayList<ItemStockEntry>();
		for (ItemStockExit itemStockExit : itemStockExitList) {
			// TODO : get category
			ItemMaster item=itemService.getItemMasterCatByID(itemStockExit.getItemID());
			List<ItemStockEntry> stockList=new ArrayList<ItemStockEntry>();
			String method=item.getItemCategory().getIssueType();
			Integer itemID=itemStockExit.getItemID();
			if(method.equalsIgnoreCase("First Expiry First Out")){
				stockList=getItemStockForStoreIDOrderByExpiryDate(facilityID, itemID);
			}else if(method.equalsIgnoreCase("Last in First Out")){
				stockList=getItemStockForStoreIDOrderByEntryDateAsc(facilityID, itemID);
			}else if(method.equalsIgnoreCase("First in First Out")){
				stockList=getItemStockForStoreIDOrderByEntryDateDesc(facilityID, itemID);
			}else {
				stockList=getItemStockForStoreIDOrderByEntryDateAsc(facilityID, itemID);
			}
			
			Integer totalQty=0;
			
			for(ItemStockEntry stock:stockList){
				totalQty=totalQty+stock.getQuantityInHand();
				itemStockList.add(stock);
				stock.setQuantity(stock.getQuantityInHand());
				if(totalQty>=itemStockExit.getQuantity()){
					stock.setQuantity(stock.getQuantityInHand()-totalQty+itemStockExit.getQuantity());
					break;
				}
				
			}
			if(totalQty<itemStockExit.getQuantity()){
				ItemStockEntry shortageStock=new ItemStockEntry();
				shortageStock.setItemID(itemID);
				shortageStock.setFacilityID(facilityID);
				shortageStock.setQuantity(itemStockExit.getQuantity()-totalQty);
				itemStockList.add(shortageStock);
			}
			
		}
		
		return itemStockList;
	}
}
