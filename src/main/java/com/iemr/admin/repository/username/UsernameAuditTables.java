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
 * <p>Each entry carries its primary key, because the rename drives updates by
 * PK through a derived table rather than filtering the UPDATE directly. Every
 * key here was verified against the AMRIT-DB migrations.
 *
 * <p>Names here are compile-time constants and are interpolated into SQL. They
 * must never be sourced from request input; only the username values are bound
 * as parameters.
 */
public final class UsernameAuditTables {

	private UsernameAuditTables() {
	}

	/** One sweepable table: its audit columns and the primary key driving the update. */
	public static final class AuditTable {
		private final String qualifiedName;
		private final String createdByColumn;
		private final String modifiedByColumn;
		private final String primaryKeyColumn;

		public AuditTable(String qualifiedName, String createdByColumn, String modifiedByColumn,
				String primaryKeyColumn) {
			this.qualifiedName = qualifiedName;
			this.createdByColumn = createdByColumn;
			this.modifiedByColumn = modifiedByColumn;
			this.primaryKeyColumn = primaryKeyColumn;
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

		public String getPrimaryKeyColumn() {
			return primaryKeyColumn;
		}
	}

	private static AuditTable pascal(String qualifiedName, String primaryKeyColumn) {
		return new AuditTable(qualifiedName, "CreatedBy", "ModifiedBy", primaryKeyColumn);
	}

	private static AuditTable snake(String qualifiedName, String primaryKeyColumn) {
		return new AuditTable(qualifiedName, "created_by", "updated_by", primaryKeyColumn);
	}

	/**
	 * Column naming was verified against the Flyway migrations in AMRIT-DB:
	 * every db_identity table below uses CreatedBy/ModifiedBy, while db_iemr is
	 * split — the newer RMNCH register tables use created_by/updated_by and the
	 * older visit tables use CreatedBy/ModifiedBy.
	 *
	 * <p>eligible_couple_tracking is lower case here on purpose: the schema
	 * creates it that way and MySQL table names are case sensitive on Linux.
	 */
	public static final List<AuditTable> TABLES = List.of(
			// --- db_identity : CreatedBy / ModifiedBy ---
			pascal("db_identity.i_beneficiarydetails_rmnch", "beneficiaryDetails_RmnchId"),
			pascal("db_identity.i_beneficiaryfamilymapping", "BenFamilyMapId"),
			pascal("db_identity.i_beneficiarydetails", "BeneficiaryDetailsId"),
			pascal("db_identity.i_beneficiarymapping", "BenMapId"),
			pascal("db_identity.i_beneficiaryidentity", "BenIdentityId"),
			pascal("db_identity.i_householddetails", "houseHoldDetailsId"),
			pascal("db_identity.i_beneficiaryimage", "BenImageId"),
			pascal("db_identity.i_beneficiaryaddress", "BenAddressID"),
			pascal("db_identity.i_beneficiaryservicemapping", "BenServiceMapID"),
			pascal("db_identity.m_beneficiaryregidmapping", "BenRegId"),
			pascal("db_identity.i_bornbirthdeatils", "BornBirthDeatilsId"),
			pascal("db_identity.i_beneficiarycontacts", "BenContactsID"),
			pascal("db_identity.i_beneficiaryconsent", "BenConsentID"),
			pascal("db_identity.i_benfamilytag", "BenFamilyTagId"),

			// --- db_iemr : created_by / updated_by ---
			snake("db_iemr.eligible_couple_tracking", "id"),
			snake("db_iemr.t_pregnant_woman_register", "id"),
			snake("db_iemr.t_eligible_couple_register", "id"),
			snake("db_iemr.t_delivery_outcome", "id"),
			snake("db_iemr.t_infant_register", "id"),
			snake("db_iemr.t_pnc_visit", "ID"),
			snake("db_iemr.t_anc_visit", "ID"),
			snake("db_iemr.t_child_register", "ID"),
			snake("db_iemr.t_pmsma", "id"),

			// --- db_iemr : CreatedBy / ModifiedBy ---
			pascal("db_iemr.t_cbacdetails", "id"),
			pascal("db_iemr.t_pnccare", "id"),
			pascal("db_iemr.t_anccare", "ID"),
			pascal("db_iemr.t_benvisitdetail", "BenVisitID"),
			pascal("db_iemr.t_childvaccinedetail1", "ID"));
}
