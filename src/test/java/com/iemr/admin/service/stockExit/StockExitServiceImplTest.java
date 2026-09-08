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
package com.iemr.admin.service.stockExit;

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

import com.iemr.admin.data.stockExit.ItemStockExit;
import com.iemr.admin.data.stockExit.T_PatientIssue;
import com.iemr.admin.repo.stockExit.ItemStockExitRepo;
import com.iemr.admin.repo.stockExit.PatientIssueRepo;
import com.iemr.admin.service.item.ItemService;
import com.iemr.admin.service.stockEntry.StockEntryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The stock exit service issues drugs to a patient, but only once it has
 * checked every line against what the store actually holds.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StockExitServiceImpl Test Suite")
class StockExitServiceImplTest {

    private static final Integer FACILITY_ID = 9001;
    private static final Integer ISSUE_ID = 5501;
    private static final Integer STOCK_ENTRY_ID = 1;
    private static final Integer ITEM_ID = 501;

    @Mock
    private StockEntryService stockEntryService;

    @Mock
    private ItemStockExitRepo itemStockExitRepo;

    @Mock
    private PatientIssueRepo patientIssueRepo;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private StockExitServiceImpl service;

    private static ItemStockExit issueLine(int quantity) {
        ItemStockExit line = new ItemStockExit();
        line.setItemStockEntryID(STOCK_ENTRY_ID);
        line.setItemID(ITEM_ID);
        line.setQuantity(quantity);
        return line;
    }

    private static T_PatientIssue patientIssue(String issueType, ItemStockExit... lines) {
        T_PatientIssue issue = new T_PatientIssue();
        issue.setPatientIssueID(ISSUE_ID);
        issue.setFacilityID(FACILITY_ID);
        issue.setIssueType(issueType);
        issue.setCreatedBy("pharmacist");
        issue.setItemStockExit(new ArrayList<>(List.of(lines)));
        return issue;
    }

    /** The stock query answers the store, item, name and quantity in hand. */
    private static Object[] stockInHandRow(int quantityInHand) {
        return new Object[] { FACILITY_ID, STOCK_ENTRY_ID, "Paracetamol 500", quantityInHand };
    }

    @Test
    @DisplayName("issuePatientDrugs should record the issue when the store can cover every line")
    void issue_shouldRecordIssueWhenStoreCovers() {
        T_PatientIssue issue = patientIssue("Manual", issueLine(10));
        when(stockEntryService.getAllItemBatchForStoreID(any(), any()))
                .thenReturn(List.<Object[]>of(stockInHandRow(100)));
        when(patientIssueRepo.save(issue)).thenReturn(issue);

        assertEquals(1, service.issuePatientDrugs(issue));
        verify(itemStockExitRepo).saveAll(anyList());
        verify(stockEntryService).updateStocks(anyList());
    }

    @Test
    @DisplayName("issuePatientDrugs should refuse the whole issue when the store cannot cover a line")
    void issue_shouldRefuseWholeIssueOnShortage() {
        T_PatientIssue issue = patientIssue("Manual", issueLine(500));
        when(stockEntryService.getAllItemBatchForStoreID(any(), any()))
                .thenReturn(List.<Object[]>of(stockInHandRow(100)));

        assertEquals(0, service.issuePatientDrugs(issue));
        verify(patientIssueRepo, never()).save(any(T_PatientIssue.class));
        verify(itemStockExitRepo, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("issuePatientDrugs should leave an issue it does not raise itself alone")
    void issue_shouldLeaveOtherIssueTypesAlone() {
        assertEquals(0, service.issuePatientDrugs(patientIssue("Prescription", issueLine(10))));
        verify(stockEntryService, never()).getAllItemBatchForStoreID(anyInt(), any());
    }

    @Test
    @DisplayName("saveItemExit should stamp each line with the issue it was raised against")
    void saveItemExit_shouldStampLinesWithIssue() {
        List<ItemStockExit> lines = new ArrayList<>(List.of(issueLine(10)));

        assertEquals(1, service.saveItemExit(lines, ISSUE_ID, "PatientIssue"));

        ArgumentCaptor<List<ItemStockExit>> captor = ArgumentCaptor.forClass(List.class);
        verify(itemStockExitRepo).saveAll(captor.capture());
        assertEquals("PatientIssue", captor.getValue().get(0).getExitType());
        assertEquals(ISSUE_ID, captor.getValue().get(0).getExitTypeID());
        verify(stockEntryService).updateStocks(lines);
    }

    @Test
    @DisplayName("getItemStockAndValidate should keep a line the store can cover and record who raised it")
    void validate_shouldKeepCoveredLine() {
        when(stockEntryService.getAllItemBatchForStoreID(any(), any()))
                .thenReturn(List.<Object[]>of(stockInHandRow(100)));

        List<ItemStockExit> kept = service.getItemStockAndValidate(List.of(issueLine(10)), FACILITY_ID,
                "pharmacist");

        assertEquals(1, kept.size());
        assertEquals(100, kept.get(0).getQuantityInHand());
        assertEquals("pharmacist", kept.get(0).getCreatedBy());
    }

    @Test
    @DisplayName("getItemStockAndValidate should drop a line the store cannot cover")
    void validate_shouldDropUncoveredLine() {
        when(stockEntryService.getAllItemBatchForStoreID(any(), any()))
                .thenReturn(List.<Object[]>of(stockInHandRow(5)));

        assertTrue(service.getItemStockAndValidate(List.of(issueLine(10)), FACILITY_ID, "pharmacist").isEmpty());
    }

    @Test
    @DisplayName("getItemStockAndValidate should keep a line the store covers exactly")
    void validate_shouldKeepExactlyCoveredLine() {
        when(stockEntryService.getAllItemBatchForStoreID(any(), any()))
                .thenReturn(List.<Object[]>of(stockInHandRow(10)));

        assertEquals(1, service.getItemStockAndValidate(List.of(issueLine(10)), FACILITY_ID, "pharmacist").size());
    }

    @Test
    @DisplayName("getItemStockAndValidate should keep nothing when the store holds none of the batches asked for")
    void validate_shouldKeepNothingWhenStoreHoldsNoBatch() {
        when(stockEntryService.getAllItemBatchForStoreID(any(), any())).thenReturn(new ArrayList<>());

        assertTrue(service.getItemStockAndValidate(List.of(issueLine(10)), FACILITY_ID, "pharmacist").isEmpty());
    }
}
