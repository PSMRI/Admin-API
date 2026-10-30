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
package com.iemr.admin.repo.nikshayidgenerate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.nikshayidgenerate.NikshayEnrollResult;
import com.iemr.admin.data.nikshayidgenerate.NikshayFlowScenario;
import com.iemr.admin.data.nikshayidgenerate.NikshayHouseholdMemberDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayIdGenerateBeneficiaryDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayLinkStatus;

/**
 * Reads the beneficiaries that need a Ni-kshay ID: TB confirmed (or NAAT detected), alive,
 * and with no Ni-kshay ID yet. Plain SQL through JdbcTemplate (as in NikshayExportRepository)
 * because the query joins db_identity and db_iemr tables on the same MySQL server.
 */
@Repository
public class NikshayIdGenerateRepository {

	// Columns and joins of the eligibility query (Q1 of NIKSHAY_ENROLLMENT.sql), shared with
	// the all-negative household query
	private static final String BENEFICIARY_COLUMNS_SQL = "SELECT "
			+ "  m.BenRegId AS beneficiary_id, "
			+ "  hh.houseoldId, "
			+ "  d.FirstName, "
			+ "  NULLIF(TRIM(CONCAT_WS(' ', d.MiddleName, d.LastName)), '') AS LastName, "
			+ "  NULLIF(TRIM(d.FatherName), '') AS FathersName, "
			+ "  TIMESTAMPDIFF(YEAR, d.DOB, CURDATE()) AS Age, "
			+ "  DATE_FORMAT(d.DOB, '%d-%m-%Y') AS DateOfBirth, "
			+ "  CASE "
			+ "    WHEN UPPER(IFNULL(d.Gender, '')) LIKE 'M%' THEN 'Male' "
			+ "    WHEN UPPER(IFNULL(d.Gender, '')) LIKE 'F%' THEN 'Female' "
			+ "    WHEN UPPER(IFNULL(d.Gender, '')) LIKE 'T%' THEN 'Transgender' "
			+ "    ELSE NULL "
			+ "  END AS Gender, "
			+ "  COALESCE(c.PreferredPhoneNum, c.PhoneNum1, c.EmergencyContactNum) AS PrimaryPhone, "
			+ "  NULLIF(TRIM(ad.PermAddrLine1), '') AS Address, "
			+ "  NULLIF(TRIM(ad.PermPinCode), '') AS Pincode, "
			+ "  COALESCE(NULLIF(TRIM(hh.residentialArea), ''), 'Unknown') AS Area, "
			+ "  COALESCE(NULLIF(TRIM(d.MaritalStatus), ''), 'Unknown') AS MaritalStatus, "
			+ "  COALESCE(NULLIF(TRIM(d.occupation), ''), 'Unknown') AS Occupation, "
			+ "  COALESCE(NULLIF(TRIM(hh.type_bpl_apl), ''), 'Unknown') AS SocioeconomicStatus, "
			+ "  CASE "
			+ "    WHEN UPPER(TRIM(IFNULL(d.community, ''))) IN ('SC', 'ST') THEN UPPER(TRIM(d.community)) "
			+ "    ELSE 'Others' "
			+ "  END AS caste, "
			+ "  'Not Applicable' AS KeyPopulation, "
			+ "  CASE "
			+ "    WHEN CONCAT_WS(',', "
			+ "      IF(LOWER(TRIM(IFNULL(scr.cough_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Cough for more than 2 weeks', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.fever_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Fever', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.weight_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Weight loss', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.sweats_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Night sweats', NULL) "
			+ "    ) = '' THEN 'Asymptomatic,' "
			+ "    ELSE CONCAT(CONCAT_WS(',', "
			+ "      IF(LOWER(TRIM(IFNULL(scr.cough_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Cough for more than 2 weeks', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.fever_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Fever', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.weight_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Weight loss', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.sweats_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Night sweats', NULL) "
			+ "    ), ',') "
			+ "  END AS Symptom, "
			+ "  CASE "
			+ "    WHEN UPPER(IFNULL(scr.hiv_status, '')) IN ('POSITIVE', 'REACTIVE', 'R') THEN 'Reactive' "
			+ "    WHEN UPPER(IFNULL(scr.hiv_status, '')) IN ('NEGATIVE', 'NON-REACTIVE', 'NON REACTIVE', 'NR') THEN 'Non-Reactive' "
			+ "    ELSE 'Unknown' "
			+ "  END AS HIVStatus, "
			// The master's ID columns are AMRIT's own numbers; Ni-kshay's IDs are in NikshayCode.
			// The village has no code: its Ni-kshay ID is looked up by name on the portal
			+ "  hh.villageid         AS ResidenceHierarchyId, "
			+ "  nv.NikshayVillageID  AS nikshay_village_id, "
			+ "  nv.VillageName       AS nikshay_village_name, "
			+ "  CAST(NULLIF(TRIM(ns.NikshayCode), '') AS UNSIGNED) AS nikshay_state_id, "
			+ "  CAST(NULLIF(TRIM(nd.NikshayCode), '') AS UNSIGNED) AS nikshay_district_id, "
			+ "  CAST(NULLIF(TRIM(nt.NikshayCode), '') AS UNSIGNED) AS nikshay_tu_id, "
			+ "  CAST(NULLIF(TRIM(nf.NikshayCode), '') AS UNSIGNED) AS SelectedHierarchyId "
			+ "FROM db_identity.i_beneficiarymapping m "
			+ "INNER JOIN db_identity.i_beneficiarydetails d "
			+ "  ON d.VanSerialNo = m.BenDetailsId AND d.VanID = m.VanID AND d.Deleted = 0 "
			+ "INNER JOIN db_identity.i_beneficiaryaddress ad "
			+ "  ON ad.BenAddressID = m.BenAddressID AND ad.Deleted = 0 "
			+ "LEFT JOIN db_identity.i_beneficiarycontacts c "
			+ "  ON c.BenContactsID = m.BenContactsId "
			+ "LEFT JOIN db_iemr.tb_confirmed_cases conf "
			+ "  ON conf.id = ( "
			+ "    SELECT c2.id FROM db_iemr.tb_confirmed_cases c2 "
			+ "    WHERE c2.benRegID = m.BenRegId ORDER BY c2.id DESC LIMIT 1) "
			+ "LEFT JOIN db_iemr.tb_suspected sus "
			+ "  ON sus.id = ( "
			+ "    SELECT x.id FROM db_iemr.tb_suspected x "
			+ "    WHERE x.benRegID = m.BenRegId OR x.ben_id = m.BenRegId "
			+ "    ORDER BY x.id DESC LIMIT 1) "
			+ "LEFT JOIN db_iemr.tb_stoptb_diagnostics diag "
			+ "  ON diag.id = ( "
			+ "    SELECT d2.id FROM db_iemr.tb_stoptb_diagnostics d2 "
			+ "    WHERE d2.ben_reg_id = m.BenRegId AND d2.deleted = 0 "
			+ "    ORDER BY d2.id DESC LIMIT 1) "
			+ "LEFT JOIN db_iemr.tb_screening scr "
			+ "  ON scr.id = ( "
			+ "    SELECT s2.id FROM db_iemr.tb_screening s2 "
			+ "    WHERE s2.ben_reg_id = m.BenRegId AND s2.deleted = 0 "
			+ "    ORDER BY s2.id DESC LIMIT 1) "
			+ "LEFT JOIN db_identity.i_beneficiarydetails_rmnch rm "
			+ "  ON rm.beneficiaryDetails_RmnchId = ( "
			+ "    SELECT MAX(r2.beneficiaryDetails_RmnchId) "
			+ "    FROM db_identity.i_beneficiarydetails_rmnch r2 "
			+ "    WHERE r2.BeneficiaryRegID = m.BenRegId "
			+ "      AND r2.houseoldId IS NOT NULL "
			+ "      AND (r2.Deleted = 0 OR r2.Deleted IS NULL) "
			+ "  ) "
			+ "LEFT JOIN db_identity.i_householddetails hh "
			+ "  ON hh.houseHoldDetailsId = ( "
			+ "    SELECT MAX(h2.houseHoldDetailsId) "
			+ "    FROM db_identity.i_householddetails h2 "
			+ "    WHERE h2.houseoldId = rm.houseoldId "
			+ "      AND (h2.Deleted = 0 OR h2.Deleted IS NULL) "
			+ "  ) "
			+ "LEFT JOIN db_iemr.m_nikshay_village nv "
			+ "  ON nv.NikshayVillageID = hh.villageid "
			+ " AND (nv.Deleted = 0 OR nv.Deleted IS NULL) "
			+ "LEFT JOIN db_iemr.m_nikshay_facility nf "
			+ "  ON nf.NikshayFacilityID = nv.NikshayFacilityID "
			+ " AND (nf.Deleted = 0 OR nf.Deleted IS NULL) "
			+ "LEFT JOIN db_iemr.m_nikshay_tu nt "
			+ "  ON nt.NikshayTUID = nf.NikshayTUID "
			+ " AND (nt.Deleted = 0 OR nt.Deleted IS NULL) "
			+ "LEFT JOIN db_iemr.m_nikshay_district nd "
			+ "  ON nd.NikshayDistrictID = nt.NikshayDistrictID "
			+ " AND (nd.Deleted = 0 OR nd.Deleted IS NULL) "
			+ "LEFT JOIN db_iemr.m_nikshay_state ns "
			+ "  ON ns.NikshayStateID = nd.NikshayStateID "
			+ " AND (ns.Deleted = 0 OR ns.Deleted IS NULL) ";

	private static final String ELIGIBLE_BENEFICIARIES_SQL = BENEFICIARY_COLUMNS_SQL
			+ "WHERE m.Deleted = 0 "
			+ "  AND m.BenRegId IS NOT NULL "
			+ "  AND IFNULL(d.is_death, 0) = 0 "
			+ "  AND ( "
			+ "    conf.benRegID IS NOT NULL "
			+ "    OR IFNULL(sus.is_tb_confirmed, 0) = 1 "
			+ "    OR IFNULL(sus.is_confirmed, 0) = 1 "
			+ "    OR ( "
			+ "      LOWER(IFNULL(COALESCE(diag.truenat_result, sus.sputum_test_result), '')) LIKE '%detect%' "
			+ "      AND LOWER(IFNULL(COALESCE(diag.truenat_result, sus.sputum_test_result), '')) NOT LIKE '%not detect%' "
			+ "    ) "
			+ "  ) "
			+ "  AND IFNULL(NULLIF(TRIM(d.nikshayId), ''), '') = '' "
			+ "  AND IFNULL(NULLIF(TRIM(sus.nikshay_id), ''), '') = '' "
			+ "  AND IFNULL(NULLIF(TRIM(diag.nikshay_id), ''), '') = '' "
			+ "ORDER BY m.BenRegId DESC";

	// "M. Tb detected" in a TrueNat / sputum result column (same check as the eligibility query)
	private static String detected(String column) {
		return "(LOWER(IFNULL(" + column + ", '')) LIKE '%detect%' "
				+ "AND LOWER(IFNULL(" + column + ", '')) NOT LIKE '%not detect%')";
	}

	// Latest X-ray result of household member "mem" from the Stop TB diagnostics form
	private static final String FORM_XRAY_SQL = "(SELECT dx.digital_chest_xray_result "
			+ "FROM db_iemr.tb_stoptb_diagnostics dx "
			+ "WHERE dx.ben_reg_id = mem.BeneficiaryRegID AND dx.deleted = 0 "
			+ "  AND NULLIF(TRIM(dx.digital_chest_xray_result), '') IS NOT NULL "
			+ "ORDER BY dx.id DESC LIMIT 1)";

	// Latest completed chest X-ray order result of "mem" (the result the X-ray upload sends)
	private static final String ORDER_XRAY_SQL = "(SELECT rx.result_summary "
			+ "FROM db_iemr.tb_diagnostic_order ox "
			+ "JOIN db_iemr.tb_diagnostic_result rx "
			+ "  ON rx.external_order_id = ox.external_order_id AND rx.deleted = 0 "
			+ "JOIN db_identity.m_beneficiaryregidmapping bx "
			+ "  ON bx.BeneficiaryID = ox.beneficiary_id AND bx.Deleted = 0 "
			+ "WHERE bx.BenRegId = mem.BeneficiaryRegID AND ox.order_type = 'XRAY_CHEST' "
			+ "  AND ox.status = 'COMPLETED' AND ox.deleted = 0 "
			+ "  AND NULLIF(TRIM(rx.result_summary), '') IS NOT NULL "
			+ "ORDER BY ox.created_date DESC, ox.id DESC LIMIT 1)";

	// A member of the household (each from their latest rmnch row) who stops it from being
	// all negative: not TB screened, ever TB confirmed or M. Tb detected, or no normal X-ray.
	// The X-ray is normal when at least one of the two results says Normal and neither
	// says anything else
	private static final String MEMBER_NOT_CLEARED_SQL = "SELECT 1 FROM db_identity.i_beneficiarydetails_rmnch mem "
			+ "WHERE mem.houseoldId = rm.houseoldId "
			+ "  AND mem.BeneficiaryRegID IS NOT NULL "
			+ "  AND (mem.Deleted = 0 OR mem.Deleted IS NULL) "
			+ "  AND mem.beneficiaryDetails_RmnchId = ( "
			+ "    SELECT MAX(r3.beneficiaryDetails_RmnchId) "
			+ "    FROM db_identity.i_beneficiarydetails_rmnch r3 "
			+ "    WHERE r3.BeneficiaryRegID = mem.BeneficiaryRegID "
			+ "      AND r3.houseoldId IS NOT NULL "
			+ "      AND (r3.Deleted = 0 OR r3.Deleted IS NULL) "
			+ "  ) "
			+ "  AND ( "
			+ "    NOT EXISTS (SELECT 1 FROM db_iemr.tb_screening s3 "
			+ "      WHERE s3.ben_reg_id = mem.BeneficiaryRegID AND s3.deleted = 0) "
			+ "    OR EXISTS (SELECT 1 FROM db_iemr.tb_confirmed_cases c3 "
			+ "      WHERE c3.benRegID = mem.BeneficiaryRegID) "
			+ "    OR EXISTS (SELECT 1 FROM db_iemr.tb_suspected x3 "
			+ "      WHERE (x3.benRegID = mem.BeneficiaryRegID OR x3.ben_id = mem.BeneficiaryRegID) "
			+ "        AND (IFNULL(x3.is_tb_confirmed, 0) = 1 OR IFNULL(x3.is_confirmed, 0) = 1 "
			+ "          OR " + detected("x3.sputum_test_result") + ")) "
			+ "    OR EXISTS (SELECT 1 FROM db_iemr.tb_stoptb_diagnostics d3 "
			+ "      WHERE d3.ben_reg_id = mem.BeneficiaryRegID AND d3.deleted = 0 "
			+ "        AND " + detected("d3.truenat_result") + ") "
			+ "    OR NOT ( "
			+ "      (" + FORM_XRAY_SQL + " IS NOT NULL OR " + ORDER_XRAY_SQL + " IS NOT NULL) "
			+ "      AND LOWER(TRIM(IFNULL(" + FORM_XRAY_SQL + ", 'normal'))) = 'normal' "
			+ "      AND LOWER(TRIM(IFNULL(" + ORDER_XRAY_SQL + ", 'normal'))) = 'normal' "
			+ "    ) "
			+ "  )";

	// Members of all-negative households who have no Ni-kshay ID yet: every member of the
	// household is TB screened, has a normal X-ray and is not TB confirmed
	private static final String ALL_NEGATIVE_BENEFICIARIES_SQL = BENEFICIARY_COLUMNS_SQL
			+ "WHERE m.Deleted = 0 "
			+ "  AND m.BenRegId IS NOT NULL "
			+ "  AND IFNULL(d.is_death, 0) = 0 "
			+ "  AND rm.houseoldId IS NOT NULL "
			+ "  AND IFNULL(NULLIF(TRIM(d.nikshayId), ''), '') = '' "
			+ "  AND IFNULL(NULLIF(TRIM(sus.nikshay_id), ''), '') = '' "
			+ "  AND IFNULL(NULLIF(TRIM(diag.nikshay_id), ''), '') = '' "
			+ "  AND NOT EXISTS (" + MEMBER_NOT_CLEARED_SQL + ") "
			+ "ORDER BY m.BenRegId DESC";

	// The given beneficiaries with the eligibility query's columns, whether or not they have a
	// Ni-kshay ID (the CONTACTS phase reads index patients enrolled earlier)
	private static final String BENEFICIARIES_BY_BEN_REG_IDS_SQL = BENEFICIARY_COLUMNS_SQL
			+ "WHERE m.Deleted = 0 "
			+ "  AND m.BenRegId IN (:benRegIds) "
			+ "ORDER BY m.BenRegId";

	// Each beneficiary's latest episode row (V133): the episode tests and contacts go to
	private static final String LATEST_EPISODES_SQL = "SELECT e.ben_reg_id, e.nikshay_id, e.episode_id, "
			+ "e.link_status, e.flow_scenario, e.stage_found, e.device_id "
			+ "FROM db_iemr.nikshay_beneficiary_episode e "
			+ "WHERE e.id IN ( "
			+ "    SELECT MAX(e2.id) FROM db_iemr.nikshay_beneficiary_episode e2 "
			+ "    WHERE e2.ben_reg_id IN (:benRegIds) GROUP BY e2.ben_reg_id)";

	// Members of the given households, each from their latest household (rmnch) row, with their
	// latest tb_screening row (also used for their symptoms when added as a contact). Same member
	// joins as Q3 / screening join as Q2 of NIKSHAY_ENROLLMENT.sql.
	private static final String HOUSEHOLD_MEMBERS_SQL = "SELECT "
			+ "  rm.houseoldId, "
			+ "  rm.BeneficiaryRegID AS beneficiary_id, "
			+ "  COALESCE(d.FirstName, d2.FirstName) AS FirstName, "
			+ "  NULLIF(TRIM(CONCAT_WS(' ', COALESCE(d.MiddleName, d2.MiddleName), COALESCE(d.LastName, d2.LastName))), '') AS LastName, "
			+ "  TIMESTAMPDIFF(YEAR, COALESCE(d.DOB, d2.DOB), CURDATE()) AS Age, "
			+ "  DATE_FORMAT(COALESCE(d.DOB, d2.DOB), '%d-%m-%Y') AS DateOfBirth, "
			+ "  CASE "
			+ "    WHEN UPPER(IFNULL(COALESCE(d.Gender, d2.Gender), '')) LIKE 'M%' THEN 'Male' "
			+ "    WHEN UPPER(IFNULL(COALESCE(d.Gender, d2.Gender), '')) LIKE 'F%' THEN 'Female' "
			+ "    WHEN UPPER(IFNULL(COALESCE(d.Gender, d2.Gender), '')) LIKE 'T%' THEN 'Transgender' "
			+ "    ELSE NULL "
			+ "  END AS Gender, "
			+ "  COALESCE(c.PreferredPhoneNum, c.PhoneNum1, c.EmergencyContactNum) AS PrimaryPhone, "
			+ "  NULLIF(TRIM(COALESCE(d.nikshayId, d2.nikshayId)), '') AS NikshayId, "
			+ "  CASE "
			+ "    WHEN UPPER(TRIM(IFNULL(COALESCE(d.community, d2.community), ''))) IN ('SC', 'ST') "
			+ "      THEN UPPER(TRIM(COALESCE(d.community, d2.community))) "
			+ "    ELSE 'Others' "
			+ "  END AS caste, "
			// Same symptom and HIV mapping as the eligibility query
			+ "  CASE "
			+ "    WHEN CONCAT_WS(',', "
			+ "      IF(LOWER(TRIM(IFNULL(scr.cough_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Cough for more than 2 weeks', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.fever_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Fever', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.weight_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Weight loss', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.sweats_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Night sweats', NULL) "
			+ "    ) = '' THEN 'Asymptomatic,' "
			+ "    ELSE CONCAT(CONCAT_WS(',', "
			+ "      IF(LOWER(TRIM(IFNULL(scr.cough_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Cough for more than 2 weeks', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.fever_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Fever', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.weight_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Weight loss', NULL), "
			+ "      IF(LOWER(TRIM(IFNULL(scr.sweats_check, ''))) IN ('1', 'yes', 'y', 'true'), 'Night sweats', NULL) "
			+ "    ), ',') "
			+ "  END AS Symptom, "
			+ "  CASE "
			+ "    WHEN UPPER(IFNULL(scr.hiv_status, '')) IN ('POSITIVE', 'REACTIVE', 'R') THEN 'Reactive' "
			+ "    WHEN UPPER(IFNULL(scr.hiv_status, '')) IN ('NEGATIVE', 'NON-REACTIVE', 'NON REACTIVE', 'NR') THEN 'Non-Reactive' "
			+ "    ELSE 'Unknown' "
			+ "  END AS HIVStatus, "
			+ "  IF(scr.id IS NULL, 0, 1) AS TbScreened, "
			+ "  scr.visit_date AS ScreeningDate "
			+ "FROM db_identity.i_beneficiarydetails_rmnch rm "
			+ "LEFT JOIN db_identity.i_beneficiarydetails d "
			+ "  ON d.BeneficiaryRegID = rm.BeneficiaryRegID AND (d.Deleted = 0 OR d.Deleted IS NULL) "
			+ "LEFT JOIN db_identity.i_beneficiarymapping map "
			+ "  ON map.BenRegId = rm.BeneficiaryRegID AND map.Deleted = 0 "
			+ "LEFT JOIN db_identity.i_beneficiarydetails d2 "
			+ "  ON d2.VanSerialNo = map.BenDetailsId AND d2.VanID = map.VanID AND d2.Deleted = 0 "
			+ "LEFT JOIN db_identity.i_beneficiarycontacts c "
			+ "  ON c.BenContactsID = map.BenContactsId "
			+ "LEFT JOIN db_iemr.tb_screening scr "
			+ "  ON scr.id = ( "
			+ "    SELECT s2.id FROM db_iemr.tb_screening s2 "
			+ "    WHERE s2.ben_reg_id = rm.BeneficiaryRegID AND s2.deleted = 0 "
			+ "    ORDER BY s2.id DESC LIMIT 1) "
			+ "WHERE rm.houseoldId IN (:householdIds) "
			+ "  AND rm.BeneficiaryRegID IS NOT NULL "
			+ "  AND (rm.Deleted = 0 OR rm.Deleted IS NULL) "
			+ "  AND rm.beneficiaryDetails_RmnchId = ( "
			+ "    SELECT MAX(r2.beneficiaryDetails_RmnchId) "
			+ "    FROM db_identity.i_beneficiarydetails_rmnch r2 "
			+ "    WHERE r2.BeneficiaryRegID = rm.BeneficiaryRegID "
			+ "      AND r2.houseoldId IS NOT NULL "
			+ "      AND (r2.Deleted = 0 OR r2.Deleted IS NULL) "
			+ "  ) "
			+ "ORDER BY rm.houseoldId, rm.BeneficiaryRegID";

	// Q5 of NIKSHAY_ENROLLMENT.sql: write the Ni-kshay ID back to the beneficiary, and to their
	// tb_suspected / tb_stoptb_diagnostics rows that don't have one yet
	private static final String SAVE_ID_ON_BEN_DETAILS_SQL = "UPDATE db_identity.i_beneficiarydetails "
			+ "SET nikshayId = :nikshayId, nikshayCreatedByAmrit = :createdByAmrit "
			+ "WHERE VanSerialNo = ( "
			+ "    SELECT m.BenDetailsId FROM db_identity.i_beneficiarymapping m "
			+ "    WHERE m.BenRegId = :benRegId AND m.Deleted = 0 LIMIT 1) "
			+ "  AND VanID = ( "
			+ "    SELECT m.VanID FROM db_identity.i_beneficiarymapping m "
			+ "    WHERE m.BenRegId = :benRegId AND m.Deleted = 0 LIMIT 1)";

	private static final String SAVE_ID_ON_TB_SUSPECTED_SQL = "UPDATE db_iemr.tb_suspected "
			+ "SET nikshay_id = :nikshayId, nikshay_created_by_amrit = :createdByAmrit "
			+ "WHERE (benRegID = :benRegId OR ben_id = :benRegId) "
			+ "  AND (nikshay_id IS NULL OR TRIM(nikshay_id) = '')";

	// One row per enrolment (V133); BeneficiaryID is looked up from the BenRegId
	private static final String ADD_EPISODE_SQL = "INSERT INTO db_iemr.nikshay_beneficiary_episode "
			+ "(beneficiary_id, ben_reg_id, nikshay_id, episode_id, link_status, flow_scenario, stage_found, device_id, "
			+ "created_by) "
			+ "VALUES (( "
			+ "    SELECT r.BeneficiaryID FROM db_identity.m_beneficiaryregidmapping r "
			+ "    WHERE r.BenRegId = :benRegId AND r.Deleted = 0 LIMIT 1), "
			+ "  :benRegId, :nikshayId, :episodeId, :linkStatus, :flowScenario, :stageFound, :deviceId, :createdBy)";

	private static final String SAVE_ID_ON_DIAGNOSTICS_SQL = "UPDATE db_iemr.tb_stoptb_diagnostics "
			+ "SET nikshay_id = :nikshayId "
			+ "WHERE ben_reg_id = :benRegId AND deleted = 0 "
			+ "  AND (nikshay_id IS NULL OR TRIM(nikshay_id) = '')";

	@Autowired
	private DataSource dataSource;

	/**
	 * Returns every beneficiary that is eligible for a Ni-kshay ID, each with the other
	 * members of their household and their TB screening status.
	 */
	public List<NikshayIdGenerateBeneficiaryDTO> findEligibleBeneficiaries() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<NikshayIdGenerateBeneficiaryDTO> beneficiaries = jdbcTemplate.query(ELIGIBLE_BENEFICIARIES_SQL,
				(rs, rowNum) -> mapRow(rs));
		addHouseholdMembers(beneficiaries);
		return beneficiaries;
	}

	/**
	 * Returns the members of all-negative households (every member TB screened, every X-ray
	 * normal, no one TB confirmed) who have no Ni-kshay ID yet. No household members are
	 * attached: there is no index patient, so no contacts.
	 */
	public List<NikshayIdGenerateBeneficiaryDTO> findAllNegativeBeneficiaries() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.query(ALL_NEGATIVE_BENEFICIARIES_SQL, (rs, rowNum) -> mapRow(rs));
	}

	/**
	 * Returns the given beneficiaries (by BenRegId), each with the other members of their
	 * household, whether or not they have a Ni-kshay ID.
	 */
	public List<NikshayIdGenerateBeneficiaryDTO> findBeneficiariesByBenRegIds(List<Long> benRegIds) {
		if (benRegIds.isEmpty()) {
			return new ArrayList<>();
		}
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		List<NikshayIdGenerateBeneficiaryDTO> beneficiaries = namedJdbcTemplate.query(BENEFICIARIES_BY_BEN_REG_IDS_SQL,
				new MapSqlParameterSource("benRegIds", benRegIds), (rs, rowNum) -> mapRow(rs));
		addHouseholdMembers(beneficiaries);
		return beneficiaries;
	}

	/**
	 * Returns each given beneficiary's latest enrolment (nikshay_beneficiary_episode), by
	 * BenRegId. A beneficiary never enrolled has no entry.
	 */
	public Map<Long, NikshayEnrollResult> findLatestEpisodes(List<Long> benRegIds) {
		Map<Long, NikshayEnrollResult> episodes = new HashMap<>();
		if (benRegIds.isEmpty()) {
			return episodes;
		}
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		List<NikshayEnrollResult> rows = namedJdbcTemplate.query(LATEST_EPISODES_SQL,
				new MapSqlParameterSource("benRegIds", benRegIds), (rs, rowNum) -> mapEpisodeRow(rs));
		for (NikshayEnrollResult row : rows) {
			episodes.put(row.getBeneficiaryId(), row);
		}
		return episodes;
	}

	private NikshayEnrollResult mapEpisodeRow(ResultSet rs) throws SQLException {
		NikshayEnrollResult result = new NikshayEnrollResult(rs.getObject("ben_reg_id", Long.class),
				rs.getString("nikshay_id"), rs.getString("episode_id"), NikshayLinkStatus.valueOf(rs.getString("link_status")),
				rs.getString("stage_found"), rs.getObject("device_id", Integer.class));
		result.setFlowScenario(NikshayFlowScenario.valueOf(rs.getString("flow_scenario")));
		return result;
	}

	// Loads the members of all the beneficiaries' households in one query and adds each
	// beneficiary's household members to it, leaving out the beneficiary themself.
	private void addHouseholdMembers(List<NikshayIdGenerateBeneficiaryDTO> beneficiaries) {
		List<Long> householdIds = new ArrayList<>();
		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			if (beneficiary.getHouseoldId() != null && !householdIds.contains(beneficiary.getHouseoldId())) {
				householdIds.add(beneficiary.getHouseoldId());
			}
		}
		if (householdIds.isEmpty()) {
			return;
		}
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		List<NikshayHouseholdMemberDTO> members = namedJdbcTemplate.query(HOUSEHOLD_MEMBERS_SQL,
				new MapSqlParameterSource("householdIds", householdIds), (rs, rowNum) -> mapMemberRow(rs));

		Map<Long, List<NikshayHouseholdMemberDTO>> membersByHousehold = new HashMap<>();
		for (NikshayHouseholdMemberDTO member : members) {
			List<NikshayHouseholdMemberDTO> householdMembers = membersByHousehold.get(member.getHouseoldId());
			if (householdMembers == null) {
				householdMembers = new ArrayList<>();
				membersByHousehold.put(member.getHouseoldId(), householdMembers);
			}
			householdMembers.add(member);
		}

		for (NikshayIdGenerateBeneficiaryDTO beneficiary : beneficiaries) {
			List<NikshayHouseholdMemberDTO> householdMembers = membersByHousehold.get(beneficiary.getHouseoldId());
			if (householdMembers == null) {
				continue;
			}
			for (NikshayHouseholdMemberDTO member : householdMembers) {
				if (!member.getBeneficiaryId().equals(beneficiary.getBeneficiaryId())) {
					beneficiary.getHouseholdMembers().add(member);
				}
			}
		}
	}

	/**
	 * Saves an enrolment: the person's Ni-kshay ID on the beneficiary (Q5) and a new
	 * nikshay_beneficiary_episode row, in one transaction. nikshayCreatedByAmrit is true
	 * only when TBMJA created the person's record (NEW). Returns the number of
	 * i_beneficiarydetails rows updated (0 when the identity record is not found).
	 */
	@Transactional
	public int saveEnrollment(NikshayEnrollResult result) {
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("benRegId", result.getBeneficiaryId());
		params.addValue("nikshayId", result.getNikshayId());
		params.addValue("createdByAmrit", result.getLinkStatus() == NikshayLinkStatus.NEW ? 1 : 0);
		params.addValue("episodeId", result.getEpisodeId());
		params.addValue("linkStatus", result.getLinkStatus().name());
		params.addValue("flowScenario", result.getFlowScenario().name());
		params.addValue("stageFound", result.getStageFound());
		params.addValue("deviceId", result.getDeviceId());
		params.addValue("createdBy", "NikshayIdGeneration");
		int updated = namedJdbcTemplate.update(SAVE_ID_ON_BEN_DETAILS_SQL, params);
		namedJdbcTemplate.update(SAVE_ID_ON_TB_SUSPECTED_SQL, params);
		namedJdbcTemplate.update(SAVE_ID_ON_DIAGNOSTICS_SQL, params);
		namedJdbcTemplate.update(ADD_EPISODE_SQL, params);
		return updated;
	}

	private NikshayHouseholdMemberDTO mapMemberRow(ResultSet rs) throws SQLException {
		NikshayHouseholdMemberDTO member = new NikshayHouseholdMemberDTO();
		member.setBeneficiaryId(rs.getObject("beneficiary_id", Long.class));
		member.setHouseoldId(rs.getObject("houseoldId", Long.class));
		member.setFirstName(rs.getString("FirstName"));
		member.setLastName(rs.getString("LastName"));
		member.setAge(rs.getObject("Age", Integer.class));
		member.setDateOfBirth(rs.getString("DateOfBirth"));
		member.setGender(rs.getString("Gender"));
		member.setPrimaryPhone(rs.getString("PrimaryPhone"));
		member.setNikshayId(rs.getString("NikshayId"));
		member.setCaste(rs.getString("caste"));
		member.setSymptom(rs.getString("Symptom"));
		member.setHivStatus(rs.getString("HIVStatus"));
		member.setTbScreened(rs.getInt("TbScreened") == 1);
		member.setScreeningDate(rs.getTimestamp("ScreeningDate"));
		return member;
	}

	private NikshayIdGenerateBeneficiaryDTO mapRow(ResultSet rs) throws SQLException {
		NikshayIdGenerateBeneficiaryDTO dto = new NikshayIdGenerateBeneficiaryDTO();
		dto.setBeneficiaryId(rs.getObject("beneficiary_id", Long.class));
		dto.setHouseoldId(rs.getObject("houseoldId", Long.class));
		dto.setFirstName(rs.getString("FirstName"));
		dto.setLastName(rs.getString("LastName"));
		dto.setFathersName(rs.getString("FathersName"));
		dto.setAge(rs.getObject("Age", Integer.class));
		dto.setDateOfBirth(rs.getString("DateOfBirth"));
		dto.setGender(rs.getString("Gender"));
		dto.setPrimaryPhone(rs.getString("PrimaryPhone"));
		dto.setAddress(rs.getString("Address"));
		dto.setPincode(rs.getString("Pincode"));
		dto.setArea(rs.getString("Area"));
		dto.setMaritalStatus(rs.getString("MaritalStatus"));
		dto.setOccupation(rs.getString("Occupation"));
		dto.setSocioeconomicStatus(rs.getString("SocioeconomicStatus"));
		dto.setCaste(rs.getString("caste"));
		dto.setKeyPopulation(rs.getString("KeyPopulation"));
		dto.setSymptom(rs.getString("Symptom"));
		dto.setHivStatus(rs.getString("HIVStatus"));
		dto.setResidenceHierarchyId(rs.getObject("ResidenceHierarchyId", Long.class));
		dto.setNikshayVillageId(rs.getObject("nikshay_village_id", Integer.class));
		dto.setNikshayVillageName(rs.getString("nikshay_village_name"));
		dto.setNikshayStateId(rs.getObject("nikshay_state_id", Long.class));
		dto.setNikshayDistrictId(rs.getObject("nikshay_district_id", Long.class));
		dto.setNikshayTuId(rs.getObject("nikshay_tu_id", Long.class));
		dto.setSelectedHierarchyId(rs.getObject("SelectedHierarchyId", Long.class));
		return dto;
	}
}
