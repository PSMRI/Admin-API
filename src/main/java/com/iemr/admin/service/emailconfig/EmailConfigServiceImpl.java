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

import java.util.ArrayList;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.emailconfig.AuthorityEmail;
import com.iemr.admin.mapper.emailconfig.InstituteEmailConfigMapper;
import com.iemr.admin.model.emailconfig.AuthEmailRequest;
import com.iemr.admin.model.emailconfig.AuthEmailResponse;
import com.iemr.admin.model.emailconfig.CreateAuthEmailRequestModel;
import com.iemr.admin.model.emailconfig.UpdateAuthEmailRequest;
import com.iemr.admin.repository.emailconfig.InstituteEmailRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class EmailConfigServiceImpl implements EmailConfigService
{

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	InstituteEmailRepo instituteRepo;
	@Autowired
	InstituteEmailConfigMapper instituteEmailConfigMapper;

	@Override
	public List<AuthEmailResponse> getAllEmailConfigs(AuthEmailRequest request)
	{
		List<AuthEmailResponse> response;
		List<AuthorityEmail> resultSet = prepareQuery(request);
		response = instituteEmailConfigMapper.resultToInstTypeEmailResponse(resultSet);
		return response;
	}

	private List<AuthorityEmail> prepareQuery(AuthEmailRequest request)
	{
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AuthorityEmail> query = builder.createQuery(AuthorityEmail.class);
		Root<AuthorityEmail> root = query.from(AuthorityEmail.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (request.getAuthorityEmailID() != null)
		{
			predicates.add(builder.equal(root.get("authorityEmailID"), request.getAuthorityEmailID()));
		}
		if (request.getDeleted() != null)
		{
			predicates.add(builder.equal(root.get("deleted"), request.getDeleted()));
		}
		if (request.getDistrictID() != null)
		{
			predicates.add(builder.equal(root.get("districtID"), request.getDistrictID()));
		}
		if (request.getDistrictBranchMappingID() != null)
		{
			predicates.add(builder.equal(root.get("districtBranchMappingID"), request.getDistrictBranchMappingID()));
		}
		if (request.getBlockID() != null)
		{
			predicates.add(builder.equal(root.get("blockID"), request.getBlockID()));
		}
		if (request.getProviderServiceMapID() != null)
		{
			predicates.add(builder.equal(root.get("providerServiceMapID"), request.getProviderServiceMapID()));
		}
		if (request.getStateID() != null)
		{
			predicates.add(builder.equal(root.get("stateID"), request.getStateID()));
		}
		query.select(root).where(predicates.toArray(new Predicate[] {}))
				.orderBy(builder.desc(root.get("authorityEmailID")));
		TypedQuery<AuthorityEmail> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}

	@Override
	public List<AuthEmailResponse> saveEmailConfigs(List<CreateAuthEmailRequestModel> request)
	{
		List<AuthEmailResponse> responses = new ArrayList<AuthEmailResponse>();
		List<AuthorityEmail> createEmailConfigs = instituteEmailConfigMapper.createRequestToInstituteEmailConf(request);
		for (AuthorityEmail createEmailConfig : createEmailConfigs)
		{
			AuthorityEmail createdEmailConfig = instituteRepo.save(createEmailConfig);
			AuthEmailResponse response = instituteEmailConfigMapper.resultToInstTypeEmailResponse(createdEmailConfig);
			responses.add(response);
		}
		return responses;
	}

	@Override
	public AuthEmailResponse updateEmailConfigs(UpdateAuthEmailRequest request)
	{
		AuthEmailResponse response;
		AuthorityEmail updateEmailConfig = instituteEmailConfigMapper.updateRequestToInstituteEmailConf(request);
		// instituteRepo.save(updateEmailConfig);
		// AuthorityEmail updatedEmailConfig = instituteRepo.findOne(request.getInstitutionTypeAuthMapID());
		AuthorityEmail updatedEmailConfig = instituteRepo.save(updateEmailConfig);
		response = instituteEmailConfigMapper.resultToInstTypeEmailResponse(updatedEmailConfig);
		return response;
	}

}
