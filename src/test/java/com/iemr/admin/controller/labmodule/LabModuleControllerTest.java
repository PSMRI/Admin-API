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
package com.iemr.admin.controller.labmodule;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.sevice.labmodule.MastersCreationServiceImpl;
import com.iemr.admin.sevice.labmodule.MastersFetchingServiceImpl;
import com.iemr.admin.sevice.labmodule.MastersMappingServiceImpl;
import com.iemr.admin.sevice.labmodule.MastersStatusUpdateImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The lab module endpoints let a provider admin define the diagnostic tests and
 * their result components, and refuse a request that does not name what to act
 * on.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LabModuleController Test Suite")
class LabModuleControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer PROCEDURE_ID = 71;
    private static final Integer COMPONENT_ID = 81;
    private static final String PROCEDURE_JSON = "{\"procedureID\":71,\"procedureName\":\"Haemoglobin\"}";

    @Mock
    private MastersCreationServiceImpl mastersCreationServiceImpl;

    @Mock
    private MastersMappingServiceImpl mastersMappingServiceImpl;

    @Mock
    private MastersFetchingServiceImpl mastersFetchingServiceImpl;

    @Mock
    private MastersStatusUpdateImpl mastersStatusUpdateImpl;

    private LabModuleController controller;

    @BeforeEach
    void setUp() {
        controller = new LabModuleController();
        controller.setMastersCreationServiceImpl(mastersCreationServiceImpl);
        controller.setMastersMappingServiceImpl(mastersMappingServiceImpl);
        controller.setMastersFetchingServiceImpl(mastersFetchingServiceImpl);
        controller.setMastersStatusUpdateImpl(mastersStatusUpdateImpl);
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    private static void assertInvalidRequest(String response) {
        assertEquals(OutputResponse.USERID_FAILURE, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("createProcedureMaster should answer the procedure the service stored")
    void createProcedureMaster_shouldAnswerStoredProcedure() throws Exception {
        when(mastersCreationServiceImpl.createProcedureMaster(anyString())).thenReturn(PROCEDURE_JSON);

        assertSuccessContaining(controller.createProcedureMaster(PROCEDURE_JSON), "Haemoglobin");
    }

    @Test
    @DisplayName("createProcedureMaster should stay at its default when the service stored nothing")
    void createProcedureMaster_shouldStayAtDefaultWhenNothingStored() throws Exception {
        when(mastersCreationServiceImpl.createProcedureMaster(anyString())).thenReturn(null);

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.createProcedureMaster("{}")));
    }

    @Test
    @DisplayName("createProcedureMaster should answer an error envelope when the store fails")
    void createProcedureMaster_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(mastersCreationServiceImpl.createProcedureMaster(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.createProcedureMaster("{}")));
    }

    @Test
    @DisplayName("createComponentMaster should answer the component the service stored")
    void createComponentMaster_shouldAnswerStoredComponent() throws Exception {
        when(mastersCreationServiceImpl.createComponentMaster(anyString()))
                .thenReturn("{\"testComponentName\":\"Haemoglobin count\"}");

        assertSuccessContaining(controller.createComponentMaster("{}"), "Haemoglobin count");
    }

    @Test
    @DisplayName("createComponentMaster should answer an error envelope when the store fails")
    void createComponentMaster_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(mastersCreationServiceImpl.createComponentMaster(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.createComponentMaster("{}")));
    }

    @Test
    @DisplayName("createProcedureComponentMapping should answer the mapping the service stored")
    void createMapping_shouldAnswerStoredMapping() throws Exception {
        when(mastersMappingServiceImpl.createProcedureComponentMapping(anyString()))
                .thenReturn("[{\"procedureName\":\"Haemoglobin\"}]");

        assertSuccessContaining(controller.createProcedureComponentMapping("{}"), "Haemoglobin");
    }

    @Test
    @DisplayName("createProcedureComponentMapping should refuse a request that maps no components")
    void createMapping_shouldRefuseEmptyRequest() throws Exception {
        when(mastersMappingServiceImpl.createProcedureComponentMapping(anyString())).thenReturn("1");

        String response = controller.createProcedureComponentMapping("{}");

        assertInvalidRequest(response);
        assertTrue(response.contains("Invalid request."), response);
    }

    @Test
    @DisplayName("createProcedureComponentMapping should report a mapping the service could not store")
    void createMapping_shouldReportUnstoredMapping() throws Exception {
        when(mastersMappingServiceImpl.createProcedureComponentMapping(anyString())).thenReturn(null);

        String response = controller.createProcedureComponentMapping("{}");

        assertInvalidRequest(response);
        assertTrue(response.contains("Error while saving the data"), response);
    }

    @Test
    @DisplayName("createProcedureComponentMapping should answer an error envelope when the store fails")
    void createMapping_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(mastersMappingServiceImpl.createProcedureComponentMapping(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.createProcedureComponentMapping("{}")));
    }

    @Test
    @DisplayName("the fetch endpoints should each answer what their own service publishes")
    void fetchEndpoints_shouldAnswerTheirOwnService() throws Exception {
        when(mastersFetchingServiceImpl.getProcedureMaster(PSM_ID)).thenReturn("[{\"a\":1}]");
        when(mastersFetchingServiceImpl.getComponentMaster(PSM_ID)).thenReturn("[{\"b\":2}]");
        when(mastersFetchingServiceImpl.getProcedureMasterDelFalse(PSM_ID)).thenReturn("[{\"c\":3}]");
        when(mastersFetchingServiceImpl.getComponentMasterDelFalse(PSM_ID)).thenReturn("[{\"d\":4}]");
        when(mastersFetchingServiceImpl.getProcCompMappingDelFalse(PSM_ID)).thenReturn("[{\"e\":5}]");
        when(mastersFetchingServiceImpl.getProcCompMappingForProcedureID(PROCEDURE_ID)).thenReturn("[{\"f\":6}]");

        assertSuccessContaining(controller.fetchProcedureMaster(PSM_ID), "\"a\":1");
        assertSuccessContaining(controller.fetchComponentMaster(PSM_ID), "\"b\":2");
        assertSuccessContaining(controller.fetchProcedureMasterDelFalse(PSM_ID), "\"c\":3");
        assertSuccessContaining(controller.fetchComponentMasterDelFalse(PSM_ID), "\"d\":4");
        assertSuccessContaining(controller.fetchProcCompMappingDelFalse(PSM_ID), "\"e\":5");
        assertSuccessContaining(controller.fetchProcCompMappingForSingleProcedure(PROCEDURE_ID), "\"f\":6");
    }

    @Test
    @DisplayName("the fetch endpoints should refuse a request that names no record to read")
    void fetchEndpoints_shouldRefuseRequestWithoutRecord() throws Exception {
        assertInvalidRequest(controller.fetchProcedureMaster(0));
        assertInvalidRequest(controller.fetchComponentMaster(0));
        assertInvalidRequest(controller.fetchProcedureMasterDelFalse(0));
        assertInvalidRequest(controller.fetchComponentMasterDelFalse(0));
        assertInvalidRequest(controller.fetchProcCompMappingDelFalse(0));
        assertInvalidRequest(controller.fetchProcCompMappingForSingleProcedure(0));
        assertInvalidRequest(controller.fetchComponentDetailsForComponentID(0));
        verify(mastersFetchingServiceImpl, never()).getProcedureMaster(anyInt());
    }

    @Test
    @DisplayName("fetchProcedureMaster should answer an error envelope when the lookup fails")
    void fetchProcedureMaster_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(mastersFetchingServiceImpl.getProcedureMaster(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.fetchProcedureMaster(PSM_ID)));
    }

    @Test
    @DisplayName("fetchComponentDetailsForComponentID should answer the component the service publishes")
    void fetchComponentDetails_shouldAnswerPublishedComponent() throws Exception {
        when(mastersFetchingServiceImpl.getComponentDetailsForComponentID(COMPONENT_ID))
                .thenReturn("{\"testComponentName\":\"Haemoglobin count\"}");

        assertSuccessContaining(controller.fetchComponentDetailsForComponentID(COMPONENT_ID),
                "Haemoglobin count");
    }

    @Test
    @DisplayName("fetchComponentDetailsForComponentID should say so when the component is not on record")
    void fetchComponentDetails_shouldSaySoForUnknownComponent() throws Exception {
        when(mastersFetchingServiceImpl.getComponentDetailsForComponentID(COMPONENT_ID)).thenReturn(null);

        assertSuccessContaining(controller.fetchComponentDetailsForComponentID(COMPONENT_ID),
                "Component Details not found in Database.");
    }

    @Test
    @DisplayName("updateProcedureStatus should answer the procedure once its status has changed")
    void updateProcedureStatus_shouldAnswerChangedProcedure() throws Exception {
        when(mastersStatusUpdateImpl.updateProcedureStatus(PROCEDURE_ID, true)).thenReturn(PROCEDURE_JSON);

        assertSuccessContaining(
                controller.updateProcedureStatus("{\"procedureID\":71,\"deleted\":true}"), "Haemoglobin");
    }

    @Test
    @DisplayName("updateProcedureStatus should report a status the service could not change")
    void updateProcedureStatus_shouldReportUnchangedStatus() throws Exception {
        when(mastersStatusUpdateImpl.updateProcedureStatus(anyInt(), anyBoolean())).thenReturn(null);

        String response = controller.updateProcedureStatus("{\"procedureID\":71,\"deleted\":true}");

        assertInvalidRequest(response);
        assertTrue(response.contains("Failed to update the status"), response);
    }

    @Test
    @DisplayName("updateProcedureStatus should refuse a request that names no procedure")
    void updateProcedureStatus_shouldRefuseRequestWithoutProcedure() {
        assertInvalidRequest(controller.updateProcedureStatus("{\"deleted\":true}"));
        assertInvalidRequest(controller.updateProcedureStatus("{\"procedureID\":0,\"deleted\":true}"));
    }

    @Test
    @DisplayName("updateProcedureStatus should answer an error envelope for a body it cannot read")
    void updateProcedureStatus_shouldAnswerErrorEnvelopeForMalformedBody() {
        assertEquals(OutputResponse.OBJECT_FAILURE, statusCodeOf(controller.updateProcedureStatus("not json")));
    }

    @Test
    @DisplayName("updateComponentStatus should answer the component once its status has changed")
    void updateComponentStatus_shouldAnswerChangedComponent() throws Exception {
        when(mastersStatusUpdateImpl.updateComponentStatus(COMPONENT_ID, true))
                .thenReturn("{\"testComponentName\":\"Haemoglobin count\"}");

        assertSuccessContaining(
                controller.updateComponentStatus("{\"componentID\":81,\"deleted\":true}"), "Haemoglobin count");
    }

    @Test
    @DisplayName("updateComponentStatus should report a status the service could not change")
    void updateComponentStatus_shouldReportUnchangedStatus() throws Exception {
        when(mastersStatusUpdateImpl.updateComponentStatus(anyInt(), anyBoolean())).thenReturn(null);

        assertInvalidRequest(controller.updateComponentStatus("{\"componentID\":81,\"deleted\":true}"));
    }

    @Test
    @DisplayName("updateComponentStatus should refuse a request that names no component")
    void updateComponentStatus_shouldRefuseRequestWithoutComponent() {
        assertInvalidRequest(controller.updateComponentStatus("{\"deleted\":true}"));
    }

    @Test
    @DisplayName("updateProcedureMaster should answer the procedure once the edit lands")
    void updateProcedureMaster_shouldAnswerEditedProcedure() throws Exception {
        when(mastersStatusUpdateImpl.updateProcedureMaster(anyString())).thenReturn(PROCEDURE_JSON);

        assertSuccessContaining(controller.updateProcedureMaster(PROCEDURE_JSON), "Haemoglobin");
    }

    @Test
    @DisplayName("updateProcedureMaster should report an edit the service could not apply")
    void updateProcedureMaster_shouldReportUnappliedEdit() throws Exception {
        when(mastersStatusUpdateImpl.updateProcedureMaster(anyString())).thenReturn(null);

        String response = controller.updateProcedureMaster("{}");

        assertInvalidRequest(response);
        assertTrue(response.contains("Failed to update procedure details"), response);
    }

    @Test
    @DisplayName("updateProcedureMaster should answer an error envelope when the edit fails")
    void updateProcedureMaster_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(mastersStatusUpdateImpl.updateProcedureMaster(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.updateProcedureMaster("{}")));
    }

    @Test
    @DisplayName("updateComponentMaster should answer the component once the edit lands")
    void updateComponentMaster_shouldAnswerEditedComponent() throws Exception {
        when(mastersStatusUpdateImpl.updateComponentMaster(anyString()))
                .thenReturn("{\"testComponentName\":\"Haemoglobin count\"}");

        assertSuccessContaining(controller.updateComponentMaster("{}"), "Haemoglobin count");
    }

    @Test
    @DisplayName("updateComponentMaster should report an edit the service could not apply")
    void updateComponentMaster_shouldReportUnappliedEdit() throws Exception {
        when(mastersStatusUpdateImpl.updateComponentMaster(anyString())).thenReturn(null);

        String response = controller.updateComponentMaster("{}");

        assertInvalidRequest(response);
        assertTrue(response.contains("Failed to update component details"), response);
    }

    @Test
    @DisplayName("updateComponentMaster should answer an error envelope when the edit fails")
    void updateComponentMaster_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(mastersStatusUpdateImpl.updateComponentMaster(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.updateComponentMaster("{}")));
    }
}
