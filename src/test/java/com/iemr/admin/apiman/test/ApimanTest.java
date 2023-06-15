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
package com.iemr.admin.apiman.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.iemr.admin.data.apiman.ApimanClient;
import com.iemr.admin.service.apiman.ApimanService;
import com.iemr.admin.utils.exception.IEMRException;

@RunWith(MockitoJUnitRunner.class)
public class ApimanTest {

	@Mock
	ApimanService apimanService;

	@Test
	public void createClient() {
		// add the behavior of calc service to add two numbers
		ApimanClient apimanClient=new ApimanClient();
		
		apimanClient.setDescription("testing");
		apimanClient.setInitialVersion("1.0");
		apimanClient.setName("Test User");
		try {
			apimanService.createClient( apimanClient);
		} catch (IEMRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// test the add functionality
//		Assert.assertEquals(mathApplication.add(10.0, 20.0), 30.0, 0);
	}
}
