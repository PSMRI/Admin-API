package com.iemr.admin.repo.nikshay;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshay.NikshayTransferOutPhiVillageMapping;

@Repository
public interface NikshayTransferOutPhiRepo extends CrudRepository<NikshayTransferOutPhiVillageMapping, Integer> {

	// Active rows for a village + TBU + PHI (unique while active)
	@Query("select p from NikshayTransferOutPhiVillageMapping p where p.villageId = :villageId and p.tbuId = :tbuId "
			+ "and p.designatedPhiId = :designatedPhiId and p.status = 'active'")
	List<NikshayTransferOutPhiVillageMapping> findActiveByVillageAndPhi(@Param("villageId") Integer villageId,
			@Param("tbuId") Integer tbuId, @Param("designatedPhiId") String designatedPhiId);

	// row = [id, village_id, VillageName, tbu_id, TUName, designated_phi_id,
	// designation_hierarchy_id, status, created_by, created_at, updated_by, updated_at]
	@Query(value = "select p.id, p.village_id, v.VillageName, p.tbu_id, t.TUName, p.designated_phi_id, p.designation_hierarchy_id, "
			+ "p.status, p.created_by, p.created_at, p.updated_by, p.updated_at "
			+ "from m_nikshay_transfer_out_phi_village_mapping p "
			+ "left join m_nikshay_village v on v.NikshayVillageID = p.village_id "
			+ "left join m_nikshay_tu t on t.NikshayTUID = p.tbu_id "
			+ "where (:villageId is null or p.village_id = :villageId) "
			+ "and (:tbuId is null or p.tbu_id = :tbuId) "
			+ "and (:districtId is null or t.NikshayDistrictID = :districtId) "
			+ "and (:activeOnly = false or p.status = 'active') "
			+ "order by v.VillageName, p.village_id, p.id desc", nativeQuery = true)
	List<Object[]> search(@Param("villageId") Integer villageId, @Param("tbuId") Integer tbuId,
			@Param("districtId") Integer districtId, @Param("activeOnly") Boolean activeOnly);

	// Villages with no active designated PHI. row = [NikshayVillageID,
	// VillageName, NikshayFacilityID, FacilityName, NikshayTUID, TUName]
	@Query(value = "select v.NikshayVillageID, v.VillageName, f.NikshayFacilityID, f.FacilityName, t.NikshayTUID, t.TUName "
			+ "from m_nikshay_village v "
			+ "join m_nikshay_facility f on f.NikshayFacilityID = v.NikshayFacilityID "
			+ "join m_nikshay_tu t on t.NikshayTUID = f.NikshayTUID "
			+ "where v.Deleted = false "
			+ "and (:villageId is null or v.NikshayVillageID = :villageId) "
			+ "and (:tbuId is null or t.NikshayTUID = :tbuId) "
			+ "and (:districtId is null or t.NikshayDistrictID = :districtId) "
			+ "and not exists (select 1 from m_nikshay_transfer_out_phi_village_mapping p "
			+ "where p.village_id = v.NikshayVillageID and p.status = 'active') "
			+ "order by v.VillageName, v.NikshayVillageID", nativeQuery = true)
	List<Object[]> findVillagesWithoutDesignatedPhi(@Param("villageId") Integer villageId,
			@Param("tbuId") Integer tbuId, @Param("districtId") Integer districtId);
}
