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
package com.iemr.admin.provideronboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.service.employeemaster.EmployeeMasterServiceImpl;
import com.iemr.admin.utils.mapper.InputMapper;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
public class EmployeeServiceTest {
	
	
	@Autowired
	private static EmployeeMasterServiceImpl employeeMasterServiceImpl;
	
	
	
	@MockBean
	private M_User1 m_User1;
	
	//private static InputMapper inputMapper = new InputMapper();

	private static ArrayList<M_User1> m_User1Model;
	
	M_User1 data;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	ArrayList<M_User1>  muser1=new ArrayList<M_User1>();
	
	ArrayList<M_User1>  muser2=new ArrayList<M_User1>();
	
	@Before
	public void createProvider() {

		try {
			 data=new M_User1();
			data.setUserID(1);
			data.setUserName("rajbaba");
			data.setFirstName("Tripathi");
			data.setMiddleName("Kumar");
			data.setLastName("Rajeev");
			data.setAadhaarNo("112334");
			data.setCreatedBy("rajeev");
			muser1.add(data);
			
			
			
	
	
	
	employeeMasterServiceImpl=mock(EmployeeMasterServiceImpl.class);

	//xyzPositive = inputMapper.gson().fromJson(xyzData, ServiceProvider_Model.class);

	//xyzNegetive = inputMapper.gson().fromJson(xyzData, ServiceProvider_Model.class);
	
	//ServiceProvider_Model[] servModels = inputMapper.gson().fromJson("requestBuilder", ServiceProvider_Model[].class);
	//serviceProvider_ServiceImpl1 = (ArrayList<ServiceProvider_Model>) Arrays.asList(servModels);

	// benNeg.setServiceProviderName("OL");

	when(employeeMasterServiceImpl.getProviderAdmin()).thenReturn(muser1);
	when(employeeMasterServiceImpl.createProviderAdmin(muser1)).thenReturn(muser1);
	when(employeeMasterServiceImpl.getProviderAdminForEdit(1)).thenReturn(data);
	// when(serviceProvider_ServiceImpl.getAllProviderName()).thenReturn(serviceProvider_ServiceImpl1);

	// when(registerBenificiaryService.updateBenificiary(benNeg)).thenReturn(0);

} catch (Exception e) {

	fail("failed with error " + e.getMessage());

}

}
	
	
	public M_User1 testData(){
		M_User1 muse=new M_User1();
		muse.setUserName("Rajeev");
		muse.setFirstName("pankush");
		muse.setMiddleName("manchanda");
	    muse.setUserID(1);
	    return muse;
	}
	
	
	
	@Test
	public void getProviderAdmin() {
		assertThat(employeeMasterServiceImpl.getProviderAdmin()).isEqualTo(muser1);

	}
	
	
	
	@Test
	public void getProviderAdminNegative() {
		// assertFalse("124",
		// condition);(serviceProvider_ServiceImpl.getAllProviderName()).isEqualTo(serviceProvider_ServiceImpl1);
//		serviceProvider_ServiceImpl1 = serviceProvider_ServiceImpl1.getAllProviderName();
		 ArrayList<M_User1> data= employeeMasterServiceImpl.getProviderAdmin();
		for (M_User1 providerd : data) {
			assertNotEquals(123, providerd.getUserName());
		}

	}
	
	
	
	
	@Test
	public void testcreateProviderAdmin() throws Exception {
		
		assertThat(employeeMasterServiceImpl.createProviderAdmin(muser1)).isEqualTo(muser1);
    }
	
	
	
	
	
	@Test
	public void testcreateProviderAdminNegative() throws Exception {
		//assertThat(serviceProvider_ServiceImpl.createProvider(dataal)).isEqualTo(dataal);
		assertNotEquals((employeeMasterServiceImpl.createProviderAdmin(muser2)), muser1);
		
    }
	
	
	@Test
	public void testgetProviderAdminData() {
		
		//Integer it=324;
		//assertThat(serviceProvider_ServiceImpl.getProviderData(it)).isEqualTo(testData().getServiceProviderId());
		assertEquals(testData().getUserID(),(testData().getUserID()));
		
		//assertEquals(serviceProvider_ServiceImpl.getProviderData(testData().getServiceProviderId()), testData().getServiceProviderId());
    }
	
	
	@Test
	public void testgetProviderAdminDataNegative(){
		assertNotEquals("", testData());
	}
	
	
	
	/*@Test
	public void testupdateProviderAdminDataNegative(){
		assertNotEquals("", testData());
	}
	*/
	
	
	
	
	
	

}
