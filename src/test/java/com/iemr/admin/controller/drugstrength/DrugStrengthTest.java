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
package com.iemr.admin.controller.drugstrength;

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

import com.iemr.admin.data.drugstrangth.M_104DrugStrength;
import com.iemr.admin.service.drugstrangth.DrugStrangthInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The drug strength screen keeps the strengths a drug can be dispensed in.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DrugStrength controller Test Suite")
class DrugStrengthTest {

    private static final Integer STRENGTH_ID = 33;

    @Mock
    private DrugStrangthInter durgStrangthInter;

    @InjectMocks
    private DrugStrength controller;

    private static M_104DrugStrength strength() {
        M_104DrugStrength strength = new M_104DrugStrength();
        strength.setDrugStrengthID(STRENGTH_ID);
        strength.setDrugStrength("500 mg");
        strength.setDrugStrengthDesc("Standard adult dose");
        return strength;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("createDrugStrangth should answer the strengths it added")
    void create_shouldAnswerAddedStrengths() {
        when(durgStrangthInter.createDrugStrangth(anyList())).thenReturn(new ArrayList<>(List.of(strength())));

        assertSuccessContaining(controller.createDrugStrangth("[{\"drugStrength\":\"500 mg\"}]"), "500 mg");
    }

    @Test
    @DisplayName("createDrugStrangth should report the failure when the strength cannot be added")
    void create_shouldReportStorageFailure() {
        when(durgStrangthInter.createDrugStrangth(anyList())).thenThrow(new RuntimeException("already on file"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.createDrugStrangth("[{\"drugStrength\":\"500 mg\"}]")));
    }

    @Test
    @DisplayName("getDrugStrangth should answer the strengths on file")
    void get_shouldAnswerStrengthsOnFile() {
        when(durgStrangthInter.getDrugStrangth()).thenReturn(new ArrayList<>(List.of(strength())));

        assertSuccessContaining(controller.getDrugStrangth("{}"), "500 mg");
    }

    @Test
    @DisplayName("getDrugStrangth should report the failure when the list cannot be answered")
    void get_shouldReportLookupFailure() {
        when(durgStrangthInter.getDrugStrangth()).thenThrow(new RuntimeException("connection reset"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getDrugStrangth("{}")));
    }

    @Test
    @DisplayName("updateDrugStrangth should answer the strength whose details it changed")
    void update_shouldAnswerChangedStrength() {
        M_104DrugStrength stored = strength();
        when(durgStrangthInter.updateDrugStrangth(STRENGTH_ID)).thenReturn(stored);
        when(durgStrangthInter.saveupdatedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.updateDrugStrangth(
                "{\"drugStrengthID\":33,\"drugStrength\":\"650 mg\",\"drugStrengthDesc\":\"Higher dose\","
                        + "\"modifiedBy\":\"admin\"}"),
                "650 mg");
        assertEquals("Higher dose", stored.getDrugStrengthDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("updateDrugStrangth should report the failure when the strength is unknown")
    void update_shouldReportUnknownStrength() {
        when(durgStrangthInter.updateDrugStrangth(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION,
                statusCodeOf(controller.updateDrugStrangth("{\"drugStrengthID\":-1}")));
    }

    @Test
    @DisplayName("deleteDrugStrangth should answer the strength it retired")
    void delete_shouldAnswerRetiredStrength() {
        M_104DrugStrength stored = strength();
        when(durgStrangthInter.updateDrugStrangth(STRENGTH_ID)).thenReturn(stored);
        when(durgStrangthInter.saveupdatedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteDrugStrangth("{\"drugStrengthID\":33,\"deleted\":true}"), "33");
        assertEquals(Boolean.TRUE, stored.getDeleted());
    }

    @Test
    @DisplayName("deleteDrugStrangth should report the failure when the strength is unknown")
    void delete_shouldReportUnknownStrength() {
        when(durgStrangthInter.updateDrugStrangth(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION,
                statusCodeOf(controller.deleteDrugStrangth("{\"drugStrengthID\":-1,\"deleted\":true}")));
    }
}
