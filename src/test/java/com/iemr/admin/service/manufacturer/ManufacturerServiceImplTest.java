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
package com.iemr.admin.service.manufacturer;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.manufacturer.M_Manufacturer;
import com.iemr.admin.repo.manufacturer.ManufacturerRepo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/** The manufacturer service is thin over its repository, but it answers nothing rather than an empty batch. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ManufacturerServiceImpl Test Suite")
class ManufacturerServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer RECORD_ID = 11;

    @Mock
    private ManufacturerRepo manufacturerRepo;

    @InjectMocks
    private ManufacturerServiceImpl service;

    @Test
    @DisplayName("createManufacturer should answer the records it stored")
    void create_shouldAnswerStoredRecords() {
        ArrayList<M_Manufacturer> stored = new ArrayList<>(List.of(new M_Manufacturer()));
        when(manufacturerRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.createManufacturer(new ArrayList<>()));
    }

    @Test
    @DisplayName("createManufacturer should answer nothing when the store took nothing")
    void create_shouldAnswerNothingWhenNothingStored() {
        when(manufacturerRepo.saveAll(anyList())).thenReturn(new ArrayList<M_Manufacturer>());

        assertNull(service.createManufacturer(new ArrayList<>()));
    }

    @Test
    @DisplayName("the lookups should each reach their own repository query")
    void lookups_shouldReachTheirOwnQuery() {
        M_Manufacturer record = new M_Manufacturer();
        ArrayList<M_Manufacturer> records = new ArrayList<>(List.of(record));
        when(manufacturerRepo.getManufacturerData(PSM_ID)).thenReturn(records);
        when(manufacturerRepo.getEditData(RECORD_ID)).thenReturn(record);
        when(manufacturerRepo.save(record)).thenReturn(record);

        assertSame(records, service.createManufacturer(PSM_ID));
        assertSame(record, service.editManufacturer(RECORD_ID));
        assertSame(record, service.saveEditedData(record));
    }

    @Test
    @DisplayName("checkManufacturerCode should report a code the provider already uses")
    void check_shouldReportUsedCode() {
        M_Manufacturer request = new M_Manufacturer();
        request.setManufacturerCode("C-1");
        request.setProviderServiceMapID(PSM_ID);
        when(manufacturerRepo.findByManufacturerCodeAndProviderServiceMapID("C-1", PSM_ID)).thenReturn(List.of(new M_Manufacturer()));

        assertTrue(service.checkManufacturerCode(request));
    }

    @Test
    @DisplayName("checkManufacturerCode should clear a code nobody uses yet")
    void check_shouldClearFreeCode() {
        M_Manufacturer request = new M_Manufacturer();
        request.setManufacturerCode("C-2");
        request.setProviderServiceMapID(PSM_ID);
        when(manufacturerRepo.findByManufacturerCodeAndProviderServiceMapID("C-2", PSM_ID)).thenReturn(new ArrayList<M_Manufacturer>());

        assertFalse(service.checkManufacturerCode(request));
    }
}
