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
package com.iemr.admin.service.nikshayidgenerate;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iemr.admin.data.nikshayidgenerate.NikshayIdGenerateBeneficiaryDTO;

/**
 * Checks a beneficiary against the BRD section 13 rules before enrolment in Ni-kshay.
 * Auto-fixes (V7) are applied to the beneficiary and logged; every other failed rule
 * is returned as a message for staff to rectify.
 *
 * Not checked here: V1 and V4 (age is worked out from the date of birth, there is no
 * separately entered age), V10 (village, checked when grouping by village), V12 and V13
 * (TPT, not for index patients), V14 (needs the Ni-kshay duplicate check) and V15
 * (the eligibility query leaves out beneficiaries that already have a Ni-kshay ID).
 */
@Component
public class NikshayBeneficiaryValidator {
	private static final Logger logger = LoggerFactory.getLogger(NikshayBeneficiaryValidator.class);

	// English letters only, words separated by single spaces
	private static final String ENGLISH_NAME = "[A-Za-z]+( [A-Za-z]+)*";
	// Printable English (ASCII) characters only, so Hindi script is rejected
	private static final String ENGLISH_TEXT = "[\\x20-\\x7E]+";
	private static final int MAX_ADDRESS_LENGTH = 2000;

	/** Returns the problems found, empty when the beneficiary can be enrolled. */
	public List<String> validate(NikshayIdGenerateBeneficiaryDTO beneficiary) {
		List<String> problems = new ArrayList<>();
		checkAge(beneficiary, problems);
		checkName(beneficiary, problems);
		checkPhone(beneficiary, problems);
		checkAddress(beneficiary, problems);
		checkGenderAndCaste(beneficiary, problems);
		checkOtherMandatoryFields(beneficiary, problems);
		return problems;
	}

	// V2, V3
	private void checkAge(NikshayIdGenerateBeneficiaryDTO beneficiary, List<String> problems) {
		Integer age = beneficiary.getAge();
		if (age == null) {
			problems.add("V3: Age is missing (no date of birth)");
		} else if (age < 1) {
			problems.add("V2: Age is under 1 year; enrol manually as per programme guidance");
		} else if (age > 99) {
			problems.add("V3: Age must be 1 to 99 (found " + age + ")");
		}
	}

	// V5, V6
	private void checkName(NikshayIdGenerateBeneficiaryDTO beneficiary, List<String> problems) {
		String firstName = trim(beneficiary.getFirstName());
		String lastName = trim(beneficiary.getLastName());
		if (firstName == null) {
			problems.add("V16: First name is missing");
		} else if (!firstName.matches(ENGLISH_NAME)) {
			problems.add("V5: First name must have English letters only (found '" + firstName + "')");
		}
		if (lastName == null) {
			problems.add("V6: Middle / last name is missing (only one name word)");
		} else if (!lastName.matches(ENGLISH_NAME)) {
			problems.add("V5: Middle / last name must have English letters only (found '" + lastName + "')");
		}
	}

	// V7 (auto-fix), V8
	private void checkPhone(NikshayIdGenerateBeneficiaryDTO beneficiary, List<String> problems) {
		String phone = beneficiary.getPrimaryPhone();
		if (phone == null || phone.isBlank()) {
			problems.add("V8: Primary phone is missing");
			return;
		}
		String cleaned = phone.replaceAll("\\s", "");
		if (cleaned.startsWith("+91")) {
			cleaned = cleaned.substring(3);
		}
		cleaned = cleaned.replaceFirst("^0+", "");
		if (!cleaned.equals(phone)) {
			logger.info("V7: Beneficiary {} phone auto-fixed from '{}' to '{}'", beneficiary.getBeneficiaryId(),
					phone, cleaned);
			beneficiary.setPrimaryPhone(cleaned);
		}
		if (!cleaned.matches("[0-9]{10}")) {
			problems.add("V8: Primary phone must be 10 digits (found '" + phone + "')");
		}
	}

	// V9
	private void checkAddress(NikshayIdGenerateBeneficiaryDTO beneficiary, List<String> problems) {
		String address = trim(beneficiary.getAddress());
		if (address == null) {
			problems.add("V9: Address is missing");
		} else if (address.length() > MAX_ADDRESS_LENGTH) {
			problems.add("V9: Address is longer than " + MAX_ADDRESS_LENGTH + " characters");
		} else if (!address.matches(ENGLISH_TEXT)) {
			problems.add("V9: Address must be in English");
		}
	}

	// V11
	private void checkGenderAndCaste(NikshayIdGenerateBeneficiaryDTO beneficiary, List<String> problems) {
		if (trim(beneficiary.getGender()) == null) {
			problems.add("V11: Gender is missing");
		}
		if (trim(beneficiary.getCaste()) == null) {
			problems.add("V11: Caste is missing");
		}
	}

	// V16: the other mandatory section 12 fields sent in the enrolment
	private void checkOtherMandatoryFields(NikshayIdGenerateBeneficiaryDTO beneficiary, List<String> problems) {
		if (trim(beneficiary.getSymptom()) == null) {
			problems.add("V16: Symptom is missing");
		}
		// Not in the BRD: Ni-kshay rejects the enrolment without it ("Pincode is mandatory"),
		// also for contacts; the spec gives 6 digits
		String pincode = trim(beneficiary.getPincode());
		if (pincode == null) {
			problems.add("V16: Pincode is missing");
		} else if (!pincode.matches("[0-9]{6}")) {
			problems.add("V16: Pincode must be 6 digits (found '" + pincode + "')");
		}
		if (beneficiary.getNikshayStateId() == null || beneficiary.getNikshayDistrictId() == null
				|| beneficiary.getNikshayTuId() == null || beneficiary.getSelectedHierarchyId() == null) {
			problems.add("V16: Ni-kshay state / district / TU / health facility not found for the village");
		}
	}

	// Null when blank
	private static String trim(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
