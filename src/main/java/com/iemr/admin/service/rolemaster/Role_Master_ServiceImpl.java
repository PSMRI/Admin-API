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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.rolemaster.RoleMaster;
import com.iemr.admin.data.rolemaster.M_Role104;
import com.iemr.admin.data.rolemaster.M_Screen;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.RoleScreenMapping;
import com.iemr.admin.data.rolemaster.StateMasterForRole;
import com.iemr.admin.data.rolemaster.StateServiceMapping;
import com.iemr.admin.repository.rolemaster.M_RoleRepo;
import com.iemr.admin.repository.rolemaster.M_ScreenRepo;
import com.iemr.admin.repository.rolemaster.M_UserservicerolemappingForRoleProviderAdminRepo;
import com.iemr.admin.repository.rolemaster.RoleMasterRepo;
import com.iemr.admin.repository.rolemaster.RoleScreenMappingRepo;
import com.iemr.admin.repository.rolemaster.StateMasterRepo;

import jakarta.persistence.EntityManager;

@Service
public class Role_Master_ServiceImpl implements Role_MasterInter
{

	@Autowired
	private StateMasterRepo stateMasterRepo;

	@Autowired
	EntityManager entityManager;

	private Logger logger = LoggerFactory.getLogger(Role_Master_ServiceImpl.class);

	@Autowired
	private M_UserservicerolemappingForRoleProviderAdminRepo m_UserservicerolemappingForRoleProviderAdminRepo;

	@Autowired
	private RoleScreenMappingRepo roleScreenMappingRepo;
	
	@Autowired
	private M_ScreenRepo m_ScreenRepo;

	@Autowired
	private RoleMasterRepo roleMasterRepo;
	@Autowired
	private M_RoleRepo mRoleRepo;

	@Override
	public ArrayList<StateServiceMapping> getStateByServiceProviderId(Integer serviceProviderID)
	{
		logger.debug("for getting state calling StateByServiceProviderId " + serviceProviderID);
		ArrayList<StateServiceMapping> stateServiceMappings = new ArrayList<StateServiceMapping>();
		ArrayList<Object[]> resultSet = roleMasterRepo.getStateByServiceProviderId(serviceProviderID);
		for (Object[] objects : resultSet)
		{
			if (objects != null && objects.length >= 2)
			{
				stateServiceMappings
						.add(new StateServiceMapping((Integer) objects[0], (String) objects[1], (Integer) objects[2]));
			}

			logger.debug("for getting state " + resultSet);
		}
		logger.debug("getting response with stateid " + stateServiceMappings);
		return stateServiceMappings;
	}

	@Override
	public ArrayList<StateServiceMapping> getServiceByServiceProviderIdAndStateId(Integer serviceProviderID,
			Integer stateID)
	{
		// TODO Auto-generated method stub
		logger.debug(" calling method with  " + serviceProviderID, stateID);
		ArrayList<StateServiceMapping> stateServiceMappings = new ArrayList<StateServiceMapping>();
		ArrayList<Object[]> resultSet =
				roleMasterRepo.getServiceByServiceProviderIdAndStateId(serviceProviderID, stateID);
		for (Object[] objects : resultSet)
		{
			if (objects != null && objects.length >= 2)
			{
				stateServiceMappings.add(new StateServiceMapping((Integer) objects[0], (Integer) objects[1],
						(String) objects[2], (Integer) objects[3]));
			}

			logger.debug("for getting service and providerServiceMapid " + resultSet);
		}

		logger.debug("getting response with serviceid and Spm mapId " + stateServiceMappings);
		return stateServiceMappings;
	}

	@Override
	public List<RoleMaster> getAllRoleByMapId()
	{
		ArrayList<RoleMaster> resList = null;
		return resList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<StateServiceMapping> getAllByMapId(Integer serviceProviderID, Integer stateID, Integer serviceID,
			Boolean isNational)
	{
		ArrayList<StateServiceMapping> resSet = new ArrayList<StateServiceMapping>();

		if (isNational == false)
		{
			resSet = (ArrayList<StateServiceMapping>) roleMasterRepo.getAllByMapId(serviceProviderID, stateID,
					serviceID);
		} else
		{
			resSet = (ArrayList<StateServiceMapping>) roleMasterRepo.getAlByMapId(serviceProviderID, serviceID);

		}

		logger.debug("getting response with serviceid and Spm mapId " + resSet);
		return resSet;
	}

	@Override
	public ArrayList<RoleMaster> getProStateServRoles(int pssmID)
	{
		ArrayList<RoleMaster> stateServiceMappings = new ArrayList<RoleMaster>();
		ArrayList<Object[]> resultSet = roleScreenMappingRepo.getAllRoleByMapId(pssmID);
		for (Object[] objects : resultSet)
		{
			if (objects != null && objects.length >= 2)
			{
				stateServiceMappings.add(new RoleMaster((Integer) objects[0], (String) objects[1], (String) objects[2],
						(Boolean) objects[3], (String) objects[4], (Integer) objects[5]));
			}
		}
		return stateServiceMappings;
	}

	@Override
	public ArrayList<RoleMaster> getProStateServRolesV1(int pssmID)
	{
		logger.info(" get all roles for service provider with map id " + pssmID);
		ArrayList<RoleMaster> stateServiceMappings = mRoleRepo.getAllRoleByMapId1(pssmID);
		logger.debug(" get all roles for service provider with map id " + stateServiceMappings);
		logger.info(" sending " + stateServiceMappings.size() + " roles for service provider with map id " + pssmID);
		return stateServiceMappings;
	}

	@Override
	public List<RoleMaster> addRole(List<RoleMaster> mRole)
	{
		List data = (List) mRoleRepo.saveAll(mRole);

		logger.debug("saved Role result is " + data);
		return data;

	}

	@Override
	public RoleMaster getRoleByRoleId(Integer roleID)
	{

		return mRoleRepo.getRoleByRoleId(roleID);
	}

	@Override
	public RoleMaster modifydata(RoleMaster editdata)
	{
		RoleMaster data = mRoleRepo.save(editdata);
		return data;

	}

	@Override
	public String deletedata(RoleMaster deleteData)
	{
		RoleMaster data = mRoleRepo.save(deleteData);

		return "success";

	}

	@Override
	public ArrayList<M_Screen> getAllFeature(Integer serviceID)
	{

		return m_ScreenRepo.getAllFeature(serviceID);
	}

	@Override
	public List<RoleScreenMapping> mapScreen(RoleScreenMapping mRoles2)
	{

		return (List<RoleScreenMapping>) roleScreenMappingRepo.save(mRoles2);
	}

	@Override
	public String settingScreenId(Integer sRSMappingID, Integer screenID)
	{
		int s = roleScreenMappingRepo.updatescreenId(sRSMappingID, screenID);
		if (s > 0)
			return "success";
		else
			return "fail";
	}

	@Override
	public ArrayList<RoleMaster> getProStateServRoles1(int pssmID)
	{

		ArrayList<RoleMaster> resList = mRoleRepo.getAllRoleByMapId1(pssmID);

		return resList;
	}

	@Override
	public List<RoleScreenMapping> mapfeature(List<RoleScreenMapping> mRoles3)
	{

		return (List<RoleScreenMapping>) roleScreenMappingRepo.saveAll(mRoles3);
	}

	@Override
	public ArrayList<M_UserservicerolemappingForRoleProviderAdmin> getServiceByServiceProviderIds(Integer userID)
	{

		// TODO Auto-generated method stub
		logger.debug(" calling method with  " + userID);
		ArrayList<M_UserservicerolemappingForRoleProviderAdmin> stateServiceMappings =
				new ArrayList<M_UserservicerolemappingForRoleProviderAdmin>();
		ArrayList<Object[]> resultSet =
				m_UserservicerolemappingForRoleProviderAdminRepo.getServiceByServiceProviderIds(userID);
		for (Object[] objects : resultSet)
		{
			if (objects != null && objects.length >= 2)
			{
				stateServiceMappings.add(new M_UserservicerolemappingForRoleProviderAdmin((String) objects[0],
						(Integer) objects[1], (Boolean) objects[2], (Integer) objects[3]));
			}

			logger.debug("for getting service and providerServiceMapid " + resultSet);
		}

		logger.debug("getting response with serviceid and Spm mapId " + stateServiceMappings);
		return stateServiceMappings;
	}

	@Override
	public ArrayList<M_UserservicerolemappingForRoleProviderAdmin>
			getStateByServiceProviderIdAndServiceLines(Integer userID, Integer serviceID, Boolean isNational)
	{
		if (isNational == false)
		{

			logger.debug("for getting state calling StateByServiceProviderId " + userID);
			ArrayList<M_UserservicerolemappingForRoleProviderAdmin> stateServiceMappings =
					new ArrayList<M_UserservicerolemappingForRoleProviderAdmin>();
			ArrayList<Object[]> resultSet = m_UserservicerolemappingForRoleProviderAdminRepo
					.getStateByServiceProviderIdAndServiceLines(userID, serviceID);
			for (Object[] objects : resultSet)
			{
				if (objects != null && objects.length >= 2)
				{
					stateServiceMappings.add(new M_UserservicerolemappingForRoleProviderAdmin((Integer) objects[0],
							(String) objects[1], (Integer) objects[2], (Integer) objects[3]));
				}

				logger.debug("for getting state " + resultSet);
			}
			logger.debug("getting response with stateid " + stateServiceMappings);
			return stateServiceMappings;
		} else
		{

			ArrayList<M_UserservicerolemappingForRoleProviderAdmin> stateServiceMappings1 =
					new ArrayList<M_UserservicerolemappingForRoleProviderAdmin>();
			ArrayList<Object[]> resultSet = m_UserservicerolemappingForRoleProviderAdminRepo
					.getStateByServiceProviderIdAndServiceLines1(userID, serviceID);
			Object obj = resultSet.get(0);
			// Integer in=new Integer((resultSet.get(0);
			// in.intValue();
			// int i=resultSet.get(0);
			// System.out.println(obj);
			ArrayList<StateMasterForRole> result = stateMasterRepo.getAllState();
			int x = 0;
			for (StateMasterForRole data : result)
			{
				stateServiceMappings1.add(new M_UserservicerolemappingForRoleProviderAdmin((Integer) obj,
						result.get(x).getStateID(), result.get(x).getStateName()));
				x++;
			}

			return stateServiceMappings1;
		}
	}

	@Override
	public ArrayList<StateServiceMapping> getAllByMapId(Integer serviceProviderID, Integer serviceID)
	{

		ArrayList<StateServiceMapping> resSet = new ArrayList<StateServiceMapping>();
		resSet = (ArrayList<StateServiceMapping>) roleMasterRepo.getAlByMapId(serviceProviderID, serviceID);
		logger.debug("getting response with serviceid and Spm mapId " + resSet);
		return resSet;

	}

	@Override
	public List<RoleMaster> getRoleMasterTM(Integer providerServiceMapID) {
		// TODO Auto-generated method stub
		ArrayList<RoleMaster> stateServiceMappings = new ArrayList<RoleMaster>();
		ArrayList<Object[]> resultSet = roleScreenMappingRepo.getAllRoleByMapId(providerServiceMapID);
		for (Object[] objects : resultSet)
		{
			if (objects != null && objects.length >= 2)
			{
				stateServiceMappings.add(new RoleMaster((Integer) objects[0], (String) objects[1], (String) objects[2],
						(Boolean) objects[3], (String) objects[4], (Integer) objects[5]));
			}
		}
		return stateServiceMappings;
	}

	@Override
	public ArrayList<RoleMaster> getProStateServRolesActive(Integer providerServiceMapID) {
		// TODO Auto-generated method stub
		return mRoleRepo.findByDeletedAndProviderServiceMapID(false,providerServiceMapID);
	}

	@Override
	public RoleMaster configWrapUpTime(RoleMaster role) throws Exception {
		// TODO Auto-generated method stub
		RoleMaster buff=mRoleRepo.findByRoleID(role.getRoleID());
		
		if(buff==null) {
			throw new Exception("Invalid Role");
		}
		if(role.getModifiedBy()==null) {
			throw new Exception("Please provide Modified by");
		}
		buff.setIsWrapUpTime(role.getIsWrapUpTime());
		buff.setWrapUpTime(role.getWrapUpTime());
		buff.setModifiedBy(role.getModifiedBy());
		
		return mRoleRepo.save(buff);
	}

}
