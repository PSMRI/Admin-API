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

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.M_User1;

import jakarta.transaction.Transactional;

@Repository
public interface EmployeeMasterRepoo extends CrudRepository<M_User1, Integer>
{

	@Query("SELECT u FROM M_User1 u WHERE u.userID=:userID  AND deleted=0")
	M_User1 editEmployee(@Param("userID") Integer userID);

	@Query("SELECT u FROM M_User1 u WHERE u.userName=:userName AND deleted=0 ")
	M_User1 findEmployeeByName(@Param("userName") String userName);

	@Query("SELECT u FROM M_User1 u WHERE u.userName=:userName OR u.aadhaarNo=:aadhaarNo OR u.pAN=:getpAN OR u.employeeID=:employeeID OR u.healthProfessionalID=:healthProfessionalID AND deleted=0 ")
	M_User1 checkingEmpDetails(@Param("userName") String userName, @Param("aadhaarNo") String aadhaarNo,
			@Param("getpAN") String getpAN,@Param("employeeID") String employeeID, @Param("healthProfessionalID") String healthProfessionalID);

	/*
	 * @Transactional
	 * 
	 * @Modifying
	 * 
	 * @Query(
	 * " UPDATE  M_Providerservicemapping_Blocking u SET u.statusID=:statusID where u.serviceProviderID = :serviceProviderID AND u.serviceID=:serviceID"
	 * ) void blockProviderByProviderIdAndServiceId(@Param("serviceProviderID")Integer
	 * serviceProviderID, @Param("serviceID") Integer serviceID,@Param("statusID") Integer statusID);
	 */

	@Transactional
	@Modifying
	@Query(" UPDATE  M_User1 u SET u.statusID=:statusID where u.userName =:userName")
	int resetpassword(@Param("userName") String userName, @Param("statusID") Integer statusID);

	@Query("Select u From M_User1 u WHERE u.isProviderAdmin=1 ORDER BY u.userName")
	ArrayList<M_User1> getAllProviderAdminData();
	// ArrayList<M_User1> getAllProviderAdminData();

	@Query(" Select u.userName From M_User1 u WHERE u.userID=:userID")
	String getUserNameByUserID(@Param("userID") Integer userID);
  
	
	
	/*@Query(value= " Select us.userName,us.userID,us.desiganationID"
			+ " From M_USRRoleMapping USR "
			+ " LEFT JOIN M_User1 us"
			+ " Where USR.ProviderServiceMapID=:providerServiceMapID AND us.",nativeQuery=true)*/
	@Query("SELECT u FROM M_User1 u WHERE u.designationID=:designationID AND u.serviceProviderID=:serviceProviderID AND u.isProviderAdmin=NULL")
	ArrayList<M_User1> getempByDesiganation(@Param("designationID") Integer designationID,@Param("serviceProviderID") Integer serviceProviderID);
	
	M_User1 findByUserID(Integer userID);
}
