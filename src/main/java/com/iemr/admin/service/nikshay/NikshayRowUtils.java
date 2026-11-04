package com.iemr.admin.service.nikshay;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Small helpers shared by the Ni-kshay device / designated PHI services for
 * request trimming, more_data JSON and reading native query rows (whose
 * column types vary by driver: BIT -> Boolean, TINYINT -> Number, etc.).
 */
final class NikshayRowUtils {

	private static final Gson GSON = new Gson();

	private NikshayRowUtils() {
	}

	static String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

	static String require(String value, String message) {
		String t = trimToNull(value);
		if (t == null) {
			throw new IllegalArgumentException(message);
		}
		return t;
	}

	static <T> T require(T value, String message) {
		if (value == null) {
			throw new IllegalArgumentException(message);
		}
		return value;
	}

	// moreData arrives as any JSON value; stored as a JSON string
	static String toJson(Object moreData) {
		return moreData == null ? null : GSON.toJson(moreData);
	}

	static Object fromJson(String moreData) {
		if (moreData == null || moreData.isBlank()) {
			return null;
		}
		try {
			return GSON.fromJson(JsonParser.parseString(moreData), Object.class);
		} catch (JsonParseException e) {
			return moreData;
		}
	}

	static String str(Object o) {
		return o == null ? null : o.toString();
	}

	static Integer toInt(Object o) {
		return o == null ? null : ((Number) o).intValue();
	}

	static Boolean toBool(Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof Boolean) {
			return (Boolean) o;
		}
		if (o instanceof Number) {
			return ((Number) o).intValue() != 0;
		}
		return Boolean.parseBoolean(o.toString());
	}
}
