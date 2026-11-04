package com.iemr.admin.data.nikshay;

/**
 * Name of a TBMJA camp device, stored as a string in
 * m_nikshay_device_configuration.device_name. It decides which activities the
 * device's PHI login is used for: NAAT for ID generation, NAAT results,
 * contacts and transfer out; XRAY for X-ray results.
 */
public enum NikshayDeviceName {
	NAAT, XRAY;

	/** Case-insensitive parse with a readable error for API input. */
	public static NikshayDeviceName from(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Device name is required");
		}
		try {
			return valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Device name must be NAAT or XRAY");
		}
	}
}
