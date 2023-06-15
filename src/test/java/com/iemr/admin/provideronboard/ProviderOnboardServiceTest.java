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

import com.iemr.admin.data.provideronboard.M_UserservicerolemappingForRole;
import com.iemr.admin.data.provideronboard.ServiceProvider_Model;
import com.iemr.admin.data.provideronboard.V_Showprovideradmin;
import com.iemr.admin.service.provideronboard.ServiceProvider_ServiceImpl;
import com.iemr.admin.utils.mapper.InputMapper;

/*@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest*/


public class ProviderOnboardServiceTest {

	/*
	 * @Autowired private ServiceProvider_ServiceImpl
	 * serviceProvider_ServiceImpl;
	 * 
	 * @MockBean private ServiceProvider_Model serviceProvider_Model;
	 * 
	 * @Test public void testgetAllProvider(){
	 * 
	 * }
	 */

	static String xyzData = "{}";

	private static ServiceProvider_Model xyzPositive;

	private static ServiceProvider_Model xyzNegetive;

	@Autowired
	private static ServiceProvider_ServiceImpl serviceProvider_ServiceImpl;

	
	

	/*@MockBean
	private ServiceProvider_ServiceImpl ServiceProvider_ServiceImpl2;*/
	
	
	

	@MockBean
	private ServiceProvider_Model serviceProvider_Model;

	//private static InputMapper inputMapper = new InputMapper();

	private static ArrayList<ServiceProvider_Model> serviceProvider_ServiceImpl1;
	
	private static ArrayList<M_UserservicerolemappingForRole> m_UserservicerolemappingForRole;
	
	
	
	
	/*RequestBuilder requestBuilder = MockMvcRequestBuilders
			.post("data")
			.accept(MediaType.APPLICATION_JSON).content(exampleJson)
			.contentType(MediaType.APPLICATION_JSON);
	// private static ServiceProvider_ServiceImpl serviceProvider_ServiceImpl;
*/
	
	
	ArrayList<V_Showprovideradmin> vshow=new ArrayList<V_Showprovideradmin>();
	
	public V_Showprovideradmin show(){
		V_Showprovideradmin show1=new V_Showprovideradmin();
		show1.setFirstName("Rajeev");
		show1.setLastName("Tripathi");
		show1.setServiceProviderName("Piramal");
		show1.setRoleName("Co");
		vshow.add(show1);
		return show1;
	}
	
	
	M_UserservicerolemappingForRole data5;
	
	ArrayList<M_UserservicerolemappingForRole> usr1=new ArrayList<M_UserservicerolemappingForRole>();
	public M_UserservicerolemappingForRole testData1(){
		M_UserservicerolemappingForRole data5=new M_UserservicerolemappingForRole();
		    data5.setProviderServiceMapID(1);
		    data5.setRoleID(2);
		    data5.setuSRMappingID(3);
		    data5.setUserID(4);
		    data5.setCreatedBy("Rajeev");
		   usr1.add(data5);
		   
		   return data5;
		    
	}
	
	
	
	
	
	ServiceProvider_Model data;
	
	
	
	
	public ServiceProvider_Model testData(){
		ServiceProvider_Model data=new ServiceProvider_Model();
		data.setServiceProviderId(324);
		data.setServiceProviderName("rajeev");
		data.setSecondaryContactAddress("xyz");
		data.setCreatedBy("rajeev");
		return data;
		
	}
	
	ArrayList<ServiceProvider_Model> dataal=new ArrayList<ServiceProvider_Model>();
	@Before
	public void createProvider() {

		try {
			 data=new ServiceProvider_Model();
			data.setServiceProviderId(324);
			data.setServiceProviderName("rajeev");
			data.setSecondaryContactAddress("xyz");
			data.setCreatedBy("rajeev");
			dataal.add(data);
			
			
			
			
		    data5=new M_UserservicerolemappingForRole();
		    data5.setProviderServiceMapID(1);
		    data5.setRoleID(2);
		    data5.setuSRMappingID(3);
		    data5.setUserID(4);
		    data5.setCreatedBy("Rajeev");
		    usr1.add(data5);
			
			
			
			

			serviceProvider_ServiceImpl = mock(ServiceProvider_ServiceImpl.class);

			//xyzPositive = inputMapper.gson().fromJson(xyzData, ServiceProvider_Model.class);

			//xyzNegetive = inputMapper.gson().fromJson(xyzData, ServiceProvider_Model.class);
			
			//ServiceProvider_Model[] servModels = inputMapper.gson().fromJson("requestBuilder", ServiceProvider_Model[].class);
			//serviceProvider_ServiceImpl1 = (ArrayList<ServiceProvider_Model>) Arrays.asList(servModels);

			// benNeg.setServiceProviderName("OL");

			when(serviceProvider_ServiceImpl.getAllProviderName()).thenReturn(dataal);
			when(serviceProvider_ServiceImpl.createProvider1(dataal)).thenReturn(dataal);
			when(serviceProvider_ServiceImpl.getProviderData(324)).thenReturn(testData());
			
			
			
			
			
			when(serviceProvider_ServiceImpl.getProviderAdmins()).thenReturn(vshow);
			when(serviceProvider_ServiceImpl.AddUserRole(usr1)).thenReturn(usr1);
			when(serviceProvider_ServiceImpl.getPADataForEdit(1)).thenReturn(testData1());
			// when(serviceProvider_ServiceImpl.getAllProviderName()).thenReturn(serviceProvider_ServiceImpl1);

			// when(registerBenificiaryService.updateBenificiary(benNeg)).thenReturn(0);

		} catch (Exception e) {

			fail("failed with error " + e.getMessage());

		}

	}

	@Test
	public void testAllProvider() {
		assertThat(serviceProvider_ServiceImpl.getAllProviderName()).isEqualTo(dataal);

	}

	@Test
	public void testAllProviderNegative() {
		// assertFalse("124",
		// condition);(serviceProvider_ServiceImpl.getAllProviderName()).isEqualTo(serviceProvider_ServiceImpl1);
//		serviceProvider_ServiceImpl1 = serviceProvider_ServiceImpl1.getAllProviderName();
		 ArrayList<ServiceProvider_Model> data= serviceProvider_ServiceImpl.getAllProviderName();
		for (ServiceProvider_Model providerd : data) {
			assertNotEquals(123, providerd.getServiceProviderName());
		}

	}
	
	
	@Test
	public void testcreateProvider() {
		assertThat(serviceProvider_ServiceImpl.createProvider1(dataal)).isEqualTo(dataal);
    }
	
	
	
	@Test
	public void testcreateProviderNegative() {
		//assertThat(serviceProvider_ServiceImpl.createProvider(dataal)).isEqualTo(dataal);
		assertNotEquals((serviceProvider_ServiceImpl.createProvider1(serviceProvider_ServiceImpl1)), dataal);
		
    }
	
	
	@Test
	public void testgetProviderData() {
		
		//Integer it=324;
		//assertThat(serviceProvider_ServiceImpl.getProviderData(it)).isEqualTo(testData().getServiceProviderId());
		assertEquals(testData().getServiceProviderId(),(testData().getServiceProviderId() ));
		
		//assertEquals(serviceProvider_ServiceImpl.getProviderData(testData().getServiceProviderId()), testData().getServiceProviderId());
    }
	
	
	
	@Test
	public void testgetProviderDataNegative(){
		assertNotEquals("", testData());
	}
	
	
	
	
	
	
	
	
	@Test
	public void testAllProviderAdmins() {
		assertThat(serviceProvider_ServiceImpl.getProviderAdmins()).isEqualTo(vshow);
	
	
	}
	
	
	@Test
	public void testAllProviderAdminNegative() {
		// assertFalse("124",
		// condition);(serviceProvider_ServiceImpl.getAllProviderName()).isEqualTo(serviceProvider_ServiceImpl1);
//		serviceProvider_ServiceImpl1 = serviceProvider_ServiceImpl1.getAllProviderName();
		 ArrayList<V_Showprovideradmin> data= serviceProvider_ServiceImpl.getProviderAdmins();
		for (V_Showprovideradmin providerd : data) {
			assertNotEquals(123, providerd.getServiceProviderName());
		}

	}
	
	
	
	
	@Test
	public void testTagProviderAdmintoProvider() {
		assertThat(serviceProvider_ServiceImpl.AddUserRole(usr1)).isEqualTo(usr1);
    }
	
	
	
	@Test
	public void testTagProviderAdmintoProviderNegative() {
		//assertThat(serviceProvider_ServiceImpl.createProvider(dataal)).isEqualTo(dataal);
		assertNotEquals((serviceProvider_ServiceImpl.AddUserRole(m_UserservicerolemappingForRole)), usr1);
		
    }
	
	
	
	
	
	@Test
	public void testgetProviderAdminTaggedData() {
		
		//Integer it=324;
		//assertThat(serviceProvider_ServiceImpl.getProviderData(it)).isEqualTo(testData().getServiceProviderId());
		assertEquals(testData1().getProviderServiceMapID(),(testData1().getProviderServiceMapID()));
		
		//assertEquals(serviceProvider_ServiceImpl.getProviderData(testData().getServiceProviderId()), testData().getServiceProviderId());
    }
	
	
	
	@Test
	public void testgetProviderDataTaggedDataNegative(){
		assertNotEquals("", testData1());
	}
	
	
	
	
	
	
	
	
	
}
