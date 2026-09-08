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
package com.iemr.admin.controller.uom;

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

import com.iemr.admin.data.uom.M_Uom;
import com.iemr.admin.service.uom.UomInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/** The unit of measure master endpoints keep the unit of measure catalogue an inventory operator picks from. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UomController Test Suite")
class UomControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer RECORD_ID = 11;

    @Mock
    private UomInter uomInter;

    @InjectMocks
    private UomController controller;

    private static M_Uom record(Integer id, String name) {
        M_Uom record = new M_Uom();
        record.setuOMID(id);
        record.setuOMName(name);
        return record;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    private static void assertGenericFailure(String response) {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("createUom should answer the records the service stored")
    void create_shouldAnswerStoredRecords() {
        when(uomInter.createDrugtypeData(anyList())).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "Tablet"))));

        assertSuccessContaining(controller.createUom("[{\"uOMCode\":\"C-1\"}]"), "Tablet");
    }

    @Test
    @DisplayName("createUom should answer an error envelope when the store fails")
    void create_shouldAnswerErrorEnvelopeOnFailure() {
        when(uomInter.createDrugtypeData(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createUom("[{}]"));
    }

    @Test
    @DisplayName("getUom should answer the records of the provider")
    void get_shouldAnswerProviderRecords() {
        when(uomInter.createDrugtypeData(PSM_ID)).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "Tablet"))));

        assertSuccessContaining(controller.getUom("{\"providerServiceMapID\":4001}"), "Tablet");
    }

    @Test
    @DisplayName("getUom should answer an error envelope when the lookup fails")
    void get_shouldAnswerErrorEnvelopeOnFailure() {
        when(uomInter.createDrugtypeData(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getUom("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("editUom should copy the edits onto the stored record")
    void edit_shouldCopyEdits() {
        M_Uom stored = record(RECORD_ID, "Tablet");
        when(uomInter.editDrugtypeData(RECORD_ID)).thenReturn(stored);
        when(uomInter.saveeditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editUom("{\"uOMID\":11,\"uOMName\":\"Tablet\",\"uOMDesc\":\"One tablet\",\"uOMCode\":\"TAB\",\"status\":\"Active\",\"modifiedBy\":\"admin\"}"), "Tablet");
        assertEquals("One tablet", stored.getuOMDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("editUom should answer an error envelope for a record that does not exist")
    void edit_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(uomInter.editDrugtypeData(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.editUom("{\"uOMID\":11,\"uOMName\":\"Tablet\",\"uOMDesc\":\"One tablet\",\"uOMCode\":\"TAB\",\"status\":\"Active\",\"modifiedBy\":\"admin\"}")));
    }

    @Test
    @DisplayName("deleteUom should mark the record deleted")
    void delete_shouldMarkRecordDeleted() {
        M_Uom stored = record(RECORD_ID, "Tablet");
        when(uomInter.editDrugtypeData(RECORD_ID)).thenReturn(stored);
        when(uomInter.saveeditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteUom("{\"uOMID\":11,\"deleted\":true}"), "Tablet");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteUom should answer an error envelope for a record that does not exist")
    void delete_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(uomInter.editDrugtypeData(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.deleteUom("{\"uOMID\":11,\"deleted\":true}")));
    }

    @Test
    @DisplayName("checkUomCode should report whether the code is already taken")
    void check_shouldReportWhetherCodeIsTaken() {
        when(uomInter.checkUomCode(any())).thenReturn(Boolean.TRUE);

        assertSuccessContaining(controller.checkUomCode("{\"uOMCode\":\"C-1\"}"), "true");
    }

    @Test
    @DisplayName("checkUomCode should answer an error envelope when the check fails")
    void check_shouldAnswerErrorEnvelopeOnFailure() {
        when(uomInter.checkUomCode(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkUomCode("{\"uOMCode\":\"C-1\"}"));
    }
}
