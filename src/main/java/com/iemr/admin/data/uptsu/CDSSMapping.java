package com.iemr.admin.data.uptsu;

import java.sql.Timestamp;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_hwc_cdss_mapping")
public class CDSSMapping {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "ProviderServiceMapID")
	private Integer psmId;
	
	@Column(name = "ServiceID")
	private Integer serviceId;
	
	@Column(name = "StateID")
	private Integer stateId;
	
	@Column(name = "IsCdss")
	private Boolean isCdss;
	
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;
	
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;
	
	@Column(name = "CreatedBy")
	private String createdBy;

}
