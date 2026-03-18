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
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.facilitytype.M_facilitytype;
import com.iemr.admin.data.parkingPlace.M_Parkingplace;
import com.iemr.admin.data.store.FacilityVillageMapping;
import com.iemr.admin.data.store.M_Facility;
import com.iemr.admin.data.store.M_facilityMap;
import com.iemr.admin.data.store.V_FetchFacility;
import com.iemr.admin.data.vanMaster.M_Van;
import com.iemr.admin.repository.facilitytype.M_facilitytypeRepo;
import com.iemr.admin.repository.parkingPlace.ParkingPlaceRepository;
import com.iemr.admin.repository.store.FacilityVillageMappingRepo;
import com.iemr.admin.repository.store.MainStoreRepo;
import com.iemr.admin.repository.store.V_FetchFacilityRepo;
import com.iemr.admin.repository.vanMaster.VanMasterRepository;
import com.iemr.admin.service.employeemaster.AshaSupervisorMappingService;
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

	@Autowired
	private FacilityVillageMappingRepo facilityVillageMappingRepo;

	@Autowired
	private AshaSupervisorMappingService ashaSupervisorMappingService;

	@Autowired
	private M_facilitytypeRepo facilityTypeRepo;

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

	@Override
	public ArrayList<M_Facility> getFacilitiesByBlock(Integer blockID) {
		return mainStoreRepo.findByBlockIDAndDeletedFalseOrderByFacilityName(blockID);
	}

	@Override
	public ArrayList<M_Facility> getAllFacilitiesByBlock(Integer blockID) {
		return mainStoreRepo.findByBlockIDOrderByFacilityName(blockID);
	}

	@Override
	public ArrayList<M_Facility> getFacilitiesByBlockAndLevel(Integer blockID, Integer levelValue, String ruralUrban) {
		if (ruralUrban == null || ruralUrban.isEmpty()) {
			return mainStoreRepo.findByBlockIDAndLevelValue(blockID, levelValue);
		}
		return mainStoreRepo.findByBlockIDAndFacilityLevel(blockID, levelValue, ruralUrban);
	}

	@Transactional
	@Override
	public M_Facility createFacilityWithHierarchy(M_Facility facility, List<Integer> villageIDs,
			Integer mainVillageID, List<Integer> childFacilityIDs) {
		if (mainStoreRepo.existsByFacilityNameAndBlockIDAndDeletedFalse(facility.getFacilityName(), facility.getBlockID())) {
			throw new RuntimeException("Facility with this name already exists in this block");
		}
		// Fix 20: validate that child facilities selected are exactly one level below this facility
		// (parentFacilityID is not sent from frontend on create; the hierarchy is built via childFacilityIDs)
		if (childFacilityIDs != null && !childFacilityIDs.isEmpty()
				&& facility.getFacilityTypeID() != null) {
			M_facilitytype ft = facilityTypeRepo.findByFacilityTypeID(facility.getFacilityTypeID());
			if (ft != null && ft.getLevelValue() != null) {
				for (Integer childID : childFacilityIDs) {
					M_Facility child = mainStoreRepo.findByFacilityIDAndDeleted(childID, false);
					if (child != null && child.getFacilityTypeID() != null) {
						M_facilitytype childFt = facilityTypeRepo.findByFacilityTypeID(child.getFacilityTypeID());
						if (childFt != null && childFt.getLevelValue() != null
								&& !childFt.getLevelValue().equals(ft.getLevelValue() + 1)) {
							throw new RuntimeException(
									"Hierarchy level mismatch: selected child facility is not at the expected level. "
									+ "Children must be exactly one level below this facility.");
						}
					}
				}
			}
		}
		facility.setMainVillageID(mainVillageID);
		M_Facility savedFacility = mainStoreRepo.save(facility);

		if (villageIDs != null && !villageIDs.isEmpty()) {
			for (Integer villageID : villageIDs) {
				FacilityVillageMapping existing = facilityVillageMappingRepo
						.findByFacilityIDAndDistrictBranchIDAndDeletedTrue(savedFacility.getFacilityID(), villageID);
				if (existing != null) {
					existing.setDeleted(false);
					existing.setModifiedBy(facility.getCreatedBy());
					facilityVillageMappingRepo.save(existing);
				} else {
					FacilityVillageMapping mapping = new FacilityVillageMapping();
					mapping.setFacilityID(savedFacility.getFacilityID());
					mapping.setDistrictBranchID(villageID);
					mapping.setCreatedBy(facility.getCreatedBy());
					mapping.setDeleted(false);
					facilityVillageMappingRepo.save(mapping);
				}
			}
		}

		if (childFacilityIDs != null && !childFacilityIDs.isEmpty()) {
			for (Integer childID : childFacilityIDs) {
				M_Facility child = mainStoreRepo.findByFacilityID(childID);
				if (child != null) {
					child.setParentFacilityID(savedFacility.getFacilityID());
					child.setModifiedBy(facility.getCreatedBy());
					mainStoreRepo.save(child);
				}
			}
		}

		return savedFacility;
	}

	@Override
	public List<Integer> getMappedVillageIDs(Integer blockID) {
		return facilityVillageMappingRepo.findMappedVillageIDsByBlockID(blockID);
	}

	@Override
	public ArrayList<FacilityVillageMapping> getVillageMappingsByFacility(Integer facilityID) {
		// Return empty if facility itself is deleted
		M_Facility facility = mainStoreRepo.findByFacilityIDAndDeleted(facilityID, false);
		if (facility == null) {
			return new ArrayList<>();
		}
		return facilityVillageMappingRepo.findByFacilityIDAndDeletedFalse(facilityID);
	}

	@Override
	public ArrayList<M_Facility> getChildFacilitiesByParent(Integer parentFacilityID) {
		return mainStoreRepo.findByParentFacilityIDAndDeletedFalseOrderByFacilityName(parentFacilityID);
	}

	@Transactional
	@Override
	public M_Facility deleteFacilityWithHierarchy(Integer facilityID, String modifiedBy) throws Exception {
		M_Facility facility = mainStoreRepo.findByFacilityID(facilityID);
		if (facility == null) {
			throw new Exception("Facility not found");
		}
		// Fix 19: clear parentFacilityID on children (unlink from hierarchy, don't block)
		ArrayList<M_Facility> children = mainStoreRepo.findByParentFacilityIDAndDeletedFalseOrderByFacilityName(facilityID);
		for (M_Facility child : children) {
			child.setParentFacilityID(null);
			child.setModifiedBy(modifiedBy);
			mainStoreRepo.save(child);
		}
		facility.setDeleted(true);
		facility.setModifiedBy(modifiedBy);
		M_Facility saved = mainStoreRepo.save(facility);
		// Fix 8: cascade soft-delete all asha_supervisor_mapping rows for this facility
		ashaSupervisorMappingService.cascadeDeleteByFacilityID(facilityID, modifiedBy);
		// Cascade soft-delete facility_village_mapping rows
		ArrayList<FacilityVillageMapping> villageMappings = facilityVillageMappingRepo.findByFacilityIDAndDeletedFalse(facilityID);
		for (FacilityVillageMapping vm : villageMappings) {
			vm.setDeleted(true);
			vm.setModifiedBy(modifiedBy);
			facilityVillageMappingRepo.save(vm);
		}
		return saved;
	}

	@Transactional
	@Override
	public M_Facility updateFacilityWithHierarchy(M_Facility facility, List<Integer> villageIDs,
			Integer mainVillageID, List<Integer> childFacilityIDs) {
		M_Facility existing = mainStoreRepo.findByFacilityID(facility.getFacilityID());
		if (existing == null) {
			throw new RuntimeException("Facility not found");
		}

		if (mainStoreRepo.existsByFacilityNameAndBlockIDAndNotFacilityID(facility.getFacilityName(), existing.getBlockID(), facility.getFacilityID())) {
			throw new RuntimeException("Facility with this name already exists in this block");
		}

		existing.setFacilityName(facility.getFacilityName());
		existing.setFacilityDesc(facility.getFacilityDesc());
		existing.setFacilityCode(facility.getFacilityCode());
		// Rural/Urban and FacilityType are read-only on edit — admin must delete and recreate to change
		existing.setMainVillageID(mainVillageID);
		existing.setModifiedBy(facility.getModifiedBy());
		M_Facility savedFacility = mainStoreRepo.save(existing);

		if (villageIDs != null) {
			List<FacilityVillageMapping> oldMappings = facilityVillageMappingRepo
					.findByFacilityIDAndDeletedFalse(facility.getFacilityID());
			// Build set of new village IDs for quick lookup
			java.util.Set<Integer> newVillageSet = new java.util.HashSet<>(villageIDs);
			// Soft-delete old mappings that are NOT in the new list
			for (FacilityVillageMapping old : oldMappings) {
				if (!newVillageSet.contains(old.getDistrictBranchID())) {
					old.setDeleted(true);
					old.setModifiedBy(facility.getModifiedBy());
					facilityVillageMappingRepo.save(old);
				}
			}
			// Build set of currently active village IDs
			java.util.Set<Integer> activeVillageSet = new java.util.HashSet<>();
			for (FacilityVillageMapping old : oldMappings) {
				if (!Boolean.TRUE.equals(old.getDeleted())) {
					activeVillageSet.add(old.getDistrictBranchID());
				}
			}
			// Add only truly new villages (not already active)
			for (Integer villageID : villageIDs) {
				if (!activeVillageSet.contains(villageID)) {
					FacilityVillageMapping softDeleted = facilityVillageMappingRepo
							.findByFacilityIDAndDistrictBranchIDAndDeletedTrue(savedFacility.getFacilityID(), villageID);
					if (softDeleted != null) {
						softDeleted.setDeleted(false);
						softDeleted.setModifiedBy(facility.getModifiedBy());
						facilityVillageMappingRepo.save(softDeleted);
					} else {
						FacilityVillageMapping mapping = new FacilityVillageMapping();
						mapping.setFacilityID(savedFacility.getFacilityID());
						mapping.setDistrictBranchID(villageID);
						mapping.setCreatedBy(facility.getModifiedBy());
						mapping.setDeleted(false);
						facilityVillageMappingRepo.save(mapping);
					}
				}
			}
		}

		if (childFacilityIDs != null) {
			// Fix 20: validate child levels before updating
			if (!childFacilityIDs.isEmpty() && existing.getFacilityTypeID() != null) {
				M_facilitytype ft = facilityTypeRepo.findByFacilityTypeID(existing.getFacilityTypeID());
				if (ft != null && ft.getLevelValue() != null) {
					for (Integer childID : childFacilityIDs) {
						M_Facility child = mainStoreRepo.findByFacilityIDAndDeleted(childID, false);
						if (child != null && child.getFacilityTypeID() != null) {
							M_facilitytype childFt = facilityTypeRepo.findByFacilityTypeID(child.getFacilityTypeID());
							if (childFt != null && childFt.getLevelValue() != null
									&& !childFt.getLevelValue().equals(ft.getLevelValue() + 1)) {
								throw new RuntimeException(
										"Hierarchy level mismatch: selected child facility is not at the expected level. "
										+ "Children must be exactly one level below this facility.");
							}
						}
					}
				}
			}
			mainStoreRepo.clearParentFacilityID(facility.getFacilityID(), facility.getModifiedBy());
			for (Integer childID : childFacilityIDs) {
				M_Facility child = mainStoreRepo.findByFacilityID(childID);
				if (child != null) {
					child.setParentFacilityID(savedFacility.getFacilityID());
					child.setModifiedBy(facility.getModifiedBy());
					mainStoreRepo.save(child);
				}
			}
		}

		return savedFacility;
	}

}
