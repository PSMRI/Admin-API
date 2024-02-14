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
package com.iemr.admin.repo.labmodule;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.admin.data.labmodule.ProcedureComponentMapping;

/***
 * 
 * @author Rajeev Tripathi
 *
 */
@Repository
public interface ProcedureComponentMappingRepo extends CrudRepository<ProcedureComponentMapping, Integer> {
	@Query("SELECT distinct pcm.procedureID, pcm.testComponentID, pm.procedureName, pm.procedureDesc, cm.testComponentName, "
			+ " pcm.createdBy, cm.testComponentDesc "
			+ " FROM ProcedureComponentMapping pcm INNER JOIN pcm.compMaster cm INNER JOIN pcm.procMaster pm"
			+ " WHERE pcm.providerServiceMapID = :psmID AND cm.providerServiceMapID = :psmID AND "
			+ " pm.providerServiceMapID = :psmID AND pcm.deleted = false AND pm.deleted = false " + "ORDER BY pcm.procedureID ")
	public ArrayList<Object[]> getProcedureComponentMappingList(@Param("psmID") Integer psmID);

	@Query("SELECT distinct pcm.procedureID, pcm.testComponentID, pm.procedureName, pm.procedureDesc, cm.testComponentName,cm.inputType,pm.procedureType,"
			+ " pcm.createdBy, cm.testComponentDesc, cm.lionicNum "
			+ " FROM ProcedureComponentMapping pcm INNER JOIN pcm.compMaster cm INNER JOIN pcm.procMaster pm"
			+ " WHERE pcm.procedureID = :pID AND pcm.deleted = false " + "ORDER BY pcm.procedureID ")
	public ArrayList<Object[]> getProcedureComponentMappingListForProcedureID(@Param("pID") Integer pID);

	@Transactional
	@Modifying
	@Query("UPDATE ProcedureComponentMapping set deleted = true, modifiedBy = :modifiedBy  WHERE procedureID = :pID AND deleted = false")
	public Integer softDeleteProcCompMapping(@Param("pID") Integer pID, @Param("modifiedBy") String modifiedBy);
}
