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
package com.iemr.admin.controller.employeemaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.employeemaster.AshaSupervisorMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.service.employeemaster.AshaSupervisorMappingService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The ASHA supervisor endpoints decide which ASHAs each supervisor oversees at
 * each facility, so a wrong mapping puts a health worker under the wrong
 * supervisor.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AshaSupervisorMappingController Test Suite")
class AshaSupervisorMappingControllerTest {

    @Mock
    private AshaSupervisorMappingService ashaSupervisorMappingService;

    @Mock
    private EmployeeMasterRepo employeeMasterRepo;

    @Mock
    private MainStoreRepo mainStoreRepo;

    @InjectMocks
    private AshaSupervisorMappingController controller;

    private static AshaSupervisorMapping mapping(Long id, Integer supervisorId, Integer ashaId) {
        AshaSupervisorMapping mapping = new AshaSupervisorMapping();
        mapping.setId(id);
        mapping.setSupervisorUserID(supervisorId);
        mapping.setAshaUserID(ashaId);
        return mapping;
    }

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    private static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    @Test
    @DisplayName("getAshasByFacility should answer the ASHAs attached to the facilities the caller lists")
    void getAshasByFacility_shouldAnswerAshasForListedFacilities() {
        M_UserServiceRoleMapping2 asha = new M_UserServiceRoleMapping2();
        asha.setuSRMappingID(9001);
        asha.setUserID(3117);
        when(ashaSupervisorMappingService.getAshasByFacility(List.of(501, 502)))
                .thenReturn(new ArrayList<>(List.of(asha)));

        assertSuccessContaining(controller.getAshasByFacility("{\"facilityIDs\":[501,502]}"), "9001");
    }

    @Test
    @DisplayName("getAshasByFacility should fall back to the single facility id when no list is sent")
    void getAshasByFacility_shouldFallBackToSingleFacilityId() {
        when(ashaSupervisorMappingService.getAshasByFacility(List.of(501))).thenReturn(new ArrayList<>());

        controller.getAshasByFacility("{\"facilityID\":501}");

        verify(ashaSupervisorMappingService).getAshasByFacility(List.of(501));
    }

    @Test
    @DisplayName("getAshasByFacility should ask for nothing when the request names no facility at all")
    void getAshasByFacility_shouldAskForNothingWithoutAFacility() {
        when(ashaSupervisorMappingService.getAshasByFacility(null)).thenReturn(new ArrayList<>());

        controller.getAshasByFacility("{}");

        verify(ashaSupervisorMappingService).getAshasByFacility(null);
    }

    @Test
    @DisplayName("getAshasByFacility should answer an error envelope when the lookup fails")
    void getAshasByFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(ashaSupervisorMappingService.getAshasByFacility(any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getAshasByFacility("{\"facilityIDs\":[501]}")));
    }

    @Test
    @DisplayName("saveAshaSupervisorMapping should answer the mappings the service stored")
    void saveAshaSupervisorMapping_shouldAnswerStoredMappings() {
        when(ashaSupervisorMappingService.saveAshaSupervisorMappings(anyList()))
                .thenReturn(new ArrayList<>(List.of(mapping(1L, 3117, 4001))));

        assertSuccessContaining(
                controller.saveAshaSupervisorMapping("[{\"supervisorUserID\":3117,\"ashaUserID\":4001}]"), "3117");
    }

    @Test
    @DisplayName("saveAshaSupervisorMapping should answer an error envelope when the store fails")
    void saveAshaSupervisorMapping_shouldAnswerErrorEnvelopeOnFailure() {
        when(ashaSupervisorMappingService.saveAshaSupervisorMappings(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.saveAshaSupervisorMapping("[{\"supervisorUserID\":3117}]")));
    }

    @Test
    @DisplayName("getSupervisorMappingByFacility should answer the mappings at the facility")
    void getSupervisorMappingByFacility_shouldAnswerFacilityMappings() {
        when(ashaSupervisorMappingService.getSupervisorMappingByFacility(501))
                .thenReturn(new ArrayList<>(List.of(mapping(1L, 3117, 4001))));

        assertSuccessContaining(controller.getSupervisorMappingByFacility("{\"facilityID\":501}"), "3117");
    }

    @Test
    @DisplayName("getSupervisorMappingByFacility should answer an error envelope when the lookup fails")
    void getSupervisorMappingByFacility_shouldAnswerErrorEnvelopeOnFailure() {
        when(ashaSupervisorMappingService.getSupervisorMappingByFacility(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getSupervisorMappingByFacility("{\"facilityID\":501}")));
    }

    @Test
    @DisplayName("deleteAshaSupervisorMapping should retire the mappings the caller names")
    void deleteAshaSupervisorMapping_shouldRetireNamedMappings() {
        String response = controller
                .deleteAshaSupervisorMapping("{\"supervisorUserID\":3117,\"facilityIDs\":[501,502]}");

        assertSuccessContaining(response, "Deleted successfully");
        verify(ashaSupervisorMappingService).deleteBySupervisorAndFacilities(3117, List.of(501, 502), "Admin");
    }

    @Test
    @DisplayName("deleteAshaSupervisorMapping should refuse a request that names no supervisor")
    void deleteAshaSupervisorMapping_shouldRefuseRequestWithoutSupervisor() {
        String response = controller.deleteAshaSupervisorMapping("{\"facilityIDs\":[501]}");

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response));
        assertTrue(response.contains("supervisorUserID and facilityIDs are required"), response);
        verify(ashaSupervisorMappingService, never()).deleteBySupervisorAndFacilities(anyInt(), anyList(), anyString());
    }

    @Test
    @DisplayName("deleteAshaSupervisorMapping should refuse a request that names no facilities")
    void deleteAshaSupervisorMapping_shouldRefuseRequestWithoutFacilities() {
        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.deleteAshaSupervisorMapping("{\"supervisorUserID\":3117}")));
    }

    @Test
    @DisplayName("deleteAshaSupervisorMapping should answer an error envelope when the retirement fails")
    void deleteAshaSupervisorMapping_shouldAnswerErrorEnvelopeOnFailure() {
        org.mockito.Mockito.doThrow(new IllegalStateException("no connection"))
                .when(ashaSupervisorMappingService)
                .deleteBySupervisorAndFacilities(anyInt(), anyList(), anyString());

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller
                .deleteAshaSupervisorMapping("{\"supervisorUserID\":3117,\"facilityIDs\":[501]}")));
    }

    @Test
    @DisplayName("updateAshaSupervisorMappingAtomically should replace the old mappings in one step")
    void updateAtomically_shouldReplaceMappingsInOneStep() {
        when(ashaSupervisorMappingService.updateAshaMappingsAtomically(anyInt(), anyList(), anyList(), anyString()))
                .thenReturn(new ArrayList<>(List.of(mapping(1L, 3117, 4001))));

        String response = controller.updateAshaSupervisorMappingAtomically("{\"supervisorUserID\":3117,"
                + "\"modifiedBy\":\"admin\",\"facilityIDs\":[501,502],"
                + "\"newMappings\":[{\"supervisorUserID\":3117,\"ashaUserID\":4001,\"facilityID\":501}]}");

        assertSuccessContaining(response, "3117");
        verify(ashaSupervisorMappingService).updateAshaMappingsAtomically(
                org.mockito.ArgumentMatchers.eq(3117), org.mockito.ArgumentMatchers.eq(List.of(501, 502)),
                anyList(), org.mockito.ArgumentMatchers.eq("admin"));
    }

    @Test
    @DisplayName("updateAshaSupervisorMappingAtomically should attribute the change to Admin when none is named")
    void updateAtomically_shouldAttributeToAdminByDefault() {
        when(ashaSupervisorMappingService.updateAshaMappingsAtomically(anyInt(), anyList(), anyList(), anyString()))
                .thenReturn(new ArrayList<>());

        controller.updateAshaSupervisorMappingAtomically("{\"supervisorUserID\":3117,\"facilityIDs\":[501]}");

        verify(ashaSupervisorMappingService).updateAshaMappingsAtomically(
                org.mockito.ArgumentMatchers.eq(3117), anyList(), anyList(),
                org.mockito.ArgumentMatchers.eq("Admin"));
    }

    @Test
    @DisplayName("updateAshaSupervisorMappingAtomically should answer an error envelope without a supervisor")
    void updateAtomically_shouldAnswerErrorEnvelopeWithoutSupervisor() {
        assertEquals(OutputResponse.CODE_EXCEPTION,
                statusCodeOf(controller.updateAshaSupervisorMappingAtomically("{\"facilityIDs\":[501]}")));
    }

    @Test
    @DisplayName("restoreAshaSupervisorMapping should reinstate the mappings the caller names")
    void restoreAshaSupervisorMapping_shouldReinstateNamedMappings() {
        String response = controller.restoreAshaSupervisorMapping("{\"ids\":[1,2]}");

        assertSuccessContaining(response, "Restored successfully");
        verify(ashaSupervisorMappingService).restoreMappings(List.of(1L, 2L), "Admin");
    }

    @Test
    @DisplayName("restoreAshaSupervisorMapping should refuse a request that names no mappings")
    void restoreAshaSupervisorMapping_shouldRefuseRequestWithoutIds() {
        String response = controller.restoreAshaSupervisorMapping("{}");

        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response));
        assertTrue(response.contains("ids are required"), response);
    }

    @Test
    @DisplayName("restoreAshaSupervisorMapping should answer an error envelope when the reinstatement fails")
    void restoreAshaSupervisorMapping_shouldAnswerErrorEnvelopeOnFailure() {
        org.mockito.Mockito.doThrow(new IllegalStateException("no connection"))
                .when(ashaSupervisorMappingService).restoreMappings(anyList(), anyString());

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.restoreAshaSupervisorMapping("{\"ids\":[1]}")));
    }

    @Test
    @DisplayName("getFacilityByMappingID should answer the facility the mapping points at")
    void getFacilityByMappingID_shouldAnswerMappedFacility() {
        M_UserServiceRoleMapping2 mapping = new M_UserServiceRoleMapping2();
        mapping.setuSRMappingID(9001);
        mapping.setFacilityID(501);
        M_Facility facility = new M_Facility();
        facility.setFacilityID(501);
        facility.setFacilityName("PHC North");
        facility.setFacilityTypeID(3);
        facility.setRuralUrban("Rural");
        when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(mapping));
        when(mainStoreRepo.findByFacilityIDAndDeleted(501, false)).thenReturn(facility);

        String response = controller.getFacilityByMappingID("{\"uSRMappingID\":9001}");

        assertSuccessContaining(response, "PHC North");
        assertTrue(response.contains("Rural"), response);
    }

    @Test
    @DisplayName("getFacilityByMappingID should report a facility that has since been deleted")
    void getFacilityByMappingID_shouldReportDeletedFacility() {
        M_UserServiceRoleMapping2 mapping = new M_UserServiceRoleMapping2();
        mapping.setuSRMappingID(9001);
        mapping.setFacilityID(501);
        when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(mapping));
        when(mainStoreRepo.findByFacilityIDAndDeleted(501, false)).thenReturn(null);

        assertSuccessContaining(controller.getFacilityByMappingID("{\"uSRMappingID\":9001}"), "facilityDeleted");
    }

    @Test
    @DisplayName("getFacilityByMappingID should answer no facility for a mapping that was retired")
    void getFacilityByMappingID_shouldAnswerNoFacilityForRetiredMapping() {
        M_UserServiceRoleMapping2 mapping = new M_UserServiceRoleMapping2();
        mapping.setuSRMappingID(9001);
        mapping.setFacilityID(501);
        mapping.setDeleted(Boolean.TRUE);
        when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(mapping));

        assertSuccessContaining(controller.getFacilityByMappingID("{\"uSRMappingID\":9001}"), "\"data\":{}");
        verify(mainStoreRepo, never()).findByFacilityIDAndDeleted(anyInt(), org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    @DisplayName("getFacilityByMappingID should answer no facility for a mapping that names none")
    void getFacilityByMappingID_shouldAnswerNoFacilityWhenMappingNamesNone() {
        M_UserServiceRoleMapping2 mapping = new M_UserServiceRoleMapping2();
        mapping.setuSRMappingID(9001);
        when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(mapping));

        assertSuccessContaining(controller.getFacilityByMappingID("{\"uSRMappingID\":9001}"), "\"data\":{}");
    }

    @Test
    @DisplayName("getFacilityByMappingID should answer no facility for a mapping that does not exist")
    void getFacilityByMappingID_shouldAnswerNoFacilityForUnknownMapping() {
        when(employeeMasterRepo.findById(9001)).thenReturn(Optional.empty());

        assertSuccessContaining(controller.getFacilityByMappingID("{\"uSRMappingID\":9001}"), "\"data\":{}");
    }

    @Test
    @DisplayName("getFacilityByMappingID should answer no facility when the request names no mapping")
    void getFacilityByMappingID_shouldAnswerNoFacilityWithoutMappingId() {
        assertSuccessContaining(controller.getFacilityByMappingID("{}"), "\"data\":{}");
        verify(employeeMasterRepo, never()).findById(anyInt());
    }

    @Test
    @DisplayName("getFacilityByMappingID should answer an error envelope when the lookup fails")
    void getFacilityByMappingID_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterRepo.findById(9001)).thenThrow(new IllegalStateException("no connection"));

        assertEquals(OutputResponse.GENERIC_FAILURE,
                statusCodeOf(controller.getFacilityByMappingID("{\"uSRMappingID\":9001}")));
    }
}
