package com.iemr.admin.data.nikshay;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * One TBMJA camp device (device_name NAAT or XRAY) and the Ni-kshay PHI login created for
 * it. The Ni-kshay job logs in with the device of the beneficiary's village:
 * the NAAT device for ID generation, NAAT results, contacts and transfer out,
 * the XRAY device for X-ray results.
 *
 * The password is stored AES encrypted (see NikshayPasswordCipher) and has no
 * {@link Expose}, so it is never part of toString() or any API response.
 */
@Data
@Entity
@Table(name = "m_nikshay_device_configuration")
public class NikshayDeviceConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "id")
	private Integer id;

	@Expose
	@Column(name = "device_id", unique = true)
	private String deviceId;

	@Expose
	@Enumerated(EnumType.STRING)
	@Column(name = "device_name")
	private NikshayDeviceName deviceName;

	@Expose
	@Column(name = "phi_user_id")
	private String phiUserId;

	@Column(name = "password")
	private String password;

	@Expose
	@Column(name = "is_active")
	private Boolean isActive = true;

	@Expose
	@Column(name = "created_by")
	private String createdBy;

	@Expose
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Expose
	@Column(name = "updated_by")
	private String updatedBy;

	@Expose
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	@Expose
	@Column(name = "more_data", columnDefinition = "json")
	private String moreData;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
