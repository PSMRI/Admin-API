package com.iemr.admin.controller.nikshay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.admin.model.nikshay.NikshayActivateRequest;
import com.iemr.admin.model.nikshay.NikshayDeviceRequest;
import com.iemr.admin.model.nikshay.NikshayVillageEnrollRequest;
import com.iemr.admin.model.nikshay.NikshayVillageRemoveRequest;
import com.iemr.admin.service.nikshay.NikshayDeviceService;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Ni-kshay camp device configuration (Module 1 of the Ni-kshay integration):
 * devices with their PHI login, and the villages each device serves.
 * Request bodies are not logged here because device requests carry passwords.
 */
@RestController
@RequestMapping(value = "/nikshay/device")
public class NikshayDeviceController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final Gson GSON = new GsonBuilder().serializeNulls().create();

	private static final String UPLOAD_TEMPLATE_NAME = "Nikshay_Device_Upload_Template.xlsx";
	private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	@Autowired
	private NikshayDeviceService nikshayDeviceService;

	@Operation(summary = "Create a Ni-kshay camp device (device name NAAT / XRAY) with its PHI login; the password is stored encrypted")
	@PostMapping(value = "/create", produces = "application/json", headers = "Authorization")
	public String createDevice(@RequestBody NikshayDeviceRequest request) {
		return respond("createDevice", () -> nikshayDeviceService.createDevice(request));
	}

	@Operation(summary = "Download the Excel template for the bulk device upload")
	@GetMapping(value = "/upload/template", headers = "Authorization")
	public ResponseEntity<byte[]> downloadUploadTemplate() {
		try {
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							ContentDisposition.attachment().filename(UPLOAD_TEMPLATE_NAME).build().toString())
					.contentType(MediaType.parseMediaType(XLSX))
					.body(nikshayDeviceService.getUploadTemplate());
		} catch (Exception e) {
			logger.error("downloadUploadTemplate failed: " + e.getMessage(), e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@Operation(summary = "Create devices from the filled Excel template; nothing is created if any row is invalid")
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json",
			headers = "Authorization")
	public String uploadDevices(@RequestParam("file") MultipartFile file, @RequestParam("createdBy") String createdBy) {
		return respond("uploadDevices", () -> {
			if (file == null || file.isEmpty()) {
				throw new IllegalArgumentException("Excel file is required");
			}
			return nikshayDeviceService.uploadDevices(file.getInputStream(), createdBy);
		});
	}

	@Operation(summary = "Update a device; the password changes only when a new one is sent")
	@PostMapping(value = "/update", produces = "application/json", headers = "Authorization")
	public String updateDevice(@RequestBody NikshayDeviceRequest request) {
		return respond("updateDevice", () -> nikshayDeviceService.updateDevice(request));
	}

	@Operation(summary = "Activate / deactivate a device; deactivating also removes it from its villages")
	@PostMapping(value = "/activate", produces = "application/json", headers = "Authorization")
	public String setDeviceActive(@RequestBody NikshayActivateRequest request) {
		return respond("setDeviceActive", () -> nikshayDeviceService.setDeviceActive(request));
	}

	@Operation(summary = "List devices (without password), each with its active villages, "
			+ "optionally by device name (NAAT / XRAY) and active flag")
	@GetMapping(value = "/list", produces = "application/json", headers = "Authorization")
	public String listDevices(@RequestParam(value = "deviceName", required = false) String deviceName,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {
		return respond("listDevices", () -> nikshayDeviceService.listDevices(deviceName, isActive));
	}

	@Operation(summary = "Search devices by serial number (partial, case-insensitive; exact match first), "
			+ "without password, each with its active villages")
	@GetMapping(value = "/search", produces = "application/json", headers = "Authorization")
	public String searchByDeviceSerialNumber(@RequestParam("deviceSerialNumber") String deviceSerialNumber) {
		return respond("searchByDeviceSerialNumber", () -> nikshayDeviceService.searchByDeviceSerialNumber(deviceSerialNumber));
	}

	@Operation(summary = "Get one device (without password) with its active villages")
	@GetMapping(value = "/get", produces = "application/json", headers = "Authorization")
	public String getDevice(@RequestParam("id") Integer id) {
		return respond("getDevice", () -> nikshayDeviceService.getDevice(id));
	}

	@Operation(summary = "Enroll a device for villages picked from one or more facilities of a TBU (facilityIds, villageIds). "
			+ "A village can have one active NAAT and one active XRAY device")
	@PostMapping(value = "/village/enroll", produces = "application/json", headers = "Authorization")
	public String enrollVillages(@RequestBody NikshayVillageEnrollRequest request) {
		return respond("enrollVillages", () -> nikshayDeviceService.enrollVillages(request));
	}

	@Operation(summary = "Remove a device from villages (marks the mappings inactive)")
	@PostMapping(value = "/village/remove", produces = "application/json", headers = "Authorization")
	public String removeVillages(@RequestBody NikshayVillageRemoveRequest request) {
		return respond("removeVillages", () -> nikshayDeviceService.removeVillages(request));
	}

	@Operation(summary = "Villages served by a device")
	@GetMapping(value = "/village/list", produces = "application/json", headers = "Authorization")
	public String getVillagesOfDevice(@RequestParam("deviceDetailId") Integer deviceDetailId,
			@RequestParam(value = "activeOnly", required = false, defaultValue = "true") Boolean activeOnly) {
		return respond("getVillagesOfDevice", () -> nikshayDeviceService.getVillagesOfDevice(deviceDetailId, activeOnly));
	}

	@Operation(summary = "Devices and PHI details (without password) mapped to a Ni-kshay location")
	@GetMapping(value = "/phiByLocation", produces = "application/json", headers = "Authorization")
	public String getPhiByLocation(@RequestParam(value = "stateId", required = false) Integer stateId,
			@RequestParam(value = "districtId", required = false) Integer districtId,
			@RequestParam(value = "tbuId", required = false) Integer tbuId,
			@RequestParam(value = "facilityId", required = false) Integer facilityId,
			@RequestParam(value = "villageId", required = false) Integer villageId,
			@RequestParam(value = "deviceName", required = false) String deviceName) {
		return respond("getPhiByLocation", () -> nikshayDeviceService.getPhiByLocation(stateId, districtId, tbuId,
				facilityId, villageId, deviceName));
	}

	private String respond(String action, ThrowingSupplier<Object> call) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(GSON.toJson(call.get()));
		} catch (Exception e) {
			logger.error(action + " failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@FunctionalInterface
	interface ThrowingSupplier<T> {
		T get() throws Exception;
	}
}
