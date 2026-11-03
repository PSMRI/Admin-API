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
package com.iemr.admin.controller.blocking;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import com.iemr.admin.service.blocking.BlockingInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The blocking endpoints suspend and reinstate a provider - whole, per service
 * line, per state, or one user at a time - and write an audit row for each
 * change of status.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("BlockingController Test Suite")
class BlockingControllerTest {

    private static final Integer PROVIDER_ID = 77;
    private static final Integer SERVICE_ID = 3;
    private static final Integer STATE_ID = 29;

    @Mock
    private BlockingInter blockingInter;

    @InjectMocks
    private BlockingController controller;

    private static M_Providerservicemapping_Blocking mapping(Integer mapId) {
        M_Providerservicemapping_Blocking mapping = new M_Providerservicemapping_Blocking();
        mapping.setProviderServiceMapID(mapId);
        mapping.setServiceProviderID(PROVIDER_ID);
        mapping.setServiceID(SERVICE_ID);
        mapping.setStateID(STATE_ID);
        mapping.setStatusID(1);
        return mapping;
    }

    private static V_Showproviderservicemapping view(Integer mapId) {
        V_Showproviderservicemapping view = new V_Showproviderservicemapping();
        view.setProviderServiceMapID(mapId);
        view.setServiceProviderID(PROVIDER_ID);
        return view;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    private static void assertCodeException(String response) {
        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(response), response);
    }

    private static void assertGenericFailure(String response) {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("blockProvider1 should record the previous status before the provider is blocked")
    void blockProvider1_shouldRecordPreviousStatus() {
        M_Serviceprovider_Blocking stored = new M_Serviceprovider_Blocking();
        stored.setServiceProviderID(PROVIDER_ID);
        stored.setServiceProviderName("Piramal Swasthya");
        stored.setStatusID(1);
        T_Serviceproviderdetail saved = new T_Serviceproviderdetail();
        saved.setServiceProviderDetailID(9001);
        when(blockingInter.getProviderDetailsById(PROVIDER_ID)).thenReturn(stored);
        when(blockingInter.saveData(any())).thenReturn(saved);

        String response = controller.blockProvider1(
                "{\"serviceProviderID\":77,\"statusID\":2,\"reason\":\"contract ended\"}");

        assertSuccessContaining(response, "9001");

        ArgumentCaptor<T_Serviceproviderdetail> captor = ArgumentCaptor.forClass(T_Serviceproviderdetail.class);
        verify(blockingInter).saveData(captor.capture());
        assertEquals(1, captor.getValue().getPreviousStatusID());
        assertEquals(2, captor.getValue().getUpdatedStatusID());
        assertEquals("contract ended", captor.getValue().getReason());
        assertEquals(2, stored.getStatusID(), "the provider must carry the new status when it is saved");
        verify(blockingInter).blockServiceProvider(stored);
    }

    @Test
    @DisplayName("blockProvider1 should answer an error envelope for a provider that does not exist")
    void blockProvider1_shouldAnswerErrorEnvelopeForUnknownProvider() {
        when(blockingInter.getProviderDetailsById(PROVIDER_ID)).thenReturn(null);

        assertCodeException(controller.blockProvider1("{\"serviceProviderID\":77,\"statusID\":2}"));
    }

    @Test
    @DisplayName("blockProvider should write one audit row per service mapping it blocks")
    void blockProvider_shouldWriteOneAuditRowPerMapping() {
        when(blockingInter.getProviderStatus(PROVIDER_ID))
                .thenReturn(new ArrayList<>(List.of(mapping(4001), mapping(4002))));
        T_Providerservicemappingdetail saved = new T_Providerservicemappingdetail();
        saved.setProviderServiceMapID(4001);
        when(blockingInter.savetpsmd(anyList())).thenReturn(new ArrayList<>(List.of(saved)));

        String response = controller.blockProvider(
                "{\"serviceProviderID\":77,\"statusID\":2,\"reason\":\"contract ended\"}");

        assertSuccessContaining(response, "4001");
        verify(blockingInter).blockProvider(PROVIDER_ID, 2);

        ArgumentCaptor<List<T_Providerservicemappingdetail>> captor = ArgumentCaptor.forClass(List.class);
        verify(blockingInter).savetpsmd(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals("contract ended", captor.getValue().get(0).getReason());
    }

    @Test
    @DisplayName("blockProvider should answer an error envelope when the mappings cannot be read")
    void blockProvider_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderStatus(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.blockProvider("{\"serviceProviderID\":77,\"statusID\":2}"));
    }

    @Test
    @DisplayName("blockProviderByServiceId should block only the service line the caller names")
    void blockProviderByServiceId_shouldBlockOnlyNamedServiceLine() {
        when(blockingInter.getProviderStatusByProviderAndServiceId(PROVIDER_ID, SERVICE_ID))
                .thenReturn(new ArrayList<>(List.of(mapping(4001))));
        T_Providerservicemappingdetail saved = new T_Providerservicemappingdetail();
        saved.setProviderServiceMapID(4001);
        when(blockingInter.savetpsmd(anyList())).thenReturn(new ArrayList<>(List.of(saved)));

        assertSuccessContaining(controller.blockProviderByServiceId(
                "{\"serviceProviderID\":77,\"serviceID\":3,\"statusID\":2}"), "4001");
        verify(blockingInter).blockProviderByProviderIdAndServiceId(PROVIDER_ID, SERVICE_ID, 2);
    }

    @Test
    @DisplayName("blockProviderByServiceId should answer an error envelope when the mappings cannot be read")
    void blockProviderByServiceId_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderStatusByProviderAndServiceId(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(
                controller.blockProviderByServiceId("{\"serviceProviderID\":77,\"serviceID\":3,\"statusID\":2}"));
    }

    @Test
    @DisplayName("getProviderStatus should answer the mapping view for the provider")
    void getProviderStatus_shouldAnswerMappingView() {
        when(blockingInter.getProviderStatus1(PROVIDER_ID)).thenReturn(new ArrayList<>(List.of(view(4001))));

        assertSuccessContaining(controller.getProviderStatus("{\"serviceProviderID\":77}"), "4001");
    }

    @Test
    @DisplayName("getProviderStatus should answer an error envelope when the view cannot be read")
    void getProviderStatus_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderStatus1(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getProviderStatus("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getProviderStatus1 should answer the second mapping view for the provider")
    void getProviderStatus1_shouldAnswerSecondView() {
        when(blockingInter.getProviderStatus2(PROVIDER_ID)).thenReturn(new ArrayList<>(List.of(view(4001))));

        assertSuccessContaining(controller.getProviderStatus1("{\"serviceProviderID\":77}"), "4001");
    }

    @Test
    @DisplayName("getProviderStatus1 should answer an error envelope when the view cannot be read")
    void getProviderStatus1_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderStatus2(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getProviderStatus1("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getServiceLiensUsingProvider should answer the service lines the provider runs")
    void getServiceLiensUsingProvider_shouldAnswerServiceLines() {
        when(blockingInter.getServiceLiensUsingProvider(PROVIDER_ID))
                .thenReturn(new ArrayList<>(List.of(mapping(4001))));

        assertSuccessContaining(controller.getServiceLiensUsingProvider("{\"serviceProviderID\":77}"), "4001");
    }

    @Test
    @DisplayName("getServiceLiensUsingProvider should answer an error envelope when the lookup fails")
    void getServiceLiensUsingProvider_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getServiceLiensUsingProvider(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getServiceLiensUsingProvider("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getProviderStatusByProviderAndServiceId should narrow the view to the service line")
    void getProviderStatusByProviderAndServiceId_shouldNarrowToServiceLine() {
        when(blockingInter.getProviderStatusByProviderAndServiceId2(PROVIDER_ID, SERVICE_ID))
                .thenReturn(new ArrayList<>(List.of(view(4001))));

        assertSuccessContaining(controller.getProviderStatusByProviderAndServiceId(
                "{\"serviceProviderID\":77,\"serviceID\":3}"), "4001");
    }

    @Test
    @DisplayName("getProviderStatusByProviderAndServiceId should answer an error envelope when the lookup fails")
    void getProviderStatusByProviderAndServiceId_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderStatusByProviderAndServiceId2(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller
                .getProviderStatusByProviderAndServiceId("{\"serviceProviderID\":77,\"serviceID\":3}"));
    }

    @Test
    @DisplayName("blockProviderByService should record the previous status of the one mapping it blocks")
    void blockProviderByService_shouldRecordPreviousStatus() {
        M_Providerservicemapping_Blocking stored = mapping(4001);
        T_Providerservicemappingdetail saved = new T_Providerservicemappingdetail();
        saved.setProviderServiceMapID(4001);
        when(blockingInter.getProviderServiceMappingDetails(PROVIDER_ID, STATE_ID, SERVICE_ID)).thenReturn(stored);
        when(blockingInter.savetpsdData(any())).thenReturn(saved);

        assertSuccessContaining(controller.blockProviderByService("{\"serviceProviderID\":77,\"stateID\":29,"
                + "\"serviceID\":3,\"statusID\":2,\"reason\":\"suspended\"}"), "4001");
        verify(blockingInter).blockProviderByService(PROVIDER_ID, STATE_ID, SERVICE_ID, 2);
    }

    @Test
    @DisplayName("blockProviderByService should answer an error envelope for a mapping that does not exist")
    void blockProviderByService_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(blockingInter.getProviderServiceMappingDetails(any(), any(), any())).thenReturn(null);

        assertCodeException(controller.blockProviderByService(
                "{\"serviceProviderID\":77,\"stateID\":29,\"serviceID\":3,\"statusID\":2}"));
    }

    @Test
    @DisplayName("getProviderStatusByService should answer the view for the service line in the state")
    void getProviderStatusByService_shouldAnswerViewForServiceInState() {
        when(blockingInter.getProviderServiceMappingDetails2(PROVIDER_ID, STATE_ID, SERVICE_ID))
                .thenReturn(new ArrayList<>(List.of(view(4001))));

        assertSuccessContaining(controller.getProviderStatusByService(
                "{\"serviceProviderID\":77,\"stateID\":29,\"serviceID\":3}"), "4001");
    }

    @Test
    @DisplayName("getProviderStatusByService should answer an error envelope when the lookup fails")
    void getProviderStatusByService_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderServiceMappingDetails2(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getProviderStatusByService("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("blockProviderByState should write one audit row per mapping in the state")
    void blockProviderByState_shouldWriteOneAuditRowPerMapping() {
        when(blockingInter.getProviderStateMappingDetails(PROVIDER_ID, STATE_ID))
                .thenReturn(List.of(mapping(4001), mapping(4002)));
        T_Providerservicemappingdetail saved = new T_Providerservicemappingdetail();
        saved.setProviderServiceMapID(4001);
        when(blockingInter.savetpsmd(anyList())).thenReturn(new ArrayList<>(List.of(saved)));

        assertSuccessContaining(controller.blockProviderByState(
                "{\"serviceProviderID\":77,\"stateID\":29,\"statusID\":2,\"reason\":\"suspended\"}"), "4001");
        verify(blockingInter).blockProviderByState(PROVIDER_ID, STATE_ID, 2);
    }

    @Test
    @DisplayName("blockProviderByState should answer an error envelope when the mappings cannot be read")
    void blockProviderByState_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderStateMappingDetails(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(
                controller.blockProviderByState("{\"serviceProviderID\":77,\"stateID\":29,\"statusID\":2}"));
    }

    @Test
    @DisplayName("getProviderStatusByState should answer the view for the state")
    void getProviderStatusByState_shouldAnswerViewForState() {
        when(blockingInter.getProviderStateMappingDetails1(PROVIDER_ID, STATE_ID))
                .thenReturn(new ArrayList<>(List.of(view(4001))));

        assertSuccessContaining(
                controller.getProviderStatusByState("{\"serviceProviderID\":77,\"stateID\":29}"), "4001");
    }

    @Test
    @DisplayName("getProviderStatusByState should answer an error envelope when the lookup fails")
    void getProviderStatusByState_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getProviderStateMappingDetails1(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getProviderStatusByState("{\"serviceProviderID\":77,\"stateID\":29}"));
    }

    @Test
    @DisplayName("blockUser should record the user's previous credentials before the block takes effect")
    void blockUser_shouldRecordPreviousCredentials() {
        UserForBlocking stored = new UserForBlocking();
        stored.setUserID(3117);
        stored.setUserName("asha.rao");
        stored.setPassword("old-secret");
        stored.setStatusID(1);
        when(blockingInter.getUserDetailByUserId(3117)).thenReturn(stored);

        String response = controller.blockUser("{\"userID\":3117,\"statusID\":2,"
                + "\"updatedPassword\":\"new-secret\",\"updatedStatusID\":2}");

        assertSuccessContaining(response, "jai");
        verify(blockingInter).blockUser(3117, 2);

        ArgumentCaptor<T_Userdetail> captor = ArgumentCaptor.forClass(T_Userdetail.class);
        verify(blockingInter).saveUserDetails(captor.capture());
        assertEquals("old-secret", captor.getValue().getPreviousPassword());
        assertEquals("new-secret", captor.getValue().getUpdatedPassword());
        assertEquals(1, captor.getValue().getPreviousStatusID());
    }

    @Test
    @DisplayName("blockUser should answer an error envelope for a user that does not exist")
    void blockUser_shouldAnswerErrorEnvelopeForUnknownUser() {
        when(blockingInter.getUserDetailByUserId(3117)).thenReturn(null);

        assertCodeException(controller.blockUser("{\"userID\":3117,\"statusID\":2}"));
    }

    @Test
    @DisplayName("getStatus should answer every status on record")
    void getStatus_shouldAnswerEveryStatus() {
        M_Status1 status = new M_Status1();
        status.setStatusID(1);
        status.setStatus("Active");
        when(blockingInter.getStatusData()).thenReturn(new ArrayList<>(List.of(status)));

        assertSuccessContaining(controller.getStatus("{}"), "Active");
    }

    @Test
    @DisplayName("getStatus should answer an error envelope when the lookup fails")
    void getStatus_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getStatusData()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getStatus("{}"));
    }

    @Test
    @DisplayName("ProviderStateAndServiceLines should create one mapping per state the request names")
    void providerStateAndServiceLines_shouldCreateOneMappingPerState() {
        when(blockingInter.AddServiceProvider(anyList()))
                .thenReturn(new ArrayList<>(List.of(mapping(4001))));

        String response = controller.ProviderStateAndServiceLines("[{\"serviceProviderID\":77,\"serviceID\":3,"
                + "\"createdBy\":\"admin\",\"statusID\":1,\"stateID1\":[29,30]}]");

        assertSuccessContaining(response, "4001");

        ArgumentCaptor<List<M_Providerservicemapping_Blocking>> captor = ArgumentCaptor.forClass(List.class);
        verify(blockingInter).AddServiceProvider(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(29, captor.getValue().get(0).getStateID());
    }

    @Test
    @DisplayName("ProviderStateAndServiceLines should fall back to the state on the record when none is listed")
    void providerStateAndServiceLines_shouldFallBackToRecordState() {
        when(blockingInter.AddServiceProvider(anyList())).thenReturn(new ArrayList<>());

        controller.ProviderStateAndServiceLines("[{\"serviceProviderID\":77,\"serviceID\":3,"
                + "\"stateID\":29,\"createdBy\":\"admin\",\"statusID\":1,\"stateID1\":[]}]");

        ArgumentCaptor<List<M_Providerservicemapping_Blocking>> captor = ArgumentCaptor.forClass(List.class);
        verify(blockingInter).AddServiceProvider(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(29, captor.getValue().get(0).getStateID());
    }

    @Test
    @DisplayName("ProviderStateAndServiceLines should answer an error envelope when no states are named")
    void providerStateAndServiceLines_shouldAnswerErrorEnvelopeWithoutStates() {
        assertCodeException(controller.ProviderStateAndServiceLines("[{\"serviceProviderID\":77}]"));
    }

    @Test
    @DisplayName("deleteProviderStateAndServiceLines should build one record per service line named")
    void deleteProviderStateAndServiceLines_shouldBuildOneRecordPerService() {
        when(blockingInter.AddServiceProvider(anyList())).thenReturn(new ArrayList<>(List.of(mapping(4001))));

        String response = controller.deleteProviderStateAndServiceLines("{\"serviceProviderID\":77,"
                + "\"stateID\":29,\"createdBy\":\"admin\",\"statusID\":1,\"serviceID1\":[3,4]}");

        assertSuccessContaining(response, "4001");

        ArgumentCaptor<List<M_Providerservicemapping_Blocking>> captor = ArgumentCaptor.forClass(List.class);
        verify(blockingInter).AddServiceProvider(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    @Test
    @DisplayName("deleteProviderStateAndServiceLines should answer an error envelope when no services are named")
    void deleteProviderStateAndServiceLines_shouldAnswerErrorEnvelopeWithoutServices() {
        assertCodeException(controller.deleteProviderStateAndServiceLines("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("createCitMappingwithServiceLines should answer the summary the service reports")
    void createCitMappingwithServiceLines_shouldAnswerSummary() {
        when(blockingInter.mapctidata(anyList())).thenReturn("2 campaigns mapped");

        assertSuccessContaining(controller.createCitMappingwithServiceLines(
                "[{\"providerServiceMapID\":4001,\"cTI_CampaignName\":\"104\"}]"), "2 campaigns mapped");
    }

    @Test
    @DisplayName("createCitMappingwithServiceLines should answer an error envelope when the mapping fails")
    void createCitMappingwithServiceLines_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.mapctidata(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createCitMappingwithServiceLines("[{\"providerServiceMapID\":4001}]"));
    }

    @Test
    @DisplayName("getMappedServiceLinesAndStatetoProvider should answer the mappings the service resolves")
    void getMappedServiceLines_shouldAnswerResolvedMappings() {
        when(blockingInter.getServiceLiensUsingProvider1(any()))
                .thenReturn(new ArrayList<>(List.of(mapping(4001))));

        assertSuccessContaining(
                controller.getMappedServiceLinesAndStatetoProvider("{\"serviceProviderID\":77}"), "4001");
    }

    @Test
    @DisplayName("getMappedServiceLinesAndStatetoProvider should answer an error envelope when the lookup fails")
    void getMappedServiceLines_shouldAnswerErrorEnvelopeOnFailure() {
        when(blockingInter.getServiceLiensUsingProvider1(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getMappedServiceLinesAndStatetoProvider("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("mapProviderAndServiceLines should mark every new mapping active")
    void mapProviderAndServiceLines_shouldMarkNewMappingsActive() {
        when(blockingInter.AddServiceProvider(anyList())).thenReturn(new ArrayList<>(List.of(mapping(4001))));

        String response = controller.mapProviderAndServiceLines("[{\"serviceProviderID\":77,\"serviceID\":3,"
                + "\"createdBy\":\"admin\",\"stateID1\":[29,30]}]");

        assertSuccessContaining(response, "4001");

        ArgumentCaptor<List<M_Providerservicemapping_Blocking>> captor = ArgumentCaptor.forClass(List.class);
        verify(blockingInter).AddServiceProvider(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(1, captor.getValue().get(0).getStatusID(), "a new mapping starts active");
    }

    @Test
    @DisplayName("mapProviderAndServiceLines should fall back to the state on the record when none is listed")
    void mapProviderAndServiceLines_shouldFallBackToRecordState() {
        when(blockingInter.AddServiceProvider(anyList())).thenReturn(new ArrayList<>());

        controller.mapProviderAndServiceLines("[{\"serviceProviderID\":77,\"serviceID\":3,"
                + "\"stateID\":29,\"createdBy\":\"admin\",\"stateID1\":[]}]");

        ArgumentCaptor<List<M_Providerservicemapping_Blocking>> captor = ArgumentCaptor.forClass(List.class);
        verify(blockingInter).AddServiceProvider(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @Test
    @DisplayName("mapProviderAndServiceLines should answer an error envelope when no states are named")
    void mapProviderAndServiceLines_shouldAnswerErrorEnvelopeWithoutStates() {
        assertCodeException(controller.mapProviderAndServiceLines("[{\"serviceProviderID\":77}]"));
    }

    @Test
    @DisplayName("editMappedServiceLinesAndStatetoProvider should copy the edits onto the stored mapping")
    void editMappedServiceLines_shouldCopyEdits() {
        M_Providerservicemapping_Blocking stored = mapping(4001);
        when(blockingInter.getDataByProviderServiceMapId(4001)).thenReturn(stored);
        when(blockingInter.updateProviderData(stored)).thenReturn(stored);

        String response = controller.editMappedServiceLinesAndStatetoProvider("{\"providerServiceMapID\":4001,"
                + "\"serviceProviderID\":78,\"serviceID\":4,\"stateID\":30,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "4001");
        assertEquals(78, stored.getServiceProviderID());
        assertEquals(30, stored.getStateID());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("editMappedServiceLinesAndStatetoProvider should answer an error envelope for an unknown mapping")
    void editMappedServiceLines_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(blockingInter.getDataByProviderServiceMapId(4001)).thenReturn(null);

        assertCodeException(
                controller.editMappedServiceLinesAndStatetoProvider("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("deleteMappedServiceLinesAndStatetoProvider should mark the mapping deleted")
    void deleteMappedServiceLines_shouldMarkMappingDeleted() {
        M_Providerservicemapping_Blocking stored = mapping(4001);
        when(blockingInter.getDataByProviderServiceMapId(4001)).thenReturn(stored);
        when(blockingInter.updateProviderData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteMappedServiceLinesAndStatetoProvider(
                "{\"providerServiceMapID\":4001,\"deleted\":true}"), "4001");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteMappedServiceLinesAndStatetoProvider should answer an error envelope for an unknown mapping")
    void deleteMappedServiceLines_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(blockingInter.getDataByProviderServiceMapId(4001)).thenReturn(null);

        assertCodeException(controller.deleteMappedServiceLinesAndStatetoProvider(
                "{\"providerServiceMapID\":4001,\"deleted\":true}"));
    }
}
