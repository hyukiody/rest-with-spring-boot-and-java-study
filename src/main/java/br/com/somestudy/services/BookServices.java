package br.com.somestudy.services;

import java.util.List;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Service;

import br.com.somestudy.controllers.BookController;
import br.com.somestudy.data.vo.v1.BookVO;
import br.com.somestudy.exceptions.RequiredObjectIsNullException;
import br.com.somestudy.exceptions.ResourceNotFoundException;
import br.com.somestudy.mapper.MyModelMapper;
import br.com.somestudy.mapper.ObjectMapper.BookMapper;
import br.com.somestudy.model.Book;
import br.com.somestudy.repositories.BookRepository;

@Service
public class BookServices {

	private Logger logger = Logger.getLogger(BookServices.class.getName());

	@Autowired
	BookRepository repository;

	public List<BookVO> findAll() {
		logger.info("Finding all books!");
		var books = MyModelMapper.parseListObjects(repository.findAll(), BookVO.class);
		books.stream().forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		return books;
	}

	public BookVO findById(Long id) {
		logger.info("Finding one book!");
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = MyModelMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		var entity = MyModelMapper.parseObject(book, Book.class);
		var vo = MyModelMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");
		
		var entity= repository.findById(book.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo = MyModelMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	public void delete(Long id) {
		logger.info("Deleting one book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		
		repository.delete(entity);
	}

	public Book createBook(BookVO bookVO) {
		Book book = BookMapper.mapToEntity(bookVO); // Map BookVO to Book entity
		// ... save the book entity using your repository
		return book;
	}

	public BookVO convertToVO(Book book) {
		return BookMapper.mapToVO(book); // Map Book Entity to BookVO
	}

}
