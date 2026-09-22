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
package com.iemr.admin.controller.userParkingPlaceMap;

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

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.userParkingPlaceMap.M_UserParkingPlaceMap;
import com.iemr.admin.data.userParkingPlaceMap.M_UserVanMapping;
import com.iemr.admin.service.userParkingPlaceMap.UserParkingPlaceMapServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The user parking place endpoints post a field user to the parking place they
 * report to, and to the vans they work out of.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserParkingPlaceMapController Test Suite")
class UserParkingPlaceMapControllerTest {

    private static final Integer MAP_ID = 9001;
    private static final Integer PSM_ID = 4001;

    @Mock
    private UserParkingPlaceMapServiceImpl userParkingPlaceMapServiceImpl;

    @InjectMocks
    private UserParkingPlaceMapController controller;

    private static M_UserParkingPlaceMap mapping(Integer id) {
        return new M_UserParkingPlaceMap(id, 3117, "Asha", "Rao", "asha.rao", 7, 301, 31, "Hosur parking",
                PSM_ID, Boolean.FALSE, Boolean.FALSE);
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
    @DisplayName("saveuserParkingPlaces should answer the postings the service stored")
    void save_shouldAnswerStoredPostings() throws Exception {
        when(userParkingPlaceMapServiceImpl.saveUserParkingPlaceDetails(anyList()))
                .thenReturn(new ArrayList<>(List.of(mapping(MAP_ID))));

        assertSuccessContaining(controller.saveuserParkingPlaces(
                "{\"userParkingPlaceMaps\":[{\"userID\":3117,\"parkingPlaceID\":31}]}"), "Asha");
    }

    @Test
    @DisplayName("saveuserParkingPlaces should answer an error envelope when the store fails")
    void save_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(userParkingPlaceMapServiceImpl.saveUserParkingPlaceDetails(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveuserParkingPlaces("{\"userParkingPlaceMaps\":[{}]}"));
    }

    @Test
    @DisplayName("getuserParkingPlaces should answer the postings matching the location filters")
    void get_shouldAnswerMatchingPostings() throws Exception {
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceMappings(77, 29, 301, 31, 7))
                .thenReturn(new ArrayList<>(List.of(mapping(MAP_ID))));

        assertSuccessContaining(controller.getuserParkingPlaces("{\"serviceProviderID\":77,\"stateID\":29,"
                + "\"districtID\":301,\"parkingPlaceID\":31,\"m_user\":{\"designationID\":7}}"), "Asha");
    }

    @Test
    @DisplayName("getuserParkingPlaces should answer an error envelope when the request names no user detail")
    void get_shouldAnswerErrorEnvelopeWithoutUserDetail() throws Exception {
        assertCodeException(controller.getuserParkingPlaces("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getuserParkingPlacesDesiganation should narrow the postings to the designation")
    void getByDesignation_shouldNarrowToDesignation() throws Exception {
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceMappings1(PSM_ID, 301, 31, 7))
                .thenReturn(new ArrayList<>(List.of(mapping(MAP_ID))));

        assertSuccessContaining(controller.getuserParkingPlacesDesiganation(
                "{\"providerServiceMapID\":4001,\"districtID\":301,\"parkingPlaceID\":31,"
                        + "\"designationID\":7}"), "Asha");
    }

    @Test
    @DisplayName("getuserParkingPlacesDesiganation should answer an error envelope when the lookup fails")
    void getByDesignation_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceMappings1(any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getuserParkingPlacesDesiganation("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("deleteuserParkingPlaceDetails should report whether the posting actually changed")
    void deleteDetails_shouldReportOutcome() throws Exception {
        when(userParkingPlaceMapServiceImpl.updateUserParkingPlaceMapStatus(any())).thenReturn(1);
        assertSuccessContaining(
                controller.deleteuserParkingPlaceDetails("{\"userParkingPlaceMapID\":9001,\"deleted\":true}"),
                "status updated successfully");

        when(userParkingPlaceMapServiceImpl.updateUserParkingPlaceMapStatus(any())).thenReturn(0);
        assertSuccessContaining(
                controller.deleteuserParkingPlaceDetails("{\"userParkingPlaceMapID\":9001,\"deleted\":true}"),
                "Failed to update the status");
    }

    @Test
    @DisplayName("deleteuserParkingPlaceDetails should answer an error envelope when the change fails")
    void deleteDetails_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(userParkingPlaceMapServiceImpl.updateUserParkingPlaceMapStatus(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(
                controller.deleteuserParkingPlaceDetails("{\"userParkingPlaceMapID\":9001,\"deleted\":true}"));
    }

    @Test
    @DisplayName("edituserParkingPlaces should copy the edits and remap the vans")
    void edit_shouldCopyEditsAndRemapVans() throws Exception {
        M_UserParkingPlaceMap stored = mapping(MAP_ID);
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceDetails(MAP_ID)).thenReturn(stored);
        when(userParkingPlaceMapServiceImpl.saveediteddata(any(), anyList())).thenReturn(stored);

        String response = controller.edituserParkingPlaces("{\"userParkingPlaceMapID\":9001,"
                + "\"parkingPlaceID\":32,\"providerServiceMapID\":4002,\"districtID\":302,"
                + "\"modifiedBy\":\"admin\",\"uservanmapping\":[{\"vanID\":71}]}");

        assertSuccessContaining(response, "Asha");
        assertEquals(32, stored.getParkingPlaceID());
        assertEquals(4002, stored.getProviderServiceMapID());
    }

    @Test
    @DisplayName("edituserParkingPlaces should answer an error envelope for a posting that does not exist")
    void edit_shouldAnswerErrorEnvelopeForUnknownPosting() throws Exception {
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceDetails(anyInt())).thenReturn(null);

        assertCodeException(controller.edituserParkingPlaces("{\"userParkingPlaceMapID\":9001}"));
    }

    @Test
    @DisplayName("deleteuserParkingPlaces should retire the posting the caller names")
    void deletePostings_shouldRetirePosting() throws Exception {
        M_UserParkingPlaceMap stored = mapping(MAP_ID);
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceDetails(MAP_ID)).thenReturn(stored);
        when(userParkingPlaceMapServiceImpl.saveediteddata(stored)).thenReturn(stored);

        assertSuccessContaining(
                controller.deleteuserParkingPlaces("{\"userParkingPlaceMapID\":9001,\"deleted\":true}"), "Asha");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteuserParkingPlaces should refuse to reinstate a user already posted elsewhere")
    void deletePostings_shouldRefuseReinstatingPostedUser() throws Exception {
        M_UserParkingPlaceMap stored = mapping(MAP_ID);
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceDetails(MAP_ID)).thenReturn(stored);
        when(userParkingPlaceMapServiceImpl.getuserexist(PSM_ID, 3117)).thenReturn(Boolean.TRUE);

        String response = controller
                .deleteuserParkingPlaces("{\"userParkingPlaceMapID\":9001,\"deleted\":false}");

        assertGenericFailure(response);
        assertTrue(response.contains("User already mapped. Cannot activate"), response);
    }

    @Test
    @DisplayName("deleteuserParkingPlaces should reinstate a user who is posted nowhere else")
    void deletePostings_shouldReinstateFreeUser() throws Exception {
        M_UserParkingPlaceMap stored = mapping(MAP_ID);
        when(userParkingPlaceMapServiceImpl.getUserParkingPlaceDetails(MAP_ID)).thenReturn(stored);
        when(userParkingPlaceMapServiceImpl.getuserexist(PSM_ID, 3117)).thenReturn(Boolean.FALSE);
        when(userParkingPlaceMapServiceImpl.saveediteddata(stored)).thenReturn(stored);

        controller.deleteuserParkingPlaces("{\"userParkingPlaceMapID\":9001,\"deleted\":false}");

        assertEquals(Boolean.FALSE, stored.getDeleted());
    }

    @Test
    @DisplayName("unmappeduser should answer the users not yet posted anywhere")
    void unmappeduser_shouldAnswerUnpostedUsers() throws Exception {
        M_User1 user = new M_User1();
        user.setUserID(3117);
        user.setFirstName("Asha");
        when(userParkingPlaceMapServiceImpl.getunmappedUser(PSM_ID, 7)).thenReturn(List.of(user));

        assertSuccessContaining(
                controller.unmappeduser("{\"providerServiceMapID\":4001,\"designationID\":7}"), "Asha");
    }

    @Test
    @DisplayName("unmappeduser should answer an error envelope when the lookup fails")
    void unmappeduser_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(userParkingPlaceMapServiceImpl.getunmappedUser(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.unmappeduser("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("mappedvan should answer the vans the posting covers")
    void mappedvan_shouldAnswerCoveredVans() throws Exception {
        M_UserVanMapping vanMapping = new M_UserVanMapping();
        vanMapping.setUserVanMapID(7001);
        when(userParkingPlaceMapServiceImpl.getuservanmapping(MAP_ID)).thenReturn(List.of(vanMapping));

        assertSuccessContaining(controller.mappedvan(MAP_ID), "7001");
    }

    @Test
    @DisplayName("mappedvan should answer an error envelope when the lookup fails")
    void mappedvan_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(userParkingPlaceMapServiceImpl.getuservanmapping(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.mappedvan(MAP_ID));
    }

    @Test
    @DisplayName("deletemappedvan should report the van mapping it released")
    void deletemappedvan_shouldReportReleasedMapping() throws Exception {
        assertSuccessContaining(
                controller.deletemappedvan("{\"userVanMapID\":7001,\"modifiedBy\":\"admin\"}"), "Success");
        verify(userParkingPlaceMapServiceImpl).deleteuservanmapping(any());
    }

    @Test
    @DisplayName("deletemappedvan should answer an error envelope when the release fails")
    void deletemappedvan_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        org.mockito.Mockito.doThrow(new IllegalStateException("no connection"))
                .when(userParkingPlaceMapServiceImpl).deleteuservanmapping(any());

        assertGenericFailure(controller.deletemappedvan("{\"userVanMapID\":7001}"));
    }
}
