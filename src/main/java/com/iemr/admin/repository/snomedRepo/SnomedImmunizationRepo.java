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
package com.iemr.admin.repository.snomedRepo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.snomedMapping.ChildVaccinations;
import com.iemr.admin.data.snomedMapping.OptionalVaccinations;


@Repository
@RestResource(exported = false)
public interface SnomedImmunizationRepo extends CrudRepository<ChildVaccinations, Integer> {

	@Transactional
	@Modifying
	@Query("UPDATE ChildVaccinations c SET c.sctCode=:sctCode, c.sctTerm=:sctTerm, c.modifiedBy=:modifiedBy WHERE c.masterID=:masterID")
	Integer updateImmunizationDetails(@Param("masterID") Short masterID,@Param("sctCode") String sctCode, @Param("sctTerm") String sctTerm,@Param("modifiedBy") String modifiedBy);
	
	@Query("SELECT c from ChildVaccinations c ")
	List<ChildVaccinations> fetchChildVaccinations();
	@Transactional
	  @Modifying
	@Query("update ChildVaccinations u set u.deleted=:deleted, u.modifiedBy=:modifiedBy where u.masterID =:masterID")
	int updateStatus(@Param("masterID")Short masterID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
}
