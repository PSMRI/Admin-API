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
package com.iemr.admin.service.villageMaster;

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

import com.iemr.admin.data.locationmaster.DistrictBranchMapping;
import com.iemr.admin.repository.villageMaster.VillageMasterRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The village service keeps the villages of a taluk, along with the panchayat,
 * habitation and pin code each one sits in.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VillageMasterServiceImpl Test Suite")
class VillageMasterServiceImplTest {

    private static final Integer BLOCK_ID = 3011;
    private static final Integer VILLAGE_ID = 30111;

    @Mock
    private VillageMasterRepository villageMasterRepository;

    @InjectMocks
    private VillageMasterServiceImpl service;

    private static Object[] villageRow() {
        return new Object[] { VILLAGE_ID, BLOCK_ID, "Anekal", "Attibele panchayat", "Attibele", "Main habitation",
                "562107", 900111, 900011, Boolean.FALSE, Boolean.TRUE };
    }

    private static DistrictBranchMapping village() {
        DistrictBranchMapping village = new DistrictBranchMapping();
        village.setDistrictBranchID(VILLAGE_ID);
        village.setVillageName("Attibele");
        village.setPanchayatName("Attibele panchayat");
        village.setHabitat("Main habitation");
        village.setPinCode("562107");
        village.setGovVillageID(900111);
        village.setGovSubDistrictID(900011);
        village.setIsRural(Boolean.TRUE);
        village.setModifiedBy("admin");
        return village;
    }

    @Test
    @DisplayName("storeVillageDetails should answer the villages the repository stored")
    void store_shouldAnswerStoredVillages() {
        ArrayList<DistrictBranchMapping> stored = new ArrayList<>(List.of(village()));
        when(villageMasterRepository.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.storeVillageDetails(new ArrayList<>()));
    }

    @Test
    @DisplayName("getAvailableVillages should rebuild one village per row the query answers")
    void getAvailable_shouldRebuildEachRow() {
        when(villageMasterRepository.getAvailableVillages(BLOCK_ID)).thenReturn(List.<Object[]>of(villageRow()));

        ArrayList<DistrictBranchMapping> villages = service.getAvailableVillages(BLOCK_ID);

        assertEquals(1, villages.size());
        assertEquals("Attibele", villages.get(0).getVillageName());
        assertEquals("Attibele panchayat", villages.get(0).getPanchayatName());
        assertEquals("562107", villages.get(0).getPinCode());
        assertEquals(Boolean.TRUE, villages.get(0).getIsRural());
    }

    @Test
    @DisplayName("getAvailableVillages should answer nothing when the taluk holds no village")
    void getAvailable_shouldAnswerNothingForEmptyTaluk() {
        when(villageMasterRepository.getAvailableVillages(BLOCK_ID)).thenReturn(new ArrayList<>());

        assertTrue(service.getAvailableVillages(BLOCK_ID).isEmpty());
    }

    @Test
    @DisplayName("updateVillageStatus should report how many villages the retirement touched")
    void updateStatus_shouldReportRowsTouched() {
        DistrictBranchMapping request = village();
        request.setDeleted(Boolean.TRUE);
        when(villageMasterRepository.updateVillageStatus(VILLAGE_ID, Boolean.TRUE, "admin")).thenReturn(1);

        assertEquals(1, service.updateVillageStatus(request));
    }

    @Test
    @DisplayName("updateVillageStatus should report nothing touched when the village is unknown")
    void updateStatus_shouldReportNothingTouchedForUnknownVillage() {
        DistrictBranchMapping request = new DistrictBranchMapping();
        request.setDistrictBranchID(-1);
        when(villageMasterRepository.updateVillageStatus(-1, null, null)).thenReturn(0);

        assertEquals(0, service.updateVillageStatus(request));
    }

    @Test
    @DisplayName("updateVillageData should carry every changed detail through to the repository")
    void updateData_shouldCarryEveryChangedDetail() {
        when(villageMasterRepository.updateVillageData("Attibele panchayat", "Attibele", "Main habitation", "562107",
                900111, 900011, VILLAGE_ID, "admin", Boolean.TRUE)).thenReturn(1);

        assertEquals(1, service.updateVillageData(village()));
    }

    @Test
    @DisplayName("updateVillageData should report nothing touched when the village is unknown")
    void updateData_shouldReportNothingTouchedForUnknownVillage() {
        DistrictBranchMapping request = village();
        request.setDistrictBranchID(-1);

        assertEquals(0, service.updateVillageData(request));
    }
}
