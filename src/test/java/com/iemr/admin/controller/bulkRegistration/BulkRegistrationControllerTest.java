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
package com.iemr.admin.controller.bulkRegistration;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.iemr.admin.data.bulkuser.BulkRegistrationError;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepoo;
import com.iemr.admin.service.bulkRegistration.BulkRegistrationService;
import com.iemr.admin.service.bulkRegistration.BulkRegistrationServiceImpl;
import com.iemr.admin.service.bulkRegistration.EmployeeXmlService;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * The bulk registration endpoint reports how much of an uploaded sheet was
 * accepted, and hands the rejected rows back as a spreadsheet the uploader can
 * correct and resubmit.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("BulkRegistrationController Test Suite")
class BulkRegistrationControllerTest {

    @Mock
    private EmployeeXmlService employeeXmlService;

    @Spy
    private BulkRegistrationServiceImpl bulkRegistrationServiceimpl;

    @Mock
    private BulkRegistrationService bulkRegistrationService;

    @Mock
    private EmployeeMasterRepoo employeeMasterRepoo;

    @Mock
    private LocationMasterServiceInter locationMasterServiceInter;

    @InjectMocks
    private BulkRegistrationController controller;

    @Test
    @DisplayName("registerBulkUser should report how many rows were accepted and what was rejected")
    void registerBulkUser_shouldReportAcceptedAndRejectedRows() throws Exception {
        doAnswer(call -> {
            bulkRegistrationServiceimpl.totalEmployeeListSize = 3;
            bulkRegistrationServiceimpl.m_bulkUser.add(new M_User1());
            bulkRegistrationServiceimpl.m_bulkUser.add(new M_User1());
            bulkRegistrationServiceimpl.errorLogs.add("Row 3: Title is missing.");
            return null;
        }).when(bulkRegistrationService).registerBulkUser(anyString(), anyString(), anyString(), anyInt());

        ResponseEntity<Map<String, Object>> response = controller.registerBulkUser(
                "<EmployeeList/>", "auth", "admin", new MockHttpServletRequest(), 77);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody().get("status"));
        assertEquals(200, response.getBody().get("statusCode"));
        assertEquals(3, response.getBody().get("totalUser"));
        assertEquals(2, response.getBody().get("registeredUser"));
        assertTrue(response.getBody().get("error").toString().contains("Title is missing."));
    }

    @Test
    @DisplayName("registerBulkUser should clear the accumulated state so the next upload starts clean")
    void registerBulkUser_shouldClearAccumulatedState() throws Exception {
        doAnswer(call -> {
            bulkRegistrationServiceimpl.totalEmployeeListSize = 1;
            bulkRegistrationServiceimpl.m_bulkUser.add(new M_User1());
            bulkRegistrationServiceimpl.errorLogs.add("Row 1: Title is missing.");
            return null;
        }).when(bulkRegistrationService).registerBulkUser(anyString(), anyString(), anyString(), anyInt());

        controller.registerBulkUser("<EmployeeList/>", "auth", "admin", new MockHttpServletRequest(), 77);

        assertTrue(bulkRegistrationServiceimpl.m_bulkUser.isEmpty());
        assertTrue(bulkRegistrationServiceimpl.errorLogs.isEmpty());
        assertEquals(0, bulkRegistrationServiceimpl.totalEmployeeListSize);
    }

    @Test
    @DisplayName("registerBulkUser should discard the rejected rows of a previous upload before it starts")
    void registerBulkUser_shouldDiscardPreviousRejectedRows() throws Exception {
        bulkRegistrationServiceimpl.bulkRegistrationErrors.add(new BulkRegistrationError());

        controller.registerBulkUser("<EmployeeList/>", "auth", "admin", new MockHttpServletRequest(), 77);

        verify(bulkRegistrationService).registerBulkUser("<EmployeeList/>", "auth", "admin", 77);
    }

    @Test
    @DisplayName("registerBulkUser should report the failure rather than a partial success")
    void registerBulkUser_shouldReportFailure() throws Exception {
        doThrow(new IllegalStateException("the upload could not be read"))
                .when(bulkRegistrationService).registerBulkUser(anyString(), anyString(), anyString(), anyInt());

        ResponseEntity<Map<String, Object>> response = controller.registerBulkUser(
                "<EmployeeList/>", "auth", "admin", new MockHttpServletRequest(), 77);

        assertEquals(500, response.getBody().get("statusCode"));
        assertEquals("the upload could not be read", response.getBody().get("message"));
    }

    @Test
    @DisplayName("downloadErrorSheet should answer the rejected rows as a spreadsheet attachment")
    void downloadErrorSheet_shouldAnswerSpreadsheetAttachment() {
        BulkRegistrationError error = new BulkRegistrationError();
        error.setUserName("EMP-1");
        error.setError(java.util.List.of("Title is missing."));
        bulkRegistrationServiceimpl.bulkRegistrationErrors.add(error);

        ResponseEntity<byte[]> response = controller.downloadErrorSheet();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("error_log.xlsx"));
        assertTrue(bulkRegistrationServiceimpl.bulkRegistrationErrors.isEmpty(),
                "the rejected rows are handed over once, then cleared");
    }

    @Test
    @DisplayName("downloadErrorSheet should still answer a sheet when nothing was rejected")
    void downloadErrorSheet_shouldAnswerSheetWithoutRejectedRows() {
        ResponseEntity<byte[]> response = controller.downloadErrorSheet();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("downloadErrorSheet should answer a failure rather than an empty file when the sheet cannot be built")
    void downloadErrorSheet_shouldAnswerFailureWhenSheetCannotBeBuilt() {
        doThrow(new IllegalStateException("out of memory")).when(bulkRegistrationServiceimpl).insertErrorLog();

        ResponseEntity<byte[]> response = controller.downloadErrorSheet();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
