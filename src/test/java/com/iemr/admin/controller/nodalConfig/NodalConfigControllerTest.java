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
package com.iemr.admin.controller.nodalConfig;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;

import com.iemr.admin.model.emailconfig.NodalEmailRequest;
import com.iemr.admin.model.emailconfig.NodalEmailResponse;
import com.iemr.admin.model.emailconfig.CreateNodalEmailRequestModel;
import com.iemr.admin.model.emailconfig.UpdateNodalEmailRequest;
import com.iemr.admin.service.nodalemailconfig.NodalConfigService;
import com.iemr.admin.utils.mapper.OutputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The nodal config screen keeps the nodal officer mailboxes a complaint is
 * escalated to.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NodalConfigController Test Suite")
class NodalConfigControllerTest {

    @Mock
    private NodalConfigService nodalConfigService;

    @InjectMocks
    private NodalConfigController controller;

    private final MockHttpServletRequest servletRequest = new MockHttpServletRequest();

    @BeforeEach
    @DisplayName("Prime the shared output builder the screens publish through")
    void setUp() {
        new OutputMapper();
    }

    private static NodalEmailResponse mailbox() {
        NodalEmailResponse mailbox = new NodalEmailResponse();
        mailbox.setEmailID("nodal.bengaluru@example.org");
        return mailbox;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    @Test
    @DisplayName("saveConfig should answer the mailboxes it recorded")
    void save_shouldAnswerRecordedMailboxes() {
        when(nodalConfigService.saveNodalEmailConfigs(anyList())).thenReturn(List.of(mailbox()));

        String response = controller.saveConfig(List.of(new CreateNodalEmailRequestModel()), servletRequest);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("nodal.bengaluru@example.org"), response);
    }

    @Test
    @DisplayName("saveConfig should report the failure when the mailbox cannot be recorded")
    void save_shouldReportStorageFailure() {
        when(nodalConfigService.saveNodalEmailConfigs(anyList())).thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.saveConfig(new ArrayList<>(), servletRequest)));
    }

    @Test
    @DisplayName("getEmailConfigs should answer the mailboxes matching what the caller narrowed by")
    void get_shouldAnswerMatchingMailboxes() {
        when(nodalConfigService.getAllNodalEmailConfigs(any(NodalEmailRequest.class))).thenReturn(List.of(mailbox()));

        String response = controller.getNodalEmailConfigs(new NodalEmailRequest(), servletRequest);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("nodal.bengaluru@example.org"), response);
    }

    @Test
    @DisplayName("getEmailConfigs should report the failure when the mailboxes cannot be answered")
    void get_shouldReportLookupFailure() {
        when(nodalConfigService.getAllNodalEmailConfigs(any(NodalEmailRequest.class)))
                .thenThrow(new RuntimeException("query timed out"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getNodalEmailConfigs(new NodalEmailRequest(), servletRequest)));
    }

    @Test
    @DisplayName("updateEmailConfig should answer the mailbox as it stands after the change")
    void update_shouldAnswerChangedMailbox() {
        when(nodalConfigService.updateNodalEmailConfigs(any(UpdateNodalEmailRequest.class))).thenReturn(mailbox());

        String response = controller.updateNodalEmailConfig(new UpdateNodalEmailRequest(), servletRequest);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("nodal.bengaluru@example.org"), response);
    }

    @Test
    @DisplayName("updateEmailConfig should report the failure when the change cannot be recorded")
    void update_shouldReportStorageFailure() {
        when(nodalConfigService.updateNodalEmailConfigs(any(UpdateNodalEmailRequest.class)))
                .thenThrow(new RuntimeException("row is locked"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.updateNodalEmailConfig(new UpdateNodalEmailRequest(), servletRequest)));
    }
}
