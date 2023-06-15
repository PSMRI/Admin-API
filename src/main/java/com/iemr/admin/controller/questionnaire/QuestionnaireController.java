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
package com.iemr.admin.controller.questionnaire;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.service.questionnaire.QuestionnaireServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.annotations.ApiParam;

@RestController
public class QuestionnaireController {

	@Autowired
	private QuestionnaireServiceImpl questionnaireService;

	@CrossOrigin()
	@RequestMapping(value = "/saveQuestionnaire", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String saveQuestionnaire(@RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			if (request != null) {
				String res = questionnaireService.SaveQuestionnaire(request);
				if (res != null)
					response.setResponse(res);
				else
					response.setError(5000, "error in saving Questionnaire data");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/getQuestionnaireList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getQuestionnaireList(
			@ApiParam("{\"providerServiceMapID\":\"Integer\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			String s = questionnaireService.getQuestionnaireList(request);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "error occured while fetching questions.........");
		} catch (Exception e) {
			response.setError(5000, e.toString());
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/deleteQuestionnaire", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteQuestionnaire(@RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			String res = questionnaireService.deleteQuestionnaire(request);
			if (res != null)
				response.setResponse(res);
			else
				response.setError(5000, "error occured while deleting question.........");
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/editQuestionnaire", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editQuestionnaire(@RequestBody String request) {
		OutputResponse response = new OutputResponse();
		try {
			String res = questionnaireService.editQuestionnaire(request);
			if (res != null)
				response.setResponse(res);
			else
				response.setError(5000, "error occured while editing question.........");
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

}
