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
package com.iemr.admin.utils.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The two mappers decide what leaves the service on the wire: the output mapper
 * publishes only the fields a carrier marks {@code @Expose}, while the input
 * mapper reads everything a caller sends.
 */
@DisplayName("Gson mapper Test Suite")
class GsonMappersTest {

    /** A carrier that exercises both the exposed and the hidden field handling. */
    private static final class Appointment {
        @Expose
        private String specialist;
        @Expose
        private Long appointmentId;
        private String internalNote;
    }

    @BeforeEach
    @DisplayName("Prime the shared output builder, which only the constructor creates")
    void primeOutputMapper() {
        new OutputMapper();
    }

    @Nested
    @DisplayName("OutputMapper")
    class OutputMapperTests {

        @Test
        @DisplayName("gson should publish the exposed fields and keep the hidden ones off the wire")
        void gson_shouldSerialiseOnlyExposedFields() {
            Appointment appointment = new Appointment();
            appointment.specialist = "dr.rao";
            appointment.internalNote = "not for the wire";

            String json = OutputMapper.gson().toJson(appointment);

            assertTrue(json.contains("\"specialist\":\"dr.rao\""), json);
            assertFalse(json.contains("internalNote"), "a field without @Expose must stay off the wire");
        }

        @Test
        @DisplayName("gson should render a long as a string so large ids survive a JavaScript caller")
        void gson_shouldRenderLongAsString() {
            Appointment appointment = new Appointment();
            appointment.appointmentId = 9_007_199_254_740_993L;

            assertTrue(OutputMapper.gson().toJson(appointment).contains("\"9007199254740993\""));
        }

        @Test
        @DisplayName("gson should serialise nulls rather than omit them")
        void gson_shouldSerialiseNulls() {
            assertTrue(OutputMapper.gson().toJson(new Appointment()).contains("null"));
        }

        @Test
        @DisplayName("gsonWithoutExpose should publish every field, annotated or not")
        void gsonWithoutExpose_shouldPublishEveryField() {
            Appointment appointment = new Appointment();
            appointment.specialist = "dr.rao";
            appointment.internalNote = "kept internally";

            String json = OutputMapper.gsonWithoutExpose().toJson(appointment);

            assertTrue(json.contains("internalNote"), json);
            assertTrue(json.contains("specialist"), json);
        }

        @Test
        @DisplayName("the constructor should reuse the shared builder across instances")
        void constructor_shouldReuseSharedBuilder() {
            new OutputMapper();

            assertNotNull(OutputMapper.gson());
        }
    }

    @Nested
    @DisplayName("InputMapper and OutputMapper together")
    class RoundTripTests {

        @Test
        @DisplayName("a carrier written by the output mapper should be readable by the input mapper")
        void carrier_shouldSurviveARoundTrip() {
            Appointment appointment = new Appointment();
            appointment.specialist = "dr.rao";
            appointment.internalNote = "not for the wire";
            Gson writer = OutputMapper.gson();

            Appointment restored = InputMapper.gson().fromJson(writer.toJson(appointment), Appointment.class);

            assertEquals("dr.rao", restored.specialist);
            assertNull(restored.internalNote, "a hidden field must not come back over the wire");
        }
    }
}
