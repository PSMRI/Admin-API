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
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;

@Repository
@RestResource(exported = false)
public interface EmployeeMasterRepo extends CrudRepository<M_UserServiceRoleMapping2, Integer> {

	@Query("SELECT distinct srm.userID, srm.roleID,"
			+ " concat(em.firstName, ' ', em.middleName, ' ', em.lastName) as EmployeeName,"
			// + " sm.stateName as stateName,"
			+ " rm.roleName as roleName,"
			// + " smm.serviceName as serviceName,"
			// + " srm.serviceProviderID,"
			// + " sp.serviceProviderName as serviceProviderName,"
			+ " srm.workingLocationID," + " psa.locationName as locationName," + " psa.address as address"
			// + " ud.fathersName as fathersName"
			// + " c.cityName as cityName "
			+ " FROM M_UserServiceRoleMapping2 srm " + " JOIN srm.employeeMaster em "
			// + " JOIN srm.stateMaster sm "
			+ " JOIN srm.mRole rm "
			// + " JOIN srm.serviceMaster smm "
			// + " JOIN srm.m_ServiceProvider sp"
			+ " JOIN srm.m_ProviderServiceAddMapping psa"
	// + " INNER JOIN srm.m_UserDemographics ud "
	// + " INNER JOIN ud.m_City c "
	// + " WHERE ud.userID = srm.userID"
	)
	ArrayList<Object[]> getEmployeeDetails();

	@Query("SELECT distinct srm.userID, srm.roleID,"
			+ " concat(em.firstName, ' ', em.middleName, ' ', em.lastName) as EmployeeName,"
			// + " sm.stateName as WorkinglocationstateName,"
			+ " rm.roleName as roleName,"
			// + " smm.serviceName as serviceName,"
			// + " srm.serviceProviderID,"
			// + " sp.serviceProviderName as serviceProviderName,"
			+ " srm.workingLocationID," + " psa.locationName as locationName," + " psa.address as address,"
			+ " ud.fathersName as fathersName," + " ud.mothersName as mothersName,"
			+ " ud.addressLine1 as addressLine1," + " ud.addressLine2 as addressLine2,"
			+ " ud.demographicID as demographicID," + " ud.cityID as cityID ," + " ud.stateID as UstateID,"
			+ " ud.countryID as countryID," + " ud.religionID as religionID," + " ud.communityID as communityID,"
			+ " c.cityName as cityName ," + " ms.stateName as stateName," + " mc.countryName as countryName,"
			+ " mr.religionType as religionType," + " mcc.communityType as communityType,"
			+ " mulm.userLangID as userLangID," + " mulm.weightage as weightage," + " mulm.languageID as languageID,"
			+ " ml.languageName as languageName," + " em.titleID as titleID," + " em.genderID as genderID,"
			+ " em.maritalStatusID as maritalStatusID," + " em.aadhaarNo as aadhaarNo," + " em.pAN as pAN,"
			+ " em.dOB as dOB," + " em.dOJ as dOJ," + " em.qualificationID as qualificationID,"
			+ " em.userName as userName," + " em.agentID as agentID," + " em.emailID as emailID,"
			+ " em.statusID as statusID," + " em.emergencyContactPerson as emergencyContactPerson,"
			+ " em.emergencyContactNo as emergencyContactNo," + " mt.titleName as titleName,"
			+ " mt.titleDesc as titleDesc," + " mss.status as status," + " mss.statusDesc as statusDesc,"
			+ " mss.statusFor as statusFor," + " muq.name as qualificationName,"
			+ " muq.userQualificationDesc as userQualificationDesc," + " mg.genderName as genderName,"
			+ " mms.status as maritalStatus," + " mms.statusDesc as maritalStatusDesc "
			+ " FROM M_UserServiceRoleMapping2 srm " + " LEFT JOIN srm.employeeMaster em "
			// + " LEFT JOIN srm.stateMaster sm "
			+ " LEFT JOIN srm.mRole rm "
			// + " LEFT JOIN srm.serviceMaster smm "
			// + " LEFT JOIN srm.m_ServiceProvider sp"
			+ " LEFT JOIN srm.m_ProviderServiceAddMapping psa" + " LEFT JOIN srm.m_UserDemographics ud "
			+ " LEFT JOIN ud.m_City c " + " LEFT JOIN ud.mstate ms " + " LEFT JOIN ud.mcountry mc "
			+ " LEFT JOIN ud.m_Religion mr" + " LEFT JOIN ud.m_Community mcc" + " LEFT JOIN srm.m_UserLangMapping mulm "
			+ " LEFT JOIN mulm.m_Language ml" + " LEFT JOIN em.m_Title mt" + " LEFT JOIN em.m_Status mss"
			+ " LEFT JOIN em.m_Userqualification muq" + " LEFT JOIN em.m_Gender mg"
			+ " LEFT JOIN em.m_Maritalstatus mms"
	// + " WHERE mulm.userID = srm.userID "

	)

	ArrayList<Object[]> getEmployeeDetails1();

	@Query("SELECT u FROM M_UserServiceRoleMapping2 u WHERE u.userID=:userID AND u.roleID=:roleID AND deleted=0")
	M_UserServiceRoleMapping2 uRoleMedit(@Param("userID") Integer userID, @Param("roleID") Integer roleID);

	@Query("SELECT u FROM M_UserServiceRoleMapping2 u WHERE u.uSRMappingID=:getuSRMappingID AND deleted=0")
	M_UserServiceRoleMapping2 uRoledelte(@Param("getuSRMappingID") Integer getuSRMappingID);

	@Transactional
	@Modifying
	@Query(" UPDATE  M_UserServiceRoleMapping2 u SET u.agentID=:agentID ,u.agentP=:agentP where u.uSRMappingID = :getuSRMappingID")
	// void createcitmapping(@Param("providerServiceMapID")Integer
	// providerServiceMapID,
	void createcitmapping(@Param("getuSRMappingID") Integer getuSRMappingID, @Param("agentID") String agentID,
			@Param("agentP") String agentP);

	@Query("SELECT em.userID,em.userName FROM M_UserServiceRoleMapping2 u left join u.employeeMaster em WHERE u.providerServiceMapID=:pid AND u.deleted=0 and em.deleted=false and em.designationID=:did group by u.userID")
	List<Object[]> getAllEmpByProviderServiceMapIDAndDesignation(@Param("pid") Integer providerServiceMapID,
			@Param("did") Integer designationID);

	@Query("SELECT em.userID,em.userName FROM M_UserServiceRoleMapping2 u left join u.employeeMaster em WHERE u.providerServiceMapID=:pid AND u.deleted=0 and em.deleted=false and em.designationID=:did and u.userID not in (:uid) group by u.userID")
	List<Object[]> getAllEmpByProviderServiceMapIDAndDesignationNotInUserID(@Param("pid") Integer providerServiceMapID,
			@Param("did") Integer designationID, @Param("uid") List<Integer> allids);

}
