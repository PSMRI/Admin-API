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
package com.iemr.admin.service.drugstrangth;

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

import com.iemr.admin.data.drugstrangth.M_104DrugStrength;
import com.iemr.admin.repo.blocking.DrugStrangthRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The drug strength service keeps the strengths a drug can be dispensed in.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DrugStrangthService Test Suite")
class DrugStrangthServiceTest {

    private static final Integer STRENGTH_ID = 33;

    @Mock
    private DrugStrangthRepo drugStrangthRepo;

    @InjectMocks
    private DrugStrangthService service;

    private static M_104DrugStrength strength() {
        M_104DrugStrength strength = new M_104DrugStrength();
        strength.setDrugStrengthID(STRENGTH_ID);
        strength.setDrugStrength("500 mg");
        return strength;
    }

    @Test
    @DisplayName("createDrugStrangth should answer the strengths the repository stored")
    void create_shouldAnswerStoredStrengths() {
        ArrayList<M_104DrugStrength> stored = new ArrayList<>(List.of(strength()));
        when(drugStrangthRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.createDrugStrangth(new ArrayList<>()));
    }

    @Test
    @DisplayName("getDrugStrangth should answer every strength on file")
    void get_shouldAnswerEveryStrength() {
        when(drugStrangthRepo.findAll()).thenReturn(new ArrayList<>(List.of(strength())));

        assertEquals(1, service.getDrugStrangth().size());
    }

    @Test
    @DisplayName("getDrugStrangth should answer nothing when no strength is on file")
    void get_shouldAnswerNothingWhenNoneOnFile() {
        when(drugStrangthRepo.findAll()).thenReturn(new ArrayList<M_104DrugStrength>());

        assertTrue(service.getDrugStrangth().isEmpty());
    }

    @Test
    @DisplayName("updateDrugStrangth and saveupdatedData should each reach their own repository query")
    void singleRecordOperations_shouldReachTheirOwnQuery() {
        M_104DrugStrength stored = strength();
        when(drugStrangthRepo.findByDrugStrengthID(STRENGTH_ID)).thenReturn(stored);
        when(drugStrangthRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.updateDrugStrangth(STRENGTH_ID));
        assertSame(stored, service.saveupdatedData(stored));
    }

    @Test
    @DisplayName("updateDrugStrangth should answer nothing when the strength is unknown")
    void update_shouldAnswerNothingForUnknownStrength() {
        when(drugStrangthRepo.findByDrugStrengthID(-1)).thenReturn(null);

        assertNull(service.updateDrugStrangth(-1));
    }
}
