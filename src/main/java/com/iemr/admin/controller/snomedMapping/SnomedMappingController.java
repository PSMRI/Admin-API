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
package com.iemr.admin.controller.snomedMapping;


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
import com.iemr.admin.service.snomedMapping.SnomedService;
import com.iemr.admin.utils.response.OutputResponse;
@RequestMapping(value = "/snomed")
@RestController
public class SnomedMappingController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	@Autowired
	private SnomedService snomedService;
	
	@CrossOrigin()
	@RequestMapping(value = "/editSnomedMappingData", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editSnomedMaster(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		try {

//			M_ProviderServiceAddMapping savedata =  InputMapper.gson().fromJson(requestObj,
//					M_ProviderServiceAddMapping .class);
			
			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsnOBJ = jsnElmnt.getAsJsonObject();
			          
			if (jsnOBJ != null) {
				String masterRes = snomedService.editSnomedMappingData(jsnOBJ,requestObj);
				if (masterRes == "Data Updated" ) {
					response.setResponse("Data Updated successfully");
				} else {
					if (masterRes == "Invalid Master Type" ) 
					{
						response.setError(5000, "Invalid Master Type");
					}
					else
					{
					
					response.setError(5000, "Unable to update data");
					}
				}

			} else {
				
				response.setError(5000, "Invalid request");
			}

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
	@RequestMapping(value = "/saveSnomedMappingData", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveSnomedMaster(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		try {

//			M_ProviderServiceAddMapping savedata =  InputMapper.gson().fromJson(requestObj,
//					M_ProviderServiceAddMapping .class);
			
			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsnOBJ = jsnElmnt.getAsJsonObject();
			          
			if (jsnOBJ != null) {
				String masterRes = snomedService.saveSnomedMappingData(jsnOBJ,requestObj);
				if (masterRes == "Data Saved" ) {
					response.setResponse("Data Saved successfully");
				} else {
					if (masterRes == "Invalid Master Type" ) 
					{
						response.setError(5000, "Invalid Master Type");
					}
					else
					{
					
					response.setError(5000, "Unable to Save data");
					}
				}

			} else {
				
				response.setError(5000, "Invalid request");
			}

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
	@RequestMapping(value = "/fetchSnomedWorklist", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String fetchSnomedWorklist(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		try {

//			M_ProviderServiceAddMapping savedata =  InputMapper.gson().fromJson(requestObj,
//					M_ProviderServiceAddMapping .class);
			
			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsnOBJ = jsnElmnt.getAsJsonObject();
			          
			if (jsnOBJ != null) {
				String res = snomedService.fetchSnomedMaster(jsnOBJ);
				if (res != null)
					response.setResponse(res);
				else
					response.setError(5000, "error in fetching worklist data");

			}
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		return response.toString();
}
	@CrossOrigin()
	@RequestMapping(value = "/updateStatus", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateStatus(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		try {

//			M_ProviderServiceAddMapping savedata =  InputMapper.gson().fromJson(requestObj,
//					M_ProviderServiceAddMapping .class);
			
//			JsonObject jsnOBJ = new JsonObject();
//			JsonParser jsnParser = new JsonParser();
//			JsonElement jsnElmnt = jsnParser.parse(requestObj);
//			jsnOBJ = jsnElmnt.getAsJsonObject();
			          
			if (requestObj != null) {
				String res = snomedService.updateStatus(requestObj);
				if (res != null)
					response.setResponse(res);
				else
					response.setError(5000, "error in updating data");

			}
		} catch (Exception e) {
			
			logger.error("Unexpected error:" , e);
			response.setError(e);

		}
		return response.toString();
}
}