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
package com.iemr.admin.service.foetalmonitormaster;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.foetalmonitormaster.FoetalMonitorDeviceID;
import com.iemr.admin.data.foetalmonitormaster.M_FoetalMonitor;
import com.iemr.admin.repo.foetalmonitormaster.FoetalMonitorDeviceIDRepo;
import com.iemr.admin.repo.foetalmonitormaster.FoetalMonitorRepository;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.utils.exception.IEMRException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A fetosense device is attached to one van at a time, so the mapping rules here
 * decide whether a foetal monitor reading can be traced back to the van it was
 * taken in.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FoetalMonitorServiceImpl Test Suite")
class FoetalMonitorServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer VAN_ID = 71;
    private static final Long VFD_ID = 9001L;

    @Mock
    private FoetalMonitorRepository foetalMonitorRepository;

    @Mock
    private VanMasterRepository masterVanRepo;

    @Mock
    private FoetalMonitorDeviceIDRepo foetalMonitorDeviceIDRepo;

    @InjectMocks
    private FoetalMonitorServiceImpl service;

    private static M_FoetalMonitor test(Integer id, String name) {
        M_FoetalMonitor test = new M_FoetalMonitor();
        test.setFoetalMonitorTestID(id);
        test.setTestName(name);
        return test;
    }

    private static FoetalMonitorDeviceID device() {
        FoetalMonitorDeviceID device = new FoetalMonitorDeviceID();
        device.setVfdID(VFD_ID);
        device.setDeviceID("FS-1");
        device.setDeviceName("Fetosense 1");
        device.setVanID(VAN_ID);
        device.setVanTypeID(1);
        device.setParkingPlaceID(31);
        device.setVanName("MMU Van 1");
        device.setProviderServiceMapID(PSM_ID);
        device.setCreatedBy("admin");
        device.setDeactivated(Boolean.FALSE);
        device.setDeleted(Boolean.FALSE);
        return device;
    }

    @Test
    @DisplayName("createFoetalMonitorTestMaster should publish the tests it stored")
    void createTestMaster_shouldPublishStoredTests() throws Exception {
        when(foetalMonitorRepository.saveAll(anyList())).thenReturn(List.of(test(11, "Non stress test")));

        String created = service.createFoetalMonitorTestMaster("[{\"testName\":\"Non stress test\"}]");

        assertTrue(created.contains("Non stress test"), created);
    }

    @Test
    @DisplayName("createFoetalMonitorTestMaster should answer nothing when it stored fewer than it was given")
    void createTestMaster_shouldAnswerNothingOnPartialStore() throws Exception {
        when(foetalMonitorRepository.saveAll(anyList())).thenReturn(new ArrayList<M_FoetalMonitor>());

        assertNull(service.createFoetalMonitorTestMaster("[{\"testName\":\"Non stress test\"}]"));
    }

    @Test
    @DisplayName("getFoetalMonitorTestMaster should publish the tests of the provider")
    void getTestMaster_shouldPublishProviderTests() {
        when(foetalMonitorRepository.getByProviderServiceMapID(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(test(11, "Non stress test"))));

        assertTrue(service.getFoetalMonitorTestMaster(PSM_ID).contains("Non stress test"));
    }

    @Test
    @DisplayName("updateFoetalMonitorTestMaster should publish the test once the edit lands")
    void updateTestMaster_shouldPublishEditedTest() {
        when(foetalMonitorRepository.updateFoetalMonitorDetails(11, "Non stress test", null, "admin")).thenReturn(1);
        when(foetalMonitorRepository.getByFoetalMonitorTestID(11)).thenReturn(test(11, "Non stress test"));

        String published = service.updateFoetalMonitorTestMaster(
                "{\"foetalMonitorTestID\":11,\"testName\":\"Non stress test\",\"modifiedBy\":\"admin\"}");

        assertTrue(published.contains("Non stress test"), published);
    }

    @Test
    @DisplayName("updateFoetalMonitorTestMaster should answer nothing for a request that names no test")
    void updateTestMaster_shouldAnswerNothingWithoutTest() {
        assertNull(service.updateFoetalMonitorTestMaster("{\"testName\":\"Non stress test\"}"));
    }

    @Test
    @DisplayName("updateFoetalMonitorTestMaster should answer nothing when the edit changed nothing")
    void updateTestMaster_shouldAnswerNothingWhenNothingChanged() {
        when(foetalMonitorRepository.updateFoetalMonitorDetails(anyInt(), any(), any(), any())).thenReturn(0);

        assertNull(service.updateFoetalMonitorTestMaster("{\"foetalMonitorTestID\":11}"));
    }

    @Test
    @DisplayName("updateFoetalMonitorTestMasterStatus should publish the test once its status has changed")
    void updateTestStatus_shouldPublishChangedTest() throws Exception {
        when(foetalMonitorRepository.updateFoetalMonitorStatus(11, true)).thenReturn(1);
        when(foetalMonitorRepository.getByFoetalMonitorTestID(11)).thenReturn(test(11, "Non stress test"));

        assertTrue(service.updateFoetalMonitorTestMasterStatus(11, true).contains("Non stress test"));
    }

    @Test
    @DisplayName("updateFoetalMonitorTestMasterStatus should answer nothing when no test changed")
    void updateTestStatus_shouldAnswerNothingWhenNothingChanged() throws Exception {
        when(foetalMonitorRepository.updateFoetalMonitorStatus(11, true)).thenReturn(0);

        assertNull(service.updateFoetalMonitorTestMasterStatus(11, true));
    }

    @Test
    @DisplayName("saveFoetalMonitorDeviceID should report the devices it stored")
    void saveDeviceID_shouldReportStoredDevices() throws Exception {
        when(foetalMonitorDeviceIDRepo.saveAll(anyList()))
                .thenReturn(new ArrayList<>(List.of(device())));

        assertEquals(1, service.saveFoetalMonitorDeviceID(new ArrayList<>(List.of(device()))));
    }

    @Test
    @DisplayName("saveFoetalMonitorDeviceID should refuse a run that stored nothing")
    void saveDeviceID_shouldRefuseEmptyStore() {
        when(foetalMonitorDeviceIDRepo.saveAll(anyList())).thenReturn(new ArrayList<FoetalMonitorDeviceID>());

        assertThrows(IEMRException.class,
                () -> service.saveFoetalMonitorDeviceID(new ArrayList<>(List.of(device()))));
    }

    @Test
    @DisplayName("getFoetalMonitorDeviceID should publish the devices of the provider")
    void getDeviceID_shouldPublishProviderDevices() throws Exception {
        when(foetalMonitorDeviceIDRepo.getFoetalMonitorDeviceID(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(device())));

        String published = service.getFoetalMonitorDeviceID(device());

        assertTrue(published.contains("fetosenseDeviceIDs"), published);
        assertTrue(published.contains("FS-1"), published);
    }

    @Test
    @DisplayName("getFoetalMonitorDeviceID should report a lookup it could not run")
    void getDeviceID_shouldReportFailedLookup() {
        when(foetalMonitorDeviceIDRepo.getFoetalMonitorDeviceID(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertThrows(IEMRException.class, () -> service.getFoetalMonitorDeviceID(device()));
    }

    @Test
    @DisplayName("deleteFoetalMonitorDeviceID should release the van the device was attached to")
    void deleteDeviceID_shouldReleaseVan() throws Exception {
        FoetalMonitorDeviceID request = device();
        when(foetalMonitorDeviceIDRepo.save(request)).thenReturn(request);
        when(masterVanRepo.updateVanFoetalMonitorsmapping(true, VAN_ID)).thenReturn(1);

        assertEquals(1, service.deleteFoetalMonitorDeviceID(request));
        verify(masterVanRepo).updateVanFoetalMonitorsmapping(true, VAN_ID);
    }

    @Test
    @DisplayName("deleteFoetalMonitorDeviceID should mark the van unmapped when the device is retired")
    void deleteDeviceID_shouldMarkVanUnmappedOnRetirement() throws Exception {
        FoetalMonitorDeviceID request = device();
        request.setDeleted(Boolean.TRUE);
        when(foetalMonitorDeviceIDRepo.save(request)).thenReturn(request);
        when(masterVanRepo.updateVanFoetalMonitorsmapping(false, VAN_ID)).thenReturn(1);

        assertEquals(1, service.deleteFoetalMonitorDeviceID(request));
        verify(masterVanRepo).updateVanFoetalMonitorsmapping(false, VAN_ID);
    }

    @Test
    @DisplayName("deleteFoetalMonitorDeviceID should refuse a device whose van could not be released")
    void deleteDeviceID_shouldRefuseWhenVanCannotBeReleased() {
        FoetalMonitorDeviceID request = device();
        when(foetalMonitorDeviceIDRepo.save(request)).thenReturn(request);
        when(masterVanRepo.updateVanFoetalMonitorsmapping(anyBoolean(), anyInt())).thenReturn(0);

        assertThrows(IEMRException.class, () -> service.deleteFoetalMonitorDeviceID(request));
    }

    @Test
    @DisplayName("deleteFoetalMonitorDeviceID should leave the vans alone for a device attached to none")
    void deleteDeviceID_shouldLeaveVansAloneForUnattachedDevice() throws Exception {
        FoetalMonitorDeviceID request = device();
        request.setVanID(null);
        when(foetalMonitorDeviceIDRepo.save(request)).thenReturn(request);

        assertEquals(1, service.deleteFoetalMonitorDeviceID(request));
        verify(masterVanRepo, never()).updateVanFoetalMonitorsmapping(anyBoolean(), anyInt());
    }

    @Test
    @DisplayName("getvanIDAndFoetalMonitorDeviceID should publish the vans and devices still free to pair")
    void getVanAndDevice_shouldPublishFreePairs() throws Exception {
        ArrayList<Object[]> vanRows = new ArrayList<>();
        vanRows.add(new Object[] { VAN_ID, "MMU Van 1", "KA-01-AB-1234" });
        when(masterVanRepo.getVanIDNotMappedWithDevice(1, 31, PSM_ID)).thenReturn(vanRows);
        when(foetalMonitorDeviceIDRepo.getFoetalMonitorDeviceIDNotMapped(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(device())));

        String published = service.getvanIDAndFoetalMonitorDeviceID(device());

        assertTrue(published.contains("VanIDs"), published);
        assertTrue(published.contains("MMU Van 1"), published);
        assertTrue(published.contains("deviceIDs"), published);
    }

    @Test
    @DisplayName("getvanIDAndFoetalMonitorDeviceID should report a lookup it could not run")
    void getVanAndDevice_shouldReportFailedLookup() {
        when(masterVanRepo.getVanIDNotMappedWithDevice(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertThrows(IEMRException.class, () -> service.getvanIDAndFoetalMonitorDeviceID(device()));
    }

    @Test
    @DisplayName("vanIDAndDeviceIDMapping should attach the device and mark the van as carrying one")
    void mapping_shouldAttachDeviceAndMarkVan() throws Exception {
        when(foetalMonitorDeviceIDRepo.createMappingOfVanIDAndDeviceID(anyInt(), anyInt(), anyInt(), anyString(),
                anyString(), anyString())).thenReturn(1);
        when(masterVanRepo.updateVanFoetalMonitorsmapping(true, VAN_ID)).thenReturn(1);

        assertEquals(1, service.vanIDAndDeviceIDMapping(device()));
    }

    @Test
    @DisplayName("vanIDAndDeviceIDMapping should refuse a pairing the van could not be marked for")
    void mapping_shouldRefuseWhenVanCannotBeMarked() {
        when(foetalMonitorDeviceIDRepo.createMappingOfVanIDAndDeviceID(anyInt(), anyInt(), anyInt(), anyString(),
                anyString(), anyString())).thenReturn(1);
        when(masterVanRepo.updateVanFoetalMonitorsmapping(anyBoolean(), anyInt())).thenReturn(0);

        assertThrows(IEMRException.class, () -> service.vanIDAndDeviceIDMapping(device()));
    }

    @Test
    @DisplayName("vanIDAndDeviceIDMapping should refuse a pairing the device could not take")
    void mapping_shouldRefuseWhenDeviceCannotTakePairing() {
        when(foetalMonitorDeviceIDRepo.createMappingOfVanIDAndDeviceID(anyInt(), anyInt(), anyInt(), anyString(),
                anyString(), anyString())).thenReturn(0);

        assertThrows(IEMRException.class, () -> service.vanIDAndDeviceIDMapping(device()));
    }

    @Test
    @DisplayName("updateFoetalMonitorDeviceID should report the device it saved")
    void updateDeviceID_shouldReportSavedDevice() throws Exception {
        FoetalMonitorDeviceID request = device();
        when(foetalMonitorDeviceIDRepo.save(request)).thenReturn(request);

        assertEquals(1, service.updateFoetalMonitorDeviceID(request));
    }

    @Test
    @DisplayName("updateFoetalMonitorDeviceID should refuse an edit the store did not take")
    void updateDeviceID_shouldRefuseUntakenEdit() {
        when(foetalMonitorDeviceIDRepo.save(any())).thenReturn(null);

        assertThrows(IEMRException.class, () -> service.updateFoetalMonitorDeviceID(device()));
    }

    @Test
    @DisplayName("getVanIDMappingWorklist should publish the pairings on record")
    void getWorklist_shouldPublishPairings() throws Exception {
        when(foetalMonitorDeviceIDRepo.getMappedWorklist(1, 31, PSM_ID))
                .thenReturn(new ArrayList<>(List.of(device())));

        assertTrue(service.getVanIDMappingWorklist(device()).contains("FS-1"));
    }

    @Test
    @DisplayName("getVanIDMappingWorklist should report a lookup it could not run")
    void getWorklist_shouldReportFailedLookup() {
        when(foetalMonitorDeviceIDRepo.getMappedWorklist(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertThrows(IEMRException.class, () -> service.getVanIDMappingWorklist(device()));
    }

    @Test
    @DisplayName("updatingvanIDAndDeviceIDMapping should clear the old van before attaching the new one")
    void updateMapping_shouldClearOldVanFirst() throws Exception {
        when(foetalMonitorDeviceIDRepo.updateVanDetailsToNull(VFD_ID)).thenReturn(1);
        when(foetalMonitorDeviceIDRepo.createMappingOfVanIDAndDeviceID(anyInt(), anyInt(), anyInt(), anyString(),
                any(), anyString())).thenReturn(1);

        assertEquals(1, service.updatingvanIDAndDeviceIDMapping(device()));
        verify(foetalMonitorDeviceIDRepo).updateVanDetailsToNull(VFD_ID);
    }

    @Test
    @DisplayName("updatingvanIDAndDeviceIDMapping should refuse an edit whose old van could not be cleared")
    void updateMapping_shouldRefuseWhenOldVanCannotBeCleared() {
        when(foetalMonitorDeviceIDRepo.updateVanDetailsToNull(VFD_ID)).thenReturn(0);

        assertThrows(IEMRException.class, () -> service.updatingvanIDAndDeviceIDMapping(device()));
    }

    @Test
    @DisplayName("updatingvanIDAndDeviceIDMapping should refuse an edit the new van could not take")
    void updateMapping_shouldRefuseWhenNewVanCannotTake() {
        when(foetalMonitorDeviceIDRepo.updateVanDetailsToNull(VFD_ID)).thenReturn(1);
        when(foetalMonitorDeviceIDRepo.createMappingOfVanIDAndDeviceID(anyInt(), anyInt(), anyInt(), anyString(),
                any(), anyString())).thenReturn(0);

        assertThrows(IEMRException.class, () -> service.updatingvanIDAndDeviceIDMapping(device()));
    }

    @Test
    @DisplayName("deleteVanIDAndDeviceIDMapping should release the pairing and free the van")
    void deleteMapping_shouldReleasePairingAndFreeVan() throws Exception {
        FoetalMonitorDeviceID request = device();
        request.setDeactivated(Boolean.TRUE);
        when(foetalMonitorDeviceIDRepo.deleteMapping(true, VFD_ID)).thenReturn(1);
        when(masterVanRepo.updateVanFoetalMonitorsmapping(false, VAN_ID)).thenReturn(1);

        assertEquals(1, service.deleteVanIDAndDeviceIDMapping(request));
    }

    @Test
    @DisplayName("deleteVanIDAndDeviceIDMapping should refuse to reinstate a van another device already holds")
    void deleteMapping_shouldRefuseVanHeldByAnotherDevice() {
        FoetalMonitorDeviceID request = device();
        request.setDeactivated(Boolean.FALSE);
        when(foetalMonitorDeviceIDRepo.getMappedVanDetails(VAN_ID)).thenReturn(1);

        IEMRException thrown = assertThrows(IEMRException.class,
                () -> service.deleteVanIDAndDeviceIDMapping(request));
        assertTrue(thrown.getMessage().contains("already mapped with a device"), thrown.getMessage());
    }

    @Test
    @DisplayName("deleteVanIDAndDeviceIDMapping should reinstate a pairing for a van that is free")
    void deleteMapping_shouldReinstatePairingForFreeVan() throws Exception {
        FoetalMonitorDeviceID request = device();
        request.setDeactivated(Boolean.FALSE);
        when(foetalMonitorDeviceIDRepo.getMappedVanDetails(VAN_ID)).thenReturn(0);
        when(foetalMonitorDeviceIDRepo.deleteMapping(false, VFD_ID)).thenReturn(1);
        when(masterVanRepo.updateVanFoetalMonitorsmapping(true, VAN_ID)).thenReturn(1);

        assertEquals(1, service.deleteVanIDAndDeviceIDMapping(request));
    }

    @Test
    @DisplayName("deleteVanIDAndDeviceIDMapping should refuse a pairing the store would not change")
    void deleteMapping_shouldRefuseUnchangedPairing() {
        FoetalMonitorDeviceID request = device();
        request.setDeactivated(Boolean.TRUE);
        when(foetalMonitorDeviceIDRepo.deleteMapping(anyBoolean(), any())).thenReturn(0);

        assertThrows(IEMRException.class, () -> service.deleteVanIDAndDeviceIDMapping(request));
    }
}
