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
package com.iemr.admin.controller.provideronboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.admin.data.provideronboard.M_104druggroup;
import com.iemr.admin.data.provideronboard.M_104drugmapping;
import com.iemr.admin.data.provideronboard.M_104drugmaster;
import com.iemr.admin.data.provideronboard.M_Calltype;
import com.iemr.admin.data.provideronboard.M_Category;
import com.iemr.admin.data.provideronboard.M_Feedbacknature;
import com.iemr.admin.data.provideronboard.M_Feedbacktype;
import com.iemr.admin.data.provideronboard.M_Institutedirectory;
import com.iemr.admin.data.provideronboard.M_Institutedirectorymapping;
import com.iemr.admin.data.provideronboard.M_Institutesubdirectory;
import com.iemr.admin.data.provideronboard.M_Institution;
import com.iemr.admin.data.provideronboard.M_Institutiontype;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.provideronboard.M_ServiceMaster;
import com.iemr.admin.data.provideronboard.M_Severity;
import com.iemr.admin.data.provideronboard.M_Subcategory;
import com.iemr.admin.data.provideronboard.M_Subservice;
import com.iemr.admin.data.provideronboard.M_SubservicemasterPA;
import com.iemr.admin.data.provideronboard.M_UserservicerolemappingForRole;
import com.iemr.admin.data.provideronboard.ServiceProviderStateServiceMappingWrapper;
import com.iemr.admin.data.provideronboard.ServiceProvider_Model;
import com.iemr.admin.data.provideronboard.V_Showprovideradmin;
import com.iemr.admin.data.provideronboard.V_Showsubcategory;
import com.iemr.admin.data.user.M_User;
import com.iemr.admin.exceptionhandler.DataNotFound;
import com.iemr.admin.service.provideronboard.Calltypeinter;
import com.iemr.admin.service.provideronboard.CategoryInter;
import com.iemr.admin.service.provideronboard.DrugMasterInter;
import com.iemr.admin.service.provideronboard.InstuteDirectoryInter;
import com.iemr.admin.service.provideronboard.M_FeedbacknatureInteger;
import com.iemr.admin.service.provideronboard.M_FeedbacktypeInter;
import com.iemr.admin.service.provideronboard.M_InstitutedirectorymappingInter;
import com.iemr.admin.service.provideronboard.M_InstitutesubdirectoryInter;
import com.iemr.admin.service.provideronboard.M_InstitutionInter;
import com.iemr.admin.service.provideronboard.M_InstitutiontypeInter;
import com.iemr.admin.service.provideronboard.M_ServiceMasterInter;
import com.iemr.admin.service.provideronboard.M_SeverityInter;
import com.iemr.admin.service.provideronboard.ServiceProvider_ServiceImpl;
import com.iemr.admin.service.provideronboard.SubServiceInter;
import com.iemr.admin.service.user.IemrUserServiceImpl;
import com.iemr.admin.to.CallType2;
import com.iemr.admin.to.CalltypeTO;
import com.iemr.admin.to.CategoryTO;
import com.iemr.admin.to.DrugMasterTO;
import com.iemr.admin.to.Example;
import com.iemr.admin.to.SubCat1;
import com.iemr.admin.to.SubCat2;
import com.iemr.admin.to.SubServiceTO;
import com.iemr.admin.to.SubServiceTO2;
import com.iemr.admin.to.blocking.BlockingTO;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
public class ProviderOnBoardController {

	/**
	 * creating Logger Object using logger variable...
	 */

	private Logger logger = LoggerFactory.getLogger(ProviderOnBoardController.class);
	private InputMapper inputMapper = new InputMapper();

	@Autowired
	private M_InstitutedirectorymappingInter m_InstitutedirectorymappingInter;

	@Autowired
	private M_InstitutesubdirectoryInter m_InstitutesubdirectoryInter;

	@Autowired
	private M_InstitutionInter m_InstitutionInter;

	@Autowired
	private M_FeedbacknatureInteger m_FeedbacknatureInteger;
	@Autowired
	private M_InstitutiontypeInter m_InstitutiontypeInter;

	@Autowired
	private InstuteDirectoryInter instuteDirectoryInter;

	@Autowired
	private M_FeedbacktypeInter m_FeedbacktypeInter;

	@Autowired
	private M_SeverityInter M_ServerityInter;

	@Autowired
	private DrugMasterInter drugMasterInter;

	@Autowired
	private CategoryInter categoryInter;

	@Autowired
	private SubServiceInter subServiceInter;

	@Autowired
	private Calltypeinter calltypeinter;

	@Autowired
	private M_ServiceMasterInter m_ServiceMasterInter;
	private ServiceProvider_ServiceImpl serviceProvider_ServiceImpl;
	private IemrUserServiceImpl iemrUserServiceImpl;

	@Autowired
	public void setServiceProvider_ServiceImpl(ServiceProvider_ServiceImpl serviceProvider_ServiceImpl) {
		this.serviceProvider_ServiceImpl = serviceProvider_ServiceImpl;
	}

	@Autowired
	public void setIemrUserServiceImpl(IemrUserServiceImpl iemrUserServiceImpl) {
		this.iemrUserServiceImpl = iemrUserServiceImpl;
	}

	
	@Operation(summary = "Create and map provder")
	@RequestMapping(value = "/providerCreationAndMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String providerCreationAndMapping(@RequestBody String comingRequest) {

		OutputResponse response = new OutputResponse();

		logger.debug("request" + comingRequest);
		try {
			Set<ServiceProvider_Model> serviceProviderSet = new HashSet<ServiceProvider_Model>();

			ServiceProvider_Model serviceProvider_Model = InputMapper.gson().fromJson(comingRequest,
					ServiceProvider_Model.class);

			ServiceProviderStateServiceMappingWrapper serviceProviderStateServiceMappingWrapper = InputMapper.gson()
					.fromJson(comingRequest, ServiceProviderStateServiceMappingWrapper.class);

			serviceProviderSet.add(serviceProvider_Model);
			Integer serviceProviderId = this.serviceProvider_ServiceImpl.createProvider(serviceProviderSet);

			if (serviceProviderId != null && serviceProviderId > 0) {
				ArrayList<Map<String, Object>> obj = serviceProviderStateServiceMappingWrapper
						.getStateAndServiceMapList();

				Set<M_ProviderServiceMapping> recordSet = new HashSet<M_ProviderServiceMapping>();
				M_ProviderServiceMapping m_ProviderServiceMapping = null;

				if (obj.size() > 0) {
					for (Map<String, Object> mapObj : obj) {
						Set<String> keySet = mapObj.keySet();
						Object stateID = null;
						ArrayList<Object> serviceList = null;
						if (keySet.contains("stateId"))
							stateID = mapObj.get("stateId");

						if (keySet.contains("services"))
							serviceList = (ArrayList<Object>) mapObj.get("services");
						if (stateID != null && serviceList != null) {
							for (Object serviceID : serviceList) {
								m_ProviderServiceMapping = new M_ProviderServiceMapping();
								m_ProviderServiceMapping.setServiceProviderID(serviceProviderId);
								m_ProviderServiceMapping.setStateID(Integer.parseInt(stateID.toString()));
								m_ProviderServiceMapping.setServiceID(Integer.parseInt(serviceID.toString()));
								m_ProviderServiceMapping.setStatusID(serviceProvider_Model.getStatusID());
								if (serviceProvider_Model.getCreatedBy() != null)
									m_ProviderServiceMapping.setCreatedBy(serviceProvider_Model.getCreatedBy());
								else
									m_ProviderServiceMapping.setCreatedBy("");

								recordSet.add(m_ProviderServiceMapping);
								logger.debug("Adding Record into list");
							}
						}
					}
				}
				List<M_ProviderServiceMapping> proSerMapID = serviceProvider_ServiceImpl
						.mapProviderStateService(recordSet);
				logger.debug("provide service state mapping done here....");

				logger.debug("Creating Provider Admin");
				M_User m_user = inputMapper.gson().fromJson(comingRequest, M_User.class);

				ArrayList<Map<String, Object>> m_userDetails = m_user.getProviderAdminDetails();

				int userID = this.iemrUserServiceImpl.createUser(m_userDetails,
						m_ProviderServiceMapping.getCreatedBy());

				logger.debug("Provider Admin creation done");

				logger.debug(
						"Creating inserting data in user service Role mapping table for Mapping role of Provider Admin");

				if (proSerMapID.size() > 0 && userID > 0) {
					List<Integer> proSerStateMapIdList = new ArrayList<Integer>();
					for (M_ProviderServiceMapping psmObj : proSerMapID) {
						proSerStateMapIdList.add(psmObj.getProviderServiceMapID());
					}
					int x = this.iemrUserServiceImpl.createUserServiceRoleMapping(proSerStateMapIdList, userID,
							m_ProviderServiceMapping.getCreatedBy());

					if (x == 1) {

						logger.debug("Role Creation Done");
						response.setResponse("true");
					} else {
						logger.debug("Role Creation Fail");
						response.setResponse("false");
					}
				} else {
					/**
					 * m_userservicerolemapping and m_user Rollback will happen....
					 */
					response.setResponse("false");
				}

			} else {
				/**
				 * Rollback provider creation.....
				 */
				response.setResponse("false");
			}
		} catch (Exception e) {
			logger.error("provider creation  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();
	}

	
	@Operation(summary = "Update provider")
	@RequestMapping(value = "/updateProvider", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String updateProvider(@RequestBody String comingRequest) {

		logger.debug("Start Updating Provider");

		OutputResponse response = new OutputResponse();
		logger.debug("request" + comingRequest);
		try {
			Set<ServiceProvider_Model> serviceProviderSet = new HashSet<ServiceProvider_Model>();

			ServiceProvider_Model serviceProvider_Model = InputMapper.gson().fromJson(comingRequest,
					ServiceProvider_Model.class);

			ServiceProviderStateServiceMappingWrapper serviceProviderStateServiceMappingWrapper = InputMapper.gson()
					.fromJson(comingRequest, ServiceProviderStateServiceMappingWrapper.class);

			ServiceProvider_Model getProviderDetails = serviceProvider_ServiceImpl
					.getProviderData(serviceProvider_Model.getServiceProviderId());

			getProviderDetails.setServiceProviderName(serviceProvider_Model.getServiceProviderName());
			getProviderDetails.setLogoFileName(serviceProvider_Model.getLogoFileName());
			getProviderDetails.setPrimaryContactAddress(serviceProvider_Model.getPrimaryContactAddress());
			getProviderDetails.setPrimaryContactEmailID(serviceProvider_Model.getPrimaryContactEmailID());
			getProviderDetails.setPrimaryContactName(serviceProvider_Model.getPrimaryContactName());
			getProviderDetails.setPrimaryContactNo(serviceProvider_Model.getPrimaryContactNo());
			getProviderDetails
					.setPrimaryContactValidityTillDate(serviceProvider_Model.getPrimaryContactValidityTillDate());
			getProviderDetails.setSecondaryContactAddress(serviceProvider_Model.getSecondaryContactAddress());
			getProviderDetails.setSecondaryContactEmailID(serviceProvider_Model.getSecondaryContactEmailID());
			getProviderDetails.setSecondaryContactName(serviceProvider_Model.getSecondaryContactName());
			getProviderDetails.setSecondaryContactNo(serviceProvider_Model.getSecondaryContactNo());
			getProviderDetails
					.setSecondaryContactValidityTillDate(serviceProvider_Model.getSecondaryContactValidityTillDate());

			ServiceProvider_Model saveProviderDetails = serviceProvider_ServiceImpl
					.upDateProviderDetails(getProviderDetails);

			response.setResponse(saveProviderDetails.toString());

		} catch (Exception e) {

			logger.error("provider updation  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();
	}

	
	@Operation(summary = "Get service line")
	@RequestMapping(value = "/getServiceline", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getServiceLine(@RequestBody String serviceline) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + serviceline);
		try {

			M_ServiceMaster serviceProvider_Model = InputMapper.gson().fromJson(serviceline, M_ServiceMaster.class);

			List<M_ServiceMaster> master1 = m_ServiceMasterInter.getAllServiceLine();

			response.setResponse(master1.toString());

		} catch (Exception e) {

			logger.error("serviceline  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get all provider name")
	@RequestMapping(value = "/getAllProvider", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllProviderName(@RequestBody String getServiceProvider) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + getServiceProvider);
		try {

			ServiceProvider_Model serviceProvider_Model = InputMapper.gson().fromJson(getServiceProvider,
					ServiceProvider_Model.class);

			ArrayList<ServiceProvider_Model> providerName = this.serviceProvider_ServiceImpl.getAllProviderName();

			response.setResponse(providerName.toString());

		} catch (Exception e) {
			logger.error("getting AllProvider  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get provider name")
	@RequestMapping(value = "/checkProvider", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getProviderName(@RequestBody String getServiceProvider) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + getServiceProvider);
		try {

			ServiceProvider_Model serviceProvider_Model = InputMapper.gson().fromJson(getServiceProvider,
					ServiceProvider_Model.class);

			String providerName = this.serviceProvider_ServiceImpl
					.getProviderName(serviceProvider_Model.getServiceProviderName());

			if (providerName != null) {
				response.setResponse("provider_name_exists");
			} else {
				response.setResponse("provider_name_doesnt_exist");
			}

		} catch (Exception e) {

			logger.error("checking Provider  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get service provider id")
	@RequestMapping(value = "/getServiceProviderid", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getProviderId(@RequestBody String getServiceProviderId) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + getServiceProviderId);
		try {

			M_ProviderServiceMapping m_ProviderServiceMapping = InputMapper.gson().fromJson(getServiceProviderId,
					M_ProviderServiceMapping.class);

			M_ProviderServiceMapping serviceProviderId = this.serviceProvider_ServiceImpl
					.getProviderserviceMapId(m_ProviderServiceMapping.getProviderServiceMapID());

			response.setResponse(serviceProviderId.toString());

		} catch (Exception e) {

			logger.error("getting ProviderId  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Save call type data")
	@RequestMapping(value = "/m/saveCalltypedata", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveCallTypeData(@RequestBody String saveCalltypeData) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + saveCalltypeData);
		try {

			M_Calltype[] callMasters = InputMapper.gson().fromJson(saveCalltypeData, M_Calltype[].class);
			List<M_Calltype> callMaster = Arrays.asList(callMasters);

			CalltypeTO[] cto = InputMapper.gson().fromJson(saveCalltypeData, CalltypeTO[].class);
			List<CalltypeTO> callMasterc = Arrays.asList(cto);

			Example[] cto1 = InputMapper.gson().fromJson(saveCalltypeData, Example[].class);

			M_Calltype resDataMap = null;
			List<M_Calltype> resList = new ArrayList<M_Calltype>();
			int x = 0;
			for (Example ex : cto1) {
				for (int i = 0; i <= 0; i++) {

					CallType2[] type2 = ex.getCallType1();
					for (CallType2 call : type2) {
						for (int j = 0; j < call.getCallTypeDesc1().size(); j++) {
							resDataMap = new M_Calltype();
							resDataMap.setCallGroupType(callMaster.get(x).getCallGroupType());
							resDataMap.setCallType(call.getCalltype());
							resDataMap.setCallTypeDesc(call.getCallTypeDesc1().get(j));
							resDataMap.setFitToBlock(Boolean.parseBoolean(call.getFitToBlock1().get(j)));
							resDataMap.setProviderServiceMapID(call.getProviderServiceMapID());
							resDataMap.setFitForFollowup(call.getFitForFollowup1().get(j));
							resDataMap.setIsInbound(call.getIsInbound1().get(j));
							resDataMap.setIsOutbound(call.getIsOutbound1().get(j));
							resDataMap.setCreatedBy(callMaster.get(x).getCreatedBy());

							resList.add(resDataMap);
						}
					}
				}
				x++;
			}
			ArrayList<M_Calltype> data = calltypeinter.saveCallList(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("creating calltype  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Create call type data")
	@RequestMapping(value = "/m/createCalltypedata", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createCalltypeData(@RequestBody String createCalltypedata) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + createCalltypedata);

		try {

			M_Calltype[] callMasters = InputMapper.gson().fromJson(createCalltypedata, M_Calltype[].class);
			List<M_Calltype> callMaster = Arrays.asList(callMasters);

			ArrayList<M_Calltype> calltypedata = calltypeinter.createCalltype(callMaster);

			response.setResponse(calltypedata.toString());

		} catch (Exception e) {

			logger.error("creating calltype  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get call type data")
	@RequestMapping(value = "/m/getCalltypedata", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getCallTypeData(@RequestBody String saveCalltypeData) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + saveCalltypeData);
		try {

			M_Calltype callMasters = InputMapper.gson().fromJson(saveCalltypeData, M_Calltype.class);

			ArrayList<M_Calltype> data = calltypeinter.getCalltypeData(callMasters.getProviderServiceMapID());

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("getting calltype  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update call type data")
	@RequestMapping(value = "/m/updateCalltypedata", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateCallTypeData(@RequestBody String updateCalltypeData) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateCalltypeData);
		try {

			M_Calltype callMasters = InputMapper.gson().fromJson(updateCalltypeData, M_Calltype.class);

			M_Calltype updateData = calltypeinter.updateCallType(callMasters.getCallTypeID());

			updateData.setCallType(callMasters.getCallType());
			updateData.setCallGroupType(callMasters.getCallGroupType());
			updateData.setCallTypeDesc(callMasters.getCallTypeDesc());
			updateData.setProviderServiceMapID(callMasters.getProviderServiceMapID());
			updateData.setFitToBlock(callMasters.getFitToBlock());
			updateData.setFitForFollowup(callMasters.getFitForFollowup());
			updateData.setIsInbound(callMasters.getIsInbound());
			updateData.setIsOutbound(callMasters.getIsOutbound());
			updateData.setProcessed(callMasters.getProcessed());
			updateData.setMaxRedial(callMasters.getMaxRedial());

			M_Calltype saveupdateData = calltypeinter.saveupdatedData(updateData);

			response.setResponse(saveupdateData.toString());

		} catch (Exception e) {

			logger.error("updatation of  calltype  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete call type")
	@RequestMapping(value = "/m/deleteCalltype", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteCallType(@RequestBody String deleteCalltype) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteCalltype);
		try {

			M_Calltype callMasters = InputMapper.gson().fromJson(deleteCalltype, M_Calltype.class);

			M_Calltype deletedData = calltypeinter.updateCallType(callMasters.getCallTypeID());

			deletedData.setDeleted(callMasters.getDeleted());

			M_Calltype saveupdateData = calltypeinter.saveupdatedData(deletedData);

			response.setResponse(saveupdateData.toString());

		} catch (Exception e) {
			logger.error("deleting calltype  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Save sub service data")
	@RequestMapping(value = "/m/saveSubserviceData", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveSubServiceData(@RequestBody String SubserviceData) {
		OutputResponse response = new OutputResponse();

		logger.debug("request" + SubserviceData);

		try {

			SubServiceTO2[] pre = InputMapper.gson().fromJson(SubserviceData, SubServiceTO2[].class);

			M_Subservice[] callMasters = InputMapper.gson().fromJson(SubserviceData, M_Subservice[].class);
			List<M_Subservice> callMaster = Arrays.asList(callMasters);

			SubServiceTO[] cto = InputMapper.gson().fromJson(SubserviceData, SubServiceTO[].class);
			List<SubServiceTO> callMasterc = Arrays.asList(cto);

			M_Subservice resDataMap = null;
			List<M_Subservice> resList = new ArrayList<M_Subservice>();
			int x = 0;
			for (SubServiceTO2 ex : pre) {
				for (int i = 0; i <= 0; i++) {
					SubServiceTO[] type2 = ex.getSubServiceDetails();
					for (SubServiceTO call : type2) {
						resDataMap = new M_Subservice();
						resDataMap.setSubServiceName(call.getSubServiceName());
						resDataMap.setSubServiceDesc(call.getSubServiceDesc());
						resDataMap.setProviderServiceMapID(callMaster.get(x).getProviderServiceMapID());
						resDataMap.setCreatedBy(callMaster.get(x).getCreatedBy());

						resList.add(resDataMap);
					}
				}
			}
			x++;

			ArrayList<M_Subservice> data = subServiceInter.saveSubList(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("create subService  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Find sub service name by map id")
	@RequestMapping(value = "/m/FindSubSerive", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String FindSubSeriveNameByMapId(@RequestBody String FindSubSeriveName) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + FindSubSeriveName);
		try {

			M_SubservicemasterPA callMasters = InputMapper.gson().fromJson(FindSubSeriveName,
					M_SubservicemasterPA.class);

			ArrayList<M_SubservicemasterPA> getSubServiceName = subServiceInter
					.getServiceNameByServiceID(callMasters.getServiceID());

			response.setResponse(getSubServiceName.toString());

		} catch (Exception e) {
			logger.error("Find subService  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get sub service name")
	@RequestMapping(value = "/m/getSubSerive", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getSubSeriveName(@RequestBody String getSubSeriveName) {
		OutputResponse response = new OutputResponse();
		logger.debug("resqest" + getSubSeriveName);

		try {

			M_Subservice callMasters = InputMapper.gson().fromJson(getSubSeriveName, M_Subservice.class);

			ArrayList<M_Subservice> getSubService = subServiceInter
					.getsubServiceName(callMasters.getProviderServiceMapID());

			response.setResponse(getSubService.toString());

		} catch (Exception e) {
			logger.error("get subService  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update sub service")
	@RequestMapping(value = "/m/updateSubSerive", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateSubSerive(@RequestBody String updateSubSeriveName) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateSubSeriveName);
		try {

			M_Subservice subservice = InputMapper.gson().fromJson(updateSubSeriveName, M_Subservice.class);
			M_Subservice getSubService = subServiceInter.getsubServiceNameById(subservice.getSubServiceID());

			getSubService.setSubServiceName(subservice.getSubServiceName());
			getSubService.setSubServiceDesc(subservice.getSubServiceDesc());
			getSubService.setProviderServiceMapID(subservice.getProviderServiceMapID());
			getSubService.setProcessed(subservice.getProcessed());

			M_Subservice savedata = subServiceInter.saveupdatedData(getSubService);

			response.setResponse(getSubService.toString());

		} catch (Exception e) {
			logger.error("updata subService  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete sub service")
	@RequestMapping(value = "/m/deleteSubSerive", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteSubSerive(@RequestBody String updateSubSeriveName) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateSubSeriveName);
		try {

			M_Subservice subservice = InputMapper.gson().fromJson(updateSubSeriveName, M_Subservice.class);

			M_Subservice getSubService = subServiceInter.getsubServiceNameById(subservice.getSubServiceID());

			getSubService.setDeleted(subservice.getDeleted());

			M_Subservice deletedData = subServiceInter.saveupdatedData(getSubService);

			response.setResponse(getSubService.toString());

		} catch (Exception e) {
			logger.error("delete subService  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Save category")
	@RequestMapping(value = "/m/saveCategory", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String saveCategory(@RequestBody String saveCategory) {
		OutputResponse response = new OutputResponse();
		logger.debug("requset" + saveCategory);
		try {

			M_Category categoryid = InputMapper.gson().fromJson(saveCategory, M_Category.class);

			SubCat2 pre = InputMapper.gson().fromJson(saveCategory, SubCat2.class);

			M_Subcategory subcategory = InputMapper.gson().fromJson(saveCategory, M_Subcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(saveCategory, CategoryTO.class);

			Integer getcategoryId = categoryInter.getCategoryId(categoryid);
			SubCat1[] pre1 = pre.getSubcatArray();
			M_Subcategory resDataMap = null;
			List<M_Subcategory> resList = new ArrayList<M_Subcategory>();
			for (SubCat1 predata : pre1) {
				resDataMap = new M_Subcategory();
				resDataMap.setCategoryID(getcategoryId);
				resDataMap.setSubCategoryName(predata.getSubCategoryName());
				resDataMap.setSubCategoryDesc(predata.getSubCategoryDesc());
				resDataMap.setSubCatFilePath(predata.getSubCatFilePath());
				resDataMap.setCreatedBy(subcategory.getCreatedBy());
				resList.add(resDataMap);

			}

			ArrayList<M_Subcategory> data = categoryInter.saveSubCatData(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Save Category  failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Save category user exist")
	@RequestMapping(value = "/m/saveCategoryUseExist", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveCategoryUseExist(@RequestBody String saveCategoryUseExist) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + saveCategoryUseExist);
		try {

			M_Category categoryid = InputMapper.gson().fromJson(saveCategoryUseExist, M_Category.class);

			SubCat2 pre = InputMapper.gson().fromJson(saveCategoryUseExist, SubCat2.class);

			M_Subcategory subcategory = InputMapper.gson().fromJson(saveCategoryUseExist, M_Subcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(saveCategoryUseExist, CategoryTO.class);
			SubCat1[] pre1 = pre.getSubcatArray();
			M_Subcategory resDataMap = null;
			List<M_Subcategory> resList = new ArrayList<M_Subcategory>();
			for (SubCat1 predata : pre1) {
				resDataMap = new M_Subcategory();
				resDataMap.setCategoryID(subcategory.getCategoryID());
				resDataMap.setSubCategoryName(predata.getSubCategoryName());
				resDataMap.setSubCategoryDesc(predata.getSubCategoryDesc());
				resDataMap.setSubCatFilePath(predata.getSubCatFilePath());
				resDataMap.setCreatedBy(subcategory.getCreatedBy());
				resList.add(resDataMap);
			}

			ArrayList<M_Subcategory> data = categoryInter.saveSubCatData(resList);

			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Save Category User Exist failed with exception " + e.getMessage(), e);

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get category by sub service id")
	@RequestMapping(value = "/m/getCategoryBySubServiceID", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getCategoryBySubServiceID(@RequestBody String getCategoryBySubServiceID) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getCategoryBySubServiceID);
		try {

			V_Showsubcategory subcategory = InputMapper.gson().fromJson(getCategoryBySubServiceID,
					V_Showsubcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(getCategoryBySubServiceID, CategoryTO.class);

			ArrayList<V_Showsubcategory> getsubCategoryByMapid = categoryInter.getCategoryByMapIDAndSubServiceID(
					subcategory.getProviderServiceMapID(), subcategory.getSubServiceID());

			response.setResponse(getsubCategoryByMapid.toString());

		} catch (Exception e) {
			logger.error("CategoryBySubServieId  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get sub category")
	@RequestMapping(value = "/m/getsubCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getsubCategory(@RequestBody String getsubCategory1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getsubCategory1);
		try {

			M_Category categoryid = InputMapper.gson().fromJson(getsubCategory1, M_Category.class);

			M_Subcategory subcategory = InputMapper.gson().fromJson(getsubCategory1, M_Subcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(getsubCategory1, CategoryTO.class);

			ArrayList<M_Subcategory> getsubCategory = categoryInter.getCategory(subcategory.getCategoryID());

			response.setResponse(getsubCategory.toString());

		} catch (Exception e) {
			logger.error("getSubCategory failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get category")
	@RequestMapping(value = "/m/getCategory", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getCategory(@RequestBody String getCategory) {
		OutputResponse response = new OutputResponse();
		logger.debug("resqust" + getCategory);
		try {

			M_Category categoryid = InputMapper.gson().fromJson(getCategory, M_Category.class);

			M_Subcategory subcategory = InputMapper.gson().fromJson(getCategory, M_Subcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(getCategory, CategoryTO.class);

			ArrayList<M_Category> getsubCategory = categoryInter.getAllCategory(categoryid.getSubServiceID(),
					categoryid.getProviderServiceMapID());

			response.setResponse(getsubCategory.toString());

		} catch (Exception e) {
			logger.error("getCategory failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update sub category")
	@RequestMapping(value = "/m/updateSubCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateSubCategory(@RequestBody String updateCategory) {
		OutputResponse response = new OutputResponse();
		logger.debug("reqest" + updateCategory);
		try {

			M_Category category = InputMapper.gson().fromJson(updateCategory, M_Category.class);

			M_Subcategory subcategory = InputMapper.gson().fromJson(updateCategory, M_Subcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(updateCategory, CategoryTO.class);

			M_Subcategory getsubCategory = categoryInter.getSubCategory(subcategory.getSubCategoryID());

			getsubCategory.setCategoryID(subcategory.getCategoryID());
			getsubCategory.setSubCategoryName(subcategory.getSubCategoryName());
			getsubCategory.setSubCategoryDesc(subcategory.getSubCategoryDesc());
			getsubCategory.setSubCatFilePath(subcategory.getSubCatFilePath());
			M_Subcategory updateData = categoryInter.updateSubCatData(getsubCategory);

			response.setResponse(getsubCategory.toString());

		} catch (Exception e) {
			logger.error("updateCategory failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get drug data")
	@RequestMapping(value = "/m/getDrugData", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getDrugData(
			@Param(value = "{\"drugID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String drugMaster)
			throws IEMRException {
		OutputResponse response = new OutputResponse();
		logger.debug("requset" + drugMaster);

		M_104drugmaster drugData = InputMapper.gson().fromJson(drugMaster, M_104drugmaster.class);
		try {
			ArrayList<M_104drugmaster> data = drugMasterInter.getAllDrugData(drugData.getDrugID(),
					drugData.getServiceProviderID(), drugData.getDeleted());

			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("get Drug failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get drug groups")
	@RequestMapping(value = "/m/getDrugGroups", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getDrugGroups(
			@Param(value = "{\"drugGroupID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String drugMaster)
			throws IEMRException {
		OutputResponse response = new OutputResponse();
		logger.debug("reqest" + drugMaster);
		M_104druggroup druggroup = InputMapper.gson().fromJson(drugMaster, M_104druggroup.class);
		try {
			ArrayList<M_104druggroup> data = drugMasterInter.getAllDrugGroups(druggroup.getDrugGroupID(),
					druggroup.getServiceProviderID(), druggroup.getDeleted());

			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("getDrugGroups failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get drug group mappings")
	@RequestMapping(value = "/m/getDrugGroupMappings", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getDrugGroupMappings(@RequestBody String drugMaster) throws IEMRException {
		OutputResponse response = new OutputResponse();
		logger.debug("requsest" + drugMaster);
		M_ProviderServiceMapping drugmapping = InputMapper.gson().fromJson(drugMaster, M_ProviderServiceMapping.class);

		try {
			ArrayList<M_104drugmapping> data = drugMasterInter.getAllDrugGroupMappings(null,
					drugmapping.getServiceProviderID(), drugmapping.getServiceID());

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("getDrugGroups failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update drug group")
	@RequestMapping(value = "/m/updateDrugGroup", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateDrugGroup(@RequestBody String updateDrugGroup) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateDrugGroup);
		try {

			M_104druggroup druggroup = InputMapper.gson().fromJson(updateDrugGroup, M_104druggroup.class);

			M_104druggroup drugGroupData = drugMasterInter.getDrugGroupById(druggroup.getDrugGroupID());

			drugGroupData.setDrugGroup(druggroup.getDrugGroup());
			drugGroupData.setDrugGroupDesc(druggroup.getDrugGroupDesc());
			drugGroupData.setModifiedBy(druggroup.getModifiedBy());

			M_104druggroup saveupdateData = drugMasterInter.saveUpdatedDrugGroup(drugGroupData);

			response.setResponse(saveupdateData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("updateDrug Group  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}
		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update drug master")
	@RequestMapping(value = "/m/updateDrugMaster", headers = "Authorization", method = RequestMethod.POST, produces = "application/json")
	public String updateDrugMaster(@RequestBody String updateDrugData) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateDrugData);
		try {
			M_104drugmaster drugmaster = InputMapper.gson().fromJson(updateDrugData, M_104drugmaster.class);

			M_104drugmaster drugMasterdata = drugMasterInter.getDrugDataById(drugmaster.getDrugID());

			drugMasterdata.setDrugName(drugmaster.getDrugName());
			drugMasterdata.setDrugDesc(drugmaster.getDrugDesc());
			drugMasterdata.setRemarks(drugmaster.getRemarks());
			drugMasterdata.setModifiedBy(drugmaster.getModifiedBy());

			M_104drugmaster saveupdateData = drugMasterInter.saveUpdatedData(drugMasterdata);

			response.setResponse(saveupdateData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("update drug Master  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}
		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update drug mapping")
	@RequestMapping(value = "/m/updateDrugMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateDrugMapping(@RequestBody String updateDrugData) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateDrugData);
		try {
			M_104drugmapping drugmap = InputMapper.gson().fromJson(updateDrugData, M_104drugmapping.class);

			M_104drugmapping drugMappingdata = drugMasterInter.getDrugMappingsById(drugmap.getDrugMapID());

			drugMappingdata.setDrugGroupID(drugmap.getDrugGroupID());
			drugMappingdata.setDrugGroupName(drugmap.getDrugGroupName());
			drugMappingdata.setDrugId(drugmap.getDrugId());
			drugMappingdata.setDrugName(drugmap.getDrugName());
			drugMappingdata.setRemarks(drugmap.getRemarks());

			M_104drugmapping saveupdateData = drugMasterInter.saveUpdatedDrugMapping(drugMappingdata);

			response.setResponse(saveupdateData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("updateDrugMapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	
	@Operation(summary = "Update drug status")
	@RequestMapping(value = "/m/updateDrugStatus", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateDrugStatus(@RequestBody String updateDrugStatus) {
		OutputResponse output = new OutputResponse();
		logger.debug("request" + updateDrugStatus);
		try {

			M_104druggroup druggroup = InputMapper.gson().fromJson(updateDrugStatus, M_104druggroup.class);

			M_104drugmaster drugmaster = InputMapper.gson().fromJson(updateDrugStatus, M_104drugmaster.class);

			M_104drugmapping drugMapping = InputMapper.gson().fromJson(updateDrugStatus, M_104drugmapping.class);

			String response = "";
			if (null != drugmaster.getDrugID()) {

				int saveupdateData = drugMasterInter.updateDrugStatus(drugmaster);

				response = "Drug status updaated to deleted: " + drugmaster.getDeleted();

			} else if (null != druggroup.getDrugGroupID()) {

				Integer getDrugGrupId = drugMasterInter.updateDrugGroupStatus(druggroup);

				response = "DrugGroup status updaated to deleted: " + drugmaster.getDeleted();

			} else if (null != drugMapping.getDrugMapID()) {
				Integer getDrugMapId = drugMasterInter.updateDrugMappingStatus(drugMapping);

				response = "DrugGroup status updaated to deleted: " + drugMapping.getDeleted();
			}

			output.setResponse(response);

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Update Drug Status failed with exception " + e.getMessage(), e);
			output.setError(e);

		}

		return output.toString();

	}

	
	@Operation(summary = "Stores drug group ")
	@RequestMapping(value = "/m/saveDrugGroup", headers = "Authorization", method = { RequestMethod.POST })
	public String saveDrugGroup(
			@Param(value = "{\"drugGroup\":\"string\", \"drugGroupDesc\":\"string\", \"providerServiceMapID\":\"integer\", \"deleted\":\"boolean\","
					+ " \"processed\":\"charactor\", \"createdBy\":\"string\"}") @RequestBody String drugMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();
		logger.debug("request" + drugMaster);

		DrugMasterTO drugGroup = InputMapper.gson().fromJson(drugMaster, DrugMasterTO.class);

		try {

			ArrayList<M_104druggroup> druggroups = drugMasterInter.saveDrugGroup(drugGroup.getDrugGroups());
			output.setResponse(druggroups.toString());
		} catch (Exception e) {
			logger.error("Save DrugGroup  failed with exception " + e.getMessage(), e);
			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Store drug detail")
	@RequestMapping(value = "/m/saveDrug", headers = "Authorization", method = { RequestMethod.POST })
	public String saveDrug(
			@Param(value = "{\"drugName\":\"string\", \"drugDesc\":\"string\", \"remarks\":\"integer\", \"deleted\":\"boolean\","
					+ " \"processed\":\"charactor\", \"providerServiceMapID\":\"integer\", \"createdBy\":\"string\"}") @RequestBody String DrugMasterTO)
			throws IEMRException {

		OutputResponse output = new OutputResponse();
		logger.debug("request" + DrugMasterTO);

		DrugMasterTO drugGroup = InputMapper.gson().fromJson(DrugMasterTO, DrugMasterTO.class);

		try {

			ArrayList<M_104drugmaster> data = drugMasterInter.saveDrugData(drugGroup.getDrugMasters());

			output.setResponse(data.toString());
		} catch (Exception e) {
			logger.error("Save Drug  failed with exception " + e.getMessage(), e);
			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Map drug with drug group")
	@RequestMapping(value = "/m/mapDrugWithGroup", headers = "Authorization", method = { RequestMethod.POST })

	public String mapDrugWithGroup(
			@Param(value = "{\"drugId\":\"integer\", \"drugName\":\"string\", \"drugGroupID\":\"integer\", \"drugGroupName\":\"string\","
					+ " \"remarks\":\"integer\", "
					+ " \"processed\":\"charactor\", \"providerServiceMapID\":\"integer\", \"createdBy\":\"string\"}") @RequestBody String DrugMasterTO)
			throws IEMRException {

		OutputResponse output = new OutputResponse();
		logger.debug("request" + DrugMasterTO);

		DrugMasterTO drugMaster = InputMapper.gson().fromJson(DrugMasterTO, DrugMasterTO.class);

		try {

			ArrayList<M_104drugmapping> data = drugMasterInter.mapDrugWithGroup(drugMaster.getDrugMappings());

			output.setResponse(data.toString());
		} catch (Exception e) {
			logger.error("Map Drug with Group  failed with exception " + e.getMessage(), e);
			output.setError(e);
		}
		return output.toString();
	}

	
	@Operation(summary = "Get severity")
	@RequestMapping(value = "/m/getServerity", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getServerity(@RequestBody String getServerity) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getServerity);

		try {

			M_Severity getSeverity = InputMapper.gson().fromJson(getServerity, M_Severity.class);

			ArrayList<M_Severity> getSerdata = M_ServerityInter.getServerity(getSeverity.getProviderServiceMapID());

			response.setResponse(getSerdata.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			logger.error("get severity  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("respone" + response);
		return response.toString();

	}

	
	@Operation(summary = "Save severity")
	@RequestMapping(value = "/m/saveServerity", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String saveServerity(@RequestBody String saveServerity) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + saveServerity);

		try {

			M_Severity[] saveServeritytype = InputMapper.gson().fromJson(saveServerity, M_Severity[].class);
			List<M_Severity> serveritydata = Arrays.asList(saveServeritytype);

			ArrayList<M_Severity> saveSerdata = M_ServerityInter.saveServerity(serveritydata);

			response.setResponse(saveSerdata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Save Serverity  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete severity")
	@RequestMapping(value = "/m/deleteServerity", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteServerity(@RequestBody String deleteServerity) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteServerity);
		try {

			M_Severity getSeverity = InputMapper.gson().fromJson(deleteServerity, M_Severity.class);

			M_Severity getDAta = M_ServerityInter.getDataByServId(getSeverity.getSeverityID());
			getDAta.setDeleted(getSeverity.getDeleted());

			M_Severity deletedata = M_ServerityInter.deletedataser(getDAta);

			response.setResponse(deletedata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete serverity  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Edit severity")
	@RequestMapping(value = "/m/editServerity", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editServerity(@RequestBody String deleteServerity) {
		OutputResponse response = new OutputResponse();
		logger.debug("requset" + deleteServerity);
		try {

			M_Severity getSeverity = InputMapper.gson().fromJson(deleteServerity, M_Severity.class);

			M_Severity getDAta = M_ServerityInter.getDataByServId(getSeverity.getSeverityID());
			getDAta.setSeverityTypeName(getSeverity.getSeverityTypeName());
			getDAta.setSeverityDesc(getSeverity.getSeverityDesc());

			M_Severity editserverity = M_ServerityInter.deletedataser(getDAta);

			response.setResponse(editserverity.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			logger.error("edit serverity  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get feedback type")
	@RequestMapping(value = "/m/getFeedbackType", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getFeedbackType(@RequestBody String getFeedbackType) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getFeedbackType);
		try {

			M_Feedbacktype getFeedbackt = InputMapper.gson().fromJson(getFeedbackType, M_Feedbacktype.class);
			ArrayList<M_Feedbacktype> getfeedbackdata = m_FeedbacktypeInter
					.getFeedbackt(getFeedbackt.getProviderServiceMapID());

			response.setResponse(getfeedbackdata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("get Feedback  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Save feedback type")
	@RequestMapping(value = "/m/saveFeedbackType", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveFeedbackType(@RequestBody String saveFeedbackType) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + saveFeedbackType);

		try {

			M_Feedbacktype[] saveServeritytype = InputMapper.gson().fromJson(saveFeedbackType, M_Feedbacktype[].class);
			List<M_Feedbacktype> feedbackTypedata = Arrays.asList(saveServeritytype);

			ArrayList<M_Feedbacktype> saveSerdata = m_FeedbacktypeInter.saveFeedbackType(feedbackTypedata);

			response.setResponse(saveSerdata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("save FeedbackType  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Edit feedback type")
	@RequestMapping(value = "/m/editFeedbackType", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editFeedbackType(@RequestBody String editFeedbackType) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + editFeedbackType);
		try {

			M_Feedbacktype getFeedbacktp = InputMapper.gson().fromJson(editFeedbackType, M_Feedbacktype.class);
			M_Feedbacktype getData = m_FeedbacktypeInter.getDataByServId(getFeedbacktp.getFeedbackTypeID());
			getData.setFeedbackTypeName(getFeedbacktp.getFeedbackTypeName());
			getData.setFeedbackDesc(getFeedbacktp.getFeedbackDesc());
			getData.setModifiedBy(getFeedbacktp.getModifiedBy());
			M_Feedbacktype deletedata = m_FeedbacktypeInter.deletedataser(getData);

			response.setResponse(deletedata.toString());

		} catch (Exception e) {
			logger.error("edit Feedback  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete feedback type")
	@RequestMapping(value = "/m/deleteFeedbackType", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteFeedbackType(@RequestBody String deleteFeedbackType) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteFeedbackType);
		try {

			M_Feedbacktype getFeedbacktp = InputMapper.gson().fromJson(deleteFeedbackType, M_Feedbacktype.class);
			M_Feedbacktype getDAta = m_FeedbacktypeInter.getDataByServId(getFeedbacktp.getFeedbackTypeID());
			getDAta.setDeleted(getFeedbacktp.getDeleted());

			M_Feedbacktype deletedata = m_FeedbacktypeInter.deletedataser(getDAta);

			response.setResponse(deletedata.toString());

		} catch (

		Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete Feedback Type  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}
		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Create category")
	@RequestMapping(value = "/m/createCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createCategory(@RequestBody String createCategory) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + createCategory);
		try {

			M_Category[] categoryid = InputMapper.gson().fromJson(createCategory, M_Category[].class);
			List<M_Category> catdata = Arrays.asList(categoryid);
			ArrayList<M_Category> createcat = categoryInter.createcat(catdata);

			response.setResponse(createcat.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("create Category  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete category")
	@RequestMapping(value = "/m/deleteCategory1", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteCategory1(@RequestBody String deleteCategory1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteCategory1);

		try {

			M_Category category = InputMapper.gson().fromJson(deleteCategory1, M_Category.class);

			M_Category deletedData = categoryInter.getcatdatabycatId(category.getCategoryID());

			deletedData.setDeleted(category.getDeleted());
			M_Category deletedata = categoryInter.deletedata(deletedData);

			response.setResponse(deletedata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete Category  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update provider admin")
	@RequestMapping(value = "/updateProviderAdmin", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateProviderAdmin(@RequestBody String updateProviderAdmin) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateProviderAdmin);
		try {
			Set<ServiceProvider_Model> serviceProviderSet = new HashSet<ServiceProvider_Model>();

			ServiceProvider_Model serviceProvider_Model = InputMapper.gson().fromJson(updateProviderAdmin,
					ServiceProvider_Model.class);

			ServiceProviderStateServiceMappingWrapper serviceProviderStateServiceMappingWrapper = InputMapper.gson()
					.fromJson(updateProviderAdmin, ServiceProviderStateServiceMappingWrapper.class);

			ServiceProvider_Model getProviderDetails = serviceProvider_ServiceImpl
					.getProviderData(serviceProvider_Model.getServiceProviderId());

			getProviderDetails.setServiceProviderName(serviceProvider_Model.getServiceProviderName());
			getProviderDetails.setLogoFileName(serviceProvider_Model.getLogoFileName());
			getProviderDetails.setPrimaryContactAddress(serviceProvider_Model.getPrimaryContactAddress());
			getProviderDetails.setPrimaryContactEmailID(serviceProvider_Model.getPrimaryContactEmailID());
			getProviderDetails.setPrimaryContactName(serviceProvider_Model.getPrimaryContactName());
			getProviderDetails.setPrimaryContactNo(serviceProvider_Model.getPrimaryContactNo());
			getProviderDetails
					.setPrimaryContactValidityTillDate(serviceProvider_Model.getPrimaryContactValidityTillDate());
			getProviderDetails.setSecondaryContactAddress(serviceProvider_Model.getSecondaryContactAddress());
			getProviderDetails.setSecondaryContactEmailID(serviceProvider_Model.getSecondaryContactEmailID());
			getProviderDetails.setSecondaryContactName(serviceProvider_Model.getSecondaryContactName());
			getProviderDetails.setSecondaryContactNo(serviceProvider_Model.getSecondaryContactNo());
			getProviderDetails
					.setSecondaryContactValidityTillDate(serviceProvider_Model.getSecondaryContactValidityTillDate());

			ServiceProvider_Model saveProviderDetails = serviceProvider_ServiceImpl
					.upDateProviderDetails(getProviderDetails);

			response.setResponse(saveProviderDetails.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Update Provider Admin  failed with exception " + e.getMessage(), e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();
	}

	
	@Operation(summary = "Create sub category")
	@RequestMapping(value = "/m/createSubCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createSubCategory(@RequestBody String createSubCategory) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + createSubCategory);
		try {

			M_Subcategory[] subcategory = InputMapper.gson().fromJson(createSubCategory, M_Subcategory[].class);
			List<M_Subcategory> Subcatdata = Arrays.asList(subcategory);

			ArrayList<M_Subcategory> saveSubCatData = categoryInter.createSubCategory(Subcatdata);

			response.setResponse(saveSubCatData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("create SubCategory  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("respone" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete sub category")
	@RequestMapping(value = "/m/deleteSubCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteSubCategory(@RequestBody String deleteCategory) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteCategory);
		try {

			M_Category category = InputMapper.gson().fromJson(deleteCategory, M_Category.class);

			M_Subcategory subcategory = InputMapper.gson().fromJson(deleteCategory, M_Subcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(deleteCategory, CategoryTO.class);

			M_Subcategory getsubCategory = categoryInter.getSubCategory(subcategory.getSubCategoryID());

			getsubCategory.setDeleted(subcategory.getDeleted());
			M_Subcategory updateData = categoryInter.updateSubCatData(getsubCategory);

			response.setResponse(getsubCategory.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete Category  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get sub category")
	@RequestMapping(value = { "/m/getSubCategory" }, method = { RequestMethod.POST }, produces = { "application/json" })
	public String getSubCategory(@RequestBody String getSubCategory) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getSubCategory);
		try {

			M_Category category = InputMapper.gson().fromJson(getSubCategory, M_Category.class);

			M_Subcategory subcategory = InputMapper.gson().fromJson(getSubCategory, M_Subcategory.class);

			CategoryTO cto = InputMapper.gson().fromJson(getSubCategory, CategoryTO.class);

			ArrayList<V_Showsubcategory> getsubCatdata = categoryInter.getSubCategory1(subcategory.getSubCategoryID());

			response.setResponse(getsubCatdata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("get SubCategory  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update category")
	@RequestMapping(value = "/m/updateCategory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateCategory(@RequestBody String deleteCategory1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteCategory1);

		try {

			M_Category category = InputMapper.gson().fromJson(deleteCategory1, M_Category.class);

			M_Category deletedData = categoryInter.getcatdatabycatId(category.getCategoryID());

			deletedData.setCategoryName(category.getCategoryName());
			deletedData.setCategoryDesc(category.getCategoryDesc());
			deletedData.setModifiedBy(category.getModifiedBy());
			deletedData.setS104_CS_Type(category.getS104_CS_Type());
			M_Category deletedata = categoryInter.deletedata(deletedData);

			response.setResponse(deletedata.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			logger.error("delete Category  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Create institute directory")
	@RequestMapping(value = "/m/createInstituteDirectory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createInstuteDirectoty(@RequestBody String createInstuteDirectory) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + createInstuteDirectory);
		try {

			M_Institutedirectory[] insdirectory = InputMapper.gson().fromJson(createInstuteDirectory,
					M_Institutedirectory[].class);
			List<M_Institutedirectory> instuteDiractoty = Arrays.asList(insdirectory);

			ArrayList<M_Institutedirectory> instuteDirectory = instuteDirectoryInter
					.createInstuteDirectory(instuteDiractoty);

			response.setResponse(instuteDirectory.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Create Institute Directory  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get institute directory")
	@RequestMapping(value = "/m/getInstituteDirectory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getInstuteDirectory(@RequestBody String getInstuteDirectory) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getInstuteDirectory);

		try {

			M_Institutedirectory insdirectory = InputMapper.gson().fromJson(getInstuteDirectory,
					M_Institutedirectory.class);

			ArrayList<M_Institutedirectory> getinstuteDirectorydata = instuteDirectoryInter
					.getInstuteDirectory(insdirectory.getProviderServiceMapId());

			response.setResponse(getinstuteDirectorydata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("get InstituteDirectory  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Edit institute directory")
	@RequestMapping(value = "/m/editInstituteDirectory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editInstuteDirectory(@RequestBody String editInstuteDirectory) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + editInstuteDirectory);

		try {

			M_Institutedirectory editinsdirectory = InputMapper.gson().fromJson(editInstuteDirectory,
					M_Institutedirectory.class);

			M_Institutedirectory getinstuteDirectorydata = instuteDirectoryInter
					.editInstuteDirectory(editinsdirectory.getInstituteDirectoryID());

			getinstuteDirectorydata.setInstituteDirectoryName(editinsdirectory.getInstituteDirectoryName());
			getinstuteDirectorydata.setInstituteDirectoryDesc(editinsdirectory.getInstituteDirectoryDesc());
			getinstuteDirectorydata.setModifiedBy(editinsdirectory.getModifiedBy());

			M_Institutedirectory data = instuteDirectoryInter.editdata(getinstuteDirectorydata);

			response.setResponse(getinstuteDirectorydata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error(" edit Institute Directory failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete institute directory")
	@RequestMapping(value = "/m/deleteInstituteDirectory", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String deleteInstuteDirectory(@RequestBody String deleteInstuteDirectory) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutedirectory editinsdirectory = InputMapper.gson().fromJson(deleteInstuteDirectory,
					M_Institutedirectory.class);

			M_Institutedirectory getinstuteDirectorydata = instuteDirectoryInter
					.editInstuteDirectory(editinsdirectory.getInstituteDirectoryID());

			getinstuteDirectorydata.setDeleted(editinsdirectory.getDeleted());

			M_Institutedirectory data = instuteDirectoryInter.editdata(getinstuteDirectorydata);

			response.setResponse(getinstuteDirectorydata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get institute type")
	@RequestMapping(value = "/m/getInstituteType", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getInstuteType(@RequestBody String getInstuteType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutiontype instuteTyp = InputMapper.gson().fromJson(getInstuteType, M_Institutiontype.class);
			ArrayList<M_Institutiontype> createinstuteType = m_InstitutiontypeInter
					.getInstuteType(instuteTyp.getProviderServiceMapID());

			response.setResponse(createinstuteType.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Create institute type")
	@RequestMapping(value = "/m/createInstituteType", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String createInstuteType(@RequestBody String createInstuteType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutiontype[] instuteTyp = InputMapper.gson().fromJson(createInstuteType, M_Institutiontype[].class);
			List<M_Institutiontype> instuteType = Arrays.asList(instuteTyp);

			ArrayList<M_Institutiontype> createinstuteType = m_InstitutiontypeInter.createInstuteType(instuteType);

			response.setResponse(createinstuteType.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Edit institute type")
	@RequestMapping(value = "/m/editInstituteType", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editInstuteType(@RequestBody String editInstuteType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutiontype instuteTyp = InputMapper.gson().fromJson(editInstuteType, M_Institutiontype.class);

			M_Institutiontype editInstuteType1 = m_InstitutiontypeInter
					.editInstuteType(instuteTyp.getInstitutionTypeID());

			editInstuteType1.setInstitutionType(instuteTyp.getInstitutionType());
			editInstuteType1.setInstitutionTypeDesc(instuteTyp.getInstitutionTypeDesc());
			editInstuteType1.setModifiedBy(instuteTyp.getModifiedBy());

			M_Institutiontype saveEditData = m_InstitutiontypeInter.saveEditdata(editInstuteType1);

			response.setResponse(saveEditData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Delete institute type")
	@RequestMapping(value = "/m/deleteInstituteType", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String deleteInstuteType(@RequestBody String deleteInstuteType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutiontype instuteTyp = InputMapper.gson().fromJson(deleteInstuteType, M_Institutiontype.class);

			M_Institutiontype editInstuteType1 = m_InstitutiontypeInter
					.editInstuteType(instuteTyp.getInstitutionTypeID());

			editInstuteType1.setDeleted(instuteTyp.getDeleted());

			M_Institutiontype saveEditData = m_InstitutiontypeInter.saveEditdata(editInstuteType1);

			response.setResponse(saveEditData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get feedback nature type")
	@RequestMapping(value = "/m/getFeedbackNatureType", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getFeedbackNatureType(@RequestBody String getFeedbackNatureType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Feedbacknature instuteTyp = InputMapper.gson().fromJson(getFeedbackNatureType, M_Feedbacknature.class);

			ArrayList<M_Feedbacknature> getFeedbackNatureData = m_FeedbacknatureInteger
					.getFeedbackNatureType(instuteTyp.getFeedbackTypeID());

			response.setResponse(getFeedbackNatureData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Create feedback nature type")
	@RequestMapping(value = "/m/createFeedbackNatureType", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String createFeedbackNatureType(@RequestBody String createFeedbackNatureType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Feedbacknature[] instuteTyp = InputMapper.gson().fromJson(createFeedbackNatureType,
					M_Feedbacknature[].class);
			List<M_Feedbacknature> instuteType = Arrays.asList(instuteTyp);

			ArrayList<M_Feedbacknature> getFeedbackNatureData = m_FeedbacknatureInteger
					.createFeedbackNatueType(instuteType);

			response.setResponse(getFeedbackNatureData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Delete feedback nature type")
	@RequestMapping(value = { "/m/deleteFeedbackNatureType" }, method = { RequestMethod.POST }, produces = {
			"application/json" }, headers = "Authorization")
	public String deleteFeedbackNatureType(@RequestBody String deleteFeedbackNatureType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Feedbacknature instuteTyp = InputMapper.gson().fromJson(deleteFeedbackNatureType, M_Feedbacknature.class);

			M_Feedbacknature geteditFeedbackNatureData = m_FeedbacknatureInteger
					.editFeedbackNatureType(instuteTyp.getFeedbackNatureID());

			geteditFeedbackNatureData.setDeleted(instuteTyp.getDeleted());

			M_Feedbacknature saveEditedData = m_FeedbacknatureInteger.saveEditedData(geteditFeedbackNatureData);

			response.setResponse(saveEditedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Edit feedback nature type")
	@RequestMapping(value = { "/m/editFeedbackNatureType" }, method = { RequestMethod.POST }, produces = {
			"application/json" }, headers = "Authorization")
	public String editFeedbackNatureType(@RequestBody String editFeedbackNatureType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Feedbacknature instuteTyp = InputMapper.gson().fromJson(editFeedbackNatureType, M_Feedbacknature.class);
			M_Feedbacknature geteditFeedbackNatureData = m_FeedbacknatureInteger
					.editFeedbackNatureType(instuteTyp.getFeedbackNatureID());

			geteditFeedbackNatureData.setFeedbackNature(instuteTyp.getFeedbackNature());
			geteditFeedbackNatureData.setFeedbackNatureDesc(instuteTyp.getFeedbackNatureDesc());
			geteditFeedbackNatureData.setModifiedBy(instuteTyp.getModifiedBy());

			M_Feedbacknature saveEditedData = m_FeedbacknatureInteger.saveEditedData(geteditFeedbackNatureData);

			response.setResponse(saveEditedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get institution")
	@RequestMapping(value = { "/m/getInstution"

	}, method = { RequestMethod.POST }, produces = { "application/json" }, headers = "Authorization")

	public String getInstution(@RequestBody String getInstution) {

		OutputResponse response = new OutputResponse();

		try {

			M_Institution insdirectory = InputMapper.gson().fromJson(getInstution, M_Institution.class);
			ArrayList<M_Institution> getInstutionData = m_InstitutionInter.getInstution(
					insdirectory.getProviderServiceMapID(), insdirectory.getStateID(), insdirectory.getDistrictID(),
					insdirectory.getBlockID());

			response.setResponse(getInstutionData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Create institution")
	@RequestMapping(value = "/m/createInstution", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String createInstution(@RequestBody String createInstution) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institution[] insdirectory = InputMapper.gson().fromJson(createInstution, M_Institution[].class);
			List<M_Institution> createinstute = Arrays.asList(insdirectory);

			ArrayList<M_Institution> createInstutionData = m_InstitutionInter.createInstution(createinstute);

			response.setResponse(createInstutionData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Edit institution")
	@RequestMapping(value = "/m/editInstution", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String editInstution(@RequestBody String editInstution) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institution insdirectory = InputMapper.gson().fromJson(editInstution, M_Institution.class);
			M_Institution createInstutionData = m_InstitutionInter.editInstution(insdirectory.getInstitutionID());
			createInstutionData.setInstitutionName(insdirectory.getInstitutionName());
			createInstutionData.setAddress(insdirectory.getAddress());
			createInstutionData.setContactNo1(insdirectory.getContactNo1());
			createInstutionData.setContactNo2(insdirectory.getContactNo2());
			createInstutionData.setContactNo3(insdirectory.getContactNo3());
			createInstutionData.setContactPerson1(insdirectory.getContactPerson1());
			createInstutionData.setContactPerson2(insdirectory.getContactPerson2());
			createInstutionData.setContactPerson3(insdirectory.getContactPerson3());
			createInstutionData.setContactPerson1_Email(insdirectory.getContactPerson1_Email());
			createInstutionData.setContactPerson2_Email(insdirectory.getContactPerson2_Email());
			createInstutionData.setContactPerson3_Email(insdirectory.getContactPerson3_Email());
			createInstutionData.setWebsite(insdirectory.getWebsite());
			M_Institution saveeditData = m_InstitutionInter.saveEditData(createInstutionData);
			response.setResponse(createInstutionData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Delete institution")
	@RequestMapping(value = "/m/deleteInstution", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteInstution(@RequestBody String deleteInstution) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institution insdirectory = InputMapper.gson().fromJson(deleteInstution, M_Institution.class);
			M_Institution createInstutionData = m_InstitutionInter.editInstution(insdirectory.getInstitutionID());
			createInstutionData.setDeleted(insdirectory.getDeleted());
			M_Institution saveeditData = m_InstitutionInter.saveEditData(createInstutionData);
			response.setResponse(createInstutionData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();
	}

	
	@Operation(summary = "Get institute sub directory")
	@RequestMapping(value = "/m/getInstutesubDirectory", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String getInstuteSubDirectory(@RequestBody String getInstuteSubDirectory) {
		OutputResponse response = new OutputResponse();

		try {
			M_Institutesubdirectory inssubdirectory = InputMapper.gson().fromJson(getInstuteSubDirectory,
					M_Institutesubdirectory.class);
			ArrayList<M_Institutesubdirectory> getinstutesubDirectorydata = m_InstitutesubdirectoryInter
					.getInstutesubDirectory(inssubdirectory.getInstituteDirectoryID(),
							inssubdirectory.getProviderServiceMapId());
			response.setResponse(getinstutesubDirectorydata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	
	@Operation(summary = "Create institute sub directory")
	@RequestMapping(value = "/m/createInstutesubDirectory", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String createInstuteSubDirectory(@RequestBody String createInstuteSubDirectory) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutesubdirectory[] inssubdirectory = InputMapper.gson().fromJson(createInstuteSubDirectory,
					M_Institutesubdirectory[].class);
			List<M_Institutesubdirectory> instuteSubDiractoty = Arrays.asList(inssubdirectory);
			ArrayList<M_Institutesubdirectory> createinstutesubDirectorydata = m_InstitutesubdirectoryInter
					.CreateInstutesubDirectory(instuteSubDiractoty);
			response.setResponse(createinstutesubDirectorydata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	
	@Operation(summary = "Edit institute sub directory")
	@RequestMapping(value = "/m/editInstutesubDirectory", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String editInstuteSubDirectory(@RequestBody String editInstuteSubDirectory) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutesubdirectory inssubdirectory = InputMapper.gson().fromJson(editInstuteSubDirectory,
					M_Institutesubdirectory.class);
			M_Institutesubdirectory getinstutesubDirectoryediteddata = m_InstitutesubdirectoryInter
					.editInstutesubDirectory(inssubdirectory.getInstituteSubDirectoryID());
			getinstutesubDirectoryediteddata
					.setInstituteSubDirectoryName(inssubdirectory.getInstituteSubDirectoryName());
			getinstutesubDirectoryediteddata
					.setInstituteSubDirectoryDesc(inssubdirectory.getInstituteSubDirectoryDesc());
			getinstutesubDirectoryediteddata.setModifiedBy(inssubdirectory.getModifiedBy());
			M_Institutesubdirectory saveEditedData = m_InstitutesubdirectoryInter
					.saveEditedData(getinstutesubDirectoryediteddata);
			response.setResponse(saveEditedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	
	@Operation(summary = "Delete institute sub directory")
	@RequestMapping(value = "/m/deleteInstutesubDirectory", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteInstuteSubDirectory(@RequestBody String deleteInstuteSubDirectory) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutesubdirectory inssubdirectory = InputMapper.gson().fromJson(deleteInstuteSubDirectory,
					M_Institutesubdirectory.class);
			M_Institutesubdirectory getinstutesubDirectoryediteddata = m_InstitutesubdirectoryInter
					.editInstutesubDirectory(inssubdirectory.getInstituteSubDirectoryID());
			getinstutesubDirectoryediteddata.setDeleted(inssubdirectory.getDeleted());
			M_Institutesubdirectory saveEditedData = m_InstitutesubdirectoryInter
					.saveEditedData(getinstutesubDirectoryediteddata);
			response.setResponse(saveEditedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	
	@Operation(summary = "Create institute sub directory mapping")
	@RequestMapping(value = "/m/createInstutesubDirectoryMapping", headers = "Authorization", method = {
			RequestMethod.POST

	}, produces = { "application/json" })

	public String createInstuteSubDirectoryMaping(@RequestBody String createInstuteSubDirectoryMapping) {
		OutputResponse response = new OutputResponse();

		try {
			M_Institutedirectorymapping[] inssubdirectory = InputMapper.gson()
					.fromJson(createInstuteSubDirectoryMapping, M_Institutedirectorymapping[].class);
			List<M_Institutedirectorymapping> instuteDiractoty = Arrays.asList(inssubdirectory);
			ArrayList<M_Institutedirectorymapping> createinstutesubDirectorydata = m_InstitutedirectorymappingInter
					.createInstituteDirectoryData(instuteDiractoty);
			response.setResponse(createinstutesubDirectorydata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	
	@Operation(summary = "Delete institute sub directory mapping")
	@RequestMapping(value = "/m/deleteInstutesubDirectoryMapping", headers = "Authorization", method = {
			RequestMethod.POST

	}, produces = { "application/json" })

	public String deleteInstuteSubDirectoryMaping(@RequestBody String deleteInstuteSubDirectoryMapping) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutedirectorymapping inssubdirectory = InputMapper.gson().fromJson(deleteInstuteSubDirectoryMapping,
					M_Institutedirectorymapping.class);
			M_Institutedirectorymapping deleteinstutesubDirectorydata = m_InstitutedirectorymappingInter
					.deleteInstituteDirectoryData(inssubdirectory.getInstituteDirMapID());
			deleteinstutesubDirectorydata.setDeleted(inssubdirectory.getDeleted());
			M_Institutedirectorymapping deletedata = m_InstitutedirectorymappingInter
					.setdeletedData(deleteinstutesubDirectorydata);
			response.setResponse(deleteinstutesubDirectorydata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	
	@Operation(summary = "Get institute sub directory mapping")
	@RequestMapping(value = "/m/getInstutesubDirectoryMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getInstuteSubDirectoryMaping(@RequestBody String getInstuteSubDirectoryMapping) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutedirectorymapping inssubdirectory = InputMapper.gson().fromJson(getInstuteSubDirectoryMapping,
					M_Institutedirectorymapping.class);
			ArrayList<M_Institutedirectorymapping> getinstutesubDirectorydata = m_InstitutedirectorymappingInter
					.getInstituteDirectoryData(inssubdirectory.getInstituteSubDirectoryID());
			response.setResponse(getinstutesubDirectorydata.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	
	@Operation(summary = "Create provider")
	@RequestMapping(value = "/createProvider", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })

	public String createProvider(@RequestBody String createProvider) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + createProvider);
		try {
			ServiceProvider_Model[] serviceProvider_Model = InputMapper.gson().fromJson(createProvider,
					ServiceProvider_Model[].class);
			List<ServiceProvider_Model> createServiceProvider = Arrays.asList(serviceProvider_Model);
			if (createServiceProvider.isEmpty())
				throw new DataNotFound("Please Enter valid data");
			ArrayList<ServiceProvider_Model> providerName = this.serviceProvider_ServiceImpl
					.createProvider1(createServiceProvider);
			response.setResponse(providerName.toString());

		} catch (Exception e) {
			logger.error("getting AllProvider  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();
	}

	
	@Operation(summary = "Provider update")
	@RequestMapping(value = "/providerUpdate", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })

	public String providerUpdate(@RequestBody String providerupdate) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + providerupdate);
		try {

			ServiceProvider_Model serviceProvider_Model = InputMapper.gson().fromJson(providerupdate,
					ServiceProvider_Model.class);
			ServiceProvider_Model getProviderDetails = this.serviceProvider_ServiceImpl
					.getProviderData(serviceProvider_Model.getServiceProviderId());
			getProviderDetails.setServiceProviderName(serviceProvider_Model.getServiceProviderName());
			getProviderDetails.setLogoFileName(serviceProvider_Model.getLogoFileName());
			getProviderDetails.setPrimaryContactAddress(serviceProvider_Model.getPrimaryContactAddress());
			getProviderDetails.setPrimaryContactEmailID(serviceProvider_Model.getPrimaryContactEmailID());
			getProviderDetails.setPrimaryContactName(serviceProvider_Model.getPrimaryContactName());
			getProviderDetails.setPrimaryContactNo(serviceProvider_Model.getPrimaryContactNo());
			getProviderDetails
					.setPrimaryContactValidityTillDate(serviceProvider_Model.getPrimaryContactValidityTillDate());
			getProviderDetails.setSecondaryContactAddress(serviceProvider_Model.getSecondaryContactAddress());
			getProviderDetails.setSecondaryContactEmailID(serviceProvider_Model.getSecondaryContactEmailID());
			getProviderDetails.setSecondaryContactName(serviceProvider_Model.getSecondaryContactName());
			getProviderDetails.setSecondaryContactNo(serviceProvider_Model.getSecondaryContactNo());
			getProviderDetails
					.setSecondaryContactValidityTillDate(serviceProvider_Model.getSecondaryContactValidityTillDate());
			getProviderDetails.setValidTill(serviceProvider_Model.getValidTill());
			getProviderDetails.setModifiedBy(serviceProvider_Model.getModifiedBy());

			ServiceProvider_Model saveProviderDetails = serviceProvider_ServiceImpl
					.upDateProviderDetails(getProviderDetails);

			response.setResponse(saveProviderDetails.toString());

		} catch (Exception e) {
			logger.error("getting AllProvider  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Provider delete")
	@RequestMapping(value = "/providerdelete", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String providerDelete(@RequestBody String providerDelete) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + providerDelete);
		try {

			ServiceProvider_Model serviceProvider_Model = InputMapper.gson().fromJson(providerDelete,
					ServiceProvider_Model.class);
			ServiceProvider_Model getProviderDetails = this.serviceProvider_ServiceImpl
					.getProviderData(serviceProvider_Model.getServiceProviderId());
			getProviderDetails.setDeleted(serviceProvider_Model.getDeleted());
			ServiceProvider_Model saveProviderDetails = serviceProvider_ServiceImpl
					.upDateProviderDetails(getProviderDetails);
			response.setResponse(saveProviderDetails.toString());

		} catch (Exception e) {
			logger.error("deleteProvider  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Mapping provider admin to provider")
	@RequestMapping(value = "/mappingProviderAdmintoProvider", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String mappingProviderAdmintoProvider(@RequestBody String mappingProviderAdmintoProvider) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + mappingProviderAdmintoProvider);
		try {
			M_UserservicerolemappingForRole[] serviceProvider_Model = InputMapper.gson()
					.fromJson(mappingProviderAdmintoProvider, M_UserservicerolemappingForRole[].class);
			List<M_UserservicerolemappingForRole> createServiceProvider = Arrays.asList(serviceProvider_Model);
			BlockingTO[] blockingto1 = InputMapper.gson().fromJson(mappingProviderAdmintoProvider, BlockingTO[].class);
			List<BlockingTO> blockingto2 = Arrays.asList(blockingto1);
			M_UserservicerolemappingForRole resDataMap = null;
			List<M_UserservicerolemappingForRole> resList = new ArrayList<M_UserservicerolemappingForRole>();
			int x = 0;
			Integer[] ProviderServiceMapID = null;
			for (BlockingTO blockingto : blockingto2) {
				ProviderServiceMapID = blockingto.getServiceProviderMapID1();
				M_UserservicerolemappingForRole providerDetails = createServiceProvider.get(x);
				for (int i = 0; i < ProviderServiceMapID.length; i++) {
					resDataMap = new M_UserservicerolemappingForRole();
					resDataMap.setUserID(providerDetails.getUserID());
					resDataMap.setRoleID(11);
					resDataMap.setProviderServiceMapID(ProviderServiceMapID[i]);
					resDataMap.setCreatedBy(providerDetails.getCreatedBy());
					resList.add(resDataMap);
				}
				x++;
			}

			ArrayList<M_UserservicerolemappingForRole> Mappeddata = serviceProvider_ServiceImpl.AddUserRole(resList);
			response.setResponse(Mappeddata.toString());

		} catch (Exception e) {
			logger.error("getting AllProvider  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Edit mapping provider admin to provider")
	@RequestMapping(value = "/editmappingProviderAdmintoProvider", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editMappingProviderAdmintoProvider(@RequestBody String editMappingProviderAdmintoProvider) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + editMappingProviderAdmintoProvider);
		try {

			M_UserservicerolemappingForRole serviceProvider_Model = InputMapper.gson()
					.fromJson(editMappingProviderAdmintoProvider, M_UserservicerolemappingForRole.class);
			M_UserservicerolemappingForRole getdataforedit = serviceProvider_ServiceImpl
					.getPADataForEdit(serviceProvider_Model.getuSRMappingID());
			getdataforedit.setProviderServiceMapID(serviceProvider_Model.getProviderServiceMapID());
			getdataforedit.setModifiedBy(serviceProvider_Model.getModifiedBy());
			M_UserservicerolemappingForRole saveEditedData = serviceProvider_ServiceImpl
					.insertEditedData(getdataforedit);
			response.setResponse(saveEditedData.toString());

		} catch (Exception e) {
			logger.error("getting AllProvider  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Delete mapping provider admin to provider")
	@RequestMapping(value = "/deletemappingProviderAdmintoProvider", headers = "Authorization", method = {
			RequestMethod.POST

	}, produces = { "application/json" })

	public String deleteMappingProviderAdmintoProvider(@RequestBody String deleteMappingProviderAdmintoProvider) {

		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteMappingProviderAdmintoProvider);
		try {
			M_UserservicerolemappingForRole serviceProvider_Model = InputMapper.gson()
					.fromJson(deleteMappingProviderAdmintoProvider, M_UserservicerolemappingForRole.class);
			M_UserservicerolemappingForRole getdataforedit = serviceProvider_ServiceImpl
					.getPADataForEdit(serviceProvider_Model.getuSRMappingID());
			getdataforedit.setDeleted(serviceProvider_Model.getDeleted());
			M_UserservicerolemappingForRole saveEditedData = serviceProvider_ServiceImpl
					.insertEditedData(getdataforedit);
			response.setResponse(saveEditedData.toString());

		} catch (Exception e) {
			logger.error("getting AllProvider  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get mapping provider admin to provider")
	@RequestMapping(value = "/getmappingProviderAdmintoProvider", headers = "Authorization", method = {
			RequestMethod.POST

	}, produces = { "application/json" })

	public String getMappingProviderAdmintoProvider(@RequestBody String getMappingProviderAdmintoProvider) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getMappingProviderAdmintoProvider);
		try {

			V_Showprovideradmin serviceProvider_Model = InputMapper.gson().fromJson(getMappingProviderAdmintoProvider,
					V_Showprovideradmin.class);

			ArrayList<V_Showprovideradmin> getAdminData = serviceProvider_ServiceImpl.getProviderAdmins();

			response.setResponse(getAdminData.toString());

		} catch (Exception e) {
			logger.error("getting AllProvider  failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Map category to feedback nature")
	@RequestMapping(value = "/m/mapCategorytoFeedbackNature", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String mapCategorytoFeedbackNature(@RequestBody String mapCategorytoFeedbackNature) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + mapCategorytoFeedbackNature);
		try {

			M_Category[] categoryid = InputMapper.gson().fromJson(mapCategorytoFeedbackNature, M_Category[].class);
			List<M_Category> catdata = Arrays.asList(categoryid);

			int x = 0;
			for (M_Category Cat : catdata) {

				Integer catid = catdata.get(x).getCategoryID();
				Integer feedbackid = catdata.get(x).getFeedbackNatureID();
				int data = categoryInter.updateCategory(catid, feedbackid);
				x++;
			}

			response.setResponse("inserted");

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("create Category  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Update category to feedback nature")
	@RequestMapping(value = "/m/updateCategorytoFeedbackNature", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateCategorytoFeedbackNature(@RequestBody String updateCategorytoFeedbackNature) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateCategorytoFeedbackNature);

		try {

			M_Category category = InputMapper.gson().fromJson(updateCategorytoFeedbackNature, M_Category.class);

			M_Category deletedDataold = categoryInter.getcatdatabycatId(category.getOldCategoryID());
			deletedDataold.setFeedbackNatureID(null);
			deletedDataold.setModifiedBy(category.getModifiedBy());
			categoryInter.deletedata(deletedDataold);

			M_Category deletedData = categoryInter.getcatdatabycatId(category.getCategoryID());

			deletedData.setFeedbackNatureID(category.getFeedbackNatureID());
			deletedData.setModifiedBy(category.getModifiedBy());
			M_Category deletedata = categoryInter.deletedata(deletedData);

			response.setResponse(deletedata.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			logger.error("delete Category  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get mapped category to feedback nature with category id and feedback nature id")
	@RequestMapping(value = "/m/getmapedCategorytoFeedbackNatureWithFeedbackNatureID", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getmapedCategorytoFeedbackNatureWithCatIDandFeedbackNatureID(
			@RequestBody String getmapedCategorytoFeedbackNature) {
		OutputResponse response = new OutputResponse();
		logger.debug("resqust" + getmapedCategorytoFeedbackNature);
		try {

			M_Category categoryid = InputMapper.gson().fromJson(getmapedCategorytoFeedbackNature, M_Category.class);

			ArrayList<M_Category> getsubCategory = categoryInter.getAllCategorywithFeedbackNatureID(
					categoryid.getProviderServiceMapID(), categoryid.getFeedbackNatureID());

			response.setResponse(getsubCategory.toString());

		} catch (Exception e) {
			logger.error("getCategory failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get unmapped category for feedback nature")
	@RequestMapping(value = "/m/getunmappedCategoryforFeedbackNature", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getunmappedCategoryforFeedbackNature(@RequestBody String getmapedCategorytoFeedbackNature) {
		OutputResponse response = new OutputResponse();
		logger.debug("resqust" + getmapedCategorytoFeedbackNature);
		try {

			M_Category categoryid = InputMapper.gson().fromJson(getmapedCategorytoFeedbackNature, M_Category.class);

			ArrayList<M_Category> getsubCategory = categoryInter
					.getUpmappedCategory(categoryid.getProviderServiceMapID());

			response.setResponse(getsubCategory.toString());

		} catch (Exception e) {
			logger.error("getCategory failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Unmap category for feedback nature")
	@RequestMapping(value = "/t/unmappCategoryforFeedbackNature", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String unmappCategoryforFeedbackNature(@RequestBody String getmapedCategorytoFeedbackNature) {
		OutputResponse response = new OutputResponse();
		logger.debug("resqust" + getmapedCategorytoFeedbackNature);
		try {

			M_Category category = InputMapper.gson().fromJson(getmapedCategorytoFeedbackNature, M_Category.class);

			M_Category deletedData = categoryInter.getcatdatabycatId(category.getCategoryID());

			deletedData.setFeedbackNatureID(null);
			deletedData.setModifiedBy(category.getModifiedBy());
			M_Category deletedata = categoryInter.deletedata(deletedData);

			response.setResponse(deletedata.toString());

		} catch (Exception e) {
			logger.error("getCategory failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Get category by map id")
	@RequestMapping(value = "/m/getCategoryByMapID", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllCategoryPsmMapid(@RequestBody String getmapedCategorytoFeedbackNature) {
		OutputResponse response = new OutputResponse();
		logger.debug("resqust" + getmapedCategorytoFeedbackNature);
		try {

			M_Category categoryid = InputMapper.gson().fromJson(getmapedCategorytoFeedbackNature, M_Category.class);

			ArrayList<M_Category> getsubCategory = categoryInter.getAllCategory1(categoryid.getProviderServiceMapID());

			response.setResponse(getsubCategory.toString());

		} catch (Exception e) {
			logger.error("getCategory failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	
	@Operation(summary = "Create institute type by district")
	@RequestMapping(value = "/m/createInstituteTypeByDist", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String createInstuteTypeByDist(@RequestBody String createInstuteType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutiontype[] instuteTyp = InputMapper.gson().fromJson(createInstuteType, M_Institutiontype[].class);
			List<M_Institutiontype> instuteType = Arrays.asList(instuteTyp);

			ArrayList<M_Institutiontype> createinstuteType = m_InstitutiontypeInter.createInstuteType(instuteType);

			response.setResponse(createinstuteType.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Create institution by village")
	@RequestMapping(value = "/m/createInstutionByVillage", headers = "Authorization", method = { RequestMethod.POST

	}, produces = { "application/json" })

	public String createInstutionByVillage(@RequestBody String createInstution) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institution[] insdirectory = InputMapper.gson().fromJson(createInstution, M_Institution[].class);
			List<M_Institution> createinstute = Arrays.asList(insdirectory);

			ArrayList<M_Institution> createInstutionData = m_InstitutionInter.createInstutionByVillage(createinstute);

			response.setResponse(createInstutionData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get institution by village")
	@RequestMapping(value = { "/m/getInstutionByVillage"

	}, method = { RequestMethod.POST }, produces = { "application/json" }, headers = "Authorization")

	public String getInstutionByVillage(@RequestBody String getInstution) {

		OutputResponse response = new OutputResponse();

		try {

			M_Institution insdirectory = InputMapper.gson().fromJson(getInstution, M_Institution.class);
			ArrayList<M_Institution> getInstutionData = m_InstitutionInter.getInstutionByVillage(
					insdirectory.getProviderServiceMapID(), insdirectory.getStateID(), insdirectory.getDistrictID(),
					insdirectory.getBlockID(), insdirectory.getVillageID());

			response.setResponse(getInstutionData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Get institute type by district")
	@RequestMapping(value = "/m/getInstituteTypeByDist", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getInstituteTypeByDist(@RequestBody String getInstuteType) {
		OutputResponse response = new OutputResponse();

		try {

			M_Institutiontype instuteTyp = InputMapper.gson().fromJson(getInstuteType, M_Institutiontype.class);
			ArrayList<M_Institutiontype> createinstuteType = m_InstitutiontypeInter.getInstuteTypeByDist(
					instuteTyp.getProviderServiceMapID(), instuteTyp.getDistrictId(), instuteTyp.getSubDistrictId(),
					instuteTyp.getVillageId());

			response.setResponse(createinstuteType.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	
	@Operation(summary = "Create institution by file")
	@RequestMapping(value = "m/createInstitutionByFile", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createInstitutionByFile(@RequestBody String getInstitutionDetails) {
		OutputResponse response = new OutputResponse();

		try {

			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(getInstitutionDetails);
			jsnOBJ = jsnElmnt.getAsJsonObject();

			String finalMsg = m_InstitutionInter.createInstitutionByFile(jsnOBJ);

			response.setResponse(finalMsg);
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}
		return response.toString();

	}
}
