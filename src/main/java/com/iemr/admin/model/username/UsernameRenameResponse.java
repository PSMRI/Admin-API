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
package com.iemr.admin.model.username;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Result of a rename.
 * {@code rowsPerTable} is ordered so the report reads in execution order.
 */
public class UsernameRenameResponse {

	private String oldUserName;
	private String newUserName;
	private int tablesAffected;
	private long totalRowsAffected;
	private Map<String, Long> rowsPerTable = new LinkedHashMap<>();

	public void addTable(String table, long rows) {
		rowsPerTable.put(table, rows);
		totalRowsAffected += rows;
		if (rows > 0) {
			tablesAffected++;
		}
	}

	public String getOldUserName() {
		return oldUserName;
	}

	public void setOldUserName(String oldUserName) {
		this.oldUserName = oldUserName;
	}

	public String getNewUserName() {
		return newUserName;
	}

	public void setNewUserName(String newUserName) {
		this.newUserName = newUserName;
	}

	public int getTablesAffected() {
		return tablesAffected;
	}

	public long getTotalRowsAffected() {
		return totalRowsAffected;
	}

	public Map<String, Long> getRowsPerTable() {
		return rowsPerTable;
	}
}
