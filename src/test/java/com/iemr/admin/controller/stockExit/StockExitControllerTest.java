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
package com.iemr.admin.controller.stockExit;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.stockExit.T_PatientIssue;
import com.iemr.admin.service.stockExit.StockExitService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The patient issue screen hands drugs out of a store to a patient.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StockExitController Test Suite")
class StockExitControllerTest {

    @Mock
    private StockExitService stockExitService;

    @InjectMocks
    private StockExitController controller;

    private static T_PatientIssue issue() {
        T_PatientIssue issue = new T_PatientIssue();
        issue.setPatientIssueID(5501);
        issue.setFacilityID(9001);
        issue.setIssueType("Manual");
        return issue;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    @Test
    @DisplayName("patientIssue should confirm the issue it recorded")
    void patientIssue_shouldConfirmRecordedIssue() {
        when(stockExitService.issuePatientDrugs(any(T_PatientIssue.class))).thenReturn(1);

        String response = controller.patientIssue(issue());

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Successfully Created"), response);
    }

    @Test
    @DisplayName("patientIssue should say the quantities are wrong when the store cannot cover the issue")
    void patientIssue_shouldSayQuantitiesAreWrong() {
        when(stockExitService.issuePatientDrugs(any(T_PatientIssue.class))).thenReturn(0);

        String response = controller.patientIssue(issue());

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Error in Quantity"), response);
    }

    @Test
    @DisplayName("patientIssue should report the failure when the issue cannot be recorded")
    void patientIssue_shouldReportStorageFailure() {
        when(stockExitService.issuePatientDrugs(any(T_PatientIssue.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.patientIssue(issue())));
    }
}
