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
package com.iemr.admin.service.facilitytype;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.store.M_FacilityLevel;
import com.iemr.admin.repository.facilitytype.M_FacilityLevelRepo;
import com.iemr.admin.repository.facilitytype.M_facilitytypeRepo;

@Service
public class M_facilitytypeServiceImpl implements M_facilitytypeInter {

	@Autowired
	private M_facilitytypeRepo m_facilitytypeRepo;

	@Autowired
	private M_FacilityLevelRepo m_facilityLevelRepo;

	@Override
	public ArrayList<M_facilitytype> getAllFicilityData(Integer providerServiceMapID) {
		ArrayList<M_facilitytype> data = m_facilitytypeRepo.getAllFicilityData(providerServiceMapID);
		return data;
	}

	@Override
	public ArrayList<M_facilitytype> getFacilityTypesByRuralUrban(Integer providerServiceMapID, String ruralUrban) {
		return new ArrayList<>(m_facilitytypeRepo.findByProviderServiceMapIDAndRuralUrban(providerServiceMapID, ruralUrban));
	}

	@Override
	public ArrayList<M_facilitytype> addAllFicilityData(List<M_facilitytype> addfacilityDetails) {
		for (M_facilitytype ft : addfacilityDetails) {
			if (m_facilitytypeRepo.existsByFacilityTypeNameAndStateIDAndDeletedFalse(
					ft.getFacilityTypeName(), ft.getStateID())) {
				throw new RuntimeException("Facility type '" + ft.getFacilityTypeName() + "' already exists");
			}
		}
		ArrayList<M_facilitytype> data = (ArrayList<M_facilitytype>) m_facilitytypeRepo.saveAll(addfacilityDetails);
		return data;
	}

	@Override
	public M_facilitytype editAllFicilityData(Integer facilityTypeID) {
		M_facilitytype data = m_facilitytypeRepo.findByFacilityTypeID(facilityTypeID);
		return data;
	}

	@Override
	public M_facilitytype updateFacilityData(M_facilitytype allFacilityData) {
		M_facilitytype data = m_facilitytypeRepo.save(allFacilityData);
		return data;
	}

	@Override
	public Boolean checkFacilityTypeCode(M_facilitytype manufacturer) {
		// TODO Auto-generated method stub
		List<M_facilitytype> manuList = m_facilitytypeRepo.findByFacilityTypeCodeAndProviderServiceMapID(
				manufacturer.getFacilityTypeCode(), manufacturer.getProviderServiceMapID());
		if (manuList.size() > 0)
			return true;
		return false;
	}

	@Override
	public ArrayList<M_FacilityLevel> getFacilityLevels() {
		return m_facilityLevelRepo.findByDeletedFalseOrderByLevelName();
	}

	@Override
	public ArrayList<M_facilitytype> getFacilityTypesByBlock(Integer blockID) {
		return new ArrayList<>(m_facilitytypeRepo.findFacilityTypesByBlock(blockID));
	}

	@Override
	public ArrayList<M_facilitytype> getFacilityTypesByState(Integer stateID) {
		return new ArrayList<>(m_facilitytypeRepo.findByStateID(stateID));
	}

	@Override
	public boolean checkFacilityTypeNameExists(String facilityTypeName, Integer stateID) {
		return m_facilitytypeRepo.existsByFacilityTypeNameAndStateIDAndDeletedFalse(facilityTypeName, stateID);
	}

}
