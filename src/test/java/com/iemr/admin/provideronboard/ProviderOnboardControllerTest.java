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
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import javax.validation.constraints.Size;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.omg.CORBA.portable.ServantObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.admin.controller.blocking.Blocking_Controller;
import com.iemr.admin.controller.provideronboard.ProviderOnBoardController;
import com.iemr.admin.data.provideronboard.M_UserservicerolemappingForRole;
import com.iemr.admin.data.provideronboard.ServiceProvider_Model;
import com.iemr.admin.data.provideronboard.V_Showprovideradmin;
import com.iemr.admin.exceptionhandler.DataNotFound;
import com.iemr.admin.service.provideronboard.ServiceProvider_ServiceImpl;



/*
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest*/
public class ProviderOnboardControllerTest {
	
	
	
	@Autowired
	private static ProviderOnBoardController providerOnboardController;
	
	
	
	@MockBean
	private ServiceProvider_Model serviceProvider_Model;
	
	
	
	ArrayList<ServiceProvider_Model> data1=new ArrayList<ServiceProvider_Model>();
	
	ServiceProvider_Model data;
	
	
	public ServiceProvider_Model returnObj(){
		
		ServiceProvider_Model data=new ServiceProvider_Model();
		data.setServiceProviderId(1);
		data.setServiceProviderName("Piramal");
		data.setPrimaryContactAddress("blr");
		data.setSecondaryContactAddress("hyd");
		data.setPrimaryContactName("Rajeev Tripathi");
		data.setPrimaryContactNo("123456789");
		data1.add(data);
		return data;
	}
	
	
	
	
	
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
	
	
	
	@Before
	  public void providercontrollerTest(){
		
		ServiceProvider_Model data=new ServiceProvider_Model();
		data.setServiceProviderId(1);
		data.setServiceProviderName("Piramal");
		data.setPrimaryContactAddress("blr");
		data.setSecondaryContactAddress("hyd");
		data.setPrimaryContactName("Rajeev Tripathi");
		data.setPrimaryContactNo("123456789");
		data1.add(data);
		
		
		
		
		
		
		M_UserservicerolemappingForRole data5=new M_UserservicerolemappingForRole();
	    data5.setProviderServiceMapID(1);
	    data5.setRoleID(2);
	    data5.setuSRMappingID(3);
	    data5.setUserID(4);
	    data5.setCreatedBy("Rajeev");
	    usr1.add(data5);
		
		
		providerOnboardController=mock(ProviderOnBoardController.class);
		
		
		// when(blockingcontroller.getMappedServiceLinesAndStatetoProvider("")).thenReturn(data1.toString()); 
		 // when(blockingcontroller.mapProviderAndServiceLines(data1.toString())).thenReturn(data1.toString());
			//when(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1")).thenReturn(data.toString());
		
		
		when(providerOnboardController.getAllProviderName("")).thenReturn(data1.toString());
		
		when(providerOnboardController.createProvider(data1.toString())).thenReturn(data1.toString());
		
		//when(providerOnboardController.createProvider(("[{\"serviceProviderId\":540,\"serviceProviderName\" : \"EXCEprion\",\"createdBy\" : \"rajeev\",\"joiningDate\" : \"2017-07-14T14:07:05.681Z\",\"stateID\" : 2,\"logoFileName\" : \"raj\",\"logoFilePath\" : \"ra\",\"primaryContactName\" : \"rajbaba\",\"primaryContactNo\" : \"1111\",\"primaryContactEmailID\" : \"xxx@gmail.com\",\"primaryContactAddress\" : \"blr\",\"primaryContactValidityTillDate\" : \"2017-07-14T14:07:05\",\"secondaryContactName\" : \"raj\",\"secondaryContactNo\" : \"233435\",\"secondaryContactEmailID\" : \"abc@gmail.com\",\"secondaryContactAddress\" : \"hyd\", \"secondaryContactValidityTillDate\" : \"2017-07-14T14:07:05.681Z\",\"statusID\" : \"1\",\"validFrom\" : \"2017-07-14T14:07:05.681Z\",\"validTill\" : \"2017-07-14T14:07:05.681Z\",\"deleted\" : false,\"createdDate\" : \"2017-07-14T14:07:05.681Z\",\"modifiedBy\" : \"rajeev\",\"lastModDate\" : \"2017-07-14T14:07:05.681Z\"}]").toString())).thenReturn(("[{\"serviceProviderId\":540,\"serviceProviderName\" : \"EXCEprion\",\"createdBy\" : \"rajeev\",\"joiningDate\" : \"2017-07-14T14:07:05.681Z\",\"stateID\" : 2,\"logoFileName\" : \"raj\",\"logoFilePath\" : \"ra\",\"primaryContactName\" : \"rajbaba\",\"primaryContactNo\" : \"1111\",\"primaryContactEmailID\" : \"xxx@gmail.com\",\"primaryContactAddress\" : \"blr\",\"primaryContactValidityTillDate\" : \"2017-07-14T14:07:05\",\"secondaryContactName\" : \"raj\",\"secondaryContactNo\" : \"233435\",\"secondaryContactEmailID\" : \"abc@gmail.com\",\"secondaryContactAddress\" : \"hyd\", \"secondaryContactValidityTillDate\" : \"2017-07-14T14:07:05.681Z\",\"statusID\" : \"1\",\"validFrom\" : \"2017-07-14T14:07:05.681Z\",\"validTill\" : \"2017-07-14T14:07:05.681Z\",\"deleted\" : false,\"createdDate\" : \"2017-07-14T14:07:05.681Z\",\"modifiedBy\" : \"rajeev\",\"lastModDate\" : \"2017-07-14T14:07:05.681Z\"}]").toString());
		when(providerOnboardController.providerUpdate("1")).thenReturn(data.toString());
		
		
		
		
		
		
		
         when(providerOnboardController.getMappingProviderAdmintoProvider("")).thenReturn(usr1.toString());
		
		when(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString())).thenReturn(usr1.toString());
		
		//when(providerOnboardController.createProvider(("[{\"serviceProviderId\":540,\"serviceProviderName\" : \"EXCEprion\",\"createdBy\" : \"rajeev\",\"joiningDate\" : \"2017-07-14T14:07:05.681Z\",\"stateID\" : 2,\"logoFileName\" : \"raj\",\"logoFilePath\" : \"ra\",\"primaryContactName\" : \"rajbaba\",\"primaryContactNo\" : \"1111\",\"primaryContactEmailID\" : \"xxx@gmail.com\",\"primaryContactAddress\" : \"blr\",\"primaryContactValidityTillDate\" : \"2017-07-14T14:07:05\",\"secondaryContactName\" : \"raj\",\"secondaryContactNo\" : \"233435\",\"secondaryContactEmailID\" : \"abc@gmail.com\",\"secondaryContactAddress\" : \"hyd\", \"secondaryContactValidityTillDate\" : \"2017-07-14T14:07:05.681Z\",\"statusID\" : \"1\",\"validFrom\" : \"2017-07-14T14:07:05.681Z\",\"validTill\" : \"2017-07-14T14:07:05.681Z\",\"deleted\" : false,\"createdDate\" : \"2017-07-14T14:07:05.681Z\",\"modifiedBy\" : \"rajeev\",\"lastModDate\" : \"2017-07-14T14:07:05.681Z\"}]").toString())).thenReturn(("[{\"serviceProviderId\":540,\"serviceProviderName\" : \"EXCEprion\",\"createdBy\" : \"rajeev\",\"joiningDate\" : \"2017-07-14T14:07:05.681Z\",\"stateID\" : 2,\"logoFileName\" : \"raj\",\"logoFilePath\" : \"ra\",\"primaryContactName\" : \"rajbaba\",\"primaryContactNo\" : \"1111\",\"primaryContactEmailID\" : \"xxx@gmail.com\",\"primaryContactAddress\" : \"blr\",\"primaryContactValidityTillDate\" : \"2017-07-14T14:07:05\",\"secondaryContactName\" : \"raj\",\"secondaryContactNo\" : \"233435\",\"secondaryContactEmailID\" : \"abc@gmail.com\",\"secondaryContactAddress\" : \"hyd\", \"secondaryContactValidityTillDate\" : \"2017-07-14T14:07:05.681Z\",\"statusID\" : \"1\",\"validFrom\" : \"2017-07-14T14:07:05.681Z\",\"validTill\" : \"2017-07-14T14:07:05.681Z\",\"deleted\" : false,\"createdDate\" : \"2017-07-14T14:07:05.681Z\",\"modifiedBy\" : \"rajeev\",\"lastModDate\" : \"2017-07-14T14:07:05.681Z\"}]").toString());
		when(providerOnboardController.editMappingProviderAdmintoProvider("1")).thenReturn(data5.toString());
		
		
		
	}
	
	
	
	@Test
	public void getAllProviderNamePositive(){
		
		
		assertThat(providerOnboardController.getAllProviderName("")).isEqualTo(providerOnboardController.getAllProviderName(""));
		  
	
		  
		  
		  assertThat(providerOnboardController.getAllProviderName("").contentEquals("blr")).isEqualTo(providerOnboardController.getAllProviderName("").contentEquals("blr"));
		  assertThat(providerOnboardController.getAllProviderName("").contentEquals("Rajeev Tripathi")).isEqualTo(providerOnboardController.getAllProviderName("").contentEquals("Rajeev Tripathi"));
		  assertThat(providerOnboardController.getAllProviderName("").contentEquals("1")).isEqualTo(providerOnboardController.getAllProviderName("").contentEquals("1"));
		  assertThat(providerOnboardController.getAllProviderName("").contentEquals("Piramal")).isEqualTo(providerOnboardController.getAllProviderName("").contentEquals("Piramal"));
		  assertThat(providerOnboardController.getAllProviderName("").contentEquals("hyd")).isEqualTo(providerOnboardController.getAllProviderName("").contentEquals("hyd"));
		  assertThat(providerOnboardController.getAllProviderName("").contentEquals("123456789")).isEqualTo(providerOnboardController.getAllProviderName("").contentEquals("123456789"));
		
	}
	
	
	@Test
	public void getAllProviderNameNegative(){
		
		  assertNotEquals(null,(providerOnboardController.getAllProviderName("")));
		  assertNotEquals("",(providerOnboardController.getAllProviderName("")));
		  assertNotEquals(new DataNotFound("data is not there"),(providerOnboardController.getAllProviderName("")));
		  
	}
	
	
	
	@Test
	public void mappingProviderAdmintoProviderPosative(){
		
		
		assertThat(providerOnboardController.createProvider(data1.toString()).equals(providerOnboardController.createProvider(data1.toString())));
		 assertThat(providerOnboardController.createProvider(data1.toString()).contentEquals("blr")).isEqualTo(providerOnboardController.createProvider(data1.toString()).contentEquals("blr"));
		 assertThat(providerOnboardController.createProvider(data1.toString()).contentEquals("Rajeev Tripathi")).isEqualTo(providerOnboardController.createProvider(data1.toString()).contentEquals("Rajeev Tripathi"));
		 assertThat(providerOnboardController.createProvider(data1.toString()).contentEquals("Piramal")).isEqualTo(providerOnboardController.createProvider(data1.toString()).contentEquals("Piramal"));
		 assertThat(providerOnboardController.createProvider(data1.toString()).contentEquals("hyd")).isEqualTo(providerOnboardController.createProvider(data1.toString()).contentEquals("hyd"));
		 assertThat(providerOnboardController.createProvider(data1.toString()).contentEquals("123456789")).isEqualTo(providerOnboardController.createProvider(data1.toString()).contentEquals("123456789"));
		
		
		
	}
	
	
	
	@Test
	public void mappingProviderAdmintoProviderNegative(){
		
		
		assertNotEquals(null,(providerOnboardController.createProvider(data1.toString())));
		  assertNotEquals("",(providerOnboardController.createProvider(data1.toString())));
		  assertNotEquals(new DataNotFound("data is not there"),(providerOnboardController.createProvider(data1.toString())));
		
	}
	
	
	
	
	@Test
	public void editMappingProviderAdmintoProviderPositive(){
		
		// assertThat(providerOnboardController.providerUpdate("1").equals(providerOnboardController.createProvider(data.toString())));
		  assertEquals(providerOnboardController.providerUpdate("1").contains("Rajeev Tripathi"),(providerOnboardController.providerUpdate("1").contains("Rajeev Tripathi")));
		  assertEquals(providerOnboardController.providerUpdate("1").contains("blr"),(providerOnboardController.providerUpdate("1").contains("blr")));
		  assertEquals(providerOnboardController.providerUpdate("1").contains("Piramal"),(providerOnboardController.providerUpdate("1").contains("Piramal")));
		  assertEquals(providerOnboardController.providerUpdate("1").contains("hyd"),(providerOnboardController.providerUpdate("1").contains("hyd")));
		  assertEquals(providerOnboardController.providerUpdate("1").contains("123456789"),(providerOnboardController.providerUpdate("1").contains("123456789")));
		  
		
	}
	
	
	@Test
	public void editMappingProviderAdmintoProviderNegative(){
		
		 assertNotEquals(null,(providerOnboardController.providerUpdate("1")));
		  assertNotEquals("",(providerOnboardController.providerUpdate("1")));
		  assertNotEquals(new DataNotFound("data is not there"),(providerOnboardController.providerUpdate("1")));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void getMappedProviderAdminNamePositive(){
		
		
		assertThat(providerOnboardController.getMappingProviderAdmintoProvider("")).isEqualTo(providerOnboardController.getMappingProviderAdmintoProvider(""));
		  
	
		  
		  
		  assertThat(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("1")).isEqualTo(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("1"));
		  assertThat(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("2")).isEqualTo(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("2"));
		  assertThat(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("3")).isEqualTo(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("3"));
		  assertThat(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("4")).isEqualTo(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("4"));
		  assertThat(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("Rajeev")).isEqualTo(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("Rajeev"));
		  //assertThat(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("123456789")).isEqualTo(providerOnboardController.getMappingProviderAdmintoProvider("").contentEquals("123456789"));
		
	}
	
	
	@Test
	public void getMappedProviderAdminNameNegative(){
		
		  assertNotEquals(null,(providerOnboardController.getMappingProviderAdmintoProvider("")));
		  assertNotEquals("",(providerOnboardController.getMappingProviderAdmintoProvider("")));
		  assertNotEquals(new DataNotFound("data is not there"),(providerOnboardController.getMappingProviderAdmintoProvider("")));
		  
	}
	
	
	
	@Test
	public void createProviderPosative(){
		
		
		assertThat(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).equals(providerOnboardController.createProvider(data1.toString())));
		 assertThat(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("1")).isEqualTo(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("1"));
		 assertThat(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("2")).isEqualTo(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("2"));
		 assertThat(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("3")).isEqualTo(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("3"));
		 assertThat(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("4")).isEqualTo(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("4"));
		 assertThat(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("Rajeev")).isEqualTo(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString()).contentEquals("Rajeev"));
		
		
		
	}
	
	
	
	@Test
	public void createProviderNegative(){
		
		
		assertNotEquals(null,(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString())));
		  assertNotEquals("",(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString())));
		  assertNotEquals(new DataNotFound("data is not there"),(providerOnboardController.mappingProviderAdmintoProvider(usr1.toString())));
		
	}
	
	
	
	
	@Test
	public void updateProviderPositive(){
		
		// assertThat(providerOnboardController.providerUpdate("1").equals(providerOnboardController.createProvider(data.toString())));
		  assertEquals(providerOnboardController.editMappingProviderAdmintoProvider("1").contains("1"),(providerOnboardController.providerUpdate("1").contains("1")));
		  assertEquals(providerOnboardController.editMappingProviderAdmintoProvider("1").contains("2"),(providerOnboardController.providerUpdate("1").contains("2")));
		  assertEquals(providerOnboardController.editMappingProviderAdmintoProvider("1").contains("3"),(providerOnboardController.providerUpdate("1").contains("3")));
		  assertEquals(providerOnboardController.editMappingProviderAdmintoProvider("1").contains("4"),(providerOnboardController.providerUpdate("1").contains("4")));
		  assertEquals(providerOnboardController.editMappingProviderAdmintoProvider("1").contains("Rajeev"),(providerOnboardController.providerUpdate("1").contains("Rajeev")));
		  
		
	}
	
	
	@Test
	public void updateProviderNegative(){
		
		 assertNotEquals(null,(providerOnboardController.editMappingProviderAdmintoProvider("1")));
		  assertNotEquals("",(providerOnboardController.editMappingProviderAdmintoProvider("1")));
		  assertNotEquals(new DataNotFound("data is not there"),(providerOnboardController.editMappingProviderAdmintoProvider("1")));
		
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*private MockMvc mockMvc;
	
	
	@Mock
    private ServiceProvider_ServiceImpl serviceProvider_ServiceImpl;
	
	
	@InjectMocks
	private ProviderOnBoardController providerOnBoardController;
	
	
	
	
	

	
	@Before
	public void setUp()throws Exception{
		
		mockMvc=MockMvcBuilders.standaloneSetup(providerOnBoardController)
				.build();
	}
	
	@Test
	public void createProvidertest()throws Exception{
		
		String json="{}";
		 mockMvc.perform(post("/createProvider")
		.accept(MediaType.APPLICATION_JSON))
		//.content(json))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		//.andExpect((jsonPath("$.*", Matchers.anyList().size()));
		//fail("Not yet implemented");
	}
	
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	
	
	@Test
	public void createProvidertest1(){
		ServiceProvider_Model data=new ServiceProvider_Model();
		ArrayList<ServiceProvider_Model> data1=new ArrayList<ServiceProvider_Model>();
		 
		data.setServiceProviderName("Rajeev");
		data.setCreatedBy("Rajeev");
		data1.add(data);
		
		
		 try {
			 
			 when(providerOnBoardController.getAllProviderName("{}")).toString();
			   //when(serviceProvider_ServiceImpl).getAllProviderName();
			 
			 String st=null;
			mockMvc.perform(post("http://localhost:8080/getAllProvider")
					.header("data", data1)
					
			.contentType(MediaType.APPLICATION_JSON)
			 .content(st))
			.andExpect(status().isOk());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//.andExpect((MockMvcResultMatchers.jsonPath("$.*",Matchers.class.getName()));
		//fail("Not yet implemented");
	}
	
	
	@Test
	public void createProvidertest2()throws Exception{
		String json="{\n" +
	         "\" serviceProviderName\" : \"Rajeev\",\n"+
				"\" createdBy\" : \"RajeevTripathi\"\n"+
	         "},{\n" +
	         "\" serviceProviderName\" : \"Rajeev\",\n"+
				"\" createdBy\" : \"RajeevTripathi\"\n"+
	         "}";
		mockMvc.perform(post("/createProvider")
				.contentType(MediaType.APPLICATION_JSON)
		 .content(json))
		.andExpect(MockMvcResultMatchers.status().isOk());
		//.andExpect((MockMvcResultMatchers.jsonPath("$.*",Matchers.class.getName()));
		//fail("Not yet implemented");
	}
	
	
	    
	@Test
	public void createProvidertest3()throws Exception{
		String json="{\n" +
	         "\" serviceProviderName\" : \"Rajeev\",\n"+
				"\" createdBy\" : \"RajeevTripathi\"\n"+
	         "},{\n" +
	         "\" serviceProviderName\" : \"Rajeev\",\n"+
				"\" createdBy\" : \"RajeevTripathi\"\n"+
	         "}";
		mockMvc.perform(post("/createProvider")
				.contentType(MediaType.APPLICATION_JSON)
		 .content(json))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect( (ResultMatcher) jsonPath("$",MockMvcResultMatchers.redirectedUrl("www.google.com")));
		
		//.andExpect((MockMvcResultMatchers.jsonPath("$.*",Matchers.class.getName()));
		//fail("Not yet implemented");
	}
	
	
	
	
	*/
	

	
	
	
	
	

}
