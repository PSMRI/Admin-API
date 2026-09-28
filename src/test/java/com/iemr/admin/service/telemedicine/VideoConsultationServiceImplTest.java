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
package com.iemr.admin.service.telemedicine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.telemedicine.M_UserTemp;
import com.iemr.admin.data.telemedicine.UserVideoConsultation;
import com.iemr.admin.data.telemedicine.VideoConsultationDomain;
import com.iemr.admin.repo.telemedicine.UserRepo;
import com.iemr.admin.repo.telemedicine.UserVideoConsultationRepo;
import com.iemr.admin.repo.telemedicine.VideoConsultationDomainRepo;
import com.iemr.admin.utils.exception.VideoConsultationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The video consultation service keeps a clinician's account on the external
 * conferencing platform in step with the account this system stores.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VideoConsultationServiceImpl Test Suite")
class VideoConsultationServiceImplTest {

    private static final Long USER_ID = 3117L;
    private static final Long MAP_ID = 8001L;
    private static final Long REMOTE_ID = 990011L;
    private static final Integer PROVIDER_ID = 5;

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserVideoConsultationRepo userVideoConsultationRepo;

    @Mock
    private VideoConsultationDomainRepo videoConsultationDomainRepo;

    @Mock
    private VideoConsultationAPIInter videoConsultationAPIInter;

    @InjectMocks
    private VideoConsultationServiceImpl service;

    private static M_UserTemp clinician() {
        M_UserTemp user = new M_UserTemp();
        user.setUserID(USER_ID);
        user.setFirstName("Asha");
        user.setLastName("Rao");
        user.setUserName("asha.rao");
        return user;
    }

    private static UserVideoConsultation account() {
        UserVideoConsultation account = new UserVideoConsultation();
        account.setUserVideoConsultationMapID(MAP_ID);
        account.setUserID(USER_ID);
        account.setVideoConsultationEmailID("asha.rao@example.org");
        account.setVideoConsultationPassword("secret");
        account.setVideoConsultationDomain("psmri");
        account.setModifiedBy("admin");
        return account;
    }

    @Test
    @DisplayName("getunmappedUser should answer the clinicians who have no account yet")
    void getunmappedUser_shouldAnswerCliniciansWithoutAccount() {
        ArrayList<M_UserTemp> free = new ArrayList<>(List.of(clinician()));
        when(userRepo.getunmappedVideoConsultationUser(PROVIDER_ID, 7)).thenReturn(free);

        assertSame(free, service.getunmappedUser(PROVIDER_ID, 7));
    }

    @Test
    @DisplayName("createUser should open the remote account and record the id it was given")
    void createUser_shouldOpenRemoteAccountAndRecordId() throws Exception {
        UserVideoConsultation request = account();
        when(userRepo.findByUserID(USER_ID)).thenReturn(clinician());
        when(videoConsultationAPIInter.createUser(any())).thenReturn(REMOTE_ID);
        when(userVideoConsultationRepo.save(request)).thenReturn(request);

        assertSame(request, service.createUser(request));
        assertEquals(REMOTE_ID, request.getVideoConsultationID());

        ArgumentCaptor<HashMap<String, String>> captor = ArgumentCaptor.forClass(HashMap.class);
        verify(videoConsultationAPIInter).createUser(captor.capture());
        assertEquals("Asha", captor.getValue().get("name"));
        assertEquals("Rao", captor.getValue().get("surname"));
        assertEquals("asha.rao", captor.getValue().get("member"));
        assertEquals("psmri", captor.getValue().get("domain"));
    }

    @Test
    @DisplayName("createUser should refuse a clinician this system does not know")
    void createUser_shouldRefuseUnknownClinician() throws Exception {
        when(userRepo.findByUserID(USER_ID)).thenReturn(null);

        VideoConsultationException refusal = assertThrows(VideoConsultationException.class,
                () -> service.createUser(account()));

        assertEquals("Invalid User", refusal.getMessage());
        verify(videoConsultationAPIInter, never()).createUser(any());
    }

    @Test
    @DisplayName("createUser should give up when the remote platform refuses the account")
    void createUser_shouldGiveUpWhenRemoteRefuses() throws Exception {
        when(userRepo.findByUserID(USER_ID)).thenReturn(clinician());
        when(videoConsultationAPIInter.createUser(any()))
                .thenThrow(new VideoConsultationException("email already registered"));

        assertThrows(VideoConsultationException.class, () -> service.createUser(account()));
        verify(userVideoConsultationRepo, never()).save(any());
    }

    @Test
    @DisplayName("editUser should record the new sign-in details against the stored account")
    void editUser_shouldRecordNewSignInDetails() throws Exception {
        UserVideoConsultation stored = account();
        stored.setVideoConsultationID(REMOTE_ID);
        stored.setVideoConsultationPassword("old-secret");
        UserVideoConsultation request = account();
        request.setVideoConsultationPassword("new-secret");
        when(userVideoConsultationRepo.findByUserVideoConsultationMapID(MAP_ID)).thenReturn(stored);
        when(userVideoConsultationRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.editUser(request));
        assertEquals("new-secret", stored.getVideoConsultationPassword());
        assertNull(stored.getUser(), "the clinician record must not travel back with the account");
    }

    @Test
    @DisplayName("editUser should leave the remote platform alone when nothing about the sign-in changed")
    void editUser_shouldLeaveRemoteAloneWhenNothingChanged() throws Exception {
        UserVideoConsultation stored = account();
        stored.setVideoConsultationID(REMOTE_ID);
        when(userVideoConsultationRepo.findByUserVideoConsultationMapID(MAP_ID)).thenReturn(stored);
        when(userVideoConsultationRepo.save(stored)).thenReturn(stored);

        service.editUser(account());

        verify(videoConsultationAPIInter, never()).editUser(any(), anyLong(), anyString());
    }

    @Test
    @DisplayName("editUser should open a remote account when the stored one never got an id")
    void editUser_shouldOpenRemoteAccountWhenIdMissing() throws Exception {
        UserVideoConsultation stored = account();
        when(userVideoConsultationRepo.findByUserVideoConsultationMapID(MAP_ID)).thenReturn(stored);
        when(userRepo.findByUserID(USER_ID)).thenReturn(clinician());
        when(videoConsultationAPIInter.createUser(any())).thenReturn(REMOTE_ID);
        when(userVideoConsultationRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.editUser(account()));
        assertEquals(REMOTE_ID, stored.getVideoConsultationID());
    }

    @Test
    @DisplayName("editUser should refuse an account this system does not know")
    void editUser_shouldRefuseUnknownAccount() {
        when(userVideoConsultationRepo.findByUserVideoConsultationMapID(MAP_ID)).thenReturn(null);

        VideoConsultationException refusal = assertThrows(VideoConsultationException.class,
                () -> service.editUser(account()));

        assertEquals("Invalid MapID", refusal.getMessage());
    }

    @Test
    @DisplayName("fetchmappedUser should answer the accounts held under the provider asked for")
    void fetchmappedUser_shouldAnswerAccountsOfProvider() {
        List<UserVideoConsultation> held = List.of(account());
        when(userVideoConsultationRepo.fetchmappedUser(PROVIDER_ID)).thenReturn(held);

        assertSame(held, service.fetchmappedUser(PROVIDER_ID));
    }

    @Test
    @DisplayName("deleteUser should carry the new status to the remote platform")
    void deleteUser_shouldCarryStatusRemotely() throws Exception {
        UserVideoConsultation stored = account();
        stored.setVideoConsultationID(REMOTE_ID);
        when(userVideoConsultationRepo.findByUserVideoConsultationMapID(MAP_ID)).thenReturn(stored);
        when(userVideoConsultationRepo.save(stored)).thenReturn(stored);
        when(videoConsultationAPIInter.editUser(any(), anyLong(), anyString())).thenReturn(REMOTE_ID);

        assertSame(stored, service.deleteUser(MAP_ID, Boolean.TRUE, "supervisor"));
        assertEquals(Boolean.TRUE, stored.getDeleted());
        assertEquals("supervisor", stored.getModifiedBy());

        ArgumentCaptor<HashMap<String, String>> captor = ArgumentCaptor.forClass(HashMap.class);
        verify(videoConsultationAPIInter).editUser(captor.capture(), anyLong(), anyString());
        assertEquals("1", captor.getValue().get("status"));
    }

    @Test
    @DisplayName("deleteUser should carry the reinstated status when the account is brought back")
    void deleteUser_shouldCarryReinstatedStatus() throws Exception {
        UserVideoConsultation stored = account();
        stored.setVideoConsultationID(REMOTE_ID);
        when(userVideoConsultationRepo.findByUserVideoConsultationMapID(MAP_ID)).thenReturn(stored);
        when(userVideoConsultationRepo.save(stored)).thenReturn(stored);

        service.deleteUser(MAP_ID, Boolean.FALSE, "supervisor");

        ArgumentCaptor<HashMap<String, String>> captor = ArgumentCaptor.forClass(HashMap.class);
        verify(videoConsultationAPIInter).editUser(captor.capture(), anyLong(), anyString());
        assertEquals("0", captor.getValue().get("status"));
    }

    @Test
    @DisplayName("deleteUser should give up when the account is unknown")
    void deleteUser_shouldGiveUpForUnknownAccount() {
        when(userVideoConsultationRepo.findByUserVideoConsultationMapID(MAP_ID)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> service.deleteUser(MAP_ID, Boolean.TRUE, "supervisor"));
    }

    @Test
    @DisplayName("getdomain should answer every conferencing domain on file")
    void getdomain_shouldAnswerEveryDomain() {
        List<VideoConsultationDomain> domains = List.of(new VideoConsultationDomain());
        when(videoConsultationDomainRepo.findAll()).thenReturn(domains);

        assertEquals(domains, service.getdomain(PROVIDER_ID));
    }
}
