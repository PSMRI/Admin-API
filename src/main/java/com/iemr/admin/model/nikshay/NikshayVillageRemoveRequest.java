package com.iemr.admin.model.nikshay;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// Remove a device from villages: marks the given device_village_mapping rows inactive
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NikshayVillageRemoveRequest {
	private List<Integer> mappingIds;
	private String updatedBy;
}
