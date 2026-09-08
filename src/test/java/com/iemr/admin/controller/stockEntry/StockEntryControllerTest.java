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
package com.iemr.admin.controller.stockEntry;

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
import org.springframework.dao.DataIntegrityViolationException;

import com.iemr.admin.data.stockExit.ItemStockExit;
import com.iemr.admin.data.stockentry.ItemStockEntry;
import com.iemr.admin.data.stockentry.PhysicalStockEntry;
import com.iemr.admin.service.stockEntry.StockEntryService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The stock entry screen records what arrives at a store and works out which
 * batches an issue should draw on.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StockEntryController Test Suite")
class StockEntryControllerTest {

    private static final Integer FACILITY_ID = 9001;

    @Mock
    private StockEntryService stockEntryService;

    @InjectMocks
    private StockEntryController controller;

    private static PhysicalStockEntry arrival() {
        PhysicalStockEntry arrival = new PhysicalStockEntry();
        arrival.setPhyEntryID(7001);
        arrival.setRefNo("GRN-101");
        return arrival;
    }

    private static ItemStockEntry batch() {
        ItemStockEntry batch = new ItemStockEntry();
        batch.setItemStockEntryID(1);
        batch.setItemID(501);
        batch.setFacilityID(FACILITY_ID);
        batch.setBatchNo("B-77");
        return batch;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String expected) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(expected), response);
    }

    @Test
    @DisplayName("physicalStockEntry should answer the arrival it recorded")
    void physicalStockEntry_shouldAnswerRecordedArrival() {
        when(stockEntryService.savePhysicalStockEntry(any(PhysicalStockEntry.class))).thenReturn(arrival());

        assertSuccessContaining(controller.physicalStockEntry(arrival()), "GRN-101");
    }

    @Test
    @DisplayName("physicalStockEntry should report the failure when a batch clashes with one on file")
    void physicalStockEntry_shouldReportClashingBatch() {
        when(stockEntryService.savePhysicalStockEntry(any(PhysicalStockEntry.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate batch number"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.physicalStockEntry(arrival())));
    }

    @Test
    @DisplayName("getItemBatchForStoreID should answer the batches the store still holds")
    void getItemBatch_shouldAnswerHeldBatches() {
        when(stockEntryService.getItemBatchForStoreID(any(ItemStockEntry.class))).thenReturn(List.of(batch()));

        assertSuccessContaining(controller.getItemBatchForStoreID(batch()), "B-77");
    }

    @Test
    @DisplayName("getItemBatchForStoreID should report the failure when the batches cannot be answered")
    void getItemBatch_shouldReportLookupFailure() {
        when(stockEntryService.getItemBatchForStoreID(any(ItemStockEntry.class)))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getItemBatchForStoreID(batch())));
    }

    @Test
    @DisplayName("allocateStockFromItemID should answer the batches the issue should draw on")
    void allocate_shouldAnswerAllocatedBatches() {
        when(stockEntryService.getItemStockFromItemID(anyInt(), anyList())).thenReturn(List.of(batch()));

        assertSuccessContaining(
                controller.allocateStockFromItemID(FACILITY_ID, new ArrayList<ItemStockExit>()), "B-77");
    }

    @Test
    @DisplayName("allocateStockFromItemID should report the failure when the allocation cannot be worked out")
    void allocate_shouldReportAllocationFailure() {
        when(stockEntryService.getItemStockFromItemID(anyInt(), anyList()))
                .thenThrow(new RuntimeException("item is not on the item master"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.allocateStockFromItemID(FACILITY_ID, new ArrayList<ItemStockExit>())));
    }
}
