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
package com.iemr.admin.controller.locationmaster;

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

import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.M_ProviderServiceAddMapping;
import com.iemr.admin.data.locationmaster.ServiceMasterLocation;
import com.iemr.admin.data.locationmaster.Showofficedetails;
import com.iemr.admin.data.locationmaster.StateServiceMapping1;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;
import com.iemr.admin.to.M_UserTO;
import com.iemr.admin.to.locationmaster.LocationTO;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class LocationMasterController {

	@Autowired
	private LocationMasterServiceInter locationMasterServiceInter;
	private Integer serviceMapid;

	private Logger logger = LoggerFactory.getLogger(LocationMasterController.class);

	private InputMapper inputMapper = new InputMapper();

	@CrossOrigin()
	@Operation(summary = "get all role 2")
	@RequestMapping(value = "/m/location/getAlllocation1", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllRole2(@RequestBody String stateserviceMapping1) {

		OutputResponse response = new OutputResponse();

		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping1.class);

			ArrayList<StateServiceMapping1> data1 = locationMasterServiceInter.getAllByMapId2(
					stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID(),
					stateserviceMapping.getServiceID());

			int tempProSerStatMapID = 0;
			if (data1 != null && data1.size() > 0) {

				for (StateServiceMapping1 obj : data1) {
					tempProSerStatMapID = obj.getProviderServiceMapID();
				}
			} else {
			}

			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();
			ArrayList<M_ProviderServiceAddMapping> rolesData = locationMasterServiceInter
					.getlocationByMapid(tempProSerStatMapID);

			logger.info("Hai" + serviceMapid);

			response.setResponse(rolesData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get all location")
	@RequestMapping(value = "/m/location/getAlllocation", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAlllocation(@RequestBody String getAlllocation) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + getAlllocation);
		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(getAlllocation,
					StateServiceMapping1.class);

			ServiceMasterLocation stateserviceMapping1 = InputMapper.gson().fromJson(getAlllocation, ServiceMasterLocation.class);

			/**
			 * sending serviceproviderId & stateid & serviceId and getting the responce
			 * using data...
			 */
			ArrayList<StateServiceMapping1> data1;

			if (stateserviceMapping1.getIsNational() == true) {

				data1 = locationMasterServiceInter.getAllByMapId3(stateserviceMapping.getServiceProviderID(),
						stateserviceMapping.getServiceID());
			} else {

				data1 = locationMasterServiceInter.getAllByMapId2(stateserviceMapping.getServiceProviderID(),
						stateserviceMapping.getStateID(), stateserviceMapping.getServiceID());
			}

			int tempProSerStatMapID = 0;
			if (data1 != null && data1.size() > 0) {

				for (StateServiceMapping1 obj : data1) {
					tempProSerStatMapID = obj.getProviderServiceMapID();
				}
			} else {
			}

			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();
			ArrayList<Showofficedetails> rolesData;
			if (!(stateserviceMapping.getDistrictID() == null)) {
				rolesData = locationMasterServiceInter.getlocationByMapid4(tempProSerStatMapID,
						stateserviceMapping.getDistrictID());
			} else {
				rolesData = locationMasterServiceInter.getlocationByMapid2(tempProSerStatMapID);
			}

			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("getting location failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get all new location")
	@RequestMapping(value = "/m/location/getAlllocationNew", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAlllocationNew(@RequestBody String getAlllocation) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + getAlllocation);
		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(getAlllocation,
					StateServiceMapping1.class);

			ServiceMasterLocation stateserviceMapping1 = InputMapper.gson().fromJson(getAlllocation, ServiceMasterLocation.class);

			ArrayList<Showofficedetails> rolesData;

			if (!(stateserviceMapping.getDistrictID() == null)) {
				rolesData = locationMasterServiceInter.getlocationByMapid4(
						stateserviceMapping.getProviderServiceMapID(), stateserviceMapping.getDistrictID());
			} else
				rolesData = locationMasterServiceInter
						.getlocationByMapid2(stateserviceMapping.getProviderServiceMapID());

			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("getting location failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Search role")
	@RequestMapping(value = "m/location/state", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String searchRole(@RequestBody String stateserviceMapping1) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + stateserviceMapping1);
		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping1.class);

			ArrayList<StateServiceMapping1> data = locationMasterServiceInter
					.getStateByServiceProviderId(stateserviceMapping.getServiceProviderID());

			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("getting state failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get service")
	@RequestMapping(value = "m/location/service", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getService(@RequestBody String stateserviceMapping1) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + stateserviceMapping1);
		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping1.class);

			ArrayList<StateServiceMapping1> data = locationMasterServiceInter.getServiceByServiceProviderIdAndStateId(
					stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID());

			response.setResponse(data.toString());
		} catch (Exception e) {

			logger.error("getting service failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get all district")
	@RequestMapping(value = "m/location/findDistrict", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllDistrict(@RequestBody String mDistrict1) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + mDistrict1);

		try {

			M_District mDistrict = InputMapper.gson().fromJson(mDistrict1, M_District.class);

			ArrayList<M_District> data1 = locationMasterServiceInter.getAllDistrictByStateId(mDistrict.getStateID());

			response.setResponse(data1.toString());

		} catch (Exception e) {

			logger.error("getting district failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get all role")
	@RequestMapping(value = "m/location/addLocation", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllRole(@RequestBody String stateserviceMapping1) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + stateserviceMapping1);

		try {

			M_UserTO m_ProviderServiceAddMapping = InputMapper.gson().fromJson(stateserviceMapping1, M_UserTO.class);

			M_ProviderServiceAddMapping resDataMap = null;
			List<M_ProviderServiceAddMapping> resList = new ArrayList<M_ProviderServiceAddMapping>();

			Integer[] pSMapid = m_ProviderServiceAddMapping.getProviderServiceMapID();

			for (int i = 0; i < pSMapid.length; i++) {

				resDataMap = new M_ProviderServiceAddMapping();
				resDataMap.setProviderServiceMapID(pSMapid[i]);
				resDataMap.setAddress(m_ProviderServiceAddMapping.getAddress());
				resDataMap.setLocationName(m_ProviderServiceAddMapping.getLocationName());
				resDataMap.setDistrictID(m_ProviderServiceAddMapping.getDistrictID());
				resDataMap.setCreatedBy(m_ProviderServiceAddMapping.getCreatedBy());
				resDataMap.setCreatedDate(m_ProviderServiceAddMapping.getCreatedDate());

				resList.add(resDataMap);
			}

			ArrayList<M_ProviderServiceAddMapping> data2 = locationMasterServiceInter.addlocation(resList);

			response.setResponse(data2.toString());

		} catch (Exception e) {

			logger.error("getting addlocation failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get edit location")
	@RequestMapping(value = "m/location/editLocation", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String geteditLocation(@RequestBody String editLocation) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + editLocation);

		try {

			M_ProviderServiceAddMapping editdata2 = InputMapper.gson().fromJson(editLocation,
					M_ProviderServiceAddMapping.class);

			M_ProviderServiceAddMapping editdata = locationMasterServiceInter.editData(editdata2.getpSAddMapID());
			editdata.setProviderServiceMapID(editdata2.getProviderServiceMapID());
			editdata.setDistrictID(editdata2.getDistrictID());
			editdata.setAddress(editdata2.getAddress());
			editdata.setLocationName(editdata2.getLocationName());
			editdata.setDeleted(editdata.getDeleted());

			M_ProviderServiceAddMapping editdata1 = locationMasterServiceInter.saveEditData(editdata);

			response.setResponse(editdata1.toString());

		} catch (Exception e) {

			logger.error("getting edit location failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Delete location")
	@RequestMapping(value = "m/location/deleteLocation", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteLocation(@RequestBody String deletelocation) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + deletelocation);

		try {

			M_ProviderServiceAddMapping deletedata2 = InputMapper.gson().fromJson(deletelocation,
					M_ProviderServiceAddMapping.class);

			M_ProviderServiceAddMapping deletedata = locationMasterServiceInter.editData(deletedata2.getpSAddMapID());

			deletedata.setDeleted(deletedata2.getDeleted());

			M_ProviderServiceAddMapping deleteddata1 = locationMasterServiceInter.saveEditData(deletedata);

			response.setResponse(deleteddata1.toString());

		} catch (Exception e) {
			logger.error("getting delete location failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get location by service id")
	@RequestMapping(value = "/m/location/getLocationByServiceID", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getLocationByServiceID(@RequestBody String getLocationByServiceID) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + getLocationByServiceID);

		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(getLocationByServiceID,
					StateServiceMapping1.class);

			M_ProviderServiceAddMapping getAllLocbyServiceid = InputMapper.gson().fromJson(getLocationByServiceID,
					M_ProviderServiceAddMapping.class);

			ArrayList<StateServiceMapping1> data1 = locationMasterServiceInter.getLocationByServiceId(
					stateserviceMapping.getServiceProviderID(), stateserviceMapping.getServiceID());

			ArrayList<Integer> data = new ArrayList<Integer>();
			int tempProSerStatMapID = 0;
			if (data1 != null && data1.size() > 0) {

				for (StateServiceMapping1 obj : data1) {
					data.add(obj.getProviderServiceMapID());
				}

			} else {
			}

			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();
			ArrayList<Showofficedetails> rolesData = locationMasterServiceInter.getlocationByMapid1(data);

			response.setResponse(rolesData.toString());

		} catch (Exception e) {
			logger.error("getting  location by Service failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get location by state id")
	@RequestMapping(value = "/m/location/getLocationByStateId", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getLocationByStateId(@RequestBody String getLocationByStateId) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + getLocationByStateId);

		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(getLocationByStateId,
					StateServiceMapping1.class);

			M_ProviderServiceAddMapping getAllLocbyServiceid = InputMapper.gson().fromJson(getLocationByStateId,
					M_ProviderServiceAddMapping.class);

			ArrayList<StateServiceMapping1> data1 = locationMasterServiceInter
					.getLocationBySateID(stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID());

			ArrayList<Integer> data = new ArrayList<Integer>();
			int tempProSerStatMapID = 0;
			if (data1 != null && data1.size() > 0) {

				for (StateServiceMapping1 obj : data1) {
					data.add(obj.getProviderServiceMapID());
				}

			} else {
			}

			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();
			ArrayList<Showofficedetails> rolesData = locationMasterServiceInter.getlocationByMapid1(data);

			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("getting  Location By State failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get office name by map id")
	@RequestMapping(value = "/m/location/getOfficeNameByMapId", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getOfficeNameByMapId(@RequestBody String getOfficeNameByMapId) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + getOfficeNameByMapId);

		try {

			LocationTO getproviderserviceMapid = InputMapper.gson().fromJson(getOfficeNameByMapId, LocationTO.class);
			List<LocationTO> feedbackTypedata = Arrays.asList(getproviderserviceMapid);

			Showofficedetails deta = null;

			ArrayList<Showofficedetails> data1 = new ArrayList<Showofficedetails>();
			for (LocationTO l : feedbackTypedata) {
				int x = 0;
				for (int i = 0; i < l.getProviderServiceMapID().size(); i++) {

					deta = new Showofficedetails();
					deta.setProviderServiceMapID(l.getProviderServiceMapID().get(x));
					data1.add(deta);
					x++;

				}

			}

			ArrayList<Showofficedetails> getdatabyMapid = locationMasterServiceInter.getOfficeName(data1);

			response.setResponse(getdatabyMapid.toString());

		} catch (Exception e) {

			logger.error("getting Office Name failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

	@CrossOrigin()
	@Operation(summary = "Get state by service id")
	@RequestMapping(value = "/m/location/getStatesByServiceID", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getStatesByServiceID(@RequestBody String stateserviceMapping1) {

		OutputResponse response = new OutputResponse();
		logger.debug("getting request" + stateserviceMapping1);

		try {

			StateServiceMapping1 stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping1.class);

			ArrayList<StateServiceMapping1> data1 = locationMasterServiceInter.getStatesByServiceId(
					stateserviceMapping.getServiceID(), stateserviceMapping.getServiceProviderID());

			response.setResponse(data1.toString());

		} catch (Exception e) {

			logger.error("getting State By Service failed with exception " + e.getMessage(), e);
			logger.error("Unexpected error:", e);
			response.setError(e);
		}

		logger.debug("responce" + response);
		return response.toString();

	}

}
