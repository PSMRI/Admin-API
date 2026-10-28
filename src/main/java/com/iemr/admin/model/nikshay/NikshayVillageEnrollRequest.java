package com.iemr.admin.model.nikshay;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Enroll a device (m_nikshay_device_configuration.id) for villages picked from
 * one or more facilities of a TBU. Each village is saved under its own facility
 * (m_nikshay_village.NikshayFacilityID), which must be one of facilityIds.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NikshayVillageEnrollRequest {
	private Integer deviceDetailId;
	private Integer stateId;
	private Integer districtId;
	private Integer tbuId;
	private List<Integer> facilityIds;
	private List<Integer> villageIds;
	private Object moreData;
	private String createdBy;
}
