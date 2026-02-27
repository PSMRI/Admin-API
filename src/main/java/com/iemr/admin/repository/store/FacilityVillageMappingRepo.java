package com.iemr.admin.repository.store;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.store.FacilityVillageMapping;

@Repository
public interface FacilityVillageMappingRepo extends CrudRepository<FacilityVillageMapping, Long> {

	ArrayList<FacilityVillageMapping> findByFacilityIDAndDeletedFalse(Integer facilityID);

	@Query("SELECT DISTINCT fvm.districtBranchID FROM FacilityVillageMapping fvm WHERE fvm.facilityID IN " +
			"(SELECT f.facilityID FROM M_Facility f WHERE f.blockID = :blockID AND f.deleted = false) " +
			"AND fvm.deleted = false")
	List<Integer> findMappedVillageIDsByBlockID(@Param("blockID") Integer blockID);

}
