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
package com.iemr.admin.service.stockEntry;

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
import org.springframework.dao.DataIntegrityViolationException;

import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.items.M_ItemCategory;
import com.iemr.admin.data.stockentry.ItemStockEntry;
import com.iemr.admin.data.stockentry.PhysicalStockEntry;
import com.iemr.admin.data.stockExit.ItemStockExit;
import com.iemr.admin.repo.stockEntry.ItemStockEntryRepo;
import com.iemr.admin.repo.stockEntry.PhysicalStockEntryRepo;
import com.iemr.admin.service.item.ItemService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The stock entry service records what arrives at a store, and works out which
 * batches an issue should draw on given the item category's issue rule.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StockEntryServiceImpl Test Suite")
class StockEntryServiceImplTest {

    private static final Integer FACILITY_ID = 9001;
    private static final Integer ITEM_ID = 501;
    private static final Integer ENTRY_ID = 7001;

    @Mock
    private PhysicalStockEntryRepo physicalStockEntryRepo;

    @Mock
    private ItemStockEntryRepo itemStockEntryRepo;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private StockEntryServiceImpl service;

    private static ItemStockEntry batch(Integer stockEntryID, Integer quantityInHand) {
        ItemStockEntry batch = new ItemStockEntry();
        batch.setItemStockEntryID(stockEntryID);
        batch.setItemID(ITEM_ID);
        batch.setFacilityID(FACILITY_ID);
        batch.setQuantity(quantityInHand);
        batch.setQuantityInHand(quantityInHand);
        return batch;
    }

    private static PhysicalStockEntry arrival() {
        PhysicalStockEntry arrival = new PhysicalStockEntry();
        arrival.setPhyEntryID(ENTRY_ID);
        arrival.setRefNo("GRN-101");
        arrival.setItemStockEntry(new ArrayList<>(List.of(batch(null, 100))));
        return arrival;
    }

    private static ItemMaster itemIssuedBy(String issueType) {
        M_ItemCategory category = new M_ItemCategory();
        category.setIssueType(issueType);
        ItemMaster item = new ItemMaster();
        item.setItemCategory(category);
        return item;
    }

    private static ItemStockExit demandFor(int quantity) {
        ItemStockExit demand = new ItemStockExit();
        demand.setItemID(ITEM_ID);
        demand.setQuantity(quantity);
        return demand;
    }

    @Test
    @DisplayName("savePhysicalStockEntry should tie each batch to the arrival and put its quantity in hand")
    void save_shouldTieBatchesToArrival() {
        PhysicalStockEntry arrival = arrival();
        when(physicalStockEntryRepo.save(arrival)).thenReturn(arrival);
        when(itemStockEntryRepo.saveAll(anyList())).thenAnswer(call -> new ArrayList<>(call.getArgument(0)));

        PhysicalStockEntry stored = service.savePhysicalStockEntry(arrival);

        assertEquals(1, stored.getItemStockEntry().size());
        ItemStockEntry batch = stored.getItemStockEntry().get(0);
        assertEquals(ENTRY_ID, batch.getEntryTypeID());
        assertEquals("physicalStockEntry", batch.getEntryType());
        assertEquals(100, batch.getQuantityInHand());
    }

    @Test
    @DisplayName("savePhysicalStockEntry should abandon the whole arrival when a batch clashes with one on file")
    void save_shouldAbandonArrivalOnClashingBatch() {
        PhysicalStockEntry arrival = arrival();
        when(physicalStockEntryRepo.save(arrival)).thenReturn(arrival);
        when(itemStockEntryRepo.saveAll(anyList()))
                .thenThrow(new DataIntegrityViolationException("duplicate batch number"));

        assertThrows(DataIntegrityViolationException.class, () -> service.savePhysicalStockEntry(arrival));
        verify(physicalStockEntryRepo).updateDelete(ENTRY_ID, true);
        assertTrue(arrival.getDeleted());
    }

    @Test
    @DisplayName("savePhysicalStockEntry should keep the arrival when the batches fail for some other reason")
    void save_shouldKeepArrivalOnOtherFailure() {
        PhysicalStockEntry arrival = arrival();
        when(physicalStockEntryRepo.save(arrival)).thenReturn(arrival);
        when(itemStockEntryRepo.saveAll(anyList())).thenThrow(new RuntimeException("connection reset"));

        assertSame(arrival, service.savePhysicalStockEntry(arrival));
        verify(physicalStockEntryRepo, org.mockito.Mockito.never()).updateDelete(anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("getItemBatchForStoreID should answer only the batches the store still holds")
    void getItemBatch_shouldAnswerBatchesStillHeld() {
        List<ItemStockEntry> held = List.of(batch(1, 10));
        when(itemStockEntryRepo.findByFacilityIDAndItemIDAndQuantityInHandGreaterThanAndDeleted(FACILITY_ID, ITEM_ID,
                0, false)).thenReturn(held);

        ItemStockEntry request = new ItemStockEntry();
        request.setFacilityID(FACILITY_ID);
        request.setItemID(ITEM_ID);

        assertSame(held, service.getItemBatchForStoreID(request));
    }

    @Test
    @DisplayName("getAllItemBatchForStoreID should answer the running totals the query worked out")
    void getAllItemBatch_shouldAnswerRunningTotals() {
        ArrayList<Object[]> totals = new ArrayList<>(List.<Object[]>of(new Object[] { ITEM_ID, 100 }));
        when(itemStockEntryRepo.getQuantityOfStock(new Integer[] { 1 }, FACILITY_ID)).thenReturn(totals);

        assertEquals(1, service.getAllItemBatchForStoreID(FACILITY_ID, new Integer[] { 1 }).size());
    }

    @Test
    @DisplayName("updateStocks should draw each issued quantity off the batch it came from")
    void updateStocks_shouldDrawIssuedQuantityOffBatch() {
        ItemStockExit issued = demandFor(30);
        issued.setItemStockEntryID(1);
        issued.setQuantityInHand(100);
        when(itemStockEntryRepo.updateStock(1, 70)).thenReturn(1);

        assertEquals(1, service.updateStocks(List.of(issued)));
        verify(itemStockEntryRepo).updateStock(1, 70);
    }

    @Test
    @DisplayName("updateStocks should count nothing when there is nothing to draw off")
    void updateStocks_shouldCountNothingForEmptyIssue() {
        assertEquals(0, service.updateStocks(new ArrayList<>()));
    }

    @Test
    @DisplayName("the ordered batch lookups should each reach their own repository query")
    void orderedLookups_shouldReachTheirOwnQuery() {
        List<ItemStockEntry> byEntryAsc = List.of(batch(1, 10));
        List<ItemStockEntry> byEntryDesc = List.of(batch(2, 10));
        List<ItemStockEntry> byExpiry = List.of(batch(3, 10));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByAsc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(byEntryAsc);
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByDesc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(byEntryDesc);
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByExpiryDateDesc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(byExpiry);

        assertSame(byEntryAsc, service.getItemStockForStoreIDOrderByEntryDateAsc(FACILITY_ID, ITEM_ID));
        assertSame(byEntryDesc, service.getItemStockForStoreIDOrderByEntryDateDesc(FACILITY_ID, ITEM_ID));
        assertSame(byExpiry, service.getItemStockForStoreIDOrderByExpiryDate(FACILITY_ID, ITEM_ID));
    }

    @Test
    @DisplayName("getItemStockFromItemID should draw on the batch expiring first when the category says so")
    void allocate_shouldDrawOnFirstExpiringBatch() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(itemIssuedBy("First Expiry First Out"));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByExpiryDateDesc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(List.of(batch(1, 100)));

        List<ItemStockEntry> allocated = service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(30)));

        assertEquals(1, allocated.size());
        assertEquals(30, allocated.get(0).getQuantity(), "only what the issue asks for is drawn from the batch");
    }

    @Test
    @DisplayName("getItemStockFromItemID should draw on the newest batch when the category issues last in first out")
    void allocate_shouldDrawOnNewestBatch() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(itemIssuedBy("Last in First Out"));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByAsc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(List.of(batch(1, 100)));

        assertEquals(1, service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(30))).size());
    }

    @Test
    @DisplayName("getItemStockFromItemID should draw on the oldest batch when the category issues first in first out")
    void allocate_shouldDrawOnOldestBatch() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(itemIssuedBy("First in First Out"));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByDesc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(List.of(batch(1, 100)));

        assertEquals(1, service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(30))).size());
    }

    @Test
    @DisplayName("getItemStockFromItemID should fall back to entry order for a category with no issue rule of its own")
    void allocate_shouldFallBackToEntryOrder() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(itemIssuedBy("Anything else"));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByAsc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(List.of(batch(1, 100)));

        assertEquals(1, service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(30))).size());
    }

    @Test
    @DisplayName("getItemStockFromItemID should spread a large issue across as many batches as it needs")
    void allocate_shouldSpreadIssueAcrossBatches() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(itemIssuedBy("First in First Out"));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByDesc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(List.of(batch(1, 20), batch(2, 50), batch(3, 40)));

        List<ItemStockEntry> allocated = service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(60)));

        assertEquals(2, allocated.size(), "the third batch is left alone once the issue is covered");
        assertEquals(20, allocated.get(0).getQuantity());
        assertEquals(40, allocated.get(1).getQuantity());
    }

    @Test
    @DisplayName("getItemStockFromItemID should record a shortage when the store cannot cover the issue")
    void allocate_shouldRecordShortage() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(itemIssuedBy("First in First Out"));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByDesc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(List.of(batch(1, 20)));

        List<ItemStockEntry> allocated = service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(50)));

        assertEquals(2, allocated.size());
        ItemStockEntry shortage = allocated.get(1);
        assertEquals(30, shortage.getQuantity(), "the shortfall is carried as a batch of its own");
        assertEquals(ITEM_ID, shortage.getItemID());
        assertEquals(FACILITY_ID, shortage.getFacilityID());
    }

    @Test
    @DisplayName("getItemStockFromItemID should record the whole issue as a shortage when the store holds none")
    void allocate_shouldRecordWholeIssueAsShortage() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(itemIssuedBy("First in First Out"));
        when(itemStockEntryRepo
                .findByFacilityIDAndItemIDAndDeletedAndQuantityInHandGreaterThanOrderByCreatedByDesc(FACILITY_ID,
                        ITEM_ID, false, 0)).thenReturn(new ArrayList<>());

        List<ItemStockEntry> allocated = service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(50)));

        assertEquals(1, allocated.size());
        assertEquals(50, allocated.get(0).getQuantity());
    }

    @Test
    @DisplayName("getItemStockFromItemID should give up when the item is not on the item master")
    void allocate_shouldGiveUpForUnknownItem() {
        when(itemService.getItemMasterCatByID(ITEM_ID)).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> service.getItemStockFromItemID(FACILITY_ID, List.of(demandFor(50))));
    }
}
