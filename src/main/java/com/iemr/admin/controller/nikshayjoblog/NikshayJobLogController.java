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
package com.iemr.admin.controller.nikshayjoblog;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayjoblog.NikshayJobHistoryLogService;
import com.iemr.admin.service.nikshayjoblog.NikshayPhase;

import io.swagger.v3.oas.annotations.Operation;

/** Read side of the Ni-kshay phase history: failed items and one beneficiary's history. */
@RestController
@RequestMapping(value = "/nikshay/log", headers = "Authorization")
public class NikshayJobLogController {
	private static final Logger logger = LoggerFactory.getLogger(NikshayJobLogController.class);

	private static final int MAX_PAGE_SIZE = 200;

	@Autowired
	private NikshayJobHistoryLogService logService;

	@Operation(summary = "Beneficiaries whose latest attempt of a phase failed, with reason, attempts and last attempt time")
	@GetMapping(value = "/failed")
	public ResponseEntity<?> getFailed(@RequestParam(value = "phase", required = false) NikshayPhase phase,
			@RequestParam(value = "reason", required = false) NikshayFailureReason reason,
			@RequestParam(value = "benId", required = false) Long benId,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "50") int size) {
		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
			return ResponseEntity.badRequest().body("page must be >= 0 and size between 1 and " + MAX_PAGE_SIZE);
		}
		try {
			// Dates are whole days: toDate is included, so the query bound is the next day's start.
			Page<NikshayJobHistoryLog> failed = logService.getFailed(phase, reason, benId,
					fromDate != null ? Timestamp.valueOf(fromDate.atStartOfDay()) : null,
					toDate != null ? Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()) : null, page, size);
			Map<String, Object> body = new HashMap<>();
			body.put("entries", failed.getContent());
			body.put("totalElements", failed.getTotalElements());
			body.put("page", page);
			body.put("size", size);
			return ResponseEntity.ok(body);
		} catch (Exception e) {
			logger.error("Error reading failed Ni-kshay beneficiaries", e);
			return ResponseEntity.status(500).body("Could not read failed beneficiaries");
		}
	}

	@Operation(summary = "Every Ni-kshay phase entry of one beneficiary (benRegId), newest first")
	@GetMapping(value = "/history")
	public ResponseEntity<?> getHistory(@RequestParam("benId") Long benId) {
		try {
			return ResponseEntity.ok(logService.getHistory(benId));
		} catch (Exception e) {
			logger.error("Error reading Ni-kshay history for ben {}", benId, e);
			return ResponseEntity.status(500).body("Could not read the beneficiary's history");
		}
	}
}
