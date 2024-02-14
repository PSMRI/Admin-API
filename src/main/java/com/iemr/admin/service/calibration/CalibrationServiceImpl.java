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
package com.iemr.admin.service.calibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iemr.admin.data.calibration.CalibrationStrip;
import com.iemr.admin.repo.calibration.CalibrationRepo;
import com.iemr.admin.repo.labmodule.IOTRepo;
import com.iemr.admin.utils.exception.IEMRException;
import com.iemr.admin.utils.mapper.OutputMapper;

@Service
@PropertySource("classpath:application.properties")
public class CalibrationServiceImpl implements CalibrationService {

	@Value("${calibrationPageSize}")
	private Integer calibrationPageSize;

	@Autowired
	private CalibrationRepo calibrationRepo;

	@Override
	public Integer saveData(CalibrationStrip req) throws IEMRException {
		Integer res = 0;
		try {
//			CalibrationStrip calib=null;
			if (req != null && req.getStripCode() != null && req.getProviderServiceMapID() != null) {
				ArrayList<CalibrationStrip> resultSet = calibrationRepo
						.checkIfAlreadyStripPresent(req.getProviderServiceMapID(), req.getStripCode());
				if (resultSet != null && resultSet.size() > 0)
					throw new IEMRException("Strip code already exists");
				else {
					CalibrationStrip calib = calibrationRepo.save(req);
					if (calib.getCalibrationStripID() != null)
						res = 1;
					else
						throw new IEMRException("Error while saving data");
				}
			}

		} catch (Exception e) {
			throw new IEMRException(e.getMessage());
		}
		return res;
	}

	@Override
	public String fetchData(CalibrationStrip req) throws IEMRException {
		Page<CalibrationStrip> calibrationList;
		ArrayList<CalibrationStrip> resultSet;
		Map<String, Object> dataMap = new HashMap<>();
		try {
			if (req != null && req.getProviderServiceMapID() != null) {
				if (req.getPageNo() != null) {
					Pageable pageable = PageRequest.of(req.getPageNo(), calibrationPageSize);
					calibrationList = calibrationRepo.getCalibrationStripsWithPagination(req.getProviderServiceMapID(),
							pageable);
					dataMap.put("calibrationData", calibrationList.getContent());
					dataMap.put("pageCount", calibrationList.getTotalPages());
				} else {
					resultSet = calibrationRepo.getCalibrationStripsWithoutPagination(req.getProviderServiceMapID());
					dataMap.put("calibrationData", resultSet);
				}
			} else
				throw new IEMRException("Error while fetching Calibration data");

		} catch (Exception e) {
			throw new IEMRException(e.getMessage());
		}

		return OutputMapper.gson().toJson(dataMap);

	}

	@Override
	public Integer deleteData(CalibrationStrip req) throws IEMRException {
		Integer res = 0;
		try {
			if (req != null && req.getCalibrationStripID() != null && req.getDeleted() != null) {
				res = calibrationRepo.deleteCalibrationStrip(req.getCalibrationStripID(), req.getDeleted());
			} else
				throw new IEMRException("Invalid request");
		} catch (Exception e) {
			throw new IEMRException(e.getMessage());
		}
		return res;
	}
	
	@Override
	public Integer updateData(CalibrationStrip req) throws IEMRException {
		Integer res = 0;
		try {
			if (req != null && req.getStripCode() != null && req.getProviderServiceMapID() != null) {
					CalibrationStrip calib = calibrationRepo.save(req);
					if (calib.getCalibrationStripID() != null)
						res = 1;
					else
						throw new IEMRException("Error while updating data");
				}
		} catch (Exception e) {
			throw new IEMRException(e.getMessage());
		}
		return res;
	}

}
