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
import com.iemr.admin.utils.exception.VideoConsultationException;
import com.iemr.admin.utils.http.HttpUtils;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@Service
public class VideoConsultationAPIServiceImpl implements VideoConsultationAPIInter {

	Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static HttpUtils httpUtils = new HttpUtils();
	private String videoConsultationAuth = ConfigProperties.getPropertyByName("videoConsultation-apikey");
	private String videoConsultationBaseUrl = ConfigProperties.getPropertyByName("videoConsultation-base-url");
	private String videoConsultationCreateUser = ConfigProperties.getPropertyByName("videoConsultation-createuser-url");
	private String videoConsultationEditUser = ConfigProperties.getPropertyByName("videoConsultation-edituser-url");
	private InputMapper inputMapper = new InputMapper();
	ObjectMapper mapper = new ObjectMapper();

	int i = 0;

	@Override
	public Long createUser(HashMap<String, String> obj) throws VideoConsultationException {
		JsonParser parser = new JsonParser();

		String result;
		HashMap<String, Object> header = new HashMap<>();
		if (videoConsultationAuth != null) {
			header.put("X-APIkey-Header", videoConsultationAuth);
		}
		header.put("Content-Type", "application/json");
		String input = "{}";

		try {
			input = mapper.writeValueAsString(obj);

		} catch (JsonProcessingException e) {
			logger.error("Error:" + e);

		}

		result = httpUtils.post(videoConsultationCreateUser.replace("videoConsultation-base-url", videoConsultationBaseUrl), input, header);
		OutputResponse identityResponse = inputMapper.gson().fromJson(result, OutputResponse.class);
		logger.info(result);
		JsonObject responseObj = (JsonObject) parser.parse(result);
		Long returnval = responseObj.get("userid").getAsLong();
		if (returnval == null || returnval == 0L) {
			throw new VideoConsultationException(responseObj.get("result").getAsString());
		}
		return returnval;
	}

	@Override
	public Long editUser(HashMap<String, String> obj, Long videoConsultationID, String videoConsultationDomain) throws VideoConsultationException {
		JsonParser parser = new JsonParser();

		String result;
		HashMap<String, Object> header = new HashMap<>();
		if (videoConsultationAuth != null) {
			header.put("X-APIkey-Header", videoConsultationAuth);
		}
		header.put("Content-Type", "application/json");

		StringBuilder url = new StringBuilder();
		url.append(videoConsultationEditUser.replace("videoConsultation-base-url", videoConsultationBaseUrl));
		url.append("/");
		url.append(videoConsultationID.toString());
		url.append("/");
		url.append(videoConsultationDomain);

		String input = "{}";

		try {
			input = mapper.writeValueAsString(obj);

		} catch (JsonProcessingException e) {
			logger.error("Error" + e);

		}

		result = httpUtils.put(url.toString(), input, header);
		OutputResponse identityResponse = inputMapper.gson().fromJson(result, OutputResponse.class);
		logger.info(result);
		JsonObject responseObj = (JsonObject) parser.parse(result);
		Long returnval = responseObj.get("userid").getAsLong();
		if (returnval == null || returnval == 0L) {
			throw new VideoConsultationException(responseObj.get("result").getAsString());
		}
		return returnval;
	}


}
