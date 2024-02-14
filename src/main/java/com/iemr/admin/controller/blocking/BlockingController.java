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

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class BlockingController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	@Autowired
	private BlockingInter blockingInter;

	@Autowired
	public void setServiceProvider_ServiceImpl(BlockingInter blockingInter) {
		this.blockingInter = blockingInter;
	}

	@CrossOrigin()
	@Operation(summary = "Block provider 1")
	@RequestMapping(value = "/blockProvider1", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String blockProvider1(@RequestBody String providerBlocking) {
		OutputResponse response = new OutputResponse();

		try {

			M_Serviceprovider_Blocking providerDetails = InputMapper.gson().fromJson(providerBlocking,
					M_Serviceprovider_Blocking.class);
			T_Serviceproviderdetail tproviderDetails = InputMapper.gson().fromJson(providerBlocking,
					T_Serviceproviderdetail.class);

			M_Serviceprovider_Blocking getProviderDetails = blockingInter
					.getProviderDetailsById(providerDetails.getServiceProviderID());
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
			blockingInter.blockServiceProvider(getProviderDetails);

			tproviderDetails.setPreviousStatusID(getProviderDetails.getStateID());

			response.setResponse(datasaved.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Block provider")
	@RequestMapping(value = "/blockProvider", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String blockProvider(@RequestBody String providerBlocking) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(providerBlocking,
					M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails = InputMapper.gson().fromJson(providerBlocking,
					T_Providerservicemappingdetail.class);

			ArrayList<M_Providerservicemapping_Blocking> getProviderStatus1 = blockingInter
					.getProviderStatus(providerDetails.getServiceProviderID());

			blockingInter.blockProvider(providerDetails.getServiceProviderID(), providerDetails.getStatusID());

			T_Providerservicemappingdetail resDataMap = null;
			List<T_Providerservicemappingdetail> resList = new ArrayList<T_Providerservicemappingdetail>();
			for (M_Providerservicemapping_Blocking obj : getProviderStatus1) {
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
			ArrayList<T_Providerservicemappingdetail> data = blockingInter.savetpsmd(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Block provider by service id")
	@RequestMapping(value = { "/blockProviderByServiceId" }, method = { RequestMethod.POST }, produces = {
			"application/json" }, headers = "Authorization")
	public String blockProviderByServiceId(@RequestBody String blockProviderByServiceId) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(blockProviderByServiceId,
					M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails = InputMapper.gson()
					.fromJson(blockProviderByServiceId, T_Providerservicemappingdetail.class);

			ArrayList<M_Providerservicemapping_Blocking> getProviderStatus2 = blockingInter
					.getProviderStatusByProviderAndServiceId(providerDetails.getServiceProviderID(),
							providerDetails.getServiceID());

			blockingInter.blockProviderByProviderIdAndServiceId(providerDetails.getServiceProviderID(),
					providerDetails.getServiceID(), providerDetails.getStatusID());

			T_Providerservicemappingdetail resDataMap = null;
			List<T_Providerservicemappingdetail> resList = new ArrayList<T_Providerservicemappingdetail>();
			for (M_Providerservicemapping_Blocking obj : getProviderStatus2) {

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
			ArrayList<T_Providerservicemappingdetail> data = blockingInter.savetpsmd(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get provider status")
	@RequestMapping(value = "/getProviderStatus", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getProviderStatus(@RequestBody String getProviderStatus) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(getProviderStatus,
					M_Providerservicemapping_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getProviderStatus1 = blockingInter
					.getProviderStatus1(providerDetails.getServiceProviderID());

			response.setResponse(getProviderStatus1.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get provider status 1")
	@RequestMapping(value = "/getProviderStatus1", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getProviderStatus1(@RequestBody String getProviderStatus) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(getProviderStatus,
					M_Providerservicemapping_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getProviderStatus1 = blockingInter
					.getProviderStatus2(providerDetails.getServiceProviderID());

			response.setResponse(getProviderStatus1.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get service liens using provider")
	@RequestMapping(value = "/getServiceLinesUsingProvider", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getServiceLiensUsingProvider(@RequestBody String getServiceLiensUsingProvider) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(getServiceLiensUsingProvider, M_Providerservicemapping_Blocking.class);

			ArrayList<M_Providerservicemapping_Blocking> getServiceLiensUsingProvider4 = blockingInter
					.getServiceLiensUsingProvider(providerDetails.getServiceProviderID());

			response.setResponse(getServiceLiensUsingProvider4.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get provider status by provider and service id")
	@RequestMapping(value = "/getProviderStatusByProviderAndServiceId", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getProviderStatusByProviderAndServiceId(@RequestBody String getProviderStatusByProviderAndServiceId) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(getProviderStatusByProviderAndServiceId, M_Providerservicemapping_Blocking.class);

			ArrayList<V_Showproviderservicemapping> getProviderStatus2 = blockingInter
					.getProviderStatusByProviderAndServiceId2(providerDetails.getServiceProviderID(),
							providerDetails.getServiceID());

			response.setResponse(getProviderStatus2.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Block provider by service")
	@RequestMapping(value = "/blockProviderByService", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String blockProviderByService(@RequestBody String providerServiceBlocking) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(providerServiceBlocking,
					M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails = InputMapper.gson()
					.fromJson(providerServiceBlocking, T_Providerservicemappingdetail.class);

			M_Providerservicemapping_Blocking getProviderSDetails = blockingInter.getProviderServiceMappingDetails(
					providerDetails.getServiceProviderID(), providerDetails.getStateID(),
					providerDetails.getServiceID());

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

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get provider status by service")
	@RequestMapping(value = "/getProviderStatusByService", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getProviderStatusByService(@RequestBody String providerServiceBlocking) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(providerServiceBlocking,
					M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails = InputMapper.gson()
					.fromJson(providerServiceBlocking, T_Providerservicemappingdetail.class);

			ArrayList<V_Showproviderservicemapping> getProviderSDetails = blockingInter
					.getProviderServiceMappingDetails2(providerDetails.getServiceProviderID(),
							providerDetails.getStateID(), providerDetails.getServiceID());

			response.setResponse(getProviderSDetails.toString());
		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Block provider by state")
	@RequestMapping(value = "/blockProviderByState", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String blockProviderByState(@RequestBody String providerStateBlocking) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(providerStateBlocking,
					M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails = InputMapper.gson().fromJson(providerStateBlocking,
					T_Providerservicemappingdetail.class);

			List<M_Providerservicemapping_Blocking> getPsmdforSate = blockingInter.getProviderStateMappingDetails(
					providerDetails.getServiceProviderID(), providerDetails.getStateID());

			blockingInter.blockProviderByState(providerDetails.getServiceProviderID(), providerDetails.getStateID(),
					providerDetails.getStatusID());

			T_Providerservicemappingdetail resDataMap = null;
			List<T_Providerservicemappingdetail> resList = new ArrayList<T_Providerservicemappingdetail>();
			for (M_Providerservicemapping_Blocking obj : getPsmdforSate) {

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
			ArrayList<T_Providerservicemappingdetail> data = blockingInter.savetpsmd(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get provider status by state")
	@RequestMapping(value = "/getProviderStatusByState", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getProviderStatusByState(@RequestBody String providerStateBlocking) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson().fromJson(providerStateBlocking,
					M_Providerservicemapping_Blocking.class);
			T_Providerservicemappingdetail tproviderserviceDetails = InputMapper.gson().fromJson(providerStateBlocking,
					T_Providerservicemappingdetail.class);

			ArrayList<V_Showproviderservicemapping> getPsmdforSate = blockingInter.getProviderStateMappingDetails1(
					providerDetails.getServiceProviderID(), providerDetails.getStateID());

			response.setResponse(getPsmdforSate.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Block user")
	@RequestMapping(value = "/blockUser", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String blockUser(@RequestBody String blockUser) {
		OutputResponse response = new OutputResponse();

		try {

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

			blockingInter.saveUserDetails(tuserDetails);

			response.setResponse("jai");

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get status")
	@RequestMapping(value = "/getStatus", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getStatus(@RequestBody String getStatus) {
		OutputResponse response = new OutputResponse();

		try {

			M_Status1 blockUser = InputMapper.gson().fromJson(getStatus, M_Status1.class);

			ArrayList<M_Status1> statusData = blockingInter.getStatusData();

			response.setResponse(statusData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Provider state and service lines")
	@RequestMapping(value = "/addProviderStateAndServiceLines", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String ProviderStateAndServiceLines(@RequestBody String ProviderStateAndServiceLines) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking[] ps = InputMapper.gson().fromJson(ProviderStateAndServiceLines,
					M_Providerservicemapping_Blocking[].class);

			List<M_Providerservicemapping_Blocking> providerDetails1 = Arrays.asList(ps);

			BlockingTO[] blockingto1 = InputMapper.gson().fromJson(ProviderStateAndServiceLines, BlockingTO[].class);

			List<BlockingTO> blockingto2 = Arrays.asList(blockingto1);
			M_Providerservicemapping_Blocking resDataMap = null;
			List<M_Providerservicemapping_Blocking> resList = new ArrayList<M_Providerservicemapping_Blocking>();

			int providerDetailsIndex = 0;
			Integer[] stateIDs = null;
			for (BlockingTO blockingto : blockingto2) {
				stateIDs = blockingto.getStateID1();
				M_Providerservicemapping_Blocking providerDetails = providerDetails1.get(providerDetailsIndex);
				if (stateIDs.length > 0) {

					for (int statesIndex = 0; statesIndex < stateIDs.length; statesIndex++) {
						resDataMap = new M_Providerservicemapping_Blocking();
						resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
						resDataMap.setServiceID(providerDetails.getServiceID());
						resDataMap.setStateID(stateIDs[statesIndex]);
						resDataMap.setCreatedBy(providerDetails.getCreatedBy());
						resDataMap.setStatusID(providerDetails.getStatusID());
						resList.add(resDataMap);

					}
				} else if (stateIDs.length == 0) {
					resDataMap = new M_Providerservicemapping_Blocking();
					resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
					resDataMap.setServiceID(providerDetails.getServiceID());
					resDataMap.setStateID(providerDetails.getStateID());
					resDataMap.setCreatedBy(providerDetails.getCreatedBy());
					resDataMap.setStatusID(providerDetails.getStatusID());
					resList.add(resDataMap);

				}
				providerDetailsIndex++;
			}

			ArrayList<M_Providerservicemapping_Blocking> data = blockingInter.AddServiceProvider(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Delete provider state and service lines")
	@RequestMapping(value = "/deleteProviderStateAndServiceLines", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteProviderStateAndServiceLines(@RequestBody String deleteProviderStateAndServiceLines) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(deleteProviderStateAndServiceLines, M_Providerservicemapping_Blocking.class);

			BlockingTO blockingto = InputMapper.gson().fromJson(deleteProviderStateAndServiceLines, BlockingTO.class);
			Integer[] ServiceID = blockingto.getServiceID1();

			M_Providerservicemapping_Blocking resDataMap = null;
			List<M_Providerservicemapping_Blocking> resList = new ArrayList<M_Providerservicemapping_Blocking>();

			for (int i = 0; i < ServiceID.length; i++) {

				resDataMap = new M_Providerservicemapping_Blocking();

				resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
				resDataMap.setServiceID(ServiceID[i]);
				resDataMap.setStateID(providerDetails.getStateID());
				resDataMap.setCreatedBy(providerDetails.getCreatedBy());
				resDataMap.setStatusID(providerDetails.getStatusID());

				resList.add(resDataMap);
			}
			ArrayList<M_Providerservicemapping_Blocking> data = blockingInter.AddServiceProvider(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Create cit mapping with service lines")
	@RequestMapping(value = "/createCitMappingwithServiceLines", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createCitMappingwithServiceLines(@RequestBody String createCitMappingwithServiceLines) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking[] providerDetails = InputMapper.gson()
					.fromJson(createCitMappingwithServiceLines, M_Providerservicemapping_Blocking[].class);
			List<M_Providerservicemapping_Blocking> ctidata = Arrays.asList(providerDetails);

			String data = blockingInter.mapctidata(ctidata);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get mapped service lines and state to provider")
	@RequestMapping(value = "/getMappedServiceLinesAndStatetoProvider", headers = "Authorization", method = {
			RequestMethod.POST }, consumes = { "application/json" }, produces = { "application/json" })
	public String getMappedServiceLinesAndStatetoProvider(@RequestBody String getMappedServiceLinesAndStatetoProvider) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(getMappedServiceLinesAndStatetoProvider, M_Providerservicemapping_Blocking.class);

			ArrayList<M_Providerservicemapping_Blocking> getServiceLiensUsingProvider4 = blockingInter
					.getServiceLiensUsingProvider1(providerDetails);

			response.setResponse(getServiceLiensUsingProvider4.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Map provider and service lines")
	@RequestMapping(value = "/mapServiceLinesAndStatetoProvider", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String mapProviderAndServiceLines(@RequestBody String mapProviderAndServiceLines) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking[] ps = InputMapper.gson().fromJson(mapProviderAndServiceLines,
					M_Providerservicemapping_Blocking[].class);

			List<M_Providerservicemapping_Blocking> providerDetails1 = Arrays.asList(ps);

			BlockingTO[] blockingto1 = InputMapper.gson().fromJson(mapProviderAndServiceLines, BlockingTO[].class);

			List<BlockingTO> blockingto2 = Arrays.asList(blockingto1);
			M_Providerservicemapping_Blocking resDataMap = null;
			List<M_Providerservicemapping_Blocking> resList = new ArrayList<M_Providerservicemapping_Blocking>();

			int providerDetailsIndex = 0;
			Integer[] stateIDs = null;
			for (BlockingTO blockingto : blockingto2) {
				stateIDs = blockingto.getStateID1();
				M_Providerservicemapping_Blocking providerDetails = providerDetails1.get(providerDetailsIndex);

				if (stateIDs.length > 0) {

					for (int statesIndex = 0; statesIndex < stateIDs.length; statesIndex++) {
						resDataMap = new M_Providerservicemapping_Blocking();
						resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
						resDataMap.setServiceID(providerDetails.getServiceID());
						resDataMap.setStateID(stateIDs[statesIndex]);
						resDataMap.setCreatedBy(providerDetails.getCreatedBy());
						resDataMap.setStatusID(1);

						resList.add(resDataMap);

					}
				} else if (stateIDs.length == 0) {
					resDataMap = new M_Providerservicemapping_Blocking();
					resDataMap.setServiceProviderID(providerDetails.getServiceProviderID());
					resDataMap.setServiceID(providerDetails.getServiceID());
					resDataMap.setStateID(providerDetails.getStateID());
					resDataMap.setCreatedBy(providerDetails.getCreatedBy());
					resDataMap.setStatusID(1);
					resList.add(resDataMap);

				}
				providerDetailsIndex++;
			}

			ArrayList<M_Providerservicemapping_Blocking> data = blockingInter.AddServiceProvider(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Edit mapped service lines and state to provider")
	@RequestMapping(value = "/editMappedServiceLinesAndStatetoProvider", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editMappedServiceLinesAndStatetoProvider(
			@RequestBody String editMappedServiceLinesAndStatetoProvider) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(editMappedServiceLinesAndStatetoProvider, M_Providerservicemapping_Blocking.class);

			M_Providerservicemapping_Blocking getProviderData = blockingInter
					.getDataByProviderServiceMapId(providerDetails.getProviderServiceMapID());
			getProviderData.setServiceProviderID(providerDetails.getServiceProviderID());
			getProviderData.setServiceID(providerDetails.getServiceID());
			getProviderData.setStateID(providerDetails.getStateID());
			getProviderData.setModifiedBy(providerDetails.getModifiedBy());

			blockingInter.updateProviderData(getProviderData);

			response.setResponse(getProviderData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Delete mapped service lines and state to provider")
	@RequestMapping(value = "/deleteMappedServiceLinesAndStatetoProvider", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteMappedServiceLinesAndStatetoProvider(
			@RequestBody String deleteMappedServiceLinesAndStatetoProvider) {
		OutputResponse response = new OutputResponse();

		try {

			M_Providerservicemapping_Blocking providerDetails = InputMapper.gson()
					.fromJson(deleteMappedServiceLinesAndStatetoProvider, M_Providerservicemapping_Blocking.class);

			M_Providerservicemapping_Blocking getProviderData = blockingInter
					.getDataByProviderServiceMapId(providerDetails.getProviderServiceMapID());
			getProviderData.setDeleted(providerDetails.getDeleted());

			blockingInter.updateProviderData(getProviderData);

			response.setResponse(getProviderData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}

}
