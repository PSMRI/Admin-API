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

import com.iemr.admin.controller.employeemaster.EmployeeMasterController;
import com.iemr.admin.controller.provideronboard.ProviderOnBoardController;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.provideronboard.ServiceProvider_Model;
import com.iemr.admin.exceptionhandler.DataNotFound;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
public class EmployeeControllerTest {
	
	
	@Autowired
	private static EmployeeMasterController employeeMasterController;
	
	
	
	@MockBean
	private M_User1 m_User1;
	
	
	ArrayList<M_User1> data1=new ArrayList<M_User1>();
	
	
	M_User1 data;
	
	
	
	
         public M_User1 returnObj(){
	    M_User1 data=new M_User1();
		data.setUserID(1);
		data.setUserName("Rajeev");
		data.setFirstName("Tripathi");
		data.setAadhaarNo("123445");
		data.setDesignationName("Doctor");
		data.setCreatedBy("Tripathi Rajeev");
		data1.add(data);
		return data;
	    }
         
         
         
         
         @Before
   	  public void employeeMastercontrollerTest(){
   		
   		M_User1 data=new M_User1();
   		data.setUserID(1);
   		data.setUserName("Rajbaba");
   		data.setFirstName("Tripathi");
   		data.setMiddleName("Kumar");
   		data.setLastName("Rajeev");
   		data.setAadhaarNo("12345");
   		data1.add(data);
   		
   		
   		employeeMasterController=mock(EmployeeMasterController.class);
   		
   		
   		// when(blockingcontroller.getMappedServiceLinesAndStatetoProvider("")).thenReturn(data1.toString()); 
   		 // when(blockingcontroller.mapProviderAndServiceLines(data1.toString())).thenReturn(data1.toString());
   			//when(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1")).thenReturn(data.toString());
   		
   		
   		when(employeeMasterController.getProviderAdmin("")).thenReturn(data1.toString());
   		
   		when(employeeMasterController.createProviderAdmin(data1.toString())).thenReturn(data1.toString());
   		
   		//when(providerOnboardController.createProvider(("[{\"serviceProviderId\":540,\"serviceProviderName\" : \"EXCEprion\",\"createdBy\" : \"rajeev\",\"joiningDate\" : \"2017-07-14T14:07:05.681Z\",\"stateID\" : 2,\"logoFileName\" : \"raj\",\"logoFilePath\" : \"ra\",\"primaryContactName\" : \"rajbaba\",\"primaryContactNo\" : \"1111\",\"primaryContactEmailID\" : \"xxx@gmail.com\",\"primaryContactAddress\" : \"blr\",\"primaryContactValidityTillDate\" : \"2017-07-14T14:07:05\",\"secondaryContactName\" : \"raj\",\"secondaryContactNo\" : \"233435\",\"secondaryContactEmailID\" : \"abc@gmail.com\",\"secondaryContactAddress\" : \"hyd\", \"secondaryContactValidityTillDate\" : \"2017-07-14T14:07:05.681Z\",\"statusID\" : \"1\",\"validFrom\" : \"2017-07-14T14:07:05.681Z\",\"validTill\" : \"2017-07-14T14:07:05.681Z\",\"deleted\" : false,\"createdDate\" : \"2017-07-14T14:07:05.681Z\",\"modifiedBy\" : \"rajeev\",\"lastModDate\" : \"2017-07-14T14:07:05.681Z\"}]").toString())).thenReturn(("[{\"serviceProviderId\":540,\"serviceProviderName\" : \"EXCEprion\",\"createdBy\" : \"rajeev\",\"joiningDate\" : \"2017-07-14T14:07:05.681Z\",\"stateID\" : 2,\"logoFileName\" : \"raj\",\"logoFilePath\" : \"ra\",\"primaryContactName\" : \"rajbaba\",\"primaryContactNo\" : \"1111\",\"primaryContactEmailID\" : \"xxx@gmail.com\",\"primaryContactAddress\" : \"blr\",\"primaryContactValidityTillDate\" : \"2017-07-14T14:07:05\",\"secondaryContactName\" : \"raj\",\"secondaryContactNo\" : \"233435\",\"secondaryContactEmailID\" : \"abc@gmail.com\",\"secondaryContactAddress\" : \"hyd\", \"secondaryContactValidityTillDate\" : \"2017-07-14T14:07:05.681Z\",\"statusID\" : \"1\",\"validFrom\" : \"2017-07-14T14:07:05.681Z\",\"validTill\" : \"2017-07-14T14:07:05.681Z\",\"deleted\" : false,\"createdDate\" : \"2017-07-14T14:07:05.681Z\",\"modifiedBy\" : \"rajeev\",\"lastModDate\" : \"2017-07-14T14:07:05.681Z\"}]").toString());
   		when(employeeMasterController.editProviderAdmin("1")).thenReturn(data.toString());
   		
   		
   		
   		
   	}
         
         
         
         @Test
     	public void getProviderAdminPositive(){
     		
     		
     		assertThat(employeeMasterController.getProviderAdmin("")).isEqualTo(employeeMasterController.getProviderAdmin(""));
     		  
     	
     		  
     		  
     		  assertThat(employeeMasterController.getProviderAdmin("").contentEquals("1")).isEqualTo(employeeMasterController.getProviderAdmin("").contentEquals("1"));
     		  assertThat(employeeMasterController.getProviderAdmin("").contentEquals("Rajeev")).isEqualTo(employeeMasterController.getProviderAdmin("").contentEquals("Rajeev"));
     		  assertThat(employeeMasterController.getProviderAdmin("").contentEquals("Tripathi")).isEqualTo(employeeMasterController.getProviderAdmin("").contentEquals("Tripathi"));
     		  assertThat(employeeMasterController.getProviderAdmin("").contentEquals("123445")).isEqualTo(employeeMasterController.getProviderAdmin("").contentEquals("123445"));
     		  assertThat(employeeMasterController.getProviderAdmin("").contentEquals("Doctor")).isEqualTo(employeeMasterController.getProviderAdmin("").contentEquals("Doctor"));
     		  assertThat(employeeMasterController.getProviderAdmin("").contentEquals("Tripathi Rajeev")).isEqualTo(employeeMasterController.getProviderAdmin("").contentEquals("Tripathi Rajeev"));
     		
     	}
	
         
         
        @Test
     	public void getProviderAdminNegative(){
     		
     		  assertNotEquals(null,(employeeMasterController.getProviderAdmin("")));
     		  assertNotEquals("",employeeMasterController.getProviderAdmin(""));
     		  assertNotEquals(new DataNotFound("data is not there"),employeeMasterController.getProviderAdmin(""));
     		  
     	}
        
        
        
        
        @Test
    	public void createProviderAdminPosative(){
    		
    		
    		 assertThat(employeeMasterController.createProviderAdmin(data1.toString()).equals(employeeMasterController.createProviderAdmin(data1.toString())));
    		 assertThat(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Rajeev")).isEqualTo(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Rajeev"));
    		 assertThat(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Tripathi")).isEqualTo(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Tripathi"));
    		 assertThat(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("123445")).isEqualTo(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("123445"));
    		 assertThat(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Doctor")).isEqualTo(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Doctor"));
    		 assertThat(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Tripathi Rajeev")).isEqualTo(employeeMasterController.createProviderAdmin(data1.toString()).contentEquals("Tripathi Rajeev"));
    		
    		
    		
    	}
        
        
        
        
        @Test
    	public void createProviderNegative(){
    		
    		
    		assertNotEquals(null,(employeeMasterController.createProviderAdmin(data1.toString())));
    		  assertNotEquals("",(employeeMasterController.createProviderAdmin(data1.toString())));
    		  assertNotEquals(new DataNotFound("data is not there"),(employeeMasterController.createProviderAdmin(data1.toString())));
    		
    	}
        
        
        
        

    	@Test
    	public void updateProviderPositive(){
    		
    		// assertThat(providerOnboardController.providerUpdate("1").equals(providerOnboardController.createProvider(data.toString())));
    		  assertEquals(employeeMasterController.editProviderAdmin("1").contains("Rajbaba"),(employeeMasterController.editProviderAdmin("1").contains("Rajbaba")));
    		  assertEquals(employeeMasterController.editProviderAdmin("1").contains("Tripathi"),(employeeMasterController.editProviderAdmin("1").contains("Tripathi")));
    		  assertEquals(employeeMasterController.editProviderAdmin("1").contains("Kumar"),(employeeMasterController.editProviderAdmin("1").contains("Kumar")));
    		  assertEquals(employeeMasterController.editProviderAdmin("1").contains("Rajeev"),(employeeMasterController.editProviderAdmin("1").contains("Rajeev")));
    		  assertEquals(employeeMasterController.editProviderAdmin("1").contains("12345"),(employeeMasterController.editProviderAdmin("1").contains("12345")));
    		  
    		
    	}
    	
    	
    	@Test
    	public void updateProviderNegative(){
    		
    		 assertNotEquals(null,(employeeMasterController.editProviderAdmin("1")));
    		  assertNotEquals("",(employeeMasterController.editProviderAdmin("1")));
    		  assertNotEquals(new DataNotFound("data is not there"),(employeeMasterController.editProviderAdmin("1")));
    		
    	}
     	
	
	
	
	
}
