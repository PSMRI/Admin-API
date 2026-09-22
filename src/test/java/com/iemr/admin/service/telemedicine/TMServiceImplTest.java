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
package com.iemr.admin.service.telemedicine;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.admin.data.telemedicine.M_UserTemp;
import com.iemr.admin.data.telemedicine.Specialization;
import com.iemr.admin.data.telemedicine.TMinput;
import com.iemr.admin.data.telemedicine.UserSpecializationMapping;
import com.iemr.admin.repo.telemedicine.SpecializationRepo;
import com.iemr.admin.repo.telemedicine.UserRepo;
import com.iemr.admin.repo.telemedicine.UserSpecializationMappingRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * The telemedicine service rebuilds the specialist roster out of the wide user
 * row the reporting query answers, and records which specialities each holds.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TMServiceImpl Test Suite")
class TMServiceImplTest {

    private static final Integer PROVIDER_ID = 5;
    private static final String SCREEN = "TM";

    @Mock
    private UserRepo userRepo;

    @Mock
    private SpecializationRepo specializationRepo;

    @Mock
    private UserSpecializationMappingRepo userSpecializationMappingRepo;

    @InjectMocks
    private TMServiceImpl service;

    private static TMinput request() {
        TMinput input = new TMinput();
        input.setServiceproviderID(PROVIDER_ID);
        input.setScreenName(SCREEN);
        return input;
    }

    /** The reporting query answers the whole user row; only some columns are read. */
    private static Object[] userRow() {
        Object[] row = new Object[33];
        row[0] = 3117;
        row[2] = "Asha";
        row[4] = "Rao";
        row[12] = 7;
        row[14] = "asha.rao";
        row[22] = "asha.rao@example.org";
        row[24] = (short) 5;
        row[32] = false;
        return row;
    }

    @Test
    @DisplayName("getUser should rebuild one specialist per row the query answers")
    void getUser_shouldRebuildEachRow() {
        when(userRepo.getUserTM(PROVIDER_ID, SCREEN)).thenReturn(new ArrayList<>(List.<Object[]>of(userRow())));

        ArrayList<M_UserTemp> users = service.getUser(request());

        assertEquals(1, users.size());
        M_UserTemp user = users.get(0);
        assertEquals(3117L, user.getUserID());
        assertEquals("Asha", user.getFirstName());
        assertEquals("Rao", user.getLastName());
        assertEquals("asha.rao", user.getUserName());
        assertEquals("asha.rao@example.org", user.getEmailID());
        assertEquals(7, user.getDesignationID());
        assertEquals(PROVIDER_ID, user.getServiceProviderID());
        assertEquals(false, user.getDeleted());
        assertNull(user.getDesignation(), "the designation record must not travel with the roster row");
    }

    @Test
    @DisplayName("getUser should answer nothing when the provider has no specialist on that screen")
    void getUser_shouldAnswerNothingForProviderWithoutSpecialists() {
        when(userRepo.getUserTM(PROVIDER_ID, SCREEN)).thenReturn(new ArrayList<>());

        assertTrue(service.getUser(request()).isEmpty());
    }

    @Test
    @DisplayName("getSpecialization should answer only the specialities still in use")
    void getSpecialization_shouldAnswerLiveSpecialities() {
        ArrayList<Specialization> live = new ArrayList<>(List.of(new Specialization()));
        when(specializationRepo.findByDeleted(false)).thenReturn(live);

        assertSame(live, service.getSpecialization());
    }

    @Test
    @DisplayName("getUserSpecialization should answer the specialities held under the provider asked for")
    void getUserSpecialization_shouldAnswerSpecialitiesOfProvider() {
        ArrayList<UserSpecializationMapping> held = new ArrayList<>(List.of(new UserSpecializationMapping()));
        when(userSpecializationMappingRepo.findByServiceprovider(PROVIDER_ID)).thenReturn(held);

        assertSame(held, service.getUserSpecialization(PROVIDER_ID));
    }

    @Test
    @DisplayName("saveUserSpecialization should answer the specialities the repository stored")
    void saveUserSpecialization_shouldAnswerStoredSpecialities() {
        ArrayList<UserSpecializationMapping> stored = new ArrayList<>(List.of(new UserSpecializationMapping()));
        when(userSpecializationMappingRepo.saveAll(anyList())).thenReturn(stored);

        assertSame(stored, service.saveUserSpecialization(new ArrayList<>()));
    }

    @Test
    @DisplayName("findUserSpecialization and saveoneUserSpecialization should each reach their own query")
    void singleRecordOperations_shouldReachTheirOwnQuery() {
        UserSpecializationMapping stored = new UserSpecializationMapping();
        stored.setUserSpecializationMapID(8001);
        when(userSpecializationMappingRepo.findByUserSpecializationMapID(8001)).thenReturn(stored);
        when(userSpecializationMappingRepo.save(stored)).thenReturn(stored);

        assertSame(stored, service.findUserSpecialization(stored));
        assertSame(stored, service.saveoneUserSpecialization(stored));
    }

    @Test
    @DisplayName("findUserSpecialization should answer nothing when the speciality is unknown")
    void findUserSpecialization_shouldAnswerNothingForUnknownSpeciality() {
        UserSpecializationMapping request = new UserSpecializationMapping();
        request.setUserSpecializationMapID(-1);
        when(userSpecializationMappingRepo.findByUserSpecializationMapID(-1)).thenReturn(null);

        assertNull(service.findUserSpecialization(request));
    }
}
