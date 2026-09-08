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
package com.iemr.admin.service.item;

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

import com.iemr.admin.data.items.CodeChecker;
import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.items.M_ItemCategory;
import com.iemr.admin.data.items.M_ItemForm;
import com.iemr.admin.data.items.M_Route;
import com.iemr.admin.repository.item.ItemCategoryRepo;
import com.iemr.admin.repository.item.ItemFormRepo;
import com.iemr.admin.repository.item.ItemRepo;
import com.iemr.admin.repository.item.RouteRepo;
import com.iemr.admin.repository.itemfacilitymapping.M_itemfacilitymappingRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * The item service maintains the inventory catalogue and the codes that keep
 * each entry unique within a provider.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ItemServiceImpl Test Suite")
class ItemServiceImplTest {

    private static final Integer PSM_ID = 4001;

    @Mock
    private ItemRepo itemRepo;

    @Mock
    private ItemCategoryRepo itemCategoryRepo;

    @Mock
    private RouteRepo routeRepo;

    @Mock
    private ItemFormRepo itemFormRepo;

    @Mock
    private M_itemfacilitymappingRepo itemfacilitymappingRepo;

    @InjectMocks
    private ItemServiceImpl service;

    private static M_ItemCategory category(Integer id) {
        M_ItemCategory category = new M_ItemCategory();
        category.setItemCategoryID(id);
        return category;
    }

    @Test
    @DisplayName("getItemCategory should read the whole catalogue when retired categories are wanted too")
    void getItemCategory_shouldReadWholeCatalogue() {
        List<M_ItemCategory> stored = List.of(category(31));
        when(itemCategoryRepo.findByProviderServiceMapIDOrderByItemCategoryName(PSM_ID)).thenReturn(stored);

        assertSame(stored, service.getItemCategory(true, PSM_ID));
    }

    @Test
    @DisplayName("getItemCategory should read only the live categories when retired ones are excluded")
    void getItemCategory_shouldReadLiveCategories() {
        List<M_ItemCategory> stored = List.of(category(31));
        when(itemCategoryRepo.findByDeletedAndProviderServiceMapIDOrderByItemCategoryName(false, PSM_ID))
                .thenReturn(stored);

        assertSame(stored, service.getItemCategory(false, PSM_ID));
    }

    @Test
    @DisplayName("getItemCategory should answer nothing when the caller names no provider")
    void getItemCategory_shouldAnswerNothingWithoutProvider() {
        assertTrue(service.getItemCategory(true, null).isEmpty());
        verify(itemCategoryRepo, never()).findByProviderServiceMapIDOrderByItemCategoryName(anyInt());
    }

    @Test
    @DisplayName("the item lookups should each reach their own repository query")
    void itemLookups_shouldReachTheirOwnQuery() {
        ItemMaster item = new ItemMaster();
        List<ItemMaster> items = List.of(item);
        when(itemRepo.save(item)).thenReturn(item);
        when(itemRepo.saveAll(anyList())).thenReturn(items);
        when(itemRepo.findByProviderServiceMapIDOrderByItemName(PSM_ID)).thenReturn(items);
        when(itemRepo.findByItemID(101)).thenReturn(item);
        when(itemRepo.findDetailOne(101)).thenReturn(item);
        when(itemRepo.getItemMasters(PSM_ID, 31)).thenReturn(items);
        when(itemRepo.deleteItemMaster(101, true)).thenReturn(1);
        when(itemRepo.discontinueItemMaster(101, true)).thenReturn(1);
        when(itemCategoryRepo.findByItemCategoryID(31)).thenReturn(category(31));
        when(routeRepo.getAll()).thenReturn(List.of(new M_Route()));
        when(itemFormRepo.getAll()).thenReturn(List.of(new M_ItemForm()));

        assertSame(item, service.createItemMaster(item));
        assertSame(items, service.addAllItemMaster(new ArrayList<>()));
        assertSame(items, service.getItemMaster(PSM_ID));
        assertSame(item, service.getItemMasterByID(101));
        assertSame(item, service.getItemMasterCatByID(101));
        assertSame(items, service.getItemMasters(PSM_ID, 31));
        assertEquals(1, service.blockItemMaster(101, true));
        assertEquals(1, service.discontinueItemMaster(101, true));
        assertEquals(31, service.getItemCategory(31).getItemCategoryID());
        assertEquals(1, service.getItemRouteProviderServiceMapID(PSM_ID).size());
        assertEquals(1, service.getItemFormProviderServiceMapID(PSM_ID).size());
    }

    @Test
    @DisplayName("updateItemIssueConfig should count only the categories that name an issue type")
    void updateItemIssueConfig_shouldCountOnlyComplete() {
        M_ItemCategory complete = category(31);
        complete.setIssueType("FIFO");
        M_ItemCategory incomplete = category(32);
        when(itemCategoryRepo.updateIssueConfig(31, "FIFO")).thenReturn(1);

        assertEquals(1, service.updateItemIssueConfig(List.of(complete, incomplete)));
        verify(itemCategoryRepo, never()).updateIssueConfig(32, null);
    }

    @Test
    @DisplayName("updateExpiryAlert should count only the categories that name an alert window")
    void updateExpiryAlert_shouldCountOnlyComplete() {
        M_ItemCategory complete = category(31);
        complete.setAlertBeforeDays(30);
        M_ItemCategory incomplete = category(32);
        when(itemCategoryRepo.updateExpiryAlert(31, 30)).thenReturn(1);

        assertEquals(1, service.updateExpiryAlert(List.of(complete, incomplete)));
    }

    @Test
    @DisplayName("createItemCategories should answer the id of the first category it stored")
    void createItemCategories_shouldAnswerFirstStoredId() {
        when(itemCategoryRepo.saveAll(anyList())).thenReturn(List.of(category(31)));

        assertEquals(31, service.createItemCategories(new ArrayList<>()));
    }

    @Test
    @DisplayName("createItemCategories should answer zero when nothing was stored")
    void createItemCategories_shouldAnswerZeroWhenNothingStored() {
        when(itemCategoryRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        assertEquals(0, service.createItemCategories(new ArrayList<>()));
    }

    @Test
    @DisplayName("createItemForms should answer the id of the first form it stored")
    void createItemForms_shouldAnswerFirstStoredId() {
        M_ItemForm form = new M_ItemForm();
        form.setItemFormID(11);
        when(itemFormRepo.saveAll(anyList())).thenReturn(List.of(form));

        assertEquals(11, service.createItemForms(new ArrayList<>()));
        when(itemFormRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        assertEquals(0, service.createItemForms(new ArrayList<>()));
    }

    @Test
    @DisplayName("createRoutes should answer the id of the first route it stored")
    void createRoutes_shouldAnswerFirstStoredId() {
        M_Route route = new M_Route();
        route.setRouteID(21);
        when(routeRepo.saveAll(anyList())).thenReturn(List.of(route));

        assertEquals(21, service.createRoutes(new ArrayList<>()));
        when(routeRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        assertEquals(0, service.createRoutes(new ArrayList<>()));
    }

    @Test
    @DisplayName("the edit and block calls should each reach their own repository query")
    void editAndBlockCalls_shouldReachTheirOwnQuery() {
        M_ItemCategory cat = category(31);
        cat.setItemCategoryDesc("Drugs");
        cat.setModifiedBy("admin");
        cat.setDeleted(Boolean.TRUE);
        M_ItemForm form = new M_ItemForm();
        form.setItemFormID(11);
        form.setItemFormDesc("Tablet");
        form.setModifiedBy("admin");
        form.setDeleted(Boolean.TRUE);
        M_Route route = new M_Route();
        route.setRouteID(21);
        route.setRouteDesc("Oral");
        route.setModifiedBy("admin");
        route.setDeleted(Boolean.TRUE);
        when(itemCategoryRepo.updateItemCategoryDetails(31, "Drugs", "admin")).thenReturn(1);
        when(itemCategoryRepo.blockItemCategory(31, Boolean.TRUE, "admin")).thenReturn(1);
        when(itemFormRepo.updateItemFormDetails(11, "Tablet", "admin")).thenReturn(1);
        when(itemFormRepo.blockItemForm(11, Boolean.TRUE, "admin")).thenReturn(1);
        when(routeRepo.updateRouteDetails(21, "Oral", "admin")).thenReturn(1);
        when(routeRepo.blockRoute(21, Boolean.TRUE, "admin")).thenReturn(1);

        assertEquals(1, service.editItemCategory(cat));
        assertEquals(1, service.blockItemCategory(cat));
        assertEquals(1, service.editItemForm(form));
        assertEquals(1, service.blockItemForm(form));
        assertEquals(1, service.editRoute(route));
        assertEquals(1, service.blockRoute(route));
    }

    @Test
    @DisplayName("the code checks should report a code the provider already uses")
    void codeChecks_shouldReportUsedCode() {
        CodeChecker checker = new CodeChecker();
        checker.setCode("CODE-1");
        checker.setProviderServiceMapID(PSM_ID);
        when(itemCategoryRepo.findByItemCategoryCodeAndProviderServiceMapID("CODE-1", PSM_ID))
                .thenReturn(List.of(category(31)));
        when(itemFormRepo.findByItemFormCodeAndProviderServiceMapID("CODE-1", PSM_ID))
                .thenReturn(List.of(new M_ItemForm()));
        when(itemRepo.findByItemCodeAndProviderServiceMapID("CODE-1", PSM_ID))
                .thenReturn(List.of(new ItemMaster()));
        when(routeRepo.findByRouteCodeAndProviderServiceMapID("CODE-1", PSM_ID))
                .thenReturn(List.of(new M_Route()));

        assertTrue(service.checkCodeCategory(checker));
        assertTrue(service.checkCodeForm(checker));
        assertTrue(service.checkCodeItem(checker));
        assertTrue(service.checkCodeRoute(checker));
    }

    @Test
    @DisplayName("the code checks should clear a code nobody uses yet")
    void codeChecks_shouldClearFreeCode() {
        CodeChecker checker = new CodeChecker();
        checker.setCode("CODE-2");
        checker.setProviderServiceMapID(PSM_ID);
        when(itemCategoryRepo.findByItemCategoryCodeAndProviderServiceMapID("CODE-2", PSM_ID))
                .thenReturn(new ArrayList<>());
        when(itemFormRepo.findByItemFormCodeAndProviderServiceMapID("CODE-2", PSM_ID))
                .thenReturn(new ArrayList<>());
        when(itemRepo.findByItemCodeAndProviderServiceMapID("CODE-2", PSM_ID)).thenReturn(new ArrayList<>());
        when(routeRepo.findByRouteCodeAndProviderServiceMapID("CODE-2", PSM_ID)).thenReturn(new ArrayList<>());

        assertFalse(service.checkCodeCategory(checker));
        assertFalse(service.checkCodeForm(checker));
        assertFalse(service.checkCodeItem(checker));
        assertFalse(service.checkCodeRoute(checker));
    }
}
