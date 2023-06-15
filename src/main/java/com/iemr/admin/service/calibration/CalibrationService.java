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

import com.iemr.admin.data.calibration.CalibrationStrip;
import com.iemr.admin.utils.exception.IEMRException;

public interface CalibrationService {
public Integer saveData(CalibrationStrip req) throws IEMRException;
public String fetchData(CalibrationStrip req) throws IEMRException;
public Integer deleteData(CalibrationStrip req) throws IEMRException;
public Integer updateData(CalibrationStrip req) throws IEMRException;
}
