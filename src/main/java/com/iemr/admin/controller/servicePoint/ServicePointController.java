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
package com.iemr.admin.controller.servicePoint;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.data.servicePoint.M_Servicepoint;
import com.iemr.admin.data.servicePoint.M_Servicepointvillagemap;
import com.iemr.admin.service.servicePoint.ServicePointServiceImpl;
import com.iemr.admin.to.servicePoint.ServicePointTO;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping(value = "/servicePointMaster")
public class ServicePointController {

	@Autowired
	private ServicePointServiceImpl ServicePointServiceImpl;

	@CrossOrigin()
	@Operation(summary = "Store service point details")
	@RequestMapping(value = "/create/servicePoints", headers = "Authorization", method = { RequestMethod.POST })
	public String saveServicePoint(
			@Param(value = "{\"servicePointName\":\"string\", \"servicePointDesc\":\"string\", \"providerServiceMapID\":\"integer\", \"servicePointHQAddress\":\"string\", "
					+ "\"countryID\":\"integer\", \"stateID\":\"integer\", \"districtID\":\"integer\", \"districtBlockID\":\"integer\", \"districtBranchID\":\"integer\", "
					+ " \"createdBy\":\"string\", \"deleted\":\"boolean\"}") @RequestBody String ServicePointMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		ServicePointTO ServicePoint = InputMapper.gson().fromJson(ServicePointMaster, ServicePointTO.class);

		try {

			ArrayList<M_Servicepoint> ServicePoints = ServicePointServiceImpl
					.saveServicePoint(ServicePoint.getServicePoints());
			output.setResponse(ServicePoints.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Get service point details")
	@RequestMapping(value = "/get/servicePoints", headers = "Authorization", method = { RequestMethod.POST })
	public String getServicePoints(
			@Param(value = "{\"stateID\":\"integer\", \"districtID\":\"integer\", \"parkingPlaceID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String ServicePointMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Servicepoint ServicePoint = InputMapper.gson().fromJson(ServicePointMaster, M_Servicepoint.class);

		try {
			ArrayList<M_Servicepoint> ServicePoints = ServicePointServiceImpl.getAvailableServicePoints(
					ServicePoint.getStateID(), ServicePoint.getDistrictID(), ServicePoint.getParkingPlaceID(),
					ServicePoint.getServiceProviderID());
			output.setResponse(ServicePoints.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Remove service point")
	@RequestMapping(value = "/remove/servicePoint", headers = "Authorization", method = { RequestMethod.POST })
	public String deleteServicePoint(
			@Param(value = "{\"servicePointID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String ServicePointMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Servicepoint ServicePoint = InputMapper.gson().fromJson(ServicePointMaster, M_Servicepoint.class);

		try {
			String response;
			int update = ServicePointServiceImpl.updateServicePointStatus(ServicePoint);
			if (update == 1) {
				response = "status updated successfully";
			} else {
				response = "Failed to update the status";
			}
			output.setResponse(response.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Edit service point")
	@RequestMapping(value = "/edit/servicePoint", headers = "Authorization", method = { RequestMethod.POST })
	public String editServicePoint(
			@Param(value = "{\"servicePointID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String ServicePointMaster)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Servicepoint ServicePoint = InputMapper.gson().fromJson(ServicePointMaster, M_Servicepoint.class);

		try {
			String response;
			M_Servicepoint data = ServicePointServiceImpl
					.getdataForEditServicePointStatus(ServicePoint.getServicePointID());
			data.setServicePointName(ServicePoint.getServicePointName());
			data.setServicePointDesc(ServicePoint.getServicePointDesc());
			data.setDistrictID(ServicePoint.getDistrictID());
			data.setDistrictBlockID(ServicePoint.getDistrictBlockID());
			data.setServicePointHQAddress(ServicePoint.getServicePointHQAddress());
			data.setProviderServiceMapID(ServicePoint.getProviderServiceMapID());
			data.setModifiedBy(ServicePoint.getModifiedBy());

			M_Servicepoint data1 = ServicePointServiceImpl.saveeditedData(data);

			output.setResponse(data1.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Store service point details by village map")
	@RequestMapping(value = "/create/servicePointVillageMaps", headers = "Authorization", method = {
			RequestMethod.POST })
	public String saveServicePointVillageMap(
			@Param(value = "{\"ServicePointID\":\"integer\", \"DistrictBranchID\":\"integer\", \"providerServiceMapID\":\"integer\", "
					+ "\"createdBy\":\"string\"}") @RequestBody String servicePointVillageMap)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		ServicePointTO servicePointMaster = InputMapper.gson().fromJson(servicePointVillageMap, ServicePointTO.class);

		try {

			ArrayList<M_Servicepointvillagemap> ServicePoints = ServicePointServiceImpl
					.saveServicePointVillageMap(servicePointMaster.getServicePointVillageMaps());
			output.setResponse(ServicePoints.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Get service point details by village map")
	@RequestMapping(value = "/get/servicePointVillageMaps", headers = "Authorization", method = { RequestMethod.POST })
	public String getServicePointVillageMaps(
			@Param(value = "{\"stateID\":\"integer\", \"districtID\":\"integer\", \"parkingPlaceID\":\"integer\", \"servicePointID\":\"integer\", \"serviceProviderID\":\"integer\"}") @RequestBody String servicePointVillageMap)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Servicepoint servicePointMaster = InputMapper.gson().fromJson(servicePointVillageMap, M_Servicepoint.class);

		try {
			ArrayList<M_Servicepointvillagemap> ServicePoints = ServicePointServiceImpl
					.getAvailableServicePointVillageMaps(servicePointMaster.getStateID(),
							servicePointMaster.getDistrictID(), servicePointMaster.getParkingPlaceID(),
							servicePointMaster.getServicePointID(), servicePointMaster.getServiceProviderID());
			output.setResponse(ServicePoints.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Remove service point by village map")
	@RequestMapping(value = "/remove/servicePointVillageMap", headers = "Authorization", method = {
			RequestMethod.POST })
	public String deleteServicePointVillageMap(
			@Param(value = "{\"servicePointVillageMapID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String servicePointVillageMap)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Servicepointvillagemap m_servicepointvillagemap = InputMapper.gson().fromJson(servicePointVillageMap,
				M_Servicepointvillagemap.class);

		try {
			String response;
			int update = ServicePointServiceImpl.updateServicePointVillageMapStatus(m_servicepointvillagemap);
			if (update == 1) {
				response = "status updated successfully";
			} else {
				response = "Failed to update the status";
			}
			output.setResponse(response.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Edit service point by village map")
	@RequestMapping(value = "/edit/servicePointVillageMap", headers = "Authorization", method = { RequestMethod.POST })
	public String editServicePointVillageMap(
			@Param(value = "{\"servicePointVillageMapID\":\"integer\", \"deleted\":\"boolean\", \"modifiedBy\":\"string\"}") @RequestBody String servicePointVillageMap)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Servicepointvillagemap m_servicepointvillagemap = InputMapper.gson().fromJson(servicePointVillageMap,
				M_Servicepointvillagemap.class);

		try {
			String response;
			M_Servicepointvillagemap getEditedData = ServicePointServiceImpl
					.updateServicePointVillageMapStatus(m_servicepointvillagemap.getServicePointVillageMapID());
			getEditedData.setServicePointID(m_servicepointvillagemap.getServicePointID());
			getEditedData.setDistrictBranchID(m_servicepointvillagemap.getDistrictBranchID());
			getEditedData.setProviderServiceMapID(m_servicepointvillagemap.getProviderServiceMapID());
			getEditedData.setModifiedBy(m_servicepointvillagemap.getModifiedBy());
			M_Servicepointvillagemap savedata = ServicePointServiceImpl.saveEditedData(getEditedData);

			output.setResponse(savedata.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}

	@CrossOrigin()
	@Operation(summary = "Get unmapped villages")
	@RequestMapping(value = "/get/unmappedvillages", headers = "Authorization", method = { RequestMethod.POST })
	public String unmappedvillages(
			@Param(value = "{\"districtBlockID\":\"integer\", \"providerServiceMapID\":\"integer\"}") @RequestBody String servicePointVillageMap)
			throws IEMRException {

		OutputResponse output = new OutputResponse();

		M_Servicepoint servicePointMaster = InputMapper.gson().fromJson(servicePointVillageMap, M_Servicepoint.class);

		try {
			List<DistrictBranchMapping> ServicePoints = ServicePointServiceImpl.getunmappedvillages(
					servicePointMaster.getProviderServiceMapID(), servicePointMaster.getDistrictBlockID());
			output.setResponse(ServicePoints.toString());
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}
}
