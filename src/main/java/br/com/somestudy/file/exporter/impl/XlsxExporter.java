package br.com.somestudy.file.exporter.impl;

import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import br.com.somestudy.data.dto.PersonDTO;

@Component
public class XlsxExporter implements PersonExporter {
	
	public Resource exportPeople(List<PersonDTO> people) throws Exception{
		try(Workbook workbook = new XSSFWorkBook()) {
			Sheet sheet = workbook.createSheet("People");
			
			Row headerRow = sheet.createRow(0);
			String[] headers = {
					"{ID}",
					"{First Name}",
					"{Last Name}",
					"{Address}",
					"{Gender}",
					"{Enabled}"};
			for (int i=0; i<headers.length; i++) {
				Cell cell=headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(createHeaderCellStyle(workbook));
			}
			
		}
	}

}
