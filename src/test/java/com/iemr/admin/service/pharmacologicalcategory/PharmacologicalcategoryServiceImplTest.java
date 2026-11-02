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
package com.iemr.admin.service.pharmacologicalcategory;

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

import com.iemr.admin.data.pharmacologicalcategory.M_Pharmacologicalcategory;
import com.iemr.admin.repo.pharmacologicalcategory.PharmacologicalcategoryRepo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/** The pharmacological category service is thin over its repository, but it answers nothing rather than an empty batch. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PharmacologicalcategoryServiceImpl Test Suite")
class PharmacologicalcategoryServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer RECORD_ID = 11;

    @Mock
    private PharmacologicalcategoryRepo pharmacologicalcategoryRepo;

    @InjectMocks
    private PharmacologicalcategoryServiceImpl service;

    @Test
    @DisplayName("createPharmacologicalcategory should answer the records it stored")
    void create_shouldAnswerStoredRecords() {
        ArrayList<M_Pharmacologicalcategory> stored = new ArrayList<>(List.of(new M_Pharmacologicalcategory()));
        when(pharmacologicalcategoryRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.createPharmacologicalcategory(new ArrayList<>()));
    }

    @Test
    @DisplayName("createPharmacologicalcategory should answer nothing when the store took nothing")
    void create_shouldAnswerNothingWhenNothingStored() {
        when(pharmacologicalcategoryRepo.saveAll(anyList())).thenReturn(new ArrayList<M_Pharmacologicalcategory>());

        assertNull(service.createPharmacologicalcategory(new ArrayList<>()));
    }

    @Test
    @DisplayName("the lookups should each reach their own repository query")
    void lookups_shouldReachTheirOwnQuery() {
        M_Pharmacologicalcategory record = new M_Pharmacologicalcategory();
        ArrayList<M_Pharmacologicalcategory> records = new ArrayList<>(List.of(record));
        when(pharmacologicalcategoryRepo.getPhormacologicalData(PSM_ID)).thenReturn(records);
        when(pharmacologicalcategoryRepo.editPhamacologicalData(RECORD_ID)).thenReturn(record);
        when(pharmacologicalcategoryRepo.save(record)).thenReturn(record);

        assertSame(records, service.getPharmacologicalcategory(PSM_ID));
        assertSame(record, service.editPharmacologicalcategory(RECORD_ID));
        assertSame(record, service.saveEditedPharData(record));
    }

    @Test
    @DisplayName("checkPharmacologicalcategoryCode should report a code the provider already uses")
    void check_shouldReportUsedCode() {
        M_Pharmacologicalcategory request = new M_Pharmacologicalcategory();
        request.setPharmCategoryCode("C-1");
        request.setProviderServiceMapID(PSM_ID);
        when(pharmacologicalcategoryRepo.findByPharmCategoryCodeAndProviderServiceMapID("C-1", PSM_ID)).thenReturn(List.of(new M_Pharmacologicalcategory()));

        assertTrue(service.checkPharmacologicalcategoryCode(request));
    }

    @Test
    @DisplayName("checkPharmacologicalcategoryCode should clear a code nobody uses yet")
    void check_shouldClearFreeCode() {
        M_Pharmacologicalcategory request = new M_Pharmacologicalcategory();
        request.setPharmCategoryCode("C-2");
        request.setProviderServiceMapID(PSM_ID);
        when(pharmacologicalcategoryRepo.findByPharmCategoryCodeAndProviderServiceMapID("C-2", PSM_ID)).thenReturn(new ArrayList<M_Pharmacologicalcategory>());

        assertFalse(service.checkPharmacologicalcategoryCode(request));
    }
}
