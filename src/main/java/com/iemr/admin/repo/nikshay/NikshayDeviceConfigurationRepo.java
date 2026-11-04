package com.iemr.admin.repo.nikshay;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshay.NikshayDeviceConfiguration;
import com.iemr.admin.data.nikshay.NikshayDeviceName;

@Repository
public interface NikshayDeviceConfigurationRepo
		extends CrudRepository<NikshayDeviceConfiguration, Integer>, NikshayDeviceConfigurationRepoCustom {

	NikshayDeviceConfiguration findByDeviceSerialNumber(String deviceSerialNumber);

	// Existing devices among the serial numbers of a bulk create, in one query
	List<NikshayDeviceConfiguration> findByDeviceSerialNumberIn(Collection<String> deviceSerialNumbers);

	// Partial, case-insensitive match on the device serial number; exact matches first
	@Query("select d from NikshayDeviceConfiguration d where lower(d.deviceSerialNumber) like lower(concat('%', :deviceSerialNumber, '%')) "
			+ "order by case when lower(d.deviceSerialNumber) = lower(:deviceSerialNumber) then 0 else 1 end, d.deviceSerialNumber")
	List<NikshayDeviceConfiguration> searchByDeviceSerialNumber(@Param("deviceSerialNumber") String deviceSerialNumber);

	@Query("select d from NikshayDeviceConfiguration d where (:deviceName is null or d.deviceName = :deviceName) "
			+ "and (:isActive is null or d.isActive = :isActive) order by d.deviceName, d.id")
	List<NikshayDeviceConfiguration> search(@Param("deviceName") NikshayDeviceName deviceName,
			@Param("isActive") Boolean isActive);
}
