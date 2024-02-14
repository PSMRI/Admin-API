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
package com.iemr.admin.repo.uom;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.uom.M_Uom;

@Repository
public interface UomRepo extends CrudRepository<M_Uom, Integer> {

	
	@Query("SELECT u FROM M_Uom u WHERE u.providerServiceMapID=:providerServiceMapID order by u.uOMName")
	ArrayList<M_Uom> getUom(@Param("providerServiceMapID")Integer providerServiceMapID);
    
	
	@Query("SELECT u FROM M_Uom u WHERE u.uOMID=:uOMID")
	M_Uom geteditedData(@Param("uOMID")Integer uOMID);
	
	@Query("SELECT u FROM M_Uom u WHERE u.uOMCode=:uOMCode And u.providerServiceMapID = :providerServiceMapID")
	List<M_Uom> findByUOMCodeAndProviderServiceMapID(String uOMCode,Integer providerServiceMapID);

}
