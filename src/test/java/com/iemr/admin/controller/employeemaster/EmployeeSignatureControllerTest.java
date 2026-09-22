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
package com.iemr.admin.controller.employeemaster;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.iemr.admin.data.employeemaster.EmployeeSignature;
import com.iemr.admin.service.employeemaster.EmployeeSignatureServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * A user's signature is stamped onto the prescriptions they sign, so the upload,
 * download and activation endpoints all guard a clinically meaningful artefact.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EmployeeSignatureController Test Suite")
class EmployeeSignatureControllerTest {

    private static final byte[] SIGNATURE = "a-png-body".getBytes(StandardCharsets.UTF_8);

    @Mock
    private EmployeeSignatureServiceImpl employeeSignatureServiceImpl;

    @InjectMocks
    private EmployeeSignatureController controller;

    private static EmployeeSignature signature(String fileName, String fileType) {
        EmployeeSignature signature = new EmployeeSignature();
        signature.setUserID(3117L);
        signature.setFileName(fileName);
        signature.setFileType(fileType);
        signature.setSignature(SIGNATURE);
        return signature;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    @Test
    @DisplayName("uploadFile should decode the uploaded body and answer the stored signature id")
    void uploadFile_shouldDecodeAndStore() {
        EmployeeSignature uploaded = new EmployeeSignature();
        uploaded.setUserID(3117L);
        uploaded.setFileContent(Base64.getEncoder().encodeToString(SIGNATURE));
        when(employeeSignatureServiceImpl.uploadSignature(any())).thenReturn(9001L);

        String response = controller.uploadFile(uploaded);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("9001"), response);
        assertArrayEquals(SIGNATURE, uploaded.getSignature(),
                "the base64 body must be decoded before it is stored");
    }

    @Test
    @DisplayName("uploadFile should answer an error envelope for a body that is not valid base64")
    void uploadFile_shouldAnswerErrorEnvelopeForInvalidBody() {
        EmployeeSignature uploaded = new EmployeeSignature();
        uploaded.setFileContent("not base 64 !!");

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.uploadFile(uploaded)));
    }

    @Test
    @DisplayName("uploadFile should answer an error envelope when the store fails")
    void uploadFile_shouldAnswerErrorEnvelopeOnStoreFailure() {
        EmployeeSignature uploaded = new EmployeeSignature();
        uploaded.setFileContent(Base64.getEncoder().encodeToString(SIGNATURE));
        when(employeeSignatureServiceImpl.uploadSignature(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.uploadFile(uploaded)));
    }

    @Test
    @DisplayName("fetchFile should answer the signature as an attachment of its own type")
    void fetchFile_shouldAnswerSignatureAsAttachment() throws Exception {
        when(employeeSignatureServiceImpl.fetchSignature(3117L))
                .thenReturn(signature("asha-signature.png", MediaType.IMAGE_PNG_VALUE));

        ResponseEntity<byte[]> response = controller.fetchFile(3117L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        assertArrayEquals(SIGNATURE, response.getBody());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("asha-signature.png"));
    }

    @Test
    @DisplayName("fetchFile should fall back to a plain download for a file type it cannot parse")
    void fetchFile_shouldFallBackForUnparseableType() throws Exception {
        when(employeeSignatureServiceImpl.fetchSignature(3117L))
                .thenReturn(signature("asha-signature.png", "not/a/media/type"));

        ResponseEntity<byte[]> response = controller.fetchFile(3117L);

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
    }

    @Test
    @DisplayName("fetchFile should fall back to a plain download when no file type is on record")
    void fetchFile_shouldFallBackWithoutAType() throws Exception {
        when(employeeSignatureServiceImpl.fetchSignature(3117L))
                .thenReturn(signature("asha-signature.png", null));

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, controller.fetchFile(3117L).getHeaders().getContentType());
    }

    @Test
    @DisplayName("fetchFile should raise rather than answer an empty download for a missing signature")
    void fetchFile_shouldRaiseForMissingSignature() {
        when(employeeSignatureServiceImpl.fetchSignature(3117L)).thenReturn(null);

        Exception thrown = assertThrows(Exception.class, () -> controller.fetchFile(3117L));
        assertTrue(thrown.getMessage().contains("Error while downloading file"), thrown.getMessage());
    }

    @Test
    @DisplayName("existFile should report both that a signature exists and whether it is active")
    void existFile_shouldReportExistenceAndActivation() throws Exception {
        when(employeeSignatureServiceImpl.existSignature(3117L)).thenReturn(Boolean.TRUE);
        when(employeeSignatureServiceImpl.isSignatureActive(3117L)).thenReturn(Boolean.TRUE);

        String response = controller.existFile(3117L);

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("\"signStatus\":\"true\""), response);
        assertTrue(response.contains("\"response\":\"true\""), response);
    }

    @Test
    @DisplayName("existFile should report a signature that exists but has been deactivated")
    void existFile_shouldReportDeactivatedSignature() throws Exception {
        when(employeeSignatureServiceImpl.existSignature(3117L)).thenReturn(Boolean.TRUE);
        when(employeeSignatureServiceImpl.isSignatureActive(3117L)).thenReturn(Boolean.FALSE);

        assertTrue(controller.existFile(3117L).contains("\"signStatus\":\"false\""));
    }

    @Test
    @DisplayName("existFile should answer an error envelope when the check fails")
    void existFile_shouldAnswerErrorEnvelopeOnFailure() throws Exception {
        when(employeeSignatureServiceImpl.existSignature(anyLong()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.existFile(3117L)));
    }

    @Test
    @DisplayName("ActivateUser should report the signature as active once it is reinstated")
    void activateUser_shouldReportSignatureActive() {
        EmployeeSignature updated = signature("asha-signature.png", MediaType.IMAGE_PNG_VALUE);
        updated.setDeleted(Boolean.FALSE);
        when(employeeSignatureServiceImpl.updateUserSignatureStatus(anyString())).thenReturn(updated);

        String response = controller.ActivateUser("{\"userID\":3117,\"deleted\":false}",
                new MockHttpServletRequest());

        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains("\"active\":true"), response);
    }

    @Test
    @DisplayName("ActivateUser should report the signature as inactive once it is deactivated")
    void activateUser_shouldReportSignatureInactive() {
        EmployeeSignature updated = signature("asha-signature.png", MediaType.IMAGE_PNG_VALUE);
        updated.setDeleted(Boolean.TRUE);
        when(employeeSignatureServiceImpl.updateUserSignatureStatus(anyString())).thenReturn(updated);

        assertTrue(controller.ActivateUser("{\"userID\":3117,\"deleted\":true}", new MockHttpServletRequest())
                .contains("\"active\":false"));
    }

    @Test
    @DisplayName("ActivateUser should treat a signature with no flag on record as inactive")
    void activateUser_shouldTreatMissingFlagAsInactive() {
        EmployeeSignature updated = signature("asha-signature.png", MediaType.IMAGE_PNG_VALUE);
        when(employeeSignatureServiceImpl.updateUserSignatureStatus(anyString())).thenReturn(updated);

        assertTrue(controller.ActivateUser("{\"userID\":3117}", new MockHttpServletRequest())
                .contains("\"active\":false"));
    }

    @Test
    @DisplayName("ActivateUser should answer an error envelope when the change fails")
    void activateUser_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeSignatureServiceImpl.updateUserSignatureStatus(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.ActivateUser("{\"userID\":3117}", new MockHttpServletRequest())));
    }
}
