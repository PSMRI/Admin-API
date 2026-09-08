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
package com.iemr.admin.controller.item;

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

import com.iemr.admin.data.items.CodeChecker;
import com.iemr.admin.data.items.ItemMaster;
import com.iemr.admin.data.items.M_ItemCategory;
import com.iemr.admin.data.items.M_ItemForm;
import com.iemr.admin.data.items.M_Route;
import com.iemr.admin.service.item.ItemService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The item endpoints keep the inventory catalogue: the drugs and consumables a
 * facility can issue, and the categories, forms and routes that classify them.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ItemController Test Suite")
class ItemControllerTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer ITEM_ID = 101;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private static ItemMaster item(Integer id, String name) {
        ItemMaster item = new ItemMaster();
        item.setItemID(id);
        item.setItemName(name);
        return item;
    }

    private static M_ItemCategory category(Integer id, String name) {
        M_ItemCategory category = new M_ItemCategory();
        category.setItemCategoryID(id);
        category.setItemCategoryName(name);
        return category;
    }

    private static M_ItemForm form(Integer id, String name) {
        M_ItemForm form = new M_ItemForm();
        form.setItemFormID(id);
        form.setItemForm(name);
        return form;
    }

    private static M_Route route(Integer id, String name) {
        M_Route route = new M_Route();
        route.setRouteID(id);
        route.setRouteName(name);
        return route;
    }

    private static CodeChecker codeChecker(String name, String code) {
        CodeChecker checker = new CodeChecker();
        checker.setName(name);
        checker.setCode(code);
        checker.setProviderServiceMapID(PSM_ID);
        return checker;
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
    @DisplayName("getItemForm should answer the item forms configured for the provider")
    void getItemForm_shouldAnswerConfiguredForms() {
        when(itemService.getItemFormProviderServiceMapID(PSM_ID)).thenReturn(List.of(form(11, "Tablet")));

        assertSuccessContaining(controller.getItemForm(PSM_ID), "Tablet");
    }

    @Test
    @DisplayName("getItemForm should answer an error envelope when the lookup fails")
    void getItemForm_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.getItemFormProviderServiceMapID(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getItemForm(PSM_ID));
    }

    @Test
    @DisplayName("getItemRoute should answer the routes configured for the provider")
    void getItemRoute_shouldAnswerConfiguredRoutes() {
        when(itemService.getItemRouteProviderServiceMapID(PSM_ID)).thenReturn(List.of(route(21, "Oral")));

        assertSuccessContaining(controller.getItemRoute(PSM_ID), "Oral");
    }

    @Test
    @DisplayName("getItemRoute should answer an error envelope when the lookup fails")
    void getItemRoute_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.getItemRouteProviderServiceMapID(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getItemRoute(PSM_ID));
    }

    @Test
    @DisplayName("getItemCategory should ask for every category when the caller sends zero")
    void getItemCategory_shouldAskForEveryCategoryOnZero() {
        when(itemService.getItemCategory(true, PSM_ID)).thenReturn(List.of(category(31, "Drugs")));

        assertSuccessContaining(controller.getItemCategory(PSM_ID, 0), "Drugs");
        verify(itemService).getItemCategory(true, PSM_ID);
    }

    @Test
    @DisplayName("getItemCategory should ask for the live categories only for any other flag")
    void getItemCategory_shouldAskForLiveCategoriesOtherwise() {
        when(itemService.getItemCategory(false, PSM_ID)).thenReturn(List.of(category(31, "Drugs")));

        assertSuccessContaining(controller.getItemCategory(PSM_ID, 1), "Drugs");
        verify(itemService).getItemCategory(false, PSM_ID);
    }

    @Test
    @DisplayName("getItemCategory should answer an error envelope when the lookup fails")
    void getItemCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.getItemCategory(anyBoolean(), anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getItemCategory(PSM_ID, 0));
    }

    @Test
    @DisplayName("createItemMaster should answer the items the service stored")
    void createItemMaster_shouldAnswerStoredItems() {
        when(itemService.addAllItemMaster(anyList())).thenReturn(List.of(item(ITEM_ID, "Paracetamol")));

        assertSuccessContaining(controller.createItemMaster(new ItemMaster[] { item(null, "Paracetamol") }),
                "Paracetamol");
    }

    @Test
    @DisplayName("createItemMaster should answer an error envelope when the store fails")
    void createItemMaster_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.addAllItemMaster(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createItemMaster(new ItemMaster[] { item(null, "Paracetamol") }));
    }

    @Test
    @DisplayName("getItemMaster should answer the catalogue of the provider")
    void getItemMaster_shouldAnswerProviderCatalogue() {
        when(itemService.getItemMaster(PSM_ID)).thenReturn(List.of(item(ITEM_ID, "Paracetamol")));

        assertSuccessContaining(controller.getItemMaster(PSM_ID), "Paracetamol");
    }

    @Test
    @DisplayName("getItemMaster should answer an error envelope when the lookup fails")
    void getItemMaster_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.getItemMaster(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getItemMaster(PSM_ID));
    }

    @Test
    @DisplayName("blockItemMaster should answer how many items the service blocked")
    void blockItemMaster_shouldAnswerBlockedCount() {
        when(itemService.blockItemMaster(ITEM_ID, true)).thenReturn(1);

        assertSuccessContaining(controller.blockItemMaster(ITEM_ID, true), "1");
    }

    @Test
    @DisplayName("blockItemMaster should answer an error envelope when the block fails")
    void blockItemMaster_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.blockItemMaster(anyInt(), anyBoolean()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.blockItemMaster(ITEM_ID, true));
    }

    @Test
    @DisplayName("discontinueItemMaster should answer how many items the service discontinued")
    void discontinueItemMaster_shouldAnswerDiscontinuedCount() {
        when(itemService.discontinueItemMaster(ITEM_ID, true)).thenReturn(1);

        assertSuccessContaining(controller.discontinueItemMaster(ITEM_ID, true), "1");
    }

    @Test
    @DisplayName("discontinueItemMaster should answer an error envelope when the change fails")
    void discontinueItemMaster_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.discontinueItemMaster(anyInt(), anyBoolean()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.discontinueItemMaster(ITEM_ID, true));
    }

    @Test
    @DisplayName("editItemMaster should copy the edited fields onto the stored item")
    void editItemMaster_shouldCopyEditedFields() {
        ItemMaster stored = item(ITEM_ID, "Paracetamol");
        ItemMaster request = item(ITEM_ID, "Paracetamol");
        request.setIsMedical(Boolean.TRUE);
        request.setItemCategoryID(31);
        request.setPharmacologyCategoryID(41);
        request.setManufacturerID(51);
        request.setIsScheduledDrug(Boolean.FALSE);
        request.setItemDesc("Antipyretic");
        request.setSctCode("SCT-1");
        request.setSctTerm("Paracetamol 500mg");
        request.setModifiedBy("admin");
        when(itemService.getItemMasterByID(ITEM_ID)).thenReturn(stored);
        when(itemService.createItemMaster(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editItemMaster(request), "Paracetamol");
        assertEquals("Antipyretic", stored.getItemDesc());
        assertEquals("SCT-1", stored.getSctCode());
        assertEquals(31, stored.getItemCategoryID());
    }

    @Test
    @DisplayName("editItemMaster should answer an error envelope for an item that does not exist")
    void editItemMaster_shouldAnswerErrorEnvelopeForUnknownItem() {
        when(itemService.getItemMasterByID(ITEM_ID)).thenReturn(null);

        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(controller.editItemMaster(item(ITEM_ID, "x"))));
    }

    @Test
    @DisplayName("configItemIssue should answer how many categories the service reconfigured")
    void configItemIssue_shouldAnswerReconfiguredCount() {
        when(itemService.updateItemIssueConfig(anyList())).thenReturn(2);

        assertSuccessContaining(controller.configItemIssue(new M_ItemCategory[] { category(31, "Drugs") }), "2");
    }

    @Test
    @DisplayName("configItemIssue should answer an error envelope when the change fails")
    void configItemIssue_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.updateItemIssueConfig(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.configItemIssue(new M_ItemCategory[] { category(31, "Drugs") }));
    }

    @Test
    @DisplayName("configexpiryalert should answer how many categories had their alert window changed")
    void configexpiryalert_shouldAnswerChangedCount() {
        when(itemService.updateExpiryAlert(anyList())).thenReturn(1);

        assertSuccessContaining(controller.configexpiryalert(new M_ItemCategory[] { category(31, "Drugs") }), "1");
    }

    @Test
    @DisplayName("configexpiryalert should answer an error envelope when the change fails")
    void configexpiryalert_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.updateExpiryAlert(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.configexpiryalert(new M_ItemCategory[] { category(31, "Drugs") }));
    }

    @Test
    @DisplayName("getItem should answer the items in the category")
    void getItem_shouldAnswerItemsInCategory() {
        when(itemService.getItemMasters(PSM_ID, 31)).thenReturn(List.of(item(ITEM_ID, "Paracetamol")));

        assertSuccessContaining(
                controller.getItem("{\"providerServiceMapID\":4001,\"itemCategoryID\":31}"), "Paracetamol");
    }

    @Test
    @DisplayName("getItem should answer an error envelope when the lookup fails")
    void getItem_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.getItemMasters(any(), any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getItem("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("createItemCategories should report success only when the service stored something")
    void createItemCategories_shouldReportOutcome() {
        when(itemService.createItemCategories(anyList())).thenReturn(1);
        assertSuccessContaining(controller.createItemCategories(new M_ItemCategory[] { category(null, "Drugs") }),
                "Item Categories saved successfully");

        when(itemService.createItemCategories(anyList())).thenReturn(0);
        assertSuccessContaining(controller.createItemCategories(new M_ItemCategory[] { category(null, "Drugs") }),
                "Failed to store Item Categories");
    }

    @Test
    @DisplayName("createItemCategories should answer an error envelope when the store fails")
    void createItemCategories_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.createItemCategories(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createItemCategories(new M_ItemCategory[] { category(null, "Drugs") }));
    }

    @Test
    @DisplayName("editItemCategory should report success only when the service changed something")
    void editItemCategory_shouldReportOutcome() {
        when(itemService.editItemCategory(any())).thenReturn(1);
        assertSuccessContaining(controller.editItemCategory(category(31, "Drugs")),
                "Item Category updated successfully");

        when(itemService.editItemCategory(any())).thenReturn(0);
        assertSuccessContaining(controller.editItemCategory(category(31, "Drugs")),
                "Failed to update Item Category");
    }

    @Test
    @DisplayName("editItemCategory should answer an error envelope when the change fails")
    void editItemCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.editItemCategory(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.editItemCategory(category(31, "Drugs")));
    }

    @Test
    @DisplayName("blockItemCategory should report success only when the service blocked something")
    void blockItemCategory_shouldReportOutcome() {
        when(itemService.blockItemCategory(any())).thenReturn(1);
        assertSuccessContaining(controller.blockItemCategory(category(31, "Drugs")),
                "Item Category blocked successfully");

        when(itemService.blockItemCategory(any())).thenReturn(0);
        assertSuccessContaining(controller.blockItemCategory(category(31, "Drugs")),
                "Failed to block Item Category");
    }

    @Test
    @DisplayName("blockItemCategory should answer an error envelope when the block fails")
    void blockItemCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.blockItemCategory(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.blockItemCategory(category(31, "Drugs")));
    }

    @Test
    @DisplayName("createItemForms should report success only when the service stored something")
    void createItemForms_shouldReportOutcome() {
        when(itemService.createItemForms(anyList())).thenReturn(1);
        assertSuccessContaining(controller.createItemForms(new M_ItemForm[] { form(null, "Tablet") }),
                "Item Forms saved successfully");

        when(itemService.createItemForms(anyList())).thenReturn(0);
        assertSuccessContaining(controller.createItemForms(new M_ItemForm[] { form(null, "Tablet") }),
                "Failed to store Item Forms");
    }

    @Test
    @DisplayName("createItemForms should answer an error envelope when the store fails")
    void createItemForms_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.createItemForms(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createItemForms(new M_ItemForm[] { form(null, "Tablet") }));
    }

    @Test
    @DisplayName("editItemForm should report success only when the service changed something")
    void editItemForm_shouldReportOutcome() {
        when(itemService.editItemForm(any())).thenReturn(1);
        assertSuccessContaining(controller.editItemForm(form(11, "Tablet")), "Item Form updated successfully");

        when(itemService.editItemForm(any())).thenReturn(0);
        assertSuccessContaining(controller.editItemForm(form(11, "Tablet")), "Failed to update Item Form");
    }

    @Test
    @DisplayName("editItemForm should answer an error envelope when the change fails")
    void editItemForm_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.editItemForm(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.editItemForm(form(11, "Tablet")));
    }

    @Test
    @DisplayName("blockItemForm should report success only when the service blocked something")
    void blockItemForm_shouldReportOutcome() {
        when(itemService.blockItemForm(any())).thenReturn(1);
        assertSuccessContaining(controller.blockItemForm(form(11, "Tablet")), "Item Form blocked successfully");

        when(itemService.blockItemForm(any())).thenReturn(0);
        assertSuccessContaining(controller.blockItemForm(form(11, "Tablet")), "Failed to block Item Form");
    }

    @Test
    @DisplayName("blockItemForm should answer an error envelope when the block fails")
    void blockItemForm_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.blockItemForm(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.blockItemForm(form(11, "Tablet")));
    }

    @Test
    @DisplayName("createRoutes should report success only when the service stored something")
    void createRoutes_shouldReportOutcome() {
        when(itemService.createRoutes(anyList())).thenReturn(1);
        assertSuccessContaining(controller.createRoutes(new M_Route[] { route(null, "Oral") }),
                "Routes saved successfully");

        when(itemService.createRoutes(anyList())).thenReturn(0);
        assertSuccessContaining(controller.createRoutes(new M_Route[] { route(null, "Oral") }),
                "Failed to store Routes");
    }

    @Test
    @DisplayName("createRoutes should answer an error envelope when the store fails")
    void createRoutes_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.createRoutes(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createRoutes(new M_Route[] { route(null, "Oral") }));
    }

    @Test
    @DisplayName("editRoute should report success only when the service changed something")
    void editRoute_shouldReportOutcome() {
        when(itemService.editRoute(any())).thenReturn(1);
        assertSuccessContaining(controller.editRoute(route(21, "Oral")), "Route data updated successfully");

        when(itemService.editRoute(any())).thenReturn(0);
        assertSuccessContaining(controller.editRoute(route(21, "Oral")), "Failed to update Route data");
    }

    @Test
    @DisplayName("editRoute should answer an error envelope when the change fails")
    void editRoute_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.editRoute(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.editRoute(route(21, "Oral")));
    }

    @Test
    @DisplayName("blockRoute should report success only when the service blocked something")
    void blockRoute_shouldReportOutcome() {
        when(itemService.blockRoute(any())).thenReturn(1);
        assertSuccessContaining(controller.blockRoute(route(21, "Oral")), "Route blocked successfully");

        when(itemService.blockRoute(any())).thenReturn(0);
        assertSuccessContaining(controller.blockRoute(route(21, "Oral")), "Failed to block Route");
    }

    @Test
    @DisplayName("blockRoute should answer an error envelope when the block fails")
    void blockRoute_shouldAnswerErrorEnvelopeOnFailure() {
        when(itemService.blockRoute(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.blockRoute(route(21, "Oral")));
    }

    @Test
    @DisplayName("checkCode should route the check to the master the caller names")
    void checkCode_shouldRouteToNamedMaster() {
        when(itemService.checkCodeCategory(any())).thenReturn(Boolean.TRUE);
        when(itemService.checkCodeForm(any())).thenReturn(Boolean.FALSE);
        when(itemService.checkCodeItem(any())).thenReturn(Boolean.TRUE);
        when(itemService.checkCodeRoute(any())).thenReturn(Boolean.FALSE);

        assertSuccessContaining(controller.blockRoute(codeChecker("itemCategory", "CAT-1")), "true");
        assertSuccessContaining(controller.blockRoute(codeChecker("itemForm", "FRM-1")), "false");
        assertSuccessContaining(controller.blockRoute(codeChecker("itemMaster", "ITM-1")), "true");
        assertSuccessContaining(controller.blockRoute(codeChecker("route", "RTE-1")), "false");
    }

    @Test
    @DisplayName("checkCode should refuse a master it does not know how to check")
    void checkCode_shouldRefuseUnknownMaster() {
        String response = controller.blockRoute(codeChecker("something-else", "X-1"));

        assertGenericFailure(response);
        assertTrue(response.contains("Failed to check code for something-else"), response);
        verify(itemService, never()).checkCodeCategory(any());
    }

    @Test
    @DisplayName("checkCode should refuse a request that leaves out what to check")
    void checkCode_shouldRefuseIncompleteRequest() {
        CodeChecker incomplete = new CodeChecker();
        incomplete.setName("itemCategory");

        String response = controller.blockRoute(incomplete);

        assertGenericFailure(response);
        assertTrue(response.contains("Name, Code and ProviderServiceMapID is mandatory"), response);
    }

    @Test
    @DisplayName("checkCode should refuse a request that names no provider mapping")
    void checkCode_shouldRefuseRequestWithoutProviderMapping() {
        CodeChecker incomplete = codeChecker("itemCategory", "CAT-1");
        incomplete.setProviderServiceMapID(0);

        assertGenericFailure(controller.blockRoute(incomplete));
    }
}
