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
package com.iemr.admin.repository.vanType;

import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.vanType.M_VanType;

import jakarta.transaction.Transactional;

@Repository
public interface VanTypeRepository extends CrudRepository<M_VanType, Integer>{
	
	@Query("SELECT v.vanTypeID, v.vanType, v.vanTypeDesc, v.deleted " 
			+ "from M_VanType v  where v.deleted = false")
	List<Objects[]> getVanTypes();
	
	@Transactional
	@Modifying
	@Query("update M_VanType v set v.deleted=:deleted, v.modifiedBy=:modifiedBy where v.vanTypeID =:vanTypeID")
	int updateVanTypeStatus(@Param("vanTypeID")Integer vanTypeID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
}
