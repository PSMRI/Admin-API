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
package com.iemr.admin.controller.nikshayphasestatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.nikshayphasestatus.NikshayBeneficiaryType;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhase;
import com.iemr.admin.data.nikshayphasestatus.NikshayPhaseStatus;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayphasestatus.NikshayPhaseStatusService;

import io.swagger.v3.oas.annotations.Operation;

/** Ni-kshay status of each beneficiary in each phase, for the Failed Beneficiaries tab and the sync status. */
@RestController
@RequestMapping(value = "/nikshay/beneficiaryStatus", headers = "Authorization")
public class NikshayPhaseStatusController {
	private static final Logger logger = LoggerFactory.getLogger(NikshayPhaseStatusController.class);

	private static final int MAX_PAGE_SIZE = 200;

	@Autowired
	private NikshayPhaseStatusService phaseStatusService;

	@Operation(summary = "Beneficiaries with their status in every Ni-kshay phase. Filters (all optional): phase, "
			+ "status (PENDING, IN_PROGRESS, DONE, FAILED, NOT_APPLICABLE or NOT_STARTED), failedOnly (with the "
			+ "failed phase, reason and error), failureReason, beneficiaryType (INDEX, CONTACT, ALL_NEGATIVE), "
			+ "search (BenRegId or name); page from 0")
	@GetMapping(value = "/list")
	public ResponseEntity<?> list(@RequestParam(required = false) NikshayPhase phase,
			@RequestParam(required = false) String status,
			@RequestParam(defaultValue = "false") boolean failedOnly,
			@RequestParam(required = false) NikshayFailureReason failureReason,
			@RequestParam(required = false) NikshayBeneficiaryType beneficiaryType,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		if (status != null && !status.equals("NOT_STARTED")) {
			try {
				NikshayPhaseStatus.valueOf(status);
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body("Unknown status " + status);
			}
		}
		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
			return ResponseEntity.badRequest().body("page must be 0 or more and size 1 to " + MAX_PAGE_SIZE);
		}
		try {
			return ResponseEntity.ok(phaseStatusService.getStatusList(phase, status, failedOnly, failureReason,
					beneficiaryType, search, page, size));
		} catch (Exception e) {
			logger.error("Error reading the Ni-kshay beneficiary status list", e);
			return ResponseEntity.status(500).body("Could not read the Ni-kshay beneficiary status list");
		}
	}

	@Operation(summary = "Number of beneficiaries per Ni-kshay phase and status")
	@GetMapping(value = "/summary")
	public ResponseEntity<?> summary() {
		try {
			return ResponseEntity.ok(phaseStatusService.getSummary());
		} catch (Exception e) {
			logger.error("Error reading the Ni-kshay phase status summary", e);
			return ResponseEntity.status(500).body("Could not read the Ni-kshay phase status summary");
		}
	}
}
