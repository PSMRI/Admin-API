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
package com.iemr.admin.data.questionnaire;

import java.util.ArrayList;
import java.util.Set;

import com.google.gson.annotations.Expose;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name="m_questionnaire")
@Data
public class Questionnaire {
	public Questionnaire() {

	}
	public Questionnaire(Integer questionID, String questionType, String question, Integer questionRank, Integer questionWeightage, String answerType) {
		this.questionID = questionID;
		this.questionType = questionType;
		this.question = question;
		this.questionRank = questionRank;
		this.questionWeightage = questionWeightage;
		this.answerType = answerType;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="QuestionID")
	private Integer questionID;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "QuestionID", referencedColumnName = "QuestionID", insertable = false, updatable = false)
    @Expose
    private Set<QuestionnaireValues> qvalues;

	@Expose
	@Transient
	Questionnaire questionnaireDetail;
	
	@Expose
	@Transient
	ArrayList<QuestionnaireValues> questionOptions;

	@Expose
	@Column(name="QuestionType")
	private String questionType;

	@Expose
	@Column(name="Question")
	private String question;
	@Expose
	@Column(name="QuestionWeightage")
	private Integer questionWeightage;
	@Expose
	@Column(name="AnswerType")
	private String answerType;
	@Expose
	@Column(name="QuestionRank")
	private Integer questionRank;
	@Expose
	@Column(name="QuestionTypeID")
	private Integer questionTypeID;
	
	@Expose
	@Column(name="CreatedBy")
	private String createdBy;

	@Expose
	@Column(name="ModifiedBy")
	private String modifiedBy;
	
	@Expose
	@Column(name="ProviderServiceMapID")
	private Integer providerServiceMapID;

	@Column(name="Deleted")
	private Boolean deleted;
}