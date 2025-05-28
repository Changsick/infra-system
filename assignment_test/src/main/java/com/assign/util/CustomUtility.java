package com.assign.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CustomUtility {

	public static LocalDateTime convertLongToLocalDateTime(Long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneOffset.systemDefault())
                .toLocalDateTime();
	}
	
	public static String convertLongToDateTimeString(Long timestamp) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		return convertLongToLocalDateTime(timestamp).format(formatter);
	}
	
	
	
}
