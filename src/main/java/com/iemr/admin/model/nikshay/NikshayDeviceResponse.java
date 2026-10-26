package com.iemr.admin.model.nikshay;

import java.util.List;
import java.util.Map;

import com.iemr.admin.data.nikshay.NikshayDeviceName;

import lombok.Data;

// Device details as returned by the APIs. Deliberately has no password field.
@Data
public class NikshayDeviceResponse {
	private Integer id;
	private String deviceId;
	private NikshayDeviceName deviceName;
	private String phiUserId;
	private Boolean isActive;
	private Object moreData;
	private Long activeVillageCount;
	private List<Map<String, Object>> villages;
	private String createdBy;
	private String createdAt;
	private String updatedBy;
	private String updatedAt;
}
