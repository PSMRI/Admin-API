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
package com.iemr.admin.repo.nikshayjoblog;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.nikshay.NikshayJobHistoryLog;
import com.iemr.admin.service.nikshayjoblog.NikshayFailureReason;
import com.iemr.admin.service.nikshayjoblog.NikshayLogStatus;
import com.iemr.admin.service.nikshayjoblog.NikshayPhase;

@Repository
public interface NikshayJobHistoryLogRepo extends CrudRepository<NikshayJobHistoryLog, Long> {

	// Earlier attempts of the same beneficiary, phase and reference; the next attempt is this + 1.
	// benId / referenceId can be null, so they're compared through COALESCE.
	@Query(" SELECT COUNT(l) FROM NikshayJobHistoryLog l WHERE l.phase = :phase "
			+ " AND COALESCE(l.benId, -1) = COALESCE(:benId, -1) "
			+ " AND COALESCE(l.referenceId, '') = COALESCE(:referenceId, '') ")
	long countAttempts(@Param("phase") NikshayPhase phase, @Param("benId") Long benId,
			@Param("referenceId") String referenceId);

	// Latest entry of each (beneficiary, phase, reference), kept only when it failed: a later
	// success drops the item from the list. Every filter is optional (null = no filter);
	// toDate is exclusive.
	String LATEST_FAILED_WHERE = " WHERE l.status = :failed "
			+ " AND l.id = (SELECT MAX(l2.id) FROM NikshayJobHistoryLog l2 WHERE l2.phase = l.phase "
			+ "   AND COALESCE(l2.benId, -1) = COALESCE(l.benId, -1) "
			+ "   AND COALESCE(l2.referenceId, '') = COALESCE(l.referenceId, '')) "
			+ " AND (:phase IS NULL OR l.phase = :phase) "
			+ " AND (:reason IS NULL OR l.reason = :reason) "
			+ " AND (:benId IS NULL OR l.benId = :benId) "
			+ " AND (:fromDate IS NULL OR l.createdAt >= :fromDate) "
			+ " AND (:toDate IS NULL OR l.createdAt < :toDate) ";

	@Query(value = " SELECT l FROM NikshayJobHistoryLog l " + LATEST_FAILED_WHERE + " ORDER BY l.id DESC ",
			countQuery = " SELECT COUNT(l) FROM NikshayJobHistoryLog l " + LATEST_FAILED_WHERE)
	Page<NikshayJobHistoryLog> findLatestFailed(@Param("failed") NikshayLogStatus failed,
			@Param("phase") NikshayPhase phase, @Param("reason") NikshayFailureReason reason,
			@Param("benId") Long benId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate,
			Pageable pageable);

	List<NikshayJobHistoryLog> findByBenIdOrderByIdDesc(Long benId);

	// Which of the given references already have a successful upload in this phase
	@Query(" SELECT DISTINCT l.referenceId FROM NikshayJobHistoryLog l WHERE l.phase = :phase "
			+ " AND l.status = :success AND l.referenceId IN :referenceIds ")
	List<String> findReferenceIdsWithStatus(@Param("phase") NikshayPhase phase,
			@Param("success") NikshayLogStatus success, @Param("referenceIds") List<String> referenceIds);
}
