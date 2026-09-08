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
package com.iemr.admin.controller.rolemaster;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.rolemaster.M_Screen;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.RoleMaster;
import com.iemr.admin.data.rolemaster.RoleScreenMapping;
import com.iemr.admin.data.rolemaster.StateServiceMapping;
import com.iemr.admin.repository.rolemaster.RoleScreenMappingRepo;
import com.iemr.admin.service.rolemaster.Role_MasterInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The role master endpoints define what each role may see, so a role saved
 * without its screens leaves its holders locked out of their own work.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RoleMasterController Test Suite")
class RoleMasterControllerTest {

    private static final Integer PROVIDER_ID = 77;
    private static final Integer PSM_ID = 4001;

    @Mock
    private RoleScreenMappingRepo roleScreenMappingRepo;

    @Mock
    private Role_MasterInter roleMasterInter;

    @InjectMocks
    private RoleMasterController controller;

    private static RoleMaster role(Integer id, String name) {
        RoleMaster role = new RoleMaster();
        role.setRoleID(id);
        role.setRoleName(name);
        role.setProviderServiceMapID(PSM_ID);
        role.setCreatedBy("admin");
        return role;
    }

    private static StateServiceMapping stateMapping(Integer psmId) {
        StateServiceMapping mapping = new StateServiceMapping();
        mapping.setProviderServiceMapID(psmId);
        mapping.setServiceProviderID(PROVIDER_ID);
        return mapping;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    private static void assertGenericFailure(String response) {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
    }

    private static void assertCodeException(String response) {
        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("searchRole should answer the states the provider serves")
    void searchRole_shouldAnswerServedStates() {
        when(roleMasterInter.getStateByServiceProviderId(PROVIDER_ID))
                .thenReturn(new ArrayList<>(List.of(stateMapping(PSM_ID))));

        assertSuccessContaining(controller.searchRole("{\"serviceProviderID\":77}"), "4001");
    }

    @Test
    @DisplayName("searchRole should answer an error envelope when the lookup fails")
    void searchRole_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getStateByServiceProviderId(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchRole("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getService should answer the service lines the provider runs in the state")
    void getService_shouldAnswerServiceLinesInState() {
        when(roleMasterInter.getServiceByServiceProviderIdAndStateId(PROVIDER_ID, 29))
                .thenReturn(new ArrayList<>(List.of(stateMapping(PSM_ID))));

        assertSuccessContaining(controller.getService("{\"serviceProviderID\":77,\"stateID\":29}"), "4001");
    }

    @Test
    @DisplayName("getService should answer an error envelope when the lookup fails")
    void getService_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getServiceByServiceProviderIdAndStateId(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getService("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getServiceByProviderId should answer the service lines the user is mapped to")
    void getServiceByProviderId_shouldAnswerMappedServiceLines() {
        M_UserservicerolemappingForRoleProviderAdmin mapping =
                new M_UserservicerolemappingForRoleProviderAdmin();
        mapping.setuSRMappingID(9001);
        when(roleMasterInter.getServiceByServiceProviderIds(3117))
                .thenReturn(new ArrayList<>(List.of(mapping)));

        assertSuccessContaining(controller.getServiceByProviderId("{\"userID\":3117}"), "9001");
    }

    @Test
    @DisplayName("getServiceByProviderId should answer an error envelope when the lookup fails")
    void getServiceByProviderId_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getServiceByServiceProviderIds(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getServiceByProviderId("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("getStateByProviderIdAndServiceID should pass the national flag through to the service")
    void getStateByProviderIdAndServiceID_shouldPassNationalFlagThrough() {
        M_UserservicerolemappingForRoleProviderAdmin mapping =
                new M_UserservicerolemappingForRoleProviderAdmin();
        mapping.setuSRMappingID(9001);
        when(roleMasterInter.getStateByServiceProviderIdAndServiceLines(3117, 3, Boolean.TRUE))
                .thenReturn(new ArrayList<>(List.of(mapping)));

        assertSuccessContaining(controller.getStateByProviderIdAndServiceID(
                "{\"userID\":3117,\"serviceID\":3,\"isNational\":true}"), "9001");
        verify(roleMasterInter).getStateByServiceProviderIdAndServiceLines(3117, 3, Boolean.TRUE);
    }

    @Test
    @DisplayName("getStateByProviderIdAndServiceID should answer an error envelope when the lookup fails")
    void getStateByProviderIdAndServiceID_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getStateByServiceProviderIdAndServiceLines(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getStateByProviderIdAndServiceID("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("getAllRole should resolve the mapping before reading its roles")
    void getAllRole_shouldResolveMappingFirst() {
        when(roleMasterInter.getAllByMapId(PROVIDER_ID, 29, 3, Boolean.FALSE))
                .thenReturn(new ArrayList<>(List.of(stateMapping(PSM_ID))));
        when(roleMasterInter.getProStateServRoles(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(role(11, "Counsellor"))));

        assertSuccessContaining(controller.getAllRole("{\"serviceProviderID\":77,\"stateID\":29,"
                + "\"serviceID\":3,\"isNational\":false}"), "Counsellor");
    }

    @Test
    @DisplayName("getAllRole should fall back to no mapping when the provider has none in the state")
    void getAllRole_shouldFallBackWithoutMapping() {
        when(roleMasterInter.getAllByMapId(any(), any(), any(), any())).thenReturn(new ArrayList<>());
        when(roleMasterInter.getProStateServRoles(0)).thenReturn(new ArrayList<>());

        controller.getAllRole("{\"serviceProviderID\":77}");

        verify(roleMasterInter).getProStateServRoles(0);
    }

    @Test
    @DisplayName("getAllRole should answer an error envelope when the lookup fails")
    void getAllRole_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getAllByMapId(any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllRole("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getAllRoleNew should read the roles straight off the mapping the caller names")
    void getAllRoleNew_shouldReadRolesFromNamedMapping() {
        when(roleMasterInter.getProStateServRoles(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(role(11, "Counsellor"))));

        assertSuccessContaining(controller.getAllRoleNew("{\"providerServiceMapID\":4001}"), "Counsellor");
    }

    @Test
    @DisplayName("getAllRoleNew should answer an error envelope when the lookup fails")
    void getAllRoleNew_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getProStateServRoles(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllRoleNew("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getAllRoles should answer the roles the newer query resolves")
    void getAllRoles_shouldAnswerResolvedRoles() {
        when(roleMasterInter.getProStateServRolesV1(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(role(11, "Counsellor"))));

        assertSuccessContaining(controller.getAllRoles("{\"providerServiceMapID\":4001}"), "Counsellor");
    }

    @Test
    @DisplayName("getAllRoles should answer an error envelope when the lookup fails")
    void getAllRoles_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getProStateServRolesV1(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllRoles("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("saveRole should map each screen the request names onto the role it stored")
    void saveRole_shouldMapScreensOntoStoredRole() {
        when(roleMasterInter.addRole(anyList())).thenReturn(List.of(role(11, "Counsellor")));

        String response = controller.saveRole("[{\"roleName\":\"Counsellor\",\"providerServiceMapID\":4001,"
                + "\"createdBy\":\"admin\",\"screenID\":[21,22]}]");

        assertSuccessContaining(response, "Counsellor");

        ArgumentCaptor<List<RoleScreenMapping>> captor = ArgumentCaptor.forClass(List.class);
        verify(roleScreenMappingRepo).saveAll(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(11, captor.getValue().get(0).getRoleID());
        assertEquals(21, captor.getValue().get(0).getScreenID());
    }

    @Test
    @DisplayName("saveRole should leave the screens unmapped when the counts do not line up")
    void saveRole_shouldLeaveScreensUnmappedOnMismatch() {
        when(roleMasterInter.addRole(anyList()))
                .thenReturn(List.of(role(11, "Counsellor"), role(12, "Supervisor")));

        controller.saveRole("[{\"roleName\":\"Counsellor\",\"screenID\":[21]}]");

        verify(roleScreenMappingRepo, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("saveRole should answer an error envelope when the store fails")
    void saveRole_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.addRole(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveRole("[{\"roleName\":\"Counsellor\"}]"));
    }

    @Test
    @DisplayName("editRole should copy the edits onto the stored role and remap its screen")
    void editRole_shouldCopyEditsAndRemapScreen() {
        RoleMaster stored = role(11, "old name");
        when(roleMasterInter.getRoleByRoleId(11)).thenReturn(stored);
        when(roleMasterInter.modifydata(stored)).thenReturn(stored);
        when(roleMasterInter.settingScreenId(31, 21)).thenReturn("screen mapped");

        String response = controller.editRole("{\"roleID\":11,\"roleName\":\"Counsellor\","
                + "\"roleDesc\":\"Handles counselling calls\",\"sRSMappingID\":31,\"screenID\":21}");

        assertSuccessContaining(response, "screen mapped");
        assertEquals("Counsellor", stored.getRoleName());
        assertEquals("Handles counselling calls", stored.getRoleDesc());
    }

    @Test
    @DisplayName("editRole should answer an error envelope for a role that does not exist")
    void editRole_shouldAnswerErrorEnvelopeForUnknownRole() {
        when(roleMasterInter.getRoleByRoleId(11)).thenReturn(null);

        assertCodeException(controller.editRole("{\"roleID\":11}"));
    }

    @Test
    @DisplayName("deleteRole should mark the role deleted and answer the outcome")
    void deleteRole_shouldMarkRoleDeleted() {
        RoleMaster stored = role(11, "Counsellor");
        when(roleMasterInter.getRoleByRoleId(11)).thenReturn(stored);
        when(roleMasterInter.deletedata(stored)).thenReturn("role deleted");

        assertSuccessContaining(controller.deleteRole("{\"roleID\":11,\"deleted\":true}"), "role deleted");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteRole should answer an error envelope for a role that does not exist")
    void deleteRole_shouldAnswerErrorEnvelopeForUnknownRole() {
        when(roleMasterInter.getRoleByRoleId(11)).thenReturn(null);

        assertCodeException(controller.deleteRole("{\"roleID\":11,\"deleted\":true}"));
    }

    @Test
    @DisplayName("searchFeature should answer the screens the service line offers")
    void searchFeature_shouldAnswerServiceScreens() {
        M_Screen screen = new M_Screen();
        screen.setScreenID(21);
        screen.setScreenName("Call handling");
        when(roleMasterInter.getAllFeature(3)).thenReturn(new ArrayList<>(List.of(screen)));

        assertSuccessContaining(controller.searchFeature("{\"serviceID\":3}"), "Call handling");
    }

    @Test
    @DisplayName("searchFeature should answer an error envelope when the lookup fails")
    void searchFeature_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getAllFeature(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchFeature("{\"serviceID\":3}"));
    }

    @Test
    @DisplayName("deleteFeature should retire the role the screen mapping points at")
    void deleteFeature_shouldRetireMappedRole() {
        RoleMaster stored = role(11, "Counsellor");
        when(roleMasterInter.getRoleByRoleId(11)).thenReturn(stored);
        when(roleMasterInter.deletedata(stored)).thenReturn("role deleted");

        assertSuccessContaining(controller.deleteFeature("{\"roleID\":11,\"screenID\":21}"), "role deleted");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteFeature should answer an error envelope for a role that does not exist")
    void deleteFeature_shouldAnswerErrorEnvelopeForUnknownRole() {
        when(roleMasterInter.getRoleByRoleId(11)).thenReturn(null);

        assertCodeException(controller.deleteFeature("{\"roleID\":11}"));
    }

    @Test
    @DisplayName("getAllRole1 should resolve the mapping before reading its roles")
    void getAllRole1_shouldResolveMappingFirst() {
        when(roleMasterInter.getAllByMapId(PROVIDER_ID, 29, 3, Boolean.FALSE))
                .thenReturn(new ArrayList<>(List.of(stateMapping(PSM_ID))));
        when(roleMasterInter.getProStateServRoles1(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(role(11, "Counsellor"))));

        assertSuccessContaining(controller.getAllRole1("{\"serviceProviderID\":77,\"stateID\":29,"
                + "\"serviceID\":3,\"isNational\":false}"), "Counsellor");
    }

    @Test
    @DisplayName("getAllRole1 should answer an error envelope when the lookup fails")
    void getAllRole1_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getAllByMapId(any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllRole1("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("editRolefeature should answer the screen mappings the service stored")
    void editRolefeature_shouldAnswerStoredMappings() {
        RoleScreenMapping mapping = new RoleScreenMapping();
        mapping.setsRSMappingID(31);
        when(roleMasterInter.mapfeature(anyList())).thenReturn(List.of(mapping));

        assertSuccessContaining(controller.editRolefeature("[{\"roleID\":11,\"screenID\":21}]"), "31");
    }

    @Test
    @DisplayName("editRolefeature should answer an error envelope when the mapping fails")
    void editRolefeature_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.mapfeature(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.editRolefeature("[{\"roleID\":11}]"));
    }

    @Test
    @DisplayName("searchRoleTM should answer the telemedicine roles under the mapping")
    void searchRoleTM_shouldAnswerTelemedicineRoles() {
        when(roleMasterInter.getRoleMasterTM(PSM_ID)).thenReturn(List.of(role(11, "TC Specialist")));

        assertSuccessContaining(controller.searchRoleTM("{\"providerServiceMapID\":4001}"), "TC Specialist");
    }

    @Test
    @DisplayName("searchRoleTM should answer an error envelope when the lookup fails")
    void searchRoleTM_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getRoleMasterTM(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchRoleTM("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getAllRoleActive should answer only the roles still in use")
    void getAllRoleActive_shouldAnswerActiveRoles() {
        when(roleMasterInter.getProStateServRolesActive(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(role(11, "Counsellor"))));

        assertSuccessContaining(controller.getAllRoleActive("{\"providerServiceMapID\":4001}"), "Counsellor");
    }

    @Test
    @DisplayName("getAllRoleActive should answer an error envelope when the lookup fails")
    void getAllRoleActive_shouldAnswerErrorEnvelopeOnFailure() {
        when(roleMasterInter.getProStateServRolesActive(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllRoleActive("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("configWrapUptime should answer the role the service saved")
    void configWrapUptime_shouldAnswerSavedRole() throws Exception {
        RoleMaster stored = role(11, "Counsellor");
        when(roleMasterInter.configWrapUpTime(any())).thenReturn(stored);

        assertSuccessContaining(controller.configWrapUptime(role(11, "Counsellor")), "Counsellor");
    }

    @Test
    @DisplayName("configWrapUptime should answer an error envelope when the change is refused")
    void configWrapUptime_shouldAnswerErrorEnvelopeWhenRefused() throws Exception {
        when(roleMasterInter.configWrapUpTime(any()))
                .thenThrow(new IllegalStateException("wrap up time must be positive"));

        assertGenericFailure(controller.configWrapUptime(role(11, "Counsellor")));
    }
}
