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
package com.iemr.admin.controller.rolemaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.rolemaster.M_Role;
import com.iemr.admin.data.rolemaster.M_Role104;
import com.iemr.admin.data.rolemaster.M_Screen;
import com.iemr.admin.data.rolemaster.M_UserservicerolemappingForRoleProviderAdmin;
import com.iemr.admin.data.rolemaster.RoleScreenMapping;
import com.iemr.admin.data.rolemaster.ServiceMaster;
import com.iemr.admin.data.rolemaster.StateServiceMapping;
import com.iemr.admin.repository.rolemaster.RoleScreenMappingRepo;
import com.iemr.admin.service.rolemaster.Role_MasterInter;
import com.iemr.admin.to.rolemaster.RoleMasterTO;
import com.iemr.admin.to.rolemaster.RoleMasterTO1;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

@RestController
public class RoleMaster_Controller {
	@Autowired
	RoleScreenMappingRepo roleScreenMappingRepo;

	/**
	 * creating Logger Object using logger variable...
	 */

	private Logger logger = LoggerFactory.getLogger(RoleMaster_Controller.class);

	/**
	 * creating inputMapper Object using inputMapper variable...
	 */

	private InputMapper inputMapper = new InputMapper();

	@Autowired
	private Role_MasterInter roleMasterInter;
	private Integer serviceMapid;

	@CrossOrigin()
	@RequestMapping(value = "m/role/state", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String searchRole(@RequestBody String stateserviceMapping1) {
		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();

		logger.debug(" get state request is" + stateserviceMapping1);
		try {
			/**
			 * craeting input request for Finding state using serviceproviderId...
			 */
			StateServiceMapping stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping.class);
			logger.debug(" converted json to gson and  request is" + stateserviceMapping);
			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();

			/**
			 * sending stateid and getting the responce using data...
			 */

			ArrayList<StateServiceMapping> data = roleMasterInter
					.getStateByServiceProviderId(stateserviceMapping.getServiceProviderID());
			logger.debug(
					"for getting state calling StateByServiceProviderId " + stateserviceMapping.getServiceProviderID());

			/**
			 * creating the response & setting the response...
			 */
			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("getting state failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}

		/**
		 * sending the response...
		 */
		logger.debug("getting state response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/service", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getService(@RequestBody String stateserviceMapping1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" get state and serviceline request is" + stateserviceMapping1);
		try {

			/**
			 * craeting input request for Finding service using sateteId&
			 * serviceProviderId...
			 */
			StateServiceMapping stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping.class);
			logger.debug("converting json to gson" + stateserviceMapping);
			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();

			/**
			 * sending serviceproviderId & stateid and getting the responce using data...
			 */

			ArrayList<StateServiceMapping> data = roleMasterInter.getServiceByServiceProviderIdAndStateId(

					stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID());
			logger.debug("calling method for getting serviceid and providerServiceid"
					+ stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID());

			/**
			 * creating the response and setting the response...
			 */
			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("getting serviceLine and spmapid failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("getting service and spmapid response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/serviceNew", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getServiceByProviderId(@RequestBody String stateserviceMapping1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" get state and serviceline request is" + stateserviceMapping1);
		try {

			/**
			 * creating the object of InputMapper and passing the passing the incoming
			 * request inside the input mapper mehod .
			 */
			M_UserservicerolemappingForRoleProviderAdmin stateserviceMapping = InputMapper.gson()
					.fromJson(stateserviceMapping1, M_UserservicerolemappingForRoleProviderAdmin.class);
			logger.debug("converting json to gson" + stateserviceMapping);
			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();

			/**
			 * calling the getServiceByServiceProviderIds Method and storing that response
			 * inside array list variable...
			 */

			ArrayList<M_UserservicerolemappingForRoleProviderAdmin> data = roleMasterInter
					.getServiceByServiceProviderIds(

							stateserviceMapping.getUserID());
			logger.debug(
					"calling method for getting serviceid and providerServiceid" + stateserviceMapping.getUserID());

			/**
			 * creating the response using Output Mapper Object calling using
			 * setResponseMethod....
			 */
			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("getting serviceLine and spmapid failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("getting service and spmapid response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/stateNew", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getStateByProviderIdAndServiceID(@RequestBody String stateserviceMapping1) {
		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();

		logger.debug(" get state request is" + stateserviceMapping1);
		try {
			/**
			 * craeting input request for Finding state using serviceproviderId...
			 */
			M_UserservicerolemappingForRoleProviderAdmin stateserviceMapping = InputMapper.gson()
					.fromJson(stateserviceMapping1, M_UserservicerolemappingForRoleProviderAdmin.class);

			StateServiceMapping stateserviceMapping2 = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping.class);
			RoleMasterTO1 stateserviceMapping3 = InputMapper.gson().fromJson(stateserviceMapping1, RoleMasterTO1.class);

			logger.debug(" converted json to gson and  request is" + stateserviceMapping);
			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();

			/**
			 * sending stateid and getting the responce using data...
			 */

			ArrayList<M_UserservicerolemappingForRoleProviderAdmin> data = roleMasterInter
					.getStateByServiceProviderIdAndServiceLines(stateserviceMapping.getUserID(),
							stateserviceMapping2.getServiceID(), stateserviceMapping3.getIsNational());
			logger.debug("for getting state calling StateByServiceProviderId " + stateserviceMapping.getUserID());

			/**
			 * creating the response & setting the response...
			 */
			response.setResponse(data.toString());

		} catch (Exception e) {

			logger.error("getting state failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}

		/**
		 * sending the response...
		 */
		logger.debug("getting state response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/search", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllRole(@RequestBody String stateserviceMapping1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" getting role Search request is" + stateserviceMapping1);

		try {

			/**
			 * craeting input request for Finding state using serviceproviderId...
			 */

			StateServiceMapping stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping.class);
			ServiceMaster stateserviceMapping3 = InputMapper.gson().fromJson(stateserviceMapping1, ServiceMaster.class);

			// ArrayList<StateServiceMapping> data1;

			// ServiceMaster
			logger.debug("converting into json to gson" + stateserviceMapping1);

			/**
			 * sending serviceproviderId & stateid & serviceId and getting the responce
			 * using data...
			 */

			ArrayList<StateServiceMapping> data = roleMasterInter.getAllByMapId(
					stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID(),
					stateserviceMapping.getServiceID(), stateserviceMapping3.getIsNational());
			logger.debug("calling mehod for getting mapid's" + stateserviceMapping.getServiceProviderID(),
					stateserviceMapping.getStateID(), stateserviceMapping.getServiceID());

			// logger.debug("getting response with serviceid and Spm mapId " +
			// data1 );

			/**
			 * creating tempSerStatMapID for storing the data into temp variable ...
			 */

			// String proSerStatMapID = "( ";
			int tempProSerStatMapID = 0;
			if (data != null && data.size() > 0) {

				/**
				 * iterating the data using for each loop...
				 */

				for (StateServiceMapping obj : data) {
					tempProSerStatMapID = obj.getProviderServiceMapID();
				}
			} else {
			}

			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();
			ArrayList<M_Role> rolesData = roleMasterInter.getProStateServRoles(tempProSerStatMapID);

			logger.info("" + serviceMapid);

			/**
			 * creating the response and setting the response...
			 */
			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("Search Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("role response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/searchNew", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllRoleNew(@RequestBody String stateserviceMapping1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" getting role Search request is" + stateserviceMapping1);

		try {

			/**
			 * craeting input request for Finding state using serviceproviderId...
			 */

			StateServiceMapping stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping.class);
			ServiceMaster stateserviceMapping3 = InputMapper.gson().fromJson(stateserviceMapping1, ServiceMaster.class);

			/*
			 * //ArrayList<StateServiceMapping> data1;
			 * 
			 * //ServiceMaster logger.debug("converting into json to gson" +
			 * stateserviceMapping1);
			 * 
			 *//**
				 * sending serviceproviderId & stateid & serviceId and getting the responce
				 * using data...
				 */
			/*
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * ArrayList<StateServiceMapping> data= roleMasterInter.getAllByMapId(
			 * stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID(),
			 * stateserviceMapping.getServiceID(),stateserviceMapping3. getIsNational());
			 * logger.debug( "calling mehod for getting mapid's" +
			 * stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID(),
			 * stateserviceMapping.getServiceID());
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * //logger.debug("getting response with serviceid and Spm mapId " + data1 );
			 * 
			 *//**
				 * creating tempSerStatMapID for storing the data into temp variable ...
				 */
			/*
			 * 
			 * // String proSerStatMapID = "( "; int tempProSerStatMapID = 0; if (data !=
			 * null && data.size() > 0) {
			 * 
			 *//**
				 * iterating the data using for each loop...
				 *//*
					 * 
					 * for (StateServiceMapping obj : data) { tempProSerStatMapID =
					 * obj.getProviderServiceMapID(); } } else { }
					 * 
					 * Map<String, Object> resMap = null; List<Map<String, Object>> resList = new
					 * ArrayList<>();
					 */
			ArrayList<M_Role> rolesData = roleMasterInter
					.getProStateServRoles(stateserviceMapping.getProviderServiceMapID());

			logger.info("" + serviceMapid);

			/**
			 * creating the response and setting the response...
			 */
			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("Search Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("role response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/searchV1", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllRoles(@RequestBody String stateserviceMapping1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" getting role Search request is " + stateserviceMapping1);

		try {

			/**
			 * craeting input request for Finding state using serviceproviderId...
			 */

			M_Role roleSearch = InputMapper.gson().fromJson(stateserviceMapping1, M_Role.class);
			ArrayList<M_Role> rolesData = roleMasterInter.getProStateServRolesV1(roleSearch.getProviderServiceMapID());
			logger.info("" + serviceMapid);
			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("Search Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("role response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/addRole", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String saveRole(@RequestBody String mRole1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug("getAll Role request is " + mRole1);
		try {

			/**
			 * creating input for RoleMaster to add Role...
			 */

			M_Role[] mRoles = InputMapper.gson().fromJson(mRole1, M_Role[].class);
			List<M_Role> mRole = Arrays.asList(mRoles);
			logger.debug("getting role request is " + mRole1);

			// for(M_Role data:mRoles)

			RoleMasterTO[] mRoles2 = InputMapper.gson().fromJson(mRole1, RoleMasterTO[].class);
			List<RoleMasterTO> rsmList = Arrays.asList(mRoles2);

			/**
			 * sending input in the form of list and taking response also in list using m
			 * role list...
			 */

			List<M_Role> mrolelist = roleMasterInter.addRole(mRole);
			tempRajeev(mrolelist, rsmList);

			/**
			 * creating the response and setting the response...
			 */
			response.setResponse(mrolelist.toString());

		} catch (Exception e) {

			logger.error("Add Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}

		/**
		 * sending the response...
		 */
		logger.debug("Role response is " + response);
		return response.toString();
	}

	/**
	 * Rajeev Temp code....
	 */
	public void tempRajeev(List<M_Role> mrolelist, List<RoleMasterTO> rsmList) {
		int x = 0;
		RoleScreenMapping rsmOBJ;
		List<RoleScreenMapping> rsmList1 = new ArrayList<>();
		if (mrolelist.size() == rsmList.size()) {
			// for (int i = 0; i < mrolelist.size(); i++)
			for (RoleMasterTO rt : rsmList) {
				for (int k = 0; k < rt.getScreenID().length; k++) {
					Integer[] screenId = rt.getScreenID();
					rsmOBJ = new RoleScreenMapping();
					rsmOBJ.setRoleID(mrolelist.get(x).getRoleID());
					rsmOBJ.setScreenID(screenId[k]);
					rsmOBJ.setCreatedBy(mrolelist.get(x).getCreatedBy());
					rsmOBJ.setProviderServiceMapID(mrolelist.get(x).getProviderServiceMapID());

					rsmList1.add(rsmOBJ);

				}

				x++;
			}
			List<RoleScreenMapping> rsmList2 = (List<RoleScreenMapping>) roleScreenMappingRepo.save(rsmList1);

		} else {

		}
	}
	// end Of Rajeev temp code....

	@CrossOrigin()
	@RequestMapping(value = "m/role/editRole", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String editRole(@RequestBody String mRole1) {

		/**
		 * creating output response Object using response vari able...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" get edit request is" + mRole1);
		try {
			/**
			 * craeting input for RoleMaster to edit Role...
			 */

			M_Role mRole = InputMapper.gson().fromJson(mRole1, M_Role.class);
			logger.debug(" converting into json to gson" + mRole1);

			RoleScreenMapping mRoles2 = InputMapper.gson().fromJson(mRole1, RoleScreenMapping.class);

			/**
			 * sending input roleid for finding all the data for that particular id and
			 * storing into the editdata ...
			 */

			M_Role editdata = roleMasterInter.getRoleByRoleId(mRole.getRoleID());
			logger.debug(" calling method for getting role based on role id" + mRole.getRoleID());

			/**
			 * setting the data into editdata and seving into the table..
			 */

			editdata.setRoleName(mRole.getRoleName());
			editdata.setRoleDesc(mRole.getRoleDesc());
			// editdata.setProviderServiceMapID(mRole.getProviderServiceMapID());
			// editdata.setScreenID(mRole.getScreenID());

			/**
			 * getting the current data which is save earlier and storing into role
			 * variable..
			 */

			M_Role role = roleMasterInter.modifydata(editdata);

			String screenMapping = roleMasterInter.settingScreenId(mRoles2.getsRSMappingID(), mRoles2.getScreenID());

			/**
			 * creating the response and setting the response...
			 */

			response.setResponse(screenMapping.toString());

		} catch (Exception e) {
			logger.error("Edit Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}

		/**
		 * sending the response...
		 */

		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/deleteRole", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String deleteRole(@RequestBody String mRole1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		try {

			/**
			 * craeting input for RoleMaster to delete Role...
			 */

			M_Role mRole = InputMapper.gson().fromJson(mRole1, M_Role.class);
			/**
			 * sending input roleid for finding all the data for that particular id and
			 * storing into the deleteddata ...
			 */

			M_Role deleteData = roleMasterInter.getRoleByRoleId(mRole.getRoleID());
			/**
			 * setting the data into deletedata and seving into the table..
			 */
			deleteData.setDeleted(mRole.getDeleted());

			/**
			 * getting the current data which is save earlier and storing into mrole
			 * variable..
			 */

			String mrole = roleMasterInter.deletedata(deleteData);

			/**
			 * creating the response and setting the response...
			 */

			response.setResponse(mrole.toString());

		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		logger.info("response" + response);

		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/searchFeature", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String searchFeature(@RequestBody String Feature) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		try {

			/**
			 * craeting input for RoleMaster to delete Role...
			 */

			M_Screen mRole = InputMapper.gson().fromJson(Feature, M_Screen.class);
			/**
			 * sending input roleid for finding all the data for that particular id and
			 * storing into the deleteddata ...
			 */

			ArrayList<M_Screen> feature = roleMasterInter.getAllFeature(mRole.getServiceID());
			/**
			 * setting the data into deletedata and seving into the table..
			 */
			;

			/**
			 * getting the current data which is save earlier and storing into mrole
			 * variable..
			 */

			/**
			 * creating the response and setting the response...
			 */

			response.setResponse(feature.toString());

		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		logger.info("response" + response);

		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/deleteFeature", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String deleteFeature(@RequestBody String mRole1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		try {

			/**
			 * craeting input for RoleMaster to delete Role...
			 */

			RoleScreenMapping mRole = InputMapper.gson().fromJson(mRole1, RoleScreenMapping.class);
			/**
			 * sending input roleid for finding all the data for that particular id and
			 * storing into the deleteddata ...
			 */

			M_Role deleteData = roleMasterInter.getRoleByRoleId(mRole.getRoleID());
			/**
			 * setting the data into deletedata and seving into the table..
			 */
			deleteData.setDeleted(true);

			/**
			 * getting the current data which is save earlier and storing into mrole
			 * variable..
			 */

			String mrole = roleMasterInter.deletedata(deleteData);

			/**
			 * creating the response and setting the response...
			 */

			response.setResponse(mrole.toString());

		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		logger.info("response" + response);

		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/search1", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String getAllRole1(@RequestBody String stateserviceMapping1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" getting role Search request is" + stateserviceMapping1);

		try {

			/**
			 * craeting input request for Finding state using serviceproviderId...
			 */

			StateServiceMapping stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping.class);
			ServiceMaster stateserviceMapping3 = InputMapper.gson().fromJson(stateserviceMapping1, ServiceMaster.class);
			logger.debug("converting into json to gson" + stateserviceMapping1);

			/**
			 * sending serviceproviderId & stateid & serviceId and getting the responce
			 * using data...
			 */

			ArrayList<StateServiceMapping> data1 = roleMasterInter.getAllByMapId(
					stateserviceMapping.getServiceProviderID(), stateserviceMapping.getStateID(),
					stateserviceMapping.getServiceID(), stateserviceMapping3.getIsNational());
			logger.debug("calling mehod for getting mapid's" + stateserviceMapping.getServiceProviderID(),
					stateserviceMapping.getStateID(), stateserviceMapping.getServiceID());

			logger.debug("getting response with serviceid and Spm mapId " + data1);

			/**
			 * creating tempSerStatMapID for storing the data into temp variable ...
			 */

			// String proSerStatMapID = "( ";
			int tempProSerStatMapID = 0;
			if (data1 != null && data1.size() > 0) {

				/**
				 * iterating the data using for each loop...
				 */

				for (StateServiceMapping obj : data1) {
					tempProSerStatMapID = obj.getProviderServiceMapID();
				}
			} else {
			}

			Map<String, Object> resMap = null;
			List<Map<String, Object>> resList = new ArrayList<>();
			ArrayList<M_Role> rolesData = roleMasterInter.getProStateServRoles1(tempProSerStatMapID);

			logger.info("" + serviceMapid);

			/**
			 * creating the response and setting the response...
			 */
			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("Search Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("role response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/mapExterafeature", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String editRolefeature(@RequestBody String editRolefeature) {

		/**
		 * creating output response Object using response vari able...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" get edit request is" + editRolefeature);
		try {
			/**
			 * craeting input for RoleMaster to edit Role...
			 */

			/*
			 * M_Role mRole = InputMapper.gson().fromJson(editRolefeature, M_Role.class);
			 * logger.debug( " converting into json to gson" + editRolefeature);
			 */

			RoleScreenMapping[] mRoles2 = InputMapper.gson().fromJson(editRolefeature, RoleScreenMapping[].class);

			List<RoleScreenMapping> mRoles3 = Arrays.asList(mRoles2);

			List<RoleScreenMapping> data = roleMasterInter.mapfeature(mRoles3);

			/**
			 * sending input roleid for finding all the data for that particular id and
			 * storing into the editdata ...
			 */

			// M_Role editdata =
			// roleMasterInter.getRoleByRoleId(mRole.getRoleID());
			// logger.debug(" calling method for getting role based on role id"
			// +mRole.getRoleID());

			/**
			 * setting the data into editdata and seving into the table..
			 */

			// editdata.setRoleName(mRole.getRoleName());
			// editdata.setRoleDesc(mRole.getRoleDesc());
			// editdata.setProviderServiceMapID(mRole.getProviderServiceMapID());
			// editdata.setScreenID(mRole.getScreenID());

			/**
			 * getting the current data which is save earlier and storing into role
			 * variable..
			 */

			// M_Role role = roleMasterInter.modifydata(editdata);

			// String
			// screenMapping=roleMasterInter.settingScreenId(mRoles2.getsRSMappingID(),mRoles2.getScreenID());

			/**
			 * creating the response and setting the response...
			 */

			response.setResponse(data.toString());

		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}

		/**
		 * sending the response...
		 */

		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "/searchRoleTM", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String searchRoleTM(@RequestBody String stateserviceMapping1) {
		OutputResponse response = new OutputResponse();
		logger.debug(" get edit request is" + stateserviceMapping1);
		try {
			M_Role roleSearch = InputMapper.gson().fromJson(stateserviceMapping1, M_Role.class);

			List<M_Role> mRoles3 = roleMasterInter.getRoleMasterTM(roleSearch.getProviderServiceMapID());

			response.setResponse(mRoles3.toString());

		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}

		/**
		 * sending the response...
		 */

		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/search/active", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String getAllRoleActive(@RequestBody String stateserviceMapping1) {

		/**
		 * creating output response Object using response variable...
		 */

		OutputResponse response = new OutputResponse();
		logger.debug(" getting role Search request is" + stateserviceMapping1);

		try {

			/**
			 * craeting input request for Finding state using serviceproviderId...
			 */

			StateServiceMapping stateserviceMapping = InputMapper.gson().fromJson(stateserviceMapping1,
					StateServiceMapping.class);

			ArrayList<M_Role> rolesData = roleMasterInter
					.getProStateServRolesActive(stateserviceMapping.getProviderServiceMapID());

			logger.info("" + serviceMapid);

			/**
			 * creating the response and setting the response...
			 */
			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("Search Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("role response is " + response);
		return response.toString();

	}

	@CrossOrigin()
	@RequestMapping(value = "m/role/configWrap", headers = "Authorization", method = {
			RequestMethod.POST }, produces = { "application/json" })
	public String configWrapUptime(@RequestBody M_Role role) {

		OutputResponse response = new OutputResponse();
		logger.debug(" getting role Search request is" + role);

		try {
			
			M_Role rolesData = roleMasterInter.configWrapUpTime(role);

			logger.info("" + rolesData);

			/**
			 * creating the response and setting the response...
			 */
			response.setResponse(rolesData.toString());

		} catch (Exception e) {

			logger.error("Search Role failed with exception " + e.getMessage(), e);
			//e.printStackTrace();
			logger.error("Unexpected error:" , e);
			response.setError(e);
		}
		/**
		 * sending the response...
		 */

		logger.debug("role response is " + response);
		return response.toString();
	}

}
