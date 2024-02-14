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
package com.iemr.admin.data.items;


import com.google.gson.annotations.Expose;
import com.iemr.admin.utils.mapper.OutputMapper;

import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class ItemInStore {

	@Expose
	private Integer itemID;

	@Expose
	private String itemName;

	@Expose
	private Long quantity;

	@Expose
	private Integer facilityID;
	
	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}
	
	public ItemInStore(Integer facilityID, Integer itemID, String itemName, Long quantity) {
		// TODO Auto-generated constructor stub
		this.facilityID=facilityID;
		this.itemID=itemID;
		this.itemName=itemName;
		this.quantity=quantity;
	}


}
