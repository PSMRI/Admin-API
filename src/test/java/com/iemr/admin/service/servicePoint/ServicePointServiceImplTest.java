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
package com.iemr.admin.service.servicePoint;

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
import com.iemr.admin.data.servicePoint.M_Servicepoint;
import com.iemr.admin.data.servicePoint.M_Servicepointvillagemap;
import com.iemr.admin.repo.locationmaster.DistrictBranchMappingRepo;
import com.iemr.admin.repository.servicePoint.ServicePointRepository;
import com.iemr.admin.repository.servicePoint.ServicePointVillageMapRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The service point service turns partly-filled location filters into wildcard
 * queries, so an operator who leaves a filter blank still sees every match.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ServicePointServiceImpl Test Suite")
class ServicePointServiceImplTest {

    private static final Integer PROVIDER_ID = 77;
    private static final Integer PSM_ID = 4001;
    private static final Integer POINT_ID = 71;

    @Mock
    private ServicePointRepository servicePointRepository;

    @Mock
    private DistrictBranchMappingRepo districtBranchMappingRepo;

    @Mock
    private ServicePointVillageMapRepository servicePointVillageMapRepository;

    @InjectMocks
    private ServicePointServiceImpl service;

    private static Object[] pointRow() {
        return new Object[] { POINT_ID, "Hosur halt", "Weekly halt", "Main Road", PSM_ID, Boolean.FALSE,
                29, "Karnataka", 301, "Bengaluru Urban", 401, "North block", 501, "Hosur", 1, "India", null,
                3, "Mobile Medical Unit", 31, "Hosur parking" };
    }

    private static Object[] villageRow() {
        return new Object[] { 9001, 29, "Karnataka", 301, "Bengaluru Urban", 401, "North block", 501, "Hosur",
                POINT_ID, "Hosur halt", PSM_ID, Boolean.FALSE, null, null, 31, "Hosur parking" };
    }

    @Test
    @DisplayName("saveServicePoint should hand its batch to the repository")
    void saveServicePoint_shouldHandBatchToRepository() {
        ArrayList<M_Servicepoint> stored = new ArrayList<>(List.of(new M_Servicepoint()));
        when(servicePointRepository.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.saveServicePoint(new ArrayList<>()));
    }

    @Test
    @DisplayName("getAvailableServicePoints should pass every filter the caller supplies through")
    void getAvailableServicePoints_shouldPassFiltersThrough() {
        when(servicePointRepository.getAvailableServicePoints("29", "301", "31", PROVIDER_ID))
                .thenReturn(List.<Object[]>of(pointRow()));

        assertEquals(1, service.getAvailableServicePoints(29, 301, 31, PROVIDER_ID).size());
    }

    @Test
    @DisplayName("getAvailableServicePoints should match everything for a filter the caller leaves blank")
    void getAvailableServicePoints_shouldWildcardBlankFilters() {
        when(servicePointRepository.getAvailableServicePoints("%%", "%%", "%%", PROVIDER_ID))
                .thenReturn(new ArrayList<>());

        service.getAvailableServicePoints(null, null, null, PROVIDER_ID);

        verify(servicePointRepository).getAvailableServicePoints("%%", "%%", "%%", PROVIDER_ID);
    }

    @Test
    @DisplayName("getAvailableServicePointVillageMaps should pass every filter the caller supplies through")
    void getVillageMaps_shouldPassFiltersThrough() {
        when(servicePointVillageMapRepository
                .getAvailableServicePointVillageMaps("29", "301", "31", "71", PROVIDER_ID))
                .thenReturn(List.<Object[]>of(villageRow()));

        assertEquals(1, service.getAvailableServicePointVillageMaps(29, 301, 31, POINT_ID, PROVIDER_ID).size());
    }

    @Test
    @DisplayName("getAvailableServicePointVillageMaps should match everything for filters left blank")
    void getVillageMaps_shouldWildcardBlankFilters() {
        when(servicePointVillageMapRepository
                .getAvailableServicePointVillageMaps("%%", "%%", "%%", "%%", PROVIDER_ID))
                .thenReturn(new ArrayList<>());

        service.getAvailableServicePointVillageMaps(null, null, null, null, PROVIDER_ID);

        verify(servicePointVillageMapRepository)
                .getAvailableServicePointVillageMaps("%%", "%%", "%%", "%%", PROVIDER_ID);
    }

    @Test
    @DisplayName("the status updates should each reach their own repository query")
    void statusUpdates_shouldReachTheirOwnQuery() {
        M_Servicepoint point = new M_Servicepoint(POINT_ID, "Hosur halt", "Weekly halt", "Main Road", PSM_ID,
                Boolean.TRUE, 1, "India", 29, "Karnataka", 301, "Bengaluru Urban", 401, "North block", 501,
                "Hosur", null, 3, "Mobile Medical Unit", 31, "Hosur parking");
        point.setModifiedBy("admin");
        M_Servicepointvillagemap map = new M_Servicepointvillagemap(9001, 29, "Karnataka", 301,
                "Bengaluru Urban", 31, "Hosur parking", POINT_ID, "Hosur halt", 501, "Hosur", PSM_ID,
                Boolean.TRUE);
        map.setModifiedBy("admin");
        when(servicePointRepository.updateServicePointStatus(POINT_ID, Boolean.TRUE, "admin")).thenReturn(1);
        when(servicePointVillageMapRepository.updateServicePointStatus(9001, Boolean.TRUE, "admin")).thenReturn(1);

        assertEquals(1, service.updateServicePointStatus(point));
        assertEquals(1, service.updateServicePointVillageMapStatus(map));
    }

    @Test
    @DisplayName("the record lookups should each reach their own repository query")
    void recordLookups_shouldReachTheirOwnQuery() {
        M_Servicepoint point = new M_Servicepoint();
        M_Servicepointvillagemap map = new M_Servicepointvillagemap();
        ArrayList<M_Servicepointvillagemap> maps = new ArrayList<>(List.of(map));
        when(servicePointRepository.findByServicePointID(POINT_ID)).thenReturn(point);
        when(servicePointRepository.save(point)).thenReturn(point);
        when(servicePointVillageMapRepository.findByServicePointVillageMapID(9001)).thenReturn(map);
        when(servicePointVillageMapRepository.save(map)).thenReturn(map);
        when(servicePointVillageMapRepository.saveAll(anyList())).thenReturn(maps);

        assertSame(point, service.getdataForEditServicePointStatus(POINT_ID));
        assertSame(point, service.saveeditedData(point));
        assertSame(map, service.updateServicePointVillageMapStatus(9001));
        assertSame(map, service.saveEditedData(map));
        assertSame(maps, service.saveServicePointVillageMap(new ArrayList<>()));
    }

    @Test
    @DisplayName("getunmappedvillages should exclude the villages already covered when there are any")
    void getunmappedvillages_shouldExcludeCoveredVillages() {
        List<DistrictBranchMapping> expected = List.of(new DistrictBranchMapping());
        when(servicePointVillageMapRepository.finbyTalukID(PSM_ID)).thenReturn(List.of(501));
        when(districtBranchMappingRepo.getunmappedvillage(List.of(501), 401)).thenReturn(expected);

        assertSame(expected, service.getunmappedvillages(PSM_ID, 401));
        verify(districtBranchMappingRepo, never()).getallvillage(anyInt());
    }

    @Test
    @DisplayName("getunmappedvillages should answer every village when none is covered yet")
    void getunmappedvillages_shouldAnswerEveryVillageWhenNoneCovered() {
        List<DistrictBranchMapping> expected = List.of(new DistrictBranchMapping());
        when(servicePointVillageMapRepository.finbyTalukID(PSM_ID)).thenReturn(new ArrayList<>());
        when(districtBranchMappingRepo.getallvillage(401)).thenReturn(expected);

        assertSame(expected, service.getunmappedvillages(PSM_ID, 401));
    }
}
