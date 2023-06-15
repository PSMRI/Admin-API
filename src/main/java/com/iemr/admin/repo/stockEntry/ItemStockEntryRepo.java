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
package com.iemr.admin.repo.stockEntry;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.stockentry.ItemStockEntry;

@Repository
@RestResource(exported = false)
public interface ItemStockEntryRepo extends CrudRepository<ItemStockEntry, Integer> {

	@Query("SELECT ise.facilityID,ise.itemID,itm.itemName,sum(ise.quantityInHand) "
			+ "from ItemStockEntry ise join ise.item itm where ise.itemID in (:itemID) and ise.facilityID=:storeID "
			+ "group by ise.facilityID,ise.itemID")
	ArrayList<Object[]> getQuantity(@Param("itemID") Integer[] itemID, @Param("storeID") Integer storeID);

	@Query("SELECT ise.facilityID,ise.itemStockEntryID,itm.itemName,ise.quantityInHand "
			+ "from ItemStockEntry ise join ise.item itm where ise.itemStockEntryID in (:itemStockID) and ise.facilityID=:storeID "
			)
	ArrayList<Object[]> getQuantityOfStock(@Param("itemStockID") Integer[] itemStockID,
			@Param("storeID") Integer storeID);

	List<ItemStockEntry> findByFacilityIDAndItemIDAndQuantityInHandGreaterThanAndDeleted(Integer facilityID,
			Integer itemID, Integer quantityInHand, Boolean deleted);

	@Transactional
	@Modifying
	@Query("UPDATE ItemStockEntry c SET c.quantityInHand = :quant WHERE c.itemStockEntryID = :id")
	Integer updateStock(@Param("id")Integer id,@Param("quant")Integer quant);
	
	List<ItemStockEntry> findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByAsc(Integer facilityID,
			Integer itemID, Boolean deleted,Integer qin);
	
	List<ItemStockEntry> findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByDesc(Integer facilityID,
			Integer itemID,  Boolean deleted,Integer qin);
	
	List<ItemStockEntry> findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByExpiryDateDesc(Integer facilityID,
			Integer itemID, Boolean deleted,Integer qin);
}
