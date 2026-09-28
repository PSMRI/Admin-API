/*
* AMRIT - Accessible Medical Records via Integrated Technologies
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
package com.iemr.admin.service.health;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The health endpoint is what the deployment's monitoring watches, so it has to
 * tell a database that is merely slow apart from one that is unreachable.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("HealthService Test Suite")
class HealthServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private RedisConnection redisConnection;

    @SuppressWarnings("unchecked")
    private static <T> ObjectProvider<T> providerOf(T value) {
        ObjectProvider<T> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(value);
        return provider;
    }

    /**
     * Builds the service without a data source so its constructor starts no background
     * cycle, then attaches the stores under test. That keeps the diagnostics the tests
     * drive from racing a scheduled cycle over the same mocks.
     */
    private HealthService serviceWith(DataSource ds, RedisConnectionFactory redis) {
        HealthService service = new HealthService(providerOf(null), providerOf(redis));
        ReflectionTestUtils.setField(service, "dataSource", ds);
        ((AtomicLong) ReflectionTestUtils.getField(service, "lastDiagnosticRunAt")).set(0);
        return service;
    }

    private void databaseAnswers() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
    }

    private void redisAnswers() {
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    }

    private static ResultSet countingResultSet(int count) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("cnt")).thenReturn(count);
        return rs;
    }

    private static ResultSet statusResultSet(long value) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getLong("Value")).thenReturn(value);
        when(rs.getInt("Value")).thenReturn((int) value);
        return rs;
    }

    @SuppressWarnings("unchecked")
    private static String statusOf(Map<String, Object> health, String service) {
        return (String) ((Map<String, Object>) health.get(service)).get("status");
    }

    @SuppressWarnings("unchecked")
    private static String severityOf(Map<String, Object> health, String service) {
        return (String) ((Map<String, Object>) health.get(service)).get("severity");
    }

    @Nested
    @DisplayName("checkHealth")
    class CheckHealthTests {

        @Test
        @DisplayName("should report the deployment up when both stores answer")
        void checkHealth_shouldReportUpWhenBothStoresAnswer() throws Exception {
            databaseAnswers();
            redisAnswers();
            HealthService service = serviceWith(dataSource, redisConnectionFactory);

            Map<String, Object> health = service.checkHealth();

            assertEquals("UP", health.get("status"));
            assertEquals("UP", statusOf(health, "mysql"));
            assertEquals("UP", statusOf(health, "redis"));
            assertNotNull(health.get("checkedAt"));
        }

        @Test
        @DisplayName("should report the deployment down when the database cannot be reached")
        void checkHealth_shouldReportDownWhenDatabaseUnreachable() throws Exception {
            when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));
            redisAnswers();
            HealthService service = serviceWith(dataSource, redisConnectionFactory);

            Map<String, Object> health = service.checkHealth();

            assertEquals("DOWN", health.get("status"));
            assertEquals("DOWN", statusOf(health, "mysql"));
            assertEquals("CRITICAL", severityOf(health, "mysql"));
        }

        @Test
        @DisplayName("should report the deployment down when Redis cannot be reached")
        void checkHealth_shouldReportDownWhenRedisUnreachable() throws Exception {
            databaseAnswers();
            when(redisConnectionFactory.getConnection())
                    .thenThrow(new IllegalStateException("connection refused"));
            HealthService service = serviceWith(dataSource, redisConnectionFactory);

            Map<String, Object> health = service.checkHealth();

            assertEquals("DOWN", health.get("status"));
            assertEquals("DOWN", statusOf(health, "redis"));
        }

        @Test
        @DisplayName("should report a store that is not configured rather than call it down")
        void checkHealth_shouldReportUnconfiguredStore() {
            HealthService service = serviceWith(null, null);

            Map<String, Object> health = service.checkHealth();

            assertEquals("UP", health.get("status"),
                    "a deployment without these stores is not itself unhealthy");
            assertEquals("NOT_CONFIGURED", statusOf(health, "mysql"));
            assertEquals("INFO", severityOf(health, "mysql"));
            assertEquals("NOT_CONFIGURED", statusOf(health, "redis"));
        }

        @Test
        @DisplayName("should report a degraded database as still up but flagged")
        void checkHealth_shouldReportDegradedDatabase() throws Exception {
            databaseAnswers();
            redisAnswers();
            HealthService service = serviceWith(dataSource, redisConnectionFactory);
            ReflectionTestUtils.invokeMethod(
                    ReflectionTestUtils.getField(service, "cachedDbSeverity"), "set", "WARNING");

            Map<String, Object> health = service.checkHealth();

            assertEquals("UP", health.get("status"), "a degraded database is still serving requests");
            assertEquals("DEGRADED", statusOf(health, "mysql"));
        }

        @Test
        @DisplayName("should report a critically degraded database as down")
        void checkHealth_shouldReportCriticalDatabaseAsDown() throws Exception {
            databaseAnswers();
            redisAnswers();
            HealthService service = serviceWith(dataSource, redisConnectionFactory);
            ReflectionTestUtils.invokeMethod(
                    ReflectionTestUtils.getField(service, "cachedDbSeverity"), "set", "CRITICAL");

            assertEquals("DOWN", service.checkHealth().get("status"));
        }
    }

    @Nested
    @DisplayName("Background diagnostics")
    class DiagnosticTests {

        private HealthService diagnosingService() throws SQLException {
            databaseAnswers();
            return serviceWith(dataSource, redisConnectionFactory);
        }

        private String severityAfterDiagnostics(HealthService service) {
            ((AtomicLong) ReflectionTestUtils.getField(service, "lastDiagnosticRunAt")).set(0);
            ReflectionTestUtils.invokeMethod(service, "runAdvancedMySQLDiagnostics");
            return (String) ReflectionTestUtils.invokeMethod(
                    ReflectionTestUtils.getField(service, "cachedDbSeverity"), "get");
        }

        private void everyCheckClean() throws SQLException {
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });
        }

        @Test
        @DisplayName("should report a healthy database when every check is clean")
        void diagnostics_shouldReportHealthyDatabase() throws Exception {
            HealthService service = diagnosingService();
            everyCheckClean();

            assertEquals("OK", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should warn when more processes are stuck than the threshold allows")
        void diagnostics_shouldWarnOnStuckProcesses() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("PROCESSLIST")) {
                    return countingResultSet(10);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("WARNING", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should stay healthy when a few processes are stuck but under the threshold")
        void diagnostics_shouldStayHealthyUnderStuckThreshold() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("PROCESSLIST")) {
                    return countingResultSet(2);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("OK", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should escalate to critical when several transactions run long")
        void diagnostics_shouldEscalateOnManyLongTransactions() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("INNODB_TRX")) {
                    return countingResultSet(6);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("CRITICAL", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should warn when a single transaction runs long")
        void diagnostics_shouldWarnOnOneLongTransaction() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("INNODB_TRX")) {
                    return countingResultSet(2);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("WARNING", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should warn when new deadlocks have happened since the previous cycle")
        void diagnostics_shouldWarnOnNewDeadlocks() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("Innodb_deadlocks")) {
                    return statusResultSet(3);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("WARNING", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should warn when new slow queries have been logged since the previous cycle")
        void diagnostics_shouldWarnOnNewSlowQueries() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("Slow_queries")) {
                    return statusResultSet(7);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("WARNING", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should escalate to critical when the connection pool is nearly exhausted")
        void diagnostics_shouldEscalateOnExhaustedPool() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("Threads_connected")) {
                    return statusResultSet(490);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("CRITICAL", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should warn when connection usage is high but not yet exhausted")
        void diagnostics_shouldWarnOnHighConnectionUsage() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenAnswer(call -> {
                String sql = call.getArgument(0);
                if (sql.contains("Threads_connected")) {
                    return statusResultSet(450);
                }
                if (sql.contains("max_connections")) {
                    return statusResultSet(500);
                }
                if (sql.startsWith("SHOW")) {
                    return statusResultSet(0);
                }
                return countingResultSet(0);
            });

            assertEquals("WARNING", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should report critical when it cannot open a connection to diagnose at all")
        void diagnostics_shouldReportCriticalWhenConnectionCannotBeOpened() throws Exception {
            HealthService service = diagnosingService();
            when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));

            assertEquals("CRITICAL", severityAfterDiagnostics(service));
        }

        @Test
        @DisplayName("should keep the previous verdict when a cycle is asked for too soon")
        void diagnostics_shouldKeepPreviousVerdictWhenAskedTooSoon() throws Exception {
            HealthService service = diagnosingService();
            everyCheckClean();
            severityAfterDiagnostics(service);
            ReflectionTestUtils.invokeMethod(
                    ReflectionTestUtils.getField(service, "cachedDbSeverity"), "set", "WARNING");
            ReflectionTestUtils.invokeMethod(service, "runAdvancedMySQLDiagnostics");

            assertEquals("WARNING",
                    ReflectionTestUtils.invokeMethod(
                            ReflectionTestUtils.getField(service, "cachedDbSeverity"), "get"),
                    "a cycle inside the guard window must not overwrite the standing verdict");
        }

        @Test
        @DisplayName("should survive a check whose query the database refuses")
        void diagnostics_shouldSurviveRefusedQuery() throws Exception {
            HealthService service = diagnosingService();
            when(statement.executeQuery(anyString())).thenThrow(new SQLException("access denied"));

            assertEquals("OK", severityAfterDiagnostics(service),
                    "a check that cannot run must not by itself condemn the database");
        }
    }

    @Test
    @DisplayName("shutdownDiagnostics should stop the background cycle")
    void shutdownDiagnostics_shouldStopBackgroundCycle() throws Exception {
        databaseAnswers();
        HealthService service = new HealthService(providerOf(dataSource), providerOf(redisConnectionFactory));

        service.shutdownDiagnostics();

        java.util.concurrent.ExecutorService scheduler = (java.util.concurrent.ExecutorService)
                ReflectionTestUtils.getField(service, "diagnosticScheduler");
        assertEquals(true, scheduler.isShutdown());
    }
}
