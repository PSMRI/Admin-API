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
package com.iemr.admin.controller.provideronboard;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.provideronboard.M_ServiceMaster;
import com.iemr.admin.data.provideronboard.M_UserservicerolemappingForRole;
import com.iemr.admin.data.provideronboard.ServiceProvider_Model;
import com.iemr.admin.data.provideronboard.V_Showprovideradmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The provider endpoints onboard a service provider, map it onto the states and
 * services it serves, and keep its provider-admin users attached to it.
 */
@DisplayName("ProviderOnBoardController provider Test Suite")
class ProviderOnBoardProviderControllerTest extends ProviderOnBoardFixture {

    private static final String ONBOARD_REQUEST = "{\"serviceProviderName\":\"Piramal Swasthya\","
            + "\"statusID\":1,\"createdBy\":\"admin\",\"providerAdminDetails\":[{\"userName\":\"pa.user\"}],"
            + "\"stateAndServiceMapList\":[{\"stateId\":\"29\",\"services\":[\"1\",\"3\"]}]}";

    private static ServiceProvider_Model provider(Integer id, String name) {
        ServiceProvider_Model provider = new ServiceProvider_Model();
        provider.setServiceProviderId(id);
        provider.setServiceProviderName(name);
        return provider;
    }

    @Test
    @DisplayName("providerCreationAndMapping should report success once the admin role is created")
    void providerCreationAndMapping_shouldReportSuccess() {
        M_ProviderServiceMapping mapping = new M_ProviderServiceMapping();
        mapping.setProviderServiceMapID(4001);
        when(serviceProvider_ServiceImpl.createProvider(any(java.util.Set.class))).thenReturn(77);
        when(serviceProvider_ServiceImpl.mapProviderStateService(any())).thenReturn(List.of(mapping));
        when(iemrUserServiceImpl.createUser(any(), anyString())).thenReturn(3117);
        when(iemrUserServiceImpl.createUserServiceRoleMapping(anyList(), anyInt(), anyString())).thenReturn(1);

        String response = controller.providerCreationAndMapping(ONBOARD_REQUEST);

        assertSuccessContaining(response, "true");
    }

    @Test
    @DisplayName("providerCreationAndMapping should report failure when the admin role cannot be created")
    void providerCreationAndMapping_shouldReportRoleCreationFailure() {
        M_ProviderServiceMapping mapping = new M_ProviderServiceMapping();
        mapping.setProviderServiceMapID(4001);
        when(serviceProvider_ServiceImpl.createProvider(any(java.util.Set.class))).thenReturn(77);
        when(serviceProvider_ServiceImpl.mapProviderStateService(any())).thenReturn(List.of(mapping));
        when(iemrUserServiceImpl.createUser(any(), anyString())).thenReturn(3117);
        when(iemrUserServiceImpl.createUserServiceRoleMapping(anyList(), anyInt(), anyString())).thenReturn(0);

        assertSuccessContaining(controller.providerCreationAndMapping(ONBOARD_REQUEST), "false");
    }

    @Test
    @DisplayName("providerCreationAndMapping should report failure when nothing was mapped for the provider")
    void providerCreationAndMapping_shouldReportFailureWhenNothingMapped() {
        when(serviceProvider_ServiceImpl.createProvider(any(java.util.Set.class))).thenReturn(77);
        when(serviceProvider_ServiceImpl.mapProviderStateService(any())).thenReturn(new ArrayList<>());
        when(iemrUserServiceImpl.createUser(any(), anyString())).thenReturn(3117);

        assertSuccessContaining(controller.providerCreationAndMapping(ONBOARD_REQUEST), "false");
    }

    @Test
    @DisplayName("providerCreationAndMapping should report failure when the provider itself is not created")
    void providerCreationAndMapping_shouldReportFailureWhenProviderNotCreated() {
        when(serviceProvider_ServiceImpl.createProvider(any(java.util.Set.class))).thenReturn(0);

        assertSuccessContaining(controller.providerCreationAndMapping(ONBOARD_REQUEST), "false");
    }

    @Test
    @DisplayName("providerCreationAndMapping should answer an error envelope when the service fails")
    void providerCreationAndMapping_shouldAnswerErrorEnvelopeOnFailure() {
        when(serviceProvider_ServiceImpl.createProvider(any(java.util.Set.class)))
                .thenThrow(new IllegalStateException("provider store is unavailable"));

        assertGenericFailure(controller.providerCreationAndMapping(ONBOARD_REQUEST));
    }

    @Test
    @DisplayName("updateProvider should answer the provider the service saved")
    void updateProvider_shouldAnswerSavedProvider() {
        when(serviceProvider_ServiceImpl.getProviderData(77)).thenReturn(provider(77, "old name"));
        when(serviceProvider_ServiceImpl.upDateProviderDetails(any()))
                .thenReturn(provider(77, "Piramal Swasthya"));

        assertSuccessContaining(controller.updateProvider("{\"serviceProviderId\":77,"
                + "\"serviceProviderName\":\"Piramal Swasthya\"}"), "Piramal Swasthya");
    }

    @Test
    @DisplayName("updateProvider should answer an error envelope for a provider that does not exist")
    void updateProvider_shouldAnswerErrorEnvelopeForUnknownProvider() {
        when(serviceProvider_ServiceImpl.getProviderData(77)).thenReturn(null);

        assertCodeException(controller.updateProvider("{\"serviceProviderId\":77}"));
    }

    @Test
    @DisplayName("getServiceLine should answer the service lines the master holds")
    void getServiceLine_shouldAnswerServiceLines() {
        M_ServiceMaster serviceMaster = new M_ServiceMaster();
        serviceMaster.setServiceID(1);
        serviceMaster.setServiceName("Tele Medicine");
        when(m_ServiceMasterInter.getAllServiceLine()).thenReturn(List.of(serviceMaster));

        assertSuccessContaining(controller.getServiceLine("{}"), "Tele Medicine");
    }

    @Test
    @DisplayName("getServiceLine should answer an error envelope when the master cannot be read")
    void getServiceLine_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_ServiceMasterInter.getAllServiceLine()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getServiceLine("{}"));
    }

    @Test
    @DisplayName("getAllProviderName should answer every provider the service holds")
    void getAllProviderName_shouldAnswerEveryProvider() {
        ArrayList<ServiceProvider_Model> providers = new ArrayList<>(List.of(provider(77, "Piramal Swasthya")));
        when(serviceProvider_ServiceImpl.getAllProviderName()).thenReturn(providers);

        assertSuccessContaining(controller.getAllProviderName("{}"), "Piramal Swasthya");
    }

    @Test
    @DisplayName("getAllProviderName should answer an error envelope when the lookup fails")
    void getAllProviderName_shouldAnswerErrorEnvelopeOnFailure() {
        when(serviceProvider_ServiceImpl.getAllProviderName()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllProviderName("{}"));
    }

    @Test
    @DisplayName("getProviderName should report that a name is already taken")
    void getProviderName_shouldReportNameTaken() {
        when(serviceProvider_ServiceImpl.getProviderName("Piramal Swasthya")).thenReturn("Piramal Swasthya");

        assertSuccessContaining(controller.getProviderName("{\"serviceProviderName\":\"Piramal Swasthya\"}"),
                "provider_name_exists");
    }

    @Test
    @DisplayName("getProviderName should report that a name is still free")
    void getProviderName_shouldReportNameFree() {
        when(serviceProvider_ServiceImpl.getProviderName("Piramal Swasthya")).thenReturn(null);

        assertSuccessContaining(controller.getProviderName("{\"serviceProviderName\":\"Piramal Swasthya\"}"),
                "provider_name_doesnt_exist");
    }

    @Test
    @DisplayName("getProviderName should answer an error envelope when the check fails")
    void getProviderName_shouldAnswerErrorEnvelopeOnFailure() {
        when(serviceProvider_ServiceImpl.getProviderName(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getProviderName("{\"serviceProviderName\":\"Piramal Swasthya\"}"));
    }

    @Test
    @DisplayName("getProviderId should answer the mapping the service resolves")
    void getProviderId_shouldAnswerResolvedMapping() {
        M_ProviderServiceMapping mapping = new M_ProviderServiceMapping();
        mapping.setProviderServiceMapID(4001);
        mapping.setServiceProviderID(77);
        when(serviceProvider_ServiceImpl.getProviderserviceMapId(4001)).thenReturn(mapping);

        assertSuccessContaining(controller.getProviderId("{\"providerServiceMapID\":4001}"), "4001");
    }

    @Test
    @DisplayName("getProviderId should answer an error envelope for a mapping that does not exist")
    void getProviderId_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(serviceProvider_ServiceImpl.getProviderserviceMapId(4001)).thenReturn(null);

        assertCodeException(controller.getProviderId("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("createProvider should answer the providers the service stored")
    void createProvider_shouldAnswerStoredProviders() {
        ArrayList<ServiceProvider_Model> stored = new ArrayList<>(List.of(provider(77, "Piramal Swasthya")));
        when(serviceProvider_ServiceImpl.createProvider1(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createProvider("[{\"serviceProviderName\":\"Piramal Swasthya\"}]"),
                "Piramal Swasthya");
    }

    @Test
    @DisplayName("createProvider should refuse an empty batch rather than store nothing quietly")
    void createProvider_shouldRefuseEmptyBatch() {
        assertGenericFailure(controller.createProvider("[]"));
    }

    @Test
    @DisplayName("providerUpdate should answer the provider the service saved")
    void providerUpdate_shouldAnswerSavedProvider() {
        when(serviceProvider_ServiceImpl.getProviderData(77)).thenReturn(provider(77, "old name"));
        when(serviceProvider_ServiceImpl.upDateProviderDetails(any()))
                .thenReturn(provider(77, "Piramal Swasthya"));

        assertSuccessContaining(controller.providerUpdate("{\"serviceProviderId\":77,"
                + "\"serviceProviderName\":\"Piramal Swasthya\",\"modifiedBy\":\"admin\"}"), "Piramal Swasthya");
    }

    @Test
    @DisplayName("providerUpdate should answer an error envelope for a provider that does not exist")
    void providerUpdate_shouldAnswerErrorEnvelopeForUnknownProvider() {
        when(serviceProvider_ServiceImpl.getProviderData(77)).thenReturn(null);

        assertCodeException(controller.providerUpdate("{\"serviceProviderId\":77}"));
    }

    @Test
    @DisplayName("providerDelete should mark the provider deleted and answer what was saved")
    void providerDelete_shouldMarkProviderDeleted() {
        ServiceProvider_Model stored = provider(77, "Piramal Swasthya");
        when(serviceProvider_ServiceImpl.getProviderData(77)).thenReturn(stored);
        when(serviceProvider_ServiceImpl.upDateProviderDetails(stored)).thenReturn(stored);

        assertSuccessContaining(controller.providerDelete("{\"serviceProviderId\":77,\"deleted\":true}"),
                "Piramal Swasthya");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("providerDelete should answer an error envelope for a provider that does not exist")
    void providerDelete_shouldAnswerErrorEnvelopeForUnknownProvider() {
        when(serviceProvider_ServiceImpl.getProviderData(77)).thenReturn(null);

        assertCodeException(controller.providerDelete("{\"serviceProviderId\":77,\"deleted\":true}"));
    }

    @Test
    @DisplayName("updateProviderAdmin should answer the provider the service saved")
    void updateProviderAdmin_shouldAnswerSavedProvider() {
        when(serviceProvider_ServiceImpl.getProviderData(77)).thenReturn(provider(77, "old name"));
        when(serviceProvider_ServiceImpl.upDateProviderDetails(any()))
                .thenReturn(provider(77, "Piramal Swasthya"));

        assertSuccessContaining(controller.updateProviderAdmin("{\"serviceProviderId\":77,"
                + "\"serviceProviderName\":\"Piramal Swasthya\"}"), "Piramal Swasthya");
    }

    @Test
    @DisplayName("updateProviderAdmin should answer an error envelope when the provider cannot be read")
    void updateProviderAdmin_shouldAnswerErrorEnvelopeOnFailure() {
        when(serviceProvider_ServiceImpl.getProviderData(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.updateProviderAdmin("{\"serviceProviderId\":77}"));
    }

    @Test
    @DisplayName("mappingProviderAdmintoProvider should map the admin onto every mapping it is given")
    void mappingProviderAdmintoProvider_shouldMapEveryMapping() {
        M_UserservicerolemappingForRole mapped = new M_UserservicerolemappingForRole();
        mapped.setuSRMappingID(9001);
        mapped.setUserID(3117);
        ArrayList<M_UserservicerolemappingForRole> stored = new ArrayList<>(List.of(mapped));
        when(serviceProvider_ServiceImpl.AddUserRole(anyList())).thenReturn(stored);

        String response = controller.mappingProviderAdmintoProvider(
                "[{\"userID\":3117,\"createdBy\":\"admin\",\"serviceProviderMapID1\":[4001,4002]}]");

        assertSuccessContaining(response, "9001");
        verify(serviceProvider_ServiceImpl).AddUserRole(anyList());
    }

    @Test
    @DisplayName("mappingProviderAdmintoProvider should answer an error envelope when no mappings are named")
    void mappingProviderAdmintoProvider_shouldAnswerErrorEnvelopeWithoutMappings() {
        assertCodeException(controller.mappingProviderAdmintoProvider("[{\"userID\":3117}]"));
    }

    @Test
    @DisplayName("editMappingProviderAdmintoProvider should answer the mapping the service saved")
    void editMappingProviderAdmintoProvider_shouldAnswerSavedMapping() {
        M_UserservicerolemappingForRole stored = new M_UserservicerolemappingForRole();
        stored.setuSRMappingID(9001);
        when(serviceProvider_ServiceImpl.getPADataForEdit(9001)).thenReturn(stored);
        when(serviceProvider_ServiceImpl.insertEditedData(stored)).thenReturn(stored);

        String response = controller.editMappingProviderAdmintoProvider(
                "{\"uSRMappingID\":9001,\"providerServiceMapID\":4002,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "9001");
        assertEquals(4002, stored.getProviderServiceMapID());
    }

    @Test
    @DisplayName("editMappingProviderAdmintoProvider should answer an error envelope for an unknown mapping")
    void editMappingProviderAdmintoProvider_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(serviceProvider_ServiceImpl.getPADataForEdit(9001)).thenReturn(null);

        assertCodeException(controller.editMappingProviderAdmintoProvider("{\"uSRMappingID\":9001}"));
    }

    @Test
    @DisplayName("deleteMappingProviderAdmintoProvider should mark the mapping deleted")
    void deleteMappingProviderAdmintoProvider_shouldMarkMappingDeleted() {
        M_UserservicerolemappingForRole stored = new M_UserservicerolemappingForRole();
        stored.setuSRMappingID(9001);
        when(serviceProvider_ServiceImpl.getPADataForEdit(9001)).thenReturn(stored);
        when(serviceProvider_ServiceImpl.insertEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(
                controller.deleteMappingProviderAdmintoProvider("{\"uSRMappingID\":9001,\"deleted\":true}"), "9001");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteMappingProviderAdmintoProvider should answer an error envelope for an unknown mapping")
    void deleteMappingProviderAdmintoProvider_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(serviceProvider_ServiceImpl.getPADataForEdit(9001)).thenReturn(null);

        assertCodeException(controller.deleteMappingProviderAdmintoProvider("{\"uSRMappingID\":9001}"));
    }

    @Test
    @DisplayName("getMappingProviderAdmintoProvider should answer the provider admins on record")
    void getMappingProviderAdmintoProvider_shouldAnswerProviderAdmins() {
        V_Showprovideradmin admin = new V_Showprovideradmin();
        admin.setuSRMappingID(9001);
        admin.setFirstName("Asha");
        ArrayList<V_Showprovideradmin> admins = new ArrayList<>(List.of(admin));
        when(serviceProvider_ServiceImpl.getProviderAdmins()).thenReturn(admins);

        assertSuccessContaining(controller.getMappingProviderAdmintoProvider("{}"), "Asha");
    }

    @Test
    @DisplayName("getMappingProviderAdmintoProvider should answer an error envelope when the lookup fails")
    void getMappingProviderAdmintoProvider_shouldAnswerErrorEnvelopeOnFailure() {
        when(serviceProvider_ServiceImpl.getProviderAdmins()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getMappingProviderAdmintoProvider("{}"));
    }
}
