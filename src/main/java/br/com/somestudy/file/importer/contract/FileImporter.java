package br.com.somestudy.file.importer.contract;

import java.io.InputStream;
import java.util.List;

import br.com.somestudy.data.dto.PersonDTO;

public interface FileImporter {
	
	List<PersonDTO> importFile(InputStream inputStream) throws Exception;

}
