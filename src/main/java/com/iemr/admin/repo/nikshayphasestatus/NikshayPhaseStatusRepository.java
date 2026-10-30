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
package com.iemr.admin.repo.nikshayphasestatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryType;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseCandidate;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseStatusCount;

/**
 * Reads and writes db_iemr.nikshay_beneficiary_phase_status (AMRIT-DB V134): one row per
 * beneficiary per Ni-kshay phase, updated in place. Plain SQL through JdbcTemplate, as in
 * NikshayIdGenerateRepository, because the status list joins db_identity for the names.
 */
@Repository
public class NikshayPhaseStatusRepository {

	// Insert or update the beneficiary's row for the phase. An attempt is counted when
	// countAttempt is 1, except when the row is already IN_PROGRESS (that try was counted
	// when it started). attempts is assigned before status so it still sees the old status.
	private static final String SAVE_SQL = "INSERT INTO db_iemr.nikshay_beneficiary_phase_status "
			+ "(ben_reg_id, beneficiary_id, beneficiary_type, index_ben_reg_id, phase, status, failure_reason, "
			+ " error_detail, attempts, nikshay_id, episode_id, last_attempt_at, completed_at) "
			+ "VALUES (:benRegId, ( "
			+ "    SELECT r.BeneficiaryID FROM db_identity.m_beneficiaryregidmapping r "
			+ "    WHERE r.BenRegId = :benRegId AND r.Deleted = 0 LIMIT 1), "
			+ "  :beneficiaryType, :indexBenRegId, :phase, :status, :failureReason, :errorDetail, :countAttempt, "
			+ "  :nikshayId, :episodeId, :lastAttemptAt, :completedAt) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "  attempts = attempts + IF(:countAttempt = 1 AND status <> 'IN_PROGRESS', 1, 0), "
			+ "  beneficiary_type = VALUES(beneficiary_type), "
			+ "  index_ben_reg_id = COALESCE(VALUES(index_ben_reg_id), index_ben_reg_id), "
			+ "  status = VALUES(status), "
			+ "  failure_reason = VALUES(failure_reason), "
			+ "  error_detail = VALUES(error_detail), "
			+ "  nikshay_id = COALESCE(VALUES(nikshay_id), nikshay_id), "
			+ "  episode_id = COALESCE(VALUES(episode_id), episode_id), "
			+ "  last_attempt_at = COALESCE(VALUES(last_attempt_at), last_attempt_at), "
			+ "  completed_at = VALUES(completed_at)";

	// A beneficiary's name: the details row by BenRegId, or through the mapping (as in the
	// household member query of NikshayIdGenerateRepository)
	private static String nameJoins(String benRegIdColumn, String alias) {
		return "LEFT JOIN db_identity.i_beneficiarydetails " + alias + "d ON " + alias + "d.BeneficiaryRegID = "
				+ benRegIdColumn + " AND (" + alias + "d.Deleted = 0 OR " + alias + "d.Deleted IS NULL) "
				+ "LEFT JOIN db_identity.i_beneficiarymapping " + alias + "m ON " + alias + "m.BenRegId = "
				+ benRegIdColumn + " AND " + alias + "m.Deleted = 0 "
				+ "LEFT JOIN db_identity.i_beneficiarydetails " + alias + "d2 ON " + alias + "d2.VanSerialNo = " + alias
				+ "m.BenDetailsId AND " + alias + "d2.VanID = " + alias + "m.VanID AND " + alias + "d2.Deleted = 0 ";
	}

	private static String name(String alias) {
		return "NULLIF(TRIM(CONCAT_WS(' ', COALESCE(" + alias + "d.FirstName, " + alias + "d2.FirstName), COALESCE("
				+ alias + "d.MiddleName, " + alias + "d2.MiddleName), COALESCE(" + alias + "d.LastName, " + alias
				+ "d2.LastName))), '')";
	}

	private static final String PHASE_ROWS_SQL = "SELECT ben_reg_id, phase, status, failure_reason, error_detail, "
			+ "attempts, nikshay_id, episode_id, last_attempt_at, completed_at "
			+ "FROM db_iemr.nikshay_beneficiary_phase_status WHERE ben_reg_id IN (:benRegIds)";

	private static final String SUMMARY_SQL = "SELECT phase, status, COUNT(*) AS cnt "
			+ "FROM db_iemr.nikshay_beneficiary_phase_status GROUP BY phase, status ORDER BY phase, status";

	// The queries below pick the beneficiaries waiting in a phase. A row is waiting when it is
	// PENDING, IN_PROGRESS (left by a run that stopped), or FAILED with an error that is
	// retried automatically: a missing field or an error / invalid test result (checked again
	// every run, so a fix or a retest is picked up), or Ni-kshay down / login rejected while
	// attempts < :maxAttempts. Other failures wait for a reprocess, which sets the row back to
	// PENDING. Every phase needs the beneficiary's ENROLMENT to be DONE. Oldest change first.

	// CONTACTS: index patients waiting to have their household contacts added.
	private static final String WAITING_FOR_CONTACTS_SQL = """
			SELECT ps.ben_reg_id, ps.beneficiary_id, ps.beneficiary_type, ps.index_ben_reg_id, ps.status
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			JOIN db_iemr.nikshay_beneficiary_phase_status enrolment
			  ON enrolment.ben_reg_id = ps.ben_reg_id
			 AND enrolment.phase = 'ENROLMENT'
			 AND enrolment.status = 'DONE'
			WHERE ps.phase = 'CONTACTS'
			  AND (ps.status IN ('PENDING', 'IN_PROGRESS')
			       OR (ps.status = 'FAILED'
			           AND (ps.failure_reason IN ('MANDATORY_FIELD_MISSING', 'TEST_RESULT_INVALID')
			                OR (ps.failure_reason IN ('NIKSHAY_UNAVAILABLE', 'LOGIN_REJECTED')
			                    AND ps.attempts < :maxAttempts))))
			ORDER BY ps.updated_at, ps.id
			LIMIT :limit
			""";

	// XRAY_UPLOAD: beneficiaries not transferred out yet (once TRANSFER_OUT is DONE their tests
	// are no longer uploaded) with a completed chest X-ray result (XRAY_CHEST order with a
	// result entered) who are either
	// 1. waiting in the phase, or
	// 2. DONE, with a newer valid result (not an error / invalid run) not uploaded yet, i.e.
	//    no SUCCESS entry for the order in the job history log.
	// The log table (V132) and tb_diagnostic_order have different collations, so the order
	// IDs are compared in one explicit collation.
	private static final String WAITING_FOR_XRAY_UPLOAD_SQL = """
			SELECT ps.ben_reg_id, ps.beneficiary_id, ps.beneficiary_type, ps.index_ben_reg_id, ps.status
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			JOIN db_iemr.nikshay_beneficiary_phase_status enrolment
			  ON enrolment.ben_reg_id = ps.ben_reg_id
			 AND enrolment.phase = 'ENROLMENT'
			 AND enrolment.status = 'DONE'
			WHERE ps.phase = 'XRAY_UPLOAD'
			  AND (
			    ((ps.status IN ('PENDING', 'IN_PROGRESS')
			      OR (ps.status = 'FAILED'
			          AND (ps.failure_reason IN ('MANDATORY_FIELD_MISSING', 'TEST_RESULT_INVALID')
			               OR (ps.failure_reason IN ('NIKSHAY_UNAVAILABLE', 'LOGIN_REJECTED')
			                   AND ps.attempts < :maxAttempts))))
			     AND EXISTS (SELECT 1
			                 FROM db_iemr.tb_diagnostic_order o
			                 JOIN db_iemr.tb_diagnostic_result r
			                   ON r.external_order_id = o.external_order_id AND r.deleted = 0
			                 WHERE o.beneficiary_id = ps.beneficiary_id
			                   AND o.order_type = 'XRAY_CHEST'
			                   AND o.status = 'COMPLETED' AND o.deleted = 0
			                   AND NULLIF(TRIM(r.result_summary), '') IS NOT NULL))
			    OR
			    (ps.status = 'DONE'
			     AND EXISTS (SELECT 1
			                 FROM db_iemr.tb_diagnostic_order o
			                 JOIN db_iemr.tb_diagnostic_result r
			                   ON r.external_order_id = o.external_order_id AND r.deleted = 0
			                 WHERE o.beneficiary_id = ps.beneficiary_id
			                   AND o.order_type = 'XRAY_CHEST'
			                   AND o.status = 'COMPLETED' AND o.deleted = 0
			                   AND NULLIF(TRIM(r.result_summary), '') IS NOT NULL
			                   AND LOWER(TRIM(r.result_summary)) NOT LIKE 'error%'
			                   AND LOWER(r.result_summary) NOT LIKE '%invalid%'
			                   AND NOT EXISTS (SELECT 1
			                                   FROM db_iemr.nikshay_worker_job_history_log j
			                                   WHERE j.reference_id COLLATE utf8mb4_unicode_ci
			                                         = o.external_order_id COLLATE utf8mb4_unicode_ci
			                                     AND j.status = 'SUCCESS')))
			  )
			  AND NOT EXISTS (SELECT 1
			                  FROM db_iemr.nikshay_beneficiary_phase_status transfer
			                  WHERE transfer.ben_reg_id = ps.ben_reg_id
			                    AND transfer.phase = 'TRANSFER_OUT'
			                    AND transfer.status = 'DONE')
			ORDER BY ps.updated_at, ps.id
			LIMIT :limit
			""";

	// NAAT_UPLOAD: the same as XRAY_UPLOAD for TrueNat results (MTB and MDR_RIF orders), and
	// only once the chest X-ray is uploaded: the beneficiary's XRAY_UPLOAD is DONE or
	// NOT_APPLICABLE, or they have no X-ray order at all.
	private static final String WAITING_FOR_NAAT_UPLOAD_SQL = """
			SELECT ps.ben_reg_id, ps.beneficiary_id, ps.beneficiary_type, ps.index_ben_reg_id, ps.status
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			JOIN db_iemr.nikshay_beneficiary_phase_status enrolment
			  ON enrolment.ben_reg_id = ps.ben_reg_id
			 AND enrolment.phase = 'ENROLMENT'
			 AND enrolment.status = 'DONE'
			WHERE ps.phase = 'NAAT_UPLOAD'
			  AND (
			    ((ps.status IN ('PENDING', 'IN_PROGRESS')
			      OR (ps.status = 'FAILED'
			          AND (ps.failure_reason IN ('MANDATORY_FIELD_MISSING', 'TEST_RESULT_INVALID')
			               OR (ps.failure_reason IN ('NIKSHAY_UNAVAILABLE', 'LOGIN_REJECTED')
			                   AND ps.attempts < :maxAttempts))))
			     AND EXISTS (SELECT 1
			                 FROM db_iemr.tb_diagnostic_order o
			                 JOIN db_iemr.tb_diagnostic_result r
			                   ON r.external_order_id = o.external_order_id AND r.deleted = 0
			                 WHERE o.beneficiary_id = ps.beneficiary_id
			                   AND o.order_type IN ('MTB', 'MDR_RIF')
			                   AND o.status = 'COMPLETED' AND o.deleted = 0
			                   AND NULLIF(TRIM(r.result_summary), '') IS NOT NULL))
			    OR
			    (ps.status = 'DONE'
			     AND EXISTS (SELECT 1
			                 FROM db_iemr.tb_diagnostic_order o
			                 JOIN db_iemr.tb_diagnostic_result r
			                   ON r.external_order_id = o.external_order_id AND r.deleted = 0
			                 WHERE o.beneficiary_id = ps.beneficiary_id
			                   AND o.order_type IN ('MTB', 'MDR_RIF')
			                   AND o.status = 'COMPLETED' AND o.deleted = 0
			                   AND NULLIF(TRIM(r.result_summary), '') IS NOT NULL
			                   AND LOWER(TRIM(r.result_summary)) NOT LIKE 'error%'
			                   AND LOWER(r.result_summary) NOT LIKE '%invalid%'
			                   AND NOT EXISTS (SELECT 1
			                                   FROM db_iemr.nikshay_worker_job_history_log j
			                                   WHERE j.reference_id COLLATE utf8mb4_unicode_ci
			                                         = o.external_order_id COLLATE utf8mb4_unicode_ci
			                                     AND j.status = 'SUCCESS')))
			  )
			  AND (EXISTS (SELECT 1
			               FROM db_iemr.nikshay_beneficiary_phase_status xray
			               WHERE xray.ben_reg_id = ps.ben_reg_id
			                 AND xray.phase = 'XRAY_UPLOAD'
			                 AND xray.status IN ('DONE', 'NOT_APPLICABLE'))
			       OR NOT EXISTS (SELECT 1
			                      FROM db_iemr.tb_diagnostic_order xo
			                      WHERE xo.beneficiary_id = ps.beneficiary_id
			                        AND xo.order_type = 'XRAY_CHEST'
			                        AND xo.deleted = 0))
			  AND NOT EXISTS (SELECT 1
			                  FROM db_iemr.nikshay_beneficiary_phase_status transfer
			                  WHERE transfer.ben_reg_id = ps.ben_reg_id
			                    AND transfer.phase = 'TRANSFER_OUT'
			                    AND transfer.status = 'DONE')
			ORDER BY ps.updated_at, ps.id
			LIMIT :limit
			""";

	// TRANSFER_OUT: beneficiaries whose X-ray and NAAT uploads are both DONE or NOT_APPLICABLE.
	// Besides the usual waiting rule, a row FAILED with DESIGNATED_PHI_NOT_MAPPED is tried
	// again every run, so the transfer goes once the village's designated PHI is added.
	// Who is actually transferred is decided by the transfer out run.
	private static final String WAITING_FOR_TRANSFER_OUT_SQL = """
			SELECT ps.ben_reg_id, ps.beneficiary_id, ps.beneficiary_type, ps.index_ben_reg_id, ps.status
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			JOIN db_iemr.nikshay_beneficiary_phase_status enrolment
			  ON enrolment.ben_reg_id = ps.ben_reg_id
			 AND enrolment.phase = 'ENROLMENT'
			 AND enrolment.status = 'DONE'
			JOIN db_iemr.nikshay_beneficiary_phase_status xray
			  ON xray.ben_reg_id = ps.ben_reg_id
			 AND xray.phase = 'XRAY_UPLOAD'
			 AND xray.status IN ('DONE', 'NOT_APPLICABLE')
			JOIN db_iemr.nikshay_beneficiary_phase_status naat
			  ON naat.ben_reg_id = ps.ben_reg_id
			 AND naat.phase = 'NAAT_UPLOAD'
			 AND naat.status IN ('DONE', 'NOT_APPLICABLE')
			WHERE ps.phase = 'TRANSFER_OUT'
			  AND (ps.status IN ('PENDING', 'IN_PROGRESS')
			       OR (ps.status = 'FAILED'
			           AND (ps.failure_reason IN ('MANDATORY_FIELD_MISSING', 'TEST_RESULT_INVALID',
			                                      'DESIGNATED_PHI_NOT_MAPPED')
			                OR (ps.failure_reason IN ('NIKSHAY_UNAVAILABLE', 'LOGIN_REJECTED')
			                    AND ps.attempts < :maxAttempts))))
			ORDER BY ps.updated_at, ps.id
			LIMIT :limit
			""";

	// The given beneficiaries whose NAAT must still wait for the chest X-ray: they have an
	// X-ray order, and their XRAY_UPLOAD is not DONE or NOT_APPLICABLE (still to come or
	// failed). Read from their ENROLMENT rows.
	private static final String WAITING_FOR_XRAY_SQL = """
			SELECT ps.ben_reg_id
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			WHERE ps.phase = 'ENROLMENT'
			  AND ps.ben_reg_id IN (:benRegIds)
			  AND NOT EXISTS (SELECT 1
			                  FROM db_iemr.nikshay_beneficiary_phase_status xray
			                  WHERE xray.ben_reg_id = ps.ben_reg_id
			                    AND xray.phase = 'XRAY_UPLOAD'
			                    AND xray.status IN ('DONE', 'NOT_APPLICABLE'))
			  AND EXISTS (SELECT 1
			              FROM db_iemr.tb_diagnostic_order xo
			              WHERE xo.beneficiary_id = ps.beneficiary_id
			                AND xo.order_type = 'XRAY_CHEST'
			                AND xo.deleted = 0)
			""";

	// The given beneficiaries whose row for the phase is FAILED with an error that is not
	// retried automatically (the opposite of the waiting rule above): they wait for a reprocess.
	private static final String WAITING_FOR_REPROCESS_SQL = """
			SELECT ps.ben_reg_id
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			WHERE ps.phase = :phase
			  AND ps.ben_reg_id IN (:benRegIds)
			  AND ps.status = 'FAILED'
			  AND NOT (ps.failure_reason IN ('MANDATORY_FIELD_MISSING', 'TEST_RESULT_INVALID')
			           OR (ps.failure_reason IN ('NIKSHAY_UNAVAILABLE', 'LOGIN_REJECTED')
			               AND ps.attempts < :maxAttempts))
			""";

	// Whether the beneficiary (BeneficiaryID) has a chest X-ray order, in any status.
	private static final String HAS_XRAY_ORDER_SQL = """
			SELECT COUNT(*)
			FROM db_iemr.tb_diagnostic_order o
			WHERE o.beneficiary_id = :beneficiaryId
			  AND o.order_type = 'XRAY_CHEST'
			  AND o.deleted = 0
			""";

	// Whether the beneficiary (BeneficiaryID) has a TrueNat order (MTB or MDR_RIF), in any status.
	private static final String HAS_NAAT_ORDER_SQL = """
			SELECT COUNT(*)
			FROM db_iemr.tb_diagnostic_order o
			WHERE o.beneficiary_id = :beneficiaryId
			  AND o.order_type IN ('MTB', 'MDR_RIF')
			  AND o.deleted = 0
			""";

	// The given beneficiaries' rows for the phase, whatever their status.
	private static final String ROWS_SQL = """
			SELECT ps.ben_reg_id, ps.beneficiary_id, ps.beneficiary_type, ps.index_ben_reg_id, ps.status
			FROM db_iemr.nikshay_beneficiary_phase_status ps
			WHERE ps.phase = :phase
			  AND ps.ben_reg_id IN (:benRegIds)
			""";

	private static final String RESET_TO_PENDING_SQL = "UPDATE db_iemr.nikshay_beneficiary_phase_status "
			+ "SET status = 'PENDING', failure_reason = NULL, error_detail = :note, attempts = 0, completed_at = NULL "
			+ "WHERE phase = :phase AND ben_reg_id IN (:benRegIds)";

	@Autowired
	private DataSource dataSource;

	/**
	 * Saves the beneficiary's status for the phase (insert or update). countAttempt says
	 * whether this is a try of the phase (IN_PROGRESS, DONE, FAILED) or not (PENDING,
	 * NOT_APPLICABLE).
	 */
	public void save(Long benRegId, String beneficiaryType, Long indexBenRegId, String phase, String status,
			String failureReason, String errorDetail, boolean countAttempt, String nikshayId, String episodeId,
			Timestamp lastAttemptAt, Timestamp completedAt) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("benRegId", benRegId);
		params.addValue("beneficiaryType", beneficiaryType);
		params.addValue("indexBenRegId", indexBenRegId);
		params.addValue("phase", phase);
		params.addValue("status", status);
		params.addValue("failureReason", failureReason);
		params.addValue("errorDetail", errorDetail);
		params.addValue("countAttempt", countAttempt ? 1 : 0);
		params.addValue("nikshayId", nikshayId);
		params.addValue("episodeId", episodeId);
		params.addValue("lastAttemptAt", lastAttemptAt);
		params.addValue("completedAt", completedAt);
		new NamedParameterJdbcTemplate(dataSource).update(SAVE_SQL, params);
	}

	/**
	 * One page of beneficiaries matching the filters, newest change first. Every filter is
	 * optional: phase / status (status NOT_STARTED = no row for the phase), failedOnly
	 * (failed in the phase, or in any phase), failureReason, beneficiaryType, and search
	 * (BenRegId, or part of the name). Each row: ben_reg_id, beneficiary_id,
	 * beneficiary_type, index_ben_reg_id, name, index_name.
	 */
	public List<Map<String, Object>> findBeneficiaries(String phase, String status, boolean failedOnly,
			String failureReason, String beneficiaryType, String search, int page, int size) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String sql = "SELECT b.ben_reg_id, b.beneficiary_id, b.beneficiary_type, b.index_ben_reg_id, "
				+ name("b") + " AS name, " + name("i") + " AS index_name "
				+ filteredBeneficiariesSql(phase, status, failedOnly, failureReason, beneficiaryType, search, params)
				+ "ORDER BY b.updated_at DESC, b.ben_reg_id DESC LIMIT :size OFFSET :offset";
		params.addValue("size", size);
		params.addValue("offset", page * size);
		return new NamedParameterJdbcTemplate(dataSource).queryForList(sql, params);
	}

	/** How many beneficiaries match the same filters as findBeneficiaries. */
	public long countBeneficiaries(String phase, String status, boolean failedOnly, String failureReason,
			String beneficiaryType, String search) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String sql = "SELECT COUNT(*) "
				+ filteredBeneficiariesSql(phase, status, failedOnly, failureReason, beneficiaryType, search, params);
		Long count = new NamedParameterJdbcTemplate(dataSource).queryForObject(sql, params, Long.class);
		return count == null ? 0 : count;
	}

	// FROM / WHERE part shared by findBeneficiaries and countBeneficiaries
	private static String filteredBeneficiariesSql(String phase, String status, boolean failedOnly,
			String failureReason, String beneficiaryType, String search, MapSqlParameterSource params) {
		StringBuilder having = new StringBuilder("HAVING 1 = 1 ");
		if (phase != null) {
			params.addValue("phase", phase);
		}
		if (status != null && status.equals("NOT_STARTED")) {
			if (phase != null) {
				having.append("AND SUM(phase = :phase) = 0 ");
			}
		} else if (status != null) {
			params.addValue("status", status);
			having.append(phase != null ? "AND SUM(phase = :phase AND status = :status) > 0 "
					: "AND SUM(status = :status) > 0 ");
		} else if (phase != null) {
			having.append("AND SUM(phase = :phase) > 0 ");
		}
		if (failedOnly) {
			having.append(phase != null ? "AND SUM(phase = :phase AND status = 'FAILED') > 0 "
					: "AND SUM(status = 'FAILED') > 0 ");
		}
		if (failureReason != null) {
			params.addValue("failureReason", failureReason);
			having.append("AND SUM(status = 'FAILED' AND failure_reason = :failureReason) > 0 ");
		}
		if (beneficiaryType != null) {
			params.addValue("beneficiaryType", beneficiaryType);
			having.append("AND MAX(beneficiary_type) = :beneficiaryType ");
		}
		String where = "WHERE 1 = 1 ";
		if (search != null) {
			if (search.matches("\\d+")) {
				params.addValue("searchId", Long.valueOf(search));
				where += "AND b.ben_reg_id = :searchId ";
			} else {
				params.addValue("searchName", "%" + search.toLowerCase() + "%");
				where += "AND LOWER(" + name("b") + ") LIKE :searchName ";
			}
		}
		return "FROM ( "
				+ "  SELECT ben_reg_id, MAX(beneficiary_id) AS beneficiary_id, MAX(beneficiary_type) AS beneficiary_type, "
				+ "    MAX(index_ben_reg_id) AS index_ben_reg_id, MAX(updated_at) AS updated_at "
				+ "  FROM db_iemr.nikshay_beneficiary_phase_status GROUP BY ben_reg_id " + having + ") b "
				+ nameJoins("b.ben_reg_id", "b") + nameJoins("b.index_ben_reg_id", "i") + where;
	}

	/** Every phase row of the given beneficiaries. */
	public List<Map<String, Object>> findPhaseRows(List<Long> benRegIds) {
		return new NamedParameterJdbcTemplate(dataSource).queryForList(PHASE_ROWS_SQL,
				new MapSqlParameterSource("benRegIds", benRegIds));
	}

	/** Up to limit index patients waiting in CONTACTS (see WAITING_FOR_CONTACTS_SQL). */
	public List<NikshayPhaseCandidate> findWaitingForContacts(int maxAttempts, int limit) {
		return findWaiting(WAITING_FOR_CONTACTS_SQL, maxAttempts, limit);
	}

	/** Up to limit beneficiaries waiting in XRAY_UPLOAD (see WAITING_FOR_XRAY_UPLOAD_SQL). */
	public List<NikshayPhaseCandidate> findWaitingForXrayUpload(int maxAttempts, int limit) {
		return findWaiting(WAITING_FOR_XRAY_UPLOAD_SQL, maxAttempts, limit);
	}

	/** Up to limit beneficiaries waiting in NAAT_UPLOAD (see WAITING_FOR_NAAT_UPLOAD_SQL). */
	public List<NikshayPhaseCandidate> findWaitingForNaatUpload(int maxAttempts, int limit) {
		return findWaiting(WAITING_FOR_NAAT_UPLOAD_SQL, maxAttempts, limit);
	}

	/** Up to limit beneficiaries waiting in TRANSFER_OUT (see WAITING_FOR_TRANSFER_OUT_SQL). */
	public List<NikshayPhaseCandidate> findWaitingForTransferOut(int maxAttempts, int limit) {
		return findWaiting(WAITING_FOR_TRANSFER_OUT_SQL, maxAttempts, limit);
	}

	/** Whether the beneficiary (BeneficiaryID) has a chest X-ray order (see HAS_XRAY_ORDER_SQL). */
	public boolean hasXrayOrder(Long beneficiaryId) {
		Integer count = new NamedParameterJdbcTemplate(dataSource).queryForObject(HAS_XRAY_ORDER_SQL,
				new MapSqlParameterSource("beneficiaryId", beneficiaryId), Integer.class);
		return count != null && count > 0;
	}

	/** Whether the beneficiary (BeneficiaryID) has a TrueNat order (see HAS_NAAT_ORDER_SQL). */
	public boolean hasNaatOrder(Long beneficiaryId) {
		Integer count = new NamedParameterJdbcTemplate(dataSource).queryForObject(HAS_NAAT_ORDER_SQL,
				new MapSqlParameterSource("beneficiaryId", beneficiaryId), Integer.class);
		return count != null && count > 0;
	}

	/** The given beneficiaries' rows for the phase; a beneficiary with no row is left out. */
	public List<NikshayPhaseCandidate> findRows(String phase, List<Long> benRegIds) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("phase", phase);
		params.addValue("benRegIds", benRegIds);
		return new NamedParameterJdbcTemplate(dataSource).query(ROWS_SQL, params, (rs, rowNum) -> mapCandidate(rs));
	}

	/** The given beneficiaries whose NAAT waits for the chest X-ray (see WAITING_FOR_XRAY_SQL). */
	public List<Long> findWaitingForXray(List<Long> benRegIds) {
		return new NamedParameterJdbcTemplate(dataSource).queryForList(WAITING_FOR_XRAY_SQL,
				new MapSqlParameterSource("benRegIds", benRegIds), Long.class);
	}

	/** The given beneficiaries waiting for a reprocess in the phase (see WAITING_FOR_REPROCESS_SQL). */
	public List<Long> findWaitingForReprocess(String phase, List<Long> benRegIds, int maxAttempts) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("phase", phase);
		params.addValue("benRegIds", benRegIds);
		params.addValue("maxAttempts", maxAttempts);
		return new NamedParameterJdbcTemplate(dataSource).queryForList(WAITING_FOR_REPROCESS_SQL, params, Long.class);
	}

	private List<NikshayPhaseCandidate> findWaiting(String sql, int maxAttempts, int limit) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("maxAttempts", maxAttempts);
		params.addValue("limit", limit);
		return new NamedParameterJdbcTemplate(dataSource).query(sql, params, (rs, rowNum) -> mapCandidate(rs));
	}

	/**
	 * Sets the given beneficiaries' rows for the phase back to PENDING with no attempts
	 * (reprocess). Returns the number of rows changed; beneficiaries with no row are skipped.
	 */
	public int resetToPending(String phase, List<Long> benRegIds, String note) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("phase", phase);
		params.addValue("benRegIds", benRegIds);
		params.addValue("note", note);
		return new NamedParameterJdbcTemplate(dataSource).update(RESET_TO_PENDING_SQL, params);
	}

	private NikshayPhaseCandidate mapCandidate(ResultSet rs) throws SQLException {
		String type = rs.getString("beneficiary_type");
		return new NikshayPhaseCandidate(rs.getObject("ben_reg_id", Long.class),
				rs.getObject("beneficiary_id", Long.class), type == null ? null : NikshayBeneficiaryType.valueOf(type),
				rs.getObject("index_ben_reg_id", Long.class), rs.getString("status"));
	}

	/** Number of beneficiaries per phase and status. */
	public List<NikshayPhaseStatusCount> countByPhaseAndStatus() {
		return new NamedParameterJdbcTemplate(dataSource).query(SUMMARY_SQL, new MapSqlParameterSource(),
				(rs, rowNum) -> mapCount(rs));
	}

	private NikshayPhaseStatusCount mapCount(ResultSet rs) throws SQLException {
		return new NikshayPhaseStatusCount(rs.getString("phase"), rs.getString("status"), rs.getLong("cnt"));
	}
}
