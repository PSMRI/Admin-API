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
package com.iemr.admin.repository.provideronboard;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.provideronboard.M_Institutiontype;

@Repository
public interface M_InstitutiontypeRepo extends CrudRepository<M_Institutiontype, Integer> {

	@Query("SELECT u FROM M_Institutiontype u where u.providerServiceMapID=:providerServiceMapID")
	ArrayList<M_Institutiontype> getInstuteType(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query("SELECT u FROM M_Institutiontype u where u.institutionTypeID=:institutionTypeID")
	M_Institutiontype editdata(@Param("institutionTypeID") Integer institutionTypeID);

	/*
	 * Created BY Du20091017
	 */
	@Query("SELECT u FROM M_Institutiontype u where u.providerServiceMapID=:providerServiceMapID AND u.districtId=:districtId")
	ArrayList<M_Institutiontype> getInstuteTypeByDist(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("districtId") Integer districtId);

	@Query("SELECT u FROM M_Institutiontype u where u.providerServiceMapID=:providerServiceMapID AND u.districtId=:districtId AND u.subDistrictId=:subDistrictId")
	ArrayList<M_Institutiontype> getInstutionTypeByBlock(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("districtId") Integer districtId, @Param("subDistrictId") Integer subDistrictId);

	@Query("SELECT u FROM M_Institutiontype u where u.providerServiceMapID=:providerServiceMapID AND u.districtId=:districtId AND u.villageId=:villageId")
	ArrayList<M_Institutiontype> getInstutionTypeByVillage(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("districtId") Integer districtId, @Param("villageId") Integer villageId);

	@Query("SELECT u FROM M_Institutiontype u where u.providerServiceMapID=:providerServiceMapID AND u.districtId=:districtId AND u.subDistrictId=:subDistrictId AND u.villageId=:villageId")
	ArrayList<M_Institutiontype> getInstutionByBlockAndVillage(
			@Param("providerServiceMapID") Integer providerServiceMapID, @Param("districtId") Integer districtId,
			@Param("subDistrictId") Integer subDistrictId, @Param("villageId") Integer villageId);

}
