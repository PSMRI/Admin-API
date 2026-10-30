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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemr.admin.data.nikshayidgenerate.NikshayHouseholdMemberDTO;
import com.iemr.admin.data.nikshayidgenerate.NikshayIdGenerateBeneficiaryDTO;

/**
 * Builds the AddIndiaTB body for an index patient (nikshay-api-spec "MINIMAL NEW-PERSON
 * BODY"). With a parentEpisodeId it creates a new episode under that existing record.
 * With a sourcePatientId it adds the person as a household contact of that index
 * patient (spec "CAPTURED CONTACT ADDINDIATB BODY").
 */
@Component
public class NikshayEnrollPayloadBuilder {

	private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	// BRD section 12: Active (Active Case Finding), to confirm with the programme
	@Value("${nikshay.enroll.type-of-case-finding:Active (Active Case Finding)}")
	private String typeOfCaseFinding;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public ObjectNode build(NikshayIdGenerateBeneficiaryDTO beneficiary, String parentEpisodeId,
			boolean isPossibleDuplicate, String sourcePatientId) {
		ObjectNode payload = objectMapper.createObjectNode();
		payload.put("TypeOfPatient", "IndiaTbPublic");
		payload.put("Stage", "PRESUMPTIVE_OPEN");
		payload.put("RegistrationDate", LocalDate.now().format(DD_MM_YYYY));
		payload.put("SelectedHierarchyId", beneficiary.getSelectedHierarchyId());
		payload.put("FirstName", beneficiary.getFirstName().trim());
		payload.put("LastName", beneficiary.getLastName().trim());
		payload.put("FathersName", beneficiary.getFathersName());
		payload.put("Age", String.valueOf(beneficiary.getAge()));
		payload.put("Gender", beneficiary.getGender());
		payload.put("PrimaryPhone", beneficiary.getPrimaryPhone());
		payload.putNull("SecondaryPhone1");
		payload.putNull("SecondaryPhone2");
		payload.putNull("SecondaryPhone3");
		payload.put("Address", beneficiary.getAddress().trim());
		payload.putNull("Ward");
		payload.putNull("Taluka");
		payload.putNull("Landmark");
		payload.put("ResidenceHierarchyId", beneficiary.getNikshayPortalVillageId());
		ArrayNode residenceHierarchyAll = payload.putArray("ResidenceHierarchyAll");
		residenceHierarchyAll.add(1);
		residenceHierarchyAll.add(beneficiary.getNikshayStateId());
		residenceHierarchyAll.add(beneficiary.getNikshayDistrictId());
		residenceHierarchyAll.add(beneficiary.getNikshayTuId());
		residenceHierarchyAll.add(beneficiary.getNikshayPortalVillageId());
		payload.put("Pincode", beneficiary.getPincode());
		payload.put("Area", beneficiary.getArea());
		payload.put("MaritalStatus", beneficiary.getMaritalStatus());
		payload.put("Occupation", beneficiary.getOccupation());
		payload.put("SocioeconomicStatus", beneficiary.getSocioeconomicStatus());
		payload.put("KeyPopulation", beneficiary.getKeyPopulation());
		payload.putNull("VaccinationDate");
		payload.put("Symptom", beneficiary.getSymptom());
		payload.put("HIVStatus", beneficiary.getHivStatus());
		payload.putNull("ContactPersonPhone");
		payload.putNull("ContactPersonName");
		payload.putNull("ContactPersonAddress");
		if (parentEpisodeId == null) {
			payload.putNull("parentEpisodeId");
		} else {
			payload.put("parentEpisodeId", Long.parseLong(parentEpisodeId));
		}
		payload.put("IsPossibleDuplicate", isPossibleDuplicate);
		if (sourcePatientId == null) {
			payload.putNull("Source");
			payload.putNull("SourcePatientId");
		} else {
			// Contact of the index patient: their episode ID, as a string
			payload.put("Source", "contactTracing");
			payload.put("SourcePatientId", sourcePatientId);
		}
		payload.put("TypeOfCaseFinding", typeOfCaseFinding);
		payload.putNull("InformantId");
		payload.put("DateOfBirth", beneficiary.getDateOfBirth());
		payload.putArray("FollowUpsDone");
		payload.putNull("TbWinReferenceId");
		payload.put("caste", beneficiary.getCaste());
		payload.putNull("healthId");
		payload.putNull("healthAddress");
		return payload;
	}

	/**
	 * Builds the ContactTracing body: the index patient's household counts, split at five
	 * years (same as Q2 of NIKSHAY_ENROLLMENT.sql; no age counts as five or more). Every
	 * member is counted, also those already in Ni-kshay. The unsplit totals stay null and
	 * the below-five fields stay null when there is no one under five, as in the capture.
	 */
	public ObjectNode buildContactTracing(String indexEpisodeId, List<NikshayHouseholdMemberDTO> members) {
		int aboveFive = 0;
		int screenedAboveFive = 0;
		int belowFive = 0;
		int screenedBelowFive = 0;
		for (NikshayHouseholdMemberDTO member : members) {
			boolean screened = Boolean.TRUE.equals(member.getTbScreened());
			if (member.getAge() != null && member.getAge() < 5) {
				belowFive++;
				if (screened) {
					screenedBelowFive++;
				}
			} else {
				aboveFive++;
				if (screened) {
					screenedAboveFive++;
				}
			}
		}

		ObjectNode payload = objectMapper.createObjectNode();
		payload.put("Id", Long.parseLong(indexEpisodeId));
		for (String field : new String[] { "NumberOfHouseholdContacts", "NumberOfScreened", "NumberOfWithSymptoms",
				"NumberOfEvaluated", "NumberOfDiagnosed", "NumberOfOnTreatment", "NumberEligibleForTPT",
				"NumberProvidedTPT" }) {
			payload.putNull(field);
		}
		putCounts(payload, "AboveFive", aboveFive, screenedAboveFive);
		if (belowFive > 0) {
			putCounts(payload, "BelowFive", belowFive, screenedBelowFive);
		} else {
			for (String field : countFields("BelowFive")) {
				payload.putNull(field);
			}
		}
		return payload;
	}

	// Contacts and screened as counted; symptoms, evaluated, diagnosed and TPT are 0 as in the capture
	private static void putCounts(ObjectNode payload, String band, int contacts, int screened) {
		String[] fields = countFields(band);
		payload.put(fields[0], contacts);
		payload.put(fields[1], screened);
		for (int i = 2; i < fields.length; i++) {
			payload.put(fields[i], 0);
		}
	}

	private static String[] countFields(String band) {
		return new String[] { "NumberOfHouseholdContacts" + band, "NumberOfScreened" + band,
				"NumberOfWithSymptoms" + band, "NumberOfEvaluated" + band, "NumberOfDiagnosed" + band,
				"NumberOfOnTreatment" + band, "NumberEligibleForTPT" + band, "NumberProvidedTPT" + band };
	}
}
