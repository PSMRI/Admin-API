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
package com.iemr.admin.service.provideronboard;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.user.M_User;
import com.iemr.admin.utils.config.ConfigProperties;
import com.iemr.admin.utils.http.HttpUtils;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@Service
public class EncryptUserPassword123 {
	private static HttpUtils utils = new HttpUtils();
	private static String commonBaseURL = ConfigProperties.getPropertyByName("common-url");
	private static String encryptPasswordURL = commonBaseURL
			+ ConfigProperties.getPropertyByName("encrypt-password-url");

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Async
	public OutputResponse encryptUserCredentials(M_User data) {
		JSONObject request = new JSONObject();
		request.put("password", data.getPassword());
		request.put("userName", data.getUserName());
		logger.info("Calling " + encryptPasswordURL + " to set password for user " + data.getUserName());
		OutputResponse response = InputMapper.gson().fromJson(utils.post(encryptPasswordURL, request.toString()),
				OutputResponse.class);
		logger.info(encryptPasswordURL + " returned response " + response);
		return response;
	}

}
