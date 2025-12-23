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
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private static final Logger logger = LoggerFactory.getLogger(HealthService.class);
    private static final String DB_HEALTH_CHECK_QUERY = "SELECT 1 as health_check";

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> checkHealth() {
        Map<String, Object> healthStatus = new HashMap<>();
        boolean overallHealth = true;

        // Check database connectivity (details logged internally, not exposed)
        boolean dbHealthy = checkDatabaseHealthInternal();
        if (!dbHealthy) {
            overallHealth = false;
        }

        // Check Redis connectivity if configured (details logged internally)
        if (redisTemplate != null) {
            boolean redisHealthy = checkRedisHealthInternal();
            if (!redisHealthy) {
                overallHealth = false;
            }
        }

        healthStatus.put("status", overallHealth ? "UP" : "DOWN");

        logger.info("Health check completed - Overall status: {}", overallHealth ? "UP" : "DOWN");
        return healthStatus;
    }

    private boolean checkDatabaseHealthInternal() {
        long startTime = System.currentTimeMillis();
        
        try (Connection connection = dataSource.getConnection()) {
            boolean isConnectionValid = connection.isValid(2); // 2 second timeout per best practices
            
            if (isConnectionValid) {
                try (PreparedStatement stmt = connection.prepareStatement(DB_HEALTH_CHECK_QUERY)) {
                    stmt.setQueryTimeout(3); // 3 second query timeout
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 1) {
                            long responseTime = System.currentTimeMillis() - startTime;
                            logger.debug("Database health check: UP ({}ms)", responseTime);
                            return true;
                        }
                    }
                }
            }
            logger.warn("Database health check: Connection not valid");
            return false;
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean checkRedisHealthInternal() {
        long startTime = System.currentTimeMillis();
        
        try {
            String pong = redisTemplate.execute((RedisCallback<String>) connection -> connection.ping());
            
            if ("PONG".equals(pong)) {
                long responseTime = System.currentTimeMillis() - startTime;
                logger.debug("Redis health check: UP ({}ms)", responseTime);
                return true;
            }
            logger.warn("Redis health check: Ping returned unexpected response");
            return false;
        } catch (Exception e) {
            logger.error("Redis health check failed: {}", e.getMessage());
            return false;
        }
    }
}
