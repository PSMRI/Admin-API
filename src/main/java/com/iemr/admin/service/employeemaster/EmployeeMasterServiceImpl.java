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

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonParser;
import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
import com.iemr.admin.data.employeemaster.M_Community;
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
import com.iemr.admin.exceptionhandler.DataNotFound;
import com.iemr.admin.repo.blocking.MProviderservicemappingBlockingRepo;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepo;
import com.iemr.admin.repo.employeemaster.EmployeeMasterRepoo;
import com.iemr.admin.repo.employeemaster.M_CommunityRepo;
import com.iemr.admin.repo.employeemaster.M_GenderRepo;
import com.iemr.admin.repo.employeemaster.M_ProviderServiceMap1Repo;
import com.iemr.admin.repo.employeemaster.M_QualificationRepo;
import com.iemr.admin.repo.employeemaster.M_ReligionRepo;
import com.iemr.admin.repo.employeemaster.M_TitleRepo;
import com.iemr.admin.repo.employeemaster.M_UserDemographicsRepo;
import com.iemr.admin.repo.employeemaster.M_UserLangMappingRepo;
import com.iemr.admin.repo.employeemaster.RoleRepo;
import com.iemr.admin.repo.employeemaster.Showofficedetails1Repo1;
import com.iemr.admin.repo.employeemaster.ShowuserdetailsfromuserservicerolemappingRepo;
import com.iemr.admin.repo.employeemaster.V_ShowuserRepo;
import com.iemr.admin.repo.employeemaster.V_UserservicerolemappingRepo;
import com.iemr.admin.repository.provideronboard.M_ServiceMasterRepo;
import com.iemr.admin.repository.rolemaster.M_UserservicerolemappingForRoleProviderAdminRepo;
import com.iemr.admin.service.user.EncryptUserPassword;
import com.iemr.admin.utils.config.ConfigProperties;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.http.HttpUtils;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@Service
public class EmployeeMasterServiceImpl implements EmployeeMasterInter {

	private static HttpUtils httpUtils = new HttpUtils();

	private String common_url = ConfigProperties.getPropertyByName("common-url");

	private final String COMMON_BASE_URL = "common-url";

	private static final Boolean ENABLE_CTI_USER_CREATION = ConfigProperties.getBoolean("enableCTIUserCreation");
	@Autowired
	private M_CommunityRepo m_CommunityRepo;

	@Autowired
	private M_ReligionRepo m_ReligionRepo;

	@Autowired
	private V_ShowuserRepo v_ShowuserRepo;

	@Autowired
	private V_UserservicerolemappingRepo v_UserservicerolemappingRepo;

	@Autowired
	private M_UserservicerolemappingForRoleProviderAdminRepo userservicerolemappingForRoleProviderAdminRepo;

	@Autowired
	private USRAgentMappingServiceImpl uSRAgentMappingServiceImpl;

	@Autowired
	private MProviderservicemappingBlockingRepo mProviderservicemappingBlockingRepo;

	@Autowired
	private EncryptUserPassword encryptUserPassword;
	private InputMapper inputMapper = new InputMapper();
	private Logger logger = LoggerFactory.getLogger(EmployeeMasterServiceImpl.class);

	private ConfigProperties configProperties;

	@Autowired
	public void setConfigProperties(ConfigProperties configProperties) {
		this.configProperties = configProperties;
	}

	@Autowired
	private M_QualificationRepo m_QualificationRepo;

	@Autowired
	private Showofficedetails1Repo1 showofficedetails1Repo1;

	@Autowired
	private M_ProviderServiceMap1Repo m_ProviderServiceMap1Repo;
	@Autowired
	private DataSource dataSource;

	@Autowired
	private M_TitleRepo m_TitleRepo;

	@Autowired
	private M_GenderRepo m_GenderRepo;

	@Autowired
	private ShowuserdetailsfromuserservicerolemappingRepo showuserdetailsfromuserservicerolemappingRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private M_UserLangMappingRepo m_UserLangMappingRepo;

	@Autowired
	private M_UserDemographicsRepo m_UserDemographicsRepo;

	@Autowired
	private EmployeeMasterRepoo employeeMasterRepo11;

	@Autowired
	private EmployeeMasterRepo employeeMasterRepo;
	@Autowired
	private EmployeeMasterRepoo employeeMasterRepoo;

	@Autowired
	M_ServiceMasterRepo serviceMasterRepo;
	/*
	 * @Override public ArrayList<M_Role> getAllRole() { //ArrayList<M_Role> resSet
	 * = new ArrayList<M_Role>(); //resSet =
	 * (ArrayList<M_Role>)roleRepo.getAllRole(); //ArrayList<M_Role >
	 * stateServiceMappings = new ArrayList<M_Role>(); //ArrayList<M_Role>
	 * data=roleRepo.getAllRole(); for (M_Role objects :data) {
	 * stateServiceMappings.add(new M_Role Integer objects, String objects, Integer
	 * objects)); } //stateServiceMappings.addAll(data); return
	 * roleRepo.getAllRole();
	 * 
	 * }
	 */

	@Autowired
	private MProviderservicemappingBlockingRepo providerServiceMappingRepo;

	@Override
	public ArrayList<M_Role> getAllRole() {
		logger.info("EmployeeMasterServiceImpl.getAllRole - start");
		// ArrayList<M_Role> resSet = new ArrayList<M_Role>();
		// resSet = (ArrayList<M_Role>)roleRepo.getAllRole();
		ArrayList<M_Role> stateServiceMappings = new ArrayList<M_Role>();
		ArrayList<M_Role> data = roleRepo.getAllRole();
		for (M_Role objects : data) {
			// if (objects!=null && objects.length>=2) {
			logger.info("State-Svc-Map: " + objects.getRoleID() + ":PSMAPID: " + objects.getProviderServiceMapID());
			stateServiceMappings
					.add(new M_Role(objects.getRoleID(), objects.getRoleName(), objects.getProviderServiceMapID()));
		}
		// }
		// stateServiceMappings.addAll(data);
		logger.info("EmployeeMasterServiceImpl.getAllRole - finish");
		return stateServiceMappings;

	}

	@Override
	public ArrayList<M_UserServiceRoleMapping2> getEmployeeDetails() {
		ArrayList<M_UserServiceRoleMapping2> stateServiceMappings = new ArrayList<M_UserServiceRoleMapping2>();
		ArrayList<Object[]> resultSet = employeeMasterRepo.getEmployeeDetails();
		for (Object[] objects : resultSet) {
			if (objects != null && objects.length >= 2) {

				stateServiceMappings.add(new M_UserServiceRoleMapping2((Integer) objects[0], (Integer) objects[1],
						(Integer) objects[2], (Integer) objects[3], (String) objects[4], (String) objects[5],
						(String) objects[6], (String) objects[7], (Integer) objects[8], (String) objects[9],
						(Integer) objects[10], (String) objects[11],
						(String) objects[12]/*
											 * ,(String) objects[13],(String) objects[14]
											 */));
			}
		}
		return stateServiceMappings;
	}

	@Override
	public Integer saveEmployee(M_User1 m_UserList) {
		logger.info("EmployeeMasterServiceImpl.saveEmployee - start");
		M_User1 data = employeeMasterRepo11.save(m_UserList);
		logger.info("Encrypt password returned " + encryptUserPassword.encryptUserCredentials(data).toString());
		Integer data1 = data.getUserID();
		logger.info("EmployeeMasterServiceImpl.saveEmployee - finish");
		return data1;
	}

	// @Async
	// private void encryptUserCredentials(M_User1 data) {
	// HttpUtils utils = new HttpUtils();
	// String commonBaseURL = ConfigProperties.getPropertyByName("common-url");
	// String encryptPasswordURL = commonBaseURL +
	// ConfigProperties.getPropertyByName("encrypt-password-url");
	// JSONObject request = new JSONObject();
	// request.put("password", data.getPassword());
	// request.put("userName", data.getUserName());
	// utils.post(encryptPasswordURL, request.toString());
	// }

	@Override
	public M_User1 editEmployee(Integer userID) {

		return employeeMasterRepo11.editEmployee(userID);
	}

	@Override
	public M_User1 saveEditData(M_User1 editdata) {
		M_User1 saveEditData = employeeMasterRepo11.save(editdata);
		encryptUserPassword.encryptUserCredentials(editdata);
		return saveEditData;
	}

	@Override
	public ArrayList<M_UserServiceRoleMapping2> getEmployeeDetails1() {
		ArrayList<M_UserServiceRoleMapping2> stateServiceMappings = new ArrayList<M_UserServiceRoleMapping2>();
		ArrayList<Object[]> resultSet = employeeMasterRepo.getEmployeeDetails1();
		for (Object[] objects : resultSet) {
			if (objects != null) {

				stateServiceMappings.add(new M_UserServiceRoleMapping2((Integer) objects[0], (Integer) objects[1],
						(Integer) objects[2], (Integer) objects[3], (String) objects[4], (String) objects[5],
						(String) objects[6], (String) objects[7], (Integer) objects[8], (String) objects[9],
						(Integer) objects[10], (String) objects[11], (String) objects[12], (String) objects[13],
						(String) objects[14], (String) objects[15], (String) objects[16], (String) objects[17],
						(String) objects[18], (String) objects[19], (Integer) objects[20], (Integer) objects[21],
						(Integer) objects[22], (Integer) objects[23], (Integer) objects[24], (Integer) objects[25],
						(String) objects[26], (String) objects[27], (String) objects[28], (String) objects[29],
						(String) objects[30], (Integer) objects[31], (Integer) objects[32], (Integer) objects[33],
						(String) objects[34], (Integer) objects[35], (Integer) objects[36], (Integer) objects[37],
						(String) objects[38], (String) objects[39], (Date) objects[40], (Date) objects[41],
						(Integer) objects[42], (String) objects[43], (String) objects[44], (String) objects[45],
						(Integer) objects[46], (String) objects[47], (String) objects[48], (Character) objects[49],
						(String) objects[50], (String) objects[51], (String) objects[52], (String) objects[53],
						(String) objects[54], (String) objects[55], (Character) objects[56], (String) objects[57],
						(String) objects[58]));
			}
		}
		return stateServiceMappings;

	}

	@Override
	public Integer saveDemography(M_UserDemographics ud) {
		M_UserDemographics data2 = m_UserDemographicsRepo.save(ud);
		return data2.getDemographicID();
	}

	@Override
	public ArrayList<M_UserLangMapping> mapLanguage(List<M_UserLangMapping> resList) {
		ArrayList<M_UserLangMapping> reslist = (ArrayList<M_UserLangMapping>) m_UserLangMappingRepo.save(resList);
		return reslist;
	}

	@Override
	public ArrayList<M_UserServiceRoleMapping2> mapRole(List<M_UserServiceRoleMapping2> resList1, String authToken) {
		ArrayList<M_UserServiceRoleMapping2> reslist = (ArrayList<M_UserServiceRoleMapping2>) employeeMasterRepo
				.save(resList1);
		if (ENABLE_CTI_USER_CREATION) {
			updateSupervisorRoleInCTI(resList1, authToken);
		}
		for (M_UserServiceRoleMapping2 mapping : resList1) {
			if (mapping.getVillageID() != null && mapping.getVillageName() != null) {
				StringBuilder sb = new StringBuilder();
				// sb.append("[");
				for (String str : mapping.getVillageID()) {
					sb.append(str).append(",");

				}
				String villageId = sb.substring(0, sb.length() - 1);
				// String s=villageId+"]";
				mapping.setVillageidDb(villageId);

				StringBuilder sc = new StringBuilder();
				// sc.append("[");
				for (String str : mapping.getVillageName()) {
					sc.append(str).append(",");

				}
				String villageName = sc.substring(0, sc.length() - 1);
				// String sn=villageName+"]";
				mapping.setVillageNameDb(villageName);
				employeeMasterRepo.save(mapping);
			}

		}
		return reslist;
	}

	private class UserServiceLine {
		String userName;
		Set<String> serviceLines;
		Set<String> roleNames;
		Set<String> campaignNames;
		Set<Integer> providerServiceMapIDs;

		public UserServiceLine() {
			this.roleNames = new HashSet<String>();
			this.serviceLines = new HashSet<String>();
			this.campaignNames = new HashSet<String>();
			this.providerServiceMapIDs = new HashSet<Integer>();
		}

		@Override
		public boolean equals(Object obj) {
			if (this.hashCode() == obj.hashCode())
				return true;
			UserServiceLine roleObj = (UserServiceLine) obj;
			if (this.roleNames.containsAll(roleObj.roleNames)) {
				return true;
			}
			if (this.serviceLines.containsAll(roleObj.serviceLines)) {
				return true;
			}
			if (this.campaignNames.containsAll(roleObj.campaignNames)) {
				return true;
			}
			if (this.providerServiceMapIDs.containsAll(roleObj.providerServiceMapIDs)) {
				return true;
			}
			return false;
		}
	}

	@Async
	private void updateSupervisorRoleInCTI(List<M_UserServiceRoleMapping2> resList1, String authToken) {
		Map<Integer, UserServiceLine> userServiceLineMap = new HashMap<Integer, UserServiceLine>();
		Set<Integer> providerMapIds = new HashSet<Integer>();
		for (M_UserServiceRoleMapping2 userRole : resList1) {
			userRole.setmRole(roleRepo.findOne(userRole.getRoleID()));
			if (userRole.getmRole().getRoleName().equalsIgnoreCase("supervisor")) {
				if (!userServiceLineMap.containsKey(userRole.getUserID())) {
					userServiceLineMap.put(userRole.getUserID(), new UserServiceLine());
				}
				userServiceLineMap.get(userRole.getUserID()).serviceLines.add(providerServiceMappingRepo
						.findOne(userRole.getProviderServiceMapID()).getM_ServicemasterForBlocking().getServiceName());
				userServiceLineMap.get(userRole.getUserID()).providerServiceMapIDs
						.add(userRole.getProviderServiceMapID());
				userServiceLineMap.get(userRole.getUserID()).userName = employeeMasterRepoo
						.getUserNameByUserID(userRole.getUserID());
				providerMapIds.add(userRole.getProviderServiceMapID());
			}
		}
		if (providerMapIds.size() > 0) {
			List<Integer> providerIds = new ArrayList<Integer>();
			providerIds.addAll(providerMapIds);
			List<M_Providerservicemapping_Blocking> providerCampaigns = providerServiceMappingRepo
					.findM_Providerservicemapping_BlockingByProviderServiceMapIDs(providerIds);
			for (M_Providerservicemapping_Blocking providerCampaign : providerCampaigns) {
				Set<String> ctiRoles = new HashSet<String>();
				String campaign = providerCampaign.getcTI_CampaignName();
				ctiRoles.addAll(getCTICampaignRoles(campaign, authToken));
				Set<Integer> userIDs = userServiceLineMap.keySet();
				for (Integer userID : userIDs) {
					if (userServiceLineMap.get(userID).providerServiceMapIDs
							.contains(providerCampaign.getProviderServiceMapID())) {
						userServiceLineMap.get(userID).roleNames.addAll(ctiRoles);
					}
				}
			}
		}

		Set<Integer> userIDs = userServiceLineMap.keySet();
		for (Integer userID : userIDs) {
			String userName = userServiceLineMap.get(userID).userName;
			Set<String> serviceLines = userServiceLineMap.get(userID).serviceLines;
			Set<String> roleNames = new HashSet<String>();
			roleNames.addAll(userServiceLineMap.get(userID).roleNames);
			String role = null;
			boolean update = false;
			for (String roleName : roleNames) {
				if (role == null) {
					role = roleName;
				}
				update = true;
				int match = 0;
				for (String serviceLine : serviceLines) {
					if (!roleName.contains(serviceLine)) {
						break;
					}
					role = roleName;
					match++;
				}
				if (match == serviceLines.size()) {
					break;
				}
			}
			if (update) {
				updateUserInCallCentre(userName, role, authToken);
			}
		}
	}

	private Set<String> getCTICampaignRoles(String campaignName, String authToken) {
		Set<String> resultSet = new HashSet<String>();
		HttpUtils httpUtils = new HttpUtils();
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", authToken);
		String getRolesURL = configProperties.getPropertyByName("common-url")
				+ configProperties.getPropertyByName("campaign-roles-url");
		JSONObject request = new JSONObject();
		request.put("campaign", campaignName);

		OutputResponse response = inputMapper.gson().fromJson(httpUtils.post(getRolesURL, request.toString(), headers),
				OutputResponse.class);
		if (response.isSuccess()) {
			JSONObject obj = new JSONObject(response.getData());
			JSONArray roles = obj.getJSONArray("roles");
			for (int roleIndex = 0; roleIndex < roles.length(); roleIndex++) {
				resultSet.add(roles.getString(roleIndex));
			}
		}
		return resultSet;
	}

	@Override
	public M_UserDemographics mdedit(Integer userID) {
		return m_UserDemographicsRepo.mdedit(userID);
	}

	@Override
	public M_UserLangMapping ulangmapedit(Integer userID, Integer languageID) {
		// TODO Auto-generated method stub
		return m_UserLangMappingRepo.ulangmapedit(userID, languageID);
	}

	@Override
	public M_UserServiceRoleMapping2 uRoleMedit(Integer userID, Integer roleID) {
		// TODO Auto-generated method stub
		return employeeMasterRepo.uRoleMedit(userID, roleID);
	}

	@Override
	public Integer saveeditDemo(M_UserDemographics ud1) {
		M_UserDemographics ud = m_UserDemographicsRepo.save(ud1);
		return ud.getDemographicID();
	}

	@Override
	public Integer saveeditlangdata(M_UserLangMapping mulm) {
		M_UserLangMapping ulm = m_UserLangMappingRepo.save(mulm);
		return ulm.getUserLangID();
	}

	@Override
	public M_UserServiceRoleMapping2 saveRoleEdit(M_UserServiceRoleMapping2 musrm) {
		M_UserServiceRoleMapping2 musrm1 = employeeMasterRepo.save(musrm);
		return musrm;
	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails2(Integer serviceProviderID,
			Integer pSMStateID) {
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails2(serviceProviderID, pSMStateID);
	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails3(Integer serviceProviderID,
			Integer roleID) {
		// TODO Auto-generated method stub
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails3(serviceProviderID, roleID);
	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails4(Integer serviceProviderID,
			Integer serviceID) {
		// TODO Auto-generated method stub
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails4(serviceProviderID, serviceID);
	}

	@Override
	public ArrayList<V_Showuser> getEmployeeDetails5() {
		// TODO Auto-generated method stub
		return v_ShowuserRepo.EmployeeDetails5();
	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails6(Integer serviceProviderID,
			Integer userID) {
		// TODO Auto-generated method stub
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails6(serviceProviderID, userID);
	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails7(Integer serviceProviderID,
			Integer pSMStateID, Integer workingDistrictID) {
		// TODO Auto-generated method stub
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails7(serviceProviderID, pSMStateID,
				workingDistrictID);
	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails8(Integer serviceProviderID,
			Integer pSMStateID, Integer workingDistrictID, Integer workingLocationID) {
		// TODO Auto-generated method stub
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails8(serviceProviderID, pSMStateID,
				workingDistrictID, workingLocationID);
	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails9(Integer serviceProviderID,
			Integer pSMStateID, Integer roleID) {
		// TODO Auto-generated method stub
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails9(serviceProviderID, pSMStateID, roleID);
	}

	@Override
	public ArrayList<M_Title> getAllTitle() {
		ArrayList<M_Title> AllTitle = new ArrayList<M_Title>();
		ArrayList<M_Title> data = m_TitleRepo.getAllTitle();
		for (M_Title objects : data) {
			// if (objects!=null && objects.length>=2) {

			AllTitle.add(new M_Title(objects.getTitleID(), (String) objects.getTitleName()));
		}
		return AllTitle;
	}

	@Override
	public ArrayList<M_Gender> getAllGender() {
		ArrayList<M_Gender> AllGender = new ArrayList<M_Gender>();
		ArrayList<M_Gender> data = m_GenderRepo.getAllGender();
		for (M_Gender objects : data) {
			// if (objects!=null && objects.length>=2) {

			AllGender.add(new M_Gender(objects.getGenderID(), (String) objects.getGenderName()));
		}
		return AllGender;
		/*
		 * @Override public M_User1 saveEmployee(M_User1 employeeMaster) { M_User1
		 * data=employeeMasterRepo11.save(employeeMaster); return data; }
		 */

	}

	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails10(Integer serviceProviderID,
			Integer pSMStateID, Integer roleID, Integer serviceID, String userName, Integer userID) {
		// TODO Auto-generated method stub
		return showuserdetailsfromuserservicerolemappingRepo.EmployeeDetails10(serviceProviderID, pSMStateID, roleID,
				serviceID, userName, userID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Showuserdetailsfromuserservicerolemapping> getEmployeeDetails11(Integer serviceProviderID,
			Integer pSMStateID, Integer serviceID, Integer roleID, String userName, Integer userID) {
		// TODO Auto-generated method stub
		JdbcTemplate select = new JdbcTemplate(dataSource);

		ArrayList<Showuserdetailsfromuserservicerolemapping> userData = new ArrayList<Showuserdetailsfromuserservicerolemapping>();
		userData = (ArrayList<Showuserdetailsfromuserservicerolemapping>) select.query(
				"call db_iemr.PR_FetchUserDetails(?,?,?,?,?,?)",
				new Object[] { serviceProviderID, pSMStateID, serviceID, roleID, userName, userID },
				new empmasterrowmapper());
		return userData;

	}

	@Override
	public ArrayList<M_ProviderServiceMap1> getAllByMapId2(Integer serviceProviderID, Integer stateID,
			Integer serviceID) {
		ArrayList<M_ProviderServiceMap1> resSet = new ArrayList<M_ProviderServiceMap1>();
		resSet = (ArrayList<M_ProviderServiceMap1>) m_ProviderServiceMap1Repo.getAllByMapId2(serviceProviderID, stateID,
				serviceID);
		// logger.info("helloooo");
		return resSet;
	}

	@Override
	public ArrayList<Showofficedetails1> getlocationByMapid2(int tempProSerStatMapID, Integer districtID) {
		ArrayList<Showofficedetails1> reslist = showofficedetails1Repo1.getlocationByMapid(tempProSerStatMapID,
				districtID);
		return reslist;
	}

	@Override
	public String FindEmployeeName(String userName) {

		M_User1 user = employeeMasterRepoo.findEmployeeByName(userName);
		if (user == null) {
			return "usernotexist";
		} else {
			return "userexist";
		}

	}

	@Override
	public M_User1 FindEmployeeName1(String userName) {

		M_User1 user = employeeMasterRepoo.findEmployeeByName(userName);

		return user;
	}

	@Override
	public ArrayList<M_Userqualification> getQualification() {
		// TODO Auto-generated method stub
		return m_QualificationRepo.getAllQualification();
	}

	@Override
	public String createAgent(String agentID, String st) {
		logger.info("EmployeeMasterServiceImpl.createAgent - start");
		String AgentsAPIURL = "";
		AgentsAPIURL = configProperties.getPropertyByName("Agents-api-url");
		String ctiServer = configProperties.getPropertyByName("callcentre-server-ip");
		AgentsAPIURL = AgentsAPIURL.replace("AGENTID", agentID).replace("AGENTNAME", st).replace("CTI_SERVER",
				ctiServer);
		// .replace("CAMPAIGN_NAME", ctiCampaignName);
		HttpUtils httpUtils = new HttpUtils();

		logger.info("AgentsAPIURL -> " + AgentsAPIURL);

		logger.info("EmployeeMasterServiceImpl.createAgent - finish");
		return AgentsAPIURL;
	}

	@Async
	@Override
	public void createUserInCallCentre(M_User1 user, String authToken) {
		logger.info("EmployeeMasterServiceImpl.createUserInCallCentre - start");
		if (ENABLE_CTI_USER_CREATION) {
			String UserCreateAPIURL = "";
			String ctiServer = configProperties.getPropertyByName("common-url");
			UserCreateAPIURL = ctiServer + configProperties.getPropertyByName("create-update-users-url");
			HashMap<String, Object> headers = new HashMap<String, Object>();
			JSONObject request = new JSONObject();
			request.put("username", user.getUserName());
			request.put("password", user.getPassword());
			request.put("firstname", user.getFirstName());
			request.put("lastname", user.getLastName());
			request.put("phone", user.getContactNo());
			request.put("email", user.getEmailID());
			request.put("role", "Default");
			request.put("designation", "Default");

			/*
			 * Note if here we will insert duplicate key then old value will be replaced
			 * with new value and it will return the old object Resign it is the internal
			 * functionality HashMap so be aware
			 */

			HttpUtils httpUtils = new HttpUtils();
			headers.put("Authorization", authToken);
			logger.info("UserCreateAPIURL -> " + UserCreateAPIURL);
			httpUtils.post(UserCreateAPIURL, request.toString(), headers);
		}
		logger.info("EmployeeMasterServiceImpl.createUserInCallCentre - finish");
	}

	@Async
	@Override
	public void updateUserInCallCentre(String userName, String ctiRole, String authToken) {
		logger.info("EmployeeMasterServiceImpl.updateUserInCallCentre - start");

		if (ENABLE_CTI_USER_CREATION) {
			String UserCreateAPIURL = "";
			String ctiServer = configProperties.getPropertyByName("common-url");
			UserCreateAPIURL = ctiServer + configProperties.getPropertyByName("create-update-users-url");
			HashMap<String, Object> headers = new HashMap<String, Object>();
			JSONObject request = new JSONObject();
			request.put("username", userName);
			request.put("role", ctiRole);
			HttpUtils httpUtils = new HttpUtils();
			headers.put("Authorization", authToken);
			logger.info("updateUserInCallCentreURL -> " + UserCreateAPIURL);
			httpUtils.post(UserCreateAPIURL, request.toString(), headers);
		}
		logger.info("EmployeeMasterServiceImpl.updateUserInCallCentre - finish");
	}

	@Override
	public Boolean checkingEmpDetails(String userName, String aadhaarNo, String getpAN, String employeeID,
			String healthProfessionalID) {
		M_User1 user = employeeMasterRepoo.checkingEmpDetails(userName, aadhaarNo, getpAN, employeeID,
				healthProfessionalID);
		if (user == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public ArrayList<M_UserServiceRoleMapping2> mapRoleUpdation(List<M_UserServiceRoleMapping2> resList1) {
		ArrayList<M_UserServiceRoleMapping2> data = (ArrayList<M_UserServiceRoleMapping2>) employeeMasterRepo
				.save(resList1);
		return data;
	}

	@Override
	public M_UserServiceRoleMapping2 uRoledelte(Integer getuSRMappingID) {

		return employeeMasterRepo.uRoledelte(getuSRMappingID);
	}

	@Override
	public String mapctiAgent(List<M_UserServiceRoleMapping2> ctidata) throws IEMRException {
		logger.info("EmployeeMasterServiceImpl.mapctiAgent - start");
		ArrayList<M_UserServiceRoleMapping2> reslist = new ArrayList<M_UserServiceRoleMapping2>();
		for (M_UserServiceRoleMapping2 i : ctidata) {
			i.setUsrMappingID(i.getuSRMappingID());
			logger.info("RoleMap --> " + i.toString() + " : SRMapID :" + i.getuSRMappingID() + " : AgentID: "
					+ i.getAgentID() + " :Paswd: " + i.getAgentPassword());
			employeeMasterRepo.createcitmapping(i.getuSRMappingID(), i.getAgentID(), i.getAgentPassword());
			uSRAgentMappingServiceImpl.updateAgentIds(i.toString());
		}

		logger.info("EmployeeMasterServiceImpl.mapctiAgent - finish");
		return "cit agent mapping done";
	}

	@Override
	public String ResetPassword(M_User1 user) {
		String password = user.getPassword();
		user.setPassword(password);
		// int user1 = employeeMasterRepoo.resetpassword(user.getUserName(),
		// user.getStatusID());
		OutputResponse response = encryptUserPassword.encryptUserCredentials(user);
		if (response.isSuccess())
			return "Password reset successfully";
		else
			return "Password Not Set Properly";
	}

	@Override
	public ArrayList<M_User1> createProviderAdmin(List<M_User1> providerAdminData) throws Exception {
		for (M_User1 user : providerAdminData) {
			if (user.getPassword() != null)
				user.setPassword(generateStrongPassword(user.getPassword()));
			else {
				throw new Exception("Password is null");
			}
		}

		ArrayList<M_User1> data = (ArrayList<M_User1>) employeeMasterRepoo.save(providerAdminData);
		return data;
	}

	@Override
	public ArrayList<M_User1> getProviderAdmin() {

		ArrayList<M_User1> providerAdminData = employeeMasterRepoo.getAllProviderAdminData();

		return providerAdminData;
	}

	@Override
	public M_User1 getProviderAdminForEdit(Integer userID) {
		M_User1 getDataFromTable = employeeMasterRepoo.findOne(userID);
		return getDataFromTable;
	}

	@Override
	public M_User1 saveeditedData(M_User1 getProviderAdminFromDB) {
		if (getProviderAdminFromDB.getDeleted() != null && !getProviderAdminFromDB.getDeleted()) {
			getProviderAdminFromDB.setFailedAttempt(0);
		}
		M_User1 savedDataintoDB = employeeMasterRepoo.save(getProviderAdminFromDB);
		return savedDataintoDB;
	}

	@Override
	public ArrayList<M_User1> createNewUser(List<M_User1> newUserData) throws Exception {

		for (M_User1 user : newUserData) {
			if (user.getPassword() != null)
				user.setPassword(generateStrongPassword(user.getPassword()));
			else {
				throw new Exception("Password is null");
			}
		}

		ArrayList<M_User1> saveDataintoDB = (ArrayList<M_User1>) employeeMasterRepoo.save(newUserData);

		return saveDataintoDB;
	}

	// temp code gievn because =, reset password code change
	public String generateStrongPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		int iterations = 1001;
		char[] chars = password.toCharArray();
		byte[] salt = getSalt();

		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 512);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return iterations + ":" + toHex(salt) + ":" + toHex(hash);
	}

	private byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	private String toHex(byte[] array) throws NoSuchAlgorithmException {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = array.length * 2 - hex.length();
		if (paddingLength > 0) {
			return String.format(new StringBuilder().append("%0").append(paddingLength).append("d").toString(),
					new Object[] { Integer.valueOf(0) }) + hex;
		}
		return hex;
	}

	// temp code gievn because =, reset password code change

	@Override
	public ArrayList<M_UserDemographics> SaveDemographics(ArrayList<M_UserDemographics> demogdata) {
		ArrayList<M_UserDemographics> saveDataintoDB = (ArrayList<M_UserDemographics>) m_UserDemographicsRepo
				.save(demogdata);
		return saveDataintoDB;
	}

	@Override
	public M_User1 editData(Integer userID) {
		M_User1 data = employeeMasterRepoo.findOne(userID);
		return data;
	}

	@Override
	public M_UserDemographics DataByUserID(Integer userID) {
		M_UserDemographics data = m_UserDemographicsRepo.findByUserID(userID);
		return data;
	}

	@Override
	public M_UserDemographics saveeditedDemoData(M_UserDemographics getdemographicsData) {
		M_UserDemographics data = m_UserDemographicsRepo.save(getdemographicsData);
		return data;
	}

	@Override
	public M_UserLangMapping updateLangMapping(Integer userLangID) {
		M_UserLangMapping data = m_UserLangMappingRepo.findOne(userLangID);
		return data;
	}

	@Override
	public M_UserLangMapping saveUserLangEditedData(M_UserLangMapping empMaster) {
		M_UserLangMapping data = m_UserLangMappingRepo.save(empMaster);
		return data;
	}

	@Override
	public M_UserServiceRoleMapping2 getDataUsrId(Integer uSRMappingID) {
		M_UserServiceRoleMapping2 data = employeeMasterRepo.findOne(uSRMappingID);
		return data;
	}

	@Override
	public M_UserServiceRoleMapping2 saveRoleMappingeditedData(M_UserServiceRoleMapping2 usrRole, String authToken) {

		M_UserServiceRoleMapping2 data = employeeMasterRepo.save(usrRole);
		if (ENABLE_CTI_USER_CREATION) {
			List<M_UserServiceRoleMapping2> list = new ArrayList<M_UserServiceRoleMapping2>();
			list.add(data);
			updateSupervisorRoleInCTI(list, authToken);
		}
//		for (M_UserServiceRoleMapping2 mapping : usrRole) {
				if (usrRole.getVillageID() != null && usrRole.getVillageName() != null) {

					StringBuilder sb = new StringBuilder();
					// sb.append("[");
					for (String str : usrRole.getVillageID()) {
						sb.append(str).append(",");

					}
					String villageId = sb.substring(0, sb.length() - 1);
					// String sr=villageId+"]";
					usrRole.setVillageidDb(villageId);

					StringBuilder sc = new StringBuilder();
					// sc.append("[");
					for (String str : usrRole.getVillageName()) {
						sc.append(str).append(",");

					}
					String villageName = sc.substring(0, sc.length() - 1);
					// String st=villageName+"]";
					usrRole.setVillageNameDb(villageName);

					M_UserServiceRoleMapping2 savedData = employeeMasterRepo.save(usrRole);
				}
		return data;
	}

	@Override
	public ArrayList<M_UserLangMapping> getMappedLanguge(Integer serviceProviderID) {
		ArrayList<M_UserLangMapping> stateServiceMappings = new ArrayList<M_UserLangMapping>();
		ArrayList<Object[]> resultSet = m_UserLangMappingRepo.getMappedLanguge(serviceProviderID);
		for (Object[] objects : resultSet) {
			if (objects != null) {

				stateServiceMappings.add(new M_UserLangMapping((Integer) objects[0], (Integer) objects[1],
						(Integer) objects[2], (Integer) objects[3], (String) objects[4], (String) objects[5],
						(Boolean) objects[6], (Boolean) objects[7], (Boolean) objects[8], (String) objects[9],
						(Boolean) objects[10], (Integer) objects[11], (Integer) objects[12], (Integer) objects[13],
						(Boolean) objects[14]));
			}
		}
		return stateServiceMappings;
	}

	@Override
	public ArrayList<V_Userservicerolemapping> getMappedRole(Integer serviceProviderID) {
		ArrayList<V_Userservicerolemapping> getData = (ArrayList<V_Userservicerolemapping>) v_UserservicerolemappingRepo
				.getAllRoleOfProvider(serviceProviderID);
		
		ArrayList<V_Userservicerolemapping> mappedRoles = new ArrayList<>();

		if (getData != null) {
			for (V_Userservicerolemapping mapping : getData) {
				if (mapping.getVillageidDb() != null) {
					mapping.setVillageID(mapping.getVillageidDb().split(","));
				} else {
					mapping.setVillageID(new String[0]);
				}

				if (mapping.getVillageNameDb() != null) {
					mapping.setVillageName(mapping.getVillageNameDb().split(","));
				} else {
					mapping.setVillageName(new String[0]);
				}
				if (mapping.getServiceID()!=null) {
					mapping.setBlockID(mapping.getBlockID());
					mapping.setBlockName(mapping.getBlockName());
					mapping.setVillageID(mapping.getVillageID());
					mapping.setVillageName(mapping.getVillageName());
				} else {
					mapping.setBlockID(null);
					mapping.setBlockName(null);
					mapping.setVillageID(null);
					mapping.setVillageName(null);
					mapping.setVillageidDb(null);
					mapping.setVillageNameDb(null);
				}
				mappedRoles.add(mapping);
			}
		}
		return mappedRoles;
		//return getData;
	}

	@Override
	public ArrayList<V_Userservicerolemapping> getMappedRole(String name, Integer userID) {
		// ArrayList<V_Userservicerolemapping> getData;
		if (name.isEmpty() || name == null) {
			ArrayList<V_Userservicerolemapping> getData = v_UserservicerolemappingRepo.getDataByUserID(userID);
			return getData;
		} else if (userID <= 0) {

			ArrayList<V_Userservicerolemapping> getData = v_UserservicerolemappingRepo.getDataByName(name);
			return getData;
		} else

			throw new DataNotFound("Please Enter Name of UserId");

	}

	@Override
	public ArrayList<M_UserLangMapping> searchMappedLangugeByUserId(Integer userID) {
		if (!(userID == 0)) {
			ArrayList<M_UserLangMapping> data = m_UserLangMappingRepo.getmappedlanguageData(userID);
			return data;
		} else
			throw new DataNotFound("Please Enter Correct  UserId");
	}

	@Override
	public ArrayList<V_Showuser> getcompleteUserDetails() {

		ArrayList<V_Showuser> data = (ArrayList<V_Showuser>) v_ShowuserRepo.getAdminDetails();

		return data;
	}

	@Override
	public ArrayList<M_Religion> getAllReligion() {
		ArrayList<M_Religion> data = (ArrayList<M_Religion>) m_ReligionRepo.findAll();
		return data;
	}

	@Override
	public ArrayList<M_Community> getAllCommunity() {
		ArrayList<M_Community> data = (ArrayList<M_Community>) m_CommunityRepo.findAll();
		return data;
	}

	@Override
	public ArrayList<V_Showuser> getEmployeeDetails4(Integer serviceProviderID) {

		return v_ShowuserRepo.EmployeeDetails4(serviceProviderID);
	}

	@Override
	public ArrayList<M_User1> getEmployeeByDesiganationID(Integer designationID, Integer serviceProviderID) {
		ArrayList<M_User1> getEmpByDesiganation = employeeMasterRepoo.getempByDesiganation(designationID,
				serviceProviderID);
		return getEmpByDesiganation;
	}

	@Override
	public void expireAuth(M_User1 user, String auth) {
		// TODO Auto-generated method stub
		try {
			ArrayList<V_Userservicerolemapping> getData = v_UserservicerolemappingRepo
					.getDataByUserID(user.getUserID());
			if (getData.size() > 0) {
				JsonParser parser = new JsonParser();

				String result;

				HashMap<String, Object> header = new HashMap<>();

				HashMap<String, String> objectmap = new HashMap<>();
				// { "userName": "104ro", "providerServiceMapID": 1247 }

				objectmap.put("userName", user.getUserName());
				objectmap.put("providerServiceMapID", getData.get(0).getProviderServiceMapID().toString());

				if (auth != null) {
					header.put("Authorization", auth);
				}
				result = httpUtils.post(
						ConfigProperties.getPropertyByName("common-forcelogout").replace(COMMON_BASE_URL, common_url),
						objectmap.toString(), header);

			}
		} catch (Exception e) {

		}

	}

	@Override
	public ArrayList<UserRole> getUserRoleTM(M_UserservicerolemappingForRoleProviderAdmin getRoleData) {
		// TODO Auto-generated method stub

		ArrayList<UserRole> userRole = new ArrayList<UserRole>();
		ArrayList<Object[]> arrayobject = userservicerolemappingForRoleProviderAdminRepo
				.getroleofuserTM(getRoleData.getUserID(), getRoleData.getProviderServiceMapID());
		if (arrayobject.size() > 0) {
			for (Object[] obj : arrayobject) {
				userRole.add(new UserRole((Integer) obj[0], (Integer) obj[1], (String) obj[2], (Boolean) obj[3],
						(Integer) obj[4], (String) obj[5], (Boolean) obj[6]));
			}
		}

		return userRole;
	}

	@Override
	public M_UserServiceRoleMapping2 deleteuserrolemapTM(M_UserServiceRoleMapping2 uSRMappingID) throws Exception {
		M_UserServiceRoleMapping2 dataout = new M_UserServiceRoleMapping2();
		if (uSRMappingID.getuSRMappingID() == null) {
			throw new Exception("Invalid User Map ID");
		}
		if (uSRMappingID.getDeleted() == null) {
			throw new Exception("Invalid activation flag");
		}

		M_UserServiceRoleMapping2 data = employeeMasterRepo.findOne(uSRMappingID.getuSRMappingID());

		M_Providerservicemapping_Blocking psm = mProviderservicemappingBlockingRepo
				.findOne(data.getProviderServiceMapID());

		if (psm.getServiceID() == null || psm.getServiceID() != 4) {
			throw new Exception("API valid only for TM service Line");
		}

		if (!uSRMappingID.getDeleted()) {
			String rolecurrent = (String) userservicerolemappingForRoleProviderAdminRepo
					.getroleofuserTMOne(uSRMappingID.getuSRMappingID());
			System.out.println(rolecurrent);
			ArrayList<UserRole> alluserRole = new ArrayList<UserRole>();
			ArrayList<Object[]> arrayobject = userservicerolemappingForRoleProviderAdminRepo
					.getroleofuserTM(data.getUserID(), data.getProviderServiceMapID());
			if (arrayobject.size() > 0) {
				for (Object[] obj : arrayobject) {
					alluserRole.add(new UserRole((Integer) obj[0], (Integer) obj[1], (String) obj[2], (Boolean) obj[3],
							(Integer) obj[4], (String) obj[5], (Boolean) obj[6]));
				}
			}
			if (rolecurrent.equalsIgnoreCase("TC Specialist") || rolecurrent.equalsIgnoreCase("Supervisor")) {
				if (alluserRole.size() != 0) {
					throw new Exception("Cannot Activate. Restricted Roles already mapped");

				}
				// else{
				// data.setDeleted(true);
				// dataout=employeeMasterRepo.save(data);
				// }
			} else {

				for (UserRole usrole : alluserRole) {
					String rolemapped = usrole.getScreenName();
					if (rolemapped.equalsIgnoreCase("TC Specialist") || rolemapped.equalsIgnoreCase("Supervisor")) {
						throw new Exception("Cannot Activate. Restricted Roles already mapped");
					}
					if (rolecurrent.equalsIgnoreCase(rolemapped)) {
						throw new Exception("Role with Same Screen already Mapped");
					}
				}

				// data.setDeleted(true);
				// dataout=employeeMasterRepo.save(data);

			}
		}
		// else{
		// data.setDeleted(false);
		// dataout=employeeMasterRepo.save(data);
		// }
		data.setDeleted(uSRMappingID.getDeleted());
		dataout = employeeMasterRepo.save(data);
		dataout.setEmployeeMaster(null);
		return dataout;
	}

}
