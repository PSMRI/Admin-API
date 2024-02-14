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
package com.iemr.admin.controller.labmodule;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.sevice.labmodule.MastersCreationServiceImpl;
import com.iemr.admin.sevice.labmodule.MastersFetchingServiceImpl;
import com.iemr.admin.sevice.labmodule.MastersMappingServiceImpl;
import com.iemr.admin.sevice.labmodule.MastersStatusUpdateImpl;
import com.iemr.admin.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;



/***
 * @category master creation for lab module
 */

@RestController
@CrossOrigin
@RequestMapping(value = "labModule", headers = "Authorization")
public class LabModuleController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	private MastersCreationServiceImpl mastersCreationServiceImpl;
	private MastersMappingServiceImpl mastersMappingServiceImpl;
	private MastersFetchingServiceImpl mastersFetchingServiceImpl;
	private MastersStatusUpdateImpl mastersStatusUpdateImpl;

	@Autowired
	public void setMastersStatusUpdateImpl(MastersStatusUpdateImpl mastersStatusUpdateImpl) {
		this.mastersStatusUpdateImpl = mastersStatusUpdateImpl;
	}

	@Autowired
	public void setMastersMappingServiceImpl(MastersMappingServiceImpl mastersMappingServiceImpl) {
		this.mastersMappingServiceImpl = mastersMappingServiceImpl;
	}

	@Autowired
	public void setMastersCreationServiceImpl(MastersCreationServiceImpl mastersCreationServiceImpl) {
		this.mastersCreationServiceImpl = mastersCreationServiceImpl;
	}
	
	@Autowired
	public void setMastersFetchingServiceImpl(MastersFetchingServiceImpl mastersFetchingServiceImpl) {
		this.mastersFetchingServiceImpl = mastersFetchingServiceImpl;
	}


	@CrossOrigin
	@Operation(summary = "Create procedure master by provider admin")
	@RequestMapping(value = { "/createProcedureMaster" }, method = { RequestMethod.POST })
	public String createProcedureMaster(@Param(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			String s = mastersCreationServiceImpl.createProcedureMaster(requestOBJ);
			logger.info(requestOBJ);
			System.out.println(requestOBJ);
			if (s != null) {
				response.setResponse(s);

			} else {
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Create component master by provider admin")
	@RequestMapping(value = { "/createComponentMaster" }, method = { RequestMethod.POST })
	public String createComponentMaster(@Param(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			String s = mastersCreationServiceImpl.createComponentMaster(requestOBJ);
			if (s != null) {
				response.setResponse(s);

			} else {
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Create procedure - component mapping by provider admin")
	@RequestMapping(value = { "/createProcedureComponentMapping" }, method = { RequestMethod.POST })
	public String createProcedureComponentMapping(@Param(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info(requestOBJ);
			String s = mastersMappingServiceImpl.createProcedureComponentMapping(requestOBJ);
			if (s != null) {
				if (s.equalsIgnoreCase("1"))
					response.setError(5002, "Invalid request.");
				else
					response.setResponse(s);

			} else {
				response.setError(5002, "Error while saving the data");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Fetch procedure master for provider-service-map-id")
	@RequestMapping(value = { "/fetchProcedureMaster/{psmID}" }, method = { RequestMethod.GET })
	public String fetchProcedureMaster(@PathVariable("psmID") Integer psmID) {
		OutputResponse response = new OutputResponse();
		try {
			if (psmID != null & psmID > 0) {
				String s = mastersFetchingServiceImpl.getProcedureMaster(psmID);
				response.setResponse(s);
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Fetch component master for provider-service-map-id")
	@RequestMapping(value = { "/fetchComponentMaster/{psmID}" }, method = { RequestMethod.GET })
	public String fetchComponentMaster(@PathVariable("psmID") Integer psmID) {
		OutputResponse response = new OutputResponse();
		try {
			if (psmID != null & psmID > 0) {
				String s = mastersFetchingServiceImpl.getComponentMaster(psmID);
				response.setResponse(s);
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Fetch procedure master for provider-service-map-id and deleted is false")
	@RequestMapping(value = { "/fetchProcedureMasterDelFalse/{psmID}" }, method = { RequestMethod.GET })
	public String fetchProcedureMasterDelFalse(@PathVariable("psmID") Integer psmID) {
		OutputResponse response = new OutputResponse();
		try {
			if (psmID != null & psmID > 0) {
				String s = mastersFetchingServiceImpl.getProcedureMasterDelFalse(psmID);
				response.setResponse(s);
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Fetch component master for provider-service-map-id and deleted false")
	@RequestMapping(value = { "/fetchComponentMasterDelFalse/{psmID}" }, method = { RequestMethod.GET })
	public String fetchComponentMasterDelFalse(@PathVariable("psmID") Integer psmID) {
		OutputResponse response = new OutputResponse();
		try {
			if (psmID != null & psmID > 0) {
				String s = mastersFetchingServiceImpl.getComponentMasterDelFalse(psmID);
				response.setResponse(s);
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Fetch procedure component mapping list for provider-service-map-id and deleted false")
	@RequestMapping(value = { "/fetchprocCompMappingDelFalse/{psmID}" }, method = { RequestMethod.GET })
	public String fetchProcCompMappingDelFalse(@PathVariable("psmID") Integer psmID) {
		OutputResponse response = new OutputResponse();
		try {
			if (psmID != null & psmID > 0) {
				String s = mastersFetchingServiceImpl.getProcCompMappingDelFalse(psmID);
				response.setResponse(s);
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Fetch procedure component mapping list for particular procedure id")
	@RequestMapping(value = { "/fetchProcCompMappingForSingleProcedure/{pID}" }, method = { RequestMethod.GET })
	public String fetchProcCompMappingForSingleProcedure(@PathVariable("pID") Integer pID) {
		OutputResponse response = new OutputResponse();
		try {
			if (pID != null & pID > 0) {
				String s = mastersFetchingServiceImpl.getProcCompMappingForProcedureID(pID);
				response.setResponse(s);
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Fetch  component details  for particular component id")
	@RequestMapping(value = { "/fetchComponentDetailsForComponentID/{cID}" }, method = { RequestMethod.GET })
	public String fetchComponentDetailsForComponentID(@PathVariable("cID") Integer cID) {
		OutputResponse response = new OutputResponse();
		try {
			if (cID != null & cID > 0) {
				String s = mastersFetchingServiceImpl.getComponentDetailsForComponentID(cID);
				if (s != null) {
					response.setResponse(s);
				} else {
					response.setResponse("Component Details not found in Database.");
				}
			} else {
				response.setError(5002, "Invalid Request ");
			}
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Update procedure status for enable or disable")
	@RequestMapping(value = { "/updateProcedureStatus" }, method = { RequestMethod.POST })
	public String updateProcedureStatus(@Param(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject jsnOBJ = new JSONObject(requestOBJ);
			if (jsnOBJ != null && jsnOBJ.has("procedureID") && jsnOBJ.getInt("procedureID") > 0
					&& jsnOBJ.has("deleted")) {
				String s = mastersStatusUpdateImpl.updateProcedureStatus(jsnOBJ.getInt("procedureID"),
						jsnOBJ.getBoolean("deleted"));
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5002, "Failed to update the status");
			} else {
				response.setError(5002, "invalid request");
			}

		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Update component status for enable or disable")
	@RequestMapping(value = { "/updateComponentStatus" }, method = { RequestMethod.POST })
	public String updateComponentStatus(@Param(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject jsnOBJ = new JSONObject(requestOBJ);
			if (jsnOBJ != null && jsnOBJ.has("componentID") && jsnOBJ.getInt("componentID") > 0
					&& jsnOBJ.has("deleted")) {
				String s = mastersStatusUpdateImpl.updateComponentStatus(jsnOBJ.getInt("componentID"),
						jsnOBJ.getBoolean("deleted"));
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5002, "Failed to update the status");
			} else {
				response.setError(5002, "invalid request");
			}

		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin
	@Operation(summary = "Update procedure master for a particular procedure")
	@RequestMapping(value = { "/updateProcedureMaster" }, method = { RequestMethod.POST })
	public String updateProcedureMaster(@Param(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {

			String s = mastersStatusUpdateImpl.updateProcedureMaster(requestOBJ);
			if (s != null) {
				response.setResponse(s);
			} else {
				response.setError(5002, "Failed to update procedure details");
			}

		} catch (Exception e) {
			response.setError(e);
		}

		return response.toString();

	}

	@CrossOrigin
	@Operation(summary = "Update component master for a particular Component")
	@RequestMapping(value = { "/updateComponentMaster" }, method = { RequestMethod.POST })
	public String updateComponentMaster(@Param(value = "{}") @RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {

			String s = mastersStatusUpdateImpl.updateComponentMaster(requestOBJ);
			if (s != null) {
				response.setResponse(s);
			} else {
				response.setError(5002, "Failed to update component details");
			}

		} catch (Exception e) {
			response.setError(e);
		}

		return response.toString();

	}

}
