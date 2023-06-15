/*
* AMRIT – Accessible Medical Records via Integrated Technology 
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

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMappingTO;

@Mapper(componentModel = "spring")
public interface ParkingPlaceTalukMappingMapper {

	ParkingPlaceTalukMappingMapper INSTANCE = Mappers.getMapper(ParkingPlaceTalukMappingMapper.class);

	@Mappings({ @Mapping(target = "ppSubDistrictMapID", source = "parkingplaceTalukMapping.ppSubDistrictMapID"),
			@Mapping(target = "parkingPlaceID", source = "parkingplaceTalukMapping.parkingPlaceID"),
			@Mapping(target = "districtID", source = "parkingplaceTalukMapping.districtID"),
			@Mapping(target = "districtBlockID", source = "parkingplaceTalukMapping.districtBlockID"),
			@Mapping(target = "parkingPlaceName", source = "parkingplaceTalukMapping.parkingplace.parkingPlaceName"),
			@Mapping(target = "districtName", source = "parkingplaceTalukMapping.m_district.districtName"),
			@Mapping(target = "districtBlockName", source = "parkingplaceTalukMapping.districtBlock.blockName"), 
			@Mapping(target = "providerServiceMapID", source = "parkingplaceTalukMapping.providerServiceMapID"),
			@Mapping(target = "parkingPlaceDeleted", source = "parkingplaceTalukMapping.parkingplace.deleted"),
			@Mapping(target = "districtDeleted", source = "parkingplaceTalukMapping.m_district.deleted"),
			@Mapping(target = "districtBlockDeleted", source = "parkingplaceTalukMapping.districtBlock.deleted"),
			@Mapping(target = "deleted", source = "parkingplaceTalukMapping.deleted"),
			@Mapping(target = "processed", source = "parkingplaceTalukMapping.processed"),
			@Mapping(target = "createdBy", source = "parkingplaceTalukMapping.createdBy"),
			@Mapping(target = "createdDate", source = "parkingplaceTalukMapping.createdDate"),
			@Mapping(target = "modifiedBy", source = "parkingplaceTalukMapping.modifiedBy"),
			@Mapping(target = "lastModDate", source = "parkingplaceTalukMapping.lastModDate"),})
	ParkingplaceTalukMappingTO getParkingplaceTalukMappingMap(ParkingplaceTalukMapping parkingplaceTalukMapping);

	List<ParkingplaceTalukMappingTO> getParkingplaceTalukMappingMapList(
			List<ParkingplaceTalukMapping> parkingplaceTalukMappingList);
}
