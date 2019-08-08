package io.github.cloudoas.excelloader.mapper;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RowMapper {
	private static final String CONFIG_NAME="mappings.json";
	private static final Logger logger = LoggerFactory.getLogger(RowMapper.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private Map<String, Map<String, String>> mappings = new HashMap<>();
	
	public static final RowMapper DEFAULT = new RowMapper().init();
	
	public RowMapper init() {
		mappings.clear();
		
		try (InputStream in = RowMapper.class.getClassLoader().getResourceAsStream(CONFIG_NAME)) {
			Map<String, Map<String, String>> mappingConfigs = objectMapper.readValue(in,new TypeReference<Map<String, Map<String, String>>>() {});
			
			mappingConfigs.entrySet().forEach(entry->{
				String name = entry.getKey();
				Map<String, String> column2fieldMappings = new HashMap<>();
				
				mappings.put(name, column2fieldMappings);
				
				Map<String, String> field2columnMappings = entry.getValue();
				field2columnMappings.entrySet().forEach(e->column2fieldMappings.put(e.getValue().toLowerCase(), e.getKey()));
			});
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return this;
	}

	public <T> T map(Row row, Class<T> type, List<String> columnHeaders) {
		if (type.isInterface()) {
			throw new IllegalArgumentException("Cannot map rows to interfaces.");
		}
		
		Map<String, String> columnMappings = mappings.get(type.getSimpleName());
		
		if (columnMappings.isEmpty()) {
			throw new IllegalArgumentException("Cannot find mapping definitions for " + type.getSimpleName());
		}
		
		if (row.getLastCellNum() < columnHeaders.size()) {
			throw new IllegalArgumentException(String.format("%d cells found but %d are expected.", row.getLastCellNum(), columnHeaders.size()));
		}
		
		try {
			T bean = type.getConstructor().newInstance();
			
			int index = 0;
			
			for (Cell cell: row) {
				String columnName = StringUtils.trimToEmpty(columnHeaders.get(index++));
				String fieldName = columnMappings.get(columnName);
				
				if(StringUtils.isBlank(fieldName)){
					logger.error("cannot find fieldName for column {}", columnName);
					continue;
				}
				
				Object cellValue = getCellValue(cell);
				
				if (null==cellValue) {
					logger.warn("empty value of {}:{} is ignored.", row.getRowNum(), columnName);
					continue;
				}
				
				Field field = type.getDeclaredField(fieldName);
				Method setter = findSetter(type, fieldName);
				
				if (null==setter) {
					logger.error("cannot find setter for the field {}", fieldName);
					continue;
				}
				
				if (field.getType().isAssignableFrom(cellValue.getClass())) {
					setter.invoke(bean, cellValue);
				}else if (field.getType().isAssignableFrom(String.class)) {
					setter.invoke(bean, cellValue.toString());
				}else {
					throw new UnsupportedOperationException(String.format("Cannot set value with type %s to the field %s with type %s", 
							cellValue.getClass().getCanonicalName(),
							fieldName,
							field.getType().getCanonicalName()));
				}
			}
			
			return bean;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	private Method findSetter(Class<?> type, String fieldName) {
		String setterName = String.format("set%s",StringUtils.capitalize(fieldName));;
		
		for (Method method: type.getDeclaredMethods()) {
			if (StringUtils.equals(method.getName(), setterName)) {
				return method;
			}
		}
		
		return null;
	}
	
    public static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                break;
            default:
        }
        
        return StringUtils.EMPTY;
    }
}
