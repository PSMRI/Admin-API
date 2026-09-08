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
package com.iemr.admin.service.itemfacilitymapping;

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

import com.iemr.admin.data.itemfacilitymapping.M_itemfacilitymapping;
import com.iemr.admin.data.itemfacilitymapping.V_fetchItemFacilityMap;
import com.iemr.admin.data.items.ItemInStore;
import com.iemr.admin.repo.stockEntry.ItemStockEntryRepo;
import com.iemr.admin.repository.itemfacilitymapping.M_itemfacilitymappingRepo;
import com.iemr.admin.repository.itemfacilitymapping.V_fetchItemFacilityMapRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The item facility service records which items a store is allowed to hold, and
 * answers what each store currently has on its shelves.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("M_itemfacilitymappingImpl Test Suite")
class M_itemfacilitymappingImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer FACILITY_ID = 9001;
    private static final Integer MAP_ID = 3301;
    private static final Integer ITEM_ID = 501;

    @Mock
    private V_fetchItemFacilityMapRepo v_fetchItemFacilityMapRepo;

    @Mock
    private M_itemfacilitymappingRepo m_itemfacilitymappingRepo;

    @Mock
    private ItemStockEntryRepo itemStockEntryRepo;

    @InjectMocks
    private M_itemfacilitymappingImpl service;

    private static M_itemfacilitymapping mapping() {
        M_itemfacilitymapping mapping = new M_itemfacilitymapping();
        mapping.setItemStoreMapID(MAP_ID);
        mapping.setItemID(ITEM_ID);
        mapping.setFacilityID(FACILITY_ID);
        mapping.setDeleted(Boolean.FALSE);
        return mapping;
    }

    @Test
    @DisplayName("mapItemtoStore should answer the mappings the repository stored")
    void map_shouldAnswerStoredMappings() {
        ArrayList<M_itemfacilitymapping> stored = new ArrayList<>(List.of(mapping()));
        when(m_itemfacilitymappingRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.mapItemtoStore(new ArrayList<>()));
    }

    @Test
    @DisplayName("editdata and saveEditedItem should each reach their own repository query")
    void singleRecordOperations_shouldReachTheirOwnQuery() {
        M_itemfacilitymapping stored = mapping();
        when(m_itemfacilitymappingRepo.findByItemFacilityMapID(MAP_ID)).thenReturn(stored);
        when(m_itemfacilitymappingRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.editdata(MAP_ID));
        assertSame(stored, service.saveEditedItem(stored));
    }

    @Test
    @DisplayName("editdata should answer nothing when the mapping is unknown")
    void edit_shouldAnswerNothingForUnknownMapping() {
        when(m_itemfacilitymappingRepo.findByItemFacilityMapID(-1)).thenReturn(null);

        assertNull(service.editdata(-1));
    }

    @Test
    @DisplayName("getsubitemforsubStote should rebuild one item per row the query answers")
    void getSubItems_shouldRebuildEachRow() {
        when(m_itemfacilitymappingRepo.getItemforSubstore(PSM_ID, FACILITY_ID))
                .thenReturn(new ArrayList<>(List.<Object[]>of(
                        new Object[] { ITEM_ID, "Paracetamol 500", Boolean.FALSE, 7 })));

        ArrayList<M_itemfacilitymapping> items = service.getsubitemforsubStote(PSM_ID, FACILITY_ID);

        assertEquals(1, items.size());
        assertEquals("Paracetamol 500", items.get(0).getItemName());
        assertEquals(ITEM_ID, items.get(0).getItemID());
    }

    @Test
    @DisplayName("getsubitemforsubStote should skip a row the query could not fill in")
    void getSubItems_shouldSkipIncompleteRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { ITEM_ID, "Paracetamol 500" });
        when(m_itemfacilitymappingRepo.getItemforSubstore(PSM_ID, FACILITY_ID)).thenReturn(rows);

        assertTrue(service.getsubitemforsubStote(PSM_ID, FACILITY_ID).isEmpty());
    }

    @Test
    @DisplayName("the mapped item lookups should each reach their own repository query")
    void mappedItemLookups_shouldReachTheirOwnQuery() {
        ArrayList<V_fetchItemFacilityMap> byProvider = new ArrayList<>(List.of(new V_fetchItemFacilityMap()));
        ArrayList<V_fetchItemFacilityMap> byFacility = new ArrayList<>(List.of(new V_fetchItemFacilityMap()));
        when(v_fetchItemFacilityMapRepo.getAllFacilityMappedData(PSM_ID)).thenReturn(byProvider);
        when(v_fetchItemFacilityMapRepo.getItemMappingsByFacilityAndSubStores(FACILITY_ID)).thenReturn(byFacility);

        assertSame(byProvider, service.getAllFacilityMappedData(PSM_ID));
        assertSame(byFacility, service.getItemMappingsByFacilityID(FACILITY_ID));
    }

    @Test
    @DisplayName("getItemMastersFromStoreID should answer what the store holds of each item mapped to it")
    void getItemMasters_shouldAnswerWhatStoreHolds() {
        when(m_itemfacilitymappingRepo.getItemforStore(FACILITY_ID))
                .thenReturn(new ArrayList<>(List.<Object[]>of(new Object[] { ITEM_ID, "Paracetamol 500" })));
        when(itemStockEntryRepo.getQuantity(any(Integer[].class), anyInt()))
                .thenReturn(new ArrayList<>(List.<Object[]>of(
                        new Object[] { FACILITY_ID, ITEM_ID, "Paracetamol 500", 250L })));

        List<ItemInStore> held = service.getItemMastersFromStoreID(FACILITY_ID);

        assertEquals(1, held.size());
        assertEquals("Paracetamol 500", held.get(0).getItemName());
        assertEquals(250L, held.get(0).getQuantity());
    }

    @Test
    @DisplayName("getItemMastersFromStoreID should answer nothing when the store holds none of its items")
    void getItemMasters_shouldAnswerNothingWhenStoreEmpty() {
        when(m_itemfacilitymappingRepo.getItemforStore(FACILITY_ID)).thenReturn(new ArrayList<>());
        when(itemStockEntryRepo.getQuantity(any(Integer[].class), anyInt())).thenReturn(new ArrayList<>());

        assertTrue(service.getItemMastersFromStoreID(FACILITY_ID).isEmpty());
    }

    @Test
    @DisplayName("deleteItemStoreMapping should report how many mappings the retirement touched")
    void delete_shouldReportRowsTouched() {
        M_itemfacilitymapping request = mapping();
        request.setDeleted(Boolean.TRUE);
        when(m_itemfacilitymappingRepo.updateDeleteMap(MAP_ID, Boolean.TRUE)).thenReturn(1);

        assertEquals(1, service.deleteItemStoreMapping(request));
    }

    @Test
    @DisplayName("deleteItemStoreMapping should report nothing touched when the mapping is unknown")
    void delete_shouldReportNothingTouchedForUnknownMapping() {
        M_itemfacilitymapping request = new M_itemfacilitymapping();
        request.setItemStoreMapID(-1);
        when(m_itemfacilitymappingRepo.updateDeleteMap(-1, null)).thenReturn(0);

        assertEquals(0, service.deleteItemStoreMapping(request));
    }
}
