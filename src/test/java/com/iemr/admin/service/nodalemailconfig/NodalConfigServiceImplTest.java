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
package com.iemr.admin.service.nodalemailconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.emailconfig.AuthorityEmail;
import com.iemr.admin.mapper.emailconfig.InstituteEmailConfigMapper;
import com.iemr.admin.model.emailconfig.NodalEmailRequest;
import com.iemr.admin.model.emailconfig.NodalEmailResponse;
import com.iemr.admin.model.emailconfig.CreateNodalEmailRequestModel;
import com.iemr.admin.model.emailconfig.UpdateNodalEmailRequest;
import com.iemr.admin.repository.emailconfig.InstituteEmailRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The nodal config service keeps the nodal officer mailboxes a complaint is
 * escalated to, narrowed by whichever parts of the location the caller names.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NodalConfigServiceImpl Test Suite")
class NodalConfigServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private InstituteEmailRepo instituteRepo;

    @Mock
    private InstituteEmailConfigMapper instituteEmailConfigMapper;

    @InjectMocks
    private NodalConfigServiceImpl service;

    private CriteriaQuery<AuthorityEmail> query;
    private TypedQuery<AuthorityEmail> typedQuery;

    @SuppressWarnings("unchecked")
    @BeforeEach
    @DisplayName("Stand in for the criteria query the service builds by hand")
    void setUp() {
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        query = mock(CriteriaQuery.class);
        Root<AuthorityEmail> root = mock(Root.class);
        typedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(builder);
        when(builder.createQuery(AuthorityEmail.class)).thenReturn(query);
        when(query.from(AuthorityEmail.class)).thenReturn(root);
        when(query.select(any())).thenReturn(query);
        when(query.where(any(Predicate[].class))).thenReturn(query);
        when(query.orderBy(any(jakarta.persistence.criteria.Order[].class))).thenReturn(query);
        when(root.get(anyString())).thenReturn(mock(Path.class));
        when(builder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(entityManager.createQuery(query)).thenReturn(typedQuery);
    }

    private static NodalEmailRequest fullyNarrowedRequest() {
        NodalEmailRequest request = new NodalEmailRequest();
        request.setAuthorityEmailID(1);
        request.setDeleted(false);
        request.setDistrictID(301);
        request.setDistrictBranchMappingID(30111);
        request.setBlockID(3011);
        request.setProviderServiceMapID(4001);
        request.setStateID(29);
        request.setMobileNo("9000000001");
        return request;
    }

    @Test
    @DisplayName("getAllEmailConfigs should answer the mailboxes the query found, as the screens read them")
    void getAll_shouldAnswerFoundMailboxes() {
        List<AuthorityEmail> found = List.of(new AuthorityEmail());
        List<NodalEmailResponse> published = List.of(new NodalEmailResponse());
        when(typedQuery.getResultList()).thenReturn(found);
        when(instituteEmailConfigMapper.resultToInstTypeEmailResponses(found)).thenReturn(published);

        assertSame(published, service.getAllNodalEmailConfigs(fullyNarrowedRequest()));
    }

    @Test
    @DisplayName("getAllEmailConfigs should narrow the query by every detail the caller named")
    void getAll_shouldNarrowByEveryNamedDetail() {
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>());

        service.getAllNodalEmailConfigs(fullyNarrowedRequest());

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(query).where(captor.capture());
        assertEquals(8, captor.getValue().length, "one narrowing per detail the caller named");
    }

    @Test
    @DisplayName("getAllEmailConfigs should narrow by nothing when the caller names nothing")
    void getAll_shouldNarrowByNothingForEmptyRequest() {
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>());

        service.getAllNodalEmailConfigs(new NodalEmailRequest());

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(query).where(captor.capture());
        assertEquals(0, captor.getValue().length);
    }

    @Test
    @DisplayName("saveEmailConfigs should store one mailbox per request and answer each as stored")
    void save_shouldStoreEachRequestedMailbox() {
        AuthorityEmail stored = new AuthorityEmail();
        when(instituteEmailConfigMapper.createRequestToInstituteEmailConfig(anyList()))
                .thenReturn(List.of(stored, stored));
        when(instituteRepo.save(stored)).thenReturn(stored);
        when(instituteEmailConfigMapper.resultInstType(stored))
                .thenReturn(new NodalEmailResponse());

        assertEquals(2, service.saveNodalEmailConfigs(List.of(new CreateNodalEmailRequestModel())).size());
    }

    @Test
    @DisplayName("saveEmailConfigs should store nothing when the caller asks for nothing")
    void save_shouldStoreNothingForEmptyRequest() {
        when(instituteEmailConfigMapper.createRequestToInstituteEmailConfig(anyList()))
                .thenReturn(new ArrayList<>());

        assertTrue(service.saveNodalEmailConfigs(new ArrayList<>()).isEmpty());
    }

    @Test
    @DisplayName("updateEmailConfigs should answer the mailbox as it stands after the change")
    void update_shouldAnswerChangedMailbox() {
        AuthorityEmail stored = new AuthorityEmail();
        NodalEmailResponse published = new NodalEmailResponse();
        when(instituteEmailConfigMapper.updateRequestToInstituteNodalEmailConf(any(UpdateNodalEmailRequest.class)))
                .thenReturn(stored);
        when(instituteRepo.save(stored)).thenReturn(stored);
        when(instituteEmailConfigMapper.resultToInstTypeNodalEmailResponse(stored)).thenReturn(published);

        assertSame(published, service.updateNodalEmailConfigs(new UpdateNodalEmailRequest()));
    }
}
