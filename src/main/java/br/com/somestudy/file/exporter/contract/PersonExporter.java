package br.com.somestudy.file.exporter.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import br.com.somestudy.data.dto.PersonDTO;

public interface PersonExporter {
	Resource exportPeople(List<PersonDTO> people) throws Exception;
	Resource exportPerson(PersonDTO person) throws Exception;
}
