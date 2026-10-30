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
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseRunSettingDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayRunMode;

/**
 * Reads and writes db_iemr.nikshay_phase_run_setting (AMRIT-DB V135): the run mode of each
 * Ni-kshay phase.
 */
@Repository
public class NikshayPhaseRunSettingRepository {

	private static final String COLUMNS_SQL = "SELECT phase, run_mode, updated_by, updated_at "
			+ "FROM db_iemr.nikshay_phase_run_setting ";

	// Insert or update, so a phase with no row (e.g. added after V135) can be set too
	private static final String SAVE_SQL = "INSERT INTO db_iemr.nikshay_phase_run_setting (phase, run_mode, updated_by) "
			+ "VALUES (:phase, :runMode, :updatedBy) "
			+ "ON DUPLICATE KEY UPDATE run_mode = VALUES(run_mode), updated_by = VALUES(updated_by), "
			+ "updated_at = CURRENT_TIMESTAMP";

	@Autowired
	private DataSource dataSource;

	/** Every phase's setting; phases not known to this code are left out. */
	public List<NikshayPhaseRunSettingDTO> findAll() {
		List<NikshayPhaseRunSettingDTO> settings = new NamedParameterJdbcTemplate(dataSource)
				.query(COLUMNS_SQL + "ORDER BY phase", (rs, rowNum) -> mapSetting(rs));
		settings.removeIf(setting -> setting.getPhase() == null);
		return settings;
	}

	/** The phase's setting, or null when it has no row. */
	public NikshayPhaseRunSettingDTO find(NikshayPhase phase) {
		List<NikshayPhaseRunSettingDTO> settings = new NamedParameterJdbcTemplate(dataSource).query(
				COLUMNS_SQL + "WHERE phase = :phase", new MapSqlParameterSource("phase", phase.name()),
				(rs, rowNum) -> mapSetting(rs));
		return settings.isEmpty() ? null : settings.get(0);
	}

	public void save(NikshayPhase phase, NikshayRunMode runMode, String updatedBy) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("phase", phase.name());
		params.addValue("runMode", runMode.name());
		params.addValue("updatedBy", updatedBy);
		new NamedParameterJdbcTemplate(dataSource).update(SAVE_SQL, params);
	}

	// An unknown phase or run mode is read as null
	private static NikshayPhaseRunSettingDTO mapSetting(ResultSet rs) throws SQLException {
		NikshayPhase phase = null;
		NikshayRunMode runMode = null;
		try {
			phase = NikshayPhase.valueOf(rs.getString("phase"));
		} catch (IllegalArgumentException | NullPointerException e) {
			// left null
		}
		try {
			runMode = NikshayRunMode.valueOf(rs.getString("run_mode"));
		} catch (IllegalArgumentException | NullPointerException e) {
			// left null
		}
		return new NikshayPhaseRunSettingDTO(phase, runMode, rs.getString("updated_by"), rs.getTimestamp("updated_at"));
	}
}
