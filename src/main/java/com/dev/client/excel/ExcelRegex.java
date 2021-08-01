package com.dev.client.excel;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ExcelRegex {
	
public ExcelRegex() {
		
	}
	
	//number인지 체크
	public Boolean isNumber(String str) {
		Boolean isNumber = false;
		
		String pattern = "^[0-9]*$";
		
		isNumber = Pattern.matches(pattern, str);
		
		return isNumber;
	}
	
	//date형인지 체크
	public Boolean isDate(String str) {
		Boolean isDate = false;
		
		String pattern = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])).*";
		
		isDate = Pattern.matches(pattern, str);
		
		return isDate;
	}
	
	//SimpleDateFormat 예외를 통해 체크
	public Boolean isDateByDateFormat(String str) {
		Boolean isDate = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:ss");
		
		try {
			sdf.setLenient(false);
			sdf.parse(str);
			isDate = true;
		} catch (Exception e) {
			isDate = false;
		}
		
		return isDate;
	}
	
}
