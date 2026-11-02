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
package com.iemr.admin.controller.foetalmonitormaster;

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

import com.iemr.admin.data.foetalmonitormaster.FoetalMonitorDeviceID;
import com.iemr.admin.service.foetalmonitormaster.FoetalMonitorService;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The fetosense endpoints keep the device catalogue and the pairing between a
 * device and the van it travels in.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FoetalMonitorController Test Suite")
class FoetalMonitorControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final String AUTH = "sess-0d5f3a7c";
    private static final String DEVICE_JSON = "{\"vfdID\":9001,\"deviceID\":\"FS-1\",\"vanID\":71,"
            + "\"providerServiceMapID\":4001,\"deactivated\":false}";

    @Mock
    private FoetalMonitorService foetalMonitorService;

    @InjectMocks
    private FoetalMonitorController controller;

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

    private static void assertInvalidRequest(String response) {
        assertEquals(OutputResponse.USERID_FAILURE, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("createFoetalMonitorTestMaster should answer the tests the service stored")
    void createTestMaster_shouldAnswerStoredTests() throws Exception {
        when(foetalMonitorService.createFoetalMonitorTestMaster(anyString()))
                .thenReturn("[{\"testName\":\"Non stress test\"}]");

        assertSuccessContaining(controller.createFoetalMonitorTestMaster("[{}]"), "Non stress test");
    }

    @Test
    @DisplayName("createFoetalMonitorTestMaster should stay at its default when the service stored nothing")
    void createTestMaster_shouldStayAtDefaultWhenNothingStored() throws Exception {
        when(foetalMonitorService.createFoetalMonitorTestMaster(anyString())).thenReturn(null);

        assertGenericFailure(controller.createFoetalMonitorTestMaster("[{}]"));
    }

    @Test
    @DisplayName("createFoetalMonitorTestMaster should answer an error envelope when the store fails")
    void createTestMaster_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(foetalMonitorService.createFoetalMonitorTestMaster(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createFoetalMonitorTestMaster("[{}]"));
    }

    @Test
    @DisplayName("fetchFoetalMonitorTestMaster should answer the tests of the provider")
    void fetchTestMaster_shouldAnswerProviderTests() {
        when(foetalMonitorService.getFoetalMonitorTestMaster(PSM_ID))
                .thenReturn("[{\"testName\":\"Non stress test\"}]");

        assertSuccessContaining(controller.fetchFoetalMonitorTestMaster(PSM_ID), "Non stress test");
    }

    @Test
    @DisplayName("fetchFoetalMonitorTestMaster should refuse a request that names no provider")
    void fetchTestMaster_shouldRefuseRequestWithoutProvider() {
        assertInvalidRequest(controller.fetchFoetalMonitorTestMaster(0));
        verify(foetalMonitorService, never()).getFoetalMonitorTestMaster(anyInt());
    }

    @Test
    @DisplayName("fetchFoetalMonitorTestMaster should answer an error envelope when the lookup fails")
    void fetchTestMaster_shouldAnswerErrorEnvelopeOnFailure() {
        when(foetalMonitorService.getFoetalMonitorTestMaster(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.fetchFoetalMonitorTestMaster(PSM_ID));
    }

    @Test
    @DisplayName("updateProcedureMaster should answer the test once the edit lands")
    void updateTestMaster_shouldAnswerEditedTest() {
        when(foetalMonitorService.updateFoetalMonitorTestMaster(anyString()))
                .thenReturn("{\"testName\":\"Non stress test\"}");

        assertSuccessContaining(controller.updateProcedureMaster("{}"), "Non stress test");
    }

    @Test
    @DisplayName("updateProcedureMaster should report an edit the service could not apply")
    void updateTestMaster_shouldReportUnappliedEdit() {
        when(foetalMonitorService.updateFoetalMonitorTestMaster(anyString())).thenReturn(null);

        String response = controller.updateProcedureMaster("{}");

        assertInvalidRequest(response);
        assertTrue(response.contains("Failed to update procedure details"), response);
    }

    @Test
    @DisplayName("updateProcedureStatus should answer the test once its status has changed")
    void updateTestStatus_shouldAnswerChangedTest() throws Exception {
        when(foetalMonitorService.updateFoetalMonitorTestMasterStatus(11, true))
                .thenReturn("{\"testName\":\"Non stress test\"}");

        assertSuccessContaining(
                controller.updateProcedureStatus("{\"foetalMonitorTestID\":11,\"deleted\":true}"),
                "Non stress test");
    }

    @Test
    @DisplayName("updateProcedureStatus should report a status the service could not change")
    void updateTestStatus_shouldReportUnchangedStatus() throws Exception {
        when(foetalMonitorService.updateFoetalMonitorTestMasterStatus(anyInt(), anyBoolean())).thenReturn(null);

        assertInvalidRequest(controller.updateProcedureStatus("{\"foetalMonitorTestID\":11,\"deleted\":true}"));
    }

    @Test
    @DisplayName("updateProcedureStatus should refuse a request that names no test")
    void updateTestStatus_shouldRefuseRequestWithoutTest() {
        assertInvalidRequest(controller.updateProcedureStatus("{\"deleted\":true}"));
    }

    @Test
    @DisplayName("saveFoetalMonitorDeviceID should report the devices it stored")
    void saveDeviceID_shouldReportStoredDevices() throws Exception {
        when(foetalMonitorService.saveFoetalMonitorDeviceID(any())).thenReturn(1);

        assertSuccessContaining(
                controller.saveFoetalMonitorDeviceID(new ArrayList<>(List.of(new FoetalMonitorDeviceID())), AUTH),
                "Device ID saved successfully");
    }

    @Test
    @DisplayName("saveFoetalMonitorDeviceID should report the reason the service refused the device")
    void saveDeviceID_shouldReportRefusal() throws Exception {
        when(foetalMonitorService.saveFoetalMonitorDeviceID(any()))
                .thenThrow(new IEMRException("Error in saving foetal monitor device ID"));

        String response = controller.saveFoetalMonitorDeviceID(new ArrayList<>(), AUTH);

        assertGenericFailure(response);
        assertTrue(response.contains("Error in saving foetal monitor device ID"), response);
    }

    @Test
    @DisplayName("saveVanIDandDeviceIDMapping should report a pairing the service accepted")
    void saveMapping_shouldReportAcceptedPairing() throws Exception {
        when(foetalMonitorService.vanIDAndDeviceIDMapping(any())).thenReturn(1);

        assertSuccessContaining(controller.saveVanIDandDeviceIDMapping(DEVICE_JSON, AUTH),
                "Mapping Done successfully");
    }

    @Test
    @DisplayName("saveVanIDandDeviceIDMapping should report the reason the service refused the pairing")
    void saveMapping_shouldReportRefusal() throws Exception {
        when(foetalMonitorService.vanIDAndDeviceIDMapping(any()))
                .thenThrow(new IEMRException("Error in updating the VanID"));

        assertGenericFailure(controller.saveVanIDandDeviceIDMapping(DEVICE_JSON, AUTH));
    }

    @Test
    @DisplayName("getFoetalMonitorDeviceID should answer the devices the service publishes")
    void getDeviceID_shouldAnswerPublishedDevices() throws Exception {
        when(foetalMonitorService.getFoetalMonitorDeviceID(any()))
                .thenReturn("{\"fetosenseDeviceIDs\":[{\"deviceID\":\"FS-1\"}]}");

        assertSuccessContaining(controller.getFoetalMonitorDeviceID(DEVICE_JSON), "FS-1");
    }

    @Test
    @DisplayName("getFoetalMonitorDeviceID should report the reason the lookup failed")
    void getDeviceID_shouldReportFailure() throws Exception {
        when(foetalMonitorService.getFoetalMonitorDeviceID(any()))
                .thenThrow(new IEMRException("Error in getting foetal monitor DeviceID"));

        assertGenericFailure(controller.getFoetalMonitorDeviceID(DEVICE_JSON));
    }

    @Test
    @DisplayName("getVanIDAndDeviceID should answer the vans and devices still free to pair")
    void getVanAndDevice_shouldAnswerFreePairs() throws Exception {
        when(foetalMonitorService.getvanIDAndFoetalMonitorDeviceID(any()))
                .thenReturn("{\"VanIDs\":[],\"deviceIDs\":[]}");

        assertSuccessContaining(controller.getVanIDAndDeviceID(DEVICE_JSON), "VanIDs");
    }

    @Test
    @DisplayName("getVanIDAndDeviceID should report the reason the lookup failed")
    void getVanAndDevice_shouldReportFailure() throws Exception {
        when(foetalMonitorService.getvanIDAndFoetalMonitorDeviceID(any()))
                .thenThrow(new IEMRException("Error in getting vanID and foetalMonitorID"));

        assertGenericFailure(controller.getVanIDAndDeviceID(DEVICE_JSON));
    }

    @Test
    @DisplayName("getMappedWorklist should answer the pairings on record")
    void getWorklist_shouldAnswerPairings() throws Exception {
        when(foetalMonitorService.getVanIDMappingWorklist(any()))
                .thenReturn("[{\"deviceID\":\"FS-1\"}]");

        assertSuccessContaining(controller.getMappedWorklist(DEVICE_JSON), "FS-1");
    }

    @Test
    @DisplayName("getMappedWorklist should report the reason the lookup failed")
    void getWorklist_shouldReportFailure() throws Exception {
        when(foetalMonitorService.getVanIDMappingWorklist(any()))
                .thenThrow(new IEMRException("Error in getting vanID mapping worklist"));

        assertGenericFailure(controller.getMappedWorklist(DEVICE_JSON));
    }

    @Test
    @DisplayName("updateFoetalMonitorDeviceID should report a device the service saved")
    void updateDeviceID_shouldReportSavedDevice() throws Exception {
        when(foetalMonitorService.updateFoetalMonitorDeviceID(any())).thenReturn(1);

        assertSuccessContaining(controller.updateFoetalMonitorDeviceID(DEVICE_JSON, AUTH),
                "DeviceID updated successfully");
    }

    @Test
    @DisplayName("updateFoetalMonitorDeviceID should report the reason the service refused the edit")
    void updateDeviceID_shouldReportRefusal() throws Exception {
        when(foetalMonitorService.updateFoetalMonitorDeviceID(any()))
                .thenThrow(new IEMRException("Error in updating the Device ID"));

        String response = controller.updateFoetalMonitorDeviceID(DEVICE_JSON, AUTH);

        assertGenericFailure(response);
        assertTrue(response.contains("Unable to update deviceID"), response);
    }

    @Test
    @DisplayName("deleteFoetalMonitorDeviceID should report a device the service retired")
    void deleteDeviceID_shouldReportRetiredDevice() throws Exception {
        when(foetalMonitorService.deleteFoetalMonitorDeviceID(any())).thenReturn(1);

        assertSuccessContaining(controller.deleteFoetalMonitorDeviceID(DEVICE_JSON, AUTH),
                "Device ID de-activated successfully");
    }

    @Test
    @DisplayName("deleteFoetalMonitorDeviceID should report the reason the service refused the retirement")
    void deleteDeviceID_shouldReportRefusal() throws Exception {
        when(foetalMonitorService.deleteFoetalMonitorDeviceID(any()))
                .thenThrow(new IEMRException("Error in de-activating the device ID"));

        assertGenericFailure(controller.deleteFoetalMonitorDeviceID(DEVICE_JSON, AUTH));
    }

    @Test
    @DisplayName("updateMapping should report a pairing the service moved")
    void updateMapping_shouldReportMovedPairing() throws Exception {
        when(foetalMonitorService.updatingvanIDAndDeviceIDMapping(any())).thenReturn(1);

        assertSuccessContaining(controller.updateMapping(DEVICE_JSON, AUTH), "Mapping updated successfully");
    }

    @Test
    @DisplayName("updateMapping should report the reason the service refused to move the pairing")
    void updateMapping_shouldReportRefusal() throws Exception {
        when(foetalMonitorService.updatingvanIDAndDeviceIDMapping(any()))
                .thenThrow(new IEMRException("Error in updating van details"));

        assertGenericFailure(controller.updateMapping(DEVICE_JSON, AUTH));
    }

    @Test
    @DisplayName("deleteVanIDAndFoetalMonitorDeviceID should report a pairing the service released")
    void deleteMapping_shouldReportReleasedPairing() throws Exception {
        when(foetalMonitorService.deleteVanIDAndDeviceIDMapping(any())).thenReturn(1);

        assertSuccessContaining(controller.deleteVanIDAndFoetalMonitorDeviceID(DEVICE_JSON, AUTH),
                "Mapped deactivated successfully");
    }

    @Test
    @DisplayName("deleteVanIDAndFoetalMonitorDeviceID should report the reason the service refused the release")
    void deleteMapping_shouldReportRefusal() throws Exception {
        when(foetalMonitorService.deleteVanIDAndDeviceIDMapping(any()))
                .thenThrow(new IEMRException("The Van is already mapped with a device"));

        String response = controller.deleteVanIDAndFoetalMonitorDeviceID(DEVICE_JSON, AUTH);

        assertGenericFailure(response);
        assertTrue(response.contains("already mapped with a device"), response);
    }
}
