package com.iemr.admin.repo.nikshay;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshay.NikshayDeviceName;
import com.iemr.admin.data.nikshay.NikshayDeviceVillageMapping;

@Repository
public interface NikshayDeviceVillageMappingRepo extends CrudRepository<NikshayDeviceVillageMapping, Integer> {

	// Active mappings of these villages to devices of one type (NAAT or XRAY)
	@Query("select m from NikshayDeviceVillageMapping m, NikshayDeviceConfiguration d where d.id = m.deviceDetailId "
			+ "and m.villageId in (:villageIds) and m.isActive = true and d.deviceName = :deviceName")
	List<NikshayDeviceVillageMapping> findActiveByVillageIdsAndDeviceName(@Param("villageIds") List<Integer> villageIds,
			@Param("deviceName") NikshayDeviceName deviceName);

	@Query("select count(m) from NikshayDeviceVillageMapping m where m.deviceDetailId = :deviceDetailId and m.isActive = true")
	long countActiveByDevice(@Param("deviceDetailId") Integer deviceDetailId);

	@Modifying
	@Query("update NikshayDeviceVillageMapping m set m.isActive = false, m.updatedBy = :updatedBy "
			+ "where m.deviceDetailId = :deviceDetailId and m.isActive = true")
	int deactivateAllForDevice(@Param("deviceDetailId") Integer deviceDetailId, @Param("updatedBy") String updatedBy);

	// row = [id, village_id, VillageName, facility_id, FacilityName, tbu_id, TUName,
	// district_id, state_id, is_active, created_by, created_at]
	@Query(value = "select m.id, m.village_id, v.VillageName, m.facility_id, f.FacilityName, m.tbu_id, t.TUName, "
			+ "m.district_id, m.state_id, m.is_active, m.created_by, m.created_at "
			+ "from m_nikshay_device_village_mapping m "
			+ "left join m_nikshay_village v on v.NikshayVillageID = m.village_id "
			+ "left join m_nikshay_facility f on f.NikshayFacilityID = m.facility_id "
			+ "left join m_nikshay_tu t on t.NikshayTUID = m.tbu_id "
			+ "where m.device_detail_id = :deviceDetailId and (:activeOnly = false or m.is_active = true) "
			+ "order by v.VillageName, m.id", nativeQuery = true)
	List<Object[]> findVillagesOfDevice(@Param("deviceDetailId") Integer deviceDetailId,
			@Param("activeOnly") Boolean activeOnly);

	// Active mappings of active devices for a location. row = [mapping id, village_id,
	// VillageName, facility_id, tbu_id, district_id, state_id, device pk id,
	// device_serial_number, device_name, phi_user_id, TUName]. Never selects the password.
	@Query(value = "select m.id, m.village_id, v.VillageName, m.facility_id, m.tbu_id, m.district_id, m.state_id, "
			+ "d.id as device_detail_id, d.device_serial_number, d.device_name, d.phi_user_id, t.TUName "
			+ "from m_nikshay_device_village_mapping m "
			+ "join m_nikshay_device_configuration d on d.id = m.device_detail_id "
			+ "left join m_nikshay_village v on v.NikshayVillageID = m.village_id "
			+ "left join m_nikshay_tu t on t.NikshayTUID = m.tbu_id "
			+ "where m.is_active = true and d.is_active = true "
			+ "and (:stateId is null or m.state_id = :stateId) "
			+ "and (:districtId is null or m.district_id = :districtId) "
			+ "and (:tbuId is null or m.tbu_id = :tbuId) "
			+ "and (:facilityId is null or m.facility_id = :facilityId) "
			+ "and (:villageId is null or m.village_id = :villageId) "
			+ "and (:deviceName is null or d.device_name = :deviceName) "
			+ "order by v.VillageName, m.village_id", nativeQuery = true)
	List<Object[]> findPhiByLocation(@Param("stateId") Integer stateId, @Param("districtId") Integer districtId,
			@Param("tbuId") Integer tbuId, @Param("facilityId") Integer facilityId,
			@Param("villageId") Integer villageId, @Param("deviceName") String deviceName);
}
