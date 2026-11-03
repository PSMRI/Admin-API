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

import org.json.JSONObject;
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

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.user.M_User;
import com.iemr.admin.service.provideronboard.EncryptUserPassword123;
import com.iemr.admin.utils.http.HttpUtils;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Passwords are never stored by this service itself: they are handed to the
 * common service, which is stood in for here.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Password encryption Test Suite")
class EncryptUserPasswordTest {

    private static final String BASE_URL = "https://common.example.org";

    @Mock
    private HttpUtils httpUtils;

    private HttpUtils originalEmployeeUtils;
    private HttpUtils originalOnboardUtils;

    private EncryptUserPassword service;
    private EncryptUserPassword123 onboardService;

    @BeforeEach
    @DisplayName("Stand in for the common service on both callers")
    void setUp() {
        originalEmployeeUtils = (HttpUtils) ReflectionTestUtils.getField(EncryptUserPassword.class, "utils");
        originalOnboardUtils = (HttpUtils) ReflectionTestUtils.getField(EncryptUserPassword123.class, "utils");
        ReflectionTestUtils.setField(EncryptUserPassword.class, "utils", httpUtils);
        ReflectionTestUtils.setField(EncryptUserPassword123.class, "utils", httpUtils);

        service = new EncryptUserPassword();
        ReflectionTestUtils.setField(service, "commonBaseURL", BASE_URL);
        service.init();

        onboardService = new EncryptUserPassword123();
        ReflectionTestUtils.setField(onboardService, "commonBaseURL", BASE_URL);
        onboardService.init();
    }

    @AfterEach
    @DisplayName("Put the real common service client back so no other suite sees the stand-in")
    void tearDown() {
        ReflectionTestUtils.setField(EncryptUserPassword.class, "utils", originalEmployeeUtils);
        ReflectionTestUtils.setField(EncryptUserPassword123.class, "utils", originalOnboardUtils);
    }

    private static M_User1 employee() {
        M_User1 employee = new M_User1();
        employee.setUserName("asha.rao");
        employee.setPassword("secret");
        return employee;
    }

    private static M_User administrator() {
        M_User administrator = new M_User();
        administrator.setUserName("admin.rao");
        administrator.setPassword("other-secret");
        return administrator;
    }

    @Test
    @DisplayName("init should address the common service under the configured base address")
    void init_shouldAddressConfiguredCommonService() {
        assertTrue(((String) ReflectionTestUtils.getField(service, "encryptPasswordURL")).startsWith(BASE_URL),
                "the encryption address must sit under the configured common service");
    }

    @Test
    @DisplayName("encryptUserCredentials should hand the employee's credentials to the common service")
    void encryptUserCredentials_shouldHandEmployeeCredentialsOver() {
        when(httpUtils.post(anyString(), anyString())).thenReturn("{\"statusCode\":200,\"status\":\"Success\"}");
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);

        OutputResponse response = service.encryptUserCredentials(employee());

        assertNotNull(response);
        verify(httpUtils).post(anyString(), body.capture());
        JSONObject sent = new JSONObject(body.getValue());
        assertEquals("asha.rao", sent.getString("userName"));
        assertEquals("secret", sent.getString("password"));
        assertTrue(sent.getBoolean("isAdmin"), "an employee account created here is created as an administrator");
    }

    @Test
    @DisplayName("encryptUserCredentials should carry back what the common service answered")
    void encryptUserCredentials_shouldCarryBackTheAnswer() {
        when(httpUtils.post(anyString(), anyString()))
                .thenReturn("{\"statusCode\":5000,\"errorMessage\":\"user not found\"}");

        assertEquals(5000, service.encryptUserCredentials(employee()).getStatusCode());
    }

    @Test
    @DisplayName("encryptUserCredentials should give up when the common service answers something unreadable")
    void encryptUserCredentials_shouldGiveUpOnUnreadableAnswer() {
        when(httpUtils.post(anyString(), anyString())).thenReturn("<html>502 Bad Gateway</html>");

        assertThrows(RuntimeException.class, () -> service.encryptUserCredentials(employee()));
    }

    @Test
    @DisplayName("the onboarding caller should hand the administrator's credentials over without the admin flag")
    void onboardingCaller_shouldHandCredentialsOverWithoutAdminFlag() {
        when(httpUtils.post(anyString(), anyString())).thenReturn("{\"statusCode\":200,\"status\":\"Success\"}");
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);

        assertNotNull(onboardService.encryptUserCredentials(administrator()));

        verify(httpUtils).post(anyString(), body.capture());
        JSONObject sent = new JSONObject(body.getValue());
        assertEquals("admin.rao", sent.getString("userName"));
        assertEquals("other-secret", sent.getString("password"));
        assertTrue(!sent.has("isAdmin"), "the onboarding caller does not claim the administrator flag");
    }

    @Test
    @DisplayName("the onboarding caller should give up when the common service answers something unreadable")
    void onboardingCaller_shouldGiveUpOnUnreadableAnswer() {
        when(httpUtils.post(anyString(), anyString())).thenReturn("gateway timeout");

        assertThrows(RuntimeException.class, () -> onboardService.encryptUserCredentials(administrator()));
    }
}
