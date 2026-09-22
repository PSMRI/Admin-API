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
package com.iemr.admin.data;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises every data carrier the service exchanges - the JPA entities, the
 * transfer objects and the small model holders - against the accessor, equality
 * and string contract their callers rely on.
 */
@DisplayName("Data carrier contract Test Suite")
class DataCarrierContractTest {

    private static final String[] CARRIER_PACKAGES = {
            "com.iemr.admin.data",
            "com.iemr.admin.to",
            "com.iemr.admin.model" };

    static List<Class<?>> carriers() {
        return ClassScanner.classesUnder(CARRIER_PACKAGES).stream()
                .filter(type -> !type.getName().endsWith("Test"))
                .filter(type -> !type.getSimpleName().equals("BeanContract"))
                .filter(type -> !type.getSimpleName().equals("ClassScanner"))
                .filter(BeanContract::isVerifiable)
                .toList();
    }

    @Test
    @DisplayName("the scan should discover the carriers rather than silently pass on an empty set")
    void carrierScan_shouldDiscoverCarriers() {
        List<Class<?>> carriers = carriers();
        assertFalse(carriers.isEmpty(), "no data carriers were discovered on the test classpath");
        assertTrue(carriers.size() > 100,
                "expected the full carrier set, found only " + carriers.size());
    }

    @ParameterizedTest(name = "{0} honours the accessor, equality and string contract")
    @DisplayName("every data carrier should honour its accessor, equality and string contract")
    @MethodSource("carriers")
    void dataCarrier_shouldHonourBeanContract(Class<?> type) throws Exception {
        BeanContract.verify(type);
    }
}
