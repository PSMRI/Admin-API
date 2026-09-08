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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.employeemaster.EmployeeSignature;
import com.iemr.admin.data.employeemaster.M_Community;
import com.iemr.admin.data.employeemaster.M_Gender;
import com.iemr.admin.data.employeemaster.M_ProviderServiceMap1;
import com.iemr.admin.data.employeemaster.M_Religion;
import com.iemr.admin.data.employeemaster.M_Role;
import com.iemr.admin.data.employeemaster.M_Title;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_UserDemographics;
import com.iemr.admin.data.employeemaster.M_UserLangMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.employeemaster.M_Userqualification;
import com.iemr.admin.data.employeemaster.Showofficedetails1;
import com.iemr.admin.data.employeemaster.Showuserdetailsfromuserservicerolemapping;
import com.iemr.admin.data.employeemaster.V_Showuser;
import com.iemr.admin.data.employeemaster.V_Userservicerolemapping;
import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.UserRole;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.exceptionhandler.DataNotFound;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepoo;
import com.iemr.admin.repo.employeemaster.EmployeeSignatureRepo;
import com.iemr.admin.repo.employeemaster.M_CommunityRepo;
import com.iemr.admin.repo.employeemaster.M_GenderRepo;
import com.iemr.admin.repo.employeemaster.M_ProviderServiceMap1Repo;
import com.iemr.admin.repo.employeemaster.M_QualificationRepo;
import com.iemr.admin.repo.employeemaster.M_ReligionRepo;
import com.iemr.admin.repo.employeemaster.M_TitleRepo;
import com.iemr.admin.repo.employeemaster.M_UserDemographicsRepo;
import com.iemr.admin.repo.employeemaster.M_UserLangMappingRepo;
import com.iemr.admin.repo.employeemaster.RoleRepo;
import com.iemr.admin.repo.employeemaster.Showofficedetails1Repo1;
import com.iemr.admin.repo.employeemaster.ShowuserdetailsfromuserservicerolemappingRepo;
import com.iemr.admin.repo.employeemaster.V_ShowuserRepo;
import com.iemr.admin.repo.employeemaster.V_UserservicerolemappingRepo;
import com.iemr.admin.repository.facilitytype.M_facilitytypeRepo;
import com.iemr.admin.repository.rolemaster.M_UserservicerolemappingForRoleProviderAdminRepo;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.service.user.EncryptUserPassword;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The employee master service is where the rules about who may work where live:
 * an ASHA must sit at a sub-centre, a role may not be mapped twice, and taking a
 * role away has to take the supervisor mappings with it.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EmployeeMasterServiceImpl Test Suite")
class EmployeeMasterServiceImplTest {

    private static final Integer USER_ID = 3117;
    private static final Integer PSM_ID = 4001;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private EmployeeMasterRepo employeeMasterRepo;

    @Mock
    private EmployeeMasterRepoo employeeMasterRepo11;

    @Mock
    private EmployeeMasterRepoo employeeMasterRepoo;

    @Mock
    private M_UserDemographicsRepo m_UserDemographicsRepo;

    @Mock
    private M_UserLangMappingRepo m_UserLangMappingRepo;

    @Mock
    private M_TitleRepo m_TitleRepo;

    @Mock
    private M_GenderRepo m_GenderRepo;

    @Mock
    private ShowuserdetailsfromuserservicerolemappingRepo showuserdetailsfromuserservicerolemappingRepo;

    @Mock
    private V_ShowuserRepo v_ShowuserRepo;

    @Mock
    private V_UserservicerolemappingRepo v_UserservicerolemappingRepo;

    @Mock
    private M_QualificationRepo m_QualificationRepo;

    @Mock
    private Showofficedetails1Repo1 showofficedetails1Repo1;

    @Mock
    private M_ProviderServiceMap1Repo m_ProviderServiceMap1Repo;

    @Mock
    private MainStoreRepo mainStoreRepo;

    @Mock
    private M_facilitytypeRepo facilityTypeRepo;

    @Mock
    private EmployeeSignatureRepo employeeSignatureRepo;

    @Mock
    private M_CommunityRepo m_CommunityRepo;

    @Mock
    private M_ReligionRepo m_ReligionRepo;

    @Mock
    private M_UserservicerolemappingForRoleProviderAdminRepo userservicerolemappingForRoleProviderAdminRepo;

    @Mock
    private AshaSupervisorMappingService ashaSupervisorMappingService;

    @Mock
    private EncryptUserPassword encryptUserPassword;

    @InjectMocks
    private EmployeeMasterServiceImpl service;

    private M_Role role;

    @BeforeEach
    void setUp() {
        role = new M_Role();
        role.setRoleID(11);
        role.setRoleName("Counsellor");
        when(roleRepo.findByRoleID(anyInt())).thenReturn(role);
    }

    private static M_UserServiceRoleMapping2 mapping(Integer id, Integer roleId) {
        M_UserServiceRoleMapping2 mapping = new M_UserServiceRoleMapping2();
        mapping.setuSRMappingID(id);
        mapping.setUserID(USER_ID);
        mapping.setRoleID(roleId);
        mapping.setProviderServiceMapID(PSM_ID);
        return mapping;
    }

    private void namedRole(String name) {
        role.setRoleName(name);
    }

    private void activeFacility(Integer facilityId, Integer typeId, Integer levelValue, Integer maxLevel) {
        M_Facility facility = new M_Facility();
        facility.setFacilityID(facilityId);
        facility.setFacilityTypeID(typeId);
        when(mainStoreRepo.findByFacilityIDAndDeleted(facilityId, false)).thenReturn(facility);
        M_facilitytype type = new M_facilitytype();
        type.setFacilityTypeID(typeId);
        type.setLevelValue(levelValue);
        when(facilityTypeRepo.findByFacilityTypeID(typeId)).thenReturn(type);
        when(facilityTypeRepo.findMaxLevelValueByProviderServiceMapID(PSM_ID)).thenReturn(maxLevel);
    }

    @Nested
    @DisplayName("mapRole")
    class MapRoleTests {

        @Test
        @DisplayName("should refuse an ASHA mapping that names no facility")
        void mapRole_shouldRefuseAshaWithoutFacility() {
            namedRole("ASHA");

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.mapRole(List.of(mapping(null, 11)), "auth"));

            assertTrue(thrown.getMessage().contains("Facility (SC) is mandatory for ASHA role"),
                    thrown.getMessage());
            verify(employeeMasterRepo, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("should refuse a mapping onto a facility that has been retired")
        void mapRole_shouldRefuseRetiredFacility() {
            M_UserServiceRoleMapping2 toMap = mapping(null, 11);
            toMap.setFacilityID(501);
            when(mainStoreRepo.findByFacilityIDAndDeleted(501, false)).thenReturn(null);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.mapRole(List.of(toMap), "auth"));

            assertTrue(thrown.getMessage().contains("is no longer active"), thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse an ASHA mapped above sub-centre level")
        void mapRole_shouldRefuseAshaAboveSubCentre() {
            namedRole("ASHA");
            M_UserServiceRoleMapping2 toMap = mapping(null, 11);
            toMap.setFacilityID(501);
            activeFacility(501, 3, 2, 4);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.mapRole(List.of(toMap), "auth"));

            assertTrue(thrown.getMessage().contains("Sub-Centre (SC) level facility"), thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse a second active mapping for the same user, role and service line")
        void mapRole_shouldRefuseDuplicateMapping() {
            when(employeeMasterRepo.existsByUserIDAndRoleIDAndProviderServiceMapIDAndDeletedFalse(USER_ID, 11, PSM_ID))
                    .thenReturn(true);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.mapRole(List.of(mapping(null, 11)), "auth"));

            assertTrue(thrown.getMessage().contains("Duplicate mapping is not allowed"), thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse a second mapping for an ASHA supervisor at the same facility")
        void mapRole_shouldRefuseDuplicateSupervisorMappingAtSameFacility() {
            namedRole("ASHA Supervisor");
            M_UserServiceRoleMapping2 toMap = mapping(null, 11);
            toMap.setFacilityID(501);
            activeFacility(501, 3, 4, 4);
            when(employeeMasterRepo
                    .existsByUserIDAndRoleIDAndProviderServiceMapIDAndFacilityIDAndDeletedFalse(
                            USER_ID, 11, PSM_ID, 501))
                    .thenReturn(true);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.mapRole(List.of(toMap), "auth"));

            assertTrue(thrown.getMessage().contains("active work location mapping for this facility"),
                    thrown.getMessage());
        }

        @Test
        @DisplayName("should let an ASHA supervisor hold a second mapping at a different facility")
        void mapRole_shouldAllowSupervisorAtDifferentFacility() throws Exception {
            namedRole("ASHA Supervisor");
            M_UserServiceRoleMapping2 toMap = mapping(null, 11);
            toMap.setFacilityID(502);
            activeFacility(502, 3, 4, 4);
            ArrayList<M_UserServiceRoleMapping2> stored = new ArrayList<>(List.of(toMap));
            when(employeeMasterRepo.saveAll(anyList())).thenReturn(stored);

            assertSame(stored, service.mapRole(List.of(toMap), "auth"));
        }

        @Test
        @DisplayName("should flatten the village lists onto the stored columns")
        void mapRole_shouldFlattenVillageLists() throws Exception {
            M_UserServiceRoleMapping2 toMap = mapping(null, 11);
            toMap.setVillageID(new String[] { "501", "502" });
            toMap.setVillageName(new String[] { "Hosur", "Devanahalli" });
            when(employeeMasterRepo.saveAll(anyList()))
                    .thenReturn(new ArrayList<>(List.of(toMap)));

            service.mapRole(List.of(toMap), "auth");

            assertEquals("501,502", toMap.getVillageidDb());
            assertEquals("Hosur,Devanahalli", toMap.getVillageNameDb());
            verify(employeeMasterRepo).save(toMap);
        }

        @Test
        @DisplayName("should leave the village columns alone when the mapping names no villages")
        void mapRole_shouldLeaveVillageColumnsAlone() throws Exception {
            M_UserServiceRoleMapping2 toMap = mapping(null, 11);
            when(employeeMasterRepo.saveAll(anyList())).thenReturn(new ArrayList<>(List.of(toMap)));

            service.mapRole(List.of(toMap), "auth");

            assertNull(toMap.getVillageidDb());
            verify(employeeMasterRepo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("saveRoleMappingeditedData")
    class SaveRoleMappingTests {

        @Test
        @DisplayName("should refuse an ASHA edit that drops the facility")
        void saveRoleMapping_shouldRefuseAshaWithoutFacility() {
            namedRole("ASHA");

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.saveRoleMappingeditedData(mapping(9001, 11), "auth"));

            assertTrue(thrown.getMessage().contains("Facility (SC) is mandatory for ASHA role"),
                    thrown.getMessage());
        }

        @Test
        @DisplayName("should skip the facility rules when the mapping is only being deactivated")
        void saveRoleMapping_shouldSkipRulesWhenDeactivating() throws Exception {
            namedRole("ASHA");
            M_UserServiceRoleMapping2 toSave = mapping(9001, 11);
            toSave.setDeleted(Boolean.TRUE);
            M_UserServiceRoleMapping2 old = mapping(9001, 11);
            when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(old));
            when(employeeMasterRepo.save(toSave)).thenReturn(toSave);

            assertSame(toSave, service.saveRoleMappingeditedData(toSave, "auth"));
            verify(ashaSupervisorMappingService).cascadeDeleteByUserID(USER_ID, "Admin");
        }

        @Test
        @DisplayName("should clear the saved villages when the facility is above sub-centre level")
        void saveRoleMapping_shouldClearVillagesAboveSubCentre() throws Exception {
            M_UserServiceRoleMapping2 toSave = mapping(9001, 11);
            toSave.setFacilityID(501);
            toSave.setVillageidDb("501,502");
            toSave.setVillageNameDb("Hosur,Devanahalli");
            activeFacility(501, 3, 2, 4);
            when(employeeMasterRepo.findById(9001)).thenReturn(Optional.empty());
            when(employeeMasterRepo.save(toSave)).thenReturn(toSave);

            service.saveRoleMappingeditedData(toSave, "auth");

            assertNull(toSave.getVillageidDb(), "a non sub-centre posting keeps no village list");
            assertNull(toSave.getVillageNameDb());
        }

        @Test
        @DisplayName("should cascade the supervisor mappings when the role changes")
        void saveRoleMapping_shouldCascadeWhenRoleChanges() throws Exception {
            M_UserServiceRoleMapping2 toSave = mapping(9001, 12);
            M_UserServiceRoleMapping2 old = mapping(9001, 11);
            when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(old));
            when(employeeMasterRepo.save(toSave)).thenReturn(toSave);

            service.saveRoleMappingeditedData(toSave, "auth");

            verify(ashaSupervisorMappingService).cascadeDeleteByUserID(USER_ID, "Admin");
        }

        @Test
        @DisplayName("should cascade only this facility when a supervisor still works elsewhere")
        void saveRoleMapping_shouldCascadeOnlyThisFacilityForBusySupervisor() throws Exception {
            namedRole("ASHA Supervisor");
            M_UserServiceRoleMapping2 toSave = mapping(9001, 11);
            toSave.setDeleted(Boolean.TRUE);
            toSave.setFacilityID(501);
            M_UserServiceRoleMapping2 old = mapping(9001, 11);
            old.setFacilityID(501);
            when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(old));
            when(employeeMasterRepo.countByUserIDAndRoleIDAndDeletedFalse(USER_ID, 11)).thenReturn(2L);
            when(employeeMasterRepo.save(toSave)).thenReturn(toSave);

            service.saveRoleMappingeditedData(toSave, "auth");

            verify(ashaSupervisorMappingService).cascadeDeleteByFacilityID(501, "Admin");
            verify(ashaSupervisorMappingService, never()).cascadeDeleteByUserID(anyInt(), anyString());
        }

        @Test
        @DisplayName("should cascade every mapping when the facility itself changes")
        void saveRoleMapping_shouldCascadeWhenFacilityChanges() throws Exception {
            M_UserServiceRoleMapping2 toSave = mapping(9001, 11);
            toSave.setFacilityID(502);
            activeFacility(502, 3, 4, 4);
            M_UserServiceRoleMapping2 old = mapping(9001, 11);
            old.setFacilityID(501);
            when(employeeMasterRepo.findById(9001)).thenReturn(Optional.of(old));
            when(employeeMasterRepo.save(toSave)).thenReturn(toSave);

            service.saveRoleMappingeditedData(toSave, "auth");

            verify(ashaSupervisorMappingService).cascadeDeleteByUserID(USER_ID, "Admin");
        }

        @Test
        @DisplayName("should flatten the village lists onto the stored columns")
        void saveRoleMapping_shouldFlattenVillageLists() throws Exception {
            M_UserServiceRoleMapping2 toSave = mapping(9001, 11);
            toSave.setVillageID(new String[] { "501", "502" });
            toSave.setVillageName(new String[] { "Hosur", "Devanahalli" });
            when(employeeMasterRepo.findById(9001)).thenReturn(Optional.empty());
            when(employeeMasterRepo.save(toSave)).thenReturn(toSave);

            service.saveRoleMappingeditedData(toSave, "auth");

            assertEquals("501,502", toSave.getVillageidDb());
            assertEquals("Hosur,Devanahalli", toSave.getVillageNameDb());
        }
    }

    @Nested
    @DisplayName("cascadeDeleteAshaMappingsForDeactivation")
    class CascadeDeactivationTests {

        @Test
        @DisplayName("should retire only this facility when the supervisor still works elsewhere")
        void cascade_shouldRetireOnlyThisFacility() {
            namedRole("ASHA Supervisor");
            M_UserServiceRoleMapping2 usrRole = mapping(9001, 11);
            usrRole.setFacilityID(501);
            when(employeeMasterRepo.countByUserIDAndRoleIDAndDeletedFalse(USER_ID, 11)).thenReturn(2L);

            service.cascadeDeleteAshaMappingsForDeactivation(usrRole);

            verify(ashaSupervisorMappingService).cascadeDeleteByUserIDAndFacilityID(USER_ID, 501, "Admin");
            verify(ashaSupervisorMappingService, never()).cascadeDeleteByUserID(anyInt(), anyString());
        }

        @Test
        @DisplayName("should retire every mapping when this was the supervisor's last facility")
        void cascade_shouldRetireEveryMappingOnLastFacility() {
            namedRole("ASHA Supervisor");
            M_UserServiceRoleMapping2 usrRole = mapping(9001, 11);
            usrRole.setFacilityID(501);
            when(employeeMasterRepo.countByUserIDAndRoleIDAndDeletedFalse(USER_ID, 11)).thenReturn(1L);

            service.cascadeDeleteAshaMappingsForDeactivation(usrRole);

            verify(ashaSupervisorMappingService).cascadeDeleteByUserID(USER_ID, "Admin");
        }

        @Test
        @DisplayName("should retire every mapping for a role that is not a supervisor")
        void cascade_shouldRetireEveryMappingForNonSupervisor() {
            service.cascadeDeleteAshaMappingsForDeactivation(mapping(9001, 11));

            verify(ashaSupervisorMappingService).cascadeDeleteByUserID(USER_ID, "Admin");
        }

        @Test
        @DisplayName("cascadeDeleteAshaMappingsForUser should hand the user straight to the mapping service")
        void cascadeForUser_shouldDelegate() {
            service.cascadeDeleteAshaMappingsForUser(USER_ID);

            verify(ashaSupervisorMappingService).cascadeDeleteByUserID(USER_ID, "Admin");
        }
    }

    @Nested
    @DisplayName("Employee lookups")
    class LookupTests {

        @Test
        @DisplayName("getAllRole should rebuild each role from what the repository holds")
        void getAllRole_shouldRebuildRoles() {
            M_Role stored = new M_Role();
            stored.setRoleID(11);
            stored.setRoleName("Counsellor");
            stored.setProviderServiceMapID(PSM_ID);
            when(roleRepo.getAllRole()).thenReturn(new ArrayList<>(List.of(stored)));

            ArrayList<M_Role> roles = service.getAllRole();

            assertEquals(1, roles.size());
            assertEquals("Counsellor", roles.get(0).getRoleName());
            assertEquals(PSM_ID, roles.get(0).getProviderServiceMapID());
        }

        @Test
        @DisplayName("getEmployeeDetails should skip a row the query could not fill")
        void getEmployeeDetails_shouldSkipUnfillableRow() {
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(null);
            rows.add(new Object[] { 9001, USER_ID, 11, PSM_ID, "a", "b", "c", "d", 1, "e", 2, "f", "g" });
            when(employeeMasterRepo.getEmployeeDetails()).thenReturn(rows);

            assertEquals(1, service.getEmployeeDetails().size());
        }

        @Test
        @DisplayName("getAllTitle should rebuild each title from what the repository holds")
        void getAllTitle_shouldRebuildTitles() {
            M_Title stored = new M_Title();
            stored.setTitleID(1);
            stored.setTitleName("Dr");
            when(m_TitleRepo.getAllTitle()).thenReturn(new ArrayList<>(List.of(stored)));

            assertEquals("Dr", service.getAllTitle().get(0).getTitleName());
        }

        @Test
        @DisplayName("getAllGender should rebuild each gender from what the repository holds")
        void getAllGender_shouldRebuildGenders() {
            M_Gender stored = new M_Gender();
            stored.setGenderID(1);
            stored.setGenderName("Female");
            when(m_GenderRepo.getAllGender()).thenReturn(new ArrayList<>(List.of(stored)));

            assertEquals("Female", service.getAllGender().get(0).getGenderName());
        }

        @Test
        @DisplayName("FindEmployeeName should distinguish a taken user name from a free one")
        void findEmployeeName_shouldDistinguishTakenFromFree() {
            when(employeeMasterRepoo.findEmployeeByName("asha.rao")).thenReturn(new M_User1());
            when(employeeMasterRepoo.findEmployeeByName("new.user")).thenReturn(null);

            assertEquals("userexist", service.FindEmployeeName("asha.rao"));
            assertEquals("usernotexist", service.FindEmployeeName("new.user"));
        }

        @Test
        @DisplayName("FindEmployeeContact should distinguish a taken number from a free one")
        void findEmployeeContact_shouldDistinguishTakenFromFree() {
            when(employeeMasterRepoo.findEmployeeByContact("9000000001")).thenReturn(new M_User1());
            when(employeeMasterRepoo.findEmployeeByContact("9000000002")).thenReturn(null);

            assertEquals("contactexist", service.FindEmployeeContact("9000000001"));
            assertEquals("contactnotexist", service.FindEmployeeContact("9000000002"));
        }

        @Test
        @DisplayName("FindEmployeeAadhaar should distinguish a taken number from a free one")
        void findEmployeeAadhaar_shouldDistinguishTakenFromFree() {
            when(employeeMasterRepoo.findEmployeeAadhaarNo("111122223333")).thenReturn(new M_User1());
            when(employeeMasterRepoo.findEmployeeAadhaarNo("444455556666")).thenReturn(null);

            assertEquals("aadhaarexist", service.FindEmployeeAadhaar("111122223333"));
            assertEquals("aadhaarnotexist", service.FindEmployeeAadhaar("444455556666"));
        }

        @Test
        @DisplayName("FindEmployeeName1 should answer the user record itself")
        void findEmployeeName1_shouldAnswerTheRecord() {
            M_User1 stored = new M_User1();
            when(employeeMasterRepoo.findEmployeeByName("asha.rao")).thenReturn(stored);

            assertSame(stored, service.FindEmployeeName1("asha.rao"));
        }

        @Test
        @DisplayName("checkingEmpDetails should report whether the identifiers are already in use")
        void checkingEmpDetails_shouldReportWhetherIdentifiersAreTaken() {
            when(employeeMasterRepoo.checkingEmpDetails("asha.rao", "1", "2", "3", "4")).thenReturn(new M_User1());
            when(employeeMasterRepoo.checkingEmpDetails("new.user", "1", "2", "3", "4")).thenReturn(null);

            assertTrue(service.checkingEmpDetails("asha.rao", "1", "2", "3", "4"));
            assertFalse(service.checkingEmpDetails("new.user", "1", "2", "3", "4"));
        }

        @Test
        @DisplayName("getQualification should hand back what the repository holds")
        void getQualification_shouldHandBackRepositoryContents() {
            ArrayList<M_Userqualification> stored = new ArrayList<>(List.of(new M_Userqualification()));
            when(m_QualificationRepo.getAllQualification()).thenReturn(stored);

            assertSame(stored, service.getQualification());
        }

        @Test
        @DisplayName("getlocationByMapid2 should hand back what the repository holds")
        void getlocationByMapid2_shouldHandBackRepositoryContents() {
            ArrayList<Showofficedetails1> stored = new ArrayList<>(List.of(new Showofficedetails1()));
            when(showofficedetails1Repo1.getlocationByMapid(PSM_ID, 301)).thenReturn(stored);

            assertSame(stored, service.getlocationByMapid2(PSM_ID, 301));
        }

        @Test
        @DisplayName("getAllByMapId2 should hand back what the repository holds")
        void getAllByMapId2_shouldHandBackRepositoryContents() {
            ArrayList<M_ProviderServiceMap1> stored = new ArrayList<>(List.of(new M_ProviderServiceMap1()));
            when(m_ProviderServiceMap1Repo.getAllByMapId2(77, 29, 3)).thenReturn(stored);

            assertSame(stored, service.getAllByMapId2(77, 29, 3));
        }

        @Test
        @DisplayName("the narrowing searches should each reach their own repository query")
        void narrowingSearches_shouldReachTheirOwnQuery() {
            ArrayList<Showuserdetailsfromuserservicerolemapping> stored =
                    new ArrayList<>(List.of(new Showuserdetailsfromuserservicerolemapping()));
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails2(77, 29)).thenReturn(stored);
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails3(77, 11)).thenReturn(stored);
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails4(77, 3)).thenReturn(stored);
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails6(77, USER_ID)).thenReturn(stored);
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails7(77, 29, 301)).thenReturn(stored);
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails8(77, 29, 301, 401)).thenReturn(stored);
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails9(77, 29, 11)).thenReturn(stored);
            when(showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails10(77, 29, 11, 3, "asha.rao", USER_ID))
                    .thenReturn(stored);

            assertSame(stored, service.getEmployeeDetails2(77, 29));
            assertSame(stored, service.getEmployeeDetails3(77, 11));
            assertSame(stored, service.getEmployeeDetails4(77, 3));
            assertSame(stored, service.getEmployeeDetails6(77, USER_ID));
            assertSame(stored, service.getEmployeeDetails7(77, 29, 301));
            assertSame(stored, service.getEmployeeDetails8(77, 29, 301, 401));
            assertSame(stored, service.getEmployeeDetails9(77, 29, 11));
            assertSame(stored, service.getEmployeeDetails10(77, 29, 11, 3, "asha.rao", USER_ID));
        }

        @Test
        @DisplayName("getEmployeeDetails5 should hand back what the view holds")
        void getEmployeeDetails5_shouldHandBackViewContents() {
            ArrayList<V_Showuser> stored = new ArrayList<>(List.of(new V_Showuser()));
            when(v_ShowuserRepo.EmployeeDetails5()).thenReturn(stored);

            assertSame(stored, service.getEmployeeDetails5());
        }

        @Test
        @DisplayName("getcompleteUserDetails should hand back what the view holds")
        void getcompleteUserDetails_shouldHandBackViewContents() {
            ArrayList<V_Showuser> stored = new ArrayList<>(List.of(new V_Showuser()));
            when(v_ShowuserRepo.getAdminDetails()).thenReturn(stored);

            assertSame(stored, service.getcompleteUserDetails());
        }

        @Test
        @DisplayName("getAllReligion and getAllCommunity should hand back what the repositories hold")
        void masters_shouldHandBackRepositoryContents() {
            ArrayList<M_Religion> religions = new ArrayList<>(List.of(new M_Religion()));
            ArrayList<M_Community> communities = new ArrayList<>(List.of(new M_Community()));
            when(m_ReligionRepo.findAll()).thenReturn(religions);
            when(m_CommunityRepo.findAll()).thenReturn(communities);

            assertEquals(1, service.getAllReligion().size());
            assertEquals(1, service.getAllCommunity().size());
        }
    }

    @Nested
    @DisplayName("getEmployeeDetails4 by provider")
    class EmployeeDetails4Tests {

        @Test
        @DisplayName("should mark a user locked out after failed sign-ins")
        void getEmployeeDetails4_shouldMarkLockedOutUser() {
            V_Showuser user = new V_Showuser();
            user.setUserID(USER_ID);
            M_User1 record = new M_User1();
            record.setUserID(USER_ID);
            record.setFailedAttempt(3);
            record.setDeleted(Boolean.TRUE);
            record.setLockTimestamp(Timestamp.valueOf("2026-02-17 09:30:00"));
            when(v_ShowuserRepo.EmployeeDetails4(77)).thenReturn(new ArrayList<>(List.of(user)));
            when(employeeMasterRepoo.findByUserIDIn(anyList())).thenReturn(new ArrayList<>(List.of(record)));

            V_Showuser enriched = service.getEmployeeDetails4(77).get(0);

            assertEquals(3, enriched.getFailedAttempt());
            assertTrue(enriched.getLockedDueToFailedAttempts());
        }

        @Test
        @DisplayName("should not mark a user locked out while their account is still active")
        void getEmployeeDetails4_shouldNotMarkActiveUserLockedOut() {
            V_Showuser user = new V_Showuser();
            user.setUserID(USER_ID);
            M_User1 record = new M_User1();
            record.setUserID(USER_ID);
            record.setDeleted(Boolean.FALSE);
            record.setLockTimestamp(Timestamp.valueOf("2026-02-17 09:30:00"));
            when(v_ShowuserRepo.EmployeeDetails4(77)).thenReturn(new ArrayList<>(List.of(user)));
            when(employeeMasterRepoo.findByUserIDIn(anyList())).thenReturn(new ArrayList<>(List.of(record)));

            V_Showuser enriched = service.getEmployeeDetails4(77).get(0);

            assertEquals(0, enriched.getFailedAttempt());
            assertFalse(enriched.getLockedDueToFailedAttempts());
        }

        @Test
        @DisplayName("should answer an empty result without asking for user records")
        void getEmployeeDetails4_shouldAnswerEmptyWithoutFurtherLookups() {
            when(v_ShowuserRepo.EmployeeDetails4(77)).thenReturn(new ArrayList<>());

            assertTrue(service.getEmployeeDetails4(77).isEmpty());
            verify(employeeMasterRepoo, never()).findByUserIDIn(anyList());
        }
    }

    @Nested
    @DisplayName("Persistence")
    class PersistenceTests {

        @Test
        @DisplayName("saveEmployee should answer the id of the stored user and encrypt its credentials")
        void saveEmployee_shouldStoreAndEncrypt() {
            M_User1 toSave = new M_User1();
            M_User1 stored = new M_User1();
            stored.setUserID(USER_ID);
            when(employeeMasterRepo11.save(toSave)).thenReturn(stored);
            when(encryptUserPassword.encryptUserCredentials(stored)).thenReturn(new OutputResponse());

            assertEquals(USER_ID, service.saveEmployee(toSave));
            verify(encryptUserPassword).encryptUserCredentials(stored);
        }

        @Test
        @DisplayName("saveEditData should re-encrypt the credentials it saved")
        void saveEditData_shouldReEncryptCredentials() {
            M_User1 toSave = new M_User1();
            when(employeeMasterRepo11.save(toSave)).thenReturn(toSave);

            assertSame(toSave, service.saveEditData(toSave));
            verify(encryptUserPassword).encryptUserCredentials(toSave);
        }

        @Test
        @DisplayName("saveDemography should answer the id of the stored demographics")
        void saveDemography_shouldAnswerStoredId() {
            M_UserDemographics stored = new M_UserDemographics();
            stored.setDemographicID(5001);
            when(m_UserDemographicsRepo.save(any())).thenReturn(stored);

            assertEquals(5001, service.saveDemography(new M_UserDemographics()));
            assertEquals(5001, service.saveeditDemo(new M_UserDemographics()));
        }

        @Test
        @DisplayName("saveeditlangdata should answer the id of the stored language mapping")
        void saveeditlangdata_shouldAnswerStoredId() {
            M_UserLangMapping stored = new M_UserLangMapping();
            stored.setUserLangID(7001);
            when(m_UserLangMappingRepo.save(any())).thenReturn(stored);

            assertEquals(7001, service.saveeditlangdata(new M_UserLangMapping()));
        }

        @Test
        @DisplayName("mapLanguage and mapRoleUpdation should hand their batches to the repositories")
        void batches_shouldReachTheRepositories() {
            ArrayList<M_UserLangMapping> languages = new ArrayList<>();
            ArrayList<M_UserServiceRoleMapping2> roles = new ArrayList<>();
            when(m_UserLangMappingRepo.saveAll(anyList())).thenReturn(languages);
            when(employeeMasterRepo.saveAll(anyList())).thenReturn(roles);

            assertSame(languages, service.mapLanguage(new ArrayList<>()));
            assertSame(roles, service.mapRoleUpdation(new ArrayList<>()));
        }

        @Test
        @DisplayName("saveeditedData should clear the failed sign-in count for a reinstated user")
        void saveeditedData_shouldClearFailedAttemptsOnReinstatement() {
            M_User1 toSave = new M_User1();
            toSave.setDeleted(Boolean.FALSE);
            toSave.setFailedAttempt(3);
            when(employeeMasterRepoo.save(toSave)).thenReturn(toSave);

            service.saveeditedData(toSave);

            assertEquals(0, toSave.getFailedAttempt());
        }

        @Test
        @DisplayName("saveeditedData should leave the failed sign-in count alone for a deactivated user")
        void saveeditedData_shouldLeaveFailedAttemptsAloneOnDeactivation() {
            M_User1 toSave = new M_User1();
            toSave.setDeleted(Boolean.TRUE);
            toSave.setFailedAttempt(3);
            when(employeeMasterRepoo.save(toSave)).thenReturn(toSave);

            service.saveeditedData(toSave);

            assertEquals(3, toSave.getFailedAttempt());
        }

        @Test
        @DisplayName("createProviderAdmin should hash the password before it is stored")
        void createProviderAdmin_shouldHashPassword() throws Exception {
            M_User1 toCreate = new M_User1();
            toCreate.setPassword("plain-secret");
            ArrayList<M_User1> stored = new ArrayList<>(List.of(toCreate));
            when(employeeMasterRepoo.saveAll(anyList())).thenReturn(stored);

            service.createProviderAdmin(List.of(toCreate));

            assertTrue(toCreate.getPassword().startsWith("1001:"),
                    "the stored password must be the salted hash, not the plain text");
        }

        @Test
        @DisplayName("createProviderAdmin should refuse an admin with no password")
        void createProviderAdmin_shouldRefuseWithoutPassword() {
            assertThrows(Exception.class, () -> service.createProviderAdmin(List.of(new M_User1())));
        }

        @Test
        @DisplayName("createNewUser should hash the password before it is stored")
        void createNewUser_shouldHashPassword() throws Exception {
            M_User1 toCreate = new M_User1();
            toCreate.setPassword("plain-secret");
            when(employeeMasterRepoo.saveAll(anyList())).thenReturn(new ArrayList<>(List.of(toCreate)));

            service.createNewUser(List.of(toCreate));

            assertFalse("plain-secret".equals(toCreate.getPassword()));
        }

        @Test
        @DisplayName("createNewUser should refuse a user with no password")
        void createNewUser_shouldRefuseWithoutPassword() {
            assertThrows(Exception.class, () -> service.createNewUser(List.of(new M_User1())));
        }

        @Test
        @DisplayName("generateStrongPassword should answer a different hash each time it is called")
        void generateStrongPassword_shouldSaltEachHash() throws Exception {
            String first = service.generateStrongPassword("plain-secret");
            String second = service.generateStrongPassword("plain-secret");

            assertNotNull(first);
            assertFalse(first.equals(second), "each hash must carry its own salt");
        }

        @Test
        @DisplayName("saveBulkUserEmployee should answer the record the repository stored")
        void saveBulkUserEmployee_shouldAnswerStoredRecord() {
            M_User1 stored = new M_User1();
            stored.setUserID(USER_ID);
            when(employeeMasterRepo11.save(any())).thenReturn(stored);

            assertSame(stored, service.saveBulkUserEmployee(new M_User1()));
        }

        @Test
        @DisplayName("the single-record lookups should each reach their own repository query")
        void singleRecordLookups_shouldReachTheirOwnQuery() {
            M_User1 user = new M_User1();
            M_UserDemographics demographics = new M_UserDemographics();
            M_UserLangMapping language = new M_UserLangMapping();
            M_UserServiceRoleMapping2 roleMapping = mapping(9001, 11);
            when(employeeMasterRepo11.editEmployee(USER_ID)).thenReturn(user);
            when(employeeMasterRepoo.findByUserID(USER_ID)).thenReturn(user);
            when(m_UserDemographicsRepo.mdedit(USER_ID)).thenReturn(demographics);
            when(m_UserDemographicsRepo.findByUserID(USER_ID)).thenReturn(demographics);
            when(m_UserDemographicsRepo.save(demographics)).thenReturn(demographics);
            when(m_UserLangMappingRepo.ulangmapedit(USER_ID, 1)).thenReturn(language);
            when(m_UserLangMappingRepo.findByUserLangID(7001)).thenReturn(language);
            when(m_UserLangMappingRepo.save(language)).thenReturn(language);
            when(employeeMasterRepo.uRoleMedit(USER_ID, 11)).thenReturn(roleMapping);
            when(employeeMasterRepo.uRoledelte(9001)).thenReturn(roleMapping);
            when(employeeMasterRepo.findByUSRMappingID(9001)).thenReturn(roleMapping);
            when(employeeMasterRepo.save(roleMapping)).thenReturn(roleMapping);

            assertSame(user, service.editEmployee(USER_ID));
            assertSame(user, service.editData(USER_ID));
            assertSame(user, service.getProviderAdminForEdit(USER_ID));
            assertSame(demographics, service.mdedit(USER_ID));
            assertSame(demographics, service.DataByUserID(USER_ID));
            assertSame(demographics, service.saveeditedDemoData(demographics));
            assertSame(language, service.ulangmapedit(USER_ID, 1));
            assertSame(language, service.updateLangMapping(7001));
            assertSame(language, service.saveUserLangEditedData(language));
            assertSame(roleMapping, service.uRoleMedit(USER_ID, 11));
            assertSame(roleMapping, service.uRoledelte(9001));
            assertSame(roleMapping, service.getDataUsrId(9001));
            assertSame(roleMapping, service.saveRoleEdit(roleMapping));
        }

        @Test
        @DisplayName("SaveDemographics should hand its batch to the repository")
        void saveDemographics_shouldHandBatchToRepository() {
            ArrayList<M_UserDemographics> stored = new ArrayList<>();
            when(m_UserDemographicsRepo.saveAll(anyList())).thenReturn(stored);

            assertSame(stored, service.SaveDemographics(new ArrayList<>()));
        }

        @Test
        @DisplayName("getProviderAdmin should hand back what the repository holds")
        void getProviderAdmin_shouldHandBackRepositoryContents() {
            ArrayList<M_User1> stored = new ArrayList<>(List.of(new M_User1()));
            when(employeeMasterRepoo.getAllProviderAdminData()).thenReturn(stored);

            assertSame(stored, service.getProviderAdmin());
        }
    }

    @Nested
    @DisplayName("ResetPassword")
    class ResetPasswordTests {

        @Test
        @DisplayName("should report success when the credential service accepts the new password")
        void resetPassword_shouldReportSuccess() {
            OutputResponse accepted = new OutputResponse();
            accepted.setResponse("done");
            when(encryptUserPassword.encryptUserCredentials(any())).thenReturn(accepted);

            assertEquals("Password reset successfully", service.ResetPassword(new M_User1()));
        }

        @Test
        @DisplayName("should report failure when the credential service refuses the new password")
        void resetPassword_shouldReportFailure() {
            when(encryptUserPassword.encryptUserCredentials(any())).thenReturn(new OutputResponse());

            assertEquals("Password Not Set Properly", service.ResetPassword(new M_User1()));
        }
    }

    @Nested
    @DisplayName("getMappedRole")
    class GetMappedRoleTests {

        @Test
        @DisplayName("should split the stored village columns back into lists")
        void getMappedRole_shouldSplitVillageColumns() {
            V_Userservicerolemapping mapping = new V_Userservicerolemapping();
            mapping.setuSRMappingID(9001);
            mapping.setServiceID(3);
            mapping.setStateID(29);
            mapping.setWorkingDistrictID("301");
            mapping.setVillageidDb("501,502");
            mapping.setVillageNameDb("Hosur,Devanahalli");
            when(v_UserservicerolemappingRepo.getAllRoleOfProvider(77))
                    .thenReturn(new ArrayList<>(List.of(mapping)));
            when(employeeMasterRepo.getFacilityInfoByMappingIDs(anyList())).thenReturn(new ArrayList<>());

            V_Userservicerolemapping answered = service.getMappedRole(77).get(0);

            assertArrayEquals(new String[] { "501", "502" }, answered.getVillageID());
            assertArrayEquals(new String[] { "Hosur", "Devanahalli" }, answered.getVillageName());
        }

        @Test
        @DisplayName("should clear the block and village details for a mapping with no service line")
        void getMappedRole_shouldClearBlockAndVillageWithoutService() {
            V_Userservicerolemapping mapping = new V_Userservicerolemapping();
            mapping.setuSRMappingID(9001);
            mapping.setStateID(29);
            mapping.setWorkingDistrictID("301");
            mapping.setVillageidDb("501");
            when(v_UserservicerolemappingRepo.getAllRoleOfProvider(77))
                    .thenReturn(new ArrayList<>(List.of(mapping)));
            when(employeeMasterRepo.getFacilityInfoByMappingIDs(anyList())).thenReturn(new ArrayList<>());

            V_Userservicerolemapping answered = service.getMappedRole(77).get(0);

            assertNull(answered.getVillageID());
            assertNull(answered.getVillageidDb());
            assertNull(answered.getBlockID());
        }

        @Test
        @DisplayName("should fill in the state and district the view could not resolve")
        void getMappedRole_shouldFillInMissingStateAndDistrict() {
            V_Userservicerolemapping mapping = new V_Userservicerolemapping();
            mapping.setuSRMappingID(9001);
            mapping.setServiceID(3);
            when(v_UserservicerolemappingRepo.getAllRoleOfProvider(77))
                    .thenReturn(new ArrayList<>(List.of(mapping)));
            when(employeeMasterRepo.getDirectStateDistrictByMappingIDs(anyList()))
                    .thenReturn(List.<Object[]>of(new Object[] { 9001, 29, "Karnataka", 301, "Bengaluru Urban", 401, "North" }));
            when(employeeMasterRepo.getFacilityInfoByMappingIDs(anyList())).thenReturn(new ArrayList<>());

            V_Userservicerolemapping answered = service.getMappedRole(77).get(0);

            assertEquals(29, answered.getStateID());
            assertEquals("Karnataka", answered.getStateName());
            assertEquals("301", answered.getWorkingDistrictID());
            assertEquals("North", answered.getBlockName());
        }

        @Test
        @DisplayName("should attach the facility details the batch lookup resolves")
        void getMappedRole_shouldAttachFacilityDetails() {
            V_Userservicerolemapping mapping = new V_Userservicerolemapping();
            mapping.setuSRMappingID(9001);
            mapping.setServiceID(3);
            mapping.setStateID(29);
            mapping.setWorkingDistrictID("301");
            when(v_UserservicerolemappingRepo.getAllRoleOfProvider(77))
                    .thenReturn(new ArrayList<>(List.of(mapping)));
            when(employeeMasterRepo.getFacilityInfoByMappingIDs(anyList()))
                    .thenReturn(List.<Object[]>of(new Object[] { 9001, 501, "PHC North", 3, "Rural" }));

            V_Userservicerolemapping answered = service.getMappedRole(77).get(0);

            assertEquals(501, answered.getFacilityID());
            assertEquals("PHC North", answered.getFacilityName());
            assertEquals("Rural", answered.getRuralUrban());
        }

        @Test
        @DisplayName("should answer an empty list when the view holds nothing")
        void getMappedRole_shouldAnswerEmptyListForEmptyView() {
            when(v_UserservicerolemappingRepo.getAllRoleOfProvider(77)).thenReturn(null);

            assertTrue(service.getMappedRole(77).isEmpty());
        }

        @Test
        @DisplayName("should search by user id when the caller sends no name")
        void getMappedRole_shouldSearchByUserIdWithoutAName() {
            ArrayList<V_Userservicerolemapping> stored = new ArrayList<>();
            when(v_UserservicerolemappingRepo.getDataByUserID(USER_ID)).thenReturn(stored);

            assertSame(stored, service.getMappedRole("", USER_ID));
        }

        @Test
        @DisplayName("should search by name when the caller sends no usable user id")
        void getMappedRole_shouldSearchByNameWithoutAUserId() {
            ArrayList<V_Userservicerolemapping> stored = new ArrayList<>();
            when(v_UserservicerolemappingRepo.getDataByName("Asha Rao")).thenReturn(stored);

            assertSame(stored, service.getMappedRole("Asha Rao", 0));
        }

        @Test
        @DisplayName("should refuse a search that names both a user and an id")
        void getMappedRole_shouldRefuseAmbiguousSearch() {
            assertThrows(DataNotFound.class, () -> service.getMappedRole("Asha Rao", USER_ID));
        }
    }

    @Nested
    @DisplayName("searchMappedLangugeByUserId")
    class SearchMappedLanguageTests {

        @Test
        @DisplayName("should answer the languages mapped to a real user")
        void searchMappedLanguage_shouldAnswerMappedLanguages() {
            ArrayList<M_UserLangMapping> stored = new ArrayList<>();
            when(m_UserLangMappingRepo.getmappedlanguageData(USER_ID)).thenReturn(stored);

            assertSame(stored, service.searchMappedLangugeByUserId(USER_ID));
        }

        @Test
        @DisplayName("should refuse a search that names no user")
        void searchMappedLanguage_shouldRefuseSearchWithoutUser() {
            assertThrows(DataNotFound.class, () -> service.searchMappedLangugeByUserId(0));
        }
    }

    @Nested
    @DisplayName("getMappedLanguge")
    class GetMappedLanguageTests {

        @Test
        @DisplayName("should rebuild one mapping per row the query answers")
        void getMappedLanguge_shouldRebuildEachRow() {
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(null);
            rows.add(new Object[] { 7001, USER_ID, 1, 5, "Kannada", "asha.rao", true, true, true, "n", false,
                    5, 5, 5, false });
            when(m_UserLangMappingRepo.getMappedLanguge(77)).thenReturn(rows);

            assertEquals(1, service.getMappedLanguge(77).size());
        }
    }

    @Nested
    @DisplayName("getEmployeeByDesiganationID")
    class EmployeeByDesignationTests {

        @Test
        @DisplayName("should mark a user whose signature is on file as active")
        void byDesignation_shouldMarkActiveSignature() {
            M_User1 user = new M_User1();
            user.setUserID(USER_ID);
            EmployeeSignature signature = new EmployeeSignature();
            signature.setDeleted(Boolean.FALSE);
            when(employeeMasterRepoo.getempByDesiganation(7, 77)).thenReturn(new ArrayList<>(List.of(user)));
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(signature);

            assertEquals("Active", service.getEmployeeByDesiganationID(7, 77).get(0).getSignatureStatus());
        }

        @Test
        @DisplayName("should mark a user whose signature has been retired as inactive")
        void byDesignation_shouldMarkRetiredSignature() {
            M_User1 user = new M_User1();
            user.setUserID(USER_ID);
            EmployeeSignature signature = new EmployeeSignature();
            signature.setDeleted(Boolean.TRUE);
            when(employeeMasterRepoo.getempByDesiganation(7, 77)).thenReturn(new ArrayList<>(List.of(user)));
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(signature);

            assertEquals("InActive", service.getEmployeeByDesiganationID(7, 77).get(0).getSignatureStatus());
        }

        @Test
        @DisplayName("should leave the signature status unset for a user with none on file")
        void byDesignation_shouldLeaveStatusUnsetWithoutSignature() {
            M_User1 user = new M_User1();
            user.setUserID(USER_ID);
            when(employeeMasterRepoo.getempByDesiganation(7, 77)).thenReturn(new ArrayList<>(List.of(user)));
            when(employeeSignatureRepo.findOneByUserID(3117L)).thenReturn(null);

            assertNull(service.getEmployeeByDesiganationID(7, 77).get(0).getSignatureStatus());
        }
    }

    @Nested
    @DisplayName("getUserRoleTM")
    class UserRoleTmTests {

        @Test
        @DisplayName("should rebuild one role per row the query answers")
        void getUserRoleTM_shouldRebuildEachRow() {
            M_UserservicerolemappingForRoleProviderAdmin request =
                    new M_UserservicerolemappingForRoleProviderAdmin();
            request.setUserID(USER_ID);
            request.setProviderServiceMapID(PSM_ID);
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(new Object[] { USER_ID, 11, "TC Specialist", false, 21, "Specialist screen", false });
            when(userservicerolemappingForRoleProviderAdminRepo.getroleofuserTM(USER_ID, PSM_ID)).thenReturn(rows);

            ArrayList<UserRole> roles = service.getUserRoleTM(request);

            assertEquals(1, roles.size());
            assertEquals("TC Specialist", roles.get(0).getRolename());
        }

        @Test
        @DisplayName("should answer an empty list when the user holds no telemedicine role")
        void getUserRoleTM_shouldAnswerEmptyListWithoutRoles() {
            M_UserservicerolemappingForRoleProviderAdmin request =
                    new M_UserservicerolemappingForRoleProviderAdmin();
            when(userservicerolemappingForRoleProviderAdminRepo.getroleofuserTM(any(), any()))
                    .thenReturn(new ArrayList<>());

            assertTrue(service.getUserRoleTM(request).isEmpty());
        }
    }

    @Nested
    @DisplayName("createAgent")
    class CreateAgentTests {

        @Test
        @DisplayName("should fill the agent and server placeholders into the configured URL")
        void createAgent_shouldFillPlaceholders() {
            com.iemr.admin.utils.config.ConfigProperties properties =
                    new com.iemr.admin.utils.config.ConfigProperties();
            service.setConfigProperties(properties);

            String url = service.createAgent("A-1", "asha.rao");

            assertNotNull(url);
            assertFalse(url.contains("AGENTID"), url);
        }
    }
}
