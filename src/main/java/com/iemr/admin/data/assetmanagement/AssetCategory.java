package com.iemr.admin.data.assetmanagement;

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

@Entity
@Table(name = "m_asset_category")
@Data
public class AssetCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "AssetCategoryID")
	private Integer assetCategoryId;

	@Expose
	@Column(name = "AssetType", nullable = false)
	private String assetType;

	@Expose
	@Column(name = "AssetCode", nullable = false)
	private String assetCode;

	@Expose
	@Column(name = "AssetName", nullable = false)
	private String assetName;

	@Expose
	@Column(name = "AssetDescription")
	private String assetDescription;

	@Expose
	@Column(name = "Status", nullable = false)
	private String status;

	@Expose
	@Column(name = "Deleted", nullable = false)
	private Boolean deleted;

	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;

	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;

	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;

	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
