package br.com.somestudy.file.exporter.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import br.com.somestudy.exception.BadRequestException;
import br.com.somestudy.file.exporter.MediaTypes;
import br.com.somestudy.file.exporter.contract.PersonExporter;
import br.com.somestudy.file.exporter.impl.CsvExporter;
import br.com.somestudy.file.exporter.impl.PdfExporter;
import br.com.somestudy.file.exporter.impl.XlsxExporter;
@Component
public class FileExporterFactory {
	
	private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);
	
	@Autowired
	private ApplicationContext context;
	
	public PersonExporter getExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return context.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return context.getBean(PdfExporter.class);
        } else {
            throw new BadRequestException("Invalid File Format!");
        }
    }
}
