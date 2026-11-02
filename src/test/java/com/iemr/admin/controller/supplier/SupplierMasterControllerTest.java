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
package com.iemr.admin.controller.supplier;

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

import com.iemr.admin.data.supplier.M_Supplier;
import com.iemr.admin.service.supplier.SupplierInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/** The supplier master endpoints keep the supplier catalogue an inventory operator picks from. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SupplierMasterController Test Suite")
class SupplierMasterControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer RECORD_ID = 11;

    @Mock
    private SupplierInter supplierInter;

    @InjectMocks
    private SupplierMasterController controller;

    private static M_Supplier record(Integer id, String name) {
        M_Supplier record = new M_Supplier();
        record.setSupplierID(id);
        record.setSupplierName(name);
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
    @DisplayName("createSupplier should answer the records the service stored")
    void create_shouldAnswerStoredRecords() {
        when(supplierInter.createSupplier(anyList())).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "MedSupply"))));

        assertSuccessContaining(controller.createSupplier("[{\"supplierCode\":\"C-1\"}]"), "MedSupply");
    }

    @Test
    @DisplayName("createSupplier should answer an error envelope when the store fails")
    void create_shouldAnswerErrorEnvelopeOnFailure() {
        when(supplierInter.createSupplier(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createSupplier("[{}]"));
    }

    @Test
    @DisplayName("getSupplier should answer the records of the provider")
    void get_shouldAnswerProviderRecords() {
        when(supplierInter.getSupplier(PSM_ID)).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "MedSupply"))));

        assertSuccessContaining(controller.getSupplier("{\"providerServiceMapID\":4001}"), "MedSupply");
    }

    @Test
    @DisplayName("getSupplier should answer an error envelope when the lookup fails")
    void get_shouldAnswerErrorEnvelopeOnFailure() {
        when(supplierInter.getSupplier(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getSupplier("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("editSupplier should copy the edits onto the stored record")
    void edit_shouldCopyEdits() {
        M_Supplier stored = record(RECORD_ID, "MedSupply");
        when(supplierInter.editSupplier(RECORD_ID)).thenReturn(stored);
        when(supplierInter.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editSupplier("{\"supplierID\":11,\"supplierDesc\":\"Bulk supplier\",\"contactPerson\":\"Asha\",\"email\":\"asha@example.org\",\"modifiedBy\":\"admin\"}"), "MedSupply");
        assertEquals("Bulk supplier", stored.getSupplierDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("editSupplier should answer an error envelope for a record that does not exist")
    void edit_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(supplierInter.editSupplier(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.editSupplier("{\"supplierID\":11,\"supplierDesc\":\"Bulk supplier\",\"contactPerson\":\"Asha\",\"email\":\"asha@example.org\",\"modifiedBy\":\"admin\"}")));
    }

    @Test
    @DisplayName("deleteSupplier should mark the record deleted")
    void delete_shouldMarkRecordDeleted() {
        M_Supplier stored = record(RECORD_ID, "MedSupply");
        when(supplierInter.editSupplier(RECORD_ID)).thenReturn(stored);
        when(supplierInter.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteSupplier("{\"supplierID\":11,\"deleted\":true}"), "MedSupply");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteSupplier should answer an error envelope for a record that does not exist")
    void delete_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(supplierInter.editSupplier(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.deleteSupplier("{\"supplierID\":11,\"deleted\":true}")));
    }

    @Test
    @DisplayName("checkSupplierCode should report whether the code is already taken")
    void check_shouldReportWhetherCodeIsTaken() {
        when(supplierInter.checkSupplierCode(any())).thenReturn(Boolean.TRUE);

        assertSuccessContaining(controller.checkSupplierCode("{\"supplierCode\":\"C-1\"}"), "true");
    }

    @Test
    @DisplayName("checkSupplierCode should answer an error envelope when the check fails")
    void check_shouldAnswerErrorEnvelopeOnFailure() {
        when(supplierInter.checkSupplierCode(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkSupplierCode("{\"supplierCode\":\"C-1\"}"));
    }
}
