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
package com.iemr.admin.service.userParkingPlaceMap;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.userParkingPlaceMap.M_UserParkingPlaceMap;
import com.iemr.admin.data.userParkingPlaceMap.M_UserVanMapping;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repository.userParkingPlaceMap.UserParkingPlaceMapRepository;
import com.iemr.admin.repository.userParkingPlaceMap.UserVanMappingRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The user parking place service keeps a field user's posting and the vans that
 * posting covers in step, replacing the van list wholesale on every edit.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserParkingPlaceMapServiceImpl Test Suite")
class UserParkingPlaceMapServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer MAP_ID = 9001;
    private static final Integer USER_ID = 3117;

    @Mock
    private UserParkingPlaceMapRepository userParkingPlaceMapRepository;

    @Mock
    private UserVanMappingRepository userVanMappingRepository;

    @Mock
    private EmployeeMasterRepo employeeMasterRepo;

    @InjectMocks
    private UserParkingPlaceMapServiceImpl service;

    private static M_UserParkingPlaceMap posting(Integer id) {
        return new M_UserParkingPlaceMap(id, USER_ID, "Asha", "Rao", "asha.rao", 7, 301, 31, "Hosur parking",
                PSM_ID, Boolean.FALSE, Boolean.FALSE);
    }

    private static M_UserVanMapping vanMapping(Integer id) {
        M_UserVanMapping mapping = new M_UserVanMapping();
        mapping.setUserVanMapID(id);
        mapping.setVanID(71);
        return mapping;
    }

    @Test
    @DisplayName("saveUserParkingPlaceDetails should attach the vans to each posting it stored")
    void save_shouldAttachVansToStoredPostings() {
        M_UserParkingPlaceMap stored = posting(MAP_ID);
        stored.setCreatedBy("admin");
        stored.setUservanmapping(new ArrayList<>(List.of(vanMapping(null))));
        when(userParkingPlaceMapRepository.saveAll(anyList()))
                .thenReturn(new ArrayList<>(List.of(stored)));

        service.saveUserParkingPlaceDetails(new ArrayList<>(List.of(stored)));

        ArgumentCaptor<List<M_UserVanMapping>> captor = ArgumentCaptor.forClass(List.class);
        verify(userVanMappingRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(MAP_ID, captor.getValue().get(0).getUserParkingPlaceMapID());
        assertEquals("admin", captor.getValue().get(0).getCreatedBy());
    }

    @Test
    @DisplayName("getUserParkingPlaceMappings should pass every filter the caller supplies through")
    void getMappings_shouldPassFiltersThrough() {
        when(userParkingPlaceMapRepository.getUserParkingPlaceMappings(77, "29", "301", "31", "7"))
                .thenReturn(List.<Object[]>of(new Object[] { MAP_ID, USER_ID, "Asha", "Rao", "asha.rao", 7,
                        "11", "Counsellor", (short) 2, "Female", 29, "Karnataka", 301, "Bengaluru Urban",
                        31, "Hosur parking", "9000000001", PSM_ID, Boolean.FALSE }));

        assertEquals(1, service.getUserParkingPlaceMappings(77, 29, 301, 31, 7).size());
    }

    @Test
    @DisplayName("getUserParkingPlaceMappings should match everything for a filter the caller leaves blank")
    void getMappings_shouldWildcardBlankFilters() {
        when(userParkingPlaceMapRepository.getUserParkingPlaceMappings(77, "%%", "%%", "%%", "%%"))
                .thenReturn(new ArrayList<>());

        service.getUserParkingPlaceMappings(77, null, null, null, null);

        verify(userParkingPlaceMapRepository).getUserParkingPlaceMappings(77, "%%", "%%", "%%", "%%");
    }

    @Test
    @DisplayName("getUserParkingPlaceMappings1 should rebuild one posting per row the query answers")
    void getMappings1_shouldRebuildEachRow() {
        when(userParkingPlaceMapRepository.getUserParkingPlaceMappings1(PSM_ID, 31, 7))
                .thenReturn(List.<Object[]>of(new Object[] { MAP_ID, USER_ID, "Asha", "Rao", "asha.rao", 7,
                        301, 31, "Hosur parking", PSM_ID, Boolean.FALSE, Boolean.FALSE, "ASHA" }));

        assertEquals(1, service.getUserParkingPlaceMappings1(PSM_ID, 301, 31, 7).size());
    }

    @Test
    @DisplayName("the record lookups should each reach their own repository query")
    void recordLookups_shouldReachTheirOwnQuery() {
        M_UserParkingPlaceMap stored = posting(MAP_ID);
        ArrayList<M_UserParkingPlaceMap> postings = new ArrayList<>(List.of(stored));
        when(userParkingPlaceMapRepository.getUserParkingPlaceMapByID(MAP_ID)).thenReturn(stored);
        when(userParkingPlaceMapRepository.findByUserParkingPlaceMapID(MAP_ID)).thenReturn(stored);
        when(userParkingPlaceMapRepository.save(stored)).thenReturn(stored);
        when(userParkingPlaceMapRepository.saveAll(anyList())).thenReturn(postings);
        when(userParkingPlaceMapRepository.updateUserParkingPlaceMapStatus(MAP_ID, Boolean.FALSE, null))
                .thenReturn(1);

        assertSame(stored, service.getUserParkingPlaceMapByID(MAP_ID));
        assertSame(stored, service.getUserParkingPlaceDetails(MAP_ID));
        assertSame(stored, service.saveediteddata(stored));
        assertSame(postings, service.saveUserParkingPlaceDetails1(new ArrayList<>()));
        assertEquals(1, service.updateUserParkingPlaceMapStatus(stored));
    }

    @Test
    @DisplayName("saveediteddata should replace the van list rather than add to it")
    void saveEdited_shouldReplaceVanList() {
        M_UserParkingPlaceMap stored = posting(MAP_ID);
        stored.setModifiedBy("admin");
        M_UserVanMapping fresh = vanMapping(null);
        when(userParkingPlaceMapRepository.save(stored)).thenReturn(stored);
        when(userVanMappingRepository.saveAll(anyList()))
                .thenAnswer(call -> new ArrayList<>((List<M_UserVanMapping>) call.getArgument(0)));

        M_UserParkingPlaceMap saved = service.saveediteddata(stored, List.of(fresh));

        verify(userVanMappingRepository).deactivatebyuserparkingplaceid(MAP_ID, "admin");
        assertEquals(1, saved.getUservanmapping().size());
        assertEquals(MAP_ID, saved.getUservanmapping().get(0).getUserParkingPlaceMapID());
        assertNull(saved.getUservanmapping().get(0).getUserParkingPlaceMap(),
                "the van mapping must not carry the posting back with it");
    }

    @Test
    @DisplayName("getunmappedUser should exclude the users already posted when there are any")
    void getunmappedUser_shouldExcludePostedUsers() {
        when(userParkingPlaceMapRepository.getmappedids(PSM_ID, 7)).thenReturn(List.of(USER_ID));
        when(employeeMasterRepo.getAllEmpByProviderServiceMapIDAndDesignationNotInUserID(PSM_ID, 7,
                List.of(USER_ID))).thenReturn(List.<Object[]>of(new Object[] { 3118, "Ravi Kumar" }));

        assertEquals(1, service.getunmappedUser(PSM_ID, 7).size());
        verify(employeeMasterRepo, never()).getAllEmpByProviderServiceMapIDAndDesignation(anyInt(), anyInt());
    }

    @Test
    @DisplayName("getunmappedUser should answer every user when none is posted yet")
    void getunmappedUser_shouldAnswerEveryUserWhenNonePosted() {
        when(userParkingPlaceMapRepository.getmappedids(PSM_ID, 7)).thenReturn(new ArrayList<>());
        when(employeeMasterRepo.getAllEmpByProviderServiceMapIDAndDesignation(PSM_ID, 7))
                .thenReturn(List.<Object[]>of(new Object[] { 3118, "Ravi Kumar" }));

        assertEquals(1, service.getunmappedUser(PSM_ID, 7).size());
    }

    @Test
    @DisplayName("getuserexist should report whether the user already holds a live posting")
    void getuserexist_shouldReportWhetherUserIsPosted() {
        when(userParkingPlaceMapRepository.findByProviderServiceMapIDAndUserIDAndDeleted(PSM_ID, USER_ID, false))
                .thenReturn(List.of(posting(MAP_ID)));
        assertTrue(service.getuserexist(PSM_ID, USER_ID));

        when(userParkingPlaceMapRepository.findByProviderServiceMapIDAndUserIDAndDeleted(PSM_ID, USER_ID, false))
                .thenReturn(new ArrayList<>());
        assertFalse(service.getuserexist(PSM_ID, USER_ID));
    }

    @Test
    @DisplayName("getuservanmapping should answer the vans the posting covers")
    void getuservanmapping_shouldAnswerCoveredVans() {
        List<M_UserVanMapping> stored = List.of(vanMapping(7001));
        when(userVanMappingRepository.findByUserParkingPlaceMapIDAndDeleted(MAP_ID)).thenReturn(stored);

        assertSame(stored, service.getuservanmapping(MAP_ID));
    }

    @Test
    @DisplayName("deleteuservanmapping should release the van mapping the caller names")
    void deleteuservanmapping_shouldReleaseNamedMapping() {
        M_UserVanMapping request = vanMapping(7001);
        request.setModifiedBy("admin");

        service.deleteuservanmapping(request);

        verify(userVanMappingRepository).deleteUservanMap(7001, "admin");
    }
}
