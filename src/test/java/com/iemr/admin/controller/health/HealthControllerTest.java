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
package com.iemr.admin.controller.health;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.iemr.admin.service.health.HealthService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Monitoring reads the HTTP status of this endpoint, so a degraded deployment
 * has to stay a 200 while an unreachable one becomes a 503.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HealthController Test Suite")
class HealthControllerTest {

    @Mock
    private HealthService healthService;

    private static Map<String, Object> health(String status) {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", status);
        health.put("checkedAt", "2026-02-17T09:30:00Z");
        return health;
    }

    @Test
    @DisplayName("checkHealth should answer 200 for a deployment that is up")
    void checkHealth_shouldAnswerOkWhenUp() {
        when(healthService.checkHealth()).thenReturn(health("UP"));

        ResponseEntity<Map<String, Object>> response = new HealthController(healthService).checkHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
    }

    @Test
    @DisplayName("checkHealth should answer 200 for a deployment that is degraded but serving")
    void checkHealth_shouldAnswerOkWhenDegraded() {
        when(healthService.checkHealth()).thenReturn(health("DEGRADED"));

        ResponseEntity<Map<String, Object>> response = new HealthController(healthService).checkHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DEGRADED", response.getBody().get("status"));
    }

    @Test
    @DisplayName("checkHealth should answer 503 for a deployment that is down")
    void checkHealth_shouldAnswerUnavailableWhenDown() {
        when(healthService.checkHealth()).thenReturn(health("DOWN"));

        ResponseEntity<Map<String, Object>> response = new HealthController(healthService).checkHealth();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    @DisplayName("checkHealth should answer 503 rather than propagate a failure of the check itself")
    void checkHealth_shouldAnswerUnavailableWhenCheckFails() {
        when(healthService.checkHealth()).thenThrow(new IllegalStateException("diagnostics unavailable"));

        ResponseEntity<Map<String, Object>> response = new HealthController(healthService).checkHealth();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("DOWN", response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}
