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
package com.iemr.admin.service.user;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.controller.rolemaster.RoleMaster_Controller;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.user.M_User;
import com.iemr.admin.data.user.M_UserServiceRoleMapping;
import com.iemr.admin.repository.user.IemrUserRepositoryImplCustom;
import com.iemr.admin.repository.user.M_UserMappingRepo;
import com.iemr.admin.service.provideronboard.EncryptUserPassword123;

@Service
public class IemrUserServiceImpl implements IemrUserService {
	
	@Autowired
	private EncryptUserPassword123 encryptUserPassword;
	
	private Logger logger = LoggerFactory.getLogger(IemrUserServiceImpl.class);
	
	@Autowired
	private IemrUserRepositoryImplCustom iemrUserRepositoryImplCustom;

	public void setIemrUserRepositoryImplCustom(IemrUserRepositoryImplCustom iemrUserRepositoryImplCustom) {
		this.iemrUserRepositoryImplCustom = iemrUserRepositoryImplCustom;
	}

	@Override
	public int createUser(ArrayList<Map<String, Object>> userDetails, String createdBy) {
		List<M_User> savedData = new ArrayList<M_User>();
		int userID;
		try {
			M_User m_user = new M_User();
			Set<M_User> requestSet = new HashSet<M_User>();
			//logger.info("helloooo");
			for (Map<String, Object> obj : userDetails) {
				Set<String> keySet = obj.keySet();
				if (keySet.contains("titleID"))
					m_user.setTitleID(Integer.parseInt(obj.get("titleID").toString()));
				if (keySet.contains("firstName"))
					m_user.setFirstName(obj.get("firstName").toString());
				if (keySet.contains("middleName"))
					m_user.setMiddleName(obj.get("middleName").toString());
				if (keySet.contains("lastName"))
					m_user.setLastName(obj.get("lastName").toString());
				if (keySet.contains("genderID"))
					m_user.setGenderID((Integer.parseInt(obj.get("genderID").toString())));
				if (keySet.contains("maritalStatusID")) {
					if (obj.get("maritalStatusID").toString().length() > 0) {
						m_user.setMaritalStatusID(Integer.parseInt(obj.get("maritalStatusID").toString()));
					}
				}
				if (keySet.contains("aadhaarNo"))
					m_user.setAadhaarNo(obj.get("aadharNo").toString());
				if (keySet.contains("panNo"))
					m_user.setPAN(obj.get("panNo").toString());
				if (keySet.contains("dob")) {
					if (obj.get("dob").toString().length() > 10) {
						OffsetDateTime odt = OffsetDateTime.parse(obj.get("dob").toString());
						OffsetDateTime odtTruncatedToWholeSecond = odt.truncatedTo(ChronoUnit.SECONDS);
						String output = odtTruncatedToWholeSecond.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
								.replace("T", " ");
						m_user.setDOB(Timestamp.valueOf(output));
					}
				}
				if (keySet.contains("doj")) {
					if (obj.get("doj").toString().length() > 10) {
						OffsetDateTime odt = OffsetDateTime.parse(obj.get("doj").toString());
						OffsetDateTime odtTruncatedToWholeSecond = odt.truncatedTo(ChronoUnit.SECONDS);
						String output = odtTruncatedToWholeSecond.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
								.replace("T", " ");
						m_user.setDOJ(Timestamp.valueOf(output));
					}
				}
				if (keySet.contains("qualificationID"))
					if (obj.get("qualificationID").toString().length() > 0) {
						m_user.setQualificationID(Integer.parseInt(obj.get("qualificationID").toString()));
					}
				if (keySet.contains("userName"))
					m_user.setUserName(obj.get("userName").toString());
				if (keySet.contains("password"))
					m_user.setPassword(obj.get("password").toString());
				if (keySet.contains("emailID"))
					m_user.setEmailID(obj.get("emailID").toString());
				/*
				 * if (keySet.contains("statusID"))
				 * m_user.setStatusID(Integer.parseInt(obj.get("statusID").
				 * toString()));
				 */
				if (keySet.contains("emrContactPersion"))
					m_user.setEmergencyContactPerson(obj.get("emrContactPersion").toString());
				if (keySet.contains("emrConctactNo"))
					m_user.setEmergencyContactNo(obj.get("emrConctactNo").toString());
				if (keySet.contains("isSupervisor"))
					m_user.setIsSupervisor((Boolean) obj.get("isSupervisor"));
				if (keySet.contains("deleted")) {
					if (obj.get("deleted").toString().length() > 0) {
						m_user.setDeleted((Boolean) obj.get("deleted"));
					}
				}
				if (createdBy == null)
					m_user.setCreatedBy("");
				else
					m_user.setCreatedBy(createdBy);
				if (keySet.contains("statusID"))
					m_user.setStatusID(Integer.parseInt(obj.get("statusID").toString()));

				//logger.info("hiiii");
			}

			requestSet.add(m_user);
			savedData = (List<M_User>) this.iemrUserRepositoryImplCustom.save(requestSet);
			
			for (M_User user : savedData) {
				encryptUserPassword.encryptUserCredentials(user);
			}

		} catch (Exception e) {
			throw e;
		}
		if (savedData.size() > 0) {
			userID = savedData.get(0).getUserID();
		} else {
			userID = 0;
		}
		return userID;

	}

	@Autowired
	private M_UserMappingRepo m_UserMappingRepo;

	public void setM_UserMappingRepo(M_UserMappingRepo m_UserMappingRepo) {
		this.m_UserMappingRepo = m_UserMappingRepo;
	}

	@Override
	public int  createUserServiceRoleMapping(List<Integer> proSerStatMapIdList, int userID, String createdBy) {
		int response;
		try {
			M_UserServiceRoleMapping usrmOBJ = null;
			Set<M_UserServiceRoleMapping> requestSet = new HashSet<M_UserServiceRoleMapping>();
			for (Integer pssmID : proSerStatMapIdList) {
				usrmOBJ = new M_UserServiceRoleMapping();
				usrmOBJ.setUserID(userID);
				usrmOBJ.setRoleID(11);
				usrmOBJ.setProviderServiceMapID(pssmID);
				usrmOBJ.setCreatedBy(createdBy);
				requestSet.add(usrmOBJ);
			}
			ArrayList<M_UserServiceRoleMapping> resultList = (ArrayList<M_UserServiceRoleMapping>) this.m_UserMappingRepo
					.save(requestSet);
			if (resultList.size() > 0) {
				response = 1;
			} else {
				response = 0;
			}
		} catch (Exception e) {
			throw e;
		}
		return response;
	}

}

