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
package com.iemr.admin.repo.nikshaytransferout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshaytransferout.NikshayContactTestsDTO;
import com.iemr.admin.data.nikshaytransferout.NikshayDesignatedPhiDTO;

/** Reads what the transfer out phase needs besides the phase status rows. */
@Repository
public class NikshayTransferOutRepository {

	// Each village's newest active designated PHI, with its name from the facility master.
	// The two tables have different collations, so the codes are compared in one explicit
	// collation.
	private static final String DESIGNATED_PHI_SQL = """
			SELECT p.village_id, p.designated_phi_id,
			       (SELECT f.FacilityName
			        FROM db_iemr.m_nikshay_facility f
			        WHERE f.NikshayCode COLLATE utf8mb4_unicode_ci = p.designated_phi_id COLLATE utf8mb4_unicode_ci
			          AND (f.Deleted = 0 OR f.Deleted IS NULL)
			        LIMIT 1) AS phi_name
			FROM db_iemr.m_nikshay_transfer_out_phi_village_mapping p
			WHERE p.village_id IN (:villageIds)
			  AND p.status = 'active'
			  AND p.id = (SELECT MAX(p2.id)
			              FROM db_iemr.m_nikshay_transfer_out_phi_village_mapping p2
			              WHERE p2.village_id = p.village_id
			                AND p2.status = 'active')
			""";

	// For each given beneficiary (by BenRegId): whether any completed TrueNat (MTB) result is
	// "TB Positive", their latest completed chest X-ray result, and whether their latest TB
	// screening shows a symptom (same symptom check as the enrolment's Symptom field).
	private static final String CONTACT_TESTS_SQL = """
			SELECT ps.ben_reg_id,
			       EXISTS (SELECT 1
			               FROM db_iemr.tb_diagnostic_order o
			               JOIN db_iemr.tb_diagnostic_result r
			                 ON r.external_order_id = o.external_order_id AND r.deleted = 0
			               WHERE o.beneficiary_id = ps.beneficiary_id
			                 AND o.order_type = 'MTB'
			                 AND o.status = 'COMPLETED' AND o.deleted = 0
			                 AND LOWER(TRIM(r.result_summary)) = 'tb positive') AS naat_positive,
			       (SELECT r.result_summary
			        FROM db_iemr.tb_diagnostic_order o
			        JOIN db_iemr.tb_diagnostic_result r
			          ON r.external_order_id = o.external_order_id AND r.deleted = 0
			        WHERE o.beneficiary_id = ps.beneficiary_id
			          AND o.order_type = 'XRAY_CHEST'
			          AND o.status = 'COMPLETED' AND o.deleted = 0
			          AND NULLIF(TRIM(r.result_summary), '') IS NOT NULL
			        ORDER BY o.created_date DESC, o.id DESC
			        LIMIT 1) AS latest_xray,
			       EXISTS (SELECT 1
			               FROM db_iemr.tb_screening scr
			               WHERE scr.id = (SELECT MAX(s2.id)
			                               FROM db_iemr.tb_screening s2
			                               WHERE s2.ben_reg_id = ps.ben_reg_id AND s2.deleted = 0)
			                 AND (LOWER(TRIM(IFNULL(scr.cough_check, ''))) IN ('1', 'yes', 'y', 'true')
			                      OR LOWER(TRIM(IFNULL(scr.fever_check, ''))) IN ('1', 'yes', 'y', 'true')
			                      OR LOWER(TRIM(IFNULL(scr.weight_check, ''))) IN ('1', 'yes', 'y', 'true')
			                      OR LOWER(TRIM(IFNULL(scr.sweats_check, ''))) IN ('1', 'yes', 'y', 'true'))) AS symptomatic
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			WHERE ps.phase = 'ENROLMENT'
			  AND ps.ben_reg_id IN (:benRegIds)
			""";

	// The given beneficiaries already transferred out: a SUCCESS transfer in the job history
	// log (so a reprocess never transfers them again).
	private static final String TRANSFERRED_SQL = """
			SELECT DISTINCT j.ben_id
			FROM db_iemr.nikshay_worker_job_history_log j
			WHERE j.phase = 'TRANSFER_OUT'
			  AND j.status = 'SUCCESS'
			  AND j.ben_id IN (:benRegIds)
			""";

	@Autowired
	private DataSource dataSource;

	/** The newest active designated PHI of each given village, by village ID. */
	public Map<Integer, NikshayDesignatedPhiDTO> findDesignatedPhis(List<Integer> villageIds) {
		Map<Integer, NikshayDesignatedPhiDTO> phis = new HashMap<>();
		if (villageIds.isEmpty()) {
			return phis;
		}
		List<NikshayDesignatedPhiDTO> rows = new NamedParameterJdbcTemplate(dataSource).query(DESIGNATED_PHI_SQL,
				new MapSqlParameterSource("villageIds", villageIds),
				(rs, rowNum) -> new NikshayDesignatedPhiDTO(rs.getObject("village_id", Integer.class),
						rs.getString("designated_phi_id"), rs.getString("phi_name")));
		for (NikshayDesignatedPhiDTO row : rows) {
			phis.put(row.getVillageId(), row);
		}
		return phis;
	}

	/** The test results and symptoms of each given beneficiary, by BenRegId. */
	public Map<Long, NikshayContactTestsDTO> findContactTests(List<Long> benRegIds) {
		Map<Long, NikshayContactTestsDTO> tests = new HashMap<>();
		if (benRegIds.isEmpty()) {
			return tests;
		}
		List<NikshayContactTestsDTO> rows = new NamedParameterJdbcTemplate(dataSource).query(CONTACT_TESTS_SQL,
				new MapSqlParameterSource("benRegIds", benRegIds),
				(rs, rowNum) -> new NikshayContactTestsDTO(rs.getObject("ben_reg_id", Long.class),
						rs.getBoolean("naat_positive"), rs.getString("latest_xray"), rs.getBoolean("symptomatic")));
		for (NikshayContactTestsDTO row : rows) {
			tests.put(row.getBenRegId(), row);
		}
		return tests;
	}

	/** The given beneficiaries already transferred out (see TRANSFERRED_SQL). */
	public Set<Long> findTransferred(List<Long> benRegIds) {
		if (benRegIds.isEmpty()) {
			return new HashSet<>();
		}
		return new HashSet<>(new NamedParameterJdbcTemplate(dataSource).queryForList(TRANSFERRED_SQL,
				new MapSqlParameterSource("benRegIds", benRegIds), Long.class));
	}
}
