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
package com.iemr.admin.controller.employeemaster;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.data.employeemaster.USRAgentMapping;
import com.iemr.admin.utils.exception.IEMRException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The agent endpoints hand out and reclaim the CTI agent ids an employee logs
 * into the call centre with.
 */
@DisplayName("EmployeeMasterController agent Test Suite")
class EmployeeMasterAgentControllerTest extends EmployeeMasterFixture {

    private static final String AGENT_REQUEST = "{\"providerServiceMapID\":4001,\"cti_CampaignName\":\"104\"}";

    private static USRAgentMapping agent(Integer id, String agentId) {
        return USRAgentMapping.initializeAllUSRAgentMapping(id, 9001, null, 4001, null, agentId,
                "agent-secret", "104", Boolean.TRUE);
    }

    @Test
    @DisplayName("getAvailableAgentIds should answer the agent ids still free on the campaign")
    void getAvailableAgentIds_shouldAnswerFreeAgentIds() throws Exception {
        when(usrAgentMappingService.getAvailableAgentIds(AGENT_REQUEST)).thenReturn(List.of(agent(1, "A-1")));

        assertSuccessContaining(controller.getAvailableAgentIds(AGENT_REQUEST), "A-1");
    }

    @Test
    @DisplayName("getAvailableAgentIds should answer an error envelope when the lookup fails")
    void getAvailableAgentIds_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(usrAgentMappingService.getAvailableAgentIds(anyString()))
                .thenThrow(new IEMRException("providerServiceMapID is required"));

        assertIemrFailure(controller.getAvailableAgentIds("{}"));
    }

    @Test
    @DisplayName("createUSRAgentMapping should answer the mappings the service stored")
    void createUSRAgentMapping_shouldAnswerStoredMappings() throws Exception {
        when(usrAgentMappingService.createUSRAgentMapping(anyString())).thenReturn(List.of(agent(1, "A-1")));

        assertSuccessContaining(
                controller.createUSRAgentMapping("[{\"agentID\":\"A-1\",\"providerServiceMapID\":4001}]"), "A-1");
    }

    @Test
    @DisplayName("createUSRAgentMapping should answer an error envelope when the store fails")
    void createUSRAgentMapping_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(usrAgentMappingService.createUSRAgentMapping(anyString()))
                .thenThrow(new IEMRException("agentID is required"));

        assertIemrFailure(controller.createUSRAgentMapping("[{}]"));
    }

    @Test
    @DisplayName("getAvailableCampaigns should answer the campaigns configured for the mapping")
    void getAvailableCampaigns_shouldAnswerConfiguredCampaigns() throws Exception {
        when(usrAgentMappingService.getAvailableCampaigns(anyString())).thenReturn(List.of("104", "1097"));

        assertSuccessContaining(controller.getAvailableCampaigns(AGENT_REQUEST), "104");
    }

    @Test
    @DisplayName("getAvailableCampaigns should answer an error envelope when the lookup fails")
    void getAvailableCampaigns_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(usrAgentMappingService.getAvailableCampaigns(anyString()))
                .thenThrow(new IEMRException("providerServiceMapID is required"));

        assertIemrFailure(controller.getAvailableCampaigns("{}"));
    }

    @Test
    @DisplayName("updateAgentIds should answer how many agent ids the service changed")
    void updateAgentIds_shouldAnswerChangedCount() throws Exception {
        when(usrAgentMappingService.updateAgentIds(anyString())).thenReturn(1);

        assertSuccessContaining(controller.updateAgentIds("{\"isAvailable\":false,\"usrMappingID\":9001}"), "1");
        verify(usrAgentMappingService).updateAgentIds("{\"isAvailable\":false,\"usrMappingID\":9001}");
    }

    @Test
    @DisplayName("updateAgentIds should answer an error envelope when the change fails")
    void updateAgentIds_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(usrAgentMappingService.updateAgentIds(anyString()))
                .thenThrow(new IEMRException("usrAgentMappingID is required"));

        assertIemrFailure(controller.updateAgentIds("{}"));
    }

    @Test
    @DisplayName("getAllAgentIds should answer every agent id under the mapping")
    void getAllAgentIds_shouldAnswerEveryAgentId() throws Exception {
        when(usrAgentMappingService.getAllAgentIds(anyString())).thenReturn(List.of(agent(1, "A-1")));

        assertSuccessContaining(controller.getAllAgentIds(AGENT_REQUEST), "A-1");
    }

    @Test
    @DisplayName("getAllAgentIds should answer an error envelope when the lookup fails")
    void getAllAgentIds_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(usrAgentMappingService.getAllAgentIds(anyString()))
                .thenThrow(new IEMRException("providerServiceMapID is required"));

        assertIemrFailure(controller.getAllAgentIds("{}"));
    }

    @Test
    @DisplayName("updateCTICampaignNameMapping should answer how many mappings moved campaign")
    void updateCTICampaignNameMapping_shouldAnswerChangedCount() throws Exception {
        when(usrAgentMappingService.updateCTICampaignNameMapping(anyString())).thenReturn(1);

        assertSuccessContaining(
                controller.updateCTICampaignNameMapping("{\"cti_CampaignName\":\"1097\",\"usrAgentMappingID\":1}"),
                "1");
    }

    @Test
    @DisplayName("updateCTICampaignNameMapping should answer an error envelope when the change fails")
    void updateCTICampaignNameMapping_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(usrAgentMappingService.updateCTICampaignNameMapping(anyString()))
                .thenThrow(new IEMRException("usrAgentMappingID is required"));

        assertIemrFailure(controller.updateCTICampaignNameMapping("{}"));
    }
}
