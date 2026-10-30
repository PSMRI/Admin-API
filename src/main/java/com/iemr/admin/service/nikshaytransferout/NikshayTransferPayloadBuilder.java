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
package com.iemr.admin.service.nikshaytransferout;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Builds the body of Ni-kshay's transfer (registry POST /v1/transfer), as captured from the
 * portal (nikshay-api-spec section 7): the episode moves from the PHI it was created under
 * to the village's designated PHI, as the current treating PHI.
 */
@Component
public class NikshayTransferPayloadBuilder {

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * episodeId: the episode to move. fromPhi: the SelectedHierarchyId it was created under.
	 * toPhi: the designated PHI. address and phone: as sent at enrolment.
	 */
	public ObjectNode build(String episodeId, long fromPhi, long toPhi, String address, String phone) {
		ObjectNode payload = objectMapper.createObjectNode();
		// A string in the capture, not a number
		payload.put("episodeId", episodeId);
		payload.put("hierarchyMappingTo", toPhi);
		payload.put("hierarchyMappingFrom", fromPhi);
		payload.put("newAddress", address);
		payload.put("mobileNo", phone);
		// The capture used the same number when there is no other
		payload.put("alternateMobileNo", phone);
		payload.put("reason", "Transfer to higher Center");
		payload.put("actionStatus", "NOACTION");
		payload.put("finalStatus", "NOACTION");
		payload.put("remarks", "");
		// T = transfer (move hierarchy); current = the current treating PHI
		payload.put("transferType", "T");
		payload.put("relationType", "current");
		return payload;
	}
}
