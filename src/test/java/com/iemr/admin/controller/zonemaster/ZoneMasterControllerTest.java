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
package com.iemr.admin.controller.zonemaster;

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

import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.data.zonemaster.M_ZoneDistrictMap;
import com.iemr.admin.service.zonemaster.ZoneMasterServiceImpl;
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
 * The zone endpoints group the districts a provider operates in, and a zone that
 * is retired has to take its district mappings with it.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ZoneMasterController Test Suite")
class ZoneMasterControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer ZONE_ID = 61;

    @Mock
    private ZoneMasterServiceImpl zoneMasterServiceImpl;

    @InjectMocks
    private ZoneMasterController controller;

    private static M_Zone zone(Integer id, String name) {
        return new M_Zone(id, name, "Northern districts", "Main Road", PSM_ID, Boolean.FALSE, 1, "India",
                29, "Karnataka", 301, "Bengaluru Urban", 401, "North block", 501, "Hosur", null, 3,
                "Tele Medicine");
    }

    private static M_ZoneDistrictMap districtMapping(Integer id) {
        return new M_ZoneDistrictMap(id, ZONE_ID, "North zone", 301, PSM_ID, Boolean.FALSE, 29, "Karnataka",
                "Bengaluru Urban", 3, "Tele Medicine", Boolean.FALSE);
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

    private static void assertCodeException(String response) {
        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("saveZone should answer the zones the service stored")
    void saveZone_shouldAnswerStoredZones() throws Exception {
        when(zoneMasterServiceImpl.createZone(anyList()))
                .thenReturn(new ArrayList<>(List.of(zone(ZONE_ID, "North zone"))));

        assertSuccessContaining(controller.saveZone("{\"zones\":[{\"zoneName\":\"North zone\"}]}"), "North zone");
    }

    @Test
    @DisplayName("saveZone should answer an error envelope when the store fails")
    void saveZone_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(zoneMasterServiceImpl.createZone(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveZone("{\"zones\":[{\"zoneName\":\"North zone\"}]}"));
    }

    @Test
    @DisplayName("getZones should answer the zones of the provider")
    void getZones_shouldAnswerProviderZones() {
        when(zoneMasterServiceImpl.getAvailableZones(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(zone(ZONE_ID, "North zone"))));

        assertSuccessContaining(controller.getZones("{\"providerServiceMapID\":4001}"), "North zone");
    }

    @Test
    @DisplayName("getZones should ask the caller for a provider when the request names none")
    void getZones_shouldAskForProvider() {
        assertSuccessContaining(controller.getZones("{}"), "Provide providerServiceMapID.");
        verify(zoneMasterServiceImpl, never()).getAvailableZones(anyInt());
    }

    @Test
    @DisplayName("getZones should stay at its default for a provider id of zero")
    void getZones_shouldStayAtDefaultForZeroProvider() {
        assertGenericFailure(controller.getZones("{\"providerServiceMapID\":0}"));
    }

    @Test
    @DisplayName("getZones should answer an error envelope for a body it cannot read")
    void getZones_shouldAnswerErrorEnvelopeForMalformedBody() {
        assertEquals(OutputResponse.OBJECT_FAILURE, statusCodeOf(controller.getZones("not json")));
    }

    @Test
    @DisplayName("mapZoneWithDistrict should answer the mappings the service stored")
    void mapZoneWithDistrict_shouldAnswerStoredMappings() throws Exception {
        when(zoneMasterServiceImpl.createZoneDistrictMapping(anyList()))
                .thenReturn(new ArrayList<>(List.of(districtMapping(9001))));

        assertSuccessContaining(controller.mapZoneWithDistrict(
                "{\"zoneDistrictMappings\":[{\"zoneID\":61,\"districtID\":301}]}"), "9001");
    }

    @Test
    @DisplayName("mapZoneWithDistrict should answer an error envelope when the store fails")
    void mapZoneWithDistrict_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(zoneMasterServiceImpl.createZoneDistrictMapping(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.mapZoneWithDistrict("{\"zoneDistrictMappings\":[{}]}"));
    }

    @Test
    @DisplayName("editZoneDistrict should copy the edits onto the stored mapping")
    void editZoneDistrict_shouldCopyEdits() throws Exception {
        M_ZoneDistrictMap stored = districtMapping(9001);
        when(zoneMasterServiceImpl.editZoneDistrictMapping(9001)).thenReturn(stored);
        when(zoneMasterServiceImpl.saveeditedData(stored)).thenReturn(stored);

        String response = controller.editZoneDistrict("{\"zoneDistrictMapID\":9001,\"zoneID\":62,"
                + "\"districtID\":302,\"providerServiceMapID\":4001,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "9001");
        assertEquals(62, stored.getZoneID());
        assertEquals(302, stored.getDistrictID());
    }

    @Test
    @DisplayName("editZoneDistrict should answer an error envelope for a mapping that does not exist")
    void editZoneDistrict_shouldAnswerErrorEnvelopeForUnknownMapping() throws Exception {
        when(zoneMasterServiceImpl.editZoneDistrictMapping(9001)).thenReturn(null);

        assertCodeException(controller.editZoneDistrict("{\"zoneDistrictMapID\":9001}"));
    }

    @Test
    @DisplayName("getZoneDistrictMappings should answer the district mappings of the provider")
    void getZoneDistrictMappings_shouldAnswerProviderMappings() {
        when(zoneMasterServiceImpl.getAvailableZoneDistrictMappings(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(districtMapping(9001))));

        assertSuccessContaining(
                controller.getZoneDistrictMappings("{\"providerServiceMapID\":4001}"), "9001");
    }

    @Test
    @DisplayName("getZoneDistrictMappings should ask the caller for a provider when the request names none")
    void getZoneDistrictMappings_shouldAskForProvider() {
        assertSuccessContaining(controller.getZoneDistrictMappings("{}"), "Provide serviceProviderID.");
    }

    @Test
    @DisplayName("deleteZone should report whether the zone was actually retired")
    void deleteZone_shouldReportOutcome() throws Exception {
        when(zoneMasterServiceImpl.updateZoneStatus(any())).thenReturn(1);
        assertSuccessContaining(controller.deleteZone("{\"zoneID\":61,\"deleted\":true}"),
                "status updated successfully");

        when(zoneMasterServiceImpl.updateZoneStatus(any())).thenReturn(0);
        assertSuccessContaining(controller.deleteZone("{\"zoneID\":61,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteZone should answer an error envelope when the change fails")
    void deleteZone_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(zoneMasterServiceImpl.updateZoneStatus(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.deleteZone("{\"zoneID\":61,\"deleted\":true}"));
    }

    @Test
    @DisplayName("updateZoneData should copy the edits onto the stored zone")
    void updateZoneData_shouldCopyEdits() throws Exception {
        M_Zone stored = zone(ZONE_ID, "old name");
        when(zoneMasterServiceImpl.getzoneByID(ZONE_ID)).thenReturn(stored);
        when(zoneMasterServiceImpl.updateZoneData(stored)).thenReturn(stored);

        String response = controller.updateZoneData("{\"zoneID\":61,\"zoneName\":\"North zone\","
                + "\"zoneDesc\":\"Northern districts\",\"zoneHQAddress\":\"Main Road\",\"stateID\":29,"
                + "\"districtID\":301,\"districtBlockID\":401,\"districtBranchID\":501,"
                + "\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "North zone");
        assertEquals("Northern districts", stored.getZoneDesc());
        assertEquals(29, stored.getStateID());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("updateZoneData should answer an error envelope for a zone that does not exist")
    void updateZoneData_shouldAnswerErrorEnvelopeForUnknownZone() throws Exception {
        when(zoneMasterServiceImpl.getzoneByID(ZONE_ID)).thenReturn(null);

        assertCodeException(controller.updateZoneData("{\"zoneID\":61}"));
    }

    @Test
    @DisplayName("getMappedDistrictByZoneID should answer the districts mapped to the zone")
    void getMappedDistrictByZoneID_shouldAnswerMappedDistricts() {
        when(zoneMasterServiceImpl.editZoneDistrictMapping1(ZONE_ID))
                .thenReturn(new ArrayList<>(List.of(districtMapping(9001))));

        assertSuccessContaining(controller.getMappedDistrictByZoneID("{\"zoneID\":61}"), "9001");
    }

    @Test
    @DisplayName("getMappedDistrictByZoneID should answer an error envelope when the lookup fails")
    void getMappedDistrictByZoneID_shouldAnswerErrorEnvelopeOnFailure() {
        when(zoneMasterServiceImpl.editZoneDistrictMapping1(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getMappedDistrictByZoneID("{\"zoneID\":61}"));
    }

    @Test
    @DisplayName("deleteZoneDistrictMapping should report whether the mapping changed")
    void deleteZoneDistrictMapping_shouldReportOutcome() throws Exception {
        when(zoneMasterServiceImpl.updateZoneDistrictMappingStatus(any())).thenReturn(0);
        assertSuccessContaining(
                controller.deleteZoneDistrictMapping("{\"zoneDistrictMapID\":9001,\"deleted\":true}"),
                "status updated successfully");

        when(zoneMasterServiceImpl.updateZoneDistrictMappingStatus(any())).thenReturn(1);
        assertSuccessContaining(
                controller.deleteZoneDistrictMapping("{\"zoneDistrictMapID\":9001,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteZoneDistrictMapping should answer an error envelope when the change fails")
    void deleteZoneDistrictMapping_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(zoneMasterServiceImpl.updateZoneDistrictMappingStatus(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(
                controller.deleteZoneDistrictMapping("{\"zoneDistrictMapID\":9001,\"deleted\":true}"));
    }
}
