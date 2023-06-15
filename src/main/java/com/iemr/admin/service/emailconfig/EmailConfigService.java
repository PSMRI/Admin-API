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
package com.iemr.admin.service.emailconfig;

import java.util.List;

import com.iemr.admin.model.emailconfig.CreateAuthEmailRequestModel;
import com.iemr.admin.model.emailconfig.AuthEmailRequest;
import com.iemr.admin.model.emailconfig.AuthEmailResponse;
import com.iemr.admin.model.emailconfig.UpdateAuthEmailRequest;

public interface EmailConfigService
{
	List<AuthEmailResponse> getAllEmailConfigs(AuthEmailRequest request);

	List<AuthEmailResponse> saveEmailConfigs(List<CreateAuthEmailRequestModel> createEmailRequests);

	AuthEmailResponse updateEmailConfigs(UpdateAuthEmailRequest request);
}
