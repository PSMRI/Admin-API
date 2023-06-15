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
package com.iemr.admin.service.apiman;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.admin.data.apiman.ApimanClient;
import com.iemr.admin.data.apiman.ApimanContract;
import com.iemr.admin.data.apiman.ApimanRegister;
import com.iemr.admin.utils.config.ConfigProperties;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.http.HttpUtils;
import com.iemr.admin.utils.mapper.InputMapper;

@Service
public class ApimanServiceImpl implements ApimanService {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	private static HttpUtils httpUtils = new HttpUtils();
	private InputMapper inputMapper = new InputMapper();
	private String apimanBaseURL = ConfigProperties.getPropertyByName("apiman-base-url");
	private String clientURL = ConfigProperties.getPropertyByName("apiman-clientcreation-url");
	private String contractURL = ConfigProperties.getPropertyByName("apiman-clientcontract-url");
	private String registerURL = ConfigProperties.getPropertyByName("apiman-register-url");
	private String getClientKey = ConfigProperties.getPropertyByName("apiman-clientkey-url");
	private String auth = ConfigProperties.getPropertyByName("apiman-auth-key");
	private final String APIMAN_URL = "APIMAN_URL";
	private final String CLIENTID = "CLIENT_ID";

	private final String apiman104apiID = ConfigProperties.getPropertyByName("apiman-104api-id");
	private final String apiman1097apiID = ConfigProperties.getPropertyByName("apiman-1097api-id");
	private final String apimanMMUapiID = ConfigProperties.getPropertyByName("apiman-MMUapi-id");
	private final String apimanInventoryapiID = ConfigProperties.getPropertyByName("apiman-Inventoryapi-id");
	private final String apimanCommonApiID = ConfigProperties.getPropertyByName("apiman-CommonApi-id");
	private final String apimanplanID = ConfigProperties.getPropertyByName("apiman-plan-id");
	private final String apimanMCTSapiID = ConfigProperties.getPropertyByName("apiman-MCTSapi-id");
	private final String apimanTMapiID = ConfigProperties.getPropertyByName("apiman-tmapi-id");
	private final String apimanSchedulingapiID = ConfigProperties.getPropertyByName("apiman-schedulingapi-id");
	
	private final String apimanorgID = ConfigProperties.getPropertyByName("apiman-organizationID");
	

	HttpHeaders headers = new HttpHeaders();

	@Override
	public ApimanClient createClient(ApimanClient apimanClient) throws IEMRException {
		// TODO Auto-generated method stub
		JsonParser parser = new JsonParser();

		String result;
		String input = apimanClient.toString();
		HashMap<String, Object> header = new HashMap<>();
		if (auth != null) {
			header.put(headers.AUTHORIZATION, auth);
		}
		header.put(headers.CONTENT_TYPE, "application/json");

		logger.info(clientURL.replace(APIMAN_URL, apimanBaseURL));
		logger.info(input);
		logger.info(header.toString());
		result = httpUtils.post(clientURL.replace(APIMAN_URL, apimanBaseURL), input, header);
		ApimanClient apimanClientresponse = InputMapper.gson().fromJson(result, ApimanClient.class);
		logger.info(apimanClientresponse.toString());

		return apimanClientresponse;
	}

	@Override
	public void createClientContract(Integer servicelineID, String clientID) {
		// TODO Auto-generated method stub
		JsonParser parser = new JsonParser();

		String result;

//		ArrayList<String> apislist = new ArrayList<>();
		ArrayList<ApimanContract> versionlist = new ArrayList<>();
//		apislist.add(apimanCommonApiID);
//		apislist.add(apimanCommonApiID);
		versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apimanCommonApiID, "open"));
		versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apimanCommonApiID, "1.0"));
		  
		 
		  
		 		 
		switch (servicelineID) {
		case 1:
			versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apiman1097apiID, "1.0"));
//			apislist.add(apiman1097apiID);
//			versionlist.add("1.0");
			break;
		case 2:
			versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apimanMMUapiID, "1.0"));
			versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apimanInventoryapiID, "1.0"));
			
//			apislist.add(apimanMMUapiID);
//			apislist.add(apimanInventoryapiID);
////			versionlist.add("1.0");
////			versionlist.add("1.0");
			break;
		case 3:
			versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apiman104apiID, "1.0"));
//			apislist.add(apiman104apiID);
//			versionlist.add("1.0");
			break;
		case 4:
			versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apimanTMapiID, "1.0"));
			versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apimanSchedulingapiID, "1.0"));
			break;
		case 5:
//			apislist.add();
			break;
		case 6:
			versionlist.add(new ApimanContract(apimanplanID, apimanorgID, apimanMCTSapiID, "1.0"));
//			apislist.add(apimanMCTSapiID);
//			versionlist.add("1.0");
			break;

		default:
			break;
		}

//		ApimanContract apimanContract = new ApimanContract();
//		apimanContract.setPlanId(apimanplanID);
//		apimanContract.setApiOrgId("IEMR");
//		
//		apimanContract.setApiVersion("1.0");
//		for (String api : apislist) {
		versionlist.parallelStream().forEach(api->{
//			apimanContract.setApiId(api);
//			apimanContract.setApiVersion(apiVersion);
			String input = api.toString();
			HashMap<String, Object> header = new HashMap<>();
			if (auth != null) {
				header.put(headers.AUTHORIZATION, auth);
			}
			header.put(headers.CONTENT_TYPE, "application/json");

			String url = contractURL.replace(APIMAN_URL, apimanBaseURL).replace(CLIENTID, clientID);
			logger.info(url);
			logger.info(input);
			logger.info(header.toString());

			String result1 = httpUtils.post(url, input, header);
			// ApimanClient apimanClientresponse =
			// InputMapper.gson().fromJson(result, ApimanClient.class);
			logger.info(result1);
		});
		
	}

	@Override
	public String registerClient(ApimanRegister apimanRegister) {
		// TODO Auto-generated method stub
		JsonParser parser = new JsonParser();

		String result;
		String input = apimanRegister.toString();
		HashMap<String, Object> header = new HashMap<>();
		if (auth != null) {
			header.put(headers.AUTHORIZATION, auth);
		}
		header.put(headers.CONTENT_TYPE, "application/json");

		logger.info(registerURL.replace(APIMAN_URL, apimanBaseURL));
		logger.info(input);
		logger.info(header.toString());
		result = httpUtils.post(registerURL.replace(APIMAN_URL, apimanBaseURL), input, header);

		return null;
	}

	@Override
	public String getClientKey(String clientID) {
		// TODO Auto-generated method stub
		JsonParser parser = new JsonParser();

		String result;
		// String input = apimanRegister.toString();
		HashMap<String, Object> header = new HashMap<>();
		if (auth != null) {
			header.put(headers.AUTHORIZATION, auth);
		}
		header.put(headers.CONTENT_TYPE, "application/json");

		logger.info(getClientKey.replace(APIMAN_URL, apimanBaseURL).replace(CLIENTID, clientID));
		// logger.info(input);
		logger.info(header.toString());
		result = httpUtils.get(getClientKey.replace(APIMAN_URL, apimanBaseURL).replace(CLIENTID, clientID), header);
		logger.info(result);
		JsonObject responseObj = parser.parse(result).getAsJsonObject();
		return responseObj.get("apiKey").getAsString();// .getAsJsonObject("apiKey").getAsString();
	}

}
