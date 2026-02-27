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
package com.iemr.admin.controller.facilitytype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.store.M_FacilityLevel;
import com.iemr.admin.service.facilitytype.M_facilitytypeInter;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class FacilitytypeController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private M_facilitytypeInter m_facilitytypeInter;

	@Operation(summary = "Get facility")
	@RequestMapping(value = "/getFacility", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getFacility(@RequestBody String getFacility) {

		OutputResponse response = new OutputResponse();

		try {

			M_facilitytype facilityDetails = InputMapper.gson().fromJson(getFacility, M_facilitytype.class);

			ArrayList<M_facilitytype> allFacilityData = m_facilitytypeInter
					.getAllFicilityData(facilityDetails.getProviderServiceMapID());

			response.setResponse(allFacilityData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Add facility")
	@RequestMapping(value = "/addFacility", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String addFacility(@RequestBody String addFacility) {

		OutputResponse response = new OutputResponse();

		try {

			M_facilitytype[] facilityDetails = InputMapper.gson().fromJson(addFacility, M_facilitytype[].class);
			List<M_facilitytype> addfacilityDetails = Arrays.asList(facilityDetails);

			ArrayList<M_facilitytype> allFacilityData = m_facilitytypeInter.addAllFicilityData(addfacilityDetails);

			response.setResponse(allFacilityData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Edit facility")
	@RequestMapping(value = "/editFacility", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editFacility(@RequestBody String editFacility) {

		OutputResponse response = new OutputResponse();

		try {

			M_facilitytype facilityDetails = InputMapper.gson().fromJson(editFacility, M_facilitytype.class);

			M_facilitytype allFacilityData = m_facilitytypeInter
					.editAllFicilityData(facilityDetails.getFacilityTypeID());

			if (facilityDetails.getFacilityTypeName() != null) {
				allFacilityData.setFacilityTypeName(facilityDetails.getFacilityTypeName());
			}
			if (facilityDetails.getRuralUrban() != null) {
				allFacilityData.setRuralUrban(facilityDetails.getRuralUrban());
			}
			if (facilityDetails.getFacilityLevelID() != null) {
				allFacilityData.setFacilityLevelID(facilityDetails.getFacilityLevelID());
			}
			if (facilityDetails.getFacilityTypeDesc() != null) {
				allFacilityData.setFacilityTypeDesc(facilityDetails.getFacilityTypeDesc());
			}
			allFacilityData.setModifiedBy(facilityDetails.getModifiedBy());

			M_facilitytype saveFacilityData = m_facilitytypeInter.updateFacilityData(allFacilityData);

			response.setResponse(saveFacilityData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Delete facility")
	@RequestMapping(value = "/deleteFacility", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String deleteFacility(@RequestBody String deleteFacility) {

		OutputResponse response = new OutputResponse();

		try {

			M_facilitytype facilityDetails = InputMapper.gson().fromJson(deleteFacility, M_facilitytype.class);

			M_facilitytype allFacilityData = m_facilitytypeInter
					.editAllFicilityData(facilityDetails.getFacilityTypeID());
			allFacilityData.setDeleted(facilityDetails.getDeleted());

			M_facilitytype saveFacilityData = m_facilitytypeInter.updateFacilityData(allFacilityData);

			response.setResponse(saveFacilityData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Check facility type code")
	@RequestMapping(value = "/checkFacilityTypeCode", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String checkFacilityTypeCode(@RequestBody String deleteManufacturer) {

		OutputResponse response = new OutputResponse();

		try {

			M_facilitytype Manufacturer = InputMapper.gson().fromJson(deleteManufacturer, M_facilitytype.class);

			Boolean saveData = m_facilitytypeInter.checkFacilityTypeCode(Manufacturer);

			response.setResponse(saveData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Get facility types by rural/urban")
	@RequestMapping(value = "/getFacilityTypesByRuralUrban", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getFacilityTypesByRuralUrban(@RequestBody String request) {

		OutputResponse response = new OutputResponse();

		try {

			M_facilitytype facilityDetails = InputMapper.gson().fromJson(request, M_facilitytype.class);

			ArrayList<M_facilitytype> facilityData = m_facilitytypeInter
					.getFacilityTypesByRuralUrban(facilityDetails.getProviderServiceMapID(),
							facilityDetails.getRuralUrban());

			response.setResponse(facilityData.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	@Operation(summary = "Get all facility levels")
	@RequestMapping(value = "/getFacilityLevels", headers = "Authorization", method = {
			RequestMethod.GET }, produces = { "application/json" })
	public String getFacilityLevels() {

		OutputResponse response = new OutputResponse();
		try {
			ArrayList<M_FacilityLevel> data = m_facilitytypeInter.getFacilityLevels();
			response.setResponse(data.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get facility types by block")
	@RequestMapping(value = "/getFacilityTypesByBlock", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getFacilityTypesByBlock(@RequestBody String request) {

		OutputResponse response = new OutputResponse();
		try {
			M_facilitytype facilityDetails = InputMapper.gson().fromJson(request, M_facilitytype.class);
			ArrayList<M_facilitytype> data = m_facilitytypeInter.getFacilityTypesByBlock(facilityDetails.getBlockID());
			response.setResponse(data.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get facility types by state")
	@RequestMapping(value = "/getFacilityTypesByState", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getFacilityTypesByState(@RequestBody String request) {

		OutputResponse response = new OutputResponse();
		try {
			M_facilitytype facilityDetails = InputMapper.gson().fromJson(request, M_facilitytype.class);
			ArrayList<M_facilitytype> data = m_facilitytypeInter.getFacilityTypesByState(facilityDetails.getStateID());
			response.setResponse(data.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Check if facility type name exists in state")
	@RequestMapping(value = "/checkFacilityTypeName", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String checkFacilityTypeName(@RequestBody String request) {

		OutputResponse response = new OutputResponse();
		try {
			M_facilitytype facilityDetails = InputMapper.gson().fromJson(request, M_facilitytype.class);
			boolean exists = m_facilitytypeInter.checkFacilityTypeNameExists(
					facilityDetails.getFacilityTypeName(), facilityDetails.getStateID());
			response.setResponse(String.valueOf(exists));
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

}
