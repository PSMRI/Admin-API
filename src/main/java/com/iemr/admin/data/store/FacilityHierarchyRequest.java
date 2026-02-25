package com.iemr.admin.data.store;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class FacilityHierarchyRequest {

	@Expose
	private M_Facility facility;

	@Expose
	private List<Integer> villageIDs;

	@Expose
	private List<Integer> childFacilityIDs;

}
