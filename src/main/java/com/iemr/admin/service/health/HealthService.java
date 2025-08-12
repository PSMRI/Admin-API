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
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private static final Logger logger = LoggerFactory.getLogger(HealthService.class);
    private static final String DB_HEALTH_CHECK_QUERY = "SELECT 1 as health_check";

    @Autowired
    private DataSource dataSource;

    @Value("${app.version:unknown}")
    private String appVersion;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> checkHealth() {
        Map<String, Object> healthStatus = new HashMap<>();
        Map<String, Object> services = new HashMap<>();
        boolean overallHealth = true;

        // Check database connectivity
        Map<String, Object> dbStatus = checkDatabaseHealth();
        services.put("database", dbStatus);
        if (!"UP".equals(dbStatus.get("status"))) {
            overallHealth = false;
        }

        // Check Redis connectivity if configured
        if (redisTemplate != null) {
            Map<String, Object> redisStatus = checkRedisHealth();
            services.put("redis", redisStatus);
            if (!"UP".equals(redisStatus.get("status"))) {
                overallHealth = false;
            }
        } else {
            Map<String, Object> redisStatus = new HashMap<>();
            redisStatus.put("status", "NOT_CONFIGURED");
            redisStatus.put("message", "Redis not configured for this environment");
            services.put("redis", redisStatus);
        }

        healthStatus.put("status", overallHealth ? "UP" : "DOWN");
        healthStatus.put("services", services);
        healthStatus.put("timestamp", Instant.now().toString());
        healthStatus.put("application", "admin-api");
        healthStatus.put("version", appVersion);

        logger.info("Health check completed - Overall status: {}", overallHealth ? "UP" : "DOWN");
        return healthStatus;
    }

    private Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> dbStatus = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try (Connection connection = dataSource.getConnection()) {
            // Test connection validity
            boolean isConnectionValid = connection.isValid(5); // 5 second timeout
            
            if (isConnectionValid) {
                // Execute a simple query to ensure database is responsive
                try (PreparedStatement stmt = connection.prepareStatement(DB_HEALTH_CHECK_QUERY);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next() && rs.getInt(1) == 1) {
                        long responseTime = System.currentTimeMillis() - startTime;
                        
                        dbStatus.put("status", "UP");
                        dbStatus.put("responseTime", responseTime + "ms");
                        dbStatus.put("message", "Database connection successful");
                        
                        logger.debug("Database health check: UP ({}ms)", responseTime);
                    } else {
                        dbStatus.put("status", "DOWN");
                        dbStatus.put("message", "Database query returned unexpected result");
                        logger.warn("Database health check: Query returned unexpected result");
                    }
                }
            } else {
                dbStatus.put("status", "DOWN");
                dbStatus.put("message", "Database connection is not valid");
                logger.warn("Database health check: Connection is not valid");
            }
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            dbStatus.put("status", "DOWN");
            dbStatus.put("responseTime", responseTime + "ms");
            dbStatus.put("error", e.getClass().getSimpleName());
            
            logger.error("Database health check failed: {}", e.getMessage(), e);
        }
        
        return dbStatus;
    }

    private Map<String, Object> checkRedisHealth() {
        Map<String, Object> redisStatus = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Test Redis connection with ping using explicit RedisCallback
            String pong = redisTemplate.execute((RedisCallback<String>) connection -> {
                return connection.ping();
            });
            
            if ("PONG".equals(pong)) {
                long responseTime = System.currentTimeMillis() - startTime;
                
                // Additional test: set and get a test key
                String testKey = "health:check:" + System.currentTimeMillis();
                String testValue = "test-value";
                
                redisTemplate.opsForValue().set(testKey, testValue);
                Object retrievedValue = redisTemplate.opsForValue().get(testKey);
                redisTemplate.delete(testKey); // Clean up test key
                
                if (testValue.equals(retrievedValue)) {
                    redisStatus.put("status", "UP");
                    redisStatus.put("responseTime", responseTime + "ms");
                    redisStatus.put("message", "Redis connection and operations successful");
                    redisStatus.put("ping", "PONG");
                    
                    logger.debug("Redis health check: UP ({}ms)", responseTime);
                } else {
                    redisStatus.put("status", "DOWN");
                    redisStatus.put("message", "Redis set/get operation failed");
                    logger.warn("Redis health check: Set/Get operation failed");
                }
            } else {
                redisStatus.put("status", "DOWN");
                redisStatus.put("message", "Redis ping returned: " + pong);
                logger.warn("Redis health check: Ping returned unexpected response: {}", pong);
            }
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            redisStatus.put("status", "DOWN");
            redisStatus.put("message", "Redis connection failed: " + e.getMessage());
            redisStatus.put("responseTime", responseTime + "ms");
            redisStatus.put("error", e.getClass().getSimpleName());
            
            logger.error("Redis health check failed", e);
        }
        
        return redisStatus;
    }
}