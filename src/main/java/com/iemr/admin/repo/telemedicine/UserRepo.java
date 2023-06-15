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
package com.iemr.admin.repo.telemedicine;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.telemedicine.M_UserTemp;

@Repository
@RestResource(exported = false)
public interface UserRepo extends CrudRepository<M_UserTemp, Long> {

	/** Get the list of users for supervisor or tc specialist or doctor in TM service line
	 for that provider not providerservicemapid
	 **/
	@Query(value = "select * from m_user usr "
			+ "join m_userservicerolemapping usrm on usrm.UserID=usr.UserID "
			+ "join m_role role on role.RoleID=usrm.RoleID "
			+ "join m_servicerolescreenmapping srm on srm.RoleID=usrm.RoleID "
			+ "join m_screen scrn on scrn.ScreenID=srm.ScreenID "
			+ "where usr.ServiceProviderID=:serviceproviderID and scrn.ServiceID=4 "
			+ " and scrn.ScreenName=:screenName group by usr.UserID ", nativeQuery = true)
	ArrayList<M_UserTemp> getUserTM(@Param("serviceproviderID")Integer serviceproviderID,@Param("screenName") String screenName);

	@Query("select new M_UserTemp( u,swy.userSwymedMapID) from M_UserTemp u left join u.userSwymed swy where swy.userSwymedMapID is null "
			+ " and u.ServiceProviderID=:serviceproviderID and u.designationID=:designationID and u.Deleted=false")
	ArrayList<M_UserTemp> getunmappedSwymedUser(@Param("serviceproviderID")Integer serviceproviderID,@Param("designationID")Integer designationID);
}
