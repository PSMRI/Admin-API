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
package com.iemr.admin.data.createOrder;

import java.sql.Date;

public class CreateOrderData {
	private String firstName;
	private String middleName;
	private String LastName;
	private String gender;
	private String dob;
	private String patientID;
	private String acc;
	
	
	public CreateOrderData() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	public String getPatientID() {
		return patientID;
	}




	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}




	public String getAcc() {
		return acc;
	}




	public void setAcc(String acc) {
		this.acc = acc;
	}




	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}




	public String getDob() {
		return dob;
	}




	public void setDob(String dob) {
		this.dob = dob;
	}
	
	
	
	

}
