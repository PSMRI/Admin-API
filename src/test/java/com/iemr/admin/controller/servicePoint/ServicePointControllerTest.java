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
package com.iemr.admin.controller.servicePoint;

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
import com.iemr.admin.data.servicePoint.M_Servicepoint;
import com.iemr.admin.data.servicePoint.M_Servicepointvillagemap;
import com.iemr.admin.service.servicePoint.ServicePointServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The service point endpoints keep the points a mobile unit halts at and the
 * villages each point covers.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ServicePointController Test Suite")
class ServicePointControllerTest {

    private static final Integer POINT_ID = 71;

    @Mock
    private ServicePointServiceImpl ServicePointServiceImpl;

    @InjectMocks
    private ServicePointController controller;

    private static M_Servicepoint point(Integer id, String name) {
        return new M_Servicepoint(id, name, "Weekly halt", "Main Road", 4001, Boolean.FALSE, 1, "India",
                29, "Karnataka", 301, "Bengaluru Urban", 401, "North block", 501, "Hosur", null, 3,
                "Mobile Medical Unit", 31, "Hosur parking");
    }

    private static M_Servicepointvillagemap villageMap(Integer id) {
        return new M_Servicepointvillagemap(id, 29, "Karnataka", 301, "Bengaluru Urban", 31, "Hosur parking",
                71, "Hosur halt", 501, "Hosur", 4001, Boolean.FALSE);
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
    @DisplayName("saveServicePoint should answer the service points the service stored")
    void saveServicePoint_shouldAnswerStoredPoints() throws Exception {
        when(ServicePointServiceImpl.saveServicePoint(anyList()))
                .thenReturn(new ArrayList<>(List.of(point(POINT_ID, "Hosur halt"))));

        assertSuccessContaining(
                controller.saveServicePoint("{\"servicePoints\":[{\"servicePointName\":\"Hosur halt\"}]}"),
                "Hosur halt");
    }

    @Test
    @DisplayName("saveServicePoint should answer an error envelope when the store fails")
    void saveServicePoint_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(ServicePointServiceImpl.saveServicePoint(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveServicePoint("{\"servicePoints\":[{}]}"));
    }

    @Test
    @DisplayName("getServicePoints should answer the points matching the location filters")
    void getServicePoints_shouldAnswerMatchingPoints() throws Exception {
        when(ServicePointServiceImpl.getAvailableServicePoints(29, 301, 31, 77))
                .thenReturn(new ArrayList<>(List.of(point(POINT_ID, "Hosur halt"))));

        assertSuccessContaining(controller.getServicePoints("{\"stateID\":29,\"districtID\":301,"
                + "\"parkingPlaceID\":31,\"serviceProviderID\":77}"), "Hosur halt");
    }

    @Test
    @DisplayName("getServicePoints should answer an error envelope when the lookup fails")
    void getServicePoints_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(ServicePointServiceImpl.getAvailableServicePoints(any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getServicePoints("{\"stateID\":29}"));
    }

    @Test
    @DisplayName("deleteServicePoint should report whether the point actually changed")
    void deleteServicePoint_shouldReportOutcome() throws Exception {
        when(ServicePointServiceImpl.updateServicePointStatus(any())).thenReturn(1);
        assertSuccessContaining(controller.deleteServicePoint("{\"servicePointID\":71,\"deleted\":true}"),
                "status updated successfully");

        when(ServicePointServiceImpl.updateServicePointStatus(any())).thenReturn(0);
        assertSuccessContaining(controller.deleteServicePoint("{\"servicePointID\":71,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteServicePoint should answer an error envelope when the change fails")
    void deleteServicePoint_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(ServicePointServiceImpl.updateServicePointStatus(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.deleteServicePoint("{\"servicePointID\":71,\"deleted\":true}"));
    }

    @Test
    @DisplayName("editServicePoint should copy the edits onto the stored point")
    void editServicePoint_shouldCopyEdits() throws Exception {
        M_Servicepoint stored = point(POINT_ID, "old name");
        when(ServicePointServiceImpl.getdataForEditServicePointStatus(POINT_ID)).thenReturn(stored);
        when(ServicePointServiceImpl.saveeditedData(stored)).thenReturn(stored);

        String response = controller.editServicePoint("{\"servicePointID\":71,"
                + "\"servicePointName\":\"Hosur halt\",\"servicePointDesc\":\"Weekly halt\","
                + "\"districtID\":301,\"districtBlockID\":401,\"servicePointHQAddress\":\"Main Road\","
                + "\"providerServiceMapID\":4001,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "Hosur halt");
        assertEquals("Weekly halt", stored.getServicePointDesc());
        assertEquals("Main Road", stored.getServicePointHQAddress());
    }

    @Test
    @DisplayName("editServicePoint should answer an error envelope for a point that does not exist")
    void editServicePoint_shouldAnswerErrorEnvelopeForUnknownPoint() throws Exception {
        when(ServicePointServiceImpl.getdataForEditServicePointStatus(POINT_ID)).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION,
                statusCodeOf(controller.editServicePoint("{\"servicePointID\":71}")));
    }

    @Test
    @DisplayName("saveServicePointVillageMap should answer the village maps the service stored")
    void saveVillageMap_shouldAnswerStoredMaps() throws Exception {
        when(ServicePointServiceImpl.saveServicePointVillageMap(anyList()))
                .thenReturn(new ArrayList<>(List.of(villageMap(9001))));

        assertSuccessContaining(controller.saveServicePointVillageMap(
                "{\"servicePointVillageMaps\":[{\"servicePointID\":71,\"districtBranchID\":501}]}"), "9001");
    }

    @Test
    @DisplayName("saveServicePointVillageMap should answer an error envelope when the store fails")
    void saveVillageMap_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(ServicePointServiceImpl.saveServicePointVillageMap(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveServicePointVillageMap("{\"servicePointVillageMaps\":[{}]}"));
    }

    @Test
    @DisplayName("getServicePointVillageMaps should answer the maps matching the location filters")
    void getVillageMaps_shouldAnswerMatchingMaps() throws Exception {
        when(ServicePointServiceImpl.getAvailableServicePointVillageMaps(29, 301, 31, POINT_ID, 77))
                .thenReturn(new ArrayList<>(List.of(villageMap(9001))));

        assertSuccessContaining(controller.getServicePointVillageMaps("{\"stateID\":29,\"districtID\":301,"
                + "\"parkingPlaceID\":31,\"servicePointID\":71,\"serviceProviderID\":77}"), "9001");
    }

    @Test
    @DisplayName("getServicePointVillageMaps should answer an error envelope when the lookup fails")
    void getVillageMaps_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(ServicePointServiceImpl.getAvailableServicePointVillageMaps(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getServicePointVillageMaps("{\"stateID\":29}"));
    }

    @Test
    @DisplayName("deleteServicePointVillageMap should report whether the map actually changed")
    void deleteVillageMap_shouldReportOutcome() throws Exception {
        when(ServicePointServiceImpl.updateServicePointVillageMapStatus(any(M_Servicepointvillagemap.class)))
                .thenReturn(1);
        assertSuccessContaining(
                controller.deleteServicePointVillageMap("{\"servicePointVillageMapID\":9001,\"deleted\":true}"),
                "status updated successfully");

        when(ServicePointServiceImpl.updateServicePointVillageMapStatus(any(M_Servicepointvillagemap.class)))
                .thenReturn(0);
        assertSuccessContaining(
                controller.deleteServicePointVillageMap("{\"servicePointVillageMapID\":9001,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("editServicePointVillageMap should copy the edits onto the stored map")
    void editVillageMap_shouldCopyEdits() throws Exception {
        M_Servicepointvillagemap stored = villageMap(9001);
        when(ServicePointServiceImpl.updateServicePointVillageMapStatus(9001)).thenReturn(stored);
        when(ServicePointServiceImpl.saveEditedData(stored)).thenReturn(stored);

        String response = controller.editServicePointVillageMap("{\"servicePointVillageMapID\":9001,"
                + "\"servicePointID\":72,\"districtBranchID\":502,\"providerServiceMapID\":4001,"
                + "\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "9001");
        assertEquals(72, stored.getServicePointID());
        assertEquals(502, stored.getDistrictBranchID());
    }

    @Test
    @DisplayName("editServicePointVillageMap should answer an error envelope for a map that does not exist")
    void editVillageMap_shouldAnswerErrorEnvelopeForUnknownMap() throws Exception {
        when(ServicePointServiceImpl.updateServicePointVillageMapStatus(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION,
                statusCodeOf(controller.editServicePointVillageMap("{\"servicePointVillageMapID\":9001}")));
    }

    @Test
    @DisplayName("unmappedvillages should answer the villages no service point covers yet")
    void unmappedvillages_shouldAnswerUncoveredVillages() throws Exception {
        when(ServicePointServiceImpl.getunmappedvillages(4001, 401))
                .thenReturn(List.of(new DistrictBranchMapping()));

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(controller.unmappedvillages(
                "{\"providerServiceMapID\":4001,\"districtBlockID\":401}")));
    }

    @Test
    @DisplayName("unmappedvillages should answer an error envelope when the lookup fails")
    void unmappedvillages_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(ServicePointServiceImpl.getunmappedvillages(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.unmappedvillages("{\"providerServiceMapID\":4001}"));
    }
}
