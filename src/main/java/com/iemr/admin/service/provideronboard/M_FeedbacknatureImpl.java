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
package com.iemr.admin.service.provideronboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.provideronboard.M_Feedbacknature;
import com.iemr.admin.repository.provideronboard.M_FeedbacknatureRepo;

@Service
public class M_FeedbacknatureImpl implements M_FeedbacknatureInteger{
	
	
	@Autowired
	private M_FeedbacknatureRepo m_FeedbacknatureRepo;

	@Override
	public ArrayList<M_Feedbacknature> getFeedbackNatureType(Integer feedbackTypeID) {
		ArrayList<M_Feedbacknature>  data=m_FeedbacknatureRepo.getInstuteType(feedbackTypeID);
		return data;
	}

	@Override
	public ArrayList<M_Feedbacknature> createFeedbackNatueType(List<M_Feedbacknature> instuteType) {
		
		ArrayList<M_Feedbacknature> data=(ArrayList<M_Feedbacknature>) m_FeedbacknatureRepo.saveAll(instuteType);
		return data;
	}

	@Override
	public M_Feedbacknature editFeedbackNatureType(Integer feedbackNatureID) {
		M_Feedbacknature data=m_FeedbacknatureRepo.editFeedbackNatureType(feedbackNatureID);
		return data;
	}

	@Override
	public M_Feedbacknature saveEditedData(M_Feedbacknature geteditFeedbackNatureData) {
		M_Feedbacknature data=m_FeedbacknatureRepo.save(geteditFeedbackNatureData);
		return data;
	}
	
	

}
