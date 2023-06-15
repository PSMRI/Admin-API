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
package com.iemr.admin.service.blocking;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.blocking.M_Providerservicemapping_Blocking;
import com.iemr.admin.data.blocking.M_Serviceprovider_Blocking;
import com.iemr.admin.data.blocking.M_Status1;
import com.iemr.admin.data.blocking.T_Providerservicemappingdetail;
import com.iemr.admin.data.blocking.T_Serviceproviderdetail;
import com.iemr.admin.data.blocking.T_Userdetail;
import com.iemr.admin.data.blocking.UserForBlocking;
import com.iemr.admin.data.blocking.V_Showproviderservicemapping;
import com.iemr.admin.repo.blocking.MProviderservicemappingBlockingRepo;
import com.iemr.admin.repo.blocking.MStatusRepo;
import com.iemr.admin.repo.blocking.T_ProviderservicemappingdetailRepo;
import com.iemr.admin.repo.blocking.T_ServiceproviderdetailRepo;
import com.iemr.admin.repo.blocking.T_UserDetailRepo;
import com.iemr.admin.repo.blocking.UserBlockingRepo;
import com.iemr.admin.repo.blocking.V_ShowproviderservicemappingRepo;
import com.iemr.admin.service.user.EncryptUserPassword;

@Service
public class Blocking_Service implements BlockingInter
{

	@Autowired
	private V_ShowproviderservicemappingRepo v_ShowproviderservicemappingRepo;
	@Autowired
	private T_UserDetailRepo t_UserDetailRepo;
	@Autowired
	private MStatusRepo mStatusRepo;

	@Autowired
	private UserBlockingRepo userBlockingRepo;

	@Autowired
	private T_ProviderservicemappingdetailRepo t_ProviderservicemappingdetailRepo;

	@Autowired
	private MProviderservicemappingBlockingRepo mProviderservicemappingBlockingRepo;

	@Autowired
	private T_ServiceproviderdetailRepo t_ServiceproviderdetailRepo;

	@Autowired
	private com.iemr.admin.repo.blocking.MServiceproviderBlockingRepo mServiceproviderBlockingRepo;

	@Autowired
	private EncryptUserPassword encryptUserPassword;

	@Override
	public M_Serviceprovider_Blocking getProviderDetailsById(Integer serviceProviderID)
	{

		return mServiceproviderBlockingRepo.getProviderDetailsByID(serviceProviderID);
	}

	@Override
	public M_Serviceprovider_Blocking blockServiceProvider(M_Serviceprovider_Blocking getProviderDetails)
	{
		M_Serviceprovider_Blocking data = mServiceproviderBlockingRepo.save(getProviderDetails);
		return data;
	}

	@Override
	public T_Serviceproviderdetail saveData(T_Serviceproviderdetail saveDetails)
	{
		T_Serviceproviderdetail data1 = t_ServiceproviderdetailRepo.save(saveDetails);
		return data1;
	}

	@Override
	public /* M_Providerservicemapping_Blocking */void blockProviderByService(Integer serviceProviderID,
			Integer stateID, Integer serviceID, Integer statusID)
	{
		/* M_Providerservicemapping_Blocking data2= */mProviderservicemappingBlockingRepo
				.blockProviderByService(serviceProviderID, stateID, serviceID, statusID);
		// return data2;
	}

	@Override
	public M_Providerservicemapping_Blocking getProviderServiceMappingDetails(Integer serviceProviderID,
			Integer stateID, Integer serviceID)
	{
		M_Providerservicemapping_Blocking data2 = mProviderservicemappingBlockingRepo
				.getProviderServiceMappingDetails(serviceProviderID, stateID, serviceID);
		return data2;
	}

	@Override
	public T_Providerservicemappingdetail savetpsdData(T_Providerservicemappingdetail tpsd)
	{
		T_Providerservicemappingdetail data3 = t_ProviderservicemappingdetailRepo.save(tpsd);
		return data3;
	}

	@Override
	public List<M_Providerservicemapping_Blocking> getProviderStateMappingDetails(Integer serviceProviderID,
			Integer stateID)
	{
		List<M_Providerservicemapping_Blocking> data4 =
				mProviderservicemappingBlockingRepo.getProviderStateMappingDetails(serviceProviderID, stateID);
		return data4;
	}

	@Override
	public void blockProviderByState(Integer serviceProviderID, Integer stateID, Integer statusID)
	{
		mProviderservicemappingBlockingRepo.blockProviderByState(serviceProviderID, stateID, statusID);

	}

	@Override
	public ArrayList<T_Providerservicemappingdetail> savetpsmd(List<T_Providerservicemappingdetail> resList)
	{
		ArrayList<T_Providerservicemappingdetail> data5 =
				(ArrayList<T_Providerservicemappingdetail>) t_ProviderservicemappingdetailRepo.save(resList);
		return data5;
	}

	@Override
	public UserForBlocking getUserDetailByUserId(Integer userID)
	{
		UserForBlocking userData = userBlockingRepo.getUserDetailByUserId(userID);
		return userData;
	}

	@Override
	public ArrayList<M_Status1> getStatusData()
	{
		// TODO Auto-generated method stub
		return mStatusRepo.getStatusData();
	}

	@Override
	public void blockUser(Integer userID, Integer statusID)
	{
		userBlockingRepo.blockUser(userID, statusID);

	}

	@Override
	public T_Userdetail saveUserDetails(T_Userdetail tuserDetails)
	{
		T_Userdetail userData = t_UserDetailRepo.save(tuserDetails);
		return userData;
	}

	@Override
	public ArrayList<M_Providerservicemapping_Blocking> getProviderStatus(Integer serviceProviderID)
	{

		return mProviderservicemappingBlockingRepo.getProviderStatus(serviceProviderID);
	}

	@Override
	public ArrayList<M_Providerservicemapping_Blocking>
			getProviderStatusByProviderAndServiceId(Integer serviceProviderID, Integer serviceID)
	{

		return mProviderservicemappingBlockingRepo.getProviderStatusByProviderAndServiceId(serviceProviderID,
				serviceID);
	}

	@Override
	public void blockProvider(Integer serviceProviderID, Integer statusID)
	{
		mProviderservicemappingBlockingRepo.blockProvider(serviceProviderID, statusID);

	}

	@Override
	public void blockProviderByProviderIdAndServiceId(Integer serviceProviderID, Integer serviceID, Integer statusID)
	{
		mProviderservicemappingBlockingRepo.blockProviderByProviderIdAndServiceId(serviceProviderID, serviceID,
				statusID);

	}

	@Override
	public ArrayList<M_Providerservicemapping_Blocking> getServiceLiensUsingProvider(Integer serviceProviderID)
	{

		/*
		 * ArrayList<StateServiceMapping1 > stateServiceMappings = new ArrayList<StateServiceMapping1>();
		 * ArrayList<Object[]>resultSet = locationMasterRepo.getStateByServiceProviderId(serviceProviderID); for
		 * (Object[] objects : resultSet) { if (objects!=null && objects.length>=2) { stateServiceMappings.add(new
		 * StateServiceMapping1((Integer)objects[0], (String)objects[1])); } } return stateServiceMappings;
		 */
		ArrayList<M_Providerservicemapping_Blocking> stateServiceMappings =
				new ArrayList<M_Providerservicemapping_Blocking>();
		ArrayList<Object[]> resultSet =
				mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider(serviceProviderID);
		for (Object[] objects : resultSet)
		{
			if (objects != null && objects.length >= 2)
			{
				stateServiceMappings.add(new M_Providerservicemapping_Blocking((Integer) objects[0],
						(Integer) objects[1], (Integer) objects[2], (String) objects[3], (Boolean) objects[4]));
			}
		}
		return stateServiceMappings;
	}

	@Override
	public ArrayList<V_Showproviderservicemapping> getProviderStatus1(Integer serviceProviderID)
	{
		// TODO Auto-generated method stub
		return v_ShowproviderservicemappingRepo.getProviderStatus(serviceProviderID);
	}
	
	
	@Override
	public ArrayList<V_Showproviderservicemapping> getProviderStatus2(Integer serviceProviderID)
	{
		// TODO Auto-generated method stub
		return v_ShowproviderservicemappingRepo.getProviderStatus1(serviceProviderID);
	}

	@Override
	public ArrayList<V_Showproviderservicemapping> getProviderServiceMappingDetails2(Integer serviceProviderID,
			Integer stateID, Integer serviceID)
	{
		// TODO Auto-generated method stub
		return v_ShowproviderservicemappingRepo.getProviderServiceMappingDetails1(serviceProviderID, stateID,
				serviceID);
	}

	@Override
	public ArrayList<V_Showproviderservicemapping> getProviderStateMappingDetails1(Integer serviceProviderID,
			Integer stateID)
	{
		// TODO Auto-generated method stub
		return v_ShowproviderservicemappingRepo.getProviderStateMappingDetails(serviceProviderID, stateID);
	}

	@Override
	public ArrayList<V_Showproviderservicemapping> getProviderStatusByProviderAndServiceId2(Integer serviceProviderID,
			Integer serviceID)
	{
		// TODO Auto-generated method stub
		return v_ShowproviderservicemappingRepo.getProviderStatusByProviderAndServiceId(serviceProviderID, serviceID);
	}

	@Override
	public ArrayList<M_Providerservicemapping_Blocking>
			AddServiceProvider(List<M_Providerservicemapping_Blocking> resList)
	{
		ArrayList<M_Providerservicemapping_Blocking> data =
				(ArrayList<M_Providerservicemapping_Blocking>) mProviderservicemappingBlockingRepo.save(resList);
		return data;
	}

	@Override
	public String mapctidata(List<M_Providerservicemapping_Blocking> ctidata)
	{
		/*
		 * @Override public ArrayList<Showofficedetails> getlocationByMapid1(ArrayList<Integer> data) {
		 * ArrayList<Showofficedetails> reslist = new ArrayList<Showofficedetails>(); for(Integer i:data){
		 * reslist.addAll(showofficedetailsRepo.getlocationByMapid1(i)); } //ArrayList<M_ProviderServiceAddMapping>
		 * reslist=m_ProviderServiceAddMappingRepo.getlocationByMapid1(data); //ArrayList<M_ProviderServiceAddMapping>
		 * reslist=(ArrayList<M_ProviderServiceAddMapping>) m_ProviderServiceAddMappingRepo.findAll(data); return
		 * reslist;
		 */

		int data = 0, counts = 0;
		ArrayList<M_Providerservicemapping_Blocking> reslist = new ArrayList<M_Providerservicemapping_Blocking>();
		for (M_Providerservicemapping_Blocking i : ctidata)
		{
			data = mProviderservicemappingBlockingRepo.createcitmapping(i.getProviderServiceMapID(),
					i.getcTI_CampaignName());
			if (data < 1)
			{
				break;
			}
			counts += data;
			// if (data>0) {
			// i.setStatus("Mapping Successfull");
			// } else {
			// i.setStatus("Mapping Failed");
			// }
		}
		String status = "Mapping Failed";
		if (counts >= ctidata.size())
		{
			status = "Mapping Successful";
		} else
		{
			status += " after " + counts + " entries.";
		}
		return status;
	}

	@Override
	public ArrayList<M_Providerservicemapping_Blocking>
			getServiceLiensUsingProvider1(M_Providerservicemapping_Blocking providerDetails)
	{

		/*
		 * ArrayList<StateServiceMapping1 > stateServiceMappings = new ArrayList<StateServiceMapping1>();
		 * ArrayList<Object[]>resultSet = locationMasterRepo.getStateByServiceProviderId(serviceProviderID); for
		 * (Object[] objects : resultSet) { if (objects!=null && objects.length>=2) { stateServiceMappings.add(new
		 * StateServiceMapping1((Integer)objects[0], (String)objects[1])); } } return stateServiceMappings;
		 */
		ArrayList<M_Providerservicemapping_Blocking> stateServiceMappings =
				new ArrayList<M_Providerservicemapping_Blocking>();
		ArrayList<Object[]> resultSet = null;
		if (providerDetails.getServiceProviderID() != null && providerDetails.getServiceID() != null
				&& providerDetails.getStateID() != null)
		{
			resultSet = mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider1(
					providerDetails.getServiceProviderID(), providerDetails.getServiceID(),
					providerDetails.getStateID());
		} else if (providerDetails.getServiceProviderID() != null && providerDetails.getServiceID() != null)
		{
			resultSet = mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider1(
					providerDetails.getServiceProviderID(), providerDetails.getServiceID());
		} else if (providerDetails.getServiceProviderID() != null)
		{
			resultSet = mProviderservicemappingBlockingRepo
					.getServiceLiensUsingProvider1(providerDetails.getServiceProviderID());
		} else
		{
			resultSet = mProviderservicemappingBlockingRepo.getServiceLiensUsingProvider1();
		}
		for (Object[] objects : resultSet)
		{
			if (objects != null && objects.length >= 2)
			{
				stateServiceMappings.add(new M_Providerservicemapping_Blocking((Integer) objects[0],
						(Integer) objects[1], (Integer) objects[2], (String) objects[3], (Integer) objects[4],
						(String) objects[5], (String) objects[6], (Boolean) objects[7], (String) objects[8],(Boolean) objects[9]));
			}
		}
		return stateServiceMappings;
	}

	@Override
	public M_Providerservicemapping_Blocking getDataByProviderServiceMapId(Integer providerServiceMapID)
	{
		M_Providerservicemapping_Blocking data = mProviderservicemappingBlockingRepo.findOne(providerServiceMapID);
		return data;
	}

	@Override
	public M_Providerservicemapping_Blocking updateProviderData(M_Providerservicemapping_Blocking getProviderData)
	{
		M_Providerservicemapping_Blocking data = mProviderservicemappingBlockingRepo.save(getProviderData);

		return data;
	}

}
