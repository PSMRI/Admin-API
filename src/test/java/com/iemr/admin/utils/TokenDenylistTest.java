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

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * The denylist keeps the identifiers of tokens that have been signed out, so a
 * stolen but otherwise valid token stops being accepted.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TokenDenylist Test Suite")
class TokenDenylistTest {

    private static final String JTI = "b0f1c2d3-4e5f-6789-abcd-ef0123456789";
    private static final String KEY = "denied_" + JTI;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private TokenDenylist tokenDenylist;

    @BeforeEach
    void setUp() {
        tokenDenylist = new TokenDenylist();
        ReflectionTestUtils.setField(tokenDenylist, "redisTemplate", redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("addTokenToDenylist")
    class AddTokenTests {

        @Test
        @DisplayName("should store the prefixed key for the lifetime the caller asks for")
        void addTokenToDenylist_shouldStorePrefixedKeyWithExpiry() {
            tokenDenylist.addTokenToDenylist(JTI, 600_000L);

            verify(valueOperations).set(KEY, " ", 600_000L, TimeUnit.MILLISECONDS);
        }

        @Test
        @DisplayName("should ignore a null token id rather than write an orphan key")
        void addTokenToDenylist_shouldIgnoreNullId() {
            tokenDenylist.addTokenToDenylist(null, 600_000L);

            verifyNoInteractions(valueOperations);
        }

        @Test
        @DisplayName("should ignore a blank token id")
        void addTokenToDenylist_shouldIgnoreBlankId() {
            tokenDenylist.addTokenToDenylist("   ", 600_000L);

            verifyNoInteractions(valueOperations);
        }

        @Test
        @DisplayName("should refuse a null expiry rather than denylist a token forever")
        void addTokenToDenylist_shouldRefuseNullExpiry() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                    () -> tokenDenylist.addTokenToDenylist(JTI, null));

            assertEquals("Expiration time must be positive", thrown.getMessage());
            verify(valueOperations, never()).set(anyString(), anyString());
        }

        @Test
        @DisplayName("should refuse an expiry that has already passed")
        void addTokenToDenylist_shouldRefuseNonPositiveExpiry() {
            assertThrows(IllegalArgumentException.class, () -> tokenDenylist.addTokenToDenylist(JTI, 0L));
            assertThrows(IllegalArgumentException.class, () -> tokenDenylist.addTokenToDenylist(JTI, -1L));
        }

        @Test
        @DisplayName("should surface a store failure rather than report a sign-out that did not happen")
        void addTokenToDenylist_shouldSurfaceStoreFailure() {
            doThrow(new IllegalStateException("redis is down"))
                    .when(valueOperations).set(KEY, " ", 600_000L, TimeUnit.MILLISECONDS);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> tokenDenylist.addTokenToDenylist(JTI, 600_000L));

            assertEquals("Failed to denylist token", thrown.getMessage());
        }
    }

    @Nested
    @DisplayName("isTokenDenylisted")
    class IsTokenDenylistedTests {

        @Test
        @DisplayName("should report a token whose key the store still holds")
        void isTokenDenylisted_shouldReportDenylistedToken() {
            when(redisTemplate.hasKey(KEY)).thenReturn(Boolean.TRUE);

            assertTrue(tokenDenylist.isTokenDenylisted(JTI));
        }

        @Test
        @DisplayName("should clear a token whose key the store no longer holds")
        void isTokenDenylisted_shouldClearUnknownToken() {
            when(redisTemplate.hasKey(KEY)).thenReturn(Boolean.FALSE);

            assertFalse(tokenDenylist.isTokenDenylisted(JTI));
        }

        @Test
        @DisplayName("should treat a null token id as not denylisted")
        void isTokenDenylisted_shouldTreatNullIdAsAllowed() {
            assertFalse(tokenDenylist.isTokenDenylisted(null));
        }

        @Test
        @DisplayName("should treat a blank token id as not denylisted")
        void isTokenDenylisted_shouldTreatBlankIdAsAllowed() {
            assertFalse(tokenDenylist.isTokenDenylisted("  "));
        }

        @Test
        @DisplayName("should let requests through rather than block everyone when the store fails")
        void isTokenDenylisted_shouldAllowWhenStoreFails() {
            when(redisTemplate.hasKey(KEY)).thenThrow(new IllegalStateException("redis is down"));

            assertFalse(tokenDenylist.isTokenDenylisted(JTI));
        }
    }
}
