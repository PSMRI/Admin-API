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
package com.iemr.admin.service.facilitytype;

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

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.store.M_FacilityLevel;
import com.iemr.admin.repository.facilitytype.M_FacilityLevelRepo;
import com.iemr.admin.repository.facilitytype.M_facilitytypeRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The facility type service keeps the kinds of health facility a state runs,
 * refusing a type name a state already has.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("M_facilitytypeServiceImpl Test Suite")
class M_facilitytypeServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer STATE_ID = 29;
    private static final Integer FACILITY_TYPE_ID = 12;

    @Mock
    private M_facilitytypeRepo m_facilitytypeRepo;

    @Mock
    private M_FacilityLevelRepo m_facilityLevelRepo;

    @InjectMocks
    private M_facilitytypeServiceImpl service;

    private static M_facilitytype facilityType() {
        M_facilitytype facilityType = new M_facilitytype();
        facilityType.setFacilityTypeID(FACILITY_TYPE_ID);
        facilityType.setFacilityTypeName("Primary Health Centre");
        facilityType.setFacilityTypeCode("PHC");
        facilityType.setProviderServiceMapID(PSM_ID);
        facilityType.setStateID(STATE_ID);
        return facilityType;
    }

    @Test
    @DisplayName("getAllFicilityData should answer the facility types of the provider asked about")
    void getAll_shouldAnswerProvidersFacilityTypes() {
        ArrayList<M_facilitytype> held = new ArrayList<>(List.of(facilityType()));
        when(m_facilitytypeRepo.getAllFicilityData(PSM_ID)).thenReturn(held);

        assertSame(held, service.getAllFicilityData(PSM_ID));
    }

    @Test
    @DisplayName("getFacilityTypesByRuralUrban should narrow the types to the setting asked for")
    void getByRuralUrban_shouldNarrowToSetting() {
        when(m_facilitytypeRepo.findByProviderServiceMapIDAndRuralUrban(PSM_ID, "Rural"))
                .thenReturn(List.of(facilityType()));

        assertEquals(1, service.getFacilityTypesByRuralUrban(PSM_ID, "Rural").size());
    }

    @Test
    @DisplayName("addAllFicilityData should store a facility type the state does not have yet")
    void add_shouldStoreNewFacilityType() {
        ArrayList<M_facilitytype> stored = new ArrayList<>(List.of(facilityType()));
        when(m_facilitytypeRepo.existsByFacilityTypeNameAndStateIDAndDeletedFalse(anyString(), anyInt()))
                .thenReturn(false);
        when(m_facilitytypeRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.addAllFicilityData(List.of(facilityType())));
    }

    @Test
    @DisplayName("addAllFicilityData should refuse a facility type name the state already has")
    void add_shouldRefuseDuplicateName() {
        when(m_facilitytypeRepo.existsByFacilityTypeNameAndStateIDAndDeletedFalse("Primary Health Centre",
                STATE_ID)).thenReturn(true);

        RuntimeException refusal = assertThrows(RuntimeException.class,
                () -> service.addAllFicilityData(List.of(facilityType())));

        assertTrue(refusal.getMessage().contains("Primary Health Centre"), refusal.getMessage());
        verify(m_facilitytypeRepo, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("editAllFicilityData and updateFacilityData should each reach their own repository query")
    void singleRecordOperations_shouldReachTheirOwnQuery() {
        M_facilitytype stored = facilityType();
        when(m_facilitytypeRepo.findByFacilityTypeID(FACILITY_TYPE_ID)).thenReturn(stored);
        when(m_facilitytypeRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.editAllFicilityData(FACILITY_TYPE_ID));
        assertSame(stored, service.updateFacilityData(stored));
    }

    @Test
    @DisplayName("editAllFicilityData should answer nothing when the facility type is unknown")
    void edit_shouldAnswerNothingForUnknownFacilityType() {
        when(m_facilitytypeRepo.findByFacilityTypeID(-1)).thenReturn(null);

        assertNull(service.editAllFicilityData(-1));
    }

    @Test
    @DisplayName("checkFacilityTypeCode should report whether the provider already uses the code")
    void checkCode_shouldReportWhetherCodeIsUsed() {
        when(m_facilitytypeRepo.findByFacilityTypeCodeAndProviderServiceMapID("PHC", PSM_ID))
                .thenReturn(List.of(facilityType()));
        assertTrue(service.checkFacilityTypeCode(facilityType()));

        when(m_facilitytypeRepo.findByFacilityTypeCodeAndProviderServiceMapID("PHC", PSM_ID))
                .thenReturn(new ArrayList<>());
        assertFalse(service.checkFacilityTypeCode(facilityType()));
    }

    @Test
    @DisplayName("getFacilityLevels should answer the levels still in use, named in order")
    void getLevels_shouldAnswerLiveLevels() {
        ArrayList<M_FacilityLevel> levels = new ArrayList<>(List.of(new M_FacilityLevel()));
        when(m_facilityLevelRepo.findByDeletedFalseOrderByLevelName()).thenReturn(levels);

        assertSame(levels, service.getFacilityLevels());
    }

    @Test
    @DisplayName("getFacilityTypesByBlock should answer the types run in the taluk asked about")
    void getByBlock_shouldAnswerTypesOfTaluk() {
        when(m_facilitytypeRepo.findFacilityTypesByBlock(3011)).thenReturn(List.of(facilityType()));

        assertEquals(1, service.getFacilityTypesByBlock(3011).size());
    }

    @Test
    @DisplayName("getFacilityTypesByState should answer the types run in the state asked about")
    void getByState_shouldAnswerTypesOfState() {
        when(m_facilitytypeRepo.findByStateID(STATE_ID)).thenReturn(List.of(facilityType()));

        assertEquals(1, service.getFacilityTypesByState(STATE_ID).size());
    }

    @Test
    @DisplayName("checkFacilityTypeNameExists should report whether the state already has the name")
    void checkName_shouldReportWhetherNameIsUsed() {
        when(m_facilitytypeRepo.existsByFacilityTypeNameAndStateIDAndDeletedFalse("Primary Health Centre",
                STATE_ID)).thenReturn(true);
        assertTrue(service.checkFacilityTypeNameExists("Primary Health Centre", STATE_ID));

        when(m_facilitytypeRepo.existsByFacilityTypeNameAndStateIDAndDeletedFalse("Sub Centre", STATE_ID))
                .thenReturn(false);
        assertFalse(service.checkFacilityTypeNameExists("Sub Centre", STATE_ID));
    }
}
