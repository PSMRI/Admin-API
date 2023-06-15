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
package com.iemr.admin.to;

import java.util.List;

public class CallType2 {

	private String callType1;
	
	private List<String> callTypeDesc1;
	private String calltype;
	private Integer providerServiceMapID;
	private List<String> fitToBlock1;
	private List<Boolean> fitForFollowup1;
	private List<Boolean> isInbound1;
	private List<Boolean> isOutbound1;
	
	
	
	
	
	
	public String getCallType1() {
		return callType1;
	}
	public void setCallType1(String callType1) {
		this.callType1 = callType1;
	}
	public List<String> getCallTypeDesc1() {
		return callTypeDesc1;
	}
	public void setCallTypeDesc1(List<String> callTypeDesc1) {
		this.callTypeDesc1 = callTypeDesc1;
	}
	public String getCalltype() {
		return calltype;
	}
	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}
	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}
	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapID = providerServiceMapID;
	}
	
	public List<String> getFitToBlock1() {
		return fitToBlock1;
	}
	public void setFitToBlock1(List<String> fitToBlock1) {
		this.fitToBlock1 = fitToBlock1;
	}
	public List<Boolean> getFitForFollowup1() {
		return fitForFollowup1;
	}
	public void setFitForFollowup1(List<Boolean> fitForFollowup1) {
		this.fitForFollowup1 = fitForFollowup1;
	}
	public List<Boolean> getIsInbound1() {
		return isInbound1;
	}
	public void setIsInbound1(List<Boolean> isInbound1) {
		this.isInbound1 = isInbound1;
	}
	public List<Boolean> getIsOutbound1() {
		return isOutbound1;
	}
	public void setIsOutbound1(List<Boolean> isOutbound1) {
		this.isOutbound1 = isOutbound1;
	}
	
	
	
	
	
	
}
