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
package com.iemr.admin.service.telemedicine;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.admin.utils.config.ConfigProperties;
import com.iemr.admin.utils.exception.SwyMedException;
import com.iemr.admin.utils.http.HttpUtils;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@Service
public class SwymedAPIServiceImpl implements SwymedAPIInter {

	Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static HttpUtils httpUtils = new HttpUtils();
	private String swymedauth = ConfigProperties.getPropertyByName("swymed-apikey");
	private String swymed_base_url = ConfigProperties.getPropertyByName("swymed-base-url");
	private String swymed_createuser = ConfigProperties.getPropertyByName("swymed-createuser-url");
	private String swymed_edituser = ConfigProperties.getPropertyByName("swymed-edituser-url");
	private InputMapper inputMapper = new InputMapper();
	ObjectMapper mapper = new ObjectMapper();

	int i = 0;

	@Override
	public Long createUser(HashMap<String, String> obj) throws SwyMedException {
		// TODO Auto-generated method stub
		JsonParser parser = new JsonParser();

		String result;
		HashMap<String, Object> header = new HashMap<>();
		if (swymedauth != null) {
			header.put("X-APIkey-Header", swymedauth);
		}
		header.put("Content-Type", "application/json");
		String input = "{}";

		try {
			input = mapper.writeValueAsString(obj);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result = httpUtils.post(swymed_createuser.replace("swymed-base-url", swymed_base_url), input, header);
		OutputResponse identityResponse = inputMapper.gson().fromJson(result, OutputResponse.class);
		// if (identityResponse.getStatusCode() ==
		// OutputResponse.USERID_FAILURE)
		// {
		// throw new IEMRException(identityResponse.getErrorMessage());
		// }
		logger.info(result);
		JsonObject responseObj = (JsonObject) parser.parse(result);
		Long returnval = responseObj.get("userid").getAsLong();
		if (returnval == null || returnval == 0L) {
			throw new SwyMedException(responseObj.get("result").getAsString());
		}
		return returnval;
	}

	@Override
	public Long editUser(HashMap<String, String> obj, Long swymedID, String swymedDomain) throws SwyMedException {
		// TODO Auto-generated method stub
		JsonParser parser = new JsonParser();

		String result;
		HashMap<String, Object> header = new HashMap<>();
		if (swymedauth != null) {
			header.put("X-APIkey-Header", swymedauth);
		}
		header.put("Content-Type", "application/json");

		StringBuilder url = new StringBuilder();
		url.append(swymed_edituser.replace("swymed-base-url", swymed_base_url));
		url.append("/");
		url.append(swymedID.toString());
		url.append("/");
		url.append(swymedDomain);

		String input = "{}";

		try {
			input = mapper.writeValueAsString(obj);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result = httpUtils.put(url.toString(), input, header);
		OutputResponse identityResponse = inputMapper.gson().fromJson(result, OutputResponse.class);
		// if (identityResponse.getStatusCode() ==
		// OutputResponse.USERID_FAILURE)
		// {
		// throw new IEMRException(identityResponse.getErrorMessage());
		// }
		logger.info(result);
		JsonObject responseObj = (JsonObject) parser.parse(result);
		Long returnval = responseObj.get("userid").getAsLong();
		if (returnval == null || returnval == 0L) {
			throw new SwyMedException(responseObj.get("result").getAsString());
		}
		return returnval;
	}

//	@Override
//	public Long activateUser(HashMap<String, String> obj) throws SwyMedException {
//		// TODO Auto-generated method stub
//
//		JsonParser parser = new JsonParser();
//
//		String result;
//		HashMap<String, Object> header = new HashMap<>();
//		if (swymedauth != null) {
//			header.put("X-APIkey-Header", swymedauth);
//		}
//		header.put("Content-Type", "application/json");
//
//		result = httpUtils.post(swymed_edituser.replace("swymed-base-url", swymed_base_url), obj.toString(), header);
//		OutputResponse identityResponse = inputMapper.gson().fromJson(result, OutputResponse.class);
//		// if (identityResponse.getStatusCode() ==
//		// OutputResponse.USERID_FAILURE)
//		// {
//		// throw new IEMRException(identityResponse.getErrorMessage());
//		// }
//		logger.info(result);
//		JsonObject responseObj = (JsonObject) parser.parse(result);
//		Long returnval = responseObj.get("userid").getAsLong();
//		if (returnval == null || returnval == 0L) {
//			throw new SwyMedException(responseObj.get("result").getAsString());
//		}
//		return returnval;
//	}

}
