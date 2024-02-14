/*
* AMRIT – Accessible Medical Records via Integrated Technology 
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

import java.io.Console;
import java.util.ArrayList;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.iemr.admin.data.questionnaire.Questionnaire;
import com.iemr.admin.data.questionnaire.QuestionnaireValues;
import com.iemr.admin.repo.questionnaire.QuestionnaireRepository;
import com.iemr.admin.repo.questionnaire.QuestionnaireValuesRepository;
import com.iemr.admin.utils.mapper.InputMapper;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

	@Autowired
	public QuestionnaireRepository questionnaireRepository;
	@Autowired
	public QuestionnaireValuesRepository questionnaireValuesRepository;

	@Override
	public String SaveQuestionnaire(String request) throws Exception {
		Questionnaire[] questionnaire = InputMapper.gson().fromJson(request, Questionnaire[].class);
		Questionnaire questionnaireDetail = new Questionnaire();
		Integer Count = 0;
		String QuestionnaireResponse = null;

		for (int i = 0; i < questionnaire.length; i++) {
			Questionnaire detail = questionnaire[i];

			//System.out.println(detail.getQuestionnaireDetail().getProviderServiceMapID());
			//System.out.println(detail.getQuestionnaireDetail().getQuestionRank());
			
			int rankCount = questionnaireRepository.findQuestionWithRank(detail.getQuestionnaireDetail().getProviderServiceMapID(), 
					detail.getQuestionnaireDetail().getQuestionRank());
			
			if(rankCount != 0 ) {
				questionnaireRepository.updateRankToNext(detail.getQuestionnaireDetail().getProviderServiceMapID(), 
						detail.getQuestionnaireDetail().getQuestionRank());
			}
			
			// saving questions
			questionnaireDetail = questionnaireRepository.save(detail.getQuestionnaireDetail());

			// saving options of the questions.
			ArrayList<QuestionnaireValues> QuestionValues = questionnaireDetail.getQuestionOptions();
			for (QuestionnaireValues options : QuestionValues) {
				options.setCreatedBy(questionnaireDetail.getCreatedBy());
				options.setProviderServiceMapID(questionnaireDetail.getProviderServiceMapID());
				options.setQuestionID(questionnaireDetail.getQuestionID());
			}
			questionnaireValuesRepository.saveAll(QuestionValues);
			Count++;
		}

		if (questionnaire.length == Count) {
			QuestionnaireResponse = "Questionnaire Data Saved Successfully";
		} else
			throw new Exception("error in saving Questionnaire data");

		return QuestionnaireResponse;
	}

	@Override
	public String getQuestionnaireList(String request) {
		Questionnaire questionnaireDetail = InputMapper.gson().fromJson(request, Questionnaire.class);
		ArrayList<Questionnaire> list0 = questionnaireRepository
				.findAllQuestions(questionnaireDetail.getProviderServiceMapID());
		
		ArrayList<Questionnaire> list1 = questionnaireRepository
				.findAllQuestionsFreeText(questionnaireDetail.getProviderServiceMapID());

		for (Questionnaire q : list1) {
			list0.add(q);
		}
		
		return new Gson().toJson(list0);
	}

	@Override
	public String deleteQuestionnaire(String request) {
		Questionnaire questionnaireDetail = InputMapper.gson().fromJson(request, Questionnaire.class);
		String response = null;

		int i = questionnaireRepository.deleteQuestion(questionnaireDetail.getProviderServiceMapID(),questionnaireDetail.getQuestionID(),
				questionnaireDetail.getDeleted());

		int j = questionnaireRepository.deleteOptions(questionnaireDetail.getProviderServiceMapID(),questionnaireDetail.getQuestionID(),
				questionnaireDetail.getDeleted());

		if(i > 0) {
			questionnaireRepository.updateRankToPrevious(questionnaireDetail.getProviderServiceMapID(), 
					questionnaireDetail.getQuestionRank());
			response = "Questionnaire Deleted Successfully";
		}		

		return response;
	}

	@Override
	public String editQuestionnaire(String request) {
		Questionnaire questionnaire = InputMapper.gson().fromJson(request, Questionnaire.class);
		
		// updating questions
		Questionnaire Question = questionnaire.getQuestionnaireDetail();
		questionnaireRepository.updateQuestion(Question.getQuestionID(), Question.getQuestion(),  
				Question.getQuestionWeightage(), Question.getAnswerType(), Question.getProviderServiceMapID(), 
				Question.getModifiedBy());

		// updating options of the questions.
		ArrayList<QuestionnaireValues> QuestionValues = Question.getQuestionOptions();
		for (QuestionnaireValues options : QuestionValues) {
			//System.out.println("options.getOptionWeightage(): "+options.getOptionWeightage());
			if(options.getQuestionValuesID() != null) {
				questionnaireRepository.updateAnswer(Question.getQuestionID(), options.getQuestionValuesID(), options.getOption(), 
						options.getOptionWeightage(), Question.getModifiedBy(),options.getDeleted());
			}
			else {
				options.setCreatedBy(Question.getCreatedBy());
				options.setProviderServiceMapID(Question.getProviderServiceMapID());
				options.setQuestionID(Question.getQuestionID());
				options.setModifiedBy(Question.getModifiedBy());
				questionnaireValuesRepository.save(options);
			}
		}
		return "Questionnaire Updated Successfully";
	}

}
