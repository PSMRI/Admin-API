package com.iemr.admin.service.nikshay;

import java.util.List;
import java.util.Map;

import com.iemr.admin.model.nikshay.NikshayActivateRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceResponse;
import com.iemr.admin.model.nikshay.NikshayVillageEnrollRequest;
import com.iemr.admin.model.nikshay.NikshayVillageRemoveRequest;

public interface NikshayDeviceService {

	NikshayDeviceResponse createDevice(NikshayDeviceRequest request) throws Exception;

	NikshayDeviceResponse updateDevice(NikshayDeviceRequest request) throws Exception;

	NikshayDeviceResponse setDeviceActive(NikshayActivateRequest request) throws Exception;

	List<NikshayDeviceResponse> listDevices(String deviceName, Boolean isActive) throws Exception;

	NikshayDeviceResponse getDevice(Integer id) throws Exception;

	List<NikshayDeviceResponse> searchByDeviceId(String deviceId) throws Exception;

	Map<String, Object> enrollVillages(NikshayVillageEnrollRequest request) throws Exception;

	Map<String, Object> removeVillages(NikshayVillageRemoveRequest request) throws Exception;

	List<Map<String, Object>> getVillagesOfDevice(Integer deviceDetailId, Boolean activeOnly) throws Exception;

	List<Map<String, Object>> getPhiByLocation(Integer stateId, Integer districtId, Integer tbuId,
			Integer facilityId, Integer villageId, String deviceName) throws Exception;
}
