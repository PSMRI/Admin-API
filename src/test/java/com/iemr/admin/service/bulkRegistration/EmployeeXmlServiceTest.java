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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.data.bulkuser.EmployeeList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Reads the uploaded spreadsheet, which reaches the service as XML. */
@DisplayName("EmployeeXmlService Test Suite")
class EmployeeXmlServiceTest {

    private final EmployeeXmlService service = new EmployeeXmlService();

    @Test
    @DisplayName("parseXml should read each employee row out of the uploaded document")
    void parseXml_shouldReadEachEmployeeRow() throws Exception {
        String xml = "<Employees><Employee><FirstName>Asha</FirstName><LastName>Rao</LastName>"
                + "<UserName>EMP-1</UserName></Employee>"
                + "<Employee><FirstName>Ravi</FirstName><LastName>Kumar</LastName>"
                + "<UserName>EMP-2</UserName></Employee></Employees>";

        EmployeeList list = service.parseXml(xml);

        assertEquals(2, list.getEmployees().size());
        assertEquals("Asha", list.getEmployees().get(0).getFirstName());
        assertEquals("EMP-2", list.getEmployees().get(1).getUserName());
    }

    @Test
    @DisplayName("parseXml should raise rather than answer a half-read document")
    void parseXml_shouldRaiseForMalformedDocument() {
        assertThrows(Exception.class, () -> service.parseXml("<Employees><Employee>"));
    }
}
