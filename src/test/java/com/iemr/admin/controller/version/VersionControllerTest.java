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
package com.iemr.admin.controller.version;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The version endpoint tells an operator which build is deployed, and must
 * answer something rather than fail when the build stamp is missing.
 */
@DisplayName("VersionController Test Suite")
class VersionControllerTest {

    private final VersionController controller = new VersionController();

    @Test
    @DisplayName("versionInformation should answer the build stamp fields the deployment records")
    void versionInformation_shouldAnswerBuildStampFields() {
        ResponseEntity<Map<String, String>> response = controller.versionInformation();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("buildTimestamp"));
        assertTrue(response.getBody().containsKey("version"));
        assertTrue(response.getBody().containsKey("branch"));
        assertTrue(response.getBody().containsKey("commitHash"));
    }

    @Test
    @DisplayName("versionInformation should answer the same stamp on every call")
    void versionInformation_shouldAnswerConsistently() {
        assertEquals(controller.versionInformation().getBody(), controller.versionInformation().getBody());
    }
}
