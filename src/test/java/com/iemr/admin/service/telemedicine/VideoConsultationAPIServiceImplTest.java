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

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.iemr.admin.utils.exception.VideoConsultationException;
import com.iemr.admin.utils.http.HttpUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * The API service is the only thing that talks to the external conferencing
 * platform, so it is checked against a stand-in for that platform's replies.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VideoConsultationAPIServiceImpl Test Suite")
class VideoConsultationAPIServiceImplTest {

    private static final String BASE_URL = "https://conferencing.example.org";
    private static final Long REMOTE_ID = 990011L;

    @Mock
    private HttpUtils httpUtils;

    private HttpUtils originalHttpUtils;

    private VideoConsultationAPIServiceImpl service;

    @BeforeEach
    @DisplayName("Stand in for the external platform and point the service at a stub address")
    void setUp() {
        originalHttpUtils = (HttpUtils) ReflectionTestUtils.getField(VideoConsultationAPIServiceImpl.class,
                "httpUtils");
        ReflectionTestUtils.setField(VideoConsultationAPIServiceImpl.class, "httpUtils", httpUtils);
        service = new VideoConsultationAPIServiceImpl();
        ReflectionTestUtils.setField(service, "videoConsultationAuth", "api-key");
        ReflectionTestUtils.setField(service, "videoConsultationBaseUrl", BASE_URL);
        ReflectionTestUtils.setField(service, "videoConsultationCreateUser",
                "videoConsultation-base-url/api/users");
        ReflectionTestUtils.setField(service, "videoConsultationEditUser", "videoConsultation-base-url/api/users");
    }

    @AfterEach
    @DisplayName("Put the real platform client back so no other suite sees the stand-in")
    void tearDown() {
        ReflectionTestUtils.setField(VideoConsultationAPIServiceImpl.class, "httpUtils", originalHttpUtils);
    }

    private static HashMap<String, String> account() {
        HashMap<String, String> obj = new HashMap<>();
        obj.put("name", "Asha");
        obj.put("email", "asha.rao@example.org");
        return obj;
    }

    @Test
    @DisplayName("createUser should answer the id the platform gave the new account")
    void createUser_shouldAnswerRemoteId() throws Exception {
        when(httpUtils.post(anyString(), anyString(), any()))
                .thenReturn("{\"userid\":990011,\"result\":\"ok\"}");

        assertEquals(REMOTE_ID, service.createUser(account()));
    }

    @Test
    @DisplayName("createUser should send the account to the address the platform is configured at")
    void createUser_shouldSendToConfiguredAddress() throws Exception {
        when(httpUtils.post(anyString(), anyString(), any()))
                .thenReturn("{\"userid\":990011,\"result\":\"ok\"}");
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HashMap<String, Object>> header = ArgumentCaptor.forClass(HashMap.class);

        service.createUser(account());

        org.mockito.Mockito.verify(httpUtils).post(url.capture(), body.capture(), header.capture());
        assertEquals(BASE_URL + "/api/users", url.getValue());
        assertTrue(body.getValue().contains("asha.rao@example.org"), body.getValue());
        assertEquals("api-key", header.getValue().get("X-APIkey-Header"));
        assertEquals("application/json", header.getValue().get("Content-Type"));
    }

    @Test
    @DisplayName("createUser should report the platform's own reason when it refuses the account")
    void createUser_shouldReportRemoteRefusal() {
        when(httpUtils.post(anyString(), anyString(), any()))
                .thenReturn("{\"userid\":0,\"result\":\"email already registered\"}");

        VideoConsultationException refusal = assertThrows(VideoConsultationException.class,
                () -> service.createUser(account()));

        assertEquals("email already registered", refusal.getMessage());
    }

    @Test
    @DisplayName("createUser should give up when the platform answers something that is not an account")
    void createUser_shouldGiveUpOnUnreadableReply() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("service unavailable");

        assertThrows(RuntimeException.class, () -> service.createUser(account()));
    }

    @Test
    @DisplayName("editUser should answer the id the platform confirmed")
    void editUser_shouldAnswerConfirmedId() throws Exception {
        when(httpUtils.put(anyString(), anyString(), any()))
                .thenReturn("{\"userid\":990011,\"result\":\"ok\"}");

        assertEquals(REMOTE_ID, service.editUser(account(), REMOTE_ID, "psmri"));
    }

    @Test
    @DisplayName("editUser should address the account and domain being changed")
    void editUser_shouldAddressAccountAndDomain() throws Exception {
        when(httpUtils.put(anyString(), anyString(), any()))
                .thenReturn("{\"userid\":990011,\"result\":\"ok\"}");
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

        service.editUser(account(), REMOTE_ID, "psmri");

        org.mockito.Mockito.verify(httpUtils).put(url.capture(), anyString(), any());
        assertEquals(BASE_URL + "/api/users/990011/psmri", url.getValue());
    }

    @Test
    @DisplayName("editUser should report the platform's own reason when it refuses the change")
    void editUser_shouldReportRemoteRefusal() {
        when(httpUtils.put(anyString(), anyString(), any()))
                .thenReturn("{\"userid\":0,\"result\":\"account is locked\"}");

        VideoConsultationException refusal = assertThrows(VideoConsultationException.class,
                () -> service.editUser(account(), REMOTE_ID, "psmri"));

        assertEquals("account is locked", refusal.getMessage());
    }

    @Test
    @DisplayName("editUser should give up when the platform answers something that is not an account")
    void editUser_shouldGiveUpOnUnreadableReply() {
        when(httpUtils.put(anyString(), anyString(), any())).thenReturn("gateway timeout");

        assertThrows(RuntimeException.class, () -> service.editUser(account(), REMOTE_ID, "psmri"));
    }

    @Test
    @DisplayName("createUser should still reach the platform when no API key is configured")
    void createUser_shouldReachPlatformWithoutApiKey() throws Exception {
        ReflectionTestUtils.setField(service, "videoConsultationAuth", null);
        when(httpUtils.post(anyString(), anyString(), any()))
                .thenReturn("{\"userid\":990011,\"result\":\"ok\"}");
        ArgumentCaptor<HashMap<String, Object>> header = ArgumentCaptor.forClass(HashMap.class);

        service.createUser(account());

        org.mockito.Mockito.verify(httpUtils).post(anyString(), anyString(), header.capture());
        assertTrue(header.getValue().get("X-APIkey-Header") == null, "no key must be sent when none is configured");
    }
}
