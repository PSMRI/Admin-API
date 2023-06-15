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
package com.iemr.admin.controller.Supplier;

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

@RestController
public class SupplierMaster_Controller {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	@Autowired
	private SupplierInter supplierInter;
	
	
	@CrossOrigin()
	@RequestMapping(value =  "/createSupplier" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String createSupplier(@RequestBody String createSupplier) {
		
		OutputResponse response = new OutputResponse();

		try {

			M_Supplier[] Supplier = InputMapper.gson().fromJson(createSupplier,
					M_Supplier[].class);
		     List<M_Supplier> SupplierData = Arrays.asList(Supplier);
		     

			
			ArrayList<M_Supplier> createdData=supplierInter.createSupplier(SupplierData);
			
			
			response.setResponse(createdData.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	
	
	
	
	
	@CrossOrigin()
	@RequestMapping(value =  "/getSupplier" ,headers = "Authorization", method = { RequestMethod.POST }, produces = {"application/json" })
	public String getSupplier(@RequestBody String getSupplier) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_Supplier Supplier = InputMapper.gson().fromJson(getSupplier,
					M_Supplier.class);
		     //List<M_Supplier> SupplierData = Arrays.asList(Supplier);
			
			ArrayList<M_Supplier> getData=supplierInter.getSupplier(Supplier.getProviderServiceMapID());
			
			//editData.setDeleted(Manufacturer.getDeleted());
			
			
			//M_Manufacturer saveData=manufacturerInter.saveEditedData(editData);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(getData.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}
	
	
	           
	
	@CrossOrigin()
	@RequestMapping(value =  "/editSupplier" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String editSupplier(@RequestBody String editSupplier) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_Supplier Supplier = InputMapper.gson().fromJson(editSupplier,
					M_Supplier.class);
		     //List<M_Supplier> SupplierData = Arrays.asList(Supplier);
			
			M_Supplier editData=supplierInter.editSupplier(Supplier.getSupplierID());

			editData.setSupplierDesc(Supplier.getSupplierDesc());
			editData.setContactPerson(Supplier.getContactPerson());
			editData.setDrugLicenseNo(Supplier.getDrugLicenseNo());
			editData.setcST_GST_No(Supplier.getcST_GST_No());
			editData.settIN_No(Supplier.gettIN_No());
			editData.setAddressLine1(Supplier.getAddressLine1());//(Manufacturer.getManufacturerDesc());
			editData.setAddressLine2(Supplier.getAddressLine2());
			editData.setCountryID(Supplier.getCountryID());
			editData.setStateID(Supplier.getStateID());
			editData.setDistrictID(Supplier.getDistrictID());
			editData.setPinCode(Supplier.getPinCode());
			editData.setPhoneNo2(Supplier.getPhoneNo2());
			editData.setPhoneNo1(Supplier.getPhoneNo1());
			editData.setEmail(Supplier.getEmail());

			editData.setModifiedBy(Supplier.getModifiedBy());
			
			M_Supplier editedData=supplierInter.saveEditedData(editData);
			
			
			//M_Manufacturer saveData=manufacturerInter.saveEditedData(editData);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(editedData.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}
	
	
	
	
	
	
	
	@CrossOrigin()
	@RequestMapping(value =  "/deleteSupplier" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String deleteSupplier(@RequestBody String deleteSupplier) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_Supplier Supplier = InputMapper.gson().fromJson(deleteSupplier,
					M_Supplier.class);
		     //List<M_Supplier> SupplierData = Arrays.asList(Supplier);
			
			M_Supplier editData=supplierInter.editSupplier(Supplier.getSupplierID());
			
			editData.setDeleted(Supplier.getDeleted());
			
			M_Supplier deletedData=supplierInter.saveEditedData(editData);
			
			
			//M_Manufacturer saveData=manufacturerInter.saveEditedData(editData);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(deletedData.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}
	
	@CrossOrigin()
	@RequestMapping(value =  "/checkSupplierCode" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String checkSupplierCode(@RequestBody String deleteSupplier) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_Supplier Supplier = InputMapper.gson().fromJson(deleteSupplier,
					M_Supplier.class);
		     //List<M_Supplier> SupplierData = Arrays.asList(Supplier);
			
			Boolean editData=supplierInter.checkSupplierCode(Supplier);
			
			
			//M_Manufacturer saveData=manufacturerInter.saveEditedData(editData);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(editData.toString());

		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}
	
	
	

}
