package com.iemr.admin.repo.nikshay;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshay.NikshayDeviceConfiguration;
import com.iemr.admin.data.nikshay.NikshayDeviceName;

@Repository
public interface NikshayDeviceConfigurationRepo extends CrudRepository<NikshayDeviceConfiguration, Integer> {

	NikshayDeviceConfiguration findByDeviceId(String deviceId);

	// Partial, case-insensitive match on the programme device ID; exact matches first
	@Query("select d from NikshayDeviceConfiguration d where lower(d.deviceId) like lower(concat('%', :deviceId, '%')) "
			+ "order by case when lower(d.deviceId) = lower(:deviceId) then 0 else 1 end, d.deviceId")
	List<NikshayDeviceConfiguration> searchByDeviceId(@Param("deviceId") String deviceId);

	@Query("select d from NikshayDeviceConfiguration d where (:deviceName is null or d.deviceName = :deviceName) "
			+ "and (:isActive is null or d.isActive = :isActive) order by d.deviceName, d.id")
	List<NikshayDeviceConfiguration> search(@Param("deviceName") NikshayDeviceName deviceName,
			@Param("isActive") Boolean isActive);
}
