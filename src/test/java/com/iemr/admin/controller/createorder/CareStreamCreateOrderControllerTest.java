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
package com.iemr.admin.controller.createorder;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.utils.response.OutputResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The imaging order screens talk to a radiology server over a hard-coded socket
 * address, so only the request reading that happens before the connection is
 * opened can be checked here; a request the screen cannot read is refused
 * without any connection being attempted at all.
 */
@DisplayName("CareStreamCreateOrderController Test Suite")
class CareStreamCreateOrderControllerTest {

    private static final String UNREADABLE_REQUEST = "{not json";

    private final CareStreamCreateOrderController controller = new CareStreamCreateOrderController();

    private static int statusCodeOf(String response) {
        return new JSONObject(response).getInt("statusCode");
    }

    @Test
    @DisplayName("createOrder should refuse a request it cannot read rather than reach the radiology server")
    void createOrder_shouldRefuseUnreadableRequest() throws Exception {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.createOrder(UNREADABLE_REQUEST)));
    }

    @Test
    @DisplayName("UpdateOrder should refuse a request it cannot read rather than reach the radiology server")
    void updateOrder_shouldRefuseUnreadableRequest() throws Exception {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.UpdateOrder(UNREADABLE_REQUEST)));
    }

    @Test
    @DisplayName("deleteOrder should refuse a request it cannot read rather than reach the radiology server")
    void deleteOrder_shouldRefuseUnreadableRequest() throws Exception {
        assertEquals(OutputResponse.GENERIC_FAILURE, statusCodeOf(controller.deleteOrder(UNREADABLE_REQUEST)));
    }
}
