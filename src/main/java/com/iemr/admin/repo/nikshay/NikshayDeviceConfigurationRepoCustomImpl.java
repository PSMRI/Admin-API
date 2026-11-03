package com.iemr.admin.repo.nikshay;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.iemr.admin.data.nikshay.NikshayDeviceConfiguration;

/**
 * Spring Data finds it by the "Impl" suffix and merges it into NikshayDeviceConfigurationRepo.
 * With rewriteBatchedStatements=true on the MySQL, a batch insert. created_at / updated_at are filled by column defaults.
 */
public class NikshayDeviceConfigurationRepoCustomImpl implements NikshayDeviceConfigurationRepoCustom {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final String INSERT_SQL = "insert into m_nikshay_device_configuration "
			+ "(device_serial_number, device_name, phi_user_id, password, is_active, created_by, more_data) "
			+ "values (?, ?, ?, ?, ?, ?, ?)";

	// Prepares the insert so MySQL returns the generated id of every inserted row
	private static final PreparedStatementCreator INSERT_STATEMENT = con -> con.prepareStatement(INSERT_SQL,
			Statement.RETURN_GENERATED_KEYS);

	private final JdbcTemplate jdbc;

	public NikshayDeviceConfigurationRepoCustomImpl(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}

	@Override
	public void batchInsert(List<NikshayDeviceConfiguration> devices) {
		if (devices == null || devices.isEmpty()) {
			return;
		}
		KeyHolder keys = new GeneratedKeyHolder();
		jdbc.batchUpdate(INSERT_STATEMENT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				bind(ps, devices.get(i));
			}

			@Override
			public int getBatchSize() {
				return devices.size();
			}
		}, keys);

		// One generated key per row, in insert order
		List<Map<String, Object>> keyList = keys.getKeyList();
		if (keyList.size() != devices.size()) {
			logger.warn("Batch insert of {} Nikshay devices returned {} generated ids; ids left empty",
					devices.size(), keyList.size());
			return;
		}
		for (int i = 0; i < devices.size(); i++) {
			devices.get(i).setId(generatedId(keyList.get(i)));
		}
	}

	@Override
	public void insert(NikshayDeviceConfiguration device) {
		KeyHolder key = new GeneratedKeyHolder();
		jdbc.update(con -> {
			PreparedStatement ps = INSERT_STATEMENT.createPreparedStatement(con);
			bind(ps, device);
			return ps;
		}, key);
		device.setId(key.getKeyList().isEmpty() ? null : generatedId(key.getKeyList().get(0)));
	}

	private static void bind(PreparedStatement ps, NikshayDeviceConfiguration d) throws SQLException {
		ps.setString(1, d.getDeviceSerialNumber());
		ps.setString(2, d.getDeviceName() == null ? null : d.getDeviceName().name());
		ps.setString(3, d.getPhiUserId());
		ps.setString(4, d.getPassword());
		ps.setBoolean(5, !Boolean.FALSE.equals(d.getIsActive()));
		ps.setString(6, d.getCreatedBy());
		ps.setString(7, d.getMoreData());
	}

	// MySQL returns the auto-increment id as GENERATED_KEY; take the single value whatever its name
	private static Integer generatedId(Map<String, Object> key) {
		Object value = key.isEmpty() ? null : key.values().iterator().next();
		return value instanceof Number ? ((Number) value).intValue() : null;
	}
}
