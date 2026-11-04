package com.iemr.admin.model.nikshay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Designated PHI for a village. Saving the same village, TBU and designated PHI
 * again updates that row (hierarchy ID) instead of adding a duplicate.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NikshayDesignatedPhiRequest {
	private Integer id;
	private Integer villageId;
	private Integer tbuId;
	private String designatedPhiId;
	private String designationHierarchyId;
	private String createdBy;
	private String updatedBy;
}
