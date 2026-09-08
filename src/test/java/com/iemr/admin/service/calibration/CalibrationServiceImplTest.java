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
package com.iemr.admin.service.calibration;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.iemr.admin.data.calibration.CalibrationStrip;
import com.iemr.admin.repo.calibration.CalibrationRepo;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.OutputMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The calibration service keeps the test strip codes a provider calibrates
 * against, refusing a code the provider already holds.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CalibrationServiceImpl Test Suite")
class CalibrationServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Long STRIP_ID = 6601L;
    private static final String STRIP_CODE = "STRIP-77";

    @Mock
    private CalibrationRepo calibrationRepo;

    @InjectMocks
    private CalibrationServiceImpl service;

    @BeforeEach
    @DisplayName("Fix the page size the screens are served in and prime the shared output builder")
    void setUp() {
        ReflectionTestUtils.setField(service, "calibrationPageSize", 10);
        new OutputMapper();
    }

    private static CalibrationStrip strip() {
        CalibrationStrip strip = new CalibrationStrip();
        strip.setCalibrationStripID(STRIP_ID);
        strip.setStripCode(STRIP_CODE);
        strip.setProviderServiceMapID(PSM_ID);
        strip.setDeleted(Boolean.FALSE);
        return strip;
    }

    private static CalibrationStrip request() {
        CalibrationStrip request = new CalibrationStrip();
        request.setStripCode(STRIP_CODE);
        request.setProviderServiceMapID(PSM_ID);
        return request;
    }

    @Test
    @DisplayName("saveData should record a strip code the provider does not hold yet")
    void save_shouldRecordNewStripCode() throws Exception {
        when(calibrationRepo.checkIfAlreadyStripPresent(PSM_ID, STRIP_CODE)).thenReturn(new ArrayList<>());
        when(calibrationRepo.save(any(CalibrationStrip.class))).thenReturn(strip());

        assertEquals(1, service.saveData(request()));
    }

    @Test
    @DisplayName("saveData should refuse a strip code the provider already holds")
    void save_shouldRefuseDuplicateStripCode() {
        when(calibrationRepo.checkIfAlreadyStripPresent(PSM_ID, STRIP_CODE))
                .thenReturn(new ArrayList<>(List.of(strip())));

        IEMRException refusal = assertThrows(IEMRException.class, () -> service.saveData(request()));

        assertEquals("Strip code already exists", refusal.getMessage());
        verify(calibrationRepo, never()).save(any(CalibrationStrip.class));
    }

    @Test
    @DisplayName("saveData should refuse a strip the repository did not give an identity")
    void save_shouldRefuseStripWithoutIdentity() {
        when(calibrationRepo.checkIfAlreadyStripPresent(PSM_ID, STRIP_CODE)).thenReturn(new ArrayList<>());
        when(calibrationRepo.save(any(CalibrationStrip.class))).thenReturn(new CalibrationStrip());

        assertEquals("Error while saving data",
                assertThrows(IEMRException.class, () -> service.saveData(request())).getMessage());
    }

    @Test
    @DisplayName("saveData should record nothing when the request names no strip code")
    void save_shouldRecordNothingWithoutStripCode() throws Exception {
        assertEquals(0, service.saveData(new CalibrationStrip()));
        verify(calibrationRepo, never()).save(any(CalibrationStrip.class));
    }

    @Test
    @DisplayName("fetchData should answer one page of strips and how many pages there are")
    void fetch_shouldAnswerOnePageAndPageCount() throws Exception {
        CalibrationStrip request = request();
        request.setPageNo(0);
        Pageable pageable = PageRequest.of(0, 10);
        Page<CalibrationStrip> page = new PageImpl<>(List.of(strip()), pageable, 1);
        when(calibrationRepo.getCalibrationStripsWithPagination(PSM_ID, pageable)).thenReturn(page);

        String answered = service.fetchData(request);

        assertTrue(answered.contains(STRIP_CODE), answered);
        assertTrue(answered.contains("pageCount"), answered);
    }

    @Test
    @DisplayName("fetchData should answer every strip when the caller asks for no particular page")
    void fetch_shouldAnswerEveryStripWithoutPaging() throws Exception {
        when(calibrationRepo.getCalibrationStripsWithoutPagination(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(strip())));

        String answered = service.fetchData(request());

        assertTrue(answered.contains(STRIP_CODE), answered);
        assertTrue(!answered.contains("pageCount"), "an unpaged answer carries no page count");
    }

    @Test
    @DisplayName("fetchData should refuse a request that names no provider")
    void fetch_shouldRefuseRequestWithoutProvider() {
        assertThrows(IEMRException.class, () -> service.fetchData(new CalibrationStrip()));
    }

    @Test
    @DisplayName("deleteData should report how many strips the retirement touched")
    void delete_shouldReportRowsTouched() throws Exception {
        CalibrationStrip request = strip();
        request.setDeleted(Boolean.TRUE);
        when(calibrationRepo.deleteCalibrationStrip(STRIP_ID, Boolean.TRUE)).thenReturn(1);

        assertEquals(1, service.deleteData(request));
    }

    @Test
    @DisplayName("deleteData should refuse a request that names no strip")
    void delete_shouldRefuseRequestWithoutStrip() {
        assertEquals("Invalid request",
                assertThrows(IEMRException.class, () -> service.deleteData(new CalibrationStrip())).getMessage());
    }

    @Test
    @DisplayName("deleteData should give up when the retirement cannot be recorded")
    void delete_shouldGiveUpWhenStorageFails() {
        CalibrationStrip request = strip();
        request.setDeleted(Boolean.TRUE);
        when(calibrationRepo.deleteCalibrationStrip(anyLong(), anyBoolean()))
                .thenThrow(new RuntimeException("row is locked"));

        assertThrows(IEMRException.class, () -> service.deleteData(request));
    }

    @Test
    @DisplayName("updateData should record the change against the strip")
    void update_shouldRecordChange() throws Exception {
        when(calibrationRepo.save(any(CalibrationStrip.class))).thenReturn(strip());

        assertEquals(1, service.updateData(request()));
    }

    @Test
    @DisplayName("updateData should refuse a strip the repository did not give an identity")
    void update_shouldRefuseStripWithoutIdentity() {
        when(calibrationRepo.save(any(CalibrationStrip.class))).thenReturn(new CalibrationStrip());

        assertEquals("Error while updating data",
                assertThrows(IEMRException.class, () -> service.updateData(request())).getMessage());
    }

    @Test
    @DisplayName("updateData should record nothing when the request names no strip code")
    void update_shouldRecordNothingWithoutStripCode() throws Exception {
        assertEquals(0, service.updateData(new CalibrationStrip()));
    }
}
