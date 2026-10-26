package com.iemr.admin.model.nikshay;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// Enroll a device (phir_device_configuration.id) for one or more Ni-kshay villages
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NikshayVillageEnrollRequest {
	private Integer deviceDetailId;
	private String createdBy;
	private List<Village> villages;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Village {
		private Integer stateId;
		private Integer districtId;
		private Integer tbuId;
		private Integer facilityId;
		private Integer villageId;
		private Object moreData;
	}
}
