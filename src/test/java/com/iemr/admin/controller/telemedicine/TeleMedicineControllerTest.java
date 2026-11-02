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
import com.iemr.admin.data.telemedicine.Specialization;
import com.iemr.admin.data.telemedicine.TMinput;
import com.iemr.admin.data.telemedicine.UserSpecializationMapping;
import com.iemr.admin.service.telemedicine.TMInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The telemedicine screen lists the specialists a provider has and records
 * which specialities each of them holds.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TeleMedicineController Test Suite")
class TeleMedicineControllerTest {

    private static final Integer PROVIDER_ID = 5;
    private static final Integer MAP_ID = 8001;

    @Mock
    private TMInter tmInter;

    @InjectMocks
    private TeleMedicineController controller;

    private static TMinput request() {
        TMinput input = new TMinput();
        input.setServiceproviderID(PROVIDER_ID);
        input.setScreenName("TM");
        return input;
    }

    private static M_UserTemp specialist() {
        M_UserTemp user = new M_UserTemp();
        user.setUserID(3117L);
        user.setFirstName("Asha");
        user.setUserName("asha.rao");
        return user;
    }

    private static Specialization speciality() {
        Specialization speciality = new Specialization();
        speciality.setSpecializationID(11);
        speciality.setSpecialization("Paediatrics");
        return speciality;
    }

    private static UserSpecializationMapping held() {
        UserSpecializationMapping mapping = new UserSpecializationMapping();
        mapping.setUserSpecializationMapID(MAP_ID);
        mapping.setSpecializationName("Paediatrics");
        mapping.setDeleted(Boolean.FALSE);
        mapping.setModifiedBy("admin");
        return mapping;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("getUserTM should answer the specialists on the screen asked for")
    void getUserTM_shouldAnswerSpecialists() {
        when(tmInter.getUser(any())).thenReturn(new ArrayList<>(List.of(specialist())));

        assertSuccessContaining(controller.getUserTM(request()), "asha.rao");
    }

    @Test
    @DisplayName("getUserTM should report the failure when the roster cannot be answered")
    void getUserTM_shouldReportLookupFailure() {
        when(tmInter.getUser(any())).thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getUserTM(request())));
    }

    @Test
    @DisplayName("getSpecialization should answer the specialities on file")
    void getSpecialization_shouldAnswerSpecialities() {
        when(tmInter.getSpecialization()).thenReturn(new ArrayList<>(List.of(speciality())));

        assertSuccessContaining(controller.getSpecialization(), "Paediatrics");
    }

    @Test
    @DisplayName("getSpecialization should report the failure when the list cannot be answered")
    void getSpecialization_shouldReportLookupFailure() {
        when(tmInter.getSpecialization()).thenThrow(new RuntimeException("connection reset"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getSpecialization()));
    }

    @Test
    @DisplayName("getUserSpecialization should answer the specialities held under the provider asked for")
    void getUserSpecialization_shouldAnswerHeldSpecialities() {
        when(tmInter.getUserSpecialization(PROVIDER_ID)).thenReturn(new ArrayList<>(List.of(held())));

        assertSuccessContaining(controller.getUserSpecialization(request()), "Paediatrics");
    }

    @Test
    @DisplayName("getUserSpecialization should report the failure when the lookup cannot be answered")
    void getUserSpecialization_shouldReportLookupFailure() {
        when(tmInter.getUserSpecialization(any())).thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getUserSpecialization(request())));
    }

    @Test
    @DisplayName("saveUserSpecialization should answer the specialities it recorded")
    void saveUserSpecialization_shouldAnswerRecordedSpecialities() {
        ArrayList<UserSpecializationMapping> request = new ArrayList<>(List.of(held()));
        when(tmInter.saveUserSpecialization(request)).thenReturn(request);

        assertSuccessContaining(controller.saveUserSpecialization(request), "Paediatrics");
    }

    @Test
    @DisplayName("saveUserSpecialization should report the failure when the speciality cannot be recorded")
    void saveUserSpecialization_shouldReportStorageFailure() {
        ArrayList<UserSpecializationMapping> request = new ArrayList<>(List.of(held()));
        when(tmInter.saveUserSpecialization(request)).thenThrow(new RuntimeException("already recorded"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveUserSpecialization(request)));
    }

    @Test
    @DisplayName("activating a speciality should answer it with the status the caller asked for")
    void activate_shouldAnswerSpecialityWithRequestedStatus() {
        UserSpecializationMapping stored = held();
        UserSpecializationMapping request = held();
        request.setDeleted(Boolean.TRUE);
        request.setModifiedBy("supervisor");
        when(tmInter.findUserSpecialization(request)).thenReturn(stored);
        when(tmInter.saveoneUserSpecialization(stored)).thenReturn(stored);

        assertSuccessContaining(controller.saveUserSpecialization(request), "8001");
        assertEquals(Boolean.TRUE, stored.getDeleted());
        assertEquals("supervisor", stored.getModifiedBy());
    }

    @Test
    @DisplayName("activating a speciality should report the failure when it is unknown")
    void activate_shouldReportUnknownSpeciality() {
        when(tmInter.findUserSpecialization(any())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.saveUserSpecialization(held())));
    }

    @Test
    @DisplayName("activating a speciality should report the failure when the change cannot be recorded")
    void activate_shouldReportStorageFailure() {
        UserSpecializationMapping stored = held();
        when(tmInter.findUserSpecialization(any())).thenReturn(stored);
        when(tmInter.saveoneUserSpecialization(stored)).thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveUserSpecialization(held())));
    }
}
