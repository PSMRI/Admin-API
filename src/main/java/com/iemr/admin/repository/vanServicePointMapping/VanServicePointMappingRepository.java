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
package com.iemr.admin.repository.vanServicePointMapping;

import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.vanServicePointMapping.M_VanServicePointMap;

import jakarta.transaction.Transactional;

@Repository
public interface VanServicePointMappingRepository extends CrudRepository<M_VanServicePointMap, Integer> {

	

	

			 
	 @Query(value = "select vs1.vanServicePointMapID, vs1.vanID,vs1.vanSession, s.servicePointID, s.servicePointName, "
	 + " vs1.providerServiceMapID, vs1.deleted"
	 + " from (SELECT * FROM M_VanServicePointMap vs where vs.vanID IS NULL or vs.vanID= :vanID )vs1 "
	 + " RIGHT JOIN m_servicepoint s on vs1.servicepointid = s.servicepointid"
	 + " where s.parkingPlaceID = :parkingPlaceID "
	 + " AND s.providerServiceMapID = :providerServiceMapID"
	 + " GROUP BY s.servicePointName" ,nativeQuery = true )
	 List<Objects[]>
	 getAvailableVanServicePointMappings( @Param("parkingPlaceID")Integer parkingPlaceID, @Param("vanID")Integer
	 vanID, @Param("providerServiceMapID")Integer providerServiceMapID);

	 @Query(value = "select vs1.vanServicePointMapID, vs1.vanID,vs1.vanSession, s.servicePointID, s.servicePointName, "
			 + " vs1.providerServiceMapID, vs1.deleted,d.districtID,d.districtName ,db.blockID,db.blockName"
			 + " from (SELECT * FROM M_VanServicePointMap vs where vs.vanID IS NULL or vs.vanID= :vanID )vs1 "
			 + " RIGHT JOIN m_servicepoint s on vs1.servicepointid = s.servicepointid"
			 + " left join m_district d on d.districtID=s.districtID"
			 + " left join m_districtblock db on db.blockID=s.districtBlockID"
			 + " where s.parkingPlaceID = :parkingPlaceID "
			 + " and s.deleted = false "
			 + " AND s.providerServiceMapID = :providerServiceMapID"
			 + " GROUP BY s.servicePointID" ,nativeQuery = true )
			 List<Objects[]>
			 getAvailableVanServicePointMappingsV1( @Param("parkingPlaceID")Integer parkingPlaceID, @Param("vanID")Integer
			 vanID, @Param("providerServiceMapID")Integer providerServiceMapID);
	/*
	 * @Query(value =
	 * "SELECT vmap.vanServicePointMapID, van.vanID, vmap.vanSession, ser.servicePointID , ser.servicePointName, vmap.providerServiceMapID, vmap.deleted"
	 * + " FROM M_VanServicePointMap vmap " +
	 * " RIGHT JOIN m_servicepoint ser on ser.servicePointID = vmap.servicePointID"
	 * + " LEFT join m_van van on vmap.vanID  =van.vanID " +
	 * " AND van.vanID =:vanID" +
	 * " where (ser.stateID IS NULL or cast(ser.stateID as char) like :stateID) "
	 * +
	 * " AND (ser.districtID IS NULL or cast(ser.districtID as char) like :districtID) "
	 * +
	 * " AND (ser.parkingPlaceID IS NULL or cast(ser.parkingPlaceID as char) like :parkingPlaceID)"
	 * , nativeQuery =true) List<Objects[]>
	 * getAvailableVanServicePointMappings(@Param("stateID")String
	 * stateID, @Param("districtID")String districtID,
	 * 
	 * @Param("parkingPlaceID")String parkingPlaceID, @Param("vanID")Integer
	 * vanID);
	 */

	@Transactional
	@Modifying
	@Query("update M_VanServicePointMap v set v.deleted=:deleted, v.modifiedBy=:modifiedBy where v.vanServicePointMapID =:vanServicePointMapID")
	int updateVanServicePointMappingStatus(@Param("vanServicePointMapID") Integer vanServicePointMapID,
			@Param("deleted") Boolean deleted, @Param("modifiedBy") String modifiedBy);

	@Query("SELECT v FROM M_VanServicePointMap v where v.vanServicePointMapID =:vanServicePointMapID")
	M_VanServicePointMap getVanServicePointMapping(@Param("vanServicePointMapID") Integer vanServicePointMapID);

}
