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
package com.iemr.admin.utils.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.utils.exception.IEMRException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RedisSessionException Test Suite")
class RedisSessionExceptionTest {

    private static final String MESSAGE = "Unable to fetch session object from Redis server";

    @Test
    @DisplayName("the message constructor should expose the message through both accessors")
    void messageConstructor_shouldExposeMessage() {
        RedisSessionException exception = new RedisSessionException(MESSAGE);

        assertEquals(MESSAGE, exception.getMessage());
        assertEquals(MESSAGE, exception.toString());
    }

    @Test
    @DisplayName("a Redis session failure should be reportable as an AMRIT exception")
    void redisSessionException_shouldBeAnIemrException() {
        assertTrue(new RedisSessionException(MESSAGE) instanceof IEMRException);
    }

    @Test
    @DisplayName("the cause constructor should adopt the stack trace of the cause without chaining it")
    void causeConstructor_shouldAdoptCauseStackTrace() {
        RuntimeException cause = new RuntimeException("connection refused");
        cause.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("com.iemr.Origin", "connect", "Origin.java", 42) });

        RedisSessionException exception = new RedisSessionException(MESSAGE, cause);

        assertEquals(MESSAGE, exception.getMessage());
        assertArrayEquals(cause.getStackTrace(), exception.getStackTrace());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("the Redis connection marker type should be instantiable")
    void redisConnectionMarker_shouldBeInstantiable() {
        assertNotNull(new RedisConnection());
    }
}
