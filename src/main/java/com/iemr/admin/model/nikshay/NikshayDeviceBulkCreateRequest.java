package com.iemr.admin.model.nikshay;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NikshayDeviceBulkCreateRequest {
	private String createdBy;
	private List<NikshayDeviceRequest> devices;

	@Override
	public String toString() {
		return "NikshayDeviceBulkCreateRequest(createdBy=" + createdBy + ", devices="
				+ (devices == null ? 0 : devices.size()) + ")";
	}
}
