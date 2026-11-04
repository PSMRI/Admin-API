package com.iemr.admin.data.nikshay;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * Designated PHI of a Ni-kshay village, used by the transfer out phase. A
 * village can have several active rows; village + TBU + PHI is unique among
 * active rows.
 */
@Data
@Entity
@Table(name = "m_nikshay_transfer_out_phi_village_mapping")
public class NikshayTransferOutPhiVillageMapping {

	public static final String STATUS_ACTIVE = "active";
	public static final String STATUS_INACTIVE = "inactive";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "id")
	private Integer id;

	@Expose
	@Column(name = "village_id")
	private Integer villageId;

	@Expose
	@Column(name = "tbu_id")
	private Integer tbuId;

	// Ni-kshay facility ID of the designated PHI that receives the transfer
	@Expose
	@Column(name = "designated_phi_id")
	private String designatedPhiId;

	// Ni-kshay hierarchy ID of the designated PHI
	@Expose
	@Column(name = "designation_hierarchy_id")
	private String designationHierarchyId;

	@Expose
	@Column(name = "status")
	private String status = STATUS_ACTIVE;

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

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
