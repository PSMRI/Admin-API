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
package com.iemr.admin.data.labmodule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;



@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  @Override
  public String convertToDatabaseColumn(List<String> list) {
    // Java 8
	  StringBuilder strbul  = new StringBuilder();
	  if(list!=null && list.size()>0){
		  Iterator<String> iter = list.iterator();
		     while(iter.hasNext())
		     {
		         strbul.append(iter.next());
		        if(iter.hasNext()){
		         strbul.append("\\|\\|");
		        }
		     } 
	  }else{
		  return null;
	  }
	     
	 return strbul.toString();
//    return String.join(",", list);
  }

  @Override
  public List<String> convertToEntityAttribute(String joined) {
	  List<String> list=new ArrayList();
	  if(joined!=null){
		  String[] strArray = joined.split("\\|\\|"); // split by "||"

		  for(int i = 0; i < strArray.length; i++) {
			  list.add( strArray[i]);
		  }  
	  }
	  
    return list;
  }

}