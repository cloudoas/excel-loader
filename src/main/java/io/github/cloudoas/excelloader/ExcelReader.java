package io.github.cloudoas.excelloader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Created by rajeevkumarsingh on 18/12/17.
 */

public class ExcelReader {
    public static final String SAMPLE_XLSX_FILE_PATH = "./src/test/resources/sample-xlsx-file.xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        // Retrieving the number of sheets in the Workbook
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

        System.out.println("Retrieving Sheets using for-each loop");
        for(Sheet sheet: workbook) {
            System.out.println("=> " + sheet.getSheetName());
        }


        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);

        System.out.println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
        sheet.forEach(row -> {
            row.forEach(cell -> {
            	System.out.print(getCellValue(cell)+"\t");
            });
            System.out.println();
        });

        // Closing the workbook
        workbook.close();
    }

    private static Object getCellValue(Cell cell) {
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