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
package com.iemr.admin.service.zonemaster;

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

import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.data.zonemaster.M_ZoneDistrictMap;
import com.iemr.admin.repository.zonemaster.ZoneDistrictMappingRepo;
import com.iemr.admin.repository.zonemaster.ZoneMasterRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The zone service rebuilds zones and their district mappings out of positional
 * query results, and cascades a retired zone onto the districts under it.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ZoneMasterServiceImpl Test Suite")
class ZoneMasterServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer ZONE_ID = 61;

    @Mock
    private ZoneMasterRepository zoneMasterRepo;

    @Mock
    private ZoneDistrictMappingRepo zoneDistrictMappingRepo;

    @InjectMocks
    private ZoneMasterServiceImpl service;

    private static Object[] zoneRow() {
        return new Object[] { ZONE_ID, "North zone", "Northern districts", "Main Road", PSM_ID, Boolean.FALSE,
                29, "Karnataka", 301, "Bengaluru Urban", 401, "North block", 501, "Hosur", 1, "India", null,
                91, "N" };
    }

    private static Object[] mappingRow() {
        return new Object[] { 9001, ZONE_ID, "North zone", 301, PSM_ID, Boolean.FALSE, 29, "Karnataka",
                "Bengaluru Urban", 1, "N", Boolean.FALSE };
    }

    @Test
    @DisplayName("createZone should hand its batch to the repository")
    void createZone_shouldHandBatchToRepository() {
        ArrayList<M_Zone> stored = new ArrayList<>(List.of(new M_Zone()));
        when(zoneMasterRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.createZone(new ArrayList<>()));
    }

    @Test
    @DisplayName("getAvailableZones should rebuild one zone per row the query answers")
    void getAvailableZones_shouldRebuildEachRow() {
        when(zoneMasterRepo.getAvailableZones(PSM_ID)).thenReturn(List.<Object[]>of(zoneRow()));

        ArrayList<M_Zone> zones = service.getAvailableZones(PSM_ID);

        assertEquals(1, zones.size());
        assertEquals("North zone", zones.get(0).getZoneName());
    }

    @Test
    @DisplayName("createZoneDistrictMapping should hand its batch to the repository")
    void createZoneDistrictMapping_shouldHandBatchToRepository() {
        ArrayList<M_ZoneDistrictMap> stored = new ArrayList<>(List.of(new M_ZoneDistrictMap()));
        when(zoneDistrictMappingRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.createZoneDistrictMapping(new ArrayList<>()));
    }

    @Test
    @DisplayName("getAvailableZoneDistrictMappings should rebuild one mapping per row the query answers")
    void getAvailableZoneDistrictMappings_shouldRebuildEachRow() {
        when(zoneDistrictMappingRepo.getAvailableZoneDistrictMappings(PSM_ID))
                .thenReturn(List.<Object[]>of(mappingRow()));

        assertEquals(1, service.getAvailableZoneDistrictMappings(PSM_ID).size());
    }

    @Test
    @DisplayName("updateZoneStatus should carry the change onto the districts under the zone")
    void updateZoneStatus_shouldCascadeToDistricts() {
        M_Zone request = new M_Zone(ZONE_ID, "North zone", null, null, PSM_ID, Boolean.TRUE, 1, "India",
                29, "Karnataka", 301, "Bengaluru Urban", 401, "North block", 501, "Hosur", null, 3,
                "Tele Medicine");
        request.setModifiedBy("admin");
        when(zoneMasterRepo.updateZoneStatus(ZONE_ID, Boolean.TRUE, "admin")).thenReturn(1);
        when(zoneDistrictMappingRepo.getAvailableZoneDistrictMappingss("61"))
                .thenReturn(List.<Object[]>of(mappingRow()));
        when(zoneDistrictMappingRepo.updateZoneDistrictMappingStatus(anyInt(), org.mockito.ArgumentMatchers.any(),
                anyString())).thenReturn(1);

        assertEquals(1, service.updateZoneStatus(request));
        verify(zoneDistrictMappingRepo).updateZoneDistrictMappingStatus(9001, Boolean.TRUE, "admin");
    }

    @Test
    @DisplayName("updateZoneDistrictMappingStatus should reach the repository query")
    void updateZoneDistrictMappingStatus_shouldReachRepository() {
        M_ZoneDistrictMap request = new M_ZoneDistrictMap(9001, ZONE_ID, "North zone", 301, PSM_ID,
                Boolean.TRUE, 29, "Karnataka", "Bengaluru Urban", 3, "Tele Medicine", Boolean.FALSE);
        request.setModifiedBy("admin");
        when(zoneDistrictMappingRepo.updateZoneDistrictMappingStatus(9001, Boolean.TRUE, "admin")).thenReturn(1);

        assertEquals(1, service.updateZoneDistrictMappingStatus(request));
    }

    @Test
    @DisplayName("the remaining calls should each reach their own repository query")
    void remainingCalls_shouldReachTheirOwnQuery() {
        M_Zone zone = new M_Zone();
        M_ZoneDistrictMap mapping = new M_ZoneDistrictMap();
        when(zoneMasterRepo.save(zone)).thenReturn(zone);
        when(zoneMasterRepo.getZoneById(ZONE_ID)).thenReturn(zone);
        when(zoneDistrictMappingRepo.findByZoneDistrictMapID(9001)).thenReturn(mapping);
        when(zoneDistrictMappingRepo.save(mapping)).thenReturn(mapping);

        assertSame(zone, service.updateZoneData(zone));
        assertSame(zone, service.getzoneByID(ZONE_ID));
        assertSame(mapping, service.editZoneDistrictMapping(9001));
        assertSame(mapping, service.saveeditedData(mapping));
    }

    @Test
    @DisplayName("editZoneDistrictMapping1 should rebuild one district per row the query answers")
    void editZoneDistrictMapping1_shouldRebuildEachRow() {
        when(zoneDistrictMappingRepo.editZoneDistrictMapping1(ZONE_ID))
                .thenReturn(List.<Object[]>of(new Object[] { "Bengaluru Urban", 301 }));

        assertEquals(1, service.editZoneDistrictMapping1(ZONE_ID).size());
    }

    @Test
    @DisplayName("getAllMappedRecord should report whether the zone maps more than one district")
    void getAllMappedRecord_shouldReportWhetherZoneMapsMany() {
        when(zoneDistrictMappingRepo.getRecord(ZONE_ID)).thenReturn(List.of(new Object(), new Object()));
        assertEquals(100, service.getAllMappedRecord(ZONE_ID));

        when(zoneDistrictMappingRepo.getRecord(ZONE_ID)).thenReturn(List.of(new Object()));
        assertEquals(200, service.getAllMappedRecord(ZONE_ID));
    }
}
