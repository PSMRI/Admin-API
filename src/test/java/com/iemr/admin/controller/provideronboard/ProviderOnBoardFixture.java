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

import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.service.provideronboard.Calltypeinter;
import com.iemr.admin.service.provideronboard.CategoryInter;
import com.iemr.admin.service.provideronboard.DrugMasterInter;
import com.iemr.admin.service.provideronboard.InstuteDirectoryInter;
import com.iemr.admin.service.provideronboard.M_FeedbacknatureInteger;
import com.iemr.admin.service.provideronboard.M_FeedbacktypeInter;
import com.iemr.admin.service.provideronboard.M_InstitutedirectorymappingInter;
import com.iemr.admin.service.provideronboard.M_InstitutesubdirectoryInter;
import com.iemr.admin.service.provideronboard.M_InstitutionInter;
import com.iemr.admin.service.provideronboard.M_InstitutiontypeInter;
import com.iemr.admin.service.provideronboard.M_ServiceMasterInter;
import com.iemr.admin.service.provideronboard.M_SeverityInter;
import com.iemr.admin.service.provideronboard.ServiceProvider_ServiceImpl;
import com.iemr.admin.service.provideronboard.SubServiceInter;
import com.iemr.admin.service.user.IemrUserServiceImpl;
import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The onboarding controller reaches thirteen collaborating services, so every
 * suite over it shares this fixture rather than re-declaring the same mocks.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
abstract class ProviderOnBoardFixture {

    @Mock
    protected M_InstitutedirectorymappingInter m_InstitutedirectorymappingInter;

    @Mock
    protected M_InstitutesubdirectoryInter m_InstitutesubdirectoryInter;

    @Mock
    protected M_InstitutionInter m_InstitutionInter;

    @Mock
    protected M_FeedbacknatureInteger m_FeedbacknatureInteger;

    @Mock
    protected M_InstitutiontypeInter m_InstitutiontypeInter;

    @Mock
    protected InstuteDirectoryInter instuteDirectoryInter;

    @Mock
    protected M_FeedbacktypeInter m_FeedbacktypeInter;

    @Mock
    protected M_SeverityInter m_ServerityInter;

    @Mock
    protected DrugMasterInter drugMasterInter;

    @Mock
    protected CategoryInter categoryInter;

    @Mock
    protected SubServiceInter subServiceInter;

    @Mock
    protected Calltypeinter calltypeinter;

    @Mock
    protected M_ServiceMasterInter m_ServiceMasterInter;

    @Mock
    protected ServiceProvider_ServiceImpl serviceProvider_ServiceImpl;

    @Mock
    protected IemrUserServiceImpl iemrUserServiceImpl;

    @InjectMocks
    protected ProviderOnBoardController controller;

    /** Reads the status code out of the JSON envelope the controller answers with. */
    protected static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    /** Asserts the envelope reports success and carries the given fragment. */
    protected static void assertSuccessContaining(String response, String fragment) {
        assertEquals(OutputResponse.SUCCESS, statusCodeOf(response), response);
        assertTrue(response.contains(fragment), response);
    }

    /** Asserts the envelope reports the generic failure the controllers fall back to. */
    protected static void assertGenericFailure(String response) {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(response), response);
    }

    /**
     * Asserts the envelope reports the code-level failure raised when a controller
     * works on a record the service could not resolve.
     */
    protected static void assertCodeException(String response) {
        assertEquals(OutputResponse.CODE_EXCEPTION, statusCodeOf(response), response);
    }
}
