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
package com.iemr.admin.service.user;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.user.M_User;
import com.iemr.admin.data.user.M_UserServiceRoleMapping;
import com.iemr.admin.repository.user.IemrUserRepositoryImplCustom;
import com.iemr.admin.repository.user.M_UserMappingRepo;
import com.iemr.admin.service.provideronboard.EncryptUserPassword123;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The user service turns the loosely typed sign-up form the onboarding screen
 * sends into a stored administrator account, then has its password encrypted.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("IemrUserServiceImpl Test Suite")
class IemrUserServiceImplTest {

    private static final int USER_ID = 3117;

    @Mock
    private EncryptUserPassword123 encryptUserPassword;

    @Mock
    private IemrUserRepositoryImplCustom iemrUserRepositoryImplCustom;

    @Mock
    private M_UserMappingRepo m_UserMappingRepo;

    @InjectMocks
    private IemrUserServiceImpl service;

    private static Map<String, Object> signUpForm() {
        Map<String, Object> form = new HashMap<>();
        form.put("titleID", 1);
        form.put("firstName", "Asha");
        form.put("middleName", "K");
        form.put("lastName", "Rao");
        form.put("genderID", 2);
        form.put("maritalStatusID", "1");
        form.put("aadhaarNo", "1234");
        form.put("aadharNo", "123412341234");
        form.put("panNo", "ABCDE1234F");
        form.put("dob", "1990-05-17T00:00:00+05:30");
        form.put("doj", "2020-01-06T00:00:00+05:30");
        form.put("qualificationID", "4");
        form.put("userName", "asha.rao");
        form.put("password", "secret");
        form.put("emailID", "asha.rao@example.org");
        form.put("emrContactPersion", "Ravi Rao");
        form.put("emrConctactNo", "9000000001");
        form.put("isSupervisor", Boolean.TRUE);
        form.put("deleted", Boolean.FALSE);
        form.put("statusID", 1);
        return form;
    }

    private static ArrayList<Map<String, Object>> formList(Map<String, Object> form) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        list.add(form);
        return list;
    }

    private static M_User storedUser() {
        M_User user = new M_User();
        user.setUserID(USER_ID);
        user.setUserName("asha.rao");
        return user;
    }

    @SuppressWarnings("unchecked")
    private M_User captureSavedUser() {
        ArgumentCaptor<Set<M_User>> captor = ArgumentCaptor.forClass(Set.class);
        verify(iemrUserRepositoryImplCustom).saveAll(captor.capture());
        return captor.getValue().iterator().next();
    }

    @Test
    @DisplayName("createUser should record every detail the sign-up form carried")
    void createUser_shouldRecordEveryFormDetail() {
        when(iemrUserRepositoryImplCustom.saveAll(anySet())).thenReturn(List.of(storedUser()));

        assertEquals(USER_ID, service.createUser(formList(signUpForm()), "admin"));

        M_User saved = captureSavedUser();
        assertEquals(1, saved.getTitleID());
        assertEquals("Asha", saved.getFirstName());
        assertEquals("K", saved.getMiddleName());
        assertEquals("Rao", saved.getLastName());
        assertEquals(2, saved.getGenderID());
        assertEquals(1, saved.getMaritalStatusID());
        assertEquals("123412341234", saved.getAadhaarNo(),
                "the Aadhaar number is read from the differently spelled key the form sends");
        assertEquals("ABCDE1234F", saved.getPAN());
        assertEquals(Timestamp.valueOf("1990-05-17 00:00:00"), saved.getDOB());
        assertEquals(Timestamp.valueOf("2020-01-06 00:00:00"), saved.getDOJ());
        assertEquals(4, saved.getQualificationID());
        assertEquals("asha.rao", saved.getUserName());
        assertEquals("secret", saved.getPassword());
        assertEquals("asha.rao@example.org", saved.getEmailID());
        assertEquals("Ravi Rao", saved.getEmergencyContactPerson());
        assertEquals("9000000001", saved.getEmergencyContactNo());
        assertEquals(Boolean.TRUE, saved.isIsSupervisor());
        assertEquals(false, saved.isDeleted());
        assertEquals(1, saved.getStatusID());
        assertEquals("admin", saved.getCreatedBy());
    }

    @Test
    @DisplayName("createUser should have the stored account's password encrypted")
    void createUser_shouldHavePasswordEncrypted() {
        M_User stored = storedUser();
        when(iemrUserRepositoryImplCustom.saveAll(anySet())).thenReturn(List.of(stored));

        service.createUser(formList(signUpForm()), "admin");

        verify(encryptUserPassword).encryptUserCredentials(stored);
    }

    @Test
    @DisplayName("createUser should record an anonymous author when the caller does not name one")
    void createUser_shouldRecordAnonymousAuthor() {
        when(iemrUserRepositoryImplCustom.saveAll(anySet())).thenReturn(List.of(storedUser()));

        service.createUser(formList(signUpForm()), null);

        assertEquals("", captureSavedUser().getCreatedBy());
    }

    @Test
    @DisplayName("createUser should leave out the details the sign-up form did not carry")
    void createUser_shouldLeaveOutMissingDetails() {
        Map<String, Object> sparse = new HashMap<>();
        sparse.put("userName", "asha.rao");
        when(iemrUserRepositoryImplCustom.saveAll(anySet())).thenReturn(List.of(storedUser()));

        service.createUser(formList(sparse), "admin");

        M_User saved = captureSavedUser();
        assertEquals("asha.rao", saved.getUserName());
        assertNull(saved.getFirstName());
        assertNull(saved.getDOB());
        assertNull(saved.getQualificationID());
    }

    @Test
    @DisplayName("createUser should ignore a date the form sent as a bare day with no time")
    void createUser_shouldIgnoreBareDayDates() {
        Map<String, Object> form = signUpForm();
        form.put("dob", "1990-05-17");
        form.put("doj", "2020-01-06");
        form.put("maritalStatusID", "");
        form.put("qualificationID", "");
        when(iemrUserRepositoryImplCustom.saveAll(anySet())).thenReturn(List.of(storedUser()));

        service.createUser(formList(form), "admin");

        M_User saved = captureSavedUser();
        assertNull(saved.getDOB(), "a date with no time is not a timestamp this service can store");
        assertNull(saved.getDOJ());
        assertNull(saved.getMaritalStatusID());
        assertNull(saved.getQualificationID());
    }

    @Test
    @DisplayName("createUser should answer no account when nothing was stored")
    void createUser_shouldAnswerNoAccountWhenNothingStored() {
        when(iemrUserRepositoryImplCustom.saveAll(anySet())).thenReturn(new ArrayList<M_User>());

        assertEquals(0, service.createUser(formList(signUpForm()), "admin"));
        verify(encryptUserPassword, never()).encryptUserCredentials(any());
    }

    @Test
    @DisplayName("createUser should give up when the account cannot be stored")
    void createUser_shouldGiveUpWhenStorageFails() {
        when(iemrUserRepositoryImplCustom.saveAll(anySet()))
                .thenThrow(new RuntimeException("duplicate user name"));

        assertThrows(RuntimeException.class, () -> service.createUser(formList(signUpForm()), "admin"));
    }

    @Test
    @DisplayName("createUser should give up when the form carries a date it cannot read")
    void createUser_shouldGiveUpOnUnreadableDate() {
        Map<String, Object> form = signUpForm();
        form.put("dob", "the seventeenth of May");

        assertThrows(RuntimeException.class, () -> service.createUser(formList(form), "admin"));
    }

    @Test
    @DisplayName("createUserServiceRoleMapping should give the new administrator a role on every service line named")
    void createRoleMapping_shouldGiveRoleOnEveryServiceLine() {
        when(m_UserMappingRepo.saveAll(anySet()))
                .thenReturn(new ArrayList<>(List.of(new M_UserServiceRoleMapping())));

        assertEquals(1, service.createUserServiceRoleMapping(List.of(4001, 4002), USER_ID, "admin"));

        ArgumentCaptor<Set<M_UserServiceRoleMapping>> captor = ArgumentCaptor.forClass(Set.class);
        verify(m_UserMappingRepo).saveAll(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertTrue(captor.getValue().stream().allMatch(one -> one.getRoleID() == 11),
                "an onboarded administrator always takes the administrator role");
        assertTrue(captor.getValue().stream().allMatch(one -> one.getUserID() == USER_ID));
    }

    @Test
    @DisplayName("createUserServiceRoleMapping should report nothing recorded when no service line was named")
    void createRoleMapping_shouldReportNothingRecordedWithoutServiceLines() {
        when(m_UserMappingRepo.saveAll(anySet())).thenReturn(new ArrayList<M_UserServiceRoleMapping>());

        assertEquals(0, service.createUserServiceRoleMapping(new ArrayList<>(), USER_ID, "admin"));
    }

    @Test
    @DisplayName("createUserServiceRoleMapping should give up when the roles cannot be recorded")
    void createRoleMapping_shouldGiveUpWhenStorageFails() {
        when(m_UserMappingRepo.saveAll(anySet())).thenThrow(new RuntimeException("row is locked"));

        assertThrows(RuntimeException.class,
                () -> service.createUserServiceRoleMapping(List.of(4001), USER_ID, "admin"));
    }

    @Test
    @DisplayName("the collaborators should be replaceable so the service can be wired by hand")
    void collaborators_shouldBeReplaceable() {
        IemrUserServiceImpl standalone = new IemrUserServiceImpl();
        standalone.setIemrUserRepositoryImplCustom(iemrUserRepositoryImplCustom);
        standalone.setM_UserMappingRepo(m_UserMappingRepo);
        when(m_UserMappingRepo.saveAll(anySet()))
                .thenReturn(new ArrayList<>(List.of(new M_UserServiceRoleMapping())));

        assertEquals(1, standalone.createUserServiceRoleMapping(List.of(4001), USER_ID, "admin"));
    }
}
