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
package com.iemr.admin.service.bulkRegistration;

import java.util.ArrayList;
import java.util.List;

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

import com.iemr.admin.data.bulkuser.BulkRegistrationError;
import com.iemr.admin.data.bulkuser.Employee;
import com.iemr.admin.data.bulkuser.EmployeeList;
import com.iemr.admin.data.employeemaster.M_Community;
import com.iemr.admin.data.employeemaster.M_Gender;
import com.iemr.admin.data.employeemaster.M_Religion;
import com.iemr.admin.data.employeemaster.M_Title;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_Userqualification;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.rolemaster.StateMasterForRole;
import com.iemr.admin.repo.employeemaster.V_ShowuserRepo;
import com.iemr.admin.service.employeemaster.EmployeeMasterInter;
import com.iemr.admin.service.locationmaster.LocationMasterServiceInter;
import com.iemr.admin.service.rolemaster.Role_MasterInter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Bulk registration turns an uploaded spreadsheet into user records. A row that
 * fails validation must be reported rather than half-saved, so the error log is
 * as much a deliverable as the users it creates.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("BulkRegistrationServiceImpl Test Suite")
class BulkRegistrationServiceImplTest {

    /** Excel serial numbers, which is how the uploaded sheet carries its dates. */
    private static final String DOB_SERIAL = "30000";
    private static final String DOJ_SERIAL = "40000";

    @Mock
    private EmployeeMasterInter employeeMasterInter;

    @Mock
    private Role_MasterInter roleMasterInter;

    @Mock
    private V_ShowuserRepo showuserRepo;

    @Mock
    private LocationMasterServiceInter locationMasterServiceInter;

    @Mock
    private EmployeeXmlService employeeXmlService;

    @InjectMocks
    private BulkRegistrationServiceImpl service;

    private Employee employee;

    @BeforeEach
    void setUp() throws Exception {
        employee = validEmployee();

        M_Title title = new M_Title();
        title.setTitleID(1);
        title.setTitleName("Ms.");
        when(employeeMasterInter.getAllTitle()).thenReturn(new ArrayList<>(List.of(title)));

        M_Gender gender = new M_Gender();
        gender.setGenderID(2);
        gender.setGenderName("Female");
        when(employeeMasterInter.getAllGender()).thenReturn(new ArrayList<>(List.of(gender)));

        M_Userqualification qualification = new M_Userqualification();
        qualification.setQualificationID(5);
        qualification.setName("MBBS");
        when(employeeMasterInter.getQualification()).thenReturn(new ArrayList<>(List.of(qualification)));

        M_Community community = new M_Community();
        community.setCommunityID(3);
        community.setCommunityType("General");
        when(employeeMasterInter.getAllCommunity()).thenReturn(new ArrayList<>(List.of(community)));

        M_Religion religion = new M_Religion();
        religion.setReligionID(4);
        religion.setReligionType("Hindu");
        when(employeeMasterInter.getAllReligion()).thenReturn(new ArrayList<>(List.of(religion)));

        StateMasterForRole state = new StateMasterForRole();
        state.setStateID(29);
        state.setStateName("Karnataka");
        when(roleMasterInter.getAllState()).thenReturn(new ArrayList<>(List.of(state)));

        M_District district = new M_District();
        district.setDistrictID(301);
        district.setDistrictName("Bengaluru Urban");
        when(locationMasterServiceInter.getAllDistrictByStateId(29)).thenReturn(new ArrayList<>(List.of(district)));

        when(employeeMasterInter.FindEmployeeName(anyString())).thenReturn("usernotexist");
        when(employeeMasterInter.FindEmployeeContact(anyString())).thenReturn("contactnotexist");
        when(employeeMasterInter.FindEmployeeAadhaar(anyString())).thenReturn("aadhaarnotexist");

        M_User1 saved = new M_User1();
        saved.setUserID(3117);
        when(employeeMasterInter.saveBulkUserEmployee(any())).thenReturn(saved);
    }

    private static Employee validEmployee() {
        Employee employee = new Employee();
        employee.setTitle("Ms");
        employee.setFirstName("Asha");
        employee.setMiddleName("");
        employee.setLastName("Rao");
        employee.setGender("Female");
        employee.setContactNo("9000000001");
        employee.setDesignation("ASHA");
        employee.setEmergencyContactNo("9000000002");
        employee.setDob(DOB_SERIAL);
        employee.setEmail("asha.rao@example.org");
        employee.setAadhaarNo("111122223333");
        employee.setPan("ABCDEFGH123");
        employee.setQualification("MBBS");
        employee.setFatherName("Ravi");
        employee.setMotherName("Meera");
        employee.setCommunity("General");
        employee.setReligion("Hindu");
        employee.setAddressLine1("Main Road");
        employee.setState("Karnataka");
        employee.setDistrict("Bengaluru Urban");
        employee.setPincode("560001");
        employee.setPermanentAddressLine1("Main Road");
        employee.setPermanentState("Karnataka");
        employee.setPermanentDistrict("Bengaluru Urban");
        employee.setPermanentPincode("560001");
        employee.setDateOfJoining(DOJ_SERIAL);
        employee.setUserName("EMP-1");
        employee.setPassword("plain-secret");
        return employee;
    }

    private void uploadContains(Employee... employees) throws Exception {
        EmployeeList list = new EmployeeList();
        list.setEmployees(new ArrayList<>(List.of(employees)));
        when(employeeXmlService.parseXml(anyString())).thenReturn(list);
    }

    @Nested
    @DisplayName("registerBulkUser")
    class RegisterBulkUserTests {

        @Test
        @DisplayName("should register a row that passes every rule")
        void register_shouldRegisterValidRow() throws Exception {
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertEquals(1, service.totalEmployeeListSize);
            assertEquals(1, service.m_bulkUser.size());
            assertTrue(service.errorLogs.isEmpty(), service.errorLogs.toString());
            verify(employeeMasterInter).saveBulkUserEmployee(any());
            verify(employeeMasterInter).saveDemography(any());
        }

        @Test
        @DisplayName("should carry the uploaded details onto the user it stores")
        void register_shouldCarryDetailsOntoStoredUser() throws Exception {
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            M_User1 stored = service.m_bulkUser.get(0);
            assertEquals("Asha", stored.getFirstName());
            assertEquals("9000000001", stored.getUserName(), "the contact number is used as the sign-in name");
            assertEquals("EMP-1", stored.getEmployeeID());
            assertEquals(77, stored.getServiceProviderID());
            assertEquals(2, stored.getStatusID());
            assertFalse("plain-secret".equals(stored.getPassword()), "the password must be hashed before storing");
        }

        @Test
        @DisplayName("should report a row that names no user")
        void register_shouldReportRowWithoutUserName() throws Exception {
            employee.setUserName("");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertEquals(1, service.errorLogs.size());
            assertTrue(service.errorLogs.get(0).contains("Please Enter UserName"), service.errorLogs.toString());
            verify(employeeMasterInter, never()).saveBulkUserEmployee(any());
        }

        @Test
        @DisplayName("should report a row whose user name is already taken")
        void register_shouldReportExistingUser() throws Exception {
            when(employeeMasterInter.FindEmployeeName(anyString())).thenReturn("userexist");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertTrue(service.errorLogs.get(0).contains("User Already exist"), service.errorLogs.toString());
        }

        @Test
        @DisplayName("should report a row whose contact number is already taken")
        void register_shouldReportExistingContact() throws Exception {
            when(employeeMasterInter.FindEmployeeContact(anyString())).thenReturn("contactexist");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertTrue(service.errorLogs.get(0).contains("Contact No Already exist"), service.errorLogs.toString());
        }

        @Test
        @DisplayName("should report every rule a row breaks rather than only the first")
        void register_shouldReportEveryBrokenRule() throws Exception {
            employee.setTitle("");
            employee.setFirstName("");
            employee.setLastName("");
            employee.setEmail("not-an-email");
            employee.setContactNo("12345");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            String reported = service.errorLogs.get(0);
            assertTrue(reported.contains("Title is missing."), reported);
            assertTrue(reported.contains("First Name is missing."), reported);
            assertTrue(reported.contains("Last Name is missing."), reported);
            assertTrue(reported.contains("Invalid Email format."), reported);
            assertTrue(reported.contains("Contact Number is invalid"), reported);
        }

        @Test
        @DisplayName("should report a name that is a number rather than a name")
        void register_shouldReportNumericName() throws Exception {
            employee.setFirstName("12345");
            employee.setLastName("67890");
            employee.setMiddleName("42");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            String reported = service.errorLogs.get(0);
            assertTrue(reported.contains("First name is invalid."), reported);
            assertTrue(reported.contains("Last name is invalid."), reported);
            assertTrue(reported.contains("Middle name is invalid."), reported);
        }

        @Test
        @DisplayName("should report a name longer than the column can hold")
        void register_shouldReportOverlongName() throws Exception {
            String tooLong = "A".repeat(51);
            employee.setFirstName(tooLong);
            employee.setMiddleName(tooLong);
            employee.setLastName(tooLong);
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            String reported = service.errorLogs.get(0);
            assertTrue(reported.contains("First name is invalid."), reported);
            assertTrue(reported.contains("Middle name is invalid."), reported);
            assertTrue(reported.contains("Last name is invalid."), reported);
        }

        @Test
        @DisplayName("should report a title the master does not know")
        void register_shouldReportUnknownTitle() throws Exception {
            employee.setTitle("Archduke");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertTrue(service.errorLogs.get(0).contains("Title is invalid."), service.errorLogs.toString());
        }

        @Test
        @DisplayName("should report a district the master does not know")
        void register_shouldReportUnknownDistrict() throws Exception {
            employee.setDistrict("Nowhere");
            employee.setPermanentDistrict("Nowhere");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            String reported = service.errorLogs.get(0);
            assertTrue(reported.contains("Current District is invalid."), reported);
            assertTrue(reported.contains("Permanent District is invalid."), reported);
        }

        @Test
        @DisplayName("should abandon the upload when a row names a state the master does not know")
        void register_shouldAbandonUploadForUnknownState() throws Exception {
            employee.setState("Atlantis");
            employee.setPermanentState("Atlantis");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertEquals(List.of("Data is invalid or empty"), service.errorLogs,
                    "an unresolvable state leaves no districts to check against, so the upload is refused");
            assertTrue(service.m_bulkUser.isEmpty());
        }

        @Test
        @DisplayName("should report an Aadhaar number that is already on file")
        void register_shouldReportDuplicateAadhaar() throws Exception {
            when(employeeMasterInter.FindEmployeeAadhaar(anyString())).thenReturn("aadhaarexist");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertTrue(service.errorLogs.get(0).contains("Duplicate  aadhaar number found"),
                    service.errorLogs.toString());
        }

        @Test
        @DisplayName("should report an Aadhaar number that is not twelve digits")
        void register_shouldReportMalformedAadhaar() throws Exception {
            employee.setAadhaarNo("1234");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertTrue(service.errorLogs.get(0).contains("Aadhaar number is invalid"),
                    service.errorLogs.toString());
        }

        @Test
        @DisplayName("should report a date of birth in the future")
        void register_shouldReportFutureDateOfBirth() throws Exception {
            employee.setDob("50000");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertTrue(service.errorLogs.get(0).contains("Date of Birth is invalid."),
                    service.errorLogs.toString());
        }

        @Test
        @DisplayName("should report a row whose mandatory fields are simply blank")
        void register_shouldReportBlankMandatoryFields() throws Exception {
            employee.setGender("");
            employee.setContactNo("");
            employee.setDesignation("");
            employee.setEmergencyContactNo("");
            employee.setDob("");
            employee.setEmail("");
            employee.setPassword("");
            employee.setQualification("");
            employee.setState("");
            employee.setDistrict("");
            employee.setPermanentState("");
            employee.setPermanentDistrict("");
            employee.setDateOfJoining("");
            uploadContains(employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            String reported = service.errorLogs.get(0);
            assertTrue(reported.contains("Gender is missing"), reported);
            assertTrue(reported.contains("Contact number missing"), reported);
            assertTrue(reported.contains("Designation is missing"), reported);
            assertTrue(reported.contains("Emergency contact number is missing"), reported);
            assertTrue(reported.contains("Date of Birth is missing."), reported);
            assertTrue(reported.contains("Email is missing."), reported);
            assertTrue(reported.contains("Qualification is missing"), reported);
            assertTrue(reported.contains("Date of Joining is missing."), reported);
        }

        @Test
        @DisplayName("should report an upload that carries no rows at all")
        void register_shouldReportEmptyUpload() throws Exception {
            EmployeeList list = new EmployeeList();
            list.setEmployees(new ArrayList<>());
            when(employeeXmlService.parseXml(anyString())).thenReturn(list);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertEquals(List.of("Data is invalid or empty"), service.errorLogs);
        }

        @Test
        @DisplayName("should report an upload it cannot read at all")
        void register_shouldReportUnreadableUpload() throws Exception {
            when(employeeXmlService.parseXml(anyString())).thenThrow(new IllegalStateException("not xml"));

            service.registerBulkUser("not xml", "auth", "admin", 77);

            assertEquals(List.of("Data is invalid or empty"), service.errorLogs);
        }

        @Test
        @DisplayName("should keep going through the sheet after a row it cannot register")
        void register_shouldKeepGoingAfterABadRow() throws Exception {
            Employee bad = validEmployee();
            bad.setUserName("");
            uploadContains(bad, employee);

            service.registerBulkUser("<EmployeeList/>", "auth", "admin", 77);

            assertEquals(2, service.totalEmployeeListSize);
            assertEquals(1, service.m_bulkUser.size(), "the good row must still be registered");
            assertEquals(1, service.errorLogs.size());
        }
    }

    @Nested
    @DisplayName("Master lookups")
    class MasterLookupTests {

        @Test
        @DisplayName("getCommunityId should resolve a known community and answer zero for an unknown one")
        void getCommunityId_shouldResolveKnownCommunity() {
            assertEquals(3, service.getCommunityId("General"));
            assertEquals(0, service.getCommunityId("Unknown"));
        }

        @Test
        @DisplayName("getQualificationId should resolve a known qualification and answer zero for an unknown one")
        void getQualificationId_shouldResolveKnownQualification() {
            assertEquals(5, service.getQualificationId("MBBS"));
            assertEquals(0, service.getQualificationId("Unknown"));
        }

        @Test
        @DisplayName("getReligionStringId should resolve a known religion")
        void getReligionStringId_shouldResolveKnownReligion() {
            assertEquals(4, service.getReligionStringId("Hindu"));
            assertEquals(0, service.getReligionStringId("Unknown"));
        }

        @Test
        @DisplayName("getReligionStringId should treat an unstated religion as none")
        void getReligionStringId_shouldTreatUnstatedAsNone() {
            assertEquals(0, service.getReligionStringId("Not given"));
            verify(employeeMasterInter, never()).getAllReligion();
        }

        @Test
        @DisplayName("getStateId should resolve a known state and load its districts")
        void getStateId_shouldResolveKnownStateAndLoadDistricts() {
            assertEquals(29, service.getStateId("Karnataka"));
            verify(locationMasterServiceInter).getAllDistrictByStateId(29);
        }

        @Test
        @DisplayName("getStateId should answer zero for a state the master does not know")
        void getStateId_shouldAnswerZeroForUnknownState() {
            assertEquals(0, service.getStateId("Atlantis"));
            verify(locationMasterServiceInter, never()).getAllDistrictByStateId(29);
        }

        @Test
        @DisplayName("getDistrictId should resolve a district once its state has been resolved")
        void getDistrictId_shouldResolveDistrictAfterState() {
            service.getStateId("Karnataka");

            assertEquals(301, service.getDistrictId("Bengaluru Urban"));
            assertEquals(0, service.getDistrictId("Nowhere"));
        }

        @Test
        @DisplayName("getDistrictId should answer zero when no district name is given")
        void getDistrictId_shouldAnswerZeroWithoutAName() {
            assertEquals(0, service.getDistrictId(""));
        }

        @Test
        @DisplayName("getAllState should hand back what the role master holds")
        void getAllState_shouldHandBackRoleMasterContents() {
            assertEquals(1, service.getAllState().size());
        }

        @Test
        @DisplayName("getDesignationId should answer the fixed designation the upload uses")
        void getDesignationId_shouldAnswerFixedDesignation() {
            assertEquals(20, service.getDesignationId("ASHA"));
        }
    }

    @Nested
    @DisplayName("Helpers")
    class HelperTests {

        @Test
        @DisplayName("escapeXmlSpecialChars should escape a bare ampersand and leave real entities alone")
        void escape_shouldEscapeBareAmpersandOnly() {
            assertEquals("Ram &amp; Co", BulkRegistrationServiceImpl.escapeXmlSpecialChars("Ram & Co"));
            assertEquals("Ram &amp; Co", BulkRegistrationServiceImpl.escapeXmlSpecialChars("Ram &amp; Co"));
            assertEquals("&lt;tag&gt;", BulkRegistrationServiceImpl.escapeXmlSpecialChars("&lt;tag&gt;"));
        }

        @Test
        @DisplayName("isNumeric should tell a number apart from a name")
        void isNumeric_shouldTellNumberFromName() {
            assertTrue(BulkRegistrationServiceImpl.isNumeric("12345"));
            assertFalse(BulkRegistrationServiceImpl.isNumeric("Asha"));
        }

        @Test
        @DisplayName("isValidAadhar should report anything that is not twelve digits")
        void isValidAadhar_shouldReportNonTwelveDigitNumbers() {
            assertFalse(BulkRegistrationServiceImpl.isValidAadhar("111122223333"));
            assertTrue(BulkRegistrationServiceImpl.isValidAadhar("1234"));
            assertTrue(BulkRegistrationServiceImpl.isValidAadhar("not-a-number"));
        }

        @Test
        @DisplayName("convertStringIntoDate should read the spreadsheet's own date serial")
        void convertStringIntoDate_shouldReadExcelSerial() {
            assertEquals("1982-02-18", BulkRegistrationServiceImpl.convertStringIntoDate(DOB_SERIAL).toString());
        }

        @Test
        @DisplayName("generateStrongPassword should answer a different hash each time it is called")
        void generateStrongPassword_shouldSaltEachHash() throws Exception {
            String first = service.generateStrongPassword("plain-secret");
            String second = service.generateStrongPassword("plain-secret");

            assertTrue(first.startsWith("1001:"), first);
            assertFalse(first.equals(second), "each hash must carry its own salt");
        }

        @Test
        @DisplayName("insertErrorLog should write one workbook row per reported row")
        void insertErrorLog_shouldWriteOneRowPerReportedRow() {
            BulkRegistrationError error = new BulkRegistrationError();
            error.setRowNumber(1);
            error.setUserName("EMP-1");
            error.setError(List.of("Title is missing."));
            service.bulkRegistrationErrors.add(error);

            byte[] workbook = service.insertErrorLog();

            assertNotNull(workbook);
            assertTrue(workbook.length > 0, "a workbook with a reported row must not be empty");
        }

        @Test
        @DisplayName("insertErrorLog should still answer a workbook when nothing was reported")
        void insertErrorLog_shouldAnswerWorkbookWithoutErrors() {
            assertTrue(service.insertErrorLog().length > 0);
        }
    }
}
