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
package com.iemr.admin.service.telemedicine;

import java.util.List;

import com.iemr.admin.data.telemedicine.M_UserTemp;
import com.iemr.admin.data.telemedicine.VideoConsultationDomain;
import com.iemr.admin.data.telemedicine.UserVideoConsultation;
import com.iemr.admin.utils.exception.VideoConsultationException;

public interface VideoConsultationInter {

	List<M_UserTemp> getunmappedUser(Integer serviceproviderID, Integer designationID);

	UserVideoConsultation createUser(UserVideoConsultation userVideoConsultation) throws VideoConsultationException;

	List<UserVideoConsultation> fetchmappedUser(Integer serviceproviderID);

	UserVideoConsultation editUser(UserVideoConsultation userVideoConsultation) throws VideoConsultationException;

	UserVideoConsultation deleteUser(Long userVideoConsultationMapID, Boolean deletedflag, String modifiedBy) throws VideoConsultationException;

	List<VideoConsultationDomain> getdomain(Integer serviceproviderID);


}
