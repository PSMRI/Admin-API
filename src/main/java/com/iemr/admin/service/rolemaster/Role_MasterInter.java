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
package com.iemr.admin.service.rolemaster;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.rolemaster.*;

public interface Role_MasterInter {

	ArrayList<StateServiceMapping> getStateByServiceProviderId(Integer serviceProviderID);

	ArrayList<StateServiceMapping> getServiceByServiceProviderIdAndStateId(Integer serviceProviderID, Integer stateID);

	List<RoleMaster> getAllRoleByMapId();

	ArrayList<StateServiceMapping> getAllByMapId(Integer serviceProviderID, Integer stateID, Integer serviceID,Boolean isNational);

	public ArrayList<RoleMaster> getProStateServRoles(int pssmID);

	public List<RoleMaster> addRole(List<RoleMaster> mRole);

	public RoleMaster getRoleByRoleId(Integer roleID);

	public RoleMaster  modifydata(RoleMaster editdata);

	public String deletedata(RoleMaster deleteData);

	ArrayList<M_Screen> getAllFeature(Integer serviceID);

	List<RoleScreenMapping> mapScreen(RoleScreenMapping mRoles2);

	String settingScreenId(Integer sRSMappingID, Integer screenID);

	public ArrayList<RoleMaster> getProStateServRoles1(int pssmID);

	List<RoleScreenMapping> mapfeature(List<RoleScreenMapping> mRoles3);

	ArrayList<M_UserservicerolemappingForRoleProviderAdmin> getServiceByServiceProviderIds(Integer userID);

	ArrayList<M_UserservicerolemappingForRoleProviderAdmin> getStateByServiceProviderIdAndServiceLines(Integer userID,
																									   Integer serviceID,Boolean isNational);

	ArrayList<StateServiceMapping> getAllByMapId(Integer serviceProviderID, Integer serviceID);

	ArrayList<RoleMaster> getProStateServRolesV1(int pssmID);

	List<RoleMaster> getRoleMasterTM(Integer providerServiceMapID);

	ArrayList<RoleMaster> getProStateServRolesActive(Integer providerServiceMapID);

	RoleMaster configWrapUpTime(RoleMaster role) throws Exception;

	ArrayList<StateMasterForRole> getAllState();





}
