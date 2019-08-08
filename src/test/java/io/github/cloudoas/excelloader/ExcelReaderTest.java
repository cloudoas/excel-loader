package io.github.cloudoas.excelloader;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import io.github.cloudoas.excelloader.model.Department;
import io.github.cloudoas.excelloader.model.Employee;

public class ExcelReaderTest {

	@Test
	public void test() throws Exception {
		ExcelReader reader = new ExcelReader();
		
		Collection<Employee> employees = reader.readObjects(new File("./src/test/resources/sample-xlsx-file.xlsx"), 0, Employee.class);
		
		assertTrue(!employees.isEmpty());
		
		Collection<Department> departments = reader.readObjects(new File("./src/test/resources/sample-xlsx-file.xlsx"), 1, Department.class);
		
		assertTrue(!departments.isEmpty());
	}
}
