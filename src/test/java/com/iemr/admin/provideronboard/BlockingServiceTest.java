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
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotEquals;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
//import com.iemr.admin.service.blocking.Blocking_Service;
//// import com.iemr.admin.utils.mapper.InputMapper;
//
///*
// * @RunWith(SpringJUnit4ClassRunner.class)
// * 
// * @SpringBootTest
// */
//public class BlockingServiceTest
//{
//
//	private static M_Providerservicemapping_Blocking m_Providerservicemapping_BlockingPositive;
//	private static M_Providerservicemapping_Blocking m_Providerservicmapping_BlockingNegative;
//
//	@Autowired
//	private Blocking_Service blocking_Service;
//
//	@MockBean
//	private M_Providerservicemapping_Blocking m_Providerservicemapping_Blocking;
//
//	// private static InputMapper inputMapper = new InputMapper();
//
//	private static ArrayList<M_Providerservicemapping_Blocking> m_problocking;
//
//	private static ArrayList<M_Providerservicemapping_Blocking> m_problocking1;
//
//	M_Providerservicemapping_Blocking data;
//
//	public M_Providerservicemapping_Blocking serviceLinestoTheProvider()
//	{
//
//		M_Providerservicemapping_Blocking data = new M_Providerservicemapping_Blocking();
//		Integer[] ServiceID = { 1, 2, 3 };
//		for (int i = 0; i <= ServiceID.length; i++)
//		{
//			data.setServiceProviderID(1);
//			data.setAddress("blr");
//			data.setCreatedBy("Rajeev Tripathi");
//			data.setServiceID(ServiceID[i]);
//			data.setStateID(6);
//			m_problocking.add(data);
//		}
//		return data;
//	}
//
//	ArrayList<M_Providerservicemapping_Blocking> data1 = new ArrayList<M_Providerservicemapping_Blocking>();
//
//	@Before
//	public void mapServiceLinestoProvider()
//	{
//
//		Integer[] ServiceID = { 1 };
//		// for(int i=0;i<=ServiceID.length;i++){
//
//		data = new M_Providerservicemapping_Blocking();
//		data.setServiceProviderID(1);
//		data.setAddress("blr");
//		data.setCreatedBy("Rajeev Tripathi");
//		// data.setServiceID(ServiceID[i]);
//		data.setServiceID(3);
//		data.setStateID(6);
//		data1.add(data);
//		// }
//
//		blocking_Service = mock(Blocking_Service.class);
//
//		when(blocking_Service.getServiceLiensUsingProvider1(null)).thenReturn(data1);
//		when(blocking_Service.AddServiceProvider(data1)).thenReturn(data1);
//		// when(blocking_Service.getDataByProviderServiceMapId(1)).thenReturn(serviceLinestoTheProvider());
//
//	}
//
//	@Test
//	public void getServiceLinesUsingProviderPosative()
//	{
//		// assertThat(serviceProvider_ServiceImpl.getAllProviderName()).isEqualTo(dataal);
//
//		assertThat(blocking_Service.getServiceLiensUsingProvider1(null)).isEqualTo(data1);
//	}
//
//	@Test
//	public void getServiceLinesUsingProviderNegative()
//	{
//
//		ArrayList<M_Providerservicemapping_Blocking> data = blocking_Service.getServiceLiensUsingProvider1(null);
//		for (M_Providerservicemapping_Blocking providerd : data)
//		{
//			assertNotEquals(123, providerd.getAddress());
//			assertNotEquals("", providerd.getAddress());
//		}
//
//	}
//
//	@Test
//	public void AddServiceProviderPositive()
//	{
//		assertThat(blocking_Service.AddServiceProvider(data1)).isEqualTo(data1);
//	}
//
//	@Test
//	public void AddServiceProviderNegative()
//	{
//
//		// assertNotEquals((serviceProvider_ServiceImpl.createProvider1(serviceProvider_ServiceImpl1)), dataal);
//		assertNotEquals((blocking_Service.AddServiceProvider(m_problocking1)), data1);
//	}
//
//	@Test
//	public void getDataByProviderServiceMapIDPositiveTesting()
//	{
//		assertEquals(data.getServiceProviderID(), (data.getServiceProviderID()));
//
//	}
//
//	@Test
//	public void getDataByProviderServiceMapIDNegativeTesting()
//	{
//		assertNotEquals("", data);
//	}
//
//}
