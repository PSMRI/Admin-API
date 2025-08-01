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
package com.iemr.admin.controller.version;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class VersionController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Operation(summary = "Version information")
	@RequestMapping(value = "/version", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public String versionInformation() {
		OutputResponse output = new OutputResponse();
		try {
			logger.info("version Controller Start");
			// Set the version information as JSON string directly
			output.setResponse(readGitPropertiesAsJson());
		} catch (Exception e) {
			output.setError(e);
		}

		logger.info("version Controller End");
		
		// Use standard toString() - no custom formatting
		return output.toString();
	}

	private String readGitPropertiesAsJson() throws Exception {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		
		// Read Git properties
		Properties gitProps = loadPropertiesFile("git.properties");
		if (gitProps != null) {
			// For git.commit.id, look for both standard and abbrev versions
			String commitId = gitProps.getProperty("git.commit.id", null);
			if (commitId == null) {
				commitId = gitProps.getProperty("git.commit.id.abbrev", "unknown");
			}
			json.append("    \"git.commit.id\": \"").append(commitId).append("\",\n");
			
			// For git.build.time, look for various possible property names
			String buildTime = gitProps.getProperty("git.build.time", null);
			if (buildTime == null) {
				buildTime = gitProps.getProperty("git.commit.time", null);
			}
			if (buildTime == null) {
				buildTime = gitProps.getProperty("git.commit.timestamp", "unknown");
			}
			json.append("    \"git.build.time\": \"").append(buildTime).append("\",\n");
		} else {
			logger.warn("git.properties file not found. Git information will be unavailable.");
			json.append("    \"git.commit.id\": \"information unavailable\",\n");
			json.append("    \"git.build.time\": \"information unavailable\",\n");
		}
		
		// Read build properties if available
		Properties buildProps = loadPropertiesFile("META-INF/build-info.properties");
		if (buildProps != null) {
			// Extract version - checking for both standard and nested formats
			String version = buildProps.getProperty("build.version", null);
			if (version == null) {
				version = buildProps.getProperty("build.version.number", null);
			}
			if (version == null) {
				version = buildProps.getProperty("version", "unknown");
			}
			json.append("    \"build.version\": \"").append(version).append("\",\n");
			
			// Extract time - checking for both standard and alternate formats
			String time = buildProps.getProperty("build.time", null);
			if (time == null) {
				time = buildProps.getProperty("build.timestamp", null);
			}
			if (time == null) {
				time = buildProps.getProperty("timestamp", "unknown");
			}
			json.append("    \"build.time\": \"").append(time).append("\",\n");
		} else {
			logger.info("build-info.properties not found, trying Maven properties");
			// Fallback to maven project version
			Properties mavenProps = loadPropertiesFile("META-INF/maven/com.iemr.admin/admin-api/pom.properties");
			if (mavenProps != null) {
				String version = mavenProps.getProperty("version", "unknown");
				json.append("    \"build.version\": \"").append(version).append("\",\n");
				json.append("    \"build.time\": \"").append(getCurrentIstTimeFormatted()).append("\",\n");
			} else {
				logger.warn("Neither build-info.properties nor Maven properties found.");
				json.append("    \"build.version\": \"3.1.0\",\n");  // Default version
				json.append("    \"build.time\": \"").append(getCurrentIstTimeFormatted()).append("\",\n");
			}
		}
		json.append("    \"current.time\": \"").append(getCurrentIstTimeFormatted()).append("\"\n");
		
		json.append("  }");
		return json.toString();
	}
	
	/**
	 * Get the current time formatted in Indian Standard Time (IST)
	 * IST is UTC+5:30
	 */
	private String getCurrentIstTimeFormatted() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		return sdf.format(new Date());
	}

	private Properties loadPropertiesFile(String resourceName) {
		ClassLoader classLoader = getClass().getClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream(resourceName)) {
			if (inputStream != null) {
				Properties props = new Properties();
				props.load(inputStream);
				return props;
			}
		} catch (IOException e) {
			logger.warn("Could not load properties file: " + resourceName, e);
		}
		return null;
	}
}