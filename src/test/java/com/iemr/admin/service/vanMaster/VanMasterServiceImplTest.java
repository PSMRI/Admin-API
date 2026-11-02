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
package com.iemr.admin.service.vanMaster;

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
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.data.vanType.M_VanType;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceRepository;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.repository.vanType.VanTypeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The van service keeps the mobile unit fleet: which vans exist, what type each
 * is, and which parking place each is stationed at.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VanMasterServiceImpl Test Suite")
class VanMasterServiceImplTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer VAN_ID = 71;
    private static final Integer PARKING_PLACE_ID = 31;

    @Mock
    private VanMasterRepository vanMasterRepository;

    @Mock
    private VanTypeRepository vanTypeRepository;

    @Mock
    private ParkingPlaceRepository parkingPlaceRepository;

    @InjectMocks
    private VanMasterServiceImpl service;

    private static Object[] fleetRow() {
        return new Object[] { VAN_ID, "Mobile unit 7", "KA-01-AB-1234", 2, "Diagnostic van", Boolean.FALSE, PSM_ID,
                1, "India", 29, "Karnataka", PARKING_PLACE_ID, "Hosur parking", 3011, "psmri", "990011",
                "van7@example.org", false };
    }

    @Test
    @DisplayName("getAvailableVans should rebuild one van per row the query answers")
    void getAvailableVans_shouldRebuildEachRow() {
        when(vanMasterRepository.getAvailableVans("31", "2", PSM_ID)).thenReturn(List.<Object[]>of(fleetRow()));

        ArrayList<M_Van> vans = service.getAvailableVans(PARKING_PLACE_ID, 2, PSM_ID);

        assertEquals(1, vans.size());
        assertEquals("Mobile unit 7", vans.get(0).getVanName());
        assertEquals("KA-01-AB-1234", vans.get(0).getVehicalNo());
        assertEquals("Hosur parking", vans.get(0).getParkingPlaceName());
    }

    @Test
    @DisplayName("getAvailableVans should match any parking place or type the caller leaves out")
    void getAvailableVans_shouldWildcardOmittedFilters() {
        when(vanMasterRepository.getAvailableVans("%%", "%%", PSM_ID)).thenReturn(new ArrayList<>());

        assertTrue(service.getAvailableVans(null, null, PSM_ID).isEmpty());
        verify(vanMasterRepository).getAvailableVans("%%", "%%", PSM_ID);
    }

    @Test
    @DisplayName("saveVanDetails and saveVanTypeDetails should answer what the repository stored")
    void saveOperations_shouldAnswerStoredRecords() {
        ArrayList<M_Van> vans = new ArrayList<>(List.of(new M_Van()));
        ArrayList<M_VanType> types = new ArrayList<>(List.of(new M_VanType()));
        when(vanMasterRepository.saveAll(anyList())).thenReturn(vans);
        when(vanTypeRepository.saveAll(anyList())).thenReturn(types);

        assertSame(vans, service.saveVanDetails(new ArrayList<>()));
        assertSame(types, service.saveVanTypeDetails(new ArrayList<>()));
    }

    @Test
    @DisplayName("updateVanStatus should report how many rows the retirement touched")
    void updateVanStatus_shouldReportRowsTouched() {
        M_Van request = new M_Van();
        request.setVanID(VAN_ID);
        request.setDeleted(Boolean.TRUE);
        request.setModifiedBy("admin");
        when(vanMasterRepository.updateVanStatus(VAN_ID, Boolean.TRUE, "admin")).thenReturn(1);

        assertEquals(1, service.updateVanStatus(request));
    }

    @Test
    @DisplayName("updateVanStatus should report nothing touched when the van is unknown")
    void updateVanStatus_shouldReportNothingTouchedForUnknownVan() {
        M_Van request = new M_Van();
        request.setVanID(-1);
        when(vanMasterRepository.updateVanStatus(-1, null, null)).thenReturn(0);

        assertEquals(0, service.updateVanStatus(request));
    }

    @Test
    @DisplayName("updateVanTypeStatus should report how many rows the retirement touched")
    void updateVanTypeStatus_shouldReportRowsTouched() {
        M_VanType request = new M_VanType(2, "Diagnostic van", "Carries lab kit", Boolean.TRUE);
        request.setModifiedBy("admin");
        when(vanTypeRepository.updateVanTypeStatus(2, Boolean.TRUE, "admin")).thenReturn(1);

        assertEquals(1, service.updateVanTypeStatus(request));
    }

    @Test
    @DisplayName("getVanTypes should rebuild one van type per row the query answers")
    void getVanTypes_shouldRebuildEachRow() {
        when(vanTypeRepository.getVanTypes())
                .thenReturn(List.<Object[]>of(new Object[] { 2, "Diagnostic van", "Carries lab kit", Boolean.FALSE }));

        ArrayList<M_VanType> types = service.getVanTypes();

        assertEquals(1, types.size());
        assertEquals("Diagnostic van", types.get(0).getVanType());
        assertEquals("Carries lab kit", types.get(0).getVanTypeDesc());
    }

    @Test
    @DisplayName("getVanTypes should answer nothing when no van type is on file")
    void getVanTypes_shouldAnswerNothingWhenNoneOnFile() {
        when(vanTypeRepository.getVanTypes()).thenReturn(new ArrayList<>());

        assertTrue(service.getVanTypes().isEmpty());
    }

    @Test
    @DisplayName("getVanByID, updateVanData and getVanMaster should each reach their own query")
    void singleRecordOperations_shouldReachTheirOwnQuery() {
        M_Van stored = new M_Van();
        List<M_Van> stationed = List.of(stored);
        when(vanMasterRepository.getVanById(VAN_ID)).thenReturn(stored);
        when(vanMasterRepository.save(stored)).thenReturn(stored);
        when(vanMasterRepository.findByProviderServiceMapIDAndParkingPlaceID(PSM_ID, PARKING_PLACE_ID))
                .thenReturn(stationed);

        assertSame(stored, service.getVanByID(VAN_ID));
        assertSame(stored, service.updateVanData(stored));
        assertSame(stationed, service.getVanMaster(PSM_ID, PARKING_PLACE_ID));
    }

    @Test
    @DisplayName("getVanFromFacilityID should answer the vans stationed at the store's parking place")
    void getVanFromFacilityID_shouldAnswerVansAtStoresParkingPlace() throws Exception {
        M_Parkingplace place = new M_Parkingplace();
        place.setParkingPlaceID(PARKING_PLACE_ID);
        place.setProviderServiceMapID(PSM_ID);
        List<M_Van> stationed = List.of(new M_Van());
        when(parkingPlaceRepository.findFirstByFacilityID(9001)).thenReturn(place);
        when(vanMasterRepository.findByProviderServiceMapIDAndParkingPlaceID(PSM_ID, PARKING_PLACE_ID))
                .thenReturn(stationed);

        assertSame(stationed, service.getVanFromFacilityID(9001));
    }

    @Test
    @DisplayName("getVanFromFacilityID should refuse a store that has no parking place of its own")
    void getVanFromFacilityID_shouldRefuseStoreWithoutParkingPlace() {
        when(parkingPlaceRepository.findFirstByFacilityID(9001)).thenReturn(null);

        Exception refusal = assertThrows(Exception.class, () -> service.getVanFromFacilityID(9001));

        assertEquals("Main Store doesnt have any Parking place mapped", refusal.getMessage());
    }

    @Test
    @DisplayName("getVanFromFacilityID should refuse a parking place record that names no parking place")
    void getVanFromFacilityID_shouldRefuseNamelessParkingPlace() {
        when(parkingPlaceRepository.findFirstByFacilityID(9001)).thenReturn(new M_Parkingplace());

        assertThrows(Exception.class, () -> service.getVanFromFacilityID(9001));
    }
}
