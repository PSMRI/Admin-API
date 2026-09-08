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
package com.iemr.admin.service.locationmaster;

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

import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.M_ProviderServiceAddMapping;
import com.iemr.admin.data.locationmaster.Showofficedetails;
import com.iemr.admin.data.locationmaster.StateServiceMapping1;
import com.iemr.admin.repo.locationmaster.LocationMasterRepo;
import com.iemr.admin.repo.locationmaster.MdistrictRepo;
import com.iemr.admin.repo.locationmaster.M_ProviderServiceAddMappingRepo;
import com.iemr.admin.repo.locationmaster.ShowofficedetailsRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The location service reads a provider's office addresses out of several
 * differently shaped queries and reshapes them into one carrier.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LocationMasterServiceImpl Test Suite")
class LocationMasterServiceImplTest {

    private static final Integer PROVIDER_ID = 77;
    private static final Integer PSM_ID = 4001;

    @Mock
    private ShowofficedetailsRepo showofficedetailsRepo;

    @Mock
    private MdistrictRepo mdistricRepo;

    @Mock
    private M_ProviderServiceAddMappingRepo m_ProviderServiceAddMappingRepo;

    @Mock
    private LocationMasterRepo locationMasterRepo;

    @InjectMocks
    private LocationMasterServiceImpl service;

    @Test
    @DisplayName("getStateByServiceProviderId should skip a row the query could not fill")
    void getStateByServiceProviderId_shouldSkipUnfillableRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new Object[] { 29, "Karnataka", 1, PSM_ID });
        when(locationMasterRepo.getStateByServiceProviderId(PROVIDER_ID)).thenReturn(rows);

        assertEquals(1, service.getStateByServiceProviderId(PROVIDER_ID).size());
    }

    @Test
    @DisplayName("getServiceByServiceProviderIdAndStateId should rebuild one mapping per row")
    void getServiceByServiceProviderIdAndStateId_shouldRebuildEachRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { 3, PSM_ID, "Tele Medicine" });
        when(locationMasterRepo.getServiceByServiceProviderIdAndStateId(PROVIDER_ID, 29)).thenReturn(rows);

        assertEquals(1, service.getServiceByServiceProviderIdAndStateId(PROVIDER_ID, 29).size());
    }

    @Test
    @DisplayName("getStatesByServiceId should rebuild one mapping per row")
    void getStatesByServiceId_shouldRebuildEachRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new Object[] { 29, "Karnataka", PSM_ID });
        when(locationMasterRepo.getStatesByServiceId(3, PROVIDER_ID)).thenReturn(rows);

        assertEquals(1, service.getStatesByServiceId(3, PROVIDER_ID).size());
    }

    @Test
    @DisplayName("getAllDistrictByStateId should rebuild each district into a plain carrier")
    void getAllDistrictByStateId_shouldRebuildEachDistrict() {
        M_District stored = new M_District();
        stored.setDistrictID(301);
        stored.setDistrictName("Bengaluru Urban");
        when(mdistricRepo.getAllDistrictByStateId(29)).thenReturn(new ArrayList<>(List.of(stored)));

        ArrayList<M_District> districts = service.getAllDistrictByStateId(29);

        assertEquals(1, districts.size());
        assertEquals("Bengaluru Urban", districts.get(0).getDistrictName());
    }

    @Test
    @DisplayName("getlocationByMapid1 should gather the offices of every mapping the caller lists")
    void getlocationByMapid1_shouldGatherAcrossMappings() {
        Showofficedetails office = new Showofficedetails();
        when(showofficedetailsRepo.getlocationByMapid1(4001)).thenReturn(new ArrayList<>(List.of(office)));
        when(showofficedetailsRepo.getlocationByMapid1(4002)).thenReturn(new ArrayList<>(List.of(office)));

        assertEquals(2, service.getlocationByMapid1(new ArrayList<>(List.of(4001, 4002))).size());
    }

    @Test
    @DisplayName("getOfficeName should gather the office of every mapping the caller lists")
    void getOfficeName_shouldGatherAcrossMappings() {
        Showofficedetails request = new Showofficedetails();
        request.setProviderServiceMapID(PSM_ID);
        when(showofficedetailsRepo.getOfficeName(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(new Showofficedetails())));

        assertEquals(1, service.getOfficeName(new ArrayList<>(List.of(request))).size());
    }

    @Test
    @DisplayName("the remaining calls should each reach their own repository query")
    void remainingCalls_shouldReachTheirOwnQuery() {
        M_ProviderServiceAddMapping address = new M_ProviderServiceAddMapping();
        ArrayList<M_ProviderServiceAddMapping> addresses = new ArrayList<>(List.of(address));
        ArrayList<Showofficedetails> offices = new ArrayList<>(List.of(new Showofficedetails()));
        StateServiceMapping1 mapping = new StateServiceMapping1(PSM_ID);
        ArrayList<StateServiceMapping1> mappings = new ArrayList<>(List.of(mapping));
        when(m_ProviderServiceAddMappingRepo.save(address)).thenReturn(address);
        when(m_ProviderServiceAddMappingRepo.saveAll(anyList())).thenReturn(addresses);
        when(m_ProviderServiceAddMappingRepo.editData(51)).thenReturn(address);
        when(m_ProviderServiceAddMappingRepo.getlocationByMapid(PSM_ID)).thenReturn(addresses);
        when(showofficedetailsRepo.getAlldata()).thenReturn(offices);
        when(showofficedetailsRepo.getlocationByMapid(PSM_ID)).thenReturn(offices);
        when(showofficedetailsRepo.getlocationByMapid3(PSM_ID, 301)).thenReturn(offices);
        when(locationMasterRepo.getProviderServiceMapID(PROVIDER_ID, 29, 3)).thenReturn(mapping);
        when(locationMasterRepo.getAllByMapId2(PROVIDER_ID, 29, 3)).thenReturn(mappings);
        when(locationMasterRepo.getAllByMapId3(PROVIDER_ID, 3)).thenReturn(mappings);
        when(locationMasterRepo.getLocationByServiceID(PROVIDER_ID, 3)).thenReturn(mappings);
        when(locationMasterRepo.getLocationByStateID(PROVIDER_ID, 29)).thenReturn(mappings);

        assertSame(address, service.addlocation(address));
        assertSame(addresses, service.addlocation(new ArrayList<>()));
        assertSame(address, service.editData(51));
        assertSame(address, service.saveEditData(address));
        assertSame(addresses, service.getlocationByMapid(PSM_ID));
        assertSame(offices, service.getAlldata());
        assertSame(offices, service.getlocationByMapid2(PSM_ID));
        assertSame(offices, service.getlocationByMapid4(PSM_ID, 301));
        assertSame(mapping, service.getAllByMapId(PROVIDER_ID, 29, 3));
        assertSame(mappings, service.getAllByMapId2(PROVIDER_ID, 29, 3));
        assertSame(mappings, service.getAllByMapId3(PROVIDER_ID, 3));
        assertSame(mappings, service.getLocationByServiceId(PROVIDER_ID, 3));
        assertSame(mappings, service.getLocationBySateID(PROVIDER_ID, 29));
    }
}
