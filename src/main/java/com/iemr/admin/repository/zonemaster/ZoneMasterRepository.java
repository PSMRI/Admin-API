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
package com.iemr.admin.repository.zonemaster;

import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.zonemaster.M_Zone;

import jakarta.transaction.Transactional;

@Repository
public interface ZoneMasterRepository extends CrudRepository<M_Zone, Integer>{
	
/*	@Query("SELECT m FROM M_Zone m where deleted=false")
	ArrayList<M_Zone> getAvailableZones();*/
	

	
	@Query("SELECT m.zoneID, m.zoneName, m.zoneDesc, m.zoneHQAddress, m.providerServiceMapID, m.deleted, m.countryID, c.countryName, m.stateID, s.stateName, "
			+ " d.districtID, d.districtName, b.blockID, b.blockName, bm.districtBranchID, bm.villageName,m.m_providerServiceMapping, sm.serviceID, sm.serviceName "
			+ " FROM M_Zone m "
			+ " LEFT JOIN m.m_country c"
			+ " LEFT JOIN m.state s"
			+ " LEFT JOIN m.m_district d"
			+ " LEFT JOIN m.districtBlock b"
			+ " LEFT JOIN m.districtBranchMapping bm"
			+ " LEFT JOIN m.m_providerServiceMapping p"
			+ " LEFT JOIN p.m_serviceMaster sm "
			+ " where p.providerServiceMapID =:providerServiceMapID ORDER By m.zoneName ")
	List<Objects[]> getAvailableZones(@Param("providerServiceMapID")Integer providerServiceMapID);

	@Transactional
	@Modifying
	@Query("update M_Zone m set m.deleted=:deleted, m.modifiedBy=:modifiedBy where m.zoneID =:zoneID")
	int updateZoneStatus(@Param("zoneID")Integer zoneID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
	
	@Query("SELECT m FROM M_Zone m where m.zoneID =:zoneID")
	M_Zone getZoneById(@Param("zoneID")Integer zoneID);
	
}
