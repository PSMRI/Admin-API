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
package com.iemr.admin.service.parkingPlace;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.controller.locationmaster.LocationMasterController;
import com.iemr.admin.data.locationmaster.DistrictBlock;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMapping;
import com.iemr.admin.data.parkingPlace.ParkingplaceTalukMappingTO;
import com.iemr.admin.mapper.parkingplacetalukmapping.ParkingPlaceTalukMappingMapper;
import com.iemr.admin.repo.locationmaster.DistrictBlockRepo;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceTalukMappingRepository;

@Service
public class ParkingPlaceTalukMappingServiceImpl implements ParkingPlaceTalukMappingService {

//	@Autowired
//	private Logger logger = LoggerFactory.getLogger(ParkingPlaceTalukMappingServiceImpl.class);
	
	@Autowired
	private ParkingPlaceTalukMappingRepository parkingPlaceTalukMappingRepository;
	
	@Autowired
	private DistrictBlockRepo districtBlockRepo;

	@Autowired
	private ParkingPlaceTalukMappingMapper parkingPlaceTalukMappingMapper;

	@Override
	public ArrayList<ParkingplaceTalukMapping> saveParkingPlaceTalukMapping(List<ParkingplaceTalukMapping> parkingPlace) {
		
		return (ArrayList<ParkingplaceTalukMapping>) parkingPlaceTalukMappingRepository.save(parkingPlace);
	}
	
	@Override
	public ParkingplaceTalukMapping updateParkingPlaceTalukMapping(ParkingplaceTalukMapping parkingPlace) {
		
		return  (ParkingplaceTalukMapping) parkingPlaceTalukMappingRepository.save(parkingPlace);
	}
	
	@Override
	public ParkingplaceTalukMapping findbyID(Integer id) {
		
		return (ParkingplaceTalukMapping) parkingPlaceTalukMappingRepository.findOne(id);
	}
	
	@Override
	public List<ParkingplaceTalukMappingTO> findbyProviderservicemapid(ParkingplaceTalukMapping parkingPlace) {
		
		return parkingPlaceTalukMappingMapper.getParkingplaceTalukMappingMapList( (List<ParkingplaceTalukMapping>) parkingPlaceTalukMappingRepository.findByParkingPlaceID(parkingPlace.getParkingPlaceID()));
	}
	
	@Override
	public List<ParkingplaceTalukMappingTO> findbyParkingplaceAndDistrictID(ParkingplaceTalukMapping parkingPlace) {
		
		return parkingPlaceTalukMappingMapper.getParkingplaceTalukMappingMapList( (List<ParkingplaceTalukMapping>) parkingPlaceTalukMappingRepository.findByParkingPlaceIDAndDistrictIDOrderByM_DistrictDistrictNameAsc(parkingPlace.getParkingPlaceID(),parkingPlace.getDistrictID()));
	}

	public List<DistrictBlock> getunmappedtaluk(Integer districtID,Integer providerservicemapID) {
		// TODO Auto-generated method stub
		List<Integer> intids=parkingPlaceTalukMappingRepository.finbyDistrictID( districtID,providerservicemapID);
//		logger.info(intids.toString());
		List<DistrictBlock> output=new ArrayList<>();
			if(intids.size()>0){
				output=districtBlockRepo.findunmapped(intids,districtID);
			}else{
				output=districtBlockRepo.findall(districtID);
			}
		return output;
	}
}
