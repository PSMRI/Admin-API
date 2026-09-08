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

import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMappingTO;
import com.iemr.admin.service.parkingPlace.ParkingPlaceTalukMappingServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The taluk mapping screen records which taluks a parking place covers, and
 * offers the district's remaining taluks as the candidates for a new mapping.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ParkingPlaceTalukMappingController Test Suite")
class ParkingPlaceTalukMappingControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer PARKING_PLACE_ID = 31;
    private static final Integer MAP_ID = 7001;
    private static final Integer DISTRICT_ID = 301;

    @Mock
    private ParkingPlaceTalukMappingServiceImpl parkingPlaceTalukMappingServiceImpl;

    @InjectMocks
    private ParkingPlaceTalukMappingController controller;

    private static ParkingplaceTalukMapping mapping() {
        ParkingplaceTalukMapping mapping = new ParkingplaceTalukMapping();
        mapping.setPpSubDistrictMapID(MAP_ID);
        mapping.setParkingPlaceID(PARKING_PLACE_ID);
        mapping.setDistrictID(DISTRICT_ID);
        mapping.setDistrictBlockID(3011);
        mapping.setProviderServiceMapID(PSM_ID);
        mapping.setCreatedBy("admin");
        return mapping;
    }

    private static ParkingplaceTalukMappingTO published() {
        ParkingplaceTalukMappingTO to = new ParkingplaceTalukMappingTO();
        to.setPpSubDistrictMapID(MAP_ID);
        to.setDistrictBlockName("Anekal");
        return to;
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
    @DisplayName("parkingPlacesTalukMapping should answer the mappings it created")
    void create_shouldAnswerCreatedMappings() {
        when(parkingPlaceTalukMappingServiceImpl.saveParkingPlaceTalukMapping(anyList()))
                .thenReturn(new ArrayList<>(List.of(mapping())));

        assertSuccessContaining(controller.parkingPlacesTalukMapping(List.of(mapping())), "7001");
    }

    @Test
    @DisplayName("parkingPlacesTalukMapping should report the failure when the mapping cannot be stored")
    void create_shouldReportStorageFailure() {
        when(parkingPlaceTalukMappingServiceImpl.saveParkingPlaceTalukMapping(anyList()))
                .thenThrow(new RuntimeException("taluk already mapped"));

        assertFailure(controller.parkingPlacesTalukMapping(List.of(mapping())), OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("updateparkingPlacesTalukMapping should answer the mapping it moved")
    void update_shouldAnswerMovedMapping() {
        ParkingplaceTalukMapping stored = mapping();
        when(parkingPlaceTalukMappingServiceImpl.findbyID(MAP_ID)).thenReturn(stored);
        when(parkingPlaceTalukMappingServiceImpl.updateParkingPlaceTalukMapping(stored)).thenReturn(stored);

        assertSuccessContaining(controller.updateparkingPlacesTalukMapping(mapping()), "7001");
        assertEquals("admin", stored.getModifiedBy(), "the caller must be recorded as the one who moved it");
    }

    @Test
    @DisplayName("updateparkingPlacesTalukMapping should leave the request alone when no mapping is named")
    void update_shouldLeaveRequestAloneWithoutMapId() {
        ParkingplaceTalukMapping request = mapping();
        request.setPpSubDistrictMapID(null);

        assertSuccessContaining(controller.updateparkingPlacesTalukMapping(request), "31");
        verify(parkingPlaceTalukMappingServiceImpl, never()).findbyID(anyInt());
    }

    @Test
    @DisplayName("updateparkingPlacesTalukMapping should report the failure when the mapping is unknown")
    void update_shouldReportUnknownMapping() {
        when(parkingPlaceTalukMappingServiceImpl.findbyID(MAP_ID)).thenReturn(null);

        assertFailure(controller.updateparkingPlacesTalukMapping(mapping()), OutputResponse.CODE_EXCEPTION);
    }

    @Test
    @DisplayName("getparkingPlacesTalukMapping should answer the mapping asked for")
    void getById_shouldAnswerNamedMapping() {
        when(parkingPlaceTalukMappingServiceImpl.findbyID(MAP_ID)).thenReturn(mapping());

        assertSuccessContaining(controller.getparkingPlacesTalukMapping(mapping()), "7001");
    }

    @Test
    @DisplayName("getparkingPlacesTalukMapping should echo the request when no mapping is named")
    void getById_shouldEchoRequestWithoutMapId() {
        ParkingplaceTalukMapping request = mapping();
        request.setPpSubDistrictMapID(null);

        assertSuccessContaining(controller.getparkingPlacesTalukMapping(request), "31");
        verify(parkingPlaceTalukMappingServiceImpl, never()).findbyID(anyInt());
    }

    @Test
    @DisplayName("getparkingPlacesTalukMapping should report the failure when the lookup cannot be answered")
    void getById_shouldReportLookupFailure() {
        when(parkingPlaceTalukMappingServiceImpl.findbyID(MAP_ID))
                .thenThrow(new RuntimeException("query timed out"));

        assertFailure(controller.getparkingPlacesTalukMapping(mapping()), OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("getallparkingPlacesTalukMapping should answer every taluk the parking place covers")
    void getAll_shouldAnswerCoveredTaluks() {
        when(parkingPlaceTalukMappingServiceImpl.findbyProviderservicemapid(any()))
                .thenReturn(List.of(published()));

        assertSuccessContaining(controller.getallparkingPlacesTalukMapping(mapping()), "Anekal");
    }

    @Test
    @DisplayName("getallparkingPlacesTalukMapping should report the failure when the lookup cannot be answered")
    void getAll_shouldReportLookupFailure() {
        when(parkingPlaceTalukMappingServiceImpl.findbyProviderservicemapid(any()))
                .thenThrow(new RuntimeException("connection reset"));

        assertFailure(controller.getallparkingPlacesTalukMapping(mapping()), OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("getafilterparkingPlacesTalukMapping should narrow the mappings to the district asked for")
    void getFiltered_shouldNarrowToDistrict() {
        when(parkingPlaceTalukMappingServiceImpl.findbyParkingplaceAndDistrictID(any()))
                .thenReturn(List.of(published()));

        assertSuccessContaining(controller.getafilterparkingPlacesTalukMapping(mapping()), "Anekal");
    }

    @Test
    @DisplayName("getafilterparkingPlacesTalukMapping should report the failure when the lookup cannot be answered")
    void getFiltered_shouldReportLookupFailure() {
        when(parkingPlaceTalukMappingServiceImpl.findbyParkingplaceAndDistrictID(any()))
                .thenThrow(new RuntimeException("query timed out"));

        assertFailure(controller.getafilterparkingPlacesTalukMapping(mapping()), OutputResponse.GENERIC_FAILURE);
    }

    @Test
    @DisplayName("activateparkingPlacesTalukMapping should answer the mapping whose status it changed")
    void activate_shouldAnswerChangedMapping() {
        ParkingplaceTalukMapping stored = mapping();
        ParkingplaceTalukMapping request = mapping();
        request.setDeleted(Boolean.TRUE);
        when(parkingPlaceTalukMappingServiceImpl.findbyID(MAP_ID)).thenReturn(stored);
        when(parkingPlaceTalukMappingServiceImpl.updateParkingPlaceTalukMapping(stored)).thenReturn(stored);

        assertSuccessContaining(controller.activateparkingPlacesTalukMapping(request), "7001");
        assertEquals(Boolean.TRUE, stored.getDeleted(), "the mapping must take the requested status");
    }

    @Test
    @DisplayName("activateparkingPlacesTalukMapping should leave the request alone when no mapping is named")
    void activate_shouldLeaveRequestAloneWithoutMapId() {
        ParkingplaceTalukMapping request = mapping();
        request.setPpSubDistrictMapID(null);

        assertSuccessContaining(controller.activateparkingPlacesTalukMapping(request), "31");
        verify(parkingPlaceTalukMappingServiceImpl, never()).findbyID(anyInt());
    }

    @Test
    @DisplayName("activateparkingPlacesTalukMapping should report the failure when the mapping is unknown")
    void activate_shouldReportUnknownMapping() {
        when(parkingPlaceTalukMappingServiceImpl.findbyID(MAP_ID)).thenReturn(null);

        assertFailure(controller.activateparkingPlacesTalukMapping(mapping()), OutputResponse.CODE_EXCEPTION);
    }

    @Test
    @DisplayName("getunmappedtaluk should offer the taluks that are still free")
    void getunmappedtaluk_shouldOfferFreeTaluks() {
        when(parkingPlaceTalukMappingServiceImpl.getunmappedtaluk(DISTRICT_ID, PSM_ID))
                .thenReturn(List.of(new DistrictBlock(3012, "Hoskote")));

        assertSuccessContaining(controller.getunmappedtaluk(mapping()), "Hoskote");
    }

    @Test
    @DisplayName("getunmappedtaluk should report the failure when the candidates cannot be worked out")
    void getunmappedtaluk_shouldReportLookupFailure() {
        when(parkingPlaceTalukMappingServiceImpl.getunmappedtaluk(any(), any()))
                .thenThrow(new RuntimeException("query timed out"));

        assertFailure(controller.getunmappedtaluk(mapping()), OutputResponse.GENERIC_FAILURE);
    }
}
