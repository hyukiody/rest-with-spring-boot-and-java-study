package br.com.somestudy.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.somestudy.controllers.PersonController;
import br.com.somestudy.data.dto.PersonDTO;
import br.com.somestudy.exception.BadRequestException;
import br.com.somestudy.exception.FileStorageException;
import br.com.somestudy.exception.RequiredObjectIsNullException;
import br.com.somestudy.exception.ResourceNotFoundException;
import br.com.somestudy.file.exporter.contract.PersonExporter;
import br.com.somestudy.file.exporter.factory.FileExporterFactory;
import br.com.somestudy.file.importer.contract.FileImporter;
import br.com.somestudy.file.importer.factory.FileImporterFactory;
import br.com.somestudy.mapper.ObjectMapper;
import br.com.somestudy.model.Person;
import br.com.somestudy.repositories.PersonRepository;
import jakarta.transaction.Transactional;



@Service
public class PersonService {

	private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

	@Autowired
	PersonRepository repository;

	@Autowired
	PagedResourcesAssembler<PersonDTO> assembler;

	@Autowired
	FileImporterFactory importer;

	@Autowired
	FileExporterFactory exporter;

	public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {

		logger.info("Finding all people!");

		var people= repository.findAll(pageable);

				return buildPagedModel(pageable,people);
	}

	public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {

		logger.info("Finding all people!");
		
		var people = repository.findPeopleByName(firstName, pageable);
		return buildPagedModel(pageable, people);
	}
	
	 public Resource exportPage(Pageable pageable, String acceptHeader) {

	        logger.info("Exporting a People page!");

	        var people = repository.findAll(pageable)
	            .map(person -> ObjectMapper.parseObject(person, PersonDTO.class))
	            .getContent();

	        try {
	            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
	            return exporter.exportPeople(people);
	        } catch (Exception e) {
	            throw new RuntimeException("Error during file export!", e);
	        }
	    }

	    public Resource exportPerson(Long id, String acceptHeader) {
	        logger.info("Exporting data of one Person!");

	        var person = repository.findById(id)
	            .map(entity -> ObjectMapper.parseObject(entity, PersonDTO.class))
	            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

	        try {
	            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
	            return exporter.exportPerson(person);
	        } catch (Exception e) {
	            throw new RuntimeException("Error during file export!", e);
	        }
	    }
	

	public PersonDTO findById(Long id) {

		logger.info("Finding one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records found for this ID!"));
		
		var dto = ObjectMapper.parseObject(entity,PersonDTO.class);
		addHateoasLinks(dto);
		
		return dto;

	}

	public PersonDTO create(PersonDTO personVo) {
		if (personVo == null)
			throw new RequiredObjectIsNullException();

        logger.info("Creating one Person!");
        var entity = ObjectMapper.parseObject(personVo, Person.class);

        var dto = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
        
	}
	public List<PersonDTO> massCreation(MultipartFile file){
		logger.info("Importing People from file!");
		
		if(file.isEmpty()) throw new BadRequestException("Please set a valid file!");
		
		try(InputStream inputStream = file.getInputStream()){
			String filename = 
				Optional.ofNullable(file.getOriginalFilename())
				.orElseThrow(() -> new BadRequestException("File name cannot be null"));
				
			FileImporter importer = this.importer.getImporter(filename);
				
			List<Person> entities = importer.importFile(inputStream).stream()
                .map(dto -> repository.save(ObjectMapper.parseObject(dto, Person.class)))
                .toList();
					
			return entities.stream()
				.map(entity -> {
					var dto = ObjectMapper.parseObject(entity, PersonDTO.class);
					addHateoasLinks(dto);
					return dto;
				})
							.toList();
		}catch(Exception e) {
			throw new FileStorageException("Error processing the file!");
		}
		
	}
	
	

	public PersonDTO update(PersonDTO personVo) {
		if (personVo == null)
			throw new RequiredObjectIsNullException();

		logger.info("Updating one person!");

		var entity = repository.findById(personVo.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(personVo.getFirstName());
		entity.setLastName(personVo.getLastName());
		entity.setAddress(personVo.getAddress());
		entity.setGender(personVo.getGender());

		var dto = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
		addHateoasLinks(dto);
		return dto;
		
	}

	@Transactional
	public PersonDTO disablePerson(Long id) {
		logger.info("Disabling one person!");

		repository.disablePerson(id);

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		var dto= ObjectMapper.parseObject(entity, PersonDTO.class);
		addHateoasLinks(dto);
		return dto;

	}

	public void delete(Long id) {

		logger.info("Deleting one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}

	private PagedModel<EntityModel<PersonDTO>> buildPagedModel(
			Pageable pageable, Page<Person> people) {

		var peopleWithLinks = people.map(person -> {
			var dto = ObjectMapper.parseObject(person, PersonDTO.class);
			addHateoasLinks(dto);

			return dto;
		});

		Link findAllLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findAll(pageable.getPageNumber(),
						pageable.getPageSize(),

						String.valueOf(pageable.getSort())))
				.withSelfRel();

		return assembler.toModel(peopleWithLinks, findAllLink);

	}

	private void addHateoasLinks(PersonDTO dto) {
		dto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("FindAll").withType("GET"));
		
		dto.add(linkTo(methodOn(PersonController.class).findByName("",  1,  12,"asc")).withRel("findByName").withType("GET"));
		
		dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));

		dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
		
		dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
		
		dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
		
		dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATCH"));
		
		dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
		
		dto.add(linkTo(methodOn(PersonController.class).exportPage(1,12,"asc", null))
			.withRel("exportPage")
			.withType("GET")
			.withTitle("Export People"));
	}

}
/*
 */