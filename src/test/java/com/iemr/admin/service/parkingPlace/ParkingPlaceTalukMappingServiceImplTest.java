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
package com.iemr.admin.service.parkingPlace;

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

import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMappingTO;
import com.iemr.admin.mapper.parkingplacetalukmapping.ParkingPlaceTalukMappingMapper;
import com.iemr.admin.repo.locationmaster.DistrictBlockRepo;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceTalukMappingRepository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The taluk mapping service records which taluks a parking place covers, and
 * offers the remaining taluks of a district as the candidates for a new one.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ParkingPlaceTalukMappingServiceImpl Test Suite")
class ParkingPlaceTalukMappingServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer PARKING_PLACE_ID = 31;
    private static final Integer DISTRICT_ID = 301;

    @Mock
    private ParkingPlaceTalukMappingRepository parkingPlaceTalukMappingRepository;

    @Mock
    private DistrictBlockRepo districtBlockRepo;

    @Mock
    private ParkingPlaceTalukMappingMapper parkingPlaceTalukMappingMapper;

    @InjectMocks
    private ParkingPlaceTalukMappingServiceImpl service;

    private static ParkingplaceTalukMapping mapping() {
        ParkingplaceTalukMapping mapping = new ParkingplaceTalukMapping();
        mapping.setPpSubDistrictMapID(7001);
        mapping.setParkingPlaceID(PARKING_PLACE_ID);
        mapping.setDistrictID(DISTRICT_ID);
        mapping.setDistrictBlockID(3011);
        mapping.setProviderServiceMapID(PSM_ID);
        return mapping;
    }

    @Test
    @DisplayName("saveParkingPlaceTalukMapping should answer the mappings the repository stored")
    void save_shouldAnswerStoredMappings() {
        ArrayList<ParkingplaceTalukMapping> stored = new ArrayList<>(List.of(mapping()));
        when(parkingPlaceTalukMappingRepository.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.saveParkingPlaceTalukMapping(new ArrayList<>()));
    }

    @Test
    @DisplayName("updateParkingPlaceTalukMapping and findbyID should each reach their own repository query")
    void singleRecordOperations_shouldReachTheirOwnQuery() {
        ParkingplaceTalukMapping stored = mapping();
        when(parkingPlaceTalukMappingRepository.save(stored)).thenReturn(stored);
        when(parkingPlaceTalukMappingRepository.findByPpSubDistrictMapID(7001)).thenReturn(stored);

        assertSame(stored, service.updateParkingPlaceTalukMapping(stored));
        assertSame(stored, service.findbyID(7001));
    }

    @Test
    @DisplayName("findbyProviderservicemapid should answer the taluks the parking place covers")
    void findbyProviderservicemapid_shouldAnswerCoveredTaluks() {
        List<ParkingplaceTalukMapping> rows = List.of(mapping());
        List<ParkingplaceTalukMappingTO> published = List.of(new ParkingplaceTalukMappingTO());
        when(parkingPlaceTalukMappingRepository.findByParkingPlaceID(PARKING_PLACE_ID)).thenReturn(rows);
        when(parkingPlaceTalukMappingMapper.getParkingplaceTalukMappingMapList(rows)).thenReturn(published);

        assertSame(published, service.findbyProviderservicemapid(mapping()));
    }

    @Test
    @DisplayName("findbyParkingplaceAndDistrictID should narrow the mappings to the district the caller names")
    void findbyParkingplaceAndDistrictID_shouldNarrowToDistrict() {
        List<ParkingplaceTalukMapping> rows = List.of(mapping());
        List<ParkingplaceTalukMappingTO> published = List.of(new ParkingplaceTalukMappingTO());
        when(parkingPlaceTalukMappingRepository
                .findByParkingPlaceIDAndDistrictIDOrderByM_DistrictDistrictNameAsc(PARKING_PLACE_ID, DISTRICT_ID))
                        .thenReturn(rows);
        when(parkingPlaceTalukMappingMapper.getParkingplaceTalukMappingMapList(rows)).thenReturn(published);

        assertSame(published, service.findbyParkingplaceAndDistrictID(mapping()));
    }

    @Test
    @DisplayName("getunmappedtaluk should exclude the taluks already covered when there are any")
    void getunmappedtaluk_shouldExcludeCoveredTaluks() {
        List<Integer> covered = List.of(3011);
        List<DistrictBlock> remaining = List.of(new DistrictBlock(3012, "Hoskote"));
        when(parkingPlaceTalukMappingRepository.finbyDistrictID(DISTRICT_ID, PSM_ID)).thenReturn(covered);
        when(districtBlockRepo.findunmapped(covered, DISTRICT_ID)).thenReturn(remaining);

        assertSame(remaining, service.getunmappedtaluk(DISTRICT_ID, PSM_ID));
        verify(districtBlockRepo, never()).findall(anyInt());
    }

    @Test
    @DisplayName("getunmappedtaluk should offer every taluk of the district when none is covered yet")
    void getunmappedtaluk_shouldOfferEveryTalukWhenNoneCovered() {
        List<DistrictBlock> all = List.of(new DistrictBlock(3011, "Anekal"), new DistrictBlock(3012, "Hoskote"));
        when(parkingPlaceTalukMappingRepository.finbyDistrictID(DISTRICT_ID, PSM_ID)).thenReturn(new ArrayList<>());
        when(districtBlockRepo.findall(DISTRICT_ID)).thenReturn(all);

        assertSame(all, service.getunmappedtaluk(DISTRICT_ID, PSM_ID));
        verify(districtBlockRepo, never()).findunmapped(anyList(), anyInt());
    }
}
