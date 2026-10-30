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
package com.iemr.admin.data.nikshayidgenerate;

/** How a beneficiary was linked to Ni-kshay at enrolment (BRD section 7). */
public enum NikshayLinkStatus {
	// No duplicate: a fresh Ni-kshay ID was created (case 1)
	NEW,
	// Duplicate is Presumptive - Open: the existing record is used, tests go to it (case 2.1)
	LINKED,
	// Duplicate is closed or has an outcome: a new episode was created under it (cases 2.2, 2.4, 2.6)
	NEW_EPISODE,
	// Duplicate is diagnosed (not on treatment) or on treatment: ID fetched only, no tests (cases 2.3, 2.5)
	LINKED_NO_ACTION
}
