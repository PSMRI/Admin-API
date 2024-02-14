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
package com.iemr.admin.repo.pharmacologicalcategory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.pharmacologicalcategory.M_Pharmacologicalcategory;

@Repository
public interface PharmacologicalcategoryRepo extends CrudRepository<M_Pharmacologicalcategory, Integer>{
   
	
	
	@Query("SELECT u FROM M_Pharmacologicalcategory u WHERE u.providerServiceMapID=:providerServiceMapID order by u.pharmCategoryName")
	ArrayList<M_Pharmacologicalcategory> getPhormacologicalData(@Param("providerServiceMapID")Integer providerServiceMapID);
   
	
	@Query("SELECT u FROM M_Pharmacologicalcategory u WHERE u.pharmacologyCategoryID=:pharmCategoryID")
	M_Pharmacologicalcategory editPhamacologicalData(@Param("pharmCategoryID")Integer pharmCategoryID);


	List<M_Pharmacologicalcategory> findByPharmCategoryCodeAndProviderServiceMapID(String pharmCategoryCode,
			Integer providerServiceMapID);

}
