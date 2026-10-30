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

/** Where a beneficiary is in one phase. A phase with no row has not started. */
public enum NikshayPhaseStatus {
	// Due, not tried yet (e.g. the household is not fully screened)
	PENDING,
	// The job is working on it
	IN_PROGRESS,
	DONE,
	// Tried and failed; the row has the reason and the error
	FAILED,
	// The phase does not apply (e.g. contacts of an all-negative member)
	NOT_APPLICABLE
}
