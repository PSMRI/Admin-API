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
package com.iemr.admin.repo.questionnaire;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.questionnaire.Questionnaire;

@Repository
public interface QuestionnaireRepository extends CrudRepository<Questionnaire, Integer> {

	@Query("SELECT DISTINCT q FROM Questionnaire q INNER JOIN q.qvalues v "
			+ "WHERE q.providerServiceMapID = :providerServiceMapID AND q.deleted is false AND v.deleted is false "
			+ "ORDER BY QuestionRank")
	public ArrayList<Questionnaire> findAllQuestions(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query(	"SELECT q FROM Questionnaire q  "
		    + "WHERE q.providerServiceMapID = :providerServiceMapID AND q.deleted is false AND q.answerType = 'Free Text' "
		    + "ORDER BY q.questionRank")
	public ArrayList<Questionnaire> findAllQuestionsFreeText(@Param("providerServiceMapID") Integer providerServiceMapID);
	
	@Query("SELECT COUNT(questionRank) FROM Questionnaire WHERE providerServiceMapID = :providerServiceMapID AND "
			+ "questionRank = :questionRank AND deleted is false")
	public int findQuestionWithRank(@Param("providerServiceMapID") Integer providerServiceMapID, 
			@Param("questionRank") Integer questionRank);
	
	@Transactional
	@Modifying
	@Query("UPDATE Questionnaire set questionRank = questionRank+1 "
			+ "WHERE questionRank >= :questionRank AND providerServiceMapID = :providerServiceMapID AND deleted is false")
	public int updateRankToNext(@Param("providerServiceMapID") Integer providerServiceMapID ,
			@Param("questionRank") Integer questionRank);

	@Transactional
	@Modifying
	@Query("UPDATE Questionnaire set questionRank = questionRank-1 "
			+ "WHERE questionRank > :questionRank AND providerServiceMapID = :providerServiceMapID AND deleted is false")
	public int updateRankToPrevious(@Param("providerServiceMapID") Integer providerServiceMapID ,
			@Param("questionRank") Integer questionRank);
	
	@Transactional
	@Modifying
	@Query("UPDATE Questionnaire set deleted = :deleted "
			+ "WHERE providerServiceMapID = :providerServiceMapID AND questionID = :questionID")
	public int deleteQuestion(@Param("providerServiceMapID") Integer providerServiceMapID ,
			@Param("questionID") Integer questionID, @Param("deleted") Boolean deleted);
 
	@Transactional
	@Modifying
	@Query("UPDATE QuestionnaireValues set deleted = :deleted "
			+ "WHERE providerServiceMapID = :providerServiceMapID AND questionID = :questionID")
	public int deleteOptions(@Param("providerServiceMapID") Integer providerServiceMapID ,
			@Param("questionID") Integer questionID, @Param("deleted") Boolean deleted);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Questionnaire q set q.question = :question, q.questionWeightage = :questionWeightage, "
			+ "q.answerType = :answerType, q.modifiedBy = :modifiedBy "
			+ "WHERE q.questionID = :questionID AND q.providerServiceMapID = :providerServiceMapID")	
	public int updateQuestion(@Param("questionID") Integer questionID, @Param("question") String question, 
			@Param("questionWeightage") Integer questionWeightage, @Param("answerType") String answerType, 
			@Param("providerServiceMapID") Integer providerServiceMapID, @Param("modifiedBy") String modifiedBy);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update QuestionnaireValues v set v.option = :option, v.optionWeightage = :optionWeightage, v.modifiedBy = :modifiedBy, v.deleted = :deleted "
			+ "WHERE v.questionID = :questionID AND v.questionValuesID = :questionValuesID")
	public int updateAnswer(@Param("questionID") Integer questionID, @Param("questionValuesID") Integer questionValuesID,  
			@Param("option") String option, @Param("optionWeightage") Integer optionWeightage, 
			@Param("modifiedBy") String modifiedBy,@Param("deleted") Boolean deleted);
}