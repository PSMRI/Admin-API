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
package com.iemr.admin.aspectj.apiman;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.iemr.admin.data.apiman.ApimanClient;
import com.iemr.admin.data.apiman.ApimanRegister;
import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
import com.iemr.admin.data.blocking.V_Showproviderservicemapping;
import com.iemr.admin.repo.blocking.MProviderservicemappingBlockingRepo;
import com.iemr.admin.repo.blocking.V_ShowproviderservicemappingRepo;
import com.iemr.admin.service.apiman.ApimanService;
import com.iemr.admin.utils.config.ConfigProperties;

@Aspect
@Configuration
@Component
public class AspectApiman {

	@Autowired
	ApimanService apimanService;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private MProviderservicemappingBlockingRepo mProviderservicemappingBlockingRepo;
	
	@Autowired
	private V_ShowproviderservicemappingRepo v_ShowproviderservicemappingRepo;
	
	private String organizationID = ConfigProperties.getPropertyByName("apiman-organizationID");

	@Pointcut("execution(* com.iemr.admin.service.blocking.Blocking_Service.AddServiceProvider(..))")
	public void addServiceProviderJoint() {
	}

	@Before(value = "addServiceProviderJoint()")
	public void afterReturning(JoinPoint joinPoint) {
		logger.info(joinPoint.getArgs().toString());

	}

	@AfterReturning(value = "addServiceProviderJoint()", returning = "result")
	public void afterReturning(JoinPoint joinPoint, Object result) {
		logger.info("{} returned with value {}", joinPoint, result);

		try {

			List<M_Providerservicemapping_Blocking> data = (List<M_Providerservicemapping_Blocking>) result;
			List<V_Showproviderservicemapping> datafull=new ArrayList<>();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			if (data.size() > 0) {
				// ids = new Integer[data.size()];
				data.forEach(action -> {
					ids.add(action.getProviderServiceMapID());
				});
				datafull = v_ShowproviderservicemappingRepo.findByProviderServiceMapIDIn(ids);
			} else {
				return;
			}
			logger.info("retrieve data from mysql : " + data.toString());
			datafull.parallelStream().forEach(m_Providerservicemapping ->{
				//for (V_Showproviderservicemapping m_Providerservicemapping : datafull) {
					ApimanClient apimanClient = new ApimanClient();
					apimanClient.setInitialVersion("1.0");
					apimanClient.setDescription(m_Providerservicemapping.getProviderServiceMapID()+"");
					String name = "";
//					if (m_Providerservicemapping.getServiceProviderName() != null) {
						name = name + m_Providerservicemapping.getServiceProviderName();
//					}
//					if (m_Providerservicemapping.getM_ServicemasterForBlocking() != null) {
						name = name + "/" + m_Providerservicemapping.getServiceName();
//					}
					if (m_Providerservicemapping.getStateName() != null) {

						name = name + "/" + m_Providerservicemapping.getStateName();
					} else {
						name = name + "/National";
					}
					apimanClient.setName(name.trim());
					try {
						apimanClient = apimanService.createClient(apimanClient);
						
						 apimanService.createClientContract(m_Providerservicemapping.getServiceID(),apimanClient.getId());
						 
						 
						 
						 // Registering the Client with APIMAN
						 ApimanRegister apimanRegister=new ApimanRegister();
						 apimanRegister.setEntityId(apimanClient.getId());
						 apimanRegister.setEntityVersion("1.0");
						 apimanRegister.setOrganizationId(organizationID);
						 apimanRegister.setType("registerClient");
						 
						 apimanService.registerClient(apimanRegister);
						 
						 // Fetching the Client Key
						 String key=apimanService.getClientKey(apimanClient.getId());
						 
						mProviderservicemappingBlockingRepo.updateAPIMAN(apimanClient.getId(), key,
								m_Providerservicemapping.getProviderServiceMapID());
					} catch (Exception e) {
						logger.error(e.getMessage());
					}

				//}
			});
		

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
