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
package com.iemr.admin.service.employeemaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.employeemaster.EmployeeSignature;
import com.iemr.admin.repo.employeemaster.EmployeeSignatureRepo;
import org.json.JSONObject;

@Service
public class EmployeeSignatureServiceImpl implements EmployeeSignatureService {

	@Autowired
	EmployeeSignatureRepo employeeSignatureRepo;

	@Override
	public Long uploadSignature(EmployeeSignature empSign) {
		// TODO Auto-generated method stub
		EmployeeSignature usrsignID = employeeSignatureRepo.findOneByUserID(empSign.getUserID());

//		if(usrsignID==null) {
//			employeeSignatureRepo.save(empSign);
//			usrsignID=empSign.getUserSignatureID();
//		}else {
//			employeeSignatureRepo.updateFile(usrsignID,empSign.getFileName(),empSign.getFileType(),empSign.getSignature(),createdBy);
//		}
		if (usrsignID != null) {
			usrsignID.setSignature(empSign.getSignature());
			usrsignID.setFileName(empSign.getFileName());
			usrsignID.setFileType(empSign.getFileType());
			usrsignID.setModifiedBy(empSign.getCreatedBy());
		} else {
			usrsignID = empSign;
		}
		usrsignID = employeeSignatureRepo.save(usrsignID);
		return usrsignID.getUserSignatureID();
	}

	@Override
	public EmployeeSignature fetchSignature(Long userSignID) {
		// TODO Auto-generated method stub
		return employeeSignatureRepo.findOneByUserID(userSignID);
	}

	public Boolean existSignature(Long userID) {
		// TODO Auto-generated method stub
		return employeeSignatureRepo.countByUserIDAndSignatureNotNull(userID)>0;
	}

	@Override
	public EmployeeSignature updateUserSignatureStatus(String activateUser) {
		JSONObject obj = new JSONObject(activateUser);
		Long userID = obj.getLong("userID");
		// String role = obj.getString("role");
		boolean active = obj.getBoolean("active");
		EmployeeSignature signature = employeeSignatureRepo.findOneByUserID(userID);
		if (signature == null) {
			throw new IllegalArgumentException("No signature found for userID: " + userID);
		}
		signature.setDeleted(!active);
		return employeeSignatureRepo.save(signature);
	}
}
