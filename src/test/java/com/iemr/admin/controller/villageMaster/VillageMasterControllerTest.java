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
package com.iemr.admin.controller.villageMaster;

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

import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.service.villageMaster.VillageMasterServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The village screen keeps the villages of a taluk and the details each one is
 * addressed by.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VillageMasterController Test Suite")
class VillageMasterControllerTest {

    private static final Integer BLOCK_ID = 3011;

    @Mock
    private VillageMasterServiceImpl villageMasterServiceImpl;

    @InjectMocks
    private VillageMasterController controller;

    private static DistrictBranchMapping village() {
        DistrictBranchMapping village = new DistrictBranchMapping();
        village.setDistrictBranchID(30111);
        village.setVillageName("Attibele");
        village.setPinCode("562107");
        return village;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("saveVillageDetails should answer the villages it added")
    void save_shouldAnswerAddedVillages() {
        when(villageMasterServiceImpl.storeVillageDetails(anyList()))
                .thenReturn(new ArrayList<>(List.of(village())));

        assertSuccessContaining(controller.saveVillageDetails(
                "{\"districtBranchMapping\":[{\"villageName\":\"Attibele\",\"blockID\":3011}]}"), "Attibele");
    }

    @Test
    @DisplayName("saveVillageDetails should report the failure when the village cannot be added")
    void save_shouldReportStorageFailure() {
        when(villageMasterServiceImpl.storeVillageDetails(anyList()))
                .thenThrow(new RuntimeException("village already on file"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveVillageDetails(
                "{\"districtBranchMapping\":[{\"villageName\":\"Attibele\"}]}")));
    }

    @Test
    @DisplayName("getVillages should answer the villages of the taluk asked about")
    void get_shouldAnswerVillagesOfTaluk() {
        when(villageMasterServiceImpl.getAvailableVillages(BLOCK_ID))
                .thenReturn(new ArrayList<>(List.of(village())));

        assertSuccessContaining(controller.getVillages("{\"blockID\":3011}"), "Attibele");
    }

    @Test
    @DisplayName("getVillages should report the failure when the villages cannot be answered")
    void get_shouldReportLookupFailure() {
        when(villageMasterServiceImpl.getAvailableVillages(any()))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getVillages("{\"blockID\":3011}")));
    }

    @Test
    @DisplayName("deleteVillage should confirm the retirement it recorded")
    void delete_shouldConfirmRetirement() {
        when(villageMasterServiceImpl.updateVillageStatus(any(DistrictBranchMapping.class))).thenReturn(1);

        assertSuccessContaining(
                controller.deleteVillage("{\"districtBranchID\":30111,\"deleted\":true,\"modifiedBy\":\"admin\"}"),
                "status updated successfully");
    }

    @Test
    @DisplayName("deleteVillage should say so when no village was retired")
    void delete_shouldSaySoWhenNothingRetired() {
        when(villageMasterServiceImpl.updateVillageStatus(any(DistrictBranchMapping.class))).thenReturn(0);

        assertSuccessContaining(controller.deleteVillage("{\"districtBranchID\":-1,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteVillage should report the failure when the retirement cannot be recorded")
    void delete_shouldReportStorageFailure() {
        when(villageMasterServiceImpl.updateVillageStatus(any(DistrictBranchMapping.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.deleteVillage("{\"districtBranchID\":30111}")));
    }

    @Test
    @DisplayName("updateVillageData should confirm the change it recorded")
    void update_shouldConfirmRecordedChange() {
        when(villageMasterServiceImpl.updateVillageData(any(DistrictBranchMapping.class))).thenReturn(1);

        assertSuccessContaining(controller.updateVillageData(
                "{\"districtBranchID\":30111,\"villageName\":\"Attibele\",\"pinCode\":\"562107\","
                        + "\"modifiedBy\":\"admin\"}"),
                "status updated successfully");
    }

    @Test
    @DisplayName("updateVillageData should say so when no village was changed")
    void update_shouldSaySoWhenNothingChanged() {
        when(villageMasterServiceImpl.updateVillageData(any(DistrictBranchMapping.class))).thenReturn(0);

        assertSuccessContaining(controller.updateVillageData("{\"districtBranchID\":-1}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("updateVillageData should report the failure when the change cannot be recorded")
    void update_shouldReportStorageFailure() {
        when(villageMasterServiceImpl.updateVillageData(any(DistrictBranchMapping.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.updateVillageData("{\"districtBranchID\":30111}")));
    }
}
