package com.iemr.admin.data.nikshay;

import lombok.Data;

@Data
public class NikshayCreatedDeviceDTO {

	private int rowIndex;
	private Integer id;
	private String deviceSerialNumber;
	private NikshayDeviceName deviceName;
	private String phiUserId;
	private Boolean isActive;
}
