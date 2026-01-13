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
package com.iemr.admin.service.health;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private static final Logger logger = LoggerFactory.getLogger(HealthService.class);
    private static final String DB_HEALTH_CHECK_QUERY = "SELECT 1 as health_check";
    private static final String DB_VERSION_QUERY = "SELECT VERSION()";

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Value("${spring.datasource.url:unknown}")
    private String dbUrl;

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.mongodb.host:localhost}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port:27017}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database:amrit}")
    private String mongoDatabase;

    public Map<String, Object> checkHealth() {
        Map<String, Object> healthStatus = new LinkedHashMap<>();
        Map<String, Object> components = new LinkedHashMap<>();
        boolean overallHealth = true;

        // Check MySQL connectivity
        Map<String, Object> mysqlStatus = checkMySQLHealth();
        components.put("mysql", mysqlStatus);
        if (!"UP".equals(mysqlStatus.get("status"))) {
            overallHealth = false;
        }

        // Check Redis connectivity if configured
        if (redisTemplate != null) {
            Map<String, Object> redisStatus = checkRedisHealth();
            components.put("redis", redisStatus);
            if (!"UP".equals(redisStatus.get("status"))) {
                overallHealth = false;
            }
        }

        // Check MongoDB connectivity if configured
        if (mongoTemplate != null) {
            Map<String, Object> mongoStatus = checkMongoDBHealth();
            components.put("mongodb", mongoStatus);
            if (!"UP".equals(mongoStatus.get("status"))) {
                overallHealth = false;
            }
        }

        healthStatus.put("status", overallHealth ? "UP" : "DOWN");
        healthStatus.put("timestamp", Instant.now().toString());
        healthStatus.put("components", components);

        logger.info("Health check completed - Overall status: {}", overallHealth ? "UP" : "DOWN");
        return healthStatus;
    }

    private Map<String, Object> checkMySQLHealth() {
        Map<String, Object> status = new LinkedHashMap<>();
        Map<String, Object> details = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();

        // Add connection details
        details.put("type", "MySQL");
        details.put("host", extractHost(dbUrl));
        details.put("port", extractPort(dbUrl));
        details.put("database", extractDatabaseName(dbUrl));

        try (Connection connection = dataSource.getConnection()) {
            // 2 second timeout per best practices
            boolean isConnectionValid = connection.isValid(2);

            if (isConnectionValid) {
                // 3 second query timeout
                try (PreparedStatement stmt = connection.prepareStatement(DB_HEALTH_CHECK_QUERY)) {
                    stmt.setQueryTimeout(3);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 1) {
                            long responseTime = System.currentTimeMillis() - startTime;
                            logger.debug("MySQL health check: UP ({}ms)", responseTime);

                            status.put("status", "UP");
                            details.put("responseTimeMs", responseTime);

                            // Get database version
                            String version = getMySQLVersion(connection);
                            if (version != null) {
                                details.put("version", version);
                            }

                            status.put("details", details);
                            return status;
                        }
                    }
                }
            }
            logger.warn("MySQL health check: Connection not valid");
            status.put("status", "DOWN");
            details.put("error", "Connection validation failed");
            status.put("details", details);
            return status;
        } catch (Exception e) {
            logger.error("MySQL health check failed: {}", e.getMessage());
            status.put("status", "DOWN");
            details.put("error", e.getMessage());
            details.put("errorType", e.getClass().getSimpleName());
            status.put("details", details);
            return status;
        }
    }

    private Map<String, Object> checkRedisHealth() {
        Map<String, Object> status = new LinkedHashMap<>();
        Map<String, Object> details = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();

        // Add connection details
        details.put("type", "Redis");
        details.put("host", redisHost);
        details.put("port", redisPort);

        try {
            // Use ping() from RedisConnection directly
            String pong = redisTemplate.execute((RedisCallback<String>) connection ->
                connection.ping()
            );

            if ("PONG".equals(pong)) {
                long responseTime = System.currentTimeMillis() - startTime;
                logger.debug("Redis health check: UP ({}ms)", responseTime);

                status.put("status", "UP");
                details.put("responseTimeMs", responseTime);

                // Get Redis version
                String version = getRedisVersion();
                if (version != null) {
                    details.put("version", version);
                }

                status.put("details", details);
                return status;
            }
            logger.warn("Redis health check: Ping returned unexpected response");
            status.put("status", "DOWN");
            details.put("error", "Ping returned unexpected response");
            status.put("details", details);
            return status;
        } catch (Exception e) {
            logger.error("Redis health check failed: {}", e.getMessage());
            status.put("status", "DOWN");
            details.put("error", e.getMessage());
            details.put("errorType", e.getClass().getSimpleName());
            status.put("details", details);
            return status;
        }
    }

    private Map<String, Object> checkMongoDBHealth() {
        Map<String, Object> status = new LinkedHashMap<>();
        Map<String, Object> details = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();

        // Add connection details
        details.put("type", "MongoDB");
        details.put("host", mongoHost);
        details.put("port", mongoPort);
        details.put("database", mongoDatabase);

        try {
            // Run ping command to check MongoDB connectivity
            Document pingResult = mongoTemplate.getDb().runCommand(new Document("ping", 1));

            if (pingResult != null && pingResult.getDouble("ok") == 1.0) {
                long responseTime = System.currentTimeMillis() - startTime;
                logger.debug("MongoDB health check: UP ({}ms)", responseTime);

                status.put("status", "UP");
                details.put("responseTimeMs", responseTime);

                // Get MongoDB version
                String version = getMongoDBVersion();
                if (version != null) {
                    details.put("version", version);
                }

                status.put("details", details);
                return status;
            }
            logger.warn("MongoDB health check: Ping returned unexpected response");
            status.put("status", "DOWN");
            details.put("error", "Ping returned unexpected response");
            status.put("details", details);
            return status;
        } catch (Exception e) {
            logger.error("MongoDB health check failed: {}", e.getMessage());
            status.put("status", "DOWN");
            details.put("error", e.getMessage());
            details.put("errorType", e.getClass().getSimpleName());
            status.put("details", details);
            return status;
        }
    }

    private String getMySQLVersion(Connection connection) {
        try (PreparedStatement stmt = connection.prepareStatement(DB_VERSION_QUERY);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            logger.debug("Could not retrieve MySQL version: {}", e.getMessage());
        }
        return null;
    }

    private String getRedisVersion() {
        try {
            Properties info = redisTemplate.execute((RedisCallback<Properties>) connection ->
                connection.serverCommands().info("server")
            );
            if (info != null && info.containsKey("redis_version")) {
                return info.getProperty("redis_version");
            }
        } catch (Exception e) {
            logger.debug("Could not retrieve Redis version: {}", e.getMessage());
        }
        return null;
    }

    private String getMongoDBVersion() {
        try {
            Document buildInfo = mongoTemplate.getDb().runCommand(new Document("buildInfo", 1));
            if (buildInfo != null && buildInfo.containsKey("version")) {
                return buildInfo.getString("version");
            }
        } catch (Exception e) {
            logger.debug("Could not retrieve MongoDB version: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Extracts host from JDBC URL.
     * Example: jdbc:mysql://mysql-container:3306/db_iemr -> mysql-container
     */
    private String extractHost(String jdbcUrl) {
        if (jdbcUrl == null || "unknown".equals(jdbcUrl)) {
            return "unknown";
        }
        try {
            // Remove jdbc:mysql:// prefix
            String withoutPrefix = jdbcUrl.replaceFirst("jdbc:mysql://", "");
            // Get host:port part (before the first /)
            int slashIndex = withoutPrefix.indexOf('/');
            String hostPort = slashIndex > 0 
                ? withoutPrefix.substring(0, slashIndex) 
                : withoutPrefix;
            // Get host (before the colon)
            int colonIndex = hostPort.indexOf(':');
            return colonIndex > 0 ? hostPort.substring(0, colonIndex) : hostPort;
        } catch (Exception e) {
            logger.debug("Could not extract host from URL: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extracts port from JDBC URL.
     * Example: jdbc:mysql://mysql-container:3306/db_iemr -> 3306
     */
    private String extractPort(String jdbcUrl) {
        if (jdbcUrl == null || "unknown".equals(jdbcUrl)) {
            return "unknown";
        }
        try {
            // Remove jdbc:mysql:// prefix
            String withoutPrefix = jdbcUrl.replaceFirst("jdbc:mysql://", "");
            // Get host:port part (before the first /)
            int slashIndex = withoutPrefix.indexOf('/');
            String hostPort = slashIndex > 0 
                ? withoutPrefix.substring(0, slashIndex) 
                : withoutPrefix;
            // Get port (after the colon)
            int colonIndex = hostPort.indexOf(':');
            return colonIndex > 0 ? hostPort.substring(colonIndex + 1) : "3306";
        } catch (Exception e) {
            logger.debug("Could not extract port from URL: {}", e.getMessage());
        }
        return "3306";
    }

    /**
     * Extracts database name from JDBC URL.
     * Example: jdbc:mysql://mysql-container:3306/db_iemr?params -> db_iemr
     */
    private String extractDatabaseName(String jdbcUrl) {
        if (jdbcUrl == null || "unknown".equals(jdbcUrl)) {
            return "unknown";
        }
        try {
            int lastSlash = jdbcUrl.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash < jdbcUrl.length() - 1) {
                String afterSlash = jdbcUrl.substring(lastSlash + 1);
                int queryStart = afterSlash.indexOf('?');
                if (queryStart > 0) {
                    return afterSlash.substring(0, queryStart);
                }
                return afterSlash;
            }
        } catch (Exception e) {
            logger.debug("Could not extract database name: {}", e.getMessage());
        }
        return "unknown";
    }
}
