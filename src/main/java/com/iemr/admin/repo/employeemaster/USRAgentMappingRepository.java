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
package com.iemr.admin.repo.employeemaster;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.USRAgentMapping;

import jakarta.transaction.Transactional;

@Repository
public interface USRAgentMappingRepository extends CrudRepository<USRAgentMapping, Integer>
{
	@Query(value = "select usra.usrAgentMappingID, usra.usrMappingID, usra.usrMapping, "
			+ "usra.providerServiceMapID, usra.providerServiceMap, usra.agentID, usra.agentPassword, "
			+ "usra.cti_CampaignName, usra.isAvailable "
			+ "from USRAgentMapping usra left join usra.usrMapping join usra.providerServiceMap "
			+ "where usra.cti_CampaignName = :cti_CampaignName "
			+ "and usra.providerServiceMapID = :providerServiceMapID and usra.deleted = false "
			+ "and usra.isAvailable = true")
	Set<Objects[]> getFreeAgentIds(@Param("cti_CampaignName") String cti_CampaignName,
			@Param("providerServiceMapID") Integer providerServiceMapID);

	@Transactional
	@Modifying
	@Query(value = "update USRAgentMapping usra set usra.isAvailable = :isAvailable, usra.usrMappingID = :usrMappingID "
			+ "where usra.usrAgentMappingID = :usrAgentMappingID")
	int updateUSRMapping(@Param("isAvailable") Boolean isAvailable, @Param("usrMappingID") Integer usrMappingID,
			@Param("usrAgentMappingID") Integer usrAgentMappingID);

	@Transactional
	@Modifying
	@Query(value = "update USRAgentMapping usra set usra.isAvailable = :isAvailable, usra.usrMappingID = :usrMappingID "
			+ "where usra.agentID = :agentID and usra.providerServiceMapID = :providerServiceMapID ")
	int updateUSRMapping(@Param("isAvailable") Boolean isAvailable, @Param("usrMappingID") Integer usrMappingID,
			@Param("agentID") String agentID, @Param("providerServiceMapID") Integer providerServiceMapID);

	@Query(value = "select distinct cti_CampaignName from USRAgentMapping "
			+ "where providerServiceMapID = :providerServiceMapID and deleted = false and " + "isAvailable = true")
	List<String> getAvailableCampaigns(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query(value = "select count(usra) from USRAgentMapping usra "
			+ "where usra.providerServiceMapID = :providerServiceMapID and "
			+ "usra.deleted = false and usra.agentID = :agentID")
	long getExistingAgent(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("agentID") String agentID);

	@Query(value = "select usra.usrAgentMappingID, usra.usrMappingID, usra.usrMapping, "
			+ "usra.providerServiceMapID, usra.providerServiceMap, usra.agentID, usra.agentPassword, "
			+ "usra.cti_CampaignName, usra.isAvailable "
			+ "from USRAgentMapping usra left join usra.usrMapping join usra.providerServiceMap "
			+ "where usra.providerServiceMapID = :providerServiceMapID and usra.deleted = false order by CAST(usra.agentID as int)")
	Set<Objects[]> getAllAgentIds(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query(value = "select usra.usrAgentMappingID, usra.usrMappingID, usra.usrMapping, "
			+ "usra.providerServiceMapID, usra.providerServiceMap, usra.agentID, usra.agentPassword, "
			+ "usra.cti_CampaignName, usra.isAvailable "
			+ "from USRAgentMapping usra left join usra.usrMapping join usra.providerServiceMap "
			+ "where usra.cti_CampaignName = :cti_CampaignName "
			+ "and usra.providerServiceMapID = :providerServiceMapID order by CAST(usra.agentID as int)")
	Set<Objects[]> getAllAgentId(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("cti_CampaignName") String cti_CampaignName);

	@Query(value = "select usra.usrAgentMappingID, usra.usrMappingID, usra.usrMapping, "
			+ "usra.providerServiceMapID, usra.providerServiceMap, usra.agentID, usra.agentPassword, "
			+ "usra.cti_CampaignName, usra.isAvailable "
			+ "from USRAgentMapping usra left join usra.usrMapping join usra.providerServiceMap "
			+ "where usra.cti_CampaignName = :cti_CampaignName "
			+ "and usra.providerServiceMapID = :providerServiceMapID and usra.deleted = false "
			+ "and usra.isAvailable = :isAvailable order by CAST(usra.agentID as int)")
	Set<Objects[]> getAllAgentIds(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("cti_CampaignName") String cti_CampaignName, @Param("isAvailable") Boolean isAvailable);
	
	@Query(value = "select usra.usrAgentMappingID, usra.usrMappingID, usra.usrMapping, "
			+ "usra.providerServiceMapID, usra.providerServiceMap, usra.agentID, usra.agentPassword, "
			+ "usra.cti_CampaignName, usra.isAvailable "
			+ "from USRAgentMapping usra left join usra.usrMapping join usra.providerServiceMap "
			+ "where usra.providerServiceMapID = :providerServiceMapID and usra.agentID = :agentID order by CAST(usra.agentID as int)")
	Set<Objects[]> getUSRAgentMappingByAgentIDAndProviderServiceMapID(@Param("agentID")String agentID, @Param("providerServiceMapID")Integer providerServiceMapID);

	@Transactional
	@Modifying
	@Query(value = "update USRAgentMapping usra set usra.cti_CampaignName = :cti_CampaignName "
			+ "where usra.usrAgentMappingID = :usrAgentMappingID")
	int updateCTICampaignNameMapping(@Param("cti_CampaignName") String cti_CampaignName,
			@Param("usrAgentMappingID") Integer usrAgentMappingID);

	@Transactional
	@Modifying
	@Query(" UPDATE  USRAgentMapping u SET u.isAvailable=true where u.agentID =:agentID")
	int updateDeletedAgentIDStatus(@Param("agentID") String agentID);

}
