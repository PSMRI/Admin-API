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
package com.iemr.admin.service.store;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.store.FacilityVillageMapping;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.data.store.M_facilityMap;
import com.iemr.admin.data.store.V_FetchFacility;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.repository.facilitytype.M_facilitytypeRepo;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceRepository;
import com.iemr.admin.repository.store.FacilityVillageMappingRepo;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.repository.store.V_FetchFacilityRepo;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.service.employeemaster.AshaSupervisorMappingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The store service maintains the facility hierarchy - which health facility
 * sits under which, and which villages each one serves - so its rules decide
 * whether a facility can be retired without orphaning what hangs off it.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StoreServiceImpl Test Suite")
class StoreServiceImplTest {

    private static final Integer FACILITY_ID = 501;
    private static final Integer PSM_ID = 4001;
    private static final Integer BLOCK_ID = 401;

    @Mock
    private MainStoreRepo mainStoreRepo;

    @Mock
    private ParkingPlaceRepository parkingPlaceRepository;

    @Mock
    private VanMasterRepository vanMasterRepository;

    @Mock
    private V_FetchFacilityRepo fetchFacilityRepo;

    @Mock
    private FacilityVillageMappingRepo facilityVillageMappingRepo;

    @Mock
    private AshaSupervisorMappingService ashaSupervisorMappingService;

    @Mock
    private M_facilitytypeRepo facilityTypeRepo;

    @InjectMocks
    private StoreServiceImpl service;

    private static M_Facility facility(Integer id, String name) {
        M_Facility facility = new M_Facility();
        facility.setFacilityID(id);
        facility.setFacilityName(name);
        facility.setBlockID(BLOCK_ID);
        facility.setProviderServiceMapID(PSM_ID);
        return facility;
    }

    private static M_facilitytype facilityType(Integer id, Integer levelValue) {
        M_facilitytype type = new M_facilitytype();
        type.setFacilityTypeID(id);
        type.setLevelValue(levelValue);
        return type;
    }

    @Nested
    @DisplayName("Reads")
    class ReadTests {

        @Test
        @DisplayName("the facility lookups should each reach their own repository query")
        void facilityLookups_shouldReachTheirOwnQuery() {
            M_Facility stored = facility(FACILITY_ID, "PHC North");
            ArrayList<M_Facility> facilities = new ArrayList<>(List.of(stored));
            List<M_Facility> asList = List.of(stored);
            when(mainStoreRepo.save(stored)).thenReturn(stored);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(stored);
            when(mainStoreRepo.findByProviderServiceMapIDOrNullOrderByFacilityName(PSM_ID)).thenReturn(asList);
            when(mainStoreRepo.saveAll(anyList())).thenReturn(asList);
            when(mainStoreRepo.getAllMainFacility(PSM_ID, true)).thenReturn(facilities);
            when(mainStoreRepo.getAllMainFacility(PSM_ID, true, FACILITY_ID)).thenReturn(facilities);
            when(mainStoreRepo.getChildFacility(PSM_ID, FACILITY_ID)).thenReturn(facilities);
            when(mainStoreRepo.findByBlockIDAndDeletedFalseOrderByFacilityName(BLOCK_ID)).thenReturn(facilities);
            when(mainStoreRepo.findByBlockIDOrderByFacilityName(BLOCK_ID)).thenReturn(facilities);
            when(mainStoreRepo.findByParentFacilityIDAndDeletedFalseOrderByFacilityName(FACILITY_ID))
                    .thenReturn(facilities);

            assertSame(stored, service.createMainStore(stored));
            assertSame(stored, service.getMainStore(FACILITY_ID));
            assertSame(asList, service.getAllMainStore(PSM_ID));
            assertSame(asList, service.addAllMainStore(new ArrayList<>()));
            assertSame(facilities, service.getMainFacility(PSM_ID, true));
            assertSame(facilities, service.getMainFacility(PSM_ID, true, FACILITY_ID));
            assertSame(facilities, service.getChildFacility(PSM_ID, FACILITY_ID));
            assertSame(facilities, service.getFacilitiesByBlock(BLOCK_ID));
            assertSame(facilities, service.getAllFacilitiesByBlock(BLOCK_ID));
            assertSame(facilities, service.getChildFacilitiesByParent(FACILITY_ID));
        }

        @Test
        @DisplayName("getFacilitiesByBlockAndLevel should ignore the rural-urban split when none is asked for")
        void getFacilitiesByBlockAndLevel_shouldIgnoreSplitWhenNotAsked() {
            when(mainStoreRepo.findByBlockIDAndLevelValue(BLOCK_ID, 4)).thenReturn(new ArrayList<>());

            service.getFacilitiesByBlockAndLevel(BLOCK_ID, 4, null);
            service.getFacilitiesByBlockAndLevel(BLOCK_ID, 4, "");

            verify(mainStoreRepo, org.mockito.Mockito.times(2)).findByBlockIDAndLevelValue(BLOCK_ID, 4);
        }

        @Test
        @DisplayName("getFacilitiesByBlockAndLevel should narrow by the rural-urban split when asked for")
        void getFacilitiesByBlockAndLevel_shouldNarrowBySplit() {
            when(mainStoreRepo.findByBlockIDAndFacilityLevel(BLOCK_ID, 4, "Rural")).thenReturn(new ArrayList<>());

            service.getFacilitiesByBlockAndLevel(BLOCK_ID, 4, "Rural");

            verify(mainStoreRepo).findByBlockIDAndFacilityLevel(BLOCK_ID, 4, "Rural");
        }

        @Test
        @DisplayName("getMapStore should answer the mapped facilities of the provider")
        void getMapStore_shouldAnswerMappedFacilities() {
            V_FetchFacility request = new V_FetchFacility();
            request.setProviderServiceMapID(PSM_ID);
            List<V_FetchFacility> stored = List.of(request);
            when(fetchFacilityRepo.findByProviderServiceMapID(PSM_ID)).thenReturn(stored);

            assertSame(stored, service.getMapStore(request));
        }

        @Test
        @DisplayName("checkStoreCode should report a facility code the provider already uses")
        void checkStoreCode_shouldReportUsedCode() {
            M_Facility request = facility(null, "PHC North");
            request.setFacilityCode("PHC-1");
            when(mainStoreRepo.findByFacilityCodeAndProviderServiceMapID("PHC-1", PSM_ID))
                    .thenReturn(List.of(facility(FACILITY_ID, "PHC North")));

            assertTrue(service.checkStoreCode(request));
        }

        @Test
        @DisplayName("checkStoreCode should clear a facility code nobody uses yet")
        void checkStoreCode_shouldClearFreeCode() {
            M_Facility request = facility(null, "PHC North");
            request.setFacilityCode("PHC-2");
            when(mainStoreRepo.findByFacilityCodeAndProviderServiceMapID("PHC-2", PSM_ID))
                    .thenReturn(new ArrayList<>());

            assertFalse(service.checkStoreCode(request));
        }

        @Test
        @DisplayName("getMappedVillageIDs should hand back what the repository holds")
        void getMappedVillageIDs_shouldHandBackRepositoryContents() {
            List<Integer> stored = List.of(601, 602);
            when(facilityVillageMappingRepo.findMappedVillageIDsByBlockID(BLOCK_ID)).thenReturn(stored);

            assertSame(stored, service.getMappedVillageIDs(BLOCK_ID));
        }

        @Test
        @DisplayName("getVillageMappingsByFacility should answer the villages a live facility serves")
        void getVillageMappingsByFacility_shouldAnswerServedVillages() {
            ArrayList<FacilityVillageMapping> stored = new ArrayList<>(List.of(new FacilityVillageMapping()));
            when(mainStoreRepo.findByFacilityIDAndDeleted(FACILITY_ID, false))
                    .thenReturn(facility(FACILITY_ID, "PHC North"));
            when(facilityVillageMappingRepo.findByFacilityIDAndDeletedFalse(FACILITY_ID)).thenReturn(stored);

            assertSame(stored, service.getVillageMappingsByFacility(FACILITY_ID));
        }

        @Test
        @DisplayName("getVillageMappingsByFacility should answer nothing for a facility that is retired")
        void getVillageMappingsByFacility_shouldAnswerNothingForRetiredFacility() {
            when(mainStoreRepo.findByFacilityIDAndDeleted(FACILITY_ID, false)).thenReturn(null);

            assertTrue(service.getVillageMappingsByFacility(FACILITY_ID).isEmpty());
            verify(facilityVillageMappingRepo, never()).findByFacilityIDAndDeletedFalse(anyInt());
        }
    }

    @Nested
    @DisplayName("deleteStore")
    class DeleteStoreTests {

        @Test
        @DisplayName("should retire a facility that nothing else hangs off")
        void deleteStore_shouldRetireUnusedFacility() throws Exception {
            M_Facility stored = facility(FACILITY_ID, "PHC North");
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setDeleted(Boolean.TRUE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(stored);
            when(mainStoreRepo.findByMainFacilityIDAndDeletedOrderByFacilityName(FACILITY_ID, false))
                    .thenReturn(new ArrayList<>());
            when(parkingPlaceRepository.findByFacilityIDAndDeleted(FACILITY_ID, false)).thenReturn(new ArrayList<>());
            when(vanMasterRepository.findByFacilityIDAndDeleted(FACILITY_ID, false)).thenReturn(new ArrayList<>());
            when(mainStoreRepo.save(stored)).thenReturn(stored);

            assertTrue(service.deleteStore(request).getDeleted());
        }

        @Test
        @DisplayName("should refuse to retire a facility that still has live children")
        void deleteStore_shouldRefuseWhenChildrenAreLive() {
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setDeleted(Boolean.TRUE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(facility(FACILITY_ID, "PHC North"));
            when(mainStoreRepo.findByMainFacilityIDAndDeletedOrderByFacilityName(FACILITY_ID, false))
                    .thenReturn(new ArrayList<>(List.of(facility(502, "Sub Centre"))));

            Exception thrown = assertThrows(Exception.class, () -> service.deleteStore(request));
            assertEquals("Child Stores are still active", thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse to retire a facility a parking place still points at")
        void deleteStore_shouldRefuseWhenMappedToParkingPlace() {
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setDeleted(Boolean.TRUE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(facility(FACILITY_ID, "PHC North"));
            when(mainStoreRepo.findByMainFacilityIDAndDeletedOrderByFacilityName(FACILITY_ID, false))
                    .thenReturn(new ArrayList<>());
            when(parkingPlaceRepository.findByFacilityIDAndDeleted(FACILITY_ID, false))
                    .thenReturn(List.of(new M_Parkingplace()));

            Exception thrown = assertThrows(Exception.class, () -> service.deleteStore(request));
            assertEquals("Store mapped to parking place", thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse to retire a facility a van still points at")
        void deleteStore_shouldRefuseWhenMappedToVan() {
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setDeleted(Boolean.TRUE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(facility(FACILITY_ID, "PHC North"));
            when(mainStoreRepo.findByMainFacilityIDAndDeletedOrderByFacilityName(FACILITY_ID, false))
                    .thenReturn(new ArrayList<>());
            when(parkingPlaceRepository.findByFacilityIDAndDeleted(FACILITY_ID, false)).thenReturn(new ArrayList<>());
            when(vanMasterRepository.findByFacilityIDAndDeleted(FACILITY_ID, false))
                    .thenReturn(List.of(new M_Van()));

            Exception thrown = assertThrows(Exception.class, () -> service.deleteStore(request));
            assertEquals("Store mapped to van", thrown.getMessage());
        }

        @Test
        @DisplayName("should reinstate a top-level facility without further checks")
        void deleteStore_shouldReinstateTopLevelFacility() throws Exception {
            M_Facility stored = facility(FACILITY_ID, "PHC North");
            stored.setDeleted(Boolean.TRUE);
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setDeleted(Boolean.FALSE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(stored);
            when(mainStoreRepo.save(stored)).thenReturn(stored);

            assertFalse(service.deleteStore(request).getDeleted());
        }

        @Test
        @DisplayName("should reinstate a child facility once its parent is live again")
        void deleteStore_shouldReinstateChildUnderLiveParent() throws Exception {
            M_Facility stored = facility(FACILITY_ID, "Sub Centre");
            stored.setMainFacilityID(500);
            M_Facility request = facility(FACILITY_ID, "Sub Centre");
            request.setDeleted(Boolean.FALSE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(stored);
            when(mainStoreRepo.findByFacilityIDAndDeleted(500, false)).thenReturn(facility(500, "PHC North"));
            when(mainStoreRepo.save(stored)).thenReturn(stored);

            assertFalse(service.deleteStore(request).getDeleted());
        }

        @Test
        @DisplayName("should refuse to reinstate a child whose parent is still retired")
        void deleteStore_shouldRefuseReinstatingUnderRetiredParent() {
            M_Facility stored = facility(FACILITY_ID, "Sub Centre");
            stored.setMainFacilityID(500);
            M_Facility request = facility(FACILITY_ID, "Sub Centre");
            request.setDeleted(Boolean.FALSE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(stored);
            when(mainStoreRepo.findByFacilityIDAndDeleted(500, false)).thenReturn(null);

            Exception thrown = assertThrows(Exception.class, () -> service.deleteStore(request));
            assertEquals("Parent Stores are still inactive", thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse a facility that is not on record")
        void deleteStore_shouldRefuseUnknownFacility() {
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setDeleted(Boolean.TRUE);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(null);

            Exception thrown = assertThrows(Exception.class, () -> service.deleteStore(request));
            assertEquals("No store available", thrown.getMessage());
        }
    }

    @Nested
    @DisplayName("mapStore and deleteMapStore")
    class MappingTests {

        private M_facilityMap mapping(boolean isMain) {
            M_facilityMap mapping = new M_facilityMap();
            mapping.setFacilityID(FACILITY_ID);
            mapping.setIsMainFacility(isMain);
            mapping.setCreatedBy("admin");
            return mapping;
        }

        @Test
        @DisplayName("mapStore should free the previous parking place before claiming the new one")
        void mapStore_shouldFreePreviousParkingPlace() {
            M_facilityMap request = mapping(true);
            request.setParkingPlaceID(701);
            request.setOldParkingPlaceID(700);
            when(parkingPlaceRepository.updatePPMap(anyInt(), any(), anyString(), any())).thenReturn(1);

            assertEquals(1, service.mapStore(List.of(request)));
            verify(parkingPlaceRepository).updatePPMap(700, null, "admin", null);
            verify(parkingPlaceRepository).updatePPMap(701, FACILITY_ID, "admin", true);
        }

        @Test
        @DisplayName("mapStore should free the previous van before claiming the new one")
        void mapStore_shouldFreePreviousVan() {
            M_facilityMap request = mapping(false);
            request.setVanID(801);
            request.setOldVanID(800);
            when(vanMasterRepository.updateVanMap(anyInt(), any(), anyString(), any())).thenReturn(1);

            assertEquals(1, service.mapStore(List.of(request)));
            verify(vanMasterRepository).updateVanMap(800, null, "admin", null);
            verify(vanMasterRepository).updateVanMap(801, FACILITY_ID, "admin", true);
        }

        @Test
        @DisplayName("mapStore should leave a main facility alone when no parking place is named")
        void mapStore_shouldLeaveMainFacilityAloneWithoutParkingPlace() {
            assertEquals(0, service.mapStore(List.of(mapping(true))));
            verify(parkingPlaceRepository, never()).updatePPMap(anyInt(), any(), anyString(), any());
        }

        @Test
        @DisplayName("deleteMapStore should free the parking place once no van hangs off it")
        void deleteMapStore_shouldFreeParkingPlace() throws Exception {
            M_facilityMap request = mapping(true);
            request.setParkingPlaceID(701);
            when(vanMasterRepository.findByParkingPlaceIDAndFacilityIDIsNotNull(701)).thenReturn(new ArrayList<>());
            when(parkingPlaceRepository.updatePPMap(701, null, "admin", null)).thenReturn(1);

            assertEquals(1, service.deleteMapStore(request));
        }

        @Test
        @DisplayName("deleteMapStore should refuse a parking place that still has a mapped van")
        void deleteMapStore_shouldRefuseParkingPlaceWithMappedVan() {
            M_facilityMap request = mapping(true);
            request.setParkingPlaceID(701);
            when(vanMasterRepository.findByParkingPlaceIDAndFacilityIDIsNotNull(701))
                    .thenReturn(List.of(new M_Van()));

            Exception thrown = assertThrows(Exception.class, () -> service.deleteMapStore(request));
            assertEquals("Please Unmap van under this Parking Place", thrown.getMessage());
        }

        @Test
        @DisplayName("deleteMapStore should free the van when no parking place is named")
        void deleteMapStore_shouldFreeVan() throws Exception {
            M_facilityMap request = mapping(false);
            request.setVanID(801);
            when(vanMasterRepository.updateVanMap(801, null, "admin", null)).thenReturn(1);

            assertEquals(1, service.deleteMapStore(request));
        }
    }

    @Nested
    @DisplayName("createFacilityWithHierarchy")
    class CreateHierarchyTests {

        @Test
        @DisplayName("should refuse a facility whose name is already taken in the block")
        void create_shouldRefuseDuplicateNameInBlock() {
            when(mainStoreRepo.existsByFacilityNameAndBlockIDAndDeletedFalse("PHC North", BLOCK_ID))
                    .thenReturn(true);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.createFacilityWithHierarchy(facility(null, "PHC North"), null, null, null));
            assertEquals("Facility with this name already exists in this block", thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse a child that does not sit one level below the new facility")
        void create_shouldRefuseChildAtWrongLevel() {
            M_Facility request = facility(null, "PHC North");
            request.setFacilityTypeID(3);
            M_Facility child = facility(502, "Sub Centre");
            child.setFacilityTypeID(9);
            when(facilityTypeRepo.findByFacilityTypeID(3)).thenReturn(facilityType(3, 2));
            when(facilityTypeRepo.findByFacilityTypeID(9)).thenReturn(facilityType(9, 5));
            when(mainStoreRepo.findByFacilityIDAndDeleted(502, false)).thenReturn(child);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.createFacilityWithHierarchy(request, null, null, List.of(502)));
            assertTrue(thrown.getMessage().contains("Hierarchy level mismatch"), thrown.getMessage());
        }

        @Test
        @DisplayName("should attach the villages the new facility serves")
        void create_shouldAttachServedVillages() {
            M_Facility request = facility(null, "PHC North");
            request.setCreatedBy("admin");
            M_Facility saved = facility(FACILITY_ID, "PHC North");
            when(mainStoreRepo.save(request)).thenReturn(saved);
            when(facilityVillageMappingRepo
                    .findByFacilityIDAndDistrictBranchIDAndDeletedTrue(FACILITY_ID, 601)).thenReturn(null);

            service.createFacilityWithHierarchy(request, List.of(601), 601, null);

            verify(facilityVillageMappingRepo).save(any(FacilityVillageMapping.class));
            assertEquals(601, request.getMainVillageID());
        }

        @Test
        @DisplayName("should reinstate a village mapping that was previously retired")
        void create_shouldReinstateRetiredVillageMapping() {
            M_Facility request = facility(null, "PHC North");
            request.setCreatedBy("admin");
            M_Facility saved = facility(FACILITY_ID, "PHC North");
            FacilityVillageMapping retired = new FacilityVillageMapping();
            retired.setDeleted(Boolean.TRUE);
            when(mainStoreRepo.save(request)).thenReturn(saved);
            when(facilityVillageMappingRepo
                    .findByFacilityIDAndDistrictBranchIDAndDeletedTrue(FACILITY_ID, 601)).thenReturn(retired);

            service.createFacilityWithHierarchy(request, List.of(601), 601, null);

            assertFalse(retired.getDeleted());
            assertEquals("admin", retired.getModifiedBy());
        }

        @Test
        @DisplayName("should re-parent the children the new facility takes over")
        void create_shouldReparentChildren() {
            M_Facility request = facility(null, "PHC North");
            request.setCreatedBy("admin");
            M_Facility saved = facility(FACILITY_ID, "PHC North");
            M_Facility child = facility(502, "Sub Centre");
            child.setProviderServiceMapID(null);
            child.setIsMainFacility(Boolean.TRUE);
            when(mainStoreRepo.save(request)).thenReturn(saved);
            when(mainStoreRepo.findByFacilityID(502)).thenReturn(child);

            service.createFacilityWithHierarchy(request, null, null, List.of(502));

            assertEquals(FACILITY_ID, child.getParentFacilityID());
            verify(mainStoreRepo).updateStoreFields(502, false, FACILITY_ID, "SUB");
        }
    }

    @Nested
    @DisplayName("deleteFacilityWithHierarchy")
    class DeleteHierarchyTests {

        @Test
        @DisplayName("should release the children before retiring the facility")
        void delete_shouldReleaseChildrenFirst() throws Exception {
            M_Facility stored = facility(FACILITY_ID, "PHC North");
            M_Facility child = facility(502, "Sub Centre");
            child.setParentFacilityID(FACILITY_ID);
            child.setProviderServiceMapID(null);
            FacilityVillageMapping villageMapping = new FacilityVillageMapping();
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(stored);
            when(mainStoreRepo.findByParentFacilityIDAndDeletedFalseOrderByFacilityName(FACILITY_ID))
                    .thenReturn(new ArrayList<>(List.of(child)));
            when(mainStoreRepo.save(stored)).thenReturn(stored);
            when(facilityVillageMappingRepo.findByFacilityIDAndDeletedFalse(FACILITY_ID))
                    .thenReturn(new ArrayList<>(List.of(villageMapping)));

            M_Facility retired = service.deleteFacilityWithHierarchy(FACILITY_ID, "admin");

            assertTrue(retired.getDeleted());
            assertNull(child.getParentFacilityID(), "a released child must not point at a retired parent");
            verify(mainStoreRepo).updateStoreFields(502, true, null, "MAIN");
            verify(ashaSupervisorMappingService).cascadeDeleteByFacilityID(FACILITY_ID, "admin");
            assertTrue(villageMapping.getDeleted(), "the villages it served must be released too");
        }

        @Test
        @DisplayName("should refuse a facility that is not on record")
        void delete_shouldRefuseUnknownFacility() {
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(null);

            Exception thrown = assertThrows(Exception.class,
                    () -> service.deleteFacilityWithHierarchy(FACILITY_ID, "admin"));
            assertEquals("Facility not found", thrown.getMessage());
        }
    }

    @Nested
    @DisplayName("updateFacilityWithHierarchy")
    class UpdateHierarchyTests {

        @Test
        @DisplayName("should refuse a facility that is not on record")
        void update_shouldRefuseUnknownFacility() {
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(null);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.updateFacilityWithHierarchy(facility(FACILITY_ID, "PHC North"), null, null, null));
            assertEquals("Facility not found", thrown.getMessage());
        }

        @Test
        @DisplayName("should refuse a rename onto a name another facility in the block already uses")
        void update_shouldRefuseDuplicateNameInBlock() {
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(facility(FACILITY_ID, "old name"));
            when(mainStoreRepo.existsByFacilityNameAndBlockIDAndNotFacilityID("PHC North", BLOCK_ID, FACILITY_ID))
                    .thenReturn(true);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.updateFacilityWithHierarchy(facility(FACILITY_ID, "PHC North"), null, null, null));
            assertEquals("Facility with this name already exists in this block", thrown.getMessage());
        }

        @Test
        @DisplayName("should copy only the fields the request actually sets")
        void update_shouldCopyOnlySuppliedFields() {
            M_Facility existing = facility(FACILITY_ID, "old name");
            existing.setRuralUrban("Rural");
            M_Facility request = new M_Facility();
            request.setFacilityID(FACILITY_ID);
            request.setFacilityName("PHC North");
            request.setModifiedBy("admin");
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(existing);
            when(mainStoreRepo.save(existing)).thenReturn(existing);

            service.updateFacilityWithHierarchy(request, null, 601, null);

            assertEquals("PHC North", existing.getFacilityName());
            assertEquals("Rural", existing.getRuralUrban(), "an unset field keeps the value on record");
            assertEquals(601, existing.getMainVillageID());
        }

        @Test
        @DisplayName("should release the villages the facility no longer serves")
        void update_shouldReleaseDroppedVillages() {
            M_Facility existing = facility(FACILITY_ID, "PHC North");
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setModifiedBy("admin");
            FacilityVillageMapping dropped = new FacilityVillageMapping();
            dropped.setDistrictBranchID(602);
            FacilityVillageMapping kept = new FacilityVillageMapping();
            kept.setDistrictBranchID(601);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(existing);
            when(mainStoreRepo.save(existing)).thenReturn(existing);
            when(facilityVillageMappingRepo.findByFacilityIDAndDeletedFalse(FACILITY_ID))
                    .thenReturn(new ArrayList<>(List.of(dropped, kept)));

            service.updateFacilityWithHierarchy(request, List.of(601), 601, null);

            assertTrue(dropped.getDeleted(), "a village dropped from the list must be released");
            assertFalse(Boolean.TRUE.equals(kept.getDeleted()));
        }

        @Test
        @DisplayName("should refuse a child that does not sit one level below the facility")
        void update_shouldRefuseChildAtWrongLevel() {
            M_Facility existing = facility(FACILITY_ID, "PHC North");
            existing.setFacilityTypeID(3);
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setModifiedBy("admin");
            M_Facility child = facility(502, "Sub Centre");
            child.setFacilityTypeID(9);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(existing);
            when(mainStoreRepo.save(existing)).thenReturn(existing);
            when(facilityTypeRepo.findByFacilityTypeID(3)).thenReturn(facilityType(3, 2));
            when(facilityTypeRepo.findByFacilityTypeID(9)).thenReturn(facilityType(9, 5));
            when(mainStoreRepo.findByFacilityIDAndDeleted(502, false)).thenReturn(child);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.updateFacilityWithHierarchy(request, null, null, List.of(502)));
            assertTrue(thrown.getMessage().contains("Hierarchy level mismatch"), thrown.getMessage());
        }

        @Test
        @DisplayName("should promote a child that is no longer part of the hierarchy")
        void update_shouldPromoteReleasedChild() {
            M_Facility existing = facility(FACILITY_ID, "PHC North");
            M_Facility request = facility(FACILITY_ID, "PHC North");
            request.setModifiedBy("admin");
            M_Facility released = facility(503, "Sub Centre B");
            released.setProviderServiceMapID(null);
            when(mainStoreRepo.findByFacilityID(FACILITY_ID)).thenReturn(existing);
            when(mainStoreRepo.save(existing)).thenReturn(existing);
            when(mainStoreRepo.findByParentFacilityIDAndDeletedFalseOrderByFacilityName(FACILITY_ID))
                    .thenReturn(new ArrayList<>(List.of(released)));

            service.updateFacilityWithHierarchy(request, null, null, new ArrayList<>());

            verify(mainStoreRepo).clearParentFacilityID(FACILITY_ID, "admin");
            verify(mainStoreRepo).updateStoreFields(503, true, null, "MAIN");
        }
    }
}
