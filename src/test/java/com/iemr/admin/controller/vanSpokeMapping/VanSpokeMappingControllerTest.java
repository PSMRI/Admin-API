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
package com.iemr.admin.controller.vanSpokeMapping;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.service.vanSpokeMapping.VanSpokeMappingService;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * The spoke mapping screen ties mobile unit vans to the telemedicine spokes
 * they serve.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VanSpokeMappingController Test Suite")
class VanSpokeMappingControllerTest {

    private static final String AUTHORIZATION = "session-key-123";
    private static final String REQUEST = "{\"vanSpokeMapping\":[{\"mmu_VanID\":71}]}";

    @Mock
    private VanSpokeMappingService vanSpokeMappingService;

    @InjectMocks
    private VanSpokeMappingController controller;

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static String errorMessageOf(String response) {
        return new JSONObject(response).getString("errorMessage");
    }

    @Test
    @DisplayName("saving a mapping should confirm the tie it recorded")
    void save_shouldConfirmRecordedTie() throws Exception {
        when(vanSpokeMappingService.saveVanSpokeMapping(anyString())).thenReturn("success");

        String response = controller.saveBenNCDCareNurseData(REQUEST, AUTHORIZATION);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Mapping done successfully"), response);
    }

    @Test
    @DisplayName("saving a mapping should report the failure when the tie was refused")
    void save_shouldReportRefusedTie() throws Exception {
        when(vanSpokeMappingService.saveVanSpokeMapping(anyString())).thenReturn("failure");

        assertEquals("error in mapping van and spoke",
                errorMessageOf(controller.saveBenNCDCareNurseData(REQUEST, AUTHORIZATION)));
    }

    @Test
    @DisplayName("saving a mapping should report the failure when the tie cannot be recorded")
    void save_shouldReportStorageFailure() throws Exception {
        when(vanSpokeMappingService.saveVanSpokeMapping(anyString()))
                .thenThrow(new IEMRException("van is already mapped"));

        assertEquals(OutputResponse.USERID_FAILURE,
                statusCodeOf(controller.saveBenNCDCareNurseData(REQUEST, AUTHORIZATION)));
    }

    @Test
    @DisplayName("getVanSpokeMapping should answer the ties held at the parking place asked about")
    void get_shouldAnswerHeldTies() throws Exception {
        when(vanSpokeMappingService.getVanSpokeMappingDetails(anyString()))
                .thenReturn("{\"vanSpokeMappedDetails\":[{\"vanspokeID\":6001}]}");

        String response = controller.getVanSpokeMapping(REQUEST, AUTHORIZATION);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("vanSpokeMappedDetails"), response);
    }

    @Test
    @DisplayName("getVanSpokeMapping should report the failure when nothing came back from the service")
    void get_shouldReportMissingAnswer() throws Exception {
        when(vanSpokeMappingService.getVanSpokeMappingDetails(anyString())).thenReturn(null);

        assertEquals("error in fetching the van and spoke data",
                errorMessageOf(controller.getVanSpokeMapping(REQUEST, AUTHORIZATION)));
    }

    @Test
    @DisplayName("getVanSpokeMapping should report the failure when the lookup cannot be answered")
    void get_shouldReportLookupFailure() throws Exception {
        when(vanSpokeMappingService.getVanSpokeMappingDetails(anyString()))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getVanSpokeMapping(REQUEST, AUTHORIZATION)));
    }

    @Test
    @DisplayName("deleteVanSpokeMapping should confirm the change it recorded")
    void delete_shouldConfirmRecordedChange() throws Exception {
        when(vanSpokeMappingService.deleteVanSpokeMapping(anyString())).thenReturn("success");

        String response = controller.deleteVanSpokeMapping(REQUEST, AUTHORIZATION);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Mapping status got updated"), response);
    }

    @Test
    @DisplayName("deleteVanSpokeMapping should report the failure when the change was refused")
    void delete_shouldReportRefusedChange() throws Exception {
        when(vanSpokeMappingService.deleteVanSpokeMapping(anyString())).thenReturn("failure");

        assertEquals("Error in deleting mapping",
                errorMessageOf(controller.deleteVanSpokeMapping(REQUEST, AUTHORIZATION)));
    }

    @Test
    @DisplayName("deleteVanSpokeMapping should report the failure when the change cannot be recorded")
    void delete_shouldReportStorageFailure() throws Exception {
        when(vanSpokeMappingService.deleteVanSpokeMapping(anyString()))
                .thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.deleteVanSpokeMapping(REQUEST, AUTHORIZATION)));
    }
}
