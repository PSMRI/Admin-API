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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_UserDemographics;
import com.iemr.admin.data.employeemaster.M_UserLangMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.employeemaster.V_Userservicerolemapping;
import com.iemr.admin.data.rolemaster.UserRole;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The write endpoints create and amend employees, their demographics, and the
 * language and role mappings that decide what each of them may do.
 */
@DisplayName("EmployeeMasterController write Test Suite")
class EmployeeMasterWriteControllerTest extends EmployeeMasterFixture {

    private static final String ADD_EMPLOYEE_REQUEST = "{\"firstName\":\"Asha\",\"lastName\":\"Rao\","
            + "\"userName\":\"asha.rao\",\"createdBy\":\"admin\",\"titleID\":1,\"genderID\":1,"
            + "\"fathersName\":\"Ravi\",\"languageID\":[1,2],\"weightage\":[5,3],"
            + "\"canRead\":[true,false],\"canWrite\":[true,false],\"canSpeak\":[true,true],"
            + "\"previleges\":[{\"providerServiceMapID\":4001,\"workingLocationID\":401,\"roleID\":[11,12]}]}";

    private static M_UserLangMapping langMapping(Integer id, Integer languageId) {
        M_UserLangMapping mapping = new M_UserLangMapping();
        mapping.setUserLangID(id);
        mapping.setLanguageID(languageId);
        return mapping;
    }

    @Test
    @DisplayName("addEmployee should store the user, the demographics, the languages and the roles")
    void addEmployee_shouldStoreEveryPartOfTheEmployee() throws Exception {
        when(employeeMasterInter.saveEmployee(any())).thenReturn(3117);
        when(employeeMasterInter.saveDemography(any())).thenReturn(5001);
        when(employeeMasterInter.mapLanguage(anyList())).thenReturn(new ArrayList<>(List.of(langMapping(7001, 1))));
        when(employeeMasterInter.mapRole(anyList(), anyString()))
                .thenReturn(new ArrayList<>(List.of(roleMapping(9001, 3117))));

        assertSuccessContaining(controller.addEmployee(ADD_EMPLOYEE_REQUEST, request), "9001");

        ArgumentCaptor<List<M_UserLangMapping>> languages = ArgumentCaptor.forClass(List.class);
        verify(employeeMasterInter).mapLanguage(languages.capture());
        assertEquals(2, languages.getValue().size(), "each language in the request must be mapped");
        assertEquals(3117, languages.getValue().get(0).getUserID());

        ArgumentCaptor<List<M_UserServiceRoleMapping2>> roles = ArgumentCaptor.forClass(List.class);
        verify(employeeMasterInter).mapRole(roles.capture(), anyString());
        assertEquals(4001, roles.getValue().get(0).getProviderServiceMapID());
    }

    @Test
    @DisplayName("addEmployee should pass the caller's authorization on to the role mapping")
    void addEmployee_shouldPassAuthorizationOn() throws Exception {
        when(employeeMasterInter.saveEmployee(any())).thenReturn(3117);
        when(employeeMasterInter.mapLanguage(anyList())).thenReturn(new ArrayList<>());
        when(employeeMasterInter.mapRole(anyList(), anyString())).thenReturn(new ArrayList<>());

        controller.addEmployee(ADD_EMPLOYEE_REQUEST, request);

        verify(employeeMasterInter).mapRole(anyList(), org.mockito.ArgumentMatchers.eq(AUTH_HEADER));
    }

    @Test
    @DisplayName("addEmployee should answer an error envelope for a request that names no languages")
    void addEmployee_shouldAnswerErrorEnvelopeWithoutLanguages() {
        when(employeeMasterInter.saveEmployee(any())).thenReturn(3117);

        assertCodeException(controller.addEmployee("{\"firstName\":\"Asha\"}", request));
    }

    @Test
    @DisplayName("addEmployee should answer an error envelope when the user cannot be stored")
    void addEmployee_shouldAnswerErrorEnvelopeOnStoreFailure() {
        when(employeeMasterInter.saveEmployee(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.addEmployee(ADD_EMPLOYEE_REQUEST, request));
    }

    @Test
    @DisplayName("editEmployee should copy the edits onto both the user and the demographics")
    void editEmployee_shouldCopyEditsOntoUserAndDemographics() {
        M_User1 stored = user(3117, "old.name");
        M_UserDemographics storedDemographics = demographics(3117, "old father");
        when(employeeMasterInter.editEmployee(3117)).thenReturn(stored);
        when(employeeMasterInter.saveEditData(stored)).thenReturn(stored);
        when(employeeMasterInter.mdedit(3117)).thenReturn(storedDemographics);
        when(employeeMasterInter.saveeditDemo(storedDemographics)).thenReturn(5001);

        String response = controller.editEmployee("{\"userID\":3117,\"firstName\":\"Asha\","
                + "\"userName\":\"asha.rao\",\"fathersName\":\"Ravi\",\"pinCode\":\"560001\"}");

        assertSuccessContaining(response, "5001");
        assertEquals("Asha", stored.getFirstName());
        assertEquals("Ravi", storedDemographics.getFathersName());
        assertEquals("560001", storedDemographics.getPinCode());
    }

    @Test
    @DisplayName("editEmployee should answer an error envelope for a user that does not exist")
    void editEmployee_shouldAnswerErrorEnvelopeForUnknownUser() {
        when(employeeMasterInter.editEmployee(3117)).thenReturn(null);

        assertCodeException(controller.editEmployee("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("deleteEmployee should mark the employee deleted and answer what was saved")
    void deleteEmployee_shouldMarkEmployeeDeleted() {
        M_User1 stored = user(3117, "asha.rao");
        when(employeeMasterInter.editEmployee(3117)).thenReturn(stored);
        when(employeeMasterInter.saveEditData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteEmployee("{\"userID\":3117}"), "asha.rao");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteEmployee should answer an error envelope for a user that does not exist")
    void deleteEmployee_shouldAnswerErrorEnvelopeForUnknownUser() {
        when(employeeMasterInter.editEmployee(3117)).thenReturn(null);

        assertCodeException(controller.deleteEmployee("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("updateEmployee should build one role mapping per role the request names")
    void updateEmployee_shouldBuildOneMappingPerRole() {
        when(employeeMasterInter.mapRoleUpdation(anyList()))
                .thenReturn(new ArrayList<>(List.of(roleMapping(9001, 3117))));

        String response = controller.updateEmployee("{\"userID\":3117,\"roleID1\":[11,12],"
                + "\"providerServiceMapID\":4001,\"workingLocationID\":401,\"createdBy\":\"admin\"}");

        assertSuccessContaining(response, "9001");

        ArgumentCaptor<List<M_UserServiceRoleMapping2>> captor = ArgumentCaptor.forClass(List.class);
        verify(employeeMasterInter).mapRoleUpdation(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(11, captor.getValue().get(0).getRoleID());
        assertEquals(4001, captor.getValue().get(0).getProviderServiceMapID());
    }

    @Test
    @DisplayName("updateEmployee should answer an error envelope when the request names no roles")
    void updateEmployee_shouldAnswerErrorEnvelopeWithoutRoles() {
        assertCodeException(controller.updateEmployee("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("deleteEmployeeRole should mark the role mapping deleted")
    void deleteEmployeeRole_shouldMarkRoleMappingDeleted() {
        M_UserServiceRoleMapping2 stored = roleMapping(9001, 3117);
        when(employeeMasterInter.uRoledelte(9001)).thenReturn(stored);
        when(employeeMasterInter.saveRoleEdit(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteEmployeeRole("{\"uSRMappingID\":9001}"), "9001");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteEmployeeRole should answer an error envelope for a mapping that does not exist")
    void deleteEmployeeRole_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(employeeMasterInter.uRoledelte(9001)).thenReturn(null);

        assertCodeException(controller.deleteEmployeeRole("{\"uSRMappingID\":9001}"));
    }

    @Test
    @DisplayName("usrRoleAndCtiMapping should answer the summary the service reports")
    void usrRoleAndCtiMapping_shouldAnswerSummary() throws Exception {
        when(employeeMasterInter.mapctiAgent(anyList())).thenReturn("2 agents mapped");

        assertSuccessContaining(controller.usrRoleAndCtiMapping("[{\"uSRMappingID\":9001,\"agentID\":\"A-1\"}]"),
                "2 agents mapped");
    }

    @Test
    @DisplayName("usrRoleAndCtiMapping should answer an error envelope when the mapping fails")
    void usrRoleAndCtiMapping_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(employeeMasterInter.mapctiAgent(anyList())).thenThrow(new IllegalStateException("cti is unreachable"));

        assertGenericFailure(controller.usrRoleAndCtiMapping("[{\"uSRMappingID\":9001}]"));
    }

    @Test
    @DisplayName("ResetUserPassword should answer the outcome the service reports")
    void resetUserPassword_shouldAnswerOutcome() {
        when(employeeMasterInter.ResetPassword(any())).thenReturn("password reset");

        assertSuccessContaining(controller.ResetUserPassword("{\"userID\":3117}"), "password reset");
    }

    @Test
    @DisplayName("ResetUserPassword should answer an error envelope when the reset fails")
    void resetUserPassword_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.ResetPassword(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.ResetUserPassword("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("createProviderAdmin should answer the admins the service stored")
    void createProviderAdmin_shouldAnswerStoredAdmins() throws Exception {
        when(employeeMasterInter.createProviderAdmin(anyList()))
                .thenReturn(new ArrayList<>(List.of(user(3117, "pa.user"))));

        assertSuccessContaining(controller.createProviderAdmin("[{\"userName\":\"pa.user\"}]"), "pa.user");
    }

    @Test
    @DisplayName("createProviderAdmin should answer an error envelope when the store fails")
    void createProviderAdmin_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(employeeMasterInter.createProviderAdmin(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createProviderAdmin("[{\"userName\":\"pa.user\"}]"));
    }

    @Test
    @DisplayName("getProviderAdmin should answer every provider admin on record")
    void getProviderAdmin_shouldAnswerEveryAdmin() {
        when(employeeMasterInter.getProviderAdmin()).thenReturn(new ArrayList<>(List.of(user(3117, "pa.user"))));

        assertSuccessContaining(controller.getProviderAdmin("{}"), "pa.user");
    }

    @Test
    @DisplayName("getProviderAdmin should answer an error envelope when the lookup fails")
    void getProviderAdmin_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getProviderAdmin()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getProviderAdmin("{}"));
    }

    @Test
    @DisplayName("editProviderAdmin should copy the edited contact details onto the stored admin")
    void editProviderAdmin_shouldCopyContactDetails() {
        M_User1 stored = user(3117, "pa.user");
        when(employeeMasterInter.getProviderAdminForEdit(3117)).thenReturn(stored);
        when(employeeMasterInter.saveeditedData(stored)).thenReturn(stored);

        String response = controller.editProviderAdmin("{\"userID\":3117,\"firstName\":\"Asha\","
                + "\"emailID\":\"asha@example.org\",\"contactNo\":\"9000000001\",\"remarks\":\"promoted\","
                + "\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "Asha");
        assertEquals("asha@example.org", stored.getEmailID());
        assertEquals("promoted", stored.getRemarks());
    }

    @Test
    @DisplayName("editProviderAdmin should answer an error envelope for an admin that does not exist")
    void editProviderAdmin_shouldAnswerErrorEnvelopeForUnknownAdmin() {
        when(employeeMasterInter.getProviderAdminForEdit(3117)).thenReturn(null);

        assertCodeException(controller.editProviderAdmin("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("deleteProviderAdmin should mark the admin deleted")
    void deleteProviderAdmin_shouldMarkAdminDeleted() {
        M_User1 stored = user(3117, "pa.user");
        when(employeeMasterInter.getProviderAdminForEdit(3117)).thenReturn(stored);
        when(employeeMasterInter.saveeditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteProviderAdmin("{\"userID\":3117,\"deleted\":true}"), "pa.user");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteProviderAdmin should answer an error envelope for an admin that does not exist")
    void deleteProviderAdmin_shouldAnswerErrorEnvelopeForUnknownAdmin() {
        when(employeeMasterInter.getProviderAdminForEdit(3117)).thenReturn(null);

        assertCodeException(controller.deleteProviderAdmin("{\"userID\":3117,\"deleted\":true}"));
    }

    @Test
    @DisplayName("createNewUser should store the demographics and register the user with the call centre")
    void createNewUser_shouldStoreDemographicsAndRegisterUser() throws Exception {
        M_User1 created = user(3117, "asha.rao");
        created.setCreatedBy("admin");
        when(employeeMasterInter.createNewUser(anyList())).thenReturn(new ArrayList<>(List.of(created)));
        when(employeeMasterInter.SaveDemographics(any())).thenReturn(new ArrayList<>());

        String response = controller.createNewUser(
                "[{\"userName\":\"asha.rao\",\"createdBy\":\"admin\",\"fathersName\":\"Ravi\"}]", request);

        assertSuccessContaining(response, "asha.rao");
        verify(employeeMasterInter).createUserInCallCentre(created, AUTH_HEADER);

        ArgumentCaptor<ArrayList<M_UserDemographics>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(employeeMasterInter).SaveDemographics(captor.capture());
        assertEquals("Ravi", captor.getValue().get(0).getFathersName());
        assertEquals(3117, captor.getValue().get(0).getUserID());
    }

    @Test
    @DisplayName("createNewUser should answer an error envelope when the store fails")
    void createNewUser_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(employeeMasterInter.createNewUser(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createNewUser("[{\"userName\":\"asha.rao\"}]", request));
    }

    @Test
    @DisplayName("editUserDetails should copy the edits onto both the user and the demographics")
    void editUserDetails_shouldCopyEditsOntoBoth() {
        M_User1 stored = user(3117, "asha.rao");
        M_UserDemographics storedDemographics = demographics(3117, "old father");
        when(employeeMasterInter.editData(3117)).thenReturn(stored);
        when(employeeMasterInter.saveeditedData(stored)).thenReturn(stored);
        when(employeeMasterInter.DataByUserID(3117)).thenReturn(storedDemographics);
        when(employeeMasterInter.saveeditedDemoData(storedDemographics)).thenReturn(storedDemographics);

        String response = controller.editUserDetails("{\"userID\":3117,\"firstName\":\"Asha\","
                + "\"emailID\":\"asha@example.org\",\"fathersName\":\"Ravi\",\"permPinCode\":\"560002\","
                + "\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "Ravi");
        assertEquals("Asha", stored.getFirstName());
        assertEquals(560002, storedDemographics.getPermPinCode());
    }

    @Test
    @DisplayName("editUserDetails should answer an empty envelope when the user has no demographics on record")
    void editUserDetails_shouldAnswerEmptyEnvelopeWithoutDemographics() {
        M_User1 stored = user(3117, "asha.rao");
        when(employeeMasterInter.editData(3117)).thenReturn(stored);
        when(employeeMasterInter.saveeditedData(stored)).thenReturn(stored);
        when(employeeMasterInter.DataByUserID(3117)).thenReturn(null);

        assertGenericFailure(controller.editUserDetails("{\"userID\":3117,\"firstName\":\"Asha\"}"));
        verify(employeeMasterInter, never()).saveeditedDemoData(any());
    }

    @Test
    @DisplayName("editUserDetails should answer an error envelope for a user that does not exist")
    void editUserDetails_shouldAnswerErrorEnvelopeForUnknownUser() {
        when(employeeMasterInter.editData(3117)).thenReturn(null);

        assertCodeException(controller.editUserDetails("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("deletedUserDetails should release the agent id and expire the session for a deleted user")
    void deletedUserDetails_shouldReleaseAgentIdAndExpireSession() {
        M_User1 stored = user(3117, "asha.rao");
        stored.setAgentID("A-1");
        M_UserDemographics storedDemographics = demographics(3117, "Ravi");
        when(employeeMasterInter.editData(3117)).thenReturn(stored);
        when(employeeMasterInter.saveeditedData(stored)).thenReturn(stored);
        when(employeeMasterInter.DataByUserID(3117)).thenReturn(storedDemographics);
        when(employeeMasterInter.saveeditedDemoData(storedDemographics)).thenReturn(storedDemographics);

        assertSuccessContaining(
                controller.deletedUserDetails("{\"userID\":3117,\"deleted\":true}", request), "Ravi");
        verify(usrAgentMappingService).updateDeletedAgentIDStatus("A-1");
        verify(employeeMasterInter).expireAuth(stored, AUTH_HEADER);
    }

    @Test
    @DisplayName("deletedUserDetails should leave the agent id alone when the user is only reinstated")
    void deletedUserDetails_shouldLeaveAgentIdAloneWhenReinstating() {
        M_User1 stored = user(3117, "asha.rao");
        M_UserDemographics storedDemographics = demographics(3117, "Ravi");
        when(employeeMasterInter.editData(3117)).thenReturn(stored);
        when(employeeMasterInter.saveeditedData(stored)).thenReturn(stored);
        when(employeeMasterInter.DataByUserID(3117)).thenReturn(storedDemographics);
        when(employeeMasterInter.saveeditedDemoData(storedDemographics)).thenReturn(storedDemographics);

        controller.deletedUserDetails("{\"userID\":3117,\"deleted\":false}", request);

        verify(usrAgentMappingService, never()).updateDeletedAgentIDStatus(anyString());
    }

    @Test
    @DisplayName("deletedUserDetails should answer an error envelope for a user that does not exist")
    void deletedUserDetails_shouldAnswerErrorEnvelopeForUnknownUser() {
        when(employeeMasterInter.editData(3117)).thenReturn(null);

        assertCodeException(controller.deletedUserDetails("{\"userID\":3117,\"deleted\":true}", request));
    }

    @Test
    @DisplayName("searchMappedLanguageByUserId should answer the languages mapped to the user")
    void searchMappedLanguageByUserId_shouldAnswerMappedLanguages() {
        when(employeeMasterInter.searchMappedLangugeByUserId(3117))
                .thenReturn(new ArrayList<>(List.of(langMapping(7001, 1))));

        assertSuccessContaining(controller.searchMappedLanguageByUserId("{\"userID\":3117}"), "7001");
    }

    @Test
    @DisplayName("searchMappedLanguageByUserId should answer an error envelope when the lookup fails")
    void searchMappedLanguageByUserId_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.searchMappedLangugeByUserId(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchMappedLanguageByUserId("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("getUserMappedLanguage should answer the languages mapped under the provider")
    void getUserMappedLanguage_shouldAnswerProviderLanguages() {
        when(employeeMasterInter.getMappedLanguge(77))
                .thenReturn(new ArrayList<>(List.of(langMapping(7001, 1))));

        assertSuccessContaining(controller.getUserMappedLanguage("{\"serviceProviderID\":77}"), "7001");
    }

    @Test
    @DisplayName("getUserMappedLanguage should answer an error envelope when the lookup fails")
    void getUserMappedLanguage_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getMappedLanguge(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getUserMappedLanguage("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("UserLangMapping should build one mapping per language in the request")
    void userLangMapping_shouldBuildOneMappingPerLanguage() {
        when(employeeMasterInter.mapLanguage(anyList()))
                .thenReturn(new ArrayList<>(List.of(langMapping(7001, 1))));

        String response = controller.UserLangMapping("[{\"userID\":3117,\"createdBy\":\"admin\","
                + "\"serviceProviderID\":77,\"languageID\":[1,2],\"weightage\":[5,3],"
                + "\"canRead\":[true,false],\"canWrite\":[true,false],\"canSpeak\":[true,true],"
                + "\"weightage_Read\":[5,3],\"weightage_Write\":[5,3],\"weightage_Speak\":[5,3]}]");

        assertSuccessContaining(response, "7001");

        ArgumentCaptor<List<M_UserLangMapping>> captor = ArgumentCaptor.forClass(List.class);
        verify(employeeMasterInter).mapLanguage(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(77, captor.getValue().get(0).getServiceProviderID());
    }

    @Test
    @DisplayName("UserLangMapping should answer an error envelope when the request names no languages")
    void userLangMapping_shouldAnswerErrorEnvelopeWithoutLanguages() {
        assertCodeException(controller.UserLangMapping("[{\"userID\":3117}]"));
    }

    @Test
    @DisplayName("updateUserLanguageMapping should copy the edits onto the stored mapping")
    void updateUserLanguageMapping_shouldCopyEdits() {
        M_UserLangMapping stored = langMapping(7001, 1);
        when(employeeMasterInter.updateLangMapping(7001)).thenReturn(stored);
        when(employeeMasterInter.saveUserLangEditedData(stored)).thenReturn(stored);

        String response = controller.updateUserLanguageMapping("{\"userLangID\":7001,\"userID\":3117,"
                + "\"languageID\":2,\"weightage\":9,\"canRead\":true,\"canWrite\":false,\"canSpeak\":true,"
                + "\"weightage_Read\":9,\"weightage_Write\":1,\"weightage_Speak\":9,\"modifiedBy\":\"admin\"}");

        assertSuccessContaining(response, "7001");
        assertEquals(2, stored.getLanguageID());
        assertEquals(9, stored.getWeightage());
    }

    @Test
    @DisplayName("updateUserLanguageMapping should answer an error envelope for a mapping that does not exist")
    void updateUserLanguageMapping_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(employeeMasterInter.updateLangMapping(7001)).thenReturn(null);

        assertCodeException(controller.updateUserLanguageMapping("{\"userLangID\":7001}"));
    }

    @Test
    @DisplayName("UserLanguageMapping should mark the language mapping deleted")
    void userLanguageMapping_shouldMarkMappingDeleted() {
        M_UserLangMapping stored = langMapping(7001, 1);
        when(employeeMasterInter.updateLangMapping(7001)).thenReturn(stored);
        when(employeeMasterInter.saveUserLangEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.UserLanguageMapping("{\"userLangID\":7001,\"deleted\":true}"), "7001");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("UserLanguageMapping should answer an error envelope for a mapping that does not exist")
    void userLanguageMapping_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(employeeMasterInter.updateLangMapping(7001)).thenReturn(null);

        assertCodeException(controller.UserLanguageMapping("{\"userLangID\":7001,\"deleted\":true}"));
    }

    @Test
    @DisplayName("UserRoleMapping should build one mapping per role under each privilege")
    void userRoleMapping_shouldBuildOneMappingPerRole() throws Exception {
        when(employeeMasterInter.mapRole(anyList(), anyString()))
                .thenReturn(new ArrayList<>(List.of(roleMapping(9001, 3117))));

        String response = controller.UserRoleMapping("[{\"userID\":3117,\"createdBy\":\"admin\","
                + "\"serviceProviderID\":77,\"previleges\":[{\"roleID\":[11,12],"
                + "\"providerServiceMapID\":4001,\"workingLocationID\":401}]}]", request);

        assertSuccessContaining(response, "9001");

        ArgumentCaptor<List<M_UserServiceRoleMapping2>> captor = ArgumentCaptor.forClass(List.class);
        verify(employeeMasterInter).mapRole(captor.capture(), anyString());
        assertEquals(2, captor.getValue().size());
        assertEquals(11, captor.getValue().get(0).getRoleID());
    }

    @Test
    @DisplayName("UserRoleMapping should answer an error envelope when the request names no privileges")
    void userRoleMapping_shouldAnswerErrorEnvelopeWithoutPrivileges() {
        assertCodeException(controller.UserRoleMapping("[{\"userID\":3117}]", request));
    }

    @Test
    @DisplayName("UserRoleMappings should carry the 1097 helpline flags onto the mapping")
    void userRoleMappings_shouldCarryHelplineFlags() throws Exception {
        when(employeeMasterInter.mapRole(anyList(), anyString()))
                .thenReturn(new ArrayList<>(List.of(roleMapping(9001, 3117))));

        String response = controller.UserRoleMappings("[{\"userID\":3117,\"createdBy\":\"admin\","
                + "\"serviceProviderID\":77,\"previleges\":[{\"providerServiceMapID\":4001,"
                + "\"workingLocationID\":401,\"stateID\":29,\"districtID\":301,\"blockID\":401,"
                + "\"blockName\":\"North\",\"facilityID\":501,"
                + "\"ID\":[{\"roleID\":11,\"inbound\":true,\"outbound\":false,"
                + "\"teleConsultation\":\"Y\"}]}]}]", request);

        assertSuccessContaining(response, "9001");

        ArgumentCaptor<List<M_UserServiceRoleMapping2>> captor = ArgumentCaptor.forClass(List.class);
        verify(employeeMasterInter).mapRole(captor.capture(), anyString());
        M_UserServiceRoleMapping2 built = captor.getValue().get(0);
        assertEquals(11, built.getRoleID());
        assertTrue(built.getInbound());
        assertEquals("Y", built.getTeleConsultation());
        assertEquals("North", built.getBlockName());
    }

    @Test
    @DisplayName("UserRoleMappings should answer an error envelope when the request names no privileges")
    void userRoleMappings_shouldAnswerErrorEnvelopeWithoutPrivileges() throws Exception {
        assertCodeException(controller.UserRoleMappings("[{\"userID\":3117}]", request));
    }

    @Test
    @DisplayName("updateUserRoleMapping should cascade the supervisor mappings when the role changes")
    void updateUserRoleMapping_shouldCascadeWhenRoleChanges() throws Exception {
        M_UserServiceRoleMapping2 stored = roleMapping(9001, 3117);
        stored.setRoleID(11);
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(stored);
        when(employeeMasterInter.saveRoleMappingeditedData(any(), anyString())).thenReturn(stored);

        String response = controller.updateUserRoleMapping("{\"uSRMappingID\":9001,\"userID\":3117,"
                + "\"roleID\":12,\"modifiedBy\":\"admin\"}", request);

        assertSuccessContaining(response, "9001");
        verify(employeeMasterInter).cascadeDeleteAshaMappingsForUser(3117);
        assertEquals(12, stored.getRoleID());
    }

    @Test
    @DisplayName("updateUserRoleMapping should cascade the supervisor mappings when the facility changes")
    void updateUserRoleMapping_shouldCascadeWhenFacilityChanges() throws Exception {
        M_UserServiceRoleMapping2 stored = roleMapping(9001, 3117);
        stored.setRoleID(11);
        stored.setFacilityID(501);
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(stored);
        when(employeeMasterInter.saveRoleMappingeditedData(any(), anyString())).thenReturn(stored);

        controller.updateUserRoleMapping("{\"uSRMappingID\":9001,\"userID\":3117,\"roleID\":11,"
                + "\"facilityID\":502}", request);

        verify(employeeMasterInter).cascadeDeleteAshaMappingsForUser(3117);
    }

    @Test
    @DisplayName("updateUserRoleMapping should leave the supervisor mappings alone when nothing changed")
    void updateUserRoleMapping_shouldLeaveSupervisorMappingsAlone() throws Exception {
        M_UserServiceRoleMapping2 stored = roleMapping(9001, 3117);
        stored.setRoleID(11);
        stored.setFacilityID(501);
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(stored);
        when(employeeMasterInter.saveRoleMappingeditedData(any(), anyString())).thenReturn(stored);

        controller.updateUserRoleMapping("{\"uSRMappingID\":9001,\"userID\":3117,\"roleID\":11,"
                + "\"facilityID\":501}", request);

        verify(employeeMasterInter, never()).cascadeDeleteAshaMappingsForUser(anyInt());
    }

    @Test
    @DisplayName("updateUserRoleMapping should carry the optional call flags only when the request sets them")
    void updateUserRoleMapping_shouldCarryOptionalFlagsOnlyWhenSet() throws Exception {
        M_UserServiceRoleMapping2 stored = roleMapping(9001, 3117);
        stored.setRoleID(11);
        stored.setInbound(Boolean.TRUE);
        stored.setTeleConsultation("Y");
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(stored);
        when(employeeMasterInter.saveRoleMappingeditedData(any(), anyString())).thenReturn(stored);

        controller.updateUserRoleMapping("{\"uSRMappingID\":9001,\"userID\":3117,\"roleID\":11}", request);

        assertTrue(stored.getInbound(), "an unset flag must keep the value already on record");
        assertEquals("Y", stored.getTeleConsultation());
    }

    @Test
    @DisplayName("updateUserRoleMapping should answer an error envelope for a mapping that does not exist")
    void updateUserRoleMapping_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(null);

        assertCodeException(controller.updateUserRoleMapping("{\"uSRMappingID\":9001}", request));
    }

    @Test
    @DisplayName("deleteUserRoleMapping should cascade the supervisor mappings before marking it deleted")
    void deleteUserRoleMapping_shouldCascadeBeforeDeleting() throws Exception {
        M_UserServiceRoleMapping2 stored = roleMapping(9001, 3117);
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(stored);
        when(employeeMasterInter.saveRoleMappingeditedData(any(), anyString())).thenReturn(stored);

        assertSuccessContaining(
                controller.deleteUserRoleMapping("{\"uSRMappingID\":9001,\"deleted\":true}", request), "9001");
        verify(employeeMasterInter).cascadeDeleteAshaMappingsForDeactivation(stored);
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteUserRoleMapping should not cascade when the mapping is being reinstated")
    void deleteUserRoleMapping_shouldNotCascadeWhenReinstating() throws Exception {
        M_UserServiceRoleMapping2 stored = roleMapping(9001, 3117);
        stored.setDeleted(Boolean.TRUE);
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(stored);
        when(employeeMasterInter.saveRoleMappingeditedData(any(), anyString())).thenReturn(stored);

        controller.deleteUserRoleMapping("{\"uSRMappingID\":9001,\"deleted\":false}", request);

        verify(employeeMasterInter, never()).cascadeDeleteAshaMappingsForDeactivation(any());
    }

    @Test
    @DisplayName("deleteUserRoleMapping should answer an error envelope for a mapping that does not exist")
    void deleteUserRoleMapping_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(employeeMasterInter.getDataUsrId(9001)).thenReturn(null);

        assertCodeException(controller.deleteUserRoleMapping("{\"uSRMappingID\":9001,\"deleted\":true}", request));
    }

    @Test
    @DisplayName("getUserRoleMapped should answer the mapped roles for the provider")
    void getUserRoleMapped_shouldAnswerMappedRoles() {
        V_Userservicerolemapping mapped = new V_Userservicerolemapping();
        mapped.setuSRMappingID(9001);
        mapped.setName("Asha Rao");
        when(employeeMasterInter.getMappedRole(77)).thenReturn(new ArrayList<>(List.of(mapped)));

        assertSuccessContaining(controller.getUserRoleMapped("{\"serviceProviderID\":77}"), "Asha Rao");
    }

    @Test
    @DisplayName("getUserRoleMapped should answer an error envelope when the lookup fails")
    void getUserRoleMapped_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getMappedRole(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getUserRoleMapped("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchMappedRoleByNameorUserId should answer the roles matching the name or id")
    void searchMappedRoleByNameorUserId_shouldAnswerMatchingRoles() {
        V_Userservicerolemapping mapped = new V_Userservicerolemapping();
        mapped.setuSRMappingID(9001);
        mapped.setName("Asha Rao");
        when(employeeMasterInter.getMappedRole("Asha Rao", 3117)).thenReturn(new ArrayList<>(List.of(mapped)));

        assertSuccessContaining(
                controller.searchMappedRoleByNameorUserId("{\"name\":\"Asha Rao\",\"userID\":3117}"), "Asha Rao");
    }

    @Test
    @DisplayName("searchMappedRoleByNameorUserId should answer an error envelope when the search fails")
    void searchMappedRoleByNameorUserId_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getMappedRole(anyString(), anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchMappedRoleByNameorUserId("{\"name\":\"Asha Rao\",\"userID\":3117}"));
    }

    @Test
    @DisplayName("getUserRoleTM should answer the telemedicine roles the service resolves")
    void getUserRoleTM_shouldAnswerTelemedicineRoles() {
        UserRole role = new UserRole();
        role.setRoleID(11);
        role.setRolename("Specialist");
        when(employeeMasterInter.getUserRoleTM(any())).thenReturn(new ArrayList<>(List.of(role)));

        assertSuccessContaining(controller.getUserRoleTM("{\"userID\":3117}"), "Specialist");
    }

    @Test
    @DisplayName("getUserRoleTM should answer an error envelope when the lookup fails")
    void getUserRoleTM_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getUserRoleTM(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getUserRoleTM("{\"userID\":3117}"));
    }

    @Test
    @DisplayName("deleteUserRoleMappingTM should answer the mapping the service retired")
    void deleteUserRoleMappingTM_shouldAnswerRetiredMapping() throws Exception {
        when(employeeMasterInter.deleteuserrolemapTM(any())).thenReturn(roleMapping(9001, 3117));

        assertSuccessContaining(
                controller.deleteUserRoleMappingTM("{\"uSRMappingID\":9001,\"deleted\":true}", request), "9001");
    }

    @Test
    @DisplayName("deleteUserRoleMappingTM should answer an error envelope when the retirement fails")
    void deleteUserRoleMappingTM_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(employeeMasterInter.deleteuserrolemapTM(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.deleteUserRoleMappingTM("{\"uSRMappingID\":9001}", request));
    }
}
