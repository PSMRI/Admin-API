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
package com.iemr.admin.service.employeemaster;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.employeemaster.M_Community;
import com.iemr.admin.data.employeemaster.M_Gender;
import com.iemr.admin.data.employeemaster.M_ProviderServiceMap1;
import com.iemr.admin.data.employeemaster.M_Religion;
import com.iemr.admin.data.employeemaster.M_Role;
import com.iemr.admin.data.employeemaster.M_Title;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_UserDemographics;
import com.iemr.admin.data.employeemaster.M_UserLangMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.employeemaster.M_Userqualification;
import com.iemr.admin.data.employeemaster.Showofficedetails1;
import com.iemr.admin.data.employeemaster.Showuserdetailsfromuserservicerolemapping;
import com.iemr.admin.data.employeemaster.V_Showuser;
import com.iemr.admin.data.employeemaster.V_Userservicerolemapping;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.UserRole;

public interface EmployeeMasterInter {

	ArrayList<M_Role> getAllRole();

	ArrayList<M_UserServiceRoleMapping2> getEmployeeDetails();

	public M_User1 editEmployee(Integer userID);

	public M_User1 saveEditData(M_User1 editdata);

	ArrayList<M_UserServiceRoleMapping2> getEmployeeDetails1();

	Integer saveEmployee(M_User1 m_UserList);

	Integer saveDemography(M_UserDemographics ud);

	ArrayList<M_UserLangMapping> mapLanguage(List<M_UserLangMapping> resList);

	ArrayList<M_UserServiceRoleMapping2> mapRole(List<M_UserServiceRoleMapping2> resList1, String authToken);

	M_UserDemographics mdedit(Integer userID);

	M_UserServiceRoleMapping2 uRoleMedit(Integer userID, Integer newRoleID);

	M_UserLangMapping ulangmapedit(Integer userID, Integer languageID);

	Integer saveeditDemo(M_UserDemographics ud1);

	Integer saveeditlangdata(M_UserLangMapping mulm);

	M_UserServiceRoleMapping2 saveRoleEdit(M_UserServiceRoleMapping2 musrm);

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails2(Integer serviceProviderID,
			Integer pSMStateID);

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails3(Integer serviceProviderID, Integer roleID);

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails4(Integer serviceProviderID,
			Integer serviceID);

	ArrayList<V_Showuser> getEmployeeDetails5();

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails6(Integer serviceProviderID, Integer userID);

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails7(Integer serviceProviderID,
			Integer pSMStateID, Integer workingDistrictID);

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails8(Integer serviceProviderID,
			Integer getpSMStateID, Integer workingDistrictID, Integer workingLocationID);

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails9(Integer serviceProviderID,
			Integer getpSMStateID, Integer roleID);

	ArrayList<M_Title> getAllTitle();

	ArrayList<M_Gender> getAllGender();

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails10(Integer serviceProviderID,
			Integer getpSMStateID, Integer roleID, Integer serviceID, String userName, Integer userID);

	ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails11(Integer serviceProviderID,
			Integer pSMStateID, Integer serviceID, Integer roleID, String userName, Integer userID);

	ArrayList<M_ProviderServiceMap1> getAllByMapId2(Integer serviceProviderID, Integer stateID, Integer serviceID);

	ArrayList<Showofficedetails1> getlocationByMapid2(int tempProSerStatMapID, Integer districtID);

	String FindEmployeeName(String userName);

	public M_User1 FindEmployeeName1(String userName);

	ArrayList<M_Userqualification> getQualification();

	String createAgent(String agentID, String st);

	void createUserInCallCentre(M_User1 user, String authToken);

	Boolean checkingEmpDetails(String userName, String aadhaarNo, String getpAN, String employeeID,
			String healthProfessionalID);

	ArrayList<M_UserServiceRoleMapping2> mapRoleUpdation(List<M_UserServiceRoleMapping2> resList1);

	M_UserServiceRoleMapping2 uRoledelte(Integer getuSRMappingID);

	String mapctiAgent(List<M_UserServiceRoleMapping2> ctidata) throws Exception;

	String ResetPassword(M_User1 employeeMaster);

	ArrayList<M_User1> createProviderAdmin(List<M_User1> providerAdminData) throws Exception;

	ArrayList<M_User1> getProviderAdmin();

	M_User1 getProviderAdminForEdit(Integer userID);

	M_User1 saveeditedData(M_User1 getProviderAdminFromDB);

	ArrayList<M_User1> createNewUser(List<M_User1> newUserData) throws Exception;

	ArrayList<M_UserDemographics> SaveDemographics(ArrayList<M_UserDemographics> demogdata);

	M_User1 editData(Integer userID);

	M_UserDemographics DataByUserID(Integer userID);

	M_UserDemographics saveeditedDemoData(M_UserDemographics getdemographicsData);

	M_UserLangMapping updateLangMapping(Integer userLangID);

	M_UserLangMapping saveUserLangEditedData(M_UserLangMapping empMaster);

	M_UserServiceRoleMapping2 getDataUsrId(Integer uSRMappingID);

	M_UserServiceRoleMapping2 saveRoleMappingeditedData(M_UserServiceRoleMapping2 usrRole, String string);

	// ArrayList<M_UserLangMapping> getMappedLanguge();

	ArrayList<V_Userservicerolemapping> getMappedRole(Integer serviceProviderID);

	ArrayList<V_Userservicerolemapping> getMappedRole(String name, Integer userID);

	ArrayList<M_UserLangMapping> searchMappedLangugeByUserId(Integer userID);

	ArrayList<V_Showuser> getcompleteUserDetails();

	ArrayList<M_Religion> getAllReligion();

	ArrayList<M_Community> getAllCommunity();

	ArrayList<V_Showuser> getEmployeeDetails4(Integer serviceProviderID);

	ArrayList<M_UserLangMapping> getMappedLanguge(Integer serviceProviderID);

	void updateUserInCallCentre(String userName, String ctiRole, String authToken);

	ArrayList<M_User1> getEmployeeByDesiganationID(Integer designationID, Integer serviceProviderID);

	void expireAuth(M_User1 editedData, String auth);

	ArrayList<UserRole> getUserRoleTM(M_UserservicerolemappingForRoleProviderAdmin getRoleData);

	M_UserServiceRoleMapping2 deleteuserrolemapTM(M_UserServiceRoleMapping2 pre) throws Exception;

    M_User1 saveBulkUserEmployee(M_User1 mUser);
}
