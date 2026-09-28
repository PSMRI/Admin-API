/*
* AMRIT - Accessible Medical Records via Integrated Technologies
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
package com.iemr.admin.service.blocking;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
import com.iemr.admin.data.blocking.M_Serviceprovider_Blocking;
import com.iemr.admin.data.blocking.M_Status1;
import com.iemr.admin.data.blocking.T_Providerservicemappingdetail;
import com.iemr.admin.data.blocking.T_Serviceproviderdetail;
import com.iemr.admin.data.blocking.T_Userdetail;
import com.iemr.admin.data.blocking.UserForBlocking;
import com.iemr.admin.data.blocking.V_Showproviderservicemapping;
import com.iemr.admin.repo.blocking.MProviderservicemappingBlockingRepo;
import com.iemr.admin.repo.blocking.MServiceproviderBlockingRepo;
import com.iemr.admin.repo.blocking.MStatusRepo;
import com.iemr.admin.repo.blocking.T_ProviderservicemappingdetailRepo;
import com.iemr.admin.repo.blocking.T_ServiceproviderdetailRepo;
import com.iemr.admin.repo.blocking.T_UserDetailRepo;
import com.iemr.admin.repo.blocking.UserBlockingRepo;
import com.iemr.admin.repo.blocking.V_ShowproviderservicemappingRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The blocking service turns each request to suspend a provider into the right
 * repository update, and reports how far a CTI campaign mapping got.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Blocking_Service Test Suite")
class BlockingServiceTest {

    private static final Integer PROVIDER_ID = 77;
    private static final Integer SERVICE_ID = 3;
    private static final Integer STATE_ID = 29;

    @Mock
    private V_ShowproviderservicemappingRepo v_ShowproviderservicemappingRepo;

    @Mock
    private T_UserDetailRepo t_UserDetailRepo;

    @Mock
    private MStatusRepo mStatusRepo;

    @Mock
    private UserBlockingRepo userBlockingRepo;

    @Mock
    private T_ProviderservicemappingdetailRepo t_ProviderservicemappingdetailRepo;

    @Mock
    private MProviderservicemappingBlockingRepo mProviderservicemappingBlockingRepo;

    @Mock
    private T_ServiceproviderdetailRepo t_ServiceproviderdetailRepo;

    @Mock
    private MServiceproviderBlockingRepo mServiceproviderBlockingRepo;

    @InjectMocks
    private Blocking_Service service;

    private static M_Providerservicemapping_Blocking mapping(Integer mapId) {
        M_Providerservicemapping_Blocking mapping = new M_Providerservicemapping_Blocking();
        mapping.setProviderServiceMapID(mapId);
        mapping.setServiceProviderID(PROVIDER_ID);
        mapping.setServiceID(SERVICE_ID);
        mapping.setStateID(STATE_ID);
        mapping.setcTI_CampaignName("104");
        return mapping;
    }

    @Test
    @DisplayName("getProviderDetailsById should hand back what the repository holds")
    void getProviderDetailsById_shouldHandBackRepositoryContents() {
        M_Serviceprovider_Blocking stored = new M_Serviceprovider_Blocking();
        when(mServiceproviderBlockingRepo.getProviderDetailsByID(PROVIDER_ID)).thenReturn(stored);

        assertSame(stored, service.getProviderDetailsById(PROVIDER_ID));
    }

    @Test
    @DisplayName("blockServiceProvider should answer the provider the repository stored")
    void blockServiceProvider_shouldAnswerStoredProvider() {
        M_Serviceprovider_Blocking stored = new M_Serviceprovider_Blocking();
        when(mServiceproviderBlockingRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.blockServiceProvider(stored));
    }

    @Test
    @DisplayName("saveData should answer the audit row the repository stored")
    void saveData_shouldAnswerStoredAuditRow() {
        T_Serviceproviderdetail stored = new T_Serviceproviderdetail();
        when(t_ServiceproviderdetailRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.saveData(stored));
    }

    @Test
    @DisplayName("the status updates should each reach their own repository query")
    void statusUpdates_shouldReachTheirOwnQuery() {
        service.blockProviderByService(PROVIDER_ID, STATE_ID, SERVICE_ID, 2);
        service.blockProviderByState(PROVIDER_ID, STATE_ID, 2);
        service.blockProvider(PROVIDER_ID, 2);
        service.blockProviderByProviderIdAndServiceId(PROVIDER_ID, SERVICE_ID, 2);
        service.blockUser(3117, 2);

        verify(mProviderservicemappingBlockingRepo).blockProviderByService(PROVIDER_ID, STATE_ID, SERVICE_ID, 2);
        verify(mProviderservicemappingBlockingRepo).blockProviderByState(PROVIDER_ID, STATE_ID, 2);
        verify(mProviderservicemappingBlockingRepo).blockProvider(PROVIDER_ID, 2);
        verify(mProviderservicemappingBlockingRepo)
                .blockProviderByProviderIdAndServiceId(PROVIDER_ID, SERVICE_ID, 2);
        verify(userBlockingRepo).blockUser(3117, 2);
    }

    @Test
    @DisplayName("the mapping lookups should each reach their own repository query")
    void mappingLookups_shouldReachTheirOwnQuery() {
        M_Providerservicemapping_Blocking stored = mapping(4001);
        ArrayList<M_Providerservicemapping_Blocking> storedList = new ArrayList<>(List.of(stored));
        List<M_Providerservicemapping_Blocking> stateList = List.of(stored);
        when(mProviderservicemappingBlockingRepo.getProviderServiceMappingDetails(PROVIDER_ID, STATE_ID, SERVICE_ID))
                .thenReturn(stored);
        when(mProviderservicemappingBlockingRepo.getProviderStateMappingDetails(PROVIDER_ID, STATE_ID))
                .thenReturn(stateList);
        when(mProviderservicemappingBlockingRepo.getProviderStatus(PROVIDER_ID)).thenReturn(storedList);
        when(mProviderservicemappingBlockingRepo.getProviderStatusByProviderAndServiceId(PROVIDER_ID, SERVICE_ID))
                .thenReturn(storedList);
        when(mProviderservicemappingBlockingRepo.findByProviderServiceMapID(4001)).thenReturn(stored);
        when(mProviderservicemappingBlockingRepo.save(stored)).thenReturn(stored);
        when(mProviderservicemappingBlockingRepo.saveAll(anyList())).thenReturn(storedList);

        assertSame(stored, service.getProviderServiceMappingDetails(PROVIDER_ID, STATE_ID, SERVICE_ID));
        assertSame(stateList, service.getProviderStateMappingDetails(PROVIDER_ID, STATE_ID));
        assertSame(storedList, service.getProviderStatus(PROVIDER_ID));
        assertSame(storedList, service.getProviderStatusByProviderAndServiceId(PROVIDER_ID, SERVICE_ID));
        assertSame(stored, service.getDataByProviderServiceMapId(4001));
        assertSame(stored, service.updateProviderData(stored));
        assertSame(storedList, service.AddServiceProvider(new ArrayList<>()));
    }

    @Test
    @DisplayName("the view lookups should each reach their own repository query")
    void viewLookups_shouldReachTheirOwnQuery() {
        ArrayList<V_Showproviderservicemapping> stored = new ArrayList<>(List.of(new V_Showproviderservicemapping()));
        when(v_ShowproviderservicemappingRepo.getProviderStatus(PROVIDER_ID)).thenReturn(stored);
        when(v_ShowproviderservicemappingRepo.getProviderStatus1(PROVIDER_ID)).thenReturn(stored);
        when(v_ShowproviderservicemappingRepo.getProviderServiceMappingDetails1(PROVIDER_ID, STATE_ID, SERVICE_ID))
                .thenReturn(stored);
        when(v_ShowproviderservicemappingRepo.getProviderStateMappingDetails(PROVIDER_ID, STATE_ID))
                .thenReturn(stored);
        when(v_ShowproviderservicemappingRepo.getProviderStatusByProviderAndServiceId(PROVIDER_ID, SERVICE_ID))
                .thenReturn(stored);

        assertSame(stored, service.getProviderStatus1(PROVIDER_ID));
        assertSame(stored, service.getProviderStatus2(PROVIDER_ID));
        assertSame(stored, service.getProviderServiceMappingDetails2(PROVIDER_ID, STATE_ID, SERVICE_ID));
        assertSame(stored, service.getProviderStateMappingDetails1(PROVIDER_ID, STATE_ID));
        assertSame(stored, service.getProviderStatusByProviderAndServiceId2(PROVIDER_ID, SERVICE_ID));
    }

    @Test
    @DisplayName("the audit writes should each reach their own repository")
    void auditWrites_shouldReachTheirOwnRepository() {
        T_Providerservicemappingdetail detail = new T_Providerservicemappingdetail();
        ArrayList<T_Providerservicemappingdetail> details = new ArrayList<>(List.of(detail));
        T_Userdetail userDetail = new T_Userdetail();
        when(t_ProviderservicemappingdetailRepo.save(detail)).thenReturn(detail);
        when(t_ProviderservicemappingdetailRepo.saveAll(anyList())).thenReturn(details);
        when(t_UserDetailRepo.save(userDetail)).thenReturn(userDetail);

        assertSame(detail, service.savetpsdData(detail));
        assertSame(details, service.savetpsmd(new ArrayList<>()));
        assertSame(userDetail, service.saveUserDetails(userDetail));
    }

    @Test
    @DisplayName("getUserDetailByUserId and getStatusData should hand back what the repositories hold")
    void userLookups_shouldHandBackRepositoryContents() {
        UserForBlocking user = new UserForBlocking();
        ArrayList<M_Status1> statuses = new ArrayList<>(List.of(new M_Status1()));
        when(userBlockingRepo.getUserDetailByUserId(3117)).thenReturn(user);
        when(mStatusRepo.getStatusData()).thenReturn(statuses);

        assertSame(user, service.getUserDetailByUserId(3117));
        assertSame(statuses, service.getStatusData());
    }

    @Test
    @DisplayName("getServiceLiensUsingProvider should skip a row the query could not fill")
    void getServiceLiensUsingProvider_shouldSkipUnfillableRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new Object[] { 4001, PROVIDER_ID, SERVICE_ID, "Tele Medicine", Boolean.FALSE });
        when(mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider(PROVIDER_ID)).thenReturn(rows);

        assertEquals(1, service.getServiceLiensUsingProvider(PROVIDER_ID).size());
    }

    @Test
    @DisplayName("mapctidata should report a clean run when every mapping is written")
    void mapctidata_shouldReportCleanRun() {
        when(mProviderservicemappingBlockingRepo.createcitmapping(anyInt(), any())).thenReturn(1);

        assertEquals("Mapping Successful", service.mapctidata(List.of(mapping(4001), mapping(4002))));
    }

    @Test
    @DisplayName("mapctidata should report how far it got when a mapping is rejected")
    void mapctidata_shouldReportHowFarItGot() {
        when(mProviderservicemappingBlockingRepo.createcitmapping(4001, "104")).thenReturn(1);
        when(mProviderservicemappingBlockingRepo.createcitmapping(4002, "104")).thenReturn(0);

        String status = service.mapctidata(List.of(mapping(4001), mapping(4002)));

        assertTrue(status.startsWith("Mapping Failed"), status);
        assertTrue(status.contains("after 1 entries"), status);
    }

    @Test
    @DisplayName("getServiceLiensUsingProvider1 should narrow by provider, service and state when all are named")
    void getServiceLiensUsingProvider1_shouldNarrowByAllThree() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { 4001, PROVIDER_ID, SERVICE_ID, "Tele Medicine", STATE_ID, "Karnataka", "104",
                Boolean.FALSE, "N", Boolean.FALSE });
        when(mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider1(PROVIDER_ID, SERVICE_ID, STATE_ID))
                .thenReturn(rows);

        assertEquals(1, service.getServiceLiensUsingProvider1(mapping(4001)).size());
    }

    @Test
    @DisplayName("getServiceLiensUsingProvider1 should narrow by provider and service when no state is named")
    void getServiceLiensUsingProvider1_shouldNarrowByProviderAndService() {
        M_Providerservicemapping_Blocking request = mapping(4001);
        request.setStateID(null);
        when(mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider1(PROVIDER_ID, SERVICE_ID))
                .thenReturn(new ArrayList<>());

        service.getServiceLiensUsingProvider1(request);

        verify(mProviderservicemappingBlockingRepo).getServiceLiensUsingProvider1(PROVIDER_ID, SERVICE_ID);
    }

    @Test
    @DisplayName("getServiceLiensUsingProvider1 should narrow by provider alone when no service is named")
    void getServiceLiensUsingProvider1_shouldNarrowByProviderAlone() {
        M_Providerservicemapping_Blocking request = mapping(4001);
        request.setStateID(null);
        request.setServiceID(null);
        when(mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider1(PROVIDER_ID))
                .thenReturn(new ArrayList<>());

        service.getServiceLiensUsingProvider1(request);

        verify(mProviderservicemappingBlockingRepo).getServiceLiensUsingProvider1(PROVIDER_ID);
    }

    @Test
    @DisplayName("getServiceLiensUsingProvider1 should answer every mapping when the request narrows nothing")
    void getServiceLiensUsingProvider1_shouldAnswerEveryMapping() {
        M_Providerservicemapping_Blocking request = new M_Providerservicemapping_Blocking();
        when(mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider1()).thenReturn(new ArrayList<>());

        service.getServiceLiensUsingProvider1(request);

        verify(mProviderservicemappingBlockingRepo).getServiceLiensUsingProvider1();
    }
}
