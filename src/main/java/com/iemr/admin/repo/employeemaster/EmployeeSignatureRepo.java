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
package com.iemr.admin.repo.employeemaster;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.employeemaster.EmployeeSignature;

@Repository
@RestResource(exported = false)
public interface EmployeeSignatureRepo extends CrudRepository<EmployeeSignature, Long> {

	EmployeeSignature findOneByUserID(Long userID);

	@Query("SELECT es.userSignatureID FROM EmployeeSignature es WHERE es.userID=:userid")
	Long findUserSignature(@Param("userid") Long userid);

	Long countByUserIDAndSignatureNotNull(Long userID);

//	@Transactional
//	@Modifying
//	@Query("update EmployeeSignature es set es.fileName=:fileName,es.fileType=:fileType "
//			+ " es.signature=:signature, es.modifiedBy=:modifiedBy where es.userSignatureID=:userSignatureID ")
//	void updateFile( @Param("userSignatureID")Long usrsignID, @Param("fileName") String fileName, @Param("fileType") String fileType,
//			@Param("signature") byte[] signature, @Param("modifiedBy") String modifiedBy);

}
