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
package com.iemr.admin.controller.store;

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

import com.iemr.admin.data.store.FacilityVillageMapping;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.data.store.M_facilityMap;
import com.iemr.admin.data.store.V_FetchFacility;
import com.iemr.admin.service.store.StoreService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The store endpoints maintain the facility hierarchy an operator sees on the
 * store screens.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StoreController Test Suite")
class StoreControllerTest {

    private static final Integer FACILITY_ID = 501;
    private static final Integer PSM_ID = 4001;
    private static final Integer BLOCK_ID = 401;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController controller;

    private static M_Facility facility(Integer id, String name) {
        M_Facility facility = new M_Facility();
        facility.setFacilityID(id);
        facility.setFacilityName(name);
        return facility;
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
    @DisplayName("createStore should answer the facilities the service stored")
    void createStore_shouldAnswerStoredFacilities() {
        when(storeService.addAllMainStore(anyList())).thenReturn(List.of(facility(FACILITY_ID, "PHC North")));

        assertSuccessContaining(controller.createStore("[{\"facilityName\":\"PHC North\"}]"), "PHC North");
    }

    @Test
    @DisplayName("createStore should answer an error envelope when the store fails")
    void createStore_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.addAllMainStore(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createStore("[{\"facilityName\":\"PHC North\"}]"));
    }

    @Test
    @DisplayName("editStore should copy the edited fields onto the stored facility")
    void editStore_shouldCopyEditedFields() {
        M_Facility stored = facility(FACILITY_ID, "PHC North");
        when(storeService.getMainStore(FACILITY_ID)).thenReturn(stored);
        when(storeService.createMainStore(stored)).thenReturn(stored);

        String response = controller.editStore("{\"facilityID\":501,\"facilityDesc\":\"Primary centre\","
                + "\"location\":\"Main Road\",\"physicalLocation\":\"Block A\",\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "PHC North");
        assertEquals("Primary centre", stored.getFacilityDesc());
        assertEquals("Block A", stored.getPhysicalLocation());
    }

    @Test
    @DisplayName("editStore should answer an error envelope for a facility that does not exist")
    void editStore_shouldAnswerErrorEnvelopeForUnknownFacility() {
        when(storeService.getMainStore(FACILITY_ID)).thenReturn(null);

        assertCodeException(controller.editStore("{\"facilityID\":501}"));
    }

    @Test
    @DisplayName("getAllStore should answer the facilities of the provider")
    void getAllStore_shouldAnswerProviderFacilities() {
        when(storeService.getAllMainStore(PSM_ID)).thenReturn(List.of(facility(FACILITY_ID, "PHC North")));

        assertSuccessContaining(controller.getAllStore(PSM_ID), "PHC North");
    }

    @Test
    @DisplayName("getAllStore should answer an error envelope when the lookup fails")
    void getAllStore_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getAllMainStore(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllStore(PSM_ID));
    }

    @Test
    @DisplayName("getMainFacility should answer the main facilities of the provider")
    void getMainFacility_shouldAnswerMainFacilities() {
        when(storeService.getMainFacility(PSM_ID, true))
                .thenReturn(new ArrayList<>(List.of(facility(FACILITY_ID, "PHC North"))));

        assertSuccessContaining(
                controller.getMainFacility("{\"providerServiceMapID\":4001,\"isMainFacility\":true}"), "PHC North");
    }

    @Test
    @DisplayName("getMainFacility should answer an error envelope when the lookup fails")
    void getMainFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getMainFacility(any(), any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getMainFacility("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getsubFacility should answer the facilities under the main facility")
    void getsubFacility_shouldAnswerSubFacilities() {
        when(storeService.getMainFacility(PSM_ID, false, 500))
                .thenReturn(new ArrayList<>(List.of(facility(FACILITY_ID, "Sub Centre"))));

        assertSuccessContaining(controller.getsubFacility("{\"providerServiceMapID\":4001,"
                + "\"isMainFacility\":false,\"mainFacilityID\":500}"), "Sub Centre");
    }

    @Test
    @DisplayName("getsubFacility should answer an error envelope when the lookup fails")
    void getsubFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getMainFacility(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getsubFacility("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("deleteStore should answer the facility the service retired")
    void deleteStore_shouldAnswerRetiredFacility() throws Exception {
        M_Facility request = facility(FACILITY_ID, "PHC North");
        when(storeService.deleteStore(request)).thenReturn(request);

        assertSuccessContaining(controller.deleteStore(request), "PHC North");
    }

    @Test
    @DisplayName("deleteStore should report the reason the service refused to retire the facility")
    void deleteStore_shouldReportRefusal() throws Exception {
        M_Facility request = facility(FACILITY_ID, "PHC North");
        when(storeService.deleteStore(request)).thenThrow(new Exception("Child Stores are still active"));

        String response = controller.deleteStore(request);

        assertGenericFailure(response);
        assertTrue(response.contains("Child Stores are still active"), response);
    }

    @Test
    @DisplayName("mapStore should answer how many mappings the service changed")
    void mapStore_shouldAnswerChangedCount() {
        when(storeService.mapStore(anyList())).thenReturn(2);

        assertSuccessContaining(controller.mapStore(List.of(new M_facilityMap())), "2");
    }

    @Test
    @DisplayName("mapStore should answer an error envelope when the mapping fails")
    void mapStore_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.mapStore(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.mapStore(List.of(new M_facilityMap())));
    }

    @Test
    @DisplayName("deleteMapStore should answer how many mappings the service released")
    void deleteMapStore_shouldAnswerReleasedCount() throws Exception {
        M_facilityMap request = new M_facilityMap();
        when(storeService.deleteMapStore(request)).thenReturn(1);

        assertSuccessContaining(controller.deleteMapStore(request), "1");
    }

    @Test
    @DisplayName("deleteMapStore should report the reason the service refused to release the mapping")
    void deleteMapStore_shouldReportRefusal() throws Exception {
        M_facilityMap request = new M_facilityMap();
        when(storeService.deleteMapStore(request))
                .thenThrow(new Exception("Please Unmap van under this Parking Place"));

        assertTrue(controller.deleteMapStore(request).contains("Please Unmap van under this Parking Place"));
    }

    @Test
    @DisplayName("getMapStore should answer the mapped facilities of the provider")
    void getMapStore_shouldAnswerMappedFacilities() {
        V_FetchFacility request = new V_FetchFacility();
        request.setProviderServiceMapID(PSM_ID);
        V_FetchFacility mapped = new V_FetchFacility();
        mapped.setFacilityName("PHC North");
        when(storeService.getMapStore(request)).thenReturn(List.of(mapped));

        assertSuccessContaining(controller.getMapStore(request), "PHC North");
    }

    @Test
    @DisplayName("getMapStore should answer an error envelope when the lookup fails")
    void getMapStore_shouldAnswerErrorEnvelopeOnFailure() {
        V_FetchFacility request = new V_FetchFacility();
        when(storeService.getMapStore(request)).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getMapStore(request));
    }

    @Test
    @DisplayName("getFacilitiesByBlock should answer the live facilities in the block")
    void getFacilitiesByBlock_shouldAnswerLiveFacilities() {
        when(storeService.getFacilitiesByBlock(BLOCK_ID))
                .thenReturn(new ArrayList<>(List.of(facility(FACILITY_ID, "PHC North"))));

        assertSuccessContaining(controller.getFacilitiesByBlock("{\"blockID\":401}"), "PHC North");
    }

    @Test
    @DisplayName("getFacilitiesByBlock should answer an error envelope when the lookup fails")
    void getFacilitiesByBlock_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getFacilitiesByBlock(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFacilitiesByBlock("{\"blockID\":401}"));
    }

    @Test
    @DisplayName("getAllFacilitiesByBlock should answer the retired facilities of the block as well")
    void getAllFacilitiesByBlock_shouldAnswerRetiredFacilitiesToo() {
        when(storeService.getAllFacilitiesByBlock(BLOCK_ID))
                .thenReturn(new ArrayList<>(List.of(facility(FACILITY_ID, "PHC North"))));

        assertSuccessContaining(controller.getAllFacilitiesByBlock("{\"blockID\":401}"), "PHC North");
    }

    @Test
    @DisplayName("getAllFacilitiesByBlock should answer an error envelope when the lookup fails")
    void getAllFacilitiesByBlock_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getAllFacilitiesByBlock(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllFacilitiesByBlock("{\"blockID\":401}"));
    }

    @Test
    @DisplayName("checkStoreCode should report whether the facility code is already taken")
    void checkStoreCode_shouldReportWhetherCodeIsTaken() {
        when(storeService.checkStoreCode(any())).thenReturn(Boolean.TRUE);

        assertSuccessContaining(controller.checkStoreCode("{\"facilityCode\":\"PHC-1\"}"), "true");
    }

    @Test
    @DisplayName("checkStoreCode should answer an error envelope when the check fails")
    void checkStoreCode_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.checkStoreCode(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkStoreCode("{\"facilityCode\":\"PHC-1\"}"));
    }

    @Test
    @DisplayName("getFacilitiesByBlockAndLevel should pass every filter the screen sends through")
    void getFacilitiesByBlockAndLevel_shouldPassFiltersThrough() {
        when(storeService.getFacilitiesByBlockAndLevel(BLOCK_ID, 4, "Rural"))
                .thenReturn(new ArrayList<>(List.of(facility(FACILITY_ID, "Sub Centre"))));

        assertSuccessContaining(controller.getFacilitiesByBlockAndLevel(
                "{\"blockID\":401,\"levelValue\":4,\"ruralUrban\":\"Rural\"}"), "Sub Centre");
        verify(storeService).getFacilitiesByBlockAndLevel(BLOCK_ID, 4, "Rural");
    }

    @Test
    @DisplayName("getFacilitiesByBlockAndLevel should answer an error envelope when the lookup fails")
    void getFacilitiesByBlockAndLevel_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getFacilitiesByBlockAndLevel(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFacilitiesByBlockAndLevel("{\"blockID\":401}"));
    }

    @Test
    @DisplayName("createFacilityWithHierarchy should pass the villages and children through to the service")
    void createFacilityWithHierarchy_shouldPassHierarchyThrough() {
        when(storeService.createFacilityWithHierarchy(any(), anyList(), anyInt(), anyList()))
                .thenReturn(facility(FACILITY_ID, "PHC North"));

        String response = controller.createFacilityWithHierarchy("{\"facility\":{\"facilityName\":\"PHC North\"},"
                + "\"villageIDs\":[601,602],\"mainVillageID\":601,\"childFacilityIDs\":[502]}");

        assertSuccessContaining(response, "PHC North");
        verify(storeService).createFacilityWithHierarchy(any(), org.mockito.ArgumentMatchers.eq(List.of(601, 602)),
                org.mockito.ArgumentMatchers.eq(601), org.mockito.ArgumentMatchers.eq(List.of(502)));
    }

    @Test
    @DisplayName("createFacilityWithHierarchy should report the reason the service refused the facility")
    void createFacilityWithHierarchy_shouldReportRefusal() {
        when(storeService.createFacilityWithHierarchy(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Facility with this name already exists in this block"));

        assertTrue(controller.createFacilityWithHierarchy("{\"facility\":{\"facilityName\":\"PHC North\"}}")
                .contains("already exists in this block"));
    }

    @Test
    @DisplayName("getMappedVillageIDs should answer the villages already spoken for in the block")
    void getMappedVillageIDs_shouldAnswerSpokenForVillages() {
        when(storeService.getMappedVillageIDs(BLOCK_ID)).thenReturn(List.of(601, 602));

        assertSuccessContaining(controller.getMappedVillageIDs("{\"blockID\":401}"), "601");
    }

    @Test
    @DisplayName("getMappedVillageIDs should answer an error envelope when the lookup fails")
    void getMappedVillageIDs_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getMappedVillageIDs(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getMappedVillageIDs("{\"blockID\":401}"));
    }

    @Test
    @DisplayName("getVillageMappingsByFacility should answer the villages the facility serves")
    void getVillageMappingsByFacility_shouldAnswerServedVillages() {
        FacilityVillageMapping mapping = new FacilityVillageMapping();
        mapping.setDistrictBranchID(601);
        when(storeService.getVillageMappingsByFacility(FACILITY_ID))
                .thenReturn(new ArrayList<>(List.of(mapping)));

        assertSuccessContaining(controller.getVillageMappingsByFacility("{\"facilityID\":501}"), "601");
    }

    @Test
    @DisplayName("getVillageMappingsByFacility should answer an error envelope when the lookup fails")
    void getVillageMappingsByFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getVillageMappingsByFacility(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getVillageMappingsByFacility("{\"facilityID\":501}"));
    }

    @Test
    @DisplayName("getChildFacilitiesByParent should answer the facilities under the parent")
    void getChildFacilitiesByParent_shouldAnswerChildren() {
        when(storeService.getChildFacilitiesByParent(FACILITY_ID))
                .thenReturn(new ArrayList<>(List.of(facility(502, "Sub Centre"))));

        assertSuccessContaining(controller.getChildFacilitiesByParent("{\"facilityID\":501}"), "Sub Centre");
    }

    @Test
    @DisplayName("getChildFacilitiesByParent should answer an error envelope when the lookup fails")
    void getChildFacilitiesByParent_shouldAnswerErrorEnvelopeOnFailure() {
        when(storeService.getChildFacilitiesByParent(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getChildFacilitiesByParent("{\"facilityID\":501}"));
    }

    @Test
    @DisplayName("updateFacilityWithHierarchy should answer the facility the service saved")
    void updateFacilityWithHierarchy_shouldAnswerSavedFacility() {
        when(storeService.updateFacilityWithHierarchy(any(), any(), any(), any()))
                .thenReturn(facility(FACILITY_ID, "PHC North"));

        assertSuccessContaining(controller.updateFacilityWithHierarchy(
                "{\"facility\":{\"facilityID\":501,\"facilityName\":\"PHC North\"}}"), "PHC North");
    }

    @Test
    @DisplayName("updateFacilityWithHierarchy should report the reason the service refused the edit")
    void updateFacilityWithHierarchy_shouldReportRefusal() {
        when(storeService.updateFacilityWithHierarchy(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Facility not found"));

        assertTrue(controller.updateFacilityWithHierarchy("{\"facility\":{\"facilityID\":501}}")
                .contains("Facility not found"));
    }

    @Test
    @DisplayName("deleteFacilityWithHierarchy should answer the facility the service retired")
    void deleteFacilityWithHierarchy_shouldAnswerRetiredFacility() throws Exception {
        when(storeService.deleteFacilityWithHierarchy(FACILITY_ID, "admin"))
                .thenReturn(facility(FACILITY_ID, "PHC North"));

        assertSuccessContaining(
                controller.deleteFacilityWithHierarchy("{\"facilityID\":501,\"modifiedBy\":\"admin\"}"),
                "PHC North");
    }

    @Test
    @DisplayName("deleteFacilityWithHierarchy should report the reason the service refused the retirement")
    void deleteFacilityWithHierarchy_shouldReportRefusal() throws Exception {
        when(storeService.deleteFacilityWithHierarchy(anyInt(), anyString()))
                .thenThrow(new Exception("Facility not found"));

        assertTrue(controller.deleteFacilityWithHierarchy("{\"facilityID\":501,\"modifiedBy\":\"admin\"}")
                .contains("Facility not found"));
    }
}
