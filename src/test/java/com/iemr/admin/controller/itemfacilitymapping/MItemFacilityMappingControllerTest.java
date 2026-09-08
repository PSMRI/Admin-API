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
package com.iemr.admin.controller.itemfacilitymapping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.items.ItemInStore;
import com.iemr.admin.data.itemfacilitymapping.M_itemfacilitymapping;
import com.iemr.admin.data.itemfacilitymapping.V_fetchItemFacilityMap;
import com.iemr.admin.service.itemfacilitymapping.M_itemfacilitymappingInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The item-facility endpoints decide which items each store may issue, so an
 * unmapped item is one the store cannot dispense.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MItemFacilityMappingController Test Suite")
class MItemFacilityMappingControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer FACILITY_ID = 501;

    @Mock
    private M_itemfacilitymappingInter M_itemfacilitymappingInter;

    @InjectMocks
    private MItemFacilityMappingController controller;

    private static M_itemfacilitymapping mapping(Integer id, Integer itemId) {
        M_itemfacilitymapping mapping = new M_itemfacilitymapping();
        mapping.setItemStoreMapID(id);
        mapping.setItemID(itemId);
        mapping.setFacilityID(FACILITY_ID);
        return mapping;
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
    @DisplayName("mapItemtoStrore should create one mapping per item the request lists")
    void mapItemtoStrore_shouldCreateOneMappingPerItem() {
        when(M_itemfacilitymappingInter.mapItemtoStore(anyList()))
                .thenReturn(new ArrayList<>(List.of(mapping(9001, 101))));

        String response = controller.mapItemtoStrore("[{\"facilityID\":501,\"mappingType\":\"Store\","
                + "\"providerServiceMapID\":4001,\"status\":\"Active\",\"createdBy\":\"admin\","
                + "\"itemID1\":[101,102]}]");

        assertSuccessContaining(response, "9001");

        ArgumentCaptor<List<M_itemfacilitymapping>> captor = ArgumentCaptor.forClass(List.class);
        verify(M_itemfacilitymappingInter).mapItemtoStore(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(FACILITY_ID, captor.getValue().get(0).getFacilityID());
    }

    @Test
    @DisplayName("mapItemtoStrore should answer an error envelope when no items are listed")
    void mapItemtoStrore_shouldAnswerErrorEnvelopeWithoutItems() {
        assertCodeException(controller.mapItemtoStrore("[{\"facilityID\":501}]"));
    }

    @Test
    @DisplayName("editItemtoStrore should copy the edits onto the stored mapping")
    void editItemtoStrore_shouldCopyEdits() {
        M_itemfacilitymapping stored = mapping(9001, 101);
        when(M_itemfacilitymappingInter.editdata(9001)).thenReturn(stored);
        when(M_itemfacilitymappingInter.saveEditedItem(stored)).thenReturn(stored);

        String response = controller.editItemtoStrore("{\"itemFacilityMapID\":9001,\"facilityID\":502,"
                + "\"itemID\":102,\"mappingType\":\"Sub Store\",\"providerServiceMapID\":4001,"
                + "\"status\":\"Inactive\"}");

        assertSuccessContaining(response, "9001");
        assertEquals(502, stored.getFacilityID());
        assertEquals("Sub Store", stored.getMappingType());
        assertEquals("Inactive", stored.getStatus());
    }

    @Test
    @DisplayName("editItemtoStrore should answer an error envelope for a mapping that does not exist")
    void editItemtoStrore_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(M_itemfacilitymappingInter.editdata(9001)).thenReturn(null);

        assertCodeException(controller.editItemtoStrore("{\"itemFacilityMapID\":9001}"));
    }

    @Test
    @DisplayName("deleteItemtoStrore should mark the mapping deleted")
    void deleteItemtoStrore_shouldMarkMappingDeleted() {
        M_itemfacilitymapping stored = mapping(9001, 101);
        when(M_itemfacilitymappingInter.editdata(9001)).thenReturn(stored);
        when(M_itemfacilitymappingInter.saveEditedItem(stored)).thenReturn(stored);

        assertSuccessContaining(
                controller.deleteItemtoStrore("{\"itemFacilityMapID\":9001,\"deleted\":true}"), "9001");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteItemtoStrore should answer an error envelope for a mapping that does not exist")
    void deleteItemtoStrore_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(M_itemfacilitymappingInter.editdata(9001)).thenReturn(null);

        assertCodeException(controller.deleteItemtoStrore("{\"itemFacilityMapID\":9001,\"deleted\":true}"));
    }

    @Test
    @DisplayName("getSubStroreitem should answer the items mapped to the sub store")
    void getSubStroreitem_shouldAnswerMappedItems() {
        when(M_itemfacilitymappingInter.getsubitemforsubStote(PSM_ID, FACILITY_ID))
                .thenReturn(new ArrayList<>(List.of(mapping(9001, 101))));

        assertSuccessContaining(
                controller.getSubStroreitem("{\"providerServiceMapID\":4001,\"facilityID\":501}"), "9001");
    }

    @Test
    @DisplayName("getSubStroreitem should answer an error envelope when the lookup fails")
    void getSubStroreitem_shouldAnswerErrorEnvelopeOnFailure() {
        when(M_itemfacilitymappingInter.getsubitemforsubStote(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getSubStroreitem("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getAllFacilityMappedData should answer the mappings of the provider")
    void getAllFacilityMappedData_shouldAnswerProviderMappings() {
        V_fetchItemFacilityMap view = new V_fetchItemFacilityMap();
        view.setItemFacilityMapID(9001);
        when(M_itemfacilitymappingInter.getAllFacilityMappedData(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(view)));

        assertSuccessContaining(
                controller.getAllFacilityMappedData("{\"providerServiceMapID\":4001}"), "9001");
    }

    @Test
    @DisplayName("getAllFacilityMappedData should answer an error envelope when the lookup fails")
    void getAllFacilityMappedData_shouldAnswerErrorEnvelopeOnFailure() {
        when(M_itemfacilitymappingInter.getAllFacilityMappedData(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllFacilityMappedData("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getItemMappingsByFacility should answer the mappings of the facility")
    void getItemMappingsByFacility_shouldAnswerFacilityMappings() {
        V_fetchItemFacilityMap view = new V_fetchItemFacilityMap();
        view.setItemFacilityMapID(9001);
        when(M_itemfacilitymappingInter.getItemMappingsByFacilityID(FACILITY_ID))
                .thenReturn(new ArrayList<>(List.of(view)));

        assertSuccessContaining(controller.getItemMappingsByFacility("{\"facilityID\":501}"), "9001");
    }

    @Test
    @DisplayName("getItemMappingsByFacility should answer an error envelope when the lookup fails")
    void getItemMappingsByFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(M_itemfacilitymappingInter.getItemMappingsByFacilityID(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getItemMappingsByFacility("{\"facilityID\":501}"));
    }

    @Test
    @DisplayName("getItemFromStoreID should answer the items the store holds")
    void getItemFromStoreID_shouldAnswerStoreItems() {
        when(M_itemfacilitymappingInter.getItemMastersFromStoreID(FACILITY_ID))
                .thenReturn(List.of(new ItemInStore(FACILITY_ID, 101, "Paracetamol", 25L)));

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(controller.getItemFromStoreID(FACILITY_ID)));
    }

    @Test
    @DisplayName("getItemFromStoreID should answer an error envelope when the lookup fails")
    void getItemFromStoreID_shouldAnswerErrorEnvelopeOnFailure() {
        when(M_itemfacilitymappingInter.getItemMastersFromStoreID(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getItemFromStoreID(FACILITY_ID));
    }

    @Test
    @DisplayName("deleteItemStoreMapping should answer how many mappings the service released")
    void deleteItemStoreMapping_shouldAnswerReleasedCount() {
        M_itemfacilitymapping request = mapping(9001, 101);
        when(M_itemfacilitymappingInter.deleteItemStoreMapping(request)).thenReturn(1);

        assertSuccessContaining(controller.deleteItemStoreMapping(request), "1");
    }

    @Test
    @DisplayName("deleteItemStoreMapping should answer an error envelope when the release fails")
    void deleteItemStoreMapping_shouldAnswerErrorEnvelopeOnFailure() {
        when(M_itemfacilitymappingInter.deleteItemStoreMapping(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.deleteItemStoreMapping(mapping(9001, 101)));
    }
}
