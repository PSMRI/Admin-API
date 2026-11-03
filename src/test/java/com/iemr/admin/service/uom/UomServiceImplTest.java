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
package com.iemr.admin.service.uom;

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

import com.iemr.admin.data.uom.M_Uom;
import com.iemr.admin.repo.uom.UomRepo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/** The unit of measure service is thin over its repository, but it answers nothing rather than an empty batch. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UomServiceImpl Test Suite")
class UomServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer RECORD_ID = 11;

    @Mock
    private UomRepo uomRepo;

    @InjectMocks
    private UomServiceImpl service;

    @Test
    @DisplayName("createDrugtypeData should answer the records it stored")
    void create_shouldAnswerStoredRecords() {
        ArrayList<M_Uom> stored = new ArrayList<>(List.of(new M_Uom()));
        when(uomRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.createDrugtypeData(new ArrayList<>()));
    }

    @Test
    @DisplayName("createDrugtypeData should answer nothing when the store took nothing")
    void create_shouldAnswerNothingWhenNothingStored() {
        when(uomRepo.saveAll(anyList())).thenReturn(new ArrayList<M_Uom>());

        assertNull(service.createDrugtypeData(new ArrayList<>()));
    }

    @Test
    @DisplayName("the lookups should each reach their own repository query")
    void lookups_shouldReachTheirOwnQuery() {
        M_Uom record = new M_Uom();
        ArrayList<M_Uom> records = new ArrayList<>(List.of(record));
        when(uomRepo.getUom(PSM_ID)).thenReturn(records);
        when(uomRepo.geteditedData(RECORD_ID)).thenReturn(record);
        when(uomRepo.save(record)).thenReturn(record);

        assertSame(records, service.createDrugtypeData(PSM_ID));
        assertSame(record, service.editDrugtypeData(RECORD_ID));
        assertSame(record, service.saveeditedData(record));
    }

    @Test
    @DisplayName("checkUomCode should report a code the provider already uses")
    void check_shouldReportUsedCode() {
        M_Uom request = new M_Uom();
        request.setuOMCode("C-1");
        request.setProviderServiceMapID(PSM_ID);
        when(uomRepo.findByUOMCodeAndProviderServiceMapID("C-1", PSM_ID)).thenReturn(List.of(new M_Uom()));

        assertTrue(service.checkUomCode(request));
    }

    @Test
    @DisplayName("checkUomCode should clear a code nobody uses yet")
    void check_shouldClearFreeCode() {
        M_Uom request = new M_Uom();
        request.setuOMCode("C-2");
        request.setProviderServiceMapID(PSM_ID);
        when(uomRepo.findByUOMCodeAndProviderServiceMapID("C-2", PSM_ID)).thenReturn(new ArrayList<M_Uom>());

        assertFalse(service.checkUomCode(request));
    }
}
