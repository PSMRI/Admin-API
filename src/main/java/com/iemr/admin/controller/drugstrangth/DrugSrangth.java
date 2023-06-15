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
package com.iemr.admin.controller.drugstrangth;

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

import com.iemr.admin.data.drugstrangth.M_104DrugStrength;
import com.iemr.admin.data.uom.M_Uom;
import com.iemr.admin.service.drugstrangth.DrugStrangthInter;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;
@RestController
public class DrugSrangth {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	@Autowired
	private DrugStrangthInter durgStrangthInter;
	
	
	
	@CrossOrigin()
	@RequestMapping(value =  "/createDrugStrangth" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String createDrugStrangth(@RequestBody String createDrugStrangth) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_104DrugStrength[] UomData = InputMapper.gson().fromJson(createDrugStrangth,
					M_104DrugStrength[].class);
		      List<M_104DrugStrength> saveDrugStrangth = Arrays.asList(UomData);
			
			ArrayList<M_104DrugStrength> saveData=durgStrangthInter.createDrugStrangth(saveDrugStrangth);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(saveData.toString());

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
	@RequestMapping(value =  "/getDrugStrangth" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String getDrugStrangth(@RequestBody String getDrugStrangth) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_104DrugStrength UomData = InputMapper.gson().fromJson(getDrugStrangth,
					M_104DrugStrength.class);
		      //List<M_104DrugStrength> saveDrugStrangth = Arrays.asList(UomData);
			
			ArrayList<M_104DrugStrength> getdata=durgStrangthInter.getDrugStrangth();
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(getdata.toString());

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
	@RequestMapping(value =  "/updateDrugStrangth" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String updateDrugStrangth(@RequestBody String updateDrugStrangth) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_104DrugStrength strangthData = InputMapper.gson().fromJson(updateDrugStrangth,
					M_104DrugStrength.class);
		      //List<M_104DrugStrength> saveDrugStrangth = Arrays.asList(UomData);
			
			M_104DrugStrength getdata=durgStrangthInter.updateDrugStrangth(strangthData.getDrugStrengthID());
			
			getdata.setDrugStrength(strangthData.getDrugStrength());
			getdata.setDrugStrengthDesc(strangthData.getDrugStrengthDesc());
			getdata.setModifiedBy(strangthData.getModifiedBy());
			
			M_104DrugStrength udateData=durgStrangthInter.saveupdatedData(getdata);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(getdata.toString());

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
	@RequestMapping(value =  "/deleteDrugStrangth" ,headers = "Authorization", method = { RequestMethod.POST }, produces = { "application/json" })
	public String deleteDrugStrangth(@RequestBody String deleteDrugStrangth) {
		//JSONObject requestOBJ = new JSONObject(providerBlocking);
		
		OutputResponse response = new OutputResponse();

		try {

			M_104DrugStrength strangthData = InputMapper.gson().fromJson(deleteDrugStrangth,
					M_104DrugStrength.class);
		      //List<M_104DrugStrength> saveDrugStrangth = Arrays.asList(UomData);
			
			M_104DrugStrength getdata=durgStrangthInter.updateDrugStrangth(strangthData.getDrugStrengthID());
			
//			getdata.setDrugStrength(strangthData.getDrugStrength());
//			getdata.setDrugStrengthDesc(strangthData.getDrugStrengthDesc());
//			getdata.setModifiedBy(strangthData.getModifiedBy());
			
			getdata.setDeleted(strangthData.getDeleted());;
			
			M_104DrugStrength udateData=durgStrangthInter.saveupdatedData(getdata);
			
			
			//ArrayList<V_Showproviderservicemapping> getProviderStatus1=blockingInter.getProviderStatus1(Pharmacologicalcategory.getServiceProviderID());
			
			response.setResponse(getdata.toString());

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
