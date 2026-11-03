package com.iemr.admin.repo.nikshayphasestatus;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshayphasestatus.NikshayJobScheduleDTO;

/**
 * The single Ni-kshay cron job row (nikshay_job_schedule, id 1). One schedule turns the
 * job on or off and says when it runs. The five phases are not separate cron jobs.
 */
@Repository

public class NikshayJobScheduleRepository {
	private static final Logger logger = LoggerFactory.getLogger(NikshayJobScheduleRepository.class);

	private static final String FIND_SQL = "SELECT enabled, cron_expression, updated_by, updated_at "
			+ "FROM db_iemr.nikshay_job_schedule WHERE id = 1";

	private static final String SAVE_SQL = "INSERT INTO db_iemr.nikshay_job_schedule "
			+ "(id, enabled, cron_expression, updated_by) VALUES (1, :enabled, :cronExpression, :updatedBy) "
			+ "ON DUPLICATE KEY UPDATE enabled = VALUES(enabled), cron_expression = VALUES(cron_expression), "
			+ "updated_by = VALUES(updated_by), updated_at = CURRENT_TIMESTAMP";

	@Autowired
	private DataSource dataSource;

	/**
	 * The saved schedule. When the table or the row is missing, the job is off so a
	 * database that has not had the table added yet does not start the cron.
	 */
	public NikshayJobScheduleDTO find() {
		try {
			List<NikshayJobScheduleDTO> rows = new NamedParameterJdbcTemplate(dataSource).query(FIND_SQL,
					(rs, rowNum) -> new NikshayJobScheduleDTO(rs.getInt("enabled") == 1, rs.getString("cron_expression"),
							rs.getString("updated_by"), rs.getTimestamp("updated_at")));
			return rows.isEmpty() ? off() : rows.get(0);
		} catch (DataAccessException e) {
			logger.warn("nikshay_job_schedule is not available; the automatic Ni-kshay job stays off: {}",
					e.getMostSpecificCause().getMessage());
			return off();
		}
	}

	public void save(boolean enabled, String cronExpression, String updatedBy) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("enabled", enabled ? 1 : 0);
		params.addValue("cronExpression", cronExpression);
		params.addValue("updatedBy", updatedBy);
		new NamedParameterJdbcTemplate(dataSource).update(SAVE_SQL, params);
	}

	private static NikshayJobScheduleDTO off() {
		return new NikshayJobScheduleDTO(false, null, null, null);
	}
}
