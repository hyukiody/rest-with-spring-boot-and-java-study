package br.com.somestudy.file.exporter.impl;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import br.com.somestudy.data.dto.PersonDTO;
import br.com.somestudy.file.exporter.contract.PersonExporter;
import br.com.somestudy.services.QRCodeService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class PdfExporter implements PersonExporter{
	
	@Autowired
	private QRCodeService service;
	
	@Override
	public Resource exportPeople(
			List<PersonDTO> people) 
					throws Exception{
		InputStream inputStream = getClass().
				getResourceAsStream("/templates/people.jrxml");
		
		if(inputStream == null) {
			throw new RuntimeException(
					"Template file not found: /templates/people.jrxml");
			
		}
		JasperReport jasperReport = 
				JasperCompileManager.
				compileReport(inputStream);
		
		JRBeanCollectionDataSource dataSource =
				new JRBeanCollectionDataSource(people);
		Map<String, Object> parameters = new HashMap<>();
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				jasperReport, parameters, dataSource);
		try (ByteArrayOutputStream outputStream = 
				new ByteArrayOutputStream()){
					JasperExportManager.
					exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
		}
		
	}
	@Override
	public Resource exportPerson(PersonDTO person) throws Exception{
		InputStream mainTemplateStream = getClass().
				getResourceAsStream(
						"/templates/person.jrxml");
		if(mainTemplateStream == null) {
			throw new RuntimeException(
					"Template file not found: /templates/person.jrxml");
		}
		InputStream subReportStream = getClass().
				getResourceAsStream(
						"/templates/books.jrxml");
		if(subReportStream == null) {
			throw new RuntimeException(
					"Template file not found : /template/books.jrxml");
		}
		JasperReport mainReport = 
				JasperCompileManager.
				compileReport(
						mainTemplateStream);
		JasperReport subReport = 
				JasperCompileManager.
				compileReport(subReportStream);
		
		InputStream qrCodeStream = 
				service.generatedQRCode(
						person.getProfileUrl(), 200, 200);
		
		JRBeanCollectionDataSource subReportDataSource = new JRBeanCollectionDataSource(person.getBooks());
		String path = getClass().getResource(
				"/templates/books.jasper").getPath();
		Map<String, Object> parameters = new HashMap<>();
		
		parameters.put("SUB_REPORT_DATA_SOURCE", subReportDataSource);
		parameters.put("BOOK_SUB_REPORT", subReport);
		parameters.put("SUB_REPORT_DIR",path);
		parameters.put("QR_CODEIMAGE", qrCodeStream);
		
		JRBeanCollectionDataSource mainDataSource = 
				new JRBeanCollectionDataSource(
						Collections.singletonList(person));
		JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, mainDataSource);
		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			return new ByteArrayResource(outputStream.toByteArray());
		}
		
	}
}
