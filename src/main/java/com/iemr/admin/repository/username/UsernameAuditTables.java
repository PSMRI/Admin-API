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
import java.util.regex.Pattern;

public final class UsernameAuditTables {
	private UsernameAuditTables() {
	}

	private static final Pattern SQL_IDENTIFIER = Pattern.compile("\\w+(\\.\\w+)?");

	private static String identifier(String name) {
		if (name == null || !SQL_IDENTIFIER.matcher(name).matches()) {
			throw new IllegalArgumentException("Illegal SQL identifier: " + name);
		}
		return name;
	}

	public static final class AuditTable {
		private final String qualifiedName;
		private final String createdByColumn;
		private final String modifiedByColumn;
		private final String primaryKeyColumn;
		private final String renameSql;

		public AuditTable(String qualifiedName, String createdByColumn, String modifiedByColumn,
				String primaryKeyColumn) {
			this.qualifiedName = identifier(qualifiedName);
			this.createdByColumn = identifier(createdByColumn);
			this.modifiedByColumn = identifier(modifiedByColumn);
			this.primaryKeyColumn = identifier(primaryKeyColumn);
			this.renameSql = String.format(
					"UPDATE %1$s SET %2$s = :newUserName, %3$s = :newUserName "
							+ "WHERE %4$s IN (SELECT %4$s FROM (SELECT %4$s FROM %1$s WHERE %2$s = :oldUserName)"
							+ " AS temp)",
					this.qualifiedName, this.createdByColumn, this.modifiedByColumn, this.primaryKeyColumn);
		}

		public String getRenameSql() {
			return renameSql;
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

	public static final List<AuditTable> TABLES = List.of(
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

			snake("db_iemr.eligible_couple_tracking", "id"),
			snake("db_iemr.t_pregnant_woman_register", "id"),
			snake("db_iemr.t_eligible_couple_register", "id"),
			snake("db_iemr.t_delivery_outcome", "id"),
			snake("db_iemr.t_infant_register", "id"),
			snake("db_iemr.t_pnc_visit", "ID"),
			snake("db_iemr.t_anc_visit", "ID"),
			snake("db_iemr.t_child_register", "ID"),
			snake("db_iemr.t_pmsma", "id"),

			pascal("db_iemr.t_cbacdetails", "id"),
			pascal("db_iemr.t_pnccare", "id"),
			pascal("db_iemr.t_anccare", "ID"),
			pascal("db_iemr.t_benvisitdetail", "BenVisitID"),
			pascal("db_iemr.t_childvaccinedetail1", "ID"));
}
