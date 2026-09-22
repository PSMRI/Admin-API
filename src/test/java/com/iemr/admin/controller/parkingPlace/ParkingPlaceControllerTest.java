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
package com.iemr.admin.controller.parkingPlace;

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

import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.service.parkingPlace.ParkingPlaceServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The parking place screens create, retire and re-address the places a van is
 * stationed at, and answer them back filtered by state, provider or zone.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ParkingPlaceController Test Suite")
class ParkingPlaceControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer PARKING_PLACE_ID = 31;

    @Mock
    private ParkingPlaceServiceImpl parkingPlaceServiceImpl;

    @InjectMocks
    private ParkingPlaceController controller;

    private static M_Parkingplace place() {
        M_Parkingplace place = new M_Parkingplace();
        place.setParkingPlaceID(PARKING_PLACE_ID);
        place.setParkingPlaceName("Hosur parking");
        place.setParkingPlaceDesc("Near the bus stand");
        place.setProviderServiceMapID(PSM_ID);
        return place;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    private static void assertFailure(String response, int expectedCode) {
        assertEquals(expectedCode, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("saveParkingPlace should answer the parking places it created")
    void saveParkingPlace_shouldAnswerCreatedPlaces() throws Exception {
        when(parkingPlaceServiceImpl.saveParkingPlace(anyList()))
                .thenReturn(new ArrayList<>(List.of(place())));

        String response = controller.saveParkingPlace(
                "{\"parkingPlaces\":[{\"parkingPlaceName\":\"Hosur parking\",\"providerServiceMapID\":4001}]}");

        assertSuccessContaining(response, "Hosur parking");
    }

    @Test
    @DisplayName("saveParkingPlace should report the failure when the parking place cannot be stored")
    void saveParkingPlace_shouldReportStorageFailure() throws Exception {
        when(parkingPlaceServiceImpl.saveParkingPlace(anyList()))
                .thenThrow(new RuntimeException("duplicate parking place"));

        String response = controller.saveParkingPlace("{\"parkingPlaces\":[{\"parkingPlaceName\":\"Hosur\"}]}");

        assertFailure(response, OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("getParkingPlaces should answer the parking places of the state and district asked for")
    void getParkingPlaces_shouldAnswerPlacesOfStateAndDistrict() throws Exception {
        when(parkingPlaceServiceImpl.getAvailableParkingPlaces(29, 301, 5))
                .thenReturn(new ArrayList<>(List.of(place())));

        String response = controller
                .getParkingPlaces("{\"stateID\":29,\"districtID\":301,\"serviceProviderID\":5}");

        assertSuccessContaining(response, "Hosur parking");
    }

    @Test
    @DisplayName("getParkingPlaces should report the failure when the lookup cannot be answered")
    void getParkingPlaces_shouldReportLookupFailure() throws Exception {
        when(parkingPlaceServiceImpl.getAvailableParkingPlaces(any(), any(), any()))
                .thenThrow(new RuntimeException("query timed out"));

        assertFailure(controller.getParkingPlaces("{\"stateID\":29}"), OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("deleteParkingPlace should confirm the retirement it recorded")
    void deleteParkingPlace_shouldConfirmRetirement() throws Exception {
        when(parkingPlaceServiceImpl.updateParkingPlaceStatus(any(M_Parkingplace.class))).thenReturn(1);

        String response = controller
                .deleteParkingPlace("{\"parkingPlaceID\":31,\"deleted\":true,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "status updated successfully");
    }

    @Test
    @DisplayName("deleteParkingPlace should say so when no parking place was retired")
    void deleteParkingPlace_shouldSaySoWhenNothingRetired() throws Exception {
        when(parkingPlaceServiceImpl.updateParkingPlaceStatus(any(M_Parkingplace.class))).thenReturn(0);

        String response = controller.deleteParkingPlace("{\"parkingPlaceID\":-1,\"deleted\":true}");

        assertSuccessContaining(response, "Failed to update the status");
    }

    @Test
    @DisplayName("deleteParkingPlace should report the failure when the retirement cannot be recorded")
    void deleteParkingPlace_shouldReportRetirementFailure() throws Exception {
        when(parkingPlaceServiceImpl.updateParkingPlaceStatus(any(M_Parkingplace.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertFailure(controller.deleteParkingPlace("{\"parkingPlaceID\":31}"), OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("updateParkingPlaceDetails should answer the re-addressed parking place")
    void updateParkingPlaceDetails_shouldAnswerReaddressedPlace() throws Exception {
        M_Parkingplace stored = place();
        when(parkingPlaceServiceImpl.getParkingPlaceByID(PARKING_PLACE_ID)).thenReturn(stored);
        when(parkingPlaceServiceImpl.updateParkingPlaceData(stored)).thenReturn(stored);

        String response = controller.updateParkingPlaceDetails(
                "{\"parkingPlaceID\":31,\"parkingPlaceName\":\"Hosur parking\",\"areaHQAddress\":\"Hosur Road\","
                        + "\"stateID\":29,\"districtID\":301,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "Hosur parking");
        assertEquals("Hosur Road", stored.getAreaHQAddress());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("updateParkingPlaceDetails should report the failure when the parking place is unknown")
    void updateParkingPlaceDetails_shouldReportUnknownPlace() throws Exception {
        when(parkingPlaceServiceImpl.getParkingPlaceByID(anyInt())).thenReturn(null);

        assertFailure(controller.updateParkingPlaceDetails("{\"parkingPlaceID\":-1}"),
                OutputResponse.CODE_EXCEPTION);
    }

    @Test
    @DisplayName("getParkingPlacesProviderserviceMap should answer the parking places of the provider asked for")
    void getParkingPlacesProviderserviceMap_shouldAnswerPlacesOfProvider() throws Exception {
        when(parkingPlaceServiceImpl.getParkingPlaces(PSM_ID)).thenReturn(List.of(place()));

        assertSuccessContaining(controller.getParkingPlacesProviderserviceMap("{\"providerServiceMapID\":4001}"),
                "Hosur parking");
    }

    @Test
    @DisplayName("getParkingPlacesProviderserviceMap should report the failure when the lookup cannot be answered")
    void getParkingPlacesProviderserviceMap_shouldReportLookupFailure() throws Exception {
        when(parkingPlaceServiceImpl.getParkingPlaces(any()))
                .thenThrow(new RuntimeException("connection reset"));

        assertFailure(controller.getParkingPlacesProviderserviceMap("{\"providerServiceMapID\":4001}"),
                OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("getSubDistrictDetailsByParkingPlaceID should answer the taluks the parking place covers")
    void getSubDistrictDetails_shouldAnswerCoveredTaluks() throws Exception {
        when(parkingPlaceServiceImpl.getSubDistrict(PARKING_PLACE_ID))
                .thenReturn(List.of(new M_Parkingplace(PARKING_PLACE_ID, 3011, "Anekal")));

        assertSuccessContaining(controller.getSubDistrictDetailsByParkingPlaceID("{\"parkingPlaceID\":31}"),
                "Anekal");
    }

    @Test
    @DisplayName("getSubDistrictDetailsByParkingPlaceID should report the failure when the lookup cannot be answered")
    void getSubDistrictDetails_shouldReportLookupFailure() throws Exception {
        when(parkingPlaceServiceImpl.getSubDistrict(any())).thenThrow(new RuntimeException("query timed out"));

        assertFailure(controller.getSubDistrictDetailsByParkingPlaceID("{\"parkingPlaceID\":31}"),
                OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("getparkingPlacesbyzoneid should answer the parking places of the zone asked for")
    void getparkingPlacesbyzoneid_shouldAnswerPlacesOfZone() throws Exception {
        when(parkingPlaceServiceImpl.getAvailableParkingPlacesbyZoneID(9, PSM_ID))
                .thenReturn(new ArrayList<>(List.of(place())));

        assertSuccessContaining(
                controller.getparkingPlacesbyzoneid("{\"zoneID\":9,\"providerServiceMapID\":4001}"),
                "Hosur parking");
    }

    @Test
    @DisplayName("getparkingPlacesbyzoneid should report the failure when the lookup cannot be answered")
    void getparkingPlacesbyzoneid_shouldReportLookupFailure() throws Exception {
        when(parkingPlaceServiceImpl.getAvailableParkingPlacesbyZoneID(any(), any()))
                .thenThrow(new RuntimeException("connection reset"));

        assertFailure(controller.getparkingPlacesbyzoneid("{\"zoneID\":9}"), OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("a malformed request body should be rejected rather than reach the service")
    void malformedBody_shouldBeRejected() throws Exception {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(runMalformed()), "malformed JSON must be refused");
    }

    private String runMalformed() throws Exception {
        try {
            return controller.getParkingPlaces("{not json");
        } catch (Exception e) {
            OutputResponse output = new OutputResponse();
            output.setError(e);
            return output.toString();
        }
    }
}
