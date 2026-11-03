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
package com.iemr.admin.controller.snomedMapping;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.gson.JsonObject;
import com.iemr.admin.service.snomedMapping.SnomedService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * The snomed screen maps entries of the clinical masters onto SNOMED codes and
 * reports back in the master's own words why a mapping was refused.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SnomedMappingController Test Suite")
class SnomedMappingControllerTest {

    private static final String REQUEST = "{\"masterType\":\"Family History\",\"masterID\":12,"
            + "\"sctCode\":\"73211009\",\"modifiedBy\":\"admin\"}";

    @Mock
    private SnomedService snomedService;

    @InjectMocks
    private SnomedMappingController controller;

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static String errorMessageOf(String response) {
        return new JSONObject(response).getString("errorMessage");
    }

    @Test
    @DisplayName("editSnomedMaster should confirm the mapping it recorded")
    void edit_shouldConfirmRecordedMapping() {
        when(snomedService.editSnomedMappingData(any(JsonObject.class), anyString())).thenReturn("Data Updated");

        String response = controller.editSnomedMaster(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Data Updated successfully"), response);
    }

    @Test
    @DisplayName("editSnomedMaster should pass on the master type refusal in the service's own words")
    void edit_shouldPassOnMasterTypeRefusal() {
        when(snomedService.editSnomedMappingData(any(JsonObject.class), anyString()))
                .thenReturn("Invalid Master Type");

        String response = controller.editSnomedMaster(REQUEST);

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response));
        assertEquals("Invalid Master Type", errorMessageOf(response));
    }

    @Test
    @DisplayName("editSnomedMaster should report a general refusal when the mapping was not recorded")
    void edit_shouldReportGeneralRefusal() {
        when(snomedService.editSnomedMappingData(any(JsonObject.class), anyString())).thenReturn(null);

        String response = controller.editSnomedMaster(REQUEST);

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response));
        assertEquals("Unable to update data", errorMessageOf(response));
    }

    @Test
    @DisplayName("editSnomedMaster should report the failure when the request cannot be read")
    void edit_shouldReportUnreadableRequest() {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.editSnomedMaster("{not json")));
    }

    @Test
    @DisplayName("saveSnomedMaster should confirm the mappings it stored")
    void save_shouldConfirmStoredMappings() {
        when(snomedService.saveSnomedMappingData(any(JsonObject.class), anyString())).thenReturn("Data Saved");

        String response = controller.saveSnomedMaster(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Data Saved successfully"), response);
    }

    @Test
    @DisplayName("saveSnomedMaster should pass on the master type refusal in the service's own words")
    void save_shouldPassOnMasterTypeRefusal() {
        when(snomedService.saveSnomedMappingData(any(JsonObject.class), anyString()))
                .thenReturn("Invalid Master Type");

        assertEquals("Invalid Master Type", errorMessageOf(controller.saveSnomedMaster(REQUEST)));
    }

    @Test
    @DisplayName("saveSnomedMaster should report a general refusal when nothing was stored")
    void save_shouldReportGeneralRefusal() {
        when(snomedService.saveSnomedMappingData(any(JsonObject.class), anyString())).thenReturn(null);

        assertEquals("Unable to Save data", errorMessageOf(controller.saveSnomedMaster(REQUEST)));
    }

    @Test
    @DisplayName("saveSnomedMaster should report the failure when the request cannot be read")
    void save_shouldReportUnreadableRequest() {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveSnomedMaster("{not json")));
    }

    @Test
    @DisplayName("fetchSnomedWorklist should answer the master the request asked for")
    void fetch_shouldAnswerRequestedMaster() {
        when(snomedService.fetchSnomedMaster(any(JsonObject.class))).thenReturn("[{\"masterName\":\"Diabetes\"}]");

        String response = controller.fetchSnomedWorklist(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Diabetes"), response);
    }

    @Test
    @DisplayName("fetchSnomedWorklist should report the failure when the worklist cannot be answered")
    void fetch_shouldReportLookupFailure() {
        when(snomedService.fetchSnomedMaster(any(JsonObject.class))).thenReturn(null);

        assertEquals("error in fetching worklist data", errorMessageOf(controller.fetchSnomedWorklist(REQUEST)));
    }

    @Test
    @DisplayName("fetchSnomedWorklist should report the failure when the request cannot be read")
    void fetch_shouldReportUnreadableRequest() {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.fetchSnomedWorklist("{not json")));
    }

    @Test
    @DisplayName("updateStatus should confirm the change it recorded")
    void updateStatus_shouldConfirmRecordedChange() {
        when(snomedService.updateStatus(anyString())).thenReturn("Data updated successfully");

        String response = controller.updateStatus(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Data updated successfully"), response);
    }

    @Test
    @DisplayName("updateStatus should report the failure when nothing came back from the service")
    void updateStatus_shouldReportMissingAnswer() {
        when(snomedService.updateStatus(anyString())).thenReturn(null);

        assertEquals("error in updating data", errorMessageOf(controller.updateStatus(REQUEST)));
    }

    @Test
    @DisplayName("updateStatus should report the failure when the change cannot be recorded")
    void updateStatus_shouldReportStorageFailure() {
        when(snomedService.updateStatus(anyString())).thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.updateStatus(REQUEST)));
    }
}
