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
package com.iemr.admin.service.rolemaster;

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

import com.iemr.admin.data.rolemaster.M_Screen;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.RoleMaster;
import com.iemr.admin.data.rolemaster.RoleScreenMapping;
import com.iemr.admin.data.rolemaster.StateMasterForRole;
import com.iemr.admin.data.rolemaster.StateServiceMapping;
import com.iemr.admin.repository.rolemaster.M_RoleRepo;
import com.iemr.admin.repository.rolemaster.M_ScreenRepo;
import com.iemr.admin.repository.rolemaster.M_UserservicerolemappingForRoleProviderAdminRepo;
import com.iemr.admin.repository.rolemaster.RoleMasterRepo;
import com.iemr.admin.repository.rolemaster.RoleScreenMappingRepo;
import com.iemr.admin.repository.rolemaster.StateMasterRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The role master service reads the role catalogue and the screens each role
 * unlocks, choosing a national or state-scoped query from the flag the caller
 * sends.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Role_Master_ServiceImpl Test Suite")
class RoleMasterServiceImplTest {

    private static final Integer PROVIDER_ID = 77;
    private static final Integer PSM_ID = 4001;

    @Mock
    private StateMasterRepo stateMasterRepo;

    @Mock
    private jakarta.persistence.EntityManager entityManager;

    @Mock
    private M_UserservicerolemappingForRoleProviderAdminRepo m_UserservicerolemappingForRoleProviderAdminRepo;

    @Mock
    private RoleScreenMappingRepo roleScreenMappingRepo;

    @Mock
    private M_ScreenRepo m_ScreenRepo;

    @Mock
    private RoleMasterRepo roleMasterRepo;

    @Mock
    private M_RoleRepo mRoleRepo;

    @InjectMocks
    private Role_Master_ServiceImpl service;

    @Test
    @DisplayName("getStateByServiceProviderId should skip a row the query could not fill")
    void getStateByServiceProviderId_shouldSkipUnfillableRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new Object[] { 29, "Karnataka", PSM_ID });
        when(roleMasterRepo.getStateByServiceProviderId(PROVIDER_ID)).thenReturn(rows);

        ArrayList<StateServiceMapping> mappings = service.getStateByServiceProviderId(PROVIDER_ID);

        assertEquals(1, mappings.size());
        assertEquals(29, mappings.get(0).getStateID());
    }

    @Test
    @DisplayName("getServiceByServiceProviderIdAndStateId should rebuild one mapping per row")
    void getServiceByServiceProviderIdAndStateId_shouldRebuildEachRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { PSM_ID, 3, "Tele Medicine", PROVIDER_ID });
        when(roleMasterRepo.getServiceByServiceProviderIdAndStateId(PROVIDER_ID, 29)).thenReturn(rows);

        assertEquals(1, service.getServiceByServiceProviderIdAndStateId(PROVIDER_ID, 29).size());
    }

    @Test
    @DisplayName("getAllRoleByMapId should answer nothing, as the catalogue is read elsewhere")
    void getAllRoleByMapId_shouldAnswerNothing() {
        assertNull(service.getAllRoleByMapId());
    }

    @Test
    @DisplayName("getAllByMapId should narrow by state for a state-scoped service line")
    void getAllByMapId_shouldNarrowByStateForStateService() {
        ArrayList<StateServiceMapping> stored = new ArrayList<>();
        when(roleMasterRepo.getAllByMapId(PROVIDER_ID, 29, 3)).thenReturn(stored);

        assertSame(stored, service.getAllByMapId(PROVIDER_ID, 29, 3, Boolean.FALSE));
        verify(roleMasterRepo).getAllByMapId(PROVIDER_ID, 29, 3);
    }

    @Test
    @DisplayName("getAllByMapId should ignore the state for a national service line")
    void getAllByMapId_shouldIgnoreStateForNationalService() {
        ArrayList<StateServiceMapping> stored = new ArrayList<>();
        when(roleMasterRepo.getAlByMapId(PROVIDER_ID, 3)).thenReturn(stored);

        assertSame(stored, service.getAllByMapId(PROVIDER_ID, 29, 3, Boolean.TRUE));
        verify(roleMasterRepo).getAlByMapId(PROVIDER_ID, 3);
    }

    @Test
    @DisplayName("getAllByMapId with two arguments should ignore the state altogether")
    void getAllByMapId_twoArguments_shouldIgnoreState() {
        ArrayList<StateServiceMapping> stored = new ArrayList<>();
        when(roleMasterRepo.getAlByMapId(PROVIDER_ID, 3)).thenReturn(stored);

        assertSame(stored, service.getAllByMapId(PROVIDER_ID, 3));
    }

    @Test
    @DisplayName("getProStateServRoles should skip a row the query could not fill")
    void getProStateServRoles_shouldSkipUnfillableRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new Object[] { 11, "Counsellor", "Handles calls", Boolean.FALSE, "admin", PSM_ID });
        when(roleScreenMappingRepo.getAllRoleByMapId(PSM_ID)).thenReturn(rows);

        ArrayList<RoleMaster> roles = service.getProStateServRoles(PSM_ID);

        assertEquals(1, roles.size());
        assertEquals("Counsellor", roles.get(0).getRoleName());
    }

    @Test
    @DisplayName("getRoleMasterTM should read the same catalogue as the state and service search")
    void getRoleMasterTM_shouldReadTheSameCatalogue() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { 11, "TC Specialist", "Telemedicine", Boolean.FALSE, "admin", PSM_ID });
        when(roleScreenMappingRepo.getAllRoleByMapId(PSM_ID)).thenReturn(rows);

        assertEquals("TC Specialist", service.getRoleMasterTM(PSM_ID).get(0).getRoleName());
    }

    @Test
    @DisplayName("getProStateServRolesV1 should convert each stored role the query answers")
    void getProStateServRolesV1_shouldConvertEachRole() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { java.util.Map.of("roleID", 11, "roleName", "Counsellor") });
        when(mRoleRepo.getAllRoleByMapId1(PSM_ID)).thenReturn(rows);

        ArrayList<RoleMaster> roles = service.getProStateServRolesV1(PSM_ID);

        assertEquals(1, roles.size());
        assertEquals("Counsellor", roles.get(0).getRoleName());
    }

    @Test
    @DisplayName("getProStateServRoles1 should convert each stored role the query answers")
    void getProStateServRoles1_shouldConvertEachRole() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { java.util.Map.of("roleID", 11, "roleName", "Counsellor") });
        when(mRoleRepo.getAllRoleByMapId1(PSM_ID)).thenReturn(rows);

        assertEquals(1, service.getProStateServRoles1(PSM_ID).size());
    }

    @Test
    @DisplayName("getProStateServRoles1 should answer nothing when the query answers nothing")
    void getProStateServRoles1_shouldAnswerNothingForNullResult() {
        when(mRoleRepo.getAllRoleByMapId1(PSM_ID)).thenReturn(null);

        assertTrue(service.getProStateServRoles1(PSM_ID).isEmpty());
    }

    @Test
    @DisplayName("the role writes should each reach their own repository")
    void roleWrites_shouldReachTheirOwnRepository() {
        RoleMaster role = new RoleMaster();
        role.setRoleID(11);
        ArrayList<RoleMaster> roles = new ArrayList<>(List.of(role));
        when(mRoleRepo.saveAll(anyList())).thenReturn(roles);
        when(mRoleRepo.getRoleByRoleId(11)).thenReturn(role);
        when(mRoleRepo.save(role)).thenReturn(role);
        when(mRoleRepo.findByDeletedAndProviderServiceMapID(false, PSM_ID)).thenReturn(roles);

        assertEquals(1, service.addRole(new ArrayList<>()).size());
        assertSame(role, service.getRoleByRoleId(11));
        assertSame(role, service.modifydata(role));
        assertEquals("success", service.deletedata(role));
        assertSame(roles, service.getProStateServRolesActive(PSM_ID));
    }

    @Test
    @DisplayName("getAllFeature and getAllState should hand back what the repositories hold")
    void masters_shouldHandBackRepositoryContents() {
        ArrayList<M_Screen> screens = new ArrayList<>(List.of(new M_Screen()));
        ArrayList<StateMasterForRole> states = new ArrayList<>(List.of(new StateMasterForRole()));
        when(m_ScreenRepo.getAllFeature(3)).thenReturn(screens);
        when(stateMasterRepo.getAllState()).thenReturn(states);

        assertSame(screens, service.getAllFeature(3));
        assertSame(states, service.getAllState());
    }

    @Test
    @DisplayName("settingScreenId should report whether the screen mapping actually moved")
    void settingScreenId_shouldReportWhetherMappingMoved() {
        when(roleScreenMappingRepo.updatescreenId(31, 21)).thenReturn(1);
        when(roleScreenMappingRepo.updatescreenId(32, 21)).thenReturn(0);

        assertEquals("success", service.settingScreenId(31, 21));
        assertEquals("fail", service.settingScreenId(32, 21));
    }

    @Test
    @DisplayName("mapfeature should hand its batch to the screen mapping repository")
    void mapfeature_shouldHandBatchToRepository() {
        ArrayList<RoleScreenMapping> stored = new ArrayList<>(List.of(new RoleScreenMapping()));
        when(roleScreenMappingRepo.saveAll(anyList())).thenReturn(stored);

        assertEquals(1, service.mapfeature(new ArrayList<>()).size());
    }

    @Test
    @DisplayName("getServiceByServiceProviderIds should skip a row the query could not fill")
    void getServiceByServiceProviderIds_shouldSkipUnfillableRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new Object[] { "Tele Medicine", 3, Boolean.FALSE, PSM_ID });
        when(m_UserservicerolemappingForRoleProviderAdminRepo.getServiceByServiceProviderIds(3117))
                .thenReturn(rows);

        assertEquals(1, service.getServiceByServiceProviderIds(3117).size());
    }

    @Test
    @DisplayName("getStateByServiceProviderIdAndServiceLines should read the state mappings for a state service")
    void getStateByServiceLines_shouldReadStateMappings() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new Object[] { PSM_ID, "Karnataka", 29, 3 });
        when(m_UserservicerolemappingForRoleProviderAdminRepo
                .getStateByServiceProviderIdAndServiceLines(3117, 3)).thenReturn(rows);

        ArrayList<M_UserservicerolemappingForRoleProviderAdmin> mappings =
                service.getStateByServiceProviderIdAndServiceLines(3117, 3, Boolean.FALSE);

        assertEquals(1, mappings.size());
    }

    @Test
    @DisplayName("getStateByServiceProviderIdAndServiceLines should list every state for a national service")
    void getStateByServiceLines_shouldListEveryStateForNationalService() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { PSM_ID });
        StateMasterForRole karnataka = new StateMasterForRole();
        karnataka.setStateID(29);
        karnataka.setStateName("Karnataka");
        StateMasterForRole kerala = new StateMasterForRole();
        kerala.setStateID(32);
        kerala.setStateName("Kerala");
        when(m_UserservicerolemappingForRoleProviderAdminRepo
                .getStateByServiceProviderIdAndServiceLines1(3117, 3)).thenReturn(rows);
        when(stateMasterRepo.getAllState()).thenReturn(new ArrayList<>(List.of(karnataka, kerala)));

        ArrayList<M_UserservicerolemappingForRoleProviderAdmin> mappings =
                service.getStateByServiceProviderIdAndServiceLines(3117, 3, Boolean.TRUE);

        assertEquals(2, mappings.size(), "a national service line reaches every state on record");
    }

    @Test
    @DisplayName("configWrapUpTime should carry the new wrap-up settings onto the stored role")
    void configWrapUpTime_shouldCarryNewSettings() throws Exception {
        RoleMaster stored = new RoleMaster();
        stored.setRoleID(11);
        RoleMaster request = new RoleMaster();
        request.setRoleID(11);
        request.setIsWrapUpTime(Boolean.TRUE);
        request.setWrapUpTime(30);
        request.setModifiedBy("admin");
        when(mRoleRepo.findByRoleID(11)).thenReturn(stored);
        when(mRoleRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.configWrapUpTime(request));
        assertEquals(30, stored.getWrapUpTime());
        assertTrue(stored.getIsWrapUpTime());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("configWrapUpTime should refuse a role that does not exist")
    void configWrapUpTime_shouldRefuseUnknownRole() {
        RoleMaster request = new RoleMaster();
        request.setRoleID(11);
        when(mRoleRepo.findByRoleID(11)).thenReturn(null);

        Exception thrown = assertThrows(Exception.class, () -> service.configWrapUpTime(request));
        assertEquals("Invalid Role", thrown.getMessage());
    }

    @Test
    @DisplayName("configWrapUpTime should refuse a change that names nobody as its author")
    void configWrapUpTime_shouldRefuseUnattributedChange() {
        RoleMaster request = new RoleMaster();
        request.setRoleID(11);
        when(mRoleRepo.findByRoleID(11)).thenReturn(new RoleMaster());

        Exception thrown = assertThrows(Exception.class, () -> service.configWrapUpTime(request));
        assertEquals("Please provide Modified by", thrown.getMessage());
    }
}
