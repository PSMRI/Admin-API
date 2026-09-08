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
package com.iemr.admin.utils.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.iemr.admin.utils.redis.RedisSessionException;
import com.iemr.admin.utils.redis.RedisStorage;
import com.iemr.admin.utils.sessionobject.SessionObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("HTTPRequestInterceptor Test Suite")
class HTTPRequestInterceptorTest {

    private static final String SESSION_KEY = "0d5f3a7c-2b91-4e6d-8f10-3a4b5c6d7e8f";
    private static final String SESSION_PAYLOAD = "{\"userName\":\"amrit-admin\"}";

    @Mock
    private RedisStorage redisStorage;

    @Mock
    private SessionObject sessionObject;

    @InjectMocks
    private HTTPRequestInterceptor interceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    @DisplayName("Prepare a request and response before each test")
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Nested
    @DisplayName("preHandle")
    class PreHandleTests {

        @Test
        @DisplayName("preHandle should admit a request whose session key resolves in Redis")
        void preHandle_shouldAdmitRequestWithResolvableSessionKey() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);
            when(sessionObject.getSessionObject(SESSION_KEY)).thenReturn(SESSION_PAYLOAD);

            assertTrue(interceptor.preHandle(request, response, new Object()));
        }

        @Test
        @DisplayName("preHandle should strip the Bearer prefix before resolving the session key")
        void preHandle_shouldStripBearerPrefix() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", "Bearer " + SESSION_KEY);
            when(sessionObject.getSessionObject(SESSION_KEY)).thenReturn(SESSION_PAYLOAD);

            assertTrue(interceptor.preHandle(request, response, new Object()));
            verify(sessionObject).getSessionObject(SESSION_KEY);
        }

        @Test
        @DisplayName("preHandle should reject a request whose session key is unknown to Redis")
        void preHandle_shouldRejectUnknownSessionKey() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);
            when(sessionObject.getSessionObject(SESSION_KEY)).thenReturn(null);

            assertFalse(interceptor.preHandle(request, response, new Object()));
            assertTrue(response.getContentAsString().contains("5000"), response.getContentAsString());
        }

        @Test
        @DisplayName("preHandle should reject a request whose session lookup fails")
        void preHandle_shouldRejectWhenSessionLookupFails() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);
            when(sessionObject.getSessionObject(SESSION_KEY))
                    .thenThrow(new RedisSessionException("Unable to fetch session object from Redis server"));

            assertFalse(interceptor.preHandle(request, response, new Object()));
            assertNull(response.getHeader("Access-Control-Allow-Origin"),
                    "no allow-list is configured, so no origin may be echoed back");
        }

        @Test
        @DisplayName("preHandle should echo an allowed origin on the rejection so the browser can read it")
        void preHandle_shouldEchoAllowedOriginOnRejection() throws Exception {
            ReflectionTestUtils.setField(interceptor, "allowedOrigins", "https://amrit.example.org");
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);
            request.addHeader("Origin", "https://amrit.example.org");
            when(sessionObject.getSessionObject(SESSION_KEY)).thenReturn(null);

            assertFalse(interceptor.preHandle(request, response, new Object()));
            assertEquals("https://amrit.example.org", response.getHeader("Access-Control-Allow-Origin"));
            assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"));
        }

        @Test
        @DisplayName("preHandle should withhold the CORS headers from a rejection for an unlisted origin")
        void preHandle_shouldWithholdCorsHeadersForUnlistedOrigin() throws Exception {
            ReflectionTestUtils.setField(interceptor, "allowedOrigins", "https://amrit.example.org");
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);
            request.addHeader("Origin", "https://attacker.example.net");
            when(sessionObject.getSessionObject(SESSION_KEY)).thenReturn(null);

            assertFalse(interceptor.preHandle(request, response, new Object()));
            assertNull(response.getHeader("Access-Control-Allow-Origin"));
        }

        @Test
        @DisplayName("preHandle should match a wildcard localhost entry in the allow list")
        void preHandle_shouldMatchWildcardLocalhostOrigin() throws Exception {
            ReflectionTestUtils.setField(interceptor, "allowedOrigins", "http://localhost:*");
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);
            request.addHeader("Origin", "http://localhost:4200");
            when(sessionObject.getSessionObject(SESSION_KEY)).thenReturn(null);

            assertFalse(interceptor.preHandle(request, response, new Object()));
            assertEquals("http://localhost:4200", response.getHeader("Access-Control-Allow-Origin"));
        }

        @Test
        @DisplayName("preHandle should pass a request without an Authorization header on to the security filters")
        void preHandle_shouldPassRequestWithoutAuthorizationOn() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");

            assertTrue(interceptor.preHandle(request, response, new Object()));
            verify(sessionObject, never()).getSessionObject(anyString());
        }

        @Test
        @DisplayName("preHandle should pass a request with a blank Authorization header on unchecked")
        void preHandle_shouldPassBlankAuthorizationOn() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", "");

            assertTrue(interceptor.preHandle(request, response, new Object()));
            verify(sessionObject, never()).getSessionObject(anyString());
        }

        @Test
        @DisplayName("preHandle should admit the unauthenticated health and version endpoints")
        void preHandle_shouldAdmitPublicEndpoints() throws Exception {
            request.setMethod("GET");
            request.setRequestURI("/health");
            assertTrue(interceptor.preHandle(request, response, new Object()));

            MockHttpServletRequest versionRequest = new MockHttpServletRequest();
            versionRequest.setMethod("GET");
            versionRequest.setRequestURI("/version");
            assertTrue(interceptor.preHandle(versionRequest, new MockHttpServletResponse(), new Object()));

            verify(sessionObject, never()).getSessionObject(anyString());
        }

        @Test
        @DisplayName("preHandle should admit the swagger UI without checking a session")
        void preHandle_shouldAdmitSwaggerUi() throws Exception {
            request.setMethod("GET");
            request.setRequestURI("/swagger-ui/index.html");

            assertTrue(interceptor.preHandle(request, response, new Object()));
            verify(sessionObject, never()).getSessionObject(anyString());
        }

        @Test
        @DisplayName("preHandle should admit the swagger support resources on a session key")
        void preHandle_shouldAdmitSwaggerSupportResources() throws Exception {
            for (String uri : new String[] { "/v3/api-docs", "/swagger-resources", "/swagger-config",
                    "/ui", "/index.html", "/swagger-initializer.js" }) {
                MockHttpServletRequest swaggerRequest = new MockHttpServletRequest();
                swaggerRequest.setMethod("GET");
                swaggerRequest.setRequestURI(uri);
                swaggerRequest.addHeader("Authorization", SESSION_KEY);

                assertTrue(interceptor.preHandle(swaggerRequest, new MockHttpServletResponse(), new Object()),
                        uri + " must be admitted without a session lookup");
            }
            verify(sessionObject, never()).getSessionObject(anyString());
        }

        @Test
        @DisplayName("preHandle should reject the error endpoint")
        void preHandle_shouldRejectTheErrorEndpoint() throws Exception {
            request.setMethod("GET");
            request.setRequestURI("/error");
            request.addHeader("Authorization", SESSION_KEY);

            assertFalse(interceptor.preHandle(request, response, new Object()));
        }

        @Test
        @DisplayName("preHandle should admit an OPTIONS preflight without checking a session")
        void preHandle_shouldAdmitOptionsPreflight() throws Exception {
            request.setMethod("OPTIONS");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);

            assertTrue(interceptor.preHandle(request, response, new Object()));
            verify(sessionObject, never()).getSessionObject(anyString());
        }
    }

    @Nested
    @DisplayName("postHandle")
    class PostHandleTests {

        @Test
        @DisplayName("postHandle should refresh the session it resolved for the request")
        void postHandle_shouldRefreshTheSession() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", "Bearer " + SESSION_KEY);
            when(sessionObject.getSessionObject(SESSION_KEY)).thenReturn(SESSION_PAYLOAD);

            interceptor.postHandle(request, response, new Object(), new ModelAndView());

            verify(sessionObject).updateSessionObject(SESSION_KEY, SESSION_PAYLOAD);
        }

        @Test
        @DisplayName("postHandle should do nothing when the request carried no Authorization header")
        void postHandle_shouldDoNothingWithoutAuthorization() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");

            interceptor.postHandle(request, response, new Object(), new ModelAndView());

            verify(sessionObject, never()).updateSessionObject(anyString(), anyString());
        }

        @Test
        @DisplayName("postHandle should swallow a session refresh failure")
        void postHandle_shouldSwallowRefreshFailure() throws Exception {
            request.setMethod("POST");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Authorization", SESSION_KEY);
            when(sessionObject.getSessionObject(SESSION_KEY))
                    .thenThrow(new RedisSessionException("Unable to fetch session object from Redis server"));

            interceptor.postHandle(request, response, new Object(), new ModelAndView());

            verify(sessionObject, never()).updateSessionObject(anyString(), anyString());
        }
    }

    @Test
    @DisplayName("afterCompletion should complete without touching the session store")
    void afterCompletion_shouldCompleteWithoutTouchingTheSessionStore() throws Exception {
        request.setMethod("POST");
        request.setRequestURI("/zonemaster/get/zones");

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(sessionObject, never()).updateSessionObject(anyString(), anyString());
    }
}
