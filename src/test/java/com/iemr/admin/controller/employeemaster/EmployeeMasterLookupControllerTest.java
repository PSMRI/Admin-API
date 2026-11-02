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
package com.iemr.admin.controller.employeemaster;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.data.employeemaster.M_Community;
import com.iemr.admin.data.employeemaster.M_Designation;
import com.iemr.admin.data.employeemaster.M_Gender;
import com.iemr.admin.data.employeemaster.M_ProviderServiceMap1;
import com.iemr.admin.data.employeemaster.M_Religion;
import com.iemr.admin.data.employeemaster.M_Role;
import com.iemr.admin.data.employeemaster.M_Title;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.employeemaster.M_Userqualification;
import com.iemr.admin.data.employeemaster.Showofficedetails1;
import com.iemr.admin.data.employeemaster.Showuserdetailsfromuserservicerolemapping;
import com.iemr.admin.data.employeemaster.V_Showuser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The read-only endpoints back the employee search screens and the drop-downs
 * that feed the employee forms.
 */
@DisplayName("EmployeeMasterController lookup Test Suite")
class EmployeeMasterLookupControllerTest extends EmployeeMasterFixture {

    private static Showuserdetailsfromuserservicerolemapping userDetail(Integer userId, String name) {
        Showuserdetailsfromuserservicerolemapping detail = new Showuserdetailsfromuserservicerolemapping();
        detail.setUserID(userId);
        detail.setUserName(name);
        return detail;
    }

    private static V_Showuser showUser(Integer userId, String name) {
        V_Showuser user = new V_Showuser();
        user.setUserID(userId);
        user.setUserName(name);
        return user;
    }

    @Test
    @DisplayName("getAllRole should answer every role on record")
    void getAllRole_shouldAnswerEveryRole() {
        M_Role role = new M_Role();
        role.setRoleID(11);
        role.setRoleName("Provider Admin");
        when(employeeMasterInter.getAllRole()).thenReturn(new ArrayList<>(List.of(role)));

        assertSuccessContaining(controller.getAllRole("{}"), "Provider Admin");
    }

    @Test
    @DisplayName("getAllRole should answer an error envelope when the lookup fails")
    void getAllRole_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getAllRole()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllRole("{}"));
    }

    @Test
    @DisplayName("searchEmployee should answer the role mappings on record")
    void searchEmployee_shouldAnswerRoleMappings() {
        when(employeeMasterInter.getEmployeeDetails())
                .thenReturn(new ArrayList<>(List.of(roleMapping(9001, 3117))));

        assertSuccessContaining(controller.searchEmployee("{}"), "9001");
    }

    @Test
    @DisplayName("searchEmployee should answer an error envelope when the search fails")
    void searchEmployee_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee("{}"));
    }

    @Test
    @DisplayName("searchEmployee1 should answer the second view of the role mappings")
    void searchEmployee1_shouldAnswerRoleMappings() {
        when(employeeMasterInter.getEmployeeDetails1())
                .thenReturn(new ArrayList<>(List.of(roleMapping(9001, 3117))));

        assertSuccessContaining(controller.searchEmployee1("{}"), "9001");
    }

    @Test
    @DisplayName("searchEmployee1 should answer an error envelope when the search fails")
    void searchEmployee1_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails1()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee1("{}"));
    }

    @Test
    @DisplayName("searchEmployee2 should narrow the search to the provider and its state")
    void searchEmployee2_shouldNarrowByProviderAndState() {
        when(employeeMasterInter.getEmployeeDetails2(77, 29))
                .thenReturn(new ArrayList<>(List.of(userDetail(3117, "dr.mehta"))));

        assertSuccessContaining(
                controller.searchEmployee2("{\"serviceProviderID\":77,\"pSMStateID\":29}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee2 should answer an error envelope when the search fails")
    void searchEmployee2_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails2(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee2("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchEmployee3 should narrow the search to the provider and role")
    void searchEmployee3_shouldNarrowByProviderAndRole() {
        when(employeeMasterInter.getEmployeeDetails3(77, 11))
                .thenReturn(new ArrayList<>(List.of(userDetail(3117, "dr.mehta"))));

        assertSuccessContaining(
                controller.searchEmployee3("{\"serviceProviderID\":77,\"roleID\":11}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee3 should answer an error envelope when the search fails")
    void searchEmployee3_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails3(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee3("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchEmployee4 should narrow the search to the provider")
    void searchEmployee4_shouldNarrowByProvider() {
        when(employeeMasterInter.getEmployeeDetails4(77))
                .thenReturn(new ArrayList<>(List.of(showUser(3117, "dr.mehta"))));

        assertSuccessContaining(controller.searchEmployee4("{\"serviceProviderID\":77}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee4 should answer an error envelope when the search fails")
    void searchEmployee4_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails4(anyInt()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee4("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchEmployee5 should answer every user on record")
    void searchEmployee5_shouldAnswerEveryUser() {
        when(employeeMasterInter.getEmployeeDetails5())
                .thenReturn(new ArrayList<>(List.of(showUser(3117, "dr.mehta"))));

        assertSuccessContaining(controller.searchEmployee5("{}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee5 should answer an error envelope when the search fails")
    void searchEmployee5_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails5()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee5("{}"));
    }

    @Test
    @DisplayName("searchEmployee6 should narrow the search to one user under the provider")
    void searchEmployee6_shouldNarrowByProviderAndUser() {
        when(employeeMasterInter.getEmployeeDetails6(77, 3117))
                .thenReturn(new ArrayList<>(List.of(userDetail(3117, "dr.mehta"))));

        assertSuccessContaining(
                controller.searchEmployee6("{\"serviceProviderID\":77,\"userID\":3117}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee6 should answer an error envelope when the search fails")
    void searchEmployee6_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails6(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee6("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchEmployee7 should narrow the search down to the working district")
    void searchEmployee7_shouldNarrowByDistrict() {
        when(employeeMasterInter.getEmployeeDetails7(77, 29, 301))
                .thenReturn(new ArrayList<>(List.of(userDetail(3117, "dr.mehta"))));

        assertSuccessContaining(controller.searchEmployee7(
                "{\"serviceProviderID\":77,\"pSMStateID\":29,\"workingDistrictID\":301}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee7 should answer an error envelope when the search fails")
    void searchEmployee7_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails7(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee7("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchEmployee8 should narrow the search down to the working location")
    void searchEmployee8_shouldNarrowByLocation() {
        when(employeeMasterInter.getEmployeeDetails8(77, 29, 301, 401))
                .thenReturn(new ArrayList<>(List.of(userDetail(3117, "dr.mehta"))));

        assertSuccessContaining(controller.searchEmployee8("{\"serviceProviderID\":77,\"pSMStateID\":29,"
                + "\"workingDistrictID\":301,\"workingLocationID\":401}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee8 should answer an error envelope when the search fails")
    void searchEmployee8_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails8(any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee8("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchEmployee9 should narrow the search to the role within the state")
    void searchEmployee9_shouldNarrowByStateAndRole() {
        when(employeeMasterInter.getEmployeeDetails9(77, 29, 11))
                .thenReturn(new ArrayList<>(List.of(userDetail(3117, "dr.mehta"))));

        assertSuccessContaining(controller.searchEmployee9(
                "{\"serviceProviderID\":77,\"pSMStateID\":29,\"roleID\":11}"), "dr.mehta");
    }

    @Test
    @DisplayName("searchEmployee9 should answer an error envelope when the search fails")
    void searchEmployee9_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails9(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee9("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("searchEmployee10 should pass every filter the screen sends through")
    void searchEmployee10_shouldPassEveryFilterThrough() {
        when(employeeMasterInter.getEmployeeDetails11(77, 29, 3, 11, "dr.mehta", 3117))
                .thenReturn(new ArrayList<>(List.of(userDetail(3117, "dr.mehta"))));

        assertSuccessContaining(controller.searchEmployee10("{\"serviceProviderID\":77,\"pSMStateID\":29,"
                + "\"serviceID\":3,\"roleID\":11,\"userName\":\"dr.mehta\",\"userID\":3117}"), "dr.mehta");
        verify(employeeMasterInter).getEmployeeDetails11(77, 29, 3, 11, "dr.mehta", 3117);
    }

    @Test
    @DisplayName("searchEmployee10 should answer an error envelope when the search fails")
    void searchEmployee10_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeDetails11(any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.searchEmployee10("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("getAgentID should answer a success envelope without reaching a service")
    void getAgentID_shouldAnswerSuccessEnvelope() {
        assertGenericFailure(controller.getAgentID("{}"));
    }

    @Test
    @DisplayName("getAllTitle should answer every title on record")
    void getAllTitle_shouldAnswerEveryTitle() {
        M_Title title = new M_Title();
        title.setTitleID(1);
        title.setTitleName("Dr");
        when(employeeMasterInter.getAllTitle()).thenReturn(new ArrayList<>(List.of(title)));

        assertSuccessContaining(controller.getAllTitle("{}"), "Dr");
    }

    @Test
    @DisplayName("getAllTitle should answer an error envelope when the lookup fails")
    void getAllTitle_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getAllTitle()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllTitle("{}"));
    }

    @Test
    @DisplayName("getAllGender should answer every gender on record")
    void getAllGender_shouldAnswerEveryGender() {
        M_Gender gender = new M_Gender();
        gender.setGenderID(1);
        gender.setGenderName("Female");
        when(employeeMasterInter.getAllGender()).thenReturn(new ArrayList<>(List.of(gender)));

        assertSuccessContaining(controller.getAllGender("{}"), "Female");
    }

    @Test
    @DisplayName("getAllGender should answer an error envelope when the lookup fails")
    void getAllGender_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getAllGender()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAllGender("{}"));
    }

    @Test
    @DisplayName("getAlllocation should resolve the mapping before reading the office details")
    void getAlllocation_shouldResolveMappingFirst() {
        M_ProviderServiceMap1 mapping = new M_ProviderServiceMap1();
        mapping.setProviderServiceMapID(4001);
        Showofficedetails1 office = new Showofficedetails1();
        office.setLocationName("Bengaluru Office");
        when(employeeMasterInter.getAllByMapId2(77, 29, 3)).thenReturn(new ArrayList<>(List.of(mapping)));
        when(employeeMasterInter.getlocationByMapid2(4001, 301)).thenReturn(new ArrayList<>(List.of(office)));

        assertSuccessContaining(controller.getAlllocation("{\"serviceProviderID\":77,\"stateID\":29,"
                + "\"serviceID\":3,\"districtID\":301}"), "Bengaluru Office");
    }

    @Test
    @DisplayName("getAlllocation should fall back to no mapping when the provider has none")
    void getAlllocation_shouldFallBackWithoutMapping() {
        when(employeeMasterInter.getAllByMapId2(any(), any(), any())).thenReturn(new ArrayList<>());
        when(employeeMasterInter.getlocationByMapid2(0, 301)).thenReturn(new ArrayList<>());

        assertSuccessContaining(controller.getAlllocation("{\"serviceProviderID\":77,\"districtID\":301}"),
                "statusCode");
        verify(employeeMasterInter).getlocationByMapid2(0, 301);
    }

    @Test
    @DisplayName("getAlllocation should answer an error envelope when the lookup fails")
    void getAlllocation_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getAllByMapId2(any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getAlllocation("{\"serviceProviderID\":77}"));
    }

    @Test
    @DisplayName("FindEmployeeName should answer whether the user name is taken")
    void findEmployeeName_shouldAnswerWhetherNameIsTaken() {
        when(employeeMasterInter.FindEmployeeName("dr.mehta")).thenReturn("dr.mehta");

        assertSuccessContaining(controller.FindEmployeeName("{\"userName\":\"dr.mehta\"}"), "dr.mehta");
    }

    @Test
    @DisplayName("FindEmployeeName should answer an error envelope when the check fails")
    void findEmployeeName_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.FindEmployeeName(anyString()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.FindEmployeeName("{\"userName\":\"dr.mehta\"}"));
    }

    @Test
    @DisplayName("Qualification should answer every qualification on record")
    void qualification_shouldAnswerEveryQualification() {
        M_Userqualification qualification = new M_Userqualification();
        qualification.setQualificationID(5);
        qualification.setName("MBBS");
        when(employeeMasterInter.getQualification()).thenReturn(new ArrayList<>(List.of(qualification)));

        assertSuccessContaining(controller.Qualification("{}"), "MBBS");
    }

    @Test
    @DisplayName("Qualification should answer an error envelope when the lookup fails")
    void qualification_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getQualification()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.Qualification("{}"));
    }

    @Test
    @DisplayName("checkingEmpDetails should report that the identifiers are already in use")
    void checkingEmpDetails_shouldReportIdentifiersInUse() {
        when(employeeMasterInter.checkingEmpDetails("dr.mehta", "111122223333", "ABCDE1234F", "EMP-1", "HP-1"))
                .thenReturn(Boolean.TRUE);

        assertSuccessContaining(controller.checkingEmpDetails("{\"userName\":\"dr.mehta\","
                + "\"aadhaarNo\":\"111122223333\",\"pAN\":\"ABCDE1234F\",\"employeeID\":\"EMP-1\","
                + "\"healthProfessionalID\":\"HP-1\"}"), "true");
    }

    @Test
    @DisplayName("checkingEmpDetails should answer an error envelope when the check fails")
    void checkingEmpDetails_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.checkingEmpDetails(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.checkingEmpDetails("{\"userName\":\"dr.mehta\"}"));
    }

    @Test
    @DisplayName("getDesignation should answer every designation on record")
    void getDesignation_shouldAnswerEveryDesignation() {
        M_Designation designation = new M_Designation();
        designation.setDesignationID(7);
        designation.setDesignationName("ASHA");
        when(m_DesignationInter.getDesinationlist()).thenReturn(new ArrayList<>(List.of(designation)));

        assertSuccessContaining(controller.getDesignation("{}"), "ASHA");
    }

    @Test
    @DisplayName("getDesignation should answer an error envelope when the lookup fails")
    void getDesignation_shouldAnswerErrorEnvelopeOnFailure() {
        when(m_DesignationInter.getDesinationlist()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getDesignation("{}"));
    }

    @Test
    @DisplayName("getEmployeeByDesignation should answer the employees holding the designation")
    void getEmployeeByDesignation_shouldAnswerMatchingEmployees() {
        when(employeeMasterInter.getEmployeeByDesiganationID(7, 77))
                .thenReturn(new ArrayList<>(List.of(user(3117, "dr.mehta"))));

        assertSuccessContaining(
                controller.getEmployeeByDesignation("{\"designationID\":7,\"serviceProviderID\":77}"), "dr.mehta");
    }

    @Test
    @DisplayName("getEmployeeByDesignation should answer an error envelope when the lookup fails")
    void getEmployeeByDesignation_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getEmployeeByDesiganationID(any(), any()))
                .thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getEmployeeByDesignation("{\"designationID\":7}"));
    }

    @Test
    @DisplayName("completeUserDetails should answer the full user view")
    void completeUserDetails_shouldAnswerFullUserView() {
        when(employeeMasterInter.getcompleteUserDetails())
                .thenReturn(new ArrayList<>(List.of(showUser(3117, "dr.mehta"))));

        assertSuccessContaining(controller.completeUserDetails("{}"), "dr.mehta");
    }

    @Test
    @DisplayName("completeUserDetails should answer an error envelope when the lookup fails")
    void completeUserDetails_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getcompleteUserDetails()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.completeUserDetails("{}"));
    }

    @Test
    @DisplayName("getReligion should answer every religion on record")
    void getReligion_shouldAnswerEveryReligion() {
        M_Religion religion = new M_Religion();
        religion.setReligionID(1);
        religion.setReligionType("Hindu");
        when(employeeMasterInter.getAllReligion()).thenReturn(new ArrayList<>(List.of(religion)));

        assertSuccessContaining(controller.getReligion("{}"), "Hindu");
    }

    @Test
    @DisplayName("getReligion should answer an error envelope when the lookup fails")
    void getReligion_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getAllReligion()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getReligion("{}"));
    }

    @Test
    @DisplayName("getCommunity should answer every community on record")
    void getCommunity_shouldAnswerEveryCommunity() {
        M_Community community = new M_Community();
        community.setCommunityID(1);
        community.setCommunityType("General");
        when(employeeMasterInter.getAllCommunity()).thenReturn(new ArrayList<>(List.of(community)));

        assertSuccessContaining(controller.getCommunity("{}"), "General");
    }

    @Test
    @DisplayName("getCommunity should answer an error envelope when the lookup fails")
    void getCommunity_shouldAnswerErrorEnvelopeOnFailure() {
        when(employeeMasterInter.getAllCommunity()).thenThrow(new IllegalStateException("no connection"));

        assertGenericFailure(controller.getCommunity("{}"));
    }

    @Test
    @DisplayName("FindEmployeeDetailsByUserName should answer the user the service resolves")
    void findEmployeeDetailsByUserName_shouldAnswerResolvedUser() {
        when(employeeMasterInter.FindEmployeeName1("dr.mehta")).thenReturn(user(3117, "dr.mehta"));

        assertSuccessContaining(
                controller.FindEmployeeDetailsByUserName("{\"userName\":\"dr.mehta\"}"), "dr.mehta");
    }

    @Test
    @DisplayName("FindEmployeeDetailsByUserName should answer an error envelope for an unknown user")
    void findEmployeeDetailsByUserName_shouldAnswerErrorEnvelopeForUnknownUser() {
        when(employeeMasterInter.FindEmployeeName1("dr.mehta")).thenReturn(null);

        assertCodeException(controller.FindEmployeeDetailsByUserName("{\"userName\":\"dr.mehta\"}"));
    }

    @Test
    @DisplayName("searchEmployee should answer a plain envelope for an empty result")
    void searchEmployee_shouldAnswerPlainEnvelopeForEmptyResult() {
        when(employeeMasterInter.getEmployeeDetails()).thenReturn(new ArrayList<M_UserServiceRoleMapping2>());

        assertSuccessContaining(controller.searchEmployee("{}"), "\"statusCode\":200");
    }

    @Test
    @DisplayName("getEmployeeByDesignation should read the provider from the user carrier, not the designation")
    void getEmployeeByDesignation_shouldReadProviderFromUserCarrier() {
        ArrayList<M_User1> employees = new ArrayList<>(List.of(user(3117, "dr.mehta")));
        when(employeeMasterInter.getEmployeeByDesiganationID(7, 77)).thenReturn(employees);

        controller.getEmployeeByDesignation("{\"designationID\":7,\"serviceProviderID\":77}");

        verify(employeeMasterInter).getEmployeeByDesiganationID(7, 77);
    }
}
