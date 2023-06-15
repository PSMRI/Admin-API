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

import com.iemr.admin.data.rolemaster.M_Role;
import com.iemr.admin.data.rolemaster.M_Role104;
import com.iemr.admin.data.rolemaster.M_Screen;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.RoleScreenMapping;
import com.iemr.admin.data.rolemaster.StateServiceMapping;

public interface Role_MasterInter {

	ArrayList<StateServiceMapping> getStateByServiceProviderId(Integer serviceProviderID);

	ArrayList<StateServiceMapping> getServiceByServiceProviderIdAndStateId(Integer serviceProviderID, Integer stateID);

	List<M_Role> getAllRoleByMapId();

	ArrayList<StateServiceMapping> getAllByMapId(Integer serviceProviderID, Integer stateID, Integer serviceID,Boolean isNational);

	public ArrayList<M_Role> getProStateServRoles(int pssmID);

	public List<M_Role> addRole(List<M_Role> mRole);

	public M_Role getRoleByRoleId(Integer roleID);

	public M_Role  modifydata(M_Role editdata);

	public String deletedata(M_Role deleteData);

	ArrayList<M_Screen> getAllFeature(Integer serviceID);

	List<RoleScreenMapping> mapScreen(RoleScreenMapping mRoles2);

	String settingScreenId(Integer sRSMappingID, Integer screenID);
	
	public ArrayList<M_Role> getProStateServRoles1(int pssmID);

	List<RoleScreenMapping> mapfeature(List<RoleScreenMapping> mRoles3);

	ArrayList<M_UserservicerolemappingForRoleProviderAdmin> getServiceByServiceProviderIds(Integer userID);

	ArrayList<M_UserservicerolemappingForRoleProviderAdmin> getStateByServiceProviderIdAndServiceLines(Integer userID,
			Integer serviceID,Boolean isNational);

	ArrayList<StateServiceMapping> getAllByMapId(Integer serviceProviderID, Integer serviceID);

	ArrayList<M_Role> getProStateServRolesV1(int pssmID);

	List<M_Role> getRoleMasterTM(Integer providerServiceMapID);

	ArrayList<M_Role> getProStateServRolesActive(Integer providerServiceMapID);

	M_Role configWrapUpTime(M_Role role) throws Exception;


	


}
