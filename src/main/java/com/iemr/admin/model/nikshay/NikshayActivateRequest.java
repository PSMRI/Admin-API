package com.iemr.admin.model.nikshay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// Activate / deactivate a device or a designated PHI mapping
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NikshayActivateRequest {
	private Integer id;
	private Boolean isActive;
	private String updatedBy;
}
