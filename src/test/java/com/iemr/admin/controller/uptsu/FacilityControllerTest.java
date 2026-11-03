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
package com.iemr.admin.controller.uptsu;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.uptsu.CDSSMapping;
import com.iemr.admin.data.uptsu.M_FacilityMapping;
import com.iemr.admin.service.uptsu.FacilityService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * The UP TSU endpoints take the facility spreadsheet an operator uploads and the
 * CDSS switch that decides whether decision support is on for a provider.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FacilityController Test Suite")
class FacilityControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final String AUTH = "sess-0d5f3a7c";

    @Mock
    private FacilityService uptsuService;

    @InjectMocks
    private FacilityController controller;

    private static final String UPLOAD_REQUEST = "{\"createdBy\":\"admin\",\"fileName\":\"facilities.xlsx\","
            + "\"providerServiceMapID\":4001,\"fileExtension\":\"xlsx\",\"fileContent\":\"data:x;base64,AAA=\"}";

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    @Test
    @DisplayName("saveFacilityData should report the upload once the service has stored it")
    void saveFacilityData_shouldReportStoredUpload() throws Exception {
        when(uptsuService.saveFacility(any())).thenReturn(List.of(new M_FacilityMapping()));

        String response = controller.saveFacilityData(UPLOAD_REQUEST, AUTH);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("saveFacilityData saved successfully"), response);
    }

    @Test
    @DisplayName("saveFacilityData should refuse an upload the service could not read")
    void saveFacilityData_shouldRefuseUnreadableUpload() throws Exception {
        when(uptsuService.saveFacility(any()))
                .thenThrow(new com.iemr.admin.utils.exception.IEMRException("Error in validating cell"));

        String response = controller.saveFacilityData(UPLOAD_REQUEST, AUTH);

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
        assertTrue(response.contains("Invalid Request"), response);
    }

    @Test
    @DisplayName("saveFacilityData should refuse a request body that is not a valid upload at all")
    void saveFacilityData_shouldRefuseMalformedRequest() {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveFacilityData("not json", AUTH)));
    }

    @Test
    @DisplayName("saveFacilityData should stay at its default when the service stored nothing")
    void saveFacilityData_shouldStayAtDefaultWhenNothingStored() throws Exception {
        when(uptsuService.saveFacility(any())).thenReturn(List.of());

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveFacilityData(UPLOAD_REQUEST, AUTH)));
    }

    @Test
    @DisplayName("saveCdssDetails should report the configuration once the service has stored it")
    void saveCdssDetails_shouldReportStoredConfiguration() {
        when(uptsuService.saveCdssDetails(any())).thenReturn(new CDSSMapping());

        String response = controller.saveCdssDetails("{\"psmId\":4001,\"isCdss\":true}");

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Data saved successfully"), response);
    }

    @Test
    @DisplayName("saveCdssDetails should report the reason the service refused the configuration")
    void saveCdssDetails_shouldReportRefusal() {
        when(uptsuService.saveCdssDetails(any())).thenThrow(new IllegalStateException("no connection"));

        String response = controller.saveCdssDetails("{\"psmId\":4001}");

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
        assertTrue(response.contains("no connection"), response);
    }

    @Test
    @DisplayName("saveCdssDetails should refuse a request body it cannot read")
    void saveCdssDetails_shouldRefuseMalformedRequest() {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveCdssDetails("not json")));
    }

    @Test
    @DisplayName("getCdssData should answer the configuration the service publishes")
    void getCdssData_shouldAnswerPublishedConfiguration() throws Exception {
        when(uptsuService.getCdssData(PSM_ID)).thenReturn("{\"psmId\":4001,\"isCdss\":true}");

        String response = controller.getCdssData(PSM_ID, AUTH);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("4001"), response);
    }

    @Test
    @DisplayName("getCdssData should answer an error envelope when the lookup fails")
    void getCdssData_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(uptsuService.getCdssData(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getCdssData(PSM_ID, AUTH)));
    }
}
