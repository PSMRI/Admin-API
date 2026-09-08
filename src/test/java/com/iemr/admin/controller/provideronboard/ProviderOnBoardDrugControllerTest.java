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
package com.iemr.admin.controller.provideronboard;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.data.provideronboard.M_104druggroup;
import com.iemr.admin.data.provideronboard.M_104drugmapping;
import com.iemr.admin.data.provideronboard.M_104drugmaster;
import com.iemr.admin.utils.exception.IEMRException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The drug endpoints keep the 104 helpline's drug catalogue for a provider. */
@DisplayName("ProviderOnBoardController drug Test Suite")
class ProviderOnBoardDrugControllerTest extends ProviderOnBoardFixture {

    private static M_104drugmaster drug(Integer id, String name) {
        M_104drugmaster drug = new M_104drugmaster();
        drug.setDrugID(id);
        drug.setDrugName(name);
        return drug;
    }

    private static M_104druggroup drugGroup(Integer id, String name) {
        M_104druggroup group = new M_104druggroup();
        group.setDrugGroupID(id);
        group.setDrugGroup(name);
        return group;
    }

    private static M_104drugmapping drugMapping(Integer id, String drugName) {
        M_104drugmapping mapping = new M_104drugmapping();
        mapping.setDrugMapID(id);
        mapping.setDrugName(drugName);
        return mapping;
    }

    @Test
    @DisplayName("getDrugData should answer the drugs the catalogue holds")
    void getDrugData_shouldAnswerCatalogueDrugs() throws IEMRException {
        ArrayList<M_104drugmaster> stored = new ArrayList<>(List.of(drug(101, "Paracetamol")));
        when(drugMasterInter.getAllDrugData(101, (short) 77, Boolean.FALSE)).thenReturn(stored);

        assertSuccessContaining(
                controller.getDrugData("{\"drugID\":101,\"serviceProviderID\":77,\"deleted\":false}"),
                "Paracetamol");
    }

    @Test
    @DisplayName("getDrugData should answer an error envelope when the catalogue cannot be read")
    void getDrugData_shouldAnswerErrorEnvelopeOnFailure() throws IEMRException {
        when(drugMasterInter.getAllDrugData(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getDrugData("{\"drugID\":101,\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getDrugGroups should answer the drug groups the catalogue holds")
    void getDrugGroups_shouldAnswerCatalogueGroups() throws IEMRException {
        ArrayList<M_104druggroup> stored = new ArrayList<>(List.of(drugGroup(201, "Analgesics")));
        when(drugMasterInter.getAllDrugGroups(201, (short) 77, Boolean.FALSE)).thenReturn(stored);

        assertSuccessContaining(
                controller.getDrugGroups("{\"drugGroupID\":201,\"serviceProviderID\":77,\"deleted\":false}"),
                "Analgesics");
    }

    @Test
    @DisplayName("getDrugGroups should answer an error envelope when the catalogue cannot be read")
    void getDrugGroups_shouldAnswerErrorEnvelopeOnFailure() throws IEMRException {
        when(drugMasterInter.getAllDrugGroups(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getDrugGroups("{\"drugGroupID\":201,\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getDrugGroupMappings should answer every mapping for the provider's service")
    void getDrugGroupMappings_shouldAnswerMappings() throws IEMRException {
        ArrayList<M_104drugmapping> stored = new ArrayList<>(List.of(drugMapping(301, "Paracetamol")));
        when(drugMasterInter.getAllDrugGroupMappings(null, 77, 3)).thenReturn(stored);

        assertSuccessContaining(
                controller.getDrugGroupMappings("{\"serviceProviderID\":77,\"serviceID\":3}"), "Paracetamol");
    }

    @Test
    @DisplayName("getDrugGroupMappings should answer an error envelope when the lookup fails")
    void getDrugGroupMappings_shouldAnswerErrorEnvelopeOnFailure() throws IEMRException {
        when(drugMasterInter.getAllDrugGroupMappings(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getDrugGroupMappings("{\"serviceProviderID\":77,\"serviceID\":3}"));
    }

    @Test
    @DisplayName("updateDrugGroup should copy the edited fields onto the stored group")
    void updateDrugGroup_shouldCopyEditedFields() {
        M_104druggroup stored = drugGroup(201, "old name");
        when(drugMasterInter.getDrugGroupById(201)).thenReturn(stored);
        when(drugMasterInter.saveUpdatedDrugGroup(stored)).thenReturn(stored);

        assertSuccessContaining(controller.updateDrugGroup("{\"drugGroupID\":201,\"drugGroup\":\"Analgesics\","
                + "\"drugGroupDesc\":\"Pain relief\",\"modifiedBy\":\"admin\"}"), "Analgesics");
        assertEquals("Pain relief", stored.getDrugGroupDesc());
        assertEquals("admin", stored.getModifiedBy());
    }

    @Test
    @DisplayName("updateDrugGroup should answer an error envelope for a group that does not exist")
    void updateDrugGroup_shouldAnswerErrorEnvelopeForUnknownGroup() {
        when(drugMasterInter.getDrugGroupById(201)).thenReturn(null);

        assertCodeException(controller.updateDrugGroup("{\"drugGroupID\":201}"));
    }

    @Test
    @DisplayName("updateDrugMaster should copy the edited fields onto the stored drug")
    void updateDrugMaster_shouldCopyEditedFields() {
        M_104drugmaster stored = drug(101, "old name");
        when(drugMasterInter.getDrugDataById(101)).thenReturn(stored);
        when(drugMasterInter.saveUpdatedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.updateDrugMaster("{\"drugID\":101,\"drugName\":\"Paracetamol\","
                + "\"drugDesc\":\"Antipyretic\",\"remarks\":\"OTC\",\"modifiedBy\":\"admin\"}"), "Paracetamol");
        assertEquals("Antipyretic", stored.getDrugDesc());
        assertEquals("OTC", stored.getRemarks());
    }

    @Test
    @DisplayName("updateDrugMaster should answer an error envelope for a drug that does not exist")
    void updateDrugMaster_shouldAnswerErrorEnvelopeForUnknownDrug() {
        when(drugMasterInter.getDrugDataById(101)).thenReturn(null);

        assertCodeException(controller.updateDrugMaster("{\"drugID\":101}"));
    }

    @Test
    @DisplayName("updateDrugMapping should copy the edited fields onto the stored mapping")
    void updateDrugMapping_shouldCopyEditedFields() {
        M_104drugmapping stored = drugMapping(301, "old name");
        when(drugMasterInter.getDrugMappingsById(301)).thenReturn(stored);
        when(drugMasterInter.saveUpdatedDrugMapping(stored)).thenReturn(stored);

        assertSuccessContaining(controller.updateDrugMapping("{\"drugMapID\":301,\"drugGroupID\":201,"
                + "\"drugGroupName\":\"Analgesics\",\"drugId\":101,\"drugName\":\"Paracetamol\","
                + "\"remarks\":\"OTC\"}"), "Paracetamol");
        assertEquals(201, stored.getDrugGroupID());
        assertEquals("Analgesics", stored.getDrugGroupName());
    }

    @Test
    @DisplayName("updateDrugMapping should answer an error envelope for a mapping that does not exist")
    void updateDrugMapping_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(drugMasterInter.getDrugMappingsById(301)).thenReturn(null);

        assertCodeException(controller.updateDrugMapping("{\"drugMapID\":301}"));
    }

    @Test
    @DisplayName("updateDrugStatus should retire the drug when the request names one")
    void updateDrugStatus_shouldRetireDrug() {
        when(drugMasterInter.updateDrugStatus(any())).thenReturn(1);

        assertSuccessContaining(controller.updateDrugStatus("{\"drugID\":101,\"deleted\":true}"),
                "Drug status updaated to deleted");
        verify(drugMasterInter).updateDrugStatus(any());
        verify(drugMasterInter, never()).updateDrugGroupStatus(any());
    }

    @Test
    @DisplayName("updateDrugStatus should retire the drug group when no drug is named")
    void updateDrugStatus_shouldRetireDrugGroup() {
        when(drugMasterInter.updateDrugGroupStatus(any())).thenReturn(1);

        assertSuccessContaining(controller.updateDrugStatus("{\"drugGroupID\":201,\"deleted\":true}"),
                "DrugGroup status updaated to deleted");
        verify(drugMasterInter).updateDrugGroupStatus(any());
        verify(drugMasterInter, never()).updateDrugStatus(any());
    }

    @Test
    @DisplayName("updateDrugStatus should retire the mapping when neither a drug nor a group is named")
    void updateDrugStatus_shouldRetireDrugMapping() {
        when(drugMasterInter.updateDrugMappingStatus(any())).thenReturn(1);

        assertSuccessContaining(controller.updateDrugStatus("{\"drugMapID\":301,\"deleted\":true}"),
                "DrugGroup status updaated to deleted");
        verify(drugMasterInter).updateDrugMappingStatus(any());
    }

    @Test
    @DisplayName("updateDrugStatus should answer an empty response when the request names nothing to retire")
    void updateDrugStatus_shouldAnswerEmptyResponseWhenNothingNamed() {
        assertSuccessContaining(controller.updateDrugStatus("{}"), "response");
        verify(drugMasterInter, never()).updateDrugStatus(any());
        verify(drugMasterInter, never()).updateDrugGroupStatus(any());
        verify(drugMasterInter, never()).updateDrugMappingStatus(any());
    }

    @Test
    @DisplayName("updateDrugStatus should answer an error envelope when the retirement fails")
    void updateDrugStatus_shouldAnswerErrorEnvelopeOnFailure() {
        when(drugMasterInter.updateDrugStatus(any())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.updateDrugStatus("{\"drugID\":101,\"deleted\":true}"));
    }

    @Test
    @DisplayName("saveDrugGroup should answer the groups the service stored")
    void saveDrugGroup_shouldAnswerStoredGroups() throws IEMRException {
        ArrayList<M_104druggroup> stored = new ArrayList<>(List.of(drugGroup(201, "Analgesics")));
        when(drugMasterInter.saveDrugGroup(anyList())).thenReturn(stored);

        assertSuccessContaining(
                controller.saveDrugGroup("{\"drugGroups\":[{\"drugGroup\":\"Analgesics\"}]}"), "Analgesics");
    }

    @Test
    @DisplayName("saveDrugGroup should answer an error envelope when no groups are named")
    void saveDrugGroup_shouldAnswerErrorEnvelopeWithoutGroups() throws IEMRException {
        when(drugMasterInter.saveDrugGroup(eq(null))).thenThrow(new IllegalArgumentException("nothing to save"));

        assertGenericFailure(controller.saveDrugGroup("{}"));
    }

    @Test
    @DisplayName("saveDrug should answer the drugs the service stored")
    void saveDrug_shouldAnswerStoredDrugs() throws IEMRException {
        ArrayList<M_104drugmaster> stored = new ArrayList<>(List.of(drug(101, "Paracetamol")));
        when(drugMasterInter.saveDrugData(anyList())).thenReturn(stored);

        assertSuccessContaining(
                controller.saveDrug("{\"drugMasters\":[{\"drugName\":\"Paracetamol\"}]}"), "Paracetamol");
    }

    @Test
    @DisplayName("saveDrug should answer an error envelope when the store fails")
    void saveDrug_shouldAnswerErrorEnvelopeOnFailure() throws IEMRException {
        when(drugMasterInter.saveDrugData(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.saveDrug("{\"drugMasters\":[{\"drugName\":\"Paracetamol\"}]}"));
    }

    @Test
    @DisplayName("mapDrugWithGroup should answer the mappings the service stored")
    void mapDrugWithGroup_shouldAnswerStoredMappings() throws IEMRException {
        ArrayList<M_104drugmapping> stored = new ArrayList<>(List.of(drugMapping(301, "Paracetamol")));
        when(drugMasterInter.mapDrugWithGroup(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.mapDrugWithGroup(
                "{\"drugMappings\":[{\"drugId\":101,\"drugGroupID\":201}]}"), "Paracetamol");
    }

    @Test
    @DisplayName("mapDrugWithGroup should answer an error envelope when the mapping fails")
    void mapDrugWithGroup_shouldAnswerErrorEnvelopeOnFailure() throws IEMRException {
        when(drugMasterInter.mapDrugWithGroup(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.mapDrugWithGroup("{\"drugMappings\":[{\"drugId\":101}]}"));
    }

    @Test
    @DisplayName("getDrugData should pass the deleted flag the caller sends straight through")
    void getDrugData_shouldPassDeletedFlagThrough() throws IEMRException {
        when(drugMasterInter.getAllDrugData(anyInt(), any(), eq(Boolean.TRUE))).thenReturn(new ArrayList<>());

        controller.getDrugData("{\"drugID\":101,\"serviceProviderID\":77,\"deleted\":true}");

        verify(drugMasterInter).getAllDrugData(101, (short) 77, Boolean.TRUE);
    }
}
