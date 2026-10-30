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
package com.iemr.admin.data.nikshayupload;

/**
 * Fields the upload needs from a test result, whatever the test type
 * (NaatResultDTO, XrayResultDTO). The Lombok getters implement it.
 */
public interface NikshayTestResult {

	Long getBeneficiaryId();

	Long getBenRegId();

	String getExternalOrderId();

	Integer getVillageId();

	String getVillage();

	String getNikshayVillageName();

	// Beneficiary's Ni-kshay ID (PatientId in Ni-kshay)
	String getNikshayId();

	// The phase upload sets it to the beneficiary's latest episode, where the test goes
	void setNikshayId(String nikshayId);

	// EMR Lite result summary, e.g. "Normal" or "TB Positive"
	String getResult();

	// When the result was saved (tb_diagnostic_result.created_date): the test's date in Ni-kshay
	java.sql.Timestamp getResultDate();
}
