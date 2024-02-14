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
package com.iemr.admin.service.provideronboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.iemr.admin.data.provideronboard.M_Category;
import com.iemr.admin.data.provideronboard.M_Subcategory;
import com.iemr.admin.data.provideronboard.V_Showsubcategory;
import com.iemr.admin.data.zonemaster.M_ZoneDistrictMap;
import com.iemr.admin.repository.provideronboard.CategoryRepo;
import com.iemr.admin.repository.provideronboard.SubCategoryRepo;
import com.iemr.admin.repository.provideronboard.V_ShowsubcategoryRepo;



@Service
public class CategoryMasterImpl implements CategoryInter{
	
	@Autowired
	private V_ShowsubcategoryRepo v_ShowsubcategoryRepo;
	
	@Autowired
	private CategoryRepo categoryRepo;
	@Autowired
	private SubCategoryRepo subCategoryRepo;

	@Override
	public Integer getCategoryId(M_Category categoryid) {
		M_Category data=categoryRepo.save(categoryid);
		return data.getCategoryID() ;
	}

	@Override
	public ArrayList<M_Subcategory> saveSubCatData(List<M_Subcategory> resList) {
		ArrayList<M_Subcategory> data2=(ArrayList<M_Subcategory>) subCategoryRepo.saveAll(resList);
		return data2;
	}

	@Override
	public ArrayList<M_Subcategory> getCategory() {
		ArrayList<M_Subcategory> data=subCategoryRepo.getCategory();
		return data;
	}

	@Override
	public M_Subcategory getSubCategory(Integer subCategoryID) {
		M_Subcategory data4=subCategoryRepo.getSubCategory(subCategoryID);
		return data4;
	}

	@Override
	public M_Subcategory updateSubCatData(M_Subcategory getsubCategory) {
		M_Subcategory updatedData=subCategoryRepo.save(getsubCategory);
		return updatedData;
	}

	@Override
	public ArrayList<M_Subcategory> getCategory(Integer categoryID) {
		ArrayList<M_Subcategory> data=subCategoryRepo.getCategory(categoryID);
		return data;
	}

	@Override
	public ArrayList<M_Category> getAllCategory(Integer subServiceID,Integer providerServiceMapID) {
		ArrayList<M_Category> catData=categoryRepo.getAllCategory(subServiceID,providerServiceMapID);
		return catData;
	}

	@Override
	public ArrayList<V_Showsubcategory> getCategoryByMapIDAndSubServiceID(Integer providerServiceMapID,
			Integer subServiceID) {
		// TODO Auto-generated method stub
		ArrayList<V_Showsubcategory> findAllcatBasedonServiceIDAndPSMid=v_ShowsubcategoryRepo.getCategoryByMapIDAndSubServiceID(providerServiceMapID,subServiceID);
		return findAllcatBasedonServiceIDAndPSMid;
	}

	@Override
	public ArrayList<M_Category> createcat(List<M_Category> catdata) {
		ArrayList<M_Category> data=(ArrayList<M_Category>) categoryRepo.saveAll(catdata);
		return data;
	}

	@Override
	public M_Category getcatdatabycatId(Integer categoryID) {
		M_Category data=categoryRepo.getCatData(categoryID);
		return data;
	}

	@Override
	public M_Category deletedata(M_Category deletedData) {
		 M_Category datadeleted=categoryRepo.save(deletedData);
		return datadeleted;
	}

	@Override
	public ArrayList<M_Subcategory> createSubCategory(List<M_Subcategory> subcatdata) {
		ArrayList<M_Subcategory> subcatData=(ArrayList<M_Subcategory>) subCategoryRepo.saveAll(subcatdata);
		return subcatData;
	}

	@Override
	public ArrayList<V_Showsubcategory> getSubCategory1(Integer subCategoryID) {
		ArrayList<V_Showsubcategory> data=v_ShowsubcategoryRepo.getSubCategory1(subCategoryID);
		return data;
	}

	@Override
	public int updateCategory(Integer catid, Integer feedbackid) {
		int update= categoryRepo.updateCategory(catid,feedbackid);
		return update;
	}

	@Override
	public ArrayList<M_Category> getAllCategory(Integer providerServiceMapID) {
ArrayList<M_Category> zoneList = new ArrayList<M_Category>();	

            //ArrayList<M_Category> catData=categoryRepo.getAllCategory(subServiceID,providerServiceMapID);
		
		List<Object[]>  allData=categoryRepo.getAllCategory(providerServiceMapID);
		
		for (Object[] objects : allData) {
			
			zoneList.add(new M_Category((Integer)objects[0],(String)objects[1], (Integer)objects[2], (String)objects[3],(Integer)objects[4]));
		}
		return zoneList;
	
	}

	@Override
	public ArrayList<M_Category> getAllCategorywithFeedbackNatureID(Integer providerServiceMapID, Integer feedbackNatureID) {
ArrayList<M_Category> zoneList = new ArrayList<M_Category>();	

            //ArrayList<M_Category> catData=categoryRepo.getAllCategory(subServiceID,providerServiceMapID);
		
		List<Object[]>  allData=categoryRepo.getAllCategorywithfeedbackNatureID(providerServiceMapID,feedbackNatureID);
		
		for (Object[] objects : allData) {
			
			zoneList.add(new M_Category((Integer)objects[0],(String)objects[1], (Integer)objects[2], (String)objects[3],(Integer)objects[4]));
		}
		return zoneList;
	
	}
	
	@Override
	public ArrayList<M_Category> getAllCategory1(Integer providerServiceMapID) {
		ArrayList<M_Category> catData=categoryRepo.getAllCategory1(providerServiceMapID);
		return catData;
	}

	@Override
	public ArrayList<M_Category> getUpmappedCategory(Integer providerServiceMapID) {
		// TODO Auto-generated method stub
		return categoryRepo.findByProviderServiceMapIDAndFeedbackNatureIDOrderByCategoryNameAsc(providerServiceMapID,null);
	}
	

}
