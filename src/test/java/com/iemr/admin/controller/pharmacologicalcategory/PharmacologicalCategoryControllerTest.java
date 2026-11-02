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
package com.iemr.admin.controller.pharmacologicalcategory;

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

import com.iemr.admin.data.pharmacologicalcategory.M_Pharmacologicalcategory;
import com.iemr.admin.service.pharmacologicalcategory.PharmacologicalcategoryInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/** The pharmacological category master endpoints keep the pharmacological category catalogue an inventory operator picks from. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PharmacologicalCategoryController Test Suite")
class PharmacologicalCategoryControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer RECORD_ID = 11;

    @Mock
    private PharmacologicalcategoryInter pharmacologicalcategoryInter;

    @InjectMocks
    private PharmacologicalCategoryController controller;

    private static M_Pharmacologicalcategory record(Integer id, String name) {
        M_Pharmacologicalcategory record = new M_Pharmacologicalcategory();
        record.setPharmacologyCategoryID(id);
        record.setPharmCategoryName(name);
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
    @DisplayName("createPharmacologicalcategory should answer the records the service stored")
    void create_shouldAnswerStoredRecords() {
        when(pharmacologicalcategoryInter.createPharmacologicalcategory(anyList())).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "Analgesic"))));

        assertSuccessContaining(controller.createPharmacologicalcategory("[{\"pharmCategoryCode\":\"C-1\"}]"), "Analgesic");
    }

    @Test
    @DisplayName("createPharmacologicalcategory should answer an error envelope when the store fails")
    void create_shouldAnswerErrorEnvelopeOnFailure() {
        when(pharmacologicalcategoryInter.createPharmacologicalcategory(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createPharmacologicalcategory("[{}]"));
    }

    @Test
    @DisplayName("getPharmacologicalcategory should answer the records of the provider")
    void get_shouldAnswerProviderRecords() {
        when(pharmacologicalcategoryInter.getPharmacologicalcategory(PSM_ID)).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "Analgesic"))));

        assertSuccessContaining(controller.getPharmacologicalcategory("{\"providerServiceMapID\":4001}"), "Analgesic");
    }

    @Test
    @DisplayName("getPharmacologicalcategory should answer an error envelope when the lookup fails")
    void get_shouldAnswerErrorEnvelopeOnFailure() {
        when(pharmacologicalcategoryInter.getPharmacologicalcategory(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getPharmacologicalcategory("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("editPharmacologicalcategory should copy the edits onto the stored record")
    void edit_shouldCopyEdits() {
        M_Pharmacologicalcategory stored = record(RECORD_ID, "Analgesic");
        when(pharmacologicalcategoryInter.editPharmacologicalcategory(RECORD_ID)).thenReturn(stored);
        when(pharmacologicalcategoryInter.saveEditedPharData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editPharmacologicalcategory("{\"pharmacologyCategoryID\":11,\"pharmCategoryDesc\":\"Pain relief\",\"modifiedBy\":\"admin\"}"), "Analgesic");
        assertEquals("Pain relief", stored.getPharmCategoryDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("editPharmacologicalcategory should answer an error envelope for a record that does not exist")
    void edit_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(pharmacologicalcategoryInter.editPharmacologicalcategory(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.editPharmacologicalcategory("{\"pharmacologyCategoryID\":11,\"pharmCategoryDesc\":\"Pain relief\",\"modifiedBy\":\"admin\"}")));
    }

    @Test
    @DisplayName("deletePharmacologicalcategory should mark the record deleted")
    void delete_shouldMarkRecordDeleted() {
        M_Pharmacologicalcategory stored = record(RECORD_ID, "Analgesic");
        when(pharmacologicalcategoryInter.editPharmacologicalcategory(RECORD_ID)).thenReturn(stored);
        when(pharmacologicalcategoryInter.saveEditedPharData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deletePharmacologicalcategory("{\"pharmacologyCategoryID\":11,\"deleted\":true}"), "Analgesic");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deletePharmacologicalcategory should answer an error envelope for a record that does not exist")
    void delete_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(pharmacologicalcategoryInter.editPharmacologicalcategory(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.deletePharmacologicalcategory("{\"pharmacologyCategoryID\":11,\"deleted\":true}")));
    }

    @Test
    @DisplayName("checkPharmacologicalcategoryCode should report whether the code is already taken")
    void check_shouldReportWhetherCodeIsTaken() {
        when(pharmacologicalcategoryInter.checkPharmacologicalcategoryCode(any())).thenReturn(Boolean.TRUE);

        assertSuccessContaining(controller.checkPharmacologicalcategoryCode("{\"pharmCategoryCode\":\"C-1\"}"), "true");
    }

    @Test
    @DisplayName("checkPharmacologicalcategoryCode should answer an error envelope when the check fails")
    void check_shouldAnswerErrorEnvelopeOnFailure() {
        when(pharmacologicalcategoryInter.checkPharmacologicalcategoryCode(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkPharmacologicalcategoryCode("{\"pharmCategoryCode\":\"C-1\"}"));
    }
}
