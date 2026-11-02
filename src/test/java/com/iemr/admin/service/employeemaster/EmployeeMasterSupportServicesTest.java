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
package com.iemr.admin.service.employeemaster;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.employeemaster.AshaSupervisorMapping;
import com.iemr.admin.data.employeemaster.EmployeeSignature;
import com.iemr.admin.data.employeemaster.M_Designation;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.employeemaster.USRAgentMapping;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repo.employeemaster.EmployeeSignatureRepo;
import com.iemr.admin.repo.employeemaster.M_DesignationRepo;
import com.iemr.admin.repo.employeemaster.USRAgentMappingRepository;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.repository.user.AshaSupervisorMappingRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The supporting employee services: the supervisor mapping store, the signature
 * store, the CTI agent-id pool and the designation master.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Employee master support service Test Suite")
class EmployeeMasterSupportServicesTest {

    private static final Integer SUPERVISOR_ID = 3117;
    private static final Integer ASHA_ID = 4001;
    private static final Integer FACILITY_ID = 501;

    @Mock
    private AshaSupervisorMappingRepo ashaSupervisorMappingRepo;

    @Mock
    private EmployeeMasterRepo employeeMasterRepo;

    @Mock
    private MainStoreRepo mainStoreRepo;

    @InjectMocks
    private AshaSupervisorMappingServiceImpl ashaService;

    @Mock
    private EmployeeSignatureRepo employeeSignatureRepo;

    @InjectMocks
    private EmployeeSignatureServiceImpl signatureService;

    @Mock
    private USRAgentMappingRepository usrAgentMappingRepository;

    @Mock
    private M_DesignationRepo m_DesignationRepo;

    @InjectMocks
    private M_DesignationImpl designationService;

    private static AshaSupervisorMapping mapping(Long id, Integer supervisorId, Integer ashaId) {
        AshaSupervisorMapping mapping = new AshaSupervisorMapping();
        mapping.setId(id);
        mapping.setSupervisorUserID(supervisorId);
        mapping.setAshaUserID(ashaId);
        mapping.setFacilityID(FACILITY_ID);
        mapping.setCreatedBy("admin");
        return mapping;
    }

    private void activeFacility() {
        M_Facility facility = new M_Facility();
        facility.setFacilityID(FACILITY_ID);
        when(mainStoreRepo.findByFacilityIDAndDeleted(FACILITY_ID, false)).thenReturn(facility);
    }

    @Nested
    @DisplayName("AshaSupervisorMappingServiceImpl")
    class AshaSupervisorMappingTests {

        @Test
        @DisplayName("saveAshaSupervisorMappings should refuse a mapping onto a retired facility")
        void save_shouldRefuseRetiredFacility() {
            when(mainStoreRepo.findByFacilityIDAndDeleted(FACILITY_ID, false)).thenReturn(null);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> ashaService.saveAshaSupervisorMappings(List.of(mapping(null, SUPERVISOR_ID, ASHA_ID))));

            assertTrue(thrown.getMessage().contains("is no longer active"), thrown.getMessage());
        }

        @Test
        @DisplayName("saveAshaSupervisorMappings should reuse a mapping that already exists")
        void save_shouldReuseExistingMapping() {
            activeFacility();
            AshaSupervisorMapping existing = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            when(ashaSupervisorMappingRepo
                    .findBySupervisorUserIDAndAshaUserIDAndFacilityIDAndDeletedFalse(SUPERVISOR_ID, ASHA_ID,
                            FACILITY_ID))
                    .thenReturn(existing);

            ArrayList<AshaSupervisorMapping> saved =
                    ashaService.saveAshaSupervisorMappings(List.of(mapping(null, SUPERVISOR_ID, ASHA_ID)));

            assertSame(existing, saved.get(0));
            verify(ashaSupervisorMappingRepo, never()).save(any());
        }

        @Test
        @DisplayName("saveAshaSupervisorMappings should retire the ASHA's mapping under a different supervisor")
        void save_shouldRetireMappingUnderOtherSupervisor() {
            activeFacility();
            AshaSupervisorMapping other = mapping(2L, 3118, ASHA_ID);
            AshaSupervisorMapping toSave = mapping(null, SUPERVISOR_ID, ASHA_ID);
            when(ashaSupervisorMappingRepo
                    .findByAshaUserIDAndFacilityIDAndDeletedFalseAndSupervisorUserIDNot(ASHA_ID, FACILITY_ID,
                            SUPERVISOR_ID))
                    .thenReturn(new ArrayList<>(List.of(other)));
            when(ashaSupervisorMappingRepo.save(toSave)).thenReturn(toSave);

            ashaService.saveAshaSupervisorMappings(List.of(toSave));

            assertTrue(other.getDeleted(), "an ASHA may report to only one supervisor at a facility");
            assertEquals("admin", other.getModifiedBy());
            verify(ashaSupervisorMappingRepo).save(other);
        }

        @Test
        @DisplayName("getSupervisorMappingByFacility should hand back what the repository holds")
        void getByFacility_shouldHandBackRepositoryContents() {
            ArrayList<AshaSupervisorMapping> stored = new ArrayList<>();
            when(ashaSupervisorMappingRepo.findActiveMappingsByFacilityID(FACILITY_ID)).thenReturn(stored);

            assertSame(stored, ashaService.getSupervisorMappingByFacility(FACILITY_ID));
        }

        @Test
        @DisplayName("getAshasByFacility should hand back what the repository holds")
        void getAshas_shouldHandBackRepositoryContents() {
            ArrayList<M_UserServiceRoleMapping2> stored = new ArrayList<>();
            when(employeeMasterRepo.findAshaUsersByFacilityIDs(anyList())).thenReturn(stored);

            assertSame(stored, ashaService.getAshasByFacility(List.of(FACILITY_ID)));
        }

        @Test
        @DisplayName("deleteMappings should retire each mapping it can resolve")
        void deleteMappings_shouldRetireResolvedMappings() {
            AshaSupervisorMapping stored = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            when(ashaSupervisorMappingRepo.findById(1L)).thenReturn(Optional.of(stored));
            when(ashaSupervisorMappingRepo.findById(2L)).thenReturn(Optional.empty());

            ashaService.deleteMappings(List.of(1L, 2L), "admin");

            assertTrue(stored.getDeleted());
            verify(ashaSupervisorMappingRepo).save(stored);
        }

        @Test
        @DisplayName("restoreMappings should reinstate each mapping it can resolve")
        void restoreMappings_shouldReinstateResolvedMappings() {
            AshaSupervisorMapping stored = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            stored.setDeleted(Boolean.TRUE);
            when(ashaSupervisorMappingRepo.findById(1L)).thenReturn(Optional.of(stored));
            when(ashaSupervisorMappingRepo.findById(2L)).thenReturn(Optional.empty());

            ashaService.restoreMappings(List.of(1L, 2L), "admin");

            assertFalse(stored.getDeleted());
            assertEquals("admin", stored.getModifiedBy());
        }

        @Test
        @DisplayName("deleteBySupervisorAndFacilities should retire every mapping at the named facilities")
        void deleteBySupervisorAndFacilities_shouldRetireEveryMapping() {
            AshaSupervisorMapping stored = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            when(ashaSupervisorMappingRepo
                    .findBySupervisorUserIDAndFacilityIDInAndDeletedFalse(SUPERVISOR_ID, List.of(FACILITY_ID)))
                    .thenReturn(new ArrayList<>(List.of(stored)));

            ashaService.deleteBySupervisorAndFacilities(SUPERVISOR_ID, List.of(FACILITY_ID), "admin");

            assertTrue(stored.getDeleted());
        }

        @Test
        @DisplayName("cascadeDeleteByUserID should retire the user's mappings on both sides of the relationship")
        void cascadeByUser_shouldRetireBothSides() {
            AshaSupervisorMapping asSupervisor = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            AshaSupervisorMapping asAsha = mapping(2L, 3118, SUPERVISOR_ID);
            when(ashaSupervisorMappingRepo.findBySupervisorUserIDAndDeletedFalse(SUPERVISOR_ID))
                    .thenReturn(new ArrayList<>(List.of(asSupervisor)));
            when(ashaSupervisorMappingRepo.findByAshaUserIDAndDeletedFalse(SUPERVISOR_ID))
                    .thenReturn(new ArrayList<>(List.of(asAsha)));

            ashaService.cascadeDeleteByUserID(SUPERVISOR_ID, "admin");

            assertTrue(asSupervisor.getDeleted());
            assertTrue(asAsha.getDeleted());
        }

        @Test
        @DisplayName("cascadeDeleteByFacilityID should retire every mapping at the facility")
        void cascadeByFacility_shouldRetireEveryMapping() {
            AshaSupervisorMapping stored = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            when(ashaSupervisorMappingRepo.findByFacilityIDAndDeletedFalse(FACILITY_ID))
                    .thenReturn(new ArrayList<>(List.of(stored)));

            ashaService.cascadeDeleteByFacilityID(FACILITY_ID, "admin");

            assertTrue(stored.getDeleted());
        }

        @Test
        @DisplayName("cascadeDeleteByUserIDAndFacilityID should retire only that user's mappings at that facility")
        void cascadeByUserAndFacility_shouldRetireBothSidesAtFacility() {
            AshaSupervisorMapping asSupervisor = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            AshaSupervisorMapping asAsha = mapping(2L, 3118, SUPERVISOR_ID);
            when(ashaSupervisorMappingRepo
                    .findBySupervisorUserIDAndFacilityIDAndDeletedFalse(SUPERVISOR_ID, FACILITY_ID))
                    .thenReturn(new ArrayList<>(List.of(asSupervisor)));
            when(ashaSupervisorMappingRepo.findByAshaUserIDAndFacilityIDAndDeletedFalse(SUPERVISOR_ID, FACILITY_ID))
                    .thenReturn(new ArrayList<>(List.of(asAsha)));

            ashaService.cascadeDeleteByUserIDAndFacilityID(SUPERVISOR_ID, FACILITY_ID, "admin");

            assertTrue(asSupervisor.getDeleted());
            assertTrue(asAsha.getDeleted());
        }

        @Test
        @DisplayName("updateAshaMappingsAtomically should retire the old mappings before saving the new ones")
        void updateAtomically_shouldRetireThenSave() {
            activeFacility();
            AshaSupervisorMapping old = mapping(1L, SUPERVISOR_ID, ASHA_ID);
            AshaSupervisorMapping fresh = mapping(null, SUPERVISOR_ID, 4002);
            when(ashaSupervisorMappingRepo
                    .findBySupervisorUserIDAndFacilityIDInAndDeletedFalse(SUPERVISOR_ID, List.of(FACILITY_ID)))
                    .thenReturn(new ArrayList<>(List.of(old)));
            when(ashaSupervisorMappingRepo.save(fresh)).thenReturn(fresh);

            ArrayList<AshaSupervisorMapping> saved = ashaService.updateAshaMappingsAtomically(
                    SUPERVISOR_ID, List.of(FACILITY_ID), List.of(fresh), "admin");

            assertTrue(old.getDeleted());
            assertSame(fresh, saved.get(0));
        }

        @Test
        @DisplayName("updateAshaMappingsAtomically should answer nothing when no new mappings are supplied")
        void updateAtomically_shouldAnswerNothingWithoutNewMappings() {
            when(ashaSupervisorMappingRepo
                    .findBySupervisorUserIDAndFacilityIDInAndDeletedFalse(anyInt(), anyList()))
                    .thenReturn(new ArrayList<>());

            assertTrue(ashaService
                    .updateAshaMappingsAtomically(SUPERVISOR_ID, List.of(FACILITY_ID), null, "admin").isEmpty());
        }
    }

    @Nested
    @DisplayName("EmployeeSignatureServiceImpl")
    class SignatureServiceTests {

        @Test
        @DisplayName("uploadSignature should overwrite the signature already on file")
        void upload_shouldOverwriteExistingSignature() {
            EmployeeSignature existing = new EmployeeSignature();
            existing.setUserID(3117L);
            existing.setUserSignatureID(9001L);
            EmployeeSignature uploaded = new EmployeeSignature();
            uploaded.setUserID(3117L);
            uploaded.setFileName("new.png");
            uploaded.setFileType("image/png");
            uploaded.setSignature(new byte[] { 1, 2, 3 });
            uploaded.setCreatedBy("admin");
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(existing);
            when(employeeSignatureRepo.save(existing)).thenReturn(existing);

            assertEquals(9001L, signatureService.uploadSignature(uploaded));
            assertEquals("new.png", existing.getFileName());
            assertEquals("admin", existing.getModifiedBy());
        }

        @Test
        @DisplayName("uploadSignature should store a first signature for a user who has none")
        void upload_shouldStoreFirstSignature() {
            EmployeeSignature uploaded = new EmployeeSignature();
            uploaded.setUserID(3117L);
            uploaded.setUserSignatureID(9002L);
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(null);
            when(employeeSignatureRepo.save(uploaded)).thenReturn(uploaded);

            assertEquals(9002L, signatureService.uploadSignature(uploaded));
        }

        @Test
        @DisplayName("fetchSignature should hand back what the repository holds")
        void fetch_shouldHandBackRepositoryContents() {
            EmployeeSignature stored = new EmployeeSignature();
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(stored);

            assertSame(stored, signatureService.fetchSignature(3117L));
        }

        @Test
        @DisplayName("existSignature should report whether any signature is on file")
        void exist_shouldReportWhetherSignatureIsOnFile() {
            when(employeeSignatureRepo.countByUserIDAndSignatureNotNull(3117L)).thenReturn(1L);
            when(employeeSignatureRepo.countByUserIDAndSignatureNotNull(3118L)).thenReturn(0L);

            assertTrue(signatureService.existSignature(3117L));
            assertFalse(signatureService.existSignature(3118L));
        }

        @Test
        @DisplayName("isSignatureActive should report whether the signature is still in use")
        void isActive_shouldReportWhetherSignatureIsInUse() {
            when(employeeSignatureRepo.countByUserIDAndSignatureNotNullAndDeletedFalse(3117L)).thenReturn(1L);
            when(employeeSignatureRepo.countByUserIDAndSignatureNotNullAndDeletedFalse(3118L)).thenReturn(0L);

            assertTrue(signatureService.isSignatureActive(3117L));
            assertFalse(signatureService.isSignatureActive(3118L));
        }

        @Test
        @DisplayName("updateUserSignatureStatus should retire a signature the caller deactivates")
        void updateStatus_shouldRetireDeactivatedSignature() {
            EmployeeSignature stored = new EmployeeSignature();
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(stored);
            when(employeeSignatureRepo.save(stored)).thenReturn(stored);

            signatureService.updateUserSignatureStatus("{\"userID\":3117,\"active\":false}");

            assertTrue(stored.getDeleted());
        }

        @Test
        @DisplayName("updateUserSignatureStatus should reinstate a signature the caller activates")
        void updateStatus_shouldReinstateActivatedSignature() {
            EmployeeSignature stored = new EmployeeSignature();
            stored.setDeleted(Boolean.TRUE);
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(stored);
            when(employeeSignatureRepo.save(stored)).thenReturn(stored);

            signatureService.updateUserSignatureStatus("{\"userID\":3117,\"active\":true}");

            assertFalse(stored.getDeleted());
        }

        @Test
        @DisplayName("updateUserSignatureStatus should refuse a user who has no signature on file")
        void updateStatus_shouldRefuseUserWithoutSignature() {
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(null);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                    () -> signatureService.updateUserSignatureStatus("{\"userID\":3117,\"active\":true}"));

            assertTrue(thrown.getMessage().contains("No signature found"), thrown.getMessage());
        }
    }

    @Nested
    @DisplayName("USRAgentMappingServiceImpl")
    class UsrAgentMappingTests {

        private USRAgentMappingServiceImpl agentService() {
            USRAgentMappingServiceImpl service = new USRAgentMappingServiceImpl();
            service.setUsrAgentMappingRepository(usrAgentMappingRepository);
            return service;
        }

        private Set<Object[]> agentRow() {
            Set<Object[]> rows = new LinkedHashSet<>();
            rows.add(new Object[] { 1, 9001, null, 4001, null, "A-1", "secret", "104", Boolean.TRUE });
            rows.add(new Object[] { 2 });
            return rows;
        }

        @Test
        @DisplayName("getAvailableAgentIds should rebuild the free agents and skip an unusable row")
        void getAvailableAgentIds_shouldRebuildFreeAgents() throws Exception {
            when(usrAgentMappingRepository.getFreeAgentIds("104", 4001)).thenReturn(agentRow());

            List<USRAgentMapping> agents = agentService()
                    .getAvailableAgentIds("{\"cti_CampaignName\":\"104\",\"providerServiceMapID\":4001}");

            assertEquals(1, agents.size());
            assertEquals("A-1", agents.get(0).getAgentID());
        }

        @Test
        @DisplayName("updateAgentIds should free the previous agent id before claiming the new one")
        void updateAgentIds_shouldFreePreviousAgentId() throws Exception {
            when(usrAgentMappingRepository.updateUSRMapping(any(), any(), any())).thenReturn(1);

            Integer changed = agentService().updateAgentIds("{\"oldAgentID\":\"A-0\","
                    + "\"providerServiceMapID\":4001,\"isAvailable\":false,\"usrMappingID\":9001,"
                    + "\"usrAgentMappingID\":1}");

            assertEquals(1, changed);
            verify(usrAgentMappingRepository).updateUSRMapping(true, null, "A-0", 4001);
        }

        @Test
        @DisplayName("updateAgentIds should leave the previous agent id alone when none is named")
        void updateAgentIds_shouldLeavePreviousAgentIdAlone() throws Exception {
            when(usrAgentMappingRepository.updateUSRMapping(any(), any(), any())).thenReturn(1);

            agentService().updateAgentIds("{\"isAvailable\":true,\"usrAgentMappingID\":1}");

            verify(usrAgentMappingRepository, never())
                    .updateUSRMapping(any(Boolean.class), any(), anyString(), anyInt());
        }

        @Test
        @DisplayName("createUSRAgentMapping should skip an agent id the provider already holds")
        void createUSRAgentMapping_shouldSkipExistingAgent() throws Exception {
            when(usrAgentMappingRepository.getExistingAgent(4001, "A-1")).thenReturn(1L);
            when(usrAgentMappingRepository.getExistingAgent(4001, "A-2")).thenReturn(0L);
            when(usrAgentMappingRepository.save(any())).thenAnswer(call -> call.getArgument(0));

            List<USRAgentMapping> created = agentService().createUSRAgentMapping(
                    "[{\"agentID\":\"A-1\",\"providerServiceMapID\":4001},"
                            + "{\"agentID\":\"A-2\",\"providerServiceMapID\":4001}]");

            assertEquals(1, created.size());
            assertEquals("A-2", created.get(0).getAgentID());
        }

        @Test
        @DisplayName("getAvailableCampaigns should hand back what the repository holds")
        void getAvailableCampaigns_shouldHandBackRepositoryContents() throws Exception {
            when(usrAgentMappingRepository.getAvailableCampaigns(4001)).thenReturn(List.of("104", "1097"));

            assertEquals(2, agentService().getAvailableCampaigns("{\"providerServiceMapID\":4001}").size());
        }

        @Test
        @DisplayName("getAllAgentIds should look the agent up directly when the caller names one")
        void getAllAgentIds_shouldLookUpNamedAgent() throws Exception {
            when(usrAgentMappingRepository
                    .getUSRAgentMappingByAgentIDAndProviderServiceMapID("A-1", 4001)).thenReturn(agentRow());

            List<USRAgentMapping> agents = agentService()
                    .getAllAgentIds("{\"agentID\":\"A-1\",\"providerServiceMapID\":4001}");

            assertEquals(1, agents.size());
        }

        @Test
        @DisplayName("getAllAgentIds should filter by availability when the caller asks for it")
        void getAllAgentIds_shouldFilterByAvailability() throws Exception {
            when(usrAgentMappingRepository.getAllAgentIds(4001, "104", true)).thenReturn(agentRow());

            assertEquals(1, agentService().getAllAgentIds(
                    "{\"providerServiceMapID\":4001,\"cti_CampaignName\":\"104\",\"isAvailable\":true}").size());
        }

        @Test
        @DisplayName("getAllAgentIds should filter by campaign alone when availability is not named")
        void getAllAgentIds_shouldFilterByCampaignAlone() throws Exception {
            when(usrAgentMappingRepository.getAllAgentId(4001, "104")).thenReturn(agentRow());

            assertEquals(1, agentService()
                    .getAllAgentIds("{\"providerServiceMapID\":4001,\"cti_CampaignName\":\"104\"}").size());
        }

        @Test
        @DisplayName("getAllAgentIds should answer every agent under the mapping when nothing is named")
        void getAllAgentIds_shouldAnswerEveryAgent() throws Exception {
            when(usrAgentMappingRepository.getAllAgentIds(4001)).thenReturn(agentRow());

            assertEquals(1, agentService().getAllAgentIds("{\"providerServiceMapID\":4001}").size());
        }

        @Test
        @DisplayName("updateCTICampaignNameMapping should answer how many mappings moved campaign")
        void updateCTICampaignNameMapping_shouldAnswerChangedCount() throws Exception {
            when(usrAgentMappingRepository.updateCTICampaignNameMapping("1097", 1)).thenReturn(1);

            assertEquals(1, agentService()
                    .updateCTICampaignNameMapping("{\"cti_CampaignName\":\"1097\",\"usrAgentMappingID\":1}"));
        }

        @Test
        @DisplayName("updateDeletedAgentIDStatus should free the agent id of a deleted user")
        void updateDeletedAgentIDStatus_shouldFreeAgentId() {
            when(usrAgentMappingRepository.updateDeletedAgentIDStatus("A-1")).thenReturn(1);

            agentService().updateDeletedAgentIDStatus("A-1");

            verify(usrAgentMappingRepository).updateDeletedAgentIDStatus("A-1");
        }
    }

    @Nested
    @DisplayName("M_DesignationImpl")
    class DesignationServiceTests {

        @Test
        @DisplayName("getDesinationlist should hand back what the repository holds")
        void getDesinationlist_shouldHandBackRepositoryContents() {
            ArrayList<M_Designation> stored = new ArrayList<>(List.of(new M_Designation()));
            when(m_DesignationRepo.getDesinationlist()).thenReturn(stored);

            assertSame(stored, designationService.getDesinationlist());
        }
    }
}
