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

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;

import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_UserDemographics;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.service.employeemaster.EmployeeMasterInter;
import com.iemr.admin.service.employeemaster.M_DesignationInter;
import com.iemr.admin.service.employeemaster.USRAgentMappingService;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Shared mocks and helpers for the suites over the employee master controller. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
abstract class EmployeeMasterFixture {

    protected static final String AUTH_HEADER = "sess-0d5f3a7c";

    @Mock
    protected M_DesignationInter m_DesignationInter;

    @Mock
    protected EmployeeMasterInter employeeMasterInter;

    @Mock
    protected USRAgentMappingService usrAgentMappingService;

    @InjectMocks
    protected EmployeeMasterController controller;

    protected MockHttpServletRequest request;

    @BeforeEach
    void prepareRequest() {
        request = new MockHttpServletRequest();
        request.addHeader("Authorization", AUTH_HEADER);
    }

    protected static M_User1 user(Integer id, String userName) {
        M_User1 user = new M_User1();
        user.setUserID(id);
        user.setUserName(userName);
        return user;
    }

    protected static M_UserDemographics demographics(Integer userId, String fathersName) {
        M_UserDemographics demographics = new M_UserDemographics();
        demographics.setUserID(userId);
        demographics.setFathersName(fathersName);
        return demographics;
    }

    protected static M_UserServiceRoleMapping2 roleMapping(Integer id, Integer userId) {
        M_UserServiceRoleMapping2 mapping = new M_UserServiceRoleMapping2();
        mapping.setuSRMappingID(id);
        mapping.setUserID(userId);
        return mapping;
    }

    protected static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    protected static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    protected static void assertGenericFailure(String response) {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
    }

    /**
     * Asserts the envelope reports the failure the response builder maps an
     * {@link com.iemr.admin.utils.exception.IEMRException} onto.
     */
    protected static void assertIemrFailure(String response) {
        assertEquals(OutputResponse.USERID_FAILURE, statusCodeOf(response), response);
    }

    protected static void assertCodeException(String response) {
        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(response), response);
    }
}
