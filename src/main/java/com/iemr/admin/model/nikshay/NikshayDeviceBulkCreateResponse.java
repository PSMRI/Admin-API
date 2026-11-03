package com.iemr.admin.model.nikshay;

import java.util.ArrayList;
import java.util.List;

import com.iemr.admin.data.nikshay.NikshayCreatedDeviceDTO;
import com.iemr.admin.data.nikshay.NikshayFailedDeviceDTO;

import lombok.Data;

@Data
public class NikshayDeviceBulkCreateResponse {

	private int total;
	private int successCount;
	private int failedCount;
	private List<NikshayCreatedDeviceDTO> created = new ArrayList<>();
	private List<NikshayFailedDeviceDTO> failed = new ArrayList<>();
}
