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
package com.iemr.admin.repository.parkingPlace;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.parkingPlace.M_Parkingplace;

@Repository
@RestResource(exported = false)
public interface ParkingPlaceRepository extends CrudRepository<M_Parkingplace, Integer> {

	@Query("SELECT pp.parkingPlaceID, pp.parkingPlaceName, pp.parkingPlaceDesc, pp.areaHQAddress, pp.providerServiceMapID, pp.deleted, pp.countryID, c.countryName, pp.stateID, s.stateName, "
			+ " d.districtID, d.districtName, b.blockID, b.blockName, bm.districtBranchID, bm.villageName,pp.m_providerServiceMapping, sm.serviceID, sm.serviceName "
			+ " FROM M_Parkingplace pp " + " LEFT JOIN pp.m_country c" + " LEFT JOIN pp.state s"
			+ " LEFT JOIN pp.m_district d" + " LEFT JOIN pp.districtBlock b" + " LEFT JOIN pp.districtBranchMapping bm"
			+ " LEFT JOIN pp.m_providerServiceMapping p " + " LEFT JOIN p.m_serviceMaster sm "
			+ " where (pp.stateID IS NULL or cast(pp.stateID as string) like :stateID) AND (pp.districtID IS NULL or cast(pp.districtID as string) like :districtID)"
			+ " AND p.serviceProviderID =:serviceProviderID")
	List<Objects[]> getAvailableParkingPlaces(@Param("stateID") String stateID, @Param("districtID") String districtID,
			@Param("serviceProviderID") Integer serviceProviderID);

	@Transactional
	@Modifying
	@Query("update M_Parkingplace pp set pp.deleted=:deleted, pp.modifiedBy=:modifiedBy where pp.parkingPlaceID =:parkingPlaceID")
	int updateParkingPlaceStatus(@Param("parkingPlaceID") Integer parkingPlaceID, @Param("deleted") Boolean deleted,
			@Param("modifiedBy") String modifiedBy);

	@Query("SELECT m FROM M_Parkingplace m where m.parkingPlaceID =:parkingPlaceID")
	M_Parkingplace getParkingPlaceById(@Param("parkingPlaceID") Integer parkingPlaceID);

	@Transactional
	@Modifying
	@Query("update M_Parkingplace pp set pp.facilityID=:facilityID, pp.isFacility=:deleted, pp.modifiedBy=:modifiedBy where pp.parkingPlaceID =:parkingPlaceID")
	Integer updatePPMap(@Param("parkingPlaceID") Integer parkingPlaceID, @Param("facilityID") Integer facilityID,
			@Param("modifiedBy") String modifiedBy, @Param("deleted") Boolean deleted);

	List<M_Parkingplace> findByProviderServiceMapID(Integer providerServiceMapID);

	M_Parkingplace findFirstByFacilityID(Integer facilityID);

	List<M_Parkingplace> findByFacilityIDAndDeleted(Integer fac, Boolean delete);

	@Query("SELECT pp.parkingPlaceID, b.blockID, b.blockName " + " FROM M_Parkingplace pp "
			+ " LEFT JOIN pp.districtBlock b" + " where pp.parkingPlaceID=:parkingPlaceID ORDER By b.blockName")
	List<Objects[]> getSubDistrict(@Param("parkingPlaceID") Integer parkingPlaceID);

	@Query("SELECT pp,c " + " FROM M_Parkingplace pp " + " LEFT JOIN pp.zone c" + " where (pp.zoneID=:zoneID)"
			+ " AND pp.providerServiceMapID =:serviceProviderID order by pp.parkingPlaceName")
	List<Objects[]> getAvailableParkingPlacesbyzoneid(@Param("zoneID") Integer zoneID,
			@Param("serviceProviderID") Integer serviceProviderID);
}
