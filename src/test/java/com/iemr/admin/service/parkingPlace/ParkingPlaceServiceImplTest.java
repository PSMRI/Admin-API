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

import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The parking place service turns the flat rows the reporting queries answer
 * back into parking places, and treats an omitted location filter as "any".
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ParkingPlaceServiceImpl Test Suite")
class ParkingPlaceServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer PARKING_PLACE_ID = 31;

    @Mock
    private ParkingPlaceRepository parkingPlaceRepository;

    @InjectMocks
    private ParkingPlaceServiceImpl service;

    private static Object[] reportingRow() {
        return new Object[] { PARKING_PLACE_ID, "Hosur parking", "Near the bus stand", "Hosur Road", PSM_ID,
                Boolean.FALSE, 1, "India", 29, "Karnataka", 301, "Bengaluru Urban", 3011, "Anekal", 30111,
                "Attibele", new M_ProviderServiceMapping(), 5, "104 Helpline" };
    }

    @Test
    @DisplayName("getAvailableParkingPlaces should rebuild one parking place per row the query answers")
    void getAvailable_shouldRebuildEachRow() {
        when(parkingPlaceRepository.getAvailableParkingPlaces("29", "301", PSM_ID))
                .thenReturn(List.<Object[]>of(reportingRow()));

        ArrayList<M_Parkingplace> places = service.getAvailableParkingPlaces(29, 301, PSM_ID);

        assertEquals(1, places.size());
        assertEquals("Hosur parking", places.get(0).getParkingPlaceName());
        assertEquals("Karnataka", places.get(0).getStateName());
        assertEquals("Hosur Road", places.get(0).getAreaHQAddress());
    }

    @Test
    @DisplayName("getAvailableParkingPlaces should match any state or district the caller leaves out")
    void getAvailable_shouldWildcardOmittedFilters() {
        when(parkingPlaceRepository.getAvailableParkingPlaces("%%", "%%", PSM_ID)).thenReturn(new ArrayList<>());

        assertTrue(service.getAvailableParkingPlaces(null, null, PSM_ID).isEmpty());
        verify(parkingPlaceRepository).getAvailableParkingPlaces("%%", "%%", PSM_ID);
    }

    @Test
    @DisplayName("saveParkingPlace should answer the parking places the repository stored")
    void save_shouldAnswerStoredPlaces() {
        ArrayList<M_Parkingplace> stored = new ArrayList<>(List.of(new M_Parkingplace()));
        when(parkingPlaceRepository.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.saveParkingPlace(new ArrayList<>()));
    }

    @Test
    @DisplayName("updateParkingPlaceStatus should report how many rows the retirement touched")
    void updateStatus_shouldReportRowsTouched() {
        M_Parkingplace request = new M_Parkingplace();
        request.setParkingPlaceID(PARKING_PLACE_ID);
        request.setDeleted(Boolean.TRUE);
        request.setModifiedBy("admin");
        when(parkingPlaceRepository.updateParkingPlaceStatus(PARKING_PLACE_ID, Boolean.TRUE, "admin")).thenReturn(1);

        assertEquals(1, service.updateParkingPlaceStatus(request));
    }

    @Test
    @DisplayName("updateParkingPlaceStatus should report nothing touched when the parking place is unknown")
    void updateStatus_shouldReportNothingTouchedForUnknownPlace() {
        M_Parkingplace request = new M_Parkingplace();
        request.setParkingPlaceID(-1);
        when(parkingPlaceRepository.updateParkingPlaceStatus(-1, null, null)).thenReturn(0);

        assertEquals(0, service.updateParkingPlaceStatus(request));
    }

    @Test
    @DisplayName("the single record lookups should each reach their own repository query")
    void singleRecordLookups_shouldReachTheirOwnQuery() {
        M_Parkingplace stored = new M_Parkingplace();
        List<M_Parkingplace> byProvider = List.of(stored);
        when(parkingPlaceRepository.getParkingPlaceById(PARKING_PLACE_ID)).thenReturn(stored);
        when(parkingPlaceRepository.save(stored)).thenReturn(stored);
        when(parkingPlaceRepository.findByProviderServiceMapID(PSM_ID)).thenReturn(byProvider);

        assertSame(stored, service.getParkingPlaceByID(PARKING_PLACE_ID));
        assertSame(stored, service.updateParkingPlaceData(stored));
        assertSame(byProvider, service.getParkingPlaces(PSM_ID));
    }

    @Test
    @DisplayName("getSubDistrict should answer the taluks the parking place covers")
    void getSubDistrict_shouldAnswerCoveredTaluks() {
        when(parkingPlaceRepository.getSubDistrict(PARKING_PLACE_ID))
                .thenReturn(List.<Object[]>of(new Object[] { PARKING_PLACE_ID, 3011, "Anekal" }));

        List<M_Parkingplace> taluks = service.getSubDistrict(PARKING_PLACE_ID);

        assertEquals(1, taluks.size());
        assertEquals("Anekal", taluks.get(0).getBlockName());
        assertEquals(3011, taluks.get(0).getDistrictBlockID());
    }

    @Test
    @DisplayName("getSubDistrict should answer nothing when the parking place covers no taluk")
    void getSubDistrict_shouldAnswerNothingWhenNoneCovered() {
        when(parkingPlaceRepository.getSubDistrict(PARKING_PLACE_ID)).thenReturn(new ArrayList<>());

        assertTrue(service.getSubDistrict(PARKING_PLACE_ID).isEmpty());
    }

    @Test
    @DisplayName("getAvailableParkingPlacesbyZoneID should name the zone on each parking place it answers")
    void getAvailableByZone_shouldNameTheZone() {
        M_Parkingplace place = new M_Parkingplace();
        place.setParkingPlaceID(PARKING_PLACE_ID);
        M_Zone zone = new M_Zone();
        zone.setZoneName("South zone");
        when(parkingPlaceRepository.getAvailableParkingPlacesbyzoneid(9, PSM_ID))
                .thenReturn(List.<Object[]>of(new Object[] { place, zone }));

        ArrayList<M_Parkingplace> places = service.getAvailableParkingPlacesbyZoneID(9, PSM_ID);

        assertEquals(1, places.size());
        assertEquals("South zone", places.get(0).getZoneName());
    }

    @Test
    @DisplayName("getAvailableParkingPlacesbyZoneID should answer nothing when the zone holds no parking place")
    void getAvailableByZone_shouldAnswerNothingForEmptyZone() {
        when(parkingPlaceRepository.getAvailableParkingPlacesbyzoneid(9, PSM_ID)).thenReturn(new ArrayList<>());

        assertTrue(service.getAvailableParkingPlacesbyZoneID(9, PSM_ID).isEmpty());
    }
}
