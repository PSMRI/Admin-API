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
package com.iemr.admin.to.blocking;

public class BlockingTO {
 private Integer serviceID1[];
 
 private Integer stateID1[];
 
 private Integer serviceProviderMapID1[];
 
 
 

public Integer[] getServiceProviderMapID1() {
	return serviceProviderMapID1;
}

public void setServiceProviderMapID1(Integer[] serviceProviderMapID1) {
	this.serviceProviderMapID1 = serviceProviderMapID1;
}

public Integer[] getServiceID1() {
	return serviceID1;
}

public void setServiceID1(Integer[] serviceID1) {
	this.serviceID1 = serviceID1;
}

public Integer[] getStateID1() {
	return stateID1;
}

public void setStateID1(Integer[] stateID1) {
	this.stateID1 = stateID1;
}




 
}
