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
package com.iemr.admin.controller.manufacturer;

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

import com.iemr.admin.data.manufacturer.M_Manufacturer;
import com.iemr.admin.service.manufacturer.ManufacturerInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/** The manufacturer master endpoints keep the manufacturer catalogue an inventory operator picks from. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ManufacturerController Test Suite")
class ManufacturerControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer RECORD_ID = 11;

    @Mock
    private ManufacturerInter manufacturerInter;

    @InjectMocks
    private ManufacturerController controller;

    private static M_Manufacturer record(Integer id, String name) {
        M_Manufacturer record = new M_Manufacturer();
        record.setManufacturerID(id);
        record.setManufacturerName(name);
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
    @DisplayName("createManufacturer should answer the records the service stored")
    void create_shouldAnswerStoredRecords() {
        when(manufacturerInter.createManufacturer(anyList())).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "Cipla"))));

        assertSuccessContaining(controller.createManufacturer("[{\"manufacturerCode\":\"C-1\"}]"), "Cipla");
    }

    @Test
    @DisplayName("createManufacturer should answer an error envelope when the store fails")
    void create_shouldAnswerErrorEnvelopeOnFailure() {
        when(manufacturerInter.createManufacturer(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createManufacturer("[{}]"));
    }

    @Test
    @DisplayName("getManufacturer should answer the records of the provider")
    void get_shouldAnswerProviderRecords() {
        when(manufacturerInter.createManufacturer(PSM_ID)).thenReturn(new ArrayList<>(List.of(record(RECORD_ID, "Cipla"))));

        assertSuccessContaining(controller.getManufacturer("{\"providerServiceMapID\":4001}"), "Cipla");
    }

    @Test
    @DisplayName("getManufacturer should answer an error envelope when the lookup fails")
    void get_shouldAnswerErrorEnvelopeOnFailure() {
        when(manufacturerInter.createManufacturer(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getManufacturer("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("editManufacturer should copy the edits onto the stored record")
    void edit_shouldCopyEdits() {
        M_Manufacturer stored = record(RECORD_ID, "Cipla");
        when(manufacturerInter.editManufacturer(RECORD_ID)).thenReturn(stored);
        when(manufacturerInter.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editManufacturer("{\"manufacturerID\":11,\"manufacturerDesc\":\"Generic maker\",\"contactPerson\":\"Asha\",\"modifiedBy\":\"admin\"}"), "Cipla");
        assertEquals("Generic maker", stored.getManufacturerDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("editManufacturer should answer an error envelope for a record that does not exist")
    void edit_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(manufacturerInter.editManufacturer(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.editManufacturer("{\"manufacturerID\":11,\"manufacturerDesc\":\"Generic maker\",\"contactPerson\":\"Asha\",\"modifiedBy\":\"admin\"}")));
    }

    @Test
    @DisplayName("deleteManufacturer should mark the record deleted")
    void delete_shouldMarkRecordDeleted() {
        M_Manufacturer stored = record(RECORD_ID, "Cipla");
        when(manufacturerInter.editManufacturer(RECORD_ID)).thenReturn(stored);
        when(manufacturerInter.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteManufacturer("{\"manufacturerID\":11,\"deleted\":true}"), "Cipla");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteManufacturer should answer an error envelope for a record that does not exist")
    void delete_shouldAnswerErrorEnvelopeForUnknownRecord() {
        when(manufacturerInter.editManufacturer(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.deleteManufacturer("{\"manufacturerID\":11,\"deleted\":true}")));
    }

    @Test
    @DisplayName("checkManufacturerCode should report whether the code is already taken")
    void check_shouldReportWhetherCodeIsTaken() {
        when(manufacturerInter.checkManufacturerCode(any())).thenReturn(Boolean.TRUE);

        assertSuccessContaining(controller.checkManufacturerCode("{\"manufacturerCode\":\"C-1\"}"), "true");
    }

    @Test
    @DisplayName("checkManufacturerCode should answer an error envelope when the check fails")
    void check_shouldAnswerErrorEnvelopeOnFailure() {
        when(manufacturerInter.checkManufacturerCode(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkManufacturerCode("{\"manufacturerCode\":\"C-1\"}"));
    }
}
