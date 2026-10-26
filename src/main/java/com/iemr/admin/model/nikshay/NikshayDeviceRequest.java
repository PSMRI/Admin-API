package com.iemr.admin.model.nikshay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Create / update body for a camp device. On update, password is changed only
 * when a non-blank value is sent. moreData is any JSON object.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NikshayDeviceRequest {
	private Integer id;
	private String deviceId;
	private String deviceName;
	private String phiUserId;
	private String password;
	private Object moreData;
	private String createdBy;
	private String updatedBy;

	@Override
	public String toString() {
		return "NikshayDeviceRequest(id=" + id + ", deviceId=" + deviceId + ", deviceName=" + deviceName + ")";
	}
}
