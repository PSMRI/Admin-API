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


import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="m_questionnairevalues")
@Data
public class QuestionnaireValues {
	public QuestionnaireValues() {
		
	}
	public QuestionnaireValues(Integer questionValuesID, String option, Integer optionWeightage){
		this.questionValuesID = questionValuesID;
		this.option = option;
		this.optionWeightage = optionWeightage;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name="QuestionValuesID")
	private Integer questionValuesID;
	
	@Expose
	@Column(name="QuestionID")
	private Integer questionID;
	
	@Expose
	@Column(name="QuestionValues")
	private String option;
	@Expose
	@Column(name="QuestionValuesWeightage")
	private Integer optionWeightage;
	
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