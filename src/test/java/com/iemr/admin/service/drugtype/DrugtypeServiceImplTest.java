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
package com.iemr.admin.service.drugtype;

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

import com.iemr.admin.data.drugtype.M_Drugtype;
import com.iemr.admin.repo.drugtype.DrugtypeRepo;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The drug type service keeps the dosage forms a provider stocks.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DrugtypeServiceImpl Test Suite")
class DrugtypeServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer DRUG_TYPE_ID = 21;

    @Mock
    private DrugtypeRepo drugtypeRepo;

    @InjectMocks
    private DrugtypeServiceImpl service;

    private static M_Drugtype drugType() {
        M_Drugtype drugType = new M_Drugtype();
        drugType.setDrugTypeID(DRUG_TYPE_ID);
        drugType.setDrugTypeName("Tablet");
        drugType.setProviderServiceMapID(PSM_ID);
        return drugType;
    }

    @Test
    @DisplayName("createDrugtypeData should answer the drug types the repository stored")
    void create_shouldAnswerStoredDrugTypes() {
        ArrayList<M_Drugtype> stored = new ArrayList<>(List.of(drugType()));
        when(drugtypeRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.createDrugtypeData(new ArrayList<>()));
    }

    @Test
    @DisplayName("createDrugtypeData should answer nothing when the repository stored nothing")
    void create_shouldAnswerNothingWhenNothingStored() {
        when(drugtypeRepo.saveAll(anyList())).thenReturn(new ArrayList<M_Drugtype>());

        assertNull(service.createDrugtypeData(new ArrayList<>()));
    }

    @Test
    @DisplayName("getDrugtypeData should answer the drug types the provider stocks")
    void get_shouldAnswerProvidersDrugTypes() {
        ArrayList<M_Drugtype> stocked = new ArrayList<>(List.of(drugType()));
        when(drugtypeRepo.getDrugtypeData(PSM_ID)).thenReturn(stocked);

        assertSame(stocked, service.getDrugtypeData(PSM_ID));
    }

    @Test
    @DisplayName("editDrugtypeData and saveeditDrugtype should each reach their own repository query")
    void singleRecordOperations_shouldReachTheirOwnQuery() {
        M_Drugtype stored = drugType();
        when(drugtypeRepo.geteditedData(DRUG_TYPE_ID)).thenReturn(stored);
        when(drugtypeRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.editDrugtypeData(DRUG_TYPE_ID));
        assertSame(stored, service.saveeditDrugtype(stored));
    }

    @Test
    @DisplayName("editDrugtypeData should answer nothing when the drug type is unknown")
    void edit_shouldAnswerNothingForUnknownDrugType() {
        when(drugtypeRepo.geteditedData(-1)).thenReturn(null);

        assertNull(service.editDrugtypeData(-1));
    }
}
