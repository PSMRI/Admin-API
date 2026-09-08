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
import org.mockito.ArgumentCaptor;

import com.iemr.admin.data.provideronboard.M_Calltype;
import com.iemr.admin.data.provideronboard.M_Subservice;
import com.iemr.admin.data.provideronboard.M_SubservicemasterPA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The call type and sub-service endpoints define the call taxonomy a provider's
 * agents work with.
 */
@DisplayName("ProviderOnBoardController call type Test Suite")
class ProviderOnBoardCallTypeControllerTest extends ProviderOnBoardFixture {

    private static M_Calltype callType(Integer id, String name) {
        M_Calltype callType = new M_Calltype();
        callType.setCallTypeID(id);
        callType.setCallType(name);
        return callType;
    }

    private static M_Subservice subService(Integer id, String name) {
        M_Subservice subService = new M_Subservice();
        subService.setSubServiceID(id);
        subService.setSubServiceName(name);
        return subService;
    }

    @Test
    @DisplayName("saveCallTypeData should flatten the nested request into one call type per description")
    void saveCallTypeData_shouldFlattenNestedRequest() {
        ArrayList<M_Calltype> stored = new ArrayList<>(List.of(callType(51, "Medical Advice")));
        when(calltypeinter.saveCallList(anyList())).thenReturn(stored);

        String request = "[{\"callGroupType\":\"Inbound\",\"createdBy\":\"admin\",\"callType1\":"
                + "[{\"calltype\":\"Medical Advice\",\"providerServiceMapID\":4001,"
                + "\"callTypeDesc1\":[\"General\",\"Urgent\"],\"fitToBlock1\":[\"true\",\"false\"],"
                + "\"fitForFollowup1\":[true,false],\"isInbound1\":[true,true],"
                + "\"isOutbound1\":[false,false]}]}]";

        assertSuccessContaining(controller.saveCallTypeData(request), "Medical Advice");

        ArgumentCaptor<List<M_Calltype>> captor = ArgumentCaptor.forClass(List.class);
        verify(calltypeinter).saveCallList(captor.capture());
        List<M_Calltype> saved = captor.getValue();
        assertEquals(2, saved.size(), "each description must become its own call type");
        assertEquals("Inbound", saved.get(0).getCallGroupType());
        assertEquals("General", saved.get(0).getCallTypeDesc());
        assertTrue(saved.get(0).getFitToBlock());
        assertEquals(4001, saved.get(0).getProviderServiceMapID());
    }

    @Test
    @DisplayName("saveCallTypeData should answer an error envelope for a request it cannot read")
    void saveCallTypeData_shouldAnswerErrorEnvelopeForUnreadableRequest() {
        assertCodeException(controller.saveCallTypeData("[{\"callGroupType\":\"Inbound\"}]"));
    }

    @Test
    @DisplayName("createCalltypeData should answer the call types the service stored")
    void createCalltypeData_shouldAnswerStoredCallTypes() {
        ArrayList<M_Calltype> stored = new ArrayList<>(List.of(callType(51, "Medical Advice")));
        when(calltypeinter.createCalltype(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createCalltypeData("[{\"callType\":\"Medical Advice\"}]"),
                "Medical Advice");
    }

    @Test
    @DisplayName("createCalltypeData should answer an error envelope when the store fails")
    void createCalltypeData_shouldAnswerErrorEnvelopeOnFailure() {
        when(calltypeinter.createCalltype(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createCalltypeData("[{\"callType\":\"Medical Advice\"}]"));
    }

    @Test
    @DisplayName("getCallTypeData should answer the call types configured for the mapping")
    void getCallTypeData_shouldAnswerConfiguredCallTypes() {
        ArrayList<M_Calltype> stored = new ArrayList<>(List.of(callType(51, "Medical Advice")));
        when(calltypeinter.getCalltypeData(4001)).thenReturn(stored);

        assertSuccessContaining(controller.getCallTypeData("{\"providerServiceMapID\":4001}"), "Medical Advice");
    }

    @Test
    @DisplayName("getCallTypeData should answer an error envelope when the lookup fails")
    void getCallTypeData_shouldAnswerErrorEnvelopeOnFailure() {
        when(calltypeinter.getCalltypeData(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getCallTypeData("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("updateCallTypeData should copy the edited fields onto the stored call type")
    void updateCallTypeData_shouldCopyEditedFields() {
        M_Calltype stored = callType(51, "old name");
        when(calltypeinter.updateCallType(51)).thenReturn(stored);
        when(calltypeinter.saveupdatedData(stored)).thenReturn(stored);

        String response = controller.updateCallTypeData("{\"callTypeID\":51,\"callType\":\"Medical Advice\","
                + "\"callGroupType\":\"Inbound\",\"callTypeDesc\":\"General\",\"providerServiceMapID\":4001,"
                + "\"fitToBlock\":true,\"fitForFollowup\":false,\"isInbound\":true,\"isOutbound\":false,"
                + "\"processed\":\"N\",\"maxRedial\":3}");

        assertSuccessContaining(response, "Medical Advice");
        assertEquals("Inbound", stored.getCallGroupType());
        assertEquals(3, stored.getMaxRedial());
    }

    @Test
    @DisplayName("updateCallTypeData should answer an error envelope for a call type that does not exist")
    void updateCallTypeData_shouldAnswerErrorEnvelopeForUnknownCallType() {
        when(calltypeinter.updateCallType(51)).thenReturn(null);

        assertCodeException(controller.updateCallTypeData("{\"callTypeID\":51}"));
    }

    @Test
    @DisplayName("deleteCallType should mark the call type deleted and answer what was saved")
    void deleteCallType_shouldMarkCallTypeDeleted() {
        M_Calltype stored = callType(51, "Medical Advice");
        when(calltypeinter.updateCallType(51)).thenReturn(stored);
        when(calltypeinter.saveupdatedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteCallType("{\"callTypeID\":51,\"deleted\":true}"), "Medical Advice");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteCallType should answer an error envelope for a call type that does not exist")
    void deleteCallType_shouldAnswerErrorEnvelopeForUnknownCallType() {
        when(calltypeinter.updateCallType(51)).thenReturn(null);

        assertCodeException(controller.deleteCallType("{\"callTypeID\":51,\"deleted\":true}"));
    }

    @Test
    @DisplayName("saveSubServiceData should flatten the nested request onto the provider mapping")
    void saveSubServiceData_shouldFlattenNestedRequest() {
        ArrayList<M_Subservice> stored = new ArrayList<>(List.of(subService(61, "Counselling")));
        when(subServiceInter.saveSubList(anyList())).thenReturn(stored);

        String request = "[{\"providerServiceMapID\":4001,\"createdBy\":\"admin\",\"subServiceDetails\":"
                + "[{\"subServiceName\":\"Counselling\",\"subServiceDesc\":\"Mental health\"}]}]";

        assertSuccessContaining(controller.saveSubServiceData(request), "Counselling");

        ArgumentCaptor<List<M_Subservice>> captor = ArgumentCaptor.forClass(List.class);
        verify(subServiceInter).saveSubList(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(4001, captor.getValue().get(0).getProviderServiceMapID());
        assertEquals("admin", captor.getValue().get(0).getCreatedBy());
    }

    @Test
    @DisplayName("saveSubServiceData should answer an error envelope for a request it cannot read")
    void saveSubServiceData_shouldAnswerErrorEnvelopeForUnreadableRequest() {
        assertCodeException(controller.saveSubServiceData("[{\"providerServiceMapID\":4001}]"));
    }

    @Test
    @DisplayName("FindSubSeriveNameByMapId should answer the master sub-services for the service")
    void findSubSeriveNameByMapId_shouldAnswerMasterSubServices() {
        M_SubservicemasterPA master = new M_SubservicemasterPA();
        master.setSubServiceMasterID(71);
        master.setSubServiceName("Counselling");
        ArrayList<M_SubservicemasterPA> stored = new ArrayList<>(List.of(master));
        when(subServiceInter.getServiceNameByServiceID(3)).thenReturn(stored);

        assertSuccessContaining(controller.FindSubSeriveNameByMapId("{\"serviceID\":3}"), "Counselling");
    }

    @Test
    @DisplayName("FindSubSeriveNameByMapId should answer an error envelope when the lookup fails")
    void findSubSeriveNameByMapId_shouldAnswerErrorEnvelopeOnFailure() {
        when(subServiceInter.getServiceNameByServiceID(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.FindSubSeriveNameByMapId("{\"serviceID\":3}"));
    }

    @Test
    @DisplayName("getSubSeriveName should answer the sub-services configured for the mapping")
    void getSubSeriveName_shouldAnswerConfiguredSubServices() {
        ArrayList<M_Subservice> stored = new ArrayList<>(List.of(subService(61, "Counselling")));
        when(subServiceInter.getsubServiceName(4001)).thenReturn(stored);

        assertSuccessContaining(controller.getSubSeriveName("{\"providerServiceMapID\":4001}"), "Counselling");
    }

    @Test
    @DisplayName("getSubSeriveName should answer an error envelope when the lookup fails")
    void getSubSeriveName_shouldAnswerErrorEnvelopeOnFailure() {
        when(subServiceInter.getsubServiceName(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getSubSeriveName("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("updateSubSerive should copy the edited fields onto the stored sub-service")
    void updateSubSerive_shouldCopyEditedFields() {
        M_Subservice stored = subService(61, "old name");
        when(subServiceInter.getsubServiceNameById(61)).thenReturn(stored);
        when(subServiceInter.saveupdatedData(stored)).thenReturn(stored);

        String response = controller.updateSubSerive("{\"subServiceID\":61,\"subServiceName\":\"Counselling\","
                + "\"subServiceDesc\":\"Mental health\",\"providerServiceMapID\":4001,\"processed\":\"N\"}");

        assertSuccessContaining(response, "Counselling");
        assertEquals("Mental health", stored.getSubServiceDesc());
        verify(subServiceInter).saveupdatedData(stored);
    }

    @Test
    @DisplayName("updateSubSerive should answer an error envelope for a sub-service that does not exist")
    void updateSubSerive_shouldAnswerErrorEnvelopeForUnknownSubService() {
        when(subServiceInter.getsubServiceNameById(61)).thenReturn(null);

        assertCodeException(controller.updateSubSerive("{\"subServiceID\":61}"));
    }

    @Test
    @DisplayName("deleteSubSerive should mark the sub-service deleted and answer what was saved")
    void deleteSubSerive_shouldMarkSubServiceDeleted() {
        M_Subservice stored = subService(61, "Counselling");
        when(subServiceInter.getsubServiceNameById(61)).thenReturn(stored);
        when(subServiceInter.saveupdatedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteSubSerive("{\"subServiceID\":61,\"deleted\":true}"), "Counselling");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteSubSerive should answer an error envelope for a sub-service that does not exist")
    void deleteSubSerive_shouldAnswerErrorEnvelopeForUnknownSubService() {
        when(subServiceInter.getsubServiceNameById(61)).thenReturn(null);

        assertCodeException(controller.deleteSubSerive("{\"subServiceID\":61,\"deleted\":true}"));
    }

    @Test
    @DisplayName("saveupdatedData should be reached with the resolved record rather than the request")
    void updateSubSerive_shouldSaveTheResolvedRecord() {
        M_Subservice stored = subService(61, "old name");
        when(subServiceInter.getsubServiceNameById(61)).thenReturn(stored);
        when(subServiceInter.saveupdatedData(any())).thenReturn(stored);

        controller.updateSubSerive("{\"subServiceID\":61,\"subServiceName\":\"Counselling\"}");

        verify(subServiceInter).saveupdatedData(stored);
    }
}
