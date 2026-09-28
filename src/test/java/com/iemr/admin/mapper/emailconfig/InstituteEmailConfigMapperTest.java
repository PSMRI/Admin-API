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
package com.iemr.admin.mapper.emailconfig;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.data.emailconfig.AuthorityEmail;
import com.iemr.admin.model.emailconfig.AuthEmailRequest;
import com.iemr.admin.model.emailconfig.AuthEmailResponse;
import com.iemr.admin.model.emailconfig.CreateAuthEmailRequestModel;
import com.iemr.admin.model.emailconfig.CreateNodalEmailRequestModel;
import com.iemr.admin.model.emailconfig.NodalEmailResponse;
import com.iemr.admin.model.emailconfig.UpdateAuthEmailRequest;
import com.iemr.admin.model.emailconfig.UpdateNodalEmailRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The escalation emails a nodal officer receives are addressed from these
 * records, so the mapper must carry every contact field across rather than drop
 * one silently.
 */
@DisplayName("InstituteEmailConfigMapper Test Suite")
class InstituteEmailConfigMapperTest {

    private final InstituteEmailConfigMapper mapper = InstituteEmailConfigMapper.INSTANCE;

    private static AuthorityEmail storedRecord() {
        AuthorityEmail stored = new AuthorityEmail();
        stored.setAuthorityEmailID(9001);
        stored.setStateID(29);
        stored.setDistrictID(301);
        stored.setBlockID(401);
        stored.setDistrictBranchMappingID(501);
        stored.setDesignationID(7);
        stored.setAuthorityName("Dr Asha Rao");
        stored.setEmailID("asha.rao@example.org");
        stored.setContactNo("9000000001");
        stored.setProviderServiceMapID(4001);
        stored.setCreatedBy("admin");
        stored.setCreatedDate(Timestamp.valueOf("2026-02-17 09:30:00"));
        stored.setModifiedBy("admin");
        stored.setDeleted(Boolean.FALSE);
        return stored;
    }

    private static CreateAuthEmailRequestModel createAuthorityRequest() {
        CreateAuthEmailRequestModel request = new CreateAuthEmailRequestModel();
        request.setStateID(29);
        request.setDistrictID(301);
        request.setBlockID(401);
        request.setDistrictBranchMappingID(501);
        request.setDesignationID(7);
        request.setAuthorityName("Dr Asha Rao");
        request.setEmailID("asha.rao@example.org");
        request.setContactNo("9000000001");
        request.setProviderServiceMapID(4001);
        request.setCreatedBy("admin");
        return request;
    }

    private static CreateNodalEmailRequestModel createNodalRequest() {
        CreateNodalEmailRequestModel request = new CreateNodalEmailRequestModel();
        request.setStateID(29);
        request.setDistrictID(301);
        request.setDesignationID(7);
        request.setAuthorityName("Dr Asha Rao");
        request.setEmailID("asha.rao@example.org");
        request.setContactNo("9000000001");
        request.setMobileNo("9000000002");
        request.setProviderServiceMapID(4001);
        request.setCreatedBy("admin");
        return request;
    }

    @Test
    @DisplayName("requestToInstituteEmailConf should carry the search request onto the record")
    void requestToInstituteEmailConf_shouldCarrySearchRequest() {
        AuthEmailRequest request = new AuthEmailRequest();
        request.setAuthorityEmailID(9001);
        request.setStateID(29);
        request.setDistrictID(301);
        request.setBlockID(401);
        request.setDistrictBranchMappingID(501);
        request.setDesignationID(7);
        request.setProviderServiceMapID(4001);
        request.setDeleted(Boolean.FALSE);

        AuthorityEmail mapped = mapper.requestToInstituteEmailConf(request);

        assertEquals(9001, mapped.getAuthorityEmailID());
        assertEquals(29, mapped.getStateID());
        assertEquals(501, mapped.getDistrictBranchMappingID());
        assertEquals(4001, mapped.getProviderServiceMapID());
    }

    @Test
    @DisplayName("requestToInstituteEmailConf should map a whole batch of search requests")
    void requestToInstituteEmailConf_shouldMapBatch() {
        AuthEmailRequest request = new AuthEmailRequest();
        request.setStateID(29);

        List<AuthorityEmail> mapped = mapper.requestToInstituteEmailConf(List.of(request));

        assertEquals(1, mapped.size());
        assertEquals(29, mapped.get(0).getStateID());
    }

    @Test
    @DisplayName("createRequestToInstituteEmailConf should carry every contact field onto the new record")
    void createRequestToInstituteEmailConf_shouldCarryContactFields() {
        AuthorityEmail mapped = mapper.createRequestToInstituteEmailConf(createAuthorityRequest());

        assertEquals("Dr Asha Rao", mapped.getAuthorityName());
        assertEquals("asha.rao@example.org", mapped.getEmailID());
        assertEquals("9000000001", mapped.getContactNo());
        assertEquals("admin", mapped.getCreatedBy());
    }

    @Test
    @DisplayName("createRequestToInstituteEmailConfig should carry the nodal officer's mobile number too")
    void createRequestToInstituteEmailConfig_shouldCarryMobileNumber() {
        AuthorityEmail mapped = mapper.createRequestToInstituteEmailConfig(createNodalRequest());

        assertEquals("Dr Asha Rao", mapped.getAuthorityName());
        assertEquals("asha.rao@example.org", mapped.getEmailID());
    }

    @Test
    @DisplayName("the create mappers should each map a whole batch")
    void createMappers_shouldMapBatches() {
        assertEquals(1, mapper.createRequestToInstituteEmailConf(List.of(createAuthorityRequest())).size());
        assertEquals(1, mapper.createRequestToInstituteEmailConfig(List.of(createNodalRequest())).size());
    }

    @Test
    @DisplayName("updateRequestToInstituteEmailConf should carry the edited fields onto the record")
    void updateRequestToInstituteEmailConf_shouldCarryEditedFields() {
        UpdateAuthEmailRequest request = new UpdateAuthEmailRequest();
        request.setAuthorityEmailID(9001);
        request.setAuthorityName("Dr Ravi Kumar");
        request.setEmailID("ravi.kumar@example.org");
        request.setContactNo("9000000003");
        request.setModifiedBy("admin");
        request.setDeleted(Boolean.FALSE);

        AuthorityEmail mapped = mapper.updateRequestToInstituteEmailConf(request);

        assertEquals(9001, mapped.getAuthorityEmailID());
        assertEquals("Dr Ravi Kumar", mapped.getAuthorityName());
        assertEquals("admin", mapped.getModifiedBy());
    }

    @Test
    @DisplayName("updateRequestToInstituteNodalEmailConf should carry the edited nodal fields onto the record")
    void updateRequestToInstituteNodalEmailConf_shouldCarryEditedFields() {
        UpdateNodalEmailRequest request = new UpdateNodalEmailRequest();
        request.setAuthorityEmailID(9001);
        request.setAuthorityName("Dr Ravi Kumar");
        request.setMobileNo("9000000004");
        request.setModifiedBy("admin");

        AuthorityEmail mapped = mapper.updateRequestToInstituteNodalEmailConf(request);

        assertEquals(9001, mapped.getAuthorityEmailID());
        assertEquals("Dr Ravi Kumar", mapped.getAuthorityName());
    }

    @Test
    @DisplayName("updateRequestToInstituteEmailConf should map a whole batch of edits")
    void updateRequestToInstituteEmailConf_shouldMapBatch() {
        UpdateAuthEmailRequest request = new UpdateAuthEmailRequest();
        request.setAuthorityEmailID(9001);

        assertEquals(1, mapper.updateRequestToInstituteEmailConf(List.of(request)).size());
    }

    @Test
    @DisplayName("resultToInstTypeEmailResponse should publish the stored record back to the caller")
    void resultToInstTypeEmailResponse_shouldPublishStoredRecord() {
        AuthEmailResponse published = mapper.resultToInstTypeEmailResponse(storedRecord());

        assertEquals(9001, published.getAuthorityEmailID());
        assertEquals("Dr Asha Rao", published.getAuthorityName());
        assertEquals("asha.rao@example.org", published.getEmailID());
        assertEquals(Timestamp.valueOf("2026-02-17 09:30:00"), published.getCreatedDate());
    }

    @Test
    @DisplayName("the nodal response mappers should publish the stored record back to the caller")
    void nodalResponseMappers_shouldPublishStoredRecord() {
        NodalEmailResponse published = mapper.resultToInstTypeNodalEmailResponse(storedRecord());
        NodalEmailResponse alsoPublished = mapper.resultInstType(storedRecord());

        assertEquals("Dr Asha Rao", published.getAuthorityName());
        assertEquals("Dr Asha Rao", alsoPublished.getAuthorityName());
        assertEquals(4001, published.getProviderServiceMapID());
    }

    @Test
    @DisplayName("the response mappers should publish a whole batch of stored records")
    void responseMappers_shouldPublishBatches() {
        List<AuthorityEmail> stored = List.of(storedRecord(), storedRecord());

        assertEquals(2, mapper.resultToInstTypeEmailResponse(stored).size());
        assertEquals(2, mapper.resultToInstTypeEmailResponses(stored).size());
    }

    @Test
    @DisplayName("the mappers should answer nothing rather than an empty record for a missing input")
    void mappers_shouldAnswerNothingForMissingInput() {
        assertNull(mapper.requestToInstituteEmailConf((AuthEmailRequest) null));
        assertNull(mapper.requestToInstituteEmailConf((List<AuthEmailRequest>) null));
        assertNull(mapper.createRequestToInstituteEmailConf((CreateAuthEmailRequestModel) null));
        assertNull(mapper.createRequestToInstituteEmailConf((List<CreateAuthEmailRequestModel>) null));
        assertNull(mapper.createRequestToInstituteEmailConfig((CreateNodalEmailRequestModel) null));
        assertNull(mapper.createRequestToInstituteEmailConfig((List<CreateNodalEmailRequestModel>) null));
        assertNull(mapper.updateRequestToInstituteEmailConf((UpdateAuthEmailRequest) null));
        assertNull(mapper.updateRequestToInstituteEmailConf((List<UpdateAuthEmailRequest>) null));
        assertNull(mapper.updateRequestToInstituteNodalEmailConf(null));
        assertNull(mapper.resultToInstTypeEmailResponse((AuthorityEmail) null));
        assertNull(mapper.resultToInstTypeEmailResponse((List<AuthorityEmail>) null));
        assertNull(mapper.resultToInstTypeNodalEmailResponse(null));
        assertNull(mapper.resultInstType(null));
        assertNull(mapper.resultToInstTypeEmailResponses(null));
    }

    @Test
    @DisplayName("the batch mappers should answer an empty batch for an empty input")
    void batchMappers_shouldAnswerEmptyBatchForEmptyInput() {
        assertTrue(mapper.requestToInstituteEmailConf(List.<AuthEmailRequest>of()).isEmpty());
        assertTrue(mapper.resultToInstTypeEmailResponse(List.<AuthorityEmail>of()).isEmpty());
        assertTrue(mapper.resultToInstTypeEmailResponses(List.<AuthorityEmail>of()).isEmpty());
    }
}
