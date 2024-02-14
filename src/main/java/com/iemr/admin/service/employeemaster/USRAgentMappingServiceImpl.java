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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.employeemaster.M_ProviderServiceMap1;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.employeemaster.USRAgentMapping;
import com.iemr.admin.repo.employeemaster.USRAgentMappingRepository;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;

@Service
public class USRAgentMappingServiceImpl implements USRAgentMappingService
{

	InputMapper inputMapper = new InputMapper();

	Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	USRAgentMappingRepository usrAgentMappingRepository;

	@Autowired
	public void setUsrAgentMappingRepository(USRAgentMappingRepository usrAgentMappingRepository)
	{
		this.usrAgentMappingRepository = usrAgentMappingRepository;
	}

   @Override
	public List<USRAgentMapping> getAvailableAgentIds(String request) throws IEMRException
	{
		logger.info("USRAgentMappingServiceImpl.getAvailableAgentIds - start");
		USRAgentMapping requestObj = inputMapper.gson().fromJson(request, USRAgentMapping.class);
		logger.debug("getAvailableAgentIds request is " + request);
		List<USRAgentMapping> results = new ArrayList<USRAgentMapping>();
		Set<Objects[]> resultSet = usrAgentMappingRepository.getFreeAgentIds(requestObj.getCti_CampaignName(),
				requestObj.getProviderServiceMapID());
		for (Object[] result : resultSet)
		{
			logger.info("inside for result -> " + result.toString());
			if (result != null && result.length >= 9)
			{
				results.add(USRAgentMapping.initializeAllUSRAgentMapping((Integer) result[0], (Integer) result[1],
						(M_UserServiceRoleMapping2) result[2], (Integer) result[3], (M_ProviderServiceMap1) result[4],
						(String) result[5], (String) result[6], (String) result[7], (Boolean) result[8]));
			}
		}
		logger.info("getAvailableAgentIds result is " + results);
		logger.info("USRAgentMappingServiceImpl.getAvailableAgentIds - finish");
		return results;
	}

	@Override
	public Integer updateAgentIds(String request) throws IEMRException
	{
		logger.info("USRAgentMappingServiceImpl.updateAgentIds - start");
		USRAgentMapping requestObj = inputMapper.gson().fromJson(request, USRAgentMapping.class);
		logger.debug("updateAgentIds request is " + request);
		if (requestObj.getOldAgentID() != null && requestObj.getProviderServiceMapID() != null)
		{
			usrAgentMappingRepository.updateUSRMapping(true, null, requestObj.getOldAgentID(),
					requestObj.getProviderServiceMapID());
		}
		int updateCount = usrAgentMappingRepository.updateUSRMapping(requestObj.getIsAvailable(),
				requestObj.getUsrMappingID(), requestObj.getUsrAgentMappingID());
		logger.debug("updateAgentIds result is " + updateCount);
		logger.info("USRAgentMappingServiceImpl.updateAgentIds - finish. updCount = " + updateCount);
		return updateCount;
	}

	@Override
	public List<USRAgentMapping> createUSRAgentMapping(String request) throws IEMRException
	{
		logger.info("USRAgentMappingServiceImpl.createUSRAgentMapping - start");
		USRAgentMapping[] requestObjs = inputMapper.gson().fromJson(request, USRAgentMapping[].class);
		logger.debug("getAvailableCampaigns request is " + request);
		ArrayList<USRAgentMapping> results = new ArrayList<USRAgentMapping>();
		for (int index = 0; index < requestObjs.length; index++)
		{
			long agents = usrAgentMappingRepository.getExistingAgent(requestObjs[index].getProviderServiceMapID(),
					requestObjs[index].getAgentID());
			logger.info("# of available agents = " + agents);
			if (agents == 0)
			{
				results.add(usrAgentMappingRepository.save(requestObjs[index]));
			}
		}
		logger.info("USRAgentMappingServiceImpl.createUSRAgentMapping - finish. results = " + results);
		return results;
	}

	@Override
	public List<String> getAvailableCampaigns(String request) throws IEMRException
	{
		USRAgentMapping requestObj = inputMapper.gson().fromJson(request, USRAgentMapping.class);
		logger.debug("getAvailableCampaigns request is " + request);
		List<String> results = usrAgentMappingRepository.getAvailableCampaigns(requestObj.getProviderServiceMapID());
		logger.info("# of available campaigns = " + results);
		logger.debug("getAvailableCampaigns result is " + results);
		return results;
	}

	@Override
	public List<USRAgentMapping> getAllAgentIds(String request) throws IEMRException
	{
		logger.info("USRAgentMappingServiceImpl.getAllAgentIds - start");
		USRAgentMapping requestObj = inputMapper.gson().fromJson(request, USRAgentMapping.class);
		logger.debug("getAvailableAgentIds request is " + request);
		List<USRAgentMapping> results = new ArrayList<USRAgentMapping>();
		Set<Objects[]> resultSet = new HashSet<Objects[]>();
		if (requestObj.getAgentID()!=null) {
			resultSet = usrAgentMappingRepository.getUSRAgentMappingByAgentIDAndProviderServiceMapID(requestObj.getAgentID(), requestObj.getProviderServiceMapID());
		} else {
		if (requestObj.getIsAvailable() != null)
		{
			resultSet = usrAgentMappingRepository.getAllAgentIds(requestObj.getProviderServiceMapID(),
					requestObj.getCti_CampaignName(), requestObj.getIsAvailable());
		} else if (requestObj.getCti_CampaignName() != null)
		{
			resultSet = usrAgentMappingRepository.getAllAgentId(requestObj.getProviderServiceMapID(),
					requestObj.getCti_CampaignName());
		} else
		{
			resultSet = usrAgentMappingRepository.getAllAgentIds(requestObj.getProviderServiceMapID());
		}
		}
		for (Object[] result : resultSet)
		{
			logger.debug("inside for result -> " + result.toString());
			if (result != null && result.length >= 9)
			{
				results.add(USRAgentMapping.initializeAllUSRAgentMapping((Integer) result[0], (Integer) result[1],
						(M_UserServiceRoleMapping2) result[2], (Integer) result[3], (M_ProviderServiceMap1) result[4],
						(String) result[5], (String) result[6], (String) result[7], (Boolean) result[8]));
			}
		}
		logger.info("getAllAgentIds result is " + results);
		logger.info("USRAgentMappingServiceImpl.getAllAgentIds - finish");
		return results;
	}

	@Override
	public Integer updateCTICampaignNameMapping(String request) throws IEMRException
	{
		logger.info("USRAgentMappingServiceImpl.updateCTICampaignNameMapping - start");
		USRAgentMapping requestObj = inputMapper.gson().fromJson(request, USRAgentMapping.class);
		logger.debug("updateCTICampaignNameMapping request is " + request);
		int updateCount = usrAgentMappingRepository.updateCTICampaignNameMapping(requestObj.getCti_CampaignName(),
				requestObj.getUsrAgentMappingID());
		logger.debug("updateAgentIds result is " + updateCount);
		logger.info("USRAgentMappingServiceImpl.updateCTICampaignNameMapping - finish. updCount = " + updateCount);
		return updateCount;
	}

	@Override
	public void updateDeletedAgentIDStatus(String agentID) {
		int data = usrAgentMappingRepository.updateDeletedAgentIDStatus(agentID);
	}
}
