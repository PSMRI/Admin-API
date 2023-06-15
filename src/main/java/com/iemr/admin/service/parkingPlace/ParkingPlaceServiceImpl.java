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
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.provideronboard.M_ProviderServiceMapping;
import com.iemr.admin.data.zonemaster.M_Zone;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceRepository;

@Service
public class ParkingPlaceServiceImpl implements ParkingPlaceService {

	@Autowired
	private ParkingPlaceRepository parkingPlaceRepository;

	@Override
	public ArrayList<M_Parkingplace> getAvailableParkingPlaces(Integer stateID, Integer districtID,
			Integer serviceProviderID) {

		String stateId = "%%";
		if (null != stateID) {
			stateId = stateID + "";
		}

		String districtId = "%%";
		if (null != districtID) {
			districtId = districtID + "";
		}

		ArrayList<M_Parkingplace> parkingPlaceList = new ArrayList<M_Parkingplace>();

		List<Objects[]> allData = parkingPlaceRepository.getAvailableParkingPlaces(stateId, districtId,
				serviceProviderID);

		for (Object[] objects : allData) {

			parkingPlaceList.add(new M_Parkingplace((Integer) objects[0], (String) objects[1], (String) objects[2],
					(String) objects[3], (Integer) objects[4], (Boolean) objects[5], (Integer) objects[6],
					(String) objects[7], (Integer) objects[8], (String) objects[9], (Integer) objects[10],
					(String) objects[11], (Integer) objects[12], (String) objects[13], (Integer) objects[14],
					(String) objects[15], (M_ProviderServiceMapping) objects[16], (Integer) objects[17],
					(String) objects[18]));

		}
		return parkingPlaceList;
	}

	@Override
	public ArrayList<M_Parkingplace> saveParkingPlace(List<M_Parkingplace> parkingPlaces) {
		ArrayList<M_Parkingplace> allData = (ArrayList<M_Parkingplace>) parkingPlaceRepository.save(parkingPlaces);
		return allData;
	}

	@Override
	public int updateParkingPlaceStatus(M_Parkingplace m_parkingplace) {
		int response = parkingPlaceRepository.updateParkingPlaceStatus(m_parkingplace.getParkingPlaceID(),
				m_parkingplace.getDeleted(), m_parkingplace.getModifiedBy());
		return response;
	}

	@Override
	public M_Parkingplace getParkingPlaceByID(Integer parkingPlaceID) {
		M_Parkingplace parkingPlace = parkingPlaceRepository.getParkingPlaceById(parkingPlaceID);
		return parkingPlace;
	}

	@Override
	public M_Parkingplace updateParkingPlaceData(M_Parkingplace m_parkingplace) {
		M_Parkingplace parkingPlace = parkingPlaceRepository.save(m_parkingplace);
		return parkingPlace;
	}

	public List<M_Parkingplace> getParkingPlaces(Integer providerServiceMapID) {

		return parkingPlaceRepository.findByProviderServiceMapID(providerServiceMapID);
	}

	public List<M_Parkingplace> getSubDistrict(Integer parkingPlaceID) {

		ArrayList<M_Parkingplace> subdistrict = new ArrayList<M_Parkingplace>();

		List<Objects[]> allData = parkingPlaceRepository.getSubDistrict(parkingPlaceID);

		for (Object[] objects : allData) {

			subdistrict.add(new M_Parkingplace((Integer) objects[0], (Integer) objects[1], (String) objects[2]));

		}
		return subdistrict;

	}

	public ArrayList<M_Parkingplace> getAvailableParkingPlacesbyZoneID(Integer zoneID, Integer providerServiceMapID) {
		// TODO Auto-generated method stub
		ArrayList<M_Parkingplace> parkingPlaceList = new ArrayList<M_Parkingplace>();
		List<Objects[]> allData = parkingPlaceRepository.getAvailableParkingPlacesbyzoneid(zoneID,
				providerServiceMapID);

		for (Object[] objects : allData) {
			M_Parkingplace pp = (M_Parkingplace) objects[0];
			if (objects.length >= 1) {
				M_Zone zone = (M_Zone) objects[1];
				pp.setZoneName(zone.getZoneName());
			}

			parkingPlaceList.add(pp);

		}
		return parkingPlaceList;
	}
}
