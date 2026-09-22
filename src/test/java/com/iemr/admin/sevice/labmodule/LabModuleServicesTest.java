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
package com.iemr.admin.sevice.labmodule;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.calibration.Calibration;
import com.iemr.admin.data.labmodule.ComponentMaster;
import com.iemr.admin.data.labmodule.ComponentResultMap;
import com.iemr.admin.data.labmodule.IOTComponent;
import com.iemr.admin.data.labmodule.IOTProcedure;
import com.iemr.admin.data.labmodule.ProcedureComponentMapping;
import com.iemr.admin.data.labmodule.ProcedureMaster;
import com.iemr.admin.repo.calibration.CalibrationAPIRepo;
import com.iemr.admin.repo.labmodule.ComponentMasterRepo;
import com.iemr.admin.repo.labmodule.ComponentResultMapRepo;
import com.iemr.admin.repo.labmodule.IOTRepo;
import com.iemr.admin.repo.labmodule.ProcedureComponentMappingRepo;
import com.iemr.admin.repo.labmodule.ProcedureMasterRepo;
import com.iemr.admin.utils.exception.IEMRException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
 * The lab module masters define the tests a diagnostic device can run and the
 * components each test reports, so a broken mapping means a result that cannot
 * be recorded against its test.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Lab module master service Test Suite")
class LabModuleServicesTest {

    private static final Integer PSM_ID = 4001;
    private static final Integer PROCEDURE_ID = 71;
    private static final Integer COMPONENT_ID = 81;

    @Mock
    private ProcedureMasterRepo procedureMasterRepo;

    @Mock
    private ComponentMasterRepo componentMasterRepo;

    @Mock
    private ComponentResultMapRepo componentResultMapRepo;

    @Mock
    private ProcedureComponentMappingRepo procedureComponentMappingRepo;

    @Mock
    private CalibrationAPIRepo calibrationAPIRepo;

    @Mock
    private IOTRepo iotRepo;

    private MastersCreationServiceImpl creationService;
    private MastersFetchingServiceImpl fetchingService;
    private MastersMappingServiceImpl mappingService;
    private MastersStatusUpdateImpl statusService;
    private IOTServiceImpl iotService;

    @BeforeEach
    void setUp() {
        creationService = new MastersCreationServiceImpl();
        creationService.setProcedureMasterRepo(procedureMasterRepo);
        creationService.setComponentMasterRepo(componentMasterRepo);
        creationService.setComponentResultMapRepo(componentResultMapRepo);
        creationService.calibrationAPIRepo = calibrationAPIRepo;
        creationService.iotRepo = iotRepo;

        fetchingService = new MastersFetchingServiceImpl();
        fetchingService.setProcedureMasterRepo(procedureMasterRepo);
        fetchingService.setComponentMasterRepo(componentMasterRepo);
        fetchingService.setComponentResultMapRepo(componentResultMapRepo);
        fetchingService.setProcedureComponentMappingRepo(procedureComponentMappingRepo);

        mappingService = new MastersMappingServiceImpl();
        mappingService.setProcedureComponentMappingRepo(procedureComponentMappingRepo);

        statusService = new MastersStatusUpdateImpl();
        statusService.setProcedureMasterRepo(procedureMasterRepo);
        statusService.setComponentMasterRepo(componentMasterRepo);
        statusService.setComponentResultMapRepo(componentResultMapRepo);
        statusService.calibrationAPIRepo = calibrationAPIRepo;
        statusService.iotRepo = iotRepo;

        iotService = new IOTServiceImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(iotService, "iotRepo", iotRepo);
    }

    /** One row of the procedure detail query, in the order the builder reads it. */
    private static ArrayList<Object[]> procedureDetailRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { PROCEDURE_ID, "Haemoglobin", "Blood test", "Lab", "Both", PSM_ID, Boolean.FALSE,
                "N", "admin", Timestamp.valueOf("2026-02-17 09:30:00"), "admin", Boolean.FALSE,
                Timestamp.valueOf("2026-02-17 09:30:00") });
        return rows;
    }

    private static ArrayList<Object[]> componentDetailRow() {
        ArrayList<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { COMPONENT_ID, "Haemoglobin count", "g/dL", "TextBox", "LOINC-1", Boolean.FALSE });
        return rows;
    }

    @Nested
    @DisplayName("MastersCreationServiceImpl")
    class CreationTests {

        @Test
        @DisplayName("createProcedureMaster should default a procedure that does not say whether it is mandatory")
        void createProcedureMaster_shouldDefaultMandatoryFlag() throws Exception {
            when(procedureMasterRepo.save(any())).thenAnswer(call -> {
                ProcedureMaster saved = call.getArgument(0);
                saved.setProcedureID(PROCEDURE_ID);
                return saved;
            });
            when(procedureMasterRepo.getProcedureDetails(PROCEDURE_ID)).thenReturn(procedureDetailRow());

            String created = creationService.createProcedureMaster(
                    "{\"procedureName\":\"Haemoglobin\",\"procedureType\":\"Lab\",\"createdBy\":\"admin\"}");

            assertTrue(created.contains("Haemoglobin"), created);
        }

        @Test
        @DisplayName("createProcedureMaster should answer nothing when the stored procedure cannot be read back")
        void createProcedureMaster_shouldAnswerNothingWhenUnreadable() throws Exception {
            when(procedureMasterRepo.save(any())).thenAnswer(call -> call.getArgument(0));
            when(procedureMasterRepo.getProcedureDetails(any())).thenReturn(new ArrayList<>());

            assertNull(creationService.createProcedureMaster("{\"procedureName\":\"Haemoglobin\"}"));
        }

        @Test
        @DisplayName("createProcedureMaster should fill the calibration URLs for a calibrated device test")
        void createProcedureMaster_shouldFillCalibrationUrls() throws Exception {
            IOTProcedure iotProcedure = new IOTProcedure();
            iotProcedure.setCalibrationCode("HB");
            Calibration calibration = new Calibration();
            calibration.setCalibrationStartAPI("http://device/start/{test_name}");
            calibration.setCalibrationStatusAPI("http://device/status/{test_name}");
            calibration.setCalibrationEndAPI("http://device/end/{test_name}");
            when(iotRepo.getIOTProcedureByID(91)).thenReturn(iotProcedure);
            when(calibrationAPIRepo.getCalibration()).thenReturn(calibration);
            when(procedureMasterRepo.save(any())).thenAnswer(call -> call.getArgument(0));
            when(procedureMasterRepo.getProcedureDetails(any())).thenReturn(procedureDetailRow());

            creationService.createProcedureMaster("{\"procedureName\":\"Haemoglobin\","
                    + "\"isCalibration\":true,\"iotProcedureID\":91}");

            verify(iotRepo).updateIOTWithCalibration("http://device/start/HB", "http://device/status/HB",
                    "http://device/end/HB", 91);
        }

        @Test
        @DisplayName("createProcedureMaster should leave the calibration URLs alone once they are set")
        void createProcedureMaster_shouldLeaveExistingCalibrationUrls() throws Exception {
            IOTProcedure iotProcedure = new IOTProcedure();
            iotProcedure.setCalibrationCode("HB");
            iotProcedure.setCalibrationStartAPI("http://device/start/HB");
            when(iotRepo.getIOTProcedureByID(91)).thenReturn(iotProcedure);
            when(calibrationAPIRepo.getCalibration()).thenReturn(new Calibration());
            when(procedureMasterRepo.save(any())).thenAnswer(call -> call.getArgument(0));
            when(procedureMasterRepo.getProcedureDetails(any())).thenReturn(procedureDetailRow());

            creationService.createProcedureMaster("{\"procedureName\":\"Haemoglobin\","
                    + "\"isCalibration\":true,\"iotProcedureID\":91}");

            verify(iotRepo, never()).updateIOTWithCalibration(anyString(), anyString(), anyString(), anyInt());
        }

        @Test
        @DisplayName("createProcedureMaster should refuse a device the calibration store does not know")
        void createProcedureMaster_shouldRefuseUnknownDevice() {
            when(iotRepo.getIOTProcedureByID(91)).thenReturn(null);

            assertThrows(IEMRException.class, () -> creationService.createProcedureMaster(
                    "{\"procedureName\":\"Haemoglobin\",\"isCalibration\":true,\"iotProcedureID\":91}"));
        }

        @Test
        @DisplayName("createComponentMaster should store the result options a picklist component offers")
        void createComponentMaster_shouldStoreResultOptions() throws Exception {
            when(componentMasterRepo.save(any())).thenAnswer(call -> call.getArgument(0));
            when(componentResultMapRepo.saveAll(anyList())).thenAnswer(call -> {
                List<ComponentResultMap> given = call.getArgument(0);
                return new ArrayList<>(given);
            });

            String created = creationService.createComponentMaster("{\"testComponentName\":\"Blood group\","
                    + "\"testComponentID\":81,\"providerServiceMapID\":4001,\"createdBy\":\"admin\","
                    + "\"compOpt\":[{\"name\":\"A+\"},{\"name\":\"B+\"}]}");

            assertTrue(created.contains("Blood group"), created);
        }

        @Test
        @DisplayName("createComponentMaster should store a component that offers no result options")
        void createComponentMaster_shouldStoreComponentWithoutOptions() throws Exception {
            when(componentMasterRepo.save(any())).thenAnswer(call -> call.getArgument(0));

            String created = creationService
                    .createComponentMaster("{\"testComponentName\":\"Haemoglobin count\"}");

            assertTrue(created.contains("Haemoglobin count"), created);
            verify(componentResultMapRepo, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("createComponentMaster should refuse a run that stored fewer options than it was given")
        void createComponentMaster_shouldRefusePartialOptionStore() {
            when(componentMasterRepo.save(any())).thenAnswer(call -> call.getArgument(0));
            when(componentResultMapRepo.saveAll(anyList())).thenReturn(new ArrayList<ComponentResultMap>());

            assertThrows(Exception.class, () -> creationService.createComponentMaster(
                    "{\"testComponentName\":\"Blood group\",\"compOpt\":[{\"name\":\"A+\"}]}"));
        }

        @Test
        @DisplayName("createComponentMaster should answer nothing when the component could not be stored")
        void createComponentMaster_shouldAnswerNothingWhenUnstored() throws Exception {
            when(componentMasterRepo.save(any())).thenReturn(null);

            assertNull(creationService.createComponentMaster("{\"testComponentName\":\"Blood group\"}"));
        }
    }

    @Nested
    @DisplayName("MastersFetchingServiceImpl")
    class FetchingTests {

        @Test
        @DisplayName("getProcedureMaster should publish the procedures of the provider")
        void getProcedureMaster_shouldPublishProviderProcedures() throws Exception {
            ProcedureMaster procedure = new ProcedureMaster();
            procedure.setProcedureID(PROCEDURE_ID);
            procedure.setProcedureName("Haemoglobin");
            when(procedureMasterRepo.findProcByPSMIDc(PSM_ID))
                    .thenReturn(new ArrayList<>(List.of(procedure)));

            assertTrue(fetchingService.getProcedureMaster(PSM_ID).contains("Haemoglobin"));
        }

        @Test
        @DisplayName("getProcedureMasterDelFalse should publish only the live procedures")
        void getProcedureMasterDelFalse_shouldPublishLiveProcedures() throws Exception {
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(new Object[] { PROCEDURE_ID, "Haemoglobin", "Blood test", "Lab" });
            when(procedureMasterRepo.getProcedureDetailsDelFalse(PSM_ID)).thenReturn(rows);

            assertTrue(fetchingService.getProcedureMasterDelFalse(PSM_ID).contains("Haemoglobin"));
        }

        @Test
        @DisplayName("getComponentMaster should publish the components of the provider")
        void getComponentMaster_shouldPublishProviderComponents() throws Exception {
            ComponentMaster component = new ComponentMaster();
            component.setTestComponentID(COMPONENT_ID);
            component.setTestComponentName("Haemoglobin count");
            when(componentMasterRepo.getComponentDetailsBypsmID(PSM_ID))
                    .thenReturn(new ArrayList<>(List.of(component)));

            assertTrue(fetchingService.getComponentMaster(PSM_ID).contains("Haemoglobin count"));
        }

        @Test
        @DisplayName("getComponentMasterDelFalse should publish only the live components")
        void getComponentMasterDelFalse_shouldPublishLiveComponents() throws Exception {
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(new Object[] { COMPONENT_ID, "Haemoglobin count", "g/dL", "TextBox", "LOINC-1", "component" });
            when(componentMasterRepo.getComponentDetailsDelFalse(PSM_ID)).thenReturn(rows);

            assertTrue(fetchingService.getComponentMasterDelFalse(PSM_ID).contains("Haemoglobin count"));
        }

        @Test
        @DisplayName("getComponentMasterDelFalse should publish an empty list when nothing is live")
        void getComponentMasterDelFalse_shouldPublishEmptyList() throws Exception {
            when(componentMasterRepo.getComponentDetailsDelFalse(PSM_ID)).thenReturn(new ArrayList<>());

            assertEquals("[]", fetchingService.getComponentMasterDelFalse(PSM_ID));
        }

        @Test
        @DisplayName("getProcCompMappingDelFalse should publish the mappings of the provider")
        void getProcCompMappingDelFalse_shouldPublishMappings() throws Exception {
            when(procedureComponentMappingRepo.getProcedureComponentMappingList(PSM_ID))
                    .thenReturn(mappingRow());

            assertTrue(fetchingService.getProcCompMappingDelFalse(PSM_ID).contains("Haemoglobin"));
        }

        @Test
        @DisplayName("getProcCompMappingDelFalse should publish an empty list when nothing is mapped")
        void getProcCompMappingDelFalse_shouldPublishEmptyList() throws Exception {
            when(procedureComponentMappingRepo.getProcedureComponentMappingList(PSM_ID))
                    .thenReturn(new ArrayList<>());

            assertEquals("[]", fetchingService.getProcCompMappingDelFalse(PSM_ID));
        }

        @Test
        @DisplayName("getProcCompMappingForProcedureID should publish the mappings of the procedure")
        void getProcCompMappingForProcedureID_shouldPublishMappings() throws Exception {
            when(procedureComponentMappingRepo.getProcedureComponentMappingListForProcedureID(PROCEDURE_ID))
                    .thenReturn(mappingRow());

            assertTrue(fetchingService.getProcCompMappingForProcedureID(PROCEDURE_ID).contains("Haemoglobin"));
        }

        @Test
        @DisplayName("getProcCompMappingForProcedureID should publish an empty list when nothing is mapped")
        void getProcCompMappingForProcedureID_shouldPublishEmptyList() throws Exception {
            when(procedureComponentMappingRepo.getProcedureComponentMappingListForProcedureID(PROCEDURE_ID))
                    .thenReturn(new ArrayList<>());

            assertEquals("[]", fetchingService.getProcCompMappingForProcedureID(PROCEDURE_ID));
        }

        @Test
        @DisplayName("getComponentDetailsForComponentID should publish a typed component without its options")
        void getComponentDetails_shouldPublishTypedComponent() throws Exception {
            ComponentMaster stored = new ComponentMaster();
            stored.setTestComponentID(COMPONENT_ID);
            stored.setTestComponentName("Haemoglobin count");
            stored.setInputType("TextBox");
            when(componentMasterRepo.findByTestComponentID(COMPONENT_ID)).thenReturn(stored);

            String published = fetchingService.getComponentDetailsForComponentID(COMPONENT_ID);

            assertTrue(published.contains("Haemoglobin count"), published);
            verify(componentResultMapRepo, never()).findByTestComponentIDAndDeleted(anyInt(), anyBoolean());
        }

        @Test
        @DisplayName("getComponentDetailsForComponentID should publish a picklist component with its options")
        void getComponentDetails_shouldPublishPicklistWithOptions() throws Exception {
            ComponentMaster stored = new ComponentMaster();
            stored.setTestComponentID(COMPONENT_ID);
            stored.setTestComponentName("Blood group");
            stored.setInputType("Dropdown");
            ComponentResultMap option = new ComponentResultMap();
            option.setResultValue("A+");
            when(componentMasterRepo.findByTestComponentID(COMPONENT_ID)).thenReturn(stored);
            when(componentResultMapRepo.findByTestComponentIDAndDeleted(COMPONENT_ID, false))
                    .thenReturn(new ArrayList<>(List.of(option)));

            String published = fetchingService.getComponentDetailsForComponentID(COMPONENT_ID);

            assertTrue(published.contains("A+"), published);
        }

        @Test
        @DisplayName("getComponentDetailsForComponentID should answer nothing for a component that does not exist")
        void getComponentDetails_shouldAnswerNothingForUnknownComponent() throws Exception {
            when(componentMasterRepo.findByTestComponentID(COMPONENT_ID)).thenReturn(null);

            assertNull(fetchingService.getComponentDetailsForComponentID(COMPONENT_ID));
        }

        private ArrayList<Object[]> mappingRow() {
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(new Object[] { PROCEDURE_ID, COMPONENT_ID, "Haemoglobin", "Blood test",
                    "Haemoglobin count", null, "g/dL" });
            return rows;
        }
    }

    @Nested
    @DisplayName("MastersMappingServiceImpl")
    class MappingTests {

        @Test
        @DisplayName("createProcedureComponentMapping should replace the previous mapping of the procedure")
        void createMapping_shouldReplacePreviousMapping() throws Exception {
            when(procedureComponentMappingRepo.saveAll(anyList()))
                    .thenAnswer(call -> new ArrayList<>((List<ProcedureComponentMapping>) call.getArgument(0)));
            ArrayList<Object[]> rows = new ArrayList<>();
            rows.add(new Object[] { PROCEDURE_ID, COMPONENT_ID, "Haemoglobin", "Blood test",
                    "Haemoglobin count", null, "g/dL" });
            when(procedureComponentMappingRepo.getProcedureComponentMappingListForProcedureID(PROCEDURE_ID))
                    .thenReturn(rows);

            String mapped = mappingService.createProcedureComponentMapping("{\"procedureID\":71,"
                    + "\"providerServiceMapID\":4001,\"createdBy\":\"admin\","
                    + "\"compList\":[{\"testComponentID\":81}]}");

            assertTrue(mapped.contains("Haemoglobin"), mapped);
            verify(procedureComponentMappingRepo).softDeleteProcCompMapping(PROCEDURE_ID, "admin");
        }

        @Test
        @DisplayName("createProcedureComponentMapping should answer nothing when it stored fewer than it was given")
        void createMapping_shouldAnswerNothingOnPartialStore() throws Exception {
            when(procedureComponentMappingRepo.saveAll(anyList()))
                    .thenReturn(new ArrayList<ProcedureComponentMapping>());

            assertNull(mappingService.createProcedureComponentMapping("{\"procedureID\":71,"
                    + "\"createdBy\":\"admin\",\"compList\":[{\"testComponentID\":81}]}"));
        }

        @Test
        @DisplayName("createProcedureComponentMapping should report a request that maps no components")
        void createMapping_shouldReportEmptyRequest() throws Exception {
            assertEquals("1", mappingService.createProcedureComponentMapping("{\"procedureID\":71}"));
            verify(procedureComponentMappingRepo, never()).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("MastersStatusUpdateImpl")
    class StatusUpdateTests {

        @Test
        @DisplayName("updateProcedureStatus should publish the procedure once its status has changed")
        void updateProcedureStatus_shouldPublishChangedProcedure() throws Exception {
            when(procedureMasterRepo.updateProcedureStatus(PROCEDURE_ID, true)).thenReturn(1);
            when(procedureMasterRepo.getProcedureDetails(PROCEDURE_ID)).thenReturn(procedureDetailRow());

            assertTrue(statusService.updateProcedureStatus(PROCEDURE_ID, true).contains("Haemoglobin"));
        }

        @Test
        @DisplayName("updateProcedureStatus should answer nothing when no procedure changed")
        void updateProcedureStatus_shouldAnswerNothingWhenNothingChanged() throws Exception {
            when(procedureMasterRepo.updateProcedureStatus(PROCEDURE_ID, true)).thenReturn(0);

            assertNull(statusService.updateProcedureStatus(PROCEDURE_ID, true));
        }

        @Test
        @DisplayName("updateComponentStatus should publish the component once its status has changed")
        void updateComponentStatus_shouldPublishChangedComponent() throws Exception {
            when(componentMasterRepo.updateComponentStatus(COMPONENT_ID, true)).thenReturn(1);
            when(componentMasterRepo.getComponentDetailsByCompID(COMPONENT_ID)).thenReturn(componentDetailRow());

            assertTrue(statusService.updateComponentStatus(COMPONENT_ID, true).contains("Haemoglobin count"));
        }

        @Test
        @DisplayName("updateComponentStatus should answer nothing when no component changed")
        void updateComponentStatus_shouldAnswerNothingWhenNothingChanged() throws Exception {
            when(componentMasterRepo.updateComponentStatus(COMPONENT_ID, true)).thenReturn(0);

            assertNull(statusService.updateComponentStatus(COMPONENT_ID, true));
        }

        @Test
        @DisplayName("updateProcedureMaster should publish the procedure once the edit lands")
        void updateProcedureMaster_shouldPublishEditedProcedure() throws Exception {
            when(procedureMasterRepo.updateProcedureDetails(anyInt(), anyString(), any(), anyString(),
                    anyString(), anyString(), any(), any(), any())).thenReturn(1);
            when(procedureMasterRepo.getProcedureDetails(PROCEDURE_ID)).thenReturn(procedureDetailRow());

            String published = statusService.updateProcedureMaster("{\"procedureID\":71,"
                    + "\"procedureName\":\"Haemoglobin\",\"procedureType\":\"Lab\",\"gender\":\"Both\","
                    + "\"modifiedBy\":\"admin\"}");

            assertTrue(published.contains("Haemoglobin"), published);
        }

        @Test
        @DisplayName("updateProcedureMaster should answer nothing for an edit that leaves out a mandatory field")
        void updateProcedureMaster_shouldAnswerNothingForIncompleteEdit() throws Exception {
            assertNull(statusService.updateProcedureMaster("{\"procedureID\":71}"));
        }

        @Test
        @DisplayName("updateProcedureMaster should clear the calibration URLs when calibration is switched off")
        void updateProcedureMaster_shouldClearCalibrationUrls() throws Exception {
            IOTProcedure iotProcedure = new IOTProcedure();
            iotProcedure.setCalibrationCode("HB");
            iotProcedure.setCalibrationStartAPI("http://device/start/HB");
            when(iotRepo.getIOTProcedureByID(91)).thenReturn(iotProcedure);
            when(calibrationAPIRepo.getCalibration()).thenReturn(new Calibration());
            when(procedureMasterRepo.updateProcedureDetails(anyInt(), anyString(), any(), anyString(),
                    anyString(), anyString(), any(), any(), any())).thenReturn(1);
            when(procedureMasterRepo.getProcedureDetails(PROCEDURE_ID)).thenReturn(procedureDetailRow());

            statusService.updateProcedureMaster("{\"procedureID\":71,\"procedureName\":\"Haemoglobin\","
                    + "\"procedureType\":\"Lab\",\"gender\":\"Both\",\"modifiedBy\":\"admin\","
                    + "\"isCalibration\":false,\"iotProcedureID\":91}");

            verify(iotRepo).updateIOTWithCalibration(null, null, null, 91);
        }

        @Test
        @DisplayName("updateProcedureMaster should refuse a device the calibration store does not know")
        void updateProcedureMaster_shouldRefuseUnknownDevice() {
            when(iotRepo.getIOTProcedureByID(91)).thenReturn(null);

            assertThrows(IEMRException.class, () -> statusService.updateProcedureMaster(
                    "{\"procedureID\":71,\"isCalibration\":true,\"iotProcedureID\":91}"));
        }

        @Test
        @DisplayName("updateComponentMaster should publish a typed component once the edit lands")
        void updateComponentMaster_shouldPublishEditedTypedComponent() throws Exception {
            when(componentMasterRepo.updateComponentDetailsTextBox(anyInt(), anyString(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1);
            when(componentMasterRepo.getComponentDetailsByCompID(COMPONENT_ID)).thenReturn(componentDetailRow());

            String published = statusService.updateComponentMaster("{\"testComponentID\":81,"
                    + "\"testComponentName\":\"Haemoglobin count\",\"inputType\":\"TextBox\","
                    + "\"modifiedBy\":\"admin\"}");

            assertTrue(published.contains("Haemoglobin count"), published);
        }

        @Test
        @DisplayName("updateComponentMaster should replace the result options of a picklist component")
        void updateComponentMaster_shouldReplacePicklistOptions() throws Exception {
            when(componentMasterRepo.updateComponentDetailsOtherThenTextBox(anyInt(), anyString(), any(),
                    anyString(), any(), any(), any())).thenReturn(1);
            when(componentResultMapRepo.saveAll(anyList()))
                    .thenAnswer(call -> new ArrayList<>((List<ComponentResultMap>) call.getArgument(0)));
            when(componentMasterRepo.getComponentDetailsByCompID(COMPONENT_ID)).thenReturn(componentDetailRow());

            String published = statusService.updateComponentMaster("{\"testComponentID\":81,"
                    + "\"testComponentName\":\"Blood group\",\"inputType\":\"Dropdown\","
                    + "\"modifiedBy\":\"admin\",\"compOpt\":[{\"name\":\"A+\"}]}");

            assertTrue(published.contains("Haemoglobin count"), published);
            verify(componentResultMapRepo).deletePreviousCompResultMappingSoft(COMPONENT_ID, "admin");
        }

        @Test
        @DisplayName("updateComponentMaster should refuse a run that stored fewer options than it was given")
        void updateComponentMaster_shouldRefusePartialOptionStore() {
            when(componentMasterRepo.updateComponentDetailsOtherThenTextBox(anyInt(), anyString(), any(),
                    anyString(), any(), any(), any())).thenReturn(1);
            when(componentResultMapRepo.saveAll(anyList())).thenReturn(new ArrayList<ComponentResultMap>());

            assertThrows(Exception.class, () -> statusService.updateComponentMaster("{\"testComponentID\":81,"
                    + "\"testComponentName\":\"Blood group\",\"inputType\":\"Dropdown\","
                    + "\"modifiedBy\":\"admin\",\"compOpt\":[{\"name\":\"A+\"}]}"));
        }

        @Test
        @DisplayName("updateComponentMaster should answer nothing for a component that names no input type")
        void updateComponentMaster_shouldAnswerNothingWithoutInputType() throws Exception {
            assertNull(statusService.updateComponentMaster("{\"testComponentID\":81}"));
        }
    }

    @Nested
    @DisplayName("IOTServiceImpl")
    class IotServiceTests {

        @Test
        @DisplayName("getIOTProcedure should answer the device tests on record")
        void getIOTProcedure_shouldAnswerDeviceTests() {
            IOTProcedure procedure = new IOTProcedure();
            procedure.setCalibrationCode("HB");
            when(iotRepo.getIOTProcedure()).thenReturn(new ArrayList<>(List.of(procedure)));

            assertTrue(iotService.getIOTProcedure().contains("HB"));
        }

        @Test
        @DisplayName("getIOTComponent should answer the device components on record")
        void getIOTComponent_shouldAnswerDeviceComponents() {
            IOTComponent component = new IOTComponent();
            when(iotRepo.getIOTComponent()).thenReturn(new ArrayList<>(List.of(component)));

            assertTrue(iotService.getIOTComponent().startsWith("["));
        }
    }
}
