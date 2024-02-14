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
package com.iemr.admin.repository.item;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.items.M_ItemForm;

import jakarta.transaction.Transactional;


@Repository
public interface ItemFormRepo extends CrudRepository<M_ItemForm, Integer> {
	
	@Query("select u from M_ItemForm u")
	List<M_ItemForm> getAll();
	
//	List<M_ItemForm> findByDeletedOrderByItemForm(Boolean deleted);
	
	List<M_ItemForm> findByProviderServiceMapIDOrderByItemForm(Integer providerServiceMapID);
	
	@Transactional
	@Modifying
	@Query("UPDATE M_ItemForm c SET c.itemFormDesc = :itemFormDesc, c.ModifiedBy = :modifiedBy WHERE c.itemFormID = :itemFormID")
	Integer updateItemFormDetails(@Param("itemFormID") Integer itemFormID,
			@Param("itemFormDesc") String itemFormDesc, @Param("modifiedBy") String modifiedBy);

	@Transactional
	@Modifying
	@Query("UPDATE M_ItemForm c SET c.deleted = :flag, c.ModifiedBy = :modifiedBy WHERE c.itemFormID = :itemFormID")
	Integer blockItemForm(@Param("itemFormID") Integer itemFormID, @Param("flag") Boolean flag,
			@Param("modifiedBy") String modifiedBy);

	List<M_ItemForm> findByItemFormCodeAndProviderServiceMapID(String itemFormCode, Integer providerServiceMapID);


}
