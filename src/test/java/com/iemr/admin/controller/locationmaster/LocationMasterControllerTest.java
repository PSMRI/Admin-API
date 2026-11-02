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
package com.iemr.admin.controller.locationmaster;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.locationmaster.M_ProviderServiceAddMapping;
import com.iemr.admin.data.locationmaster.Showofficedetails;
import com.iemr.admin.data.locationmaster.StateServiceMapping1;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The location endpoints keep the office addresses a provider works out of, and
 * choose between a national and a state-scoped lookup depending on the service
 * line.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LocationMasterController Test Suite")
class LocationMasterControllerTest {

    private static final Integer PROVIDER_ID = 77;
    private static final Integer PSM_ID = 4001;

    @Mock
    private LocationMasterServiceInter locationMasterServiceInter;

    @InjectMocks
    private LocationMasterController controller;

    private static StateServiceMapping1 mapping(Integer psmId) {
        return new StateServiceMapping1(psmId);
    }

    private static Showofficedetails office(String name) {
        Showofficedetails office = new Showofficedetails();
        office.setLocationName(name);
        office.setProviderServiceMapID(PSM_ID);
        return office;
    }

    private static M_ProviderServiceAddMapping address(Integer id, String name) {
        M_ProviderServiceAddMapping address = new M_ProviderServiceAddMapping();
        address.setpSAddMapID(id);
        address.setLocationName(name);
        return address;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    private static void assertGenericFailure(String response) {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
    }

    private static void assertCodeException(String response) {
        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(response), response);
    }

    @Test
    @DisplayName("getAllRole2 should resolve the mapping before reading its addresses")
    void getAllRole2_shouldResolveMappingFirst() {
        when(locationMasterServiceInter.getAllByMapId2(PROVIDER_ID, 29, 3))
                .thenReturn(new ArrayList<>(List.of(mapping(PSM_ID))));
        when(locationMasterServiceInter.getlocationByMapid(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(address(51, "Bengaluru Office"))));

        assertSuccessContaining(controller.getAllRole2(
                "{\"serviceProviderID\":77,\"stateID\":29,\"serviceID\":3}"), "Bengaluru Office");
    }

    @Test
    @DisplayName("getAllRole2 should fall back to no mapping when the provider has none")
    void getAllRole2_shouldFallBackWithoutMapping() {
        when(locationMasterServiceInter.getAllByMapId2(any(), any(), any())).thenReturn(new ArrayList<>());
        when(locationMasterServiceInter.getlocationByMapid(0)).thenReturn(new ArrayList<>());

        controller.getAllRole2("{\"serviceProviderID\":77}");

        verify(locationMasterServiceInter).getlocationByMapid(0);
    }

    @Test
    @DisplayName("getAllRole2 should answer an error envelope when the lookup fails")
    void getAllRole2_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getAllByMapId2(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllRole2("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getAlllocation should use the national lookup for a national service line")
    void getAlllocation_shouldUseNationalLookup() {
        when(locationMasterServiceInter.getAllByMapId3(PROVIDER_ID, 3))
                .thenReturn(new ArrayList<>(List.of(mapping(PSM_ID))));
        when(locationMasterServiceInter.getlocationByMapid2(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(office("Bengaluru Office"))));

        assertSuccessContaining(controller.getAlllocation(
                "{\"serviceProviderID\":77,\"serviceID\":3,\"isNational\":true}"), "Bengaluru Office");
        verify(locationMasterServiceInter).getAllByMapId3(PROVIDER_ID, 3);
    }

    @Test
    @DisplayName("getAlllocation should use the state lookup for a state-scoped service line")
    void getAlllocation_shouldUseStateLookup() {
        when(locationMasterServiceInter.getAllByMapId2(PROVIDER_ID, 29, 3))
                .thenReturn(new ArrayList<>(List.of(mapping(PSM_ID))));
        when(locationMasterServiceInter.getlocationByMapid2(PSM_ID)).thenReturn(new ArrayList<>());

        controller.getAlllocation("{\"serviceProviderID\":77,\"stateID\":29,\"serviceID\":3,\"isNational\":false}");

        verify(locationMasterServiceInter).getAllByMapId2(PROVIDER_ID, 29, 3);
    }

    @Test
    @DisplayName("getAlllocation should narrow to the district when the caller names one")
    void getAlllocation_shouldNarrowToDistrict() {
        when(locationMasterServiceInter.getAllByMapId2(any(), any(), any()))
                .thenReturn(new ArrayList<>(List.of(mapping(PSM_ID))));
        when(locationMasterServiceInter.getlocationByMapid4(PSM_ID, 301))
                .thenReturn(new ArrayList<>(List.of(office("Bengaluru Office"))));

        assertSuccessContaining(controller.getAlllocation("{\"serviceProviderID\":77,\"stateID\":29,"
                + "\"serviceID\":3,\"districtID\":301,\"isNational\":false}"), "Bengaluru Office");
        verify(locationMasterServiceInter, never()).getlocationByMapid2(anyInt());
    }

    @Test
    @DisplayName("getAlllocation should answer an error envelope when the national flag is missing")
    void getAlllocation_shouldAnswerErrorEnvelopeWithoutNationalFlag() {
        assertCodeException(controller.getAlllocation("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getAlllocationNew should read the addresses straight off the mapping the caller names")
    void getAlllocationNew_shouldReadFromNamedMapping() {
        when(locationMasterServiceInter.getlocationByMapid2(PSM_ID))
                .thenReturn(new ArrayList<>(List.of(office("Bengaluru Office"))));

        assertSuccessContaining(controller.getAlllocationNew("{\"providerServiceMapID\":4001}"),
                "Bengaluru Office");
    }

    @Test
    @DisplayName("getAlllocationNew should narrow to the district when the caller names one")
    void getAlllocationNew_shouldNarrowToDistrict() {
        when(locationMasterServiceInter.getlocationByMapid4(PSM_ID, 301))
                .thenReturn(new ArrayList<>(List.of(office("Bengaluru Office"))));

        assertSuccessContaining(
                controller.getAlllocationNew("{\"providerServiceMapID\":4001,\"districtID\":301}"),
                "Bengaluru Office");
    }

    @Test
    @DisplayName("getAlllocationNew should answer an error envelope when the lookup fails")
    void getAlllocationNew_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getlocationByMapid2(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAlllocationNew("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("searchRole should answer the states the provider serves")
    void searchRole_shouldAnswerServedStates() {
        when(locationMasterServiceInter.getStateByServiceProviderId(PROVIDER_ID))
                .thenReturn(new ArrayList<>(List.of(mapping(PSM_ID))));

        assertSuccessContaining(controller.searchRole("{\"serviceProviderID\":77}"), "4001");
    }

    @Test
    @DisplayName("searchRole should answer an error envelope when the lookup fails")
    void searchRole_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getStateByServiceProviderId(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchRole("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getService should answer the service lines the provider runs in the state")
    void getService_shouldAnswerServiceLinesInState() {
        when(locationMasterServiceInter.getServiceByServiceProviderIdAndStateId(PROVIDER_ID, 29))
                .thenReturn(new ArrayList<>(List.of(mapping(PSM_ID))));

        assertSuccessContaining(controller.getService("{\"serviceProviderID\":77,\"stateID\":29}"), "4001");
    }

    @Test
    @DisplayName("getService should answer an error envelope when the lookup fails")
    void getService_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getServiceByServiceProviderIdAndStateId(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getService("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getAllDistrict should answer the districts of the state")
    void getAllDistrict_shouldAnswerStateDistricts() {
        M_District district = new M_District();
        district.setDistrictID(301);
        district.setDistrictName("Bengaluru Urban");
        when(locationMasterServiceInter.getAllDistrictByStateId(29))
                .thenReturn(new ArrayList<>(List.of(district)));

        assertSuccessContaining(controller.getAllDistrict("{\"stateID\":29}"), "Bengaluru Urban");
    }

    @Test
    @DisplayName("getAllDistrict should answer an error envelope when the lookup fails")
    void getAllDistrict_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getAllDistrictByStateId(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllDistrict("{\"stateID\":29}"));
    }

    @Test
    @DisplayName("getAllRole should create one address per mapping the request names")
    void getAllRole_shouldCreateOneAddressPerMapping() {
        when(locationMasterServiceInter.addlocation(anyList()))
                .thenReturn(new ArrayList<>(List.of(address(51, "Bengaluru Office"))));

        String response = controller.getAllRole("{\"providerServiceMapID\":[4001,4002],"
                + "\"address\":\"Main Road\",\"locationName\":\"Bengaluru Office\",\"districtID\":301,"
                + "\"createdBy\":\"admin\",\"abdmFacilityId\":\"ABDM-1\","
                + "\"abdmFacilityName\":\"Bengaluru PHC\"}");

        assertSuccessContaining(response, "Bengaluru Office");

        ArgumentCaptor<List<M_ProviderServiceAddMapping>> captor = ArgumentCaptor.forClass(List.class);
        verify(locationMasterServiceInter).addlocation(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals("ABDM-1", captor.getValue().get(0).getAbdmFacilityId());
    }

    @Test
    @DisplayName("getAllRole should answer an error envelope when no mappings are named")
    void getAllRole_shouldAnswerErrorEnvelopeWithoutMappings() {
        assertCodeException(controller.getAllRole("{\"address\":\"Main Road\"}"));
    }

    @Test
    @DisplayName("geteditLocation should copy the edits onto the stored address")
    void geteditLocation_shouldCopyEdits() {
        M_ProviderServiceAddMapping stored = address(51, "old name");
        when(locationMasterServiceInter.editData(51)).thenReturn(stored);
        when(locationMasterServiceInter.saveEditData(stored)).thenReturn(stored);

        String response = controller.geteditLocation("{\"pSAddMapID\":51,\"providerServiceMapID\":4001,"
                + "\"districtID\":301,\"address\":\"Main Road\",\"locationName\":\"Bengaluru Office\","
                + "\"abdmFacilityId\":\"ABDM-1\",\"abdmFacilityName\":\"Bengaluru PHC\"}");

        assertSuccessContaining(response, "Bengaluru Office");
        assertEquals("Main Road", stored.getAddress());
        assertEquals("ABDM-1", stored.getAbdmFacilityId());
    }

    @Test
    @DisplayName("geteditLocation should answer an error envelope for an address that does not exist")
    void geteditLocation_shouldAnswerErrorEnvelopeForUnknownAddress() {
        when(locationMasterServiceInter.editData(51)).thenReturn(null);

        assertCodeException(controller.geteditLocation("{\"pSAddMapID\":51}"));
    }

    @Test
    @DisplayName("deleteLocation should mark the address deleted")
    void deleteLocation_shouldMarkAddressDeleted() {
        M_ProviderServiceAddMapping stored = address(51, "Bengaluru Office");
        when(locationMasterServiceInter.editData(51)).thenReturn(stored);
        when(locationMasterServiceInter.saveEditData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteLocation("{\"pSAddMapID\":51,\"deleted\":true}"),
                "Bengaluru Office");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteLocation should answer an error envelope for an address that does not exist")
    void deleteLocation_shouldAnswerErrorEnvelopeForUnknownAddress() {
        when(locationMasterServiceInter.editData(51)).thenReturn(null);

        assertCodeException(controller.deleteLocation("{\"pSAddMapID\":51,\"deleted\":true}"));
    }

    @Test
    @DisplayName("getLocationByServiceID should gather the addresses of every mapping on the service line")
    void getLocationByServiceID_shouldGatherAddressesAcrossMappings() {
        when(locationMasterServiceInter.getLocationByServiceId(PROVIDER_ID, 3))
                .thenReturn(new ArrayList<>(List.of(mapping(4001), mapping(4002))));
        when(locationMasterServiceInter.getlocationByMapid1(any()))
                .thenReturn(new ArrayList<>(List.of(office("Bengaluru Office"))));

        assertSuccessContaining(
                controller.getLocationByServiceID("{\"serviceProviderID\":77,\"serviceID\":3}"),
                "Bengaluru Office");

        ArgumentCaptor<ArrayList<Integer>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(locationMasterServiceInter).getlocationByMapid1(captor.capture());
        assertEquals(List.of(4001, 4002), captor.getValue());
    }

    @Test
    @DisplayName("getLocationByServiceID should answer an error envelope when the lookup fails")
    void getLocationByServiceID_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getLocationByServiceId(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getLocationByServiceID("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getLocationByStateId should gather the addresses of every mapping in the state")
    void getLocationByStateId_shouldGatherAddressesAcrossMappings() {
        when(locationMasterServiceInter.getLocationBySateID(PROVIDER_ID, 29))
                .thenReturn(new ArrayList<>(List.of(mapping(4001))));
        when(locationMasterServiceInter.getlocationByMapid1(any()))
                .thenReturn(new ArrayList<>(List.of(office("Bengaluru Office"))));

        assertSuccessContaining(
                controller.getLocationByStateId("{\"serviceProviderID\":77,\"stateID\":29}"), "Bengaluru Office");
    }

    @Test
    @DisplayName("getLocationByStateId should answer an error envelope when the lookup fails")
    void getLocationByStateId_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getLocationBySateID(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getLocationByStateId("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getOfficeNameByMapId should ask for one office per mapping the request lists")
    void getOfficeNameByMapId_shouldAskForOneOfficePerMapping() {
        when(locationMasterServiceInter.getOfficeName(any()))
                .thenReturn(new ArrayList<>(List.of(office("Bengaluru Office"))));

        assertSuccessContaining(
                controller.getOfficeNameByMapId("{\"providerServiceMapID\":[4001,4002]}"), "Bengaluru Office");

        ArgumentCaptor<ArrayList<Showofficedetails>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(locationMasterServiceInter).getOfficeName(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    @Test
    @DisplayName("getOfficeNameByMapId should answer an error envelope when no mappings are listed")
    void getOfficeNameByMapId_shouldAnswerErrorEnvelopeWithoutMappings() {
        assertCodeException(controller.getOfficeNameByMapId("{}"));
    }

    @Test
    @DisplayName("getStatesByServiceID should answer the states the service line runs in")
    void getStatesByServiceID_shouldAnswerServedStates() {
        when(locationMasterServiceInter.getStatesByServiceId(3, PROVIDER_ID))
                .thenReturn(new ArrayList<>(List.of(mapping(PSM_ID))));

        assertSuccessContaining(
                controller.getStatesByServiceID("{\"serviceID\":3,\"serviceProviderID\":77}"), "4001");
    }

    @Test
    @DisplayName("getStatesByServiceID should answer an error envelope when the lookup fails")
    void getStatesByServiceID_shouldAnswerErrorEnvelopeOnFailure() {
        when(locationMasterServiceInter.getStatesByServiceId(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getStatesByServiceID("{\"serviceID\":3}"));
    }
}
