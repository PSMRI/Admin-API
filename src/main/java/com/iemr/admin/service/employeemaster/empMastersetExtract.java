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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iemr.admin.data.employeemaster.Showuserdetailsfromuserservicerolemapping;

public class empMastersetExtract implements ResultSetExtractor {
    
    Logger logger = LoggerFactory.getLogger(empMastersetExtract.class);
	
//	 @Override
//	    public Object extractData(ResultSet rs) throws SQLException {
//	        Person person = new Person();
//	        person.setFirstName(rs.getString(1));
//	        person.setLastName(rs.getString(2));
//	        return person;
//	    }

	@Override
	public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub
		logger.info("empMastersetExtract.extractData - start");
		Showuserdetailsfromuserservicerolemapping userdetail=new Showuserdetailsfromuserservicerolemapping();
//		userdetail.setAadhaarNo(rs.getString("AadhaarNo"));
		userdetail.setuSRMappingID(rs.getInt("USRMappingID"));
		userdetail.setProviderServiceMapID(rs.getInt("ProviderServiceMapID"));
		userdetail.setUserID(rs.getInt("UserID"));
		userdetail.setRoleID(rs.getInt("RoleID"));
		userdetail.setRoleName(rs.getString("RoleName"));
		userdetail.setTitleID(rs.getInt("TitleID"));
		userdetail.setTitleName(rs.getString("TitleName"));
		userdetail.setFirstName(rs.getString("FirstName"));
		userdetail.setMiddleName(rs.getString("MiddleName"));
		userdetail.setLastName(rs.getString("LastName"));
		userdetail.setGenderID(rs.getInt("GenderID"));
		userdetail.setGenderName(rs.getString("GenderName"));
		userdetail.setMaritalStatusID(rs.getInt("MaritalStatusID"));
		userdetail.setMaritalStatus(rs.getString("MaritalStatus"));
		userdetail.setAadhaarNo(rs.getString("AadhaarNo"));
		userdetail.setpAN(rs.getString("PAN"));
		userdetail.setdOB(rs.getDate("DOB"));
		userdetail.setdOB(rs.getDate("DOJ"));
		userdetail.setQualificationID(rs.getInt("QualificationID"));
		userdetail.setQualification(rs.getString("Qualification"));
		userdetail.setUserName(rs.getString("UserName"));
		userdetail.setPassword(rs.getString("Password"));
		userdetail.setAgentID(rs.getString("AgentID"));
		userdetail.setAgentPassword(rs.getString("AgentPassword"));
		userdetail.setEmailID(rs.getString("EmailID"));
		userdetail.setStatusID(rs.getInt("StatusID"));
		userdetail.setUserStatus(rs.getString("UserStatus"));
		userdetail.setEmergencyContactPerson(rs.getString("EmergencyContactPerson"));
		userdetail.setEmergencyContactNo(rs.getString("EmergencyContactNo"));
		userdetail.setIsSupervisor(rs.getBoolean("IsSupervisor"));
		userdetail.setUserDeleted(rs.getBoolean("UserDeleted"));
		userdetail.setDemographicID(rs.getInt("DemographicID"));
		userdetail.setFathersName(rs.getString("FathersName"));
		userdetail.setMothersName(rs.getString("MothersName"));
		userdetail.setCommunityID(rs.getInt("CommunityID"));
		userdetail.setCommunityType(rs.getString("CommunityType"));
		userdetail.setReligionID(rs.getInt("ReligionID"));
		userdetail.setReligionType(rs.getString("ReligionType"));
		userdetail.setUserAddressLine1(rs.getString("UserAddressLine1"));
		userdetail.setUserAddressLine2(rs.getString("UserAddressLine2"));
		/*userdetail.setUserAddressLine3(rs.getString("UserAddressLine3"));
		userdetail.setUserAddressLine4(rs.getString("UserAddressLine4"));
		userdetail.setUserAddressLine5(rs.getString("UserAddressLine5"));*/
		userdetail.setUserPremanentAddress(rs.getString("UserPremanentAddress"));
		userdetail.setUserCityID(rs.getInt("UserCityID"));
		userdetail.setUserCity(rs.getString("UserCity"));
		userdetail.setUserStateID(rs.getInt("UserStateID"));
		userdetail.setUserState(rs.getString("UserState"));
		userdetail.setUserCountryID(rs.getInt("UserCountryID"));
		userdetail.setUserCountry(rs.getString("UserCountry"));
		userdetail.setPinCode(rs.getString("PinCode"));
		userdetail.setIsPresent(rs.getBoolean("IsPresent"));
		userdetail.setIsPermanent(rs.getBoolean("IsPermanent"));
		userdetail.setServiceProviderID(rs.getInt("ServiceProviderID"));
		userdetail.setServiceProviderName(rs.getString("ServiceProviderName"));
		userdetail.setServiceID(rs.getInt("ServiceID"));
		userdetail.setServiceName(rs.getString("ServiceName"));
		userdetail.setpSMStateID(rs.getInt("PSMStateID"));
		userdetail.setpSMStateName(rs.getString("PSMStateName"));
		userdetail.setWorkingLocationID(rs.getInt("WorkingLocationID"));
		userdetail.setWorkingLocationName(rs.getString("WorkingLocationName"));
		userdetail.setWorkingDistrictID(rs.getInt("WorkingDistrictID"));
		userdetail.setWorkingDistrictName(rs.getString("WorkingDistrictName"));
		userdetail.setWorkingAddress(rs.getString("WorkingAddress"));
 		userdetail.setuSRMDeleted(rs.getBoolean("USRMDeleted"));
		
		logger.info("empMastersetExtract.extractData - finish : userdetail " + userdetail);
		return userdetail;
	}

}
