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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.Showuserdetailsfromuserservicerolemapping;


@Repository
public interface ShowuserdetailsfromuserservicerolemappingRepo extends CrudRepository<Showuserdetailsfromuserservicerolemapping, Integer> {

	@Query("SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.pSMStateID=:pSMStateID AND deleted=0")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails2(@Param ("serviceProviderID") Integer serviceProviderID,
			@Param ("pSMStateID")Integer pSMStateID);
	
	
	
	@Query("SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.roleID=:roleID AND deleted=0")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails3(@Param ("serviceProviderID")Integer serviceProviderID, @Param ("roleID") Integer roleID);


	@Query("SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.serviceID=:serviceID AND deleted=0")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails4(@Param ("serviceProviderID")Integer serviceProviderID, @Param ("serviceID") Integer serviceID);



	
	@Query("SELECT DISTINCT  u FROM Showuserdetailsfromuserservicerolemapping u")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails5();
	//ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails5(Integer serviceProviderID, String userName);


	@Query("SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.userID=:userID AND deleted=0")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails6(@Param ("serviceProviderID")Integer serviceProviderID, @Param ("userID") Integer userID);
	//ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails6(Integer serviceProviderID, Integer userID);


     
	
	
	@Query("SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.pSMStateID=:pSMStateID AND u.workingDistrictID=:workingDistrictID AND deleted=0")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails7(@Param ("serviceProviderID") Integer serviceProviderID,
			@Param ("pSMStateID") Integer pSMStateID,@Param ("workingDistrictID") Integer workingDistrictID );
	//ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails8(Integer serviceProviderID, Integer pSMStateID,
			//Integer workingDistrictID);


	@Query("SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.pSMStateID=:pSMStateID AND u.workingDistrictID=:workingDistrictID AND u.workingLocationID=:workingLocationID AND deleted=0")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails8(@Param ("serviceProviderID") Integer serviceProviderID,
			@Param ("pSMStateID") Integer pSMStateID,@Param ("workingDistrictID") Integer workingDistrictID,@Param ("workingLocationID") Integer workingLocationID );


	@Query("SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.pSMStateID=:pSMStateID AND u.roleID=:roleID AND deleted=0")
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails9(@Param("serviceProviderID") Integer serviceProviderID,@Param("pSMStateID") Integer pSMStateID,
			@Param("roleID") Integer roleID);
	
	
	
	

//    @Procedure(name="PR_FetchUserDetails")
//    ArrayList<Showuserdetailsfromuserservicerolemapping> FetchUserDetails(@Param ("serviceProviderID") Integer serviceProviderID);
//	
//    
//    
//    
    
    
    
     
	/*@Query(value="PR_FetchUserDetails(null,null,null,null,null,null)", nativeQuery = true )
	ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails11(@Param ("serviceProviderID")Integer serviceProviderID,
			@Param ("pSMStateID") Integer pSMStateID, @Param ("roleID") Integer roleID,@Param ("serviceID") Integer serviceID,@Param("userName") String userName,@Param ("userID") Integer userID);*/
	
	@Query(" SELECT u FROM Showuserdetailsfromuserservicerolemapping u WHERE u.serviceProviderID=:serviceProviderID AND u.pSMStateID=:pSMStateID OR u.pSMStateID=:pSMStateID  AND  u.roleID=:roleID  OR u.roleID=:roleID AND u.serviceID=:serviceID OR u.serviceID=:serviceID AND u.userName=:userName OR u.userName=:userName AND u.userID=:userID OR u.userID=:userID AND deleted=0")
	 ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails10(@Param ("serviceProviderID")Integer serviceProviderID,
			@Param ("pSMStateID") Integer pSMStateID, @Param ("roleID") Integer roleID,@Param ("serviceID") Integer serviceID,@Param("userName") String userName,@Param ("userID") Integer userID);
	
	
	
	/*ArrayList<Showuserdetailsfromuserservicerolemapping> EmployeeDetails8(Integer serviceProviderID, Integer pSMStateID,
			Integer workingDistrictID, Integer workingLocationID);
*/	

}
