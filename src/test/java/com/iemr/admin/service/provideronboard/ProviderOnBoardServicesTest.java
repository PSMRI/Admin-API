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
package com.iemr.admin.service.provideronboard;

import java.util.ArrayList;
import java.util.List;
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

import com.google.gson.JsonObject;
import com.iemr.admin.data.provideronboard.M_104druggroup;
import com.iemr.admin.data.provideronboard.M_104drugmapping;
import com.iemr.admin.data.provideronboard.M_104drugmaster;
import com.iemr.admin.data.provideronboard.M_Calltype;
import com.iemr.admin.data.provideronboard.M_Category;
import com.iemr.admin.data.provideronboard.M_Feedbacknature;
import com.iemr.admin.data.provideronboard.M_Feedbacktype;
import com.iemr.admin.data.provideronboard.M_Institutedirectory;
import com.iemr.admin.data.provideronboard.M_Institutedirectorymapping;
import com.iemr.admin.data.provideronboard.M_Institutesubdirectory;
import com.iemr.admin.data.provideronboard.M_Institution;
import com.iemr.admin.data.provideronboard.M_Institutiontype;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.provideronboard.M_ServiceMaster;
import com.iemr.admin.data.provideronboard.M_Severity;
import com.iemr.admin.data.provideronboard.M_Subcategory;
import com.iemr.admin.data.provideronboard.M_Subservice;
import com.iemr.admin.data.provideronboard.M_SubservicemasterPA;
import com.iemr.admin.data.provideronboard.M_UserservicerolemappingForRole;
import com.iemr.admin.data.provideronboard.ServiceProvider_Model;
import com.iemr.admin.data.provideronboard.V_Showprovideradmin;
import com.iemr.admin.data.provideronboard.V_Showsubcategory;
import com.iemr.admin.exceptionhandler.DataNotFound;
import com.iemr.admin.repository.provideronboard.CalltypeRepo;
import com.iemr.admin.repository.provideronboard.CategoryRepo;
import com.iemr.admin.repository.provideronboard.DrugGroupRepo;
import com.iemr.admin.repository.provideronboard.DrugMappingRepo;
import com.iemr.admin.repository.provideronboard.DrugMasterRepo;
import com.iemr.admin.repository.provideronboard.IemrServiceRepository1;
import com.iemr.admin.repository.provideronboard.InstuteDirectoryRepo;
import com.iemr.admin.repository.provideronboard.M_FeedbacknatureRepo;
import com.iemr.admin.repository.provideronboard.M_FeedbacktypeRepo;
import com.iemr.admin.repository.provideronboard.M_InstitutedirectorymappingRepo;
import com.iemr.admin.repository.provideronboard.M_InstitutesubdirectoryRepo;
import com.iemr.admin.repository.provideronboard.M_InstitutionRepo;
import com.iemr.admin.repository.provideronboard.M_InstitutiontypeRepo;
import com.iemr.admin.repository.provideronboard.M_ProviderServiceMappingRepo;
import com.iemr.admin.repository.provideronboard.M_ServiceMasterRepo;
import com.iemr.admin.repository.provideronboard.M_SeverityRepo;
import com.iemr.admin.repository.provideronboard.M_SubservicemasterPArepo;
import com.iemr.admin.repository.provideronboard.M_UserservicerolemappingForRoleRepo;
import com.iemr.admin.repository.provideronboard.SubCategoryRepo;
import com.iemr.admin.repository.provideronboard.SubserviceMasterRepo;
import com.iemr.admin.repository.provideronboard.V_ShowprovideradminRepo;
import com.iemr.admin.repository.provideronboard.V_ShowsubcategoryRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The onboarding services are thin over their repositories, but a few of them
 * choose which query to run from what the caller left blank - and those choices
 * decide what an operator sees on screen.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Provider onboarding service Test Suite")
class ProviderOnBoardServicesTest {

    private static final Integer PSM_ID = 4001;

    @Mock
    private CalltypeRepo calltypeRepo;

    @InjectMocks
    private CalltypeServiceImpl calltypeService;

    @Mock
    private V_ShowsubcategoryRepo v_ShowsubcategoryRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private SubCategoryRepo subCategoryRepo;

    @InjectMocks
    private CategoryMasterImpl categoryService;

    @Mock
    private DrugGroupRepo drugGroupRepo;

    @Mock
    private DrugMasterRepo drugMasterRepo;

    @Mock
    private DrugMappingRepo drugMappingRepo;

    @InjectMocks
    private DrugMasterImpl drugService;

    @Mock
    private InstuteDirectoryRepo instuteDirectoryRepo;

    @InjectMocks
    private InstuteDirectoryServiceImpl directoryService;

    @Mock
    private M_FeedbacknatureRepo m_FeedbacknatureRepo;

    @InjectMocks
    private M_FeedbacknatureImpl feedbackNatureService;

    @Mock
    private M_FeedbacktypeRepo m_FeedbacktypeRepo;

    @InjectMocks
    private M_FeedbacktypeImpl feedbackTypeService;

    @Mock
    private M_InstitutedirectorymappingRepo m_InstitutedirectorymappingRepo;

    @InjectMocks
    private M_InstitutedirectorymappingImpl directoryMappingService;

    @Mock
    private M_InstitutesubdirectoryRepo m_InstitutesubdirectoryRepo;

    @InjectMocks
    private M_InstitutesubdirectoryImpl subDirectoryService;

    @Mock
    private M_InstitutionRepo m_InstitutionRepo;

    @InjectMocks
    private M_InstitutionImpl institutionService;

    @Mock
    private M_InstitutiontypeRepo m_InstitutiontypeRepo;

    @InjectMocks
    private M_InstitutiontypeImpl instituteTypeService;

    @Mock
    private M_ServiceMasterRepo mservicemasteRepo;

    @InjectMocks
    private M_ServiceMasterImpl serviceMasterService;

    @Mock
    private M_SeverityRepo m_ServerityRepo;

    @InjectMocks
    private M_SeverityImpl severityService;

    @Mock
    private M_SubservicemasterPArepo m_SubservicemasterPArepo;

    @Mock
    private SubserviceMasterRepo subserviceMasterRepo;

    @InjectMocks
    private SubserviceImpl subServiceService;

    @Mock
    private V_ShowprovideradminRepo v_ShowprovideradminRepo;

    @Mock
    private M_UserservicerolemappingForRoleRepo m_UserservicerolemappingForRoleRepo;

    @Mock
    private IemrServiceRepository1 iemrServiceRepository1;

    @Mock
    private M_ProviderServiceMappingRepo m_ProviderServiceMappingRepo;

    @InjectMocks
    private ServiceProvider_ServiceImpl providerService;

    @Nested
    @DisplayName("CalltypeServiceImpl")
    class CallTypeServiceTests {

        @Test
        @DisplayName("should hand each call to its own repository query")
        void callType_shouldReachTheirOwnQuery() {
            M_Calltype callType = new M_Calltype();
            ArrayList<M_Calltype> stored = new ArrayList<>(List.of(callType));
            when(calltypeRepo.saveAll(anyList())).thenReturn(stored);
            when(calltypeRepo.updateCallType(51)).thenReturn(callType);
            when(calltypeRepo.save(callType)).thenReturn(callType);
            when(calltypeRepo.getCalltypeData(PSM_ID)).thenReturn(stored);

            assertSame(stored, calltypeService.saveCallList(new ArrayList<>()));
            assertSame(stored, calltypeService.createCalltype(new ArrayList<>()));
            assertSame(callType, calltypeService.updateCallType(51));
            assertSame(callType, calltypeService.saveupdatedData(callType));
            assertSame(stored, calltypeService.getCalltypeData(PSM_ID));
        }
    }

    @Nested
    @DisplayName("CategoryMasterImpl")
    class CategoryServiceTests {

        @Test
        @DisplayName("getCategoryId should store the category and answer the id it was given")
        void getCategoryId_shouldStoreAndAnswerId() {
            M_Category category = new M_Category();
            M_Category stored = new M_Category();
            stored.setCategoryID(81);
            when(categoryRepo.save(category)).thenReturn(stored);

            assertEquals(81, categoryService.getCategoryId(category));
        }

        @Test
        @DisplayName("the sub-category calls should each reach their own repository query")
        void subCategoryCalls_shouldReachTheirOwnQuery() {
            M_Subcategory subCategory = new M_Subcategory();
            ArrayList<M_Subcategory> stored = new ArrayList<>(List.of(subCategory));
            ArrayList<V_Showsubcategory> views = new ArrayList<>(List.of(new V_Showsubcategory()));
            when(subCategoryRepo.saveAll(anyList())).thenReturn(stored);
            when(subCategoryRepo.getCategory()).thenReturn(stored);
            when(subCategoryRepo.getCategory(81)).thenReturn(stored);
            when(subCategoryRepo.getSubCategory(91)).thenReturn(subCategory);
            when(subCategoryRepo.save(subCategory)).thenReturn(subCategory);
            when(v_ShowsubcategoryRepo.getSubCategory1(91)).thenReturn(views);
            when(v_ShowsubcategoryRepo.getCategoryByMapIDAndSubServiceID(PSM_ID, 61)).thenReturn(views);

            assertSame(stored, categoryService.saveSubCatData(new ArrayList<>()));
            assertSame(stored, categoryService.createSubCategory(new ArrayList<>()));
            assertSame(stored, categoryService.getCategory());
            assertSame(stored, categoryService.getCategory(81));
            assertSame(subCategory, categoryService.getSubCategory(91));
            assertSame(subCategory, categoryService.updateSubCatData(subCategory));
            assertSame(views, categoryService.getSubCategory1(91));
            assertSame(views, categoryService.getCategoryByMapIDAndSubServiceID(PSM_ID, 61));
        }

        @Test
        @DisplayName("the category calls should each reach their own repository query")
        void categoryCalls_shouldReachTheirOwnQuery() {
            M_Category category = new M_Category();
            ArrayList<M_Category> stored = new ArrayList<>(List.of(category));
            when(categoryRepo.saveAll(anyList())).thenReturn(stored);
            when(categoryRepo.getAllCategory(61, PSM_ID)).thenReturn(stored);
            when(categoryRepo.getAllCategory1(PSM_ID)).thenReturn(stored);
            when(categoryRepo.getCatData(81)).thenReturn(category);
            when(categoryRepo.save(category)).thenReturn(category);
            when(categoryRepo.updateCategory(81, 21)).thenReturn(1);
            when(categoryRepo.findByProviderServiceMapIDAndFeedbackNatureIDOrderByCategoryNameAsc(PSM_ID, null))
                    .thenReturn(stored);

            assertSame(stored, categoryService.createcat(new ArrayList<>()));
            assertSame(stored, categoryService.getAllCategory(61, PSM_ID));
            assertSame(stored, categoryService.getAllCategory1(PSM_ID));
            assertSame(category, categoryService.getcatdatabycatId(81));
            assertSame(category, categoryService.deletedata(category));
            assertEquals(1, categoryService.updateCategory(81, 21));
            assertSame(stored, categoryService.getUpmappedCategory(PSM_ID));
        }

        @Test
        @DisplayName("getAllCategory should rebuild one category per row the query answers")
        void getAllCategory_shouldRebuildEachRow() {
            when(categoryRepo.getAllCategory(PSM_ID))
                    .thenReturn(List.<Object[]>of(new Object[] { 81, "Medical", 61, "Counselling", PSM_ID }));

            ArrayList<M_Category> categories = categoryService.getAllCategory(PSM_ID);

            assertEquals(1, categories.size());
            assertEquals("Medical", categories.get(0).getCategoryName());
        }

        @Test
        @DisplayName("getAllCategorywithFeedbackNatureID should rebuild one category per row the query answers")
        void getAllCategoryWithFeedbackNature_shouldRebuildEachRow() {
            when(categoryRepo.getAllCategorywithfeedbackNatureID(PSM_ID, 21))
                    .thenReturn(List.<Object[]>of(new Object[] { 81, "Medical", 61, "Counselling", PSM_ID }));

            assertEquals(1, categoryService.getAllCategorywithFeedbackNatureID(PSM_ID, 21).size());
        }
    }

    @Nested
    @DisplayName("DrugMasterImpl")
    class DrugServiceTests {

        @Test
        @DisplayName("getAllDrugData should read the live catalogue when the caller excludes retired drugs")
        void getAllDrugData_shouldReadLiveCatalogue() {
            when(drugMasterRepo.getValidDrugData("77"))
                    .thenReturn(List.<Object[]>of(new Object[] { 101, "Paracetamol", "Antipyretic", "OTC",
                            Boolean.FALSE, (short) 77 }));

            ArrayList<M_104drugmaster> drugs = drugService.getAllDrugData(101, (short) 77, Boolean.FALSE);

            assertEquals(1, drugs.size());
            assertEquals("Paracetamol", drugs.get(0).getDrugName());
            verify(drugMasterRepo, org.mockito.Mockito.never()).getAllDrugData(anyString(), anyString());
        }

        @Test
        @DisplayName("getAllDrugData should read the whole catalogue when retired drugs are wanted too")
        void getAllDrugData_shouldReadWholeCatalogue() {
            when(drugMasterRepo.getAllDrugData("101", "77")).thenReturn(new ArrayList<>());

            drugService.getAllDrugData(101, (short) 77, Boolean.TRUE);

            verify(drugMasterRepo).getAllDrugData("101", "77");
        }

        @Test
        @DisplayName("getAllDrugData should ask for the whole catalogue when the caller names nothing")
        void getAllDrugData_shouldAskForWholeCatalogueWithoutFilters() {
            when(drugMasterRepo.getAllDrugData("", "")).thenReturn(new ArrayList<>());

            drugService.getAllDrugData(null, null, null);

            verify(drugMasterRepo).getAllDrugData("", "");
        }

        @Test
        @DisplayName("getAllDrugGroups should read the live groups when the caller excludes retired ones")
        void getAllDrugGroups_shouldReadLiveGroups() {
            when(drugGroupRepo.getValidDrugGroups("77"))
                    .thenReturn(List.<Object[]>of(new Object[] { 201, "Analgesics", "Pain relief", Boolean.FALSE,
                            (short) 77 }));

            assertEquals(1, drugService.getAllDrugGroups(201, (short) 77, Boolean.FALSE).size());
        }

        @Test
        @DisplayName("getAllDrugGroups should read every group when retired ones are wanted too")
        void getAllDrugGroups_shouldReadEveryGroup() {
            when(drugGroupRepo.getAllDrugGroups("201", "77")).thenReturn(new ArrayList<>());

            drugService.getAllDrugGroups(201, (short) 77, Boolean.TRUE);

            verify(drugGroupRepo).getAllDrugGroups("201", "77");
        }

        @Test
        @DisplayName("getAllDrugGroupMappings should rebuild one mapping per row the query answers")
        void getAllDrugGroupMappings_shouldRebuildEachRow() {
            when(drugMappingRepo.getAllDrugGroupMappings("", 77, 3))
                    .thenReturn(List.<Object[]>of(new Object[] { 301, 101, "Paracetamol", 201, "Analgesics", "OTC",
                            Boolean.FALSE, 77, PSM_ID, "N", Boolean.FALSE }));

            assertEquals(1, drugService.getAllDrugGroupMappings(null, 77, 3).size());
        }

        @Test
        @DisplayName("the drug writes should each reach their own repository")
        void drugWrites_shouldReachTheirOwnRepository() {
            M_104druggroup group = new M_104druggroup();
            group.setDrugGroupID(201);
            M_104drugmaster drug = new M_104drugmaster();
            M_104drugmapping mapping = new M_104drugmapping();
            ArrayList<M_104druggroup> groups = new ArrayList<>(List.of(group));
            ArrayList<M_104drugmaster> drugs = new ArrayList<>(List.of(drug));
            ArrayList<M_104drugmapping> mappings = new ArrayList<>(List.of(mapping));
            when(drugGroupRepo.save(group)).thenReturn(group);
            when(drugGroupRepo.saveAll(anyList())).thenReturn(groups);
            when(drugGroupRepo.getDrugGroupById(201)).thenReturn(group);
            when(drugMasterRepo.saveAll(anyList())).thenReturn(drugs);
            when(drugMasterRepo.save(drug)).thenReturn(drug);
            when(drugMasterRepo.getDrugDataById(101)).thenReturn(drug);
            when(drugMappingRepo.saveAll(anyList())).thenReturn(mappings);
            when(drugMappingRepo.save(mapping)).thenReturn(mapping);
            when(drugMappingRepo.getDrugMappingById(301)).thenReturn(mapping);

            assertEquals(201, drugService.getDrugGrupId(group));
            assertSame(groups, drugService.saveDrugGroup(new ArrayList<>()));
            assertSame(group, drugService.getDrugGroupById(201));
            assertSame(group, drugService.saveUpdatedDrugGroup(group));
            assertSame(drugs, drugService.saveDrugData(new ArrayList<>()));
            assertSame(drug, drugService.getDrugDataById(101));
            assertSame(drug, drugService.saveUpdatedData(drug));
            assertSame(mappings, drugService.mapDrugWithGroup(new ArrayList<>()));
            assertSame(mapping, drugService.getDrugMappingsById(301));
            assertSame(mapping, drugService.saveUpdatedDrugMapping(mapping));
        }

        @Test
        @DisplayName("the status updates should each reach their own repository query")
        void statusUpdates_shouldReachTheirOwnQuery() {
            M_104druggroup group = new M_104druggroup();
            group.setDrugGroupID(201);
            group.setDeleted(Boolean.TRUE);
            group.setModifiedBy("admin");
            M_104drugmaster drug = new M_104drugmaster();
            drug.setDrugID(101);
            drug.setDeleted(Boolean.TRUE);
            drug.setModifiedBy("admin");
            M_104drugmapping mapping = new M_104drugmapping();
            mapping.setDrugMapID(301);
            mapping.setDeleted(Boolean.TRUE);
            mapping.setModifiedBy("admin");
            when(drugGroupRepo.updateStatus(201, Boolean.TRUE, "admin")).thenReturn(1);
            when(drugMasterRepo.updateStatus(101, Boolean.TRUE, "admin")).thenReturn(1);
            when(drugMappingRepo.updateStatus(301, Boolean.TRUE, "admin")).thenReturn(1);

            assertEquals(1, drugService.updateDrugGroupStatus(group));
            assertEquals(1, drugService.updateDrugStatus(drug));
            assertEquals(1, drugService.updateDrugMappingStatus(mapping));
        }
    }

    @Nested
    @DisplayName("Institute services")
    class InstituteServiceTests {

        @Test
        @DisplayName("the directory calls should each reach their own repository query")
        void directoryCalls_shouldReachTheirOwnQuery() {
            M_Institutedirectory directory = new M_Institutedirectory();
            ArrayList<M_Institutedirectory> stored = new ArrayList<>(List.of(directory));
            when(instuteDirectoryRepo.saveAll(anyList())).thenReturn(stored);
            when(instuteDirectoryRepo.getInstuteDirectory(PSM_ID)).thenReturn(stored);
            when(instuteDirectoryRepo.editInstuteDirectory(11)).thenReturn(directory);
            when(instuteDirectoryRepo.save(directory)).thenReturn(directory);

            assertSame(stored, directoryService.createInstuteDirectory(new ArrayList<>()));
            assertSame(stored, directoryService.getInstuteDirectory(PSM_ID));
            assertSame(directory, directoryService.editInstuteDirectory(11));
            assertSame(directory, directoryService.editdata(directory));
        }

        @Test
        @DisplayName("the sub-directory calls should each reach their own repository query")
        void subDirectoryCalls_shouldReachTheirOwnQuery() {
            M_Institutesubdirectory subDirectory = new M_Institutesubdirectory();
            ArrayList<M_Institutesubdirectory> stored = new ArrayList<>(List.of(subDirectory));
            when(m_InstitutesubdirectoryRepo.getInstutesubDirectory(11, PSM_ID)).thenReturn(stored);
            when(m_InstitutesubdirectoryRepo.saveAll(anyList())).thenReturn(stored);
            when(m_InstitutesubdirectoryRepo.editInstutesubDirectory(41)).thenReturn(subDirectory);
            when(m_InstitutesubdirectoryRepo.save(subDirectory)).thenReturn(subDirectory);

            assertSame(stored, subDirectoryService.getInstutesubDirectory(11, PSM_ID));
            assertSame(stored, subDirectoryService.CreateInstutesubDirectory(new ArrayList<>()));
            assertSame(subDirectory, subDirectoryService.editInstutesubDirectory(41));
            assertSame(subDirectory, subDirectoryService.saveEditedData(subDirectory));
        }

        @Test
        @DisplayName("getInstituteDirectoryData should skip a row the query could not fill")
        void getInstituteDirectoryData_shouldSkipUnfillableRow() {
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(null);
            rows.add(new Object[] { 51, 31, 11, 41, PSM_ID, Boolean.FALSE, "admin", "District Hospital",
                    "Hospitals", "Government" });
            when(m_InstitutedirectorymappingRepo.getMappingData(41)).thenReturn(rows);

            assertEquals(1, directoryMappingService.getInstituteDirectoryData(41).size());
        }

        @Test
        @DisplayName("the directory mapping writes should each reach their own repository query")
        void directoryMappingWrites_shouldReachTheirOwnQuery() {
            M_Institutedirectorymapping mapping = new M_Institutedirectorymapping();
            ArrayList<M_Institutedirectorymapping> stored = new ArrayList<>(List.of(mapping));
            when(m_InstitutedirectorymappingRepo.saveAll(anyList())).thenReturn(stored);
            when(m_InstitutedirectorymappingRepo.getdata(51)).thenReturn(mapping);
            when(m_InstitutedirectorymappingRepo.save(mapping)).thenReturn(mapping);

            assertSame(stored, directoryMappingService.createInstituteDirectoryData(new ArrayList<>()));
            assertSame(mapping, directoryMappingService.deleteInstituteDirectoryData(51));
            assertSame(mapping, directoryMappingService.setdeletedData(mapping));
        }

        @Test
        @DisplayName("getInstution should narrow by block only when a block is named")
        void getInstution_shouldNarrowByBlockOnlyWhenNamed() {
            ArrayList<M_Institution> stored = new ArrayList<>();
            when(m_InstitutionRepo.getInstution(PSM_ID, 29, 301)).thenReturn(stored);
            when(m_InstitutionRepo.getInstution(PSM_ID, 29, 301, 401)).thenReturn(stored);

            institutionService.getInstution(PSM_ID, 29, 301, null);
            institutionService.getInstution(PSM_ID, 29, 301, 401);

            verify(m_InstitutionRepo).getInstution(PSM_ID, 29, 301);
            verify(m_InstitutionRepo).getInstution(PSM_ID, 29, 301, 401);
        }

        @Test
        @DisplayName("getInstutionByVillage should pick the query that matches what the caller named")
        void getInstutionByVillage_shouldPickMatchingQuery() {
            ArrayList<M_Institution> stored = new ArrayList<>();
            when(m_InstitutionRepo.getInstution(PSM_ID, 29, 301)).thenReturn(stored);
            when(m_InstitutionRepo.getInstutionByBlock(PSM_ID, 29, 301, 401)).thenReturn(stored);
            when(m_InstitutionRepo.getInstutionByVillage(PSM_ID, 29, 301, 501)).thenReturn(stored);
            when(m_InstitutionRepo.getInstutionByBlockAndVillage(PSM_ID, 29, 301, 401, 501)).thenReturn(stored);

            institutionService.getInstutionByVillage(PSM_ID, 29, 301, null, null);
            institutionService.getInstutionByVillage(PSM_ID, 29, 301, 401, null);
            institutionService.getInstutionByVillage(PSM_ID, 29, 301, null, 501);
            institutionService.getInstutionByVillage(PSM_ID, 29, 301, 401, 501);

            verify(m_InstitutionRepo).getInstution(PSM_ID, 29, 301);
            verify(m_InstitutionRepo).getInstutionByBlock(PSM_ID, 29, 301, 401);
            verify(m_InstitutionRepo).getInstutionByVillage(PSM_ID, 29, 301, 501);
            verify(m_InstitutionRepo).getInstutionByBlockAndVillage(PSM_ID, 29, 301, 401, 501);
        }

        @Test
        @DisplayName("the institution writes should each reach their own repository query")
        void institutionWrites_shouldReachTheirOwnQuery() {
            M_Institution institution = new M_Institution();
            ArrayList<M_Institution> stored = new ArrayList<>(List.of(institution));
            when(m_InstitutionRepo.saveAll(anyList())).thenReturn(stored);
            when(m_InstitutionRepo.geteditedData(31)).thenReturn(institution);
            when(m_InstitutionRepo.save(institution)).thenReturn(institution);

            assertSame(stored, institutionService.createInstution(new ArrayList<>()));
            assertSame(stored, institutionService.createInstutionByVillage(new ArrayList<>()));
            assertSame(institution, institutionService.editInstution(31));
            assertSame(institution, institutionService.saveEditData(institution));
        }

        @Test
        @DisplayName("createInstitutionByFile should report a run that stored rows")
        void createInstitutionByFile_shouldReportStoredRows() {
            ArrayList<Object[]> answer = new ArrayList<>();
            answer.add(new Object[] { 12, 0 });
            when(m_InstitutionRepo.institutionByFile(anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(answer);

            assertEquals("Data Saved Successfully", institutionService.createInstitutionByFile(uploadRequest()));
        }

        @Test
        @DisplayName("createInstitutionByFile should report a file whose rows are already on record")
        void createInstitutionByFile_shouldReportDuplicateRows() {
            ArrayList<Object[]> answer = new ArrayList<>();
            answer.add(new Object[] { 0, 12 });
            when(m_InstitutionRepo.institutionByFile(anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(answer);

            assertEquals("Data is already present", institutionService.createInstitutionByFile(uploadRequest()));
        }

        @Test
        @DisplayName("createInstitutionByFile should report a file the database could not read")
        void createInstitutionByFile_shouldReportUnusableFile() {
            ArrayList<Object[]> answer = new ArrayList<>();
            answer.add(new Object[] { -1, 0 });
            when(m_InstitutionRepo.institutionByFile(anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(answer);

            assertEquals("The Data in file is not appropriate",
                    institutionService.createInstitutionByFile(uploadRequest()));
        }

        @Test
        @DisplayName("createInstitutionByFile should answer nothing when the database reports nothing")
        void createInstitutionByFile_shouldAnswerNothingForEmptyReport() {
            when(m_InstitutionRepo.institutionByFile(anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(new ArrayList<>());

            assertNull(institutionService.createInstitutionByFile(uploadRequest()));
        }

        private JsonObject uploadRequest() {
            JsonObject request = new JsonObject();
            request.addProperty("createdBy", "admin");
            request.addProperty("userID", 3117);
            request.addProperty("serviceProviderID", 77);
            return request;
        }

        @Test
        @DisplayName("getInstuteTypeByDist should pick the query that matches what the caller named")
        void getInstuteTypeByDist_shouldPickMatchingQuery() {
            ArrayList<M_Institutiontype> stored = new ArrayList<>();
            when(m_InstitutiontypeRepo.getInstuteTypeByDist(PSM_ID, 301)).thenReturn(stored);
            when(m_InstitutiontypeRepo.getInstutionTypeByBlock(PSM_ID, 301, 401)).thenReturn(stored);
            when(m_InstitutiontypeRepo.getInstutionTypeByVillage(PSM_ID, 301, 501)).thenReturn(stored);
            when(m_InstitutiontypeRepo.getInstutionByBlockAndVillage(PSM_ID, 301, 401, 501)).thenReturn(stored);

            instituteTypeService.getInstuteTypeByDist(PSM_ID, 301, null, null);
            instituteTypeService.getInstuteTypeByDist(PSM_ID, 301, 401, null);
            instituteTypeService.getInstuteTypeByDist(PSM_ID, 301, null, 501);
            instituteTypeService.getInstuteTypeByDist(PSM_ID, 301, 401, 501);

            verify(m_InstitutiontypeRepo).getInstuteTypeByDist(PSM_ID, 301);
            verify(m_InstitutiontypeRepo).getInstutionTypeByBlock(PSM_ID, 301, 401);
            verify(m_InstitutiontypeRepo).getInstutionTypeByVillage(PSM_ID, 301, 501);
            verify(m_InstitutiontypeRepo).getInstutionByBlockAndVillage(PSM_ID, 301, 401, 501);
        }

        @Test
        @DisplayName("the institute type writes should each reach their own repository query")
        void instituteTypeWrites_shouldReachTheirOwnQuery() {
            M_Institutiontype type = new M_Institutiontype();
            ArrayList<M_Institutiontype> stored = new ArrayList<>(List.of(type));
            when(m_InstitutiontypeRepo.saveAll(anyList())).thenReturn(stored);
            when(m_InstitutiontypeRepo.getInstuteType(PSM_ID)).thenReturn(stored);
            when(m_InstitutiontypeRepo.editdata(21)).thenReturn(type);
            when(m_InstitutiontypeRepo.save(type)).thenReturn(type);

            assertSame(stored, instituteTypeService.createInstuteType(new ArrayList<>()));
            assertSame(stored, instituteTypeService.createInstuteTypeByDist(new ArrayList<>()));
            assertSame(stored, instituteTypeService.getInstuteType(PSM_ID));
            assertSame(type, instituteTypeService.editInstuteType(21));
            assertSame(type, instituteTypeService.saveEditdata(type));
        }
    }

    @Nested
    @DisplayName("Feedback, severity and sub-service services")
    class RemainingServiceTests {

        @Test
        @DisplayName("the feedback nature calls should each reach their own repository query")
        void feedbackNatureCalls_shouldReachTheirOwnQuery() {
            M_Feedbacknature nature = new M_Feedbacknature();
            ArrayList<M_Feedbacknature> stored = new ArrayList<>(List.of(nature));
            when(m_FeedbacknatureRepo.getInstuteType(41)).thenReturn(stored);
            when(m_FeedbacknatureRepo.saveAll(anyList())).thenReturn(stored);
            when(m_FeedbacknatureRepo.editFeedbackNatureType(21)).thenReturn(nature);
            when(m_FeedbacknatureRepo.save(nature)).thenReturn(nature);

            assertSame(stored, feedbackNatureService.getFeedbackNatureType(41));
            assertSame(stored, feedbackNatureService.createFeedbackNatueType(new ArrayList<>()));
            assertSame(nature, feedbackNatureService.editFeedbackNatureType(21));
            assertSame(nature, feedbackNatureService.saveEditedData(nature));
        }

        @Test
        @DisplayName("the feedback type calls should each reach their own repository query")
        void feedbackTypeCalls_shouldReachTheirOwnQuery() {
            M_Feedbacktype type = new M_Feedbacktype();
            ArrayList<M_Feedbacktype> stored = new ArrayList<>(List.of(type));
            when(m_FeedbacktypeRepo.getAllFeedbackType(PSM_ID)).thenReturn(stored);
            when(m_FeedbacktypeRepo.saveAll(anyList())).thenReturn(stored);
            when(m_FeedbacktypeRepo.deleteFeedback(41)).thenReturn(type);
            when(m_FeedbacktypeRepo.save(type)).thenReturn(type);

            assertSame(stored, feedbackTypeService.getFeedbackt(PSM_ID));
            assertSame(stored, feedbackTypeService.saveFeedbackType(new ArrayList<>()));
            assertSame(type, feedbackTypeService.getDataByServId(41));
            assertSame(type, feedbackTypeService.deletedataser(type));
        }

        @Test
        @DisplayName("the severity calls should each reach their own repository query")
        void severityCalls_shouldReachTheirOwnQuery() {
            M_Severity severity = new M_Severity();
            ArrayList<M_Severity> stored = new ArrayList<>(List.of(severity));
            when(m_ServerityRepo.getAllServerity(PSM_ID)).thenReturn(stored);
            when(m_ServerityRepo.saveAll(anyList())).thenReturn(stored);
            when(m_ServerityRepo.editServerity(31)).thenReturn(severity);
            when(m_ServerityRepo.save(severity)).thenReturn(severity);

            assertSame(stored, severityService.getServerity(PSM_ID));
            assertSame(stored, severityService.saveServerity(new ArrayList<>()));
            assertSame(severity, severityService.getDataByServId(31));
            assertSame(severity, severityService.deletedataser(severity));
        }

        @Test
        @DisplayName("the sub-service calls should each reach their own repository query")
        void subServiceCalls_shouldReachTheirOwnQuery() {
            M_Subservice subService = new M_Subservice();
            ArrayList<M_Subservice> stored = new ArrayList<>(List.of(subService));
            ArrayList<M_SubservicemasterPA> masters = new ArrayList<>(List.of(new M_SubservicemasterPA()));
            when(subserviceMasterRepo.saveAll(anyList())).thenReturn(stored);
            when(subserviceMasterRepo.getsubServiceName(PSM_ID)).thenReturn(stored);
            when(subserviceMasterRepo.getsubServiceNameById(61)).thenReturn(subService);
            when(subserviceMasterRepo.save(subService)).thenReturn(subService);
            when(m_SubservicemasterPArepo.getServiceNameByServiceID(3)).thenReturn(masters);

            assertSame(stored, subServiceService.saveSubList(new ArrayList<>()));
            assertSame(stored, subServiceService.getsubServiceName(PSM_ID));
            assertSame(subService, subServiceService.getsubServiceNameById(61));
            assertSame(subService, subServiceService.saveupdatedData(subService));
            assertSame(masters, subServiceService.getServiceNameByServiceID(3));
        }

        @Test
        @DisplayName("getAllServiceLine should hand back what the repository holds")
        void getAllServiceLine_shouldHandBackRepositoryContents() {
            List<M_ServiceMaster> stored = List.of(new M_ServiceMaster());
            when(mservicemasteRepo.getAllServiceline()).thenReturn(stored);

            assertSame(stored, serviceMasterService.getAllServiceLine());
        }
    }

    @Nested
    @DisplayName("ServiceProvider_ServiceImpl")
    class ProviderServiceTests {

        @Test
        @DisplayName("createProvider should answer the id of the provider it stored")
        void createProvider_shouldAnswerStoredId() {
            ServiceProvider_Model stored = new ServiceProvider_Model();
            stored.setServiceProviderId(77);
            when(iemrServiceRepository1.saveAll(any(Set.class)))
                    .thenReturn(new ArrayList<>(List.of(stored)));

            assertEquals(77, providerService.createProvider(Set.of(new ServiceProvider_Model())));
        }

        @Test
        @DisplayName("createProvider1 should refuse an empty batch rather than store nothing quietly")
        void createProvider1_shouldRefuseEmptyBatch() {
            assertThrows(DataNotFound.class, () -> providerService.createProvider1(new ArrayList<>()));
        }

        @Test
        @DisplayName("createProvider1 should refuse a batch the repository stored nothing from")
        void createProvider1_shouldRefuseBatchThatStoredNothing() {
            when(iemrServiceRepository1.saveAll(anyList())).thenReturn(new ArrayList<>());

            assertThrows(DataNotFound.class,
                    () -> providerService.createProvider1(List.of(new ServiceProvider_Model())));
        }

        @Test
        @DisplayName("createProvider1 should answer what the repository stored")
        void createProvider1_shouldAnswerStoredProviders() {
            ArrayList<ServiceProvider_Model> stored = new ArrayList<>(List.of(new ServiceProvider_Model()));
            when(iemrServiceRepository1.saveAll(anyList())).thenReturn(stored);

            assertSame(stored, providerService.createProvider1(List.of(new ServiceProvider_Model())));
        }

        @Test
        @DisplayName("the provider lookups should each reach their own repository query")
        void providerLookups_shouldReachTheirOwnQuery() {
            ServiceProvider_Model provider = new ServiceProvider_Model();
            ArrayList<ServiceProvider_Model> providers = new ArrayList<>(List.of(provider));
            M_ProviderServiceMapping mapping = new M_ProviderServiceMapping();
            M_UserservicerolemappingForRole roleMapping = new M_UserservicerolemappingForRole();
            ArrayList<M_UserservicerolemappingForRole> roleMappings = new ArrayList<>(List.of(roleMapping));
            ArrayList<V_Showprovideradmin> admins = new ArrayList<>(List.of(new V_Showprovideradmin()));
            when(iemrServiceRepository1.getProviderName("Piramal Swasthya")).thenReturn("Piramal Swasthya");
            when(iemrServiceRepository1.getAllProviderName()).thenReturn(providers);
            when(iemrServiceRepository1.getProviderData(77)).thenReturn(provider);
            when(iemrServiceRepository1.save(provider)).thenReturn(provider);
            when(iemrServiceRepository1.saveAll(anyList())).thenReturn(providers);
            when(m_ProviderServiceMappingRepo.getPSMID(PSM_ID)).thenReturn(mapping);
            when(m_ProviderServiceMappingRepo.saveAll(any(Set.class))).thenReturn(List.of(mapping));
            when(m_UserservicerolemappingForRoleRepo.saveAll(anyList())).thenReturn(roleMappings);
            when(m_UserservicerolemappingForRoleRepo.findByUSRMappingID(9001)).thenReturn(roleMapping);
            when(m_UserservicerolemappingForRoleRepo.save(roleMapping)).thenReturn(roleMapping);
            when(v_ShowprovideradminRepo.getAllProviderAdmin()).thenReturn(admins);

            assertEquals("Piramal Swasthya", providerService.getProviderName("Piramal Swasthya"));
            assertSame(providers, providerService.getAllProviderName());
            assertSame(provider, providerService.getProviderData(77));
            assertSame(provider, providerService.upDateProviderDetails(provider));
            assertSame(providers, providerService.createProvider(List.of(provider)));
            assertSame(mapping, providerService.getProviderserviceMapId(PSM_ID));
            assertEquals(1, providerService.mapProviderStateService(Set.of(mapping)).size());
            assertSame(roleMappings, providerService.AddUserRole(new ArrayList<>()));
            assertSame(roleMapping, providerService.getPADataForEdit(9001));
            assertSame(roleMapping, providerService.insertEditedData(roleMapping));
            assertSame(admins, providerService.getProviderAdmins());
        }
    }
}
