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
package com.iemr.admin.controller.telemedicine;

import java.util.ArrayList;
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

import com.iemr.admin.data.telemedicine.M_UserTemp;
import com.iemr.admin.data.telemedicine.UserVideoConsultation;
import com.iemr.admin.data.telemedicine.VideoConsultationDomain;
import com.iemr.admin.service.telemedicine.VideoConsultationInter;
import com.iemr.admin.utils.exception.VideoConsultationException;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * The video consultation screen mints conferencing accounts for clinicians and
 * keeps their sign-in details and status up to date.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VideoConsultationController Test Suite")
class VideoConsultationControllerTest {

    private static final Integer PROVIDER_ID = 5;
    private static final Long MAP_ID = 8001L;

    @Mock
    private VideoConsultationInter videoConsultationInter;

    @InjectMocks
    private VideoConsultationController controller;

    private static M_UserTemp clinician() {
        M_UserTemp user = new M_UserTemp();
        user.setUserID(3117L);
        user.setUserName("asha.rao");
        return user;
    }

    private static UserVideoConsultation account() {
        UserVideoConsultation account = new UserVideoConsultation();
        account.setUserVideoConsultationMapID(MAP_ID);
        account.setUserID(3117L);
        account.setVideoConsultationEmailID("asha.rao@example.org");
        account.setVideoConsultationDomain("psmri");
        return account;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("getUserTM should answer the clinicians who have no conferencing account yet")
    void getUnmapped_shouldAnswerCliniciansWithoutAccount() {
        when(videoConsultationInter.getunmappedUser(PROVIDER_ID, 7)).thenReturn(List.of(clinician()));

        assertSuccessContaining(controller.getUserTM(PROVIDER_ID, 7), "asha.rao");
    }

    @Test
    @DisplayName("getUserTM should report the failure when the candidates cannot be worked out")
    void getUnmapped_shouldReportLookupFailure() {
        when(videoConsultationInter.getunmappedUser(any(), any()))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getUserTM(PROVIDER_ID, 7)));
    }

    @Test
    @DisplayName("createUserTM should answer the conferencing account it opened")
    void createUser_shouldAnswerOpenedAccount() throws Exception {
        when(videoConsultationInter.createUser(any())).thenReturn(account());

        assertSuccessContaining(controller.createUserTM(account()), "asha.rao@example.org");
    }

    @Test
    @DisplayName("createUserTM should report the platform's reason when the account is refused")
    void createUser_shouldReportRemoteRefusal() throws Exception {
        when(videoConsultationInter.createUser(any()))
                .thenThrow(new VideoConsultationException("email already registered"));

        assertEquals(OutputResponse.VIDEOCONSULTATION_EXCEPTION, statusCodeOf(controller.createUserTM(account())));
    }

    @Test
    @DisplayName("editUser should answer the account whose sign-in details it changed")
    void editUser_shouldAnswerChangedAccount() throws Exception {
        when(videoConsultationInter.editUser(any())).thenReturn(account());

        assertSuccessContaining(controller.editUser(account()), "8001");
    }

    @Test
    @DisplayName("editUser should report the failure when the account is unknown")
    void editUser_shouldReportUnknownAccount() throws Exception {
        when(videoConsultationInter.editUser(any())).thenThrow(new VideoConsultationException("Invalid MapID"));

        assertEquals(OutputResponse.VIDEOCONSULTATION_EXCEPTION, statusCodeOf(controller.editUser(account())));
    }

    @Test
    @DisplayName("deleting an account should answer it with the status the caller asked for")
    void deleteUser_shouldAnswerAccountWithRequestedStatus() throws Exception {
        UserVideoConsultation retired = account();
        retired.setDeleted(Boolean.TRUE);
        when(videoConsultationInter.deleteUser(MAP_ID, Boolean.TRUE, "supervisor")).thenReturn(retired);

        assertSuccessContaining(controller.createUserTM("supervisor", MAP_ID, Boolean.TRUE), "8001");
    }

    @Test
    @DisplayName("deleting an account should report the failure when the platform refuses the change")
    void deleteUser_shouldReportRemoteRefusal() throws Exception {
        when(videoConsultationInter.deleteUser(anyLong(), anyBoolean(), anyString()))
                .thenThrow(new VideoConsultationException("account is locked"));

        assertEquals(OutputResponse.VIDEOCONSULTATION_EXCEPTION,
                statusCodeOf(controller.createUserTM("supervisor", MAP_ID, Boolean.TRUE)));
    }

    @Test
    @DisplayName("getmappedUsers should answer the accounts held under the provider asked for")
    void getmappedUsers_shouldAnswerAccountsOfProvider() {
        when(videoConsultationInter.fetchmappedUser(PROVIDER_ID)).thenReturn(new ArrayList<>(List.of(account())));

        assertSuccessContaining(controller.getmappedUsers(PROVIDER_ID), "asha.rao@example.org");
    }

    @Test
    @DisplayName("getmappedUsers should report the failure when the lookup cannot be answered")
    void getmappedUsers_shouldReportLookupFailure() {
        when(videoConsultationInter.fetchmappedUser(any())).thenThrow(new RuntimeException("connection reset"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getmappedUsers(PROVIDER_ID)));
    }

    @Test
    @DisplayName("getdomain should answer the conferencing domains on file")
    void getdomain_shouldAnswerDomains() {
        VideoConsultationDomain domain = new VideoConsultationDomain();
        domain.setVideoConsultationDomainID(1);
        domain.setVideoConsultationDoamin("psmri");
        when(videoConsultationInter.getdomain(PROVIDER_ID)).thenReturn(List.of(domain));

        assertSuccessContaining(controller.getdomain(PROVIDER_ID), "psmri");
    }

    @Test
    @DisplayName("getdomain should report the failure when the domains cannot be answered")
    void getdomain_shouldReportLookupFailure() {
        when(videoConsultationInter.getdomain(any())).thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getdomain(PROVIDER_ID)));
    }
}
