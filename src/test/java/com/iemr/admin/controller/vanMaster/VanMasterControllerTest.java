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
package com.iemr.admin.controller.vanMaster;

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

import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.data.vanType.M_VanType;
import com.iemr.admin.service.vanMaster.VanMasterServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The van screens keep the mobile unit fleet: what vans exist, what type each
 * is, and which parking place or main store each belongs to.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VanMasterController Test Suite")
class VanMasterControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer VAN_ID = 71;
    private static final Integer PARKING_PLACE_ID = 31;

    @Mock
    private VanMasterServiceImpl vanMasterServiceImpl;

    @InjectMocks
    private VanMasterController controller;

    private static M_Van van() {
        M_Van van = new M_Van();
        van.setVanID(VAN_ID);
        van.setVanName("Mobile unit 7");
        van.setVehicalNo("KA-01-AB-1234");
        van.setParkingPlaceID(PARKING_PLACE_ID);
        van.setProviderServiceMapID(PSM_ID);
        return van;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("saveVanDetails should answer the vans it added to the fleet")
    void saveVanDetails_shouldAnswerAddedVans() throws Exception {
        when(vanMasterServiceImpl.saveVanDetails(anyList())).thenReturn(new ArrayList<>(List.of(van())));

        assertSuccessContaining(controller.saveVanDetails(
                "{\"vanMaster\":[{\"vanName\":\"Mobile unit 7\",\"vehicalNo\":\"KA-01-AB-1234\"}]}"),
                "Mobile unit 7");
    }

    @Test
    @DisplayName("saveVanDetails should report the failure when the van cannot be added")
    void saveVanDetails_shouldReportStorageFailure() throws Exception {
        when(vanMasterServiceImpl.saveVanDetails(anyList()))
                .thenThrow(new RuntimeException("vehicle number already on file"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.saveVanDetails("{\"vanMaster\":[{\"vanName\":\"Mobile unit 7\"}]}")));
    }

    @Test
    @DisplayName("getServicePoints should answer the vans of the parking place and type asked for")
    void getVans_shouldAnswerVansOfParkingPlaceAndType() throws Exception {
        when(vanMasterServiceImpl.getAvailableVans(PARKING_PLACE_ID, 2, PSM_ID))
                .thenReturn(new ArrayList<>(List.of(van())));

        assertSuccessContaining(controller.getServicePoints(
                "{\"parkingPlaceID\":31,\"vanTypeID\":2,\"providerServiceMapID\":4001}"), "Mobile unit 7");
    }

    @Test
    @DisplayName("getServicePoints should report the failure when the fleet cannot be answered")
    void getVans_shouldReportLookupFailure() throws Exception {
        when(vanMasterServiceImpl.getAvailableVans(any(), any(), any()))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getServicePoints("{\"providerServiceMapID\":4001}")));
    }

    @Test
    @DisplayName("deleteVanDetails should confirm the retirement it recorded")
    void deleteVanDetails_shouldConfirmRetirement() throws Exception {
        when(vanMasterServiceImpl.updateVanStatus(any(M_Van.class))).thenReturn(1);

        assertSuccessContaining(controller.deleteVanDetails("{\"vanID\":71,\"deleted\":true,\"modifiedBy\":\"admin\"}"),
                "status updated successfully");
    }

    @Test
    @DisplayName("deleteVanDetails should say so when no van was retired")
    void deleteVanDetails_shouldSaySoWhenNothingRetired() throws Exception {
        when(vanMasterServiceImpl.updateVanStatus(any(M_Van.class))).thenReturn(0);

        assertSuccessContaining(controller.deleteVanDetails("{\"vanID\":-1,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteVanDetails should report the failure when the retirement cannot be recorded")
    void deleteVanDetails_shouldReportRetirementFailure() throws Exception {
        when(vanMasterServiceImpl.updateVanStatus(any(M_Van.class))).thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.deleteVanDetails("{\"vanID\":71}")));
    }

    @Test
    @DisplayName("updateZoneData should answer the van whose details it changed")
    void updateVanDetails_shouldAnswerChangedVan() throws Exception {
        M_Van stored = van();
        when(vanMasterServiceImpl.getVanByID(VAN_ID)).thenReturn(stored);
        when(vanMasterServiceImpl.updateVanData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.updateZoneData(
                "{\"vanID\":71,\"vanName\":\"Mobile unit 9\",\"vehicalNo\":\"KA-01-AB-9999\",\"vanTypeID\":2,"
                        + "\"stateID\":29,\"parkingPlaceID\":31,\"modifiedBy\":\"admin\","
                        + "\"videoConsultationDomain\":\"psmri\"}"),
                "Mobile unit 9");
        assertEquals("KA-01-AB-9999", stored.getVehicalNo());
        assertEquals("psmri", stored.getVideoConsultationDomain());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("updateZoneData should report the failure when the van is unknown")
    void updateVanDetails_shouldReportUnknownVan() throws Exception {
        when(vanMasterServiceImpl.getVanByID(anyInt())).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.updateZoneData("{\"vanID\":-1}")));
    }

    @Test
    @DisplayName("saveVanTypeDetails should answer the van types it added")
    void saveVanTypeDetails_shouldAnswerAddedTypes() throws Exception {
        when(vanMasterServiceImpl.saveVanTypeDetails(anyList())).thenReturn(
                new ArrayList<>(List.of(new M_VanType(2, "Diagnostic van", "Carries lab kit", Boolean.FALSE))));

        assertSuccessContaining(
                controller.saveVanTypeDetails("{\"vanTypeMaster\":[{\"vanType\":\"Diagnostic van\"}]}"),
                "Diagnostic van");
    }

    @Test
    @DisplayName("saveVanTypeDetails should report the failure when the van type cannot be added")
    void saveVanTypeDetails_shouldReportStorageFailure() throws Exception {
        when(vanMasterServiceImpl.saveVanTypeDetails(anyList())).thenThrow(new RuntimeException("already on file"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(
                controller.saveVanTypeDetails("{\"vanTypeMaster\":[{\"vanType\":\"Diagnostic van\"}]}")));
    }

    @Test
    @DisplayName("getVanTypes should answer the van types on file")
    void getVanTypes_shouldAnswerTypesOnFile() {
        when(vanMasterServiceImpl.getVanTypes()).thenReturn(
                new ArrayList<>(List.of(new M_VanType(2, "Diagnostic van", "Carries lab kit", Boolean.FALSE))));

        assertSuccessContaining(controller.getVanTypes(), "Diagnostic van");
    }

    @Test
    @DisplayName("getVanTypes should report the failure when the list cannot be answered")
    void getVanTypes_shouldReportLookupFailure() {
        when(vanMasterServiceImpl.getVanTypes()).thenThrow(new RuntimeException("connection reset"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getVanTypes()));
    }

    @Test
    @DisplayName("deleteVanType should confirm the retirement it recorded")
    void deleteVanType_shouldConfirmRetirement() throws Exception {
        when(vanMasterServiceImpl.updateVanTypeStatus(any(M_VanType.class))).thenReturn(1);

        assertSuccessContaining(controller.deleteVanType("{\"vanTypeID\":2,\"deleted\":true}"),
                "status updated successfully");
    }

    @Test
    @DisplayName("deleteVanType should say so when no van type was retired")
    void deleteVanType_shouldSaySoWhenNothingRetired() throws Exception {
        when(vanMasterServiceImpl.updateVanTypeStatus(any(M_VanType.class))).thenReturn(0);

        assertSuccessContaining(controller.deleteVanType("{\"vanTypeID\":-1,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteVanType should report the failure when the retirement cannot be recorded")
    void deleteVanType_shouldReportRetirementFailure() throws Exception {
        when(vanMasterServiceImpl.updateVanTypeStatus(any(M_VanType.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.deleteVanType("{\"vanTypeID\":2}")));
    }

    @Test
    @DisplayName("getVanMaster should answer the vans of the provider and parking place asked for")
    void getVanMaster_shouldAnswerVansOfProviderAndParkingPlace() throws Exception {
        when(vanMasterServiceImpl.getVanMaster(PSM_ID, PARKING_PLACE_ID)).thenReturn(List.of(van()));

        assertSuccessContaining(
                controller.getVanMaster("{\"providerServiceMapID\":4001,\"parkingPlaceID\":31}"), "Mobile unit 7");
    }

    @Test
    @DisplayName("getVanMaster should report the failure when the lookup cannot be answered")
    void getVanMaster_shouldReportLookupFailure() throws Exception {
        when(vanMasterServiceImpl.getVanMaster(any(), any())).thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getVanMaster("{\"providerServiceMapID\":4001}")));
    }

    @Test
    @DisplayName("getVanFromFacilityID should answer the vans belonging to the store asked for")
    void getVanFromFacilityID_shouldAnswerVansOfStore() throws Exception {
        M_Van request = new M_Van();
        request.setFacilityID(9001);
        when(vanMasterServiceImpl.getVanFromFacilityID(9001)).thenReturn(List.of(van()));

        assertSuccessContaining(controller.getVanFromFacilityID(request), "Mobile unit 7");
    }

    @Test
    @DisplayName("getVanFromFacilityID should report the failure when the store has no parking place")
    void getVanFromFacilityID_shouldReportStoreWithoutParkingPlace() throws Exception {
        M_Van request = new M_Van();
        request.setFacilityID(9001);
        when(vanMasterServiceImpl.getVanFromFacilityID(9001))
                .thenThrow(new Exception("Main Store doesnt have any Parking place mapped"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getVanFromFacilityID(request)));
    }
}
