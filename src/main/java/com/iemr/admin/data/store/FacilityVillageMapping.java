package com.iemr.admin.data.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.sql.Timestamp;

import org.hibernate.annotations.Formula;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;

@Entity
@Table(name = "facility_village_mapping")
@Data
public class FacilityVillageMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    @Column(name = "FacilityVillageMappingID")
    private Long facilityVillageMappingID;

    @Expose
    @Column(name = "FacilityID")
    private Integer facilityID;

    @Expose
    @Column(name = "DistrictBranchID")
    private Integer districtBranchID;

    @Expose
    @Formula("(SELECT dbm.VillageName FROM m_DistrictBranchMapping dbm WHERE dbm.DistrictBranchID = {alias}.DistrictBranchID)")
    private String villageName;

    @Expose
    @Column(name = "ProviderServiceMapID")
    private Integer providerServiceMapID = 0;

    @Expose
    @Column(name = "Deleted", insertable = true, updatable = true)
    private Boolean deleted = false;

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
