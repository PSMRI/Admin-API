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
package com.iemr.admin.controller.questionnaire;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.service.questionnaire.QuestionnaireServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * The questionnaire screen keeps the feedback questions a provider asks: adding
 * them, listing them, editing them and retiring them.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("QuestionnaireController Test Suite")
class QuestionnaireControllerTest {

    private static final String REQUEST = "{\"providerServiceMapID\":4001,\"questionID\":501}";

    @Mock
    private QuestionnaireServiceImpl questionnaireService;

    @InjectMocks
    private QuestionnaireController controller;

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static String errorMessageOf(String response) {
        return new JSONObject(response).getString("errorMessage");
    }

    @Test
    @DisplayName("saveQuestionnaire should confirm the questions it stored")
    void save_shouldConfirmStoredQuestions() throws Exception {
        when(questionnaireService.SaveQuestionnaire(anyString()))
                .thenReturn("Questionnaire Data Saved Successfully");

        String response = controller.saveQuestionnaire(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Questionnaire Data Saved Successfully"), response);
    }

    @Test
    @DisplayName("saveQuestionnaire should report the failure when nothing came back from the service")
    void save_shouldReportMissingAnswer() throws Exception {
        when(questionnaireService.SaveQuestionnaire(anyString())).thenReturn(null);

        assertEquals("error in saving Questionnaire data", errorMessageOf(controller.saveQuestionnaire(REQUEST)));
    }

    @Test
    @DisplayName("saveQuestionnaire should report the failure when the questions cannot be stored")
    void save_shouldReportStorageFailure() throws Exception {
        when(questionnaireService.SaveQuestionnaire(anyString()))
                .thenThrow(new Exception("error in saving Questionnaire data"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.saveQuestionnaire(REQUEST)));
    }

    @Test
    @DisplayName("getQuestionnaireList should answer the questions the provider asks")
    void getList_shouldAnswerProvidersQuestions() {
        when(questionnaireService.getQuestionnaireList(anyString()))
                .thenReturn("[{\"question\":\"Was the visit useful?\"}]");

        String response = controller.getQuestionnaireList(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Was the visit useful?"), response);
    }

    @Test
    @DisplayName("getQuestionnaireList should report the failure when nothing came back from the service")
    void getList_shouldReportMissingAnswer() {
        when(questionnaireService.getQuestionnaireList(anyString())).thenReturn(null);

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getQuestionnaireList(REQUEST)));
    }

    @Test
    @DisplayName("getQuestionnaireList should report the failure when the list cannot be answered")
    void getList_shouldReportLookupFailure() {
        when(questionnaireService.getQuestionnaireList(anyString()))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.getQuestionnaireList(REQUEST)));
    }

    @Test
    @DisplayName("deleteQuestionnaire should confirm the question it retired")
    void delete_shouldConfirmRetiredQuestion() {
        when(questionnaireService.deleteQuestionnaire(anyString()))
                .thenReturn("Questionnaire Deleted Successfully");

        String response = controller.deleteQuestionnaire(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Questionnaire Deleted Successfully"), response);
    }

    @Test
    @DisplayName("deleteQuestionnaire should report the failure when no question was retired")
    void delete_shouldReportNothingRetired() {
        when(questionnaireService.deleteQuestionnaire(anyString())).thenReturn(null);

        assertEquals("error occured while deleting question.........",
                errorMessageOf(controller.deleteQuestionnaire(REQUEST)));
    }

    @Test
    @DisplayName("deleteQuestionnaire should report the failure when the retirement cannot be recorded")
    void delete_shouldReportStorageFailure() {
        when(questionnaireService.deleteQuestionnaire(anyString())).thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.deleteQuestionnaire(REQUEST)));
    }

    @Test
    @DisplayName("editQuestionnaire should confirm the change it recorded")
    void edit_shouldConfirmRecordedChange() {
        when(questionnaireService.editQuestionnaire(anyString())).thenReturn("Questionnaire Updated Successfully");

        String response = controller.editQuestionnaire(REQUEST);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("Questionnaire Updated Successfully"), response);
    }

    @Test
    @DisplayName("editQuestionnaire should report the failure when nothing came back from the service")
    void edit_shouldReportMissingAnswer() {
        when(questionnaireService.editQuestionnaire(anyString())).thenReturn(null);

        assertEquals("error occured while editing question.........",
                errorMessageOf(controller.editQuestionnaire(REQUEST)));
    }

    @Test
    @DisplayName("editQuestionnaire should report the failure when the change cannot be recorded")
    void edit_shouldReportStorageFailure() {
        when(questionnaireService.editQuestionnaire(anyString())).thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.editQuestionnaire(REQUEST)));
    }
}
