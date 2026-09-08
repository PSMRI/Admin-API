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
package com.iemr.admin.controller.vanServicePointMapping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.vanServicePointMapping.M_VanServicePointMap;
import com.iemr.admin.service.vanServicePointMapping.VanServicePointMappingServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The van service point screen records which service points each van visits,
 * adding new visits and changing the session of ones already on file.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VanServicePointMappingController Test Suite")
class VanServicePointMappingControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer VAN_ID = 71;
    private static final Integer MAP_ID = 8801;

    @Mock
    private VanServicePointMappingServiceImpl vanServicePointMappingServiceImpl;

    @InjectMocks
    private VanServicePointMappingController controller;

    private static M_VanServicePointMap visit() {
        return new M_VanServicePointMap(MAP_ID, VAN_ID, (short) 1, 88, "Attibele PHC", PSM_ID, Boolean.FALSE);
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("saveVanServicePointMappings should store a brand new visit as it stands")
    void save_shouldStoreBrandNewVisit() throws Exception {
        when(vanServicePointMappingServiceImpl.saveVanServicePointMappings(anyList()))
                .thenReturn(new ArrayList<>(List.of(visit())));

        assertSuccessContaining(controller.saveVanServicePointMappings(
                "{\"vanServicePointMappings\":[{\"vanID\":71,\"servicePointID\":88,\"vanSession\":1,"
                        + "\"createdBy\":\"admin\"}]}"),
                "Attibele PHC");
        verify(vanServicePointMappingServiceImpl, never()).getVanServicePointMappingByID(anyInt());
    }

    @Test
    @DisplayName("saveVanServicePointMappings should change the session of a visit already on file")
    void save_shouldChangeSessionOfExistingVisit() throws Exception {
        M_VanServicePointMap stored = visit();
        when(vanServicePointMappingServiceImpl.getVanServicePointMappingByID(MAP_ID)).thenReturn(stored);
        when(vanServicePointMappingServiceImpl.saveVanServicePointMappings(anyList()))
                .thenReturn(new ArrayList<>(List.of(stored)));

        controller.saveVanServicePointMappings(
                "{\"vanServicePointMappings\":[{\"vanServicePointMapID\":8801,\"vanSession\":2,"
                        + "\"createdBy\":\"supervisor\"}]}");

        ArgumentCaptor<List<M_VanServicePointMap>> captor = ArgumentCaptor.forClass(List.class);
        verify(vanServicePointMappingServiceImpl).saveVanServicePointMappings(captor.capture());
        assertEquals((short) 2, captor.getValue().get(0).getVanSession());
        assertEquals("supervisor", captor.getValue().get(0).getModifiedBy());
    }

    @Test
    @DisplayName("saveVanServicePointMappings should report the failure when the visit named is unknown")
    void save_shouldReportUnknownVisit() throws Exception {
        when(vanServicePointMappingServiceImpl.getVanServicePointMappingByID(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.saveVanServicePointMappings(
                "{\"vanServicePointMappings\":[{\"vanServicePointMapID\":8801,\"vanSession\":2}]}")));
    }

    @Test
    @DisplayName("saveVanServicePointMappings should report the failure when the visits cannot be stored")
    void save_shouldReportStorageFailure() throws Exception {
        when(vanServicePointMappingServiceImpl.saveVanServicePointMappings(anyList()))
                .thenThrow(new RuntimeException("service point already visited in that session"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveVanServicePointMappings(
                "{\"vanServicePointMappings\":[{\"vanID\":71,\"servicePointID\":88}]}")));
    }

    @Test
    @DisplayName("getVanServicePointMappings should answer the visits of the van asked about")
    void get_shouldAnswerVisitsOfVan() throws Exception {
        when(vanServicePointMappingServiceImpl.getAvailableVanServicePointMappings(31, VAN_ID, PSM_ID))
                .thenReturn(new ArrayList<>(List.of(visit())));

        assertSuccessContaining(controller.getVanServicePointMappings(
                "{\"parkingPlaceID\":31,\"vanID\":71,\"providerServiceMapID\":4001}"), "Attibele PHC");
    }

    @Test
    @DisplayName("getVanServicePointMappings should report the failure when the visits cannot be answered")
    void get_shouldReportLookupFailure() throws Exception {
        when(vanServicePointMappingServiceImpl.getAvailableVanServicePointMappings(any(), any(), any()))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getVanServicePointMappings("{\"vanID\":71}")));
    }

    @Test
    @DisplayName("vanServicePointMappingsV1 should answer the visits with their district and taluk")
    void getV1_shouldAnswerVisitsWithLocation() throws Exception {
        when(vanServicePointMappingServiceImpl.getAvailableVanServicePointMappingsV1(31, VAN_ID, PSM_ID))
                .thenReturn(new ArrayList<>(List.of(new M_VanServicePointMap(MAP_ID, VAN_ID, (short) 1, 88,
                        "Attibele PHC", PSM_ID, Boolean.FALSE, 301, "Bengaluru Urban", 3011, "Anekal"))));

        assertSuccessContaining(controller.vanServicePointMappingsV1(
                "{\"parkingPlaceID\":31,\"vanID\":71,\"providerServiceMapID\":4001}"), "Anekal");
    }

    @Test
    @DisplayName("vanServicePointMappingsV1 should report the failure when the visits cannot be answered")
    void getV1_shouldReportLookupFailure() throws Exception {
        when(vanServicePointMappingServiceImpl.getAvailableVanServicePointMappingsV1(any(), any(), any()))
                .thenThrow(new RuntimeException("connection reset"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.vanServicePointMappingsV1("{\"vanID\":71}")));
    }

    @Test
    @DisplayName("deleteVanServicePointMappingDetails should confirm the retirement it recorded")
    void delete_shouldConfirmRetirement() throws Exception {
        when(vanServicePointMappingServiceImpl.updateVanServicePointMappingStatus(any(M_VanServicePointMap.class)))
                .thenReturn(1);

        assertSuccessContaining(controller.deleteVanServicePointMappingDetails(
                "{\"vanServicePointMapID\":8801,\"deleted\":true,\"modifiedBy\":\"admin\"}"),
                "status updated successfully");
    }

    @Test
    @DisplayName("deleteVanServicePointMappingDetails should say so when no visit was retired")
    void delete_shouldSaySoWhenNothingRetired() throws Exception {
        when(vanServicePointMappingServiceImpl.updateVanServicePointMappingStatus(any(M_VanServicePointMap.class)))
                .thenReturn(0);

        assertSuccessContaining(
                controller.deleteVanServicePointMappingDetails("{\"vanServicePointMapID\":-1,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteVanServicePointMappingDetails should report the failure when the retirement cannot be recorded")
    void delete_shouldReportStorageFailure() throws Exception {
        when(vanServicePointMappingServiceImpl.updateVanServicePointMappingStatus(any(M_VanServicePointMap.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(
                controller.deleteVanServicePointMappingDetails("{\"vanServicePointMapID\":8801}")));
    }
}
