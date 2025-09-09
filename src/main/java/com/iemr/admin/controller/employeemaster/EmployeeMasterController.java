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
package com.iemr.admin.controller.employeemaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.admin.data.employeemaster.M_Community;
import com.iemr.admin.data.employeemaster.M_Designation;
import com.iemr.admin.data.employeemaster.M_Gender;
import com.iemr.admin.data.employeemaster.M_ProviderServiceMap1;
import com.iemr.admin.data.employeemaster.M_Religion;
import com.iemr.admin.data.employeemaster.M_Role;
import com.iemr.admin.data.employeemaster.M_Title;
import com.iemr.admin.data.employeemaster.M_User1;
import com.iemr.admin.data.employeemaster.M_UserDemographics;
import com.iemr.admin.data.employeemaster.M_UserLangMapping;
import com.iemr.admin.data.employeemaster.M_UserServiceRoleMapping2;
import com.iemr.admin.data.employeemaster.M_Userqualification;
import com.iemr.admin.data.employeemaster.Showofficedetails1;
import com.iemr.admin.data.employeemaster.Showuserdetailsfromuserservicerolemapping;
import com.iemr.admin.data.employeemaster.V_Showuser;
import com.iemr.admin.data.employeemaster.V_Userservicerolemapping;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.UserRole;
import com.iemr.admin.service.employeemaster.EmployeeMasterInter;
import com.iemr.admin.service.employeemaster.M_DesignationInter;
import com.iemr.admin.service.employeemaster.USRAgentMappingService;
import com.iemr.admin.to.employeemaster.Previleges1097_3;
import com.iemr.admin.to.employeemaster.Previleges2;
import com.iemr.admin.to.employeemaster.Previlegs1;
import com.iemr.admin.to.employeemaster.Priveleges1097_1;
import com.iemr.admin.to.employeemaster.Priveleges1097_2;
import com.iemr.admin.to.employeemaster.UserTO;
import com.iemr.admin.to.employeemaster.UserTO1;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;


@RestController
public class EmployeeMasterController {

	@Autowired
	private M_DesignationInter m_DesignationInter;

	ClientConfig config = new ClientConfig();
	Client client = ClientBuilder.newClient(config);

	private InputMapper inputMapper = new InputMapper();

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private EmployeeMasterInter employeeMasterInter;

	@Autowired
	public void setServiceProvider_ServiceImpl(EmployeeMasterInter blockingInter) {
		this.employeeMasterInter = blockingInter;
	}

	private USRAgentMappingService usrAgentMappingService;

	@Autowired
	public void setUsrAgentMappingService(USRAgentMappingService usrAgentMappingService) {
		this.usrAgentMappingService = usrAgentMappingService;
	}

	@Operation(summary = "Get all role")
	@RequestMapping(value = "/m/getAllRole", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllRole(@RequestBody String getAllRole) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getAllRole);

		try {
			/**
			 * creating the InputMapper class Object and passing the coming request
			 */

			M_Role m_role = InputMapper.gson().fromJson(getAllRole, M_Role.class);

			/**
			 * calling the getAllRole Method of employeeMasterInteface and storing the
			 * response in array list type variable
			 */
			ArrayList<M_Role> allroledata = employeeMasterInter.getAllRole();

			/**
			 * creating the response with passing variable inside setResponse method of
			 * Output Response class
			 */
			response.setResponse(allroledata.toString());

			/**
			 * catching exception if any error will come it will throw the exception.
			 */
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Getting All Role failed with exception " + e.getMessage(), e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Search employee")
	@RequestMapping(value = "/m/SearchEmployee", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee(@RequestBody String searchEmployee) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchEmployee);
		try {

			/**
			 * creating the InputMapper object and calling json method and passing the
			 * coming request in side method
			 */

			M_UserServiceRoleMapping2 m_UserServiceRoleMapping = InputMapper.gson().fromJson(searchEmployee,
					M_UserServiceRoleMapping2.class);

			/**
			 * calling getEmployee Details Method with no argument storing that value inside
			 * array list variable
			 */
			ArrayList<M_UserServiceRoleMapping2> data = employeeMasterInter.getEmployeeDetails();

			/**
			 * creating the response with setResonse method of OutputResponse class
			 */
			response.setResponse(data.toString());

			/**
			 * handling the exception if any error will come it will show the proper log
			 * trace.
			 */
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("search Employee failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 1")
	@RequestMapping(value = "/m/SearchEmployee1", headers = "Authorization", method = RequestMethod.POST, produces = "application/json")
	public String searchEmployee1(@RequestBody String searchEmployee1) {

		logger.debug("request" + searchEmployee1);
		OutputResponse response = new OutputResponse();

		try {
			/**
			 * creating the object of InputMapper Class with reference of
			 * M_UserServiceRoleMapping2 and calling the from json method and passing the
			 * coming request.
			 */
			M_UserServiceRoleMapping2 m_UserServiceRoleMapping = InputMapper.gson().fromJson(searchEmployee1,
					M_UserServiceRoleMapping2.class);
			/**
			 * calling get employeeDetails1 method with no argument and storing the value in
			 * array list variable
			 */
			ArrayList<M_UserServiceRoleMapping2> data = employeeMasterInter.getEmployeeDetails1();

			/**
			 * creating the request with setResponse method of OutputMapper class.
			 */
			response.setResponse(data.toString());

			/**
			 * handling the exception if any error will come it will show the proper log
			 * trace.
			 */
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Search Employee1 failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 2")
	@RequestMapping(value = "/m/SearchEmployee2", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee2(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchEmployee1);

		try {

			/**
			 * creating the object of InputMapper Class with reference of
			 * M_UserServiceRoleMapping2 and calling the from json method and passing the
			 * coming request.
			 */
			Showuserdetailsfromuserservicerolemapping userDetail = InputMapper.gson().fromJson(searchEmployee1,
					Showuserdetailsfromuserservicerolemapping.class);
			/**
			 * calling get employeeDetails2 method with two argument and storing the value
			 * in array list variable
			 */

			ArrayList<Showuserdetailsfromuserservicerolemapping> data = employeeMasterInter
					.getEmployeeDetails2(userDetail.getServiceProviderID(), userDetail.getpSMStateID());

			/**
			 * creating the request with calling setResponse method of OutputMapper class.
			 * and passing array list type value.
			 */
			response.setResponse(data.toString());

			/**
			 * handling the exception if any error will come it will show the proper log
			 * trace.
			 */
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Unexpected error:", e);
			logger.error("Search Employee2 failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 3")
	@RequestMapping(value = "/m/SearchEmployee3", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee3(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchEmployee1);

		try {
			/**
			 * Creating the inputMapper object with the referance of
			 * showuserdetailsfromuserserviceRoleMapping and passing the coming request.
			 */

			Showuserdetailsfromuserservicerolemapping userDetail = InputMapper.gson().fromJson(searchEmployee1,
					Showuserdetailsfromuserservicerolemapping.class);

			/**
			 * Calling the getEmployee3 method with 2 Argument and storing the value in
			 * array list variable
			 */
			ArrayList<Showuserdetailsfromuserservicerolemapping> data = employeeMasterInter
					.getEmployeeDetails3(userDetail.getServiceProviderID(), userDetail.getRoleID());

			/**
			 * Creating the response with setResponse Method of OutputMapper Class
			 */
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Search Employee3 failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 4")
	@RequestMapping(value = "/m/SearchEmployee4", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee4(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchEmployee1);

		try {

			/**
			 * Creating the Object of InputMapper class and with referance of VShowUser And
			 * passing the coming request.
			 */
			V_Showuser userDetail = InputMapper.gson().fromJson(searchEmployee1, V_Showuser.class);

			/**
			 * Calling the getEmployeeDetails4 Method with 1 argument and storing that value
			 * into arrayList variable
			 */

			ArrayList<V_Showuser> data = employeeMasterInter.getEmployeeDetails4(userDetail.getServiceProviderID());

			/**
			 * Creating the response with the setResponse method of OutputMapper Class
			 */

			response.setResponse(data.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Search Employee4 failed with exception " + e.getMessage(), e);
			response.setError(e);
		}

		return response.toString();

	}

	@Operation(summary = "Search employee 5")
	@RequestMapping(value = "/m/SearchEmployee5", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee5(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchEmployee1);

		try {

			V_Showuser userDetail = InputMapper.gson().fromJson(searchEmployee1, V_Showuser.class);

			ArrayList<V_Showuser> data = employeeMasterInter.getEmployeeDetails5();
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Search Employee5 failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 6")
	@RequestMapping(value = "/m/SearchEmployee6", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee6(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchEmployee1);

		try {

			Showuserdetailsfromuserservicerolemapping userDetail = InputMapper.gson().fromJson(searchEmployee1,
					Showuserdetailsfromuserservicerolemapping.class);

			ArrayList<Showuserdetailsfromuserservicerolemapping> data = employeeMasterInter
					.getEmployeeDetails6(userDetail.getServiceProviderID(), userDetail.getUserID());
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Search Employee6 failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 7")
	@RequestMapping(value = "/m/SearchEmployee7", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee7(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchEmployee1);

		try {

			Showuserdetailsfromuserservicerolemapping userDetail = InputMapper.gson().fromJson(searchEmployee1,
					Showuserdetailsfromuserservicerolemapping.class);

			ArrayList<Showuserdetailsfromuserservicerolemapping> data = employeeMasterInter.getEmployeeDetails7(
					userDetail.getServiceProviderID(), userDetail.getpSMStateID(), userDetail.getWorkingDistrictID());
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 8")
	@RequestMapping(value = "/m/SearchEmployee8", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee8(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();

		try {

			Showuserdetailsfromuserservicerolemapping userDetail = InputMapper.gson().fromJson(searchEmployee1,
					Showuserdetailsfromuserservicerolemapping.class);

			ArrayList<Showuserdetailsfromuserservicerolemapping> data = employeeMasterInter.getEmployeeDetails8(
					userDetail.getServiceProviderID(), userDetail.getpSMStateID(), userDetail.getWorkingDistrictID(),
					userDetail.getWorkingLocationID());
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee 9")
	@RequestMapping(value = "/m/SearchEmployee9", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee9(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();

		try {

			Showuserdetailsfromuserservicerolemapping userDetail = InputMapper.gson().fromJson(searchEmployee1,
					Showuserdetailsfromuserservicerolemapping.class);

			ArrayList<Showuserdetailsfromuserservicerolemapping> data = employeeMasterInter.getEmployeeDetails9(
					userDetail.getServiceProviderID(), userDetail.getpSMStateID(), userDetail.getRoleID());
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Search employee filter")
	@RequestMapping(value = "/m/SearchEmployeeFilter", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchEmployee10(@RequestBody String searchEmployee1) {
		OutputResponse response = new OutputResponse();

		try {

			Showuserdetailsfromuserservicerolemapping userDetail = InputMapper.gson().fromJson(searchEmployee1,
					Showuserdetailsfromuserservicerolemapping.class);

			ArrayList<Showuserdetailsfromuserservicerolemapping> data = employeeMasterInter.getEmployeeDetails11(
					userDetail.getServiceProviderID(), userDetail.getpSMStateID(), userDetail.getServiceID(),
					userDetail.getRoleID(), userDetail.getUserName(), userDetail.getUserID());
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Get agent id")
	@RequestMapping(value = "/getAgentID", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAgentID(@RequestBody String addEmployee) {
		OutputResponse response = new OutputResponse();

		try {

		} catch (Exception e) {
			logger.error("get agent id failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Add employee")
	@RequestMapping(value = "/m/AddEmployee", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String addEmployee(@RequestBody String addEmployee, HttpServletRequest request) {
		logger.info("employeemaster/EmployeeMasterController.addEmployee - start");
		OutputResponse response = new OutputResponse();

		try {

			UserTO employeeMaster = InputMapper.gson().fromJson(addEmployee, UserTO.class);

			Previlegs1 pre = InputMapper.gson().fromJson(addEmployee, Previlegs1.class);
			M_User1 m_UserList = new M_User1();
			m_UserList.setFirstName(employeeMaster.getFirstName());
			m_UserList.setTitleID(employeeMaster.getTitleID());
			m_UserList.setMiddleName(employeeMaster.getMiddleName());
			m_UserList.setLastName(employeeMaster.getLastName());
			m_UserList.setGenderID(employeeMaster.getGenderID());
			m_UserList.setMaritalStatusID(employeeMaster.getMaritalStatusID());
			m_UserList.setAadhaarNo(employeeMaster.getAadhaarNo());
			m_UserList.setpAN(employeeMaster.getpAN());
			m_UserList.setdOB(employeeMaster.getdOB());
			m_UserList.setdOJ(employeeMaster.getdOJ());
			m_UserList.setQualificationID(employeeMaster.getQualificationID());
			m_UserList.setUserName(employeeMaster.getUserName());
			m_UserList.setPassword(employeeMaster.getPassword());
			m_UserList.setAgentID(employeeMaster.getAgentID());
			m_UserList.setAgentPassword(employeeMaster.getAgentPassword());
			m_UserList.setEmailID(employeeMaster.getEmailID());
			m_UserList.setStatusID(employeeMaster.getStatusID());
			m_UserList.setEmergencyContactPerson(employeeMaster.getEmergencyContactPerson());
			m_UserList.setEmergencyContactNo(employeeMaster.getEmergencyContactNo());
			m_UserList.setIsSupervisor(employeeMaster.getIsSupervisor());
			m_UserList.setCreatedBy(employeeMaster.getCreatedBy());
			m_UserList.setModifiedBy(employeeMaster.getModifiedBy());
			m_UserList.setDesignationID(employeeMaster.getDesignationID());

			Integer dataList = employeeMasterInter.saveEmployee(m_UserList);
			M_UserDemographics ud = new M_UserDemographics();
			ud.setUserID(dataList);
			ud.setFathersName(employeeMaster.getFathersName());
			ud.setMothersName(employeeMaster.getMothersName());
			ud.setCommunityID(employeeMaster.getCommunityID());
			ud.setReligionID(employeeMaster.getReligionID());
			ud.setAddressLine1(employeeMaster.getAddressLine1());
			ud.setAddressLine2(employeeMaster.getAddressLine2());
			ud.setCityID(employeeMaster.getCityID());
			ud.setStateID(employeeMaster.getStateID());
			ud.setCountryID(employeeMaster.getCountryID());
			ud.setPinCode(employeeMaster.getPinCode());
			ud.setIsPresent(employeeMaster.getIsPresent());
			ud.setIsPermanent(employeeMaster.getIsPermanent());
			ud.setCreatedBy(employeeMaster.getCreatedBy());
			Integer data2 = employeeMasterInter.saveDemography(ud);

			M_UserLangMapping resDataMap = null;
			List<M_UserLangMapping> resList = new ArrayList<M_UserLangMapping>();

			Integer[] languageID = employeeMaster.getLanguageID();
			Integer[] weightage = employeeMaster.getWeightage();
			Boolean[] canread = employeeMaster.getCanRead();
			Boolean[] canwrite = employeeMaster.getCanWrite();
			Boolean[] canSpeek = employeeMaster.getCanSpeak();

			for (int i = 0; i < languageID.length; i++) {

				resDataMap = new M_UserLangMapping();
				resDataMap.setLanguageID(languageID[i]);
				resDataMap.setWeightage(weightage[i]);
				resDataMap.setCanRead(canread[i]);
				resDataMap.setCanWrite(canwrite[i]);
				resDataMap.setCanSpeak(canSpeek[i]);
				resDataMap.setUserID(dataList);
				resDataMap.setCreatedBy(employeeMaster.getCreatedBy());
				resList.add(resDataMap);
			}

			ArrayList<M_UserLangMapping> data3 = employeeMasterInter.mapLanguage(resList);

			M_UserServiceRoleMapping2 resDataMap1 = null;
			List<M_UserServiceRoleMapping2> resList1 = new ArrayList<M_UserServiceRoleMapping2>();

			Previleges2[] predata1 = pre.getPrevileges();
			for (Previleges2 previl : predata1) {
				{

					resDataMap1 = new M_UserServiceRoleMapping2();
					resDataMap1.setUserID(dataList);
					resDataMap1.setProviderServiceMapID(previl.getProviderServiceMapID());
					resDataMap1.setWorkingLocationID(previl.getWorkingLocationID());
					resDataMap1.setCreatedBy(employeeMaster.getCreatedBy());
					resList1.add(resDataMap1);
				}
			}
			ArrayList<M_UserServiceRoleMapping2> data4 = employeeMasterInter.mapRole(resList1,
					request.getHeader("Authorization"));

			response.setResponse(data4.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);
			logger.info("EmployeeMasterController.addEmployee - finish - response: " + response.toString());
		}

		logger.info(
				"employeemaster/EmployeeMasterController.addEmployee - finish - happy path --> " + response.toString());
		return response.toString();

	}

	@Operation(summary = "Edit employee")
	@RequestMapping(value = "/m/editEmployee", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editEmployee(@RequestBody String editEmployee) {
		OutputResponse response = new OutputResponse();

		try {

			M_UserDemographics employeeMaster = InputMapper.gson().fromJson(editEmployee, M_UserDemographics.class);

			M_UserLangMapping empMaster = InputMapper.gson().fromJson(editEmployee, M_UserLangMapping.class);
			M_UserServiceRoleMapping2 empMaster1 = InputMapper.gson().fromJson(editEmployee,
					M_UserServiceRoleMapping2.class);

			M_User1 employeeMaster2 = InputMapper.gson().fromJson(editEmployee, M_User1.class);

			M_User1 editdata = employeeMasterInter.editEmployee(employeeMaster2.getUserID());
			editdata.setFirstName(employeeMaster2.getFirstName());
			editdata.setMiddleName(employeeMaster2.getMiddleName());
			editdata.setLastName(employeeMaster2.getLastName());
			editdata.setGenderID(employeeMaster2.getGenderID());
			editdata.setMaritalStatusID(employeeMaster2.getMaritalStatusID());
			editdata.setAadhaarNo(employeeMaster2.getAadhaarNo());
			editdata.setpAN(employeeMaster2.getpAN());
			editdata.setdOB(employeeMaster2.getdOB());
			editdata.setdOJ(employeeMaster2.getdOJ());
			editdata.setQualificationID(employeeMaster2.getQualificationID());
			editdata.setUserName(employeeMaster2.getUserName());
			editdata.setPassword(employeeMaster2.getPassword());
			editdata.setAgentID(employeeMaster2.getAgentID());
			editdata.setAgentPassword(employeeMaster2.getAgentPassword());
			editdata.setEmailID(employeeMaster2.getEmailID());
			editdata.setStatusID(employeeMaster2.getStatusID());
			editdata.setEmergencyContactPerson(employeeMaster2.getEmergencyContactPerson());
			editdata.setEmergencyContactNo(employeeMaster2.getEmergencyContactNo());
			editdata.setIsSupervisor(employeeMaster2.getIsSupervisor());
			editdata.setDeleted(editdata.getDeleted());
			editdata.setModifiedBy(employeeMaster2.getModifiedBy());
			editdata.setLastModDate(employeeMaster2.getLastModDate());
			M_User1 editdata1 = employeeMasterInter.saveEditData(editdata);

			M_UserDemographics ud1 = employeeMasterInter.mdedit(editdata.getUserID());

			ud1.setFathersName(employeeMaster.getFathersName());
			ud1.setMothersName(employeeMaster.getMothersName());
			ud1.setCommunityID(employeeMaster.getCommunityID());
			ud1.setReligionID(employeeMaster.getReligionID());
			ud1.setAddressLine1(employeeMaster.getAddressLine1());
			ud1.setAddressLine2(employeeMaster.getAddressLine2());
			ud1.setStateID(employeeMaster.getStateID());
			ud1.setCountryID(employeeMaster.getCountryID());
			ud1.setPinCode(employeeMaster.getPinCode());
			ud1.setIsPresent(employeeMaster.getIsPresent());
			ud1.setIsPermanent(employeeMaster.getIsPermanent());
			ud1.setDeleted(ud1.getDeleted());
			Integer data2 = employeeMasterInter.saveeditDemo(ud1);
			UserTO1 userto = InputMapper.gson().fromJson(editEmployee, UserTO1.class);

			response.setResponse(data2.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Delete employee")
	@RequestMapping(value = "/m/deleteEmployee", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteEmployee(@RequestBody String deleteEmployee) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteEmployee);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(deleteEmployee, M_User1.class);

			M_User1 deletedata = employeeMasterInter.editEmployee(employeeMaster.getUserID());

			deletedata.setDeleted(true);

			M_User1 deleted1 = employeeMasterInter.saveEditData(deletedata);
			response.setResponse(deleted1.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete Employee failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "All title")
	@RequestMapping(value = "/m/AllTitle", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllTitle(@RequestBody String getAllTitle) {
		OutputResponse response = new OutputResponse();

		try {

			M_Title m_UserServiceRoleMapping = InputMapper.gson().fromJson(getAllTitle, M_Title.class);

			ArrayList<M_Title> data = employeeMasterInter.getAllTitle();
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();

	}

	@Operation(summary = "Get all gender")
	@RequestMapping(value = "/m/AllGender", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllGender(@RequestBody String getAllGender) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getAllGender);
		try {

			M_Gender m_UserServiceRoleMapping = InputMapper.gson().fromJson(getAllGender, M_Gender.class);

			ArrayList<M_Gender> data = employeeMasterInter.getAllGender();
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("getting gender failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Get all location")
	@RequestMapping(value = "/m/location/getAlllocation22", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAlllocation(@RequestBody String getAlllocation) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug("request" + getAlllocation);
		try {

			M_ProviderServiceMap1 stateserviceMapping = InputMapper.gson().fromJson(getAlllocation,
					M_ProviderServiceMap1.class);

			/**
			 * sending serviceproviderId & stateid & serviceId and getting the responce
			 * using data...
			 */

			ArrayList<M_ProviderServiceMap1> data1 = employeeMasterInter.getAllByMapId2(
					stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID(),
					stateserviceMapping.getServiceID());

			/**
			 * creating tempSerStatMapID for storing the data into temp variable ...
			 */

			int tempProSerStatMapID = 0;
			if (data1 != null && data1.size() > 0) {

				/**
				 * iterating the data using for each loop...
				 */

				for (M_ProviderServiceMap1 obj : data1) {
					tempProSerStatMapID = obj.getProviderServiceMapID();
				}
			} else {
			}

			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();
			ArrayList<Showofficedetails1> rolesData = employeeMasterInter.getlocationByMapid2(tempProSerStatMapID,
					stateserviceMapping.getDistrictID());

			response.setResponse(rolesData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("get all location  failed with exception " + e.getMessage(), e);
			response.setError(e);
		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Find employee by name")
	@RequestMapping(value = "/m/FindEmployeeByName", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String FindEmployeeName(@RequestBody String FindEmployee) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + FindEmployee);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(FindEmployee, M_User1.class);

			String checkuser = employeeMasterInter.FindEmployeeName(employeeMaster.getUserName());

			response.setResponse(checkuser.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Find Employee failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Qualification")
	@RequestMapping(value = "/m/Qualification", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String Qualification(@RequestBody String Qualification) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + Qualification);

		try {

			M_Userqualification userQualification = InputMapper.gson().fromJson(Qualification,
					M_Userqualification.class);

			ArrayList<M_Userqualification> getQuali = employeeMasterInter.getQualification();

			response.setResponse(getQuali.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Qualification failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Find employee details")
	@RequestMapping(value = "/m/FindEmployeeDetails", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String checkingEmpDetails(@RequestBody String FindEmployeeDetails) {
		OutputResponse response = new OutputResponse();
		logger.debug("requset" + FindEmployeeDetails);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(FindEmployeeDetails, M_User1.class);

			Boolean checkuser = employeeMasterInter.checkingEmpDetails(employeeMaster.getUserName(),
					employeeMaster.getAadhaarNo(), employeeMaster.getpAN(), employeeMaster.getEmployeeID(),
					employeeMaster.getHealthProfessionalID());

			response.setResponse(checkuser.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Find Employee Details failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		Boolean data = Boolean.parseBoolean(response.toString());
		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Update employer role")
	@RequestMapping(value = "/m/updateEmployeeRole", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateEmployee(@RequestBody String updateEmployee) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateEmployee);

		try {

			UserTO employeeMasterto = InputMapper.gson().fromJson(updateEmployee, UserTO.class);

			M_User1 employeeMaster = InputMapper.gson().fromJson(updateEmployee, M_User1.class);

			M_UserServiceRoleMapping2 empMaster1 = InputMapper.gson().fromJson(updateEmployee,
					M_UserServiceRoleMapping2.class);

			Integer[] roleID = employeeMasterto.getRoleID1();

			M_UserServiceRoleMapping2 resDataMap1 = null;
			List<M_UserServiceRoleMapping2> resList1 = new ArrayList<M_UserServiceRoleMapping2>();
			for (int i = 0; i < roleID.length; i++) {

				resDataMap1 = new M_UserServiceRoleMapping2();

				resDataMap1.setRoleID(roleID[i]);
				resDataMap1.setUserID(employeeMasterto.getUserID());
				resDataMap1.setProviderServiceMapID(empMaster1.getProviderServiceMapID());
				resDataMap1.setWorkingLocationID(empMaster1.getWorkingLocationID());
				resDataMap1.setCreatedBy(employeeMaster.getCreatedBy());
				resList1.add(resDataMap1);
			}

			ArrayList<M_UserServiceRoleMapping2> data4 = employeeMasterInter.mapRoleUpdation(resList1);

			response.setResponse(data4.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("update Employee failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Delete employer role")
	@RequestMapping(value = "/m/deleteEmployeeRole", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteEmployeeRole(@RequestBody String deleteEmployeeRole) {
		OutputResponse response = new OutputResponse();
		logger.debug("requset" + deleteEmployeeRole);

		try {

			UserTO employeeMasterto = InputMapper.gson().fromJson(deleteEmployeeRole, UserTO.class);

			M_User1 employeeMaster = InputMapper.gson().fromJson(deleteEmployeeRole, M_User1.class);

			M_UserServiceRoleMapping2 empMaster1 = InputMapper.gson().fromJson(deleteEmployeeRole,
					M_UserServiceRoleMapping2.class);

			M_UserServiceRoleMapping2 musrm = employeeMasterInter.uRoledelte(empMaster1.getuSRMappingID());

			musrm.setDeleted(true);

			M_UserServiceRoleMapping2 data4 = employeeMasterInter.saveRoleEdit(musrm);

			response.setResponse(data4.toString());

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			logger.error("delete Employee failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Get designation")
	@RequestMapping(value = "/m/getDesignation", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getDesignation(@RequestBody String getDesignation) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getDesignation);

		try {

			M_Designation employeeMaster = InputMapper.gson().fromJson(getDesignation, M_Designation.class);

			ArrayList<M_Designation> desiganationlist = m_DesignationInter.getDesinationlist();

			response.setResponse(desiganationlist.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("designation failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		Boolean data = Boolean.parseBoolean(response.toString());
		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Get employee by designation")
	@RequestMapping(value = "/m/getEmployeeByDesignation", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getEmployeeByDesignation(@RequestBody String getDesignation) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getDesignation);

		try {

			M_Designation employeeMaster = InputMapper.gson().fromJson(getDesignation, M_Designation.class);
			M_User1 employeeMaster1 = InputMapper.gson().fromJson(getDesignation, M_User1.class);

			ArrayList<M_User1> employeeBydesiganation = employeeMasterInter.getEmployeeByDesiganationID(
					employeeMaster.getDesignationID(), employeeMaster1.getServiceProviderID());
			
			response.setResponse(employeeBydesiganation.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("designation failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		Boolean data = Boolean.parseBoolean(response.toString());
		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Get available agent ids")
	@RequestMapping(value = "/getAvailableAgentIds", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAvailableAgentIds(@Param("{\"providerServiceMapID\":\"Integer - Provider Service Map ID\", "
			+ "\"cti_CampaignName\":\"String - Campaign Name\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("getAvailableAgentIds request is " + request);
		try {
			response.setResponse(usrAgentMappingService.getAvailableAgentIds(request).toString());
		} catch (Exception e) {
			logger.error("getAvailableAgentIds failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getAvailableAgentIds response is " + response);
		return response.toString();
	}

	@Operation(summary = "Create USR agent mapping")
	@RequestMapping(value = "/createUSRAgentMapping", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String createUSRAgentMapping(
			@Param("[{\"agentID\":\"AgentID as a string\", \"agentPassword\":\"Agent password as a String\", "
					+ "\"providerServiceMapID\":\"Integer - Provider service Map ID\", "
					+ "\"cti_CampaignName\":\"String: campaign name as in CTI\", "
					+ "\"createdBy\":\"String: User name\"},...]") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("createUSRAgentMapping request is " + request);
		try {
			response.setResponse(usrAgentMappingService.createUSRAgentMapping(request).toString());
		} catch (Exception e) {
			logger.error("createUSRAgentMapping failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("createUSRAgentMapping response is " + response);
		return response.toString();
	}

	@Operation(summary = "Get available campaigns")
	@RequestMapping(value = "/getAvailableCampaigns", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAvailableCampaigns(@Param("{\"providerServiceMapID\":\"Integer: provider service map ID\", "
			+ "\"serviceName\":\"Optional - String - service name\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("getAvailableCampaigns request is " + request);
		try {
			response.setResponse(usrAgentMappingService.getAvailableCampaigns(request).toString());
		} catch (Exception e) {
			logger.error("getAvailableCampaigns failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getAvailableCampaigns response is " + response);
		return response.toString();
	}

	@Operation(summary = "Update agent ids")
	@RequestMapping(value = "/updateAgentIds", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String updateAgentIds(@Param("{\"isAvailable\":\"Boolean - is agent ID available for future use\", "
			+ "\"usrMappingID\":\"Integer - user service role mapping id if isavailable is sent as false\", "
			+ "\"usrAgentMappingID\": \"Integer\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.debug("updateAgentIds request is " + request);
		try {
			response.setResponse(usrAgentMappingService.updateAgentIds(request).toString());
		} catch (Exception e) {
			logger.error("updateAgentIds failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		logger.debug("updateAgentIds response is " + response);
		return response.toString();
	}

	@Operation(summary = "User role and cti mapping")
	@RequestMapping(value = "/usrRoleAndCtiMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String usrRoleAndCtiMapping(@RequestBody String usrRoleAndCtiMapping) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + usrRoleAndCtiMapping);

		try {
			M_UserServiceRoleMapping2[] employeeMaster = InputMapper.gson().fromJson(usrRoleAndCtiMapping,
					M_UserServiceRoleMapping2[].class);
			List<M_UserServiceRoleMapping2> ctidata = Arrays.asList(employeeMaster);
			String data = employeeMasterInter.mapctiAgent(ctidata);
			response.setResponse(data.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("usrRoleAndCtiMapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Find employee details by user name")
	@RequestMapping(value = "/m/FindEmployeeDetailsByUserName", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String FindEmployeeDetailsByUserName(@RequestBody String FindEmployeeDetailsByUserName) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + FindEmployeeDetailsByUserName);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(FindEmployeeDetailsByUserName, M_User1.class);

			M_User1 checkuser = employeeMasterInter.FindEmployeeName1(employeeMaster.getUserName());

			response.setResponse(checkuser.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Find Employee failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Reset user password")
	@RequestMapping(value = "/m/ResetUserPassword", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String ResetUserPassword(@RequestBody String ResetUserPassword) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + ResetUserPassword);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(ResetUserPassword, M_User1.class);

			String userupdate = employeeMasterInter.ResetPassword(employeeMaster);

			response.setResponse(userupdate.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Find Employee failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Create provider admin")
	@RequestMapping(value = "/createProviderAdmin", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String createProviderAdmin(@RequestBody String createProviderAdmin) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + createProviderAdmin);

		try {

			M_User1[] employeeMaster = InputMapper.gson().fromJson(createProviderAdmin, M_User1[].class);
			List<M_User1> providerAdminData = Arrays.asList(employeeMaster);

			ArrayList<M_User1> newlyCreatedProviderAdminData = employeeMasterInter
					.createProviderAdmin(providerAdminData);

			response.setResponse(newlyCreatedProviderAdminData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("create Provider Admin failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Get provider admin")
	@RequestMapping(value = "/getProviderAdmin", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getProviderAdmin(@RequestBody String getProviderAdmin) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getProviderAdmin);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(getProviderAdmin, M_User1.class);

			ArrayList<M_User1> getAllProviderAdminFromDB = employeeMasterInter.getProviderAdmin();

			response.setResponse(getAllProviderAdminFromDB.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("get Provider Admin failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Edit provider admin")
	@RequestMapping(value = "/editProviderAdmin", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editProviderAdmin(@RequestBody String editProviderAdmin) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + editProviderAdmin);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(editProviderAdmin, M_User1.class);

			M_User1 getProviderAdminFromDB = employeeMasterInter.getProviderAdminForEdit(employeeMaster.getUserID());
			getProviderAdminFromDB.setTitleID(employeeMaster.getTitleID());
			getProviderAdminFromDB.setFirstName(employeeMaster.getFirstName());
			getProviderAdminFromDB.setMiddleName(employeeMaster.getMiddleName());
			getProviderAdminFromDB.setLastName(employeeMaster.getLastName());
			getProviderAdminFromDB.setAadhaarNo(employeeMaster.getAadhaarNo());
			getProviderAdminFromDB.setpAN(employeeMaster.getpAN());
			getProviderAdminFromDB.setEmailID(employeeMaster.getEmailID());
			getProviderAdminFromDB.setContactNo(employeeMaster.getContactNo());
			getProviderAdminFromDB.setEmergencyContactNo(employeeMaster.getEmergencyContactNo());
			getProviderAdminFromDB.setEmergencyContactPerson(employeeMaster.getEmergencyContactPerson());
			getProviderAdminFromDB.setModifiedBy(employeeMaster.getModifiedBy());
			getProviderAdminFromDB.setRemarks(employeeMaster.getRemarks());
			getProviderAdminFromDB.setdOB(employeeMaster.getdOB());

			M_User1 saveeditedData = employeeMasterInter.saveeditedData(getProviderAdminFromDB);

			response.setResponse(saveeditedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("edit Provider Admin failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Delete provider admin")
	@RequestMapping(value = "/deleteProviderAdmin", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteProviderAdmin(@RequestBody String deleteProviderAdmin) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteProviderAdmin);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(deleteProviderAdmin, M_User1.class);

			M_User1 getProviderAdminFromDB = employeeMasterInter.getProviderAdminForEdit(employeeMaster.getUserID());

			getProviderAdminFromDB.setDeleted(employeeMaster.getDeleted());

			M_User1 saveeditedData = employeeMasterInter.saveeditedData(getProviderAdminFromDB);

			response.setResponse(saveeditedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete Provider Admin failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "Create new user")
	@RequestMapping(value = "/createNewUser", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createNewUser(@RequestBody String createNewUser, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + createNewUser);

		try {

			M_User1[] employeeMaster = InputMapper.gson().fromJson(createNewUser, M_User1[].class);
			List<M_User1> newUserData = Arrays.asList(employeeMaster);
			String authToken = request.getHeader("Authorization");

			M_UserDemographics[] demographics = InputMapper.gson().fromJson(createNewUser, M_UserDemographics[].class);
			List<M_UserDemographics> demographicsData = Arrays.asList(demographics);

			ArrayList<M_User1> newlyCreatedUserData = employeeMasterInter.createNewUser(newUserData);

			ArrayList<M_UserDemographics> demogdata = new ArrayList<M_UserDemographics>();
			M_UserDemographics dmData = null;

			int x = 0;
			for (M_User1 m_user : newlyCreatedUserData) {

				dmData = new M_UserDemographics();
				dmData.setUserID(newlyCreatedUserData.get(x).getUserID());
				dmData.setFathersName(demographicsData.get(x).getFathersName());
				dmData.setMothersName(demographicsData.get(x).getMothersName());
				dmData.setCommunityID(demographicsData.get(x).getCommunityID());
				dmData.setReligionID(demographicsData.get(x).getReligionID());
				dmData.setAddressLine1(demographicsData.get(x).getAddressLine1());
				dmData.setAddressLine2(demographicsData.get(x).getAddressLine2());
				dmData.setPermAddressLine1(demographicsData.get(x).getPermAddressLine1());
				dmData.setPermAddressLine2(demographicsData.get(x).getPermAddressLine2());
				dmData.setPermStateID(demographicsData.get(x).getPermStateID());
				dmData.setPermDistrictID(demographicsData.get(x).getPermDistrictID());
				dmData.setPermPinCode(demographicsData.get(x).getPermPinCode());
				dmData.setPermanentAddress(demographicsData.get(x).getPermanentAddress());
				dmData.setIsPermanent(demographicsData.get(x).getIsPermanent());
				dmData.setCityID(demographicsData.get(x).getCityID());
				dmData.setStateID(demographicsData.get(x).getStateID());
				dmData.setCountryID(demographicsData.get(x).getCountryID());
				dmData.setPinCode(demographicsData.get(x).getPinCode());
				dmData.setIsPresent(demographicsData.get(x).getIsPresent());
				dmData.setIsPermanent(demographicsData.get(x).getIsPermanent());
				dmData.setCreatedBy(newlyCreatedUserData.get(x).getCreatedBy());
				dmData.setDistrictID(demographicsData.get(x).getDistrictID());

				demogdata.add(dmData);
				x++;
				employeeMasterInter.createUserInCallCentre(m_user, authToken);
			}

			ArrayList<M_UserDemographics> newlyCreatedDemographicsDetails = employeeMasterInter
					.SaveDemographics(demogdata);

			response.setResponse(newlyCreatedUserData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("create Provider Admin failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Edit user details")
	@RequestMapping(value = "/editUserDetails", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editUserDetails(@RequestBody String editUserDetails) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + editUserDetails);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(editUserDetails, M_User1.class);
			M_UserDemographics demographics = InputMapper.gson().fromJson(editUserDetails, M_UserDemographics.class);
			M_User1 getIdforedit = employeeMasterInter.editData(employeeMaster.getUserID());

			getIdforedit.setTitleID(employeeMaster.getTitleID());
			getIdforedit.setFirstName(employeeMaster.getFirstName());
			getIdforedit.setMiddleName(employeeMaster.getMiddleName());
			getIdforedit.setLastName(employeeMaster.getLastName());
			getIdforedit.setGenderID(employeeMaster.getGenderID());
			getIdforedit.setMaritalStatusID(employeeMaster.getMaritalStatusID());
			getIdforedit.setAadhaarNo(employeeMaster.getAadhaarNo());
			getIdforedit.setpAN(employeeMaster.getpAN());
			getIdforedit.setdOB(employeeMaster.getdOB());
			getIdforedit.setEmployeeID(employeeMaster.getEmployeeID());
			getIdforedit.setdOJ(employeeMaster.getdOJ());
			getIdforedit.setQualificationID(employeeMaster.getQualificationID());
			getIdforedit.setHealthProfessionalID(employeeMaster.getHealthProfessionalID());
			getIdforedit.setDesignationID(employeeMaster.getDesignationID());
			getIdforedit.setEmailID(employeeMaster.getEmailID());
			getIdforedit.setEmergencyContactPerson(employeeMaster.getEmergencyContactPerson());
			getIdforedit.setEmergencyContactNo(employeeMaster.getEmergencyContactNo());
			getIdforedit.setModifiedBy(employeeMaster.getModifiedBy());
			getIdforedit.setContactNo(employeeMaster.getContactNo());
			getIdforedit.setIsExternal(employeeMaster.getIsExternal());

			employeeMasterInter.saveeditedData(getIdforedit);
			M_UserDemographics getdemographicsData = employeeMasterInter.DataByUserID(employeeMaster.getUserID());
			if (null != getdemographicsData && null != demographics) {
				getdemographicsData.setFathersName(demographics.getFathersName());
				getdemographicsData.setMothersName(demographics.getMothersName());
				getdemographicsData.setCommunityID(demographics.getCommunityID());
				getdemographicsData.setReligionID(demographics.getReligionID());
				getdemographicsData.setAddressLine1(demographics.getAddressLine1());
				getdemographicsData.setAddressLine2(demographics.getAddressLine2());

				getdemographicsData.setPermAddressLine1(demographics.getPermAddressLine1());
				getdemographicsData.setPermAddressLine2(demographics.getPermAddressLine2());
				getdemographicsData.setPermStateID(demographics.getPermStateID());
				getdemographicsData.setPermDistrictID(demographics.getPermDistrictID());
				getdemographicsData.setPermPinCode(demographics.getPermPinCode());
				getdemographicsData.setPermanentAddress(demographics.getPermanentAddress());
				getdemographicsData.setCityID(demographics.getCityID());
				getdemographicsData.setStateID(demographics.getStateID());
				getdemographicsData.setCountryID(demographics.getCountryID());
				getdemographicsData.setPinCode(demographics.getPinCode());
				getdemographicsData.setIsPermanent(demographics.getIsPermanent());
				getdemographicsData.setIsPresent(demographics.getIsPresent());
				getdemographicsData.setDistrictID(demographics.getDistrictID());
				getdemographicsData.setModifiedBy(demographics.getModifiedBy());

				M_UserDemographics saveDemoData = employeeMasterInter.saveeditedDemoData(getdemographicsData);

				response.setResponse(saveDemoData.toString());
			}

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("create Provider Admin failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Deleted user details")
	@RequestMapping(value = "/deletedUserDetails", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deletedUserDetails(@RequestBody String deletedUserDetails, HttpServletRequest httpRequest) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deletedUserDetails);

		try {

			M_User1 employeeMaster = InputMapper.gson().fromJson(deletedUserDetails, M_User1.class);
			M_UserDemographics demographics = InputMapper.gson().fromJson(deletedUserDetails, M_UserDemographics.class);
			M_User1 getIdforedit = employeeMasterInter.editData(employeeMaster.getUserID());
			getIdforedit.setDeleted(employeeMaster.getDeleted());
			M_User1 editedData = employeeMasterInter.saveeditedData(getIdforedit);

			M_UserDemographics getdemographicsData = employeeMasterInter.DataByUserID(employeeMaster.getUserID());
			getdemographicsData.setDeleted(demographics.getDeleted());
			M_UserDemographics saveDemoData = employeeMasterInter.saveeditedDemoData(getdemographicsData);
			if (employeeMaster.getDeleted()) {
				usrAgentMappingService.updateDeletedAgentIDStatus(editedData.getAgentID());
			}
			String auth = httpRequest.getHeader("authorization");
			employeeMasterInter.expireAuth(editedData, auth);
			response.setResponse(saveDemoData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("deletedUserDetails failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Search mapped language by user id")
	@RequestMapping(value = "/searchMappedLanguageByUserId", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchMappedLanguageByUserId(@RequestBody String searchMappedLanguageByUserId) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchMappedLanguageByUserId);

		try {

			M_UserLangMapping newUserData = InputMapper.gson().fromJson(searchMappedLanguageByUserId,
					M_UserLangMapping.class);

			ArrayList<M_UserLangMapping> getAllMappedData = employeeMasterInter
					.searchMappedLangugeByUserId(newUserData.getUserID());

			response.setResponse(getAllMappedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("searchMappedLanguageByUserId failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Get user mapped language")
	@RequestMapping(value = "/getUserMappedLanguage", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getUserMappedLanguage(@RequestBody String getUserMappedLanguage) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getUserMappedLanguage);

		try {

			M_UserLangMapping newUserData = InputMapper.gson().fromJson(getUserMappedLanguage, M_UserLangMapping.class);

			ArrayList<M_UserLangMapping> getAllMappedData = employeeMasterInter
					.getMappedLanguge(newUserData.getServiceProviderID());

			response.setResponse(getAllMappedData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("getUserMappedLanguage failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "User language mapping")
	@RequestMapping(value = "/userLanguageMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String UserLangMapping(@RequestBody String deletedUserDetails) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deletedUserDetails);

		try {

			UserTO[] newUserData = InputMapper.gson().fromJson(deletedUserDetails, UserTO[].class);
			List<UserTO> employeeMaster = Arrays.asList(newUserData);

			M_UserLangMapping resDataMap = null;
			List<M_UserLangMapping> resList = new ArrayList<M_UserLangMapping>();

			int x = 0;
			for (UserTO ut : employeeMaster) {
				Integer[] languageID = employeeMaster.get(x).getLanguageID();
				Integer[] weightage = employeeMaster.get(x).getWeightage();
				Boolean[] canread = employeeMaster.get(x).getCanRead();
				Boolean[] canwrite = employeeMaster.get(x).getCanWrite();
				Boolean[] canSpeek = employeeMaster.get(x).getCanSpeak();
				Integer[] weightage_Read = employeeMaster.get(x).getWeightage_Read();
				Integer[] weightage_Write = employeeMaster.get(x).getWeightage_Write();
				Integer[] weightage_Speak = employeeMaster.get(x).getWeightage_Speak();

				for (int i = 0; i < languageID.length; i++) {

					resDataMap = new M_UserLangMapping();
					resDataMap.setLanguageID(languageID[i]);
					resDataMap.setWeightage(weightage[i]);
					resDataMap.setCanRead(canread[i]);
					resDataMap.setCanWrite(canwrite[i]);
					resDataMap.setCanSpeak(canSpeek[i]);
					resDataMap.setWeightage_Read(weightage_Read[i]);
					resDataMap.setWeightage_Write(weightage_Write[i]);
					resDataMap.setWeightage_Speak(weightage_Speak[i]);
					resDataMap.setUserID(employeeMaster.get(x).getUserID());
					resDataMap.setCreatedBy(employeeMaster.get(x).getCreatedBy());
					resDataMap.setServiceProviderID(employeeMaster.get(x).getServiceProviderID());
					resList.add(resDataMap);
				}
				x++;
			}

			ArrayList<M_UserLangMapping> data3 = employeeMasterInter.mapLanguage(resList);
			response.setResponse(data3.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("userLanguageMapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Update user language mapping")
	@RequestMapping(value = "/updateUserLanguageMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateUserLanguageMapping(@RequestBody String updateUserLanguageMapping) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateUserLanguageMapping);

		try {

			M_UserLangMapping employeeMaster = InputMapper.gson().fromJson(updateUserLanguageMapping,
					M_UserLangMapping.class);

			M_UserLangMapping empMaster = employeeMasterInter.updateLangMapping(employeeMaster.getUserLangID());

			empMaster.setUserID(employeeMaster.getUserID());
			empMaster.setLanguageID(employeeMaster.getLanguageID());
			empMaster.setWeightage(employeeMaster.getWeightage());
			empMaster.setCanRead(employeeMaster.getCanRead());
			empMaster.setCanWrite(employeeMaster.getCanWrite());
			empMaster.setCanSpeak(employeeMaster.getCanSpeak());
			empMaster.setWeightage_Read(employeeMaster.getWeightage_Read());
			empMaster.setWeightage_Speak(employeeMaster.getWeightage_Speak());
			empMaster.setWeightage_Write(employeeMaster.getWeightage_Write());
			empMaster.setModifiedBy(employeeMaster.getModifiedBy());

			M_UserLangMapping savelangData = employeeMasterInter.saveUserLangEditedData(empMaster);

			response.setResponse(savelangData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("updateUserLanguageMapping  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Delete user language mapping")
	@RequestMapping(value = "/deleteUserLanguageMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String UserLanguageMapping(@RequestBody String deleteUserLanguageMapping) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deleteUserLanguageMapping);

		try {

			M_UserLangMapping employeeMaster = InputMapper.gson().fromJson(deleteUserLanguageMapping,
					M_UserLangMapping.class);

			M_UserLangMapping empMaster = employeeMasterInter.updateLangMapping(employeeMaster.getUserLangID());
			empMaster.setDeleted(employeeMaster.getDeleted());
			M_UserLangMapping savelangData = employeeMasterInter.saveUserLangEditedData(empMaster);

			response.setResponse(savelangData.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete Languagemapping  failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "User role mapping")
	@RequestMapping(value = "/userRoleMapping", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String UserRoleMapping(@RequestBody String userRoleMapping, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + userRoleMapping);

		try {

			Previlegs1[] preda = InputMapper.gson().fromJson(userRoleMapping, Previlegs1[].class);
			List<Previlegs1> pre = Arrays.asList(preda);
			int x = 0;

			UserTO[] newUserData = InputMapper.gson().fromJson(userRoleMapping, UserTO[].class);
			List<UserTO> employeeMaster = Arrays.asList(newUserData);

			M_UserServiceRoleMapping2 resDataMap1 = null;
			List<M_UserServiceRoleMapping2> resList1 = new ArrayList<M_UserServiceRoleMapping2>();

			for (UserTO usrole : employeeMaster) {
				Previleges2[] predata1 = pre.get(x).getPrevileges();
				for (Previleges2 previl : predata1) {
					Integer[] RoleID = previl.getRoleID();

					for (int j = 0; j < RoleID.length; j++) {

						resDataMap1 = new M_UserServiceRoleMapping2();
						resDataMap1.setRoleID(RoleID[j]);
						resDataMap1.setUserID(employeeMaster.get(x).getUserID());
						resDataMap1.setProviderServiceMapID(previl.getProviderServiceMapID());
						resDataMap1.setWorkingLocationID(previl.getWorkingLocationID());
						resDataMap1.setCreatedBy(employeeMaster.get(x).getCreatedBy());
						resDataMap1.setServiceProviderID(employeeMaster.get(x).getServiceProviderID());
						resList1.add(resDataMap1);
					}
					x++;
				}

			}

			ArrayList<M_UserServiceRoleMapping2> data4 = employeeMasterInter.mapRole(resList1,
					request.getHeader("Authorization"));

			response.setResponse(data4.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Userlangmapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();

	}

	@Operation(summary = "User role mappings")
	@RequestMapping(value = "/userRoleMappings", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })

	public String UserRoleMappings(@RequestBody String userRoleMapping, HttpServletRequest request) throws Exception {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + userRoleMapping);

		Priveleges1097_1[] preda = InputMapper.gson().fromJson(userRoleMapping, Priveleges1097_1[].class);
		List<Priveleges1097_1> pre = Arrays.asList(preda);

		Previleges1097_3[] preda1 = InputMapper.gson().fromJson(userRoleMapping, Previleges1097_3[].class);
		List<Previleges1097_3> pre1 = Arrays.asList(preda1);

		try {

			int x = 0;
			int y = 0;
			UserTO[] newUserData = InputMapper.gson().fromJson(userRoleMapping, UserTO[].class);
			List<UserTO> employeeMaster = Arrays.asList(newUserData);

			M_UserServiceRoleMapping2 resDataMap1 = null;
			List<M_UserServiceRoleMapping2> resList1 = new ArrayList<M_UserServiceRoleMapping2>();

			for (UserTO usrole : employeeMaster) {
				Previleges1097_3[] predata1 = pre.get(x).getPrevileges();
				for (Previleges1097_3 previl : predata1) {

					Priveleges1097_2[] predata2 = previl.getID();
					for (Priveleges1097_2 previl1 : predata2) {
						resDataMap1 = new M_UserServiceRoleMapping2();
						resDataMap1.setRoleID(previl1.getRoleID());
						resDataMap1.setInbound(previl1.getInbound());
						resDataMap1.setOutbound(previl1.getOutbound());
						if (previl1.getTeleConsultation() != null) {
							resDataMap1.setTeleConsultation(previl1.getTeleConsultation());
						}
					}
					resDataMap1.setUserID(employeeMaster.get(x).getUserID());
					resDataMap1.setProviderServiceMapID(previl.getProviderServiceMapID());
					resDataMap1.setWorkingLocationID(previl.getWorkingLocationID());
					resDataMap1.setCreatedBy(employeeMaster.get(x).getCreatedBy());
					resDataMap1.setServiceProviderID(employeeMaster.get(x).getServiceProviderID());
					resDataMap1.setBlockID(previl.getBlockID());
					resDataMap1.setBlockName(previl.getBlockName());
					resDataMap1.setVillageID(previl.getVillageID());
					resDataMap1.setVillageName(previl.getVillageName());
					resList1.add(resDataMap1);

				}
				x++;

			}
			ArrayList<M_UserServiceRoleMapping2> data4 = employeeMasterInter.mapRole(resList1,
					request.getHeader("Authorization"));

			response.setResponse(data4.toString());
		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("Userlangmapping failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Update user role mapping")
	@RequestMapping(value = "/updateUserRoleMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String updateUserRoleMapping(@RequestBody String updateUserRoleMapping, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + updateUserRoleMapping);

		try {

			ObjectMapper objectMapper = new ObjectMapper();

			M_UserServiceRoleMapping2 pre = objectMapper.readValue(updateUserRoleMapping,
					M_UserServiceRoleMapping2.class);

			M_UserServiceRoleMapping2 usrRole = employeeMasterInter.getDataUsrId(pre.getuSRMappingID());

			usrRole.setUserID(pre.getUserID());
			usrRole.setRoleID(pre.getRoleID());
			usrRole.setAgentPassword(pre.getAgentPassword());
			usrRole.setProviderServiceMapID(pre.getProviderServiceMapID());
			usrRole.setWorkingLocationID(pre.getWorkingLocationID());
			usrRole.setModifiedBy(pre.getModifiedBy());
			usrRole.setBlockID(pre.getBlockID());
			usrRole.setBlockName(pre.getBlockName());
			usrRole.setVillageID(pre.getVillageID());
			usrRole.setVillageName(pre.getVillageName());

			if (pre.getTeleConsultation() != null) {
				usrRole.setTeleConsultation(pre.getTeleConsultation());
			}

			if (pre.getInbound() != null) {
				usrRole.setInbound(pre.getInbound());
			}
			if (pre.getOutbound() != null) {
				usrRole.setOutbound(pre.getOutbound());
			}

			M_UserServiceRoleMapping2 savedata = employeeMasterInter.saveRoleMappingeditedData(usrRole,
					request.getHeader("Authorization"));

			response.setResponse(savedata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("update langmapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Delete user role mapping")
	@RequestMapping(value = "/deleteUserRoleMapping", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteUserRoleMapping(@RequestBody String deletedUserRoleMapping, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deletedUserRoleMapping);

		try {

			M_UserServiceRoleMapping2 pre = InputMapper.gson().fromJson(deletedUserRoleMapping,
					M_UserServiceRoleMapping2.class);

			M_UserServiceRoleMapping2 usrRole = employeeMasterInter.getDataUsrId(pre.getuSRMappingID());

			usrRole.setDeleted(pre.getDeleted());

			M_UserServiceRoleMapping2 savedata = employeeMasterInter.saveRoleMappingeditedData(usrRole,
					request.getHeader("Authorization"));

			response.setResponse(savedata.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete langmapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Get user role mapped")
	@RequestMapping(value = "/getUserRoleMapped", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getUserRoleMapped(@RequestBody String getUserRoleMapped) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getUserRoleMapped);

		try {

			V_Userservicerolemapping getRoleData = InputMapper.gson().fromJson(getUserRoleMapped,
					V_Userservicerolemapping.class);

			ArrayList<V_Userservicerolemapping> usrRole = employeeMasterInter
					.getMappedRole(getRoleData.getServiceProviderID());

			String jsonResponse = new Gson().toJson(usrRole);
			response.setResponse(jsonResponse);

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete langmapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Search mapped role by name or user id")
	@RequestMapping(value = "/searchMappedRoleByNameorUserId", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String searchMappedRoleByNameorUserId(@RequestBody String searchMappedRoleByNameorUserId) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + searchMappedRoleByNameorUserId);

		try {

			V_Userservicerolemapping getRoleData = InputMapper.gson().fromJson(searchMappedRoleByNameorUserId,
					V_Userservicerolemapping.class);

			ArrayList<V_Userservicerolemapping> usrRole = employeeMasterInter.getMappedRole(getRoleData.getName(),
					getRoleData.getUserID());

			response.setResponse(usrRole.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete langmapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Complete user detail")
	@RequestMapping(value = "/completeUserDetails", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String completeUserDetails(@RequestBody String completeUserDetails) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + completeUserDetails);

		try {

			V_Showuser getRoleData = InputMapper.gson().fromJson(completeUserDetails, V_Showuser.class);

			ArrayList<V_Showuser> usrRole = employeeMasterInter.getcompleteUserDetails();

			response.setResponse(usrRole.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete langmapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Get religion")
	@RequestMapping(value = "/getReligion", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getReligion(@RequestBody String getReligion) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getReligion);

		try {

			M_Religion getRoleData = InputMapper.gson().fromJson(getReligion, M_Religion.class);

			ArrayList<M_Religion> usrRole = employeeMasterInter.getAllReligion();

			response.setResponse(usrRole.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("getReligion failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Get community")
	@RequestMapping(value = "/getCommunity", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getCommunity(@RequestBody String getCommunity) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getCommunity);

		try {

			M_Community getRoleData = InputMapper.gson().fromJson(getCommunity, M_Community.class);

			ArrayList<M_Community> usrRole = employeeMasterInter.getAllCommunity();

			response.setResponse(usrRole.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("getReligion failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Get all agent ids")
	@RequestMapping(value = "/getAllAgentIds", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllAgentIds(@Param("{\"providerServiceMapID\":\"Integer - Provider Service Map ID\", "
			+ "\"cti_CampaignName\":\"Optional String - Campaign Name\", "
			+ "\"isAvailable\":\"Optional boolean - availability status\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("getAllAgentIds request is " + request);
		try {
			response.setResponse(usrAgentMappingService.getAllAgentIds(request).toString());
		} catch (Exception e) {
			logger.error("getAllAgentIds failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getAllAgentIds response is " + response);
		return response.toString();
	}

	@Operation(summary = "Update CTI campaign name mapping")
	@RequestMapping(value = "/updateCTICampaignNameMapping", headers = "Authorization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String updateCTICampaignNameMapping(@Param("{\"cti_CampaignName\":\"String new campaign name\", "
			+ "\"usrAgentMappingID\": \"Integer\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.debug("updateCTICampaignNameMapping request is " + request);
		try {
			response.setResponse(usrAgentMappingService.updateCTICampaignNameMapping(request).toString());
		} catch (Exception e) {
			logger.error("updateCTICampaignNameMapping failed with exception " + e.getMessage(), e);
			response.setError(e);
		}
		logger.debug("updateCTICampaignNameMapping response is " + response);
		return response.toString();
	}

	@Operation(summary = "Get user role TM")
	@RequestMapping(value = "/getUserRoleTM", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getUserRoleTM(@RequestBody String getCommunity) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + getCommunity);

		try {

			M_UserservicerolemappingForRoleProviderAdmin getRoleData = InputMapper.gson().fromJson(getCommunity,
					M_UserservicerolemappingForRoleProviderAdmin.class);

			ArrayList<UserRole> usrRole = employeeMasterInter.getUserRoleTM(getRoleData);

			response.setResponse(usrRole.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("getReligion failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}

	@Operation(summary = "Delete user role mapping TM")
	@RequestMapping(value = "/deleteUserRoleMappingTM", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteUserRoleMappingTM(@RequestBody String deletedUserRoleMapping, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.debug("request" + deletedUserRoleMapping);

		try {

			M_UserServiceRoleMapping2 pre = InputMapper.gson().fromJson(deletedUserRoleMapping,
					M_UserServiceRoleMapping2.class);
			M_UserServiceRoleMapping2 usrRole = employeeMasterInter.deleteuserrolemapTM(pre);
			response.setResponse(usrRole.toString());

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
			logger.error("delete langmapping failed with exception " + e.getMessage(), e);
			response.setError(e);

		}

		logger.debug("response" + response);
		return response.toString();
	}
}
