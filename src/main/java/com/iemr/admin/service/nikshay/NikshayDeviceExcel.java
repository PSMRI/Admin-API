package com.iemr.admin.service.nikshay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.iemr.admin.data.nikshay.NikshayDeviceName;
import com.iemr.admin.model.nikshay.NikshayDeviceRequest;

/**
 * Excel template and reader for the bulk device upload. The template has one
 * header row; columns are read by position, so the column order here is the
 * contract between the downloaded template and the upload.
 */
final class NikshayDeviceExcel {

	static final int MAX_ROWS = 1000;

	private static final String SHEET_NAME = "Devices";
	private static final String[] HEADERS = { "Device Serial Number*", "Device Name* (NAAT / XRAY)",
			"PHI User ID*", "Password*" };
	private static final int COL_SERIAL = 0;
	private static final int COL_NAME = 1;
	private static final int COL_PHI_USER = 2;
	private static final int COL_PASSWORD = 3;

	private NikshayDeviceExcel() {
	}

	/** One data row of the upload; rowNumber is the Excel row number (header is row 1). */
	static class DeviceRow {
		final int rowNumber;
		final NikshayDeviceRequest request;

		DeviceRow(int rowNumber, NikshayDeviceRequest request) {
			this.rowNumber = rowNumber;
			this.request = request;
		}
	}

	static byte[] template() throws IOException {
		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = wb.createSheet(SHEET_NAME);
			CellStyle headerStyle = wb.createCellStyle();
			Font bold = wb.createFont();
			bold.setBold(true);
			headerStyle.setFont(bold);

			Row header = sheet.createRow(0);
			for (int i = 0; i < HEADERS.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(HEADERS[i]);
				cell.setCellStyle(headerStyle);
				sheet.setColumnWidth(i, 28 * 256);
			}
			sheet.createFreezePane(0, 1);

			// Serial numbers are text; keeps Excel from turning long numbers into 1.23E+11
			CellStyle text = wb.createCellStyle();
			text.setDataFormat(wb.createDataFormat().getFormat("@"));
			sheet.setDefaultColumnStyle(COL_SERIAL, text);
			sheet.setDefaultColumnStyle(COL_PHI_USER, text);
			sheet.setDefaultColumnStyle(COL_PASSWORD, text);

			// Device name dropdown
			DataValidationHelper helper = sheet.getDataValidationHelper();
			String[] names = new String[NikshayDeviceName.values().length];
			for (int i = 0; i < names.length; i++) {
				names[i] = NikshayDeviceName.values()[i].name();
			}
			DataValidation validation = helper.createValidation(helper.createExplicitListConstraint(names),
					new CellRangeAddressList(1, MAX_ROWS, COL_NAME, COL_NAME));
			validation.setShowErrorBox(true);
			validation.createErrorBox("Invalid device name", "Choose NAAT or XRAY");
			sheet.addValidationData(validation);

			wb.write(out);
			return out.toByteArray();
		}
	}

	/** Reads the first sheet; fully blank rows are skipped. Values are trimmed, blanks become null. */
	static List<DeviceRow> read(InputStream in) throws IOException {
		List<DeviceRow> rows = new ArrayList<>();
		DataFormatter formatter = new DataFormatter();
		try (Workbook wb = WorkbookFactory.create(in)) {
			Sheet sheet = wb.getSheetAt(0);
			for (int r = 1; r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				String serial = cell(formatter, row, COL_SERIAL);
				String name = cell(formatter, row, COL_NAME);
				String phiUser = cell(formatter, row, COL_PHI_USER);
				String password = cell(formatter, row, COL_PASSWORD);
				if (serial == null && name == null && phiUser == null && password == null) {
					continue;
				}
				if (rows.size() == MAX_ROWS) {
					throw new IllegalArgumentException("At most " + MAX_ROWS + " devices can be uploaded at a time");
				}
				NikshayDeviceRequest request = new NikshayDeviceRequest();
				request.setDeviceSerialNumber(serial);
				request.setDeviceName(name);
				request.setPhiUserId(phiUser);
				request.setPassword(password);
				rows.add(new DeviceRow(r + 1, request));
			}
		}
		return rows;
	}

	private static String cell(DataFormatter formatter, Row row, int col) {
		Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
		if (cell == null) {
			return null;
		}
		// A serial number typed into a number cell would otherwise come back as 1.23457E+11
		if (cell.getCellType() == CellType.NUMERIC && !DateUtil.isCellDateFormatted(cell)) {
			return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
		}
		return NikshayRowUtils.trimToNull(formatter.formatCellValue(cell));
	}
}
