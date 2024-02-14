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
package com.iemr.admin.service.store;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.data.store.M_facilityMap;
import com.iemr.admin.data.store.V_FetchFacility;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceRepository;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.repository.store.V_FetchFacilityRepo;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.utils.exception.IEMRException;

@Service
public class StoreServiceImpl implements StoreService {

	@Autowired
	private MainStoreRepo mainStoreRepo;

	@Autowired
	private ParkingPlaceRepository parkingPlaceRepository;

	@Autowired
	private VanMasterRepository vanMasterRepository;
	
	@Autowired
	private V_FetchFacilityRepo fetchFacilityRepo;

	// @Autowired
	// private SubStoreRepo subStoreRepo;

	@Override
	public M_Facility createMainStore(M_Facility mainStoreFacility) {

		// TODO Auto-generated method stub
		return mainStoreRepo.save(mainStoreFacility);
	}

	// @Override
	// public SubStoreFacility createSubStore(SubStoreFacility subStoreFacility)
	// {
	// // TODO Auto-generated method stub
	// return subStoreRepo.save(subStoreFacility);
	// }

	@Override
	public M_Facility getMainStore(Integer mainStoreID) {
		// TODO Auto-generated method stub
		return mainStoreRepo.findByFacilityID(mainStoreID);
	}

	// @Override
	// public SubStoreFacility getSubStore(Integer subStoreID) {
	// // TODO Auto-generated method stub
	// return subStoreRepo.findOne(subStoreID);
	// }

	@Override
	public List<M_Facility> getAllMainStore(Integer providerServiceMapID) {
		// TODO Auto-generated method stub
		return (List<M_Facility>) mainStoreRepo.findByProviderServiceMapIDOrderByFacilityName(providerServiceMapID);
	}

	// @Override
	// public List<SubStoreFacility> getAllSubStore(Integer
	// providerServiceMapID) {
	// // TODO Auto-generated method stub
	// return (List<SubStoreFacility>)
	// subStoreRepo.findByProviderServiceMapID(providerServiceMapID);
	// }

	@Override

	public List<M_Facility> addAllMainStore(List<M_Facility> maniStore) {
		// TODO Auto-generated method stub
		// List<M_Facility> store=(List<M_Facility>)
		// mainStoreRepo.save(maniStore);
		// for(int i=0;i<store.length)
		return (List<M_Facility>) mainStoreRepo.saveAll(maniStore);
	}

	@Override
	public ArrayList<M_Facility> getMainFacility(Integer providerServiceMapID, Boolean isMainFacility) {
		ArrayList<M_Facility> data = mainStoreRepo.getAllMainFacility(providerServiceMapID, isMainFacility);
		return data;
	}

	@Override
	public ArrayList<M_Facility> getMainFacility(Integer providerServiceMapID, Boolean isMainFacility,
			Integer mainFacilityID) {
		ArrayList<M_Facility> data = mainStoreRepo.getAllMainFacility(providerServiceMapID, isMainFacility,
				mainFacilityID);
		return data;
	}

	@Override
	public ArrayList<M_Facility> getChildFacility(Integer providerServiceMapID, Integer mainFacilityID) {
		// TODO Auto-generated method stub
		ArrayList<M_Facility> data = mainStoreRepo.getChildFacility(providerServiceMapID, mainFacilityID);
		return data;
	}

	@Override
	public M_Facility deleteStore(M_Facility facility) throws Exception {
		// TODO Auto-generated method stub

		M_Facility stores = mainStoreRepo.findByFacilityID(facility.getFacilityID());
		if (stores != null && facility.getDeleted() != null) {
			if (facility.getDeleted()) {
				List<M_Facility> childStore = mainStoreRepo.findByMainFacilityIDAndDeletedOrderByFacilityName(facility.getFacilityID(),
						false);
				if (childStore.size() == 0) {
					storePPVanMapCheck(stores);
					stores.setDeleted(true);
					stores = mainStoreRepo.save(stores);
				} else {
					throw new Exception("Child Stores are still active");
				}
			} else {
				if (stores.getMainFacilityID() != null) {
					M_Facility parentStore = mainStoreRepo.findByFacilityIDAndDeleted(stores.getMainFacilityID(),
							false);
					if (parentStore != null) {
//						storePPVanMapCheck(stores);
						stores.setDeleted(false);
						stores = mainStoreRepo.save(stores);
					} else {
						throw new Exception("Parent Stores are still inactive");
					}
				} else {
					stores.setDeleted(false);
					stores = mainStoreRepo.save(stores);
				}

			}
		} else {
			throw new Exception("No store available");
		}
		return stores;
	}
	
	public Boolean storePPVanMapCheck(M_Facility facility) throws Exception {
		List<M_Parkingplace> pp=parkingPlaceRepository.findByFacilityIDAndDeleted(facility.getFacilityID(), false);
		if(pp.size()>0){
			throw new Exception("Store mapped to parking place");
		}
		List<M_Van> van=vanMasterRepository.findByFacilityIDAndDeleted(facility.getFacilityID(), false);
		if(van.size()>0){
			throw new Exception("Store mapped to van");
		}
		return true;	
		
	}

	@Override
	public Integer mapStore(List<M_facilityMap> facilityMap) {
		Integer cnt = 0;
		for (M_facilityMap action : facilityMap) {
			if (action.getIsMainFacility()&&action.getParkingPlaceID() != null) {
				if (action.getOldParkingPlaceID() != null) {
					parkingPlaceRepository.updatePPMap(action.getOldParkingPlaceID(), null, action.getCreatedBy(), null);
				}
				cnt = cnt + parkingPlaceRepository.updatePPMap(action.getParkingPlaceID(), action.getFacilityID(),
						action.getCreatedBy(), true);
			} else if(!action.getIsMainFacility()){
				if (action.getOldVanID() != null) {
					vanMasterRepository.updateVanMap(action.getOldVanID(), null, action.getCreatedBy(), null);
				}
				cnt = cnt + vanMasterRepository.updateVanMap(action.getVanID(), action.getFacilityID(),
						action.getCreatedBy(), true);
			}
		}
		return cnt;
	}

	@Override
	public Integer deleteMapStore(M_facilityMap action) throws Exception {
		// TODO Auto-generated method stub
		Integer cnt = 0;
		if (action.getParkingPlaceID() != null) {
			List<M_Van> van=vanMasterRepository.findByParkingPlaceIDAndFacilityIDIsNotNull(action.getParkingPlaceID());
			if(van.size()>0){
				throw new Exception("Please Unmap van under this Parking Place");
			}
			cnt = cnt
					+ parkingPlaceRepository.updatePPMap(action.getParkingPlaceID(), null, action.getCreatedBy(), null);
		} else {
			cnt = cnt + vanMasterRepository.updateVanMap(action.getVanID(), null, action.getCreatedBy(), null);
		}
		return cnt;
	}

	@Override
	public List<V_FetchFacility> getMapStore(V_FetchFacility facilitymap) {
		// TODO Auto-generated method stub
		return fetchFacilityRepo.findByProviderServiceMapID(facilitymap.getProviderServiceMapID());
	}

	@Override
	public Boolean checkStoreCode(M_Facility manufacturer) {
		// TODO Auto-generated method stub
		List<M_Facility> manuList=mainStoreRepo.findByFacilityCodeAndProviderServiceMapID(manufacturer.getFacilityCode() ,manufacturer.getProviderServiceMapID());
		if(manuList.size()>0)
			return true;
		return false;
	}

}
