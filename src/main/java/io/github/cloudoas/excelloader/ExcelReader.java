package io.github.cloudoas.excelloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cloudoas.excelloader.mapper.RowMapper;

public class ExcelReader {
	private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
	
	public <T> Collection<T> readObjects(File excelFile, int sheetIndex, Class<T> dataType) throws Exception{
		Workbook workbook = WorkbookFactory.create(excelFile);
		
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		
		if (null==sheet) {
			logger.error("cannot find sheet at index: " + sheetIndex);
			return Collections.<T>emptyList();
		}
		
		RowMapper rowMapper = RowMapper.DEFAULT;
		
		List<T> results = new ArrayList<>();
		List<String> columnHeaders = new ArrayList<>();
		
		Iterator<Row> rowItr = sheet.rowIterator();
		
		if (!rowItr.hasNext()) {
			logger.error("no rows found in sheet at index: " + sheetIndex);
			return Collections.<T>emptyList();		
		}
		
		Row firstRow = rowItr.next();
		
		firstRow.forEach(cell->{
			Object cellValue = RowMapper.getCellValue(cell);
			
			if (null!=cellValue) {
				columnHeaders.add(cellValue.toString().toLowerCase());
			}
		});
		
		
		while (rowItr.hasNext()) {
			Row row = rowItr.next();
			
			T bean = rowMapper.map(row, dataType, columnHeaders);
			
			results.add(bean);
		}
		
		return results;
	}
}