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

import com.iemr.admin.data.provideronboard.M_Category;
import com.iemr.admin.data.provideronboard.M_Subcategory;
import com.iemr.admin.data.provideronboard.V_Showsubcategory;

public interface CategoryInter {

	Integer getCategoryId(M_Category categoryid);

	ArrayList<M_Subcategory> saveSubCatData(List<M_Subcategory> resList);

	ArrayList<M_Subcategory> getCategory();

	M_Subcategory getSubCategory(Integer subCategoryID);

	M_Subcategory updateSubCatData(M_Subcategory getsubCategory);

	ArrayList<M_Subcategory> getCategory(Integer categoryID);

	ArrayList<M_Category> getAllCategory(Integer subServiceID, Integer providerServiceMapID);

	ArrayList<V_Showsubcategory> getCategoryByMapIDAndSubServiceID(Integer providerServiceMapID, Integer subServiceID);

	ArrayList<M_Category> createcat(List<M_Category> catdata);

	M_Category getcatdatabycatId(Integer categoryID);

	M_Category deletedata(M_Category deletedData);

	ArrayList<M_Subcategory> createSubCategory(List<M_Subcategory> subcatdata);

	ArrayList<V_Showsubcategory> getSubCategory1(Integer subCategoryID);

	int updateCategory(Integer catid, Integer feedbackid);

	ArrayList<M_Category> getAllCategory(Integer providerServiceMapID);

	ArrayList<M_Category> getAllCategory1(Integer providerServiceMapID);

	ArrayList<M_Category> getAllCategorywithFeedbackNatureID(Integer providerServiceMapID,
			Integer feedbackNatureID);

	ArrayList<M_Category> getUpmappedCategory(Integer providerServiceMapID);

}
