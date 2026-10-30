package com.iemr.admin.controller.nikshay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.admin.model.nikshay.NikshayActivateRequest;
import com.iemr.admin.model.nikshay.NikshayDesignatedPhiRequest;
import com.iemr.admin.service.nikshay.NikshayDesignatedPhiService;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Designated PHI of each Ni-kshay village, used by the transfer out phase of
 * the Ni-kshay job (Module 1 of the Ni-kshay integration). Responses keep null
 * fields (e.g. designationHierarchyId) so every row has the same keys.
 */
@RestController
@RequestMapping(value = "/nikshay/designatedPhi")
public class NikshayDesignatedPhiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final Gson GSON = new GsonBuilder().serializeNulls().create();

	@Autowired
	private NikshayDesignatedPhiService nikshayDesignatedPhiService;

	@Operation(summary = "Save a designated PHI for a village (a village can have several); saving the same village, TBU and PHI again updates it")
	@PostMapping(value = "/save", produces = "application/json", headers = "Authorization")
	public String saveDesignatedPhi(@RequestBody NikshayDesignatedPhiRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(GSON.toJson(nikshayDesignatedPhiService.saveDesignatedPhi(request)));
		} catch (Exception e) {
			logger.error("saveDesignatedPhi failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toStringWithSerialization();
	}

	@Operation(summary = "Update a designated PHI mapping by id; only the fields sent (not null) are changed")
	@PostMapping(value = "/update", produces = "application/json", headers = "Authorization")
	public String updateDesignatedPhi(@RequestBody NikshayDesignatedPhiRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(GSON.toJson(nikshayDesignatedPhiService.updateDesignatedPhi(request)));
		} catch (Exception e) {
			logger.error("updateDesignatedPhi failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toStringWithSerialization();
	}

	@Operation(summary = "Activate / deactivate a designated PHI mapping")
	@PostMapping(value = "/activate", produces = "application/json", headers = "Authorization")
	public String setActive(@RequestBody NikshayActivateRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(GSON.toJson(nikshayDesignatedPhiService.setActive(request)));
		} catch (Exception e) {
			logger.error("setActive designated PHI failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toStringWithSerialization();
	}

	@Operation(summary = "Mark a designated PHI mapping inactive by id (body: id, updatedBy)")
	@PostMapping(value = "/markInactive", produces = "application/json", headers = "Authorization")
	public String markInactive(@RequestBody NikshayActivateRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(GSON.toJson(nikshayDesignatedPhiService.markInactive(request)));
		} catch (Exception e) {
			logger.error("markInactive designated PHI failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toStringWithSerialization();
	}

	@Operation(summary = "Get designated PHI by village, TBU or district. unmappedOnly=true lists villages "
			+ "without an active designated PHI; activeOnly=false includes inactive rows")
	@GetMapping(value = "/get", produces = "application/json", headers = "Authorization")
	public String getDesignatedPhi(@RequestParam(value = "villageId", required = false) Integer villageId,
			@RequestParam(value = "tbuId", required = false) Integer tbuId,
			@RequestParam(value = "districtId", required = false) Integer districtId,
			@RequestParam(value = "activeOnly", required = false, defaultValue = "true") Boolean activeOnly,
			@RequestParam(value = "unmappedOnly", required = false, defaultValue = "false") Boolean unmappedOnly) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(GSON.toJson(
					nikshayDesignatedPhiService.getDesignatedPhi(villageId, tbuId, districtId, activeOnly, unmappedOnly)));
		} catch (Exception e) {
			logger.error("getDesignatedPhi failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toStringWithSerialization();
	}

	@Operation(summary = "Get all designated PHI mappings; activeOnly=true leaves out inactive rows")
	@GetMapping(value = "/getAll", produces = "application/json", headers = "Authorization")
	public String getAllDesignatedPhi(
			@RequestParam(value = "activeOnly", required = false, defaultValue = "false") Boolean activeOnly) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(GSON.toJson(nikshayDesignatedPhiService.getAllDesignatedPhi(activeOnly)));
		} catch (Exception e) {
			logger.error("getAllDesignatedPhi failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toStringWithSerialization();
	}
}
