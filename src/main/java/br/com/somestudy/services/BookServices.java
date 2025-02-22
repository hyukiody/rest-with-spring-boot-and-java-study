package br.com.somestudy.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.somestudy.controllers.BookController;
import br.com.somestudy.data.vo.v1.BookVO;
import br.com.somestudy.exceptions.RequiredObjectIsNullException;
import br.com.somestudy.exceptions.ResourceNotFoundException;
import br.com.somestudy.mapper.DozerMapper;
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
public class BookServices {

	private Logger logger = Logger.getLogger(BookServices.class.getName());

	@Autowired
	BookRepository repository;

	@Autowired
	PagedResourcesAssembler<BookVO> assembler;

	/*
	 * Credits for the project author : the usage of dependencies 
	 * that make possible for the page modeling mechanism seems is advanced. 
	 * The method declaration initiates as a cast call;
	 */
	
	public PagedModel <EntityModel<BookVO>> findAll(Pageable pageable){
		
		
		logger.info("Finding all books!");

		var booksPage = repository.findAll(pageable);
		//class conversion through the current optimized model mapper native method
		var booksVOs = booksPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
		//
		booksVOs.map(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));

		Link findAllLink = linkTo(
				methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();

		return assembler.toModel(booksVOs, findAllLink);
	}

	public BookVO findById(Long id) {
		logger.info("Finding one book!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}

	public BookVO create(BookVO book) {
		if (book == null)
			throw new RequiredObjectIsNullException();

		logger.info("Creating one book!");

		var entity = DozerMapper.parseObject(book, Book.class);
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	public BookVO update(BookVO book) {
		if (book == null)
			throw new RequiredObjectIsNullException();

		logger.info("Updating one book!");

		var entity = repository.findById(book.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());

		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	public void delete(Long id) {
		logger.info("Deleting one book!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		repository.delete(entity);
	}

}
/*
 * 
 */
