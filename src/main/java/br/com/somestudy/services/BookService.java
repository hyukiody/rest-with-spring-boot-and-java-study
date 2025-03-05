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

import br.com.somestudy.controllers.BookController;
import br.com.somestudy.data.dto.BookDTO;
import br.com.somestudy.exception.RequiredObjectIsNullException;
import br.com.somestudy.exception.ResourceNotFoundException;
import br.com.somestudy.mapper.ObjectMapper;
import br.com.somestudy.model.Book;
import br.com.somestudy.repositories.BookRepository;
/*
 *  
 * Logger class is used to log the messages to the main API prompt.
 * 
 * Since each action made in the API is essentially the application's translation
 * of the HTML query and the SQL database requisitions responses to the 
 * ongoing local java program, the service class is important for local java methods 
 * nesting, clean code architecture and that makes possible the direct call into the
 * CONTROLLER classes, that are the nesting of the SPRING API annotations and the local
 * Java program.
 * 
 * SERVICE CLASSES brings within its methods implementation integrates repository,
 * and the usage of the chosen ModelMapper dependencies for class type conversions
 * between the DTO API interface treatment class and the local running logical model
 * classes integrates the object occlusion between the user interface and the local 
 * program execution instance.
 * 
 * Here is a brief description of the PageModel<EntityModel<BookVO>> class method used here in the service
 * class for posterior referencing in the controller class:
 * 		The EntityModel is a Spring HATEOAS class; its called a wrapper class made for
 * 		attaching hypermedia links to the object, but it obligates the implementation
 * 		of the first object model to this 
 *  
*/
@Service
public class BookService {

	private Logger logger = Logger.getLogger(BookService.class.getName());

	@Autowired
	BookRepository repository;

	@Autowired
	PagedResourcesAssembler<BookDTO> assembler;

	/*
	 * Credits for the project author : the usage of dependencies 
	 * that make possible for the page modeling mechanism seems is advanced. 
	 * The method declaration initiates as a cast call;
	 */
	
	public PagedModel <EntityModel<BookDTO>> findAll(Pageable pageable){
		
		
		logger.info("Finding all books!");

		var books= repository.findAll(pageable);
		//class conversion through the current optimized model mapper native method
		var booksWithLinks = books.map(book-> {
			var dto = ObjectMapper.parseObject(book, BookDTO.class);
			addHateoasLinks(dto);
			return dto;
		});
		//

		Link findAllLink = linkTo(
				methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();

		return assembler.toModel(booksWithLinks, findAllLink);
	}

	public BookDTO findById(Long id) {
		logger.info("Finding one book!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		
		var dto = ObjectMapper.parseObject(entity, BookDTO.class);
		addHateoasLinks(dto);
		return dto;
	}

	public BookDTO create(BookDTO book) {
		if (book == null)
			throw new RequiredObjectIsNullException();

		logger.info("Creating one book!");

		var entity = ObjectMapper.parseObject(book, Book.class);
		var dto = ObjectMapper.parseObject(repository.save(entity), BookDTO.class);
		addHateoasLinks(dto);
		return dto;
	}

	public BookDTO update(BookDTO book) {
		if (book == null)
			throw new RequiredObjectIsNullException();

		logger.info("Updating one book!");

		var entity = repository.findById(book.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());

		var dto = ObjectMapper.parseObject(repository.save(entity), BookDTO.class);
		addHateoasLinks(dto);
		return dto;
	}

	public void delete(Long id) {
		logger.info("Deleting one book!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		repository.delete(entity);
	}
	
	private void addHateoasLinks(BookDTO dto) {
		dto.add(linkTo(methodOn(BookController.class)
				.findById(dto.getId())).withSelfRel().withType("GET"));
		
		dto.add(linkTo(methodOn(BookController.class)
				.findAll(1,12,"asc")).withRel("findAll").withType("GET"));
		
		dto.add(linkTo(methodOn(BookController.class)
				.create(dto)).withRel("create").withType("POST"));	
		
		dto.add(linkTo(methodOn(BookController.class)
				.update(dto)).withRel("update").withType("PUT"));
		
		dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
		
	}

}
/*
 * 
 */
