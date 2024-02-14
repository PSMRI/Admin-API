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
import com.iemr.admin.data.telemedicine.VideoConsultationDomain;
import com.iemr.admin.data.telemedicine.UserVideoConsultation;
import com.iemr.admin.repo.telemedicine.VideoConsultationDomainRepo;
import com.iemr.admin.repo.telemedicine.UserRepo;
import com.iemr.admin.repo.telemedicine.UserVideoConsultationRepo;
import com.iemr.admin.utils.exception.VideoConsultationException;

@Service
public class VideoConsultationServiceImpl implements VideoConsultationInter {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private UserVideoConsultationRepo userVideoConsultationRepo;
	
	@Autowired
	private VideoConsultationDomainRepo videoConsultationDomainRepo;
	
	@Autowired
	private VideoConsultationAPIInter videoConsultationAPIInter;
	
	
	
	

	@Override
	public List<M_UserTemp> getunmappedUser(Integer serviceproviderID, Integer designationID) {
		return userRepo.getunmappedVideoConsultationUser(serviceproviderID, designationID);
	}

	@Override
	public UserVideoConsultation createUser(UserVideoConsultation userVideoConsultation) throws VideoConsultationException {
		M_UserTemp user=userRepo.findByUserID(userVideoConsultation.getUserID());
		if(user==null){
			throw new VideoConsultationException("Invalid User");
		}
		HashMap<String,String> obj=new HashMap<>();
		obj.put("name",user.getFirstName());
		obj.put("surname",user.getLastName());
		obj.put("email",userVideoConsultation.getVideoConsultationEmailID());
		obj.put("password",userVideoConsultation.getVideoConsultationPassword());
		obj.put("domain",userVideoConsultation.getVideoConsultationDomain());
		obj.put("kind","2");
		obj.put("status","1");
		obj.put("member",user.getUserName());
				
		Long videoConsultationID=videoConsultationAPIInter.createUser( obj);
		
		userVideoConsultation.setVideoConsultationID(videoConsultationID);
		
		return userVideoConsultationRepo.save(userVideoConsultation);
	}

	@Override
	public UserVideoConsultation editUser(UserVideoConsultation userVideoConsultation) throws VideoConsultationException {
		UserVideoConsultation swy=userVideoConsultationRepo.findByUserVideoConsultationMapID(userVideoConsultation.getUserVideoConsultationMapID());
		
		
		if(swy==null){
			throw new VideoConsultationException("Invalid MapID");
		}
		swy.setVideoConsultationEmailID(userVideoConsultation.getVideoConsultationEmailID());
		swy.setVideoConsultationPassword(userVideoConsultation.getVideoConsultationPassword());
		swy.setModifiedBy(userVideoConsultation.getModifiedBy());
		swy.setUser(null);
		if(swy.getVideoConsultationID()==null){
			
			return createUser(swy);
		}
		HashMap<String,String> obj=new HashMap<>();
		obj.put("userid",swy.getVideoConsultationID().toString());
		int i=0;
		if(!swy.getVideoConsultationEmailID().equals(userVideoConsultation.getVideoConsultationEmailID())){
			obj.put("email",userVideoConsultation.getVideoConsultationEmailID());	
			i++;
		}
		if(!swy.getVideoConsultationPassword().equals(userVideoConsultation.getVideoConsultationPassword())){
			obj.put("password",userVideoConsultation.getVideoConsultationPassword());	
			i++;
		}
		if(i>0){
			Long videoConsultationID=videoConsultationAPIInter.editUser(obj,swy.getVideoConsultationID(),swy.getVideoConsultationDomain());
		}
		
		
		
		return userVideoConsultationRepo.save(swy);
	}
	
	@Override
	public List<UserVideoConsultation> fetchmappedUser(Integer serviceproviderID) {
		return userVideoConsultationRepo.fetchmappedUser(serviceproviderID);
	}

	@Override
	public UserVideoConsultation deleteUser(Long userVideoConsultationMapID, Boolean deletedflag,String modifiedBy) throws VideoConsultationException {
		UserVideoConsultation swy=userVideoConsultationRepo.findByUserVideoConsultationMapID(userVideoConsultationMapID);
		swy.setDeleted(deletedflag);
		swy.setModifiedBy(modifiedBy);
		swy.setUser(null);
		
		HashMap<String,String> obj=new HashMap<>();
		obj.put("status",deletedflag?"1":"0");
		
		Long videoConsultationID=videoConsultationAPIInter.editUser(obj,swy.getVideoConsultationID(),swy.getVideoConsultationDomain());
		
		
		
		return userVideoConsultationRepo.save(swy);
	}

	@Override
	public List<VideoConsultationDomain> getdomain(Integer serviceproviderID) {
		return (List<VideoConsultationDomain>) videoConsultationDomainRepo.findAll();
	}

}
