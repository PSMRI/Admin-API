/*
* AMRIT – Accessible Medical Records via Integrated Technologies
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
package com.iemr.admin.controller.nikshayphasestatus;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.nikshayidgenerate.NikshayErroredBeneficiaryDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayJobScheduleDTO;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayRunMode;
import com.iemr.admin.service.nikshayjoblog.NikshayJobRunContext;
import com.iemr.admin.service.nikshayjoblog.NikshayTriggerType;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseRunService;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseRunSettingService;
import com.iemr.admin.utils.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Runs the Ni-kshay phases one at a time (ENROLMENT, XRAY_UPLOAD, NAAT_UPLOAD, CONTACTS) and
 * sets their run mode (automatic job or manual push only).
 */
@RestController
@RequestMapping(value = "/nikshay/phase", headers = "Authorization")
public class NikshayPhaseController {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseController.class);

	@Autowired
	private NikshayPhaseRunService phaseRunService;

	@Autowired
	private NikshayPhaseRunSettingService settingService;

	@Autowired
	private JwtUtil jwtUtil;

	@Operation(summary = "Run a Ni-kshay phase for the beneficiaries waiting in it, or for the given BenRegIds "
			+ "(optional body); returns the ones that failed")
	@PostMapping(value = "/{phase}/run")
	public ResponseEntity<?> run(@PathVariable("phase") NikshayPhase phase,
			@RequestBody(required = false) List<Long> benRegIds, HttpServletRequest request) {
		try {
			List<NikshayErroredBeneficiaryDTO> errored = phaseRunService.run(phase,
					benRegIds == null || benRegIds.isEmpty() ? null : benRegIds, context(request));
			return ResponseEntity.ok(errored);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(409).body(e.getMessage());
		} catch (Exception e) {
			logger.error("Error running Ni-kshay phase {}", phase, e);
			return ResponseEntity.status(500).body("Could not run Ni-kshay phase " + phase);
		}
	}

	@Operation(summary = "Set the given BenRegIds back to PENDING in a Ni-kshay phase (reprocess); with runNow=true, "
			+ "run the phase for them straight away and return the ones that failed")
	@PostMapping(value = "/{phase}/reprocess")
	public ResponseEntity<?> reprocess(@PathVariable("phase") NikshayPhase phase, @RequestBody List<Long> benRegIds,
			@RequestParam(value = "runNow", defaultValue = "false") boolean runNow, HttpServletRequest request) {
		if (benRegIds == null || benRegIds.isEmpty()) {
			return ResponseEntity.badRequest().body("At least one BenRegId is required");
		}
		try {
			List<NikshayErroredBeneficiaryDTO> errored = phaseRunService.reprocess(phase, benRegIds, runNow,
					context(request));
			return ResponseEntity.ok(errored != null ? errored
					: benRegIds.size() + " beneficiaries set for reprocess in " + phase);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(409).body(e.getMessage());
		} catch (Exception e) {
			logger.error("Error reprocessing Ni-kshay phase {}", phase, e);
			return ResponseEntity.status(500).body("Could not reprocess Ni-kshay phase " + phase);
		}
	}

	@Operation(summary = "Stored run mode of each phase. The scheduled job does not use this. "
			+ "One switch in nikshay_job_schedule turns every phase on or off together.")
	@GetMapping(value = "/settings")
	public ResponseEntity<?> settings() {
		try {
			return ResponseEntity.ok(settingService.findAll());
		} catch (Exception e) {
			logger.error("Error reading the Ni-kshay phase settings", e);
			return ResponseEntity.status(500).body("Could not read the Ni-kshay phase settings");
		}
	}

	@Operation(summary = "Set a Ni-kshay phase's run mode, body {\"runMode\": \"AUTOMATIC\"} or {\"runMode\": \"MANUAL\"}; "
			+ "returns the saved setting")
	@PutMapping(value = "/{phase}/setting")
	public ResponseEntity<?> setRunMode(@PathVariable("phase") NikshayPhase phase,
			@RequestBody Map<String, String> body, HttpServletRequest request) {
		String value = body == null ? null : body.get("runMode");
		NikshayRunMode runMode;
		try {
			runMode = NikshayRunMode.valueOf(value == null ? "" : value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("runMode must be AUTOMATIC or MANUAL");
		}
		try {
			return ResponseEntity.ok(settingService.setRunMode(phase, runMode, currentUsername(request)));
		} catch (Exception e) {
			logger.error("Error setting the run mode of Ni-kshay phase {}", phase, e);
			return ResponseEntity.status(500).body("Could not set the run mode of Ni-kshay phase " + phase);
		}
	}

	@Operation(summary = "The one Ni-kshay cron job: enabled and cron from nikshay_job_schedule. "
			+ "When it is on, every phase runs. There is no separate on/off per phase.")
	@GetMapping(value = "/job")
	public ResponseEntity<?> jobSchedule() {
		try {
			return ResponseEntity.ok(settingService.findSchedule());
		} catch (Exception e) {
			logger.error("Error reading the Ni-kshay job schedule", e);
			return ResponseEntity.status(500).body("Could not read the Ni-kshay job schedule");
		}
	}

	@Operation(summary = "Set the one Ni-kshay cron job. Body {\"enabled\": true, \"cron\": \"0 0 2 * * *\"}. "
			+ "Cron is Spring format: second minute hour day month weekday. Applies without a restart.")
	@PutMapping(value = "/job")
	public ResponseEntity<?> setJobSchedule(@RequestBody Map<String, Object> body, HttpServletRequest request) {
		boolean enabled = false;
		String cron = null;
		if (body != null) {
			Object enabledValue = body.get("enabled");
			enabled = enabledValue instanceof Boolean ? (Boolean) enabledValue
					: Boolean.parseBoolean(enabledValue == null ? "" : enabledValue.toString());
			cron = body.get("cron") == null ? null : body.get("cron").toString();
		}
		try {
			NikshayJobScheduleDTO saved = settingService.saveSchedule(enabled, cron, currentUsername(request));
			return ResponseEntity.ok(saved);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			logger.error("Error saving the Ni-kshay job schedule", e);
			return ResponseEntity.status(500).body("Could not save the Ni-kshay job schedule");
		}
	}

	private NikshayJobRunContext context(HttpServletRequest request) {
		return new NikshayJobRunContext(NikshayTriggerType.MANUAL, currentUsername(request));
	}

	/** Best-effort: only recorded as triggered_by in the history log. */
	private String currentUsername(HttpServletRequest request) {
		try {
			String header = request.getHeader("Authorization");
			if (header == null) {
				return "unknown";
			}
			String token = header.startsWith("Bearer ") ? header.substring(7) : header;
			String username = jwtUtil.extractUsername(token);
			return username != null ? username : "unknown";
		} catch (Exception e) {
			return "unknown";
		}
	}
}
