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
package com.iemr.admin.controller.drugtype;

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

import com.iemr.admin.data.drugtype.M_Drugtype;
import com.iemr.admin.service.drugtype.DrugtypeInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The drug type screen keeps the dosage forms a provider stocks.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DrugtypeController Test Suite")
class DrugtypeControllerTest {

    private static final Integer DRUG_TYPE_ID = 21;

    @Mock
    private DrugtypeInter drugtypeInter;

    @InjectMocks
    private DrugtypeController controller;

    private static M_Drugtype drugType() {
        M_Drugtype drugType = new M_Drugtype();
        drugType.setDrugTypeID(DRUG_TYPE_ID);
        drugType.setDrugTypeName("Tablet");
        drugType.setDrugTypeCode("TAB");
        drugType.setProviderServiceMapID(4001);
        return drugType;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("createManufacturer should answer the drug types it added")
    void create_shouldAnswerAddedDrugTypes() {
        when(drugtypeInter.createDrugtypeData(anyList())).thenReturn(new ArrayList<>(List.of(drugType())));

        assertSuccessContaining(
                controller.createManufacturer("[{\"drugTypeName\":\"Tablet\",\"drugTypeCode\":\"TAB\"}]"), "Tablet");
    }

    @Test
    @DisplayName("createManufacturer should report the failure when the drug type cannot be added")
    void create_shouldReportStorageFailure() {
        when(drugtypeInter.createDrugtypeData(anyList())).thenThrow(new RuntimeException("already on file"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.createManufacturer("[{\"drugTypeName\":\"Tablet\"}]")));
    }

    @Test
    @DisplayName("getManufacturer should answer the drug types the provider stocks")
    void get_shouldAnswerStockedDrugTypes() {
        when(drugtypeInter.getDrugtypeData(4001)).thenReturn(new ArrayList<>(List.of(drugType())));

        assertSuccessContaining(controller.getManufacturer("{\"providerServiceMapID\":4001}"), "Tablet");
    }

    @Test
    @DisplayName("getManufacturer should report the failure when the list cannot be answered")
    void get_shouldReportLookupFailure() {
        when(drugtypeInter.getDrugtypeData(any())).thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getManufacturer("{\"providerServiceMapID\":4001}")));
    }

    @Test
    @DisplayName("editManufacturer should answer the drug type whose details it changed")
    void edit_shouldAnswerChangedDrugType() {
        M_Drugtype stored = drugType();
        when(drugtypeInter.editDrugtypeData(DRUG_TYPE_ID)).thenReturn(stored);
        when(drugtypeInter.saveeditDrugtype(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editManufacturer(
                "{\"drugTypeID\":21,\"drugTypeName\":\"Capsule\",\"drugTypeCode\":\"CAP\",\"status\":\"A\","
                        + "\"modifiedBy\":\"admin\"}"),
                "Capsule");
        assertEquals("CAP", stored.getDrugTypeCode());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("editManufacturer should report the failure when the drug type is unknown")
    void edit_shouldReportUnknownDrugType() {
        when(drugtypeInter.editDrugtypeData(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION,
                statusCodeOf(controller.editManufacturer("{\"drugTypeID\":-1}")));
    }

    @Test
    @DisplayName("deleteManufacturer should answer the drug type it retired")
    void delete_shouldAnswerRetiredDrugType() {
        M_Drugtype stored = drugType();
        when(drugtypeInter.editDrugtypeData(DRUG_TYPE_ID)).thenReturn(stored);
        when(drugtypeInter.saveeditDrugtype(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteManufacturer("{\"drugTypeID\":21,\"deleted\":true}"), "21");
        assertEquals(Boolean.TRUE, stored.getDeleted());
    }

    @Test
    @DisplayName("deleteManufacturer should report the failure when the drug type is unknown")
    void delete_shouldReportUnknownDrugType() {
        when(drugtypeInter.editDrugtypeData(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION,
                statusCodeOf(controller.deleteManufacturer("{\"drugTypeID\":-1,\"deleted\":true}")));
    }
}
