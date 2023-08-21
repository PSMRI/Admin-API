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
package com.iemr.admin.repository.vanMaster;

import java.util.ArrayList;
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
import com.iemr.admin.data.vanMaster.M_Van;

@Repository
@RestResource(exported = false)
public interface VanMasterRepository extends CrudRepository<M_Van, Integer>{
	

//	
//	@Query("SELECT v.vanID, v.vanName, v.vehicalNo, v.vanTypeID, vt.vanType, v.deleted, v.providerServiceMapID, v.countryID, "
//			+ "c.countryName, v.stateID, s.stateName, v.districtID, d.districtName, v.parkingPlaceID, pp.parkingPlaceName,pp.districtBlockID "
//			+ " FROM M_Van v "
//			+ " LEFT JOIN v.m_vanType vt "
//			+ " LEFT JOIN v.m_parkingplace pp"
//			+ " LEFT JOIN v.m_country c"
//			+ " LEFT JOIN v.state s"
//			+ " LEFT JOIN v.m_district d"
//			+ " LEFT JOIN v.m_providerServiceMapping p "
//			+ " where (v.districtID IS NULL or cast(v.districtID as string) like :districtID) "
//			+ " AND (v.parkingPlaceID IS NULL or cast(v.parkingPlaceID as string) like :parkingPlaceID)"
//			+ " AND (v.vanTypeID IS NULL or cast(v.vanTypeID as string) like :vanTypeID) "
//			+ " AND p.providerServiceMapID =:providerServiceMapID")
//	List<Objects[]> getAvailableVans(@Param("districtID")String districtID, 
//			@Param("parkingPlaceID")String parkingPlaceID, @Param("vanTypeID")String vanTypeID,@Param("providerServiceMapID") Integer providerServiceMapID);

	@Transactional
	@Modifying
	@Query("update M_Van v set v.deleted=:deleted, v.modifiedBy=:modifiedBy where v.vanID =:vanID")
	int updateVanStatus(@Param("vanID")Integer vanID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
	
	
	@Query("SELECT v FROM M_Van v where v.vanID =:vanID")
	M_Van getVanById(@Param("vanID")Integer vanID);
	
	@Transactional
	@Modifying
	@Query("update M_Van pp set pp.facilityID=:facilityID, pp.isFacility=:deleted, pp.modifiedBy=:modifiedBy where pp.vanID =:vanID")
	Integer updateVanMap(@Param("vanID")Integer vanID,@Param("facilityID")Integer facilityID,@Param("modifiedBy")String modifiedBy,@Param("deleted")Boolean deleted);
	
	
	List<M_Van> findByProviderServiceMapIDAndParkingPlaceID(Integer providerServiceMapID,Integer pp);
	
	List<M_Van> findByParkingPlaceIDAndFacilityIDIsNotNull(Integer pp);
	
	List<M_Van> findByFacilityIDAndDeleted(Integer fac,Boolean delete);
	
	@Query("SELECT v.vanID, v.vanName, v.vehicalNo, v.vanTypeID, vt.vanType, v.deleted, v.providerServiceMapID, v.countryID, "
			+ "c.countryName, v.stateID, s.stateName, v.parkingPlaceID, pp.parkingPlaceName,pp.districtBlockID,v.videoConsultationDomain,v.videoConsultationID,v.videoConsultationEmail,v.vanSpokeMapped "
			+ " FROM M_Van v "
			+ " LEFT JOIN v.m_vanType vt "
			+ " LEFT JOIN v.m_parkingplace pp"
			+ " LEFT JOIN v.m_country c"
			+ " LEFT JOIN v.state s"
			+ " LEFT JOIN v.m_providerServiceMapping p "
			+ " where (v.parkingPlaceID IS NULL or cast(v.parkingPlaceID as string) like :parkingPlaceID)"
			+ " AND (v.vanTypeID IS NULL or cast(v.vanTypeID as string) like :vanTypeID) "
			+ " AND p.providerServiceMapID =:providerServiceMapID")
	List<Objects[]> getAvailableVans(@Param("parkingPlaceID")String parkingPlaceID, @Param("vanTypeID")String vanTypeID,@Param("providerServiceMapID") Integer providerServiceMapID);

	/***
	 * 
	 * @param vanID
	 * @param deleted
	 * @param modifiedBy
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("update M_Van v set v.vanSpokeMapped=:vanSpokeMapped, v.modifiedBy=:modifiedBy where v.vanID =:vanID")
	int updateVanSpokeMapping(@Param("vanID")Integer vanID, @Param("vanSpokeMapped")Boolean vanSpokeMapped, @Param("modifiedBy")String modifiedBy);


	/***
	 * @param vanfoetalMonitorIDmapped
	 * @param vanID
	 * @return
	 * created to update the van and FoetalMonitorDeviceID mapping 
	 */
	@Modifying
	@Transactional
	@Query("UPDATE M_Van mv SET mv.vanfoetalMonitorIDmapped = :vanfoetalMonitorIDmapped WHERE mv.vanID = :vanID ")
	public int updateVanFoetalMonitorsmapping(@Param("vanfoetalMonitorIDmapped") Boolean vanfoetalMonitorIDmapped, @Param("vanID") Integer vanID);
	
	/***
	 * @param providerServiceMapID
	 * @return
	 */
	@Query("Select mv.vanID, mv.vehicalNo,mv.vanName from M_Van mv WHERE mv.providerServiceMapID = :providerServiceMapID "
			+ " and mv.vanTypeID = :vanTypeID and mv.parkingPlaceID = :parkingPlaceID and mv.vanfoetalMonitorIDmapped = false")
	public ArrayList<Object[]> getVanIDNotMappedWithDevice(@Param("vanTypeID") Integer vanTypeID,
			@Param("parkingPlaceID") Integer parkingPlaceID,@Param("providerServiceMapID") Integer providerServiceMapID);
	
}
