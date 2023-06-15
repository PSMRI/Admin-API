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
package com.iemr.admin.service.uptsu;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.iemr.admin.data.uptsu.M_FacilityMapping;
import com.iemr.admin.data.uptsu.UploadRequest;
import com.iemr.admin.repository.uptsu.FacilityRepository;
import com.iemr.admin.utils.exception.IEMRException;

@Service
public class FacilityServiceImpl implements FacilityService {

	@Autowired
	private FacilityRepository uptsuUploadRepository;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Override
	public Iterable<M_FacilityMapping> saveFacility(UploadRequest request) throws EncryptedDocumentException, IOException, IEMRException {
		List<M_FacilityMapping> list = new ArrayList<M_FacilityMapping>();
		Iterable<M_FacilityMapping> data = null;
		if(null != request) {
		String fileContent = request.getFileContent();
		String split = fileContent.split(",")[1];
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decode = decoder.decode(split);

		ByteArrayInputStream is = new ByteArrayInputStream(decode);
		Workbook book = WorkbookFactory.create(is);
		Sheet sheetAt = book.getSheetAt(0);
		int lastRowNum = sheetAt.getLastRowNum();
		for (int i = 1; i <= lastRowNum; i++) {
			M_FacilityMapping mapping = null;
			Row row = sheetAt.getRow(i);
			mapping = convertToObject(row, request);
			list.add(mapping);
		}
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		uptsuUploadRepository.updatedeleteStatus(request.getProviderServiceMapID(),timestamp,request.getCreatedBy());
		data = uptsuUploadRepository.save(list);
		}
	return data;
	}

	private M_FacilityMapping convertToObject(Row row, UploadRequest request) throws IEMRException {
		M_FacilityMapping mapping = new M_FacilityMapping();
		int noOfColumns = row.getLastCellNum();
		mapping.setCreatedBy(request.getCreatedBy());
		mapping.setModifiedBy(null);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		mapping.setCreatedDate(timestamp);
		mapping.setLastModDate(timestamp);
		mapping.setProviderServiceMapID(request.getProviderServiceMapID());
		mapping.setProcessed('N');
		mapping.setDeleted(false);
		for (int j = 0; j <= noOfColumns; j++) {
			Cell cell = row.getCell(j);
			setValue(j, cell, mapping);
		}
		return mapping;
	}

	private void setValue(int j, Cell cell, M_FacilityMapping mapping) throws IEMRException {
		switch (Integer.valueOf(j)) {
		case 0:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setEmployeeCode(String.valueOf(getCellValue(cell)));
			} else {
				throw new IEMRException("Error in validating cell " + cell);
			}
			break;
		case 1:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setEmployeeName(String.valueOf(getCellValue(cell)));
			} else {
				throw new IEMRException("Error in validating cell " + cell);
			}
			break;
		case 2:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setSurveyFacility(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setSurveyFacility(null);
			}
			break;
		case 3:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setOfficeType(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setOfficeType(null);
			}
			break;
		case 4:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setOfficeCategory(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setOfficeCategory(null);
			}
			break;
		case 5:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setPostingOffice(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setPostingOffice(null);
			}
			break;
		case 6:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setPostDistrict(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setPostDistrict(null);
			}
			break;
		case 7:
			if (null != getCellValue(cell) && !StringUtils.isEmpty(getCellValue(cell))) {
				mapping.setDesignationId((Integer) getCellValue(cell));
			} else {
				mapping.setDesignationId(0);
			}
			break;
		case 8:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setDesignation(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setDesignation(null);
			}
			break;
		case 9:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setDesCategory(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setDesCategory(null);
			}
			break;
		case 10:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setDesSubCategory(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setDesSubCategory(null);
			}
			break;
		case 11:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setEmployeeType(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setEmployeeType(null);
			}
			break;
		case 12:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setCadre(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setCadre(null);
			}
			break;
		case 13:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setActiveStatus(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setActiveStatus(null);
			}
			break;
		case 14:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setFacilityType(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setFacilityType(null);
			}
			break;
		case 15:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setPresentMoboleNo(String.valueOf(getCellValue(cell)));
			} else {
				throw new IEMRException("Error in validating cell " + cell);
			}
			break;
		case 16:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setDivisionName(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setDivisionName(null);
			}
			break;
		case 17:
			if (null != getCellValue(cell) && !StringUtils.isEmpty(getCellValue(cell))) {
				mapping.setDivisionCode((Integer) getCellValue(cell));
			} else {
				mapping.setDivisionCode(0);
			}

			break;
		case 18:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setDistrictName(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setDistrictName(null);
			}
			break;
		case 19:
			if (null != getCellValue(cell) && !StringUtils.isEmpty(getCellValue(cell))) {
				mapping.setDlgd((Integer) getCellValue(cell));
			} else {
				mapping.setDlgd(0);
			}
			break;
		case 20:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setTehsilName(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setTehsilName(null);
			}
			break;
		case 21:
			if (null != getCellValue(cell) && !StringUtils.isEmpty(getCellValue(cell))) {
				mapping.setTlgd((Integer) getCellValue(cell));
			} else {
				mapping.setTlgd(0);
			}
			break;
		case 22:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setBlockName(String.valueOf(getCellValue(cell)));
			} else {
				throw new IEMRException("Error in validating cell " + cell);
			}
			break;
		case 23:
			if (null != getCellValue(cell) && !StringUtils.isEmpty(getCellValue(cell))) {
				mapping.setBlgd((Integer) getCellValue(cell));
			} else {
				mapping.setBlgd(0);
			}
			break;
		case 24:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setGramPanchayatName(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setGramPanchayatName(null);
			}
			break;
		case 25:
			if (null != getCellValue(cell) && !StringUtils.isEmpty(getCellValue(cell))) {
				mapping.setGlgd((Integer) getCellValue(cell));
			} else {
				mapping.setGlgd(0);
			}
			break;
		case 26:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setVillageName(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setVillageName(null);
			}
			break;
		case 27:
			if (null != getCellValue(cell) && !StringUtils.isEmpty(getCellValue(cell))) {
				mapping.setVillageCode((Integer) getCellValue(cell));
			} else {
				mapping.setVillageCode(0);
			}
			break;
		case 28:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setFacilityName(String.valueOf(getCellValue(cell)));
			} else {
				throw new IEMRException("Error in validating cell " + cell);
			}

			break;
		case 29:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setFacilityCode(String.valueOf(getCellValue(cell)));
			} else {
				throw new IEMRException("Error in validating cell " + cell);
			}
			break;
		case 30:
			break;
		case 31:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setFcStatus(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setFcStatus(null);
			}
			break;
		case 32:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setFacilityClassification(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setFacilityClassification(null);
			}
			break;
		case 33:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setFacilityCategory(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setFacilityCategory(null);
			}
			break;
		case 34:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setLongitude(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setLongitude(null);
			}
			break;
		case 35:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setLatitude(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setLatitude(null);
			}
			break;
		case 36:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setHimsCode(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setHimsCode(null);
			}
			break;
		case 37:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setHwcStatus(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setHwcStatus(null);
			}
			break;
		case 38:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setFruStatus(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setFruStatus(null);
			}
			break;
		case 39:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setHfrCode(String.valueOf(getCellValue(cell)));
			} else {
				throw new IEMRException("Error in validating cell " + cell);
			}
			break;
		case 40:
			if (!StringUtils.isEmpty(String.valueOf(getCellValue(cell)))) {
				mapping.setHprCode(String.valueOf(getCellValue(cell)));
			} else {
				mapping.setHprCode(null);
			}
			break;
		}
	}

	private Object getCellValue(Cell cell) throws IEMRException {
		Object value = "";
		if (null != cell) {
			switch (cell.getCellType()) {
			case BLANK:
				break;
				//throw new IEMRException("Error in validating cell " + cell);
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case NUMERIC:
				Double myDouble = Double.valueOf(cell.getNumericCellValue());
				//double dbl = myDouble.doubleValue();
				value = Integer.valueOf(myDouble.intValue());
				break;
			case STRING:
				value = cell.getStringCellValue();
				break;
			}
		} /*
			 * else { throw new IEMRException("Error in validating xlsx cell " + cell); }
			 */
		return value;
	}
}
