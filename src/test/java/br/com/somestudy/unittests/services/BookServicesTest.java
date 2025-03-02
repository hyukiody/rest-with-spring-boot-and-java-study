package br.com.somestudy.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import br.com.somestudy.data.dto.BookDTO;
import br.com.somestudy.exceptions.RequiredObjectIsNullException;
import br.com.somestudy.model.Book;
import br.com.somestudy.repositories.BookRepository;
import br.com.somestudy.services.BookService;
import br.com.somestudy.unittests.mapper.mocks.MockBook;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServicesTest {

	MockBook input;

	@InjectMocks
	private BookService service;

	@Mock
	BookRepository repository;

	@Mock
	PagedResourcesAssembler<BookDTO> assembler;

	void setUp() {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	void findById() {
		Book book = input.mockEntity(1);
		book.setId(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(book));

		var result = service.findById(1L);

		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());

		assertNotNull(result.getLinks().stream()
				.anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/book/v1") 
				&& link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream()
				.anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/book/v1") 
				&& link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream()
				.anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/book/v1") 
				&& link.getType().equals("POST")));

		assertNotNull(result.getLinks().stream()
				.anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/book/v1") 
				&& link.getType().equals("PUT")));

		assertNotNull(result.getLinks().stream()
				.anyMatch(link -> link.getRel().value().equals("delete")
				&& link.getHref().endsWith("/api/book/v1") 
				&& link.getType().equals("DELETE")));
		
		assertEquals("Some author1", result.getAuthor());
		assertEquals(25D, result.getPrice());
		assertEquals("Some Title1", result.getTitle());
		assertNotNull(result.getLaunchDate());
		
	}
	
	@Test
	void create() {
		BookDTO dto = input.mockDTO(1);
		
		Book entity = input.mockEntity(1);
		
		when(repository.save(any(Book.class))).thenReturn(entity);	
		
		var result = service.create(dto);
		
		assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("self")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("GET")
            ));

        assertNotNull(result.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("findAll")
                    && link.getHref().endsWith("/api/book/v1")
                    && link.getType().equals("GET")
            )
        );

        assertNotNull(result.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("create")
                    && link.getHref().endsWith("/api/book/v1")
                    && link.getType().equals("POST")
            )
        );

        assertNotNull(result.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("update")
                    && link.getHref().endsWith("/api/book/v1")
                    && link.getType().equals("PUT")
            )
        );

        assertNotNull(result.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("delete")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("DELETE")
            )
        );

        assertEquals("Some Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());
	}

	@Test
	void update() {
		Book book = input.mockEntity(1);
		Book persisted = book;
		
		persisted.setId(1L);
		
		BookDTO dto = input.mockDTO(1);
		
		when(repository.findById(1L)).thenReturn(Optional.of(book));
		when(repository.save(book)).thenReturn(persisted);
		
		var result = service.update(dto);
		
		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());
		
		assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
	                ));

		assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET")
	                )
	        );

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST")
	                )
	        );

        assertNotNull(result.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT")
	                )
	        );

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("DELETE")
	                )
	        );

        assertEquals("Some Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());
		
	}
	@Test
	void testUpdateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class,
				() ->{
					service.update(null);
				});
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
		
	}
	
	void Delete() {
		Book book = input.mockEntity(1);
		book.setId(1);
		when(repository.findById(1L)).thenReturn(Optional.of(book));
		
		service.delete(1L);
		verify(repository, times(1)).findById(anyLong());
		verify(repository, times(1)).delete(any(Book.class));
		verifyNoMoreInteractions(repository);
	}
	@Test
	void findAll() {
		List<Book> mockEntityList = input.mockEntityList();
		Page<Book> mockPage = new PageImpl<>(mockEntityList);
		when(repository.findAll(any(Pageable.class))).thenReturn(mockPage);
		
		List<BookDTO> mockDtoList = input.mockDTOList();
		
		List<EntityModel<BookDTO>> entityModels = mockDtoList.stream()
				.map(EntityModel::of)
				.collect(Collectors.toList());
		
		PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
				mockPage.getSize(),
				mockPage.getNumber(),
				mockPage.getTotalElements(),
				mockPage.getTotalPages()
				);
		
		PagedModel<EntityModel<BookDTO>> result = 
				service.findAll(PageRequest.of(0, 14));
		
		List<BookDTO> books = result.getContent()
				.stream()
				.map(EntityModel:: getContent)
				.collect(Collectors.toList());
		
		
	}
	
	private static void validateIndividualBook(BookDTO book, int i) {
		assertNotNull(book);
		assertNotNull(book.getId());
		assertNotNull(book.getLinks());
		
		assertNotNull(book.getLinks().stream()
				.anyMatch(link ->  link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/book/v1" + i)
				&& link.getType().equals("GET")
				));
		
		assertNotNull(book.getLinks().stream()
				.anyMatch(link ->  link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/book/v1")
				&& link.getType().equals("GET")
				));
		
		assertNotNull(book.getLinks().stream()
				.anyMatch(link ->  link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/book/v1")
				&& link.getType().equals("POST")
				));
		
		assertNotNull(book.getLinks().stream()
				.anyMatch(link ->  link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/book/v1")
				&& link.getType().equals("PUT")
				));
		
		assertNotNull(book.getLinks().stream()
				.anyMatch(link ->  link.getRel().value().equals("delete")
				&& link.getHref().endsWith("/api/book/v1" + i)
				&& link.getType().equals("DELETE")
				));
		
		assertEquals("Some Author" + i, book.getAuthor());
		assertEquals(25D, book.getPrice());
		assertEquals("Some title" + i, book.getTitle());
		assertNotNull(book.getLaunchDate());
		
				
				
		
		

	}
}
