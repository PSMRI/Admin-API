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
package com.iemr.admin.utils;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.http.AuthorizationHeaderRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * The filter is the service's front door: it lets the monitoring and login
 * endpoints through untouched, turns away calls from origins that are not on the
 * allow list, and rejects anything else that carries no usable JWT.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("JwtUserIdValidationFilter Test Suite")
class JwtUserIdValidationFilterTest {

    private static final String ALLOWED_ORIGINS = "https://amrit.example.org,http://localhost:*";
    private static final String TOKEN = "a.jwt.token";

    @Mock
    private JwtAuthenticationUtil jwtAuthenticationUtil;

    private RecordingChain chain;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private JwtUserIdValidationFilter filter;

    /** Remembers what the filter handed on, so the wrapping can be inspected. */
    private static final class RecordingChain implements FilterChain {
        private ServletRequest passedRequest;
        private int invocations;

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {
            this.passedRequest = servletRequest;
            this.invocations++;
        }
    }

    @BeforeEach
    void setUp() {
        chain = new RecordingChain();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setMethod("POST");
        filter = new JwtUserIdValidationFilter(jwtAuthenticationUtil, ALLOWED_ORIGINS);
    }

    @Nested
    @DisplayName("Monitoring endpoints")
    class MonitoringEndpointTests {

        @Test
        @DisplayName("doFilter should let the health endpoint through without looking at a token")
        void doFilter_shouldPassHealthEndpointThrough() throws Exception {
            request.setMethod("GET");
            request.setRequestURI("/health");

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
            assertSameRequestPassedOn();
            verifyNoInteractions(jwtAuthenticationUtil);
        }

        @Test
        @DisplayName("doFilter should let the version endpoint through without looking at a token")
        void doFilter_shouldPassVersionEndpointThrough() throws Exception {
            request.setMethod("GET");
            request.setRequestURI("/version");

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
            verifyNoInteractions(jwtAuthenticationUtil);
        }

        @Test
        @DisplayName("doFilter should let the monitoring endpoints through even from an unlisted origin")
        void doFilter_shouldPassHealthEndpointThroughForAnyOrigin() throws Exception {
            request.setMethod("GET");
            request.setRequestURI("/health");
            request.addHeader("Origin", "https://attacker.example.net");

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
            assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        }
    }

    @Nested
    @DisplayName("Origin validation")
    class OriginValidationTests {

        @Test
        @DisplayName("doFilter should turn away a request from an origin that is not on the allow list")
        void doFilter_shouldTurnAwayUnlistedOrigin() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Origin", "https://attacker.example.net");

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
            assertEquals(0, chain.invocations);
        }

        @Test
        @DisplayName("doFilter should carry on for a request from an origin on the allow list")
        void doFilter_shouldCarryOnForAllowedOrigin() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Origin", "https://amrit.example.org");
            request.setCookies(new Cookie(Constants.JWT_TOKEN, TOKEN));
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN)).thenReturn(true);

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
        }

        @Test
        @DisplayName("doFilter should accept a wildcard localhost origin from the allow list")
        void doFilter_shouldAcceptWildcardLocalhostOrigin() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Origin", "http://localhost:4200");
            request.setCookies(new Cookie(Constants.JWT_TOKEN, TOKEN));
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN)).thenReturn(true);

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
        }

        @Test
        @DisplayName("doFilter should turn away every origin when no allow list is configured")
        void doFilter_shouldTurnAwayEveryOriginWithoutAnAllowList() throws Exception {
            filter = new JwtUserIdValidationFilter(jwtAuthenticationUtil, "  ");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Origin", "https://amrit.example.org");

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        }

        @Test
        @DisplayName("doFilter should turn away a preflight that names no origin")
        void doFilter_shouldTurnAwayPreflightWithoutOrigin() throws Exception {
            request.setMethod("OPTIONS");
            request.setRequestURI("/zonemaster/get/zones");

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
            assertEquals(0, chain.invocations);
        }

        @Test
        @DisplayName("doFilter should turn away a preflight from an unlisted origin")
        void doFilter_shouldTurnAwayPreflightFromUnlistedOrigin() throws Exception {
            request.setMethod("OPTIONS");
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader("Origin", "https://attacker.example.net");

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        }
    }

    @Nested
    @DisplayName("Public endpoints")
    class PublicEndpointTests {

        @Test
        @DisplayName("doFilter should let the login endpoint through untouched")
        void doFilter_shouldPassLoginThrough() throws Exception {
            request.setRequestURI("/user/userAuthenticate");

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
            assertSameRequestPassedOn();
            verifyNoInteractions(jwtAuthenticationUtil);
        }

        @Test
        @DisplayName("doFilter should let the concurrent-session logout through untouched")
        void doFilter_shouldPassConcurrentLogoutThrough() throws Exception {
            request.setRequestURI("/user/logOutUserFromConcurrentSession");

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
        }

        @Test
        @DisplayName("doFilter should let the swagger UI and its api-docs through untouched")
        void doFilter_shouldPassSwaggerThrough() throws Exception {
            for (String uri : Arrays.asList("/swagger-ui/index.html", "/v3/api-docs", "/public/anything",
                    "/user/refreshToken")) {
                RecordingChain publicChain = new RecordingChain();
                MockHttpServletRequest publicRequest = new MockHttpServletRequest();
                publicRequest.setMethod("GET");
                publicRequest.setRequestURI(uri);

                filter.doFilter(publicRequest, new MockHttpServletResponse(), publicChain);

                assertEquals(1, publicChain.invocations, uri + " must be let through untouched");
            }
            verifyNoInteractions(jwtAuthenticationUtil);
        }
    }

    @Nested
    @DisplayName("Token validation")
    class TokenValidationTests {

        @Test
        @DisplayName("doFilter should carry on with a blanked Authorization header for a valid cookie token")
        void doFilter_shouldCarryOnForValidCookieToken() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.setCookies(new Cookie(Constants.JWT_TOKEN, TOKEN));
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN)).thenReturn(true);

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
            assertTrue(chain.passedRequest instanceof AuthorizationHeaderRequestWrapper,
                    "a validated request must reach the controllers with its Authorization header replaced");
        }

        @Test
        @DisplayName("doFilter should carry on for a valid token supplied in the Jwttoken header")
        void doFilter_shouldCarryOnForValidHeaderToken() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader(Constants.JWT_TOKEN, TOKEN);
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN)).thenReturn(true);

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
            assertTrue(chain.passedRequest instanceof AuthorizationHeaderRequestWrapper);
        }

        @Test
        @DisplayName("doFilter should reject a cookie token the validator does not accept")
        void doFilter_shouldRejectUnacceptedCookieToken() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.setCookies(new Cookie(Constants.JWT_TOKEN, TOKEN));
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN)).thenReturn(false);

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
            assertEquals(0, chain.invocations);
        }

        @Test
        @DisplayName("doFilter should reject a request that carries no token at all")
        void doFilter_shouldRejectRequestWithoutToken() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
            verify(jwtAuthenticationUtil, never()).validateUserIdAndJwtToken(anyString());
        }

        @Test
        @DisplayName("doFilter should reject rather than propagate a failure raised while validating")
        void doFilter_shouldRejectWhenValidationRaises() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.setCookies(new Cookie(Constants.JWT_TOKEN, TOKEN));
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN))
                    .thenThrow(new IEMRException("Validation error: Invalid User ID."));

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
            assertEquals(0, chain.invocations);
        }
    }

    @Nested
    @DisplayName("Mobile clients")
    class MobileClientTests {

        @Test
        @DisplayName("doFilter should carry a mobile call through on its Authorization header alone")
        void doFilter_shouldCarryMobileCallThrough() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader(Constants.USER_AGENT, "okhttp/4.9.3");
            request.addHeader("Authorization", "Bearer session-key");

            filter.doFilter(request, response, chain);

            assertEquals(1, chain.invocations);
            assertSameRequestPassedOn();
            assertNull(UserAgentContext.getUserAgent(), "the user agent must not outlive the request");
        }

        @Test
        @DisplayName("doFilter should reject a mobile call that carries no Authorization header")
        void doFilter_shouldRejectMobileCallWithoutAuthorization() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader(Constants.USER_AGENT, "okhttp/4.9.3");

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        }

        @Test
        @DisplayName("doFilter should reject a browser call that carries only an Authorization header")
        void doFilter_shouldRejectBrowserCallWithOnlyAuthorization() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader(Constants.USER_AGENT, "Mozilla/5.0");
            request.addHeader("Authorization", "Bearer session-key");

            filter.doFilter(request, response, chain);

            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        }
    }

    @Nested
    @DisplayName("userId cookie")
    class UserIdCookieTests {

        @Test
        @DisplayName("doFilter should expire a userId cookie a caller tries to smuggle in")
        void doFilter_shouldExpireUserIdCookie() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.setCookies(new Cookie("userId", "3117"), new Cookie(Constants.JWT_TOKEN, TOKEN));
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN)).thenReturn(true);

            filter.doFilter(request, response, chain);

            Cookie cleared = response.getCookie("userId");
            assertNotNull(cleared, "the smuggled cookie must be sent back expired");
            assertEquals(0, cleared.getMaxAge());
            assertNull(cleared.getValue());
            assertTrue(cleared.isHttpOnly());
            assertTrue(cleared.getSecure());
        }

        @Test
        @DisplayName("doFilter should leave the response cookies alone when the request carries none")
        void doFilter_shouldLeaveCookiesAloneWhenNoneArePresent() throws Exception {
            request.setRequestURI("/zonemaster/get/zones");
            request.addHeader(Constants.JWT_TOKEN, TOKEN);
            when(jwtAuthenticationUtil.validateUserIdAndJwtToken(TOKEN)).thenReturn(true);

            filter.doFilter(request, response, chain);

            assertNull(response.getCookie("userId"));
        }
    }

    private void assertSameRequestPassedOn() {
        assertTrue(chain.passedRequest == request, "the original request must be handed on unwrapped");
    }
}
