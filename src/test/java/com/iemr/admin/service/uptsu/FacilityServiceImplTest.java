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
package com.iemr.admin.service.uptsu;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.uptsu.CDSSMapping;
import com.iemr.admin.data.uptsu.M_FacilityMapping;
import com.iemr.admin.data.uptsu.UploadRequest;
import com.iemr.admin.repository.uptsu.CDSSMappingRepo;
import com.iemr.admin.repository.uptsu.FacilityRepository;
import com.iemr.admin.utils.exception.IEMRException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The UP TSU facility upload turns an operator's spreadsheet into the facility
 * mapping rows the state's field staff are attached to, so a column read into
 * the wrong field would post a health worker to the wrong facility.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FacilityServiceImpl Test Suite")
class FacilityServiceImplTest {

    private static final Integer PSM_ID = 4001;

    /** The columns the upload reads as whole numbers rather than text. */
    private static final Set<Integer> NUMERIC_COLUMNS = Set.of(7, 17, 19, 21, 23, 25, 27);

    /** The columns the upload refuses to accept blank. */
    private static final Set<Integer> MANDATORY_COLUMNS = Set.of(0, 1, 15, 22, 28, 29, 39);

    @Mock
    private FacilityRepository uptsuUploadRepository;

    @Mock
    private CDSSMappingRepo cdssMappingRepo;

    @InjectMocks
    private FacilityServiceImpl service;

    private static String uploadOf(Workbook workbook) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return "data:application/vnd.ms-excel;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /** Builds a one-row upload whose every column carries a usable value. */
    private static Workbook completeUpload() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Facilities");
        Row header = sheet.createRow(0);
        Row data = sheet.createRow(1);
        for (int column = 0; column <= 40; column++) {
            header.createCell(column).setCellValue("column " + column);
            if (NUMERIC_COLUMNS.contains(column)) {
                data.createCell(column).setCellValue(column + 100);
            } else {
                data.createCell(column).setCellValue("value " + column);
            }
        }
        return workbook;
    }

    private static UploadRequest requestFor(Workbook workbook) throws Exception {
        UploadRequest request = new UploadRequest();
        request.setCreatedBy("admin");
        request.setProviderServiceMapID(PSM_ID);
        request.setFileName("facilities.xlsx");
        request.setFileExtension("xlsx");
        request.setFileContent(uploadOf(workbook));
        return request;
    }

    @Test
    @DisplayName("saveFacility should turn each spreadsheet row into a facility mapping")
    void saveFacility_shouldMapEachRow() throws Exception {
        UploadRequest request = requestFor(completeUpload());
        when(uptsuUploadRepository.saveAll(anyList())).thenAnswer(call -> call.getArgument(0));

        Iterable<M_FacilityMapping> saved = service.saveFacility(request);

        List<M_FacilityMapping> rows = new ArrayList<>();
        saved.forEach(rows::add);
        assertEquals(1, rows.size());
        M_FacilityMapping mapped = rows.get(0);
        assertEquals("value 0", mapped.getEmployeeCode());
        assertEquals("value 1", mapped.getEmployeeName());
        assertEquals(107, mapped.getDesignationId(), "column 7 is read as a whole number");
        assertEquals("value 22", mapped.getBlockName());
        assertEquals("value 39", mapped.getHfrCode());
        assertEquals("admin", mapped.getCreatedBy());
        assertEquals(PSM_ID, mapped.getProviderServiceMapID());
        assertEquals('N', mapped.getProcessed());
        assertEquals(false, mapped.isDeleted());
    }

    @Test
    @DisplayName("saveFacility should retire the provider's previous upload before storing the new one")
    void saveFacility_shouldRetirePreviousUpload() throws Exception {
        UploadRequest request = requestFor(completeUpload());
        when(uptsuUploadRepository.saveAll(anyList())).thenAnswer(call -> call.getArgument(0));

        service.saveFacility(request);

        verify(uptsuUploadRepository).updatedeleteStatus(org.mockito.ArgumentMatchers.eq(PSM_ID), any(), anyString());
    }

    @Test
    @DisplayName("saveFacility should store every row of a multi-row upload")
    void saveFacility_shouldStoreEveryRow() throws Exception {
        Workbook workbook = completeUpload();
        Sheet sheet = workbook.getSheetAt(0);
        Row second = sheet.createRow(2);
        for (int column = 0; column <= 40; column++) {
            if (NUMERIC_COLUMNS.contains(column)) {
                second.createCell(column).setCellValue(column + 200);
            } else {
                second.createCell(column).setCellValue("second " + column);
            }
        }
        when(uptsuUploadRepository.saveAll(anyList())).thenAnswer(call -> call.getArgument(0));

        ArgumentCaptor<List<M_FacilityMapping>> captor = ArgumentCaptor.forClass(List.class);
        service.saveFacility(requestFor(workbook));

        verify(uptsuUploadRepository).saveAll(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    @Test
    @DisplayName("saveFacility should leave an optional column unset rather than invent a value")
    void saveFacility_shouldLeaveOptionalColumnUnset() throws Exception {
        Workbook workbook = completeUpload();
        workbook.getSheetAt(0).getRow(1).getCell(2).setBlank();
        when(uptsuUploadRepository.saveAll(anyList())).thenAnswer(call -> call.getArgument(0));

        Iterable<M_FacilityMapping> saved = service.saveFacility(requestFor(workbook));

        assertNull(saved.iterator().next().getSurveyFacility());
    }

    @Test
    @DisplayName("saveFacility should fall back to zero for a numeric column left blank")
    void saveFacility_shouldFallBackToZeroForBlankNumericColumn() throws Exception {
        Workbook workbook = completeUpload();
        workbook.getSheetAt(0).getRow(1).getCell(7).setBlank();
        when(uptsuUploadRepository.saveAll(anyList())).thenAnswer(call -> call.getArgument(0));

        Iterable<M_FacilityMapping> saved = service.saveFacility(requestFor(workbook));

        assertEquals(0, saved.iterator().next().getDesignationId());
    }

    @Test
    @DisplayName("saveFacility should refuse an upload whose mandatory column is blank")
    void saveFacility_shouldRefuseBlankMandatoryColumn() throws Exception {
        Workbook workbook = completeUpload();
        workbook.getSheetAt(0).getRow(1).getCell(0).setBlank();
        UploadRequest request = requestFor(workbook);

        assertThrows(IEMRException.class, () -> service.saveFacility(request));
    }

    @Test
    @DisplayName("saveFacility should read a boolean cell rather than refuse it")
    void saveFacility_shouldReadBooleanCell() throws Exception {
        Workbook workbook = completeUpload();
        workbook.getSheetAt(0).getRow(1).getCell(2).setCellValue(true);
        when(uptsuUploadRepository.saveAll(anyList())).thenAnswer(call -> call.getArgument(0));

        Iterable<M_FacilityMapping> saved = service.saveFacility(requestFor(workbook));

        assertEquals("true", saved.iterator().next().getSurveyFacility());
    }

    @Test
    @DisplayName("saveFacility should answer nothing when there is no upload to read")
    void saveFacility_shouldAnswerNothingWithoutAnUpload() throws Exception {
        assertNull(service.saveFacility(null));
        verify(uptsuUploadRepository, org.mockito.Mockito.never()).saveAll(anyList());
    }

    @Test
    @DisplayName("saveFacility should refuse a payload that is not a workbook at all")
    void saveFacility_shouldRefuseNonWorkbookPayload() {
        UploadRequest request = new UploadRequest();
        request.setCreatedBy("admin");
        request.setProviderServiceMapID(PSM_ID);
        request.setFileContent("data:text/plain;base64," + Base64.getEncoder().encodeToString("not a workbook".getBytes()));

        assertThrows(Exception.class, () -> service.saveFacility(request));
    }

    @Test
    @DisplayName("saveCdssDetails should retire the provider's previous configuration before storing the new one")
    void saveCdssDetails_shouldRetirePreviousConfiguration() {
        CDSSMapping previous = new CDSSMapping();
        previous.setPsmId(PSM_ID);
        CDSSMapping request = new CDSSMapping();
        request.setPsmId(PSM_ID);
        request.setIsCdss(Boolean.TRUE);
        when(cdssMappingRepo.findByPsmIdAndDeleted(PSM_ID, false)).thenReturn(previous);
        when(cdssMappingRepo.save(request)).thenReturn(request);

        assertEquals(request, service.saveCdssDetails(request));
        assertTrue(previous.getDeleted(), "the configuration it replaces must be retired");
    }

    @Test
    @DisplayName("saveCdssDetails should store the first configuration a provider has")
    void saveCdssDetails_shouldStoreFirstConfiguration() {
        CDSSMapping request = new CDSSMapping();
        request.setPsmId(PSM_ID);
        when(cdssMappingRepo.findByPsmIdAndDeleted(PSM_ID, false)).thenReturn(null);
        when(cdssMappingRepo.save(request)).thenReturn(request);

        assertEquals(request, service.saveCdssDetails(request));
    }

    @Test
    @DisplayName("getCdssData should publish the stored configuration as JSON")
    void getCdssData_shouldPublishStoredConfiguration() throws Exception {
        CDSSMapping stored = new CDSSMapping();
        stored.setPsmId(PSM_ID);
        stored.setIsCdss(Boolean.TRUE);
        when(cdssMappingRepo.findByPsmIdAndDeleted(PSM_ID, false)).thenReturn(stored);

        String published = service.getCdssData(PSM_ID);

        assertTrue(published.contains("\"psmId\":4001"), published);
        assertTrue(published.contains("\"isCdss\":true"), published);
    }

    @Test
    @DisplayName("getCdssData should publish a null rather than fail for a provider with no configuration")
    void getCdssData_shouldPublishNullForUnconfiguredProvider() throws Exception {
        when(cdssMappingRepo.findByPsmIdAndDeleted(anyInt(), any())).thenReturn(null);

        assertEquals("null", service.getCdssData(PSM_ID));
    }
}
