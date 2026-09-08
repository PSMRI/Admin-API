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
package com.iemr.admin.mapper.parkingplacetalukmapping;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.locationmaster.M_District;
import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMappingTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The mapper flattens a stored taluk mapping and the three records it points at
 * into the single carrier the parking place screens read.
 */
@DisplayName("ParkingPlaceTalukMappingMapper Test Suite")
class ParkingPlaceTalukMappingMapperTest {

    private final ParkingPlaceTalukMappingMapper mapper = ParkingPlaceTalukMappingMapper.INSTANCE;

    private static ParkingplaceTalukMapping fullMapping() {
        ParkingplaceTalukMapping mapping = new ParkingplaceTalukMapping();
        mapping.setPpSubDistrictMapID(7001);
        mapping.setParkingPlaceID(31);
        mapping.setDistrictID(301);
        mapping.setDistrictBlockID(3011);
        mapping.setProviderServiceMapID(4001);
        mapping.setDeleted(Boolean.FALSE);
        mapping.setProcessed("N");
        mapping.setCreatedBy("admin");
        mapping.setModifiedBy("supervisor");

        M_Parkingplace parkingplace = new M_Parkingplace();
        parkingplace.setParkingPlaceName("Hosur parking");
        parkingplace.setDeleted(Boolean.FALSE);
        mapping.setParkingplace(parkingplace);

        M_District district = new M_District();
        district.setDistrictName("Bengaluru Urban");
        district.setDeleted(Boolean.FALSE);
        mapping.setM_district(district);

        DistrictBlock block = new DistrictBlock();
        block.setBlockName("Anekal");
        block.setDeleted(Boolean.FALSE);
        mapping.setDistrictBlock(block);
        return mapping;
    }

    @Test
    @DisplayName("should carry the names of the parking place, district and taluk onto one carrier")
    void shouldFlattenNamesOntoOneCarrier() {
        ParkingplaceTalukMappingTO published = mapper.getParkingplaceTalukMappingMap(fullMapping());

        assertEquals(7001, published.getPpSubDistrictMapID());
        assertEquals("Hosur parking", published.getParkingPlaceName());
        assertEquals("Bengaluru Urban", published.getDistrictName());
        assertEquals("Anekal", published.getDistrictBlockName());
        assertEquals(4001, published.getProviderServiceMapID());
        assertEquals("admin", published.getCreatedBy());
        assertEquals("supervisor", published.getModifiedBy());
        assertEquals("N", published.getProcessed());
        assertEquals(Boolean.FALSE, published.getDeleted());
        assertEquals(Boolean.FALSE, published.getParkingPlaceDeleted());
        assertEquals(Boolean.FALSE, published.getDistrictDeleted());
        assertEquals(Boolean.FALSE, published.getDistrictBlockDeleted());
    }

    @Test
    @DisplayName("should leave the names empty when the mapping points at nothing")
    void shouldLeaveNamesEmptyWhenNothingPointedAt() {
        ParkingplaceTalukMapping mapping = new ParkingplaceTalukMapping();
        mapping.setPpSubDistrictMapID(7002);

        ParkingplaceTalukMappingTO published = mapper.getParkingplaceTalukMappingMap(mapping);

        assertEquals(7002, published.getPpSubDistrictMapID());
        assertNull(published.getParkingPlaceName());
        assertNull(published.getDistrictName());
        assertNull(published.getDistrictBlockName());
        assertNull(published.getParkingPlaceDeleted());
    }

    @Test
    @DisplayName("should answer nothing for a mapping that is not there at all")
    void shouldAnswerNothingForAbsentMapping() {
        assertNull(mapper.getParkingplaceTalukMappingMap(null));
        assertNull(mapper.getParkingplaceTalukMappingMapList(null));
    }

    @Test
    @DisplayName("should publish one carrier per mapping in the list")
    void shouldPublishOneCarrierPerMapping() {
        List<ParkingplaceTalukMappingTO> published = mapper
                .getParkingplaceTalukMappingMapList(List.of(fullMapping(), fullMapping()));

        assertEquals(2, published.size());
        assertEquals("Anekal", published.get(0).getDistrictBlockName());
    }

    @Test
    @DisplayName("should publish an empty list when there is no mapping to publish")
    void shouldPublishEmptyListForNoMappings() {
        assertTrue(mapper.getParkingplaceTalukMappingMapList(new ArrayList<>()).isEmpty());
    }
}
