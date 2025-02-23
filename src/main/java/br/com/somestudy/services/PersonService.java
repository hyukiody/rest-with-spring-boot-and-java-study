package br.com.somestudy.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.somestudy.controllers.PersonController;
import br.com.somestudy.data.dto.PersonDTO;
import br.com.somestudy.exceptions.RequiredObjectIsNullException;
import br.com.somestudy.exceptions.ResourceNotFoundException;
import br.com.somestudy.mapper.ObjectMapper;
import br.com.somestudy.model.Person;
import br.com.somestudy.repositories.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonService {

	private Logger logger = Logger.getLogger(PersonService.class.getName());

	@Autowired
	PersonRepository repository;

	@Autowired
	PagedResourcesAssembler<PersonDTO> assembler;

	public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable){

		logger.info("Finding all people!");

		var personPage = repository.findAll(pageable);

		var personVosPage = personPage.map(p -> ObjectMapper.parseObject(p, PersonDTO.class));
		
		personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getId())).withSelfRel()));

		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(),pageable.getPageSize(), "asc")).withSelfRel();

		return assembler.toModel(personVosPage, link);
	}
	
	public PagedModel<EntityModel<PersonDTO>> findPersonByName(String firstName, Pageable pageable) {
		
		logger.info("Finding all people!");
		
		var personPage = repository.findPeopleByName(firstName, pageable);
		
		var personVosPage = personPage.map(p -> ObjectMapper.parseObject(p,  PersonDTO.class));
		personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getId())).withSelfRel()));
		
		Link link = linkTo(
				methodOn(PersonController.class)
				.findAll(pageable.getPageNumber(),
						pageable.getPageSize(),
						"asc")).withSelfRel();
				
		
		return assembler.toModel(personVosPage, link);
	}
	

	public PersonDTO findById(Long id) {

		logger.info("Finding one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this Id!"));

		var vo = ObjectMapper.parseObject(entity, PersonDTO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;

	}

	public PersonDTO create(PersonDTO personVo) {
		if (personVo == null)
			throw new RequiredObjectIsNullException();

		logger.info("Creating one person!");
		var entity = ObjectMapper.parseObject(personVo, Person.class);
		var vo = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
		return vo;

	}

	public PersonDTO update(PersonDTO personVo) {
		if(personVo == null) throw new RequiredObjectIsNullException();

		logger.info("Updating one person!");

		var entity = repository.findById(personVo.getId())
				.orElseThrow(()-> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(personVo.getFirstName());
		entity.setLastName(personVo.getLastName());
		entity.setAddress(personVo.getAddress());
		entity.setGender(personVo.getGender());

		var vo = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}
	
	@Transactional
	public PersonDTO disablePerson(Long id) {
		logger.info("Disabling one person!");
		
		repository.disablePerson(id);
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		var vo = ObjectMapper.parseObject(entity, PersonDTO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		
		return vo;
		
	}
	

	public void delete(Long id) {

		logger.info("Deleting one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}

	
}
/*
 */