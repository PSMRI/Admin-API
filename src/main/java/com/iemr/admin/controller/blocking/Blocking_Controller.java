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
package com.iemr.admin.controller.blocking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
import com.iemr.admin.data.blocking.M_Serviceprovider_Blocking;
import com.iemr.admin.data.blocking.M_Status1;
import com.iemr.admin.data.blocking.T_Providerservicemappingdetail;
import com.iemr.admin.data.blocking.T_Serviceproviderdetail;
import com.iemr.admin.data.blocking.T_Userdetail;
import com.iemr.admin.data.blocking.UserForBlocking;
import com.iemr.admin.data.blocking.V_Showproviderservicemapping;
import com.iemr.admin.service.blocking.BlockingInter;
import com.iemr.admin.to.blocking.BlockingTO;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@RestController
public class Blocking_Controller
{
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	@Autowired
	private BlockingInter blockingInter;

	@Autowired
	public void setServiceProvider_ServiceImpl(BlockingInter blockingInter)
	{
		this.blockingInter = blockingInter;
	}

	@CrossOrigin()
	@RequestMapping(value = "/blockProvider1", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String blockProvider1(@RequestBody String providerBlocking)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{
			

			M_Serviceprovider_Blocking providerDetails =
					InputMapper.gson().fromJson(providerBlocking, M_Serviceprovider_Blocking.class);
			T_Serviceproviderdetail tproviderDetails =
					InputMapper.gson().fromJson(providerBlocking, T_Serviceproviderdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			M_Serviceprovider_Blocking getProviderDetails =
					blockingInter.getProviderDetailsById(providerDetails.getServiceProviderID());
			T_Serviceproviderdetail saveDetails = new T_Serviceproviderdetail();
			saveDetails.setServiceProviderID(getProviderDetails.getServiceProviderID());
			saveDetails.setServiceProviderName(getProviderDetails.getServiceProviderName());
			saveDetails.setdOJ(getProviderDetails.getJoiningDate());
			saveDetails.setLogoFileName(getProviderDetails.getLogoFileName());
			saveDetails.setLogoFilePath(getProviderDetails.getLogoFilePath());
			saveDetails.setPrimaryContactName(getProviderDetails.getPrimaryContactName());
			saveDetails.setPrimaryContactNo(getProviderDetails.getPrimaryContactNo());
			saveDetails.setPrimaryContactEmailID(getProviderDetails.getPrimaryContactEmailID());
			saveDetails.setPrimaryContactAddress(getProviderDetails.getPrimaryContactAddress());
			saveDetails.setPrimaryContactValidityTillDate(getProviderDetails.getPrimaryContactValidityTillDate());
			saveDetails.setSecondaryContactName(getProviderDetails.getServiceProviderName());
			saveDetails.setSecondaryContactNo(getProviderDetails.getSecondaryContactNo());
			saveDetails.setSecondaryContactEmailID(getProviderDetails.getSecondaryContactEmailID());
			saveDetails.setSecondaryContactAddress(getProviderDetails.getSecondaryContactAddress());
			saveDetails.setSecondaryContactValidityTillDate(getProviderDetails.getSecondaryContactValidityTillDate());
			saveDetails.setPreviousStatusID(getProviderDetails.getStatusID());
			saveDetails.setUpdatedStatusID(providerDetails.getStatusID());
			saveDetails.setReason(tproviderDetails.getReason());
			saveDetails.setValidFrom(getProviderDetails.getValidFrom());
			saveDetails.setValidTill(getProviderDetails.getValidTill());
			saveDetails.setDeleted(getProviderDetails.getDeleted());
			saveDetails.setProcessed(getProviderDetails.getProcessed());
			saveDetails.setCreatedBy(getProviderDetails.getCreatedBy());
			saveDetails.setCreatedDate(getProviderDetails.getCreatedDate());
			saveDetails.setModifiedBy(getProviderDetails.getModifiedBy());
			saveDetails.setLastModDate(getProviderDetails.getLastModDate());
			T_Serviceproviderdetail datasaved = blockingInter.saveData(saveDetails);
			getProviderDetails.setStatusID(providerDetails.getStatusID());
			M_Serviceprovider_Blocking blockprovider = blockingInter.blockServiceProvider(getProviderDetails);

			tproviderDetails.setPreviousStatusID(getProviderDetails.getStateID());

			// getprov

			// T_Serviceproviderdetail
			// saveblockingDetails=blockingInter.saveDetails(getProviderDetails);

			/*
			 * ArrayList<M_UserServiceRoleMapping2> data = employeeMasterInter.getEmployeeDetails(
			 * m_UserServiceRoleMapping.getRoleID(), m_UserServiceRoleMapping.getProviderServiceMapID());
			 */

			/*
			 * ArrayList<M_UserServiceRoleMapping2> data = employeeMasterInter.getEmployeeDetails();
			 */

			// M_User1 deleted1 = employeeMasterInter.saveEditData(deletedata);
			response.setResponse(datasaved.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}

		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/blockProvider", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String blockProvider(@RequestBody String providerBlocking)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(providerBlocking, M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails =
					InputMapper.gson().fromJson(providerBlocking, T_Providerservicemappingdetail.class);

			ArrayList<M_Providerservicemapping_Blocking> getProviderStatus1 =
					blockingInter.getProviderStatus(providerDetails.getServiceProviderID());

			blockingInter.blockProvider(providerDetails.getServiceProviderID(), providerDetails.getStatusID());

			T_Providerservicemappingdetail resDataMap = null;
			List<T_Providerservicemappingdetail> resList = new ArrayList<T_Providerservicemappingdetail>();
			int x = 0;
			for (M_Providerservicemapping_Blocking obj : getProviderStatus1)
			{

				// for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){

				resDataMap = new T_Providerservicemappingdetail();

				resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
				resDataMap.setServiceProviderID(obj.getServiceProviderID());
				resDataMap.setServiceID(obj.getServiceID());
				resDataMap.setStateID(obj.getStateID());
				resDataMap.setCityID(obj.getCityID());
				resDataMap.setCountryID(obj.getCountryID());
				resDataMap.setDistrictID(obj.getDistrictID());
				resDataMap.setDistrictBlockID(obj.getDistrictBlockID());
				resDataMap.setAddress(obj.getAddress());
				resDataMap.setPreviousStatusID(obj.getStatusID());
				resDataMap.setUpdatedStatusID(providerDetails.getStatusID());
				resDataMap.setReason(tproviderserviceDetails.getReason());
				resDataMap.setValidFrom(obj.getValidFrom());
				resDataMap.setValidTill(obj.getValidTill());
				resDataMap.setDeleted(obj.getDeleted());
				resDataMap.setProcessed(obj.getProcessed());
				resDataMap.setCreatedBy(obj.getCreatedBy());
				resDataMap.setModifiedBy(obj.getModifiedBy());
				resList.add(resDataMap);

			}
			// }
			ArrayList<T_Providerservicemappingdetail> data = blockingInter.savetpsmd(resList);

			response.setResponse(data.toString());
			// response.setResponse(datasaved.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = { "/blockProviderByServiceId" }, method = { RequestMethod.POST },
			produces =
	{ "application/json" }, headers = "Authorization")
	public String blockProviderByServiceId(@RequestBody String blockProviderByServiceId)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(blockProviderByServiceId, M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails =
					InputMapper.gson().fromJson(blockProviderByServiceId, T_Providerservicemappingdetail.class);

			ArrayList<M_Providerservicemapping_Blocking> getProviderStatus2 =
					blockingInter.getProviderStatusByProviderAndServiceId(providerDetails.getServiceProviderID(),
							providerDetails.getServiceID());

			blockingInter.blockProviderByProviderIdAndServiceId(providerDetails.getServiceProviderID(),
					providerDetails.getServiceID(), providerDetails.getStatusID());

			T_Providerservicemappingdetail resDataMap = null;
			List<T_Providerservicemappingdetail> resList = new ArrayList<T_Providerservicemappingdetail>();
			int x = 0;
			for (M_Providerservicemapping_Blocking obj : getProviderStatus2)
			{

				// for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){

				resDataMap = new T_Providerservicemappingdetail();

				resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
				resDataMap.setServiceProviderID(obj.getServiceProviderID());
				resDataMap.setServiceID(obj.getServiceID());
				resDataMap.setStateID(obj.getStateID());
				resDataMap.setCityID(obj.getCityID());
				resDataMap.setCountryID(obj.getCountryID());
				resDataMap.setDistrictID(obj.getDistrictID());
				resDataMap.setDistrictBlockID(obj.getDistrictBlockID());
				resDataMap.setAddress(obj.getAddress());
				resDataMap.setPreviousStatusID(obj.getStatusID());
				resDataMap.setUpdatedStatusID(providerDetails.getStatusID());
				resDataMap.setReason(tproviderserviceDetails.getReason());
				resDataMap.setValidFrom(obj.getValidFrom());
				resDataMap.setValidTill(obj.getValidTill());
				resDataMap.setDeleted(obj.getDeleted());
				resDataMap.setProcessed(obj.getProcessed());
				resDataMap.setCreatedBy(obj.getCreatedBy());
				resDataMap.setModifiedBy(obj.getModifiedBy());
				resList.add(resDataMap);
			}
			// }
			ArrayList<T_Providerservicemappingdetail> data = blockingInter.savetpsmd(resList);

			response.setResponse(data.toString());
			// response.setResponse(datasaved.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/getProviderStatus", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String getProviderStatus(@RequestBody String getProviderStatus)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(getProviderStatus, M_Providerservicemapping_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getProviderStatus1 =
					blockingInter.getProviderStatus1(providerDetails.getServiceProviderID());

			response.setResponse(getProviderStatus1.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}
	
	
	
	@CrossOrigin()
	@RequestMapping(value = "/getProviderStatus1", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String getProviderStatus1(@RequestBody String getProviderStatus)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(getProviderStatus, M_Providerservicemapping_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getProviderStatus1 =
					blockingInter.getProviderStatus2(providerDetails.getServiceProviderID());

			response.setResponse(getProviderStatus1.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}
	
	
	

	@CrossOrigin()
	@RequestMapping(value = "/getServiceLinesUsingProvider", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String getServiceLiensUsingProvider(@RequestBody String getServiceLiensUsingProvider)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(getServiceLiensUsingProvider, M_Providerservicemapping_Blocking.class);

			ArrayList<M_Providerservicemapping_Blocking> getServiceLiensUsingProvider4 =
					blockingInter.getServiceLiensUsingProvider(providerDetails.getServiceProviderID());

			response.setResponse(getServiceLiensUsingProvider4.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/getProviderStatusByProviderAndServiceId", headers = "Authorization",
			method =
	{ RequestMethod.POST }, produces = { "application/json" })
	public String getProviderStatusByProviderAndServiceId(@RequestBody String getProviderStatusByProviderAndServiceId)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(getProviderStatusByProviderAndServiceId, M_Providerservicemapping_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getProviderStatus2 =
					blockingInter.getProviderStatusByProviderAndServiceId2(providerDetails.getServiceProviderID(),
							providerDetails.getServiceID());

			response.setResponse(getProviderStatus2.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/blockProviderByService", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String blockProviderByService(@RequestBody String providerServiceBlocking)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(providerServiceBlocking, M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails =
					InputMapper.gson().fromJson(providerServiceBlocking, T_Providerservicemappingdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			M_Providerservicemapping_Blocking getProviderSDetails =
					blockingInter.getProviderServiceMappingDetails(providerDetails.getServiceProviderID(),
							providerDetails.getStateID(), providerDetails.getServiceID());

			blockingInter.blockProviderByService(providerDetails.getServiceProviderID(), providerDetails.getStateID(),
					providerDetails.getServiceID(), providerDetails.getStatusID());
			T_Providerservicemappingdetail tpsd = new T_Providerservicemappingdetail();
			tpsd.setProviderServiceMapID(getProviderSDetails.getProviderServiceMapID());
			tpsd.setServiceProviderID(getProviderSDetails.getServiceProviderID());
			tpsd.setServiceID(getProviderSDetails.getServiceID());
			tpsd.setStateID(getProviderSDetails.getStateID());
			tpsd.setCityID(getProviderSDetails.getCityID());
			tpsd.setCountryID(getProviderSDetails.getCountryID());
			tpsd.setDistrictID(getProviderSDetails.getDistrictID());
			tpsd.setDistrictBlockID(getProviderSDetails.getDistrictBlockID());
			tpsd.setAddress(getProviderSDetails.getAddress());
			tpsd.setPreviousStatusID(getProviderSDetails.getStatusID());
			tpsd.setUpdatedStatusID(providerDetails.getStatusID());
			tpsd.setReason(tproviderserviceDetails.getReason());
			tpsd.setValidFrom(getProviderSDetails.getValidTill());
			tpsd.setValidTill(getProviderSDetails.getValidTill());
			tpsd.setDeleted(getProviderSDetails.getDeleted());
			tpsd.setProcessed(getProviderSDetails.getProcessed());
			tpsd.setCreatedBy(getProviderSDetails.getCreatedBy());
			tpsd.setModifiedBy(getProviderSDetails.getModifiedBy());
			T_Providerservicemappingdetail res = blockingInter.savetpsdData(tpsd);

			response.setResponse(res.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/getProviderStatusByService", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String getProviderStatusByService(@RequestBody String providerServiceBlocking)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(providerServiceBlocking, M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails =
					InputMapper.gson().fromJson(providerServiceBlocking, T_Providerservicemappingdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getProviderSDetails =
					blockingInter.getProviderServiceMappingDetails2(providerDetails.getServiceProviderID(),
							providerDetails.getStateID(), providerDetails.getServiceID());

			response.setResponse(getProviderSDetails.toString());
		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/blockProviderByState", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String blockProviderByState(@RequestBody String providerStateBlocking)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(providerStateBlocking, M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails =
					InputMapper.gson().fromJson(providerStateBlocking, T_Providerservicemappingdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			List<M_Providerservicemapping_Blocking> getPsmdforSate = blockingInter.getProviderStateMappingDetails(
					providerDetails.getServiceProviderID(), providerDetails.getStateID());

			blockingInter.blockProviderByState(providerDetails.getServiceProviderID(), providerDetails.getStateID(),
					providerDetails.getStatusID());

			T_Providerservicemappingdetail resDataMap = null;
			List<T_Providerservicemappingdetail> resList = new ArrayList<T_Providerservicemappingdetail>();
			int x = 0;
			for (M_Providerservicemapping_Blocking obj : getPsmdforSate)
			{

				// for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){

				resDataMap = new T_Providerservicemappingdetail();

				resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
				resDataMap.setServiceProviderID(obj.getServiceProviderID());
				resDataMap.setServiceID(obj.getServiceID());
				resDataMap.setStateID(obj.getStateID());
				resDataMap.setCityID(obj.getCityID());
				resDataMap.setCountryID(obj.getCountryID());
				resDataMap.setDistrictID(obj.getDistrictID());
				resDataMap.setDistrictBlockID(obj.getDistrictBlockID());
				resDataMap.setAddress(obj.getAddress());
				resDataMap.setPreviousStatusID(obj.getStatusID());
				resDataMap.setUpdatedStatusID(providerDetails.getStatusID());
				resDataMap.setReason(tproviderserviceDetails.getReason());
				resDataMap.setValidFrom(obj.getValidFrom());
				resDataMap.setValidTill(obj.getValidTill());
				resDataMap.setDeleted(obj.getDeleted());
				resDataMap.setProcessed(obj.getProcessed());
				resDataMap.setCreatedBy(obj.getCreatedBy());
				resDataMap.setModifiedBy(obj.getModifiedBy());
				resList.add(resDataMap);

			}
			// }
			ArrayList<T_Providerservicemappingdetail> data = blockingInter.savetpsmd(resList);

			response.setResponse(data.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/getProviderStatusByState", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String getProviderStatusByState(@RequestBody String providerStateBlocking)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails =
					InputMapper.gson().fromJson(providerStateBlocking, M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails =
					InputMapper.gson().fromJson(providerStateBlocking, T_Providerservicemappingdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getPsmdforSate = blockingInter.getProviderStateMappingDetails1(
					providerDetails.getServiceProviderID(), providerDetails.getStateID());

			response.setResponse(getPsmdforSate.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/blockUser", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String blockUser(@RequestBody String blockUser)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			UserForBlocking blockUser1 = InputMapper.gson().fromJson(blockUser, UserForBlocking.class);

			T_Userdetail userDetails = InputMapper.gson().fromJson(blockUser, T_Userdetail.class);

			UserForBlocking userData = blockingInter.getUserDetailByUserId(blockUser1.getUserID());

			blockingInter.blockUser(blockUser1.getUserID(), blockUser1.getStatusID());

			T_Userdetail tuserDetails = new T_Userdetail();
			tuserDetails.setUserID(userData.getUserID());
			tuserDetails.setUserName(userData.getUserName());
			tuserDetails.setPreviousPassword(userData.getPassword());
			tuserDetails.setUpdatedPassword(userDetails.getUpdatedPassword());
			tuserDetails.setcZUserID(userData.getcZUserID());
			tuserDetails.setcZRole(userData.getcZRole());
			tuserDetails.setAgentID(userData.getAgentID());
			tuserDetails.setPreviousAgentPassword(userData.getAgentPassword());
			tuserDetails.setUpdatedAgentPassword(userDetails.getUpdatedAgentPassword());
			tuserDetails.setPreviousCZPassword(userData.getcZPassword());
			tuserDetails.setUpdatedCZPassword(userDetails.getUpdatedCZPassword());
			tuserDetails.setPreviousStatusID(userData.getStatusID());
			tuserDetails.setUpdatedStatusID(userDetails.getUpdatedStatusID());
			tuserDetails.setCreatedBy(userData.getCreatedBy());
			tuserDetails.setModifiedBy(userData.getModifiedBy());
			// tuserDetails.setProcessed(userData.getp);

			T_Userdetail saveData = blockingInter.saveUserDetails(tuserDetails);

			response.setResponse("jai");

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/getStatus", headers = "Authorization", method = { RequestMethod.POST },
			produces =
	{ "application/json" })
	public String getStatus(@RequestBody String getStatus)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Status1 blockUser = InputMapper.gson().fromJson(getStatus, M_Status1.class);

			ArrayList<M_Status1> statusData = blockingInter.getStatusData();

			response.setResponse(statusData.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/addProviderStateAndServiceLines", headers = "Authorization",
			method =
	{ RequestMethod.POST }, produces = { "application/json" })
	public String ProviderStateAndServiceLines(@RequestBody String ProviderStateAndServiceLines)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking[] ps =
					InputMapper.gson().fromJson(ProviderStateAndServiceLines, M_Providerservicemapping_Blocking[].class);

			List<M_Providerservicemapping_Blocking> providerDetails1 = Arrays.asList(ps);

			BlockingTO[] blockingto1 = InputMapper.gson().fromJson(ProviderStateAndServiceLines, BlockingTO[].class);

			List<BlockingTO> blockingto2 = Arrays.asList(blockingto1);
			// T_Providerservicemappingdetail tproviderserviceDetails =
			// InputMapper.gson().fromJson(ProviderStateAndServiceLines,
			// T_Providerservicemappingdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			// List<M_Providerservicemapping_Blocking>
			// getPsmdforSate=blockingInter.getProviderStateMappingDetails(providerDetails.getServiceProviderID(),providerDetails.getStateID());

			// blockingInter.blockProviderByState(providerDetails.getServiceProviderID(),providerDetails.getStateID(),providerDetails.getStatusID());
			M_Providerservicemapping_Blocking resDataMap = null;
			List<M_Providerservicemapping_Blocking> resList = new ArrayList<M_Providerservicemapping_Blocking>();

			int providerDetailsIndex = 0;
			Integer[] stateIDs = null;
			for (BlockingTO blockingto : blockingto2)
			{
				stateIDs = blockingto.getStateID1();
				M_Providerservicemapping_Blocking providerDetails = providerDetails1.get(providerDetailsIndex);
				/*
				 * for(M_Providerservicemapping_Blocking providerDetails:providerDetails1){ for(int
				 * i=0;i<ServiceID.length;i++){
				 * 
				 * //for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){
				 * 
				 * resDataMap=new M_Providerservicemapping_Blocking();
				 * 
				 * //resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
				 * resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
				 * resDataMap.setServiceID(ServiceID[i]); resDataMap.setStateID(providerDetails.getStateID());
				 * resDataMap.setCreatedBy(providerDetails.getCreatedBy());
				 * resDataMap.setStatusID(providerDetails.getStatusID());
				 * 
				 * // resList.add(resDataMap);
				 * 
				 * resList.add(resDataMap);
				 * 
				 * } }
				 */

				// for(M_Providerservicemapping_Blocking providerDetails:providerDetails1){
				if (stateIDs.length > 0)
				{

					for (int statesIndex = 0; statesIndex < stateIDs.length; statesIndex++)
					{
						// for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){
						resDataMap = new M_Providerservicemapping_Blocking();
						// resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
						resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
						resDataMap.setServiceID(providerDetails.getServiceID());
						resDataMap.setStateID(stateIDs[statesIndex]);
						resDataMap.setCreatedBy(providerDetails.getCreatedBy());
						// resDataMap.setStatusID(providerDetails.getStatusID());
						resDataMap.setStatusID(providerDetails.getStatusID());
						// resList.add(resDataMap);

						resList.add(resDataMap);

					}
					// }
					// x++;
				} else if (stateIDs.length == 0)
				{
					resDataMap = new M_Providerservicemapping_Blocking();
					// resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
					resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
					resDataMap.setServiceID(providerDetails.getServiceID());
					resDataMap.setStateID(providerDetails.getStateID());
					resDataMap.setCreatedBy(providerDetails.getCreatedBy());
					// resDataMap.setStatusID(providerDetails.getStatusID());
					resDataMap.setStatusID(providerDetails.getStatusID());
					// resList.add(resDataMap);
					resList.add(resDataMap);

				}
				providerDetailsIndex++;
			}

			// }
			ArrayList<M_Providerservicemapping_Blocking> data = blockingInter.AddServiceProvider(resList);

			response.setResponse(data.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}


	// this i have to implement when requeired becouse bipin sir said don't change any thing in provider

	@CrossOrigin()
	@RequestMapping(value = "/deleteProviderStateAndServiceLines", headers = "Authorization",
			method =
	{ RequestMethod.POST }, produces = { "application/json" })
	public String deleteProviderStateAndServiceLines(@RequestBody String deleteProviderStateAndServiceLines)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(deleteProviderStateAndServiceLines, M_Providerservicemapping_Blocking.class);

			BlockingTO blockingto = InputMapper.gson().fromJson(deleteProviderStateAndServiceLines, BlockingTO.class);
			// T_Providerservicemappingdetail tproviderserviceDetails =
			// InputMapper.gson().fromJson(ProviderStateAndServiceLines,
			// T_Providerservicemappingdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			// List<M_Providerservicemapping_Blocking>
			// getPsmdforSate=blockingInter.getProviderStateMappingDetails(providerDetails.getServiceProviderID(),providerDetails.getStateID());

			// blockingInter.blockProviderByState(providerDetails.getServiceProviderID(),providerDetails.getStateID(),providerDetails.getStatusID());
			Integer[] ServiceID = blockingto.getServiceID1();

			M_Providerservicemapping_Blocking resDataMap = null;
			List<M_Providerservicemapping_Blocking> resList = new ArrayList<M_Providerservicemapping_Blocking>();

			for (int i = 0; i < ServiceID.length; i++)
			{

				// for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){

				resDataMap = new M_Providerservicemapping_Blocking();

				// resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
				resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
				resDataMap.setServiceID(ServiceID[i]);
				resDataMap.setStateID(providerDetails.getStateID());
				resDataMap.setCreatedBy(providerDetails.getCreatedBy());
				resDataMap.setStatusID(providerDetails.getStatusID());

				// resList.add(resDataMap);

				resList.add(resDataMap);
			}
			// }
			ArrayList<M_Providerservicemapping_Blocking> data = blockingInter.AddServiceProvider(resList);

			response.setResponse(data.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/createCitMappingwithServiceLines", headers = "Authorization",
			method =
	{ RequestMethod.POST }, produces = { "application/json" })
	public String createCitMappingwithServiceLines(@RequestBody String createCitMappingwithServiceLines)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking[] providerDetails = InputMapper.gson()
					.fromJson(createCitMappingwithServiceLines, M_Providerservicemapping_Blocking[].class);
			List<M_Providerservicemapping_Blocking> ctidata = Arrays.asList(providerDetails);

			String data = blockingInter.mapctidata(ctidata);

			response.setResponse(data.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/getMappedServiceLinesAndStatetoProvider", headers = "Authorization",
			method =
	{ RequestMethod.POST }, consumes = { "application/json" }, produces = { "application/json" })
	public String getMappedServiceLinesAndStatetoProvider(@RequestBody String getMappedServiceLinesAndStatetoProvider)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(getMappedServiceLinesAndStatetoProvider, M_Providerservicemapping_Blocking.class);

			ArrayList<M_Providerservicemapping_Blocking> getServiceLiensUsingProvider4 =
					blockingInter.getServiceLiensUsingProvider1(providerDetails);

			response.setResponse(getServiceLiensUsingProvider4.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}

		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/mapServiceLinesAndStatetoProvider", headers = "Authorization",
			method =
	{ RequestMethod.POST }, produces = { "application/json" })
	public String mapProviderAndServiceLines(@RequestBody String mapProviderAndServiceLines)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking[] ps =
					InputMapper.gson().fromJson(mapProviderAndServiceLines, M_Providerservicemapping_Blocking[].class);

			List<M_Providerservicemapping_Blocking> providerDetails1 = Arrays.asList(ps);

			BlockingTO[] blockingto1 = InputMapper.gson().fromJson(mapProviderAndServiceLines, BlockingTO[].class);

			List<BlockingTO> blockingto2 = Arrays.asList(blockingto1);
			// T_Providerservicemappingdetail tproviderserviceDetails =
			// InputMapper.gson().fromJson(ProviderStateAndServiceLines,
			// T_Providerservicemappingdetail.class);

			// M_Serviceprovider_Blocking providerDetails =
			// InputMapper.gson().toj(providerBlocking,M_Serviceprovider_Blocking.class);

			// List<M_Providerservicemapping_Blocking>
			// getPsmdforSate=blockingInter.getProviderStateMappingDetails(providerDetails.getServiceProviderID(),providerDetails.getStateID());

			// blockingInter.blockProviderByState(providerDetails.getServiceProviderID(),providerDetails.getStateID(),providerDetails.getStatusID());
			M_Providerservicemapping_Blocking resDataMap = null;
			List<M_Providerservicemapping_Blocking> resList = new ArrayList<M_Providerservicemapping_Blocking>();

			int providerDetailsIndex = 0;
			Integer[] stateIDs = null;
			for (BlockingTO blockingto : blockingto2)
			{
				stateIDs = blockingto.getStateID1();
				M_Providerservicemapping_Blocking providerDetails = providerDetails1.get(providerDetailsIndex);
				/*
				 * for(M_Providerservicemapping_Blocking providerDetails:providerDetails1){ for(int
				 * i=0;i<ServiceID.length;i++){
				 * 
				 * //for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){
				 * 
				 * resDataMap=new M_Providerservicemapping_Blocking();
				 * 
				 * //resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
				 * resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
				 * resDataMap.setServiceID(ServiceID[i]); resDataMap.setStateID(providerDetails.getStateID());
				 * resDataMap.setCreatedBy(providerDetails.getCreatedBy());
				 * resDataMap.setStatusID(providerDetails.getStatusID());
				 * 
				 * // resList.add(resDataMap);
				 * 
				 * resList.add(resDataMap);
				 * 
				 * } }
				 */

				// for(M_Providerservicemapping_Blocking providerDetails:providerDetails1){
				if (stateIDs.length > 0)
				{

					for (int statesIndex = 0; statesIndex < stateIDs.length; statesIndex++)
					{
						// for(int i=0; i < obj.getProviderServiceMapID().SIZE;i++ ){
						resDataMap = new M_Providerservicemapping_Blocking();
						// resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
						resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
						resDataMap.setServiceID(providerDetails.getServiceID());
						resDataMap.setStateID(stateIDs[statesIndex]);
						resDataMap.setCreatedBy(providerDetails.getCreatedBy());
						// resDataMap.setStatusID(providerDetails.getStatusID());
						resDataMap.setStatusID(1);
						// resList.add(resDataMap);

						resList.add(resDataMap);

					}
					// }
					// x++;
				} else if (stateIDs.length == 0)
				{
					resDataMap = new M_Providerservicemapping_Blocking();
					// resDataMap.setProviderServiceMapID(obj.getProviderServiceMapID());
					resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
					resDataMap.setServiceID(providerDetails.getServiceID());
					resDataMap.setStateID(providerDetails.getStateID());
					resDataMap.setCreatedBy(providerDetails.getCreatedBy());
					// resDataMap.setStatusID(providerDetails.getStatusID());
					resDataMap.setStatusID(1);
					// resList.add(resDataMap);
					resList.add(resDataMap);

				}
				providerDetailsIndex++;
			}

			// }
			ArrayList<M_Providerservicemapping_Blocking> data = blockingInter.AddServiceProvider(resList);

			response.setResponse(data.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/editMappedServiceLinesAndStatetoProvider", headers = "Authorization",
			method =
	{ RequestMethod.POST }, produces = { "application/json" })
	public String editMappedServiceLinesAndStatetoProvider(@RequestBody String editMappedServiceLinesAndStatetoProvider)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(editMappedServiceLinesAndStatetoProvider, M_Providerservicemapping_Blocking.class);

			M_Providerservicemapping_Blocking getProviderData =
					blockingInter.getDataByProviderServiceMapId(providerDetails.getProviderServiceMapID());
			getProviderData.setServiceProviderID(providerDetails.getServiceProviderID());
			getProviderData.setServiceID(providerDetails.getServiceID());
			getProviderData.setStateID(providerDetails.getStateID());
			//getProviderData.setStatusID(providerDetails.getStatusID());
			getProviderData.setModifiedBy(providerDetails.getModifiedBy());

			M_Providerservicemapping_Blocking updatedData = blockingInter.updateProviderData(getProviderData);

			response.setResponse(getProviderData.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/deleteMappedServiceLinesAndStatetoProvider", headers = "Authorization",
			method =
	{ RequestMethod.POST }, produces = { "application/json" })
	public String
			deleteMappedServiceLinesAndStatetoProvider(@RequestBody String deleteMappedServiceLinesAndStatetoProvider)
	{
		// JSONObject requestOBJ = new JSONObject(providerBlocking);
		OutputResponse response = new OutputResponse();

		try
		{

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(deleteMappedServiceLinesAndStatetoProvider, M_Providerservicemapping_Blocking.class);

			M_Providerservicemapping_Blocking getProviderData =
					blockingInter.getDataByProviderServiceMapId(providerDetails.getProviderServiceMapID());
			getProviderData.setDeleted(providerDetails.getDeleted());

			M_Providerservicemapping_Blocking updatedData = blockingInter.updateProviderData(getProviderData);

			response.setResponse(getProviderData.toString());

		} catch (Exception e)
		{
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

}
