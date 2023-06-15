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
package com.iemr.admin.repo.blocking;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;

@Repository
@RestResource(exported = false)
public interface MProviderservicemappingBlockingRepo extends CrudRepository<M_Providerservicemapping_Blocking, Integer>
{

	/* @Query("SELECT u FROM M_Role u where u.providerServiceMapID = :pssmID") */
	@Transactional
	@Modifying
	@Query(" UPDATE  M_Providerservicemapping_Blocking u SET u.statusID=:statusID where u.serviceProviderID = :serviceProviderID AND u.stateID=:stateID AND u.serviceID=:serviceID")
	void blockProviderByService(@Param("serviceProviderID") Integer serviceProviderID,
			@Param("stateID") Integer stateID, @Param("serviceID") Integer serviceID,
			@Param("statusID") Integer statusID);

	@Query("SELECT u FROM M_Providerservicemapping_Blocking u where u.serviceProviderID = :serviceProviderID AND u.stateID=:stateID AND u.serviceID=:serviceID AND deleted=0")
	M_Providerservicemapping_Blocking getProviderServiceMappingDetails(
			@Param("serviceProviderID") Integer serviceProviderID, @Param("stateID") Integer stateID,
			@Param("serviceID") Integer serviceID);

	@Query("SELECT u FROM M_Providerservicemapping_Blocking u where u.serviceProviderID = :serviceProviderID AND u.stateID=:stateID AND deleted=0")
	List<M_Providerservicemapping_Blocking> getProviderStateMappingDetails(
			@Param("serviceProviderID") Integer serviceProviderID, @Param("stateID") Integer stateID);

	// void blockProviderByState(Integer serviceProviderID, Integer stateID, Integer statusID);

	@Transactional
	@Modifying
	@Query(" UPDATE  M_Providerservicemapping_Blocking u SET u.statusID=:statusID where u.serviceProviderID = :serviceProviderID AND u.stateID=:stateID")
	void blockProviderByState(@Param("serviceProviderID") Integer serviceProviderID, @Param("stateID") Integer stateID,
			@Param("statusID") Integer statusID);

	@Query("SELECT u FROM M_Providerservicemapping_Blocking u where u.serviceProviderID = :serviceProviderID AND deleted=0")
	ArrayList<M_Providerservicemapping_Blocking>
			getProviderStatus(@Param("serviceProviderID") Integer serviceProviderID);

	@Query("SELECT u FROM M_Providerservicemapping_Blocking u where u.serviceProviderID = :serviceProviderID AND u.serviceID=:serviceID AND deleted=0")
	ArrayList<M_Providerservicemapping_Blocking> getProviderStatusByProviderAndServiceId(
			@Param("serviceProviderID") Integer serviceProviderID, @Param("serviceID") Integer serviceID);

	@Transactional
	@Modifying
	@Query(" UPDATE  M_Providerservicemapping_Blocking u SET u.statusID=:statusID where u.serviceProviderID = :serviceProviderID")
	void blockProvider(@Param("serviceProviderID") Integer serviceProviderID, @Param("statusID") Integer statusID);

	@Transactional
	@Modifying
	@Query(" UPDATE  M_Providerservicemapping_Blocking u SET u.statusID=:statusID where u.serviceProviderID = :serviceProviderID AND u.serviceID=:serviceID")
	void blockProviderByProviderIdAndServiceId(@Param("serviceProviderID") Integer serviceProviderID,
			@Param("serviceID") Integer serviceID, @Param("statusID") Integer statusID);

	@Query(" SELECT distinct srm.providerServiceMapID,srm.serviceProviderID,srm.serviceID,"
			+ " sm.serviceName as serviceName," + " sm.isNational as isNational "
			+ " FROM M_Providerservicemapping_Blocking srm " + "  JOIN srm.m_ServicemasterForBlocking sm"
			+ " WHERE srm.serviceProviderID =:serviceProviderID AND srm.deleted=0" + " GROUP BY sm.serviceName")
	ArrayList<Object[]> getServiceLiensUsingProvider(@Param("serviceProviderID") Integer serviceProviderID);

	@Transactional
	@Modifying
	@Query(" UPDATE  M_Providerservicemapping_Blocking u SET u.cTI_CampaignName=:cTI_CampaignName where u.providerServiceMapID = :providerServiceMapID")
	Integer createcitmapping(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("cTI_CampaignName") String cTI_CampaignName);

	@Query(" SELECT distinct srm.providerServiceMapID,srm.serviceProviderID,srm.serviceID,"
			+ " COALESCE(sm.serviceName, '') as serviceName," + " COALESCE(srm.stateID, '') as stateID,"
			+ " COALESCE(sfb.stateName, '') as stateName,"
			+ " COALESCE(spmf.serviceProviderName, '') as serviceProviderName,"
			+ " srm.deleted as deleted, srm.cTI_CampaignName,"
			+ " spmf.deleted as serviceProviderDeleted "
			+ " FROM M_Providerservicemapping_Blocking srm"
			+ " LEFT JOIN srm.m_ServicemasterForBlocking sm" + " LEFT JOIN srm.m_StateForBlocking sfb"
			+ " LEFT JOIN srm.serviceProvider_ModelForBlocking spmf"
			+ " ORDER BY spmf.serviceProviderName")
	ArrayList<Object[]> getServiceLiensUsingProvider1();

	@Query(" SELECT distinct srm.providerServiceMapID,srm.serviceProviderID,srm.serviceID,"
			+ " COALESCE(sm.serviceName, '') as serviceName, "
			+ " COALESCE(srm.stateID, '') as stateID,"
			+ " COALESCE(sfb.stateName, '') as stateName,"
			+ " COALESCE(spmf.serviceProviderName, '') as serviceProviderName,"
			+ " srm.deleted as deleted, "
			+ " srm.cTI_CampaignName,"
			+ " spmf.deleted as serviceProviderDeleted "
			+ " FROM M_Providerservicemapping_Blocking srm"
			+ " LEFT JOIN srm.m_ServicemasterForBlocking sm LEFT JOIN srm.m_StateForBlocking sfb"
			+ " LEFT JOIN srm.serviceProvider_ModelForBlocking spmf where srm.serviceProviderID = :serviceProviderID"
			+ " and srm.serviceID = :serviceID and srm.stateID = :stateID"
			+ " ORDER BY spmf.serviceProviderName")
	ArrayList<Object[]> getServiceLiensUsingProvider1(@Param("serviceProviderID") Integer serviceProviderID,
			@Param("serviceID") Integer serviceID, @Param("stateID") Integer stateID);

	@Query(" SELECT distinct srm.providerServiceMapID,srm.serviceProviderID,srm.serviceID,"
			+ " COALESCE(sm.serviceName, '') as serviceName, COALESCE(srm.stateID, '') as stateID,"
			+ " COALESCE(sfb.stateName, '') as stateName,"
			+ " COALESCE(spmf.serviceProviderName, '') as serviceProviderName,"
			+ " srm.deleted as deleted, srm.cTI_CampaignName,"
			+ " spmf.deleted as serviceProviderDeleted "
			+ " FROM M_Providerservicemapping_Blocking srm"
			+ " LEFT JOIN srm.m_ServicemasterForBlocking sm LEFT JOIN srm.m_StateForBlocking sfb"
			+ " LEFT JOIN srm.serviceProvider_ModelForBlocking spmf where srm.serviceProviderID = :serviceProviderID"
			+ " and srm.serviceID = :serviceID"
			+ " ORDER BY spmf.serviceProviderName")
	ArrayList<Object[]> getServiceLiensUsingProvider1(@Param("serviceProviderID") Integer serviceProviderID,
			@Param("serviceID") Integer serviceID);

	@Query(" SELECT distinct srm.providerServiceMapID,srm.serviceProviderID,srm.serviceID,"
			+ " COALESCE(sm.serviceName, '') as serviceName, COALESCE(srm.stateID, '') as stateID,"
			+ " COALESCE(sfb.stateName, '') as stateName,"
			+ " COALESCE(spmf.serviceProviderName, '') as serviceProviderName,"
			+ " srm.deleted as deleted, srm.cTI_CampaignName,"
			+ " spmf.deleted as serviceProviderDeleted "
			+ " FROM M_Providerservicemapping_Blocking srm"
			+ " LEFT JOIN srm.m_ServicemasterForBlocking sm LEFT JOIN srm.m_StateForBlocking sfb"
			+ " LEFT JOIN srm.serviceProvider_ModelForBlocking spmf where srm.serviceProviderID = :serviceProviderID"
			+ " ORDER BY spmf.serviceProviderName")
	ArrayList<Object[]> getServiceLiensUsingProvider1(@Param("serviceProviderID") Integer serviceProviderID);

	@Query("select providerMap from M_Providerservicemapping_Blocking providerMap where "
			+ "providerServiceMapID in :providerServiceMapIDs")
	List<M_Providerservicemapping_Blocking> findM_Providerservicemapping_BlockingByProviderServiceMapIDs(
			@Param("providerServiceMapIDs") List<Integer> providerServiceMapID);

	@Transactional
	@Modifying
	@Query(" UPDATE  M_Providerservicemapping_Blocking u SET u.apimanClientID=:apimanClientID, u.apimanClientKey = :apimanClientKey where  u.providerServiceMapID=:providerServiceMapID")
	void updateAPIMAN(@Param("apimanClientID")String id,@Param("apimanClientKey") String string,@Param("providerServiceMapID") Integer providerServiceMapID);

	List<M_Providerservicemapping_Blocking> findByProviderServiceMapIDIn(ArrayList<Integer> ids);

}
