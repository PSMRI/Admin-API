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
package com.iemr.admin.service.telemedicine;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.telemedicine.M_UserTemp;
import com.iemr.admin.data.telemedicine.SwymedDomain;
import com.iemr.admin.data.telemedicine.UserSwymed;
import com.iemr.admin.repo.telemedicine.SwymedDomainRepo;
import com.iemr.admin.repo.telemedicine.UserRepo;
import com.iemr.admin.repo.telemedicine.UserSwymedRepo;
import com.iemr.admin.utils.exception.SwyMedException;

@Service
public class SwymedServiceImpl implements SwymedInter {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private UserSwymedRepo userSwymedRepo;
	
	@Autowired
	private SwymedDomainRepo swymedDomainRepo;
	
	@Autowired
	private SwymedAPIInter swymedAPIInter;
	
	
	
	

	@Override
	public List<M_UserTemp> getunmappedUser(Integer serviceproviderID, Integer designationID) {
		// TODO Auto-generated method stub
		return userRepo.getunmappedSwymedUser(serviceproviderID, designationID);
	}

	@Override
	public UserSwymed createUser(UserSwymed userSwymed) throws SwyMedException {
		// TODO Auto-generated method stub
		M_UserTemp user=userRepo.findOne(userSwymed.getUserID());
		if(user==null){
			throw new SwyMedException("Invalid User");
		}
		HashMap<String,String> obj=new HashMap<>();
		obj.put("name",user.getFirstName());
		obj.put("surname",user.getLastName());
		obj.put("email",userSwymed.getSwymedEmailID());
		obj.put("password",userSwymed.getSwymedPassword());
		obj.put("domain",userSwymed.getSwymedDomain());
		obj.put("kind","2");
		obj.put("status","1");
		obj.put("member",user.getUserName());
//		obj.put(key, value);
				
		//remove this comment for deeper integration
		Long swymedID=swymedAPIInter.createUser( obj);
		
		userSwymed.setSwymedID(swymedID);
		
		return userSwymedRepo.save(userSwymed);
	}

	@Override
	public UserSwymed editUser(UserSwymed userSwymed) throws SwyMedException {
		// TODO Auto-generated method stub
		UserSwymed swy=userSwymedRepo.findOne(userSwymed.getUserSwymedMapID());
		
		
		if(swy==null){
			throw new SwyMedException("Invalid MapID");
		}
		swy.setSwymedEmailID(userSwymed.getSwymedEmailID());
		swy.setSwymedPassword(userSwymed.getSwymedPassword());
		swy.setModifiedBy(userSwymed.getModifiedBy());
		swy.setUser(null);
		if(swy.getSwymedID()==null){
			
			return createUser(swy);
		}
		HashMap<String,String> obj=new HashMap<>();
		obj.put("userid",swy.getSwymedID().toString());
		int i=0;
		if(!swy.getSwymedEmailID().equals(userSwymed.getSwymedEmailID())){
			obj.put("email",userSwymed.getSwymedEmailID());	
			i++;
		}
		if(!swy.getSwymedPassword().equals(userSwymed.getSwymedPassword())){
			obj.put("password",userSwymed.getSwymedPassword());	
			i++;
		}
		// remove this comment for deeper integration
		if(i>0){
			Long swymedID=swymedAPIInter.editUser(obj,swy.getSwymedID(),swy.getSwymedDomain());
		}
		
//		
		
		
//		swy.setDeleted(deletedflag);
		return userSwymedRepo.save(swy);
	}
	
	@Override
	public List<UserSwymed> fetchmappedUser(Integer serviceproviderID) {
		// TODO Auto-generated method stub
		return userSwymedRepo.fetchmappedUser(serviceproviderID);
	}

	@Override
	public UserSwymed deleteUser(Long userSwymedMapID, Boolean deletedflag,String modifiedBy) throws SwyMedException {
		// TODO Auto-generated method stub
		UserSwymed swy=userSwymedRepo.findOne(userSwymedMapID);
		swy.setDeleted(deletedflag);
		swy.setModifiedBy(modifiedBy);
		swy.setUser(null);
		
		HashMap<String,String> obj=new HashMap<>();
//		obj.put("userid",swy.getSwymedID().toString());
		obj.put("status",deletedflag?"1":"0");
		
		// remove this comment for deeper integration
		Long swymedID=swymedAPIInter.editUser(obj,swy.getSwymedID(),swy.getSwymedDomain());
		
		
		
		return userSwymedRepo.save(swy);
	}

	@Override
	public List<SwymedDomain> getdomain(Integer serviceproviderID) {
		// TODO Auto-generated method stub
		return (List<SwymedDomain>) swymedDomainRepo.findAll();
	}

}
