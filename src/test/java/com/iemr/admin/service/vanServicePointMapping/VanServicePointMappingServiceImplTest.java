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
package com.iemr.admin.service.vanServicePointMapping;

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

import com.iemr.admin.data.vanServicePointMapping.M_VanServicePointMap;
import com.iemr.admin.repository.vanServicePointMapping.VanServicePointMappingRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The van service point service records which service points a van visits and
 * in which session of the day.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VanServicePointMappingServiceImpl Test Suite")
class VanServicePointMappingServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer VAN_ID = 71;
    private static final Integer PARKING_PLACE_ID = 31;
    private static final Integer MAP_ID = 8801;

    @Mock
    private VanServicePointMappingRepository vanServicePointMappingRepository;

    @InjectMocks
    private VanServicePointMappingServiceImpl service;

    private static Object[] mappingRow() {
        return new Object[] { MAP_ID, VAN_ID, (short) 1, 88, "Attibele PHC", PSM_ID, Boolean.FALSE };
    }

    private static Object[] detailedMappingRow() {
        return new Object[] { MAP_ID, VAN_ID, (short) 1, 88, "Attibele PHC", PSM_ID, Boolean.FALSE, 301,
                "Bengaluru Urban", 3011, "Anekal" };
    }

    @Test
    @DisplayName("getAvailableVanServicePointMappings should rebuild one visit per row the query answers")
    void getAvailable_shouldRebuildEachRow() {
        when(vanServicePointMappingRepository.getAvailableVanServicePointMappings(PARKING_PLACE_ID, VAN_ID, PSM_ID))
                .thenReturn(List.<Object[]>of(mappingRow()));

        ArrayList<M_VanServicePointMap> visits = service.getAvailableVanServicePointMappings(PARKING_PLACE_ID,
                VAN_ID, PSM_ID);

        assertEquals(1, visits.size());
        assertEquals("Attibele PHC", visits.get(0).getServicePointName());
        assertEquals(VAN_ID, visits.get(0).getVanID());
        assertEquals((short) 1, visits.get(0).getVanSession());
    }

    @Test
    @DisplayName("getAvailableVanServicePointMappings should answer nothing when the van visits nowhere")
    void getAvailable_shouldAnswerNothingWhenNoVisits() {
        when(vanServicePointMappingRepository.getAvailableVanServicePointMappings(PARKING_PLACE_ID, VAN_ID, PSM_ID))
                .thenReturn(new ArrayList<>());

        assertTrue(service.getAvailableVanServicePointMappings(PARKING_PLACE_ID, VAN_ID, PSM_ID).isEmpty());
    }

    @Test
    @DisplayName("getAvailableVanServicePointMappingsV1 should also carry the district and taluk of each visit")
    void getAvailableV1_shouldCarryDistrictAndTaluk() {
        when(vanServicePointMappingRepository.getAvailableVanServicePointMappingsV1(PARKING_PLACE_ID, VAN_ID,
                PSM_ID)).thenReturn(List.<Object[]>of(detailedMappingRow()));

        ArrayList<M_VanServicePointMap> visits = service.getAvailableVanServicePointMappingsV1(PARKING_PLACE_ID,
                VAN_ID, PSM_ID);

        assertEquals(1, visits.size());
        assertEquals("Bengaluru Urban", visits.get(0).getDistrictName());
        assertEquals("Anekal", visits.get(0).getBlockName());
    }

    @Test
    @DisplayName("getAvailableVanServicePointMappingsV1 should answer nothing when the van visits nowhere")
    void getAvailableV1_shouldAnswerNothingWhenNoVisits() {
        when(vanServicePointMappingRepository.getAvailableVanServicePointMappingsV1(PARKING_PLACE_ID, VAN_ID,
                PSM_ID)).thenReturn(new ArrayList<>());

        assertTrue(service.getAvailableVanServicePointMappingsV1(PARKING_PLACE_ID, VAN_ID, PSM_ID).isEmpty());
    }

    @Test
    @DisplayName("saveVanServicePointMappings should answer the visits the repository stored")
    void save_shouldAnswerStoredVisits() {
        ArrayList<M_VanServicePointMap> stored = new ArrayList<>(List.of(new M_VanServicePointMap()));
        when(vanServicePointMappingRepository.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.saveVanServicePointMappings(new ArrayList<>()));
    }

    @Test
    @DisplayName("updateVanServicePointMappingStatus should report how many rows the retirement touched")
    void updateStatus_shouldReportRowsTouched() {
        M_VanServicePointMap request = new M_VanServicePointMap();
        request.setVanServicePointMapID(MAP_ID);
        request.setDeleted(Boolean.TRUE);
        request.setModifiedBy("admin");
        when(vanServicePointMappingRepository.updateVanServicePointMappingStatus(MAP_ID, Boolean.TRUE, "admin"))
                .thenReturn(1);

        assertEquals(1, service.updateVanServicePointMappingStatus(request));
    }

    @Test
    @DisplayName("updateVanServicePointMappingStatus should report nothing touched when the visit is unknown")
    void updateStatus_shouldReportNothingTouchedForUnknownVisit() {
        M_VanServicePointMap request = new M_VanServicePointMap();
        request.setVanServicePointMapID(-1);
        when(vanServicePointMappingRepository.updateVanServicePointMappingStatus(-1, null, null)).thenReturn(0);

        assertEquals(0, service.updateVanServicePointMappingStatus(request));
    }

    @Test
    @DisplayName("getVanServicePointMappingByID should answer the visit asked for")
    void getById_shouldAnswerNamedVisit() {
        M_VanServicePointMap stored = new M_VanServicePointMap();
        when(vanServicePointMappingRepository.getVanServicePointMapping(MAP_ID)).thenReturn(stored);

        assertSame(stored, service.getVanServicePointMappingByID(MAP_ID));
    }

    @Test
    @DisplayName("getVanServicePointMappingByID should answer nothing when the visit is unknown")
    void getById_shouldAnswerNothingForUnknownVisit() {
        when(vanServicePointMappingRepository.getVanServicePointMapping(-1)).thenReturn(null);

        assertNull(service.getVanServicePointMappingByID(-1));
    }
}
