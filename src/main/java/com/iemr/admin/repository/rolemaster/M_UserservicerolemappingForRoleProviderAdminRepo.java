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
package com.iemr.admin.repository.rolemaster;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;


@Repository
@RestResource(exported = false)
public interface M_UserservicerolemappingForRoleProviderAdminRepo
		extends CrudRepository<M_UserservicerolemappingForRoleProviderAdmin, Integer> {

	/*
	 * @Query("SELECT distinct rp.serviceID," +
	 * " sm.serviceName as serviceName," + " sm.isNational as isNational," +
	 * "rp.statusID" + " FROM StateServiceMapping rp " +
	 * " JOIN rp.serviceMaster sm " +
	 * " WHERE rp.serviceProviderID =:serviceProviderID AND rp.deleted=0")
	 * ArrayList<Object[]> getServiceByServiceProviderIds(Integer
	 * serviceProviderID);
	 * 
	 * 
	 * ArrayList<Object[]> getStateByServiceProviderIdAndServiceLines(Integer
	 * serviceProviderID, Integer serviceID);
	 */

	@Query("SELECT sm.serviceName as serviceName," + " ssm.serviceID," + " sm.isNational as isNational,"
			+ " ssm.statusID" + " FROM M_UserservicerolemappingForRoleProviderAdmin usr "
			+ " JOIN usr.stateServiceMapping ssm " + " JOIN ssm.serviceMaster sm "
			+ " WHERE usr.userID =:userID AND ssm.statusID=2 AND usr.deleted=0" + "GROUP BY sm.serviceName")
	ArrayList<Object[]> getServiceByServiceProviderIds(@Param("userID") Integer serviceProviderID);

	@Query("SELECT distinct ssm.stateID," + " sm.stateName, " + " ssm.providerServiceMapID," + " ssm.statusID"
			+ " FROM M_UserservicerolemappingForRoleProviderAdmin usr" + " JOIN usr.stateServiceMapping ssm "
			+ " JOIN ssm.stateMaster sm "
			+ " WHERE usr.userID =:userID AND ssm.serviceID=:serviceID AND ssm.statusID=2 AND usr.deleted=0 and ssm.deleted=false ORDER BY sm.stateName ")
	ArrayList<Object[]> getStateByServiceProviderIdAndServiceLines(@Param("userID") Integer userID,
			@Param("serviceID") Integer serviceID);

	@Query("SELECT  usr.providerServiceMapID" + " FROM M_UserservicerolemappingForRoleProviderAdmin usr"
			+ " JOIN usr.stateServiceMapping ssm "
			+ " WHERE usr.userID =:userID AND ssm.serviceID=:serviceID AND usr.deleted=0 ")
	ArrayList<Object[]> getStateByServiceProviderIdAndServiceLines1(@Param("userID") Integer userID,
			@Param("serviceID") Integer serviceID);

	@Query(value="Select urm.UserID,srm.roleid,rolename,ro.deleted roledeleted,srm.screenID,screenName,sc.deleted screendeleted from m_userservicerolemapping urm"
	+" join M_role ro on urm.RoleID=ro.RoleID"
			+" join m_servicerolescreenmapping srm on srm.RoleID=ro.RoleID"
	+" inner join m_screen sc on srm.ScreenID=sc.ScreenID "
			+" where srm.ProviderServiceMapId=:serviceID AND urm.userid=:userID and SC.DELETED=FALSE and urm.Deleted=false and ro.deleted=false and srm.deleted=false"
	,nativeQuery = true)
	ArrayList<Object[]> getroleofuserTM(@Param("userID") Integer userID, @Param("serviceID") Integer serviceID);
	
	@Query(value="Select screenName from m_userservicerolemapping urm"
			+" join M_role ro on urm.RoleID=ro.RoleID"
					+" join m_servicerolescreenmapping srm on srm.RoleID=ro.RoleID"
			+" inner join m_screen sc on srm.ScreenID=sc.ScreenID "
					+" where  urm.USRMappingID=:usermapID and SC.DELETED=FALSE  and ro.deleted=false and srm.deleted=false"
			,nativeQuery = true)
			Object getroleofuserTMOne(@Param("usermapID") Integer usermapID);

}
