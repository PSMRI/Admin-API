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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.iemr.admin.data.user.M_User;
import com.iemr.admin.repository.user.UserLoginRepo;
import com.iemr.admin.utils.exception.IEMRException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("JwtAuthenticationUtil Test Suite")
class JwtAuthenticationUtilTest {

    private static final String TOKEN = "a.jwt.token";
    private static final String USER_ID = "3117";

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private UserLoginRepo userLoginRepo;

    @Mock
    private Claims claims;

    @Mock
    private HttpServletRequest request;

    private JwtAuthenticationUtil authenticationUtil;

    @BeforeEach
    void setUp() {
        authenticationUtil = new JwtAuthenticationUtil(cookieUtil, jwtUtil);
        ReflectionTestUtils.setField(authenticationUtil, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(authenticationUtil, "userLoginRepo", userLoginRepo);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("validateJwtToken should answer the subject when the cookie carries a valid token")
    void validateJwtToken_shouldAnswerSubjectForValidToken() {
        when(cookieUtil.getCookieValue(request, "Jwttoken")).thenReturn(Optional.of(TOKEN));
        when(jwtUtil.validateToken(TOKEN)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("dr.mehta");

        ResponseEntity<String> response = authenticationUtil.validateJwtToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("dr.mehta", response.getBody());
    }

    @Test
    @DisplayName("validateJwtToken should refuse a request that carries no token cookie")
    void validateJwtToken_shouldRefuseWhenCookieAbsent() {
        when(cookieUtil.getCookieValue(request, "Jwttoken")).thenReturn(Optional.empty());

        ResponseEntity<String> response = authenticationUtil.validateJwtToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error 401: Unauthorized - JWT Token is not set!", response.getBody());
    }

    @Test
    @DisplayName("validateJwtToken should refuse a token the validator rejects")
    void validateJwtToken_shouldRefuseInvalidToken() {
        when(cookieUtil.getCookieValue(request, "Jwttoken")).thenReturn(Optional.of(TOKEN));
        when(jwtUtil.validateToken(TOKEN)).thenReturn(null);

        ResponseEntity<String> response = authenticationUtil.validateJwtToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error 401: Unauthorized - Invalid JWT Token!", response.getBody());
    }

    @Test
    @DisplayName("validateJwtToken should refuse a valid token that names no subject")
    void validateJwtToken_shouldRefuseTokenWithoutSubject() {
        when(cookieUtil.getCookieValue(request, "Jwttoken")).thenReturn(Optional.of(TOKEN));
        when(jwtUtil.validateToken(TOKEN)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(null);

        ResponseEntity<String> response = authenticationUtil.validateJwtToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error 401: Unauthorized - Username is missing!", response.getBody());
    }

    @Test
    @DisplayName("validateJwtToken should refuse a valid token whose subject is blank")
    void validateJwtToken_shouldRefuseTokenWithBlankSubject() {
        when(cookieUtil.getCookieValue(request, "Jwttoken")).thenReturn(Optional.of(TOKEN));
        when(jwtUtil.validateToken(TOKEN)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("");

        ResponseEntity<String> response = authenticationUtil.validateJwtToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error 401: Unauthorized - Username is missing!", response.getBody());
    }

    @Test
    @DisplayName("validateUserIdAndJwtToken should accept a user the cache already holds")
    void validateUserIdAndJwtToken_shouldAcceptCachedUser() throws IEMRException {
        M_User cached = new M_User();
        cached.setUserID(3117);
        when(jwtUtil.validateToken(TOKEN)).thenReturn(claims);
        when(claims.get("userId", String.class)).thenReturn(USER_ID);
        when(valueOperations.get("user_" + USER_ID)).thenReturn(cached);

        assertTrue(authenticationUtil.validateUserIdAndJwtToken(TOKEN));
        verify(userLoginRepo, never()).findByUserID(anyInt());
    }

    @Test
    @DisplayName("validateUserIdAndJwtToken should fall back to the database and cache what it finds")
    void validateUserIdAndJwtToken_shouldFallBackToDatabaseAndCache() throws IEMRException {
        M_User stored = new M_User();
        stored.setUserID(3117);
        stored.setUserName("dr.mehta");
        when(jwtUtil.validateToken(TOKEN)).thenReturn(claims);
        when(claims.get("userId", String.class)).thenReturn(USER_ID);
        when(valueOperations.get("user_" + USER_ID)).thenReturn(null);
        when(userLoginRepo.findByUserID(3117)).thenReturn(stored);

        assertTrue(authenticationUtil.validateUserIdAndJwtToken(TOKEN));
        verify(valueOperations).set(eq("user_" + USER_ID), any(M_User.class), anyLong(), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("validateUserIdAndJwtToken should reject a token the validator does not accept")
    void validateUserIdAndJwtToken_shouldRejectInvalidToken() {
        when(jwtUtil.validateToken(TOKEN)).thenReturn(null);

        IEMRException thrown = assertThrows(IEMRException.class,
                () -> authenticationUtil.validateUserIdAndJwtToken(TOKEN));
        assertTrue(thrown.getMessage().contains("Invalid JWT token."), thrown.getMessage());
    }

    @Test
    @DisplayName("validateUserIdAndJwtToken should reject a user neither the cache nor the database knows")
    void validateUserIdAndJwtToken_shouldRejectUnknownUser() {
        when(jwtUtil.validateToken(TOKEN)).thenReturn(claims);
        when(claims.get("userId", String.class)).thenReturn(USER_ID);
        when(valueOperations.get("user_" + USER_ID)).thenReturn(null);
        when(userLoginRepo.findByUserID(3117)).thenReturn(null);

        IEMRException thrown = assertThrows(IEMRException.class,
                () -> authenticationUtil.validateUserIdAndJwtToken(TOKEN));
        assertTrue(thrown.getMessage().contains("Invalid User ID."), thrown.getMessage());
    }

    @Test
    @DisplayName("validateUserIdAndJwtToken should surface a non-numeric user id as a validation failure")
    void validateUserIdAndJwtToken_shouldRejectNonNumericUserId() {
        when(jwtUtil.validateToken(TOKEN)).thenReturn(claims);
        when(claims.get("userId", String.class)).thenReturn("not-a-number");
        when(valueOperations.get(anyString())).thenReturn(null);

        assertThrows(IEMRException.class, () -> authenticationUtil.validateUserIdAndJwtToken(TOKEN));
    }
}
