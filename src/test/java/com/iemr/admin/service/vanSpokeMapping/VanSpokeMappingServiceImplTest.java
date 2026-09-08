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
package com.iemr.admin.service.vanSpokeMapping;

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

import com.iemr.admin.data.VanSpokeMapping.m_VanSpokeMapping;
import com.iemr.admin.repo.VanSpokeMappingRepo.VanSpokeMappingRepo;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * The spoke mapping service ties a mobile unit van to the telemedicine spoke it
 * serves, and marks the van itself as spoken for while that tie holds.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VanSpokeMappingServiceImpl Test Suite")
class VanSpokeMappingServiceImplTest {

    private static final Integer VAN_ID = 71;

    @Mock
    private VanSpokeMappingRepo vanSpokeMappingRepo;

    @Mock
    private VanMasterRepository vanMasterRepository;

    @InjectMocks
    private VanSpokeMappingServiceImpl service;

    private static m_VanSpokeMapping mapping() {
        m_VanSpokeMapping mapping = new m_VanSpokeMapping();
        mapping.setVanspokeID(6001);
        mapping.setMmu_VanID(VAN_ID);
        mapping.setMmu_parkingPlaceID(31);
        mapping.setMmu_servicePointID(88);
        mapping.setMmu_vantypeID(2);
        mapping.setCreatedBy("admin");
        mapping.setDeleted(Boolean.FALSE);
        return mapping;
    }

    private static final String SAVE_REQUEST = "{\"vanSpokeMapping\":[{\"mmu_VanID\":71,\"tm_SpokeID\":9,"
            + "\"createdBy\":\"admin\"}]}";

    @Test
    @DisplayName("saveVanSpokeMapping should record the tie and mark the van as spoken for")
    void save_shouldRecordTieAndMarkVanSpokenFor() throws Exception {
        when(vanSpokeMappingRepo.saveAll(anyList())).thenReturn(List.of(mapping()));
        when(vanMasterRepository.updateVanSpokeMapping(VAN_ID, true, "admin")).thenReturn(1);

        assertEquals("success", service.saveVanSpokeMapping(SAVE_REQUEST));
        verify(vanMasterRepository).updateVanSpokeMapping(VAN_ID, true, "admin");
    }

    @Test
    @DisplayName("saveVanSpokeMapping should report failure when the van could not be marked as spoken for")
    void save_shouldReportFailureWhenVanNotMarked() throws Exception {
        when(vanSpokeMappingRepo.saveAll(anyList())).thenReturn(List.of(mapping()));
        when(vanMasterRepository.updateVanSpokeMapping(anyInt(), anyBoolean(), anyString())).thenReturn(0);

        assertEquals("failure", service.saveVanSpokeMapping(SAVE_REQUEST));
    }

    @Test
    @DisplayName("saveVanSpokeMapping should report failure when the request carries no mapping at all")
    void save_shouldReportFailureWithoutMapping() throws Exception {
        assertEquals("failure", service.saveVanSpokeMapping("{\"somethingElse\":1}"));
        verify(vanSpokeMappingRepo, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("saveVanSpokeMapping should report failure when the request's mapping is empty")
    void save_shouldReportFailureForNullMapping() throws Exception {
        assertEquals("failure", service.saveVanSpokeMapping("{\"vanSpokeMapping\":null}"));
    }

    @Test
    @DisplayName("getVanSpokeMappingDetails should answer the ties held at the parking place asked about")
    void get_shouldAnswerTiesAtParkingPlace() throws Exception {
        when(vanSpokeMappingRepo.getVanSpokeMappingDetails(31, 88, 2))
                .thenReturn(new ArrayList<>(List.of(mapping())));

        String answered = service.getVanSpokeMappingDetails(
                "{\"mmu_parkingplaceID\":31,\"mmu_servicePointId\":88,\"mmu_vanTypeID\":2}");

        assertTrue(answered.contains("vanSpokeMappedDetails"), answered);
        assertTrue(answered.contains("6001"), answered);
    }

    @Test
    @DisplayName("getVanSpokeMappingDetails should answer an empty holding when the request names nothing")
    void get_shouldAnswerEmptyHoldingForEmptyRequest() throws Exception {
        assertEquals("{}", service.getVanSpokeMappingDetails("{}"));
    }

    @Test
    @DisplayName("getVanSpokeMappingDetails should give up when the request leaves out a filter it needs")
    void get_shouldGiveUpOnIncompleteRequest() {
        assertThrows(RuntimeException.class,
                () -> service.getVanSpokeMappingDetails("{\"mmu_parkingplaceID\":31}"));
    }

    @Test
    @DisplayName("deleteVanSpokeMapping should release the van when the tie is retired")
    void delete_shouldReleaseVanWhenTieRetired() throws Exception {
        m_VanSpokeMapping retired = mapping();
        retired.setDeleted(Boolean.TRUE);
        when(vanSpokeMappingRepo.save(any(m_VanSpokeMapping.class))).thenReturn(retired);
        when(vanMasterRepository.updateVanSpokeMapping(VAN_ID, false, "admin")).thenReturn(1);

        assertEquals("success", service.deleteVanSpokeMapping(
                "{\"vanSpokeDelete\":{\"vanspokeID\":6001,\"mmu_VanID\":71,\"createdBy\":\"admin\","
                        + "\"deleted\":true}}"));
        verify(vanMasterRepository).updateVanSpokeMapping(VAN_ID, false, "admin");
    }

    @Test
    @DisplayName("deleteVanSpokeMapping should mark the van as spoken for again when the tie is reinstated")
    void delete_shouldMarkVanSpokenForWhenTieReinstated() throws Exception {
        when(vanSpokeMappingRepo.save(any(m_VanSpokeMapping.class))).thenReturn(mapping());
        when(vanMasterRepository.updateVanSpokeMapping(VAN_ID, true, "admin")).thenReturn(1);

        assertEquals("success", service.deleteVanSpokeMapping(
                "{\"vanSpokeDelete\":{\"vanspokeID\":6001,\"mmu_VanID\":71,\"createdBy\":\"admin\","
                        + "\"deleted\":false}}"));
        verify(vanMasterRepository).updateVanSpokeMapping(VAN_ID, true, "admin");
    }

    @Test
    @DisplayName("deleteVanSpokeMapping should report failure when the request carries no tie to retire")
    void delete_shouldReportFailureWithoutTie() throws Exception {
        assertEquals("failure", service.deleteVanSpokeMapping("{\"somethingElse\":1}"));
        verify(vanSpokeMappingRepo, never()).save(any(m_VanSpokeMapping.class));
    }

    @Test
    @DisplayName("deleteVanSpokeMapping should report failure when the van could not be released")
    void delete_shouldReportFailureWhenVanNotReleased() throws Exception {
        when(vanSpokeMappingRepo.save(any(m_VanSpokeMapping.class))).thenReturn(mapping());
        when(vanMasterRepository.updateVanSpokeMapping(anyInt(), anyBoolean(), anyString())).thenReturn(0);

        assertEquals("failure", service.deleteVanSpokeMapping(
                "{\"vanSpokeDelete\":{\"vanspokeID\":6001,\"mmu_VanID\":71,\"createdBy\":\"admin\"}}"));
    }

    @Test
    @DisplayName("updateVanSpokeMapping should record every tie the request carried")
    void update_shouldRecordEveryTie() throws Exception {
        when(vanSpokeMappingRepo.saveAll(anyList())).thenReturn(new ArrayList<>(List.of(mapping())));

        assertEquals("success", service.updateVanSpokeMapping("[{\"vanspokeID\":6001,\"mmu_VanID\":71}]"));
    }

    @Test
    @DisplayName("updateVanSpokeMapping should give up when the request is not a list of ties")
    void update_shouldGiveUpOnUnreadableRequest() {
        assertThrows(RuntimeException.class, () -> service.updateVanSpokeMapping("{not json"));
    }
}
