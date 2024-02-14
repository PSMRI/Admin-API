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
package com.iemr.admin.controller.stockExit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.stockExit.T_PatientIssue;
import com.iemr.admin.service.stockExit.StockExitService;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class StockExitController {

	@Autowired
	StockExitService stockExitService;

	@CrossOrigin()
	@Operation(summary = "Store service point details")
	@RequestMapping(value = "/patientIssue", headers = "Authorization", method = { RequestMethod.POST })
	public String patientIssue(@RequestBody T_PatientIssue patientIssue) {

		OutputResponse output = new OutputResponse();

		try {

			Integer value = stockExitService.issuePatientDrugs(patientIssue);
			if (value == 1) {
				output.setResponse("Successfully Created");
			} else {
				output.setResponse("Error in Quantity");
			}
		} catch (Exception e) {

			output.setError(e);
		}
		return output.toString();
	}
}
