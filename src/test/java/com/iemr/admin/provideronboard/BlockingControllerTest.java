///*
//* AMRIT – Accessible Medical Records via Integrated Technology 
//* Integrated EHR (Electronic Health Records) Solution 
//*
//* Copyright (C) "Piramal Swasthya Management and Research Institute" 
//*
//* This file is part of AMRIT.
//*
//* This program is free software: you can redistribute it and/or modify
//* it under the terms of the GNU General Public License as published by
//* the Free Software Foundation, either version 3 of the License, or
//* (at your option) any later version.
//*
//* This program is distributed in the hope that it will be useful,
//* but WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//* GNU General Public License for more details.
//*
//* You should have received a copy of the GNU General Public License
//* along with this program.  If not, see https://www.gnu.org/licenses/.
//*/
//package com.iemr.admin.provideronboard;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.iemr.admin.controller.blocking.BlockingController;
//import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
//import com.iemr.admin.exceptionhandler.DataNotFound;
//
///*
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest*/
////@ContextConfiguration(classes = { Blocking_Controller.class, Blocking_Service.class })
//public class BlockingControllerTest {
//	
//	
//      private static M_Providerservicemapping_Blocking m_Providerservicemapping_BlockingPositive;
//	  private static M_Providerservicemapping_Blocking m_Providerservicmapping_BlockingNegative;
//	  
//	  
//	  
//	 @Autowired
//	 private BlockingController blockingcontroller;
//	 
//	 
//  @MockBean
//  private M_Providerservicemapping_Blocking m_Providerservicemapping_Blocking;
//  
//  
//private static ArrayList<M_Providerservicemapping_Blocking> m_problocking;
//  
//  private static ArrayList<M_Providerservicemapping_Blocking> m_problocking1;
//  
//  M_Providerservicemapping_Blocking data;
//  
//  
//  
//  
//  
//  
//  
//  public  M_Providerservicemapping_Blocking serviceLinestoTheProvider(){
//	  
//	   M_Providerservicemapping_Blocking data=new M_Providerservicemapping_Blocking();
//	   Integer[] ServiceID={1,2,3};
//	   for(int i=0;i<=ServiceID.length;i++){
//	   data.setServiceProviderID(1);
//	   data.setAddress("blr");
//	   data.setCreatedBy("Rajeev Tripathi");
//	   data.setServiceID(ServiceID[i]);
//	   data.setStateID(6);
//	   m_problocking.add(data);
//	   }
//	   return data;
// }
//  
//  
//  ArrayList<M_Providerservicemapping_Blocking> data1=new ArrayList<M_Providerservicemapping_Blocking>();
//  
// // @Before
//  public void mapServiceLinestoProvider(){
//	  
//	  
//	  Integer[] ServiceID={1};
//	 // for(int i=0;i<=ServiceID.length;i++){
//		  
//	       data=new M_Providerservicemapping_Blocking();
//		   data.setServiceProviderID(1);
//		   data.setAddress("blr");
//		   data.setCreatedBy("Rajeev Tripathi");
//		   //data.setServiceID(1);
//		   data.setServiceID(3);
//		   data.setStateID(6);
//		   data1.add(data);
//		  // }
//	  
//		   blockingcontroller=mock(BlockingController.class);
//	  
//	  when(blockingcontroller.getMappedServiceLinesAndStatetoProvider("")).thenReturn(data1.toString());
//	  
//	  when(blockingcontroller.mapProviderAndServiceLines(data1.toString())).thenReturn(data1.toString());
//	  
//		when(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1")).thenReturn(data.toString());
//	  
//  }
//  
//  
//  
////  @Test
//  public void getServiceLinesUsingProviderPosative(){
//	 //assertThat(serviceProvider_ServiceImpl.getAllProviderName()).isEqualTo(dataal);
//	  
//	  assertThat(blockingcontroller.getMappedServiceLinesAndStatetoProvider("")).isEqualTo(blockingcontroller.getMappedServiceLinesAndStatetoProvider(""));
//	  
//	 
//	  //boolean dat=blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contains("blr");
//	 // for(String mappedData:blockingcontroller.getMappedServiceLinesAndStatetoProvider(""));
//	  
//	  
//	  assertThat(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("blr")).isEqualTo(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("blr"));
//	  assertThat(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("Rajeev Tripathi")).isEqualTo(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("Rajeev Tripathi"));
//	  assertThat(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("1")).isEqualTo(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("1"));
//	  assertThat(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("3")).isEqualTo(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("3"));
//	  assertThat(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("6")).isEqualTo(blockingcontroller.getMappedServiceLinesAndStatetoProvider("").contentEquals("6")); 
//  }
//  
//  
//  
//  
//  
// // @Test
//  public void getServiceLinesUsingProviderNegative(){
//	 //assertThat(serviceProvider_ServiceImpl.getAllProviderName()).isEqualTo(dataal);
//	  
////	  assertNotEquals(null,(blockingcontroller.getMappedServiceLinesAndStatetoProvider("")));
////	  assertNotEquals("",(blockingcontroller.getMappedServiceLinesAndStatetoProvider("")));
////	  assertNotEquals(new DataNotFound("data is not there"),(blockingcontroller.getMappedServiceLinesAndStatetoProvider("")));
//	  
//  }
//  
//  
//  
//  
//  
//  
// // @Test
//  public void mapProviderAndServiceLinesPosative(){
//	  
//	 assertThat(blockingcontroller.mapProviderAndServiceLines(data1.toString()).equals(blockingcontroller.mapProviderAndServiceLines(data1.toString())));
//	 assertThat(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("blr")).isEqualTo(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("blr"));
//	 assertThat(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("Rajeev Tripathi")).isEqualTo(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("Rajeev Tripathi"));
//	 assertThat(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("1")).isEqualTo(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("1"));
//	 assertThat(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("3")).isEqualTo(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("3"));
//	 assertThat(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("6")).isEqualTo(blockingcontroller.mapProviderAndServiceLines(data1.toString()).contentEquals("6"));
//	  
//	  }
//
//  
//  
//  
////  @Test
//  public void mapProviderAndServiceLinesNegative(){
//	  
////	  assertNotEquals(null,(blockingcontroller.mapProviderAndServiceLines(data1.toString())));
////	  assertNotEquals("",(blockingcontroller.mapProviderAndServiceLines(data1.toString())));
////	  assertNotEquals(new DataNotFound("data is not there"),(blockingcontroller.mapProviderAndServiceLines(data1.toString())));
//	  	  
//  }
//  
//  
//  
////  @Test
////  public void editMappedServiceLinesAndStatetoProviderPositive(){
////	  assertThat(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").equals(blockingcontroller.mapProviderAndServiceLines(data.toString())));
////	  assertEquals(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("Rajeev Tripathi"),(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("Rajeev Tripathi")));
////	  assertEquals(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("blr"),(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("blr")));
////	  assertEquals(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("1"),(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("1")));
////	  assertEquals(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("3"),(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("3")));
////	  assertEquals(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("6"),(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1").contains("6")));
////	  
////	  	  
////  }
////  
//  
//  
////  @Test
////  public void editMappedServiceLinesAndStatetoProviderNegative(){
////	  
////	  assertNotEquals(null,(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1")));
////	  assertNotEquals("",(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1")));
////	  assertNotEquals(new DataNotFound("data is not there"),(blockingcontroller.editMappedServiceLinesAndStatetoProvider("1")));
////	  	  
////  }
////  
//
//
//
//	 
//	 
//	 
//	
//	  
//	  
///*	
////	@MockBean
////	 M_Providerservicemapping_Blocking data2=new M_Providerservicemapping_Blocking();
////	String data=new String(data2.toString());
//
//	private MockMvc mockMvc;
//	//private MProviderservicemappingBlockingRepo showproviderservicemappingRepoMock;
//
//	private M_Providerservicemapping_Blocking data = new M_Providerservicemapping_Blocking();
//
//	class ResultObject {
//		public ResultObject(int i, int j, int k, String string) {
//			this.obj0 = i;
//			this.obj1 = j;
//			this.obj2 = k;
//			this.obj3 = string;
//		}
//
//		Integer obj0;
//		Integer obj1;
//		Integer obj2;
//		String obj3;
//	}
//
//	private List<Integer> failSet = Arrays.asList(1, 2, 3);
//
//	Object[] data1Obj = { new ResultObject(1, 2, 3, "test1"), new ResultObject(1, 2, 3, "test2") };
//	private ArrayList<Object[]> data1 = new ArrayList<Object[]>();
//
//	private List<M_Providerservicemapping_Blocking> passProviders = Arrays.asList(data);
//	private List<M_Providerservicemapping_Blocking> passSet = Arrays.asList(data);
//
//	
//	
//	
//	
//	@InjectMocks
//	private Blocking_Controller blocking_Controller;
//
//	@Before
//	public void setUp() throws Exception {
//		mockMvc = MockMvcBuilders.standaloneSetup(blocking_Controller).build();
//		//showproviderservicemappingRepoMock = (MProviderservicemappingBlockingRepo) mock(
//				//M_Providerservicemapping_Blocking.class);
//		//data1.add(new )
//		//when((showproviderservicemappingRepoMock.getServiceLiensUsingProvider1())).thenReturn(data1);
//	}
//	//java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 5
//	
//	public static String asJsonString(final Object obj) {
//	    try {
//	        return new ObjectMapper().writeValueAsString(obj);
//	    } catch (Exception e) {
//	        throw new RuntimeException(e);
//	    }
//	}
//	
//	
//	@Test
//	public void getProviderMappedInformation1() throws Exception {
//	
//	
//	 String json="{\n" + "\" serviceProviderID \" : \"324\",\n"+
//			  "\" stateID\" : \"5\",\n"+ "\" serviceID1\" : \"[2,3,4]\",\n"+
//			  "\" createdBy\" : \"Rajeev\"\n"+ "},{\n" +
//			  "\" serviceProviderID \" : \"324\",\n"+ "\" stateID\" : \"5\",\n"+
//			  "\" serviceID1\" : \"[2,3,4]\",\n"+ "\" createdBy\" : \"Rajeev\"\n"+ "}";
//	
//	  
//	  Gson g = new Gson(); 
//		 M_Providerservicemapping_Blocking p = g.fromJson(json, M_Providerservicemapping_Blocking.class);
//		 String str = g.toJson(p);
//		 
//		 
//		 
//		 M_Providerservicemapping_Blocking ps = InputMapper.gson().fromJson(json.toString(),
//					M_Providerservicemapping_Blocking.class);
//		 String str1 = g.toJson(ps);
//		
//		M_Providerservicemapping_Blocking mpb=new M_Providerservicemapping_Blocking("cnp");
//		
//		
//		//Object data = new Gson().toJson(mpb);
//		mockMvc.perform(post("/getMappedServiceLinesAndStatetoProvider")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(str))
//		         .andExpect(status().isOk());
//		          //
//				//.content(data));
//				//.content(data.toString()))
//				
//		// .andExpect(jsonPath("$.createdBy").exists());
//		// .andExpect(content().string("Honda Civic"));
//
//	}
//
//	
//	  @Test public void MappedProvidertoServiceLinesAndState() throws
//	  Exception{
//	  
//	  
//	  
////	  String json="{\n" + "\" serviceProviderName\" : \"Rajeev\",\n"+
////	  "\" createdBy\" : \"RajeevTripathi\"\n"+ "},{\n" +
////	  "\" serviceProviderName\" : \"Rajeev\",\n"+
////	  "\" createdBy\" : \"RajeevTripathi\"\n"+ "}";
//	  
//	  
//	  
//	  
//	  String json="{\n" + "\" serviceProviderID \" : \"324\",\n"+
//	  "\" stateID\" : \"5\",\n"+ "\" serviceID1\" : \"[2,3,4]\",\n"+
//	  "\" createdBy\" : \"Rajeev\"\n"+ "},{\n" +
//	  "\" serviceProviderID \" : \"324\",\n"+ "\" stateID\" : \"5\",\n"+
//	  "\" serviceID1\" : \"[2,3,4]\",\n"+ "\" createdBy\" : \"Rajeev\"\n"+ "}";
//	
//	  
//	  Gson g = new Gson(); 
//		 M_Providerservicemapping_Blocking[] p = g.fromJson(json, M_Providerservicemapping_Blocking[].class);
//		 String str = g.toJson(p);
//		 
//		 
//		 
//		 M_Providerservicemapping_Blocking[] ps = InputMapper.gson().fromJson(json.toString(),
//					M_Providerservicemapping_Blocking[].class);
//		 String str1 = g.toJson(ps);
//
//				// Read more: http://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html#ixzz537AMFMGs
//	  mockMvc.perform(post("/mapServiceLinesAndStatetoProvider")
//	  .contentType(MediaType.APPLICATION_JSON) .content("x"))
//	  .andExpect(MockMvcResultMatchers.status().isOk());
//	  
//	  }
//	 
//
//	
//	 * @Test public void MappedProvidertoServiceLinesAndStateNegative() throws
//	 * Exception{
//	 * 
//	 * 
//	 * 
//	 * String json="{\n" + "\" serviceProviderName\" : \"Rajeev\",\n"+
//	 * "\" createdBy\" : \"RajeevTripathi\"\n"+ "},{\n" +
//	 * "\" serviceProviderName\" : \"Rajeev\",\n"+
//	 * "\" createdBy\" : \"RajeevTripathi\"\n"+ "}";
//	 * 
//	 * 
//	 * 
//	 * 
//	 * String json="{\n" + "\" serviceProviderID \" : \"324\",\n"+
//	 * "\" stateID\" : \"5\",\n"+ "\" createdBy\" : \"Rajeev\"\n"+ "}";
//	 * mockMvc.perform(post("/mapServiceLinesAndStatetoProvider")
//	 * .contentType(MediaType.APPLICATION_JSON) .content(json))
//	 * .andExpect(MockMvcResultMatchers.status().isOk());
//	 * 
//	 * 
//	 * }
//	 
//
//	
//	 * @Test public void getProviderMappedInformation1() throws Exception{
//	 * String json="";
//	 * mockMvc.perform(post("/getMappedServiceLinesAndStatetoProvider")
//	 * .contentType(MediaType.APPLICATION_JSON) .content(json))
//	 * .andExpect(MockMvcResultMatchers.status().isOk());
//	 * 
//	 * }
//	 
//*/
//}
