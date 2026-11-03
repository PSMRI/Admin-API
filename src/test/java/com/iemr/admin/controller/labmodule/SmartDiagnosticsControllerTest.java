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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.iemr.admin.sevice.labmodule.IOTService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * The smart diagnostics endpoints tell the client which device tests exist and
 * where the rapid screening device is reachable.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SmartDiagnosticsController Test Suite")
class SmartDiagnosticsControllerTest {

    @Mock
    private IOTService iotService;

    @InjectMocks
    private SmartDiagnosticsController controller;

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    @Test
    @DisplayName("getIOTProcedure should answer the device tests the service publishes")
    void getIOTProcedure_shouldAnswerDeviceTests() {
        when(iotService.getIOTProcedure()).thenReturn("[{\"calibrationCode\":\"HB\"}]");

        String response = controller.getIOTProcedure();

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("HB"), response);
    }

    @Test
    @DisplayName("getIOTProcedure should answer an error envelope when the lookup fails")
    void getIOTProcedure_shouldAnswerErrorEnvelopeOnFailure() {
        when(iotService.getIOTProcedure()).thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getIOTProcedure()));
    }

    @Test
    @DisplayName("getIOTComponent should answer the device components the service publishes")
    void getIOTComponent_shouldAnswerDeviceComponents() {
        when(iotService.getIOTComponent()).thenReturn("[{\"iotComponentID\":1}]");

        String response = controller.getIOTComponent();

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("iotComponentID"), response);
    }

    @Test
    @DisplayName("getIOTComponent should answer an error envelope when the lookup fails")
    void getIOTComponent_shouldAnswerErrorEnvelopeOnFailure() {
        when(iotService.getIOTComponent()).thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getIOTComponent()));
    }

    @Test
    @DisplayName("getBiologicalScreeningDeviceUrl should answer the configured device address")
    void getBiologicalScreeningDeviceUrl_shouldAnswerConfiguredAddress() {
        ReflectionTestUtils.setField(controller, "biologicalScreeningDeviceUrl", "http://device.local:9000");

        assertEquals("http://device.local:9000", controller.getBiologicalScreeningDeviceUrl());
    }
}
