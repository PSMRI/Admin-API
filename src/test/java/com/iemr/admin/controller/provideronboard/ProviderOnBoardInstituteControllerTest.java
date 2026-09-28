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

import com.google.gson.JsonObject;
import com.iemr.admin.data.provideronboard.M_Institutedirectory;
import com.iemr.admin.data.provideronboard.M_Institutedirectorymapping;
import com.iemr.admin.data.provideronboard.M_Institutesubdirectory;
import com.iemr.admin.data.provideronboard.M_Institution;
import com.iemr.admin.data.provideronboard.M_Institutiontype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The institute endpoints keep the referral directory a provider's agents route
 * callers to - the directories, their sub-directories, and the institutions
 * mapped underneath them.
 */
@DisplayName("ProviderOnBoardController institute Test Suite")
class ProviderOnBoardInstituteControllerTest extends ProviderOnBoardFixture {

    private static M_Institutedirectory directory(Integer id, String name) {
        M_Institutedirectory directory = new M_Institutedirectory();
        directory.setInstituteDirectoryID(id);
        directory.setInstituteDirectoryName(name);
        return directory;
    }

    private static M_Institutiontype instituteType(Integer id, String name) {
        M_Institutiontype type = new M_Institutiontype();
        type.setInstitutionTypeID(id);
        type.setInstitutionType(name);
        return type;
    }

    private static M_Institution institution(Integer id, String name) {
        M_Institution institution = new M_Institution();
        institution.setInstitutionID(id);
        institution.setInstitutionName(name);
        return institution;
    }

    private static M_Institutesubdirectory subDirectory(Integer id, String name) {
        M_Institutesubdirectory subDirectory = new M_Institutesubdirectory();
        subDirectory.setInstituteSubDirectoryID(id);
        subDirectory.setInstituteSubDirectoryName(name);
        return subDirectory;
    }

    private static M_Institutedirectorymapping directoryMapping(Integer id, Integer institutionId) {
        M_Institutedirectorymapping mapping = new M_Institutedirectorymapping();
        mapping.setInstituteDirMapID(id);
        mapping.setInstitutionID(institutionId);
        return mapping;
    }

    @Test
    @DisplayName("createInstuteDirectoty should answer the directories the service stored")
    void createInstuteDirectoty_shouldAnswerStoredDirectories() {
        ArrayList<M_Institutedirectory> stored = new ArrayList<>(List.of(directory(11, "Hospitals")));
        when(instuteDirectoryInter.createInstuteDirectory(anyList())).thenReturn(stored);

        assertSuccessContaining(
                controller.createInstuteDirectoty("[{\"instituteDirectoryName\":\"Hospitals\"}]"), "Hospitals");
    }

    @Test
    @DisplayName("createInstuteDirectoty should answer an error envelope when the store fails")
    void createInstuteDirectoty_shouldAnswerErrorEnvelopeOnFailure() {
        when(instuteDirectoryInter.createInstuteDirectory(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createInstuteDirectoty("[{\"instituteDirectoryName\":\"Hospitals\"}]"));
    }

    @Test
    @DisplayName("getInstuteDirectory should answer the directories configured for the mapping")
    void getInstuteDirectory_shouldAnswerConfiguredDirectories() {
        ArrayList<M_Institutedirectory> stored = new ArrayList<>(List.of(directory(11, "Hospitals")));
        when(instuteDirectoryInter.getInstuteDirectory(4001)).thenReturn(stored);

        assertSuccessContaining(controller.getInstuteDirectory("{\"providerServiceMapId\":4001}"), "Hospitals");
    }

    @Test
    @DisplayName("getInstuteDirectory should answer an error envelope when the lookup fails")
    void getInstuteDirectory_shouldAnswerErrorEnvelopeOnFailure() {
        when(instuteDirectoryInter.getInstuteDirectory(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getInstuteDirectory("{\"providerServiceMapId\":4001}"));
    }

    @Test
    @DisplayName("editInstuteDirectory should copy the edited fields onto the stored directory")
    void editInstuteDirectory_shouldCopyEditedFields() {
        M_Institutedirectory stored = directory(11, "old name");
        when(instuteDirectoryInter.editInstuteDirectory(11)).thenReturn(stored);
        when(instuteDirectoryInter.editdata(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editInstuteDirectory("{\"instituteDirectoryID\":11,"
                + "\"instituteDirectoryName\":\"Hospitals\",\"instituteDirectoryDesc\":\"Referral hospitals\","
                + "\"modifiedBy\":\"admin\"}"), "Hospitals");
        assertEquals("Referral hospitals", stored.getInstituteDirectoryDesc());
    }

    @Test
    @DisplayName("editInstuteDirectory should answer an error envelope for a directory that does not exist")
    void editInstuteDirectory_shouldAnswerErrorEnvelopeForUnknownDirectory() {
        when(instuteDirectoryInter.editInstuteDirectory(11)).thenReturn(null);

        assertCodeException(controller.editInstuteDirectory("{\"instituteDirectoryID\":11}"));
    }

    @Test
    @DisplayName("deleteInstuteDirectory should mark the directory deleted")
    void deleteInstuteDirectory_shouldMarkDirectoryDeleted() {
        M_Institutedirectory stored = directory(11, "Hospitals");
        when(instuteDirectoryInter.editInstuteDirectory(11)).thenReturn(stored);
        when(instuteDirectoryInter.editdata(stored)).thenReturn(stored);

        assertSuccessContaining(
                controller.deleteInstuteDirectory("{\"instituteDirectoryID\":11,\"deleted\":true}"), "Hospitals");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteInstuteDirectory should answer an error envelope for a directory that does not exist")
    void deleteInstuteDirectory_shouldAnswerErrorEnvelopeForUnknownDirectory() {
        when(instuteDirectoryInter.editInstuteDirectory(11)).thenReturn(null);

        assertCodeException(controller.deleteInstuteDirectory("{\"instituteDirectoryID\":11,\"deleted\":true}"));
    }

    @Test
    @DisplayName("getInstuteType should answer the institute types configured for the mapping")
    void getInstuteType_shouldAnswerConfiguredTypes() {
        ArrayList<M_Institutiontype> stored = new ArrayList<>(List.of(instituteType(21, "PHC")));
        when(m_InstitutiontypeInter.getInstuteType(4001)).thenReturn(stored);

        assertSuccessContaining(controller.getInstuteType("{\"providerServiceMapID\":4001}"), "PHC");
    }

    @Test
    @DisplayName("getInstuteType should answer an error envelope when the lookup fails")
    void getInstuteType_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutiontypeInter.getInstuteType(anyInt())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getInstuteType("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("createInstuteType should answer the institute types the service stored")
    void createInstuteType_shouldAnswerStoredTypes() {
        ArrayList<M_Institutiontype> stored = new ArrayList<>(List.of(instituteType(21, "PHC")));
        when(m_InstitutiontypeInter.createInstuteType(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createInstuteType("[{\"institutionType\":\"PHC\"}]"), "PHC");
    }

    @Test
    @DisplayName("createInstuteType should answer an error envelope when the store fails")
    void createInstuteType_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutiontypeInter.createInstuteType(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createInstuteType("[{\"institutionType\":\"PHC\"}]"));
    }

    @Test
    @DisplayName("editInstuteType should copy the edited fields onto the stored institute type")
    void editInstuteType_shouldCopyEditedFields() {
        M_Institutiontype stored = instituteType(21, "old name");
        when(m_InstitutiontypeInter.editInstuteType(21)).thenReturn(stored);
        when(m_InstitutiontypeInter.saveEditdata(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editInstuteType("{\"institutionTypeID\":21,\"institutionType\":\"PHC\","
                + "\"institutionTypeDesc\":\"Primary health centre\",\"modifiedBy\":\"admin\"}"), "PHC");
        assertEquals("Primary health centre", stored.getInstitutionTypeDesc());
    }

    @Test
    @DisplayName("editInstuteType should answer an error envelope for an institute type that does not exist")
    void editInstuteType_shouldAnswerErrorEnvelopeForUnknownType() {
        when(m_InstitutiontypeInter.editInstuteType(21)).thenReturn(null);

        assertCodeException(controller.editInstuteType("{\"institutionTypeID\":21}"));
    }

    @Test
    @DisplayName("deleteInstuteType should mark the institute type deleted")
    void deleteInstuteType_shouldMarkTypeDeleted() {
        M_Institutiontype stored = instituteType(21, "PHC");
        when(m_InstitutiontypeInter.editInstuteType(21)).thenReturn(stored);
        when(m_InstitutiontypeInter.saveEditdata(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteInstuteType("{\"institutionTypeID\":21,\"deleted\":true}"), "PHC");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteInstuteType should answer an error envelope for an institute type that does not exist")
    void deleteInstuteType_shouldAnswerErrorEnvelopeForUnknownType() {
        when(m_InstitutiontypeInter.editInstuteType(21)).thenReturn(null);

        assertCodeException(controller.deleteInstuteType("{\"institutionTypeID\":21,\"deleted\":true}"));
    }

    @Test
    @DisplayName("createInstuteTypeByDist should answer the institute types the service stored")
    void createInstuteTypeByDist_shouldAnswerStoredTypes() {
        ArrayList<M_Institutiontype> stored = new ArrayList<>(List.of(instituteType(21, "PHC")));
        when(m_InstitutiontypeInter.createInstuteType(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createInstuteTypeByDist("[{\"institutionType\":\"PHC\"}]"), "PHC");
    }

    @Test
    @DisplayName("createInstuteTypeByDist should answer an error envelope when the store fails")
    void createInstuteTypeByDist_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutiontypeInter.createInstuteType(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createInstuteTypeByDist("[{\"institutionType\":\"PHC\"}]"));
    }

    @Test
    @DisplayName("getInstituteTypeByDist should answer the institute types under the location")
    void getInstituteTypeByDist_shouldAnswerTypesUnderLocation() {
        ArrayList<M_Institutiontype> stored = new ArrayList<>(List.of(instituteType(21, "PHC")));
        when(m_InstitutiontypeInter.getInstuteTypeByDist(4001, 31, 41, 51)).thenReturn(stored);

        assertSuccessContaining(controller.getInstituteTypeByDist("{\"providerServiceMapID\":4001,"
                + "\"districtId\":31,\"subDistrictId\":41,\"villageId\":51}"), "PHC");
    }

    @Test
    @DisplayName("getInstituteTypeByDist should answer an error envelope when the lookup fails")
    void getInstituteTypeByDist_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutiontypeInter.getInstuteTypeByDist(any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getInstituteTypeByDist("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("getInstution should answer the institutions under the block")
    void getInstution_shouldAnswerInstitutionsUnderBlock() {
        ArrayList<M_Institution> stored = new ArrayList<>(List.of(institution(31, "District Hospital")));
        when(m_InstitutionInter.getInstution(4001, 29, 301, 401)).thenReturn(stored);

        assertSuccessContaining(controller.getInstution("{\"providerServiceMapID\":4001,\"stateID\":29,"
                + "\"districtID\":301,\"blockID\":401}"), "District Hospital");
    }

    @Test
    @DisplayName("getInstution should answer an error envelope when the lookup fails")
    void getInstution_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutionInter.getInstution(any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getInstution("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("createInstution should answer the institutions the service stored")
    void createInstution_shouldAnswerStoredInstitutions() {
        ArrayList<M_Institution> stored = new ArrayList<>(List.of(institution(31, "District Hospital")));
        when(m_InstitutionInter.createInstution(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createInstution("[{\"institutionName\":\"District Hospital\"}]"),
                "District Hospital");
    }

    @Test
    @DisplayName("createInstution should answer an error envelope when the store fails")
    void createInstution_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutionInter.createInstution(anyList())).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createInstution("[{\"institutionName\":\"District Hospital\"}]"));
    }

    @Test
    @DisplayName("editInstution should copy every contact field onto the stored institution")
    void editInstution_shouldCopyContactFields() {
        M_Institution stored = institution(31, "old name");
        when(m_InstitutionInter.editInstution(31)).thenReturn(stored);
        when(m_InstitutionInter.saveEditData(stored)).thenReturn(stored);

        String response = controller.editInstution("{\"institutionID\":31,"
                + "\"institutionName\":\"District Hospital\",\"address\":\"Main Road\",\"contactNo1\":\"9000000001\","
                + "\"contactNo2\":\"9000000002\",\"contactNo3\":\"9000000003\",\"contactPerson1\":\"Asha\","
                + "\"contactPerson2\":\"Ravi\",\"contactPerson3\":\"Meera\","
                + "\"contactPerson1_Email\":\"asha@example.org\",\"contactPerson2_Email\":\"ravi@example.org\","
                + "\"contactPerson3_Email\":\"meera@example.org\",\"website\":\"https://example.org\"}");

        assertSuccessContaining(response, "District Hospital");
        assertEquals("Main Road", stored.getAddress());
        assertEquals("asha@example.org", stored.getContactPerson1_Email());
        assertEquals("https://example.org", stored.getWebsite());
    }

    @Test
    @DisplayName("editInstution should answer an error envelope for an institution that does not exist")
    void editInstution_shouldAnswerErrorEnvelopeForUnknownInstitution() {
        when(m_InstitutionInter.editInstution(31)).thenReturn(null);

        assertCodeException(controller.editInstution("{\"institutionID\":31}"));
    }

    @Test
    @DisplayName("deleteInstution should mark the institution deleted")
    void deleteInstution_shouldMarkInstitutionDeleted() {
        M_Institution stored = institution(31, "District Hospital");
        when(m_InstitutionInter.editInstution(31)).thenReturn(stored);
        when(m_InstitutionInter.saveEditData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.deleteInstution("{\"institutionID\":31,\"deleted\":true}"),
                "District Hospital");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteInstution should answer an error envelope for an institution that does not exist")
    void deleteInstution_shouldAnswerErrorEnvelopeForUnknownInstitution() {
        when(m_InstitutionInter.editInstution(31)).thenReturn(null);

        assertCodeException(controller.deleteInstution("{\"institutionID\":31,\"deleted\":true}"));
    }

    @Test
    @DisplayName("createInstutionByVillage should answer the institutions the service stored")
    void createInstutionByVillage_shouldAnswerStoredInstitutions() {
        ArrayList<M_Institution> stored = new ArrayList<>(List.of(institution(31, "Sub Centre")));
        when(m_InstitutionInter.createInstutionByVillage(anyList())).thenReturn(stored);

        assertSuccessContaining(controller.createInstutionByVillage("[{\"institutionName\":\"Sub Centre\"}]"),
                "Sub Centre");
    }

    @Test
    @DisplayName("createInstutionByVillage should answer an error envelope when the store fails")
    void createInstutionByVillage_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutionInter.createInstutionByVillage(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createInstutionByVillage("[{\"institutionName\":\"Sub Centre\"}]"));
    }

    @Test
    @DisplayName("getInstutionByVillage should answer the institutions under the village")
    void getInstutionByVillage_shouldAnswerInstitutionsUnderVillage() {
        ArrayList<M_Institution> stored = new ArrayList<>(List.of(institution(31, "Sub Centre")));
        when(m_InstitutionInter.getInstutionByVillage(4001, 29, 301, 401, 501)).thenReturn(stored);

        assertSuccessContaining(controller.getInstutionByVillage("{\"providerServiceMapID\":4001,\"stateID\":29,"
                + "\"districtID\":301,\"blockID\":401,\"villageID\":501}"), "Sub Centre");
    }

    @Test
    @DisplayName("getInstutionByVillage should answer an error envelope when the lookup fails")
    void getInstutionByVillage_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutionInter.getInstutionByVillage(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getInstutionByVillage("{\"providerServiceMapID\":4001}"));
    }

    @Test
    @DisplayName("createInstitutionByFile should answer the summary the service reports for the upload")
    void createInstitutionByFile_shouldAnswerUploadSummary() {
        when(m_InstitutionInter.createInstitutionByFile(any(JsonObject.class))).thenReturn("12 institutions created");

        assertSuccessContaining(controller.createInstitutionByFile("{\"fileName\":\"institutions.xlsx\"}"),
                "12 institutions created");
    }

    @Test
    @DisplayName("createInstitutionByFile should answer an error envelope for a payload that is not an object")
    void createInstitutionByFile_shouldAnswerErrorEnvelopeForNonObjectPayload() {
        assertGenericFailure(controller.createInstitutionByFile("\"just a string\""));
    }

    @Test
    @DisplayName("getInstuteSubDirectory should answer the sub-directories under the directory")
    void getInstuteSubDirectory_shouldAnswerSubDirectories() {
        ArrayList<M_Institutesubdirectory> stored = new ArrayList<>(List.of(subDirectory(41, "Government")));
        when(m_InstitutesubdirectoryInter.getInstutesubDirectory(11, 4001)).thenReturn(stored);

        assertSuccessContaining(
                controller.getInstuteSubDirectory("{\"instituteDirectoryID\":11,\"providerServiceMapId\":4001}"),
                "Government");
    }

    @Test
    @DisplayName("getInstuteSubDirectory should answer an error envelope when the lookup fails")
    void getInstuteSubDirectory_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutesubdirectoryInter.getInstutesubDirectory(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getInstuteSubDirectory("{\"instituteDirectoryID\":11}"));
    }

    @Test
    @DisplayName("createInstuteSubDirectory should answer the sub-directories the service stored")
    void createInstuteSubDirectory_shouldAnswerStoredSubDirectories() {
        ArrayList<M_Institutesubdirectory> stored = new ArrayList<>(List.of(subDirectory(41, "Government")));
        when(m_InstitutesubdirectoryInter.CreateInstutesubDirectory(anyList())).thenReturn(stored);

        assertSuccessContaining(
                controller.createInstuteSubDirectory("[{\"instituteSubDirectoryName\":\"Government\"}]"),
                "Government");
    }

    @Test
    @DisplayName("createInstuteSubDirectory should answer an error envelope when the store fails")
    void createInstuteSubDirectory_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutesubdirectoryInter.CreateInstutesubDirectory(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createInstuteSubDirectory("[{\"instituteSubDirectoryName\":\"Government\"}]"));
    }

    @Test
    @DisplayName("editInstuteSubDirectory should copy the edited fields onto the stored sub-directory")
    void editInstuteSubDirectory_shouldCopyEditedFields() {
        M_Institutesubdirectory stored = subDirectory(41, "old name");
        when(m_InstitutesubdirectoryInter.editInstutesubDirectory(41)).thenReturn(stored);
        when(m_InstitutesubdirectoryInter.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(controller.editInstuteSubDirectory("{\"instituteSubDirectoryID\":41,"
                + "\"instituteSubDirectoryName\":\"Government\",\"instituteSubDirectoryDesc\":\"State run\","
                + "\"modifiedBy\":\"admin\"}"), "Government");
        assertEquals("State run", stored.getInstituteSubDirectoryDesc());
    }

    @Test
    @DisplayName("editInstuteSubDirectory should answer an error envelope for a sub-directory that does not exist")
    void editInstuteSubDirectory_shouldAnswerErrorEnvelopeForUnknownSubDirectory() {
        when(m_InstitutesubdirectoryInter.editInstutesubDirectory(41)).thenReturn(null);

        assertCodeException(controller.editInstuteSubDirectory("{\"instituteSubDirectoryID\":41}"));
    }

    @Test
    @DisplayName("deleteInstuteSubDirectory should mark the sub-directory deleted")
    void deleteInstuteSubDirectory_shouldMarkSubDirectoryDeleted() {
        M_Institutesubdirectory stored = subDirectory(41, "Government");
        when(m_InstitutesubdirectoryInter.editInstutesubDirectory(41)).thenReturn(stored);
        when(m_InstitutesubdirectoryInter.saveEditedData(stored)).thenReturn(stored);

        assertSuccessContaining(
                controller.deleteInstuteSubDirectory("{\"instituteSubDirectoryID\":41,\"deleted\":true}"),
                "Government");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteInstuteSubDirectory should answer an error envelope for a sub-directory that does not exist")
    void deleteInstuteSubDirectory_shouldAnswerErrorEnvelopeForUnknownSubDirectory() {
        when(m_InstitutesubdirectoryInter.editInstutesubDirectory(41)).thenReturn(null);

        assertCodeException(controller.deleteInstuteSubDirectory("{\"instituteSubDirectoryID\":41,\"deleted\":true}"));
    }

    @Test
    @DisplayName("createInstuteSubDirectoryMaping should answer the mappings the service stored")
    void createInstuteSubDirectoryMaping_shouldAnswerStoredMappings() {
        ArrayList<M_Institutedirectorymapping> stored =
                new ArrayList<>(List.of(directoryMapping(51, 31)));
        when(m_InstitutedirectorymappingInter.createInstituteDirectoryData(anyList())).thenReturn(stored);

        assertSuccessContaining(
                controller.createInstuteSubDirectoryMaping("[{\"institutionID\":31,\"instituteDirectoryID\":11}]"),
                "51");
    }

    @Test
    @DisplayName("createInstuteSubDirectoryMaping should answer an error envelope when the store fails")
    void createInstuteSubDirectoryMaping_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutedirectorymappingInter.createInstituteDirectoryData(anyList()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.createInstuteSubDirectoryMaping("[{\"institutionID\":31}]"));
    }

    @Test
    @DisplayName("deleteInstuteSubDirectoryMaping should mark the mapping deleted")
    void deleteInstuteSubDirectoryMaping_shouldMarkMappingDeleted() {
        M_Institutedirectorymapping stored = directoryMapping(51, 31);
        when(m_InstitutedirectorymappingInter.deleteInstituteDirectoryData(51)).thenReturn(stored);
        when(m_InstitutedirectorymappingInter.setdeletedData(stored)).thenReturn(stored);

        assertSuccessContaining(
                controller.deleteInstuteSubDirectoryMaping("{\"instituteDirMapID\":51,\"deleted\":true}"),
                "51");
        assertTrue(stored.getDeleted());
    }

    @Test
    @DisplayName("deleteInstuteSubDirectoryMaping should answer an error envelope for a mapping that does not exist")
    void deleteInstuteSubDirectoryMaping_shouldAnswerErrorEnvelopeForUnknownMapping() {
        when(m_InstitutedirectorymappingInter.deleteInstituteDirectoryData(51)).thenReturn(null);

        assertCodeException(
                controller.deleteInstuteSubDirectoryMaping("{\"instituteDirMapID\":51,\"deleted\":true}"));
    }

    @Test
    @DisplayName("getInstuteSubDirectoryMaping should answer the mappings under the sub-directory")
    void getInstuteSubDirectoryMaping_shouldAnswerMappings() {
        ArrayList<M_Institutedirectorymapping> stored =
                new ArrayList<>(List.of(directoryMapping(51, 31)));
        when(m_InstitutedirectorymappingInter.getInstituteDirectoryData(41)).thenReturn(stored);

        assertSuccessContaining(controller.getInstuteSubDirectoryMaping("{\"instituteSubDirectoryID\":41}"), "51");
    }

    @Test
    @DisplayName("getInstuteSubDirectoryMaping should answer an error envelope when the lookup fails")
    void getInstuteSubDirectoryMaping_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_InstitutedirectorymappingInter.getInstituteDirectoryData(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getInstuteSubDirectoryMaping("{\"instituteSubDirectoryID\":41}"));
    }
}
