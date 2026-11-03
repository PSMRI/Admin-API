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
package com.iemr.admin.controller.provideronboard;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.iemr.admin.data.provideronboard.M_Category;
import com.iemr.admin.data.provideronboard.M_Feedbacknature;
import com.iemr.admin.data.provideronboard.M_Feedbacktype;
import com.iemr.admin.data.provideronboard.M_Severity;
import com.iemr.admin.data.provideronboard.M_Subcategory;
import com.iemr.admin.data.provideronboard.V_Showsubcategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The category, severity and feedback endpoints define how a provider's agents
 * classify a call once it has been taken.
 */
@DisplayName("ProviderOnBoardController category Test Suite")
class ProviderOnBoardCategoryControllerTest extends ProviderOnBoardFixture {

    private static final String SUBCAT_REQUEST = "{\"categoryID\":81,\"createdBy\":\"admin\",\"subcatArray\":"
            + "[{\"subCategoryName\":\"Fever\",\"subCategoryDesc\":\"High temperature\","
            + "\"subCatFilePath\":\"/fever\"}]}";

    private static M_Category category(Integer id, String name) {
        M_Category category = new M_Category();
        category.setCategoryID(id);
        category.setCategoryName(name);
        return category;
    }

    private static M_Subcategory subCategory(Integer id, String name) {
        M_Subcategory subCategory = new M_Subcategory();
        subCategory.setSubCategoryID(id);
        subCategory.setSubCategoryName(name);
        return subCategory;
    }

    @Test
    @DisplayName("saveCategory should attach the sub-categories to the category the service resolves")
    void saveCategory_shouldAttachSubCategoriesToResolvedCategory() {
        ArrayList<M_Subcategory> stored = new ArrayList<>(List.of(subCategory(91, "Fever")));
        when(categoryInter.getCategoryId(any())).thenReturn(81);
        when(categoryInter.saveSubCatData(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.saveCategory(SUBCAT_REQUEST), "Fever");

        ArgumentCaptor<List<M_Subcategory>> captor = ArgumentCaptor.forClass(List.class);
        verify(categoryInter).saveSubCatData(captor.capture());
        assertEquals(81, captor.getValue().get(0).getCategoryID());
        assertEquals("admin", captor.getValue().get(0).getCreatedBy());
    }

    @Test
    @DisplayName("saveCategory should answer an error envelope when no sub-categories are named")
    void saveCategory_shouldAnswerErrorEnvelopeWithoutSubCategories() {
        when(categoryInter.getCategoryId(any())).thenReturn(81);

        assertCodeException(controller.saveCategory("{\"categoryID\":81}"));
    }

    @Test
    @DisplayName("saveCategoryUseExist should attach the sub-categories to the category the caller names")
    void saveCategoryUseExist_shouldAttachSubCategoriesToNamedCategory() {
        ArrayList<M_Subcategory> stored = new ArrayList<>(List.of(subCategory(91, "Fever")));
        when(categoryInter.saveSubCatData(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.saveCategoryUseExist(SUBCAT_REQUEST), "Fever");

        ArgumentCaptor<List<M_Subcategory>> captor = ArgumentCaptor.forClass(List.class);
        verify(categoryInter).saveSubCatData(captor.capture());
        assertEquals(81, captor.getValue().get(0).getCategoryID());
    }

    @Test
    @DisplayName("saveCategoryUseExist should answer an error envelope when no sub-categories are named")
    void saveCategoryUseExist_shouldAnswerErrorEnvelopeWithoutSubCategories() {
        assertCodeException(controller.saveCategoryUseExist("{\"categoryID\":81}"));
    }

    @Test
    @DisplayName("getCategoryBySubServiceID should answer the categories under the sub-service")
    void getCategoryBySubServiceID_shouldAnswerCategories() {
        V_Showsubcategory view = new V_Showsubcategory();
        view.setSubCategoryID(91);
        view.setSubCategoryName("Fever");
        ArrayList<V_Showsubcategory> stored = new ArrayList<>(List.of(view));
        when(categoryInter.getCategoryByMapIDAndSubServiceID(4001, 61)).thenReturn(stored);

        assertSuccessContaining(
                controller.getCategoryBySubServiceID("{\"providerServiceMapID\":4001,\"subServiceID\":61}"), "Fever");
    }

    @Test
    @DisplayName("getCategoryBySubServiceID should answer an error envelope when the lookup fails")
    void getCategoryBySubServiceID_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.getCategoryByMapIDAndSubServiceID(anyInt(), anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(
                controller.getCategoryBySubServiceID("{\"providerServiceMapID\":4001,\"subServiceID\":61}"));
    }

    @Test
    @DisplayName("getsubCategory should answer the sub-categories of the category")
    void getsubCategory_shouldAnswerSubCategories() {
        ArrayList<M_Subcategory> stored = new ArrayList<>(List.of(subCategory(91, "Fever")));
        when(categoryInter.getCategory(81)).thenReturn(stored);

        assertSuccessContaining(controller.getsubCategory("{\"categoryID\":81}"), "Fever");
    }

    @Test
    @DisplayName("getsubCategory should answer an error envelope when the lookup fails")
    void getsubCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.getCategory(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getsubCategory("{\"categoryID\":81}"));
    }

    @Test
    @DisplayName("getCategory should answer the categories under the sub-service and mapping")
    void getCategory_shouldAnswerCategories() {
        ArrayList<M_Category> stored = new ArrayList<>(List.of(category(81, "Medical")));
        when(categoryInter.getAllCategory(61, 4001)).thenReturn(stored);

        assertSuccessContaining(controller.getCategory("{\"subServiceID\":61,\"providerServiceMapID\":4001}"),
                "Medical");
    }

    @Test
    @DisplayName("getCategory should answer an error envelope when the lookup fails")
    void getCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.getAllCategory(anyInt(), anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getCategory("{\"subServiceID\":61,\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("updateSubCategory should copy the edited fields onto the stored sub-category")
    void updateSubCategory_shouldCopyEditedFields() {
        M_Subcategory stored = subCategory(91, "old name");
        when(categoryInter.getSubCategory(91)).thenReturn(stored);
        when(categoryInter.updateSubCatData(stored)).thenReturn(stored);

        String response = controller.updateSubCategory("{\"subCategoryID\":91,\"categoryID\":81,"
                + "\"subCategoryName\":\"Fever\",\"subCategoryDesc\":\"High temperature\","
                + "\"subCatFilePath\":\"/fever\"}");

        assertSuccessContaining(response, "Fever");
        assertEquals("High temperature", stored.getSubCategoryDesc());
        assertEquals("/fever", stored.getSubCatFilePath());
    }

    @Test
    @DisplayName("updateSubCategory should answer an error envelope for a sub-category that does not exist")
    void updateSubCategory_shouldAnswerErrorEnvelopeForUnknownSubCategory() {
        when(categoryInter.getSubCategory(91)).thenReturn(null);

        assertCodeException(controller.updateSubCategory("{\"subCategoryID\":91}"));
    }

    @Test
    @DisplayName("createCategory should answer the categories the service stored")
    void createCategory_shouldAnswerStoredCategories() {
        ArrayList<M_Category> stored = new ArrayList<>(List.of(category(81, "Medical")));
        when(categoryInter.createcat(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createCategory("[{\"categoryName\":\"Medical\"}]"), "Medical");
    }

    @Test
    @DisplayName("createCategory should answer an error envelope when the store fails")
    void createCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.createcat(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createCategory("[{\"categoryName\":\"Medical\"}]"));
    }

    @Test
    @DisplayName("deleteCategory1 should mark the category deleted and answer what was saved")
    void deleteCategory1_shouldMarkCategoryDeleted() {
        M_Category stored = category(81, "Medical");
        when(categoryInter.getcatdatabycatId(81)).thenReturn(stored);
        when(categoryInter.deletedata(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteCategory1("{\"categoryID\":81,\"deleted\":true}"), "Medical");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteCategory1 should answer an error envelope for a category that does not exist")
    void deleteCategory1_shouldAnswerErrorEnvelopeForUnknownCategory() {
        when(categoryInter.getcatdatabycatId(81)).thenReturn(null);

        assertCodeException(controller.deleteCategory1("{\"categoryID\":81,\"deleted\":true}"));
    }

    @Test
    @DisplayName("createSubCategory should answer the sub-categories the service stored")
    void createSubCategory_shouldAnswerStoredSubCategories() {
        ArrayList<M_Subcategory> stored = new ArrayList<>(List.of(subCategory(91, "Fever")));
        when(categoryInter.createSubCategory(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createSubCategory("[{\"subCategoryName\":\"Fever\"}]"), "Fever");
    }

    @Test
    @DisplayName("createSubCategory should answer an error envelope when the store fails")
    void createSubCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.createSubCategory(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createSubCategory("[{\"subCategoryName\":\"Fever\"}]"));
    }

    @Test
    @DisplayName("deleteSubCategory should mark the sub-category deleted and answer what was saved")
    void deleteSubCategory_shouldMarkSubCategoryDeleted() {
        M_Subcategory stored = subCategory(91, "Fever");
        when(categoryInter.getSubCategory(91)).thenReturn(stored);
        when(categoryInter.updateSubCatData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteSubCategory("{\"subCategoryID\":91,\"deleted\":true}"), "Fever");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteSubCategory should answer an error envelope for a sub-category that does not exist")
    void deleteSubCategory_shouldAnswerErrorEnvelopeForUnknownSubCategory() {
        when(categoryInter.getSubCategory(91)).thenReturn(null);

        assertCodeException(controller.deleteSubCategory("{\"subCategoryID\":91,\"deleted\":true}"));
    }

    @Test
    @DisplayName("getSubCategory should answer the sub-category view the service resolves")
    void getSubCategory_shouldAnswerSubCategoryView() {
        V_Showsubcategory view = new V_Showsubcategory();
        view.setSubCategoryID(91);
        view.setSubCategoryName("Fever");
        ArrayList<V_Showsubcategory> stored = new ArrayList<>(List.of(view));
        when(categoryInter.getSubCategory1(91)).thenReturn(stored);

        assertSuccessContaining(controller.getSubCategory("{\"subCategoryID\":91}"), "Fever");
    }

    @Test
    @DisplayName("getSubCategory should answer an error envelope when the lookup fails")
    void getSubCategory_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.getSubCategory1(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getSubCategory("{\"subCategoryID\":91}"));
    }

    @Test
    @DisplayName("updateCategory should copy the edited fields onto the stored category")
    void updateCategory_shouldCopyEditedFields() {
        M_Category stored = category(81, "old name");
        when(categoryInter.getcatdatabycatId(81)).thenReturn(stored);
        when(categoryInter.deletedata(stored)).thenReturn(stored);

        String response = controller.updateCategory("{\"categoryID\":81,\"categoryName\":\"Medical\","
                + "\"categoryDesc\":\"Clinical calls\",\"modifiedBy\":\"admin\",\"s104_CS_Type\":true}");

        assertSuccessContaining(response, "Medical");
        assertEquals("Clinical calls", stored.getCategoryDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("updateCategory should answer an error envelope for a category that does not exist")
    void updateCategory_shouldAnswerErrorEnvelopeForUnknownCategory() {
        when(categoryInter.getcatdatabycatId(81)).thenReturn(null);

        assertCodeException(controller.updateCategory("{\"categoryID\":81}"));
    }

    @Test
    @DisplayName("mapCategorytoFeedbackNature should map every category the request names")
    void mapCategorytoFeedbackNature_shouldMapEveryCategory() {
        when(categoryInter.updateCategory(81, 21)).thenReturn(1);
        when(categoryInter.updateCategory(82, 22)).thenReturn(1);

        String response = controller.mapCategorytoFeedbackNature(
                "[{\"categoryID\":81,\"feedbackNatureID\":21},{\"categoryID\":82,\"feedbackNatureID\":22}]");

        assertSuccessContaining(response, "inserted");
        verify(categoryInter).updateCategory(81, 21);
        verify(categoryInter).updateCategory(82, 22);
    }

    @Test
    @DisplayName("mapCategorytoFeedbackNature should answer an error envelope when the mapping fails")
    void mapCategorytoFeedbackNature_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.updateCategory(anyInt(), anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.mapCategorytoFeedbackNature("[{\"categoryID\":81,\"feedbackNatureID\":21}]"));
    }

    @Test
    @DisplayName("updateCategorytoFeedbackNature should clear the old category before mapping the new one")
    void updateCategorytoFeedbackNature_shouldClearOldCategoryFirst() {
        M_Category previous = category(80, "Previous");
        previous.setFeedbackNatureID(21);
        M_Category current = category(81, "Medical");
        when(categoryInter.getcatdatabycatId(80)).thenReturn(previous);
        when(categoryInter.getcatdatabycatId(81)).thenReturn(current);
        when(categoryInter.deletedata(any())).thenReturn(current);

        String response = controller.updateCategorytoFeedbackNature("{\"oldCategoryID\":80,\"categoryID\":81,"
                + "\"feedbackNatureID\":22,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "Medical");
        assertNull(previous.getFeedbackNatureID(), "the old category must stop pointing at a feedback nature");
        assertEquals(22, current.getFeedbackNatureID());
    }

    @Test
    @DisplayName("updateCategorytoFeedbackNature should answer an error envelope for an unknown old category")
    void updateCategorytoFeedbackNature_shouldAnswerErrorEnvelopeForUnknownOldCategory() {
        when(categoryInter.getcatdatabycatId(anyInt())).thenReturn(null);

        assertCodeException(
                controller.updateCategorytoFeedbackNature("{\"oldCategoryID\":80,\"categoryID\":81}"));
    }

    @Test
    @DisplayName("getmapedCategorytoFeedbackNature should answer the categories mapped to the feedback nature")
    void getmapedCategorytoFeedbackNature_shouldAnswerMappedCategories() {
        ArrayList<M_Category> stored = new ArrayList<>(List.of(category(81, "Medical")));
        when(categoryInter.getAllCategorywithFeedbackNatureID(4001, 21)).thenReturn(stored);

        assertSuccessContaining(controller.getmapedCategorytoFeedbackNatureWithCatIDandFeedbackNatureID(
                "{\"providerServiceMapID\":4001,\"feedbackNatureID\":21}"), "Medical");
    }

    @Test
    @DisplayName("getmapedCategorytoFeedbackNature should answer an error envelope when the lookup fails")
    void getmapedCategorytoFeedbackNature_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.getAllCategorywithFeedbackNatureID(anyInt(), anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getmapedCategorytoFeedbackNatureWithCatIDandFeedbackNatureID(
                "{\"providerServiceMapID\":4001,\"feedbackNatureID\":21}"));
    }

    @Test
    @DisplayName("getunmappedCategoryforFeedbackNature should answer the categories still unmapped")
    void getunmappedCategoryforFeedbackNature_shouldAnswerUnmappedCategories() {
        ArrayList<M_Category> stored = new ArrayList<>(List.of(category(81, "Medical")));
        when(categoryInter.getUpmappedCategory(4001)).thenReturn(stored);

        assertSuccessContaining(
                controller.getunmappedCategoryforFeedbackNature("{\"providerServiceMapID\":4001}"), "Medical");
    }

    @Test
    @DisplayName("getunmappedCategoryforFeedbackNature should answer an error envelope when the lookup fails")
    void getunmappedCategoryforFeedbackNature_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.getUpmappedCategory(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getunmappedCategoryforFeedbackNature("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("unmappCategoryforFeedbackNature should clear the feedback nature from the category")
    void unmappCategoryforFeedbackNature_shouldClearFeedbackNature() {
        M_Category stored = category(81, "Medical");
        stored.setFeedbackNatureID(21);
        when(categoryInter.getcatdatabycatId(81)).thenReturn(stored);
        when(categoryInter.deletedata(stored)).thenReturn(stored);

        assertSuccessContaining(
                controller.unmappCategoryforFeedbackNature("{\"categoryID\":81,\"modifiedBy\":\"admin\"}"), "Medical");
        assertNull(stored.getFeedbackNatureID());
    }

    @Test
    @DisplayName("unmappCategoryforFeedbackNature should answer an error envelope for an unknown category")
    void unmappCategoryforFeedbackNature_shouldAnswerErrorEnvelopeForUnknownCategory() {
        when(categoryInter.getcatdatabycatId(81)).thenReturn(null);

        assertCodeException(controller.unmappCategoryforFeedbackNature("{\"categoryID\":81}"));
    }

    @Test
    @DisplayName("getAllCategoryPsmMapid should answer every category under the mapping")
    void getAllCategoryPsmMapid_shouldAnswerEveryCategory() {
        ArrayList<M_Category> stored = new ArrayList<>(List.of(category(81, "Medical")));
        when(categoryInter.getAllCategory1(4001)).thenReturn(stored);

        assertSuccessContaining(controller.getAllCategoryPsmMapid("{\"providerServiceMapID\":4001}"), "Medical");
    }

    @Test
    @DisplayName("getAllCategoryPsmMapid should answer an error envelope when the lookup fails")
    void getAllCategoryPsmMapid_shouldAnswerErrorEnvelopeOnFailure() {
        when(categoryInter.getAllCategory1(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllCategoryPsmMapid("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getServerity should answer the severities configured for the mapping")
    void getServerity_shouldAnswerConfiguredSeverities() {
        M_Severity severity = new M_Severity();
        severity.setSeverityID(31);
        severity.setSeverityTypeName("Critical");
        ArrayList<M_Severity> stored = new ArrayList<>(List.of(severity));
        when(m_ServerityInter.getServerity(4001)).thenReturn(stored);

        assertSuccessContaining(controller.getServerity("{\"providerServiceMapID\":4001}"), "Critical");
    }

    @Test
    @DisplayName("getServerity should answer an error envelope when the lookup fails")
    void getServerity_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_ServerityInter.getServerity(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getServerity("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("saveServerity should answer the severities the service stored")
    void saveServerity_shouldAnswerStoredSeverities() {
        M_Severity severity = new M_Severity();
        severity.setSeverityID(31);
        severity.setSeverityTypeName("Critical");
        ArrayList<M_Severity> stored = new ArrayList<>(List.of(severity));
        when(m_ServerityInter.saveServerity(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.saveServerity("[{\"severityTypeName\":\"Critical\"}]"), "Critical");
    }

    @Test
    @DisplayName("saveServerity should answer an error envelope when the store fails")
    void saveServerity_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_ServerityInter.saveServerity(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveServerity("[{\"severityTypeName\":\"Critical\"}]"));
    }

    @Test
    @DisplayName("deleteServerity should mark the severity deleted and answer what was saved")
    void deleteServerity_shouldMarkSeverityDeleted() {
        M_Severity stored = new M_Severity();
        stored.setSeverityID(31);
        stored.setSeverityTypeName("Critical");
        when(m_ServerityInter.getDataByServId(31)).thenReturn(stored);
        when(m_ServerityInter.deletedataser(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteServerity("{\"severityID\":31,\"deleted\":true}"), "Critical");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteServerity should answer an error envelope for a severity that does not exist")
    void deleteServerity_shouldAnswerErrorEnvelopeForUnknownSeverity() {
        when(m_ServerityInter.getDataByServId(31)).thenReturn(null);

        assertCodeException(controller.deleteServerity("{\"severityID\":31,\"deleted\":true}"));
    }

    @Test
    @DisplayName("editServerity should copy the edited fields onto the stored severity")
    void editServerity_shouldCopyEditedFields() {
        M_Severity stored = new M_Severity();
        stored.setSeverityID(31);
        when(m_ServerityInter.getDataByServId(31)).thenReturn(stored);
        when(m_ServerityInter.deletedataser(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editServerity("{\"severityID\":31,\"severityTypeName\":\"Critical\","
                + "\"severityDesc\":\"Needs escalation\"}"), "Critical");
        assertEquals("Needs escalation", stored.getSeverityDesc());
    }

    @Test
    @DisplayName("editServerity should answer an error envelope for a severity that does not exist")
    void editServerity_shouldAnswerErrorEnvelopeForUnknownSeverity() {
        when(m_ServerityInter.getDataByServId(31)).thenReturn(null);

        assertCodeException(controller.editServerity("{\"severityID\":31}"));
    }

    @Test
    @DisplayName("getFeedbackType should answer the feedback types configured for the mapping")
    void getFeedbackType_shouldAnswerConfiguredFeedbackTypes() {
        M_Feedbacktype feedbackType = new M_Feedbacktype();
        feedbackType.setFeedbackTypeID(41);
        feedbackType.setFeedbackTypeName("Complaint");
        ArrayList<M_Feedbacktype> stored = new ArrayList<>(List.of(feedbackType));
        when(m_FeedbacktypeInter.getFeedbackt(4001)).thenReturn(stored);

        assertSuccessContaining(controller.getFeedbackType("{\"providerServiceMapID\":4001}"), "Complaint");
    }

    @Test
    @DisplayName("getFeedbackType should answer an error envelope when the lookup fails")
    void getFeedbackType_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_FeedbacktypeInter.getFeedbackt(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFeedbackType("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("saveFeedbackType should answer the feedback types the service stored")
    void saveFeedbackType_shouldAnswerStoredFeedbackTypes() {
        M_Feedbacktype feedbackType = new M_Feedbacktype();
        feedbackType.setFeedbackTypeID(41);
        feedbackType.setFeedbackTypeName("Complaint");
        ArrayList<M_Feedbacktype> stored = new ArrayList<>(List.of(feedbackType));
        when(m_FeedbacktypeInter.saveFeedbackType(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.saveFeedbackType("[{\"feedbackTypeName\":\"Complaint\"}]"), "Complaint");
    }

    @Test
    @DisplayName("saveFeedbackType should answer an error envelope when the store fails")
    void saveFeedbackType_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_FeedbacktypeInter.saveFeedbackType(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveFeedbackType("[{\"feedbackTypeName\":\"Complaint\"}]"));
    }

    @Test
    @DisplayName("editFeedbackType should copy the edited fields onto the stored feedback type")
    void editFeedbackType_shouldCopyEditedFields() {
        M_Feedbacktype stored = new M_Feedbacktype();
        stored.setFeedbackTypeID(41);
        when(m_FeedbacktypeInter.getDataByServId(41)).thenReturn(stored);
        when(m_FeedbacktypeInter.deletedataser(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editFeedbackType("{\"feedbackTypeID\":41,"
                + "\"feedbackTypeName\":\"Complaint\",\"feedbackDesc\":\"Service issue\","
                + "\"modifiedBy\":\"admin\"}"), "Complaint");
        assertEquals("Service issue", stored.getFeedbackDesc());
    }

    @Test
    @DisplayName("editFeedbackType should answer an error envelope for a feedback type that does not exist")
    void editFeedbackType_shouldAnswerErrorEnvelopeForUnknownFeedbackType() {
        when(m_FeedbacktypeInter.getDataByServId(41)).thenReturn(null);

        assertCodeException(controller.editFeedbackType("{\"feedbackTypeID\":41}"));
    }

    @Test
    @DisplayName("deleteFeedbackType should mark the feedback type deleted")
    void deleteFeedbackType_shouldMarkFeedbackTypeDeleted() {
        M_Feedbacktype stored = new M_Feedbacktype();
        stored.setFeedbackTypeID(41);
        stored.setFeedbackTypeName("Complaint");
        when(m_FeedbacktypeInter.getDataByServId(41)).thenReturn(stored);
        when(m_FeedbacktypeInter.deletedataser(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteFeedbackType("{\"feedbackTypeID\":41,\"deleted\":true}"),
                "Complaint");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteFeedbackType should answer an error envelope for a feedback type that does not exist")
    void deleteFeedbackType_shouldAnswerErrorEnvelopeForUnknownFeedbackType() {
        when(m_FeedbacktypeInter.getDataByServId(41)).thenReturn(null);

        assertCodeException(controller.deleteFeedbackType("{\"feedbackTypeID\":41,\"deleted\":true}"));
    }

    @Test
    @DisplayName("getFeedbackNatureType should answer the natures under the feedback type")
    void getFeedbackNatureType_shouldAnswerNatures() {
        M_Feedbacknature nature = new M_Feedbacknature();
        nature.setFeedbackNatureID(21);
        nature.setFeedbackNature("Escalation");
        ArrayList<M_Feedbacknature> stored = new ArrayList<>(List.of(nature));
        when(m_FeedbacknatureInteger.getFeedbackNatureType(41)).thenReturn(stored);

        assertSuccessContaining(controller.getFeedbackNatureType("{\"feedbackTypeID\":41}"), "Escalation");
    }

    @Test
    @DisplayName("getFeedbackNatureType should answer an error envelope when the lookup fails")
    void getFeedbackNatureType_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_FeedbacknatureInteger.getFeedbackNatureType(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getFeedbackNatureType("{\"feedbackTypeID\":41}"));
    }

    @Test
    @DisplayName("createFeedbackNatureType should answer the natures the service stored")
    void createFeedbackNatureType_shouldAnswerStoredNatures() {
        M_Feedbacknature nature = new M_Feedbacknature();
        nature.setFeedbackNatureID(21);
        nature.setFeedbackNature("Escalation");
        ArrayList<M_Feedbacknature> stored = new ArrayList<>(List.of(nature));
        when(m_FeedbacknatureInteger.createFeedbackNatueType(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createFeedbackNatureType("[{\"feedbackNature\":\"Escalation\"}]"),
                "Escalation");
    }

    @Test
    @DisplayName("createFeedbackNatureType should answer an error envelope when the store fails")
    void createFeedbackNatureType_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_FeedbacknatureInteger.createFeedbackNatueType(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createFeedbackNatureType("[{\"feedbackNature\":\"Escalation\"}]"));
    }

    @Test
    @DisplayName("deleteFeedbackNatureType should mark the nature deleted")
    void deleteFeedbackNatureType_shouldMarkNatureDeleted() {
        M_Feedbacknature stored = new M_Feedbacknature();
        stored.setFeedbackNatureID(21);
        stored.setFeedbackNature("Escalation");
        when(m_FeedbacknatureInteger.editFeedbackNatureType(21)).thenReturn(stored);
        when(m_FeedbacknatureInteger.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteFeedbackNatureType("{\"feedbackNatureID\":21,\"deleted\":true}"),
                "Escalation");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteFeedbackNatureType should answer an error envelope for a nature that does not exist")
    void deleteFeedbackNatureType_shouldAnswerErrorEnvelopeForUnknownNature() {
        when(m_FeedbacknatureInteger.editFeedbackNatureType(21)).thenReturn(null);

        assertCodeException(controller.deleteFeedbackNatureType("{\"feedbackNatureID\":21,\"deleted\":true}"));
    }

    @Test
    @DisplayName("editFeedbackNatureType should copy the edited fields onto the stored nature")
    void editFeedbackNatureType_shouldCopyEditedFields() {
        M_Feedbacknature stored = new M_Feedbacknature();
        stored.setFeedbackNatureID(21);
        when(m_FeedbacknatureInteger.editFeedbackNatureType(21)).thenReturn(stored);
        when(m_FeedbacknatureInteger.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editFeedbackNatureType("{\"feedbackNatureID\":21,"
                + "\"feedbackNature\":\"Escalation\",\"feedbackNatureDesc\":\"Raise to supervisor\","
                + "\"modifiedBy\":\"admin\"}"), "Escalation");
        assertEquals("Raise to supervisor", stored.getFeedbackNatureDesc());
    }

    @Test
    @DisplayName("editFeedbackNatureType should answer an error envelope for a nature that does not exist")
    void editFeedbackNatureType_shouldAnswerErrorEnvelopeForUnknownNature() {
        when(m_FeedbacknatureInteger.editFeedbackNatureType(21)).thenReturn(null);

        assertCodeException(controller.editFeedbackNatureType("{\"feedbackNatureID\":21}"));
    }
}
