/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.admin.data.employeemaster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.Formula;

import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import lombok.Data;

@Entity
@Table(name = "asha_supervisor_mapping")
@Data
public class AshaSupervisorMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "id")
	private Long id;

	@Expose
	@Column(name = "supervisorUserID")
	private Integer supervisorUserID;

	@Expose
	@Column(name = "ashaUserID")
	private Integer ashaUserID;

	@Expose
	@Column(name = "facilityID")
	private Integer facilityID;

	@Expose
	@Formula("(SELECT u.FirstName FROM m_User u WHERE u.UserID = ashaUserID)")
	private String ashaFirstName;

	@Expose
	@Formula("(SELECT u.LastName FROM m_User u WHERE u.UserID = ashaUserID)")
	private String ashaLastName;

	@Expose
	@Column(name = "deleted", insertable = false, updatable = true)
	private Boolean deleted;

	@Expose
	@Column(name = "createdBy")
	private String createdBy;

	@Expose
	@Column(name = "createdDate", insertable = false, updatable = false)
	private Timestamp createdDate;

	@Expose
	@Column(name = "modifiedBy")
	private String modifiedBy;

	@Expose
	@Column(name = "lastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;

	@Transient
	@Expose
	private List<Integer> facilityIDs;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
}
