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
package com.iemr.admin.service.apiman;

import java.util.HashMap;
import java.util.List;

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

import com.iemr.admin.data.apiman.ApimanClient;
import com.iemr.admin.data.apiman.ApimanRegister;
import com.iemr.admin.utils.http.HttpUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The apiman service registers a new service line as a client of the API
 * gateway and signs it up to the API contracts that service line needs.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ApimanServiceImpl Test Suite")
class ApimanServiceImplTest {

    private static final String BASE_URL = "https://gateway.example.org";
    private static final String CLIENT_ID = "client-104";

    @Mock
    private HttpUtils httpUtils;

    private HttpUtils originalHttpUtils;

    private ApimanServiceImpl service;

    @BeforeEach
    @DisplayName("Stand in for the gateway and point the service at stub addresses")
    void setUp() {
        originalHttpUtils = (HttpUtils) ReflectionTestUtils.getField(ApimanServiceImpl.class, "httpUtils");
        ReflectionTestUtils.setField(ApimanServiceImpl.class, "httpUtils", httpUtils);
        service = new ApimanServiceImpl();
        ReflectionTestUtils.setField(service, "apimanBaseURL", BASE_URL);
        ReflectionTestUtils.setField(service, "clientURL", "APIMAN_URL/clients");
        ReflectionTestUtils.setField(service, "contractURL", "APIMAN_URL/clients/CLIENT_ID/contracts");
        ReflectionTestUtils.setField(service, "registerURL", "APIMAN_URL/register");
        ReflectionTestUtils.setField(service, "getClientKey", "APIMAN_URL/clients/CLIENT_ID/apikey");
        ReflectionTestUtils.setField(service, "auth", "Bearer gateway-token");
        ReflectionTestUtils.setField(service, "apimanplanID", "plan");
        ReflectionTestUtils.setField(service, "apimanorgID", "org");
        ReflectionTestUtils.setField(service, "apimanCommonApiID", "common");
        ReflectionTestUtils.setField(service, "apiman1097apiID", "api1097");
        ReflectionTestUtils.setField(service, "apimanMMUapiID", "mmu");
        ReflectionTestUtils.setField(service, "apimanInventoryapiID", "inventory");
        ReflectionTestUtils.setField(service, "apiman104apiID", "api104");
        ReflectionTestUtils.setField(service, "apimanTMapiID", "tm");
        ReflectionTestUtils.setField(service, "apimanSchedulingapiID", "scheduling");
        ReflectionTestUtils.setField(service, "apimanMCTSapiID", "mcts");
    }

    @AfterEach
    @DisplayName("Put the real gateway client back so no other suite sees the stand-in")
    void tearDown() {
        ReflectionTestUtils.setField(ApimanServiceImpl.class, "httpUtils", originalHttpUtils);
    }

    private static ApimanClient client() {
        ApimanClient client = new ApimanClient();
        client.setId(CLIENT_ID);
        client.setName("104 Helpline");
        client.setInitialVersion("1.0");
        return client;
    }

    @Test
    @DisplayName("createClient should answer the client the gateway registered")
    void createClient_shouldAnswerRegisteredClient() throws Exception {
        when(httpUtils.post(anyString(), anyString(), any()))
                .thenReturn("{\"id\":\"client-104\",\"name\":\"104 Helpline\"}");

        ApimanClient registered = service.createClient(client());

        assertEquals(CLIENT_ID, registered.getId());
        assertEquals("104 Helpline", registered.getName());
    }

    @Test
    @DisplayName("createClient should send the client to the gateway with the configured credentials")
    void createClient_shouldSendWithConfiguredCredentials() throws Exception {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{\"id\":\"client-104\"}");
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HashMap<String, Object>> header = ArgumentCaptor.forClass(HashMap.class);

        service.createClient(client());

        verify(httpUtils).post(url.capture(), anyString(), header.capture());
        assertEquals(BASE_URL + "/clients", url.getValue());
        assertEquals("Bearer gateway-token", header.getValue().get("Authorization"));
        assertEquals("application/json", header.getValue().get("Content-Type"));
    }

    @Test
    @DisplayName("createClient should give up when the gateway answers something that is not a client")
    void createClient_shouldGiveUpOnUnreadableReply() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("null");

        assertThrows(RuntimeException.class, () -> service.createClient(client()));
    }

    @Test
    @DisplayName("createClientContract should sign a helpline service line up to its own API as well as the shared one")
    void createClientContract_shouldSignUpHelplineApis() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{}");

        service.createClientContract(1, CLIENT_ID);

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(httpUtils, times(3)).post(anyString(), body.capture(), any());
        assertTrue(body.getAllValues().stream().anyMatch(sent -> sent.contains("api1097")), body.getAllValues()
                .toString());
        assertTrue(body.getAllValues().stream().filter(sent -> sent.contains("common")).count() == 2,
                "both versions of the shared API must be contracted");
    }

    @Test
    @DisplayName("createClientContract should sign a mobile unit service line up to its stock APIs too")
    void createClientContract_shouldSignUpMobileUnitApis() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{}");

        service.createClientContract(2, CLIENT_ID);

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(httpUtils, times(4)).post(anyString(), body.capture(), any());
        List<String> sent = body.getAllValues();
        assertTrue(sent.stream().anyMatch(one -> one.contains("mmu")), sent.toString());
        assertTrue(sent.stream().anyMatch(one -> one.contains("inventory")), sent.toString());
    }

    @Test
    @DisplayName("createClientContract should sign a telemedicine service line up to its scheduling API")
    void createClientContract_shouldSignUpTelemedicineApis() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{}");

        service.createClientContract(4, CLIENT_ID);

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(httpUtils, times(4)).post(anyString(), body.capture(), any());
        assertTrue(body.getAllValues().stream().anyMatch(one -> one.contains("scheduling")),
                body.getAllValues().toString());
    }

    @Test
    @DisplayName("createClientContract should contract only the shared API for a service line with no API of its own")
    void createClientContract_shouldContractOnlySharedApi() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{}");

        service.createClientContract(5, CLIENT_ID);

        verify(httpUtils, times(2)).post(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("createClientContract should contract only the shared API for a service line it does not recognise")
    void createClientContract_shouldContractOnlySharedApiForUnknownServiceLine() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{}");

        service.createClientContract(99, CLIENT_ID);

        verify(httpUtils, times(2)).post(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("createClientContract should address the client whose contracts are being signed")
    void createClientContract_shouldAddressTheClient() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{}");
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

        service.createClientContract(3, CLIENT_ID);

        verify(httpUtils, times(3)).post(url.capture(), anyString(), any());
        assertEquals(BASE_URL + "/clients/" + CLIENT_ID + "/contracts", url.getValue());
    }

    @Test
    @DisplayName("registerClient should publish the registration to the gateway")
    void registerClient_shouldPublishRegistration() {
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{}");
        ApimanRegister registration = new ApimanRegister();
        registration.setType("client");
        registration.setEntityId(CLIENT_ID);
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

        assertNull(service.registerClient(registration), "registration answers nothing back to the caller");
        verify(httpUtils).post(url.capture(), anyString(), any());
        assertEquals(BASE_URL + "/register", url.getValue());
    }

    @Test
    @DisplayName("getClientKey should answer the API key the gateway issued")
    void getClientKey_shouldAnswerIssuedKey() {
        when(httpUtils.get(anyString(), any())).thenReturn("{\"apiKey\":\"key-abc-123\"}");

        assertEquals("key-abc-123", service.getClientKey(CLIENT_ID));
    }

    @Test
    @DisplayName("getClientKey should give up when the gateway does not answer a key")
    void getClientKey_shouldGiveUpWithoutKey() {
        when(httpUtils.get(anyString(), any())).thenReturn("{\"result\":\"unknown client\"}");

        assertThrows(RuntimeException.class, () -> service.getClientKey(CLIENT_ID));
    }

    @Test
    @DisplayName("the gateway should still be reached when no credentials are configured")
    void gatewayCalls_shouldStillBeReachedWithoutCredentials() throws Exception {
        ReflectionTestUtils.setField(service, "auth", null);
        when(httpUtils.post(anyString(), anyString(), any())).thenReturn("{\"id\":\"client-104\"}");
        ArgumentCaptor<HashMap<String, Object>> header = ArgumentCaptor.forClass(HashMap.class);

        service.createClient(client());

        verify(httpUtils).post(anyString(), anyString(), header.capture());
        assertNull(header.getValue().get("Authorization"), "no credentials must be sent when none are configured");
    }
}
