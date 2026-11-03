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
package com.iemr.admin.controller.facilitytype;

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

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.data.store.M_FacilityLevel;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.service.facilitytype.M_facilitytypeInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The facility type endpoints define the levels of the health facility
 * hierarchy, so a type still in use may not be retired out from under the
 * facilities that carry it.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FacilitytypeController Test Suite")
class FacilitytypeControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer TYPE_ID = 3;

    @Mock
    private M_facilitytypeInter m_facilitytypeInter;

    @Mock
    private MainStoreRepo mainStoreRepo;

    @InjectMocks
    private FacilitytypeController controller;

    private static M_facilitytype type(Integer id, String name) {
        M_facilitytype type = new M_facilitytype();
        type.setFacilityTypeID(id);
        type.setFacilityTypeName(name);
        return type;
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

    @Test
    @DisplayName("getFacility should answer the facility types of the provider")
    void getFacility_shouldAnswerProviderTypes() {
        when(m_facilitytypeInter.getAllFicilityData(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(type(TYPE_ID, "PHC"))));

        assertSuccessContaining(controller.getFacility("{\"providerServiceMapID\":4001}"), "PHC");
    }

    @Test
    @DisplayName("getFacility should answer an error envelope when the lookup fails")
    void getFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.getAllFicilityData(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFacility("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("addFacility should answer the facility types the service stored")
    void addFacility_shouldAnswerStoredTypes() {
        when(m_facilitytypeInter.addAllFicilityData(anyList()))
                .thenReturn(new ArrayList<>(List.of(type(TYPE_ID, "PHC"))));

        assertSuccessContaining(controller.addFacility("[{\"facilityTypeName\":\"PHC\"}]"), "PHC");
    }

    @Test
    @DisplayName("addFacility should answer an error envelope when the store fails")
    void addFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.addAllFicilityData(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.addFacility("[{\"facilityTypeName\":\"PHC\"}]"));
    }

    @Test
    @DisplayName("editFacility should copy only the fields the request actually sets")
    void editFacility_shouldCopyOnlySuppliedFields() {
        M_facilitytype stored = type(TYPE_ID, "old name");
        stored.setRuralUrban("Rural");
        stored.setFacilityTypeDesc("old description");
        when(m_facilitytypeInter.editAllFicilityData(TYPE_ID)).thenReturn(stored);
        when(m_facilitytypeInter.updateFacilityData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editFacility(
                "{\"facilityTypeID\":3,\"facilityTypeName\":\"PHC\",\"modifiedBy\":\"admin\"}"), "PHC");
        assertEquals("Rural", stored.getRuralUrban(), "an unset field keeps the value on record");
        assertEquals("old description", stored.getFacilityTypeDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("editFacility should copy the rural-urban split and description when the request sets them")
    void editFacility_shouldCopySuppliedSplitAndDescription() {
        M_facilitytype stored = type(TYPE_ID, "old name");
        when(m_facilitytypeInter.editAllFicilityData(TYPE_ID)).thenReturn(stored);
        when(m_facilitytypeInter.updateFacilityData(stored)).thenReturn(stored);

        controller.editFacility("{\"facilityTypeID\":3,\"ruralUrban\":\"Urban\","
                + "\"facilityTypeDesc\":\"Urban primary centre\",\"modifiedBy\":\"admin\"}");

        assertEquals("Urban", stored.getRuralUrban());
        assertEquals("Urban primary centre", stored.getFacilityTypeDesc());
    }

    @Test
    @DisplayName("editFacility should answer an error envelope for a facility type that does not exist")
    void editFacility_shouldAnswerErrorEnvelopeForUnknownType() {
        when(m_facilitytypeInter.editAllFicilityData(TYPE_ID)).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.editFacility("{\"facilityTypeID\":3}")));
    }

    @Test
    @DisplayName("deleteFacility should refuse to retire a facility type facilities still carry")
    void deleteFacility_shouldRefuseTypeInUse() {
        when(m_facilitytypeInter.editAllFicilityData(TYPE_ID)).thenReturn(type(TYPE_ID, "PHC"));
        when(mainStoreRepo.findByFacilityTypeIDAndDeletedFalse(TYPE_ID))
                .thenReturn(List.of(new M_Facility(), new M_Facility()));

        String response = controller.deleteFacility("{\"facilityTypeID\":3,\"deleted\":true}");

        assertGenericFailure(response);
        assertTrue(response.contains("in use by 2 active facilities"), response);
    }

    @Test
    @DisplayName("deleteFacility should retire a facility type nothing carries")
    void deleteFacility_shouldRetireUnusedType() {
        M_facilitytype stored = type(TYPE_ID, "PHC");
        when(m_facilitytypeInter.editAllFicilityData(TYPE_ID)).thenReturn(stored);
        when(mainStoreRepo.findByFacilityTypeIDAndDeletedFalse(TYPE_ID)).thenReturn(new ArrayList<>());
        when(m_facilitytypeInter.updateFacilityData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteFacility("{\"facilityTypeID\":3,\"deleted\":true}"), "PHC");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteFacility should reinstate a facility type without checking what carries it")
    void deleteFacility_shouldReinstateWithoutChecking() {
        M_facilitytype stored = type(TYPE_ID, "PHC");
        stored.setDeleted(Boolean.TRUE);
        when(m_facilitytypeInter.editAllFicilityData(TYPE_ID)).thenReturn(stored);
        when(m_facilitytypeInter.updateFacilityData(stored)).thenReturn(stored);

        controller.deleteFacility("{\"facilityTypeID\":3,\"deleted\":false}");

        verify(mainStoreRepo, never()).findByFacilityTypeIDAndDeletedFalse(anyInt());
    }

    @Test
    @DisplayName("checkFacilityTypeCode should report whether the code is already taken")
    void checkFacilityTypeCode_shouldReportWhetherCodeIsTaken() {
        when(m_facilitytypeInter.checkFacilityTypeCode(any())).thenReturn(Boolean.TRUE);

        assertSuccessContaining(controller.checkFacilityTypeCode("{\"facilityTypeCode\":\"PHC-1\"}"), "true");
    }

    @Test
    @DisplayName("checkFacilityTypeCode should answer an error envelope when the check fails")
    void checkFacilityTypeCode_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.checkFacilityTypeCode(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkFacilityTypeCode("{\"facilityTypeCode\":\"PHC-1\"}"));
    }

    @Test
    @DisplayName("getFacilityTypesByRuralUrban should narrow the types to the split the caller names")
    void getByRuralUrban_shouldNarrowToSplit() {
        when(m_facilitytypeInter.getFacilityTypesByRuralUrban(PSM_ID, "Rural"))
                .thenReturn(new ArrayList<>(List.of(type(TYPE_ID, "PHC"))));

        assertSuccessContaining(controller.getFacilityTypesByRuralUrban(
                "{\"providerServiceMapID\":4001,\"ruralUrban\":\"Rural\"}"), "PHC");
    }

    @Test
    @DisplayName("getFacilityTypesByRuralUrban should answer an error envelope when the lookup fails")
    void getByRuralUrban_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.getFacilityTypesByRuralUrban(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFacilityTypesByRuralUrban("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getFacilityLevels should answer the hierarchy levels on record")
    void getFacilityLevels_shouldAnswerHierarchyLevels() {
        M_FacilityLevel level = new M_FacilityLevel();
        when(m_facilitytypeInter.getFacilityLevels()).thenReturn(new ArrayList<>(List.of(level)));

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(controller.getFacilityLevels()));
    }

    @Test
    @DisplayName("getFacilityLevels should answer an error envelope when the lookup fails")
    void getFacilityLevels_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.getFacilityLevels()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFacilityLevels());
    }

    @Test
    @DisplayName("getFacilityTypesByBlock should answer the types used in the block")
    void getByBlock_shouldAnswerBlockTypes() {
        when(m_facilitytypeInter.getFacilityTypesByBlock(401))
                .thenReturn(new ArrayList<>(List.of(type(TYPE_ID, "PHC"))));

        assertSuccessContaining(controller.getFacilityTypesByBlock("{\"blockID\":401}"), "PHC");
    }

    @Test
    @DisplayName("getFacilityTypesByBlock should answer an error envelope when the lookup fails")
    void getByBlock_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.getFacilityTypesByBlock(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFacilityTypesByBlock("{\"blockID\":401}"));
    }

    @Test
    @DisplayName("getFacilityTypesByState should answer the types used in the state")
    void getByState_shouldAnswerStateTypes() {
        when(m_facilitytypeInter.getFacilityTypesByState(29))
                .thenReturn(new ArrayList<>(List.of(type(TYPE_ID, "PHC"))));

        assertSuccessContaining(controller.getFacilityTypesByState("{\"stateID\":29}"), "PHC");
    }

    @Test
    @DisplayName("getFacilityTypesByState should answer an error envelope when the lookup fails")
    void getByState_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.getFacilityTypesByState(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFacilityTypesByState("{\"stateID\":29}"));
    }

    @Test
    @DisplayName("checkFacilityTypeName should report whether the name is already used in the state")
    void checkFacilityTypeName_shouldReportWhetherNameIsUsed() {
        when(m_facilitytypeInter.checkFacilityTypeNameExists("PHC", 29)).thenReturn(true);

        assertSuccessContaining(
                controller.checkFacilityTypeName("{\"facilityTypeName\":\"PHC\",\"stateID\":29}"), "true");
    }

    @Test
    @DisplayName("checkFacilityTypeName should answer an error envelope when the check fails")
    void checkFacilityTypeName_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_facilitytypeInter.checkFacilityTypeNameExists(anyString(), anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkFacilityTypeName("{\"facilityTypeName\":\"PHC\",\"stateID\":29}"));
    }
}
