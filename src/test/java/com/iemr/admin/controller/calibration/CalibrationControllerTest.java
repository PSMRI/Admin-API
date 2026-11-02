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
package com.iemr.admin.controller.calibration;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.calibration.CalibrationStrip;
import com.iemr.admin.service.calibration.CalibrationService;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The calibration screen keeps the test strip codes a provider calibrates
 * against.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CalibrationController Test Suite")
class CalibrationControllerTest {

    private static final String REQUEST = "{\"calibrationStripID\":6601,\"stripCode\":\"STRIP-77\","
            + "\"providerServiceMapID\":4001,\"deleted\":false,\"createdBy\":\"admin\"}";

    @Mock
    private CalibrationService calibrationService;

    @InjectMocks
    private CalibrationController controller;

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static String errorMessageOf(String response) {
        return new JSONObject(response).getString("errorMessage");
    }

    @Test
    @DisplayName("createCalibrationStrip should confirm the strip it recorded")
    void create_shouldConfirmRecordedStrip() throws Exception {
        when(calibrationService.saveData(any(CalibrationStrip.class))).thenReturn(1);

        String response = controller.createCalibrationStrip(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Data saved successfully"), response);
    }

    @Test
    @DisplayName("createCalibrationStrip should report the failure when no strip was recorded")
    void create_shouldReportNothingRecorded() throws Exception {
        when(calibrationService.saveData(any(CalibrationStrip.class))).thenReturn(0);

        assertEquals("Error while saving Calibration master data",
                errorMessageOf(controller.createCalibrationStrip(REQUEST)));
    }

    @Test
    @DisplayName("createCalibrationStrip should pass on the refusal in the service's own words")
    void create_shouldPassOnRefusal() throws Exception {
        when(calibrationService.saveData(any(CalibrationStrip.class)))
                .thenThrow(new IEMRException("Strip code already exists"));

        assertEquals("Strip code already exists", errorMessageOf(controller.createCalibrationStrip(REQUEST)));
    }

    @Test
    @DisplayName("fetchCalibrationStrips should answer the strips the provider holds")
    void fetch_shouldAnswerHeldStrips() throws Exception {
        when(calibrationService.fetchData(any(CalibrationStrip.class)))
                .thenReturn("{\"calibrationData\":[{\"stripCode\":\"STRIP-77\"}]}");

        String response = controller.fetchCalibrationStrips(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("STRIP-77"), response);
    }

    @Test
    @DisplayName("fetchCalibrationStrips should report the failure when the strips cannot be answered")
    void fetch_shouldReportLookupFailure() throws Exception {
        when(calibrationService.fetchData(any(CalibrationStrip.class)))
                .thenThrow(new IEMRException("Error while fetching Calibration data"));

        assertEquals(OutputResponse.USERID_FAILURE, statusCodeOf(controller.fetchCalibrationStrips(REQUEST)));
    }

    @Test
    @DisplayName("deleteCalibrationStrip should confirm the retirement it recorded")
    void delete_shouldConfirmRetirement() throws Exception {
        when(calibrationService.deleteData(any(CalibrationStrip.class))).thenReturn(1);

        String response = controller.deleteCalibrationStrip(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Data deleted successfully"), response);
    }

    @Test
    @DisplayName("deleteCalibrationStrip should report the failure when no strip was retired")
    void delete_shouldReportNothingRetired() throws Exception {
        when(calibrationService.deleteData(any(CalibrationStrip.class))).thenReturn(0);

        assertEquals("Error while updating Calibration master data",
                errorMessageOf(controller.deleteCalibrationStrip(REQUEST)));
    }

    @Test
    @DisplayName("deleteCalibrationStrip should report the failure when the request is refused")
    void delete_shouldReportRefusal() throws Exception {
        when(calibrationService.deleteData(any(CalibrationStrip.class)))
                .thenThrow(new IEMRException("Invalid request"));

        assertEquals(OutputResponse.USERID_FAILURE, statusCodeOf(controller.deleteCalibrationStrip(REQUEST)));
    }

    @Test
    @DisplayName("updateCalibrationStrip should confirm the change it recorded")
    void update_shouldConfirmRecordedChange() throws Exception {
        when(calibrationService.updateData(any(CalibrationStrip.class))).thenReturn(1);

        String response = controller.updateCalibrationStrip(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Data updated successfully"), response);
    }

    @Test
    @DisplayName("updateCalibrationStrip should report the failure when no strip was changed")
    void update_shouldReportNothingChanged() throws Exception {
        when(calibrationService.updateData(any(CalibrationStrip.class))).thenReturn(0);

        assertEquals("Error while updating Calibration master data",
                errorMessageOf(controller.updateCalibrationStrip(REQUEST)));
    }

    @Test
    @DisplayName("updateCalibrationStrip should pass on the refusal in the service's own words")
    void update_shouldPassOnRefusal() throws Exception {
        when(calibrationService.updateData(any(CalibrationStrip.class)))
                .thenThrow(new IEMRException("Error while updating data"));

        assertEquals("Error while updating data", errorMessageOf(controller.updateCalibrationStrip(REQUEST)));
    }
}
