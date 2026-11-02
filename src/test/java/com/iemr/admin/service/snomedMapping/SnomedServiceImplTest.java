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
package com.iemr.admin.service.snomedMapping;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.admin.data.snomedMapping.ChildVaccinations;
import com.iemr.admin.data.snomedMapping.DiseaseType;
import com.iemr.admin.data.snomedMapping.OptionalVaccinations;
import com.iemr.admin.repository.snomedRepo.SnomedImmunizationRepo;
import com.iemr.admin.repository.snomedRepo.SnomedMappingRepo;
import com.iemr.admin.repository.snomedRepo.SnomedVaccinationRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The snomed service maps three separate clinical masters onto SNOMED codes,
 * choosing the master to work on from the request's own master type.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SnomedServiceImpl Test Suite")
class SnomedServiceImplTest {

    private static final Short MASTER_ID = 12;

    @Mock
    private SnomedMappingRepo snomedFamilyHistoryRepo;

    @Mock
    private SnomedVaccinationRepo snomedVaccinationRepo;

    @Mock
    private SnomedImmunizationRepo snomedImmunizationRepo;

    @InjectMocks
    private SnomedServiceImpl service;

    private static String requestFor(String masterType) {
        return "{\"masterType\":\"" + masterType + "\",\"masterID\":12,\"sctCode\":\"73211009\","
                + "\"sctTerm\":\"Diabetes mellitus\",\"modifiedBy\":\"admin\",\"deleted\":false,"
                + "\"mappingDetails\":[{\"masterID\":12,\"sctCode\":\"73211009\"}]}";
    }

    private static JsonObject asJson(String request) {
        return JsonParser.parseString(request).getAsJsonObject();
    }

    @Test
    @DisplayName("editSnomedMappingData should record the code against the family history master")
    void edit_shouldRecordCodeAgainstFamilyHistory() {
        String request = requestFor("Family History");
        when(snomedFamilyHistoryRepo.updateFamilyHistoryDetails(MASTER_ID, "73211009", "Diabetes mellitus", "admin"))
                .thenReturn(1);

        assertEquals("Data Updated", service.editSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("editSnomedMappingData should record the code against the optional vaccination master")
    void edit_shouldRecordCodeAgainstOptionalVaccination() {
        String request = requestFor("Optional Vaccination");
        when(snomedVaccinationRepo.updateVaccinationDetails(any(), anyString(), anyString(), anyString()))
                .thenReturn(1);

        assertEquals("Data Updated", service.editSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("editSnomedMappingData should record the code against the immunization master")
    void edit_shouldRecordCodeAgainstImmunization() {
        String request = requestFor("Immunization");
        when(snomedImmunizationRepo.updateImmunizationDetails(any(), anyString(), anyString(), anyString()))
                .thenReturn(1);

        assertEquals("Data Updated", service.editSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("editSnomedMappingData should give up on a master type it does not recognise")
    void edit_shouldGiveUpOnUnknownMasterType() {
        String request = requestFor("Astrology");

        assertThrows(NullPointerException.class, () -> service.editSnomedMappingData(asJson(request), request),
                "no master was touched, so there is no row count to judge the edit by");
    }

    @Test
    @DisplayName("editSnomedMappingData should refuse a request that is not there at all")
    void edit_shouldRefuseAbsentRequest() {
        assertEquals("Invalid Master Type", service.editSnomedMappingData(null, null));
    }

    @Test
    @DisplayName("editSnomedMappingData should give up when the master row could not be touched")
    void edit_shouldGiveUpWhenNothingTouched() {
        String request = requestFor("Family History");
        when(snomedFamilyHistoryRepo.updateFamilyHistoryDetails(any(), anyString(), anyString(), anyString()))
                .thenReturn(null);

        assertThrows(NullPointerException.class, () -> service.editSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("saveSnomedMappingData should store the family history mappings the request carried")
    void save_shouldStoreFamilyHistoryMappings() {
        String request = requestFor("Family History");
        when(snomedFamilyHistoryRepo.saveAll(anyList())).thenReturn(List.of(new DiseaseType()));

        assertEquals("Data Saved", service.saveSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("saveSnomedMappingData should store the optional vaccination mappings the request carried")
    void save_shouldStoreOptionalVaccinationMappings() {
        String request = requestFor("Optional Vaccination");
        when(snomedVaccinationRepo.saveAll(anyList())).thenReturn(List.of(new OptionalVaccinations()));

        assertEquals("Data Saved", service.saveSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("saveSnomedMappingData should store the immunization mappings the request carried")
    void save_shouldStoreImmunizationMappings() {
        String request = requestFor("Immunization");
        when(snomedImmunizationRepo.saveAll(anyList())).thenReturn(List.of(new ChildVaccinations()));

        assertEquals("Data Saved", service.saveSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("saveSnomedMappingData should say nothing was saved when the repository stored nothing")
    void save_shouldSayNothingSavedWhenRepositoryStoredNothing() {
        String request = requestFor("Family History");
        when(snomedFamilyHistoryRepo.saveAll(anyList())).thenReturn(new ArrayList<DiseaseType>());

        assertEquals(null, service.saveSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("saveSnomedMappingData should refuse a master type it does not recognise")
    void save_shouldRefuseUnknownMasterType() {
        String request = requestFor("Astrology");

        assertEquals("Invalid Master Type", service.saveSnomedMappingData(asJson(request), request));
    }

    @Test
    @DisplayName("saveSnomedMappingData should refuse a request that is not there at all")
    void save_shouldRefuseAbsentRequest() {
        assertEquals("Invalid Master Type", service.saveSnomedMappingData(null, null));
    }

    @Test
    @DisplayName("fetchSnomedMaster should answer the family history master as it stands")
    void fetch_shouldAnswerFamilyHistoryMaster() {
        DiseaseType entry = new DiseaseType();
        entry.setMasterName("Diabetes");
        when(snomedFamilyHistoryRepo.fetchDiseaseType()).thenReturn(List.of(entry));

        assertTrue(service.fetchSnomedMaster(asJson(requestFor("Family History"))).contains("Diabetes"));
    }

    @Test
    @DisplayName("fetchSnomedMaster should answer the optional vaccination master as it stands")
    void fetch_shouldAnswerOptionalVaccinationMaster() {
        OptionalVaccinations entry = new OptionalVaccinations();
        entry.setMasterName("Typhoid");
        when(snomedVaccinationRepo.fetchOptionalVaccinations()).thenReturn(List.of(entry));

        assertTrue(service.fetchSnomedMaster(asJson(requestFor("Optional Vaccination"))).contains("Typhoid"));
    }

    @Test
    @DisplayName("fetchSnomedMaster should answer the immunization master as it stands")
    void fetch_shouldAnswerImmunizationMaster() {
        ChildVaccinations entry = new ChildVaccinations();
        entry.setMasterName("BCG");
        when(snomedImmunizationRepo.fetchChildVaccinations()).thenReturn(List.of(entry));

        assertTrue(service.fetchSnomedMaster(asJson(requestFor("Immunization"))).contains("BCG"));
    }

    @Test
    @DisplayName("fetchSnomedMaster should refuse a master type it does not recognise")
    void fetch_shouldRefuseUnknownMasterType() {
        assertEquals("Invalid Master Type", service.fetchSnomedMaster(asJson(requestFor("Astrology"))));
    }

    @Test
    @DisplayName("fetchSnomedMaster should refuse a request that is not there at all")
    void fetch_shouldRefuseAbsentRequest() {
        assertEquals("Invalid request", service.fetchSnomedMaster(null));
    }

    @Test
    @DisplayName("updateStatus should retire the entry in whichever master the request names")
    void updateStatus_shouldRetireEntryInNamedMaster() {
        when(snomedFamilyHistoryRepo.updateStatus(any(), anyBoolean(), anyString())).thenReturn(1);

        assertEquals("Data updated successfully", service.updateStatus(requestFor("Family History")));
        verify(snomedFamilyHistoryRepo).updateStatus(MASTER_ID, Boolean.FALSE, "admin");
    }

    @Test
    @DisplayName("updateStatus should retire the entry in the optional vaccination master")
    void updateStatus_shouldRetireOptionalVaccinationEntry() {
        when(snomedVaccinationRepo.updateStatus(any(), anyBoolean(), anyString())).thenReturn(1);

        assertEquals("Data updated successfully", service.updateStatus(requestFor("Optional Vaccination")));
    }

    @Test
    @DisplayName("updateStatus should retire the entry in the immunization master")
    void updateStatus_shouldRetireImmunizationEntry() {
        when(snomedImmunizationRepo.updateStatus(any(), anyBoolean(), anyString())).thenReturn(1);

        assertEquals("Data updated successfully", service.updateStatus(requestFor("Immunization")));
    }

    @Test
    @DisplayName("updateStatus should say so when no entry was touched")
    void updateStatus_shouldSaySoWhenNothingTouched() {
        when(snomedFamilyHistoryRepo.updateStatus(any(), anyBoolean(), anyString())).thenReturn(0);

        assertEquals("Data not updated", service.updateStatus(requestFor("Family History")));
    }

    @Test
    @DisplayName("updateStatus should touch no master when the master type is not recognised")
    void updateStatus_shouldTouchNoMasterForUnknownType() {
        assertEquals("Data not updated", service.updateStatus(requestFor("Astrology")));
    }
}
