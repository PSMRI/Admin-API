package com.iemr.admin.service.nikshay;

import java.util.List;
import java.util.Map;

import com.iemr.admin.model.nikshay.NikshayActivateRequest;
import com.iemr.admin.model.nikshay.NikshayDesignatedPhiRequest;

public interface NikshayDesignatedPhiService {

	Map<String, Object> saveDesignatedPhi(NikshayDesignatedPhiRequest request) throws Exception;

	Map<String, Object> updateDesignatedPhi(NikshayDesignatedPhiRequest request) throws Exception;

	Map<String, Object> setActive(NikshayActivateRequest request) throws Exception;

	Map<String, Object> markInactive(NikshayActivateRequest request) throws Exception;

	List<Map<String, Object>> getDesignatedPhi(Integer villageId, Integer tbuId, Integer districtId,
			Boolean activeOnly, Boolean unmappedOnly) throws Exception;

	List<Map<String, Object>> getAllDesignatedPhi(Boolean activeOnly) throws Exception;
}
