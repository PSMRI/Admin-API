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
package com.iemr.admin.repo.nikshayupload;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshay.DiagnosticOrder;
import com.iemr.admin.data.nikshayupload.NaatResultDTO;
import com.iemr.admin.data.nikshayupload.XrayResultDTO;

@Repository
public interface DiagnosticOrderRepo extends CrudRepository<DiagnosticOrder, Long> {

	// Completed MTB orders with their result, joined on externalOrderId (see DiagnosticResult).
	// Ni-kshay ID and current village/state come from db_identity: BeneficiaryID -> BenRegId
	// (m_beneficiaryregidmapping) -> details/address (i_beneficiarymapping). LEFT JOINs, so a
	// beneficiary whose identity hasn't synced still returns its result, with those fields null.
	// nv gives the Ni-kshay name for the village ID, so the service can tell whether the ID
	// really is a Ni-kshay village ID (see NikshayUploadService.villageProblem). The Ni-kshay
	// state code is taken from the village's own hierarchy, not from CurrStateId: that is an
	// AMRIT m_state ID, and the same number is a different state in m_nikshay_state.
	@Query(" SELECT new com.iemr.admin.data.nikshayupload.NaatResultDTO(o.beneficiaryId, rm.benRegId, d.nikshayId, "
			+ " a.currVillageId, a.currVillage, nv.villageName, a.currStateId, ns.nikshayCode, "
			+ " nf.facilityName, nf.nikshayCode, o.visitCode, o.vanID, o.externalOrderId, r.resultSummary, "
			+ " r.tbPresence, o.createdDate, r.createdDate) "
			+ " FROM DiagnosticOrder o "
			+ " JOIN DiagnosticResult r ON r.externalOrderId = o.externalOrderId AND r.deleted = false "
			+ " LEFT JOIN NikshayBenRegIdMapping rm ON rm.beneficiaryID = o.beneficiaryId AND rm.deleted = false "
			+ " LEFT JOIN NikshayBenMapping m ON m.benRegId = rm.benRegId AND m.deleted = false "
			+ " LEFT JOIN NikshayBenDetails d ON d.beneficiaryDetailsId = m.benDetailsId AND d.deleted = false "
			+ " LEFT JOIN NikshayBenAddress a ON a.benAddressID = m.benAddressId "
			+ " LEFT JOIN NikshayVillage nv ON nv.nikshayVillageID = a.currVillageId AND nv.deleted = false "
			+ " LEFT JOIN NikshayFacility nf ON nf.nikshayFacilityID = nv.nikshayFacilityID AND nf.deleted = false "
			+ " LEFT JOIN NikshayTU nt ON nt.nikshayTUID = nf.nikshayTUID AND nt.deleted = false "
			+ " LEFT JOIN NikshayDistrict nd ON nd.nikshayDistrictID = nt.nikshayDistrictID AND nd.deleted = false "
			+ " LEFT JOIN NikshayState ns ON ns.nikshayStateID = nd.nikshayStateID AND ns.deleted = false "
			+ " WHERE o.orderType = 'MTB' AND o.status = 'COMPLETED' AND o.deleted = false "
			+ " AND o.beneficiaryId IN :beneficiaryIds "
			+ " ORDER BY o.beneficiaryId, o.createdDate ")
	List<NaatResultDTO> findMtbResults(@Param("beneficiaryIds") List<Long> beneficiaryIds);

	// Completed TrueNat Rif resistance (MDR_RIF) orders, joined the same way as findMtbResults.
	@Query(" SELECT new com.iemr.admin.data.nikshayupload.NaatResultDTO(o.beneficiaryId, rm.benRegId, d.nikshayId, "
			+ " a.currVillageId, a.currVillage, nv.villageName, a.currStateId, ns.nikshayCode, "
			+ " nf.facilityName, nf.nikshayCode, o.visitCode, o.vanID, o.externalOrderId, r.resultSummary, "
			+ " r.tbPresence, o.createdDate, r.createdDate) "
			+ " FROM DiagnosticOrder o "
			+ " JOIN DiagnosticResult r ON r.externalOrderId = o.externalOrderId AND r.deleted = false "
			+ " LEFT JOIN NikshayBenRegIdMapping rm ON rm.beneficiaryID = o.beneficiaryId AND rm.deleted = false "
			+ " LEFT JOIN NikshayBenMapping m ON m.benRegId = rm.benRegId AND m.deleted = false "
			+ " LEFT JOIN NikshayBenDetails d ON d.beneficiaryDetailsId = m.benDetailsId AND d.deleted = false "
			+ " LEFT JOIN NikshayBenAddress a ON a.benAddressID = m.benAddressId "
			+ " LEFT JOIN NikshayVillage nv ON nv.nikshayVillageID = a.currVillageId AND nv.deleted = false "
			+ " LEFT JOIN NikshayFacility nf ON nf.nikshayFacilityID = nv.nikshayFacilityID AND nf.deleted = false "
			+ " LEFT JOIN NikshayTU nt ON nt.nikshayTUID = nf.nikshayTUID AND nt.deleted = false "
			+ " LEFT JOIN NikshayDistrict nd ON nd.nikshayDistrictID = nt.nikshayDistrictID AND nd.deleted = false "
			+ " LEFT JOIN NikshayState ns ON ns.nikshayStateID = nd.nikshayStateID AND ns.deleted = false "
			+ " WHERE o.orderType = 'MDR_RIF' AND o.status = 'COMPLETED' AND o.deleted = false "
			+ " AND o.beneficiaryId IN :beneficiaryIds "
			+ " ORDER BY o.beneficiaryId, o.createdDate ")
	List<NaatResultDTO> findRifResults(@Param("beneficiaryIds") List<Long> beneficiaryIds);

	// Completed chest X-ray orders, joined the same way as findMtbResults. An invalid X-ray
	// ("AI Invalid Result") is CLOSED by FLW-API, never COMPLETED, so it's excluded here.
	@Query(" SELECT new com.iemr.admin.data.nikshayupload.XrayResultDTO(o.beneficiaryId, rm.benRegId, d.nikshayId, "
			+ " a.currVillageId, a.currVillage, nv.villageName, a.currStateId, ns.nikshayCode, "
			+ " nf.facilityName, nf.nikshayCode, o.visitCode, o.vanID, o.externalOrderId, r.resultSummary, "
			+ " r.tbPresence, o.createdDate, r.createdDate) "
			+ " FROM DiagnosticOrder o "
			+ " JOIN DiagnosticResult r ON r.externalOrderId = o.externalOrderId AND r.deleted = false "
			+ " LEFT JOIN NikshayBenRegIdMapping rm ON rm.beneficiaryID = o.beneficiaryId AND rm.deleted = false "
			+ " LEFT JOIN NikshayBenMapping m ON m.benRegId = rm.benRegId AND m.deleted = false "
			+ " LEFT JOIN NikshayBenDetails d ON d.beneficiaryDetailsId = m.benDetailsId AND d.deleted = false "
			+ " LEFT JOIN NikshayBenAddress a ON a.benAddressID = m.benAddressId "
			+ " LEFT JOIN NikshayVillage nv ON nv.nikshayVillageID = a.currVillageId AND nv.deleted = false "
			+ " LEFT JOIN NikshayFacility nf ON nf.nikshayFacilityID = nv.nikshayFacilityID AND nf.deleted = false "
			+ " LEFT JOIN NikshayTU nt ON nt.nikshayTUID = nf.nikshayTUID AND nt.deleted = false "
			+ " LEFT JOIN NikshayDistrict nd ON nd.nikshayDistrictID = nt.nikshayDistrictID AND nd.deleted = false "
			+ " LEFT JOIN NikshayState ns ON ns.nikshayStateID = nd.nikshayStateID AND ns.deleted = false "
			+ " WHERE o.orderType = 'XRAY_CHEST' AND o.status = 'COMPLETED' AND o.deleted = false "
			+ " AND o.beneficiaryId IN :beneficiaryIds "
			+ " ORDER BY o.beneficiaryId, o.createdDate ")
	List<XrayResultDTO> findXrayResults(@Param("beneficiaryIds") List<Long> beneficiaryIds);
}
