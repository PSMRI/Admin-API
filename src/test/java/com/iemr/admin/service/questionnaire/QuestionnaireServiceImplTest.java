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
package com.iemr.admin.service.questionnaire;

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

import com.iemr.admin.data.questionnaire.Questionnaire;
import com.iemr.admin.data.questionnaire.QuestionnaireValues;
import com.iemr.admin.repo.questionnaire.QuestionnaireRepository;
import com.iemr.admin.repo.questionnaire.QuestionnaireValuesRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The questionnaire service keeps the feedback questions a provider asks, the
 * options each question offers, and the order the questions are asked in.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("QuestionnaireServiceImpl Test Suite")
class QuestionnaireServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer QUESTION_ID = 501;

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private QuestionnaireValuesRepository questionnaireValuesRepository;

    @InjectMocks
    private QuestionnaireServiceImpl service;

    private static final String SAVE_REQUEST = "[{\"questionnaireDetail\":{\"question\":\"Was the visit useful?\","
            + "\"questionRank\":1,\"questionWeightage\":5,\"answerType\":\"Radio\",\"providerServiceMapID\":4001,"
            + "\"createdBy\":\"admin\",\"questionOptions\":[{\"option\":\"Yes\",\"optionWeightage\":5}]}}]";

    private static Questionnaire stored() {
        Questionnaire question = new Questionnaire();
        question.setQuestionID(QUESTION_ID);
        question.setQuestion("Was the visit useful?");
        question.setQuestionRank(1);
        question.setProviderServiceMapID(PSM_ID);
        question.setCreatedBy("admin");
        QuestionnaireValues option = new QuestionnaireValues();
        option.setOption("Yes");
        question.setQuestionOptions(new ArrayList<>(List.of(option)));
        return question;
    }

    @Test
    @DisplayName("SaveQuestionnaire should store the question and stamp its options with the question it belongs to")
    void save_shouldStoreQuestionAndStampOptions() throws Exception {
        when(questionnaireRepository.findQuestionWithRank(PSM_ID, 1)).thenReturn(0);
        when(questionnaireRepository.save(any(Questionnaire.class))).thenReturn(stored());

        assertEquals("Questionnaire Data Saved Successfully", service.SaveQuestionnaire(SAVE_REQUEST));

        ArgumentCaptor<List<QuestionnaireValues>> captor = ArgumentCaptor.forClass(List.class);
        verify(questionnaireValuesRepository).saveAll(captor.capture());
        assertEquals(QUESTION_ID, captor.getValue().get(0).getQuestionID());
        assertEquals(PSM_ID, captor.getValue().get(0).getProviderServiceMapID());
        assertEquals("admin", captor.getValue().get(0).getCreatedBy());
    }

    @Test
    @DisplayName("SaveQuestionnaire should push the questions below down when the new one takes an occupied rank")
    void save_shouldPushLowerQuestionsDown() throws Exception {
        when(questionnaireRepository.findQuestionWithRank(PSM_ID, 1)).thenReturn(1);
        when(questionnaireRepository.save(any(Questionnaire.class))).thenReturn(stored());

        service.SaveQuestionnaire(SAVE_REQUEST);

        verify(questionnaireRepository).updateRankToNext(PSM_ID, 1);
    }

    @Test
    @DisplayName("SaveQuestionnaire should leave the order alone when the new question's rank is free")
    void save_shouldLeaveOrderAloneForFreeRank() throws Exception {
        when(questionnaireRepository.findQuestionWithRank(PSM_ID, 1)).thenReturn(0);
        when(questionnaireRepository.save(any(Questionnaire.class))).thenReturn(stored());

        service.SaveQuestionnaire(SAVE_REQUEST);

        verify(questionnaireRepository, never()).updateRankToNext(anyInt(), anyInt());
    }

    @Test
    @DisplayName("SaveQuestionnaire should give up when the question cannot be stored")
    void save_shouldGiveUpWhenStorageFails() {
        when(questionnaireRepository.save(any(Questionnaire.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertThrows(RuntimeException.class, () -> service.SaveQuestionnaire(SAVE_REQUEST));
    }

    @Test
    @DisplayName("getQuestionnaireList should answer the multiple choice and free text questions together")
    void getList_shouldAnswerBothKindsOfQuestion() {
        Questionnaire freeText = new Questionnaire();
        freeText.setQuestion("Anything else?");
        when(questionnaireRepository.findAllQuestions(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(stored())));
        when(questionnaireRepository.findAllQuestionsFreeText(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(freeText)));

        String answered = service.getQuestionnaireList("{\"providerServiceMapID\":4001}");

        assertTrue(answered.contains("Was the visit useful?"), answered);
        assertTrue(answered.contains("Anything else?"), answered);
    }

    @Test
    @DisplayName("getQuestionnaireList should answer an empty list when the provider asks nothing")
    void getList_shouldAnswerEmptyListForProviderWithNoQuestions() {
        when(questionnaireRepository.findAllQuestions(PSM_ID)).thenReturn(new ArrayList<>());
        when(questionnaireRepository.findAllQuestionsFreeText(PSM_ID)).thenReturn(new ArrayList<>());

        assertEquals("[]", service.getQuestionnaireList("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("deleteQuestionnaire should retire the question, its options and close the gap in the order")
    void delete_shouldRetireQuestionAndCloseGap() {
        when(questionnaireRepository.deleteQuestion(PSM_ID, QUESTION_ID, Boolean.TRUE)).thenReturn(1);
        when(questionnaireRepository.deleteOptions(PSM_ID, QUESTION_ID, Boolean.TRUE)).thenReturn(1);

        assertEquals("Questionnaire Deleted Successfully", service.deleteQuestionnaire(
                "{\"providerServiceMapID\":4001,\"questionID\":501,\"questionRank\":1,\"deleted\":true}"));
        verify(questionnaireRepository).updateRankToPrevious(PSM_ID, 1);
    }

    @Test
    @DisplayName("deleteQuestionnaire should answer nothing and leave the order alone when no question was retired")
    void delete_shouldAnswerNothingWhenNoQuestionRetired() {
        when(questionnaireRepository.deleteQuestion(anyInt(), anyInt(), anyBoolean())).thenReturn(0);
        when(questionnaireRepository.deleteOptions(anyInt(), anyInt(), anyBoolean())).thenReturn(0);

        assertNull(service.deleteQuestionnaire(
                "{\"providerServiceMapID\":4001,\"questionID\":-1,\"questionRank\":1,\"deleted\":true}"));
        verify(questionnaireRepository, never()).updateRankToPrevious(anyInt(), anyInt());
    }

    @Test
    @DisplayName("editQuestionnaire should change the question and the options that already exist")
    void edit_shouldChangeQuestionAndExistingOptions() {
        String request = "{\"questionnaireDetail\":{\"questionID\":501,\"question\":\"Was the visit helpful?\","
                + "\"questionWeightage\":6,\"answerType\":\"Radio\",\"providerServiceMapID\":4001,"
                + "\"modifiedBy\":\"supervisor\",\"questionOptions\":[{\"questionValuesID\":9001,"
                + "\"option\":\"Yes\",\"optionWeightage\":6,\"deleted\":false}]}}";

        assertEquals("Questionnaire Updated Successfully", service.editQuestionnaire(request));

        verify(questionnaireRepository).updateQuestion(QUESTION_ID, "Was the visit helpful?", 6, "Radio", PSM_ID,
                "supervisor");
        verify(questionnaireRepository).updateAnswer(QUESTION_ID, 9001, "Yes", 6, "supervisor", Boolean.FALSE);
        verify(questionnaireValuesRepository, never()).save(any(QuestionnaireValues.class));
    }

    @Test
    @DisplayName("editQuestionnaire should add an option that has never been stored before")
    void edit_shouldAddBrandNewOption() {
        String request = "{\"questionnaireDetail\":{\"questionID\":501,\"question\":\"Was the visit helpful?\","
                + "\"questionWeightage\":6,\"answerType\":\"Radio\",\"providerServiceMapID\":4001,"
                + "\"createdBy\":\"admin\",\"modifiedBy\":\"supervisor\","
                + "\"questionOptions\":[{\"option\":\"Maybe\",\"optionWeightage\":3}]}}";

        service.editQuestionnaire(request);

        ArgumentCaptor<QuestionnaireValues> captor = ArgumentCaptor.forClass(QuestionnaireValues.class);
        verify(questionnaireValuesRepository).save(captor.capture());
        assertEquals("Maybe", captor.getValue().getOption());
        assertEquals(QUESTION_ID, captor.getValue().getQuestionID());
        assertEquals(PSM_ID, captor.getValue().getProviderServiceMapID());
        assertEquals("supervisor", captor.getValue().getModifiedBy());
        verify(questionnaireRepository, never()).updateAnswer(anyInt(), anyInt(), anyString(), anyInt(), anyString(),
                anyBoolean());
    }

    @Test
    @DisplayName("editQuestionnaire should give up when the change cannot be recorded")
    void edit_shouldGiveUpWhenStorageFails() {
        when(questionnaireRepository.updateQuestion(anyInt(), anyString(), anyInt(), anyString(), anyInt(),
                anyString())).thenThrow(new RuntimeException("row is locked"));

        assertThrows(RuntimeException.class, () -> service.editQuestionnaire(
                "{\"questionnaireDetail\":{\"questionID\":501,\"question\":\"Was it helpful?\","
                        + "\"questionWeightage\":6,\"answerType\":\"Radio\",\"providerServiceMapID\":4001,"
                        + "\"modifiedBy\":\"supervisor\",\"questionOptions\":[]}}"));
    }
}
