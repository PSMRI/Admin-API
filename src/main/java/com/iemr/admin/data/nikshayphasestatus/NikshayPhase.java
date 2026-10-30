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
package com.iemr.admin.data.nikshayphasestatus;

/**
 * A Ni-kshay phase a beneficiary goes through, in the order the phases are run. Each phase
 * is run on its own (POST /nikshay/phase/{phase}/run) and picks the beneficiaries waiting in
 * it from nikshay_beneficiary_phase_status. logPhase is the phase's name in the job history
 * log (nikshay_worker_job_history_log).
 */
public enum NikshayPhase {
	// The beneficiary's Ni-kshay ID (index patient, contact or all-negative member)
	ENROLMENT(com.iemr.admin.service.nikshayjoblog.NikshayPhase.NIKSHAY_ID_GENERATION),
	// The TrueNat results, MTB and Rif resistance (MTB-RIF), added to the beneficiary's
	// latest episode
	NAAT_UPLOAD(com.iemr.admin.service.nikshayjoblog.NikshayPhase.NAAT),
	// The chest X-ray result, added to the beneficiary's latest episode
	XRAY_UPLOAD(com.iemr.admin.service.nikshayjoblog.NikshayPhase.XRAY),
	// The index patient's household counts and contacts
	CONTACTS(com.iemr.admin.service.nikshayjoblog.NikshayPhase.CONTACT_TPT),
	// The episode moved to the village's designated PHI, once the tests are uploaded
	TRANSFER_OUT(com.iemr.admin.service.nikshayjoblog.NikshayPhase.TRANSFER_OUT);

	private final com.iemr.admin.service.nikshayjoblog.NikshayPhase logPhase;

	NikshayPhase(com.iemr.admin.service.nikshayjoblog.NikshayPhase logPhase) {
		this.logPhase = logPhase;
	}

	public com.iemr.admin.service.nikshayjoblog.NikshayPhase getLogPhase() {
		return logPhase;
	}
}
