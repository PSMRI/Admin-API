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
package com.iemr.admin.repository.username;

import java.util.List;

/**
 * Registry of the tables swept when a username is renamed.
 *
 * <p>{@code CreatedBy}/{@code ModifiedBy} across AMRIT store the username as a
 * denormalised string rather than a UserID foreign key, so a rename has to be
 * propagated by hand. This list is deliberately scoped to the RMNCH/FLW tables
 * the field workflow reads back — it is NOT every table carrying an audit
 * column (there are ~594 of those across db_iemr and db_identity). Renaming a
 * user therefore leaves the old username intact in tables outside this list.
 *
 * <p>Names here are compile-time constants and are interpolated into SQL. They
 * must never be sourced from request input; only the username values are bound
 * as parameters.
 */
public final class UsernameAuditTables {

	private UsernameAuditTables() {
	}

	/** One sweepable table and the audit columns it happens to use. */
	public static final class AuditTable {
		private final String qualifiedName;
		private final String createdByColumn;
		private final String modifiedByColumn;

		public AuditTable(String qualifiedName, String createdByColumn, String modifiedByColumn) {
			this.qualifiedName = qualifiedName;
			this.createdByColumn = createdByColumn;
			this.modifiedByColumn = modifiedByColumn;
		}

		public String getQualifiedName() {
			return qualifiedName;
		}

		public String getCreatedByColumn() {
			return createdByColumn;
		}

		public String getModifiedByColumn() {
			return modifiedByColumn;
		}
	}

	private static AuditTable pascal(String qualifiedName) {
		return new AuditTable(qualifiedName, "CreatedBy", "ModifiedBy");
	}

	private static AuditTable snake(String qualifiedName) {
		return new AuditTable(qualifiedName, "created_by", "updated_by");
	}

	/**
	 * Column naming was verified against the Flyway migrations in AMRIT-DB:
	 * every db_identity table below uses CreatedBy/ModifiedBy, while db_iemr is
	 * split — the newer RMNCH register tables use created_by/updated_by and the
	 * older visit tables use CreatedBy/ModifiedBy.
	 */
	public static final List<AuditTable> TABLES = List.of(
			// --- db_identity : CreatedBy / ModifiedBy ---
			pascal("db_identity.i_beneficiarydetails_rmnch"),
			pascal("db_identity.i_beneficiaryfamilymapping"),
			pascal("db_identity.i_beneficiarydetails"),
			pascal("db_identity.i_beneficiarymapping"),
			pascal("db_identity.i_beneficiaryidentity"),
			pascal("db_identity.i_householddetails"),
			pascal("db_identity.i_beneficiaryimage"),
			pascal("db_identity.i_beneficiaryaddress"),
			pascal("db_identity.i_beneficiaryservicemapping"),
			pascal("db_identity.m_beneficiaryregidmapping"),
			pascal("db_identity.i_bornbirthdeatils"),
			pascal("db_identity.i_beneficiarycontacts"),
			pascal("db_identity.i_beneficiaryconsent"),
			pascal("db_identity.i_benfamilytag"),

			// --- db_iemr : created_by / updated_by ---
			snake("db_iemr.eligible_couple_tracking"),
			snake("db_iemr.t_pregnant_woman_register"),
			snake("db_iemr.t_eligible_couple_register"),
			snake("db_iemr.t_delivery_outcome"),
			snake("db_iemr.t_infant_register"),
			snake("db_iemr.t_pnc_visit"),
			snake("db_iemr.t_anc_visit"),
			snake("db_iemr.t_child_register"),
			snake("db_iemr.t_pmsma"),

			// --- db_iemr : CreatedBy / ModifiedBy ---
			pascal("db_iemr.t_cbacdetails"),
			pascal("db_iemr.t_pnccare"),
			pascal("db_iemr.t_anccare"),
			pascal("db_iemr.t_benvisitdetail"),
			pascal("db_iemr.t_childvaccinedetail1"));
}
