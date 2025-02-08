package br.com.somestudy.services;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.somestudy.controllers.PersonController;
import br.com.somestudy.data.vo.v1.PersonVO;
import br.com.somestudy.exceptions.RequiredObjectIsNullException;
import br.com.somestudy.exceptions.ResourceNotFoundException;
import br.com.somestudy.mapper.MyModelMapper;
import br.com.somestudy.repositories.PersonRepository;
import br.com.somestudy.model.Person;

@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	@Autowired
	PersonRepository repository;

	public List<PersonVO> findAll(){
		
		logger.info("Finding all people!");
		
		var persons = MyModelMapper.parseListObjects(repository.findAll(), PersonVO.class);
		
		persons
			.stream()
				.forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		return persons;
	}

	public PersonVO findById(Long id) {

		logger.info("Finding one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this Id!"));

		var vo = MyModelMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;

	}

	public PersonVO create(PersonVO person) {
		if (person == null)
			throw new RequiredObjectIsNullException();

		logger.info("Creating one person!");
		var entity = MyModelMapper.parseObject(person, Person.class);
		var vo = MyModelMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
		
	}
	
	public PersonVO update(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one person!");
		
		var entity = repository.findById(person.getKey())
				.orElseThrow(()-> new ResourceNotFoundException("No records found for this Id!"));
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = MyModelMapper.parseObject(repository.save(entity), PersonVO.class);
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