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

import com.iemr.admin.data.provideronboard.M_Feedbacktype;
import com.iemr.admin.repository.provideronboard.M_FeedbacktypeRepo;
@Service
class M_FeedbacktypeImpl implements M_FeedbacktypeInter {
	
	@Autowired
	private M_FeedbacktypeRepo m_FeedbacktypeRepo;

	@Override
	public ArrayList<M_Feedbacktype> getFeedbackt(Integer providerServiceMapID) {
		 ArrayList<M_Feedbacktype> data=m_FeedbacktypeRepo.getAllFeedbackType(providerServiceMapID);
		return data;
	}

	@Override
	public ArrayList<M_Feedbacktype> saveFeedbackType(List<M_Feedbacktype> feedbackTypedata) {
		ArrayList<M_Feedbacktype> data=(ArrayList<M_Feedbacktype>) m_FeedbacktypeRepo.saveAll(feedbackTypedata);
		return data;
	}

	@Override
	public M_Feedbacktype getDataByServId(Integer feedbackTypeID) {
		M_Feedbacktype deletefeedback=m_FeedbacktypeRepo.deleteFeedback(feedbackTypeID);
		return deletefeedback;
	}

	@Override
	public M_Feedbacktype deletedataser(M_Feedbacktype getDAta) {
		M_Feedbacktype deletefeedbackdata=m_FeedbacktypeRepo.save(getDAta);
		return deletefeedbackdata;
	}

}
