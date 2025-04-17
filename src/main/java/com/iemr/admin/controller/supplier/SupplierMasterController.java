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
package com.iemr.admin.controller.supplier;

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

import com.iemr.admin.data.supplier.M_Supplier;
import com.iemr.admin.service.supplier.SupplierInter;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class SupplierMasterController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private SupplierInter supplierInter;

	
	@Operation(summary = "Create supplier")
	@RequestMapping(value = "/createSupplier", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createSupplier(@RequestBody String createSupplier) {

		OutputResponse response = new OutputResponse();

		try {
			M_Supplier[] supplier = InputMapper.gson().fromJson(createSupplier, M_Supplier[].class);
			List<M_Supplier> SupplierData = Arrays.asList(supplier);
			ArrayList<M_Supplier> createdData = supplierInter.createSupplier(SupplierData);
			response.setResponse(createdData.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();

	}

	
	@Operation(summary = "Get supplier")
	@RequestMapping(value = "/getSupplier", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getSupplier(@RequestBody String getSupplier) {
		OutputResponse response = new OutputResponse();

		try {
			M_Supplier supplier = InputMapper.gson().fromJson(getSupplier, M_Supplier.class);
			ArrayList<M_Supplier> getData = supplierInter.getSupplier(supplier.getProviderServiceMapID());
			response.setResponse(getData.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

	
	@Operation(summary = "Edit supplier")
	@RequestMapping(value = "/editSupplier", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editSupplier(@RequestBody String editSupplier) {

		OutputResponse response = new OutputResponse();

		try {
			M_Supplier supplier = InputMapper.gson().fromJson(editSupplier, M_Supplier.class);
			M_Supplier editData = supplierInter.editSupplier(supplier.getSupplierID());

			editData.setSupplierDesc(supplier.getSupplierDesc());
			editData.setContactPerson(supplier.getContactPerson());
			editData.setDrugLicenseNo(supplier.getDrugLicenseNo());
			editData.setcST_GST_No(supplier.getcST_GST_No());
			editData.settIN_No(supplier.gettIN_No());
			editData.setAddressLine1(supplier.getAddressLine1());
			editData.setAddressLine2(supplier.getAddressLine2());
			editData.setCountryID(supplier.getCountryID());
			editData.setStateID(supplier.getStateID());
			editData.setDistrictID(supplier.getDistrictID());
			editData.setPinCode(supplier.getPinCode());
			editData.setPhoneNo2(supplier.getPhoneNo2());
			editData.setPhoneNo1(supplier.getPhoneNo1());
			editData.setEmail(supplier.getEmail());
			editData.setModifiedBy(supplier.getModifiedBy());

			M_Supplier editedData = supplierInter.saveEditedData(editData);

			response.setResponse(editedData.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();

	}

	
	@Operation(summary = "Delete supplier")
	@RequestMapping(value = "/deleteSupplier", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String deleteSupplier(@RequestBody String deleteSupplier) {

		OutputResponse response = new OutputResponse();

		try {
			M_Supplier supplier = InputMapper.gson().fromJson(deleteSupplier, M_Supplier.class);
			M_Supplier editData = supplierInter.editSupplier(supplier.getSupplierID());
			editData.setDeleted(supplier.getDeleted());
			M_Supplier deletedData = supplierInter.saveEditedData(editData);
			response.setResponse(deletedData.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();

	}

	
	@Operation(summary = "Check supplier code")
	@RequestMapping(value = "/checkSupplierCode", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String checkSupplierCode(@RequestBody String deleteSupplier) {

		OutputResponse response = new OutputResponse();

		try {
			M_Supplier supplier = InputMapper.gson().fromJson(deleteSupplier, M_Supplier.class);
			Boolean editData = supplierInter.checkSupplierCode(supplier);
			response.setResponse(editData.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
		}
		return response.toString();
	}

}
