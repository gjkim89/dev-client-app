package com.dev.client.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class ExcelFile {
	
	private final SpreadsheetVersion supplyExcelVersion = SpreadsheetVersion.EXCEL2007;
	
	private final int ROW_START_INDEX = 0;
	
	private final int COLUMN_START_INDEX = 0;
	
	private JsonReader reader;
	
	private Gson gson;
	
	private JsonElement jsonElement;
	
	private List<JsonObject> jsonObjects;
	
	private HSSFWorkbook wb;
	
	private Sheet sheet;
	
	private CellStyle headerStyle;
	
	private CellStyle bodyStyle;
	
	private Font font;
	
	private File file;
	
	private FileOutputStream fileOut;
	
	//엑셀 생성자 함수
	public ExcelFile(String json, String path, String fileName) {
		this.gson = new Gson();
		this.jsonObjects = new ArrayList<JsonObject>();
		this.jsonElement = gson.fromJson(json, JsonElement.class);
		
		if(validateJsonArray(jsonElement)) {
			this.reader = new JsonReader(new StringReader(json));
			
			try {
				reader.beginArray();
				while(reader.hasNext()) {
					jsonObjects.add((JsonObject) gson.fromJson(reader, JsonObject.class));
				}
				reader.endArray();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			validateMaxRow(jsonObjects);
		}else {
			jsonObjects.add(gson.fromJson(json, JsonObject.class));
		}
		
		
		this.wb = new HSSFWorkbook();
		this.sheet = wb.createSheet();
		this.file = new File(path+fileName+".xls");
		
		try {
			this.fileOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		this.font = wb.createFont();
		font.setBold(true);
		this.headerStyle = wb.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setFont(font);
		
		this.bodyStyle = wb.createCellStyle();
		bodyStyle.setAlignment(HorizontalAlignment.CENTER);
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		createExcel(jsonObjects);
	}
	
	private Boolean validateJsonArray(JsonElement jsonElement) {
		Boolean isArray = false;
		
		if(jsonElement.isJsonArray()) {
			isArray = true;
		}
		
		return isArray;
	}
	
	//2007이전 버전은 100만개 이하의 데이터만 만들 수 있기 때문에 해당 사이즈 체크하여 넘으면 예외 발생
	private void validateMaxRow(List<JsonObject> jsonObjects) {
		
		int maxRows = supplyExcelVersion.getMaxRows();
		
		if(jsonObjects.size() > maxRows) {
			throw new IllegalArgumentException(String.format("This ExcelFile does not support over %s rows", maxRows));
		}
		
	}
	
	//엑셀 생성 함수
	private void createExcel(List<JsonObject> jsonObjects) {
		
		if(jsonObjects.isEmpty()) {
			return;
		}else {
			
			createHeaders(jsonObjects.get(0), sheet, ROW_START_INDEX, COLUMN_START_INDEX);
			
			int rowIndex = ROW_START_INDEX + 1;
			if(validateInnerObject(jsonObjects.get(0))) {//키값 내 객체가 있으면 Body row 번호가 하나 늘어남
				rowIndex = ROW_START_INDEX + 2;
			}
			
			for(JsonObject object : jsonObjects) {
				createBody(object, rowIndex++, COLUMN_START_INDEX);
			}
			
		}//end else
		
		//셀 너비 자동으로 맞추기
		int cellSize = sheet.getRow(0).getLastCellNum();
		for(int i = 0; i < cellSize; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, (sheet.getColumnWidth(i)+(short)2000));
		}
		
	}
	
	//헤더 생성 함수
	private void createHeaders(JsonObject jsonObject, Sheet sheet, int rowIndex, int columnStartIndex) {
		Set<String> keys = jsonObject.keySet();
		
		Row row = sheet.createRow(rowIndex);
		Row subRow = null;
		
		int columnIndex = columnStartIndex;
		
		//jsonObject 내 객체 있을시
		if(validateInnerObject(jsonObject)) {
			subRow = sheet.createRow(rowIndex+1);
			
			for(String key : keys) {//키값 for문으로 헤더 값 넣어주기
				if(jsonObject.get(key).isJsonObject()) {//키값이 객체일 경우
					Cell cell = row.createCell(columnIndex);//객체일때 객체명을 셀 값으로 넣어줌
					cell.setCellValue(getInnerObjectKey(jsonObject));
					cell.setCellStyle(headerStyle);
					Set<String> subKeys = jsonObject.get(key).getAsJsonObject().keySet();
					for(String subKey : subKeys) {
						Cell subCell = subRow.createCell(columnIndex++);
						subCell.setCellValue(subKey);
						subCell.setCellStyle(headerStyle);
					}
				}else {
					Cell cell = row.createCell(columnIndex++);
					cell.setCellValue(key);
					cell.setCellStyle(headerStyle);
				}
			}//end for
			
			int firstCol = getInnerObjectIndex(jsonObject);
			int size = getInnerObjectSize(jsonObject);
			//객체 헤더 병합
			sheet.addMergedRegion(new CellRangeAddress(0, 0, firstCol, firstCol+size-1));
			
			List<Integer> columnIndexs = getMergeHeaderColumnIndex(jsonObject);
			for(int i = 0; i < columnIndexs.size(); i++) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, columnIndexs.get(i), columnIndexs.get(i)));
			}
			
		}else {
			for(String key : keys) {
				Cell cell = row.createCell(columnIndex++);
				cell.setCellValue(key);
				cell.setCellStyle(headerStyle);
			}
		}
		
	}
	
	//바디 생성 함수
	private void createBody(JsonObject jsonObject, int rowIndex, int columnStartIndex) {
		
		Set<String> keys = jsonObject.keySet();
		
		Row row = sheet.createRow(rowIndex);
		
		int columnIndex = columnStartIndex;
		
		if(validateInnerObject(jsonObject)) {
			
			for(String key : keys) {
				
				if(jsonObject.get(key).isJsonObject()) {
					Set<String> subKeys = jsonObject.get(key).getAsJsonObject().keySet();
					JsonObject object = jsonObject.get(key).getAsJsonObject();
					for(String subKey : subKeys) {
						Cell cell = row.createCell(columnIndex++);
						createCellValue(cell, object.get(subKey).isJsonNull() ? object.get(subKey).getAsJsonNull() : object.get(subKey).getAsString());
						cell.setCellStyle(bodyStyle);
					}
				}else {
					Cell cell = row.createCell(columnIndex++);
					createCellValue(cell, jsonObject.get(key).isJsonNull() ? jsonObject.get(key).getAsJsonNull() : jsonObject.get(key).getAsString());
					cell.setCellStyle(bodyStyle);
				}
				
			}//end for
			
		}else {
			for(String key : keys) {
				Cell cell = row.createCell(columnIndex++);
				createCellValue(cell, jsonObject.get(key).isJsonNull() ? jsonObject.get(key).getAsJsonNull() : jsonObject.get(key).getAsString());
				cell.setCellStyle(bodyStyle);
			}
		}//end else
		
	}
	
	//데이터 타입 확인하여 바디 내 cell 삽입
	private void createCellValue(Cell cell, Object cellValue) {
		
		ExcelRegex regex = new ExcelRegex();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:ss");
		
		if(regex.isNumber(cellValue.toString())) {
			Double numberValue = Double.valueOf(cellValue.toString());
			cell.setCellValue(numberValue);
		}else if(regex.isDate(cellValue.toString())) {
			try {
				cell.setCellValue(sdf.format(sdf.parse(cellValue.toString())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			cell.setCellValue(cellValue.toString());
		}
		
	}
	
	//jsonObject 내 객체를 가지고 있는지 검사 함수
	private Boolean validateInnerObject(JsonObject object) {
		Boolean check = false;
		
		Set<String> keys = object.keySet();
		
		for(String key : keys) {
			if(object.get(key).isJsonObject()) {
				check = true;
			}
		}
		
		return check;
	}
	
	//jsonObject 내 객체의 인덱스 번호 구하는 함수
	private int getInnerObjectIndex(JsonObject object) {
		
		int index = 0;
		
		List<String> keys = new ArrayList<String>();
		
		for(String key : object.keySet()) {
			keys.add(key);
		}
		for(String key : keys) {
			if(object.get(key).isJsonObject()) {
				index = keys.indexOf(key);
			}
		}
		
		return index;
	}
	
	//jsonObject 내 객체명 구하는 함수
	private String getInnerObjectKey(JsonObject object) {
		
		String name = "";
		
		for(String key : object.keySet()) {
			if(object.get(key).isJsonObject()) {
				name = key;
			}
		}
		
		return name;
	}
	
	//jsonObject 내 객체의 사이즈 구하는 함수
	private int getInnerObjectSize(JsonObject object) {
		
		int size = 0;
		
		for(String key : object.keySet()) {
			if(object.get(key).isJsonObject()) {
				size = object.get(key).getAsJsonObject().size();
			}
		}
		
		return size;
	}
	
	//헤더 병합해야하는 컬럼 index 구하기
	private List<Integer> getMergeHeaderColumnIndex(JsonObject object) {
		//sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
		List<Integer> objectIndex = new ArrayList<Integer>();
		List<Integer> columnIndex = new ArrayList<Integer>();
		
		int objectStartIndex = getInnerObjectIndex(object);
		int objectEndIndex = objectStartIndex + getInnerObjectSize(object);
		int cellSize = sheet.getRow(0).getLastCellNum();
		
		for(int i = objectStartIndex; i < objectEndIndex; i++) {
			objectIndex.add(i);
		}
		
		for(int i = 0; i < cellSize; i++) {
			if(objectIndex.contains(i)) {
				continue;
			}
			columnIndex.add(i);
		}
		
		return columnIndex;
	}
	
	//엑셀 다운로드 함수
	public void write() throws IOException {
		
		wb.write(fileOut);
		wb.close();
		fileOut.close();
		
	}
	
}
